package com.example.notesapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class SetAlarmActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private String id, period = "never";
    private ListView periodsListView;
    private List<String> periods;
    private Button choose_time_button, set_alarm_button, drop_alarm_button;
    private Integer hourOfDay, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        getAndSetIntentData();

        periods = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.periods)));
        periodsListView = findViewById(R.id.periodsListView);
        periodsListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, periods);
        periodsListView.setAdapter(adapter);
        periodsListView.setItemChecked(0,true);
        periodsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        period = "never";
                        break;
                    case 1:
                        period = "2min";
                        break;
                    case 2:
                        period = "5min";
                        break;
                    case 3:
                        period = "1hour";
                        break;
                    case 4:
                        period = "1day";
                        break;
                }
            }
        });

        choose_time_button = findViewById(R.id.choose_time_button);
        choose_time_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        set_alarm_button = findViewById(R.id.set_alarm_button);
        set_alarm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);

                startAlarm(c);

            }
        });

        drop_alarm_button = findViewById(R.id.drop_alarm_button);
        drop_alarm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dropAlarm();
            }
        });
    }

    void getAndSetIntentData(){
        if(getIntent().hasExtra("id") && getIntent().hasExtra("header") && getIntent().hasExtra("text")){
            id = getIntent().getStringExtra("id");
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.hourOfDay = hourOfDay;
        this.minute = minute;
        set_alarm_button.setVisibility(View.VISIBLE);

    }

    private void startAlarm(Calendar c){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setAction(id);
        intent.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Integer.valueOf(id), intent, 0);
        switch (period){
            case "never":
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                Toast.makeText(this, "Alarm was set to " +
                        DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime()), Toast.LENGTH_SHORT).show();
                break;
            case "2min":
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 1000 * 60 * 2 , pendingIntent);
                Toast.makeText(this, "Alarm was set to " +
                        DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime()) + " with interval for 2 minutes", Toast.LENGTH_SHORT).show();
                break;
            case "5min":
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 1000 * 60 * 5 , pendingIntent);
                Toast.makeText(this, "Alarm was set to " +
                        DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime()) + " with interval for 5 minutes", Toast.LENGTH_SHORT).show();
                break;
            case "1hour":
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 1000 * 60 * 60 , pendingIntent);
                Toast.makeText(this, "Alarm was set to " +
                        DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime()) + " with interval for 1 hour", Toast.LENGTH_SHORT).show();
                break;
            case "1day":
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 1000 * 60 * 60 * 24 , pendingIntent);
                Toast.makeText(this, "Alarm was set to " +
                        DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime()) + " with interval for 1 day", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void dropAlarm(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setAction(id);
        intent.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Integer.valueOf(id), intent, 0);

        alarmManager.cancel(pendingIntent);
        Toast.makeText(SetAlarmActivity.this, "Alarm was dropped for this note" + id, Toast.LENGTH_SHORT).show();
    }
}
