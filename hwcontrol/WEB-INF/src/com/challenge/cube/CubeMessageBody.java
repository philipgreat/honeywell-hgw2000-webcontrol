package com.challenge.cube;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = CubeMsgBodySerializer.class)
public class CubeMessageBody {
	//static String testBody = "{\"msgid\":\"2387r958273894\",\"action\":\"request\",\"subaction\":\"heartbeat\"}";
	int length = 0;
	byte[] body ;
	List<CubeLoop> deviceLoopMap;
	List<CubeLoop> keyTypeLoopMap;
	
	
	public List<CubeLoop> getKeyTypeLoopMap() {
		return keyTypeLoopMap;
	}
	public void setKeyTypeLoopMap(List<CubeLoop> keyTypeLoopMap) {
		this.keyTypeLoopMap = keyTypeLoopMap;
	}
	public List<CubeLoop> getDeviceLoopMap() {
		return deviceLoopMap;
	}
	public void setDeviceLoopMap(List<CubeLoop> deviceLoopMap) {
		this.deviceLoopMap = deviceLoopMap;
	}
	
	public CubeLoop newLoop(){
		CubeLoop loop = new CubeLoop();
		loop.setParent(this);
		return this.addLoop(loop);
	}
	
	public CubeLoop newKeyTypeLoop(){
		CubeLoop loop = new CubeLoop();
		loop.setParent(this);
		return this.addKeyTypeLoop(loop);
	}
	
	protected void ensureLoopList(){
		
		if(deviceLoopMap == null){
			deviceLoopMap =  new ArrayList<CubeLoop>();
		}
		
	}
	protected CubeLoop addLoop(CubeLoop cubeLoop){
		ensureLoopList();
		this.getDeviceLoopMap().add(cubeLoop);
		return cubeLoop;
	}
	
	protected CubeLoop addKeyTypeLoop(CubeLoop cubeLoop){
		ensureaddKeyTypeLoop();
		this.getKeyTypeLoopMap().add(cubeLoop);
		return cubeLoop;
	}
	private void ensureaddKeyTypeLoop() {
		if(keyTypeLoopMap == null){
			keyTypeLoopMap =  new ArrayList<CubeLoop>();
		}
		
	}
	public CubeMessageBody(){
		
	}
	public byte [] getBodyData(){
		return body;
	}
	public int length() {
		// TODO Auto-generated method stub
		return length;
	}
	
	private List<CubeProperty> properties;
	private String bodyString;
	

	public List<CubeProperty> getProperties() {
		if(properties == null){
			properties = new ArrayList<CubeProperty>();
		}
		return properties;
	}
	
	public CubeMessageBody addProperty(CubeProperty property){
		getProperties().add(property);
		return this;
	}
	
	public CubeMessageBody addProperty(String  propertyName, String value){
		CubeProperty property = new CubeProperty();
		property.setPropertyName(propertyName);
		property.setPropertyValue(new CubeValue(value));
		addProperty(property);
		return this;
	}
	
	public void setProperties(List<CubeProperty> properties) {
		this.properties = properties;
	}


	
	
	
	public static void main(String[] args) throws JsonProcessingException {
		
		CubeMessageBody body = new CubeMessageBody();
		
		//body.withId("newid").withAction("request").withSubaction()
		body.buildHeartbeat();
		
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(new CubeMessageBody().buildHeartbeat());
		System.out.println(msg);
		msg = mapper.writeValueAsString(new CubeMessageBody().buildLogin("cubeid", "password"));
		System.out.println(msg);
		msg = mapper.writeValueAsString(new CubeMessageBody().buildGetConfig());
		System.out.println(msg);
		//
		
		
		
	}
	
	public CubeMessageBody msgBody(){
		return this.withMsgid(System.currentTimeMillis()+"");
	}
	public String getBody(){
		
		return this.bodyString;
		
	}
	protected CubeMessageBody done() {
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			this.bodyString =  mapper.writeValueAsString(this);
			this.body  =this.bodyString.getBytes();
			this.length = body.length;
			return this;
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			throw new IllegalStateException("Parsing error: "+ e.getMessage());
		}
		
		
		
	}
	public CubeMessageBody buildLogin(String cubeid, String cubepwd){
		
		return withRegister().withCubeid(cubeid).withCubepwd(cubepwd).done();
		
	}
	
	public CubeMessageBody buildHeartbeat(){
		
		return msgBody().withAction("request").withSubaction("heartbeat").done();
		
	}
	public CubeMessageBody buildGetConfig(){
		
		return msgBody().withAction("request").withSubaction("getdeviceconfig").withModuletype("cube").withVersion("0").done();
		
	}
	
	
	
	
	
	
	/*{
“msgid”:”2387r958273894”
“action”:”request”
“subaction”:” configmodule”
“moduletype”:”cube”
“cubeid”:”1324545”
“cubeoldpwd”:”29u4rji” 
“cubepwd”:”jowjeoiu2o0”
}*/
	public CubeMessageBody buildChangeCubePassword(String cubeId, String oldPassword, String newPassword){
		
		return msgBody().withAction("request")
				.withSubaction("getdeviceconfig")
				.withModuletype("cube")
				.withCubeid(cubeId)
				.withCubeoldpwd(oldPassword)
				.withCubepwd(newPassword)
				.withVersion("0").done();
		
	}
	/*{
“msgid”:”2387r958273894”
“action”:”request”
“subaction”:” configmodule”
“moduletype”:”cube”
“cubeid”:”1324545”
 “aliasname”:”我的家”
}*/
	public CubeMessageBody buildConfigAlias(String cubeId, String aliasName){
		
		return msgBody().withAction("request")
				.withSubaction("getdeviceconfig")
				.withModuletype("cube")
				.withCubeid(cubeId)
				.withAliasname(aliasName)
				.done();
		
	}
	/*
 “action”:”event”
“subaction”:” configmodule”
“configtype”:”add”
}*/
	public CubeMessageBody buildDiscoverNewModule(){
		
		return msgBody().withAction("event")
				.withSubaction("getdeviceconfig")
				.withModuletype("configmodule")
				.withConfigtype("add")
				.done();
		
	}
	
	/*
	 * {
“msgid”:”2387r958273894”
“action”:”request”
“subaction”:” getnewmodulelist”
}*/
	
	public CubeMessageBody buildGetDiscoveredNewModuleInfo(){
		
		return msgBody().withAction("request")
				.withSubaction("getnewmodulelist")
				.withModuletype("configmodule")
				
				.done();
		
	}
	/*
	 * {
“msgid”:”2387r958273894”
“action”:”request”
“subaction”:” getdeviceconfigipvdp”
“moduletype”:”cube”
 “version”:”0”
}
	 * 
	 * */
	
	public CubeMessageBody buildGetDeviceConfigIPVDP(){
		
		return msgBody().withAction("request")
				.withSubaction("getdeviceconfigipvdp")
				.withModuletype("cube")
				.withVersion("0")
				.done();
		
	}
	
	/*
	 * {
“msgid”:”2387r958273894”,
“action”:”request”,
“subaction”:” configmodule”,
“moduletype”:”cube”,
“configtype”:”ethernet”,
“ethmode”:”manual/dhcp”,
“ethname”:”eth0/eth1” , // currently only support eth1
“ethip”:”10.2.2.5”,
“ethmask”:”255.0.0.0”,
“ethgw”:”10.0.0.1”,
“ethdns1”:”10.0.0.1”,
“ethdns2”:”10.0.0.2”,
}
	 * 
	 * */
	
	public CubeMessageBody buildConfigEthernet(String ethMode, String ethName, 
			String ethIp, String ethMask, String ethGW, String ethDNS1, String ethDNS2){
		
		return msgBody().withAction("request")
				.withSubaction("configmodule")
				.withModuletype("cube")
				.withConfigtype("ethernet")
				.withEthMode(ethMode)
				.withEthName(ethName)
				.withEthIp(ethIp)
				.withEthMask(ethMask)
				.withEthGW(ethGW)
				.withEthDNS1(ethDNS1)
				.withEthDNS2(ethDNS2)
				//.with
				.done();
		
	}
	
	public CubeMessageBody buildConfigSecurityCode(String oldPwd, 
			String newPwd
			){
		
		return msgBody().withAction("request")
				.withSubaction("configsecurity")
				.withModuletype("cube")
				.withOldpwd(oldPwd)
				.withNewpwd(newPwd)
				//.with
				.done();
		
	}
	public CubeMessageBody buildGet485ConfigType(){
		
		return msgBody().withAction("request")
				.withSubaction("get485configtype")
				.withModuletype("cube")
				
				//.with
				.done();
		
	}
	/*{
“msgid”:”2387r958273894”
“action”:”request”
“subaction”:”configmodule”
“configtype”:”modify”
“moduletype”:”485”
“primaryid”:100
 “aliasname”:”485设备modify”
 }*/
	public CubeMessageBody buildConfig485Module(String macAddr,String ipAddr,String aliasName){
		
		return msgBody().withAction("request")
				.withSubaction("configmodule")
				.withConfigtype("add")
				.withModuletype("485")
				.withModulemacaddr(macAddr)
				.withModuleipaddr(ipAddr)
				.withAliasname(aliasName)
				
				//.with
				.done();
		
	}
	/*{
“msgid”:”2387r958273894”
“action”:”response”
“subaction”:”configmodule”
“configtype”:”modify”
“moduletype”:”485”
“errorcode”:0
“primaryid”:100
 “aliasname”:”485设备modify”
 }*/
	public CubeMessageBody buildModify485Module( String primaryId,String aliasNam){
		
		return msgBody().withAction("request")
				.withSubaction("configmodule")
				.withConfigtype("modify")
				.withModuletype("485")
				.withPrimaryid(primaryId)
				.withAliasname(aliasNam)
				
				//.with
				.done();
		
	}
	
	public CubeMessageBody buildDelete485Module( String primaryId){
		
		return msgBody().withAction("request")
				.withSubaction("configmodule")
				.withConfigtype("modify")
				.withModuletype("485")
				.withPrimaryid(primaryId)
				
				
				//.with
				.done();
		
	}
	/*{
“msgid”:”2387r958273894”
“action”:”request”
“subaction”:” configdevice”
“moduletype”:”485”
“configtype”:”add”
“primaryid”:100（外围设备表的id）
 “looptype”:”aircondition”
“brandname”:”dakin”
“portid”:1
 “deviceloopmap”:
[
{
“slaveaddr”:2
“loopid”:1
“roomid”:1
“aliasname”:”主卧空调”
},
{
“slaveaddr”:2
“loopid”:2
“roomid”:2
 “aliasname”:”客卧空调”
},
]
}*/
	public CubeMessageBody buildConfig485Aircondition( String primaryId, String brandName, String portId){
		
		return msgBody().withAction("request")
				.withSubaction("configdevice")
				.withConfigtype("add")
				.withModuletype("485")
				.withPrimaryid(primaryId)
				.withLooptype("aircondition")
				.withBrandname(brandName)
				.withPortid(portId)
				.withDeviceMap("") //TODO here is complex
				//.with
				.done();
		
	}
	public CubeMessageBody buildDelete485Aircondition( String primaryId, String brandName, String portId){
		
		return msgBody().withAction("request")
				.withSubaction("configdevice")
				.withConfigtype("delete")
				.withModuletype("485")
				.withPrimaryid(primaryId)
				.withLooptype("aircondition")
				.withBrandname(brandName)
				.withPortid(portId)
				.withDeviceMap("") //TODO here is complex
				//.with
				.done();
		
	}
	/*7）查询485空调状态
head
PHONE_CUBE_PROTOCOL 0x00  0x********
B
O
D
y
{"action":"request",
"subaction":"readdevice",
"deviceloopmap":
[{"loopid":1,"brandname":"ecc_o1","looptype":"aircondition","modulemacaddr":"00:1F:55:2A:00:74","slaveaddr":1,"portid":1}
],
"msgid":"28"
,"moduletype":"485"
}*/
	public CubeMessageBody buildRead485Aircondition(String loopid, String macAddr, String slaveAddr, String portId){
		
		return msgBody().withAction("request")
				.withSubaction("readdevice")
				.withModuletype("485")
				.newLoop().withLoopid(loopid)
				.withBrandname("ecc_01")
				.withLooptype("aircondition")
				.withModulemacaddr(macAddr)
				.withSlaveaddr(slaveAddr)
				.withPortid(portId)
				.parent()
				//.with
				.done();
		
	}
	
	
	/*8）设置485空调状态
head
PHONE_CUBE_PROTOCOL 0x00  0x********
body
{"action":"request",
"subaction":"setdevice",
"deviceloopmap":[
{
"fanspeed":"low","loopid":1,"brandname":"ecc_o1",
"looptype":"aircondition",
"modulemacaddr":"00:1F:55:2A:00:74",
"slaveaddr":1,"status":"on","portid":1,"settemp":12,"mode":"cooling"
}
],
"msgid":"29",
"moduletype":"485"
}*/
	
	public CubeMessageBody buildSet485Aircondition(String loopId, String fanspeed, String macAddr, String slaveAddr, String airConditionStatus, String portId, String tempreture, String workMode){
		return msgBody().withAction("request")
				.withSubaction("setdevice")
				.withModuletype("485")
				.newLoop()
				.withLoopid(loopId)
				.withFanspeed(fanspeed)
				.withLooptype("aircondition")
				.withModulemacaddr(macAddr)
				.withSlaveaddr(slaveAddr)
				.withStatus(airConditionStatus)
				.withPortid(portId)
				.withSettemp(tempreture)
				.withMode(workMode)
				.parent()
				.done();
		
				
	}
	
	//12）查询温控器状态
	//13）设置温控器状态
/*
12）查询温控器状态
head
PHONE_CUBE_PROTOCOL 0x00  0x********
body
{"action":"request","subaction":"readdevice","deviceloopmap":[{" 
"uuid":"0081"，
}],
"msgid":"36"
*/
	
	public CubeMessageBody buildReadThermostat( String deviceUUid){
		return msgBody()
				.withAction("request")
				.withSubaction("readdevice")
				.newLoop().withUuid(deviceUUid)
				.parent().done();
	}
	/*
13）设置温控器状态
head
PHONE_CUBE_PROTOCOL 0x00  0x********
body
{"action":"request","subaction":"setdevice","deviceloopmap":
[
{"uuid":"0081"，
"fanspeed":"low",""status":"on"settemp":12,"mode":"cooling"}],
"msgid":"37}*/
	
	public CubeMessageBody buildSetThermostat( String deviceUUid, String fanSpeed, String status, String tempurature, String workMode){
		return msgBody()
				.withAction("request")
				.withSubaction("readdevice")
				.newLoop().withUuid(deviceUUid)
				.withFanspeed(fanSpeed)
				.withStatus(status)
				.withSettemp(tempurature)
				.withMode(workMode)
				.parent().done();
	}
	
	//1.      APP登陆cube&心跳
	//1)      APP用cubeid登陆cube
	//7）查询485空调状态
	//8）设置485空调状态
	//
	//12）查询温控器状态
	//13）设置温控器状态
	//
	//7）查询bacnet空调状态
	//8）设置bacnet空调状态
	//
	//15）查询sparklighting 灯光状态
	
	/*
	 * body
{"action":"request",
"subaction":"readdevice",
"deviceloopmap":[
{"maskid":1,"loopid":1,"deviceid":1,"looptype":"light"},
{"maskid":1,"loopid":2,"deviceid":1,"looptype":"light"}],
"msgid":"9",
"moduletype":"sparklighting"
}
	 * 
	 * */
	public CubeMessageBody buildReadSparkLightStatus(String maskId, String loopId, String deviceId){
		return msgBody().withAction("request")
				.withSubaction("readdevice")
				.withModuletype("sparklighting")
				.newLoop().withMaskid(maskId)
				.withLoopid(loopId)
				.withDeviceid(deviceId)
				.withLooptype("light")
				.parent()
				.done();
	
	
	}
	//16）设置sparklighting 灯光状态
	/*
	 * 
	 * {"action":"request","subaction":"setdevice","deviceloopmap":[
{"maskid":1,"loopid":1,"looptype":"light","openclosepercent":45,"status":"on","deviceid":1}
],
"msgid":"10","moduletype":"sparklighting"}

	 * 
	 * */
	public CubeMessageBody buildSetSparkLightStatus(String maskId, String loopId, String deviceId, String openClosePercent, String statusValue ){
		return msgBody().withAction("request")
				.withSubaction("setdevice")
				.withModuletype("sparklighting")
				.newLoop()
					.withMaskid(maskId)
					.withLoopid(loopId)
					.withLooptype("light")
					.withOpenclosepercent(openClosePercent)
					.withStatus(statusValue)
					.withDeviceid(deviceId)				
				.parent()
				.done();
	}
	
	
	//17）查询sparklighting 窗帘状态
	/*
	 * {
"msgid":"2387r958273894"                                                                                                  
"action":"request"
"subaction":" readdevice"
"moduletype":"sparklighting"
 "deviceloopmap"
[
{
"maskid":1
"deviceid":7
"loopid":1
"looptype":"curtain"
},
{
"maskid":1
"deviceid":7
"loopid":2
"looptype":"curtain"
}
]
}

	 * */
	public CubeMessageBody buildReadSparkLightCurtainStatus(String maskId, String loopId, String deviceId){
		return msgBody().withAction("request")
				.withSubaction("readdevice")
				.withModuletype("sparklighting")
				.newLoop()
					.withMaskid(maskId)
					.withLoopid(loopId)
					.withDeviceid(deviceId)
					.withLooptype("curtain")
				.parent()
				.done();
	
	
	}
	
	
	//18）设置sparklighting 窗帘状态
	/*{"action":"request","subaction":"setdevice",
	 * "deviceloopmap":
	 * [{"maskid":1,"loopid":1,"looptype":"curtain","status":
	 * "opening","deviceid":2}],"msgid":"12","moduletype":"sparklighting"}
	*/
	
	
	public CubeMessageBody buildSetSparkLightCurtainStatus(String maskId, String loopId, String deviceId,  String statusValue ){
		return msgBody().withAction("request")
				.withSubaction("setdevice")
				.withModuletype("sparklighting")
				.newLoop()
					.withMaskid(maskId)
					.withLoopid(loopId)
					.withLooptype("curtain")
					//.withOpenclosepercent(openClosePercent)
					.withStatus(statusValue)
					.withDeviceid(deviceId)				
				.parent()
				.done();
	}
	
	
	//19）查询sparklighting relay状态
	
	/*19）查询sparklighting relay状态
head
PHONE_CUBE_PROTOCOL 0x00  0x********
body
{
"msgid":"2387r958273894"                                                                                                  
"action":"request"
"subaction":" readdevice"
"moduletype":"sparklighting"
 "deviceloopmap"
[
{
"maskid":1
"deviceid":7
"loopid":1
"looptype":"relay"
},
{
"maskid":1
"deviceid":7
"loopid":2
"looptype":"relay"
}
]
}*/
	public CubeMessageBody buildReadSparkLightRelayStatus(String maskId, String loopId, String deviceId){
		return msgBody().withAction("request")
				.withSubaction("readdevice")
				.withModuletype("sparklighting")
				.newLoop()
					.withMaskid(maskId)
					.withLoopid(loopId)
					.withDeviceid(deviceId)
					.withLooptype("relay")
				.parent()
				.done();
	
	
	}
	//20）设置sparklighting relay状态
	/*20）设置sparklighting relay状态
head
PHONE_CUBE_PROTOCOL 0x00  0x********
body
{
"msgid":"2387r958273894"                                                                                                  
"action":"request"
"subaction":" setdevice"
"moduletype":" sparklighting"
 "deviceloopmap"
[
{
"maskid":1
"deviceid":7
"loopid":1
"looptype":"relay"
 "status":"on"
},
{
"maskid":1
"deviceid":7
"loopid":2
"looptype":"relay"
 "status":"off"
},
]
}
*/
	public CubeMessageBody buildSetSparkLightRelayStatus(String maskId, String loopId, String deviceId,  String statusValue ){
		return msgBody().withAction("request")
				.withSubaction("setdevice")
				.withModuletype("sparklighting")
				.newLoop()
					.withMaskid(maskId)
					.withLoopid(loopId)
					.withLooptype("relay")
					//.withOpenclosepercent(openClosePercent)
					.withStatus(statusValue)
					.withDeviceid(deviceId)				
				.parent()
				.done();
	}
	
	
	public CubeMessageBody buildSetBackAudioStatus(String loopId, String serial,  String keytype, String keyvalue ){
		return msgBody().withAction("request")
				.withSubaction("setdevice")
				.withModuletype("backaudio")
				.withLoopid(loopId)
				.withModuleserialnum(serial)
				.newKeyTypeLoop()
					.withKeytype(keytype).withKeyvalue(keyvalue).parent().done();
				
	}
	
	
	
	//21）查询sparklighting 空气质量状态
	
	
	
	//
	//7）设置relay回路状态
	//8）查询relay回路状态
	//
	//7）backaudio控制
	//8）backaudio查询
	//
	//4）获取联动规则
	
	/*
4）获取联动规则
head
PHONE_CUBE_PROTOCOL 0x00  0x********
body
{
"msgid":" 2387r958273894",
 "action":"request",
"subaction":" configdevice",
"moduletype":" scenariotrigger",
"primaryid":1,
}*/
	public CubeMessageBody buildReadScenarioList(String priamaryId){
		return msgBody().withAction("request")
				.withSubaction("configdevice")
				.withModuletype("scenariotrigger")
				.withPrimaryid(priamaryId)
				.done();
	}
	
	//5）Enable/Disable场景联动规则
	
	/*5）Enable/Disable场景联动规则
head
PHONE_CUBE_PROTOCOL 0x00  0x********
body
{
"msgid":" 2387r958273894",
 "action":" request",
"subaction":"configdevice",
"moduletype":" scenariotrigger",
"primaryid":1,
 "switchstatus":"on/off",
}*/
	public CubeMessageBody buildSetScenarioStatus(String primaryId, String status){
		return msgBody().withAction("request")
				.withSubaction("configdevice")
				.withModuletype("scenariotrigger")
				.withPrimaryid(primaryId)
				.withSwitchstatus(status)
				.done();
	}
	
	
	//
	//1）查询某个房间的temperature, humidity,pm2.5
	//
	//1）新风控制
	//2）新风查询
	//3）空气质量查询
	
	
	/*
	 * 
	 * {"action":"request",
"subaction":"readdevice",
"deviceloopmap":
[{"loopid":1,"brandname":"ecc_o1","looptype":"aircondition","modulemacaddr":"00:1F:55:2A:00:74","slaveaddr":1,"portid":1}
],
"msgid":"28"
,"moduletype":"485"
}

	 * 
	 * */
	/*
	 * 
	 * {
“msgid”:”2387r958273894”                                                                                                  
“action”:”request”
“subaction”:” setdevice”
“moduletype”:” scenario”
“scenarioid":10
“securitypwd”:”1123445”
 }

	 * 
	 * */
	
	public CubeMessageBody buildSetScenario(String scenarioId, String password){
		return msgBody().withAction("request")
				.withSubaction("setdevice")
				.withModuletype("scenario")
				.withScenarioid(scenarioId)
				.withSecuritypwd(password)
				.done();
	}
	
	private CubeMessageBody withDeviceMap(String deviceMap) {
		// TODO Auto-generated method stub
		addProperty("deviceloopmap", deviceMap); 
		return this;
	}
	private CubeMessageBody withOldpwd(String value) {
		// TODO Auto-generated method stub
		addProperty("oldpwd", value); 
		return this;
	}
	private CubeMessageBody withNewpwd(String value) {
		// TODO Auto-generated method stub
		addProperty("newpwd", value); 
		return this;
	}
	private CubeMessageBody withEthMode(String value) {
		// TODO Auto-generated method stub
		addProperty("ethmode", value); 
		return this;
	}
	
	private CubeMessageBody withEthName(String value) {
		// TODO Auto-generated method stub
		addProperty("ethname", value); 
		return this;
	}
	private CubeMessageBody withEthIp(String value) {
		// TODO Auto-generated method stub
		addProperty("ethip", value); 
		return this;
	}
	private CubeMessageBody withEthMask(String value) {
		// TODO Auto-generated method stub
		addProperty("ethmask", value); 
		return this;
	}
	private CubeMessageBody withEthGW(String value) {
		// TODO Auto-generated method stub
		addProperty("ethgw", value); 
		return this;
	}
	private CubeMessageBody withEthDNS1(String value) {
		// TODO Auto-generated method stub
		addProperty("ethdns1", value); 
		return this;
	}

	private CubeMessageBody withEthDNS2(String value) {
		// TODO Auto-generated method stub
		addProperty("ethdns2", value); 
		return this;
	}

	
	private CubeMessageBody withRegister() {
		// TODO Auto-generated method stub
		return msgBody().withAction("request").withSubaction("register");
	}

	public CubeMessageBody withMsgid(String value){ addProperty("msgid", value); return this;}
	public CubeMessageBody withAction(String value){ addProperty("action", value); return this;}
	public CubeMessageBody withSubaction(String value){ addProperty("subaction", value); return this;}
	public CubeMessageBody withCubeid(String value){ addProperty("cubeid", value); return this;}
	public CubeMessageBody withCubepwd(String value){ addProperty("cubepwd", value); return this;}
	public CubeMessageBody withCubeoldpwd(String value){ addProperty("cubeoldpwd", value); return this;}
	public CubeMessageBody withCloudid(String value){ addProperty("cloudid", value); return this;}
	public CubeMessageBody withCloudpwd(String value){ addProperty("cloudpwd", value); return this;}
	public CubeMessageBody withModuletype(String value){ addProperty("moduletype", value); return this;}
	public CubeMessageBody withModulemacaddr(String value){ addProperty("modulemacaddr", value); return this;}
	public CubeMessageBody withModuleipaddr(String value){ addProperty("moduleipaddr", value); return this;}
	public CubeMessageBody withModuleserialnum(String value){ addProperty("moduleserialnum", value); return this;}
	public CubeMessageBody withMachinetype(String value){ addProperty("machinetype", value); return this;}
	public CubeMessageBody withDevicetype(String value){ addProperty("devicetype", value); return this;}
	//public CubeMessageBody with(String value){ addProperty("", value); return this;}
	public CubeMessageBody withLooptype(String value){ addProperty("looptype", value); return this;}
	public CubeMessageBody withBrandname(String value){ addProperty("brandname", value); return this;}
	//public CubeMessageBody with(String value){ addProperty("", value); return this;}
	//public CubeMessageBody with(String value){ addProperty("", value); return this;}
	public CubeMessageBody withAliasname(String value){ addProperty("aliasname", value); return this;}
	public CubeMessageBody withSubdevtype(String value){ addProperty("subdevtype", value); return this;}
	public CubeMessageBody withRoomname(String value){ addProperty("roomname", value); return this;}
	public CubeMessageBody withErrorcode(String value){ addProperty("errorcode", value); return this;}
	//public CubeMessageBody with(String value){ addProperty("", value); return this;}
	public CubeMessageBody withModuleloopmap(String value){ addProperty("moduleloopmap", value); return this;}
	public CubeMessageBody withDeviceloopmap(String value){ addProperty("deviceloopmap", value); return this;}
	public CubeMessageBody withSparklightloopmap(String value){ addProperty("sparklightloopmap", value); return this;}
	public CubeMessageBody withBacnetloopmap(String value){ addProperty("bacnetloopmap", value); return this;}
	public CubeMessageBody withIrloopmap(String value){ addProperty("irloopmap", value); return this;}
	public CubeMessageBody with485Loopmap(String value){ addProperty("485loopmap", value); return this;}
	public CubeMessageBody withRelayloopmap(String value){ addProperty("relayloopmap", value); return this;}
	public CubeMessageBody withAlarmzoneloopmap(String value){ addProperty("alarmzoneloopmap", value); return this;}
	public CubeMessageBody withMaialoopmap(String value){ addProperty("maialoopmap", value); return this;}
	public CubeMessageBody withDeviceid(String value){ addProperty("deviceid", value); return this;}
	public CubeMessageBody withSlaveaddr(String value){ addProperty("slaveaddr", value); return this;}
	public CubeMessageBody withLoopnum(String value){ addProperty("loopnum", value); return this;}
	public CubeMessageBody withLoopid(String value){ addProperty("loopid", value); return this;}
	public CubeMessageBody withMaskid(String value){ addProperty("maskid", value); return this;}
	public CubeMessageBody withPortid(String value){ addProperty("portid", value); return this;}
	public CubeMessageBody withPortrate(String value){ addProperty("portrate", value); return this;}
	public CubeMessageBody withCubebacnetid(String value){ addProperty("cubebacnetid", value); return this;}
	public CubeMessageBody withBacnetdeviceid(String value){ addProperty("bacnetdeviceid", value); return this;}
	public CubeMessageBody withSettemp(String value){ addProperty("settemp", value); return this;}
	public CubeMessageBody withCurrenttmp(String value){ addProperty("currenttmp", value); return this;}
	public CubeMessageBody withMode(String value){ addProperty("mode", value); return this;}
	//public CubeMessageBody with(String value){ addProperty("", value); return this;}
	public CubeMessageBody withFanspeed(String value){ addProperty("fanspeed", value); return this;}
	public CubeMessageBody withStatus(String value){ addProperty("status", value); return this;}
	public CubeMessageBody withSwitchstatus(String value){ addProperty("switchstatus", value); return this;}
	//public CubeMessageBody with(String value){ addProperty("", value); return this;}
	public CubeMessageBody withOpenclosepercent(String value){ addProperty("openclosepercent", value); return this;}
	public CubeMessageBody withZonetype(String value){ addProperty("zonetype", value); return this;}
	public CubeMessageBody withAlarmtimer(String value){ addProperty("alarmtimer", value); return this;}
	public CubeMessageBody withAlarmenable(String value){ addProperty("alarmenable", value); return this;}
	public CubeMessageBody withArmtype(String value){ addProperty("armtype", value); return this;}
	public CubeMessageBody withIsarm(String value){ addProperty("isarm", value); return this;}
	public CubeMessageBody withSecuritypwd(String value){ addProperty("securitypwd", value); return this;}
	public CubeMessageBody withAlarmtype(String value){ addProperty("alarmtype", value); return this;}
	public CubeMessageBody withAlarmmsgid(String value){ addProperty("alarmmsgid", value); return this;}
	public CubeMessageBody withTime(String value){ addProperty("time", value); return this;}
	public CubeMessageBody withRtspurl(String value){ addProperty("rtspurl", value); return this;}
	public CubeMessageBody withAirtype(String value){ addProperty("airtype", value); return this;}
	public CubeMessageBody withCurrenttime(String value){ addProperty("currenttime", value); return this;}
	public CubeMessageBody withCurrentco2(String value){ addProperty("currentco2", value); return this;}
	public CubeMessageBody withCurrentpm2_5(String value){ addProperty("currentpm2_5", value); return this;}
	public CubeMessageBody withCurrenthumid(String value){ addProperty("currenthumid", value); return this;}
	public CubeMessageBody withHnsserveraddr(String value){ addProperty("hnsserveraddr", value); return this;}
	public CubeMessageBody withCallmsg(String value){ addProperty("callmsg", value); return this;}
	public CubeMessageBody withCalltype(String value){ addProperty("calltype", value); return this;}
	public CubeMessageBody withCallsessionid(String value){ addProperty("callsessionid", value); return this;}
	public CubeMessageBody withVideoport(String value){ addProperty("videoport", value); return this;}
	public CubeMessageBody withAudioport(String value){ addProperty("audioport", value); return this;}
	public CubeMessageBody withVideocodectype(String value){ addProperty("videocodectype", value); return this;}
	public CubeMessageBody withAudiocodectype(String value){ addProperty("audiocodectype", value); return this;}
	public CubeMessageBody withVideoratio(String value){ addProperty("videoratio", value); return this;}
	public CubeMessageBody withTakecallipaddr(String value){ addProperty("takecallipaddr", value); return this;}
	public CubeMessageBody withDooropentype(String value){ addProperty("dooropentype", value); return this;}
	public CubeMessageBody withDooropenway(String value){ addProperty("dooropenway", value); return this;}
	public CubeMessageBody withDooropenrole(String value){ addProperty("dooropenrole", value); return this;}
	public CubeMessageBody withDooropenid(String value){ addProperty("dooropenid", value); return this;}
	public CubeMessageBody withUuid(String value){ addProperty("uuid", value); return this;}
	public CubeMessageBody withIpctype(String value){ addProperty("ipctype", value); return this;}
	public CubeMessageBody withIpcsrc(String value){ addProperty("ipcsrc", value); return this;}
	public CubeMessageBody withIpcurl(String value){ addProperty("ipcurl", value); return this;}
	public CubeMessageBody withIpcusername(String value){ addProperty("ipcusername", value); return this;}
	public CubeMessageBody withIpcpassword(String value){ addProperty("ipcpassword", value); return this;}
	public CubeMessageBody withIpcloopmap(String value){ addProperty("ipcloopmap", value); return this;}
	public CubeMessageBody withDisturbtype(String value){ addProperty("disturbtype", value); return this;}
	public CubeMessageBody withKeytype(String value){ addProperty("keytype", value); return this;}
	public CubeMessageBody with(String value){ addProperty("", value); return this;}
	public CubeMessageBody withKeyvalue(String value){ addProperty("keyvalue", value); return this;}
	public CubeMessageBody withKeytypeloop(String value){ addProperty("keytypeloop", value); return this;}
	public CubeMessageBody withVersion(String value){ addProperty("version", value); return this;}
	public CubeMessageBody withUpgradetype(String value){ addProperty("upgradetype", value); return this;}
	public CubeMessageBody withControltype(String value){ addProperty("controltype", value); return this;}
	public CubeMessageBody withConfigdata(String value){ addProperty("configdata", value); return this;}
	public CubeMessageBody withConfigtype(String value){ addProperty("configtype", value); return this;}
	public CubeMessageBody withScenarioid(String value){ addProperty("scenarioid", value); return this;}
	public CubeMessageBody withPrimaryid(String value){ addProperty("primaryid", value); return this;}
	public CubeMessageBody withResponseprimaryid(String value){ addProperty("responseprimaryid", value); return this;}
	//public CubeMessageBody withKeytype(String value){ addProperty("Keytype", value); return this;}
	//public CubeMessageBody withKeyvalue(String value){ addProperty("keyvalue", value); return this;}
	public CubeMessageBody withSecurityscenarioid(String value){ addProperty("securityscenarioid", value); return this;}
	//public CubeMessageBody withCallsessionid(String value){ addProperty("callsessionid", value); return this;}
	//public CubeMessageBody withControltype(String value){ addProperty("controltype", value); return this;}
	public CubeMessageBody withCycletype(String value){ addProperty("cycletype", value); return this;}
	public CubeMessageBody withHumidity(String value){ addProperty("humidity", value); return this;}
	public CubeMessageBody withDehumidity(String value){ addProperty("dehumidity", value); return this;}
	public CubeMessageBody withInner(String value){ addProperty("inner", value); return this;}
	public CubeMessageBody withOutside(String value){ addProperty("outside", value); return this;}
	
	
	
}
