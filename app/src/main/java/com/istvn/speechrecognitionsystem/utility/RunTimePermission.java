package com.istvn.speechrecognitionsystem.utility;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

/**
 * All runtime permission handler
 * Checking permission availability,
 * Request for new permissions etc
 */
public class RunTimePermission {

    /**
     * Checking permission availability for specific permission
     * @param context: current activity
     * @param permissions: required permissions
     * @return permissions status
     */
    public static boolean hasPermissions(Context context, String... permissions){
        if (context != null && permissions != null){
            for (String permission: permissions){
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
                    return false;// in case permission denied
                }
            }
        }
        return true;// permission granted
    }

    /**
     * Checking permissions, if permission required then it will show dialog for permission
     * @param activity: current main thread
     * @param permissions: all required permissions
     * @return permissions status
     */
    public static boolean checkPermissionWithDialog(Activity activity, String[] permissions){
        if (Build.VERSION.SDK_INT >= 23 && !hasPermissions(activity, permissions)){
            ActivityCompat.requestPermissions(activity, permissions, AllKeys.REQUEST_PERMISSION);
        }
        return true;// Bellow 23 do not require run time permission
    }

    /**
     * Checking predefined required permissions status
     * @param activity: current using main thread
     * @return permissions status
     */
    public static boolean checkRequiredPermissions(Activity activity){
        String[] permissions = {Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_NUMBERS,
                Manifest.permission.READ_SMS,
                Manifest.permission.READ_CONTACTS
        };
        return checkPermissionWithDialog(activity, permissions);
    }

    /**
     * Show a dialog to go setting page to allow required permission
     * @param context: current uses activity
     */
    public static void displayWhenNeverAskAgainDialog(final Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Please allow the permissions to use this features \n"
                + "Tap Settings -> Select Permissions and Enable permission");

        builder.setCancelable(false);
        builder.setPositiveButton("SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", context.getPackageName(),null );
                intent.setData(uri);
                context.startActivity(intent);
            }
        });

        builder.setNegativeButton("Not now", null);
        builder.show();
    }
}
