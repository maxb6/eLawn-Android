/*
References:
Coding in Flow, Notifications Tutorial Part 1 - NOTIFICATION CHANNELS - Android Studio Tutorial. [Video] Available: https://www.youtube.com/watch?v=tTbd1Mfi-Sk. [Accessed: 01-Apr-2021].
Coding in Flow, How to Start a Foreground Service in Android (With Notification Channels). [Video] Available: https://www.youtube.com/watch?v=FbpD5RZtbCc>. [Accessed: 02-Apr-2021].
 */

package com.example.elawn_android;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.elawn_android.Service.App;

public class MyService extends Service {

    //this method is called the first time we create our service
    @Override
    public void onCreate() {
        super.onCreate();
    }

    //this method is called everytime on start service is called
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, MainActivity2.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);

        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_1_ID)
                .setSmallIcon(R.drawable.elawn_logo)
                .setContentTitle("E-LAWN")
                .setContentText("E-LAWN mower battery low")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1,notification);
        stopForeground(false);
        return START_STICKY;

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
