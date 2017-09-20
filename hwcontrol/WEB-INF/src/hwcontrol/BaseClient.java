package hwcontrol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.challenge.cube.CubeConnection;
import com.google.gson.Gson;

public class BaseClient {
	public String CONFIG_PATH = "conf/cube.json";

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
	protected void connect() throws IOException {
		
		loadConfiguration();
		
		this.connectToGateway(this.configuration.getUsername(), this.configuration.getPassword(),
				this.configuration.getHostIPAddress(), this.configuration.getPort());
		
	}
	protected void connectToGateway(String username, String password,
			String hostname, int port) throws IOException  {
		
		throw new IOException("Sub class must implement it");
		
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
	public String viewScreenLog() throws IOException {

		return loadStringFromFile("/home/hi/honeywell.log");

	}
	protected static String CUBE_CLIENT_LOG_FILE="/home/hi/cube-client.log";
	public String viewCubeClientLog() throws IOException {

		return loadStringFromFile(CUBE_CLIENT_LOG_FILE);
		
	}
	private Configuration configuration;
	
	protected void logln(String message) {
		String currentTimeExpr = currentTimeExpr();
		String logValue = String.format("%s: %s",currentTimeExpr, message);
		System.out.println(logValue);
		
		
	}

	private String currentTimeExpr() {
		String dateformat = "yyyy-MM-dd'T'HH:mm:ss";
		SimpleDateFormat format = new SimpleDateFormat(dateformat);
	
		return format.format(new Date());
	}
	protected void log(String message) {

		System.out.print(message);
	}
	
	public void configCubeClient(String username, String password,
			String hostname, int port) throws IOException {
		if (this.configuration == null) {

			this.configuration = new Configuration();
		}

		this.configuration.setUsername(username);
		this.configuration.setPassword(password);
		this.configuration.setHostIPAddress(hostname);
		this.configuration.setPort(port);
		this.connect();//测试过了才保存，这个可能抛异常
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
	
}
