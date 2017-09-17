package com.challenge.cube;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = CubeReponseBodyDeserializer.class)
public class CubeReponseBody {
	private int errorCode = Integer.MAX_VALUE;
	private String action;
	private String subaction;
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getSubaction() {
		return subaction;
	}
	public void setSubaction(String subaction) {
		this.subaction = subaction;
	}
	
	
}
