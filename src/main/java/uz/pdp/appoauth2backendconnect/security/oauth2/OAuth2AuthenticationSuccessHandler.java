package uz.pdp.appoauth2backendconnect.security.oauth2;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import uz.pdp.appoauth2backendconnect.entity.User;
import uz.pdp.appoauth2backendconnect.payload.oauth2.OAuth2UserInfo;
import uz.pdp.appoauth2backendconnect.payload.oauth2.OAuth2UserInfoFactory;
import uz.pdp.appoauth2backendconnect.repository.UserRepository;
import uz.pdp.appoauth2backendconnect.security.JwtProvider;
import uz.pdp.appoauth2backendconnect.security.UserPrinsipal;
import uz.pdp.appoauth2backendconnect.utils.AppConstants;
import uz.pdp.appoauth2backendconnect.utils.CookieUtils;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static uz.pdp.appoauth2backendconnect.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.*;


@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy()
                .sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) {
        Optional<String> optionalSignRedirectUri = CookieUtils
                .getCookie(request, REDIRECT_URI_SIGN_SUCCESS)
                .map(Cookie::getValue);

        Optional<String> optionalConnectRedirectUri = CookieUtils
                .getCookie(request, REDIRECT_URI_CONNECT_SUCCESS)
                .map(Cookie::getValue);


        String targetUrl = null;
        String token = null;
        UserPrinsipal userPrincipal = (UserPrinsipal) authentication.getPrincipal();

        if (optionalSignRedirectUri.isPresent()) {

            //CLIENT CONNECT QILINMAGAN OAUTH2 SERVICE BILAN SIGN-IN QILMOQCHI
            //BU HOLATDA UNGA ERROR MSG QAYTARAMIZ
            if (userPrincipal.getId() == null) {
                String failUrl = CookieUtils.getCookie(request, REDIRECT_URI_SIGN_FAIL)
                        .map(Cookie::getValue)
                        .orElse(("/"));
                return UriComponentsBuilder.fromUriString(failUrl)
                        .queryParam("error", "Oka avval ro'yxatdan o'ting")
                        .build().toUriString();

            }
            targetUrl = optionalSignRedirectUri.orElse(getDefaultTargetUrl());
            String accessToken;
            String refreshToken;
            accessToken = jwtProvider.generateAccessToken(userPrincipal);
            refreshToken = jwtProvider.generateRefreshToken(userPrincipal);
            return UriComponentsBuilder.fromUriString(targetUrl)
                    .queryParam(AppConstants.ACCESS_TOKEN, AppConstants.AUTH_TYPE_BEARER + accessToken)
                    .queryParam(AppConstants.REFRESH_TOKEN, refreshToken)
                    .build().toUriString();
        } else if (optionalConnectRedirectUri.isPresent()) {
            System.out.println(userPrincipal);
            Optional<String> optionalUserId = CookieUtils
                    .getCookie(request, USER_ID_COOKIE_NAME)
                    .map(Cookie::getValue);
            if (optionalUserId.isEmpty()) {
                String failUrl = CookieUtils.getCookie(request, REDIRECT_URI_CONNECT_FAIL)
                        .map(Cookie::getValue)
                        .orElse("/error");
                return UriComponentsBuilder.fromUriString(failUrl)
                        .queryParam("error", "Oka avval cabinetga kirib keyin connect qiling")
                        .build().toUriString();
            }
            Optional<String> optionalProvider = CookieUtils.getCookie(request,
                    PROVIDER_COOKIE_NAME).map(Cookie::getValue);
            if (optionalProvider.isPresent()) {
                String provider = optionalProvider.get();
                OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                        provider,
                        userPrincipal.getAttributes());
                Optional<User> optionalUser = userRepository.findById(UUID.fromString(optionalUserId.get()));
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    user.setName(oAuth2UserInfo.getName());
                    user.setImageUrl(oAuth2UserInfo.getImageUrl());
                    if (Objects.equals("google", provider)) {
                        if (Objects.isNull(user.getGoogleUsername()))
                            user.setGoogleUsername(userPrincipal.getUsername());
                        else user.setGoogleUsername(null);
                    } else if (Objects.equals("github", provider)) {
                        if (Objects.isNull(user.getGithubUsername()))
                            user.setGithubUsername(userPrincipal.getUsername());
                        else user.setGithubUsername(null);
                    }
                    userRepository.save(user);
                    return UriComponentsBuilder.fromUriString(optionalConnectRedirectUri.get())
                            .queryParam("success", "OK okasi")
                            .queryParam("provider", provider)
                            .build().toUriString();
                }
            }
        }
        return null;
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}