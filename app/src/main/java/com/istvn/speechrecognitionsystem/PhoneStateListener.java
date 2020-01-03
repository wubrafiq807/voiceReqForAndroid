package com.istvn.speechrecognitionsystem;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.istvn.speechrecognitionsystem.datastorage.SaveDataOnPreference;
import com.istvn.speechrecognitionsystem.services.CallRecordService;
import com.istvn.speechrecognitionsystem.utility.AllKeys;

import static com.istvn.speechrecognitionsystem.utility.RunTimePermission.hasPermissions;
import static com.istvn.speechrecognitionsystem.utility.Utils.log;

public class PhoneStateListener extends BroadcastReceiver {

    static boolean incomingCall = false;
    private String mPhoneNumber;

    private SaveDataOnPreference saveDataOnPreference;

    /**
     * For Every new call this method will be call
     *
     * @param context: current uses context
     * @param intent:  Telephony Intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);

        // Call state type
        String event = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        // Call is ringing
        if (event.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            log("Ringing");
            // call type is incoming call
            incomingCall = true;
        } else if (event.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                            != PackageManager.PERMISSION_GRANTED) {
                return;
            } else {
                mPhoneNumber = tMgr.getLine1Number();
                //log("Own number: " + mPhoneNumber);
            }

            // Call is running
            log("Connected");
            if (saveDataOnPreference == null) {
                saveDataOnPreference = new SaveDataOnPreference(context);
            }

            if (saveDataOnPreference.getAutoRecord()) {
                log("Service Started");
                startService(context, intent);
            }

        } else if (event.equals(TelephonyManager.EXTRA_STATE_IDLE)) {

            //Call is disconnected or finished
            log("Call disconnected");
            if (saveDataOnPreference == null) {
                saveDataOnPreference = new SaveDataOnPreference(context);
            }

            if (saveDataOnPreference.getAutoRecord())
                context.stopService(new Intent(context, CallRecordService.class));
        }
    }

    /**
     * Start call recording service
     *
     * @param context
     * @param intent: Telephony Intent
     */
    private void startService(Context context, Intent intent) {
        String[] permissions = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_NUMBERS,
                Manifest.permission.READ_SMS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CONTACTS
        };

        if (hasPermissions(context, permissions)) {
            intent.setClass(context, CallRecordService.class);
            intent.putExtra(AllKeys.INCOMING_CALL, incomingCall);
            intent.putExtra(AllKeys.OWN_PHONE_NUMBER, mPhoneNumber);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }

        } else {
            log("Required permission is missing");
        }
    }
}
