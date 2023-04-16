package uz.pdp.appoauth2backendconnect.exceptions;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uz.pdp.appoauth2backendconnect.payload.ApiResponse;
import uz.pdp.appoauth2backendconnect.payload.ErrorCode;

import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(RestExeption.class)
    public HttpEntity<ApiResponse<List<ErrorCode>>> exceptionHandler(RestExeption e){
        return new ResponseEntity<>(
                ApiResponse.failResponse(
                        e.getMessage(),
                        e.getStatus().value()
                ),
                e.getStatus()
        );
    }

}
