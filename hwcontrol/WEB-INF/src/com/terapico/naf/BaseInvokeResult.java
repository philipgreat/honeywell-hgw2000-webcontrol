package com.terapico.naf;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class BaseInvokeResult {
	private Object result;
	private Method method;
	private Object [] parameters;
	private String renderKey;
	
	public static BaseInvokeResult createInstance(Object actualResult,String renderKey){
		BaseInvokeResult result=new BaseInvokeResult();
		result.setRenderKey(renderKey);
		result.setResult(actualResult);
		return result;
	}
	private void setRenderKey(String renderKey) {
		// TODO Auto-generated method stub
		this.renderKey=renderKey;
	}
	public Object getActualResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public Object[] getParameters() {
		return parameters;
	}
	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}
	public String getRenderKey() {		
		return renderKey;		
		//System.out.println(method.getReturnType());
		
	}
	
	public boolean isGenericResult() {
		
		if(renderKey!=null){
			return true;
		}
		
		if(method==null){
			return false;
		}	
		
		Type type=method.getGenericReturnType();
		if(type instanceof ParameterizedType){
			return true;
		}
		
		return false;
	}
}
