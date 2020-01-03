package com.istvn.speechrecognitionsystem.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.istvn.speechrecognitionsystem.R;
import com.istvn.speechrecognitionsystem.model.LoginResponse;
import com.istvn.speechrecognitionsystem.model.RecognitionViewResponse;
import com.istvn.speechrecognitionsystem.network.GetDataService;
import com.istvn.speechrecognitionsystem.network.RetrofitClientInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecognitionView extends AppCompatActivity {
    TextView callerPhoneNumber, receiverPhoneNumber, recordingDate, recordStartTime, recordEndTime, recordDuration;
    TextView extractedEmails, extractedNames, extractedPhone, extractedText;
    private ProgressDialog progressDialog;

    private Toolbar toolbar;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();

        setSupportActionBar(toolbar);
        String voiceReqID = getIntent().getStringExtra("voice_req");

        progressDialog.setMessage(getResources().getString(R.string.loading));

        setSupportActionBar(toolbar);
        // Set the Title
        title.setText("Recognition Details");

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        networkCallViewRecognized(voiceReqID);


    }

    private void networkCallViewRecognized(String voiceReqID) {
        progressDialog.show();
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<RecognitionViewResponse> call = service.getRecognitiobByID("/api-v1/voiceReqs/" + voiceReqID + "/");
        call.enqueue(new Callback<RecognitionViewResponse>() {
            @Override
            public void onResponse(Call<RecognitionViewResponse> call, Response<RecognitionViewResponse> response) {

                progressDialog.dismiss();
                if (!response.body().getError()) {
                    receiverPhoneNumber.setText(response.body().getResult().getReceiverPhoneNo());
                    recordStartTime.setText(response.body().getResult().getRecordStartTime());
                    callerPhoneNumber.setText(response.body().getResult().getCallerPhoneNo());
                    recordEndTime.setText(response.body().getResult().getRecordEndTime());
                    recordingDate.setText(response.body().getResult().getCreatedDate().toString().substring(0, 10));
                    String emails = response.body().getResult().getEmails().toString().trim();
                    extractedEmails.setText(emails.length() > 0 ? emails.substring(0, emails.length() - 1) : "");
                    String names = response.body().getResult().getNames().toString().trim();
                    extractedNames.setText(names.length() > 0 ? names.substring(0, names.length() - 1) : "");
                    String phones = response.body().getResult().getPhones().toString().trim();
                    extractedPhone.setText(phones.length() > 0 ? phones.substring(0, phones.length() - 1) : "");
                    extractedText.setText(response.body().getResult().getText().toString().trim());

                } else {
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RecognitionViewResponse> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    private void init() {
        setContentView(R.layout.activity_recogntion_view);
        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.appNameTv);
        callerPhoneNumber = findViewById(R.id.caller_phone_number);
        receiverPhoneNumber = findViewById(R.id.receiver_phone_number);
        recordingDate = findViewById(R.id.recording_date);
        recordStartTime = findViewById(R.id.record_start_time);
        recordEndTime = findViewById(R.id.record_end_time);
        recordDuration = findViewById(R.id.total_duration);
        extractedEmails = findViewById(R.id.extracted_email);
        extractedNames = findViewById(R.id.extracted_name);
        extractedPhone = findViewById(R.id.extracted_phone_number);
        extractedText = findViewById(R.id.extracted_text);
        progressDialog = new ProgressDialog(this);
    }

}
