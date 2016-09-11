package com.ln.firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ln.app.MainApplication;
import com.ln.mycoupon.FirstActivity;
import com.ln.mycoupon.R;

/**
 * Created by luongnguyen on 6/17/16.
 * <></>
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final int NOTIFICATION_ID = 9083150;


    @Override
    public void onCreate() {
        super.onCreate();

        startForeground(NOTIFICATION_ID, createNotification());

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: ");

        Log.d(TAG, "From: " + remoteMessage.getData());
        Log.d(TAG, "get notification");
        Log.d(TAG, "Notification NewsOfCustomer Body: " + remoteMessage.toString());

        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("message");


        sendNotification(message, title);


    }

    private Notification createNotification() {
        Builder builder = new Builder(this);
        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Mycoupon");
        builder.setContentText("Tap to see my coupon");
        builder.setOngoing(true);
        builder.setPriority(-2);
        builder.setCategory("service");
//        134217728
        builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, FirstActivity.class), 134217728));
        return builder.build();
    }

    private void sendNotification(String messageBody, String title) {
        Intent intent = new Intent(this, FirstActivity.class);
        intent.putExtra(MainApplication.PUSH_NOTIFICATION, MainApplication.NOTIFICATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
