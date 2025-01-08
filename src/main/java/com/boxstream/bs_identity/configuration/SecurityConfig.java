package com.boxstream.bs_identity.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    public final String PUBLIC_ENDPOINT[] = {
            "/users/add",
            "/auth/token",
            "/auth/introspect",
            "/auth/logout",
            "/auth/refreshtoken"
    };

    private final String VIEW_ALL_USERS = "/users/all";

    @Autowired
    private JwtDecoderCustom jwtDecoderCustom;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        // disable csrf
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        // public endpoint
        httpSecurity.authorizeHttpRequests(
                requests ->
                    requests
                            .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINT).permitAll()
                            .requestMatchers(HttpMethod.GET, VIEW_ALL_USERS).hasRole("ADMIN") // or hasAuthority("ROLE_ADMIN")
                            .anyRequest().authenticated()); // others are private

        // This config to make sure all others request must decode and validate TOKEN
        // Is the TOKEN Valid | Expired | Logged-out ?
        // We make a JwtDecoderCustom to handle those stuff
        httpSecurity.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoderCustom) // Call the custom that handle JWT validate
                        .jwtAuthenticationConverter(jwtAuthenticationConverter())) // apply a custom converter
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint()) // point to custom handle if have exception occur in this step
        );


        return httpSecurity.build();
    }


    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    // a converter to map: SCOPE_ to ROLE_
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}
