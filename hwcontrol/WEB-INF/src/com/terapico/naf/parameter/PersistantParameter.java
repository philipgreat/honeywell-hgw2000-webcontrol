package com.terapico.naf.parameter;

import java.lang.reflect.Type;

public class PersistantParameter extends Parameter {
	private String type;
	private String persistantId;

	public String getPersistantId() {
		
		if(persistantId==null){
			return System.currentTimeMillis()+type;
		}
		
		return persistantId;
	}
	public void setPersistantId(String id) {
		persistantId = id;
	}
	public String getType(){
		return this.type;
	}
	public void setType(String type){
		this.type=type;
	}
	public static PersistantParameter newInstance(Type type,Object inputParameter) {
		PersistantParameter parameter = new PersistantParameter();
		parameter.setLastUsedTime(new java.util.Date());
		parameter.setValue(inputParameter);
		parameter.setUsedCount(1);
		Class<?> clazz=(Class<?> )type;
		parameter.setType(clazz.getName());		
		return parameter;
	}
	public static PersistantParameter newInstance(String name, Object inputParameter) {
		// TODO Auto-generated method stub
		
		PersistantParameter parameter = new PersistantParameter();
		parameter.setLastUsedTime(new java.util.Date());
		parameter.setValue(inputParameter);
		parameter.setUsedCount(1);
		//parameter.setType(clazz.getName());	
		parameter.setName(name);
		return parameter;

	}
	public static PersistantParameter newInstance(Type type, String name, Object inputParameter) {
		// TODO Auto-generated method stub
		PersistantParameter parameter = new PersistantParameter();
		parameter.setLastUsedTime(new java.util.Date());
		parameter.setValue(inputParameter);
		parameter.setUsedCount(1);
		Class<?> clazz=(Class<?> )type;
		parameter.setName(name);
		parameter.setType(clazz.getName());		
		return parameter;
	}

}
