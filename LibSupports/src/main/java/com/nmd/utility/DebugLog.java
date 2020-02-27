package com.nmd.utility;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.nmd.utility.other.ZNetworkData;
import com.nmd.utility.other.ZUploadLogService;
import com.nmd.utility.other.ZUploadLogService.OnUploadLogResult;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

public class DebugLog {
	enum Type {
		v,
		d,
		i,
		n,
		w,
		e
	}
	
	public static void log(Type type, String tag, String msg, Object obj, boolean upOnline){
    	String message = "";
    	boolean isException = false;
    	if (obj != null) {
    		if (obj instanceof Exception) {
        		isException = true;
        		Exception e = (Exception) obj;
        		StringWriter errors = new StringWriter();
            	e.printStackTrace(new PrintWriter(errors));
            	
            	message = msg.trim().isEmpty() ? errors.toString() : msg.trim()+"\n"+errors.toString();
        	} else if (obj instanceof Throwable) {
        		isException = true;
        		Throwable e = (Throwable) obj;
        		StringWriter errors = new StringWriter();
            	e.printStackTrace(new PrintWriter(errors));
            	
            	message = msg.trim().isEmpty() ? errors.toString() : msg.trim()+"\n"+errors.toString();
        	} else {
        		isException = false;
        		message = msg.trim().isEmpty() ? String.valueOf(obj).trim() : msg.trim()+"\n"+String.valueOf(obj).trim();
        	}
    	} else {
    		isException = false;
    		message = msg.trim();
    	}
 
    	
    	StackTraceElement stack = Thread.currentThread().getStackTrace()[4];
        String fullClassName = stack.getClassName();
        String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);

        if (className.contains("$")) {
            className = className.substring(0, className.lastIndexOf("$"));
        }

        String methodName = stack.getMethodName();
        int lineNumber = stack.getLineNumber();
        
        String check = "at ("+ className + ".java:" + lineNumber + ") " + "[" + methodName + "] ";
        
        if (isException) {
        	switch (type) {
			case v:
				if (UtilityMain.SHOW_LOG_D) {
					Log.v(tag, check + message);
				}
				if (UtilityMain.DEBUG_D) {
		            appendLog("Log---v " + check + message, upOnline, tag);
		        }
				break;

			case d:
				if (UtilityMain.SHOW_LOG_D) {
					Log.d(tag, check + message);
				}
				if (UtilityMain.DEBUG_D) {
		            appendLog("Log---d " + check + message, upOnline, tag);
		        }
				break;

			case i:
				if (UtilityMain.SHOW_LOG_I) {
					Log.i(tag, check + message);
				}
				if (UtilityMain.DEBUG_I) {
		            appendLog("Log---i " + check + message, upOnline, tag);
		        } else {
					if (upOnline) {
						appendLog("Log---i " + check + message, true, tag);
					}
				}
				break;

			case n:
				if (UtilityMain.SHOW_LOG_N) {
					Log.i(tag, check + message);
				}
				if (UtilityMain.DEBUG_N) {
		            appendLog("Log---n " + check + message, upOnline, tag);
		        }
				break;

			case w:
				if (UtilityMain.SHOW_LOG_I) {
					Log.w(tag, check + message);
				}
				if (UtilityMain.DEBUG_I) {
		            appendLog("Log---w " + check + message, upOnline, tag);
		        }
				break;

			case e:
				if (UtilityMain.SHOW_LOG_E) {
					Log.e(tag, check + message);
				}
				if (UtilityMain.DEBUG_E) {
		            appendLog("Log---e " + check + message, upOnline, tag);
		        }
				break;

			default:
				break;
			}
        } else {
        	switch (type) {
			case v:
				if (UtilityMain.SHOW_LOG_D && UtilityMain.SHOW_LOG_E_EXCEPTION) {
					Log.v(tag, check + message);
				}
				if (UtilityMain.DEBUG_D && UtilityMain.DEBUG_E_EXCEPTION) {
		            appendLog("Log---v " + check + message, upOnline, tag);
		        }
				break;

			case d:
				if (UtilityMain.SHOW_LOG_D && UtilityMain.SHOW_LOG_E_EXCEPTION) {
					Log.d(tag, check + message);
				}
				if (UtilityMain.DEBUG_D && UtilityMain.DEBUG_E_EXCEPTION) {
		            appendLog("Log---d " + check + message, upOnline, tag);
		        }
				break;

			case i:
				if (UtilityMain.SHOW_LOG_I && UtilityMain.SHOW_LOG_E_EXCEPTION) {
					Log.i(tag, check + message);
				}
				if (UtilityMain.DEBUG_I && UtilityMain.DEBUG_E_EXCEPTION) {
		            appendLog("Log---i " + check + message, upOnline, tag);
				} else {
					if (upOnline) {
						appendLog("Log---i " + check + message, true, tag);
					}
				}
				break;

			case n:
				if (UtilityMain.SHOW_LOG_N && UtilityMain.SHOW_LOG_E_EXCEPTION) {
					Log.i(tag, check + message);
				}
				if (UtilityMain.DEBUG_N && UtilityMain.DEBUG_E_EXCEPTION) {
		            appendLog("Log---n " + check + message, upOnline, tag);
		        }
				break;

			case w:
				if (UtilityMain.SHOW_LOG_I && UtilityMain.SHOW_LOG_E_EXCEPTION) {
					Log.w(tag, check + message);
				}
				if (UtilityMain.DEBUG_I && UtilityMain.DEBUG_E_EXCEPTION) {
		            appendLog("Log---w " + check + message, upOnline, tag);
		        }
				break;

			case e:
				if (UtilityMain.SHOW_LOG_E_EXCEPTION) {
					Log.e(tag, check + message);
				}
				if (UtilityMain.DEBUG_E_EXCEPTION) {
		            appendLog("Log---e " + check + message, upOnline, tag);
		        }
				break;

			default:
				break;
			}
        }
        
    }
	
    public static void logv(Object obj){
        log(Type.v, UtilityMain.TAG, "", obj, false);
    }
    
    public static void logv(String tag, Object obj){
    	log(Type.v, tag, "", obj, false);
    }
    
    public static void logv(String tag, String msg, Object obj){
    	log(Type.v, tag, msg, obj, false);
    }
	
    public static void logd(Object obj){
        log(Type.d, UtilityMain.TAG, "", obj, false);
    }
    
    public static void logd(String tag, Object obj){
    	log(Type.d, tag, "", obj, false);
    }
    
    public static void logd(String tag, String msg, Object obj){
    	log(Type.d, tag, msg, obj, false);
    }
	
    public static void logi(Object obj){
        log(Type.i, UtilityMain.TAG, "", obj, false);
    }
    
    public static void logi(String tag, Object obj){
    	log(Type.i, tag, "", obj, false);
    }
    
    public static void logi(String tag, String msg, Object obj){
    	log(Type.i, tag, msg, obj, false);
    }
	
    public static void logn(Object obj){
        log(Type.n, UtilityMain.TAG, "", obj, false);
    }
    
    public static void logn(String tag, Object obj){
    	log(Type.n, tag, "", obj, false);
    }
    
    public static void logn(String tag, String msg, Object obj){
    	log(Type.n, tag, msg, obj, false);
    }
	
    public static void logw(Object obj){
        log(Type.w, UtilityMain.TAG, "", obj, false);
    }
    
    public static void logw(String tag, Object obj){
    	log(Type.w, tag, "", obj, false);
    }
    
    public static void logw(String tag, String msg, Object obj){
    	log(Type.w, tag, msg, obj, false);
    }
	
    public static void loge(Object obj){
        log(Type.e, UtilityMain.TAG, "", obj, false);
    }
    
    public static void loge(String tag, Object obj){
    	log(Type.e, tag, "", obj, false);
    }
    
    public static void loge(String tag, String msg, Object obj){
    	log(Type.e, tag, msg, obj, false);
    }
    
    public static void log_online(Object obj){
    	log(Type.i, "", "", obj, true);
    }
    
    public static void log_online(String filename, Object obj){
    	log(Type.i, filename, "", obj, true);
    }
    
	static void appendLog(String text, boolean upOnline, String filename) {
		if (upOnline && UtilityMain.DEBUG_ONLINE && !text.trim().isEmpty()) {
			if (filename.trim().isEmpty()) filename = UtilityMain.zfilename();
			Context context = UtilityMain.mContext;
			if (context == null) {
				DebugLog.loge("Context null!");
			} else {
				new ZUploadLogService().uploadLogV2(context, ZNetworkData.API_UPLOG_ONLINE,  ZNetworkData.uploadlog(filename, text), new OnUploadLogResult() {

					@Override
					public void uploadLogMethod(boolean isSuccess) {}
				});
			}

		}
		if (!UtilityMain.isRecordLog) {
			return;
		}
		
		boolean isNew = false;
		
		File logFile = new File(UtilityMain.path + "/" + UtilityMain.TAG + ".log");
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
				isNew = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
			if (isNew) {
				buf.append(UtilLibs.getInfoDevices()).append('\n');
			}
			buf.append(getCurrentTime() + " (" + UtilLibs.getTimeZoneInLocal() + ")" + "_" + text).append('\n');
			buf.newLine();
			buf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    @SuppressLint("SimpleDateFormat")
	private static String getCurrentTime(){
    	SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
        Date resultdate = new Date(System.currentTimeMillis());
		return sdf.format(resultdate);
    }
}
