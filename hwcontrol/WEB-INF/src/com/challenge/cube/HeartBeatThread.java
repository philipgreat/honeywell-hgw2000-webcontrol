package com.challenge.cube;

import java.io.IOException;

public class HeartBeatThread extends Thread {
	private HeartbeatInfo heartbeatInfo;
	private CubeConnection cubeConnection;

	public HeartBeatThread(HeartbeatInfo heartbeatInfo, CubeConnection connection){
		this.heartbeatInfo = heartbeatInfo;
		this.cubeConnection = connection;
	}

	@Override
	public void run() {
		
		while(true){
			doJob();
			
		}
	}
	protected void doJob(){
		try {
			boolean timeout = heartbeatInfo.waitForTimeout();
			
			if(timeout){
				
				cubeConnection.heartbeat();
				//send hearbeat to connection
			}
			
		}catch (InterruptedException e) {
			
			
			
		}catch (IOException e) {
			
		}
		
	}
	
	
	
	
}
