package com.nmd.utility.common;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class PermissionCheck {

    public static boolean checkPermission(Activity activity, String permission) {
        return checkPermission(activity, permission, false, 0);
    }

    public static boolean checkPermission(Activity activity, String permission, boolean request, int request_code) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                if (request) ActivityCompat.requestPermissions(activity, new String[]{permission}, request_code);
                return false;
            }
        }
        return true;
    }

    public static boolean checkPermission(Activity activity, String[] permissions) {
        return checkPermission(activity, permissions, false, 0);
    }

    public static boolean checkPermission(Activity activity, String[] permissions, boolean request, int request_code) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissions.length > 0) {
                ArrayList<String> arrayTmp = new ArrayList<>();
                for (String permission : permissions) {
                    if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                        arrayTmp.add(permission);
                    }
                }
                if (arrayTmp.size() > 0) {
                    String[] permission_requests = new String[arrayTmp.size()];
                    arrayTmp.toArray(permission_requests);
                    if (request) ActivityCompat.requestPermissions(activity, permission_requests, request_code);
                    return false;
                }
            }
        }
        return true;
    }

    public static String[] listStoragePermission() {
        return new String[] { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE };
    }

    public static String[] listLocationPermission() {
        return new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION };
    }

    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionCheck.REQUEST_PERMISSIONS: {
                DebugLog.loge("countError: "+countError);
                if (countError < 3) {
                    checkPermission();
                } else {
                    //TODO something!!
                    onBackPressed();
                }
            }
        }
    }
    */
}
