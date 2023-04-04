package uz.pdp.appoauth2backendconnect.payload;

import lombok.Builder;
import lombok.Data;
import uz.pdp.appoauth2backendconnect.utils.AppConstants;


@Builder
@Data
public class TokenDTO {
    private final String refreshToken;

    private final String accessToken;

    private final String tokenType = AppConstants.AUTH_TYPE_BEARER;

    public TokenDTO(String refreshToken, String accessToken) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }
}
