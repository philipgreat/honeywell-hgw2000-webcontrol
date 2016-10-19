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
	 * 4.1.1 �1�7�0�5�1�7�1�7�� �1�7�0�5�1�7�1�7��0�1�1�7�1�7 cfg/ack/req/res �1�7�1�7�1�7�1�7,�1�7�1�7�1�7�1�1�1�7�1�7�0�6�0�0�1�7�0�9�1�7�0�5�1�7�1�7��,�1�7�1�7�0�9�1�7�1�7�1�7�1�7�1�7�0�1�1�7�1�7 Maia II �1�7�0�5�7�3 ?
	 * �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7 cfg �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�1�7: token$cfg,lig,id,action,on/off,dimmer,0\n ack �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�1�7:
	 * token$ack,lig,id,action,on/off,dimmer,err �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7: id: �1�7�0�5�1�7�1�7��1�7�1�7 id
	 * �1�7�1�7,�1�7�1�7�0�3�0�1�1�7�1�7�1�7�1�7�1�7�݄1�7�1�7; action: 4 �0�2�1�7�1�7�1�7�0�1�1�7�1�7�1�7,5 �0�2�1�7�1�7�0�3�0�1�1�7�1�7�1�7�ց0�5�1�7�1�7��,6 �0�2�1�7�1�9�1�7�0�3�0�1�1�7�1�7�1�7�ց0�5�1�7�1�7��; on/off: �1�7�1�7�1�7�1�0�1�7�1�7�1�7,0
	 * �0�2�1�7�1�7,1 �0�2�1�7�1�7;
	 * 
	 * 
	 * dimmer:�1�7�1�7�1�7�1�7�1�7�1�7�1�7,�1�7�1�7�1�7�1�7 Maia I �1�7�1�7�0�3�0�1�1�7�1�7�0�5 128 �0�2�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7,129 �0�2�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7,255 �0�2�1�7�1�7�1�7�1�7�1�7 �1�7�1�7�1�7�1�7 ;�1�7�1�7�1�7�1�7 Maia II
	 * �0�3�0�1,�0�3�0�1�0�1�1�7�0�6�1�7�1�7�1�7�1�7�1�7�1�7,�1�7�1�7�1�7�1�7�0�8�1�7�1�7�1�7�0�2�1�7�1�7�1�7�0�2 0-100 �1�7�1�7�0�6�1�7�0�4�1�7�0�2�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7,0 �0�2 �1�7�1�7�1�7�1�7�1�7�1�7�1�7,100 �0�2�1�7�1�7�1�7�1�7�1�7�1�7�0�0�1�7; err: �1�7�1�7�1�7�1�7�0�8�1�7 cfg
	 * �1�7�1�7�1�7�1�7�1�7�1�7�0�8�0�7�0�5�1�7�1�7��1�7�1�7�1�7�ց0�7�1�7�1�7�1�8�0�3�1�7�1�7�1�7�1�7�1�7�1�7,�1�7�1�7 on/off �1�7�1�7�0�2 0 �1�7�1�7�0�2�1�7�1�7,dimmer �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7, �1�7�1�7�1�7�1�7�1�7�1�7�0�2 255�1�7�1�7ack �1�7�1�7�1�7�1�7�1�7�1�7�0�2 cfg
	 * �1�7�1�7�1�7�1�7�0�3�1�6�1�7,id�1�7�1�7action�1�7�1�7on/off�1�7�1�7dimmer �1�7�1�7�1�7�1�7�1�7�0�5�1�7�1�7�0�5�0�5�1�7�1�7�0�8�1�7�1�7 ? �1�7�1�7�0�9�1�7�1�7�1�7�1�7 req �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�1�7:
	 * token$req,lig,id,0,0,0,0\n res �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�1�7:
	 * token$res,lig,id,0,on/off,dimmer,err �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7: id: �1�7�0�5�1�7�1�7��1�7�1�7 id �1�7�1�7,�1�7�1�7�0�3�0�1�1�7�1�7�1�7�1�7�1�7�݄1�7�1�7;
	 * on/off: �1�7�1�7�1�7�1�0�1�7�1�7�1�7,0 �0�2�1�7�1�7,1 �0�2�1�7�1�7; dimmer:�1�7�1�7�1�7�1�7�1�7�1�7�1�7,128 �0�2�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7,129 �0�2�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7,255 �0�2�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7 ; err:
	 * �1�7�1�7�1�7�1�7�0�8�1�7
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
	 * 4.1.3.1 485 �1�7�0�3�1�1�0�1�1�7 485 �1�7�0�3�1�1�0�1�1�7�1�7��0�1�1�7�1�7 cfg/ack�1�7�1�7req/res �1�7�1�7�1�7�1�7�1�7�1�7�1�7�6�9�1�7�1�7�1�7�1�7 cfg/ack
	 * �1�7�1�7�1�7�1�1�1�7�1�7�1�7�1�7��1�7�1�7req/res �1�7�1�7�1�7�1�8�1�7�0�9�1�7�� �0�8�0�0�1�7�1�7 ?? �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7 cfg �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7
	 * token$cfg,ac,id,on/off,mode,fan,dir,temp_set,0,0\n ack �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7
	 * token$ack,ac, id,on/off,mode,fan,dir,temp_set,0,err\n �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�5�1�7 id�1�7�1�7 �1�7�0�1�1�7
	 * id �1�7�0�0�1�7�1�7�1�7�0�3�0�1�1�7�1�7�1�7�1�7�1�7�݄1�7�0�0�1�7 on/off�1�7�1�7 �1�7�1�7�1�7�1�0�1�7�1�7�1�7�1�7�1�71 �0�2�1�7�1�7, 0 �0�2�1�7�1�5�1�7 mode�1�7�1�7 �0�0�0�4�0�5�1�7�6�50 �0�2�1�7�0�8�1�7�0�0�0�4, 1 �1�7�1�7�0�0�0�4, 2
	 * �0�2�1�7�1�7�1�7�1�7�0�0�0�4, 3 �0�2�1�7�1�7�1�7�1�7�0�0�0�4, 4 �0�2�1�7�1�7�0�5�0�0 �0�4�1�7�1�7 fan�1�7�1�7 �1�7�1�7�1�7�1�7�0�5�1�7�6�50 �0�2�1�7�0�8�1�7, 1 �0�2�1�7�0�5�1�7, 2 �0�2�1�7�1�7�1�7�1�9�1�7, 3 �0�2�1�7�1�7�1�7�1�9�� dir�1�7�1�7
	 * �1�7�1�7�1�7�1�7�0�5�1�7�6�5�1�7�1�7�1�7�1�7�1�7�6�0 temp_set�1�7�1�7 �1�7�1�7�1�7�1�7�1�7�0�9�0�2�1�7�1�7�1�7���1�7�1�7 16�1�7�1�730 �0�8�1�7�4�3 err�1�7�1�7 �1�7�1�7�1�7�1�7�0�8�1�7 cfg �1�7�1�7�1�7�1�7�1�7�1�7 dir
	 * �1�7�1�7�0�2�1�7�1�7�1�7�1�7�1�7�6�5�0�3�0�1�1�7�1�7�0�2�1�7�1�7�0�1�1�7�0�8�1�7�0�5�1�7�6�5�1�7�1�7�0�2 255 �1�7�1�7�1�7���1�7ack �1�7�1�7�1�7�1�7�1�7�1�7�0�2 cfg �1�7�1�7�1�7�1�7�0�3�1�7
	 * �1�7�1�7�1�7�1�7id�1�7�1�7on/off�1�7�1�7mode�1�7�1�7fan�1�7�1�7dir�1�7�1�7temp_set �1�7�1�7�1�7�1�7�1�7�0�5�1�7�1�7�0�5�0�5�1�7�1�7�0�8�1�7�1�7 ?? �1�7�1�7�0�9�1�7�1�7�1�7�1�7 req �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7
	 * token$req,ac,id,0,0,0,0,0,0,0\n res �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7
	 * token$res,ac,id,on/off,mode,fan,dir,temp_set,temp_cur,err\n �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�5�1�7
	 * id�1�7�1�7 �1�7�0�1�1�7 id �1�7�0�0�1�7�1�7�1�7�0�3�0�1�1�7�1�7�1�7�1�7�1�7�݄1�7�0�0�1�7 on/off�1�7�1�7 �1�7�1�7�1�7�1�0�1�7�1�7�1�7�1�7�1�71 �0�2�1�7�1�7, 0 �0�2�1�7�1�5�1�7 Honeywell Honeywell
	 * Shanghai R&D Center Page 19 of 26 mode�1�7�1�7 �0�0�0�4�0�5�1�7�6�50 �0�2�1�7�0�8�1�7�0�0�0�4, 1 �1�7�1�7�0�0�0�4, 2 �0�2�1�7�1�7�1�7�1�7�0�0�0�4, 3
	 * �0�2�1�7�1�7�1�7�1�7�0�0�0�4, 4 �0�2�1�7�1�7�0�5�0�0 �0�4�1�7�1�7 fan�1�7�1�7 �1�7�1�7�1�7�1�7�0�5�1�7�6�50 �0�2�1�7�0�8�1�7, 1 �0�2�1�7�0�5�1�7, 2 �0�2�1�7�1�7�1�7�1�9�1�7, 3 �0�2�1�7�1�7�1�7�1�9�� dir�1�7�1�7 �1�7�1�7�1�7�1�7�0�5�1�7�6�5�1�7�1�7�1�7�1�7�1�7�6�0
	 * temp_set�1�7�1�7 �1�7�1�7�1�7�1�7�1�7�0�9�0�2�1�7�1�7�1�7���1�7�1�7 16�1�7�1�730 �0�8�1�7�4�3 temp_cur�1�7�1�7 �1�7�1�7�0�2�1�7�0�9�0�2�1�7�1�7�1�7���1�7�1�7 16�1�7�1�730 �0�8�1�7�4�3 err�1�7�1�7 �1�7�1�7�1�7�1�7�0�8�1�7 req
	 * �1�7�1�7�1�7�1�7�1�7�0�5�1�7�1�7�0�8�1�7�1�7 id �1�7�0�7�0�7�0�1�1�7�1�7��1�7�1�7�1�7�1�7�0�8�0�0�1�7�1�7�0�9�1�7�1�7res �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�2 req �1�7�1�7�1�7�1�7�0�3�1�6�1�7�1�7�1�7�1�7�1�7�1�7�1�7
	 * on/off�1�7�1�7mode�1�7�1�7fan�1�7�1�7dir�1�7�1�7temp_set�1�7�1�7temp_cur �1�7�1�7�1�7�1�7�0�7�1�7�0�2�0�5�1�7�1�7�1�7�1�7�1�7�1�7 dir �1�7�1�7�1�7�1�7 255�1�7�1�7
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
	 * 4.1.3.2 �1�7�1�7�1�7�1�7�0�1�1�7 �1�7�1�7�1�7�1�7�0�1�1�7�1�7�1�7�0�1�1�7�1�7 cfg/ack �1�7�1�7�1�7�6�5�1�7�1�7�0�1�1�7�0�6�1�7�1�7��0�8�0�0�1�7�0�4�1�7�0�9�1�7�1�7 �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7 cfg �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7
	 * token$cfg,ac,id,irid ,0\n ack �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7 token$ack,ac,id,irid,err\n
	 * �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�5�1�7 id�1�7�1�7 �1�7�0�1�1�7�1�7��1�7�1�7 id �1�7�1�7, �1�7�1�7�0�3�0�1�1�7�1�7�1�7�1�7�1�7�݄1�7�0�0�1�7 irid�1�7�1�7 �1�7�0�1�1�7�1�7�1�7�1�7�1�7�0�0�0�4 id �1�7�0�0�1�7�1�7�1�7�0�3�0�1�1�7�1�7�1�7�1�7�1�7�݄1�7�0�0�1�7 err�1�7�1�7
	 * �1�7�1�7�1�7�1�7�0�8�1�7 ack �1�7�1�7�1�7�1�7�1�7�1�7�0�2 cfg �1�7�1�7�1�7�1�7�0�3�1�6�1�7�1�7�1�7id�1�7�1�7irid �1�7�1�7�1�7�0�5�0�5�0�5�1�7�1�7�0�8
	 */

	public ExecutionResult controlInfradRedAirCondition(int id, int irid)
			throws IOException {

		String requestCommand = this.construstRequest("cfg", "ac", id, irid, 0);
		return getResult(requestCommand);

	}

	/*
	 * 
	 * 4.1.4 �1�7�1�7�0�1�1�7�� �1�7�1�7�0�1�1�7��0�1�1�7�1�7 cfg/ack�1�7�1�7req/res �1�7�1�7�1�7�1�7�1�7�1�7�1�7�6�9�1�7�1�7�1�7�1�7 cfg/ack �1�7�1�7�1�7�1�1�1�7�1�7�1�7�1�7��1�7�1�7req/res
	 * �1�7�1�7�1�7�1�8�1�7�0�9�1�7��0�8�0�0�1�7�1�7 ?? �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7 cfg �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7 token$cfg,ufh,id,on/off,temp_set,0,0\n ack
	 * �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7 token$ack,ufh,id,on/off,temp_set,0,0\n �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�5�1�7 id�1�7�1�7 �1�7�1�7�0�1�1�7��1�7�1�7 id
	 * �1�7�0�0�1�7�1�7�1�7�0�3�0�1�1�7�1�7�1�7�1�7�1�7�݄1�7�0�0�1�7 on/off�1�7�1�7 �1�7�1�7�1�7�1�0�1�7�1�7�1�7�1�7�1�70 �0�2�1�7�1�5�1�71 �0�2�1�7�1�7�1�7�1�7 temp_set�1�7�1�7 �1�7�1�7�1�7�1�7�1�7�0�9�0�2�1�7�1�7�1�7���1�7�1�7 16�1�7�1�730 �0�8�1�7�4�3 err�1�7�1�7
	 * �1�7�1�7�1�7�1�7�0�8�1�7 ack �1�7�1�7�1�7�1�7�1�7�1�7�0�2 cfg �1�7�1�7�1�7�1�7�0�3�1�6�1�7�1�7�1�7id�1�7�1�7on/off�1�7�1�7temp_set �1�7�1�7�1�7�0�5�0�5�0�5�1�7�1�7�0�8�1�7�1�7 ?? �1�7�1�7�0�9�1�7�1�7�1�7�1�7 req �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7
	 * token$req,ufh,id,0,0,0,0\n Honeywell Honeywell Shanghai R&D Center Page
	 * 20 of 26 res �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7 token$res,ufh,id,on/off,temp_set,temp_cur,err\n
	 * �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�5�1�7 id�1�7�1�7 �1�7�1�7�0�1�1�7��1�7�1�7 id �1�7�0�0�1�7�1�7�1�7�0�3�0�1�1�7�1�7�1�7�1�7�1�7�݄1�7�0�0�1�7 on/off�1�7�1�7 �1�7�1�7�1�7�1�0�1�7�1�7�1�7�1�7�1�70 �0�2�1�7�1�5�1�71 �0�2�1�7�1�7�1�7�1�7 temp_set�1�7�1�7
	 * �1�7�1�7�1�7�1�7�1�7�0�9�0�2�1�7�1�7�1�7���1�7�1�7 16�1�7�1�730 �0�8�1�7�4�3 temp_cur�1�7�1�7 �1�7�1�7�0�2�1�7�0�9�0�2�1�7�1�7�1�7���1�7�1�7 16�1�7�1�730 �0�8�1�7�4�3 err�1�7�1�7 �1�7�1�7�1�7�1�7�0�8�1�7 req �1�7�1�7�1�7�1�7�1�7�0�5�1�7�1�7�0�8�1�7�1�7 id
	 * �1�7�0�7�0�7�1�7�0�1�1�7��1�7�1�7�1�7�1�7�0�8�0�0�1�7�1�7�0�9�1�7�1�7res �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�2 req �1�7�1�7�1�7�1�7�0�3�1�6�1�7�1�7�1�7�1�7�1�7�1�7�1�7 on/off�1�7�1�7temp_set�1�7�1�7temp_cur �1�7�1�7�1�7�1�7�0�7�1�7�0�2�0�5�1�7�1�7
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
	 * 4.1.5 �1�7�1�7�1�7�1�7 �1�7�1�7�1�7�1�7���1�7�1�7�1�7�1�7�1�7�1�6�0�8�1�7�0�3�0�1�0�1�1�7�1�7�1�7�0�0�1�7�1�7�1�7�1�7�1�5�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�����1�7�1�7�0�8�1�7�1�7���1�7�1�7�1�7�1�7�1�7��1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7���1�7��1�7�0�7�1�7 �1�7�1�3�1�7�1�7�1�7�1�7�1�7�0�1�1�7�1�7
	 * cfg/ack�1�7�1�7req/res �1�7�1�7�1�7�1�7�1�7�1�7�1�7�6�9�1�7�1�7�1�7�1�7 cfg/ack �1�7�1�7�1�7�1�1�1�7�1�7�0�1�1�7�1�7�1�5�1�7req/res �1�7�1�7�1�7�1�8�1�7�0�9�1�7�1�7�1�7�1�7�0�8�0�0�1�7�1�7�1�7�1�7�1�7�1�7 �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�5�1�7 id�1�7�1�7
	 * �1�7�1�7�1�7�1�7�1�7��1�7�1�7 id �1�7�0�0�1�7�1�7�1�7�0�3�0�1�1�7�1�7�1�7�1�7�1�7�݄1�7�0�0�1�7 on/off�1�7�1�7 �1�7�1�7�1�7�1�0�1�7�1�7�1�7�1�7�1�70 �0�2�1�7�1�5�1�71 �0�2�1�7�1�7�1�7�1�7 err�1�7�1�7 �1�7�1�7�1�7�1�7�0�0�1�7 ?? �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7 cfg
	 * �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7 token$cfg,relay,id,on/off,0,0,0,0\n ack �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7
	 * token$ack,relay,id,on/off,0,0,0,err\n �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�5�1�7 id�1�7�1�7 �1�7�1�7�1�7�1�7�1�7��1�7�1�7 id
	 * �1�7�0�0�1�7�1�7�1�7�0�3�0�1�1�7�1�7�1�7�1�7�1�7�݄1�7�0�0�1�7 on/off�1�7�1�7 �1�7�1�7�1�7�1�0�1�7�1�7�1�7�1�7�1�70 �0�2�1�7�1�5�1�71 �0�2�1�7�1�7�1�7�1�7 err�1�7�1�7 �1�7�1�7�1�7�1�7�0�0�1�7 �1�7�1�7�1�7�1�3�0�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7���1�7�1�7�1�7�0�2 0
	 * �1�7�1�7�1�7�1�7�0�2�1�7�1�7�1�7�1�7�1�7�6�5�1�7�1�7�1�7�1�8�1�7�1�7�^ack �1�7�1�7�1�7�1�7�1�7�1�7�0�2 cfg �1�7�1�7�1�7�1�7�0�3�1�6�1�7�1�7�1�7id�1�7�1�7on/off �1�7�1�7�1�7�0�5�0�5�0�5�1�7�1�7�0�8�1�7�1�7 ?? �1�7�1�7�0�9�1�7�1�7�1�7�1�7 req �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7
	 * token$req,relay,id,0,0,0,0,0\n res �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7
	 * token$res,relay,id,on/off,0,0,0,err\n �1�7�1�7�0�9�1�7�1�7�1�7�1�7�1�7���1�7�1�7�1�7�0�2 0 �1�7�1�7�1�7�1�7�0�2�1�7�1�7�1�7�1�7�1�7�6�5�1�7�1�7�1�7�1�8�1�7�1�7�^req �1�7�1�7�1�7�1�7�1�7�0�5�1�7�1�7�0�8�1�7�1�7
	 * id �1�7�0�7�0�7�1�7�1�7�1�1�1�7�1�7�1�7�0�8�0�0�1�7�1�7 �0�9�1�7�1�7res �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�2 req �1�7�1�7�1�7�1�7�0�3�1�6�1�7�1�7�1�7�1�7�1�7�1�7�1�7 on/off �1�7�1�7�0�7�1�7�0�2�0�5�1�7�1�7
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
	 * 4.1.6 �1�7�1�7�1�7�1�7�1�7�� �1�7�1�7�1�7�1�7�1�7��0�1�1�7�1�7 cfg/ack/req/res �1�7�1�7�1�7�6�5�1�7�1�7�1�7�1�1�1�7�1�7�0�0�1�7�1�7�1�7�1�7��1�7�1�7�1�7�1�7�0�9�1�7�1�7�1�7�1�7�1�7�0�1�1�7�0�4�1�7�1�7�1�7�1�7��1�7�1�7 Honeywell
	 * Honeywell Shanghai R&D Center Page 21 of 26 ?? �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7 cfg �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7
	 * token$cfg,curtain,id,action,on/off,position,0\n ack �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7 token$ack,
	 * curtain,id,action,on/off,position,err\n �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�5�1�7 id�1�7�1�7 �1�7�1�7�1�7�1�7�1�7��1�7�1�7 id
	 * �1�7�0�0�1�7�1�7�1�7�0�3�0�1�1�7�1�7�1�7�1�7�1�7�݄1�7�0�0�1�7 action�1�7�1�74 �0�2�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�3�1�75 �0�2�1�7�1�7�0�3�0�1�1�7�1�7�1�7�Մ1�7�1�7�1�7�1�7��1�7�1�76 �0�2�1�7�1�9�1�7�0�3�0�1�1�7�1�7�1�7�Մ1�7�1�7�1�7�1�7��1�7�1�7 on/off�1�7�1�7 �1�7�1�7�1�7�1�0�1�7�1�7�1�7�1�7�1�70
	 * �0�2�1�7�1�5�1�71 �0�2�1�7�1�7�1�7�1�72 �0�2�0�5�1�7�1�7�1�7�1�7�1�7�1�7 MaiaII�1�7�1�7 position: err�1�7�1�7 �1�7�1�7�1�7�1�7�0�8�1�7 ack �1�7�1�7�1�7�1�7�1�7�1�7�0�2 cfg
	 * �1�7�1�7�1�7�1�7�0�3�1�6�1�7�1�7�1�7id�1�7�1�7action�1�7�1�7on/off �1�7�1�7�1�7�0�5�0�5�0�5�1�7�1�7�0�8�1�7�1�7 ?? �1�7�1�7�0�9�1�7�1�7�1�7�1�7 req �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7
	 * token$req,curtain,id,0,0,0,0\n res �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7
	 * token$res,curtain,id,0,on/off,position,err\n �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�5�1�7 id�1�7�1�7 �1�7�1�7�1�7�1�7�1�7��1�7�1�7 id
	 * �1�7�0�0�1�7�1�7�1�7�0�3�0�1�1�7�1�7�1�7�1�7�1�7�݄1�7�0�0�1�7 on/off�1�7�1�7 �1�7�1�7�1�7�1�0�1�7�1�7�1�7�1�7�1�70 �0�2�1�7�1�5�1�71 �0�2�1�7�1�7�1�7�1�7 position�1�7�1�7 err�1�7�1�7 �1�7�1�7�1�7�1�7�0�8�1�7
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
	 * 4.1.7 HBUS �1�7�1�7�1�7�1�7�1�7�� �1�7�1�7�1�7�1�7�1�7��0�1�1�7�1�7 cfg/ack/req/res �1�7�1�7�1�7�6�5�1�7�1�7�1�7�1�1�1�7�1�7�0�0�1�7�1�7�1�7�1�7��1�7�1�7�1�7�1�7�0�9�1�7�1�7�1�7�1�7�1�7�0�1�1�7�0�4�1�7�1�7�1�7�1�7��1�7�1�7 ?? �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7
	 * cfg �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7 token$cfg,hbuscurtain,area,loop,action,on/off,position,0\n
	 * ack �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7 token$ack, hbuscurtain,area,loop,action,on/off,position,err\n
	 * �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�5�1�7 id�1�7�1�7 �1�7�1�7�1�7�1�7�1�7��1�7�1�7 id �1�7�0�0�1�7�1�7�1�7�0�3�0�1�1�7�1�7�1�7�1�7�1�7�݄1�7�0�0�1�7 action�1�7�1�74 �0�2�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�3�1�75 �0�2�1�7�1�4�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�Մ1�7�1�7�1�7�1�7��1�7�1�76
	 * �0�2�1�7�1�9�1�7�0�3�0�1�1�7�1�7�1�7�Մ1�7�1�7�1�7�1�7��1�7�1�7 on/off�1�7�1�7 �1�7�1�7�1�7�1�0�1�7�1�7�1�7�1�7�1�70 �0�2�1�7�1�5�1�71 �0�2�1�7�1�7�1�7�1�72 �0�2�0�5 position: err�1�7�1�7 �1�7�1�7�1�7�1�7�0�8�1�7 ack �1�7�1�7�1�7�1�7�1�7�1�7�0�2 cfg
	 * �1�7�1�7�1�7�1�7�0�3�1�6�1�7�1�7�1�7id�1�7�1�7action�1�7�1�7on/off �1�7�1�7�1�7�0�5�0�5�0�5�1�7�1�7�0�8�1�7�1�7 ?? �1�7�1�7�0�9�1�7�1�7�1�7�1�7 req �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7 Honeywell Honeywell
	 * Shanghai R&D Center Page 22 of 26
	 * token$req,hbuscurtain,area,loop,0,0,0,0\n res �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7
	 * token$res,hbuscurtain,area,loop,0,on/off,position,err\n �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�5�1�7 id�1�7�1�7
	 * �1�7�1�7�1�7�1�7�1�7��1�7�1�7 id �1�7�0�0�1�7�1�7�1�7�0�3�0�1�1�7�1�7�1�7�1�7�1�7�݄1�7�0�0�1�7 on/off�1�7�1�7 �1�7�1�7�1�7�1�0�1�7�1�7�1�7�1�7�1�70 �0�2�1�7�1�5�1�71 �0�2�1�7�1�7�1�7�1�7 position�1�7�1�7 err�1�7�1�7 �1�7�1�7�1�7�1�7�0�8�1�7
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
	 * 4.1.8 �1�7�1�7�1�7�1�7�1�7�� �1�7�1�7�1�7�1�7�1�7��0�1�1�7�1�7 cfg/ack �1�7�1�7�1�7�6�5�1�7�1�7�1�7�1�1�1�7�1�7�0�6�1�7�1�7�1�7�1�7��1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7��1�7�1�7�0�1�1�7�0�2�1�7�0�9�1�7�1�7�1�7�6�9 ?? �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7 cfg �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7
	 * token$cfg,ir,devid,modid,0\n ack �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7 token$ack, ir,devid,modid,err\n
	 * �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�5�1�7 devid�1�7�1�7 �1�7�1�7�1�7�1�7�1�7��1�7�1�7 id �1�7�0�0�1�7�1�7�1�7�0�3�0�1�1�7�1�7�1�7�1�7�1�7�݄1�7�0�0�1�7 modid�1�7�1�7�1�7�1�7�1�7�1�7�1�7��0�0�0�4�1�7�0�0�1�7�1�7�1�7�0�3�0�1�1�7�1�7�1�7�1�7�1�7�݄1�7�0�0�1�7 err�1�7�1�7 �1�7�1�7�1�7�1�7�0�8�1�7
	 * ack �1�7�1�7�1�7�1�7�1�7�1�7�0�2 cfg �1�7�1�7�1�7�1�7�0�3�1�6�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�5�0�5�0�5�1�7�1�7�0�8�1�7�1�7
	 */

	public ExecutionResult controlInfraRedDevice(int devid, int modid)
			throws IOException {

		String requestCommand = this.construstRequest("cfg", "ir", devid,
				modid, 0);
		return getResult(requestCommand);

	}

	/*
	 * 
	 * 4.1.9 Wifi2IR �1�7�� Wifiir �1�7��0�1�1�7�1�7 cfg/ack �1�7�1�7�1�7�6�5�1�7�1�7�1�7�1�1�1�7�1�7�1�7�1�7�1�7�1�7�1�6�1�7�1�7�1�7�1�7��1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7��1�7�1�7�0�1�1�7�0�2�1�7�0�9�1�7�1�7�1�7�6�9 ?? �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7
	 * cfg �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7 token$cfg,wifi2ir,moduleid,keyindex,delay\n ack �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7
	 * token$ack,wifi2ir,moduleid, keyindex,err\n �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�5�1�7 Moduleid: wifiir
	 * �1�7��1�7�1�7�0�0�1�7�1�7 id Keyindex�1�7�1�7wifiir �1�7��1�7�0�4�1�7�1�7�1�7 index Delay: �1�7�1�7�1�7�0�8�1�7�1�7�1�7�1�7�0�2�0�7�1�7�1�7�0�1�1�7�0�2�1�7�4�8�0�8�1�7�1�7�0�0�1�7�1�7 5 Err: �1�7�1�7�1�7�1�7�1�7
	 */

	public ExecutionResult controlWifi2IR(int moduleid, int keyindex, int delay)
			throws IOException {

		String requestCommand = this.construstRequest("cfg", "wifi2ir",
				moduleid, keyindex, delay);
		return getResult(requestCommand);

	}

	/*
	 * 4.1.10 �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�� �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�1�1�7�1�7 cfg/ack/req/res �1�7�1�7�1�7�6�5�1�7�1�7�1�7�1�1�1�7�1�7�0�6�0�0�1�7�0�9�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7��1�7�1�7 ?? �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7 cfg
	 * �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7 token$cfg,music,area,status,vol,cmd,para,0\n ack �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7
	 * token$ack,music,area,status,vol,cmd,para,err �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�5�1�7 area�1�7�1�7
	 * �1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�2�1�7�1�7�0�7�1�7�1�7�1�7�1�7�1�7�0�0�1�7 status�1�7�1�7 1 stop, 2 play, 3 pause, -1 �1�7�0�2�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7 vol�1�7�1�7 �1�7�1�7�1�7�1�7�1�7�1�7���1�7�1�7�1�7�1�7��
	 * 0~31�1�7�1�7-1 �1�7�0�2�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7 cmd�1�7�1�7 �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�6�0 -1�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7 1�1�7�1�7�1�7�1�7�1�7 para�1�7�1�7�1�7�1�7 para=1 �1�7�1�7�0�5�1�7�1�7�0�5�1�7�0�1�1�7para=0
	 * �1�7�1�7�0�5�1�7�1�7�0�5�1�7�1�7 para�1�7�1�7 �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�8�1�7�1�7�1�7�1�7(cmd �1�7�1�7�0�8�1�7�0�4�1�7�1�7�1�7)�1�7�1�7
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
	 * 4.1.11 �1�7�1�7�1�7�1�7�1�7�1�7 �1�7�1�7�1�7�1�7�1�7�1�7�0�1�1�7�1�7 req/res �1�7�1�7�1�7�6�5�1�7�1�7�1�7�1�0�1�7�1�7�1�7�1�7�1�7�0�8�0�0�1�7�1�7�0�9�1�7�1�7 req �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7
	 * token$req,sensor,area,loop,sensortype,subnetid,deviceid,logicnum\n res
	 * �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7
	 * token$res,sensor,area,loop,sensortype,d1,d2,d3,d4,d5,d6,d7,d8,d9,0\n
	 * �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�5�1�7 area�1�7�1�7 �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�8�1�7�1�7�1�7�1�7�1�7�1�7�0�0�1�7 loop�1�7�1�7 �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�8�1�7�1�7���1�7�1�7 sensortype�1�7�1�7 �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7 1 8in1
	 * DeviceType315, 2 8in1 DeviceType314, 3 12in1, 4 SensorInOne�1�7�1�7 subnetid�1�7�1�7
	 * �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7 ID�1�7�1�7 deviceid�1�7�1�7 �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�� ID�1�7�1�7 logicnum�1�7�1�7 �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�8�1�7�1�7�0�0�1�7
	 * d1,d2,d3,d4,d5,d6,d7,d8,d9 �1�7�1�7 sensortype �1�7�1�7�1�5�1�7�1�7�1�7�1�7�0�5�1�7 1 8in1 DeviceType315 �1�7�0�0�1�1�1�7 1
	 * �0�8�0�0�1�7�1�7�1�7�0�0�1�1�1�7 2 �0�8�0�0�1�7�1�70�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�70�1�7�1�70�1�7�1�7�1�7�0�1�1�7�0�2�1�7�1�7�1�7�˄1�7�1�7�1�7�0�1�1�7�0�2�1�7�1�7�1�7�˄1�7�1�70 3 12in1
	 * �1�7�0�6�1�7�1�7�1�7�1�7�1�7�0�2�1�7�1�1�1�7�1�7�1�7�0�2�1�7�0�9�0�2�1�7�1�7�1�7�1�7�0�3�1�7�˄1�7�1�7�1�7�1�7�1�7�0�0�1�7�˄1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�0�1�1�1�7 1�1�7�1�7�1�7�0�0�1�1�1�7 2�1�7�1�70 4 SensorInOne
	 * �1�7�1�7�0�2�1�7�0�9�0�2�1�7�1�7�1�7�1�7�0�3�1�7�˄1�7�1�7�1�7�1�7�1�7�0�0�1�7�˄1�7�1�7�0�5�1�7�0�2�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7���1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�0�1�1�1�7 1�1�7�1�7�1�7�0�0�1�1�1�7 2 4.1.12 �1�7�1�7�1�7�1�7�1�7 �1�7��1�7�1�7�1�7�1�7�1�7�ք1�7 err
	 * �1�7�1�7�0�2�1�7�1�7�1�7�1�7�0�0�1�7�1�7�1�7�1�7�1�7�1�7�0�5�1�7�1�7�0�1�0�5�1�7�Ǆ1�7�0�0�1�7�1�7�1�7�1�7�G�1�7�1�7�1�7�1�7�1�7�0�5�1�7 ?? 0�1�7�1�7�1�7��1�7�1�7�1�7�1�7�1�7�1�7 ?? 1�1�7�1�7�1�7��1�7�1�7�1�7�1�7�0�2�1�7�1�1�1�7 ?? 2�1�7�1�7
	 * �0�8�0�0�ā0�4(�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�9�1�7�1�7�1�7�1�7��1�7�1�7�1�7�9�7�1�7�1�7�1�7�1�7�1�7�0�2�1�7�0�5�0�6�1�7�1�7�1�7�0�2�1�7�1�7)�1�7�1�7 ?? 128�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�2�1�7�1�1�1�7 Honeywell Honeywell Shanghai
	 * R&D Center Page 25 of 26 ?? 129�1�7�1�7�1�7��1�7�1�7�1�7�1�7�0�5�1�7�1�7�1�7�1�7 ?? 130�1�7�1�7 �1�7�1�7�1�7�7�9�0�2�1�7�1�7 ?? 131�1�7�1�7 �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7 ??
	 * 132�1�7�1�7�1�7��1�7�1�7�1�7�0�5�1�7 ?? 133�1�7�1�7�1�7�1�7�1�7�1�9�1�7�1�7�1�7 ?? 134�1�7�1�7�1�7��1�7�1�7�1�7�1�7
	 * 
	 * 
	 * �1�7�1�7�1�7�1�7�1�7�1�7�0�1�1�7�1�7 req/res �1�7�1�7�1�7�6�5�1�7�1�7�1�7�1�0�1�7�1�7�1�7�1�7�1�7�0�8�0�0�1�7�1�7�0�9�1�7�1�7 req �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7
	 * token$req,sensor,area,loop,sensortype,subnetid,deviceid,logicnum\n res
	 * �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7
	 * token$res,sensor,area,loop,sensortype,d1,d2,d3,d4,d5,d6,d7,d8,d9,0\n
	 */

	public ExecutionResult querySensor(int area, int loop, int sensortype,
			int subnetid, int deviceid, int logicNumber) throws IOException {

		String requestCommand = this.construstRequest("req", "sensor", area,
				loop, sensortype, subnetid, deviceid, logicNumber);
		return getResult(requestCommand);

	}

	/*
	 * 4.2.1 �1�7�1�7�1�7�1�7 �1�7�1�7�1�7�1�7���1�7�1�7�1�7�1�7�1�7�1�6�0�8�1�7�0�3�0�1�0�1�1�7�0�3�1�7�1�7�1�7�0�0�0�4�1�7�1�7�1�7�1�7�1�7�1�7�0�0�0�4�1�7�0�7�0�5�7�2�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�1�1�7�0�0�0�4�1�7�0�4�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�1�1�7
	 * �1�7�1�7�0�3�1�7�1�7�1�7�1�7�1�7�0�0�0�4�1�7�1�7�0�0�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�3�0�1�1�7�1�7�1�7�1�3�1�7�1�7���1�7�1�7�0�4�1�7�1�7�1�7�1�7�0�6�1�7�0�3�0�1�1�7�1�7�1�7�1�7�1�7�݄1�7�1�7�1�7�1�7�1�7�Ԅ1�7�1�7�1�7 id �1�7�0�8�1�7 �1�7�1�7�1�7�1�7�0�1�1�7�1�7 cfg/ack�1�7�1�7req/res �1�7�1�7�1�7�1�7�1�7�1�7�1�7�6�9�1�7�1�7
	 * cfg/ack �1�7�1�7�1�7�1�1�1�7�1�7�0�9�1�7�1�7�1�7�1�7�1�7req/res �1�7�1�7�1�7�1�8�1�7�0�9�1�7�1�7�0�2�1�7�1�7�1�7�1�7(�0�0�1�7�0�3�1�7�1�7�1�7 �0�8�1�7�1�7�0�0�1�7�1�7 trigger �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�ӄ1�7�0�9)�1�7�1�7 ?? �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7 cfg
	 * �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7 token$cfg,scenario,sid,1\n ack �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7
	 * token$ack,scenario,sid,result\n �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�5�1�7 sid�1�7�1�7 �1�7�1�7�1�7�1�7 id �1�7�0�0�1�7�1�7�1�7�0�3�0�1�1�7�1�7�1�7�1�7�1�7�݄1�7�0�0�1�7
	 * result: �1�7�1�7�1�7�0�9�1�7�1�7�1�7�1�71 �0�2�1�7�0�6�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�5�0�2�0�2�1�7�1�9�1�7 cfg �1�7�1�7�1�7�1�7�1�7�0�5�0�8�1�7�1�7�0�8�1�7�1�7 id �1�7�0�7�0�5�1�7�1�7�1�7�1�7�1�7ack �1�7�1�7�1�7�1�7�1�7�1�7�0�2 cfg �1�7�1�7�1�7�1�7�0�3�1�6�1�7�1�7�1�7sid
	 * �1�7�1�7�1�7�0�5�1�7�1�7 cfg �1�7�1�7�1�7�1�7�1�7�0�8�1�7�1�7 result �1�7�1�7�1�7�0�5�1�7�1�7�0�5�1�7�1�7�1�7�0�9�1�7�1�7�1�7�1�7 ?? �1�7�1�7�0�9�1�7�1�7�1�7�1�7 req �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7
	 * token$req,scenario,areaid,1\n res �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7 token$res,scenario,sid,result\n
	 * �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�5�1�7 sid�1�7�1�7 �1�7�1�7�1�7�1�7 id �1�7�0�0�1�7�1�7�1�7�0�3�0�1�1�7�1�7�1�7�1�7�1�7�݄1�7�0�0�1�7 areaid: �1�7�1�7�1�7�1�7 id �1�7�0�0�1�7�1�7�1�7�0�3�0�1�1�7�1�7�1�7�1�7�1�7�݄1�7�0�0�1�7 result:
	 * �1�7�1�7�1�7�0�9�1�7�1�7�1�7�1�71 �0�2�1�7�0�6�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�5�0�2�0�2�1�7�1�9�1�7 �1�7�1�7�1�7�1�7�1�7�1�7�0�9�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�9�0�5�1�7�1�7�1�7�1�7�1�7�1�7�0�8�1�7�0�1�0�5�1�7�1�7�1�7�1�7�1�7req �1�7�1�7�1�7�1�7�1�7�1�7 areaid �1�7�1�7�0�2�1�7�1�7�0�8�1�7�1�7�0�9�1�7�1�7�1�7�1�7�1�7�1�7�0�0�1�7res
	 * �1�7�1�7�1�7�1�7�1�7�ք1�7 sid �1�7�1�6�1�7�1�7�1�7�0�9�1�7�1�7�1�7�0�5�1�7�1�7�1�7�1�7�0�0�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�0�8�1�7�1�7�1�7�ʦÄ1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�5�0�2 no�1�7�1�7
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
	 * 4.2.2 �1�7�1�7�1�7�1�7�1�7�1�7 �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�0�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�1�1�7�0�3�0�1�1�7�1�7�1�7�0�2�1�7�1�7�1�7�0�0�1�7�1�7�1�7څ�1�7�1�7�0�2�1�7�4�5�1�7�1�7�1�7�1�7�0�8�1�7�0�5�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�9�1�7�1�7�1�7�1�7�1�7�1�7�0�6�1�7�1�7�1�7�1�7 �1�7�1�7�0�9�0�0�1�7�0�3�1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7 �1�7�1�7�1�7�1�7�1�7�1�7�0�1�1�7�1�7
	 * cfg/ack�1�7�1�7req/res �1�7�1�7�1�7�1�7�1�7�1�7�1�7�6�9�1�7�1�7�1�7�1�7 cfg/ack �1�7�1�7�1�7�1�1�1�7�1�7�0�0�1�7�1�7�1�7�1�7�1�7�1�7�1�7req/res �1�7�1�7�1�7�1�8�1�7�0�9�1�7�1�7�1�7�1�7�1�7�1�7�0�8 �0�0�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7 id
	 * �1�7�0�0�1�7�1�7�1�7�0�2�1�7�0�1�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�0�1�0�5�1�7�1�7�1�7�0�7�1�7�0�6�1�7�1�7�1�7�1�7�1�7�0�8�1�7�1�7�1�7�1�7�0�2�1�7�0�5�1�7�1�7 0�1�7�1�799 �0�8�1�7�1�7�1�7�1�7�1�7 �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�2�1�7�1�7�1�7�1�7�1�7�1�7 id �1�7�0�8�1�7 ?? �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7 cfg �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7
	 * token$cfg,trigger,id,on/off\n ack �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7 token$ack,trigger,id,result\n
	 * �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�5�1�7 id�1�7�1�7 �1�7�1�7�1�7�1�7�1�7�1�7 id �1�7�0�0�1�7 on/off: 0 �0�2�1�7�1�9�0�0�1�7�1�7�1�7�1�7�1�7�1�7�1�71 �0�2�1�7�1�0�1�7�1�7�1�7�1�7�1�7�1�7�1�7 result: �1�7�1�7�1�7�0�9�1�7�1�7�1�7�1�71
	 * �0�2�1�7�0�6�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�5�0�2�0�2�1�7�1�9�1�7 cfg �1�7�1�7�1�7�1�7�1�7�1�7�0�0�1�7�1�7�1�9�0�0�1�7�1�7�1�7�1�7�1�7�1�7�1�7ack �1�7�1�7�1�7�1�7�1�7�1�7�0�2 cfg �1�7�1�7�1�7�1�7�0�3�1�6�1�7�1�7�1�7id �1�7�1�7�1�7�0�5�1�7�1�7 cfg �1�7�1�7�1�7�1�7�1�7�0�8�1�7�1�7 result
	 * �1�7�1�7�1�7�0�5�0�2�1�7�1�7�1�7�0�9�1�7�1�7�1�7�1�7 ?? �1�7�1�7�0�9�1�7�1�7�1�7�1�7 req �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7 token$req,trigger,id,0,0\n res �1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�0�5�1�7
	 * token$res,trigger,id,result,sid \n �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�5�1�7 id�1�7�1�7 �1�7�1�7�1�7�1�7�1�7�1�7 id �1�7�0�0�1�7 sid�1�7�1�7 �1�7�1�7�1�7�1�7 id
	 * �1�7�0�0�1�7�0�3�0�1�1�7�1�7�1�7�1�5�1�7 result: �1�7�1�7�1�7�0�9�1�7�1�7�1�7�1�71 �0�2�1�7�0�6�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�5�0�2�0�2�1�7�1�9�1�7 req �1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�1�1�7�1�7�1�7�1�7�1�7 id �1�7�0�0�1�7res �1�7�1�7�1�7�8�8�1�7�1�7 status
	 * �1�7�1�7�1�7�݁1�6�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�2�0�8�0�0�1�7�1�7�1�7�1�7 sid �1�7�1�7�1�7�݁1�6�1�7 �1�7�1�7�0�2�0�3�0�1�1�7�1�7�1�7�1�7 id �1�7�0�0�1�7�1�7�1�7�1�7�0�4�1�7�1�7�0�8�1�7���1�7�1�7�1�7�1�7�1�7�1�7�1�7 id �1�7�1�7�0�2 0
	 */

	public ExecutionResult controlTriger(int id, int onOrOff)
			throws IOException {

		String requestCommand = this.construstRequest("cfg", "triger", id,
				onOrOff);
		return getResult(requestCommand);

	}

	@Override
	public String toString() {
		return super.toString();
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

						logln("�1�7�1�7�1�7�0�1�6�4�1�7�1�7�1�7�1�3�1�7�1�7�1�7�1�7�1�7�0�4�1�7�1�7�1�7�1�7�0�8�1�7�1�7�1�7�1�7!");
						this.ensureState();
						continue; 
					}
					return response;

				} catch (SocketTimeoutException e) {
					logln("�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�2�1�7�1�7�1�7�1�7�0�0�1�7�1�7�1�7�1�7�1�7�1�7�0�8�1�7�1�7�1�7�1�7......");
					this.ensureState();
					continue;
				}

			}

			logln("�1�7�1�7�1�7�0�7�0�6�1�7�1�7�1�7�1�7�1�7�1�7�1�7 " + count + " �1�7���1�7�1�7�1�7�1�7�1�7 ");
			throw new IOException("�1�7�1�7�1�7�0�7�0�6�1�7�1�7�1�7�1�7�1�7�1�7�1�7 " + count
					+ " �1�7���1�7�0�1�1�7�1�1�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7HoneyWell�1�7�1�7�1�7�1�7�1�7�0�9�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�1�7�0�9�1�7�1�7�1�7�1�7�0�5�1�7�1�7�ք1�7�0�4�1�7�1�7");
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
