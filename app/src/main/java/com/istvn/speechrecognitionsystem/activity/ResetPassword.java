package com.istvn.speechrecognitionsystem.activity;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.istvn.speechrecognitionsystem.R;
import com.istvn.speechrecognitionsystem.model.LoginResponse;
import com.istvn.speechrecognitionsystem.network.GetDataService;
import com.istvn.speechrecognitionsystem.network.RetrofitClientInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPassword extends AppCompatActivity {
    Toolbar toolbar;
    TextView title;
    EditText resetPasswordEditText;
    Button resetButton;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View initialization
        init();

        setSupportActionBar(toolbar);
        // Set the Title
        title.setText(getResources().getString(R.string.reset_password_title));

        progressDialog.setMessage(getResources().getString(R.string.loading));

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        resetPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!isUserNameValid(s.toString())) {
                    resetPasswordEditText.setError(getString(R.string.invalid_username));
                    resetButton.setEnabled(false);
                }else if (!resetPasswordEditText.getText().toString().trim().isEmpty()){
                    resetButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                netWorkCallSendResetPasswordLink(resetPasswordEditText.getText().toString().trim());
            }
        });

    }

    /**
     * Send Reset Password Link
     * @param email
     */
    private void netWorkCallSendResetPasswordLink(String email) {
        progressDialog.show();
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<LoginResponse> call = service.passwordResetRequest("/api-v1/get-password/users/" + email);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressDialog.dismiss();
                if (!response.body().getError())
                    alertDialogue(response.body().getMessage());
                else {
                    resetPasswordEditText.setError(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    /**
     * Show Alert Dialogue
     * @param msg
     */
    private void alertDialogue(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(msg);
        dialog.setPositiveButton("OK", null);
        dialog.show();// show alert dialog
    }

    /**
     * View Initializations
     */
    private void init() {

        setContentView(R.layout.activity_forgot_password);

        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.appNameTv);
        resetPasswordEditText = findViewById(R.id.resetPasswordEditText);
        resetButton = findViewById(R.id.resetPasswordButton);
        progressDialog = new ProgressDialog(this);
    }

    /**
     * Check Email Format
     * @param username
     * @return boolean
     */
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
}
