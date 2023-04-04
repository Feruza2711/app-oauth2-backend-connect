package uz.pdp.appoauth2backendconnect.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import uz.pdp.appoauth2backendconnect.utils.Constants;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {

    public String generateAccessToken(UserPrinsipal userPrinsipal){
        Date accessExpirationDate = new Date(System.currentTimeMillis()
                + Constants.ACCESS_TOKEN_EXPIRATION);
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, Constants.ACCESS_TOKEN_KEY)
                .setSubject(userPrinsipal.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(accessExpirationDate)
                .compact();

    }

    public String generateRefreshToken(UserPrinsipal userPrinsipal){
        Date refreshExpirationDate = new Date(System.currentTimeMillis()
                + Constants.ACCESS_TOKEN_EXPIRATION);

     return Jwts
                .builder()
                .signWith(SignatureAlgorithm.HS256, Constants.REFRESH_TOKEN_KEY)
                .setSubject(userPrinsipal.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(refreshExpirationDate)
                .compact();

    }

    public UUID getUserIdFromToken(String token) {
        return UUID.fromString(Jwts
                .parser()
                .setSigningKey(Constants.ACCESS_TOKEN_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject());
    }
}
