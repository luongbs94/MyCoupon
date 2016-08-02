package com.ln.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ln.app.MainApplication;

/**
 * Created by Nhahv on 6/29/2016.
 * <></>
 */
public class ConnectivityReceiver extends BroadcastReceiver {

    public static ConnectivityReceiverListener mListener;

    public ConnectivityReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isConnect = isConnect();
        if (mListener != null) {
            mListener.onNetworkConnectChange(isConnect);
        }
    }

    public static boolean isConnect() {

        ConnectivityManager cm = (ConnectivityManager) MainApplication.getInstance()
                .getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public interface ConnectivityReceiverListener {

        void onNetworkConnectChange(boolean isConnect);
    }
}
