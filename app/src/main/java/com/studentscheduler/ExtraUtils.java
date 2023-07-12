package com.studentscheduler;

import android.database.Cursor;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class ExtraUtils {
    public static void updateNoItemsMessage(ArrayList<?> array, TextView tview) {
        if (array.size() > 0) tview.setVisibility(View.GONE);
        else tview.setVisibility(View.VISIBLE);
    }

    public static boolean fieldIsNotValid(EditText textField, Integer minLength, String errorMessage) {
        if (String.valueOf(textField.getText()).isEmpty() || String.valueOf(textField.getText()).length() < minLength) {
            textField.setError(errorMessage);
            textField.requestFocus();
            return true;
        }
        return false;
    }

    public static boolean canDeleteTerm(DatabaseManager DB, String termID) {
        String query = "Select * from " + DatabaseManager.TABLE_TERM_COURSES + " " +
                "       Where TID = " + termID + " ;";
        Cursor data = DB.runQuery(query);
        if (data != null) {
            if (data.moveToFirst()) {
                int idIndex = data.getColumnIndex("TID");
                if (idIndex != -1)
                    return false;
            }
            data.close();
        }
        return true;
    }
}
