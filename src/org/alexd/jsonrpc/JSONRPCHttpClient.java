package org.alexd.jsonrpc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

/**
 * Implementation of JSON-RPC over HTTP/POST
 */
public class JSONRPCHttpClient extends JSONRPCClient
{

	/*
	 * HttpClient to issue the HTTP/POST request
	 */
	private OkHttpClient httpClient;
	/*
	 * Service URI
	 */
	private String serviceUri;
	
	// HTTP 1.0
	private static final ProtocolVersion PROTOCOL_VERSION = new ProtocolVersion("HTTP", 1, 0);
	
 	/**
	 * Construct a JsonRPCClient with the given httpClient and service uri
	 *
     * @param client
     *            httpClient to use
	 * @param uri
	 *            uri of the service
	 */
	public JSONRPCHttpClient(OkHttpClient okHttpClient, String uri){
		httpClient = okHttpClient;
		serviceUri = uri;
	}
	
	/**
	 * Construct a JsonRPCClient with the given service uri
	 * 
	 * @param uri
	 *            uri of the service
	 */
	public JSONRPCHttpClient(String uri)
	{
		this(new OkHttpClient(), uri);
	}
	public static final  MediaType JSON
    = MediaType.parse("application/json; charset=utf-8");
	protected JSONObject doJSONRequest(JSONObject jsonRequest) throws JSONRPCException
	{
		if(_debug){
			Log.i(JSONRPCHttpClient.class.toString(), "Request: " + jsonRequest.toString());
		}
				
		RequestBody body = RequestBody.create(JSON, jsonRequest.toString());
        Request request = new Request.Builder()
          .url(serviceUri)
          .post(body)
          .build();
        
		try
		{
			// Execute the request and try to decode the JSON Response
			long t = System.currentTimeMillis();
			Response response = httpClient.newCall(request).execute();
			
			t = System.currentTimeMillis() - t;
			String responseString = response.body().string();
			
			responseString = responseString.trim();
			
			if(_debug){
				Log.i(JSONRPCHttpClient.class.toString(), "Response: " + responseString);
			}
			JSONObject jsonResponse = new JSONObject(responseString);
			// Check for remote errors
			if (jsonResponse.has("error"))
			{
				Object jsonError = jsonResponse.get("error");
				if (!jsonError.equals(null))
					throw new JSONRPCException(jsonResponse.get("error"));
				return jsonResponse; // JSON-RPC 1.0
			}
			else
			{
				return jsonResponse; // JSON-RPC 2.0
			}
		}
		// Underlying errors are wrapped into a JSONRPCException instance
		catch (ClientProtocolException e)
		{
			throw new JSONRPCException("HTTP error", e);
		}
		catch (IOException e)
		{
			throw new JSONRPCException("IO error", e);
		}
		catch (JSONException e)
		{
			throw new JSONRPCException("Invalid JSON response", e);
		}
	}
}
