package com.challenge.cube;

public class HeartbeatInfo {
	private int sleepTime = 2000;//in milionseconds
	
	
	//return 0 - timeout
	//return 1 - interrupted
	public volatile boolean notified = false;
	
	public boolean waitForTimeout() throws InterruptedException{
		
		this.wait(sleepTime);
		if(notified){
			
			notified = !notified;
			
			return true;
			
		}
		return false;
		
		
		
	}
	public void wakeup(){
		notified = true;
		this.notifyAll();
		
	}
	
	
}
