package com.istvn.speechrecognitionsystem.model;

import android.support.annotation.Nullable;
/**
 * Authentication result : success (user details) or error message.
 */
public class RegistrationResult {
    @Nullable
    private RegisteredUser success;
    @Nullable
    private Integer error;

    RegistrationResult(@Nullable Integer error) {
        this.error = error;
    }

    RegistrationResult(@Nullable RegisteredUser success) {
        this.success = success;
    }

    @Nullable
    public RegisteredUser getSuccess() {
        return success;
    }

    @Nullable
    public Integer getError() {
        return error;
    }
}
