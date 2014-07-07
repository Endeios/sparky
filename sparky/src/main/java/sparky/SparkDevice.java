package sparky;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class SparkDevice {
	private String id;
	private String name;
	private String lastApp;
	private Date lastHeard;
	private boolean connected;
	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

	public SparkDevice(String jsonObjectString) {
		JSONObject jsonToken = (JSONObject) new JSONTokener(jsonObjectString)
				.nextValue();
		initByToken(jsonToken);
	}

	public SparkDevice(JSONObject jsonObject) {
		initByToken(jsonObject);
	}

	public static List<SparkDevice> parseDevices(String jsonArrayString) {
		List<SparkDevice> ret = new ArrayList<SparkDevice>();
		JSONTokener tokener = new JSONTokener(jsonArrayString);
		JSONArray array = (JSONArray) tokener.nextValue();
		int l = array.length();
		for (int i = 0; i < l; i++) {
			ret.add(new SparkDevice(array.getJSONObject(i)));
		}
		return ret;
	}

	private void initByToken(JSONObject jsonToken) {
		id = jsonToken.getString("id");
		name = jsonToken.getString("name");
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		try {
			lastApp = jsonToken.getString("last_app");
		} catch (JSONException exception) {
			lastApp = null;
		}
		try {
			String lastHeardString = jsonToken.getString("last_heard");
			lastHeard = dateFormat.parse(lastHeardString);
		} catch (JSONException exception) {
			exception.printStackTrace();
		} catch (ParseException e) {
			lastHeard = null;
			e.printStackTrace();
		}
		connected = jsonToken.getBoolean("connected");
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public Date getLastHeard() {
		return lastHeard;
	}

	public void setLastHeard(Date lastHeard) {
		this.lastHeard = lastHeard;
	}

	public String getLastApp() {
		return lastApp;
	}

	public void setLastApp(String lastApp) {
		this.lastApp = lastApp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "SparkDevice [id=" + id + ", name=" + name + ", lastApp="
				+ lastApp + ", lastHeard=" + lastHeard + ", connected="
				+ connected + "]";
	}

}
