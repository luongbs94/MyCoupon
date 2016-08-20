package com.ln.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.ln.app.MainApplication;

/**
 * Created by luongnguyen on 6/17/16.
 * <></>
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";


    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);


//        MainApplication.setDeviceToken(refreshedToken);
        writePreferences(MainApplication.DEVICE_TOKEN, refreshedToken);
    }

    private void writePreferences(String key, String token) {
        MainApplication
                .getPreferences()
                .edit()
                .putString(key, token)
                .apply();

    }
}