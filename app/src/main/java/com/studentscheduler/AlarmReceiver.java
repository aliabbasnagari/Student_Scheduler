package com.studentscheduler;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Display a toast message
        String tt = intent.getStringExtra("title");
        int nid = intent.getIntExtra("nid", 0);
        Toast.makeText(context, "Your alert message " + tt, Toast.LENGTH_SHORT).show();

        // You can also perform any other desired action here
        NotificationUtils.showNotification(context, nid, context.getString(R.string.app_name), tt);
    }
}

