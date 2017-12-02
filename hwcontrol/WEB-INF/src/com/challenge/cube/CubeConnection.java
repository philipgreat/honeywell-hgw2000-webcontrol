/**
 * 
 */
package com.challenge.cube;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Philip
 *
 */
public class CubeConnection {

	private String ipAddress;
	private int port;
	private Socket sock;
	private int timeOutInSeconds;
	private OutputStream outputStream;
	private InputStream inputStream;

	private HeartbeatThread heartbeatThread;
	private HeartbeatInfo    heartbeatInfo;
	public int getTimeOutInSeconds() {
		return timeOutInSeconds;
	}

	public void setTimeOutInSeconds(int timeOutInSeconds) {
		this.timeOutInSeconds = timeOutInSeconds;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	public boolean isClosed(){
		
		return this.sock.isClosed();
		
	}
	private void close() {
		// TODO Auto-generated method stub

	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Socket getSock() {
		return sock;
	}

	public void setSock(Socket sock) {
		this.sock = sock;
	}

	public CubeReponseBody auth(String cudeId, String password) throws IOException {

		// CubeMessage message = new CubeMessage();
		CubeMessageBody body = new CubeMessageBody().buildLogin(cudeId,
				password);

		
		return execute(body);

	}

	private long lastCommandTime = System.currentTimeMillis();

	public CubeReponseBody heartbeat() throws IOException {
		

		// CubeMessage message = new CubeMessage();
		CubeMessageBody body = new CubeMessageBody().buildHeartbeat();

		return executeInternal(body);

	}

	public CubeReponseBody getConfig() throws IOException {

		// CubeMessage message = new CubeMessage();
		CubeMessageBody body = new CubeMessageBody().buildGetConfig();

		return execute(body);

	}
	
	
	protected CubeReponseBody unpack(String jsonString) throws JsonParseException, JsonMappingException, IOException{
		
		ObjectMapper mapper = new ObjectMapper();
		CubeReponseBody body = mapper.readValue(jsonString, CubeReponseBody.class);
		body.setResponsText(jsonString);
		return body;
	}
	
	protected synchronized CubeReponseBody execute(CubeMessageBody body)
			throws IOException{
		
		return this.executeInternal(body);
		
	}
	
	protected synchronized CubeReponseBody executeInternal(CubeMessageBody body)
			throws IOException {
		
		synchronized (this.getOutputStream()) {
			this.getOutputStream().write(CubeMessage.pack(body));
			
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			System.out.println(body.getBody());
			synchronized (this.getInputStream()) {
				byte messageheader[] = new byte[CubeMessage.getHeaderLength()];

				this.getInputStream().read(messageheader);
				CubeMessageHeader header = CubeMessageHeader
						.parseHeader(messageheader);

				ByteBuffer bodyBuffer = ByteBuffer.allocate(header
						.getMessageLength());
				byte[] bodyData = new byte[1424];
				int readLength = 0;
				int remain = header.getMessageLength();
				while (remain > 0) {
					readLength = this.getInputStream().read(bodyData);
					bodyBuffer.put(bodyData, 0, readLength);
					remain -= readLength;
				}

				String jsonBody = new String(bodyBuffer.array());
				lastCommandTime = System.currentTimeMillis();
				System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
				System.out.println(jsonBody);
				return unpack(jsonBody);
			}
		}

		// byte [] result = this.getInputStream().read(b)
		// read the header first

	}

	public void connect() throws IOException {
		// TODO Auto-generated method stub
		InetAddress addr = InetAddress.getByName(this.getIpAddress());
		SocketAddress sockaddr = new InetSocketAddress(addr, port);
		sock = new Socket();
		timeOutInSeconds = 2; // 2 seconds
		sock.connect(sockaddr, timeOutInSeconds * 1000);
		// 启动线程开始监控

		this.outputStream = sock.getOutputStream();
		this.inputStream = sock.getInputStream();
		
		//this.au
		
		
		
	}

	public CubeConnection(String ipAddress, int port) {
		// TODO Auto-generated constructor stub
		this.ipAddress = ipAddress;
		this.port = port;

	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		/*
		String ipAddress = "192.168.0.104";
		String cudeId = "001F552A0562";
		String password = "12345";
		int port = 9000;
		*/
		String ipAddress = "114.252.61.176";
		String cudeId = "001F552A051C";
		String password = "12345";
		int port = 9000;
		CubeConnection conn = new CubeConnection(ipAddress, port);
		conn.connect();
		
		CubeReponseBody bodys[] = {conn.auth(cudeId, password),
				
				conn.execute(new CubeMessageBody().buildSetBackAudioStatus("1", "4241660241b180ce", "volumn1", "22"))
				};
		
		
		for(int i=0;i<bodys.length;i++){
			
			log(i+": "+ bodys[i].getErrorCode());
		}
		
		
		conn.close();

	}
	public static void log(Object message){
		System.out.println(message);
	}

}
