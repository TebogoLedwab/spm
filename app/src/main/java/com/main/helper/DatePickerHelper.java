package com.main.helper;

import android.app.DatePickerDialog;
import android.content.Context;
import java.time.LocalDate;
import java.util.Calendar;

public class DatePickerHelper {

    // 1. Updated interface to use LocalDate
    public interface OnDateSelectedListener {
        void onDateSelected(LocalDate date);
    }

    public static void showDatePicker(Context context, OnDateSelectedListener listener) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // 2. Construct the LocalDate object (adjusting the 0-indexed month)
                    LocalDate localDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay);

                    if (listener != null) {
                        listener.onDateSelected(localDate);
                    }
                },
                year, month, day
        );

        // Calculate tomorrow's start time for restriction
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        tomorrow.set(Calendar.HOUR_OF_DAY, 0);
        tomorrow.set(Calendar.MINUTE, 0);
        tomorrow.set(Calendar.SECOND, 0);
        tomorrow.set(Calendar.MILLISECOND, 0);

        datePickerDialog.getDatePicker().setMinDate(tomorrow.getTimeInMillis());

        datePickerDialog.show();
    }
}
