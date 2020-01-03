package com.istvn.speechrecognitionsystem.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Patterns;

import com.istvn.speechrecognitionsystem.R;

public class RegistrationViewModel extends ViewModel {

    private MutableLiveData<RegistrationFormState> registrationFormState = new MutableLiveData<>();
    private MutableLiveData<RegistrationResult> registrationResult = new MutableLiveData<>();
    private RegistrationRepository registrationRepository;

    RegistrationViewModel(RegistrationRepository registrationRepository) {
        this.registrationRepository = registrationRepository;
    }

    public LiveData<RegistrationFormState> getRegistrationFormState() {
        return registrationFormState;
    }

    public LiveData<RegistrationResult> getRegistrationResult() {
        return registrationResult;
    }

    public void registration(String username, String password, String confirmedPassword) {
        // can be launched in a separate asynchronous job
        Result<RegisteredUser> result = registrationRepository.registration(username, password, confirmedPassword);

        if (result instanceof Result.Success) {
            RegisteredUser data = ((Result.Success<RegisteredUser>) result).getData();
            registrationResult.setValue(new RegistrationResult(new RegisteredUser(
                    data.getUserName(),
                    data.getPassword(),
                    data.getConfirmedPassword())));
        } else {
            registrationResult.setValue(new RegistrationResult(R.string.registration_failed));
        }
    }

    public void registrationDataChanged(String username, String password, String confirmedPassword) {
        if (!isUserNameValid(username)) {
            registrationFormState.setValue(new RegistrationFormState(R.string.invalid_username, null, null));
        }else if (!isPasswordValid(password)) {
            registrationFormState.setValue(new RegistrationFormState(null, R.string.invalid_password, null));
        }else if (!isConfirmedPasswordMatched(password, confirmedPassword)){
            registrationFormState.setValue(new RegistrationFormState(null, null, R.string.password_not_match));
        }else {
            registrationFormState.setValue(new RegistrationFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.trim().isEmpty()) {
            return !username.trim().isEmpty();
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 4;
    }

    private boolean isConfirmedPasswordMatched(String password, String confirmedPassword){
        return password.equals(confirmedPassword);
    }

    private boolean isUserRegistered(String username){
        return username.equals("rafiq.istvn@gmail.com");
    }
}
