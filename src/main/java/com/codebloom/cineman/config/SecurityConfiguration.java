package com.codebloom.cineman.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtFilter jwtFilter;
    private final UserDetailsService userDetailsService;
    private final CustomAuthenticatedEntryPoint customAuthenticatedEntryPoint;

    @Value("${api.path}")
    private String apiPath;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults());
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(requests -> requests

                        .requestMatchers(
                                "/cineman-ws",
                                "/qrcode/**",
                                String.format("%s/auth/login", apiPath),
                                String.format("%s/auth/user", apiPath),
                                String.format("%s/auth/logout", apiPath),
                                String.format("%s/auth/register", apiPath),
                                String.format("%s/auth/refresh-token", apiPath),
                                String.format("%s/auth/confirm-email", apiPath),
                                String.format("%s/auth/social-login", apiPath),
                                String.format("%s/auth/social/callback", apiPath),
                                String.format("%s/admin/province/all", apiPath),

                                // Swagger
                                String.format("%s/api-docs", apiPath),
                                String.format("%s/api-docs/**", apiPath),
                                String.format("%s/swagger-resources/**", apiPath),
                                String.format("%s/configuration/ui", apiPath),
                                String.format("%s/configuration/security", apiPath),
                                String.format("%s/swagger-ui/**", apiPath),
                                String.format("%s/swagger-ui.html", apiPath),
                                String.format("%s/swagger-ui/index.html", apiPath)
                                ).permitAll()

                        .requestMatchers(HttpMethod.GET, String.format("%s/movie/movie-theater/**", apiPath)).permitAll()
                        .requestMatchers(HttpMethod.GET, String.format("%s/movie/all", apiPath)).permitAll()
                        .requestMatchers(HttpMethod.GET, String.format("%s/movie/*", apiPath)).permitAll()
                        .requestMatchers(HttpMethod.GET, String.format("%s/show-times/movie/*/movie-theater/*", apiPath)).permitAll()
                        .requestMatchers(HttpMethod.GET, String.format("%s/show-times/movie/*/movie-theater/*/by-date/**", apiPath)).permitAll()
                        .requestMatchers(HttpMethod.POST, String.format("/mail/send")).permitAll()

                .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
//        http.exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(customAuthenticatedEntryPoint)
//                .accessDeniedHandler(customAccessDeniedHandler));
        return http.build();
    }


    @Bean
    public WebSecurityCustomizer ignoreResources() {
        return webSecurity -> webSecurity
                .ignoring()
                .requestMatchers("/actuator/**",
                        "/v3/**",
                        "/webjars/**",
                        "/swagger-ui*/*swagger-initializer.js",
                        "/swagger-ui*/**",
                        "/favicon.ico");
    }


}
