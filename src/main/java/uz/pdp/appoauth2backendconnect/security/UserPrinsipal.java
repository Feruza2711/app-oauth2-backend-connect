package uz.pdp.appoauth2backendconnect.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import uz.pdp.appoauth2backendconnect.entity.User;
import uz.pdp.appoauth2backendconnect.payload.oauth2.OAuth2UserInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@Getter
public class UserPrinsipal implements UserDetails , OAuth2User {

    private final User user;
    private Map<String, Object> attributes;

    private UUID id;

    private Collection<? extends GrantedAuthority> authorities;

    private String password;

    private String username;

    private boolean accountNonExpired;

    private boolean accountNonLocked;

    private boolean credentialsNonExpired;

    private boolean enabled;

    public UserPrinsipal(User user) {
        this.user = user;
        this.id = user.getId();
        this.authorities = new ArrayList<>();
        this.password = user.getPassword();
        this.username = user.getPhoneNumber();
        this.accountNonExpired = user.isAccountNonExpired();
        this.accountNonLocked = user.isAccountNonLocked();
        this.credentialsNonExpired = user.isCredentialsNonExpired();
        this.enabled = user.isEnabled();
    }

    public UserPrinsipal(User user, Map<String, Object> attributes, OAuth2UserInfo oAuth2UserInfo) {
        this(user);
        this.attributes = attributes;
        if (username == null)
            username = oAuth2UserInfo.getUsername();
    }

    @Override
    public String getName() {
        if (id == null)
            return username;
        return id.toString();
    }
}
