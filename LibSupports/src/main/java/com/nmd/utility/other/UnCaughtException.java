package com.nmd.utility.other;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ShareCompat;

import com.nmd.utility.DebugLog;
import com.nmd.utility.UtilLibs;
import com.nmd.utility.UtilityMain;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UnCaughtException implements UncaughtExceptionHandler {

	private Context context;

	public UnCaughtException(Context ctx) {
		context = ctx;
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		try {
			StringBuilder report = new StringBuilder();
			Date curDate = new Date();
			report.append("Error Report collected on : ").append(curDate.toString()).append('\n').append('\n');
			report.append("Informations :").append('\n');
			report.append(UtilLibs.getInfoDevices());
			report.append('\n').append('\n');
			report.append("Stack:\n");
			final Writer result = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			report.append(result.toString());
			printWriter.close();
			report.append('\n');
			report.append("**** End of current Report ***");
			sendErrorMail(report, e);
		} catch (Throwable ignore) {
			Log.e(UnCaughtException.class.getName(), "Error while sending error e-mail", ignore);
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	void recordErrorLog(String content){
		File logFile = UtilityMain.logCrashFile();
		if (logFile == null) return;
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (Exception e) {
				DebugLog.logi("DebugLog", "Can't create log file in AppDataDir");
				return;
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
		
		try{
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
			buf.append(sdf.format(new Date(System.currentTimeMillis())) + "_" + content);
			buf.newLine();
			buf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method for call alert dialog when application crashed!
	 */
	public void sendErrorMail(final StringBuilder errorContent, Throwable e) {
		recordErrorLog(errorContent.toString());
		if (UtilityMain.emailsForErrorReport.length == 0) {
			System.exit(0);
		} else {
			final AlertDialog.Builder builder = new AlertDialog.Builder(context);
			new Thread() {
				@Override
				public void run() {
					Looper.prepare();
					builder.setTitle("Xin lỗi...!");
					builder.setMessage("Rất tiếc, Ứng dụng đã bị dừng đột ngột do lỗi.");
					builder.create();
					builder.setNegativeButton("Bỏ qua", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							System.exit(0);
						}
					});
					builder.setPositiveButton("Gửi lỗi", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String subject = "Ứng dụng bị dừng!";
							StringBuilder body = new StringBuilder();
							body.append(errorContent.toString()).append('\n').append('\n');

							try {
								ShareCompat.IntentBuilder.from((Activity) context)
										.setType("message/rfc822")
										.addEmailTo(UtilityMain.emailsForErrorReport)
										.setSubject(subject)
										.setText(body)
										.setChooserTitle("Gửi lỗi")
										.startChooser();
							} catch (Exception e1) {
								Log.e(UnCaughtException.class.getName(), "Error while sending error e-mail", e1);
							} finally {
								System.exit(0);
							}
						}
					});
					builder.show();
					Looper.loop();
				}
			}.start();
		}
	}
}