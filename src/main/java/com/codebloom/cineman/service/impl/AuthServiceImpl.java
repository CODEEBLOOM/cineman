package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.LoginRequest;
import com.codebloom.cineman.controller.response.TokenResponse;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.exception.ForBiddenException;
import com.codebloom.cineman.exception.InvalidDataException;
import com.codebloom.cineman.model.UserEntity;
import com.codebloom.cineman.model.UserPrincipal;
import com.codebloom.cineman.repository.UserRepository;
import com.codebloom.cineman.service.AuthService;
import com.codebloom.cineman.service.JwtService;
import com.codebloom.cineman.service.MyUserDetailsService;
import com.codebloom.cineman.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;


import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static com.codebloom.cineman.common.enums.TokenType.REFRESH_TOKEN;


@Service
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION-SERVICE")
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final ApplicationContext context;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    @Value("${spring.security.oauth2.client.provider.google.user-info-uri}")
    private String googleUserInfoUri;




    /**
     * Hàm get access token và refresh token
     * @param request Login request object
     * @return access token và refresh token
     */
    @Override
    public TokenResponse getAccessToken(LoginRequest request) {
        log.info("get access token");

        try {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    request.getEmail(), request.getPassword()
            );
            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException e) {
            log.info("Authentication failed: {}", e.getMessage());
            throw new DataNotFoundException("Email or password is incorrect");
        }

        UserEntity user =  userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Email or password is incorrect"));
        UserPrincipal userPrincipal = new UserPrincipal(user);

        String accessToken =  jwtService.generateAccessToken(user.getUserId(), request.getEmail(), userPrincipal.getAuthorities());
        String refreshToken =  jwtService.generateRefreshToken(user.getUserId(), request.getEmail(), userPrincipal.getAuthorities());
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Hàm refresh token: tạo ra accessToken mới và refresh toke mới
     * @param refreshToken : accessToken
     * @return access token và refresh token
     */
    @Override
    public TokenResponse getRefreshToken(String refreshToken) {
        if (!StringUtils.hasLength(refreshToken)) {
            throw new InvalidDataException("Token must be not blank");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            UserDetails userDetails = context.getBean(MyUserDetailsService.class).loadUserByUsername(jwtService.extractUsername(refreshToken, REFRESH_TOKEN));
            try {
                UserEntity user = userRepository.findByRefreshToken(refreshToken);
                if(!jwtService.validateToken(refreshToken, REFRESH_TOKEN, userDetails)){
                    throw new ForBiddenException("Invalid refresh token");
                }
                // generate new access token
                String accessToken = jwtService.generateAccessToken(user.getUserId(), user.getEmail(), userDetails.getAuthorities());
                String newRefreshToken = jwtService.generateRefreshToken(user.getUserId(), user.getEmail(), userDetails.getAuthorities());
                userService.updateRefreshToken(newRefreshToken, false);
                return TokenResponse.builder().accessToken(accessToken).refreshToken(newRefreshToken).build();
            } catch (Exception e) {
                log.error("Access denied! errorMessage: {}", e.getMessage());
                throw new ForBiddenException(e.getMessage());
            }
        }
        return null;
    }

    /**
     * Hàm generate auth url
     * @param loginType : google
     * @return auth url
     */
    @Override
    public String generateAuthUrl(String loginType) {
        String url = "";
        loginType = loginType.trim().toLowerCase(); // Normalize the login type

        if ("google".equals(loginType)) {
            GoogleAuthorizationCodeRequestUrl urlBuilder = new GoogleAuthorizationCodeRequestUrl(
                    googleClientId,
                    googleRedirectUri,
                    Arrays.asList("email", "profile", "openid"));
            url = urlBuilder.build();
        }
        return url;
    }

    @Override
    public Map<String, Object> authenticateAndFetchProfile(String code, String loginType) throws IOException {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            String accessToken;

            switch (loginType.toLowerCase()) {
                case "google":
                    accessToken = new GoogleAuthorizationCodeTokenRequest(
                            new NetHttpTransport(), new GsonFactory(),
                            googleClientId,
                            googleClientSecret,
                            code,
                            googleRedirectUri
                    ).execute().getAccessToken();

                    // Configure RestTemplate to include the access token in the Authorization header
                    restTemplate.getInterceptors().add((req, body, executionContext) -> {
                        req.getHeaders().set("Authorization", "Bearer " + accessToken);
                        return executionContext.execute(req, body);
                    });

                    // Make a GET request to fetch user information
                    return new ObjectMapper().readValue(
                            restTemplate.getForEntity(googleUserInfoUri, String.class).getBody(),
                            new TypeReference<>() {});
                //break;
//                case "facebook":
//                    // Facebook token request setup
//                    String urlGetAccessToken = UriComponentsBuilder
//                            .fromUriString(facebookTokenUri)
//                            .queryParam("client_id", facebookClientId)
//                            .queryParam("redirect_uri", facebookRedirectUri)
//                            .queryParam("client_secret", facebookClientSecret)
//                            .queryParam("code", code)
//                            .toUriString();
//
//                    // Use RestTemplate to fetch the Facebook access token
//                    ResponseEntity<String> response = restTemplate.getForEntity(urlGetAccessToken, String.class);
//                    ObjectMapper mapper = new ObjectMapper();
//                    JsonNode node = mapper.readTree(response.getBody());
//                    accessToken = node.get("access_token").asText();
//
//                    // Set the URL for the Facebook API to fetch user info
//                    // Lấy thông tin người dùng
//                    String userInfoUri = facebookUserInfoUri + "&access_token=" + accessToken;
//                    return mapper.readValue(
//                            restTemplate.getForEntity(userInfoUri, String.class).getBody(),
//                            new TypeReference<>() {});
//                //break;

                default:
                    log.info("Unsupported login type: {}", loginType);
                    return null;
            }
    }


}
