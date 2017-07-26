//package com.nmd.utility;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.Socket;
//import java.net.SocketTimeoutException;
//import java.net.UnknownHostException;
//import java.nio.charset.Charset;
//import java.security.KeyManagementException;
//import java.security.KeyStore;
//import java.security.KeyStoreException;
//import java.security.NoSuchAlgorithmException;
//import java.security.UnrecoverableKeyException;
//import java.security.cert.X509Certificate;
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.TrustManager;
//import javax.net.ssl.X509TrustManager;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpVersion;
//import org.apache.http.ParseException;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.CookieStore;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.ResponseHandler;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.client.protocol.ClientContext;
//import org.apache.http.conn.ClientConnectionManager;
//import org.apache.http.conn.ConnectTimeoutException;
//import org.apache.http.conn.scheme.PlainSocketFactory;
//import org.apache.http.conn.scheme.Scheme;
//import org.apache.http.conn.scheme.SchemeRegistry;
//import org.apache.http.conn.ssl.SSLSocketFactory;
//import org.apache.http.cookie.Cookie;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.entity.mime.HttpMultipartMode;
//import org.apache.http.entity.mime.MultipartEntity;
//import org.apache.http.entity.mime.content.FileBody;
//import org.apache.http.entity.mime.content.StringBody;
//import org.apache.http.impl.client.BasicCookieStore;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.params.BasicHttpParams;
//import org.apache.http.params.CoreProtocolPNames;
//import org.apache.http.params.HttpConnectionParams;
//import org.apache.http.params.HttpParams;
//import org.apache.http.params.HttpProtocolParams;
//import org.apache.http.protocol.BasicHttpContext;
//import org.apache.http.protocol.HTTP;
//import org.apache.http.protocol.HttpContext;
//import org.apache.http.util.EntityUtils;
//import org.json.JSONObject;
//
//import com.nmd.utility.other.Data;
//
//
//@SuppressWarnings("deprecation")
//public class CustomNetworkService {
//	String parseResponseData(InputStream is) {
//		if (is == null)
//			return "";
//		try {
//			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
//			StringBuilder sb = new StringBuilder();
//			sb.append(reader.readLine() + "\n");
//
//			String line = "0";
//			while ((line = reader.readLine()) != null) {
//				sb.append(line + "\n");
//			}
//			is.close();
//			return sb.toString();
//		} catch (Exception e) {
//			if (e != null)
//				DebugLog.loge("Error:\n" + e.toString());
//			return "";
//		}
//	}
//
//	public String getResponseByGETMethod(String url) {
//		String result = "";
//		InputStream is = null;
//
//		if (url.equals("")) {
//			DebugLog.loge("\nURL EMPTY!!!");
//			return "";
//		} else {
//			DebugLog.logn("\nConnecting to...\n" + url);
//		}
//
//		try {
//			HttpGet httpget = new HttpGet(url);
//			httpget.addHeader("accept", "application/json");
//			httpget.addHeader("Charset", "UTF-8");
//
//			HttpParams httpParams = new BasicHttpParams();
//			HttpConnectionParams.setConnectionTimeout(httpParams, 20000);
//			HttpConnectionParams.setSoTimeout(httpParams, 20000);
//
//			@SuppressWarnings("resource")
//			HttpClient httpclient = new DefaultHttpClient(httpParams);
//			HttpResponse response = httpclient.execute(httpget);
//
//			// UrlEncodedFormEntity encodeData = new UrlEncodedFormEntity(new
//			// ArrayList<NameValuePair>(), "UTF-8");
//			// response.setEntity(encodeData);
//
//			HttpEntity entity = response.getEntity();
//			is = entity.getContent();
//
//			result = parseResponseData(is);
//		} catch (SocketTimeoutException stoex) {
//			if (stoex != null)
//				DebugLog.loge("Error in socket connection:\n" + stoex.getMessage());
//		} catch (ConnectTimeoutException cex) {
//			if (cex != null)
//				DebugLog.loge("Error in http connection:\n" + cex.getMessage());
//		} catch (Exception e) {
//			if (e != null)
//				DebugLog.loge("Error:\n" + e.toString());
//		}
//
//		return result;
//	}
//
//	public String getResponseByPOSTMethod(String url, ArrayList<Data> data) {
//		if (url.equals("")) {
//			DebugLog.loge("\nURL EMPTY!!!");
//			return "";
//		} else {
//			DebugLog.logn("\nConnecting to...\n" + url);
//		}
//
//		if (data.size() <= 0) {
//			DebugLog.logn("\nRESQUEST DATA NOT FOUND!!!");
//			return getResponseByGETMethod(url);
//		}
//		@SuppressWarnings("resource")
//		DefaultHttpClient httpclient = new DefaultHttpClient();
//		HttpPost httpost = new HttpPost(url);
//
//		List<BasicNameValuePair> param = new ArrayList<BasicNameValuePair>();
//		DebugLog.logn("\n" + "Request data :");
//		for (int i = 0; i < data.size(); i++) {
//			DebugLog.logn("\n" + data.get(i).getKey() + " : " + data.get(i).getValue());
//			param.add(new BasicNameValuePair(data.get(i).getKey(), data.get(i).getValue()));
//		}
//
//		String serverResponse = "";
//		try {
//			httpost.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
//
//			HttpResponse response = httpclient.execute(httpost);
//			HttpEntity entity = response.getEntity();
//
//			response = httpclient.execute(httpost);
//			entity = response.getEntity();
//
//			List<Cookie> cookies = httpclient.getCookieStore().getCookies();
//
//			if (cookies.isEmpty()) {
//				DebugLog.loge("None");
//			} else {
//				for (int i = 0; i < cookies.size(); i++) {
//					if (cookies.get(i).toString().contains("remember_token")) {
//						ArrayList<String> list = UtilLibs.splitComme2(cookies.get(i).toString(), "\\]\\[");
//						for (int j = 0; j < list.size(); j++) {
//							if (list.get(j).toString().contains("value")) {
//								// String value = list.get(j).toString();
//							}
//						}
//					}
//				}
//			}
//
//			httpclient.getConnectionManager().shutdown();
//			serverResponse = EntityUtils.toString(entity);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return serverResponse;
//	}
//
//	public String getResponseByMultipartEntityWithFile(String url, ArrayList<Data> data, String keyOfFile, File file) {
//		if (url.equals("")) {
//			DebugLog.loge("\nURL EMPTY!!!");
//			return null;
//		} else {
//			DebugLog.logn("\nConnecting to...\n" + url);
//		}
//		if (data.size() <= 0) {
//			DebugLog.logn("\nRESQUEST DATA NOT FOUND!!!");
//		}
//		String execute = "";
//		HttpClient mHttpClient;
//		try {
//			HttpParams params = new BasicHttpParams();
//			params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
//			params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");
//			mHttpClient = getNewHttpClient();
//
//			HttpPost httppost = new HttpPost(url);
//			httppost.addHeader("Charset", "UTF-8");
//
//			if (data != null) {
//				if (data.size() > 0) {
//					MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
//
//					DebugLog.logn("\n" + "Request data :");
//					for (int i = 1; i < data.size(); i++) {
//						DebugLog.logn("\n" + data.get(i).getKey() + " : " + data.get(i).getValue());
//						multipartEntity.addPart(data.get(i).getKey(), new StringBody(data.get(i).getValue(), Charset.forName("UTF-8")));
//					}
//
//					multipartEntity.addPart(keyOfFile, new FileBody(file, "image/" + data.get(0).getValue()));
//
//					httppost.setEntity(multipartEntity);
//				}
//			}
//
//			execute = mHttpClient.execute(httppost, new ResponseHandler<String>() {
//				@Override
//				public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
//					HttpEntity r_entity = response.getEntity();
//					String responseString = EntityUtils.toString(r_entity);
//					return responseString;
//				}
//			});
//
//			return execute;
//		} catch (ClientProtocolException e) {
//			if (e != null)
//				DebugLog.loge("\nGet response with exception result:\n" + e.getMessage().toString());
//		} catch (IOException e) {
//			if (e != null)
//				DebugLog.loge("\nGet response with exception result:\n" + e.getMessage().toString());
//		}
//		return null;
//	}
//
//	public String getResponseByHttpsPOSTMethod(String url, ArrayList<Data> data) {
//		String result = "";
//		InputStream is = null;
//
//		if (url.equals("")) {
//			DebugLog.loge("\nURL EMPTY!!!");
//			return "";
//		} else {
//			DebugLog.logn("\nConnecting to...\n" + url);
//		}
//
//		if (data.size() <= 0) {
//			DebugLog.logn("\nRESQUEST DATA NOT FOUND!!!");
//		}
//
//		try {
//			HttpPost httppost = new HttpPost(url);
//
//			if (data.size() > 0) {
//				List<BasicNameValuePair> param = new ArrayList<BasicNameValuePair>();
//				DebugLog.logn("\n" + "Request data :");
//				for (int i = 0; i < data.size(); i++) {
//					DebugLog.logn("\n" + data.get(i).getKey() + " : " + data.get(i).getValue());
//					param.add(new BasicNameValuePair(data.get(i).getKey(), data.get(i).getValue()));
//				}
//
//				UrlEncodedFormEntity encodeData = new UrlEncodedFormEntity(param);
//				httppost.setEntity(encodeData);
//			}
//
//			CookieStore cookieStore = new BasicCookieStore();
//			HttpContext httpContext = new BasicHttpContext();
//			httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
//
//			HttpClient httpclient = getNewHttpClient();
//			HttpResponse response = httpclient.execute(httppost);
//			HttpEntity entity = response.getEntity();
//			is = entity.getContent();
//
//			List<Cookie> cookies = cookieStore.getCookies();
//			String remember_token = "";
//
//			if (cookies.isEmpty()) {
//				DebugLog.loge("None");
//			} else {
//				for (int i = 0; i < cookies.size(); i++) {
//					// DebugLog.loge("cookies " + i + ":\n" +
//					// cookies.get(i).toString());
//					if (cookies.get(i).toString().contains("remember_token")) {
//						ArrayList<String> list = UtilLibs.splitComme2(cookies.get(i).toString(), "\\]\\[");
//						for (int j = 0; j < list.size(); j++) {
//							// DebugLog.loge("\n"+ list.get(j).toString());
//							if (list.get(j).toString().contains("value")) {
//								String value = list.get(j).toString();
//								remember_token = value.replaceAll("value:", " ");
//							}
//						}
//					}
//				}
//			}
//
//			String result0 = parseResponseData(is);
//			JSONObject js = new JSONObject();
//			js.put("result", result0);
//			js.put("remember_token", remember_token.trim());
//			result = js.toString();
//			// result = parseResponseData(is);
//
//		} catch (SocketTimeoutException stoex) {
//			if (stoex != null)
//				DebugLog.loge("Error in socket connection:\n" + stoex.getMessage());
//		} catch (ConnectTimeoutException cex) {
//			if (cex != null)
//				DebugLog.loge("Error in http connection:\n" + cex.getMessage());
//		} catch (Exception e) {
//			if (e != null)
//				DebugLog.loge("Error:\n" + e.toString());
//		}
//
//		return result;
//	}
//
//	class MySSLSocketFactory extends SSLSocketFactory {
//		SSLContext sslContext = SSLContext.getInstance("TLS");
//
//		public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
//			super(truststore);
//
//			TrustManager tm = new X509TrustManager() {
//
//				@Override
//				public void checkClientTrusted(X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
//				}
//
//				@Override
//				public void checkServerTrusted(X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
//				}
//
//				@Override
//				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//					return null;
//				}
//
//			};
//
//			sslContext.init(null, new TrustManager[] { tm }, null);
//		}
//
//		@Override
//		public Socket createSocket() throws IOException {
//			return sslContext.getSocketFactory().createSocket();
//		}
//
//		@Override
//		public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
//			return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
//		}
//	}
//
//	public String getResponseByBasicNameValuePairWithSSL(String url, ArrayList<Data> data) {
//		String result = "";
//		InputStream is = null;
//
//		if (url.equals("")) {
//			DebugLog.loge("\nURL EMPTY!!!");
//			return "";
//		} else {
//			DebugLog.logn("\nConnecting to...\n" + url);
//		}
//
//		try {
//			HttpPost httppost = new HttpPost(url);
//			httppost.addHeader("Charset", "UTF-8");
//
//			if (data != null) {
//				if (data.size() > 0) {
//					List<BasicNameValuePair> param = new ArrayList<BasicNameValuePair>();
//					DebugLog.logn("\n" + "Request data :");
//					for (int i = 0; i < data.size(); i++) {
//						DebugLog.logn("\n" + data.get(i).getKey() + " : " + data.get(i).getValue());
//						param.add(new BasicNameValuePair(data.get(i).getKey(), data.get(i).getValue()));
//					}
//
//					UrlEncodedFormEntity encodeData = new UrlEncodedFormEntity(param, "UTF-8");
//					httppost.setEntity(encodeData);
//				}
//			}
//
//			HttpClient httpclient = getNewHttpClient();
//			HttpResponse response = httpclient.execute(httppost);
//			HttpEntity entity = response.getEntity();
//			is = entity.getContent();
//
//			result = parseResponseData(is);
//		} catch (SocketTimeoutException stoex) {
//			if (stoex != null)
//				DebugLog.loge("Error in socket connection:\n" + stoex.getMessage());
//		} catch (ConnectTimeoutException cex) {
//			if (cex != null)
//				DebugLog.loge("Error in http connection:\n" + cex.getMessage());
//		} catch (Exception e) {
//			if (e != null)
//				DebugLog.loge("Error:\n" + e.toString());
//		}
//
//		return result;
//	}
//
//	public String getResponseByGETMethodWithSSL(String url, ArrayList<Data> data) {
//		String result = "";
//		InputStream is = null;
//
//		if (url.equals("")) {
//			DebugLog.loge("\nURL EMPTY!!!");
//			return "";
//		} else {
//			DebugLog.logn("\nConnecting to...\n" + url);
//		}
//
//		List<BasicNameValuePair> param = new ArrayList<BasicNameValuePair>();
//		DebugLog.logn("\n" + "Request data :");
//		for (int i = 0; i < data.size(); i++) {
//			DebugLog.logn("\n" + data.get(i).getKey() + " : " + data.get(i).getValue());
//			param.add(new BasicNameValuePair(data.get(i).getKey(), data.get(i).getValue()));
//		}
//
//		StringBuilder newUrl = new StringBuilder();
//		newUrl.append(url);
//		for (int k = 0; k < param.size(); k++) {
//			if (k == 0) {
//				newUrl.append("?");
//			} else {
//				newUrl.append("&");
//			}
//			newUrl.append(param.get(k).getName());
//			newUrl.append("=" + param.get(k).getValue());
//		}
//
//		try {
//			HttpGet httpget = new HttpGet(newUrl.toString());
//			httpget.addHeader("accept", "application/json");
//			httpget.addHeader("Charset", "UTF-8");
//
//			HttpParams httpParams = new BasicHttpParams();
//			HttpConnectionParams.setConnectionTimeout(httpParams, 20000);
//			HttpConnectionParams.setSoTimeout(httpParams, 20000);
//
//			HttpClient httpclient = getNewHttpClient();
//			HttpResponse response = httpclient.execute(httpget);
//			HttpEntity entity = response.getEntity();
//			is = entity.getContent();
//
//			result = parseResponseData(is);
//		} catch (SocketTimeoutException stoex) {
//			if (stoex != null)
//				DebugLog.loge("Error in socket connection:\n" + stoex.getMessage());
//		} catch (ConnectTimeoutException cex) {
//			if (cex != null)
//				DebugLog.loge("Error in http connection:\n" + cex.getMessage());
//		} catch (Exception e) {
//			if (e != null)
//				DebugLog.loge("Error:\n" + e.toString());
//		}
//
//		return result;
//	}
//
//	HttpClient getNewHttpClient() {
//		try {
//			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//			trustStore.load(null, null);
//
//			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
//			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//
//			HttpParams params = new BasicHttpParams();
//			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
//			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
//			HttpConnectionParams.setConnectionTimeout(params, 20000);
//			HttpConnectionParams.setSoTimeout(params, 20000);
//
//			SchemeRegistry registry = new SchemeRegistry();
//			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//			registry.register(new Scheme("https", sf, 443));
//
//			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
//
//			return new DefaultHttpClient(ccm, params);
//		} catch (Exception e) {
//			return new DefaultHttpClient();
//		}
//	}
//
//	public static String postSimple(String url, String json) {
//		InputStream inputStream = null;
//		String result = "";
//		try {
//			@SuppressWarnings("resource")
//			HttpClient httpclient = new DefaultHttpClient();
//			HttpPost httpPost = new HttpPost(url);
//			StringEntity se = new StringEntity(json);
//			httpPost.setEntity(se);
//			httpPost.setHeader("Accept", "application/json");
//			httpPost.setHeader("Content-type", "application/json");
//			HttpResponse httpResponse = httpclient.execute(httpPost);
//			inputStream = httpResponse.getEntity().getContent();
//
//			if (inputStream != null)
//				result = convertInputStreamToString(inputStream);
//			else
//				result = "Did not work!";
//
//		} catch (Exception e) {
//		}
//		return result;
//	}
//
//	static String convertInputStreamToString(InputStream inputStream) throws IOException {
//		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//		String line = "";
//		String result = "";
//		while ((line = bufferedReader.readLine()) != null)
//			result += line;
//
//		inputStream.close();
//		return result;
//
//	}
//}
