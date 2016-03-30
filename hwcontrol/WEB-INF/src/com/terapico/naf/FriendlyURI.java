package com.terapico.naf;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class FriendlyURI {
	

	private List<String> parameters;
	private String serviceName;
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public void addParameter(String parameter) {
		if(parameters==null){
			parameters=new ArrayList<String>(10);
			
		}
		parameters.add(parameter);
	
	}
	public String[] getParameter()
	{
		if(parameters==null){
			return new String[0]; 
		}
		return parameters.toArray(new String[0]);		
	}

	public  static FriendlyURI parse(String requestURI,int start) throws UnsupportedEncodingException
	{
		if(start<0){
			throw new IllegalArgumentException(" parse(String requestURI,int start): start<0, not expected");
		}
		if(requestURI==null){
			throw new IllegalArgumentException("parse(String requestURI,int start):  requestURI is null, not expected");
		}
		String array[]=requestURI.split("/");
		if(array.length<start+1){
			throw new IllegalArgumentException("requestURI '"+requestURI+"' not full defined");
		}
		FriendlyURI furi=new FriendlyURI();
		furi.setServiceName(array[start]);
		for(int i=start+1;i<array.length;i++){
			String val=URLDecoder.decode(array[i],"UTF-8").trim();
			furi.addParameter(val);
		}
		
		
		//System.out.println(URLDecoder.decode(schema[2],"UTF-8"));
		return furi;
		
	
		
	}
	public static FriendlyURI parse(StringBuffer requestURL, int start) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return parse(requestURL.toString(),start);
	}
	
	public String joinParameter(String delimit)  {
		// TODO Auto-generated method stub
		if(this.getParameter()==null){
			return "";
		}
		int length=this.getParameter().length;
		if(length==0l){
			return "";
		}
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<length;i++){
			if(i>0){
				sb.append(delimit);
			}
			sb.append(getParameter()[i]);
			
		}
		return sb.toString();
	}
	public String toString()  {
		// TODO Auto-generated method stub
	
		StringBuffer sb=new StringBuffer(100);
		sb.append(this.getServiceName());
		sb.append("(");
		sb.append(joinParameter(","));
		sb.append(");");
		return sb.toString();
	}
	public int getParameterLength() {
		// TODO Auto-generated method stub
		if(parameters==null){
			return 0; 
		}
		return this.parameters.size();
	}



}
