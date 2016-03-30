package hwcontrol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.google.gson.Gson;

public class HGW2000Controller {

	public HGW2000Controller() {

		init();

	}

	private Configuration configuration;

	private String hostName;
	private int port;

	private String errorMessage;

	public String viewLog() throws IOException {

		return loadStringFromFile("/home/hi/honeywell.log");

	}

	public String getLastErrorMessage() {
		return errorMessage;
	}

	public Configuration viewCurrentConfiguration() {
		return configuration;
	}

	public void init() {

		try {
			this.loadConfiguration();

		} catch (Exception e) {

			if (e.getMessage() != null) {
				this.errorMessage = e.getMessage();
			}

			if (e.getCause() != null) {
				this.errorMessage += ", caused by: "
						+ e.getCause().getMessage();
			}

		}
	}

	protected void ensureState() throws UnsupportedEncodingException, IOException {

		if (configuration == null) {

			return;
		}

		if (this.token == null) {
			// move out code from here to ensure connect every time

		}
		try {
			this.connectToGateway(configuration.getUsername(),
					configuration.getPassword(),
					configuration.getHostIPAddress(), configuration.getPort(),
					true);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String CONFIG_PATH = "conf/hgw2000.json";

	protected void loadConfiguration() throws IOException {
		File configFile = new File(CONFIG_PATH);

		if (!configFile.exists()) {
			logln("File: " + CONFIG_PATH
					+ " does not exist yet, please connect to hgw host first");
			return;
		}

		String configurationText = loadStringFromFile(CONFIG_PATH);
		Gson gson = new Gson();
		this.configuration = gson.fromJson(configurationText,
				Configuration.class);

	}

	protected void saveCurrentConfiguration() throws IOException {
		if (this.configuration == null) {
			throw new IllegalStateException(
					"Can not save the configuration, current configuration is not load or configured yet");
		}
		Gson gson = new Gson();
		String content = gson.toJson(this.configuration);
		saveStringToFile(content, CONFIG_PATH);

	}

	public void saveConfiguration(String username, String password,
			String hostname, int port) throws IOException {
		if (this.configuration == null) {

			this.configuration = new Configuration();

			// throw new
			// IllegalStateException("Can not save the configuration, current configuration is not load or configured yet");
		}

		this.configuration.setUsername(username);
		this.configuration.setPassword(password);
		this.configuration.setHostIPAddress(hostname);
		this.configuration.setPort(port);

		Gson gson = new Gson();
		String content = gson.toJson(this.configuration);
		saveStringToFile(content, CONFIG_PATH);

	}

	protected void saveStringToFile(String content, String fileName)
			throws IOException {

		BufferedWriter outBuffer = new BufferedWriter(new FileWriter(fileName));
		outBuffer.write(content);
		outBuffer.close();

	}

	protected String loadStringFromFile(String fileName) throws IOException {

		StringBuffer stringBuffer = new StringBuffer();

		BufferedReader in = new BufferedReader(new FileReader(fileName));
		String str;
		while ((str = in.readLine()) != null) {
			stringBuffer.append(str);
			stringBuffer.append(newLine());
		}
		in.close();

		return stringBuffer.toString();

	}

	protected String newLine() {
		return "\r\n";
	}

	InetSocketAddress gatewayAddress;
	private DatagramSocket clientSocket;

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void connectToGateway(String username, String password,
			String hostname, int port, boolean saveConfig)
			throws NoSuchAlgorithmException, UnsupportedEncodingException,
			IOException {

		gatewayAddress = new InetSocketAddress(hostname, port);
		String response = executeCommand("$verify," + username + ","
				+ this.getKeyedDigest(password) + "\n");

		String commands[] = response.split(",");
		int length = commands.length;
		if (length < 2) {
			throw new IllegalStateException(response
					+ " is not contains the right parameters");
		}

		if (!"$verify".equals(commands[0])) {
			throw new IllegalStateException(response + " is not as expected");
		}
		if ("error".equals(commands[1])) {
			throw new IllegalStateException(response
					+ " is not as expected, authentication failed!");
		}
		logln("New token: " + commands[1].trim());
		this.setToken(commands[1].trim());

		if (saveConfig) {
			saveConfiguration(username, password, hostname, port);
		}

	}

	public void connectToGateway(String username, String password,
			String hostname, int port) throws NoSuchAlgorithmException,
			UnsupportedEncodingException, IOException {

		connectToGateway(username, password, hostname, port, false);

	}

	private void logln(String message) {

		System.out.println(message);
	}

	private void log(String message) {

		System.out.print(message);
	}

	/*
	 * 
	 * 4.1.1 灯光设备 灯光设备支持 cfg/ack/req/res 命令,用于控制和查询灯光设备,查询命令仅支持 Maia II 灯光。 ?
	 * 控制命令 cfg 命令格式如下: token$cfg,lig,id,action,on/off,dimmer,0\n ack 命令格式如下:
	 * token$ack,lig,id,action,on/off,dimmer,err 命令各项参数意义如下: id: 灯光设备的 id
	 * 号,从系统数据中获得; action: 4 为单灯控制,5 为打开系统所有灯光设备,6 为关闭系统所有灯光设备; on/off: 开关参数,0
	 * 为关,1 为开;
	 * 
	 * 
	 * dimmer:调光参数,对于 Maia I 的系统来说 128 为增加亮度,129 为减少亮度,255 为无调光 操作 ;对于 Maia II
	 * 系统,系统支持定点调光,可以指定此参数为 0-100 来实现精确控制亮度,0 为 最低亮度,100 为最高亮度。; err: 错误号。 cfg
	 * 命令可以对灯光设备进行的开关和调光控制,当 on/off 项为 0 的时候,dimmer 项无意义, 建议设为 255。ack 命令作为 cfg
	 * 命令的回复,id、action、on/off、dimmer 各项得值均一一对应。 ? 查询命令 req 命令格式如下:
	 * token$req,lig,id,0,0,0,0\n res 命令格式如下:
	 * token$res,lig,id,0,on/off,dimmer,err 命令各项参数意义如下: id: 灯光设备的 id 号,从系统数据中获得;
	 * on/off: 开关参数,0 为关,1 为开; dimmer:调光参数,128 为增加亮度,129 为减少亮度,255 为无调光操作 ; err:
	 * 错误号。
	 */

	public ExecutionResult controlLight(int lightId, int action, int onOrOff,
			int dimmer) throws IOException {

		String requestCommand = this.construstRequest("cfg", "lig", lightId,
				lightId, onOrOff, dimmer, 0);
		// String commands[]=response.split(",");
		// int length=commands.length;
		return getResult(requestCommand);
	}

	public ExecutionResult queryLight(int lightId) throws IOException {

		String requestCommand = this.construstRequest("req", "lig", lightId, 0,
				0, 0, 0);
		// String commands[]=response.split(",");
		// int length=commands.length;
		return getResult(requestCommand);
	}

	public ExecutionResult controlHBusLight(int area, int loop, int action,
			int onOrOff, int dimmer) throws IOException {

		String requestCommand = this.construstRequest("cfg", "hbuslig", area,
				loop, action, onOrOff, dimmer, 0);
		return getResult(requestCommand);

	}

	public ExecutionResult queryHBusLight(int area, int loop)
			throws IOException {

		String requestCommand = this.construstRequest("req", "ac", area, loop,
				0, 0, 0, 0);
		return getResult(requestCommand);

	}

	/*
	 * 4.1.3.1 485 接口空调 485 接口空调设备支持 cfg/ack，req/res 四种命令。其中 cfg/ack
	 * 用于控制设备，req/res 用于查询设备 状态。 ?? 控制命令 cfg 命令格式如下：
	 * token$cfg,ac,id,on/off,mode,fan,dir,temp_set,0,0\n ack 命令格式如下：
	 * token$ack,ac, id,on/off,mode,fan,dir,temp_set,0,err\n 命令各项参数意义如下： id： 空调
	 * id 号，从系统数据中获得； on/off： 开关参数，1 为开, 0 为关； mode： 模式选项，0 为自动模式, 1 风模式, 2
	 * 为制热模式, 3 为制冷模式, 4 为除湿模 式； fan： 风速选项，0 为自动, 1 为低风, 2 为中速风, 3 为高速风； dir：
	 * 风向选项，保留项； temp_set： 设置温度，范围在 16～30 之间； err： 错误号。 cfg 命令中 dir
	 * 项为保留项，系统暂时不支持该选项，设为 255 即可。ack 命令作为 cfg 命令的回
	 * 复，id、on/off、mode、fan、dir、temp_set 各项的值均一一对应。 ?? 查询命令 req 命令格式如下：
	 * token$req,ac,id,0,0,0,0,0,0,0\n res 命令格式如下：
	 * token$res,ac,id,on/off,mode,fan,dir,temp_set,temp_cur,err\n 命令各项参数意义如下：
	 * id： 空调 id 号，从系统数据中获得； on/off： 开关参数，1 为开, 0 为关； Honeywell Honeywell
	 * Shanghai R&D Center Page 19 of 26 mode： 模式选项，0 为自动模式, 1 风模式, 2 为制热模式, 3
	 * 为制冷模式, 4 为除湿模 式； fan： 风速选项，0 为自动, 1 为低风, 2 为中速风, 3 为高速风； dir： 风向选项，保留项；
	 * temp_set： 设置温度，范围在 16～30 之间； temp_cur： 当前温度，范围在 16～30 之间； err： 错误号。 req
	 * 命令表示对指定 id 号的空调设备进行状态查询。res 命令中作为 req 命令的回复，返回
	 * on/off、mode、fan、dir、temp_set、temp_cur 等项的当前值，其中 dir 返回 255。
	 */
	public ExecutionResult controlAirCondition(int id, int onOrOff, int mode,
			int fan, int windDirection, int tempToSet) throws IOException {

		String requestCommand = this.construstRequest("cfg", "ac", id, onOrOff,
				mode, fan, windDirection, tempToSet, 0, 0);
		return getResult(requestCommand);

	}

	public ExecutionResult queryAirCondition(int id) throws IOException {

		String requestCommand = this.construstRequest("req", "ac", id, 0, 0, 0,
				0, 0, 0, 0);
		return getResult(requestCommand);

	}

	/*
	 * 4.1.3.2 红外空调 红外空调仅支持 cfg/ack 命令，不支持对设备状态的查询。 控制命令 cfg 命令格式如下：
	 * token$cfg,ac,id,irid ,0\n ack 命令格式如下： token$ack,ac,id,irid,err\n
	 * 命令各项参数意义如下： id： 空调设备的 id 号, 从系统数据中获得； irid： 空调红外模式 id 号，从系统数据中获得； err：
	 * 错误号。 ack 命令作为 cfg 命令的回复，id、irid 项的值一一对应
	 */

	public ExecutionResult controlInfradRedAirCondition(int id, int irid)
			throws IOException {

		String requestCommand = this.construstRequest("cfg", "ac", id, irid, 0);
		return getResult(requestCommand);

	}

	/*
	 * 
	 * 4.1.4 地暖设备 地暖设备支持 cfg/ack，req/res 四种命令。其中 cfg/ack 用于控制设备，req/res
	 * 用于查询设备状态。 ?? 控制命令 cfg 命令格式如下： token$cfg,ufh,id,on/off,temp_set,0,0\n ack
	 * 命令格式如下： token$ack,ufh,id,on/off,temp_set,0,0\n 命令各项参数意义如下： id： 地暖设备的 id
	 * 号，从系统数据中获得； on/off： 开关参数，0 为关，1 为开； temp_set： 设置温度，范围在 16～30 之间； err：
	 * 错误号。 ack 命令作为 cfg 命令的回复，id、on/off，temp_set 项的值一一对应。 ?? 查询命令 req 命令格式如下：
	 * token$req,ufh,id,0,0,0,0\n Honeywell Honeywell Shanghai R&D Center Page
	 * 20 of 26 res 命令格式如下： token$res,ufh,id,on/off,temp_set,temp_cur,err\n
	 * 命令各项参数意义如下： id： 地暖设备的 id 号，从系统数据中获得； on/off： 开关参数，0 为关，1 为开； temp_set：
	 * 设置温度，范围在 16～30 之间； temp_cur： 当前温度，范围在 16～30 之间； err： 错误号。 req 命令表示对指定 id
	 * 号的地暖设备进行状态查询。res 命令中作为 req 命令的回复，返回 on/off、temp_set、temp_cur 等项的当前值。
	 */

	public ExecutionResult controlUFHeat(int id, int onOrOff, int tempToSet)
			throws IOException {

		String requestCommand = this.construstRequest("cfg", "ufh", id,
				onOrOff, tempToSet, 0, 0);
		return getResult(requestCommand);

	}

	public ExecutionResult queryUFHeat(int id) throws IOException {

		String requestCommand = this.construstRequest("req", "ufh", id, 0, 0,
				0, 0);
		return getResult(requestCommand);

	}

	/*
	 * 
	 * 4.1.5 开关 霍尼韦尔智能家居系统支持四个开关，可用来接入新风、热水、煤气等设备，控制这些设备的开 关。开关支持
	 * cfg/ack，req/res 四种命令。其中 cfg/ack 用于控制开关，req/res 用于查询开关状态。命令 各项参数意义如下： id：
	 * 开关设备的 id 号，从系统数据中获得； on/off： 开关参数，0 为关，1 为开； err： 错误号； ?? 控制命令 cfg
	 * 命令格式如下： token$cfg,relay,id,on/off,0,0,0,0\n ack 命令格式如下：
	 * token$ack,relay,id,on/off,0,0,0,err\n 命令各项参数意义如下： id： 开关设备的 id
	 * 号，从系统数据中获得； on/off： 开关参数，0 为关，1 为开； err： 错误号； 开关的控制命令中，置为 0
	 * 的项为保留项，暂无定义。ack 命令作为 cfg 命令的回复，id、on/off 项的值一一对应。 ?? 查询命令 req 命令格式如下：
	 * token$req,relay,id,0,0,0,0,0\n res 命令格式如下：
	 * token$res,relay,id,on/off,0,0,0,err\n 查询命令中，置为 0 的项为保留项，暂无定义。req 命令表示对指定
	 * id 号的开关进行状态查 询。res 命令中作为 req 命令的回复，返回 on/off 项的当前值。
	 */
	public ExecutionResult controlRelay(int id, int onOrOff) throws IOException {

		String requestCommand = this.construstRequest("cfg", "relay", id,
				onOrOff, 0, 0, 0, 0);
		return getResult(requestCommand);

	}

	public ExecutionResult queryRelay(int id) throws IOException {

		String requestCommand = this.construstRequest("req", "relay", id, 0, 0,
				0, 0, 0);
		return getResult(requestCommand);

	}

	/*
	 * 
	 * 4.1.6 窗帘设备 窗帘设备支持 cfg/ack/req/res 命令，用于控制窗帘设备，查询命令仅支持窗帘设备。 Honeywell
	 * Honeywell Shanghai R&D Center Page 21 of 26 ?? 控制命令 cfg 命令格式如下：
	 * token$cfg,curtain,id,action,on/off,position,0\n ack 命令格式如下： token$ack,
	 * curtain,id,action,on/off,position,err\n 命令各项参数意义如下： id： 窗帘设备的 id
	 * 号，从系统数据中获得； action：4 为单个窗帘控制，5 为打开系统所有窗帘设备，6 为关闭系统所有窗帘设备； on/off： 开关参数，0
	 * 为关，1 为开，2 为停（仅对 MaiaII） position: err： 错误号。 ack 命令作为 cfg
	 * 命令的回复，id、action、on/off 项的值一一对应。 ?? 查询命令 req 命令格式如下：
	 * token$req,curtain,id,0,0,0,0\n res 命令格式如下：
	 * token$res,curtain,id,0,on/off,position,err\n 命令各项参数意义如下： id： 窗帘设备的 id
	 * 号，从系统数据中获得； on/off： 开关参数，0 为关，1 为开； position： err： 错误号。
	 */

	public ExecutionResult controlCurtain(int id, int action, int onOrOff,
			int position) throws IOException {

		String requestCommand = this.construstRequest("cfg", "curtain", id,
				action, onOrOff, position, 0);
		return getResult(requestCommand);

	}

	public ExecutionResult queryCurtain(int id) throws IOException {

		String requestCommand = this.construstRequest("req", "curtain", id, 0,
				0, 0, 0);
		return getResult(requestCommand);

	}

	/*
	 * 4.1.7 HBUS 窗帘设备 窗帘设备支持 cfg/ack/req/res 命令，用于控制窗帘设备，查询命令仅支持窗帘设备。 ?? 控制命令
	 * cfg 命令格式如下： token$cfg,hbuscurtain,area,loop,action,on/off,position,0\n
	 * ack 命令格式如下： token$ack, hbuscurtain,area,loop,action,on/off,position,err\n
	 * 命令各项参数意义如下： id： 窗帘设备的 id 号，从系统数据中获得； action：4 为单个窗帘控制，5 为打开该区域所有窗帘设备，6
	 * 为关闭系统所有窗帘设备； on/off： 开关参数，0 为关，1 为开，2 为停 position: err： 错误号。 ack 命令作为 cfg
	 * 命令的回复，id、action、on/off 项的值一一对应。 ?? 查询命令 req 命令格式如下： Honeywell Honeywell
	 * Shanghai R&D Center Page 22 of 26
	 * token$req,hbuscurtain,area,loop,0,0,0,0\n res 命令格式如下：
	 * token$res,hbuscurtain,area,loop,0,on/off,position,err\n 命令各项参数意义如下： id：
	 * 窗帘设备的 id 号，从系统数据中获得； on/off： 开关参数，0 为关，1 为开； position： err： 错误号。
	 */

	public ExecutionResult controlHBusCurtain(int area, int loop, int action,
			int onOrOff, int position) throws IOException {

		String requestCommand = this.construstRequest("cfg", "hbuscurtain",
				area, loop, action, onOrOff, position, 0);
		return getResult(requestCommand);

	}

	public ExecutionResult queryHBusCurtain(int area, int loop)
			throws IOException {

		String requestCommand = this.construstRequest("req", "hbuscurtain",
				area, loop, 0, 0, 0, 0);
		return getResult(requestCommand);

	}

	public ExecutionResult getResult(String requestCommand) throws IOException {
		ExecutionResult result = new ExecutionResult();
		result.setSentCommand(requestCommand);
		String response = retryCommand(requestCommand);

		result.setReceivedResponse(response);
		return result;

	}

	/*
	 * 4.1.8 红外设备 红外设备支持 cfg/ack 命令，用于控制红外设备，该类型设备不支持查询命令。 ?? 控制命令 cfg 命令格式如下：
	 * token$cfg,ir,devid,modid,0\n ack 命令格式如下： token$ack, ir,devid,modid,err\n
	 * 命令各项参数意义如下： devid： 红外设备的 id 号，从系统数据中获得； modid：红外设备模式号，从系统数据中获得； err： 错误号。
	 * ack 命令作为 cfg 命令的回复，各项的值一一对应。
	 */

	public ExecutionResult controlInfraRedDevice(int devid, int modid)
			throws IOException {

		String requestCommand = this.construstRequest("cfg", "ir", devid,
				modid, 0);
		return getResult(requestCommand);

	}

	/*
	 * 
	 * 4.1.9 Wifi2IR 设备 Wifiir 设备支持 cfg/ack 命令，用于控制无线红外设备，该类型设备不支持查询命令。 ?? 控制命令
	 * cfg 命令格式如下： token$cfg,wifi2ir,moduleid,keyindex,delay\n ack 命令格式如下：
	 * token$ack,wifi2ir,moduleid, keyindex,err\n 命令各项参数意义如下： Moduleid: wifiir
	 * 设备的模块 id Keyindex：wifiir 设备的键的 index Delay: 发送红外信号的延迟时间，默认使用 5 Err: 错误号
	 */

	public ExecutionResult controlWifi2IR(int moduleid, int keyindex, int delay)
			throws IOException {

		String requestCommand = this.construstRequest("cfg", "wifi2ir",
				moduleid, keyindex, delay);
		return getResult(requestCommand);

	}

	/*
	 * 4.1.10 背景音乐设备 背景音乐支持 cfg/ack/req/res 命令，用于控制和查询背景音乐设备。 ?? 控制命令 cfg
	 * 命令格式如下： token$cfg,music,area,status,vol,cmd,para,0\n ack 命令格式如下：
	 * token$ack,music,area,status,vol,cmd,para,err 命令各项参数意义如下： area：
	 * 背景音乐播放的区域号； status： 1 stop, 2 play, 3 pause, -1 此参数不处理； vol： 音量大小，范围
	 * 0~31，-1 此参数不处理； cmd： 其它背景音命令； -1：不处理 1：配合 para，当 para=1 表示上一首，para=0
	 * 表示下一首 para： 其它背景音命令对应参数(cmd 对应的参数)；
	 */

	public ExecutionResult controlMusic(int area, int status, int volumn,
			int command, int parameter) throws IOException {

		String requestCommand = this.construstRequest("cfg", "music", area,
				status, volumn, command, parameter, 0);
		return getResult(requestCommand);

	}

	public ExecutionResult queryMusic(int area) throws IOException {

		String requestCommand = this.construstRequest("req", "music", area, 0,
				0, 0, 0, 0, 0, 0);
		return getResult(requestCommand);

	}

	/*
	 * 4.1.11 传感器 传感器支持 req/res 命令，用于传感器状态查询。 req 命令格式如下：
	 * token$req,sensor,area,loop,sensortype,subnetid,deviceid,logicnum\n res
	 * 命令格式如下：
	 * token$res,sensor,area,loop,sensortype,d1,d2,d3,d4,d5,d6,d7,d8,d9,0\n
	 * 命令各项参数意义如下： area： 传感器对应的区域号； loop： 传感器对应回路； sensortype： 传感器类型 1 8in1
	 * DeviceType315, 2 8in1 DeviceType314, 3 12in1, 4 SensorInOne； subnetid：
	 * 传感器的子网 ID； deviceid： 传感器的设备 ID； logicnum： 传感器的逻辑号；
	 * d1,d2,d3,d4,d5,d6,d7,d8,d9 跟 sensortype 相关，如下： 1 8in1 DeviceType315 干节点 1
	 * 状态，干节点 2 状态，0，动静传感器，0，0，延迟时间高位，延迟时间低位，0 3 12in1
	 * 成功或者失败，当前温度，亮度高位，亮度低位，动静传感器，超声波，干节点 1，干节点 2，0 4 SensorInOne
	 * 当前温度，亮度高位，亮度低位，湿度，空气传感器，煤气传感器，动静传感器，干节点 1，干节点 2 4.1.12 错误号 设备命令中的 err
	 * 项为错误号，该项的值是统一规定的，具体定义如下： ?? 0：设备正常； ?? 1：设备访问失败； ?? 2：
	 * 状态未知(例如控制无反馈设备，则不会有明确的成功与失败)； ?? 128：发送命令失败； Honeywell Honeywell Shanghai
	 * R&D Center Page 25 of 26 ?? 129：设备返回值错误； ?? 130： 命令超时； ?? 131： 命令解析错误； ??
	 * 132：设备故障； ?? 133：总线出错； ?? 134：设备掉线
	 * 
	 * 
	 * 传感器支持 req/res 命令，用于传感器状态查询。 req 命令格式如下：
	 * token$req,sensor,area,loop,sensortype,subnetid,deviceid,logicnum\n res
	 * 命令格式如下：
	 * token$res,sensor,area,loop,sensortype,d1,d2,d3,d4,d5,d6,d7,d8,d9,0\n
	 */

	public ExecutionResult querySensor(int area, int loop, int sensortype,
			int subnetid, int deviceid, int logicNumber) throws IOException {

		String requestCommand = this.construstRequest("req", "sensor", area,
				loop, sensortype, subnetid, deviceid, logicNumber);
		return getResult(requestCommand);

	}

	/*
	 * 4.2.1 场景 霍尼韦尔智能家居系统支持场景模式，场景模式是灯光、窗帘、空调模式以及防区布撤防等设置的
	 * 组合。场景模式被预先配置在系统数据库中，用户可以从系统数据中获得所有场景 id 号。 场景支持 cfg/ack，req/res 四种命令。其
	 * cfg/ack 用于控制场景，req/res 用于查询当前场景(全局场景 应该使用 trigger 触发器进行查询)。 ?? 控制命令 cfg
	 * 命令格式如下： token$cfg,scenario,sid,1\n ack 命令格式如下：
	 * token$ack,scenario,sid,result\n 命令各项参数意义如下： sid： 场景 id 号，从系统数据中获得；
	 * result: 控制结果，1 为成功，其他值为失败。 cfg 命令表示应用指定 id 号的场景，ack 命令作为 cfg 命令的回复，sid
	 * 项的值与 cfg 命令对应， result 项的值表示控制结果。 ?? 查询命令 req 命令格式如下：
	 * token$req,scenario,areaid,1\n res 命令格式如下： token$res,scenario,sid,result\n
	 * 命令各项参数意义如下： sid： 场景 id 号，从系统数据中获得； areaid: 区域 id 号，从系统数据中获得； result:
	 * 控制结果，1 为成功，其他值为失败。 场景查询命令是用来查询某个区域应用的场景，req 命令中 areaid 项为需要查询的区域号，res
	 * 命令中的 sid 回复查询到的场景号，如果该区域没有应用任何场景，此项值为 no。
	 */

	public ExecutionResult controlScenario(int scenarioId) throws IOException {

		String requestCommand = this.construstRequest("cfg", "scenario",
				scenarioId, 1);
		return getResult(requestCommand);

	}

	public ExecutionResult queryScenario(int areaid) throws IOException {

		String requestCommand = this.construstRequest("req", "scenario",
				areaid, 0);
		return getResult(requestCommand);

	}

	/*
	 * 4.2.2 触发器 触发器用于触发舒适日历功能，系统可以按照预先设定的时间触发相应的场景，查询触发器也用来 查询全局场景信息。 触发器支持
	 * cfg/ack，req/res 四种命令。其中 cfg/ack 用于控制触发器，req/res 用于查询触发器状 态。触发器本身没有 id
	 * 号，但为了保持命令格式的统一，由后台程序自动生成的一个 0～99 之间的随 机整数作为触发器 id 号。 ?? 控制命令 cfg 命令格式如下：
	 * token$cfg,trigger,id,on/off\n ack 命令格式如下： token$ack,trigger,id,result\n
	 * 命令各项参数意义如下： id： 触发器 id 号； on/off: 0 为关闭触发器，1 为打开触发器； result: 控制结果，1
	 * 为成功，其他值为失败。 cfg 命令控制打开或关闭触发器，ack 命令作为 cfg 命令的回复，id 项的值与 cfg 命令对应， result
	 * 项的值为控制结果。 ?? 查询命令 req 命令格式如下： token$req,trigger,id,0,0\n res 命令格式如下：
	 * token$res,trigger,id,result,sid \n 命令各项参数意义如下： id： 触发器 id 号； sid： 场景 id
	 * 号，系统数据； result: 控制结果，1 为成功，其他值为失败。 req 命令中随机生成触发器 id 号，res 命令将在 status
	 * 项中回复触发器当前状态，在 sid 项中回复 当前系统场景 id 号，如果没有应用场景，则 id 号为 0
	 */

	public ExecutionResult controlTriger(int id, int onOrOff)
			throws IOException {

		String requestCommand = this.construstRequest("cfg", "triger", id,
				onOrOff);
		return getResult(requestCommand);

	}

	public ExecutionResult queryTriger(int id) throws IOException {

		String requestCommand = this
				.construstRequest("req", "triger", id, 0, 0);
		return getResult(requestCommand);

	}

	private Object lock = new Object();

	private String retryCommand(String command) throws IOException {
		
		synchronized (lock) {
			if(this.token==null){
				this.ensureState();
			}
			final int count = 3;
			for (int i = 0; i < count; i++) {
				try {
					
					
					String response = executeCommand(command);
					if (response.startsWith("$verify,error")) {

						logln("发现会话过期，重新执行认证过程!");
						this.ensureState();
						continue; 
					}
					return response;

				} catch (SocketTimeoutException e) {
					logln("发现请求超时，试图重新认证网关......");
					this.ensureState();
					continue;
				}

			}

			logln("重试的次数超过 " + count + " 次，放弃 ");
			throw new IOException("重试的次数超过 " + count
					+ " 次，只能放弃，请检查HoneyWell网关是否开启，网线是否连上，有电没电");
		}

	}

	private String executeCommand(String command) throws IOException {


		String commandWithToken=this.getToken()+command;
		
		if(command.startsWith("$verify")){
			commandWithToken=command;
		}
		log("Executing command: " + commandWithToken);
		
		byte[] sendContent = commandWithToken.getBytes();
		int length = sendContent.length;

		send(this.gatewayAddress, sendContent, length);
		log("TX -> " +gatewayAddress.getHostString()+":"+gatewayAddress.getPort()+": "+ commandWithToken);

		byte[] receiveContent = receive(this.gatewayAddress);
		String response = new String(receiveContent, "UTF-8");
		log("RX <- " +gatewayAddress.getHostString()+":"+gatewayAddress.getPort()+": " + response);
		
		return response;

	}



	public DatagramSocket getSocket() throws SocketException {
		
		if (this.clientSocket == null) {
			this.clientSocket = new DatagramSocket();
		}
		if (this.clientSocket.isClosed()) {
			this.clientSocket = new DatagramSocket();
		}
		clientSocket.setSoTimeout(18000);
		//clientSocket.set
		return this.clientSocket;
		
		/*
		
		
		this.clientSocket = new DatagramSocket();
		clientSocket.setSoTimeout(6000);
		return this.clientSocket;
		
		*
		*
		*/
		

	}

	protected void send(InetSocketAddress dst, byte[] inBuffer, int len)
			throws IOException {

		DatagramSocket socket = getSocket();
		DatagramPacket request = new DatagramPacket(inBuffer, len, dst);
		socket.send(request);

	}

	protected byte[] receive(InetSocketAddress dst) throws IOException {

		DatagramSocket socket = this.clientSocket;
		byte[] inbuf = new byte[1500];
		DatagramPacket packet = new DatagramPacket(inbuf, inbuf.length, dst);
		socket.receive(packet);

		int numBytesReceived = packet.getLength();

		if (numBytesReceived <= 0) {
			// socket.close();
			throw new IllegalStateException(
					"Txpect to receive echo from the gateway");
		}

		return Arrays.copyOf(inbuf, numBytesReceived);

	}

	public String construstRequest(String command, Object... parameters) throws UnsupportedEncodingException, IOException {
		StringBuffer stringBuffer = new StringBuffer(100);
		
		//stringBuffer.append(this.token);
		stringBuffer.append("$" + command);
		for (Object o : parameters) {
			stringBuffer.append(",");
			stringBuffer.append(o) ;

		}

		stringBuffer.append("\n");
		return stringBuffer.toString();
		// this.sendCommand(command);

	}

	public VersionInfo versionInfo() throws Exception {
		VersionInfo info = new VersionInfo();
		info.setVersion("0.06");
		return info;
	}

	public String getKeyedDigest(String password)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {

		if (password == null) {
			throw new IllegalArgumentException("The password can not be null");
		}
		if (password.length() == 0) {
			throw new IllegalArgumentException("The password can not be empty");
		}
		if (password.trim().length() == 0) {
			throw new IllegalArgumentException(
					"The password can not be emptry after trimed.");
		}

		byte[] utf16leBytes = password.getBytes("UTF-16LE");
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(utf16leBytes);
		byte[] digistedBytes = md5.digest();
		return toHexString(digistedBytes);

	}

	private static String toHexString(byte[] fieldData) {
		StringBuilder resultBuffer = new StringBuilder();
		for (int i = 0; i < fieldData.length; i++) {
			int v = (fieldData[i] & 0xFF);
			if (v <= 0xF) {
				resultBuffer.append("0");
			}
			resultBuffer.append(Integer.toHexString(v));
		}
		return resultBuffer.toString();
	}

	private String token;

	public void close() {

		if (this.clientSocket == null) {
			return;
		}
		this.clientSocket.close();
	}
}
