package com.github.klefstad_teaching.cs122b.idm.component;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.github.klefstad_teaching.cs122b.idm.config.IDMServiceConfig;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.TokenStatus;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class IDMJwtManager
{
    private final JWTManager jwtManager;

    @Autowired
    public IDMJwtManager(IDMServiceConfig serviceConfig)
    {
        this.jwtManager =
            new JWTManager.Builder()
                .keyFileName(serviceConfig.keyFileName())
                .accessTokenExpire(serviceConfig.accessTokenExpire())
                .maxRefreshTokenLifeTime(serviceConfig.maxRefreshTokenLifeTime())
                .refreshTokenExpire(serviceConfig.refreshTokenExpire())
                .build();
    }

    private SignedJWT buildAndSignJWT(JWTClaimsSet claimsSet)
        throws JOSEException
    {
        JWSHeader header = new JWSHeader.Builder(JWTManager.JWS_ALGORITHM)
                .keyID(jwtManager.getEcKey().getKeyID())
                .type(JWTManager.JWS_TYPE)
                .build();

        SignedJWT signedJWT = new SignedJWT(header, claimsSet);


        signedJWT.sign(jwtManager.getSigner());

        return signedJWT;
    }

    private void verifyJWT(SignedJWT jwt)
            throws JOSEException, BadJOSEException, ParseException {
        jwt.verify(jwtManager.getVerifier());
        jwtManager.getJwtProcessor().process(jwt.serialize(), null);

        if (Instant.now().isAfter(jwt.getJWTClaimsSet().getExpirationTime().toInstant()))
            throw new ResultError(IDMResults.ACCESS_TOKEN_IS_EXPIRED);
    }

    public String buildAccessToken(User user)
    {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .expirationTime(Date.from(Instant.now().plus(jwtManager.getAccessTokenExpire())))
                .claim(JWTManager.CLAIM_ID, user.getId())
                .claim(JWTManager.CLAIM_ROLES, user.getRoles())
                .issueTime(Date.from(Instant.now()))
                .build();

        try {
            return buildAndSignJWT(claimsSet).serialize();
        } catch (JOSEException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void verifyAccessToken(String jws)
    {
        try {
            SignedJWT signedJWT = SignedJWT.parse(jws);
            this.verifyJWT(signedJWT);
        } catch (ParseException | JOSEException | BadJOSEException e) {
            throw new ResultError(IDMResults.ACCESS_TOKEN_IS_INVALID);
        }
    }

    public RefreshToken buildRefreshToken(User user)
    {
        return new RefreshToken()
                .setToken(generateUUID().toString())
                .setUserId(user.getId())
                .setTokenStatus(TokenStatus.ACTIVE)
                .setExpireTime(Instant.now().plus(jwtManager.getRefreshTokenExpire()))
                .setMaxLifeTime(Instant.now().plus(jwtManager.getMaxRefreshTokenLifeTime()));
    }

    public boolean hasExpired(RefreshToken refreshToken)
    {
        return refreshToken.getTokenStatus() == TokenStatus.EXPIRED;
    }

    public boolean needsRefresh(RefreshToken refreshToken)
    {
        return !(Instant.now().isAfter(refreshToken.getExpireTime()) || Instant.now().isAfter(refreshToken.getMaxLifeTime()));
    }

    public void updateRefreshTokenExpireTime(RefreshToken refreshToken)
    {
        refreshToken.setExpireTime(Instant.now().plus(jwtManager.getRefreshTokenExpire()));
    }

    private UUID generateUUID()
    {
        return UUID.randomUUID();
    }
}
