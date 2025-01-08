package com.boxstream.bs_identity.configuration;

import com.boxstream.bs_identity.dto.request.IntrospectRequest;
import com.boxstream.bs_identity.exception.ApplicationException;
import com.boxstream.bs_identity.exception.ErrorCode;
import com.boxstream.bs_identity.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

@Component
public class JwtDecoderCustom implements JwtDecoder {

    /**
     * JwtDecoderCustom implements JwtDecoder
     * Wrap the NimbusJwtDecoder to decode JWT
     * What for?
     * for validate JWT first (valid | expired | logged-out)
     * before do next stuff
     *
     */

    @Value("${jwt.signerKey}")
    private String signingKey;

    @Autowired
    private AuthenticationService authenticationService;

    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {

        // First, before decode JWT, introspect it
        // this introspect() have handle to validate JWT
        try{
            IntrospectRequest introspectRequest = IntrospectRequest.builder().token(token).build();
            var response = authenticationService.introspect(introspectRequest);

            if(!response.isValid()) throw new ApplicationException(ErrorCode.INVALID_TOKEN);
        } catch (JOSEException | ParseException e) {
            throw new JwtException(e.getMessage());
        }

        // Do build Jwt follow SpringSecurity required
        if(Objects.isNull(nimbusJwtDecoder)){
            SecretKeySpec secretKeySpec = new SecretKeySpec(signingKey.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }
        return nimbusJwtDecoder.decode(token);
    }
}
