package com.studentscheduler;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateUtils {
    public static void chooseTime(Context context, Calendar dateInfo, SwitchCompat swAlert) {
        Calendar calendar = Calendar.getInstance();
        int hod = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, (timePickerView, selectedHourOfDay, selectedMinute) -> {
            dateInfo.set(Calendar.HOUR_OF_DAY, selectedHourOfDay);
            dateInfo.set(Calendar.MINUTE, selectedMinute);
            dateInfo.set(Calendar.SECOND, 0);
        }, hod, min, false);
        timePickerDialog.show();
        timePickerDialog.setOnCancelListener(dialogInterface -> swAlert.setChecked(false));
    }

    public static void chooseDate(Context context, TextView textView, Calendar dateInfo) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
            String selectedDate = selectedDayOfMonth + "/" + (selectedMonth + 1) + "/" + selectedYear;
            textView.setText(selectedDate);
            dateInfo.set(Calendar.YEAR, selectedYear);
            dateInfo.set(Calendar.MONTH, selectedMonth);
            dateInfo.set(Calendar.DAY_OF_MONTH, selectedDayOfMonth);
        }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    public static void scheduleAlarm(Context context, PendingIntent pendingIntent, Calendar datetime) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, datetime.getTimeInMillis(), pendingIntent);
    }

    public static void scheduleAlarm(Context context, String title, int nid, Calendar datetime) {
        Calendar current = Calendar.getInstance();
        if (datetime.compareTo(current) > 0) {
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("title", title);
            intent.putExtra("nid", nid);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, nid, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, datetime.getTimeInMillis(), pendingIntent);
        }
    }

    public static void cancelAlarm(Context context, PendingIntent pendingIntent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    public static void cancelAlarm(Context context, int id) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    public static void cancelAllAlarms(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= 34) {
            alarmManager.cancelAll();
        } else {
            Intent intent = new Intent(context, AlarmReceiver.class);
            for (int i = 1; i <= 100; i++) {
                PendingIntent pi1 = PendingIntent.getBroadcast(context, i, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                PendingIntent pi2 = PendingIntent.getBroadcast(context, i + 10000, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                PendingIntent pi3 = PendingIntent.getBroadcast(context, i + 20000, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                PendingIntent pi4 = PendingIntent.getBroadcast(context, i + 30000, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                PendingIntent pi5 = PendingIntent.getBroadcast(context, i + 40000, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                PendingIntent pi6 = PendingIntent.getBroadcast(context, i + 50000, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                alarmManager.cancel(pi1);
                alarmManager.cancel(pi2);
                alarmManager.cancel(pi3);
                alarmManager.cancel(pi4);
                alarmManager.cancel(pi5);
                alarmManager.cancel(pi6);
            }
        }
    }
}
