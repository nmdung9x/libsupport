package com.nmd.utility.other;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

public class NotificationCustomSettings {
	static Context mContext;
	private static NotificationManager notificationManager;
	public static Notification notification = null;
	public static int NOTIFICATION_ID = 0;
	
	public static String title = "";
	public static String sound = "";
	public static boolean isPlaySound = true;
	public static boolean isVibrate = true;
	
	public static int iconKitkat = -1;
	public static int iconLollipop = -1;
	
	public static void setContext(Context context) {
		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mContext = context;
	}
	
	static int getNotificationIcon(int iconKitkat, int iconLollipop) {
	    boolean whiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
	    return whiteIcon ? iconLollipop : iconKitkat;
	}
	
	public static void generateNotification(Context context, Class<?> cls, int icon, String message) {
		generateNotification(context, cls, icon, message, "", "");
	}
	
	@SuppressWarnings("deprecation")
	public static void generateNotification(Context context,  Class<?> cls, int icon, String message, String checkingKey, String checkingValue) {
		if (notificationManager == null) setContext(context);
		
		if (iconKitkat == -1){
			iconKitkat = icon;
		}
		
		if (iconLollipop == -1){
			iconLollipop = icon;
		}
		
		NOTIFICATION_ID = (int) System.currentTimeMillis();

		long when = System.currentTimeMillis();
		Notification notification = new Notification();
		
		

		Intent notificationIntent = null;
		try {
			notificationIntent = new Intent(context, cls);
		} catch (Exception e) {
		}
		
		if (!checkingKey.equals("") && !checkingValue.equals("")){
			notificationIntent.putExtra(checkingKey, checkingValue);
		}
		
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		notificationIntent.setAction("" + NOTIFICATION_ID);
		
		PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
//		notification.setLatestEventInfo(context, title, message, intent);
		if (android.os.Build.VERSION.SDK_INT >= 16) {
			if(title.equals("")){
				notification  = new NotificationCompat.Builder(context)
						.setContentTitle(message)
						.setSmallIcon(getNotificationIcon(iconKitkat, iconLollipop))
						.setStyle(new NotificationCompat.BigTextStyle().bigText(message))
						.setWhen(when)
						.setContentIntent(intent)
						.build();
			}else{
				notification  = new NotificationCompat.Builder(context)
						.setContentTitle(title)
						.setContentText(message)
						.setSmallIcon(getNotificationIcon(iconKitkat, iconLollipop))
						.setStyle(new NotificationCompat.BigTextStyle().bigText(message))
						.setWhen(when)
						.setContentIntent(intent)
						.build();
			}
		} else {
			notification = new Notification(getNotificationIcon(iconKitkat, iconLollipop), message, when);
			notification.contentIntent = intent;
		}
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		// Play default notification sound
		if(isPlaySound){
			if(sound.equals("")){
				notification.defaults |= Notification.DEFAULT_SOUND;			
			}else{
//			e.g: "android.resource://com.niw.lounge/" + R.raw.entersound
				Uri path = Uri.parse(sound);
				notification.sound = path;			
			}
		}
	
		if(isVibrate){
			notification.defaults |= Notification.DEFAULT_VIBRATE;			
		}
		notificationManager.notify(NOTIFICATION_ID, notification);
	}
	
	public static String getCheckingValueFromNotify(Intent intent, String checkingKey){
		String data = "";
		try {
			Bundle extras = intent.getExtras();
			if(extras != null){
		        if(extras.containsKey(checkingKey)){
		        	data = extras.getString(checkingKey);
		        }
			}
		} catch (Exception e) {
		}
		return data;
	}
	
	public static void cancel(Context context, int notifyID) {
		if (notificationManager == null) setContext(context);
		try {
			notificationManager.cancel(notifyID);;
		} catch (Exception e) {
		}
	}
	
	public static void cancelAll(Context context) {
		if (notificationManager == null) setContext(context);
		notificationManager.cancelAll();
	}

}
