package uz.pdp.appoauth2backendconnect.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpDTO {
    private String phoneNumber;
    private String password;
    private String prePassword;

}
