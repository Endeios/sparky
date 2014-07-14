package sparky;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Test;

import sparky.api.SparkService;
import sparky.impl.SparkIoException;
import sparky.impl.SparkServiceBaseImpl;

public class TestService {

	@Test
	public void test() throws SparkIoException, InterruptedException, IOException {
		SparkService service = new SparkServiceBaseImpl("endeios@gmail.com", "Passwd");
		List<SparkDevice> myDevices = service.getDevices();
		for (SparkDevice sparkDevice : myDevices) {
			System.out.println("\t"+sparkDevice);
			String info = service.getDeviceInfo(sparkDevice);
			JSONObject data = (JSONObject) new JSONTokener(info).nextValue();
			JSONObject vars = data.getJSONObject("variables");
			String[] varNames = JSONObject.getNames(vars);
			for (String varName : varNames) {
				String varVal = service.getVariable(sparkDevice, varName);
				System.out.println(varName+"="+varVal);
			}
			/*
			System.out.println(service.callFunction(sparkDevice, "alarm", "A"));
			Thread.sleep(1000);
			System.out.println(service.callFunction(sparkDevice, "alarm", "S"));
			Thread.sleep(1000);
			System.out.println(service.callFunction(sparkDevice, "alarm", "A"));
			Thread.sleep(1000);
			System.out.println(service.callFunction(sparkDevice, "alarm", "S"));
			Thread.sleep(1000);
			System.out.println(service.callFunction(sparkDevice, "alarm", "A"));
			Thread.sleep(1000);
			System.out.println(service.callFunction(sparkDevice, "alarm", "S"));
			Thread.sleep(1000);
			*/
			InputStream es = service.getEventStream(sparkDevice, "status");
			char c = 0;
			while((c=(char) es.read())!=0){
				System.out.print(c);
			};
		}
				
		
	}

}

