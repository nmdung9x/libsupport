package com.nmd.utility.common;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.nmd.utility.DebugLog;
import com.nmd.utility.UtilLibs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeUtils {

    public static Date convertString(String value, String formatDate) {
        DateFormat format = new SimpleDateFormat(formatDate, Locale.getDefault());
        try {
            return format.parse(value);
        } catch (Exception e) {
            DebugLog.loge(e);
        }
        return new Date();
    }

    public static Date convertMillis(Long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        return calendar.getTime();
    }

    public static String parseDate(String value) {
        if (!value.trim().isEmpty()) {
            if (value.contains(" ")) {
                return parseDate2(value);
            }
            if (value.contains("/")) {
                ArrayList<String> arrayList = UtilLibs.splitComme2(value, "/");
                if (arrayList.size() == 3) {
                    String year = arrayList.get(2);
                    String month = arrayList.get(1);
                    String day = arrayList.get(0);

                    if (month.length() == 1) month = "0" + month;
                    if (day.length() == 1) day = "0" + day;

                    return year + "-" + month + "-" + day;
                }
            }
        }
        return "";
    }

    public static String parseDate2(String value) {
        if (!value.trim().isEmpty()) {
            if (value.contains(" ")) {
                ArrayList<String> arrayList = UtilLibs.splitComme2(value, " ");
                if (arrayList.size() == 2) {
                    return parseDate(arrayList.get(0));
                }
            } else {
                return parseDate(value);
            }
        }
        return "";
    }

    public static String parseTime(String value) {
        if (!value.trim().isEmpty()) {
            if (value.contains(" ")) {
                ArrayList<String> arrayList = UtilLibs.splitComme2(value, " ");
                if (arrayList.size() == 2) {
                    return arrayList.get(1);
                }
            } else {
                return value;
            }
        }
        return value;
    }

    public interface OnGetDatePickerResult {
        void getResult(Date result, String resultText);
    }

    public static void showDatePicker(final Context context, boolean showTimePicker, OnGetDatePickerResult callback) {
        showDatePicker(context, "", showTimePicker, callback);
    }

    public static void showTimePicker(Context context, final String date, OnGetDatePickerResult callback) {
        showTimePicker(context, "", date, callback);
    }

    public static void showDatePicker(final Context context, String defaultDate, final boolean showTimePicker, final OnGetDatePickerResult callback) {
        final Calendar current = Calendar.getInstance();
        if (!defaultDate.isEmpty()) {
            current.setTimeInMillis(parseTimeToMilis(defaultDate, "dd/MM/yyyy"));
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                long result_milliseconds = UtilLibs.parseTimeToMilliseconds(dayOfMonth, monthOfYear + 1, year, 1, 0, 0);
                String result = UtilLibs.getDateTime(result_milliseconds, "dd/MM/yyyy");
                DebugLog.loge(result);
                if (showTimePicker) showTimePicker(context, result, callback);
                else {
                    if (callback != null) callback.getResult(convertMillis(result_milliseconds), result);
                }
            }
        }, current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public static void showTimePicker(Context context, String defaultTime, final String date, final OnGetDatePickerResult callback) {
        final Calendar current = Calendar.getInstance();
        if (!defaultTime.isEmpty()) {
            current.setTimeInMillis(parseTimeToMilis(defaultTime, "HH:mm"));
        }
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String result = hourOfDay + ":" + minute;
                if (date.isEmpty()) {
                    if (callback != null) callback.getResult(convertString(result, "HH:mm"), result);
                } else {
                    if (callback != null) callback.getResult(convertString(result, "dd/MM/yyyy HH:mm"), date + " " + result);
                }
            }
        }, current.get(Calendar.HOUR_OF_DAY), current.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    public static void showDatePicker(final Context context, final TextView textView, final boolean showTimePicker) {
        final Calendar getDate = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                long result_milliseconds = UtilLibs.parseTimeToMilliseconds(dayOfMonth, monthOfYear + 1, year, 1, 0, 0);
                String result = UtilLibs.getDateTime(result_milliseconds, "dd/MM/yyyy");
                DebugLog.loge(result);
                if (showTimePicker) showTimePicker(context, result, textView);
                else {
                    textView.setText(result);
                    textView.setTextColor(Color.parseColor("#5A6487"));
                }
            }
        }, getDate.get(Calendar.YEAR), getDate.get(Calendar.MONTH), getDate.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public static void showTimePicker(Context context, final String date, final TextView textView) {
        final Calendar getDate = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String result = hourOfDay + ":" + minute;
                if (date.isEmpty()) {
                    textView.setText(result);
                } else {
                    textView.setText(date + " " + result);
                }
                textView.setTextColor(Color.parseColor("#5A6487"));
            }
        }, getDate.get(Calendar.HOUR_OF_DAY), getDate.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    public static void showDatePicker(final Context context, String defaultDate, Date minDate, Date maxDate, final OnGetDatePickerResult callback) {
        final Calendar current = Calendar.getInstance();
        if (!defaultDate.isEmpty()) {
            current.setTimeInMillis(parseTimeToMilis(defaultDate, "dd/MM/yyyy"));
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                long result_milliseconds = UtilLibs.parseTimeToMilliseconds(dayOfMonth, monthOfYear + 1, year, 1, 0, 0);
                String result = UtilLibs.getDateTime(result_milliseconds, "dd/MM/yyyy");
                DebugLog.loge(result);
                if (callback != null) callback.getResult(convertMillis(result_milliseconds), result);
            }
        }, current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DAY_OF_MONTH));
        if (minDate != null) datePickerDialog.getDatePicker().setMinDate(minDate.getTime());
        if (maxDate != null) datePickerDialog.getDatePicker().setMaxDate(maxDate.getTime());
        datePickerDialog.show();
    }

    public static long parseTimeToMilis(String value, String dateFormat) {
        try {
            DateFormat format = new SimpleDateFormat(dateFormat, Locale.getDefault());
            Date date = format.parse(value);
            return date.getTime();
        } catch (Exception e) {
            DebugLog.loge(e);
        }

        return 0;
    }

    public static String formatDate(Date date, String dateFormat) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.getDefault());
            return format.format(date);
        } catch (Exception e) {
            DebugLog.loge(e);
        }

        return "";
    }

    public static Date checkDate0(String value) {
        if (value.contains("T") && value.contains("Z")) {
            TimeZone utc = TimeZone.getTimeZone("UTC");
            try {
                SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'");
                sourceFormat.setTimeZone(utc);
                return sourceFormat.parse(value);
            } catch (Exception e) {
                DebugLog.logi(e);
                DebugLog.loge("parse again");
                try {
                    SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    sourceFormat.setTimeZone(utc);
                    Date result = sourceFormat.parse(value);
                    DebugLog.loge("parse success");
                    return result;
                } catch (Exception e2) {
                    DebugLog.loge(e2);
                }
            }
        }

        return new Date();
    }

    public static String checkDate(String value) {
        if (value.contains("T") && value.contains("Z")) {
            try {
                SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date convertedDate = checkDate0(value);
                String result = destFormat.format(convertedDate);
//                DebugLog.loge(result);
                return result;
            } catch (Exception e) {
                DebugLog.loge(e);
            }
        }

        String tmp = value.replaceAll("T", " ");
        return tmp.replaceAll("Z", "");
    }

    public static String parseDateTime(String value, String formatFrom, String formatTo) {
        if (value.trim().equals("null")) return "";
        if (value.trim().isEmpty()) return "";
        value = checkDate(value);
        String result;
        try {
            DateFormat format = new SimpleDateFormat(formatFrom, Locale.getDefault());
            Date date = format.parse(value);

            DateFormat dateFormat = new SimpleDateFormat(formatTo, Locale.getDefault());
            result = dateFormat.format(date);
        } catch (Exception e) {
            DebugLog.loge(e);
            return value;
        }
        return result;
    }

    public static int checkDayOfWeek(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        String value = sdf.format(date);
        if (value.equalsIgnoreCase("Monday")) {
            return 2;
        } else if (value.equalsIgnoreCase("Tuesday")) {
            return 3;
        } else if (value.equalsIgnoreCase("Wednesday")) {
            return 4;
        } else if (value.equalsIgnoreCase("Thursday")) {
            return 5;
        } else if (value.equalsIgnoreCase("Friday")) {
            return 6;
        } else if (value.equalsIgnoreCase("Saturday")) {
            return 7;
        } else if (value.equalsIgnoreCase("Sunday")) {
            return 1;
        }
        return 1;
    }

    public static Date yesterdayDate() {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        return yesterday.getTime();
    }

    public static Date sevenDayBeforeDate() {
        Calendar sevenDayBefore = Calendar.getInstance();
        sevenDayBefore.add(Calendar.DATE, -6);
        return sevenDayBefore.getTime();
    }

    public static Date firstDayOfWeek() {
        Calendar firstDayOfWeek = Calendar.getInstance();
        if (firstDayOfWeek.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            firstDayOfWeek.add(Calendar.WEEK_OF_YEAR, -1);
        }
        firstDayOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return firstDayOfWeek.getTime();
    }

    public static Date lastDayOfWeakDate() {
        Calendar today = Calendar.getInstance();
        Calendar lastDayOfWeak = Calendar.getInstance();
        if (lastDayOfWeak.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            lastDayOfWeak.add(Calendar.WEEK_OF_YEAR, 1);
        }
        lastDayOfWeak.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        if(lastDayOfWeak.after(today)){
            lastDayOfWeak = today;
        }
        return lastDayOfWeak.getTime();
    }

    public static Date firstDayOfLastWeekDate() {
        Calendar firstDayOfLastWeek = Calendar.getInstance();
        if (firstDayOfLastWeek.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            firstDayOfLastWeek.add(Calendar.WEEK_OF_YEAR, -2);
        } else {
            firstDayOfLastWeek.add(Calendar.WEEK_OF_YEAR, -1);
        }
        firstDayOfLastWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return firstDayOfLastWeek.getTime();
    }

    public static Date lastDayOfLastWeakDate() {
        Calendar lastDayOfLastWeak = Calendar.getInstance();
        if (lastDayOfLastWeak.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            lastDayOfLastWeak.add(Calendar.WEEK_OF_YEAR, -1);
        } else {
            lastDayOfLastWeak.add(Calendar.WEEK_OF_YEAR, 0);
        }
        lastDayOfLastWeak.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return lastDayOfLastWeak.getTime();
    }

    public static Date last30DayDate() {
        Calendar last30DayStart = Calendar.getInstance();
        last30DayStart.add(Calendar.DAY_OF_WEEK, -29);
        return last30DayStart.getTime();
    }

    public static Date thisMonthStartDate() {
        Calendar thisMonthStart = Calendar.getInstance();
        thisMonthStart.set(Calendar.DAY_OF_MONTH, 1);
        return thisMonthStart.getTime();
    }

    public static Date thisMonthEndDate() {
        Calendar today = Calendar.getInstance();
        Calendar thisMonthEnd = Calendar.getInstance();
        thisMonthEnd.set(Calendar.DAY_OF_MONTH, thisMonthEnd.getActualMaximum(Calendar.DAY_OF_MONTH));
        if (thisMonthEnd.after(today)){
            thisMonthEnd = today;
        }
        return thisMonthEnd.getTime();
    }

    public static Date lastMonthStartDate() {
        Calendar lastMonthStart = Calendar.getInstance();
        lastMonthStart.add(Calendar.MONTH, -1);
        lastMonthStart.set(Calendar.DAY_OF_MONTH, 1);
        return lastMonthStart.getTime();
    }

    public static Date lastMonthEndDate() {
        Calendar lastMonthEnd = Calendar.getInstance();
        lastMonthEnd.add(Calendar.MONTH, -1);
        lastMonthEnd.set(Calendar.DAY_OF_MONTH, lastMonthEnd.getActualMaximum(Calendar.DAY_OF_MONTH));
        return lastMonthEnd.getTime();
    }

    public static Date lastSixMonthDate() {
        Calendar lastMonthEnd = Calendar.getInstance();
        lastMonthEnd.add(Calendar.MONTH, -6);
        return lastMonthEnd.getTime();
    }

    public static Date lastYearDate() {
        Calendar lastMonthEnd = Calendar.getInstance();
        lastMonthEnd.add(Calendar.YEAR, -1);
        return lastMonthEnd.getTime();
    }
}
