package com.istvn.speechrecognitionsystem.model;

public class RegistrationRepository {

    private static volatile RegistrationRepository instance;

    private RegistrationDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private RegisteredUser user = null;

    // private constructor : singleton access
    private RegistrationRepository(RegistrationDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static RegistrationRepository getInstance(RegistrationDataSource dataSource) {
        if (instance == null) {
            instance = new RegistrationRepository(dataSource);
        }
        return instance;
    }

    public boolean isRgistered() {
        return user != null;
    }

    private void setRegisteredUser(RegisteredUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public Result<RegisteredUser> registration(String username, String password, String confirmedPassword) {
        // handle registration
        Result<RegisteredUser> result = dataSource.registration(username, password, confirmedPassword);
        if (result instanceof Result.Success) {
            setRegisteredUser(((Result.Success<RegisteredUser>) result).getData());
        }
        return result;
    }
}
