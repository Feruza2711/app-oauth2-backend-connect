package uz.pdp.appoauth2backendconnect.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignInDTO {
    private String phoneNumber;
    private String password;

}
