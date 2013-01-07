package com.localhostr.android;


import static com.localhostr.android.LocalhostrApp.DEBUG;
import static com.localhostr.android.LocalhostrApp.TAG;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;


public class Utils {

	static {
		//Initialize for our connections
		SSLContext context = null;

		try {
			context = SSLContext.getInstance("TLS");
			context.init(null, new TrustManager[] {new AcceptAllSSL()}, new SecureRandom());
		} catch (Exception e) {

			if (DEBUG) {
				Log.e(TAG, "", e);
			}
		}		

		HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
	}

	private Utils() {
		//Only static access to this class
	}

	/**
	 * Create a JSON string from an Exception.<br> <br>
	 * A sample string will looking like this:
	 * 	<pre>
	 * 	[ {
	 * 		"exception": {
	 * 			"type": "java.io.IOException",
	 * 			"message": "Host 'foo' was not found"
	 * 		}
	 * 	} ]
	 * 	</pre>
	 * 
	 * Note: The result will only be wrapped with '[' and ']' if 
	 * <code>asArray</code> is set.
	 * 
	 * @param e the exception to create the JSON string from
	 * @param asArray specifies if the resulting string should be a {@link JSONArray}
	 * @return a {@link JSONArray} if <code>asArray</code> is set, {@link JSONObject} 
	 * otherwise
	 */
	public static String createJSONStringFromException(Exception e, boolean asArray) {
		StringBuilder sb = new StringBuilder();
		if (asArray) sb.append("[");

		sb.append("{ ")
		.append("\"exception\": { ")
		.append("\"type\": \"").append(e.getClass()).append("\",")
		.append("\"message\": \"")
		.append(e.getMessage().replaceAll("\"", "'"))
		.append("\"")
		.append("}")
		.append("}");

		if (asArray) sb.append("]");

		return sb.toString();
	}

	/**
	 * Returns a sample response from Localhostr containing information
	 * about a single file
	 * @return
	 * @throws IOException an exception will never be thrown, however,
	 * this has been added to the method declaration as this method is
	 * typically called in the place of {@link #makeApiRequest(String, String)}
	 * which <b>does</b> throw an exception.
	 */
	public static String getSampleFilesList() throws IOException {
		return "[ " + 
		"{"+
		"\"added\": \"2012-12-25T16:34:11Z\"," +
		"\"name\": \"localhostr.png\"," +
		"\"downloads\": 8," +
		"\"direct\": {" +
		"\"150x\": \"http://localhostr.com/file/150/c0rx8FLYwjwx/localhostr.png\"," +
		"\"930x\": \"http://localhostr.com/file/930/c0rx8FLYwjwx/localhostr.png\"" +
		"}," +
		"\"href\": \"http://lh.rs/c0rx8FLYwjwx\"," +
		"\"type\": \"image\"," +  
		"\"id\": \"c0rx8FLYwjwx\"," + 
		"\"size\": 112064" + 
		"}]";
	}

	/**
	 * Convenience method to retrieve the current database instance. This is 
	 * preferred to creating a new {@link DB} instance so this resource
	 * can be shared.
	 * @param ctx
	 * @return
	 */
	public static DB getDB(Context ctx) {
		return ((LocalhostrApp) ctx.getApplicationContext()).getDB();
	}

	/**
	 * Make a request to the Localhostr API service. This should <b>not</b>
	 * be called on the application's main thread as it involves a blocking
	 * request, and doing so on the main thread will cause the app
	 * to freeze.
	 * 
	 * @param address The address to make the request to.
	 * @param authString the Base64 encoded version of the user's email and 
	 * password to negotiate Basic authentication.
	 * @return The API service response
	 * @throws IOException if an error occurs while performing the request
	 * @see {@link Constants}
	 * @see DB#makeAuthString()
	 * @see #createJSONStringFromException(Exception, boolean)
	 */
	public static String makeApiRequest(String address, String authString) 
	throws IOException {

		HttpsURLConnection connection = null;
		URL url;
		String result = null;

		try {
			url = new URL(address);

			connection = (HttpsURLConnection) url.openConnection();
			connection.addRequestProperty("Authorization", "Basic " + authString);
			connection.addRequestProperty("Accept", Constants.ACCEPT_HEADER);
			connection.connect();
			int response = connection.getResponseCode();
			BufferedInputStream is = null;

			if (response == HttpsURLConnection.HTTP_OK) {
				is = new BufferedInputStream(connection.getInputStream());
			}

			else {
				is = new BufferedInputStream(connection.getErrorStream());
			}

			int c = -1;
			StringBuilder sb = new StringBuilder();

			while ((c = is.read()) != -1) {
				sb.append((char) c);
			}

			result = sb.toString();

		} catch (IOException e) {
			throw e;

		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

		return result;
	}

	/**
	 * A {@link TrustManager} which accepts all certificates.
	 * @author Al
	 *
	 */
	private static final class AcceptAllSSL implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType)
		throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType)
		throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}
}
