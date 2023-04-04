package uz.pdp.appoauth2backendconnect.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RestExeption extends RuntimeException {
    private String message;
    private HttpStatus status=HttpStatus.BAD_REQUEST;

    private RestExeption(String message) {
        this.message=message;
    }

    public static RestExeption restThrow(String message) {
        return new RestExeption(message);
    }
}
