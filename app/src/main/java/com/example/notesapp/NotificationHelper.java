package com.example.notesapp;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper {

    public static final String alarmChannelId = "alarmChannel";
    public static final String alarmChannelName = "Alarm Channel";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void createChannel(){
        NotificationChannel alarmChannel = new NotificationChannel(alarmChannelId, alarmChannelName, NotificationManager.IMPORTANCE_DEFAULT);
        alarmChannel.enableLights(true);
        alarmChannel.enableVibration(true);
        alarmChannel.setLightColor(R.color.design_default_color_on_primary);
        alarmChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(alarmChannel);
    }

    public NotificationManager getManager(){
        if(manager == null){
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return manager;
    }

    public NotificationCompat.Builder getAlarmChannelNotification(String title, String message){
        return  new NotificationCompat.Builder(getApplicationContext(), alarmChannelId)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_alarm_add_black_24dp);
    }
}
