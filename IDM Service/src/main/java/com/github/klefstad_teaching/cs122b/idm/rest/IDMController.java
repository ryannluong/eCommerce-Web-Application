package com.github.klefstad_teaching.cs122b.idm.rest;

import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;
import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.idm.component.IDMAuthenticationManager;
import com.github.klefstad_teaching.cs122b.idm.component.IDMJwtManager;
import com.github.klefstad_teaching.cs122b.idm.model.request.AuthenticateRequest;
import com.github.klefstad_teaching.cs122b.idm.model.request.LoginRequest;
import com.github.klefstad_teaching.cs122b.idm.model.request.RefreshRequest;
import com.github.klefstad_teaching.cs122b.idm.model.request.RegisterRequest;
import com.github.klefstad_teaching.cs122b.idm.model.response.AuthenticateResponse;
import com.github.klefstad_teaching.cs122b.idm.model.response.LoginResponse;
import com.github.klefstad_teaching.cs122b.idm.model.response.RefreshResponse;
import com.github.klefstad_teaching.cs122b.idm.model.response.RegisterResponse;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.TokenStatus;
import com.github.klefstad_teaching.cs122b.idm.util.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.regex.Pattern;

@RestController
public class IDMController
{
    private final IDMAuthenticationManager authManager;
    private final IDMJwtManager            jwtManager;
    private final Validate                 validate;

    @Autowired
    public IDMController(IDMAuthenticationManager authManager,
                         IDMJwtManager jwtManager,
                         Validate validate)
    {
        this.authManager = authManager;
        this.jwtManager = jwtManager;
        this.validate = validate;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request)
    {
        validate.validateUser(request.getEmail(), request.getPassword());
        authManager.createAndInsertUser(request.getEmail(), request.getPassword());

        return new RegisterResponse()
                .setResult(IDMResults.USER_REGISTERED_SUCCESSFULLY)
                .toResponse();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request)
    {
        validate.validateUser(request.getEmail(), request.getPassword());

        User user = authManager.selectAndAuthenticateUser(request.getEmail(), request.getPassword());

        RefreshToken refreshToken = jwtManager.buildRefreshToken(user);
        authManager.insertRefreshToken(refreshToken);

        return new LoginResponse()
                .setAccessToken(jwtManager.buildAccessToken(user))
                .setRefreshToken(refreshToken.getToken())
                .setResult(IDMResults.USER_LOGGED_IN_SUCCESSFULLY)
                .toResponse();
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@RequestBody RefreshRequest request)
    {
        String token = request.getRefreshToken();
        RefreshToken refreshToken = authManager.verifyRefreshToken(token);

        if (jwtManager.hasExpired(refreshToken))
            throw new ResultError(IDMResults.REFRESH_TOKEN_IS_EXPIRED);
        if (refreshToken.getTokenStatus() == TokenStatus.REVOKED)
            throw new ResultError(IDMResults.REFRESH_TOKEN_IS_REVOKED);
        if (!jwtManager.needsRefresh(refreshToken)) {
            authManager.expireRefreshToken(refreshToken);
            throw new ResultError(IDMResults.REFRESH_TOKEN_IS_EXPIRED);
        }

        jwtManager.updateRefreshTokenExpireTime(refreshToken); // Updates RefreshToken
        authManager.updateRefreshTokenExpireTime(refreshToken); // Updates db refresh token

        if (refreshToken.getExpireTime().isAfter(refreshToken.getMaxLifeTime())) {
            authManager.revokeRefreshToken(refreshToken);

            User temp_user = authManager.getUserFromRefreshToken(refreshToken);
            RefreshToken new_token = jwtManager.buildRefreshToken(temp_user);
            authManager.insertRefreshToken(new_token);

            return new RefreshResponse()
                    .setAccessToken(jwtManager.buildAccessToken(temp_user))
                    .setRefreshToken(new_token.getToken())
                    .setResult(IDMResults.RENEWED_FROM_REFRESH_TOKEN)
                    .toResponse();
        }

        return new RefreshResponse()
                .setAccessToken(jwtManager.buildAccessToken(authManager.getUserFromRefreshToken(refreshToken)))
                .setRefreshToken(refreshToken.getToken())
                .setResult(IDMResults.RENEWED_FROM_REFRESH_TOKEN)
                .toResponse();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticateResponse> authenticate(@RequestBody AuthenticateRequest request)
    {
        String accessToken = request.getAccessToken();
        jwtManager.verifyAccessToken(accessToken);

        return new AuthenticateResponse()
                .setResult(IDMResults.ACCESS_TOKEN_IS_VALID)
                .toResponse();
    }
}
