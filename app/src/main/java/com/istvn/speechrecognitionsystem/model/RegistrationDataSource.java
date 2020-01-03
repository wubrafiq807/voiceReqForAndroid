package com.istvn.speechrecognitionsystem.model;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class RegistrationDataSource {

    public Result<RegisteredUser> registration(String username, String password, String confirmedPassword) {

        try {
            // Registered User authentication
            RegisteredUser user =
                    new RegisteredUser(
                            username,
                            password,
                            confirmedPassword);
            return new Result.Success<>(user);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error registration", e));
        }
    }
}
