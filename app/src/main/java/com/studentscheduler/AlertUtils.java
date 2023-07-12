package com.studentscheduler;

import android.app.AlertDialog;
import android.content.Context;

public class AlertUtils {
    public static void showAlert(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
