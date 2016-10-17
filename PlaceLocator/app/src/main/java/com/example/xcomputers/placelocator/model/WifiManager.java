package com.example.xcomputers.placelocator.model;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

/**
 * Created by svetlio on 17.10.2016 г..
 */

public class WifiManager {
    private Activity activity;
    private static WifiManager myInstance = null;
    public static WifiManager getInstance(Activity activity){
        if(myInstance == null){
            myInstance = new WifiManager(activity);
        }
        else{
            myInstance.setActivity(activity);
        }
        return myInstance;
    }
    private WifiManager(Activity activity){
        this.activity = activity;
    }
    private void setActivity(Activity activity){
        this.activity = activity;
    }
    public AlertDialog promptUserToTurnOnWifi() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        AlertDialog alertDialog = null;
        if (!isConnectingToInternet()) {
            builder.setTitle("Internet Services Not Active");
            builder.setMessage("Please enable Internet Services");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Show location settings when the user acknowledges the alert dialog
                    if(!isConnectingToInternet()) {
                        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        activity.startActivity(intent);
                    }
                }
            });

            alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
        return alertDialog;
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        } else {
            if (connectivityManager != null) {
                //noinspection deprecation
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}

