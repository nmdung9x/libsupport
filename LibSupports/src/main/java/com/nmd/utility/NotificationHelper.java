package com.nmd.utility;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

/**
 * Created by nmd9x on 9/5/17.
 */

public class NotificationHelper extends ContextWrapper {
    private NotificationManager manager;
    public static final String PRIMARY_CHANNEL = "default";
    public static final String CHANNEL_DEFAULT = "Primary Channel";

    String sound = "";
    boolean isVibrate = false;
    boolean isNotificationLight = false;

    /**
     * Registers notification channels, which can be used later by individual notifications.
     *
     * @param ctx The application context
     */
    public NotificationHelper(Context ctx) {
        super(ctx);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(PRIMARY_CHANNEL, CHANNEL_DEFAULT, NotificationManager.IMPORTANCE_DEFAULT); //IMPORTANCE_HIGH
            channel.enableVibration(isVibrate);
            if (isNotificationLight) {
                channel.enableLights(isNotificationLight);
            }
            if (!(sound.equals("") && sound.equals("default"))) {
                channel.setSound(Uri.parse(sound), null);
            }
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC); //VISIBILITY_PRIVATE
            getManager().createNotificationChannel(channel);
        }
    }

    /**
     * Get a notification of type 1
     *
     * Provide the builder rather than the notification it's self as useful for making notification
     * changes.
     *
     * @param title the title of the notification
     * @param body the body text for the notification
     * @return the builder as it keeps a reference to the notification (since API 24)
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    Notification.Builder getNotificationO(String title, String body, String full_body, Object smallIcon, PendingIntent intent) {
        Notification.Builder mBuilder = new Notification.Builder(getApplicationContext(), PRIMARY_CHANNEL);
        if (!title.trim().isEmpty()) mBuilder.setContentTitle(title);
        if (!body.trim().isEmpty()) mBuilder.setContentText(body);
        if (!full_body.trim().isEmpty()) mBuilder.setStyle(new Notification.BigTextStyle().bigText(full_body));
        if (smallIcon != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && smallIcon instanceof Icon) {
                mBuilder.setSmallIcon((Icon) smallIcon);
            } else {
                mBuilder.setSmallIcon((int) smallIcon);
            }
//        } else {
//            mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        }
        mBuilder.setAutoCancel(false);
        if (intent != null) mBuilder.setContentIntent(intent);
        return mBuilder;
    }

    public Notification.Builder getNotification(String title, String body, String full_body, Object smallIcon, PendingIntent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return getNotificationO(title, body, full_body, smallIcon, intent);
        }

        Notification.Builder mBuilder = new Notification.Builder(getApplicationContext());
        if (!title.trim().isEmpty()) mBuilder.setContentTitle(title);
        if (!body.trim().isEmpty()) mBuilder.setContentText(body);
        if (!full_body.trim().isEmpty()) {
            if (Build.VERSION.SDK_INT >= 24) {
                mBuilder.setStyle(new Notification.BigTextStyle().bigText(full_body));
//            } else {
//                mBuilder.setContentTitle(getString(R.string.app_name));
            }
        }
        if (smallIcon != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && smallIcon instanceof Icon) {
                mBuilder.setSmallIcon((Icon) smallIcon);
            } else {
                mBuilder.setSmallIcon((int) smallIcon);
            }
//        } else {
//            mBuilder.setSmallIcon(Build.VERSION.SDK_INT >= 24 ? R.mipmap.ic_launcher_round : R.mipmap.ic_launcher);
        }

        mBuilder.setAutoCancel(false);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        if (intent != null) mBuilder.setContentIntent(intent);
        return mBuilder;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Notification.Builder getSummaryNotification(String title, String body, int smallIcon, @NonNull Bitmap iconLarge, @NonNull String group_key) {
        Notification.InboxStyle inboxStyle = new Notification.InboxStyle();
        if (!title.trim().isEmpty()) inboxStyle.setBigContentTitle(title);
        if (!body.trim().isEmpty()) inboxStyle.setSummaryText(body);

        Notification.Builder mBuilder = new Notification.Builder(getApplicationContext());
        mBuilder.setAutoCancel(false);
        mBuilder.setGroup(group_key);
        mBuilder.setGroupSummary(true);
        mBuilder.setStyle(inboxStyle);
        if (smallIcon != -1) mBuilder.setSmallIcon(smallIcon);
        mBuilder.setLargeIcon(iconLarge);
        return mBuilder;
    }

    public void setSound(String s) {
        sound = s;
    }

    public void setVibrate(boolean b) {
        isVibrate = b;
    }

    public void setNotificationLight(boolean b) {
        isNotificationLight = b;
    }

    /**
     * Send a notification.
     *
     * @param id The ID of the notification
     * @param notification The notification object
     */
    public void notify(int id, Notification.Builder mBuilder) {
        notify(id, mBuilder.build());
    }

    public void notify(int id, Notification.Builder mBuilder, int flag) {
        Notification notification = mBuilder.build();
        notification.flags = flag;
        notify(id, notification);
    }

    public void notify(int id, Notification notification) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            if (sound.equals("default")){
                notification.defaults |= Notification.DEFAULT_SOUND;
            } else {
//			e.g: "android.resource://com.niw.lounge/" + R.raw.entersound
                notification.sound = Uri.parse(sound);
            }
            if (isVibrate){
                notification.defaults |= Notification.DEFAULT_VIBRATE;
            }
            if (isNotificationLight) {
                notification.defaults |= Notification.DEFAULT_LIGHTS;
                notification.flags |= Notification.FLAG_SHOW_LIGHTS;
            }
        }
        getManager().notify(id, notification);
    }

    public void remove(int id) {
        getManager().cancel(id);
    }

    public void removeAll(int id) {
        getManager().cancelAll();
    }

    /**
     * Get the notification manager.
     *
     * Utility method as this helper works with it a lot.
     *
     * @return The system service NotificationManager
     */
    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }
}