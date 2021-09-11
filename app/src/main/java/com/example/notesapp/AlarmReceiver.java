package com.example.notesapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String id = intent.getStringExtra("id");
        String note_title = "No Title", note_text = "No Text";

        MyDataBaseHelper myDB = new MyDataBaseHelper(context);
        Cursor cursor = myDB.getNodeWithId(id);
        if (cursor.getCount() == 0) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,Integer.valueOf(id), intent, 0);
            alarmManager.cancel(pendingIntent);

        } else{
            cursor.moveToFirst();
            note_title = cursor.getString(1).equals("") ? "No Title" : cursor.getString(1);
            note_text = cursor.getString(2).equals("") ? "No Text" : cursor.getString(2);
            cursor.close();

            NotificationHelper notificationHelper = new NotificationHelper(context);
            NotificationCompat.Builder builder = notificationHelper.getAlarmChannelNotification(note_title,note_text);
            notificationHelper.getManager().notify(Integer.valueOf(id),builder.build());
        }
    }
}
