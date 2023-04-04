package uz.pdp.appoauth2backendconnect.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorCode {
    private String name;
    private int code;
}
