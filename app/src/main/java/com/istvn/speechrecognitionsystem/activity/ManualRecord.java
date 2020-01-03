package com.istvn.speechrecognitionsystem.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.istvn.speechrecognitionsystem.R;
import com.istvn.speechrecognitionsystem.services.CallRecordService;
import com.istvn.speechrecognitionsystem.utility.AllKeys;

import java.util.Timer;
import java.util.TimerTask;

import static com.istvn.speechrecognitionsystem.utility.RunTimePermission.hasPermissions;
import static com.istvn.speechrecognitionsystem.utility.Utils.log;

public class ManualRecord extends AppCompatActivity implements View.OnClickListener {

    static boolean manualRecord = false;
    private TextView recordTime;
    private ImageButton recordStop, recordList;
    private int cnt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();

        recordStop.setOnClickListener(this);
        recordList.setOnClickListener(this);
    }

    private void init() {

        setContentView(R.layout.activity_manual_record);
        recordStop = findViewById(R.id.recordStop);
        recordList = findViewById(R.id.recordList);
        recordTime = findViewById(R.id.recordingTime);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.recordStop:
                if (cnt % 2 == 0){

                    // Manual Record True
                    manualRecord = true;

                    // Start Timer
                    timer();

                    // Start Recording Service
                    startService(this, new Intent());

                    // Change ImageButton into Stop Icon
                    recordStop.setImageResource(R.drawable.stop);
                }else {

                    // Manual Record False
                    manualRecord = false;

                    // Stop Recording Service
                    this.stopService(new Intent(this, CallRecordService.class));

                    // Change ImageButton into Start Icon
                    recordStop.setImageResource(R.drawable.play);

                    // Stop Timer
                    t.cancel();

                    // Update The Timer Text
                    recordTime.setText("00 : 00 : 00");
                }

                cnt ++;
                cnt %= 2;
                break;
            case R.id.recordList:
                startActivity(new Intent(this, MainActivity.class));
                this.finish();
                break;
        }
    }

    private void startService(Context context, Intent intent) {
        String[] permissions = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };

        if (hasPermissions(context, permissions)){
            intent.setClass(context, CallRecordService.class);
            intent.putExtra(AllKeys.MANUAL_RECORD, manualRecord);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                context.startForegroundService(intent);
            }else{
                context.startService(intent);
            }

        }else{
            log("Required permission is missing");
        }
    }

    int minute, seconds, hour;
    private Timer t;
    public void timer(){

        minute = 0; seconds = 0; hour = 0;

        t = new Timer("hello", true);

        t.schedule(new TimerTask() {
            @Override
            public void run() {
                recordTime.post(new Runnable() {
                    @Override
                    public void run() {
                        seconds++;
                        if (seconds == 60) {
                            seconds = 0;
                            minute++;
                        }
                        if (minute == 60) {
                            minute = 0;
                            hour++;
                        }
                        recordTime.setText(""
                                + (hour > 9 ? hour : ("0" + hour)) + " : "
                                + (minute > 9 ? minute : ("0" + minute))
                                + " : "
                                + (seconds > 9 ? seconds : "0" + seconds));
                    }
                });
            }
        },1000,1000);

    }
}
