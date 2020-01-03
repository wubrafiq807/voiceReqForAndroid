package com.istvn.speechrecognitionsystem.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.istvn.speechrecognitionsystem.R;
import com.istvn.speechrecognitionsystem.model.LoginResponse;
import com.istvn.speechrecognitionsystem.model.RegisteredUser;
import com.istvn.speechrecognitionsystem.model.RegistrationFormState;
import com.istvn.speechrecognitionsystem.model.RegistrationResult;
import com.istvn.speechrecognitionsystem.model.RegistrationViewModel;
import com.istvn.speechrecognitionsystem.model.RegistrationViewModelFactory;
import com.istvn.speechrecognitionsystem.network.GetDataService;
import com.istvn.speechrecognitionsystem.network.RetrofitClientInstance;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRegistration extends AppCompatActivity {

    EditText mEmail, mPassword, mConfirmedPassword;
    Button mRegistration;

    RegistrationViewModel registrationViewModel;

    ProgressDialog progressDialog;

    Toolbar toolbar;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usre_registration);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // View initialization
        init();

        setSupportActionBar(toolbar);
        // Set the Title
        title.setText(getResources().getString(R.string.user_registration_title));

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        registrationViewModel = ViewModelProviders.of(this, new RegistrationViewModelFactory())
                .get(RegistrationViewModel.class);

        registrationViewModel.getRegistrationFormState().observe(this, new Observer<RegistrationFormState>() {
            @Override
            public void onChanged(@Nullable RegistrationFormState registrationFormState) {
                if (registrationFormState == null) {
                    return;
                }
                mRegistration.setEnabled(registrationFormState.isDataValid());
                if (registrationFormState.getUsernameError() != null) {
                    mEmail.setError(getString(registrationFormState.getUsernameError()));
                }
                if (registrationFormState.getPasswordError() != null) {
                    mPassword.setError(getString(registrationFormState.getPasswordError()));
                }
                if (registrationFormState.getConfirmedPasswordError() != null) {
                    mConfirmedPassword.setError(getString(registrationFormState.getConfirmedPasswordError()));
                }
            }
        });

        registrationViewModel.getRegistrationResult().observe(this, new Observer<RegistrationResult>() {
            @Override
            public void onChanged(@Nullable RegistrationResult registrationResult) {

                if (registrationResult == null) {
                    return;
                }
                progressDialog.dismiss();
                if (registrationResult.getError() != null) {
                    showLoginFailed(registrationResult.getError());
                }
                if (registrationResult.getSuccess() != null) {
                    updateUiWithUser(registrationResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                //finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                registrationViewModel.registrationDataChanged(
                        mEmail.getText().toString(),
                        mPassword.getText().toString(),
                        mConfirmedPassword.getText().toString());
            }
        };

        mEmail.addTextChangedListener(afterTextChangedListener);
        mPassword.addTextChangedListener(afterTextChangedListener);
        mConfirmedPassword.addTextChangedListener(afterTextChangedListener);

        mConfirmedPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    registrationViewModel.registration(
                            mEmail.getText().toString(),
                            mPassword.getText().toString(),
                            mConfirmedPassword.getText().toString());
                }
                return false;
            }
        });

        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();

                // If User Already Exist
                String email = mEmail.getText().toString().trim();

                if (networkCallExistEmail(email)){
                    mEmail.setError(getResources().getString(R.string.user_already_registered));
                }else {
                    registrationViewModel.registration(
                            mEmail.getText().toString(),
                            mPassword.getText().toString(),
                            mConfirmedPassword.getText().toString());
                    networkCallRegisterUser(email, mPassword.getText().toString().trim(), 1);
                }
            }
        });
    }

    /**
     * Manually Register User Method
     * @param email
     * @param password
     * @param login_type
     */
    private void networkCallRegisterUser(String email, String password, int login_type) {
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<LoginResponse> call = service.registerUser(email, password, login_type);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                //Log.d("RRR", "onResponse: Registered: " + response.body().getResult().getEmail());
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

            }
        });
    }

    /**
     * If User Registered Already
     * @param existEmail
     */
    private boolean networkCallExistEmail(String existEmail) {
        progressDialog.show();
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<LoginResponse> call = service.isExistUser("/api-v1/users/check/" + existEmail);
        boolean rs = false;
        try {
            LoginResponse result = call.execute().body();
            //Log.d("RRR", "networkCall: Exist User: " + result.getMessage());

            if (result.getResult() != null)
                rs = true;

            progressDialog.dismiss();
        } catch (IOException e) {
            e.printStackTrace();
            progressDialog.dismiss();
        }

        return rs;
    }

    /**
     * View Initialization's Method
     */
    private void init() {

        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.appNameTv);
        mEmail = findViewById(R.id.emailEditText);
        mPassword = findViewById(R.id.passwordEditText);
        mConfirmedPassword = findViewById(R.id.confirmedPasswordEditText);
        mRegistration = findViewById(R.id.registerButton);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.loading));
    }

    private void updateUiWithUser(RegisteredUser model) {
        String welcome = getString(R.string.welcome) + model.getUserName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
