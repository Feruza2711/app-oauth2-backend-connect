package uz.pdp.appoauth2backendconnect.payload.oauth2;

import uz.pdp.appoauth2backendconnect.exceptions.OAuth2AuthenticationProcessingException;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId,
                                                   Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase("google")) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase("github")) {
            return new GithubOAuth2UserInfo(attributes);
        }
        throw new OAuth2AuthenticationProcessingException();
    }
}