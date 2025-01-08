package com.boxstream.bs_identity.service;

import com.boxstream.bs_identity.dto.request.AuthenticationRequest;
import com.boxstream.bs_identity.dto.request.IntrospectRequest;
import com.boxstream.bs_identity.dto.request.LogoutRequest;
import com.boxstream.bs_identity.dto.request.RefreshTokenRequest;
import com.boxstream.bs_identity.dto.response.AuthenticationResponse;
import com.boxstream.bs_identity.dto.response.IntrospectResponse;
import com.boxstream.bs_identity.dto.response.NewTokenResponse;
import com.boxstream.bs_identity.entity.InvalidatedToken;
import com.boxstream.bs_identity.entity.User;
import com.boxstream.bs_identity.exception.*;
import com.boxstream.bs_identity.repository.InvalidatedTokenRepository;
import com.boxstream.bs_identity.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimNames;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
public class AuthenticationService {

    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal // to not inject this to contructor
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    public IntrospectResponse introspect(IntrospectRequest request) throws AuthenticationFailedException, JOSEException, ParseException {
        var token = request.getToken();
        try {
            verifyToken(token);
        } catch (ApplicationException e) {
            return IntrospectResponse.builder().valid(false).build(); // if catch any exception from verifyToken(token);
        }
        return IntrospectResponse.builder().valid(true).build();
    }

    public AuthenticationResponse authenticateReturnJWT(AuthenticationRequest authenticationRequest) throws KeyLengthException {
        User user =
                userRepository.findByUsername(authenticationRequest.getUsername())
                        .orElseThrow(UserNotFoundException::new);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());

        if(!authenticated) throw new AuthenticationFailedException();

        // GENERATE TOKEN
        var token = gennerateTokenUsingNimbus(user);

        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(token)
                .build();
    }

    private String gennerateTokenUsingNimbus(User user) throws KeyLengthException {
        // build header
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        // build claims - Data in JWT body is called Claims
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername()) // subject is stand for the logged-in user
                .issuer("tienphuckx.com") // the domain that provide this JWT
                .issueTime(new Date())
                .expirationTime(new Date( // the time that token will be expired in 1 hour
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))
                .claim("email", user.getEmail())
                .claim("phone", user.getPhone())
                .claim("scope", buildScopeRoles(user))
                .jwtID(UUID.randomUUID().toString()) // setting a jwt ID to validate logged-out token
                .build();

        // Final Payload
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(jwsHeader, payload); // param are header and payload

        /**
             Sign the JWT
             MACSigner() is symmetric : one key for both encrypt and decrypt : AES, DES
             asymmetric algorithm: RSA, etc.
             MACSigner() need a secret key 32 bytes
         */

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes(StandardCharsets.UTF_8)));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Could not generate JWT token", e);
            throw new RuntimeException(e);
        }

    }

    String buildScopeRoles(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        user.getRoles().forEach(role -> {
            stringJoiner.add("ROLE_" + role.getName());
            if(!CollectionUtils.isEmpty(role.getPermissions()))
                role.getPermissions().forEach(permission -> {
                    stringJoiner.add(permission.getName());
                });

        });
        return stringJoiner.toString();
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        var signToken = verifyToken(request.getToken());

        // get JWTID from token
        String jwtid = signToken.getJWTClaimsSet().getJWTID();
        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

        // build InvalidatedToken
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jwtid)
                .expiryTime(expiryTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);
    }


    /**
     * This function for verify a token
     * Invalid or Expired
     * @param token
     * @return
     * @throws JOSEException
     * @throws ParseException
     */
    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {

        // get verify the signature
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes(StandardCharsets.UTF_8));

        // get signature from token
        SignedJWT signedJWT = SignedJWT.parse(token);

        // get expired time of token
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        // do verify
        var verified = signedJWT.verify(verifier);

        // invalid token?
        if(!verified) throw new ApplicationException(ErrorCode.INVALID_TOKEN);

        // expired token?
        if(expiryTime.before(new Date())) throw new ApplicationException(ErrorCode.TOKEN_EXPIRED);

        // check if this token already exists in InvalidToken table
        // (mean user have logged-out for this token)
        String tokenJWTID = signedJWT.getJWTClaimsSet().getJWTID();
        if(invalidatedTokenRepository.existsById(tokenJWTID)) throw new ApplicationException(ErrorCode.TOKEN_LOGGED_OUT);

        return signedJWT;
    }

    /**
     * A token is about expired (not expired yet)
     * Do refresh it
     * Using the RefreshToken
     *
     * @param refreshTokenRequest
     * @return
     */
    public NewTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) throws ParseException, JOSEException {

        /**
         * do verify current token
         * must a valid token
         * verifyToken() already impl to validate
         */
        var signJWT = verifyToken(refreshTokenRequest.getToken());

        /**
         * get jit and expiryTime to build InvalidatedToken
         * then do logout the current token
         */
        var jit = signJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signJWT.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();
        invalidatedTokenRepository.save(invalidatedToken);


        /**
         * now build a new token for client
         */
        var username = signJWT.getJWTClaimsSet().getSubject();
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApplicationException(ErrorCode.UNAUTHENTICATED));
        var token = gennerateTokenUsingNimbus(user);

        return NewTokenResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

}
