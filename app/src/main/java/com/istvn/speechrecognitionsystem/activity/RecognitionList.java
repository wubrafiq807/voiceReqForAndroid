package com.istvn.speechrecognitionsystem.activity;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.istvn.speechrecognitionsystem.R;
import com.istvn.speechrecognitionsystem.adapter.RecognitionListAdapter;
import com.istvn.speechrecognitionsystem.datastorage.SaveDataOnPreference;
import com.istvn.speechrecognitionsystem.model.RecognitionListResponse;
import com.istvn.speechrecognitionsystem.network.GetDataService;
import com.istvn.speechrecognitionsystem.network.RetrofitClientInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecognitionList extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView title;

    private RecognitionListAdapter recognitionListAdapter;
    private RecyclerView recyclerView;

    private SaveDataOnPreference saveDataOnPreference;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View Initializations
        init();

        setSupportActionBar(toolbar);
        // Set the Title
        title.setText(getResources().getString(R.string.recognition_list_title));

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Set Progress Dialogue Message
        progressDialog.setMessage(getString(R.string.loading));

        //Log.d("RRR", "onCreate: " + saveDataOnPreference.getUserId());

        // NetworkCall For Recognition List
        networkCallRecognitionList(saveDataOnPreference.getUserId());

    }

    /**
     * NetworkCall For Recognition List
     */
    private void networkCallRecognitionList(String userId) {
        progressDialog.show();
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<RecognitionListResponse> call = service.getRecognitionList("/api-v1/voiceReqs/?user_id=" + userId);
        call.enqueue(new Callback<RecognitionListResponse>() {
            @Override
            public void onResponse(Call<RecognitionListResponse> call, Response<RecognitionListResponse> response) {
                progressDialog.dismiss();

                // Set the Recognition Adapter into Recycler View
                setAdapter(response.body());
            }

            @Override
            public void onFailure(Call<RecognitionListResponse> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    /**
     * Set the Recognition Adapter into Recycler View
     */
    private void setAdapter(RecognitionListResponse model) {

        // Initialize holidayAdapter;
        recognitionListAdapter = new RecognitionListAdapter(this, model);
        // set the layoutManager into recyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // set the holidayAdapter into RecyclerView
        recyclerView.setAdapter(recognitionListAdapter);
    }

    /**
     * View Initializations
     */
    private void init() {
        setContentView(R.layout.activity_recognition_list);

        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.appNameTv);
        recyclerView = findViewById(R.id.recyclerViewRecognitionList);

        saveDataOnPreference = new SaveDataOnPreference(this);
        progressDialog = new ProgressDialog(this);
    }
}
