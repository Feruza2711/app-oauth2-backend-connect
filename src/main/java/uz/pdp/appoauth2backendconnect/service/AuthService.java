package uz.pdp.appoauth2backendconnect.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.pdp.appoauth2backendconnect.entity.User;
import uz.pdp.appoauth2backendconnect.exceptions.RestExeption;
import uz.pdp.appoauth2backendconnect.payload.ApiResponse;
import uz.pdp.appoauth2backendconnect.payload.SignInDTO;
import uz.pdp.appoauth2backendconnect.payload.SignUpDTO;
import uz.pdp.appoauth2backendconnect.payload.TokenDTO;
import uz.pdp.appoauth2backendconnect.repository.UserRepository;
import uz.pdp.appoauth2backendconnect.security.JwtProvider;
import uz.pdp.appoauth2backendconnect.security.UserPrinsipal;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;


    public AuthService(UserRepository userRepository,
                       @Lazy PasswordEncoder passwordEncoder,
                       @Lazy AuthenticationManager authenticationManager, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }



    public ApiResponse<String> signUp(SignUpDTO signDTO) {
        if(userRepository.existsByPhoneNumber(signDTO.getPhoneNumber())){
            throw RestExeption.restThrow("This user already exsist");
        }
        User user=new User(
                signDTO.getPhoneNumber(),
                passwordEncoder.encode(signDTO.getPassword())
        );
        userRepository.save(user);
        return ApiResponse.successResponse("ok");
    }

    public ApiResponse<TokenDTO> signIn(SignInDTO signInDTO) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInDTO.getPhoneNumber(),
                        signInDTO.getPassword()
                )
        );

        UserPrinsipal userPrincipal =(UserPrinsipal) authenticate.getPrincipal();



        return ApiResponse.successResponse(
                TokenDTO.builder()
                        .accessToken(jwtProvider.generateAccessToken(userPrincipal))
                        .refreshToken(jwtProvider.generateRefreshToken(userPrincipal))
                        .build()
        );

    }
}


