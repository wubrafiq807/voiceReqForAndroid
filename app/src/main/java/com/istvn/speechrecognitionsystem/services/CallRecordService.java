package com.istvn.speechrecognitionsystem.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.TelephonyManager;

import com.istvn.speechrecognitionsystem.R;
import com.istvn.speechrecognitionsystem.datastorage.SaveDataOnPreference;
import com.istvn.speechrecognitionsystem.model.AudioUploadResponse;
import com.istvn.speechrecognitionsystem.network.GetDataService;
import com.istvn.speechrecognitionsystem.network.RetrofitClientInstance;
import com.istvn.speechrecognitionsystem.utility.AllKeys;
import com.istvn.speechrecognitionsystem.utility.FileAnalyser;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javazoom.jl.converter.Converter;
import javazoom.jl.decoder.JavaLayerException;

import static com.istvn.speechrecognitionsystem.utility.Utils.log;

/**
 * Phone call recorder background service
 * In every phone call this recorder will be call
 * From background it will record phone call
 * Save phone call into internal storage
 */
public class CallRecordService extends Service {

    private boolean incomingCall = false;
    private boolean manualRecord = false;
    private MediaRecorder recorder;
    private String number, startTime, endTime, ownPhoneNumber;
    private NotificationManagerCompat notificationManager;
    private String filePath;
    private String callFlag;
    private SaveDataOnPreference pref;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Called by the system every time a client explicitly starts the service by calling
     * @param intent : will receive a telephony Intent
     * @param flags : call status
     * @param startId: A unique integer representing this specific request
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            incomingCall = intent.getBooleanExtra(AllKeys.INCOMING_CALL, false);
            manualRecord = intent.getBooleanExtra(AllKeys.MANUAL_RECORD, false);
            number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            //log("Dialed / Incoming Number: " + number);
            ownPhoneNumber = intent.getStringExtra(AllKeys.OWN_PHONE_NUMBER);
            if (ownPhoneNumber == null || ownPhoneNumber.isEmpty())
                ownPhoneNumber = "Unknown";
            startRecording();
        }
        return START_STICKY;
    }

    /**
     * Yes! we need to record call
     * Lets record user call :)
     */
    private void startRecording() {

        if (recorder != null) {
            // recorder already running
            return;
        }
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        filePath = getFileName();
        recorder.setOutputFile(filePath);

        try {
            recorder.prepare();
            log("Recording start");

            Date date = new Date();
            long time = date.getTime();
            Timestamp ts = new Timestamp(time);
            startTime = ts.toString();

            recorder.start();
            showNotification();

        } catch (IOException e) {
            log("Prepare() failed");
            e.printStackTrace();
        }
    }

    /**
     * This method will return the audio file save location
     * @return filePath: path of audio file where we will save audio
     */
    public String getFileName() {

        String filePath = Environment.getExternalStorageDirectory().getPath();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        filePath += "/" + AllKeys.RECORD_DIRECTORY_NAME + "/" + date + "/";

        File file = new File(filePath);

        // When file is not already exist
        if (!file.exists()) {
            // Make a new directory with current file name
            file.mkdirs();

            // Create no media file
            createNoMedia(file.getAbsolutePath());
        }

        // Define call type
        String callType;
        if (manualRecord) {
            callType = "MANUAL_";
            callFlag = "1";
        } else {
            callType = incomingCall ? "IN_" : "OUT_";
            callFlag = "2";
        }

        String time = new SimpleDateFormat("hhmmss").format(new Date());

        // Phone number
        if (number == null) number = "Unknown";

        // Own Phone number
        if (ownPhoneNumber == null) ownPhoneNumber = "Unknown";

        // Audio save location
        String audioPath;
        if (manualRecord) {
            audioPath = file.getAbsolutePath() + "/" + callType + time + AllKeys.AUDIO_EXTENSION;
        } else {
            audioPath = file.getAbsolutePath() + "/CALL_" + callType + number + "_" + time + AllKeys.AUDIO_EXTENSION;
        }

        return audioPath;
    }

    /**
     * Show recording status on notification bar
     */
    public void showNotification() {

        String notificationTitle = "Recording";

        // For android oreo or higher we need to start notification in foreground
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(AllKeys.CHANNEL_ID,
                    notificationTitle,
                    NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, AllKeys.CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(AllKeys.NOTIFICATION_ID, notification);

        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, AllKeys.CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(notificationTitle)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setOngoing(true);

            notificationManager = NotificationManagerCompat.from(this);

            notificationManager.notify(AllKeys.NOTIFICATION_ID, builder.build());

        }
    }

    /**
     * NoMedia is a specific file type
     * It prevent media app to do not search a directory
     * @param path: file save location
     */
    private void createNoMedia(String path) {
        File file = new File(path + "/" + ".nomedia");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            log("Failed to create noMedia file");
        }
    }

    /**
     * Stop call recorder
     */
    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
        }
        log("recording stopped");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true);
        } else {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(AllKeys.NOTIFICATION_ID);
        }

        pref = new SaveDataOnPreference(this);

        if (callFlag.equals("1")){
            saveManualRecordingToServer(filePath);
        }else{
            saveCallRecordingToServer(filePath);
        }
    }

    private void saveCallRecordingToServer(String audioFile) {
        log("Called saveCallRecordingToServer");

        convertAudioAfterCall(audioFile);
    }

    private void convertAudioAfterCall(final String audioFile) {

        AndroidAudioConverter.with(this)
                .setFile(new File(audioFile))
                .setFormat(AudioFormat.WAV)
                .setCallback(new IConvertCallback() {
                    @Override
                    public void onSuccess(File convertedFile) {
                        log(convertedFile.getName());

                        File file = new File(audioFile);

                        FileAnalyser analyser = new FileAnalyser(file.getName());

                        log("info: " + file.getName() + ", "+ analyser.getPhoneNo()+ ", "+ ownPhoneNumber);

                        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), convertedFile);
                        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("audioFile",convertedFile.getName(),requestFile);

                        RequestBody userId = RequestBody.create(MediaType.parse("multipart/form-data"), pref.getUserId());
                        RequestBody type = RequestBody.create(MediaType.parse("multipart/form-data"), callFlag);
                        RequestBody end = RequestBody.create(MediaType.parse("multipart/form-data"), endTime);
                        RequestBody start = RequestBody.create(MediaType.parse("multipart/form-data"), startTime);
                        RequestBody callerPhoneNumber, receiverPhoneNumber;

                        if (incomingCall){
                            callerPhoneNumber = RequestBody.create(MediaType.parse("multipart/form-data"), analyser.getPhoneNo());
                            receiverPhoneNumber = RequestBody.create(MediaType.parse("multipart/form-data"), ownPhoneNumber);
                        }else {
                            callerPhoneNumber = RequestBody.create(MediaType.parse("multipart/form-data"), ownPhoneNumber);
                            receiverPhoneNumber = RequestBody.create(MediaType.parse("multipart/form-data"), analyser.getPhoneNo());
                        }

                        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
                        Call<AudioUploadResponse> call = service.callRecordUpload(userId,type, end, start,
                                callerPhoneNumber, receiverPhoneNumber, multipartBody);
                        call.enqueue(new Callback<AudioUploadResponse>() {
                            @Override
                            public void onResponse(Call<AudioUploadResponse> call, Response<AudioUploadResponse> response) {
                                log("" + response.body().getMessage());
                            }

                            @Override
                            public void onFailure(Call<AudioUploadResponse> call, Throwable t) {
                                log("Upload Error: "+t.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception error) {
                        log("Failed: " + error);

                    }
                }).convert();

    }

    private void convertAudio(String audioFile) {

        AndroidAudioConverter.with(this)
                .setFile(new File(audioFile))
                .setFormat(AudioFormat.WAV)
                .setCallback(new IConvertCallback() {
                    @Override
                    public void onSuccess(File convertedFile) {

                        log(convertedFile.getName());
                        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), convertedFile);
                        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("audioFile",convertedFile.getName(),requestFile);

                        RequestBody userId = RequestBody.create(MediaType.parse("multipart/form-data"), pref.getUserId());
                        RequestBody type = RequestBody.create(MediaType.parse("multipart/form-data"), callFlag);
                        RequestBody end = RequestBody.create(MediaType.parse("multipart/form-data"), endTime);
                        RequestBody start = RequestBody.create(MediaType.parse("multipart/form-data"), startTime);

                        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
                        Call<AudioUploadResponse> call = service.audioUpload(userId,type, end, start, multipartBody);
                        call.enqueue(new Callback<AudioUploadResponse>() {
                            @Override
                            public void onResponse(Call<AudioUploadResponse> call, Response<AudioUploadResponse> response) {
                                log("" + response.body().getMessage());
                            }

                            @Override
                            public void onFailure(Call<AudioUploadResponse> call, Throwable t) {
                                log("Upload Error: "+t.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception error) {
                        log("Failed: " + error);

                    }
                }).convert();
    }

    private void saveManualRecordingToServer(String audioFile) {

        log("Called saveManualRecordingToServer");
        File file = new File(audioFile);

        convertAudio(audioFile);
    }

    /**
     * App is closing, so stop recorder
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        Date date= new Date();
        long time = date.getTime();
        Timestamp ts = new Timestamp(time);
        endTime = ts.toString();

        stopRecording();
    }
}
