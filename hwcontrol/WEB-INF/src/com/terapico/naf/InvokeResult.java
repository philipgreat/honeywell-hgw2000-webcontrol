package com.terapico.naf;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class InvokeResult extends BaseInvokeResult{

	
	public static BaseInvokeResult createInstance(Object actualResult, Method method, Object []parameters){
		InvokeResult result=new InvokeResult();
		result.setMethod(method);
		result.setResult(actualResult);
		result.setParameters(parameters);
		return result;
	}

	public String getRenderKey() {
		
		if(!this.isGenericResult()){
			throw new IllegalStateException("Should not call  getRenderKey() when not a parameter return type");
		}
		
		
		Type type=this.getMethod().getGenericReturnType();		
		ParameterizedType parameterReutrnType=(ParameterizedType)type;		
		Type []types= parameterReutrnType.getActualTypeArguments();
		String  parameterTypeExpr=joinParametersTypes(types,'_');
		String returnTypeExpr=this.getMethod().getReturnType().getSimpleName();
		return parameterTypeExpr+"$"+returnTypeExpr;
		
		//System.out.println(method.getReturnType());
		
	}
	protected static String joinParametersTypes(Type []types,char connectChar)
	{
		StringBuilder stringBuilder=new StringBuilder();
		for(int i=0;i<types.length;i++){
			if(i>0){
				stringBuilder.append(connectChar);
			}		
			Class clazz=(Class)types[i];
			stringBuilder.append(clazz.getSimpleName());
		}
		return stringBuilder.toString();
	}
	
	
	
}
