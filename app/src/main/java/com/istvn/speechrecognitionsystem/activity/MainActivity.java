package com.istvn.speechrecognitionsystem.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.istvn.speechrecognitionsystem.R;

import com.istvn.speechrecognitionsystem.Tools;
import com.istvn.speechrecognitionsystem.adapter.CallListRvAdapter;
import com.istvn.speechrecognitionsystem.datastorage.SaveDataOnPreference;
import com.istvn.speechrecognitionsystem.interfaces.ClickListener;
import com.istvn.speechrecognitionsystem.model.Audio;
import com.istvn.speechrecognitionsystem.services.VoIpCallDetection;
import com.istvn.speechrecognitionsystem.utility.AllKeys;
import com.istvn.speechrecognitionsystem.utility.FileAnalyser;
import com.istvn.speechrecognitionsystem.utility.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.ILoadCallback;

import static com.istvn.speechrecognitionsystem.utility.RunTimePermission.checkRequiredPermissions;
import static com.istvn.speechrecognitionsystem.utility.RunTimePermission.displayWhenNeverAskAgainDialog;
import static com.istvn.speechrecognitionsystem.utility.RunTimePermission.hasPermissions;
import static com.istvn.speechrecognitionsystem.utility.Utils.log;

public class MainActivity extends AppCompatActivity implements ClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        SeekBar.OnSeekBarChangeListener,
        MediaPlayer.OnCompletionListener{

    private ArrayList<Audio> audioList;
    @BindView(R.id.recycler_view)
    RecyclerView callListRv;
    @BindView(R.id.layout_refresh)
    SwipeRefreshLayout swapRefresh;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recordNotFoundTv)
    TextView noRecordFound;
    @BindView(R.id.seekBar2)
    SeekBar seekBar;
    @BindView(R.id.songCurrentDurationLabel)
    TextView songCurrentDurationLabel;
    @BindView(R.id.songTotalDurationLabel)
    TextView songTotalDurationLabel;
    @BindView(R.id.tv_number)
    TextView phoneNo;
    @BindView(R.id.tv_date_time)
    TextView dateTime;
    @BindView(R.id.bottomSheetRl)
    RelativeLayout rlBottomsheet;
    @BindView(R.id.btnPlayTop)
    AppCompatImageButton btnPlayTop;
    @BindView(R.id.bt_close)
    AppCompatImageButton btnClose;
    @BindView(R.id.sheet_header)
    RelativeLayout sheetHeader;
    @BindView(R.id.iv_call_type)
    ImageView callTypeIv;

    @BindView(R.id.appNameTv)
    TextView appTitle;

    private CallListRvAdapter callListRvAdapter;
    private BottomSheetBehavior bottomSheetBehavior;
    private MediaPlayer mediaPlayer;
    private int selectedAudioPosition = 0;
    private Handler handler;
    private boolean seekTrackTouch = false;

    private boolean audioPlaying = false;

    SaveDataOnPreference saveDataOnPreference;

    private ActionModeCallback actionModeCallback;
    private ActionMode mActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        saveDataOnPreference = new SaveDataOnPreference(this);
        ButterKnife.bind(this);
        initialize();

        AndroidAudioConverter.load(this, new ILoadCallback() {
            @Override
            public void onSuccess() {
                // Great!
                log("loaded");
            }
            @Override
            public void onFailure(Exception error) {
                // FFmpeg is not supported by device
            }
        });

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        boolean autoRecord = sharedPreferences.getBoolean(getResources().getString(R.string.switch_auto_record), true);

        // Set the updated auto record status in the Save data class
        saveDataOnPreference.setAutoRecord(autoRecord);

        setSupportActionBar(toolbar);
        // Set the Title
        appTitle.setText(getResources().getString(R.string.recording_list));

        // Runtime Permission checking
        if (!checkRequiredPermissions(this)){
            displayWhenNeverAskAgainDialog(this);
        }else {
            getCallList();
/*
            String main_data[] = {"data1", "is_primary", "data3", "data2", "data1",
                    "is_primary", "photo_uri", "mimetype"};
            Object object = getContentResolver().
                    query(Uri.withAppendedPath(android.provider.ContactsContract.Profile.CONTENT_URI, "data"),
                            main_data, "mimetype=?",
                            new String[]{"vnd.android.cursor.item/phone_v2"},
                            "is_primary DESC");
            String s1="";
            if (object != null) {
                do {
                    if (!((Cursor) (object)).moveToNext())
                        break;
                    // This is the phoneNumber
                    s1 =s1+"---"+ ((Cursor) (object)).getString(4);
                    log("Phone: " + ((Cursor) (object)).getString(4));
                } while (true);
                ((Cursor) (object)).close();
            }

            log("All Phone Number: " + s1);
*/
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ManualRecord.class));
                finish();
            }
        });



        Intent intent = new Intent(this, VoIpCallDetection.class);
        startService(intent);
        log("VoIpCallDetection Service Started...");

    }

    /**
     * Initialize all object, view
     */
    private void initialize() {
        handler = new Handler();
        recyclerView();
        swapRefresh.setColorSchemeResources(R.color.colorAccent);
        swapRefresh.setOnRefreshListener(this);
        btnPlayTop.setOnClickListener(btnClickListener);
        btnClose.setOnClickListener(btnClickListener);

        // bottom sheet {media player}
        bottomSheetBehavior = BottomSheetBehavior.from(rlBottomsheet);
        bottomSheetBehavior.setState(3);// 3: full bottom sheet
        bottomSheetBehavior.setPeekHeight(100);
        bottomSheetBehavior.setHideable(false);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);

        Tools.setSystemBarColor(this, R.color.colorPrimary);

        actionModeCallback = new ActionModeCallback();
    }

    /**
     * Menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Menu Item Actions
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.user_profile:
                startActivity(new Intent(this, UserProfile.class));
                return true;
            case R.id.recognition_list:
                startActivity(new Intent(this, RecognitionList.class));
                return true;
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Logout Method
     */
    private void logout() {
        saveDataOnPreference.logout();
        startActivity(new Intent(this, LoginActivity.class));
        this.finish();
    }

    /**
     * Recycler view initialize
     */
    public void recyclerView(){
        getCallList();
        callListRvAdapter = new CallListRvAdapter(this, audioList);
        callListRv.setHasFixedSize(true);
        callListRv.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL,false));
        callListRv.setAdapter(callListRvAdapter);
    }

    /**
     * Button click listener
     */
    View.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.bt_close:
                    rlBottomsheet.setVisibility(View.GONE);
                    setViewOnFinish();

                    break;
                case R.id.btnPlayTop:
                    if (mediaPlayer.isPlaying()){
                        pausePlayer();
                    }else{
                        resumePlayer();
                    }
                    break;
            }
        }
    };

    /**
     * Retrieve recording from phone storage
     * And make a array list call audio list
     */
    public void getCallList(){
        audioList = new ArrayList<>();

        // Audio directory path
        String path = Environment.getExternalStorageDirectory().toString().toLowerCase()+"/"+AllKeys.RECORD_DIRECTORY_NAME;
        File mainFolder = new File(path);

        // Checking is the main folder exist or not
        if (mainFolder.isDirectory()){

            // All sub folder under the main directory
            File[] listOfFolders = mainFolder.listFiles();
            if (listOfFolders != null) {
                for (File item : listOfFolders) {

                    // When file type is directory
                    if (item.isDirectory()) {

                        File subFolder = new File(path + "/" + item.getName());
                        File[] fileList = subFolder.listFiles();

                        for (File audio : fileList) {

                            // When file is not a audio file -> continue for next file
                            if (audio.getName().equals(".nomedia")) continue;

                            // Analise file name
                            FileAnalyser analyser = new FileAnalyser(audio.getName());

                            // Add a item to audio list
                            audioList.add(new Audio(analyser.getPhoneNo(),
                                    audio.getAbsolutePath(),
                                    analyser.getDateTime(item.getName()),
                                    analyser.getCallType(),
                                    analyser.getAudioDuration(this,
                                            audio.length(),
                                            audio.getAbsolutePath()))
                            );
                        }

                    }
                }
                // sort array as descending order
                Collections.reverse(audioList);
            }

        }

        // Visible empty message when audio list is empty
        if (audioList.size() < 1){
            noRecordFound.setVisibility(View.VISIBLE);
        }else{
            noRecordFound.setVisibility(View.GONE);
        }
    }

    /**
     * Runtime permissions callback function
     * @param requestCode: run time permission id
     * @param permissions: requested permissions
     * @param grantResults: granted permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Checking run time permission status
        if(requestCode == AllKeys.REQUEST_PERMISSION){

            // When all required permission is granted
            if (hasPermissions(this, permissions)){
                // Permission is granted so we can retrieve call list
                getCallList();
            }else{
                Toast.makeText(getApplicationContext(),this.getResources().getString(R.string.permission_denied),Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Recycler view on item Onclick listener from adapter class
     * @param position: audio position
     */
    @Override
    public void onClick(int position) {
        if (callListRvAdapter.getSelectedItemCount() > 0) {
            enableActionMode(position);
        }else {
            rlBottomsheet.setVisibility(View.VISIBLE);
            updateBottomSheet(position);
            selectedAudioPosition = position;
            startPlayer();
        }
    }

    private void enableActionMode(int position) {
        if (mActionMode == null) {
            mActionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        callListRvAdapter.toggleSelection(position);
        int count = callListRvAdapter.getSelectedItemCount();

        if (count == 0) {
            mActionMode.finish();
        } else {
            mActionMode.setTitle(String.valueOf(count));
            mActionMode.invalidate();
        }
    }

    /**
     * Recycler view on item onLongClick listener from adapter class
     * @param position: audio position
     */
    @Override
    public void onLongClick(int position) {
        log("Long Pressed" + position);
        enableActionMode(position);
    }

    /**
     * Update bottom sheet media player view
     * @param position: audio position from audio list view
     */
    private void updateBottomSheet(int position) {
        audioList.get(position);
        if (audioList.get(position).getCallType().equals("IN")){
            callTypeIv.setImageDrawable(getResources().getDrawable(R.drawable.call_in));
        }else{
            callTypeIv.setImageDrawable(getResources().getDrawable(R.drawable.calls_out));
        }

        phoneNo.setText(audioList.get(position).getPhoneNo());
        dateTime.setText(audioList.get(position).getDateTime());

    }

    /**
     * Recycler view refresh
     * */
    @Override
    public void onRefresh() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView();
                swapRefresh.setRefreshing(false);
            }
        },1000);

    }

    /**
     * Media player complete listener
     * @param mp: mediaPlayer
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        setViewOnFinish();
        log("Playing completed");
    }

    /**
     * Start audio player
     */
    private void startPlayer(){

        seekBar.setProgress(0);
        songCurrentDurationLabel.setText(getResources().getString(R.string.zero));
        mediaPlayer.reset();
        try {

            // Audio source add
            mediaPlayer.setDataSource(audioList.get(selectedAudioPosition).getPath());

            // Media player listener
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    mp.start();
                    startSeekHandler();

                    audioPlaying = true;
                    btnPlayTop.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_pause));
                    songTotalDurationLabel.setText(Utils.milliSecondsToTimer(mediaPlayer.getDuration()));
                }
            });

            // Start async task
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            log("Error"+e);
        }

    }


    /**
     * Pause media player
     */
    private void pausePlayer(){
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            stopSeekHandler();
            mediaPlayer.pause();
            btnPlayTop.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_play));
        }
    }

    /**
     * Resume media player
     */
    private void resumePlayer(){
        startSeekHandler();
        mediaPlayer.start();
        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
        btnPlayTop.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_pause));
    }

    /**
     *  Seek handler runner{update seek progress}
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            songCurrentDurationLabel.setText(Utils.milliSecondsToTimer(mediaPlayer.getCurrentPosition()));

            // Update seek bar
            if (!seekTrackTouch) {
                seekBar.setProgress(Utils.getProgressPercentage(mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration()));
            }

            log("Handler running");
            startSeekHandler();
        }
    };

    /**
     * Start seek handler
     * Each 500 millisecond seek bar will be update
     */
    private void startSeekHandler() {
        handler.postDelayed(mUpdateTimeTask,500);
    }

    /**
     * When audio play is completed
     * Then this method will be call
     */
    private void setViewOnFinish() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            seekBar.setProgress(100);
            songCurrentDurationLabel.setText(songTotalDurationLabel.getText().toString());
        }
        // Don't need seek bar so stop seek handler
        stopSeekHandler();
    }

    /**
     * Stop seek bar progressing
     */
    private void stopSeekHandler(){
        if (mUpdateTimeTask != null) handler.removeCallbacks(mUpdateTimeTask);
    }

    // Seek bar progress change
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    /**
     * Seekbar started
     * @param seekBar
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        seekTrackTouch = true;
        int currentPosition = Utils.progressToTimer(seekBar.getProgress(), mediaPlayer.getDuration());
        mediaPlayer.seekTo(currentPosition);
        songCurrentDurationLabel.setText(Utils.getProgressPercentage(mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration()));
    }

    /**
     * SeekBar stopped
     * @param seekBar
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        seekTrackTouch = false;
        int currentPosition = Utils.progressToTimer(seekBar.getProgress(), mediaPlayer.getDuration());
        mediaPlayer.seekTo(currentPosition);
        songCurrentDurationLabel.setText("" + Utils.milliSecondsToTimer(mediaPlayer.getCurrentPosition()));
    }

    /**
     * App is going to destroy
     * Release media player
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null){
            setViewOnFinish();
            mediaPlayer.release();
        }

        Intent intent = new Intent(this, VoIpCallDetection.class);
        stopService(intent);
        log("VoIpCallDetection Service Stopped.");
    }

    /**
     * App is going to pause mode, pause media player
     */
    @Override
    protected void onPause() {
        super.onPause();
        pausePlayer();
    }

    private class ActionModeCallback implements ActionMode.Callback{

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            Tools.setSystemBarColor(MainActivity.this, R.color.colorDarkBlue2);
            actionMode.getMenuInflater().inflate(R.menu.menu_delete, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            int id = menuItem.getItemId();
            if (id == R.id.action_delete) {
                deleteInbox();
                actionMode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            callListRvAdapter.clearSelections();
            mActionMode = null;
            Tools.setSystemBarColor(MainActivity.this, R.color.colorPrimary);
        }
    }

    private void deleteInbox() {
       List<Integer> selectedItemPositions = callListRvAdapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {

            deleteFiles(selectedItemPositions.get(i));
            callListRvAdapter.removeData(selectedItemPositions.get(i));
        }
        callListRvAdapter.notifyDataSetChanged();
    }

    private void deleteFiles(int position) {
        callListRvAdapter.deleteItemFromStorage(position);
    }
}
