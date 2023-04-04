package uz.pdp.appoauth2backendconnect.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import uz.pdp.appoauth2backendconnect.entity.User;
import uz.pdp.appoauth2backendconnect.exceptions.OAuth2AuthenticationProcessingException;
import uz.pdp.appoauth2backendconnect.payload.oauth2.OAuth2UserInfo;
import uz.pdp.appoauth2backendconnect.payload.oauth2.OAuth2UserInfoFactory;
import uz.pdp.appoauth2backendconnect.repository.UserRepository;
import uz.pdp.appoauth2backendconnect.security.UserPrinsipal;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {


    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest,
                                         OAuth2User oAuth2User) {
        String provider = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory
                .getOAuth2UserInfo(
                        provider,
                        oAuth2User.getAttributes());

        if (Objects.isNull(oAuth2UserInfo.getUsername())
                || oAuth2UserInfo.getUsername().isBlank())
            throw new OAuth2AuthenticationProcessingException();

        Optional<User> userOptional = Optional.empty();


        if (Objects.equals("google", provider))
            userOptional = userRepository.findByGoogleUsername(oAuth2UserInfo.getUsername());
        else if (Objects.equals("github", provider)) {
            userOptional = userRepository.findByGithubUsername(oAuth2UserInfo.getUsername());
        }

        User user = new User();
        if (userOptional.isPresent()) {
            user = userOptional.get();
            user = updateExistingUser(user, oAuth2UserInfo);
        }

        return new UserPrinsipal(user,
                oAuth2User.getAttributes(),
                oAuth2UserInfo);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setName(oAuth2UserInfo.getName());
        existingUser.setImageUrl(oAuth2UserInfo.getImageUrl());
        return userRepository.save(existingUser);
    }

}