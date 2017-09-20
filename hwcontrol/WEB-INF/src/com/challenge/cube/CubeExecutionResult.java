package com.challenge.cube;

import hwcontrol.ExecutionResult;

public class CubeExecutionResult extends ExecutionResult {
	public void setReceivedResponse(CubeReponseBody receivedResponse) {
		
		exectionTime=System.currentTimeMillis()-exectionTime;
		//this.receivedResponse = receivedResponse;
		this.responseBody = receivedResponse;
		this.receivedResponse = receivedResponse.getAction();
		//this.receivedResponse = receivedResponse
		
	}
	public int getErrorCode() {
		
		if(receivedResponse ==null){
			//there must be a network error
			return 255;
		}
		CubeReponseBody body=(CubeReponseBody)responseBody;
		return body.getErrorCode();
		
	}
}
