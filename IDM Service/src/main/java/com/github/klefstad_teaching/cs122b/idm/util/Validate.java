package com.github.klefstad_teaching.cs122b.idm.util;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.TokenStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.regex.Pattern;

@Component
public final class Validate
{
    public void validateUser(String email, char[] password) {
        validateEmail(email);
        validatePassword(password);
    }

    public void validateEmail(String email) {
        if (email.length() < 6 || email.length() > 32)
            throw new ResultError(IDMResults.EMAIL_ADDRESS_HAS_INVALID_LENGTH);

        if (!Pattern.matches("^[a-zA-Z0-9]+@[a-zA-Z0-9]+[.][a-zA-Z0-9]+$", email))
            throw new ResultError(IDMResults.EMAIL_ADDRESS_HAS_INVALID_FORMAT);

    }

    public void validatePassword(char[] password) {
        if (password.length < 10 || password.length > 20)
            throw new ResultError(IDMResults.PASSWORD_DOES_NOT_MEET_LENGTH_REQUIREMENTS);

        if (!Pattern.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).+$", new String(password)))
            throw new ResultError(IDMResults.PASSWORD_DOES_NOT_MEET_CHARACTER_REQUIREMENT);
    }
}
