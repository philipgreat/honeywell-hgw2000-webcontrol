package com.challenge.cube;

public class HeartbeatInfo {
	private int sleepTime = 60*1000;//in milionseconds
	
	
	//return 0 - timeout
	//return 1 - interrupted
	public volatile boolean notified = false;
	
	public synchronized boolean waitForTimeout() throws InterruptedException{
		
		this.wait(sleepTime);
		if(notified){
			
			notified = !notified;
			
			return false;
			
		}
		return true;
		
		
		
	}
	public synchronized void wakeup(){
		notified = true;
		this.notifyAll();
		
	}
	
	
}
