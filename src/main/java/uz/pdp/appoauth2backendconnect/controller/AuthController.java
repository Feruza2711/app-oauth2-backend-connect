package uz.pdp.appoauth2backendconnect.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.pdp.appoauth2backendconnect.payload.ApiResponse;
import uz.pdp.appoauth2backendconnect.payload.SignInDTO;
import uz.pdp.appoauth2backendconnect.payload.SignUpDTO;
import uz.pdp.appoauth2backendconnect.payload.TokenDTO;
import uz.pdp.appoauth2backendconnect.service.AuthService;
import uz.pdp.appoauth2backendconnect.utils.AppConstants;


@RestController
@RequestMapping(AuthController.BASE_PATH)
@RequiredArgsConstructor
public class AuthController {
    public static final String BASE_PATH= AppConstants.BASE_PATH+"/auth";
    public static final String SIGN_UP_PATH = "/sign-up";
    public static final String SIGN_IN_PATH = "/sign-in";
    private final AuthService authService;

    @PostMapping(SIGN_UP_PATH)
    public ApiResponse<String> signUp(@RequestBody SignUpDTO signDTO) {
        return authService.signUp(signDTO);
    }


    @PostMapping(SIGN_IN_PATH)
    public ApiResponse<TokenDTO> signIn(@RequestBody SignInDTO signInDTO){
        return authService.signIn(signInDTO);

    }





}
