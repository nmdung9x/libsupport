package com.nmd.utility.other;

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

import com.nmd.utility.SharedPreference;
import com.nmd.utility.UtilLibs;
import com.nmd.utility.UtilityMain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

public class UnCaughtException implements UncaughtExceptionHandler {

	private Context context;

	public UnCaughtException(Context ctx) {
		context = ctx;
	}

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
			Log.e(UnCaughtException.class.getName(), "Error while sendErrorMail" + report);
			sendErrorMail(report, e);
		} catch (Throwable ignore) {
			Log.e(UnCaughtException.class.getName(), "Error while sending error e-mail", ignore);
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	void recordErrorLog(String content){
		File logFile = new File(UtilityMain.path+ "/"+UtilityMain.TAG+"_error.log");
		if (!logFile.exists()){
			try{
				logFile.createNewFile();
			} catch (IOException e){
				e.printStackTrace();
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
		if(SharedPreference.get(context, UtilLibs.Keys.LIBS_SUPP_IS_APP_CRASH_0.name(), "0").equals("0")){
			SharedPreference.set(context, UtilLibs.Keys.LIBS_SUPP_IS_APP_CRASH_0.name(), "1");
		}else{
			SharedPreference.set(context, UtilLibs.Keys.LIBS_SUPP_IS_APP_CRASH_1.name(), "1");
		}
//		if(!UtilLibs.isAppCrash0(context)){
//			UtilLibs.hasAppCrash0(context, true);	
//		}else{
//			UtilLibs.hasAppCrash(context, true);			
//		}
		recordErrorLog(errorContent.toString());
		System.exit(1);
		android.os.Process.killProcess(android.os.Process.myPid());
		
//		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
//		new Thread() {
//			@Override
//			public void run() {
//				Looper.prepare();
//				builder.setTitle("Sorry...!");
//				builder.create();
//				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							System.exit(0);
//							android.os.Process.killProcess(android.os.Process.myPid()); 
//						}
//					});
//				builder.setPositiveButton("Report", new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog,
//								int which) {
//							Intent sendIntent = new Intent(Intent.ACTION_SEND);
//							String subject = "Your App crashed! Fix it!";
//							StringBuilder body = new StringBuilder("Yoddle");
//							body.append('\n').append('\n');
//							body.append(errorContent).append('\n').append('\n');
//							// sendIntent.setType("text/plain");
//							sendIntent.setType("message/rfc822");
//							sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "dungnm@niw.com.vn" });
//							sendIntent.putExtra(Intent.EXTRA_TEXT, body.toString());
//							sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
//							sendIntent.setType("message/rfc822");
//							context1.startActivity(sendIntent);
//							android.os.Process.killProcess(android.os.Process.myPid()); 
//							System.exit(0);
//						}
//					});
//				builder.setMessage("Oops,Your application has crashed");
//				builder.show();
//				Looper.loop();
//			}
//		}.start();
	}
}