package uz.pdp.appoauth2backendconnect.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uz.pdp.appoauth2backendconnect.entity.User;
import uz.pdp.appoauth2backendconnect.repository.UserRepository;
import uz.pdp.appoauth2backendconnect.utils.AppConstants;

import java.io.IOException;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class MyFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public MyFilter(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(AppConstants.AUTH_HEADER);

        try {
            if (Objects.nonNull(authHeader)) {
                User user = null;
                if (authHeader.startsWith(AppConstants.AUTH_TYPE_BASIC))
                    user = getUserFromBasic(authHeader);
                else if (authHeader.startsWith(AppConstants.AUTH_TYPE_BEARER))
                    user = getUserFromBearer(authHeader);
                UserPrinsipal userPrincipal = new UserPrinsipal(user);
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                userPrincipal,
                                null,
                                userPrincipal.getAuthorities()
                        )
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }

    private User getUserFromBearer(String authHeader) {
        UUID userId;
        try {
            userId = jwtProvider.getUserIdFromToken(
                    authHeader.substring(AppConstants.AUTH_TYPE_BEARER.length()));
        } catch (ExpiredJwtException |
                 UnsupportedJwtException |
                 MalformedJwtException |
                 SignatureException |
                 IllegalArgumentException e) {
            log.error("getUserFromBearer: ", e);
            throw e;
        }
        return userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("Bunday user yo'q"));
    }

    private User getUserFromBasic(String authHeader) {
        String[] basicAuthFromHeader = getBasicAuthFromHeader(authHeader);
        Optional<User> optionalUser = userRepository.findByPhoneNumber(basicAuthFromHeader[0]);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordEncoder.matches(
                    basicAuthFromHeader[1],
                    user.getPassword())) {
                return user;
            }
        }
        throw new UsernameNotFoundException("User mavjud emas yoki xato");
    }

    private String[] getBasicAuthFromHeader(String authHeader) {
        return new String(Base64.getDecoder().decode(
                authHeader.substring(AppConstants.AUTH_TYPE_BASIC.length())
        )).split(":");
    }

}
