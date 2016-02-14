package com.ccss.youthvolunteer.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class CheckNetworkConnection {
    private static final String TAG = CheckNetworkConnection.class.getSimpleName();

    public static boolean isInternetAvailable(Context context) {
        boolean connectedViaWifi = false;
        boolean connectedViaMobile = false;

        NetworkInfo info = ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (info == null) {
            Log.d(TAG, "no internet connection");
            return false;
        } else {
            if (info.isConnected()) {
                Log.d(TAG, " internet connection available...");
                return true;
            } else {
                Log.d(TAG, " internet connection");
                return true;
            }
        }
    }
}
