package uz.pdp.appoauth2backendconnect.utils;

import org.springframework.beans.factory.annotation.Value;

public class Constants {
    @Value("${app.jwt.token.access.key}")
     public static String ACCESS_TOKEN_KEY;

    @Value("${app.jwt.token.access.expiration}")
    public static Long ACCESS_TOKEN_EXPIRATION;

    @Value("${app.jwt.token.refresh.key}")
    public static String REFRESH_TOKEN_KEY;

    @Value("${app.jwt.token.refresh.expiration}")
    public static Long REFRESH_TOKEN_EXPIRATION;

    @Value("${spring.mail.username}")
    public static String sender;
}
