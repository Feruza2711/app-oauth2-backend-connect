package uz.pdp.appoauth2backendconnect.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import uz.pdp.appoauth2backendconnect.security.MyFilter;
import uz.pdp.appoauth2backendconnect.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import uz.pdp.appoauth2backendconnect.security.oauth2.OAuth2AuthenticationFailureHandler;
import uz.pdp.appoauth2backendconnect.security.oauth2.OAuth2AuthenticationSuccessHandler;
import uz.pdp.appoauth2backendconnect.service.CustomOAuth2UserService;
import uz.pdp.appoauth2backendconnect.utils.AppConstants;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        jsr250Enabled = true,
        securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler auth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final MyFilter myFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors()
                .and()
                .csrf()
                .disable()
                .formLogin()
                .disable()
                .httpBasic()
                .disable()
                .authorizeHttpRequests(f -> {
                    try {
                        f
                                .requestMatchers(AppConstants.OPEN_PAGES)
                                .permitAll()
                                .requestMatchers(AppConstants.BASE_PATH + "/**")
                                .authenticated()
                                .requestMatchers(
                                        "/**"
//                                                "/*",
//                                                "/robots.txt",
//                                                "/sitemap.xml",
//                                                "/favicon.ico",
//                                                "/*/*.png",
//                                                "/*/*.gif",
//                                                "/*/*.svg",
//                                                "/*/*.jpg",
//                                                "/*/*.html",
//                                                "/*/*.css",
//                                                "/*/*.js",
//                                                "/v2/**",
//                                                "/csrf",
//                                                "/webjars/*"
                                )

                                .permitAll()
                                .and()
                                .oauth2Login()
                                .authorizationEndpoint()
                                .baseUri("/oauth2/authorize")
                                .authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository)
                                .and()
                                .redirectionEndpoint()
                                .baseUri("/oauth2/callback/*")
                                .and()
                                .userInfoEndpoint()
                                .userService(customOAuth2UserService)
                                .and()
                                .successHandler(auth2AuthenticationSuccessHandler)
                                .failureHandler(oAuth2AuthenticationFailureHandler)
//                                .loginPage("/login")
//                                .disable()
                                .loginPage("/sign-in")
                        ;

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .addFilterBefore(myFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


}
