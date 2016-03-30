package hwcontrol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeControl {
	
	public static HomeControl control;
	
	public static synchronized HomeControl instance()
	{
		if(control==null){
			 control=new HomeControl();
		}
		return control;
		
	}
	
	private HomeControl(){
		controller=new HGW2000Controller();
		
		
	}
	
	private HGW2000Controller controller;



	public Configuration viewCurrentConfiguration() {
		return controller.viewCurrentConfiguration();
	}

	public String viewLog() throws IOException{
		
		return controller.viewLog();
	}

	public ExecutionResult queryHBusLight(int area, int loop)
			throws IOException {
		return controller.queryHBusLight(area, loop);
	}

	public ExecutionResult controlAirCondition(int id, int onOrOff, int mode,
			int fan, int windDirection, int tempToSet) throws IOException {
		return controller.controlAirCondition(id, onOrOff, mode, fan,
				windDirection, tempToSet);
	}

	public ExecutionResult queryAirCondition(int id) throws IOException {
		return controller.queryAirCondition(id);
	}



	public ExecutionResult controlInfradRedAirCondition(int id, int irid)
			throws IOException {
		return controller.controlInfradRedAirCondition(id, irid);
	}

	public ExecutionResult controlUFHeat(int id, int onOrOff, int tempToSet)
			throws IOException {
		return controller.controlUFHeat(id, onOrOff, tempToSet);
	}

	public ExecutionResult queryUFHeat(int id) throws IOException {
		return controller.queryUFHeat(id);
	}

	public ExecutionResult controlRelay(int id, int onOrOff) throws IOException {
		return controller.controlRelay(id, onOrOff);
	}

	public ExecutionResult queryRelay(int id) throws IOException {
		return controller.queryRelay(id);
	}

	public ExecutionResult controlCurtain(int id, int action, int onOrOff,
			int position) throws IOException {
		return controller.controlCurtain(id, action, onOrOff, position);
	}

	public ExecutionResult queryCurtain(int id) throws IOException {
		return controller.queryCurtain(id);
	}

	public ExecutionResult controlHBusCurtain(int area, int loop, int action,
			int onOrOff, int position) throws IOException {
		return controller.controlHBusCurtain(area, loop, action, onOrOff,
				position);
	}

	public ExecutionResult queryHBusCurtain(int area, int loop)
			throws IOException {
		return controller.queryHBusCurtain(area, loop);
	}

	public ExecutionResult controlInfraRedDevice(int devid, int modid)
			throws IOException {
		return controller.controlInfraRedDevice(devid, modid);
	}

	public ExecutionResult controlWifi2IR(int moduleid, int keyindex, int delay)
			throws IOException {
		return controller.controlWifi2IR(moduleid, keyindex, delay);
	}

	public ExecutionResult controlMusic(int area, int status, int volumn,
			int command, int parameter) throws IOException {
		return controller
				.controlMusic(area, status, volumn, command, parameter);
	}

	public ExecutionResult queryMusic(int area) throws IOException {
		return controller.queryMusic(area);
	}

	public ExecutionResult querySensor(int area, int loop, int sensortype,
			int subnetid, int deviceid, int logicNumber) throws IOException {
		return controller.querySensor(area, loop, sensortype, subnetid,
				deviceid, logicNumber);
	}

	public ExecutionResult controlScenario(int scenarioId) throws IOException {
		return controller.controlScenario(scenarioId);
	}

	public ExecutionResult queryScenario(int areaid) throws IOException {
		return controller.queryScenario(areaid);
	}

	public ExecutionResult controlTriger(int id, int onOrOff)
			throws IOException {
		return controller.controlTriger(id, onOrOff);
	}

	public ExecutionResult queryTriger(int id) throws IOException {
		return controller.queryTriger(id);
	}

	public void connectToGateway(String username, String password,
			String hostname, int port) throws NoSuchAlgorithmException,
			UnsupportedEncodingException, IOException {
		controller.connectToGateway(username, password, hostname, port);
		
	}

	public ExecutionResult controlLight(int lightId, int action, int onOrOff, int dimmer)
			throws IOException {
		return controller.controlLight(lightId, action, onOrOff, dimmer);
	}

	public ExecutionResult controlHBusLight(int area, int loop, int action, int onOrOff,
			int dimmer) throws IOException {
		return controller.controlHBusLight(area, loop, action, onOrOff, dimmer);
	}

	public VersionInfo versionInfo() throws Exception {
		return controller.versionInfo();
	}
	
	
	
}