package sparky.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.json.JSONTokener;

import sparky.SparkDevice;
import sparky.api.SparkService;

public class SparkServiceBaseImpl implements SparkService {

	private String password;
	private String username;
	public static final String baseSparkUrl = "https://spark:spak@api.spark.io";
	public static final String authUrl = baseSparkUrl + "/oauth/token";
	public static final String devicesUrl = baseSparkUrl + "/v1/devices";

	Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private HttpClient httpclient;
	private String currentToken;
	private JSONObject jsonToken;

	public SparkServiceBaseImpl(String username, String password)
			throws SparkIoException {
		this.username = username;
		this.password = password;
		httpclient = new DefaultHttpClient();
		currentToken = getToken(this.username, this.password);
		jsonToken = (JSONObject) new JSONTokener(currentToken).nextValue();
		logger.info("Current token is "+currentToken);
		

	}
	
	private HttpGet getAuthGet(String url){
		HttpGet authGet = new HttpGet(url);
		String token = jsonToken.getString("access_token");
		authGet.addHeader("Authorization", "Bearer "+token);
		return authGet;
	}
	
	private HttpPost getAuthPost(String url){
		HttpPost authGet = new HttpPost(url);
		String token = jsonToken.getString("access_token");
		authGet.addHeader("Authorization", "Bearer "+token);
		return authGet;
	}

	public List<SparkDevice> getDevices() throws SparkIoException {
		HttpGet get = getAuthGet(devicesUrl);
		try {
			HttpResponse resp = httpclient.execute(get);
			String data = streamToString(resp.getEntity().getContent());
			return SparkDevice.parseDevices(data);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new SparkIoException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new SparkIoException(e);
		}
	}

	public String getDeviceInfo(SparkDevice device) throws SparkIoException {
		HttpGet get = getAuthGet(devicesUrl+"/"+device.getId());
		return doGet(get);
	}

	private String doGet(HttpGet get) throws SparkIoException {
		HttpResponse resp;
		try {
			resp = httpclient.execute(get);
			String data = streamToString(resp.getEntity().getContent());
			return data;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new SparkIoException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new SparkIoException(e);
		}
	}
	

	public String subscribeEventOnAllDevices(String eventName) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getToken(String username, String password)
			throws SparkIoException {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		HttpPost httppost = new HttpPost(authUrl);
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("password", password));
		nameValuePairs.add(new BasicNameValuePair("grant_type", "password"));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			return streamToString(response.getEntity().getContent());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SparkIoException(e);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SparkIoException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SparkIoException(e);
		}

	}

	private String streamToString(InputStream in) throws IOException {
		InputStreamReader is = new InputStreamReader(in);
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(is);
		String read = br.readLine();

		while (read != null) {
			// System.out.println(read);
			sb.append(read);
			read = br.readLine();

		}

		return sb.toString();
	}

	public String getCurrentToken() {
		return currentToken;
	}

	public String getVariable(SparkDevice device, String varName) throws SparkIoException {
		HttpGet get = getAuthGet(devicesUrl+"/" +device.getId()+"/"+varName);
		return doGet(get);
	}

	public String callFunction(SparkDevice device, String functionName,
			Object... args) throws SparkIoException {
		String sendArgs = "";
		for(int i=0;i<args.length;i++){
			if(i==0)
				sendArgs=args[i].toString();
			else
				sendArgs=sendArgs+","+args[i].toString();
		}
		
		HttpPost post = getAuthPost(devicesUrl+"/"+device.getId()+"/"+functionName);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("args", sendArgs));
		try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse data = httpclient.execute(post);
			return streamToString(data.getEntity().getContent());
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new SparkIoException(e);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new SparkIoException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new SparkIoException(e);
		}
	}

	public InputStream getEventStream(SparkDevice device, String event) throws SparkIoException {
		try {
			URL url = new URL(devicesUrl+"/"+device.getId()+"/events/"+event);
			
			URLConnection conne = url.openConnection();
			String token = jsonToken.getString("access_token");
			conne.setRequestProperty("Authorization", "Bearer "+token);
			return conne.getInputStream();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SparkIoException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SparkIoException(e);
		}
		
	}

}
