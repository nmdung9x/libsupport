package com.nmd.utility.other;

import java.util.ArrayList;

import com.nmd.utility.UtilityMain;

public class ZNetworkData {
	
	public static String HOST_NAME = "";
	
	public static String API_CHECKLOG = "";
	public static String API_UPLOG = "";
	public static String API_UPLOG_ONLINE = "";
	
	public static String API_CHECKLOG(){
		if(!HOST_NAME.isEmpty()){
			return HOST_NAME + "/checklog";
		}else{
			return API_CHECKLOG;
		}
	}
	
	public static String API_UPLOG(){
		if(!HOST_NAME.isEmpty()){
			return HOST_NAME + "/uploadlog";
		}else{
			return API_UPLOG;
		}
	}

	public static ArrayList<Data> checklog(String name, String status, String note){
		ArrayList<Data> data = new ArrayList<Data>();
		data.add(new Data("name", name.replaceAll(" ", "_")));
		data.add(new Data("status", status));
		data.add(new Data("note", note.replaceAll(" ", "_")));
		
		return data;
	}

	public static ArrayList<Data> uploadlog(String filename){
		ArrayList<Data> data = new ArrayList<Data>();
		data.add(new Data("filename", filename.replaceAll(" ", "_")));
		if (UtilityMain.mContext != null) {
			data.add(new Data("package", UtilityMain.TAG));
		}
		
		return data;
	}
	
	public static ArrayList<Data> uploadlog(String filename, String content){
		ArrayList<Data> data = new ArrayList<Data>();
		data.add(new Data("filename", filename.replaceAll(" ", "_")));
		data.add(new Data("content", content));
		if (UtilityMain.mContext != null) {
			data.add(new Data("package", UtilityMain.TAG));
		}
		return data;
	}
	
}
