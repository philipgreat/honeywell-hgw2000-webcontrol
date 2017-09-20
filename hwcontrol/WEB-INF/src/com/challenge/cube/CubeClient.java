package com.challenge.cube;

import hwcontrol.BaseClient;
import hwcontrol.Configuration;
import hwcontrol.ExecutionResult;
import hwcontrol.VersionInfo;

import java.io.IOException;

public class CubeClient extends BaseClient{

	private static CubeClient control;
	
	private CubeConnection connection;
	
	public static void main(String[] args) {
		

	}
	public static synchronized CubeClient instance() 
	{
		if(control==null){
			 control=new CubeClient();
			 
			 
		}
		
		
		return control;
		
	}
	public Configuration viewCurrentConfiguration() {
		
		return null;
	}

	public String viewScreenLog() throws IOException {
		
		return null;
	}
	
	public ExecutionResult querySparkLight(String maskId, String loopId, String deviceId)
			throws IOException {
		CubeMessageBody body = new CubeMessageBody().buildReadSparkLightStatus(maskId, 
				loopId,
				deviceId);
		return getResult(body);
		//return getResult(null);
	}

	public ExecutionResult controlAirCondition(int id, int onOrOff, int mode,
			int fan, int windDirection, int tempToSet) throws IOException {
		
		return getResult(null);
	}

	public ExecutionResult queryAirCondition(int id) throws IOException {
		
		return getResult(null);
	}

	public ExecutionResult controlInfradRedAirCondition(int id, int irid)
			throws IOException {
		
		return getResult(null);
	}

	public ExecutionResult controlUFHeat(int id, int onOrOff, int tempToSet)
			throws IOException {
		
		return getResult(null);
	}

	public ExecutionResult queryUFHeat(int id) throws IOException {
		
		return getResult(null);
	}

	public ExecutionResult controlRelay(int id, int onOrOff) throws IOException {
		
		return getResult(null);
	}

	public ExecutionResult queryRelay(int id) throws IOException {
		
		return getResult(null);
	}

	public ExecutionResult controlCurtain(int id, int action, int onOrOff,
			int position) throws IOException {
		
		return getResult(null);
	}

	public ExecutionResult queryCurtain(int id) throws IOException {
		
		return getResult(null);
	}

	public ExecutionResult controlSparkCurtain(String maskId, String loopId, String deviceId,  String openingOrClosing) throws IOException {
		CubeMessageBody body = new CubeMessageBody().buildSetSparkLightCurtainStatus(maskId, loopId, deviceId, openingOrClosing);
		return getResult(body);
		//return getResult(null);
	}

	public ExecutionResult querySparkCurtain(String maskId, String loopId, String deviceId)
			throws IOException {
		CubeMessageBody body = new CubeMessageBody().buildReadSparkLightCurtainStatus(maskId, loopId, deviceId);
		return getResult(body);
		

	}

	public ExecutionResult controlSparkRelay(String maskId, String loopId, String deviceId,  String onOrOff) throws IOException {
		CubeMessageBody body = new CubeMessageBody().buildSetSparkLightRelayStatus(maskId, loopId, deviceId, onOrOff);
		return getResult(body);
		//return getResult(null);
	}
	
	public ExecutionResult querySparkRelay(String maskId, String loopId, String deviceId)
			throws IOException {
		CubeMessageBody body = new CubeMessageBody().buildReadSparkLightRelayStatus(maskId, loopId, deviceId);
		return getResult(body);
		

	}
	
	public ExecutionResult controlInfraRedDevice(int devid, int modid)
			throws IOException {
		
		return getResult(null);
	}

	public ExecutionResult controlWifi2IR(int moduleid, int keyindex, int delay)
			throws IOException {
		
		return getResult(null);
	}

	public ExecutionResult controlMusic(int area, int status, int volumn,
			int command, int parameter) throws IOException {
		
		return getResult(null);
	}

	public ExecutionResult queryMusic(int area) throws IOException {
		
		return getResult(null);
	}

	public ExecutionResult querySensor(int area, int loop, int sensortype,
			int subnetid, int deviceid, int logicNumber) throws IOException {
		
		return getResult(null);
	}

	public ExecutionResult controlScenario(String scenarioId) throws IOException {
		
		CubeMessageBody body = new CubeMessageBody().buildSetScenario(scenarioId,"123456");
		return getResult(body);
	}

	public ExecutionResult queryScenario(int areaid) throws IOException {
		
		return getResult(null);
	}

	public ExecutionResult controlTriger(int id, int onOrOff)
			throws IOException {
		
		return getResult(null);
	}

	public ExecutionResult queryTriger(int id) throws IOException {
		
		return getResult(null);
	}
	protected boolean isNeedReconnect(){
		if(this.connection==null){
			return true;
		}
		if(this.connection.isClosed()){
			return true;
		}
		return false;
	}
	protected void ensureState() throws IOException{
		if(!isNeedReconnect()){
			return;
		}

		this.connect();
		
	}
	

	protected void connectToGateway(String username, String password,
			String hostname, int port) throws IOException  {
		//String ipAddress = "192.168.0.104";
		//int port = 9000;
		connection = new CubeConnection(hostname, port);
		try {
			connection.connect();
		} catch (IOException e) {
			// 连接失败了，重置connection，这样有机会重新连接
			connection = null;
			throw e;
			//return;
		}
		String cudeId = username;
		//String password = "12345";
		/*
		CubeReponseBody bodys[] = {conn.auth(cudeId, password),
				conn.getConfig(),conn.heartbeat(),
				conn.execute(new CubeMessageBody().buildSetSparkLightStatus("1", "2", "4", "0", "off")),
				conn.execute(new CubeMessageBody().buildSetSparkLightRelayStatus("1", "1", "2", "on"))
				};
		*/
		connection.auth(cudeId, password);
		
		
	}

	public ExecutionResult controlLight(int lightId, int action, int onOrOff, int dimmer) throws IOException {
		
		return getResult(null);
	}

	public ExecutionResult controlSparkLight(String maskId, String loopId, String deviceId,
			String onOrOff, String dimmer) throws IOException {
		
		CubeMessageBody body = new CubeMessageBody().buildSetSparkLightStatus(maskId, loopId, deviceId, dimmer, onOrOff);
		return getResult(body);
	}
	
	public ExecutionResult viewCubeConfig() throws IOException {
		
		CubeMessageBody body = new CubeMessageBody().buildGetConfig();
		return getResult(body);
	}
	
	public VersionInfo versionInfo() throws Exception {
		
		return null;
	}
	public ExecutionResult versionInfo2() throws Exception {
		
		CubeMessageBody body = new CubeMessageBody().buildGetConfig();
		return getResult(body);
	}
	
	public ExecutionResult getResult(CubeMessageBody messageBody) throws IOException {
		ensureState();
		
		
		CubeExecutionResult result = new CubeExecutionResult();
		if(messageBody==null){
			result.setReceivedResponse("该功能暂时没有实现,25");
			result.setWebCommand("空的命令");
			return result;
		}
		
		this.logln(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+messageBody.getBody());
		
		result.setSentCommand(messageBody.getBody());
		
		try{
			CubeReponseBody reponse = connection.execute(messageBody);
			//result.set
			result.setReceivedResponse(reponse);
			this.logln("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"+reponse.getResponsText());
			return result;
		}catch(IllegalArgumentException e){
			//发生这个错误是因为
			result.setWebResult("网关通信错误，返回了不认识的协议头，连接讲关闭重启");
			
			this.connect();
			return result;
		}
		
		

	}

}
