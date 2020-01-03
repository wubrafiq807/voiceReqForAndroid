package com.istvn.speechrecognitionsystem.model;

import android.support.annotation.Nullable;

public class RegistrationFormState {
    @Nullable
    private Integer usernameError;
    @Nullable
    private Integer passwordError;
    @Nullable
    private Integer confirmedPasswordError;
    private boolean isDataValid;

    RegistrationFormState(@Nullable Integer usernameError, @Nullable Integer passwordError, @Nullable Integer confirmedPasswordError) {
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.confirmedPasswordError = confirmedPasswordError;
        this.isDataValid = false;
    }

    RegistrationFormState(boolean isDataValid) {
        this.usernameError = null;
        this.passwordError = null;
        this.confirmedPasswordError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    public Integer getPasswordError() {
        return passwordError;
    }

    @Nullable
    public Integer getConfirmedPasswordError(){
        return confirmedPasswordError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}
