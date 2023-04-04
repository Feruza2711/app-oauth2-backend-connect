package uz.pdp.appoauth2backendconnect.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.pdp.appoauth2backendconnect.entity.templete.AbsUUIDEntity;

@Table(name = "users")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User extends AbsUUIDEntity {

  @Column(unique = true)
    private String phoneNumber;

    private String password;

    private String googleUsername;

    private String githubUsername;

    private String name;

    private String imageUrl;


    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;

    public String getPhoneNumber() {
        return phoneNumber;
    }


    public String getPassword() {
        return password;
    }

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public User(String phoneNumber, String password) {
        this.phoneNumber = phoneNumber;
        this.password = password;
    }
}
