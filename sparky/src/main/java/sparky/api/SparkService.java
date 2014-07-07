package sparky.api;

import java.util.List;

import sparky.SparkDevice;
import sparky.impl.SparkIoException;

public interface SparkService {
	public List<SparkDevice> getDevices() throws SparkIoException;
	public String getDeviceInfo(SparkDevice device) throws SparkIoException;
	public String subscribeEventOnAllDevices(String eventName);
	public String getToken(String username,String password) throws SparkIoException;
	public String getVariable(SparkDevice device,String varName) throws SparkIoException;
	public String callFunction(SparkDevice device,String functionName,Object...args) throws SparkIoException;
}

