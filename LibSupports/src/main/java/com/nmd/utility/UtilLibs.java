package com.nmd.utility;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.DecimalFormat;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Telephony;
import android.text.Html;
import android.text.Spannable;
import android.text.method.ScrollingMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.RequiresPermission;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.multidex.BuildConfig;

import com.nmd.utility.common.Aes;
import com.nmd.utility.other.Data;
import com.nmd.utility.other.ResizeHeightAnimation;
import com.nmd.utility.other.ResizeWidthAnimation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.regex.Pattern;

@SuppressLint({ "SimpleDateFormat", "InlinedApi", "DefaultLocale" })
@SuppressWarnings("unused")
public class UtilLibs {
	public enum Keys {
		LIBS_SUPP_IS_APP_CRASH_1, LIBS_SUPP_IS_APP_CRASH_0
	}

	public static boolean isAppCrash(Context context) {
		if (SharedPreference.get(context, Keys.LIBS_SUPP_IS_APP_CRASH_1.name(), "0").equals("1")) {
			SharedPreference.set(context, Keys.LIBS_SUPP_IS_APP_CRASH_0.name(), "0");
			SharedPreference.set(context, Keys.LIBS_SUPP_IS_APP_CRASH_1.name(), "0");
			return true;
		} else {
			return false;
		}
	}

	public static boolean isDeviceRooted() {
		String[] locations = {"/system/bin/", "/system/xbin/", "/sbin/", "/system/sd/xbin/",
				"/system/bin/failsafe/", "/data/local/xbin/", "/data/local/bin/", "/data/local/",
				"/system/sbin/", "/usr/bin/", "/vendor/bin/"};
		for (String location : locations) {
			if (new File(location + "su").exists()) {
				return true;
			}
		}
		return false;
	}

	@SuppressLint("PackageManagerGetSignatures")
	public static String getSignatures(Context context, String algorithm, boolean showColon) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(BuildConfig.APPLICATION_ID, PackageManager.GET_SIGNATURES);
			for (Signature s : info.signatures) {
				MessageDigest md = MessageDigest.getInstance(algorithm);
				md.update(s.toByteArray());
				final byte[] d = md.digest();
				final StringBuilder sb = new StringBuilder();
				for (int i = 0; i < d.length; i++) {
					if (i != 0 && showColon) sb.append(":");
					int b = d[i] & 0xff;
					String h = Integer.toHexString(b);
					if (h.length() == 1) sb.append("0");
					sb.append(h);
				}
				return sb.toString();
			}
		} catch (Exception e) {
			DebugLog.loge(e);
		}
		return "";
	}

	/**
	 * Get Time Zone In Local.
	 *
	 * @return (int) -12 to 14
	 */
	public static long getTimeZoneInLocal() {
		return TimeZone.getDefault().getRawOffset() / 1000 / 3600;
	}

	public static long getCurrentTimeMillisByTimeZone(int timezone) {
		return System.currentTimeMillis() - ((getTimeZoneInLocal() - timezone) * 3600 * 1000);

	}

	/**
	 * Get DateTime.
	 *
	 * @param dateTimeInMillis
	 *            datetime in milliseconds
	 * @param format
	 *            like "yyyy/MM/dd HH:mm:ss"
	 * 
	 * @return (String) result allow format
	 */
	public static String getDateTime(Object dateTimeInMillis, String format) {
		long value = 0;
		try {
			value = checkLongValue(String.valueOf(dateTimeInMillis));
		} catch (Exception e) {
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(value);
	}

	public static long parseTimeToMillis(String value, String dateFormat) {
		try {
			DateFormat format = new SimpleDateFormat(dateFormat, Locale.US);
			Date date = format.parse(value);
			return date == null ? 0 : date.getTime();
		} catch (Exception e) {
			DebugLog.loge(e);
		}

		return 0;
	}

	public static int checkDayOfWeek(long dateTimeInMilliseconds) {
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.ENGLISH);
		String value = sdf.format(dateTimeInMilliseconds);
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

	public static long parseTimeToMilliseconds(int dayOfMonth, int month, int year, int hour, int minute, int second) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);

		return calendar.getTimeInMillis();
	}

	/**
	 * Get DateTime.
	 *
	 * @param date
	 *            like "ddMMyyyy"
	 * 
	 * @return (String) result allow format "yyyyMMdd"
	 */
	public static String ddMMyyyy2yyyyMMdd(String date) {
		String result = "";
		String day = "";
		String month = "";
		String year = "";

		if (date.contains("-")) {
			String[] arrSp = date.split("-");
			day = arrSp[0];
			month = arrSp[1];
			year = arrSp[2];

			if (day.length() == 1) {
				day = "0" + day;
			}
			if (month.length() == 1) {
				month = "0" + month;
			}

			result = year + "-" + month + "-" + day;
		}
		if (date.contains("\\/")) {
			String[] arrSp = date.split("\\/");
			day = arrSp[0];
			month = arrSp[1];
			year = arrSp[2];

			if (day.length() == 1) {
				day = "0" + day;
			}
			if (month.length() == 1) {
				month = "0" + month;
			}

			result = year + "\\/" + month + "\\/" + day;
		}

		return result;
	}

	/**
	 * Get DateTime.
	 *
	 * @param date
	 *            like "yyyyMMdd"
	 * 
	 * @return (String) result allow format "ddMMyyyy"
	 */
	public static String yyyyMMdd2ddMMyyyy(String date) {
		String result = "";
		String day = "";
		String month = "";
		String year = "";

		if (date.contains("-")) {
			String[] arrSp = date.split("-");
			day = arrSp[2];
			month = arrSp[1];
			year = arrSp[0];

			if (day.length() == 1) {
				day = "0" + day;
			}
			if (month.length() == 1) {
				month = "0" + month;
			}

			result = day + "-" + month + "-" + year;
		}

		if (date.contains("\\/")) {
			String[] arrSp = date.split("\\/");
			day = arrSp[2];
			month = arrSp[1];
			year = arrSp[0];

			if (day.length() == 1) {
				day = "0" + day;
			}
			if (month.length() == 1) {
				month = "0" + month;
			}

			result = day + "\\/" + month + "\\/" + year;
		}

		return result;
	}

	/**
	 * Make a standard toast that just contains a text view.
	 *
	 * @param context
	 *            The context to use. Usually your Application or Activity
	 *            object.
	 * @param message
	 *            The text to show. Can be formatted text.
	 * 
	 */
	public static void showToast(Context context, String message) {
		if (!message.isEmpty()) {
			Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 230);
			toast.show();
		}
	}
	
	public static void showToast(Object obj) {
		if (UtilityMain.mContext == null) return;
		if(obj == null) return;
		showToast(UtilityMain.mContext, String.valueOf(obj));
	}

	/**
	 * Make a standard toast that just contains a text view and show line into
	 * code on logcat.
	 *
	 * @param context
	 *            The context to use. Usually your Application or Activity
	 *            object.
	 * @param message
	 *            The text to show. Can be formatted text.
	 * 
	 */
	public static void showToastDebug(Context context, String message) {
		if (!message.isEmpty()) {
			DebugLog.loge(message);
			String fullClassName = Thread.currentThread().getStackTrace()[3].getClassName();
			String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
			if (className.contains("$")) {
				className = className.substring(0, className.lastIndexOf("$"));
			}
			String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
			int lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();
			String debugData = "className: " + className + "\nmethodName: " + methodName + "\nlineNumber: " + lineNumber;

			Toast toast = Toast.makeText(context, debugData + "\n \n" + message, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 230);
			toast.show();
		}
	}

	/**
	 * Make a standard toast that just contains a text view in center screen.
	 *
	 * @param context
	 *            The context to use. Usually your Application or Activity
	 *            object.
	 * @param message
	 *            The text to show. Can be formatted text.
	 * 
	 */
	public static void showToastCenter(Context context, String message) {
		if (!message.isEmpty()) {
			Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 230);
			setToastMessageCenter(toast);
			toast.show();
		}
	}

	/**
	 * Make a standard toast that just contains a text view in custom location
	 * on screen.
	 *
	 * @param context
	 *            The context to use. Usually your Application or Activity
	 *            object.
	 * @param message
	 *            The text to show. Can be formatted text.
	 * @param gravity
	 *            Set the location at which the notification should appear on
	 *            the screen.
	 * 
	 */
	public static void showToast(Context context, String message, int gravity) {
		if (!message.isEmpty() && context != null) {
			Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
			toast.setGravity(gravity, 0, 0);
			setToastMessageCenter(toast);
			toast.show();
		}
	}

	static Toast setToastMessageCenter(Toast toast) {
		TextView v = toast.getView().findViewById(android.R.id.message);

		if (v != null) {
			v.setGravity(Gravity.CENTER);
		}
		return toast;
	}

	public static View showToastNew(Activity activity, String message) {
		if (activity != null) {
			ViewGroup root = activity.findViewById(android.R.id.content);
			View viewContent = LayoutInflater.from(activity).inflate(R.layout.view_new_toast, null);
			root.addView(viewContent);
			((TextView) viewContent.findViewById(R.id.tvContent)).setText(message);
			viewContent.bringToFront();
			new Handler().postDelayed(() -> viewContent.animate()
					.alpha(0)
					.setDuration(300)
					.setInterpolator(new DecelerateInterpolator())
					.withEndAction(() -> {
						try {
							viewContent.setVisibility(View.GONE);
							root.removeView(viewContent);
						} catch (Exception e) {
							DebugLog.loge(e);
						}
					}), 1200);
			return viewContent;
		} else DebugLog.loge("activity == null");
		return null;
	}

	/**
	 * Check Input string is Null or Empty
	 *
	 * @return (boolean) true/false
	 */
	public static boolean isNullOrEmpty(Object obj) {
		String inputString = String.valueOf(obj);
		if (obj == null) {
			return true;
		} else {
			if (inputString.isEmpty()) {
				return true;
			} else {
				return inputString.equals("null");
			}
		}
	}

	/**
	 * Sets the right-hand compound drawable of the TextView to the "error" icon
	 * and sets an error message that will be displayed in a popup when the
	 * TextView has focus. The icon and error message will be reset to null when
	 * any key events cause changes to the TextView's text. If the error is
	 * null, the error message and icon will be cleared. .
	 *
	 * @param obj
	 *            must be TextView or EditText.
	 * @param errorMessage
	 *            error message to show.
	 * 
	 */
	public static void showErrorNullOrEmpty(final Object obj, final String errorMessage) {
		if (obj == null)
			return;
		if (obj instanceof TextView) {
			final TextView view = (TextView) obj;
			view.setError(errorMessage);
			view.setOnFocusChangeListener((arg0, arg1) -> {
				if (view.getText().toString().isEmpty() || view.getText().toString().equals("")) {
					view.setError(errorMessage);
				} else {
					view.setError(null);
				}
			});
		} else if (obj instanceof EditText) {
			final EditText view = (EditText) obj;
			view.setError(errorMessage);
			view.setOnFocusChangeListener((arg0, arg1) -> {
				if (view == null || view.getText().toString().isEmpty() || view.getText().toString().equals("")) {
					view.setError(errorMessage);
				} else {
					view.setError(null);
				}
			});
		}
	}

	public static void showErrorNullOrEmptyWithThemeLight(final Object obj, final String errorMessage) {
		if (obj == null)
			return;
		if (obj instanceof TextView) {
			final TextView view = (TextView) obj;
			view.setError(Html.fromHtml("<font color='black'>" + errorMessage + "!</font>"));
			view.setOnFocusChangeListener((arg0, arg1) -> {
				if (view.getText().toString().isEmpty() || view.getText().toString().equals("")) {
					view.setError(Html.fromHtml("<font color='black'>" + errorMessage + "!</font>"));
				} else {
					view.setError(null);
				}
			});
		} else if (obj instanceof EditText) {
			final EditText view = (EditText) obj;
			view.setError(Html.fromHtml("<font color='black'>" + errorMessage + "!</font>"));
			view.setOnFocusChangeListener((arg0, arg1) -> {
				if (view.getText().toString().isEmpty() || view.getText().toString().equals("")) {
					view.setError(Html.fromHtml("<font color='black'>" + errorMessage + "!</font>"));
				} else {
					view.setError(null);
				}
			});
		}
	}

	public static void hideErrorTextView(View view) {
		try {
			if (view == null)
				return;
			if (view instanceof TextView) {
				((TextView) view).setError(null);
			}
			if (view instanceof EditText) {
				((EditText) view).setError(null);
			}
		} catch (Exception ignored) {
		}
	}

	public static void showKeyboard(Context context) {
		showKeyboard((Activity) context);
	}

	public static void showKeyboardEditText(Context context, EditText editText) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
	}

	public static void showKeyboard(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}

	public static void showKeyboard(Context context, View viewFocus) {
		showKeyboard((Activity) context, viewFocus, false);
	}

	public static void showKeyboard(Context context, View viewFocus, boolean requestFocus) {
		showKeyboard((Activity) context, viewFocus, requestFocus);
	}

	public static void showKeyboard(Activity activity, View viewFocus, boolean requestFocus) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

		if (requestFocus) viewFocus.requestFocus();
	}

	public static void hideKeyboard(Activity activity) {
		View view = activity.findViewById(android.R.id.content);
		hideKeyboard(activity, view);
	}

	public static void hideKeyboard(Context context, View view) {
		try {
			if (view != null) {
				Activity activity = (Activity) context;
				InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
			}
		} catch (Exception e) {
			DebugLog.loge(e);
		}
	}

	/**
	 * Request to hide the soft input window from the context of the window that
	 * is currently accepting input.
	 *
	 * @param context
	 *            The context to use. Usually your Application or Activity
	 *            object.
	 * @param editText
	 *            must be EditText
	 * 
	 */
	public static void removeFocusAndHideKeyboard(final Context context, final EditText editText) {
		editText.setOnFocusChangeListener((v, hasFocus) -> {
			if (hasFocus) {
				InputMethodManager inputMgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
				inputMgr.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
			}
		});
		editText.requestFocus();
	}

	public static void updateListHeight(ListView listview) {
		if (listview == null) {
			return;
		}
		ListAdapter listAdapter = listview.getAdapter();
		if (listAdapter == null) {
			return;

		}

		int totalHeight = 0;
		int adapterCount = listAdapter.getCount();
		for (int size = 0; size < adapterCount; size++) {
			View listItem = listAdapter.getView(size, null, listview);
			listItem.measure(0, 0);
			totalHeight = totalHeight + listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listview.getLayoutParams();
		params.height = totalHeight + (listview.getDividerHeight() * (adapterCount - 1));
		listview.setLayoutParams(params);
	}

	public static void updateListHeight(ListView listview, int tempHeight) {
		if (listview == null) {
			return;
		}
		ListAdapter listAdapter = listview.getAdapter();
		if (listAdapter == null) {
			return;

		}

		int totalHeight = 0;
		int adapterCount = listAdapter.getCount();
		for (int size = 0; size < adapterCount; size++) {
			View listItem = listAdapter.getView(size, null, listview);
			listItem.measure(0, 0);
			// totalHeight += measureHeight(0, listItem);
			totalHeight = totalHeight + listItem.getMeasuredHeight() + tempHeight;
		}

		ViewGroup.LayoutParams params = listview.getLayoutParams();
		params.height = totalHeight + (listview.getDividerHeight() * (adapterCount - 1));
		listview.setLayoutParams(params);
	}

	/**
	 * Checking format input email.
	 *
	 * @param email
	 *            check email allow format
	 *            "[a-zA-Z0-9._-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2}[A-Za-z]*$+" or
	 *            "[a-zA-Z0-9._-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2}[A-Za-z]*$+\\.+[a-z]+"
	 * 
	 * @return (boolean) true/false
	 */
	public static boolean validateEmail(String email) {
		Pattern pattern = Pattern.compile("[a-zA-Z0-9._-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2}[A-Za-z]*$+");
		Pattern pattern2 = Pattern.compile("[a-zA-Z0-9._-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2}[A-Za-z]*$+\\.+[a-z]+");
		return pattern.matcher(email).matches() || pattern2.matcher(email).matches();
	}

	/**
	 * Call Email Application On Device.
	 *
	 * @param context
	 *            The context to use. Usually your Application or Activity
	 *            object.
	 * @param email
	 *            email to send
	 * @param subject
	 *            subject to send
	 * @param bodyText
	 *            content mail to send
	 * 
	 */
	public static void callEmailApplication(Context context, String email, String subject, String bodyText) {
		callEmailApplication(context, new String[] { email }, subject, bodyText);
	}
	
	public static void callEmailApplication(String email, String subject, String bodyText) {
		if (UtilityMain.mContext == null) return;
		callEmailApplication(UtilityMain.mContext, new String[] { email }, subject, bodyText);
	}
	
	public static void callEmailApplication(String[] emails, String subject, String bodyText) {
		if (UtilityMain.mContext == null) return;
		callEmailApplication(UtilityMain.mContext, emails, subject, bodyText);
	}
	
	public static void callEmailApplication(Context context, String[] emails, String subject, String bodyText) {
		final Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("text/message");
		emailIntent.putExtra(Intent.EXTRA_EMAIL, emails);
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		emailIntent.putExtra(Intent.EXTRA_TEXT, bodyText);
		context.startActivity(Intent.createChooser(emailIntent, "Email app").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	}
	
	@SuppressLint("NewApi")
	public static void sendSMS(Context context, String content, String phoneNumber, String messageError) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context);

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("sms:"+phoneNumber));  // This ensures only SMS apps respond
                intent.putExtra("sms_body", content);

                if (defaultSmsPackageName != null) {
                    intent.setPackage(defaultSmsPackageName);
                } else {
                	String defaultApplication = Settings.Secure.getString(context.getContentResolver(), "sms_default_application");
                    if (defaultApplication != null) {
                        intent.setPackage(defaultApplication);
                    } else {
                    	if (messageError.isEmpty()) {
                    		showToast(context, messageError);	
                    	}
                        return;
                    }
                }
                context.startActivity(intent);

            } else {
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:"+phoneNumber));
                sendIntent.putExtra("sms_body", content);
                context.startActivity(sendIntent);
            }
        } catch (Exception e) {
            DebugLog.loge(e);
            if (messageError.isEmpty()) {
        		showToast(context, messageError);	
        	}
        }
    }

	public static String getDomainName(String url) {
		try {
			URL uri = new URL(url.toLowerCase());
			String host = uri.getHost();
			String protocol = uri.getProtocol();
			int port = uri.getPort();
			return protocol + "://" + host + (port > 0 ? ":" + port : "");
		} catch (Exception e) {
			DebugLog.logv(e);
		}
		return "";
	}
	
	/**
	 * Get File Name And Extension.
	 *
	 * @param strUrl
	 *            input url of file
	 * 
	 * @return (String) filename and extension
	 */
	public static String getFileNameAndExtension(String strUrl) {
		String fileName = "";
		try {
			int i = strUrl.lastIndexOf('/');
			if (i > 0) {
				fileName = strUrl.substring(i + 1);
			}
		} catch (Exception e) {
			DebugLog.loge(e);
		}

		return fileName;
	}

	/**
	 * Get Extension.
	 *
	 * @param strUrl
	 *            input url of file
	 * 
	 * @return (String) extension
	 */
	public static String getExtension(String strUrl) {
		String fileName = "";
		
		try {
			int i = strUrl.lastIndexOf('.');
			if (i > 0) {
				fileName = strUrl.substring(i + 1);
			}
		} catch (Exception e) {
			DebugLog.loge(e);
		}
		

		return fileName;
	}

	/**
	 * Get File Name.
	 *
	 * @param strUrl
	 *            input url of file
	 * 
	 * @return (String) filename
	 */
	public static String getFileName(String strUrl) {
		String fileName = "";
		
		try {
			fileName = strUrl.substring(strUrl.lastIndexOf('/') + 1, strUrl.lastIndexOf('.'));
		} catch (Exception e) {
			DebugLog.loge(e);
		}
		
		return fileName;
	}

	/**
	 * Splits this string using the supplied regularExpression.
	 *
	 * @param text
	 *            input string to splits
	 * @param split
	 *            like ";" "," "_" etc
	 * 
	 * @return (String[]) result allow split
	 */
	public static String[] splitComme(String text, String split) {
		try {
			return text.split(split);
		} catch (Exception e) {
			DebugLog.loge(e);
		}
		return new String[]{};
	}

	/**
	 * Splits this string using the supplied regularExpression.
	 *
	 * @param text
	 *            input string to splits
	 * @param split
	 *            like ";" "," "_" etc
	 * 
	 * @return (ArrayListString) result allow split
	 */
	public static ArrayList<String> splitComme2(String text, String split) {
		try {
			String[] items = text.split(split);
			ArrayList<String> newItems = new ArrayList<>();
			for (int i = 0; i < items.length; i++) {
				if (!items[i].equals("")) {
					newItems.add(items[i]);
				}
			}
			return newItems;
		} catch (Exception e) {
			DebugLog.loge(e);
		}
		return new ArrayList<>();
	}

	/**
	 * Convert DP to Pixel.
	 *
	 * @param context
	 *            The context to use. Usually your Application or Activity
	 *            object.
	 * @param dp
	 *            The unit to convert from.
	 * 
	 * @return (int) result.
	 */
	
	public static int convertDPtoPixel(int dp, Context context) {
		return convertDPtoPixel(context, dp);
	}
	
	public static int convertDPtoPixel(Context context, int dp) {
		Resources r = context.getResources();
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
		return px;
	}

	public static float convertPixelsToDp(Context context, float px) {
		return convertPixelsToDp(px, context);
	}
	
	public static float convertPixelsToDp(float px, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;
	}

	static String convertToHex(byte[] data) {
		StringBuilder buf = new StringBuilder();
		for (byte b : data) {
			int halfbyte = (b >>> 4) & 0x0F;
			int two_half = 0;
			do {
				buf.append(halfbyte <= 9 ? (char) ('0' + halfbyte) : (char) ('a' + halfbyte - 10));
				halfbyte = b & 0x0F;
			} while (two_half++ < 1);
		}
		return buf.toString();
	}

	/**
	 * Encrypt SHA1.
	 *
	 * @param text
	 *            input string
	 * 
	 * @return (String) result.
	 */
	public static String encryptSHA1(String text) {
		String result = "";
		if (text.equals("")) {
			return "";
		}
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
			md.update(text.getBytes(StandardCharsets.ISO_8859_1), 0, text.length());
			byte[] sha1hash = md.digest();
			result = convertToHex(sha1hash);
		} catch (NoSuchAlgorithmException ignored) {
		}
		return result;
	}

	/**
	 * Encrypt MD5.
	 *
	 * @param text
	 *            input string
	 * 
	 * @return (String) result.
	 */
	public static String encryptMD5(String text) {
		if (text.equals("")) {
			return "";
		}
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.reset();
			digest.update(text.getBytes());
			byte[] a = digest.digest();
			int len = a.length;
			StringBuilder sb = new StringBuilder(len << 1);
			for (int i = 0; i < len; i++) {
				sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
				sb.append(Character.forDigit(a[i] & 0x0f, 16));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Encrypt AES.
	 *
	 * @param data
	 *            input string
	 * 
	 * @return (String) result.
	 */

	public static String encryptAES(byte[] pass_key, String data) {
		return Aes.encrypt(data, Aes.generateKey(pass_key), null);
	}

	/**
	 * Decrypt AES.
	 *
	 * @param encryptedValue
	 *            input string
	 * 
	 * @return (String) result.
	 */
	public static String decryptAES(byte[] pass_key, String encryptedValue) {
		return Aes.decrypt(encryptedValue, Aes.generateKey(pass_key), null);
	}

	/**
	 * Gen KeyHash for facebook or something use.
	 *
	 * @param context
	 *            Class for retrieving various kinds of information related to
	 *            the application packages that are currently installed on the
	 *            device.
	 * 
	 * @return (String) result.
	 */
	public static String genKeyHash(Context context) {
		String keyHash = "error";
		PackageManager manager = context.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = manager.getPackageInfo(context.getPackageName(), 0);
			if (pi == null) {
				DebugLog.loge("Error: pi == null");
				return keyHash;
			}
			PackageInfo info = manager.getPackageInfo(pi.packageName, PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
				DebugLog.loge("KeyHash >>>\n" + keyHash);
			}
		} catch (Exception e) {
			DebugLog.loge(e);
		}
		return keyHash;
	}

	public static String genKeyHash() {
		if (UtilityMain.mContext == null) return "error";
		return genKeyHash(UtilityMain.mContext);
	}
	
	/**
	 * Get Day In DatePicker.
	 *
	 * @param datePicker
	 *            See DatePicker
	 * 
	 * @return (String) result.
	 */
	public static String getDayInDatePicker(DatePicker datePicker) {
		String day;
		int dayOfMonth = datePicker.getDayOfMonth();

		if (dayOfMonth >= 10) {
			day = String.valueOf(dayOfMonth);
		} else {
			day = "0" + dayOfMonth;
		}

		return day;
	}

	/**
	 * Get Month In DatePicker.
	 *
	 * @param datePicker
	 *            See DatePicker
	 * 
	 * @return (String) result.
	 */
	public static String getMonthInDatePicker(DatePicker datePicker) {
		String month;
		int mMonth = datePicker.getMonth();
		mMonth++;
		if (mMonth >= 10) {
			month = String.valueOf(mMonth);
		} else {
			month = "0" + mMonth;
		}

		return month;
	}

	public static boolean isNumeric(String str) {
		try {
			double d = Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	/**
	 * Checking data in array changing!
	 *
	 * @param oldList
	 *            oldList
	 * @param newList
	 *            newList
	 * 
	 * @return (boolean) true/false
	 */
	public static boolean isArrayStringChange(ArrayList<String> oldList, ArrayList<String> newList) {
		boolean ischange = false;
		if (newList.size() != oldList.size()) {
			ischange = true;
		} else {
			for (int i = 0; i < newList.size(); i++) {
				boolean check = true;
				for (int j = 0; j < oldList.size(); j++) {
					if (oldList.get(j).equals(newList.get(i))) {
						check = false;
						break;
					}
				}
				if (check) {
					ischange = true;
					break;
				}
			}
		}
		return ischange;
	}

	/**
	 * Checking exist key in array.
	 *
	 * @param list
	 *            arraylist
	 * @param key
	 *            key to check
	 * 
	 * @return (boolean) true/false
	 */
	public static boolean isExistKey(ArrayList<String> list, String key) {
		boolean isExistKey = false;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equals(key)) {
				isExistKey = true;
				break;
			}
		}

		return isExistKey;
	}

	/**
	 * Checks whether a wired headset is connected or not.
	 *
	 * @param context
	 *            The context to use. Usually your Application or Activity
	 *            object.
	 * 
	 * @return (boolean) true/false.
	 */
	@SuppressWarnings("deprecation")
	public static boolean handleHeadphonesState(Context context) {
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

		return am.isWiredHeadsetOn();
	}

	/**
	 * Indicates whether network connectivity exists and it is possible to
	 * establish connections and pass data
	 *
	 * @param context
	 *            The context to use. Usually your Application or Activity
	 *            object.
	 * 
	 * @return (boolean) true/false.
	 */
	@RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
	public static boolean isNetworkConnect(Context context) {
		final ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
		return activeNetwork != null && activeNetwork.isConnected();
	}

	//TODO ===================== JSON =====================

	public static Object getObjectInJsonObj(JSONObject jsonObject, String key) {
		return JsonUtils.getObjectInJsonObj(jsonObject, key);
	}

	public static String getStringInJsonObj(JSONObject jsonObject, String key) {
		return JsonUtils.getStringInJsonObj(jsonObject, key);
	}

	public static ArrayList<Object> getListObjectInJsonArray(JSONArray jsonArray) {
		return JsonUtils.getListObjectInJsonArray(jsonArray);
	}

	public static ArrayList<String> getListStringInJsonArray(JSONArray jsonArray) {
		return JsonUtils.getListStringInJsonArray(jsonArray);
	}

	public static Object getObjectInJsonArray(JSONArray jsonArray, int i) {
		return JsonUtils.getObjectInJsonArray(jsonArray, i);
	}

	public static String getStringInJsonArray(JSONArray jsonArray, int i) {
		return JsonUtils.getStringInJsonArray(jsonArray, i);
	}

	public static String getStringInJsonArray(JSONArray jsonArray, int i, String keyJsonObj) {
		return JsonUtils.getStringInJsonArray(jsonArray, i, keyJsonObj);
	}
	
	public static ArrayList<String> parseJSONArrayStringToArray(String data) {
        return JsonUtils.parseJSONArrayStringToArray(data);
    }

    public static String parseJSONArrayStringToString(String data) {
        return JsonUtils.parseJSONArrayStringToString(data);
    }
    
    public static JSONArray JSONArrayRemove(JSONArray ja, int pos) {
        return JsonUtils.JSONArrayRemove(ja, pos);
    }

	public static JSONObject getJSONObjectFromJSONObject(JSONObject obj, Object key) {
		return JsonUtils.getJSONObjectFromJSONObject(obj, key);
	}

	public static JSONArray getJSONArrayFromJSONObject(JSONObject obj, Object key) {
		return JsonUtils.getJSONArrayFromJSONObject(obj, key);
	}

	public static ArrayList<JSONObject> parseJSONArrayToArrayListJSON(JSONArray jsonArray) {
		return JsonUtils.parseJSONArrayToArrayListJSON(jsonArray);
	}

	public static JSONObject putObject(JSONObject jsonObject, String key, Object value) {
		return JsonUtils.putObject(jsonObject, key, value);
	}

	public static JSONObject getJSONObjectFromJSONArray(JSONArray array, int pos) {
		return JsonUtils.getJSONObjectFromJSONArray(array, pos);
	}

	public static JSONArray addAll(JSONArray jsonArray, JSONArray jsonArray2) {
		return JsonUtils.addAll(jsonArray, jsonArray2);
	}

	public static String convertArrayListDataToJson(ArrayList<Data> arrayList) {
		return JsonUtils.convertArrayListDataToJson(arrayList);
	}

	public static JSONObject parseQueryToJson(String query) {
		return JsonUtils.parseQueryToJson(query);
	}

	public static String parseJsonToQuery(JSONObject jsonObject) {
		return JsonUtils.parseJsonToQuery(jsonObject);
	}

	public static int checkPosition(ArrayList<JSONObject> arrayList, String check, String key) {
		return JsonUtils.checkPosition(arrayList, check, key);
	}

	public static String formatCurrency(String value) {
		if (Build.VERSION.SDK_INT >= 24) {
			DecimalFormat df = new DecimalFormat("#,###,###,###,###"); //"#,###,###,###,###"
			return formatCurrency(value, df);
		} else {
			return getDecimalFormattedString(value);
		}
	}

	public static String formatCurrency(String value, String pattern) {
		if (Build.VERSION.SDK_INT >= 24) {
			DecimalFormat df = new DecimalFormat(pattern); //"#,###,###,###,###"
			return formatCurrency(value, df);
		} else {
			return getDecimalFormattedString(value);
		}
	}

	public static String formatCurrency(String value, DecimalFormat df) {
		if (Build.VERSION.SDK_INT >= 24) {
			String result = "0";
			if (value.trim().isEmpty()) return result;
			try {
				result = df.format(Double.valueOf(value));
			} catch (Exception e) {
				DebugLog.loge(e);
			}
			return result;
		} else {
			return getDecimalFormattedString(value);
		}
	}

	static String getDecimalFormattedString(String value) {
		if (value.contains(".")) {
			String[] tmp = value.split("\\.");
			if (tmp.length > 0) {
				value = tmp[0];
			}
		}
		StringTokenizer lst = new StringTokenizer(value, ".");
		String str1 = value;
		String str2 = "";
		if (lst.countTokens() > 1) {
			str1 = lst.nextToken();
			str2 = lst.nextToken();
		}
		StringBuilder str3 = new StringBuilder();
		int i = 0;
		int j = -1 + str1.length();
		if (str1.charAt(-1 + str1.length()) == '.') {
			j--;
			str3 = new StringBuilder(".");
		}
		for (int k = j; ; k--) {
			if (k < 0) {
				if (str2.length() > 0)
					str3.append(".").append(str2);
				return str3.toString();
			}
			if (i == 3) {
				str3.insert(0, "."); //,
				i = 0;
			}
			str3.insert(0, str1.charAt(k));
			i++;
		}
	}

	public static String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return capitalize(model);
		} else {
			return capitalize(manufacturer) + " " + model;
		}
	}

	static String capitalize(String s) {
		if (s == null || s.length() == 0) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		} else {
			return Character.toUpperCase(first) + s.substring(1);
		}
	}

	public static String resizeImage(String pathImage, String targetPath, int maxSize, int quality, boolean isDeleteOrigin) {
		if (pathImage.equals(targetPath)) {
			isDeleteOrigin = false;
		}
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(pathImage, options);
			int imageHeight = options.outHeight;
			int imageWidth = options.outWidth;

			ExifInterface exif = new ExifInterface(pathImage);
			String TAG_ORIENTATION = exif.getAttribute(ExifInterface.TAG_ORIENTATION);

			DebugLog.loge(TAG_ORIENTATION);

			double ratito = 1;
			if (imageWidth > imageHeight) {
				if (imageWidth > maxSize) {
					ratito = imageWidth / maxSize;
				}
			} else {
				if (imageHeight > maxSize) {
					ratito = imageHeight / maxSize;
				}
			}

			DebugLog.logd("resize ratio: " + ratito);

			if (ratito < 1) {
				ratito = 1;
			}

			options.inSampleSize = (int) ratito;
			options.inJustDecodeBounds = false;
			Bitmap out = BitmapFactory.decodeFile(pathImage, options);

			File file = new File(targetPath);
			FileOutputStream fOut;

			fOut = new FileOutputStream(file);
			out.compress(Bitmap.CompressFormat.PNG, quality, fOut);
			fOut.flush();
			fOut.close();
			out.recycle();

			DebugLog.logd("\norigin:\npath --> " + pathImage + "\nsize --> " + new File(pathImage).length());
			DebugLog.logd("\nresize:\npath --> " + targetPath + "\nsize --> " + file.length());

			if (file.length() <= 0) {
				return pathImage;
			}

			if (isDeleteOrigin) {
				new File(pathImage).delete();
			}
		} catch (Exception e) {
			return pathImage;
		}
		return targetPath;
	}

	public static String resizeImageAndRotate(String pathImage, String targetPath, int maxSize, int quality, boolean isDeleteOrigin) {
		if (pathImage.equals(targetPath)) {
			isDeleteOrigin = false;
		}
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(pathImage, options);
			int imageHeight = options.outHeight;
			int imageWidth = options.outWidth;

			ExifInterface exif = new ExifInterface(pathImage);
			String TAG_ORIENTATION = exif.getAttribute(ExifInterface.TAG_ORIENTATION);

			Matrix matrix = new Matrix();
			if (Integer.parseInt(TAG_ORIENTATION) == ExifInterface.ORIENTATION_ROTATE_90) {
				DebugLog.loge("rotate: 90");
				matrix.postRotate(90);
			} else if (Integer.parseInt(TAG_ORIENTATION) == ExifInterface.ORIENTATION_ROTATE_270) {
				DebugLog.loge("rotate: 270");
				matrix.postRotate(270);
			} else if (Integer.parseInt(TAG_ORIENTATION) == ExifInterface.ORIENTATION_ROTATE_180) {
				DebugLog.loge("rotate: 180");
				matrix.postRotate(180);
			}

			double ratito = 1;
			if (imageWidth > imageHeight) {
				if (imageWidth > maxSize) {
					ratito = imageWidth / maxSize;
				}
			} else {
				if (imageHeight > maxSize) {
					ratito = imageHeight / maxSize;
				}
			}

			DebugLog.logd("resize ratio: " + ratito);

			if (ratito < 1) {
				ratito = 1;
			}

			options.inSampleSize = (int) ratito;
			options.inJustDecodeBounds = false;
			if (TAG_ORIENTATION.equals("0")) {
				Bitmap out = BitmapFactory.decodeFile(pathImage, options);

				File file = new File(targetPath);
				FileOutputStream fOut;

				fOut = new FileOutputStream(file);
				out.compress(Bitmap.CompressFormat.JPEG, quality, fOut);
				fOut.flush();
				fOut.close();
				out.recycle();

				DebugLog.logd("\norigin:\npath --> " + pathImage + "\nsize --> " + new File(pathImage).length());
				DebugLog.logd("\nresize:\npath --> " + targetPath + "\nsize --> " + file.length());

				if (file.length() <= 0) {
					return pathImage;
				}

			} else {
				Bitmap _out = BitmapFactory.decodeFile(pathImage, options);
				Bitmap out = Bitmap.createBitmap(_out, 0, 0, _out.getWidth(), _out.getHeight(), matrix, true);

				File file = new File(targetPath);
				FileOutputStream fOut;

				fOut = new FileOutputStream(file);
				out.compress(Bitmap.CompressFormat.JPEG, quality, fOut);
				fOut.flush();
				fOut.close();
				out.recycle();
				_out.recycle();

				DebugLog.logd("\norigin:\npath --> " + pathImage + "\nsize --> " + new File(pathImage).length());
				DebugLog.logd("\nresize:\npath --> " + targetPath + "\nsize --> " + file.length());

				if (file.length() <= 0) {
					return pathImage;
				}

			}

			if (isDeleteOrigin) {
				new File(pathImage).delete();
			}
		} catch (Exception e) {
			return pathImage;
		}
		return targetPath;
	}

	public static String rotateImage(String pathImage, String targetPath, boolean isDeleteOrigin) {
		if (pathImage.equals(targetPath)) {
			isDeleteOrigin = false;
		}
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = false;

			ExifInterface exif = new ExifInterface(pathImage);
			String TAG_ORIENTATION = exif.getAttribute(ExifInterface.TAG_ORIENTATION);

			Matrix matrix = new Matrix();
			if (Integer.parseInt(TAG_ORIENTATION) == ExifInterface.ORIENTATION_ROTATE_90) {
				DebugLog.loge("rotate: 90");
				matrix.postRotate(90);
			} else if (Integer.parseInt(TAG_ORIENTATION) == ExifInterface.ORIENTATION_ROTATE_270) {
				DebugLog.loge("rotate: 270");
				matrix.postRotate(270);
			} else if (Integer.parseInt(TAG_ORIENTATION) == ExifInterface.ORIENTATION_ROTATE_180) {
				DebugLog.loge("rotate: 180");
				matrix.postRotate(180);
			}

			if (TAG_ORIENTATION.equals("0")) {
				// Bitmap out = BitmapFactory.decodeFile(pathImage, options);
				//
				// File file = new File(targetPath);
				// FileOutputStream fOut;
				//
				// fOut = new FileOutputStream(file);
				// out.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
				// fOut.flush();
				// fOut.close();
				// out.recycle();
				//
				//
				// DebugLog.logd("\norigin:\npath --> " + pathImage+"\nsize -->
				// "+new File(pathImage).length());
				// DebugLog.logd("\nresize:\npath --> " + targetPath+"\nsize -->
				// "+file.length());

				// if(file.length()<=0){
				return pathImage;
				// }

			} else {
				Bitmap _out = BitmapFactory.decodeFile(pathImage, options);
				Bitmap out = Bitmap.createBitmap(_out, 0, 0, _out.getWidth(), _out.getHeight(), matrix, true);

				File file = new File(targetPath);
				FileOutputStream fOut;

				fOut = new FileOutputStream(file);
				out.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
				fOut.flush();
				fOut.close();
				out.recycle();
				_out.recycle();

				DebugLog.logd("\norigin:\npath --> " + pathImage + "\nsize --> " + new File(pathImage).length());
				DebugLog.logd("\nresize:\npath --> " + targetPath + "\nsize --> " + file.length());

				if (file.length() <= 0) {
					return pathImage;
				}

			}

			if (isDeleteOrigin) {
				new File(pathImage).delete();
			}
		} catch (Exception e) {
			return pathImage;
		}
		return targetPath;
	}

	static Handler h;
	static Runnable r;

	public interface OnExecuteMethod {
		void executeMethod();
	}

	static OnExecuteMethod callbackOnExecuteMethod;

	public static void delayRun(long time, OnExecuteMethod onExecuteMethod) {
		callbackOnExecuteMethod = onExecuteMethod;
		if (h == null)
			h = new Handler();
		if (r != null) {
			h.removeCallbacks(r);
		}
		r = new Runnable() {

			@Override
			public void run() {
				callbackOnExecuteMethod.executeMethod();
			}
		};
		h.postDelayed(r, time);
	}

	public interface OnGetUrlMethod {
		void executeMethod(String newUrl);
	}

	static OnGetUrlMethod callbackOnGetUrlMethod;

	public static void getRedirectUrl(String url, OnGetUrlMethod onGetUrlMethod) {
		callbackOnGetUrlMethod = onGetUrlMethod;
		String newUrl = "";
		try {

			URL obj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
			conn.setReadTimeout(5000);
			conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
			conn.addRequestProperty("User-Agent", "Mozilla");
			conn.addRequestProperty("Referer", "google.com");

			DebugLog.logd("Request URL ... " + url);

			boolean redirect = false;

			// normally, 3xx is redirect
			int status = conn.getResponseCode();
			if (status != HttpURLConnection.HTTP_OK) {
				if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER)
					redirect = true;
			}

			DebugLog.logd("Response Code ... " + status);

			if (redirect) {

				// get redirect url from "location" header field
				newUrl = conn.getHeaderField("Location");

				// get the cookie if need, for login
				String cookies = conn.getHeaderField("Set-Cookie");

				// open the new connnection again
				conn = (HttpURLConnection) new URL(newUrl).openConnection();
				conn.setRequestProperty("Cookie", cookies);
				conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
				conn.addRequestProperty("User-Agent", "Mozilla");
				conn.addRequestProperty("Referer", "google.com");

				DebugLog.logd("Redirect to URL : " + newUrl);

			}

			callbackOnGetUrlMethod.executeMethod(newUrl);

			// GET content web
			// BufferedReader in = new BufferedReader(
			// new InputStreamReader(conn.getInputStream()));
			// String inputLine;
			// StringBuffer html = new StringBuffer();
			//
			// while ((inputLine = in.readLine()) != null) {
			// html.append(inputLine);
			// }
			// in.close();
			//
			// DebugLog.logd("URL Content... \n" + html.toString());
			// DebugLog.logd("---->Done");

		} catch (Exception e) {
			DebugLog.loge(e);
		}
	}

	/**
	 * Sets the typeface and style in which the text should be displayed.
	 *
	 */
	public static void setTypeface(Context context, View view, String fontAssets) {
		Typeface typeFont = null;
		if (fontAssets.length() > 0) {
			typeFont = Typeface.createFromAsset(context.getAssets(), fontAssets);
		} else {
			typeFont = Typeface.createFromAsset(context.getAssets(), "");
		}
		if (view instanceof TextView) {
			((TextView) view).setTypeface(typeFont);
		} else if (view instanceof EditText) {
			((EditText) view).setTypeface(typeFont);
		} else if (view instanceof Button) {
			((Button) view).setTypeface(typeFont);
		}
	}

	public static void overrideFonts(final Context context, final View v, String fontAssets) {
		try {
			if (v instanceof ViewGroup) {
				ViewGroup vg = (ViewGroup) v;
				for (int i = 0; i < vg.getChildCount(); i++) {
					View child = vg.getChildAt(i);
					overrideFonts(context, child, fontAssets);
				}
			} else if (v instanceof TextView || v instanceof Button || v instanceof EditText) {
				Typeface type = Typeface.createFromAsset(context.getAssets(), fontAssets);
				((TextView) v).setTypeface(type);
			}
		} catch (Exception ignored) {
		}
	}

	/**
	 * Start Resize Width View Animation
	 *
	 */
	public static void startResizeWidthViewAnimation(View view, long duration, int width) {
		ResizeWidthAnimation anim = new ResizeWidthAnimation(view, width);
		anim.setDuration(duration);
		view.startAnimation(anim);
	}

	public static void startResizeHeightViewAnimation(View view, long duration, int height) {
		ResizeHeightAnimation anim = new ResizeHeightAnimation(view, height);
		anim.setDuration(duration);
		view.startAnimation(anim);
	}

	/**
	 * Start Resize Width View Animation
	 *
	 */
	public static void startResizeWidthViewAnimation(View view, long duration, int position, int width) {
		animate(view).x(position).setDuration(duration);
		ResizeWidthAnimation anim = new ResizeWidthAnimation(view, width);
		anim.setDuration(duration);
		view.startAnimation(anim);
	}

	public static void startResizeHeightViewAnimation(View view, long duration, int position, int height) {
		animate(view).y(position).setDuration(duration);
		ResizeHeightAnimation anim = new ResizeHeightAnimation(view, height);
		anim.setDuration(duration);
		view.startAnimation(anim);
	}

	/**
	 * Start View Alpha Animation
	 *
	 */
	public static void startViewAlphaAnimation(boolean isShow, final View view, final long duration) {
		if (isShow) {
			view.setVisibility(View.VISIBLE);
			animate(view).alpha(1).setDuration(duration);
		} else {
			view.setVisibility(View.GONE);
			animate(view).alpha(0).setDuration(duration);
		}
	}

	static Handler h1;
	static Runnable r1;

	/**
	 * Start Scale View Animation
	 *
	 */
	public static void startScaleViewAnimation(final View view, float scale, long duration1, final long duration2) {
		animate(view).scaleX(scale).scaleY(scale).setDuration(duration1);
		if (h1 == null)
			h1 = new Handler();
		if (r1 != null) {
			h1.removeCallbacks(r1);
		}
		r1 = () -> animate(view).scaleX(1).scaleY(1).setDuration(duration2);
		h1.postDelayed(r1, duration1);
	}

	public static void showViewFadeAnimation(View viewShow, final View viewHide, long duration) {
		viewShow.setVisibility(View.VISIBLE);
		viewShow.setAlpha(0);
		viewShow.animate()
				.alpha(1)
				.setDuration(duration)
				.setInterpolator(new DecelerateInterpolator());
		if (viewHide != null) {
			viewHide.animate()
					.alpha(0)
					.setDuration(300)
					.setInterpolator(new DecelerateInterpolator())
					.withEndAction(() -> viewHide.setVisibility(View.GONE));
		}
	}

	/**
	 * Get Value From Data.
	 *
	 */
	public static String getValueFromData(ArrayList<Data> listData, String key) {
		if (listData.size() == 0)
			return "";
		for (int i = 0; i < listData.size(); i++) {
			if (listData.get(i).getKey().equals(key)) {
				return listData.get(i).getValue();
			}
		}
		return "";
	}

	/**
	 * Get InfoDevices.
	 *
	 */
	public static String getInfoDevices(Context context) {
		StringBuilder message = new StringBuilder();
		message.append("Locale: ").append(Locale.getDefault()).append('\n');
		try {
			if (context != null) {
				PackageManager pm = context.getPackageManager();
				PackageInfo pi;
				pi = pm.getPackageInfo(context.getPackageName(), 0);
				message.append("Package: ").append(pi.packageName).append('\n');
				message.append("Version: ").append(pi.versionName).append('\n');
				message.append("VersionCode: ").append(pi.versionCode).append('\n');
			} else {
				message.append("Context == null\n");
			}
			
		} catch (Exception e) {
			DebugLog.loge("Error:\n"+ e);
			if (context != null) {
				message.append("Could not get Version information for").append(context.getPackageName()).append('\n');
			}
			
		}
		message.append("Phone Model:").append(android.os.Build.MODEL).append('\n');
		message.append("Android Version:").append(android.os.Build.VERSION.RELEASE).append('\n');
		message.append("Board: ").append(android.os.Build.BOARD).append('\n');
		message.append("Brand: ").append(android.os.Build.BRAND).append('\n');
		message.append("Device: ").append(android.os.Build.DEVICE).append('\n');
		message.append("Host: ").append(android.os.Build.HOST).append('\n');
		message.append("ID: ").append(android.os.Build.ID).append('\n');
		message.append("Model: ").append(android.os.Build.MODEL).append('\n');
		message.append("Product:").append(android.os.Build.PRODUCT).append('\n');
		message.append("Type: ").append(android.os.Build.TYPE).append('\n');
//		StatFs stat = getStatFs();
//		message.append("Total Internal memory: ").append(getTotalInternalMemorySize(stat)).append('\n');
//		message.append("Available Internal memory: ").append(getAvailableInternalMemorySize(stat)).append('\n');
		return message.toString();
	}
	
	/*
	private StatFs getStatFs() {
		File path = Environment.getDataDirectory();
		return new StatFs(path.getPath());
	}

	private long getAvailableInternalMemorySize(StatFs stat) {
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	private long getTotalInternalMemorySize(StatFs stat) {
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}
*/
	
	public static String getInfoDevices() {
		return getInfoDevices(UtilityMain.mContext);
	}

	/**
	 * Get SharedPreference Value.
	 *
	 */
	public static String getSharedPreference(Context context, Object key) {
		return SharedPreference.get(context, String.valueOf(key), "");
	}

	/**
	 * Remove Accents.
	 *
	 */
	@SuppressLint("DefaultLocale")
	public static String removeAccents(String value) {
		String text = value.toLowerCase();
		String data = Normalizer.normalize(text, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
		return data.replaceAll("đ", "d");
	}

	//TODO for old version
	public static int checkIntValue(Object data) {
		return parseInt(data);
	}

	public static int parseInt(Object data) {
		return parseInt(data, 0);
	}

	public static int parseInt(Object data, int default_value) {
		try {
			return Integer.parseInt(String.valueOf(data));
		} catch (Exception e) {
			DebugLog.logi(e);
		}
		return default_value;
	}

	//TODO for old version
	public static float checkFloatValue(Object data) {
		return parseFloat(data);
	}

	public static float parseFloat(Object data) {
		return parseFloat(data, 0);
	}

	public static float parseFloat(Object data, float default_value) {
		try {
			return Float.parseFloat(String.valueOf(data));
		} catch (Exception e) {
			DebugLog.logi(e);
		}
		return default_value;
	}

	//TODO for old version
	public static long checkLongValue(Object data) {
		return parseLong(data);
	}

	public static long parseLong(Object data) {
		return parseLong(data, 0);
	}

	public static long parseLong(Object data, long default_value) {
		try {
			return Long.parseLong(String.valueOf(data));
		} catch (Exception e) {
			DebugLog.logi(e);
		}
		return default_value;
	}

	//TODO for old version
	public static double checkDoubleValue(Object data) {
		return parseDouble(data);
	}

	public static double parseDouble(Object data) {
		return parseDouble(data, 0);
	}

	public static double parseDouble(Object data, double default_value) {
		try {
			return Double.parseDouble(String.valueOf(data));
		} catch (Exception e) {
			DebugLog.logi(e);
		}
		return default_value;
	}

	//TODO for old version
	public static boolean checkBooleanValue(Object data) {
		return parseBoolean(data);
	}

	public static boolean parseBoolean(Object data) {
		try {
			return Boolean.parseBoolean(String.valueOf(data));
		} catch (Exception e) {
			DebugLog.logi(e);
		}
		return false;
	}

	@SuppressLint("ClickableViewAccessibility")
	public static void setScrollEditText(Context context, final EditText editText) {
		editText.setScroller(new Scroller(context));
		editText.setMaxLines(3);
		editText.setVerticalScrollBarEnabled(true);
		editText.setMovementMethod(new ScrollingMovementMethod());

		editText.setOnTouchListener((view, event) -> {
			if (view.getId() == editText.getId()) {
				view.getParent().requestDisallowInterceptTouchEvent(true);
				if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
					view.getParent().requestDisallowInterceptTouchEvent(false);
				}
			}
			return false;
		});
	}

	public static void scrollUpToTop(final ScrollView scrollView, long delayTime) {
		new Handler().postDelayed(() -> scrollView.smoothScrollTo(0, 0), delayTime);
	}

	@SuppressLint("DefaultLocale")
	public static void shareViaFacebook(Context context, String urlToShare) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		// intent.putExtra(Intent.EXTRA_SUBJECT, "Foo bar"); // NB: has no
		// effect!
		intent.putExtra(Intent.EXTRA_TEXT, urlToShare);

		// See if official Facebook app is found
		boolean facebookAppFound = false;
		List<ResolveInfo> matches = context.getPackageManager().queryIntentActivities(intent, 0);
		for (ResolveInfo info : matches) {
			if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana")) {
				intent.setPackage(info.activityInfo.packageName);
				facebookAppFound = true;
				break;
			}
		}

		// As fallback, launch sharer.php in a browser
		if (!facebookAppFound) {
			String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + urlToShare;
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
		}

		context.startActivity(intent);
	}

	public static void addStoreData(Context context, Object key, String value, String split) {
		if (!checkStoreData(context, key, value, split)) {
			String current = SharedPreference.get(context, String.valueOf(key), "");
			SharedPreference.set(context, String.valueOf(key), current + value + split);
		}
	}

	public static void removeStoreData(Context context, Object key, String value, String split) {
		if (checkStoreData(context, key, value, split)) {
			String current = SharedPreference.get(context, String.valueOf(key), "");
			SharedPreference.set(context, String.valueOf(key), current.replaceAll(value + split, ""));
		}
	}

	public static ArrayList<String> getListStoreData(Context context, Object key, String split) {
		try {
			return splitComme2(SharedPreference.get(context, String.valueOf(key), ""), split);
		} catch (Exception ignored) {
		}
		return new ArrayList<>();
	}

	public static boolean checkStoreData(Context context, Object key, String value, String split) {
		String current = SharedPreference.get(context, String.valueOf(key), "");
		return current.contains(value);
	}

	public static void addStoreData(Context context, Object key, String value) {
		addStoreData(context, key, value, ";");
	}

	public static void removeStoreData(Context context, Object key, String value) {
		removeStoreData(context, key, value, ";");
	}

	public static ArrayList<String> getListStoreData(Context context, Object key) {
		return getListStoreData(context, key, ";");
	}

	public static boolean checkStoreData(Context context, Object key, String value) {
		return checkStoreData(context, key, value, ";");
	}

	public static void gotoStore(Context context, String packageName) {
		Uri uri = Uri.parse("market://details?id=" + packageName);
		Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW);
		myAppLinkToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		myAppLinkToMarket.setPackage("com.android.vending");
		myAppLinkToMarket.setData(uri);
		try {
			context.startActivity(myAppLinkToMarket);
		} catch (Exception e) {
			DebugLog.loge(e);
			myAppLinkToMarket.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
			try {
				context.startActivity(myAppLinkToMarket);
			} catch (Exception e2) {
				DebugLog.loge(e2);
			}
		}
	}

	public static void quickSort(int startPosition, int endPosition, int[] array) {
		int pivot = findPivot(startPosition, endPosition, array);
		if (pivot == -1)
			return;
		int partition = pointPartition(startPosition, endPosition, array[pivot], array);
		quickSort(startPosition, partition - 1, array);
		quickSort(partition, endPosition, array);
	}

	private static int findPivot(int i, int j, int[] array) {
		if (array.length == 1) {
			return -1;
		}
		int k = i + 1;
		int pivot = array[i];

		while ((k <= j) && (array[k] == pivot)) {
			k++;
		}
		if (k > j) {
			return -1;
		}
		if (array[k] > pivot) {
			return k;
		} else {
			return i;
		}
	}

	private static int pointPartition(int i, int j, int pivotKey, int[] array) {
		int partition = -1;

		int L = i;
		int R = j;
		while (L <= R) {
			while (array[L] < pivotKey)
				L++;
			while (array[R] >= pivotKey)
				R--;
			if (L < R) {
				int temp = array[L];
				array[L] = array[R];
				array[R] = temp;
			}
		}
		partition = L;
		return partition;

	}
	
	public static void copyTextToClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Clipboard", text);
        clipboard.setPrimaryClip(clip);
    }
	
	public static void saveImageViewToFile(ImageView imgView, String path, String fileName, boolean isRecycle) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                if (!f.mkdir()) return;
            }
        } catch (Exception e) {
            DebugLog.loge(e);
        }
        try {
            Bitmap bitmap = ((BitmapDrawable)imgView.getDrawable()).getBitmap();
            File file = new File(path, fileName);
            FileOutputStream fOut;

            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            if (isRecycle) {
            	bitmap.recycle();	
            }
           
        } catch (Exception e) {
            DebugLog.loge(e);
        }
    }

	public static void addContentMediaForImageFile(Context context, File file, String picture_title, String picture_description){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, picture_title);
        values.put(MediaStore.Images.Media.DESCRIPTION, picture_description);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis ());
        values.put(MediaStore.Images.ImageColumns.BUCKET_ID, file.toString().toLowerCase(Locale.US).hashCode());
        values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, file.getName().toLowerCase(Locale.US));
        values.put("_data", file.getAbsolutePath());

        ContentResolver cr = context.getContentResolver();
        cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }
	
	public static String encodeImageBase64(String image_url) {
        Bitmap bitmapOrg = BitmapFactory.decodeFile(image_url);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 100, bao);
        byte[] ba = bao.toByteArray();
        String result = Base64.encodeToString(ba, Base64.DEFAULT);
        bitmapOrg.recycle();
        return result;
    }

	public static String encodeImageToBase64(Bitmap image)
	{
		if (image == null) return "";
		Bitmap bitmap = image.copy(image.getConfig(), image.isMutable());
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
		byte[] b = outputStream.toByteArray();
		return Base64.encodeToString(b, Base64.DEFAULT);
	}

	public static Bitmap decodeBase64ToImage(String input)
	{
		if (input.trim().isEmpty()) return null;
		byte[] decodedByte = Base64.decode(input, 0);
		return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
	}

	public static void colorText(TextView textView, String textSelect, String color) {
		styleText(textView, textSelect, color, textView.getTextSize(), -1);
	}

	public static void boldText(TextView textView, String textSelect) {
		styleText(textView, textSelect, "", textView.getTextSize(), android.graphics.Typeface.BOLD);
	}

	public static void styleText(TextView textView, String textSelect, String color, float textSize, int typeface) {
		if (!color.isEmpty() && !color.contains("#")) {
			color = "#"+color;
		}
		String origin = textView.getText().toString();
		try {
			textView.setText(origin, TextView.BufferType.SPANNABLE);
			Spannable s = (Spannable) textView.getText();
			int start = textView.getText().toString().indexOf(textSelect);
			int end = start + textSelect.length();
			if (typeface != -1) s.setSpan(new StyleSpan(typeface), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			if (!color.isEmpty()) s.setSpan(new ForegroundColorSpan(Color.parseColor(color)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (textSize > 0) s.setSpan(new AbsoluteSizeSpan((int) textSize), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		} catch (Exception e) {
			DebugLog.loge(e);
			textView.setText(origin);
		}
	}

	public static void startActivity(Activity activity, Class<?> cls, JSONObject data) {
		startActivity(activity, cls, data, null);
	}

	public static void startActivity(Activity activity, Class<?> cls, JSONObject data, Integer requestCode) {
		Intent intent = new Intent(activity, cls);
		if (data != null) {
			Bundle bundle = new Bundle();
			Iterator<String> keys = data.keys();
			while(keys.hasNext()) {
				String key = keys.next();
				bundle.putString(key, JsonUtils.getStringInJsonObj(data, key));
			}
			intent.putExtras(bundle);
		}

		if (requestCode != null) {
			activity.startActivityForResult(intent, requestCode);
		} else {
			activity.startActivity(intent);
		}
	}

	public static String getBundleData(Activity activity, String key) {
		Bundle bundle = activity.getIntent().getExtras();
		if (bundle != null) {
			if (bundle.get(key) != null) {
				return String.valueOf(bundle.get(key));
			}
		}
		return "";
	}

	static StringBuilder mFormatBuilder;
	static Formatter mFormatter;

	public static String convertTime(int timeMs, boolean showMilli) {
		if (mFormatBuilder == null) mFormatBuilder = new StringBuilder();
		if (mFormatter == null) mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

		int totalSeconds = timeMs / 1000;

		int milli = timeMs % 1000;
		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;

		mFormatBuilder.setLength(0);
		if (showMilli) {
			if (hours > 0) {
				return mFormatter.format("%02d:%02d:%02d,%03d", hours, minutes, seconds, milli).toString();
			} else {
				return mFormatter.format("00:%02d:%02d,%03d", minutes, seconds, milli).toString();
			}
		} else {
			if (hours > 0) {
				return mFormatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString();
			} else {
				return mFormatter.format("00:%02d:%02d", minutes, seconds).toString();
			}
		}
	}

	@ColorInt
	public static int getColor(Context context, @ColorRes int id) {
		return ResourcesCompat.getColor(context.getResources(), id, context.getTheme());
	}

	public static Drawable getDrawable(Context context, @DrawableRes int id) {
		return ResourcesCompat.getDrawable(context.getResources(), id, context.getTheme());
	}

	public static void animationView(View view, ViewAnimationListener callback) {
		startScaleViewAnimation(view, 0.9f, 175, callback);
	}

	public interface ViewAnimationListener {
		void onComplete();
	}

	/**
	 * Start Scale View Animation
	 */
	public static void startScaleViewAnimation(final View view, float scale, long duration, ViewAnimationListener callback) {
		ViewCompat.animate(view).scaleX(scale).scaleY(scale).alpha(0.5f).setDuration(duration);
		new Handler().postDelayed(() -> ViewCompat.animate(view).scaleX(1).scaleY(1).alpha(1).setDuration(duration), duration);
		new Handler().postDelayed(() -> {
			if (callback != null) callback.onComplete();
		}, duration * 2);
	}

	public static boolean isTablet() {
		return (Resources.getSystem().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK)
				>= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
	public static boolean isLandscape(Context context) {
		int orientation = context.getResources().getConfiguration().orientation;
//        DebugLog.loge(orientation == Configuration.ORIENTATION_LANDSCAPE ? "YES" : "NO");
		return orientation == Configuration.ORIENTATION_LANDSCAPE;
	}
}
