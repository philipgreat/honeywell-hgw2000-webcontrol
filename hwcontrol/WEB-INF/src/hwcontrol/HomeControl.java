package hwcontrol;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class HomeControl implements HomeControlAPI  {
	
	public static HomeControlAPI control;
	
	public static synchronized HomeControlAPI instance()
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



	/* (non-Javadoc)
	 * @see hwcontrol.HomeControlAPI#viewCurrentConfiguration()
	 */
	public Configuration viewCurrentConfiguration() {
		return controller.viewCurrentConfiguration();
	}

	/* (non-Javadoc)
	 * @see hwcontrol.HomeControlAPI#viewLog()
	 */
	public String viewLog() throws IOException{
		
		return controller.viewLog();
	}

	/* (non-Javadoc)
	 * @see hwcontrol.HomeControlAPI#queryHBusLight(int, int)
	 */
	public ExecutionResult queryHBusLight(int area, int loop)
			throws IOException {
		return controller.queryHBusLight(area, loop);
	}

	/* (non-Javadoc)
	 * @see hwcontrol.HomeControlAPI#controlAirCondition(int, int, int, int, int, int)
	 */
	public ExecutionResult controlAirCondition(int id, int onOrOff, int mode,
			int fan, int windDirection, int tempToSet) throws IOException {
		return controller.controlAirCondition(id, onOrOff, mode, fan,
				windDirection, tempToSet);
	}

	/* (non-Javadoc)
	 * @see hwcontrol.HomeControlAPI#queryAirCondition(int)
	 */
	public ExecutionResult queryAirCondition(int id) throws IOException {
		return controller.queryAirCondition(id);
	}



	/* (non-Javadoc)
	 * @see hwcontrol.HomeControlAPI#controlInfradRedAirCondition(int, int)
	 */
	public ExecutionResult controlInfradRedAirCondition(int id, int irid)
			throws IOException {
		return controller.controlInfradRedAirCondition(id, irid);
	}

	/* (non-Javadoc)
	 * @see hwcontrol.HomeControlAPI#controlUFHeat(int, int, int)
	 */
	public ExecutionResult controlUFHeat(int id, int onOrOff, int tempToSet)
			throws IOException {
		return controller.controlUFHeat(id, onOrOff, tempToSet);
	}

	/* (non-Javadoc)
	 * @see hwcontrol.HomeControlAPI#queryUFHeat(int)
	 */
	public ExecutionResult queryUFHeat(int id) throws IOException {
		return controller.queryUFHeat(id);
	}

	/* (non-Javadoc)
	 * @see hwcontrol.HomeControlAPI#controlRelay(int, int)
	 */
	public ExecutionResult controlRelay(int id, int onOrOff) throws IOException {
		return controller.controlRelay(id, onOrOff);
	}

	/* (non-Javadoc)
	 * @see hwcontrol.HomeControlAPI#queryRelay(int)
	 */
	public ExecutionResult queryRelay(int id) throws IOException {
		return controller.queryRelay(id);
	}

	/* (non-Javadoc)
	 * @see hwcontrol.HomeControlAPI#controlCurtain(int, int, int, int)
	 */
	public ExecutionResult controlCurtain(int id, int action, int onOrOff,
			int position) throws IOException {
		return controller.controlCurtain(id, action, onOrOff, position);
	}

	/* (non-Javadoc)
	 * @see hwcontrol.HomeControlAPI#queryCurtain(int)
	 */
	public ExecutionResult queryCurtain(int id) throws IOException {
		return controller.queryCurtain(id);
	}

	/* (non-Javadoc)
	 * @see hwcontrol.HomeControlAPI#controlHBusCurtain(int, int, int, int, int)
	 */
	public ExecutionResult controlHBusCurtain(int area, int loop, int action,
			int onOrOff, int position) throws IOException {
		return controller.controlHBusCurtain(area, loop, action, onOrOff,
				position);
	}

	/* (non-Javadoc)
	 * @see hwcontrol.HomeControlAPI#queryHBusCurtain(int, int)
	 */
	public ExecutionResult queryHBusCurtain(int area, int loop)
			throws IOException {
		return controller.queryHBusCurtain(area, loop);
	}

	/* (non-Javadoc)
	 * @see hwcontrol.HomeControlAPI#controlInfraRedDevice(int, int)
	 */
	public ExecutionResult controlInfraRedDevice(int devid, int modid)
			throws IOException {
		return controller.controlInfraRedDevice(devid, modid);
	}

	/* (non-Javadoc)
	 * @see hwcontrol.HomeControlAPI#controlWifi2IR(int, int, int)
	 */
	public ExecutionResult controlWifi2IR(int moduleid, int keyindex, int delay)
			throws IOException {
		return controller.controlWifi2IR(moduleid, keyindex, delay);
	}

	/* (non-Javadoc)
	 * @see hwcontrol.HomeControlAPI#controlMusic(int, int, int, int, int)
	 */
	public ExecutionResult controlMusic(int area, int status, int volumn,
			int command, int parameter) throws IOException {
		return controller
				.controlMusic(area, status, volumn, command, parameter);
	}

	/* (non-Javadoc)
	 * @see hwcontrol.HomeControlAPI#queryMusic(int)
	 */
	public ExecutionResult queryMusic(int area) throws IOException {
		return controller.queryMusic(area);
	}

	/* (non-Javadoc)
	 * @see hwcontrol.HomeControlAPI#querySensor(int, int, int, int, int, int)
	 */
	public ExecutionResult querySensor(int area, int loop, int sensortype,
			int subnetid, int deviceid, int logicNumber) throws IOException {
		return controller.querySensor(area, loop, sensortype, subnetid,
				deviceid, logicNumber);
	}

	/* (non-Javadoc)
	 * @see hwcontrol.HomeControlAPI#controlScenario(int)
	 */
	public ExecutionResult controlScenario(int scenarioId) throws IOException {
		return controller.controlScenario(scenarioId);
	}

	/* (non-Javadoc)
	 * @see hwcontrol.HomeControlAPI#queryScenario(int)
	 */
	public ExecutionResult queryScenario(int areaid) throws IOException {
		return controller.queryScenario(areaid);
	}

	/* (non-Javadoc)
	 * @see hwcontrol.HomeControlAPI#controlTriger(int, int)
	 */
	public ExecutionResult controlTriger(int id, int onOrOff)
			throws IOException {
		return controller.controlTriger(id, onOrOff);
	}

	/* (non-Javadoc)
	 * @see hwcontrol.HomeControlAPI#queryTriger(int)
	 */
	public ExecutionResult queryTriger(int id) throws IOException {
		return controller.queryTriger(id);
	}

	/* (non-Javadoc)
	 * @see hwcontrol.HomeControlAPI#connectToGateway(java.lang.String, java.lang.String, java.lang.String, int)
	 */
	public void connectToGateway(String username, String password,
			String hostname, int port) throws NoSuchAlgorithmException,
			UnsupportedEncodingException, IOException {
		controller.connectToGateway(username, password, hostname, port);
		
	}

	/* (non-Javadoc)
	 * @see hwcontrol.HomeControlAPI#controlLight(int, int, int, int)
	 */
	public ExecutionResult controlLight(int lightId, int action, int onOrOff, int dimmer)
			throws IOException {
		return controller.controlLight(lightId, action, onOrOff, dimmer);
	}

	/* (non-Javadoc)
	 * @see hwcontrol.HomeControlAPI#controlHBusLight(int, int, int, int, int)
	 */
	public ExecutionResult controlHBusLight(int area, int loop, int action, int onOrOff,
			int dimmer) throws IOException {
		return controller.controlHBusLight(area, loop, action, onOrOff, dimmer);
	}

	/* (non-Javadoc)
	 * @see hwcontrol.HomeControlAPI#versionInfo()
	 */
	public VersionInfo versionInfo() throws Exception {
		return controller.versionInfo();
	}
	
	
	
}