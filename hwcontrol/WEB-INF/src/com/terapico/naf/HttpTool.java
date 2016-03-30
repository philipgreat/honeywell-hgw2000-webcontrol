package com.terapico.naf;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HttpTool {
	
	public String getHeader(URL url) throws Exception{				
		StringBuilder stringBuilder=new StringBuilder();
		
		URLConnection conn = url.openConnection();
	 
		//get all headers
		Map<String, List<String>> map = conn.getHeaderFields();
		for (Map.Entry<String, List<String>> entry : map.entrySet()) {
			if(entry.getKey() ==null){
				continue;
			}
			stringBuilder.append( entry.getKey() +": "+ entry.getValue().get(0));
			stringBuilder.append("\r\n");
		}
		
		return stringBuilder.toString();
		
	}
	
	protected String getValueInExpr(String contentType, String prefix)
	{		
		Pattern pattern = Pattern.compile(prefix+"=\\S+(;)?");
		Matcher matcher = pattern.matcher(contentType);
		if(!matcher.find()){
			return null;
		}
		String temp=matcher.group();
		temp=temp.replace(prefix+"=", "");
		temp=temp.replace(";", "");
		return temp;
	}
	protected String getEncoding(String contentType)
	{		
		String encoding=getValueInExpr(contentType,"charset");
		if(encoding!=null){
			return encoding;
		}
		encoding=getValueInExpr(contentType,"encoding");
		if(encoding!=null){
			return encoding;
		}
		return "UTF-8";		
	}
	
	public String getContent(URL url) throws Exception{				
		StringBuilder stringBuilder=new StringBuilder();
		
		URLConnection conn = url.openConnection();
		
		String encoding=null;
		
		//get all headers
		Map<String, List<String>> map = conn.getHeaderFields();
		for (Map.Entry<String, List<String>> entry : map.entrySet()) {
			if(entry.getKey() ==null){
				continue;
			}
			stringBuilder.append( entry.getKey() +": "+ entry.getValue().get(0));
			stringBuilder.append("\r\n");
			if("Content-Type".equalsIgnoreCase(entry.getKey())){
				encoding=getEncoding(entry.getValue().get(0));				
			}
			//System.out.println(encoding);
			
		}
		BufferedReader reader=new BufferedReader(new InputStreamReader(conn.getInputStream(),encoding));
		String line;
	    while ((line = reader.readLine()) != null) {
	    	stringBuilder.append(line);
	    	stringBuilder.append("\r\n");
	    }		
		
		return stringBuilder.toString();
		
	}
	
}
