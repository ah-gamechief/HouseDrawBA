package com.ba.housedrawba.Utils;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
    public static int TYPE_WIFI_AS = 1;
    public static int TYPE_MOBILE_AS = 2;
    public static int TYPE_NOT_CONNECTED_AS = 0;

    public static int getConnectivityASStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI_AS;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE_AS;
        }
        return TYPE_NOT_CONNECTED_AS;
    }

    public static String getConnectivityStatusStringAS(Context context) {
        int connAS = NetworkUtil.getConnectivityASStatus(context);
        String status = null;
        if (connAS == NetworkUtil.TYPE_WIFI_AS) {
            status = "Wifi enabled";
        } else if (connAS == NetworkUtil.TYPE_MOBILE_AS) {
            status = "Mobile data enabled";
        } else if (connAS == NetworkUtil.TYPE_NOT_CONNECTED_AS) {
            status = "Not connected to Internet";
        }
        return status;
    }
}
