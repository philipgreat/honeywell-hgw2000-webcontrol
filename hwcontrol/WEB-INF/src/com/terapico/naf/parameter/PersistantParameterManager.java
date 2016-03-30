package com.terapico.naf.parameter;

import java.lang.reflect.Type;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.terapico.mongodb.MangoTool;
import com.terapico.naf.ExpressionBeanTool;

public class PersistantParameterManager extends ParameterManager {

	private DB database;

	private DBCollection collection;

	@Override
	public Map<Type, List<Parameter>> getParameters() throws Exception {
		// find all types first
		// then find one by one

		List<PersistantParameter> parameters = MangoTool.findAllBeans(collection);
		Map<Type, List<Parameter>>storedParameters=new HashMap<Type, List<Parameter>> ();
		for (PersistantParameter parameter : parameters) {

			String className = parameter.getType();
			Type type =ExpressionBeanTool.getTypeFromString(className);

			List<Parameter> parameterGroup = storedParameters.get(type);

			if (parameterGroup == null) {
				parameterGroup = new ArrayList<Parameter>();
				parameterGroup.add(parameter);
				storedParameters.put(type, parameterGroup);
				continue;
			}

			if (parameterGroup.indexOf(parameter) >= 0) {
				// add count;
				int index = parameterGroup.indexOf(parameter);
				Parameter existingParameter = parameterGroup.get(index);
				existingParameter.increaseUsedCount();
				continue;
			}
			parameterGroup.add(parameter);

		}

		return storedParameters;
	}

	public PersistantParameterManager() throws UnknownHostException {

		MongoClient mongoClient = new MongoClient("localhost", 27017);

		this.database = mongoClient.getDB("mydb");
		this.collection = database.getCollection("testCollection");

	}

	public List<Parameter> getParametersByType(Type type) throws Exception {

		Class<?> clazz = (Class<?>) type;
		BasicDBObject query = new BasicDBObject();
		query.put("type", clazz.getName());
		
		BasicDBObject sortCondition = new BasicDBObject();
		sortCondition.put("lastUsedTime", -1);
		//collection.createIndex(index);
		List<Parameter> retList = MangoTool.findBeans(collection, query,sortCondition);

		return retList;

	}
	
	
	public List<Parameter> getParametersByName(String parameterName) throws Exception {

		BasicDBObject query = new BasicDBObject();
		query.put("name", parameterName);
		
		BasicDBObject sortCondition = new BasicDBObject();
		sortCondition.put("lastUsedTime", -1);
		//collection.createIndex(index);
		List<Parameter> retList = MangoTool.findBeans(collection, query,sortCondition);

		return retList;

	}
	
	public List<Parameter> findParameter(String parameterType, String parameterName) throws Exception {

		BasicDBObject query = new BasicDBObject();
		query.put("name", parameterName);
		query.put("type", parameterType);
		
		
		BasicDBObject sortCondition = new BasicDBObject();
		sortCondition.put("lastUsedTime", -1);
		//collection.createIndex(index);
		List<Parameter> retList = MangoTool.findBeans(collection, query,sortCondition);

		return retList;

	}
	

	public DBCollection getCollection() {
		return this.collection;
	}

	
	public void removeParameter(String persistantId) {
		// TODO Auto-generated method stub
		BasicDBObject deleteQuery = new BasicDBObject();
		deleteQuery.put("_id", new ObjectId(persistantId));
		this.getCollection().remove(deleteQuery);
		
		System.out.println("try to delete "+ persistantId);
	}
	
	
	@Override
	public void saveParameters(Type[] types, String[] parameterNames, Object[] inputParameters) throws Exception {

		for (int i = 0; i < types.length; i++) {
			Object object = inputParameters[i];
			if (object == null) {
				continue;
			}
			if("".equals(object.toString())){
				continue;
			}
			Type type = types[i];
			String name=parameterNames[i];
			List<Parameter> parameters = getParametersByType(type);
			PersistantParameter parameter = PersistantParameter.newInstance(type, name,object);

			int index = parameters.indexOf(parameter);
			logln("index: parameters.indexOf(parameter) "+index);
			if (index < 0) {
				// not fount, this parameter is not exist
				DBObject newDBObject = MangoTool.getDBObjectFromBean(parameter);
				this.getCollection().insert(newDBObject);
				
				continue;
			}

			PersistantParameter oldParameter = (PersistantParameter) parameters.get(index);
			
			BasicDBObject deleteQuery = new BasicDBObject();
			deleteQuery.put("_id", new ObjectId(oldParameter.getPersistantId()));
			this.getCollection().remove(deleteQuery);
			oldParameter.increaseUsedCount();
			oldParameter.setLastUsedTime(new java.util.Date());
			DBObject newObject = MangoTool.getDBObjectFromBean(oldParameter);
			
			this.getCollection().insert(newObject);

			// this.getCollection().save(dbObject);

		}

	}

	private void logln(String string) {
		// TODO Auto-generated method stub
		System.out.println(string);
	}

}
