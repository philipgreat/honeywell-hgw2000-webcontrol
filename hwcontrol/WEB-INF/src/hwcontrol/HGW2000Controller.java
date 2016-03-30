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
	 * 4.1.1 �ƹ��豸 �ƹ��豸֧�� cfg/ack/req/res ����,���ڿ��ƺͲ�ѯ�ƹ��豸,��ѯ�����֧�� Maia II �ƹ⡣ ?
	 * �������� cfg �����ʽ����: token$cfg,lig,id,action,on/off,dimmer,0\n ack �����ʽ����:
	 * token$ack,lig,id,action,on/off,dimmer,err ������������������: id: �ƹ��豸�� id
	 * ��,��ϵͳ�����л��; action: 4 Ϊ���ƿ���,5 Ϊ��ϵͳ���еƹ��豸,6 Ϊ�ر�ϵͳ���еƹ��豸; on/off: ���ز���,0
	 * Ϊ��,1 Ϊ��;
	 * 
	 * 
	 * dimmer:�������,���� Maia I ��ϵͳ��˵ 128 Ϊ��������,129 Ϊ��������,255 Ϊ�޵��� ���� ;���� Maia II
	 * ϵͳ,ϵͳ֧�ֶ������,����ָ���˲���Ϊ 0-100 ��ʵ�־�ȷ��������,0 Ϊ �������,100 Ϊ������ȡ�; err: ����š� cfg
	 * ������ԶԵƹ��豸���еĿ��غ͵������,�� on/off ��Ϊ 0 ��ʱ��,dimmer ��������, ������Ϊ 255��ack ������Ϊ cfg
	 * ����Ļظ�,id��action��on/off��dimmer �����ֵ��һһ��Ӧ�� ? ��ѯ���� req �����ʽ����:
	 * token$req,lig,id,0,0,0,0\n res �����ʽ����:
	 * token$res,lig,id,0,on/off,dimmer,err ������������������: id: �ƹ��豸�� id ��,��ϵͳ�����л��;
	 * on/off: ���ز���,0 Ϊ��,1 Ϊ��; dimmer:�������,128 Ϊ��������,129 Ϊ��������,255 Ϊ�޵������ ; err:
	 * ����š�
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
	 * 4.1.3.1 485 �ӿڿյ� 485 �ӿڿյ��豸֧�� cfg/ack��req/res ����������� cfg/ack
	 * ���ڿ����豸��req/res ���ڲ�ѯ�豸 ״̬�� ?? �������� cfg �����ʽ���£�
	 * token$cfg,ac,id,on/off,mode,fan,dir,temp_set,0,0\n ack �����ʽ���£�
	 * token$ack,ac, id,on/off,mode,fan,dir,temp_set,0,err\n �����������������£� id�� �յ�
	 * id �ţ���ϵͳ�����л�ã� on/off�� ���ز�����1 Ϊ��, 0 Ϊ�أ� mode�� ģʽѡ�0 Ϊ�Զ�ģʽ, 1 ��ģʽ, 2
	 * Ϊ����ģʽ, 3 Ϊ����ģʽ, 4 Ϊ��ʪģ ʽ�� fan�� ����ѡ�0 Ϊ�Զ�, 1 Ϊ�ͷ�, 2 Ϊ���ٷ�, 3 Ϊ���ٷ磻 dir��
	 * ����ѡ������ temp_set�� �����¶ȣ���Χ�� 16��30 ֮�䣻 err�� ����š� cfg ������ dir
	 * ��Ϊ�����ϵͳ��ʱ��֧�ָ�ѡ���Ϊ 255 ���ɡ�ack ������Ϊ cfg ����Ļ�
	 * ����id��on/off��mode��fan��dir��temp_set �����ֵ��һһ��Ӧ�� ?? ��ѯ���� req �����ʽ���£�
	 * token$req,ac,id,0,0,0,0,0,0,0\n res �����ʽ���£�
	 * token$res,ac,id,on/off,mode,fan,dir,temp_set,temp_cur,err\n �����������������£�
	 * id�� �յ� id �ţ���ϵͳ�����л�ã� on/off�� ���ز�����1 Ϊ��, 0 Ϊ�أ� Honeywell Honeywell
	 * Shanghai R&D Center Page 19 of 26 mode�� ģʽѡ�0 Ϊ�Զ�ģʽ, 1 ��ģʽ, 2 Ϊ����ģʽ, 3
	 * Ϊ����ģʽ, 4 Ϊ��ʪģ ʽ�� fan�� ����ѡ�0 Ϊ�Զ�, 1 Ϊ�ͷ�, 2 Ϊ���ٷ�, 3 Ϊ���ٷ磻 dir�� ����ѡ������
	 * temp_set�� �����¶ȣ���Χ�� 16��30 ֮�䣻 temp_cur�� ��ǰ�¶ȣ���Χ�� 16��30 ֮�䣻 err�� ����š� req
	 * �����ʾ��ָ�� id �ŵĿյ��豸����״̬��ѯ��res ��������Ϊ req ����Ļظ�������
	 * on/off��mode��fan��dir��temp_set��temp_cur ����ĵ�ǰֵ������ dir ���� 255��
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
	 * 4.1.3.2 ����յ� ����յ���֧�� cfg/ack �����֧�ֶ��豸״̬�Ĳ�ѯ�� �������� cfg �����ʽ���£�
	 * token$cfg,ac,id,irid ,0\n ack �����ʽ���£� token$ack,ac,id,irid,err\n
	 * �����������������£� id�� �յ��豸�� id ��, ��ϵͳ�����л�ã� irid�� �յ�����ģʽ id �ţ���ϵͳ�����л�ã� err��
	 * ����š� ack ������Ϊ cfg ����Ļظ���id��irid ���ֵһһ��Ӧ
	 */

	public ExecutionResult controlInfradRedAirCondition(int id, int irid)
			throws IOException {

		String requestCommand = this.construstRequest("cfg", "ac", id, irid, 0);
		return getResult(requestCommand);

	}

	/*
	 * 
	 * 4.1.4 ��ů�豸 ��ů�豸֧�� cfg/ack��req/res ����������� cfg/ack ���ڿ����豸��req/res
	 * ���ڲ�ѯ�豸״̬�� ?? �������� cfg �����ʽ���£� token$cfg,ufh,id,on/off,temp_set,0,0\n ack
	 * �����ʽ���£� token$ack,ufh,id,on/off,temp_set,0,0\n �����������������£� id�� ��ů�豸�� id
	 * �ţ���ϵͳ�����л�ã� on/off�� ���ز�����0 Ϊ�أ�1 Ϊ���� temp_set�� �����¶ȣ���Χ�� 16��30 ֮�䣻 err��
	 * ����š� ack ������Ϊ cfg ����Ļظ���id��on/off��temp_set ���ֵһһ��Ӧ�� ?? ��ѯ���� req �����ʽ���£�
	 * token$req,ufh,id,0,0,0,0\n Honeywell Honeywell Shanghai R&D Center Page
	 * 20 of 26 res �����ʽ���£� token$res,ufh,id,on/off,temp_set,temp_cur,err\n
	 * �����������������£� id�� ��ů�豸�� id �ţ���ϵͳ�����л�ã� on/off�� ���ز�����0 Ϊ�أ�1 Ϊ���� temp_set��
	 * �����¶ȣ���Χ�� 16��30 ֮�䣻 temp_cur�� ��ǰ�¶ȣ���Χ�� 16��30 ֮�䣻 err�� ����š� req �����ʾ��ָ�� id
	 * �ŵĵ�ů�豸����״̬��ѯ��res ��������Ϊ req ����Ļظ������� on/off��temp_set��temp_cur ����ĵ�ǰֵ��
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
	 * 4.1.5 ���� ����Τ�����ܼҾ�ϵͳ֧���ĸ����أ������������·硢��ˮ��ú�����豸��������Щ�豸�Ŀ� �ء�����֧��
	 * cfg/ack��req/res ����������� cfg/ack ���ڿ��ƿ��أ�req/res ���ڲ�ѯ����״̬������ ��������������£� id��
	 * �����豸�� id �ţ���ϵͳ�����л�ã� on/off�� ���ز�����0 Ϊ�أ�1 Ϊ���� err�� ����ţ� ?? �������� cfg
	 * �����ʽ���£� token$cfg,relay,id,on/off,0,0,0,0\n ack �����ʽ���£�
	 * token$ack,relay,id,on/off,0,0,0,err\n �����������������£� id�� �����豸�� id
	 * �ţ���ϵͳ�����л�ã� on/off�� ���ز�����0 Ϊ�أ�1 Ϊ���� err�� ����ţ� ���صĿ��������У���Ϊ 0
	 * ����Ϊ��������޶��塣ack ������Ϊ cfg ����Ļظ���id��on/off ���ֵһһ��Ӧ�� ?? ��ѯ���� req �����ʽ���£�
	 * token$req,relay,id,0,0,0,0,0\n res �����ʽ���£�
	 * token$res,relay,id,on/off,0,0,0,err\n ��ѯ�����У���Ϊ 0 ����Ϊ��������޶��塣req �����ʾ��ָ��
	 * id �ŵĿ��ؽ���״̬�� ѯ��res ��������Ϊ req ����Ļظ������� on/off ��ĵ�ǰֵ��
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
	 * 4.1.6 �����豸 �����豸֧�� cfg/ack/req/res ������ڿ��ƴ����豸����ѯ�����֧�ִ����豸�� Honeywell
	 * Honeywell Shanghai R&D Center Page 21 of 26 ?? �������� cfg �����ʽ���£�
	 * token$cfg,curtain,id,action,on/off,position,0\n ack �����ʽ���£� token$ack,
	 * curtain,id,action,on/off,position,err\n �����������������£� id�� �����豸�� id
	 * �ţ���ϵͳ�����л�ã� action��4 Ϊ�����������ƣ�5 Ϊ��ϵͳ���д����豸��6 Ϊ�ر�ϵͳ���д����豸�� on/off�� ���ز�����0
	 * Ϊ�أ�1 Ϊ����2 Ϊͣ������ MaiaII�� position: err�� ����š� ack ������Ϊ cfg
	 * ����Ļظ���id��action��on/off ���ֵһһ��Ӧ�� ?? ��ѯ���� req �����ʽ���£�
	 * token$req,curtain,id,0,0,0,0\n res �����ʽ���£�
	 * token$res,curtain,id,0,on/off,position,err\n �����������������£� id�� �����豸�� id
	 * �ţ���ϵͳ�����л�ã� on/off�� ���ز�����0 Ϊ�أ�1 Ϊ���� position�� err�� ����š�
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
	 * 4.1.7 HBUS �����豸 �����豸֧�� cfg/ack/req/res ������ڿ��ƴ����豸����ѯ�����֧�ִ����豸�� ?? ��������
	 * cfg �����ʽ���£� token$cfg,hbuscurtain,area,loop,action,on/off,position,0\n
	 * ack �����ʽ���£� token$ack, hbuscurtain,area,loop,action,on/off,position,err\n
	 * �����������������£� id�� �����豸�� id �ţ���ϵͳ�����л�ã� action��4 Ϊ�����������ƣ�5 Ϊ�򿪸��������д����豸��6
	 * Ϊ�ر�ϵͳ���д����豸�� on/off�� ���ز�����0 Ϊ�أ�1 Ϊ����2 Ϊͣ position: err�� ����š� ack ������Ϊ cfg
	 * ����Ļظ���id��action��on/off ���ֵһһ��Ӧ�� ?? ��ѯ���� req �����ʽ���£� Honeywell Honeywell
	 * Shanghai R&D Center Page 22 of 26
	 * token$req,hbuscurtain,area,loop,0,0,0,0\n res �����ʽ���£�
	 * token$res,hbuscurtain,area,loop,0,on/off,position,err\n �����������������£� id��
	 * �����豸�� id �ţ���ϵͳ�����л�ã� on/off�� ���ز�����0 Ϊ�أ�1 Ϊ���� position�� err�� ����š�
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
	 * 4.1.8 �����豸 �����豸֧�� cfg/ack ������ڿ��ƺ����豸���������豸��֧�ֲ�ѯ��� ?? �������� cfg �����ʽ���£�
	 * token$cfg,ir,devid,modid,0\n ack �����ʽ���£� token$ack, ir,devid,modid,err\n
	 * �����������������£� devid�� �����豸�� id �ţ���ϵͳ�����л�ã� modid�������豸ģʽ�ţ���ϵͳ�����л�ã� err�� ����š�
	 * ack ������Ϊ cfg ����Ļظ��������ֵһһ��Ӧ��
	 */

	public ExecutionResult controlInfraRedDevice(int devid, int modid)
			throws IOException {

		String requestCommand = this.construstRequest("cfg", "ir", devid,
				modid, 0);
		return getResult(requestCommand);

	}

	/*
	 * 
	 * 4.1.9 Wifi2IR �豸 Wifiir �豸֧�� cfg/ack ������ڿ������ߺ����豸���������豸��֧�ֲ�ѯ��� ?? ��������
	 * cfg �����ʽ���£� token$cfg,wifi2ir,moduleid,keyindex,delay\n ack �����ʽ���£�
	 * token$ack,wifi2ir,moduleid, keyindex,err\n �����������������£� Moduleid: wifiir
	 * �豸��ģ�� id Keyindex��wifiir �豸�ļ��� index Delay: ���ͺ����źŵ��ӳ�ʱ�䣬Ĭ��ʹ�� 5 Err: �����
	 */

	public ExecutionResult controlWifi2IR(int moduleid, int keyindex, int delay)
			throws IOException {

		String requestCommand = this.construstRequest("cfg", "wifi2ir",
				moduleid, keyindex, delay);
		return getResult(requestCommand);

	}

	/*
	 * 4.1.10 ���������豸 ��������֧�� cfg/ack/req/res ������ڿ��ƺͲ�ѯ���������豸�� ?? �������� cfg
	 * �����ʽ���£� token$cfg,music,area,status,vol,cmd,para,0\n ack �����ʽ���£�
	 * token$ack,music,area,status,vol,cmd,para,err �����������������£� area��
	 * �������ֲ��ŵ�����ţ� status�� 1 stop, 2 play, 3 pause, -1 �˲��������� vol�� ������С����Χ
	 * 0~31��-1 �˲��������� cmd�� ������������� -1�������� 1����� para���� para=1 ��ʾ��һ�ף�para=0
	 * ��ʾ��һ�� para�� ���������������Ӧ����(cmd ��Ӧ�Ĳ���)��
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
	 * 4.1.11 ������ ������֧�� req/res ������ڴ�����״̬��ѯ�� req �����ʽ���£�
	 * token$req,sensor,area,loop,sensortype,subnetid,deviceid,logicnum\n res
	 * �����ʽ���£�
	 * token$res,sensor,area,loop,sensortype,d1,d2,d3,d4,d5,d6,d7,d8,d9,0\n
	 * �����������������£� area�� ��������Ӧ������ţ� loop�� ��������Ӧ��·�� sensortype�� ���������� 1 8in1
	 * DeviceType315, 2 8in1 DeviceType314, 3 12in1, 4 SensorInOne�� subnetid��
	 * ������������ ID�� deviceid�� ���������豸 ID�� logicnum�� ���������߼��ţ�
	 * d1,d2,d3,d4,d5,d6,d7,d8,d9 �� sensortype ��أ����£� 1 8in1 DeviceType315 �ɽڵ� 1
	 * ״̬���ɽڵ� 2 ״̬��0��������������0��0���ӳ�ʱ���λ���ӳ�ʱ���λ��0 3 12in1
	 * �ɹ�����ʧ�ܣ���ǰ�¶ȣ����ȸ�λ�����ȵ�λ�����������������������ɽڵ� 1���ɽڵ� 2��0 4 SensorInOne
	 * ��ǰ�¶ȣ����ȸ�λ�����ȵ�λ��ʪ�ȣ�������������ú�����������������������ɽڵ� 1���ɽڵ� 2 4.1.12 ����� �豸�����е� err
	 * ��Ϊ����ţ������ֵ��ͳһ�涨�ģ����嶨�����£� ?? 0���豸������ ?? 1���豸����ʧ�ܣ� ?? 2��
	 * ״̬δ֪(��������޷����豸���򲻻�����ȷ�ĳɹ���ʧ��)�� ?? 128����������ʧ�ܣ� Honeywell Honeywell Shanghai
	 * R&D Center Page 25 of 26 ?? 129���豸����ֵ���� ?? 130�� ���ʱ�� ?? 131�� ����������� ??
	 * 132���豸���ϣ� ?? 133�����߳��� ?? 134���豸����
	 * 
	 * 
	 * ������֧�� req/res ������ڴ�����״̬��ѯ�� req �����ʽ���£�
	 * token$req,sensor,area,loop,sensortype,subnetid,deviceid,logicnum\n res
	 * �����ʽ���£�
	 * token$res,sensor,area,loop,sensortype,d1,d2,d3,d4,d5,d6,d7,d8,d9,0\n
	 */

	public ExecutionResult querySensor(int area, int loop, int sensortype,
			int subnetid, int deviceid, int logicNumber) throws IOException {

		String requestCommand = this.construstRequest("req", "sensor", area,
				loop, sensortype, subnetid, deviceid, logicNumber);
		return getResult(requestCommand);

	}

	/*
	 * 4.2.1 ���� ����Τ�����ܼҾ�ϵͳ֧�ֳ���ģʽ������ģʽ�ǵƹ⡢�������յ�ģʽ�Լ����������������õ�
	 * ��ϡ�����ģʽ��Ԥ��������ϵͳ���ݿ��У��û����Դ�ϵͳ�����л�����г��� id �š� ����֧�� cfg/ack��req/res ���������
	 * cfg/ack ���ڿ��Ƴ�����req/res ���ڲ�ѯ��ǰ����(ȫ�ֳ��� Ӧ��ʹ�� trigger ���������в�ѯ)�� ?? �������� cfg
	 * �����ʽ���£� token$cfg,scenario,sid,1\n ack �����ʽ���£�
	 * token$ack,scenario,sid,result\n �����������������£� sid�� ���� id �ţ���ϵͳ�����л�ã�
	 * result: ���ƽ����1 Ϊ�ɹ�������ֵΪʧ�ܡ� cfg �����ʾӦ��ָ�� id �ŵĳ�����ack ������Ϊ cfg ����Ļظ���sid
	 * ���ֵ�� cfg �����Ӧ�� result ���ֵ��ʾ���ƽ���� ?? ��ѯ���� req �����ʽ���£�
	 * token$req,scenario,areaid,1\n res �����ʽ���£� token$res,scenario,sid,result\n
	 * �����������������£� sid�� ���� id �ţ���ϵͳ�����л�ã� areaid: ���� id �ţ���ϵͳ�����л�ã� result:
	 * ���ƽ����1 Ϊ�ɹ�������ֵΪʧ�ܡ� ������ѯ������������ѯĳ������Ӧ�õĳ�����req ������ areaid ��Ϊ��Ҫ��ѯ������ţ�res
	 * �����е� sid �ظ���ѯ���ĳ����ţ����������û��Ӧ���κγ���������ֵΪ no��
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
	 * 4.2.2 ������ ���������ڴ��������������ܣ�ϵͳ���԰���Ԥ���趨��ʱ�䴥����Ӧ�ĳ�������ѯ������Ҳ���� ��ѯȫ�ֳ�����Ϣ�� ������֧��
	 * cfg/ack��req/res ����������� cfg/ack ���ڿ��ƴ�������req/res ���ڲ�ѯ������״ ̬������������û�� id
	 * �ţ���Ϊ�˱��������ʽ��ͳһ���ɺ�̨�����Զ����ɵ�һ�� 0��99 ֮����� ��������Ϊ������ id �š� ?? �������� cfg �����ʽ���£�
	 * token$cfg,trigger,id,on/off\n ack �����ʽ���£� token$ack,trigger,id,result\n
	 * �����������������£� id�� ������ id �ţ� on/off: 0 Ϊ�رմ�������1 Ϊ�򿪴������� result: ���ƽ����1
	 * Ϊ�ɹ�������ֵΪʧ�ܡ� cfg ������ƴ򿪻�رմ�������ack ������Ϊ cfg ����Ļظ���id ���ֵ�� cfg �����Ӧ�� result
	 * ���ֵΪ���ƽ���� ?? ��ѯ���� req �����ʽ���£� token$req,trigger,id,0,0\n res �����ʽ���£�
	 * token$res,trigger,id,result,sid \n �����������������£� id�� ������ id �ţ� sid�� ���� id
	 * �ţ�ϵͳ���ݣ� result: ���ƽ����1 Ϊ�ɹ�������ֵΪʧ�ܡ� req ������������ɴ����� id �ţ�res ����� status
	 * ���лظ���������ǰ״̬���� sid ���лظ� ��ǰϵͳ���� id �ţ����û��Ӧ�ó������� id ��Ϊ 0
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

						logln("���ֻỰ���ڣ�����ִ����֤����!");
						this.ensureState();
						continue; 
					}
					return response;

				} catch (SocketTimeoutException e) {
					logln("��������ʱ����ͼ������֤����......");
					this.ensureState();
					continue;
				}

			}

			logln("���ԵĴ������� " + count + " �Σ����� ");
			throw new IOException("���ԵĴ������� " + count
					+ " �Σ�ֻ�ܷ���������HoneyWell�����Ƿ����������Ƿ����ϣ��е�û��");
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
