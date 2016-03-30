package com.terapico.naf.parameter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ParameterManager {
	Map<Type, List<Parameter>> parameters;

	public Map<Type, List<Parameter>> getParameters() throws Exception {
		if(parameters==null){			
			parameters=new HashMap<Type, List<Parameter>> ();
		}
		return parameters;
	}

	public void setParameters(Map<Type, List<Parameter>> parameters) {
		this.parameters = parameters;
	}

	public List<Parameter> getParametersByType(Type clazz) throws Exception {
		if(parameters==null){
			return new ArrayList<Parameter>();
		}
		return parameters.get(clazz);
	}
	public List<Parameter> getParametersByName(String parameterName) throws Exception{
		
		return null;
	}

	public void saveParameters(Object[] inputParameters)throws Exception {
		
		
	}
	

	
	public void saveParameters(Type[] types,Object[] inputParameters) throws Exception{

		

	}

	public void saveParameters(String[] names, Object[] inputParameters) throws Exception {
		// TODO Auto-generated method stub
	
	}

	public void saveParameters(Type[] types, String names, Object[] inputParameters) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void saveParameters(Type[] types, String[] parameterNames, Object[] inputParameters) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public List<Parameter> findParameter(String type, String name) throws Exception{
		// TODO Auto-generated method stub
		return null;
	}

	public void removeParameter(String string) {
		// TODO Auto-generated method stub
		
	}



}
