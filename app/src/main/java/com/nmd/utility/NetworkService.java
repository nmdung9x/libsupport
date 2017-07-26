package com.nmd.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.nmd.utility.other.Data;

import android.content.Context;

@SuppressWarnings("deprecation")
public class NetworkService {

	public static String requestMethod = "POST";

	HttpURLConnection createConnectionWithURL(String strUrl) throws IOException, ProtocolException {
		URL url = new URL(strUrl);
		HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
		
		urlc.setRequestMethod(requestMethod);
		urlc.setDoOutput(true);
		urlc.setDoInput(true);
		urlc.setUseCaches(false);
		urlc.setAllowUserInteraction(false);
		urlc.setConnectTimeout(20000);

		return urlc;
	}
	
	HttpURLConnection createConnectionWithURL(String strUrl, ArrayList<Data> header) throws IOException, ProtocolException {
		URL url = new URL(strUrl);
		HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
		
		if(header.size()>0){
			DebugLog.logn("\n" + "Add Header :");
			for (int i = 0; i < header.size(); i++) {
				DebugLog.logn("\n" + header.get(i).getKey() + " : " + header.get(i).getValue());
				urlc.setRequestProperty(header.get(i).getKey(), header.get(i).getValue());
			}
		}

		urlc.setRequestMethod(requestMethod);
		urlc.setDoOutput(true);
		urlc.setDoInput(true);
		urlc.setUseCaches(false);
		urlc.setAllowUserInteraction(false);
		urlc.setConnectTimeout(20000);

		return urlc;
	}

	String getQuery(List<BasicNameValuePair> params) throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		boolean first = true;

		for (BasicNameValuePair pair : params) {
			if (first) {
				first = false;
			} else {
				result.append("&");
			}

			result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
		}

		return result.toString();
	}

	/*
	 * TODO**********************************************************************
	 */
	
	String parseResponseData(InputStream is){
		if(is==null) return "";
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			sb.append(reader.readLine() + "\n");

			String line = "0";
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			return sb.toString();
		} catch (Exception e) {
				DebugLog.loge(e);
			return "";
		}
	}

	@SuppressWarnings("resource")
	public String getResponseByGETMethod(String url) {
		String result = "";
		InputStream is = null;
		
		if (url.equals("")) {
			DebugLog.loge("\nURL EMPTY!!!");
			return "";
		} else {
			DebugLog.logn("\nConnecting to...\n" + url);
		}

		try {
			HttpGet httpget = new HttpGet(url);
			httpget.addHeader("accept", "application/json");
			httpget.addHeader("Charset", "UTF-8");
			

			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 20000);
			HttpConnectionParams.setSoTimeout(httpParams, 20000);

			HttpClient httpclient = new DefaultHttpClient(httpParams);
			HttpResponse response = httpclient.execute(httpget);
			
//			UrlEncodedFormEntity encodeData = new UrlEncodedFormEntity(new ArrayList<NameValuePair>(), "UTF-8");
//			response.setEntity(encodeData);

			HttpEntity entity = response.getEntity();
			is = entity.getContent();
			
			result = parseResponseData(is);
		} catch (SocketTimeoutException stoex) {
				DebugLog.loge("Error in socket connection:\n" + stoex);
		} catch (ConnectTimeoutException cex) {
				DebugLog.loge("Error in http connection:\n" + cex);
		} catch (Exception e) {
			DebugLog.loge("Error:\n" + e);
		}

		return result;
	}

	@SuppressWarnings("resource")
	public String getResponseByGETMethod(String url, ArrayList<Data> data) {
		String result = "";
		InputStream is = null;
		
		if (url.equals("")) {
			DebugLog.loge("\nURL EMPTY!!!");
			return "";
		} else {
			DebugLog.logn("\nConnecting to...\n" + url);
		}
		
		if (data == null) {
//			DebugLog.logn("\nRESQUEST DATA NOT FOUND!!!");
			return getResponseByGETMethod(url);
		}
		
		if (data.size() <= 0) {
//			DebugLog.logn("\nRESQUEST DATA NOT FOUND!!!");
			return getResponseByGETMethod(url);
		}
		
		List<BasicNameValuePair> param = new ArrayList<BasicNameValuePair>();
		DebugLog.logn("\n" + "Request data :");
		for (int i = 0; i < data.size(); i++) {
			DebugLog.logn("\n" + data.get(i).getKey() + " : " + data.get(i).getValue());
			param.add(new BasicNameValuePair(data.get(i).getKey(), data.get(i).getValue()));
		}
		
		StringBuilder newUrl = new StringBuilder();
		newUrl.append(url);
		for (int k = 0; k < param.size(); k++) {
			if (k == 0) {
				newUrl.append("?");
			} else {
				newUrl.append("&");
			}
			newUrl.append(param.get(k).getName());
			newUrl.append("=" + param.get(k).getValue());
		}

		try {
			HttpGet httpget = new HttpGet(newUrl.toString());
			httpget.addHeader("accept", "application/json");

			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 20000);
			HttpConnectionParams.setSoTimeout(httpParams, 20000);

			HttpClient httpclient = new DefaultHttpClient(httpParams);
			HttpResponse response = httpclient.execute(httpget);

			HttpEntity entity = response.getEntity();
			is = entity.getContent();
			
			result = parseResponseData(is);
		} catch (SocketTimeoutException stoex) {
				DebugLog.loge("Error in socket connection:\n" + stoex);
		} catch (ConnectTimeoutException cex) {
				DebugLog.loge("Error in http connection:\n" + cex);
		} catch (Exception e) {
			DebugLog.loge("Error:\n" + e);
		}

		return result;
	}

	@SuppressWarnings("resource")
	public String getResponseByGETMethod(String url, ArrayList<Data> data, ArrayList<Data> header) {
		String result = "";
		InputStream is = null;
		
		if (url.equals("")) {
			DebugLog.loge("\nURL EMPTY!!!");
			return "";
		} else {
			DebugLog.logn("\nConnecting to...\n" + url);
		}
		
		if (data == null) {
//			DebugLog.logn("\nRESQUEST DATA NOT FOUND!!!");
			return getResponseByGETMethod(url);
		}
		
		if (data.size() <= 0) {
//			DebugLog.logn("\nRESQUEST DATA NOT FOUND!!!");
			return getResponseByGETMethod(url);
		}
		
		List<BasicNameValuePair> param = new ArrayList<BasicNameValuePair>();
		DebugLog.logn("\n" + "Request data :");
		for (int i = 0; i < data.size(); i++) {
			DebugLog.logn("\n" + data.get(i).getKey() + " : " + data.get(i).getValue());
			param.add(new BasicNameValuePair(data.get(i).getKey(), data.get(i).getValue()));
		}
		
		StringBuilder newUrl = new StringBuilder();
		newUrl.append(url);
		for (int k = 0; k < param.size(); k++) {
			if (k == 0) {
				newUrl.append("?");
			} else {
				newUrl.append("&");
			}
			newUrl.append(param.get(k).getName());
			newUrl.append("=" + param.get(k).getValue());
		}

		try {
			HttpGet httpget = new HttpGet(newUrl.toString());
//			httpget.addHeader("accept", "application/json");
			if(header.size()>0){
				DebugLog.logn("\n" + "Add Header :");
				for (int i = 0; i < header.size(); i++) {
					DebugLog.logn("\n" + header.get(i).getKey() + " : " + header.get(i).getValue());
					httpget.addHeader(header.get(i).getKey(), header.get(i).getValue());
				}
			}

			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 20000);
			HttpConnectionParams.setSoTimeout(httpParams, 20000);

			HttpClient httpclient = new DefaultHttpClient(httpParams);
			HttpResponse response = httpclient.execute(httpget);

			HttpEntity entity = response.getEntity();
			is = entity.getContent();
			
			result = parseResponseData(is);
		} catch (SocketTimeoutException stoex) {
				DebugLog.loge("Error in socket connection:\n" + stoex);
		} catch (ConnectTimeoutException cex) {
				DebugLog.loge("Error in http connection:\n" + cex);
		} catch (Exception e) {
			DebugLog.loge("Error:\n" + e);
		}

		return result;
	}
	
	public String getResponseByttpsPOSTMethod(String url, ArrayList<Data> data) {
		String result = "";
		InputStream is = null;

		if (url.equals("")) {
			DebugLog.loge("\nURL EMPTY!!!");
			return "";
		} else {
			DebugLog.logn("\nConnecting to...\n" + url);
		}

		if (data.size() <= 0) {
			DebugLog.logn("\nRESQUEST DATA NOT FOUND!!!");
		}

		try {
			HttpPost httppost = new HttpPost(url);

			if (data.size() > 0) {
				List<BasicNameValuePair> param = new ArrayList<BasicNameValuePair>();
				DebugLog.logn("\n" + "Request data :");
				for (int i = 0; i < data.size(); i++) {
					DebugLog.logn("\n" + data.get(i).getKey() + " : " + data.get(i).getValue());
					param.add(new BasicNameValuePair(data.get(i).getKey(), data.get(i).getValue()));
				}

				UrlEncodedFormEntity encodeData = new UrlEncodedFormEntity(param);
				httppost.setEntity(encodeData);
			}

			CookieStore cookieStore = new BasicCookieStore();
			HttpContext httpContext = new BasicHttpContext();
			httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

			HttpClient httpclient = getNewHttpClient();
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();

			List<Cookie> cookies = cookieStore.getCookies();
			String remember_token = "";

			if (cookies.isEmpty()) {
				DebugLog.loge("None");
			} else {
				for (int i = 0; i < cookies.size(); i++) {
					// DebugLog.loge("cookies " + i + ":\n" +
					// cookies.get(i).toString());
					if (cookies.get(i).toString().contains("remember_token")) {
						ArrayList<String> list = UtilLibs.splitComme2(cookies.get(i).toString(), "\\]\\[");
						for (int j = 0; j < list.size(); j++) {
							// DebugLog.loge("\n"+ list.get(j).toString());
							if (list.get(j).toString().contains("value")) {
								String value = list.get(j).toString();
								remember_token = value.replaceAll("value:", " ");
							}
						}
					}
				}
			}

			String result0 = parseResponseData(is);
			JSONObject js = new JSONObject();
			js.put("result", result0);
			js.put("remember_token", remember_token.trim());
			result = js.toString();
			// result = parseResponseData(is);

		} catch (SocketTimeoutException stoex) {
				DebugLog.loge("Error in socket connection:\n" + stoex);
		} catch (ConnectTimeoutException cex) {
				DebugLog.loge("Error in http connection:\n" + cex);
		} catch (Exception e) {
				DebugLog.loge("Error:\n" + e);
		}

		return result;
	}
	
	public String getResponseByttpsPOSTMethod(String url, ArrayList<Data> data, ArrayList<Data> header) {
		String result = "";
		InputStream is = null;

		if (url.equals("")) {
			DebugLog.loge("\nURL EMPTY!!!");
			return "";
		} else {
			DebugLog.logn("\nConnecting to...\n" + url);
		}

		if (data.size() <= 0) {
			DebugLog.logn("\nRESQUEST DATA NOT FOUND!!!");
		}

		try {
			HttpPost httppost = new HttpPost(url);
			if(header.size()>0){
				DebugLog.logn("\n" + "Add Header :");
				for (int i = 0; i < header.size(); i++) {
					DebugLog.logn("\n" + header.get(i).getKey() + " : " + header.get(i).getValue());
					httppost.addHeader(header.get(i).getKey(), header.get(i).getValue());
				}
			}


			if (data.size() > 0) {
				List<BasicNameValuePair> param = new ArrayList<BasicNameValuePair>();
				DebugLog.logn("\n" + "Request data :");
				for (int i = 0; i < data.size(); i++) {
					DebugLog.logn("\n" + data.get(i).getKey() + " : " + data.get(i).getValue());
					param.add(new BasicNameValuePair(data.get(i).getKey(), data.get(i).getValue()));
				}

				UrlEncodedFormEntity encodeData = new UrlEncodedFormEntity(param);
				httppost.setEntity(encodeData);
			}

			CookieStore cookieStore = new BasicCookieStore();
			HttpContext httpContext = new BasicHttpContext();
			httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

			HttpClient httpclient = getNewHttpClient();
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();

			List<Cookie> cookies = cookieStore.getCookies();
			String remember_token = "";

			if (cookies.isEmpty()) {
				DebugLog.loge("None");
			} else {
				for (int i = 0; i < cookies.size(); i++) {
					// DebugLog.loge("cookies " + i + ":\n" +
					// cookies.get(i).toString());
					if (cookies.get(i).toString().contains("remember_token")) {
						ArrayList<String> list = UtilLibs.splitComme2(cookies.get(i).toString(), "\\]\\[");
						for (int j = 0; j < list.size(); j++) {
							// DebugLog.loge("\n"+ list.get(j).toString());
							if (list.get(j).toString().contains("value")) {
								String value = list.get(j).toString();
								remember_token = value.replaceAll("value:", " ");
							}
						}
					}
				}
			}

			String result0 = parseResponseData(is);
			JSONObject js = new JSONObject();
			js.put("result", result0);
			js.put("remember_token", remember_token.trim());
			result = js.toString();
			// result = parseResponseData(is);

		} catch (SocketTimeoutException stoex) {
				DebugLog.loge("Error in socket connection:\n" + stoex);
		} catch (ConnectTimeoutException cex) {
				DebugLog.loge("Error in http connection:\n" + cex);
		} catch (Exception e) {
				DebugLog.loge("Error:\n" + e);
		}

		return result;
	}

	public String getResponseByBasicNameValuePairWithSSL(String url, ArrayList<Data> data) {
		String result = "";
		InputStream is = null;
		
		if (url.equals("")) {
			DebugLog.loge("\nURL EMPTY!!!");
			return "";
		} else {
			DebugLog.logn("\nConnecting to...\n" + url);
		}
		
		try {
			HttpPost httppost = new HttpPost(url);
			httppost.addHeader("Charset", "UTF-8");
			
			if(data!=null){
				if(data.size()>0){
					List<BasicNameValuePair> param = new ArrayList<BasicNameValuePair>();
					DebugLog.logn("\n" + "Request data :");
					for (int i = 0; i < data.size(); i++) {
						DebugLog.logn("\n" + data.get(i).getKey() + " : " + data.get(i).getValue());
						param.add(new BasicNameValuePair(data.get(i).getKey(), data.get(i).getValue()));
					}
					
					UrlEncodedFormEntity encodeData = new UrlEncodedFormEntity(param, "UTF-8");
					httppost.setEntity(encodeData);
				}
			}

			HttpClient httpclient = getNewHttpClient();
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
			
			result = parseResponseData(is);
		} catch (SocketTimeoutException stoex) {
			if (stoex != null)
				DebugLog.loge("Error in socket connection:\n" + stoex);
		} catch (ConnectTimeoutException cex) {
			if (cex != null)
				DebugLog.loge("Error in http connection:\n" + cex);
		} catch (Exception e) {
			if (e != null)
			DebugLog.loge("Error:\n" + e);
		}
		
		return result;
	}
	
	public String getResponseByBasicNameValuePairWithSSL(String url, ArrayList<Data> data, ArrayList<Data> header) {
		String result = "";
		InputStream is = null;
		
		if (url.equals("")) {
			DebugLog.loge("\nURL EMPTY!!!");
			return "";
		} else {
			DebugLog.logn("\nConnecting to...\n" + url);
		}
		
		try {
			HttpPost httppost = new HttpPost(url);
//			httppost.addHeader("Charset", "UTF-8");
			if(header.size()>0){
				DebugLog.logn("\n" + "Add Header :");
				for (int i = 0; i < header.size(); i++) {
					DebugLog.logn("\n" + header.get(i).getKey() + " : " + header.get(i).getValue());
					httppost.addHeader(header.get(i).getKey(), header.get(i).getValue());
				}
			}
			
			if(data!=null){
				if(data.size()>0){
					List<BasicNameValuePair> param = new ArrayList<BasicNameValuePair>();
					DebugLog.logn("\n" + "Request data :");
					for (int i = 0; i < data.size(); i++) {
						DebugLog.logn("\n" + data.get(i).getKey() + " : " + data.get(i).getValue());
						param.add(new BasicNameValuePair(data.get(i).getKey(), data.get(i).getValue()));
					}
					
					UrlEncodedFormEntity encodeData = new UrlEncodedFormEntity(param, "UTF-8");
					httppost.setEntity(encodeData);
				}
			}

			HttpClient httpclient = getNewHttpClient();
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
			
			result = parseResponseData(is);
		} catch (SocketTimeoutException stoex) {
			if (stoex != null)
				DebugLog.loge("Error in socket connection:\n" + stoex);
		} catch (ConnectTimeoutException cex) {
			if (cex != null)
				DebugLog.loge("Error in http connection:\n" + cex);
		} catch (Exception e) {
			if (e != null)
			DebugLog.loge("Error:\n" + e);
		}
		
		return result;
	}
	
	public String getResponseByGETMethodWithSSL(String url, ArrayList<Data> data) {
		String result = "";
		InputStream is = null;
		
		if (url.equals("")) {
			DebugLog.loge("\nURL EMPTY!!!");
			return "";
		} else {
			DebugLog.logn("\nConnecting to...\n" + url);
		}
		
		List<BasicNameValuePair> param = new ArrayList<BasicNameValuePair>();
		DebugLog.logn("\n" + "Request data :");
		for (int i = 0; i < data.size(); i++) {
			DebugLog.logn("\n" + data.get(i).getKey() + " : " + data.get(i).getValue());
			param.add(new BasicNameValuePair(data.get(i).getKey(), data.get(i).getValue()));
		}
		
		StringBuilder newUrl = new StringBuilder();
		newUrl.append(url);
		for (int k = 0; k < param.size(); k++) {
			if (k == 0) {
				newUrl.append("?");
			} else {
				newUrl.append("&");
			}
			newUrl.append(param.get(k).getName());
			newUrl.append("=" + param.get(k).getValue());
		}
		
		try {
			HttpGet httpget = new HttpGet(newUrl.toString());
			httpget.addHeader("accept", "application/json");
			httpget.addHeader("Charset", "UTF-8");

			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 20000);
			HttpConnectionParams.setSoTimeout(httpParams, 20000);

			HttpClient httpclient = getNewHttpClient();
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();

			result = parseResponseData(is);
		} catch (SocketTimeoutException stoex) {
				DebugLog.loge("Error in socket connection:\n" + stoex);
		} catch (ConnectTimeoutException cex) {
				DebugLog.loge("Error in http connection:\n" + cex);
		} catch (Exception e) {
			DebugLog.loge("Error:\n" + e);
		}
		
		return result;
	}
	
	public String getResponseByGETMethodWithSSL(String url, ArrayList<Data> data, ArrayList<Data> header) {
		String result = "";
		InputStream is = null;
		
		if (url.equals("")) {
			DebugLog.loge("\nURL EMPTY!!!");
			return "";
		} else {
			DebugLog.logn("\nConnecting to...\n" + url);
		}
		
		List<BasicNameValuePair> param = new ArrayList<BasicNameValuePair>();
		DebugLog.logn("\n" + "Request data :");
		for (int i = 0; i < data.size(); i++) {
			DebugLog.logn("\n" + data.get(i).getKey() + " : " + data.get(i).getValue());
			param.add(new BasicNameValuePair(data.get(i).getKey(), data.get(i).getValue()));
		}
		
		StringBuilder newUrl = new StringBuilder();
		newUrl.append(url);
		for (int k = 0; k < param.size(); k++) {
			if (k == 0) {
				newUrl.append("?");
			} else {
				newUrl.append("&");
			}
			newUrl.append(param.get(k).getName());
			newUrl.append("=" + param.get(k).getValue());
		}
		
		try {
			HttpGet httpget = new HttpGet(newUrl.toString());
//			httpget.addHeader("accept", "application/json");
//			httpget.addHeader("Charset", "UTF-8");
			
			if(header.size()>0){
				DebugLog.logn("\n" + "Add Header :");
				for (int i = 0; i < header.size(); i++) {
					DebugLog.logn("\n" + header.get(i).getKey() + " : " + header.get(i).getValue());
					httpget.addHeader(header.get(i).getKey(), header.get(i).getValue());
				}
			}

			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 20000);
			HttpConnectionParams.setSoTimeout(httpParams, 20000);

			HttpClient httpclient = getNewHttpClient();
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();

			result = parseResponseData(is);
		} catch (SocketTimeoutException stoex) {
				DebugLog.loge("Error in socket connection:\n" + stoex);
		} catch (ConnectTimeoutException cex) {
				DebugLog.loge("Error in http connection:\n" + cex);
		} catch (Exception e) {
			DebugLog.loge("Error:\n" + e);
		}
		
		return result;
	}
	
	HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			HttpConnectionParams.setConnectionTimeout(params, 20000);
			HttpConnectionParams.setSoTimeout(params, 20000);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {

				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
				}

				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

			};

			sslContext.init(null, new TrustManager[]{tm}, null);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
		}
	}
	

	/*
	 * TODO**********************************************************************
	 */

	String getResponseData(HttpURLConnection urlc) throws IOException, JSONException {
		urlc.connect();
		int result = urlc.getResponseCode();
		if (result == HttpURLConnection.HTTP_OK) {
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(urlc.getInputStream(), "utf-8"));

			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			br.close();

			DebugLog.logn("\nGet response with result:\n" + sb.toString());
			return sb.toString();
		} else {
			DebugLog.loge("\nConnection return with result " + result);
			return "";
		}
	}
	
/*
	public String getResponseByBasicNameValuePair(String url, String keyParam, ArrayList<Data> data) {
		if (url.equals("")) {
			DebugLog.loge("\nURL EMPTY!!!");
			return "";
		} else {
			DebugLog.logn("\nConnecting to...\n" + url);
		}
		
		String jsonResult = "";
		HttpURLConnection urlc = null;

		try {
			urlc = createConnectionWithURL(url);
			if (urlc == null) {
				DebugLog.loge("\nGet response error when connection...");
				return "";
			}

			if(data!=null){
				JSONObject jsonParam = new JSONObject();
				if (data.size() > 0) {
					DebugLog.logn("\n" + "Request data :");
					for (int i = 0; i < data.size(); i++) {
						DebugLog.logn("\n" + data.get(i).getKey() + " : " + data.get(i).getValue());
						jsonParam.put(data.get(i).getKey(), data.get(i).getValue());
					}
					
					BasicNameValuePair param = new BasicNameValuePair(keyParam, jsonParam.toString());
					
					OutputStreamWriter out = new OutputStreamWriter(urlc.getOutputStream());
					out.write(param.toString());
					out.close();
				}
			}

			jsonResult = getResponseData(urlc);

			return jsonResult;
		} catch (Exception ex) {
				DebugLog.loge("\nGet response with exception result:\n" + ex);
		} finally {
			if (urlc != null) urlc.disconnect();
		}
		return "";
	}
*/
	public String getResponseByBasicNameValuePair(String url, ArrayList<Data> data) {
		if (url.equals("")) {
			DebugLog.loge("\nURL EMPTY!!!");
			return "";
		} else {
			DebugLog.logn("\nConnecting to...\n" + url);
		}

		HttpURLConnection urlc = null;

		try {
			urlc = createConnectionWithURL(url);
			if (urlc == null) {
				DebugLog.loge("\nGet response error when connection...");
				return "";
			}

			if (data.size() > 0) {
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				DebugLog.logn("\n" + "Request data :");
				for (int i = 0; i < data.size(); i++) {
					DebugLog.logn("\n" + data.get(i).getKey() + " : " + data.get(i).getValue());
					params.add(new BasicNameValuePair(data.get(i).getKey(), data.get(i).getValue()));
				}

				OutputStreamWriter out = new OutputStreamWriter(urlc.getOutputStream());
				out.write(getQuery(params));
				out.close();
			}

			String result = getResponseData(urlc);

			return result;
		} catch (Exception ex) {
				DebugLog.loge("\nGet response with exception result:\n" + ex);
		} finally {
			if (urlc != null)
				urlc.disconnect();
		}

		return "";
	}
	
	public String getResponseByBasicNameValuePair(String url, ArrayList<Data> data, ArrayList<Data> header) {
		if (url.equals("")) {
			DebugLog.loge("\nURL EMPTY!!!");
			return "";
		} else {
			DebugLog.logn("\nConnecting to...\n" + url);
		}

		HttpURLConnection urlc = null;

		try {
			urlc = createConnectionWithURL(url, header);
			if (urlc == null) {
				DebugLog.loge("\nGet response error when connection...");
				return "";
			}

			if (data.size() > 0) {
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				DebugLog.logn("\n" + "Request data :");
				for (int i = 0; i < data.size(); i++) {
					DebugLog.logn("\n" + data.get(i).getKey() + " : " + data.get(i).getValue());
					params.add(new BasicNameValuePair(data.get(i).getKey(), data.get(i).getValue()));
				}

				OutputStreamWriter out = new OutputStreamWriter(urlc.getOutputStream());
				out.write(getQuery(params));
				out.close();
			}

			String result = getResponseData(urlc);

			return result;
		} catch (Exception ex) {
				DebugLog.loge("\nGet response with exception result:\n" + ex);
		} finally {
			if (urlc != null)
				urlc.disconnect();
		}

		return "";
	}

	@SuppressWarnings("resource")
	public String getResponseByMultipartEntity(String url, ArrayList<Data> data) {
		if (url.equals("")) {
			DebugLog.loge("\nURL EMPTY!!!");
			return "";
		} else {
			DebugLog.logn("\nConnecting to...\n" + url);
		}
		if (data.size() <= 0) {
			DebugLog.logn("\nRESQUEST DATA NOT FOUND!!!");
		}
		String execute = "";
		DefaultHttpClient mHttpClient;
		try {
			HttpParams params = new BasicHttpParams();
			params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");
			mHttpClient = new DefaultHttpClient(params);

			HttpPost httppost = new HttpPost(url);
			httppost.addHeader("Charset", "UTF-8");

			if(data!=null){
				if(data.size()>0){
					MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
					DebugLog.logn("\n" + "Request data :");
					for (int i = 0; i < data.size(); i++) {
						DebugLog.logn("\n" + data.get(i).getKey() + " : " + data.get(i).getValue());
						multipartEntity.addPart(data.get(i).getKey(), new StringBody(data.get(i).getValue(), Charset.forName("UTF-8")));
					}
					httppost.setEntity(multipartEntity);
				}
			}

			execute = mHttpClient.execute(httppost, new ResponseHandler<String>() {
				@Override
				public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
					HttpEntity r_entity = response.getEntity();
					String responseString = EntityUtils.toString(r_entity);
					return responseString;
				}
			});

			return execute;
		} catch (ClientProtocolException e) {
				DebugLog.loge("\nGet response with exception result:\n" + e);
		} catch (IOException e) {
				DebugLog.loge("\nGet response with exception result:\n" + e);
		}
		return "";
	}
	
	@SuppressWarnings("resource")
	public String getResponseByMultipartEntity(String url, ArrayList<Data> data, ArrayList<Data> header) {
		if (url.equals("")) {
			DebugLog.loge("\nURL EMPTY!!!");
			return "";
		} else {
			DebugLog.logn("\nConnecting to...\n" + url);
		}
		if (data.size() <= 0) {
			DebugLog.logn("\nRESQUEST DATA NOT FOUND!!!");
		}
		String execute = "";
		DefaultHttpClient mHttpClient;
		try {
			HttpParams params = new BasicHttpParams();
			params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");
			mHttpClient = new DefaultHttpClient(params);

			HttpPost httppost = new HttpPost(url);
//			httppost.addHeader("Charset", "UTF-8");
			if(header.size()>0){
				DebugLog.logn("\n" + "Add Header :");
				for (int i = 0; i < header.size(); i++) {
					DebugLog.logn("\n" + header.get(i).getKey() + " : " + header.get(i).getValue());
					httppost.addHeader(header.get(i).getKey(), header.get(i).getValue());
				}
			}
			

			if(data!=null){
				if(data.size()>0){
					MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
					DebugLog.logn("\n" + "Request data :");
					for (int i = 0; i < data.size(); i++) {
						DebugLog.logn("\n" + data.get(i).getKey() + " : " + data.get(i).getValue());
						multipartEntity.addPart(data.get(i).getKey(), new StringBody(data.get(i).getValue(), Charset.forName("UTF-8")));
					}
					httppost.setEntity(multipartEntity);
				}
			}

			execute = mHttpClient.execute(httppost, new ResponseHandler<String>() {
				@Override
				public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
					HttpEntity r_entity = response.getEntity();
					String responseString = EntityUtils.toString(r_entity);
					return responseString;
				}
			});

			return execute;
		} catch (ClientProtocolException e) {
				DebugLog.loge("\nGet response with exception result:\n" + e);
		} catch (IOException e) {
				DebugLog.loge("\nGet response with exception result:\n" + e);
		}
		return "";
	}

	@SuppressWarnings("resource")
	public String getResponseByMultipartEntityWithFile(String url, ArrayList<Data> data, String keyOfFile, File file) {
		if (url.equals("")) {
			DebugLog.loge("\nURL EMPTY!!!");
			return "";
		} else {
			DebugLog.logn("\nConnecting to...\n" + url);
		}
		if (data.size() <= 0) {
			DebugLog.logn("\nRESQUEST DATA NOT FOUND!!!");
		}
		String execute = "";
		DefaultHttpClient mHttpClient;
		try {
			HttpParams params = new BasicHttpParams();
			params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");
			mHttpClient = new DefaultHttpClient(params);

			HttpPost httppost = new HttpPost(url);
			httppost.addHeader("Charset", "UTF-8");
			httppost.addHeader("content_type", "application/octet-stream");

			if(data!=null){
				if(data.size()>0){
					MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
					
					DebugLog.logn("\n" + "Request data :");
					for (int i = 0; i < data.size(); i++) {
						DebugLog.logn("\n" + data.get(i).getKey() + " : " + data.get(i).getValue());
						multipartEntity.addPart(data.get(i).getKey(), new StringBody(data.get(i).getValue(), Charset.forName("UTF-8")));
					}
					
					multipartEntity.addPart(keyOfFile, new FileBody(file));
					
					httppost.setEntity(multipartEntity);
				}
			}

			execute = mHttpClient.execute(httppost, new ResponseHandler<String>() {
				@Override
				public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
					HttpEntity r_entity = response.getEntity();
					String responseString = EntityUtils.toString(r_entity);
					return responseString;
				}
			});

			return execute;
		} catch (ClientProtocolException e) {
				DebugLog.loge("\nGet response with exception result:\n" + e);
		} catch (IOException e) {
				DebugLog.loge("\nGet response with exception result:\n" + e);
		}
		return "";
	}

	@SuppressWarnings("resource")
	public String getResponseByMultipartEntityWithFile(String url, ArrayList<Data> data, ArrayList<Data> header, String keyOfFile, File file) {
		if (url.equals("")) {
			DebugLog.loge("\nURL EMPTY!!!");
			return "";
		} else {
			DebugLog.logn("\nConnecting to...\n" + url);
		}
		if (data.size() <= 0) {
			DebugLog.logn("\nRESQUEST DATA NOT FOUND!!!");
		}
		String execute = "";
		DefaultHttpClient mHttpClient;
		try {
			HttpParams params = new BasicHttpParams();
			params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");
			mHttpClient = new DefaultHttpClient(params);

			HttpPost httppost = new HttpPost(url);
//			httppost.addHeader("Charset", "UTF-8");
//			httppost.addHeader("content_type", "application/octet-stream");
			if(header.size()>0){
				DebugLog.logn("\n" + "Add Header :");
				for (int i = 0; i < header.size(); i++) {
					DebugLog.logn("\n" + header.get(i).getKey() + " : " + header.get(i).getValue());
					httppost.addHeader(header.get(i).getKey(), header.get(i).getValue());
				}
			}

			if(data!=null){
				if(data.size()>0){
					MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
					
					DebugLog.logn("\n" + "Request data :");
					for (int i = 0; i < data.size(); i++) {
						DebugLog.logn("\n" + data.get(i).getKey() + " : " + data.get(i).getValue());
						multipartEntity.addPart(data.get(i).getKey(), new StringBody(data.get(i).getValue(), Charset.forName("UTF-8")));
					}
					
					multipartEntity.addPart(keyOfFile, new FileBody(file));
					
					httppost.setEntity(multipartEntity);
				}
			}

			execute = mHttpClient.execute(httppost, new ResponseHandler<String>() {
				@Override
				public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
					HttpEntity r_entity = response.getEntity();
					String responseString = EntityUtils.toString(r_entity);
					return responseString;
				}
			});

			return execute;
		} catch (ClientProtocolException e) {
				DebugLog.loge("\nGet response with exception result:\n" + e);
		} catch (IOException e) {
				DebugLog.loge("\nGet response with exception result:\n" + e);
		}
		return "";
	}

	@SuppressWarnings("resource")
	public String getResponseByMultipartEntityWithMutliFile(String url, ArrayList<Data> data, ArrayList<String> keyOfFile, ArrayList<String> url_files) {
		if (url.equals("")) {
			DebugLog.loge("\nURL EMPTY!!!");
			return "";
		} else {
			DebugLog.logn("\nConnecting to...\n" + url);
		}
		if (data.size() <= 0) {
			DebugLog.logn("\nRESQUEST DATA NOT FOUND!!!");
		}

		int listFileLength = 0;
		if (keyOfFile.size() != url_files.size()) {
			DebugLog.loge("\nList file NOT SAME!!!");
			if (keyOfFile.size() < url_files.size()) {
				listFileLength = keyOfFile.size();
			} else {
				listFileLength = keyOfFile.size();
			}
		}
		String execute = "";
		DefaultHttpClient mHttpClient;
		try {
			HttpParams params = new BasicHttpParams();
			params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");
			mHttpClient = new DefaultHttpClient(params);

			HttpPost httppost = new HttpPost(url);
			httppost.addHeader("Charset", "UTF-8");

			MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

			DebugLog.logn("\n" + "Request data :");
			for (int i = 0; i < data.size(); i++) {
				DebugLog.logn("\n" + data.get(i).getKey() + " : " + data.get(i).getValue());
				multipartEntity.addPart(data.get(i).getKey(), new StringBody(data.get(i).getValue(), Charset.forName("UTF-8")));
			}

			for (int i = 0; i < listFileLength; i++) {
				multipartEntity.addPart(keyOfFile.get(i).toString(), new FileBody((new File(url_files.get(i)))));
			}

			httppost.setEntity(multipartEntity);

			execute = mHttpClient.execute(httppost, new ResponseHandler<String>() {
				@Override
				public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
					HttpEntity r_entity = response.getEntity();
					String responseString = EntityUtils.toString(r_entity);
					return responseString;
				}
			});

			return execute;
		} catch (ClientProtocolException e) {
				DebugLog.loge("\nGet response with exception result:\n" + e);
		} catch (IOException e) {
				DebugLog.loge("\nGet response with exception result:\n" + e);
		}
		return "";
	}

	@SuppressWarnings("resource")
	public String getResponseByMultipartEntityWithMutliFile(String url, ArrayList<Data> data, ArrayList<Data> header, ArrayList<String> keyOfFile, ArrayList<String> url_files) {
		if (url.equals("")) {
			DebugLog.loge("\nURL EMPTY!!!");
			return "";
		} else {
			DebugLog.logn("\nConnecting to...\n" + url);
		}
		if (data.size() <= 0) {
			DebugLog.logn("\nRESQUEST DATA NOT FOUND!!!");
		}

		int listFileLength = 0;
		if (keyOfFile.size() != url_files.size()) {
			DebugLog.loge("\nList file NOT SAME!!!");
			if (keyOfFile.size() < url_files.size()) {
				listFileLength = keyOfFile.size();
			} else {
				listFileLength = keyOfFile.size();
			}
		}
		String execute = "";
		DefaultHttpClient mHttpClient;
		try {
			HttpParams params = new BasicHttpParams();
			params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");
			mHttpClient = new DefaultHttpClient(params);

			HttpPost httppost = new HttpPost(url);
//			httppost.addHeader("Charset", "UTF-8");
			if(header.size()>0){
				DebugLog.logn("\n" + "Add Header :");
				for (int i = 0; i < header.size(); i++) {
					DebugLog.logn("\n" + header.get(i).getKey() + " : " + header.get(i).getValue());
					httppost.addHeader(header.get(i).getKey(), header.get(i).getValue());
				}
			}

			MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

			DebugLog.logn("\n" + "Request data :");
			for (int i = 0; i < data.size(); i++) {
				DebugLog.logn("\n" + data.get(i).getKey() + " : " + data.get(i).getValue());
				multipartEntity.addPart(data.get(i).getKey(), new StringBody(data.get(i).getValue(), Charset.forName("UTF-8")));
			}

			for (int i = 0; i < listFileLength; i++) {
				multipartEntity.addPart(keyOfFile.get(i).toString(), new FileBody((new File(url_files.get(i)))));
			}

			httppost.setEntity(multipartEntity);

			execute = mHttpClient.execute(httppost, new ResponseHandler<String>() {
				@Override
				public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
					HttpEntity r_entity = response.getEntity();
					String responseString = EntityUtils.toString(r_entity);
					return responseString;
				}
			});

			return execute;
		} catch (ClientProtocolException e) {
				DebugLog.loge("\nGet response with exception result:\n" + e);
		} catch (IOException e) {
				DebugLog.loge("\nGet response with exception result:\n" + e);
		}
		return "";
	}

	/*
	 * TODO**********************************************************************
	 */

	JSONObject getResponseJSON(HttpURLConnection urlc) throws IOException, JSONException {
		urlc.connect();
		int result = urlc.getResponseCode();
		if (result == HttpURLConnection.HTTP_OK) {
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(urlc.getInputStream(), "utf-8"));

			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			br.close();

			DebugLog.logn("\nGet response with result:\n" + sb.toString());
			JSONObject jsonResult = new JSONObject(sb.toString());
			return jsonResult;
		} else {
			DebugLog.loge("\nConnection return with result " + result);
			return null;
		}
	}

	public JSONObject getResponseAPIByBasicNameValuePair(String url, String keyParam, ArrayList<Data> data) {
		if (url.equals("")) {
			DebugLog.loge("\nURL EMPTY!!!");
			return null;
		} else {
			DebugLog.logn("\nConnecting to...\n" + url);
		}
		if (data.size() <= 0) {
			DebugLog.logn("\nRESQUEST DATA NOT FOUND!!!");
		}
		JSONObject jsonResult = null;
		HttpURLConnection urlc = null;

		try {
			urlc = createConnectionWithURL(url);
			if (urlc == null) {
				DebugLog.loge("\nGet response error when connection...");
				return null;
			}

			JSONObject jsonParam = new JSONObject();

			DebugLog.logn("\n" + "Request data :");
			for (int i = 0; i < data.size(); i++) {
				DebugLog.logn("\n" + data.get(i).getKey() + " : " + data.get(i).getValue());
				jsonParam.put(data.get(i).getKey(), data.get(i).getValue());
			}

			BasicNameValuePair param = new BasicNameValuePair(keyParam, jsonParam.toString());

			OutputStreamWriter out = new OutputStreamWriter(urlc.getOutputStream());
			out.write(param.toString());
			out.close();

			jsonResult = getResponseJSON(urlc);
			if (jsonResult != null) {
				// DebugLog.logn("Get response with result: " +
				// jsonResult.toString());
			} else {
				DebugLog.loge("\nGet response with null result!");
			}

			return jsonResult;
		} catch (Exception ex) {
				DebugLog.loge("\nGet response with exception result:\n" + ex);
		} finally {
			if (urlc != null)
				urlc.disconnect();
		}
		return null;
	}

	public JSONObject getResponseAPIByBasicNameValuePair(String url, ArrayList<Data> data) {
		if (url.equals("")) {
			DebugLog.loge("\nURL EMPTY!!!");
			return null;
		} else {
			DebugLog.logn("\nConnecting to...\n" + url);
		}
		if (data.size() <= 0) {
			DebugLog.logn("\nRESQUEST DATA NOT FOUND!!!");
		}
		HttpURLConnection urlc = null;

		try {
			urlc = createConnectionWithURL(url);
			if (urlc == null) {
				DebugLog.loge("\nGet response error when connection...");
				return null;
			}

			List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

			DebugLog.logn("\n" + "Request data :");
			for (int i = 0; i < data.size(); i++) {
				DebugLog.logn("\n" + data.get(i).getKey() + " : " + data.get(i).getValue());
				params.add(new BasicNameValuePair(data.get(i).getKey(), data.get(i).getValue()));
			}

			OutputStreamWriter out = new OutputStreamWriter(urlc.getOutputStream());
			out.write(getQuery(params));
			out.close();

			JSONObject result = getResponseJSON(urlc);
			if (result != null) {
				// DebugLog.logn("Get response with result: " +
				// result.toString());
			} else {
				DebugLog.loge("\nGet response with null result!");
			}

			return result;
		} catch (Exception ex) {
			if (ex != null)
				DebugLog.loge("\nGet response with exception result:\n" + ex);
		} finally {
			if (urlc != null)
				urlc.disconnect();
		}

		return null;
	}

	@SuppressWarnings("resource")
	public JSONObject getResponseAPIByMultipartEntity(String url, ArrayList<Data> data) {
		if (url.equals("")) {
			DebugLog.loge("\nURL EMPTY!!!");
			return null;
		} else {
			DebugLog.logn("\nConnecting to...\n" + url);
		}
		if (data.size() <= 0) {
			DebugLog.logn("\nRESQUEST DATA NOT FOUND!!!");
		}
		String execute = "";
		DefaultHttpClient mHttpClient;
		try {
			HttpParams params = new BasicHttpParams();
			params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");
			mHttpClient = new DefaultHttpClient(params);

			HttpPost httppost = new HttpPost(url);
			httppost.addHeader("Charset", "UTF-8");

			MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

			DebugLog.logn("\n" + "Request data :");
			for (int i = 0; i < data.size(); i++) {
				DebugLog.logn("\n" + data.get(i).getKey() + " : " + data.get(i).getValue());
				multipartEntity.addPart(data.get(i).getKey(), new StringBody(data.get(i).getValue(), Charset.forName("UTF-8")));
			}

			httppost.setEntity(multipartEntity);

			execute = mHttpClient.execute(httppost, new ResponseHandler<String>() {
				@Override
				public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
					HttpEntity r_entity = response.getEntity();
					String responseString = EntityUtils.toString(r_entity);
					return responseString;
				}
			});

			JSONObject jsonResult = new JSONObject(execute);
			if (jsonResult != null) {
				DebugLog.logn("\nGet response result:\n" + jsonResult.toString());
			}

			return jsonResult;
		} catch (ClientProtocolException e) {
				DebugLog.loge("\nGet response with exception result:\n" + e);
		} catch (IOException e) {
				DebugLog.loge("\nGet response with exception result:\n" + e);
		} catch (JSONException e) {
				DebugLog.loge("\nGet response with exception result:\n" + e);
		}
		return null;
	}

	@SuppressWarnings("resource")
	public JSONObject getResponseAPIByMultipartEntityWithFile(String url, ArrayList<Data> data, String keyOfFile, File file) {
		if (url.equals("")) {
			DebugLog.loge("\nURL EMPTY!!!");
			return null;
		} else {
			DebugLog.logn("\nConnecting to...\n" + url);
		}
		if (data.size() <= 0) {
			DebugLog.logn("\nRESQUEST DATA NOT FOUND!!!");
		}
		String execute = "";
		DefaultHttpClient mHttpClient;
		try {
			HttpParams params = new BasicHttpParams();
			params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");
			mHttpClient = new DefaultHttpClient(params);

			HttpPost httppost = new HttpPost(url);
			httppost.addHeader("Charset", "UTF-8");

			MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

			DebugLog.logn("\n" + "Request data :");
			for (int i = 0; i < data.size(); i++) {
				DebugLog.logn("\n" + data.get(i).getKey() + " : " + data.get(i).getValue());
				multipartEntity.addPart(data.get(i).getKey(), new StringBody(data.get(i).getValue(), Charset.forName("UTF-8")));
			}

			multipartEntity.addPart(keyOfFile, new FileBody(file));

			httppost.setEntity(multipartEntity);

			execute = mHttpClient.execute(httppost, new ResponseHandler<String>() {
				@Override
				public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
					HttpEntity r_entity = response.getEntity();
					String responseString = EntityUtils.toString(r_entity);
					return responseString;
				}
			});

			JSONObject jsonResult = new JSONObject(execute);
			if (jsonResult != null) {
				DebugLog.logn("\nGet response result:\n" + jsonResult.toString());
			}

			return jsonResult;
		} catch (ClientProtocolException e) {
				DebugLog.loge("\nGet response with exception result:\n" + e);
		} catch (IOException e) {
				DebugLog.loge("\nGet response with exception result:\n" + e);
		} catch (JSONException e) {
				DebugLog.loge("\nGet response with exception result:\n" + e);
		}
		return null;
	}

	@SuppressWarnings("resource")
	public JSONObject getResponseAPIByMultipartEntityWithMutliFile(String url, ArrayList<Data> data, ArrayList<String> keyOfFile, File[] files) {
		if (url.equals("")) {
			DebugLog.loge("\nURL EMPTY!!!");
			return null;
		} else {
			DebugLog.logn("\nConnecting to...\n" + url);
		}
		if (data.size() <= 0) {
			DebugLog.logn("\nRESQUEST DATA NOT FOUND!!!");
		}

		int listFileLength = 0;
		if (keyOfFile.size() != files.length) {
			DebugLog.loge("\nList file NOT SAME!!!");
			if (keyOfFile.size() < files.length) {
				listFileLength = keyOfFile.size();
			} else {
				listFileLength = files.length;
			}
		}
		String execute = "";
		DefaultHttpClient mHttpClient;
		try {
			HttpParams params = new BasicHttpParams();
			params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");
			mHttpClient = new DefaultHttpClient(params);

			HttpPost httppost = new HttpPost(url);
			httppost.addHeader("Charset", "UTF-8");

			MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

			DebugLog.logn("\n" + "Request data :");
			for (int i = 0; i < data.size(); i++) {
				DebugLog.logn("\n" + data.get(i).getKey() + " : " + data.get(i).getValue());
				multipartEntity.addPart(data.get(i).getKey(), new StringBody(data.get(i).getValue(), Charset.forName("UTF-8")));
			}

			for (int i = 0; i < listFileLength; i++) {
				multipartEntity.addPart(keyOfFile.get(i).toString(), new FileBody(files[i]));
			}

			httppost.setEntity(multipartEntity);

			execute = mHttpClient.execute(httppost, new ResponseHandler<String>() {
				@Override
				public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
					HttpEntity r_entity = response.getEntity();
					String responseString = EntityUtils.toString(r_entity);
					return responseString;
				}
			});

			JSONObject jsonResult = new JSONObject(execute);
			if (jsonResult != null) {
				DebugLog.logn("\nGet response result:\n" + jsonResult.toString());
			}

			return jsonResult;
		} catch (ClientProtocolException e) {
				DebugLog.loge("\nGet response with exception result:\n" + e);
		} catch (IOException e) {
				DebugLog.loge("\nGet response with exception result:\n" + e);
		} catch (JSONException e) {
				DebugLog.loge("\nGet response with exception result:\n" + e);
		}
		return null;
	}
	
    // TODO
    public String getResponseFromRequestPostRaw(final Context context, String urlAPI, String objData) {
        HttpURLConnection connection = null;
        try {
            DebugLog.logn("urlAPI: " + urlAPI);
            DebugLog.logn("rawData: " + objData);
            URL url = new URL(urlAPI);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setReadTimeout(20000);
            connection.setConnectTimeout(20000);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(objData.toCharArray());
            out.close();

            try {
                int statusCode = connection.getResponseCode();
                String responseData = convertInputStreamToString(connection.getInputStream());
                if (HttpURLConnection.HTTP_OK == statusCode) {
                    return responseData;
                } else {
                    String errorData = convertInputStreamToString(connection.getErrorStream());
                    return errorData;
                }
            } catch (Exception e) {
                DebugLog.loge(e);
                String errorData = convertInputStreamToString(connection.getErrorStream());
                return errorData;
            }
        } catch (IOException e) {
            DebugLog.loge(e);
            return "Connect to server failed, please check your network connection!";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    private static String convertInputStreamToString(InputStream in) {
        StringBuilder sb = new StringBuilder("");
        if (in != null) {
            InputStreamReader is = null;
            BufferedReader br = null;
            String line;
            try {
                is = new InputStreamReader(in, "UTF-8");
                br = new BufferedReader(is);
                line = br.readLine();
                while (line != null) {
                    sb.append(line);
                    line = br.readLine();
                }
            } catch (IOException e) {
                DebugLog.loge(e);
                return "";
            } finally {
                try {
                    if (br != null) br.close();
                    if (is != null) is.close();
                    in.close();
                } catch (IOException e) {
                    DebugLog.loge(e);
                }
            }
        }
        return sb.toString();
    }

}
