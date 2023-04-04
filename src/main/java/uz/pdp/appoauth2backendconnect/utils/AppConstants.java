package uz.pdp.appoauth2backendconnect.utils;


import uz.pdp.appoauth2backendconnect.controller.AuthController;

public interface AppConstants {
    String BASE_PATH="/api";
    String AUTH_HEADER="Authorization";
    String AUTH_TYPE_BASIC="Basic ";
    String AUTH_TYPE_BEARER="Bearer ";
    String[] OPEN_PAGES = {
            AuthController.BASE_PATH + "/**",
    };

    String ACCESS_TOKEN = "AccessToken";
    String REFRESH_TOKEN = "RefreshToken";
}
