package uz.pdp.appoauth2backendconnect.security.oauth2;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import uz.pdp.appoauth2backendconnect.security.JwtProvider;
import uz.pdp.appoauth2backendconnect.utils.AppConstants;
import uz.pdp.appoauth2backendconnect.utils.CookieUtils;


import java.util.Objects;

@Component
@RequiredArgsConstructor
public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    private final JwtProvider jwtProvider;

    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    public static final String REDIRECT_URI_SIGN_SUCCESS = "redirect_uri_sign_success";
    public static final String REDIRECT_URI_SIGN_FAIL = "redirect_uri_sign_fail";
    public static final String REDIRECT_URI_CONNECT_SUCCESS = "redirect_uri_connect_success";
    public static final String REDIRECT_URI_CONNECT_FAIL = "redirect_uri_connect_fail";
    public static final String PROVIDER_COOKIE_NAME = "provider";
    public static final String USER_ID_COOKIE_NAME = "userId";
    private static final int cookieExpireSeconds = 180;


    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return CookieUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                .map(cookie -> CookieUtils.deserialize(cookie, OAuth2AuthorizationRequest.class))
                .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        if (Objects.isNull(authorizationRequest)) {
            CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
            CookieUtils.deleteCookie(request, response, REDIRECT_URI_SIGN_SUCCESS);
            return;
        }

        CookieUtils.addCookie(response,
                OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                CookieUtils.serialize(authorizationRequest),
                cookieExpireSeconds);
        String redirectUriAfterSuccessSignIn = request.getParameter(REDIRECT_URI_SIGN_SUCCESS);
        String redirectUriAfterFailLogin = request.getParameter(REDIRECT_URI_SIGN_FAIL);
        String redirectUriAfterSuccessConnect = request.getParameter(REDIRECT_URI_CONNECT_SUCCESS);
        String redirectUriAfterFailConnect = request.getParameter(REDIRECT_URI_CONNECT_FAIL);
        String token = request.getParameter(AppConstants.ACCESS_TOKEN);


        String provider =
                request.getRequestURI()
                        .substring(request.getRequestURI()
                                .lastIndexOf("/") + 1);

        if (StringUtils.isNotBlank(redirectUriAfterSuccessSignIn)) {
            CookieUtils.addCookie(response,
                    REDIRECT_URI_SIGN_SUCCESS,
                    redirectUriAfterSuccessSignIn,
                    cookieExpireSeconds);
            CookieUtils.addCookie(response,
                    REDIRECT_URI_SIGN_FAIL,
                    redirectUriAfterFailLogin,
                    cookieExpireSeconds);
        } else if (StringUtils.isNotBlank(redirectUriAfterSuccessConnect)) {
            CookieUtils.addCookie(response,
                    PROVIDER_COOKIE_NAME,
                    provider,
                    cookieExpireSeconds);
            CookieUtils.addCookie(response,
                    REDIRECT_URI_CONNECT_SUCCESS,
                    redirectUriAfterSuccessConnect,
                    cookieExpireSeconds);
            CookieUtils.addCookie(response,
                    REDIRECT_URI_CONNECT_FAIL,
                    redirectUriAfterFailConnect,
                    cookieExpireSeconds);
            if (Objects.nonNull(token)) {
                try {
                    String userId = jwtProvider
                            .getUserIdFromToken(token
                                    .substring(
                                            AppConstants.AUTH_TYPE_BEARER.length())).toString();
                    CookieUtils.addCookie(response,
                            USER_ID_COOKIE_NAME,
                            userId,
                            cookieExpireSeconds);
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        return this.loadAuthorizationRequest(request);
    }

    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        CookieUtils.deleteCookie(request, response, REDIRECT_URI_SIGN_SUCCESS);
        CookieUtils.deleteCookie(request, response, REDIRECT_URI_SIGN_FAIL);
        CookieUtils.deleteCookie(request, response, REDIRECT_URI_CONNECT_SUCCESS);
        CookieUtils.deleteCookie(request, response, REDIRECT_URI_CONNECT_FAIL);
        CookieUtils.deleteCookie(request, response, PROVIDER_COOKIE_NAME);
        CookieUtils.deleteCookie(request, response, USER_ID_COOKIE_NAME);

    }
}