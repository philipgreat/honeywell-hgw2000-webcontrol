package com.challenge.cube;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HeartbeatThread extends Thread {
	private HeartbeatInfo heartbeatInfo;
	private CubeConnection cubeConnection;

	public HeartbeatThread(HeartbeatInfo heartbeatInfo, CubeConnection connection){
		this.heartbeatInfo = heartbeatInfo;
		this.cubeConnection = connection;
	}

	@Override
	public void run() {
		this.setName("HEARTBEAT THREAD");
		while(true){
			doJob();
		}
	}
	private String currentTimeExpr() {
		String dateformat = "yyyy-MM-dd'T'HH:mm:ss";
		SimpleDateFormat format = new SimpleDateFormat(dateformat);
	
		return format.format(new Date());
	}
	protected void logln(String message) {
		String currentTimeExpr = currentTimeExpr();
		String logValue = String.format("%s: %s",currentTimeExpr, message);
		System.out.println(logValue);
		
		
	}
	
	protected void doJob(){
		try {
			boolean timeout = heartbeatInfo.waitForTimeout();
			
			if(timeout){
				
				cubeConnection.heartbeat();
				logln("timeout, sent a heartbeat.................");
				//send hearbeat to connection
			}
			
		}catch (InterruptedException e) {
			
			
			
		}catch (IOException e) {
			
		}
		
	}
	
	
	
	
}
