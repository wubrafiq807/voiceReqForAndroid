package com.istvn.speechrecognitionsystem.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.istvn.speechrecognitionsystem.R;
import com.istvn.speechrecognitionsystem.datastorage.SaveDataOnPreference;
import com.istvn.speechrecognitionsystem.model.LoginResponse;
import com.istvn.speechrecognitionsystem.network.GetDataService;
import com.istvn.speechrecognitionsystem.network.RetrofitClientInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfile extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView title;
    private EditText userNameEditText, userEmailEditText, userPhoneNumberEditText;
    private EditText userPasswordEditText, userConfirmedPasswordEditText;
    private Button userProfileSubmitButton, userProfileCancelButton;

    private ProgressDialog progressDialog;

    private SaveDataOnPreference saveDataOnPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View Initializations
        init();

        setSupportActionBar(toolbar);
        // Set the Title
        title.setText(getResources().getString(R.string.user_profile));
        progressDialog.setMessage(getString(R.string.loading));

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        placeholderUserProfile();

        userEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isUserEmailValid(s.toString().trim())) {
                    userEmailEditText.setError(getString(R.string.invalid_username));
                    userProfileSubmitButton.setEnabled(false);
                } else if (!userEmailEditText.getText().toString().trim().isEmpty()) {
                    userProfileSubmitButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (isPasswordMatched(
                        userPasswordEditText.getText().toString().trim(),
                        userConfirmedPasswordEditText.getText().toString().trim())) {
                    userProfileSubmitButton.setEnabled(true);
                } else {
                    userConfirmedPasswordEditText.setError(getString(R.string.password_not_match));
                    userProfileSubmitButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        userPasswordEditText.addTextChangedListener(textWatcher);
        userConfirmedPasswordEditText.addTextChangedListener(textWatcher);

        userProfileSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                networkCallUserUpdate(saveDataOnPreference.getUserId());
            }
        });

        userProfileCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity();
            }
        });
    }

    /**
     * Update User
     * @param userId
     */
    private void networkCallUserUpdate(String userId) {
        progressDialog.show();
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<LoginResponse> call = service.userUpdate("/api-v1/users/" + userId + "/",
                userNameEditText.getText().toString(), userEmailEditText.getText().toString(),
                userPhoneNumberEditText.getText().toString(), userPasswordEditText.getText().toString());
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                progressDialog.dismiss();
                if (!response.body().getError()){
                    Toast.makeText(getApplicationContext(),response.body().getMessage(), Toast.LENGTH_LONG).show();
                    saveDataOnPreference.saveUser(response.body());
                    startNewActivity();
                }else {
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    /**
     * Start Main Activity
     */
    private void startNewActivity() {
        startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }

    /**
     * Check Both passwords are matched or not
     *
     * @param pass1
     * @param pass2
     * @return
     */
    private boolean isPasswordMatched(String pass1, String pass2) {

        return pass1.equals(pass2);
    }

    /**
     * Place Holder User Data Into User Profile
     */
    private void placeholderUserProfile() {
        userNameEditText.setText(saveDataOnPreference.getUserName());
        userEmailEditText.setText(saveDataOnPreference.getUserEmailAddress());
        userPhoneNumberEditText.setText(saveDataOnPreference.getUserPhoneNumber());
    }

    /**
     * View Initializations
     */
    private void init() {
        setContentView(R.layout.activity_user_profile);

        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.appNameTv);
        userNameEditText = findViewById(R.id.userNameEditText);
        userEmailEditText = findViewById(R.id.userEmailEditText);
        userPhoneNumberEditText = findViewById(R.id.userPhoneNumberEditText);
        userPasswordEditText = findViewById(R.id.userPasswordEditText);
        userConfirmedPasswordEditText = findViewById(R.id.userConfirmedPasswordEditText);
        userProfileSubmitButton = findViewById(R.id.userProfileSubmitButton);
        userProfileCancelButton = findViewById(R.id.userProfileCancelButton);

        saveDataOnPreference = new SaveDataOnPreference(this);
        progressDialog = new ProgressDialog(this);
    }

    private boolean isUserEmailValid(String email) {
        if (email == null) {
            return false;
        }
        if (email.trim().isEmpty()) {
            return !email.trim().isEmpty();
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }
}
