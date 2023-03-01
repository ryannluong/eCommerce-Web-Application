package com.github.klefstad_teaching.cs122b.idm.repo;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.TokenStatus;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.sql.Types;

@Component
public class IDMRepo
{
    private final NamedParameterJdbcTemplate template;

    @Autowired
    public IDMRepo(NamedParameterJdbcTemplate template)
    {
        this.template = template;
    }

    public void registerUser(User user)
    {
        try {
            this.template.update(
                    "INSERT INTO idm.user (email, user_status_id, salt, hashed_password)" +
                            "VALUES (:email, :user_status_id, :salt, :hashed_password);",

                    new MapSqlParameterSource()
                            .addValue("email", user.getEmail(), Types.VARCHAR)
                            .addValue("user_status_id", 1, Types.INTEGER)
                            .addValue("salt", user.getSalt(), Types.CHAR)
                            .addValue("hashed_password", user.getHashedPassword(), Types.CHAR));
        } catch (DuplicateKeyException e) {
            throw new ResultError(IDMResults.USER_ALREADY_EXISTS);
        }
    }

    public User selectUser(String email)
    {
        try {
            return this.template.queryForObject(
                    "SELECT id, email, user_status_id, salt, hashed_password " +
                    "FROM idm.user WHERE email = :email;",

                    new MapSqlParameterSource()
                            .addValue("email", email, Types.VARCHAR),
                    (rs, rowNum) ->
                            new User()
                                    .setId(rs.getInt("id"))
                                    .setEmail(rs.getString("email"))
                                    .setUserStatus(UserStatus.fromId(rs.getInt("user_status_id")))
                                    .setSalt((rs.getString("salt")))
                                    .setHashedPassword((rs.getString("hashed_password")))
            );
        } catch (DataAccessException e) {
            throw new ResultError(IDMResults.USER_NOT_FOUND);
        }
    }

    public void insertRefreshToken(RefreshToken refreshToken)
    {
        this.template.update(
                "INSERT INTO idm.refresh_token (token, user_id, token_status_id, expire_time, max_life_time) " +
                "VALUES (:token, :user_id, :token_status_id, :expire_time, :max_life_time);",

                new MapSqlParameterSource()
                        .addValue("token", refreshToken.getToken(), Types.CHAR)
                        .addValue("user_id", refreshToken.getUserId(), Types.INTEGER)
                        .addValue("token_status_id", refreshToken.getTokenStatus().id(), Types.INTEGER)
                        .addValue("expire_time", Timestamp.from(refreshToken.getExpireTime()), Types.TIMESTAMP)
                        .addValue("max_life_time", Timestamp.from(refreshToken.getMaxLifeTime()), Types.TIMESTAMP)
        );
    }

    public User getUserFromRefreshToken(RefreshToken token)
    {
        try {
            return this.template.queryForObject(
                    "SELECT u.id, u.email, u.user_status_id, u.salt, u.hashed_password " +
                    "FROM idm.user AS u, idm.refresh_token AS rt " +
                    "WHERE :token = rt.token AND u.id = rt.user_id;",

                    new MapSqlParameterSource()
                            .addValue("token", token.getToken(), Types.CHAR),
                    (rs, rowNum) ->
                            new User()
                                    .setId(rs.getInt("id"))
                                    .setEmail(rs.getString("email"))
                                    .setUserStatus(UserStatus.fromId(rs.getInt("user_status_id")))
                                    .setSalt((rs.getString("salt")))
                                    .setHashedPassword((rs.getString("hashed_password")))
            );
        } catch (DataAccessException e) {
            throw new ResultError(IDMResults.USER_NOT_FOUND);
        }
    }

    public RefreshToken getRefreshTokenFromToken(String refreshToken)
    {
        try {
            return this.template.queryForObject(
                    "SELECT id, token, user_id, token_status_id, expire_time, max_life_time " +
                    "FROM idm.refresh_token AS rt " +
                    "WHERE rt.token = :token;",

                    new MapSqlParameterSource()
                            .addValue("token", refreshToken, Types.CHAR),
                    (rs, rowNum) ->
                            new RefreshToken()
                                    .setId(rs.getInt("id"))
                                    .setToken(rs.getString("token"))
                                    .setUserId(rs.getInt("user_id"))
                                    .setTokenStatus(TokenStatus.fromId(rs.getInt("token_status_id")))
                                    .setExpireTime(rs.getTimestamp("expire_time").toInstant())
                                    .setMaxLifeTime(rs.getTimestamp("max_life_time").toInstant())
            );
        } catch (DataAccessException e) {
            throw new ResultError(IDMResults.REFRESH_TOKEN_NOT_FOUND);
        }
    }

    public void expireRefreshToken(RefreshToken token) {
        this.template.update(
                "UPDATE idm.refresh_token " +
                "SET token_status_id = :token_status_id " +
                "WHERE id = :id;",

                new MapSqlParameterSource()
                        .addValue("token_status_id", TokenStatus.EXPIRED.id(), Types.INTEGER)
                        .addValue("id", token.getId(), Types.INTEGER)
        );
    }

    public void revokeRefreshToken(RefreshToken token) {
        this.template.update(
                "UPDATE idm.refresh_token " +
                        "SET token_status_id = :token_status_id " +
                        "WHERE id = :id;",

                new MapSqlParameterSource()
                        .addValue("token_status_id", TokenStatus.REVOKED.id(), Types.INTEGER)
                        .addValue("id", token.getId(), Types.INTEGER)
        );
    }

    public void updateRefreshTokenExpireTime(RefreshToken token) {
        this.template.update(
                "UPDATE idm.refresh_token " +
                "SET expire_time = :expire_time " +
                "WHERE id = :id;",

                new MapSqlParameterSource()
                        .addValue("expire_time", Timestamp.from(token.getExpireTime()), Types.TIMESTAMP)
                        .addValue("id", token.getId(), Types.INTEGER)
        );
    }
}
