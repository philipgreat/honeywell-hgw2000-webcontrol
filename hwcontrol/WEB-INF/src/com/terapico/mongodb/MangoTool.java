package com.terapico.mongodb;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class MangoTool {

	
	public static  <T> List<T> findBeans(DBCollection collection,DBObject query,DBObject sortCondition, int limit) throws Exception
	{
		
		List<T> retList=new ArrayList<T>();
		DBCursor cursor=collection.find(query).sort(sortCondition).limit(limit);
		try {
			while (cursor.hasNext()) {

				DBObject doc=cursor.next();
				T obj =(T) MangoTool.getBeanFromDBObject(doc);
				retList.add(obj);			
			}
		} finally {
			cursor.close();
		}		
		return retList;		
	}
	
	public static  <T> List<T> findBeans(DBCollection collection,DBObject query,DBObject sortCondition) throws Exception
	{
		
		List<T> retList=new ArrayList<T>();
		DBCursor cursor=collection.find(query).sort(sortCondition).limit(16);
		try {
			while (cursor.hasNext()) {

				DBObject doc=cursor.next();
				T obj =(T) MangoTool.getBeanFromDBObject(doc);
				retList.add(obj);			
			}
		} finally {
			cursor.close();
		}		
		return retList;		
	}
	
	public static  <T> List<T> findBeans(DBCollection collection,DBObject query) throws Exception
	{
		
		List<T> retList=new ArrayList<T>();
		DBCursor cursor=collection.find(query);
		try {
			while (cursor.hasNext()) {

				DBObject doc=cursor.next();
				T obj =(T) MangoTool.getBeanFromDBObject(doc);
				retList.add(obj);			
			}
		} finally {
			cursor.close();
		}		
		return retList;		
	}
	

	
	public  static  <T> List<T> findAllBeans(DBCollection collection) throws Exception
	{
		
		
		return findBeans(collection,null);		
	}
	
	public static Object findOneBean(DBCollection collection,DBObject query) throws Exception
	{
		
		
		DBCursor cursor=collection.find(query);
		try {
			if (!cursor.hasNext()) {
				return null;	
			}
			DBObject doc=cursor.next();
			Object obj = MangoTool.getBeanFromDBObject(doc);
			return obj;	
			
		} finally {
			cursor.close();
		}

		
	}
	
	public static void peekObject(Object object,Integer level) throws IntrospectionException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		level++;
		if (object == null) {
			level--;
			System.out.println("object is null");
			return;
		}
		if (isBasicType(object)) {
			showProperty(level,"root",object);;
			// doc.put(propName+":class",
			// returnObject.getClass().getName());
			level--;
			return;
		}
		BeanInfo bi = Introspector.getBeanInfo(object.getClass());
		PropertyDescriptor[] pds = bi.getPropertyDescriptors();
		
		for (int i = 0; i < pds.length; i++) {
			PropertyDescriptor propertyDescriptor=pds[i];
			String propName = propertyDescriptor.getName();
			if ("class".equals(propName)) {				
				continue;
			}
			Method readMethod = propertyDescriptor.getReadMethod();
			Object returnObject = readMethod.invoke(object, new Object[0]);
			if (null == returnObject) {
				showProperty(level,propName,null);
				continue;
			}
			if (isBasicType(returnObject)) {
				showProperty(level,propName,returnObject);;
				// doc.put(propName+":class",
				// returnObject.getClass().getName());
				continue;
			}
			if (isArrayOfBasicType(returnObject)) {
				Object [] objarray=(Object [])returnObject;
				int x=0;
				showProperty(level,propName);
				for(Object item:objarray){
					
					showProperty(level+1,"["+x+"]",item);;
					x++;
				}
				
				continue;
			}
			if (isArrayOfComponentType(returnObject)) {
				//TODO add component type processing here	
				Object [] collectionObject=(Object[])returnObject;
					
				showProperty(level,propName+""); 
				int x=0;
				for(Object item:collectionObject){					
					if(isBasicType(item)){						
						showProperty(level+1,"["+x+"]",item);;
						x++;
						continue;
					}
					peekObject(item,level);
					x++;
				}				
				continue;
				
			}
			
			if (isTypeOfCollection(returnObject)) {
				//list and set
				Collection collectionObject=(Collection)returnObject;
								
				int x=0;
				for(Object item:collectionObject){
					
					if(isBasicType(item)){
						
						showProperty(level+1,"("+x+")",item);;
						x++;
						continue;
					}
					peekObject(item,level);
					x++;
				}
				
				continue;
			}
			if (isTypeOfMap(returnObject)) {
				//hashmap, hashtable
				Map mapObject=(Map)returnObject;				
				Collection keyCollection=mapObject.keySet();
				for(Object key:keyCollection){
					Object value=mapObject.get(key);
					if(isBasicType(value)){
						showProperty(level+1,"'"+key+"'",value);;
						continue;
					}
					showProperty(level+1,"'"+key+"'");					
					peekObject(returnObject,level);
				}				
				continue;
			}
			
			showProperty(level,propName);
			peekObject(returnObject,level);
		}
		level--;
	}
	public static void showProperty(int level,String key,Object value){
		repeat('\t',level-1);
		System.out.println(key+": "+value);
	}
	public static void showProperty(int level,String key){
		repeat('\t',level-1);
		System.out.println(key+":");
	}
	public static void repeat(char ch, int times){
		for(int i=0;i<times;i++)System.out.print(ch);
	}
	
	public static DBObject getDBObjectFromCollectionMapBean(Object returnObject) throws Exception{
		
		
		if (isArrayOfComponentType(returnObject)) {
			//TODO add component type processing here	
			Object [] collectionObject=(Object[])returnObject;
			BasicDBList dbList=new BasicDBList();				
			for(Object item:collectionObject){
				if(isBasicType(item)){
					dbList.add(item);
					continue;
				}
				dbList.add(getDBObjectFromBean(item));
			}				
			//doc.put(propName, dbList);
			return dbList;
		}
		if (isTypeOfCollection(returnObject)) {
			//list and set
			Collection collectionObject=(Collection)returnObject;
			BasicDBList dbList=new BasicDBList();				
			for(Object item:collectionObject){
				if(isBasicType(item)){
					dbList.add(item);
					continue;
				}
				dbList.add(getDBObjectFromBean(item));
			}
			//dbList.put("class", returnObject.getClass().getName());
			return dbList;
		}
		if (isTypeOfMap(returnObject)) {
			//list and set
			Map mapObject=(Map)returnObject;
			BasicDBObject subDoc = new BasicDBObject();
			subDoc.put("class",returnObject.getClass().getName());
			Collection keyCollection=mapObject.keySet();
			for(Object key:keyCollection){
				Object value=mapObject.get(key);
				if(isBasicType(value)){
					subDoc.put(key.toString(),value);
					continue;
				}
				subDoc.put(key.toString(),getDBObjectFromBean(returnObject));
			}				
			return subDoc;			
		}

		
		
		return null;
	}
	
	
	
	
	public static boolean isComplexCollectionType(Object object) 
	{
		
		if (isArrayOfComponentType(object)) {
			return true;
		}
		if (isTypeOfCollection(object)) {
			//list and set
			return true;
		}
		if (isTypeOfMap(object)) {
			return true;
		}
		return false;
		
		
	}
	public static boolean isComplexCollectionByClass(Class clazz) 
	{
				
		if (isArrayOfComponentClass(clazz)) {
			return true;
		}
		if (Collection.class.isAssignableFrom(clazz)) {
			//list and set
			return true;
		}
		if(Map.class.isAssignableFrom(clazz)){
			return true;
		}
		return false;
		
		
	}
	
	public static DBObject getDBObjectFromBean(Object object) throws Exception {
		if (object == null) {
			throw new IllegalArgumentException("getDBObjectFromBean(Object object)'s parameter 'object' can not be null.");
		}
		
		BasicDBObject doc = new BasicDBObject();
		
		if(isBasicTypeByClass(object.getClass())){
			doc.put("class", object.getClass().getName());
			doc.put("value", object);
			return doc;
		}
		if (isArrayOfBasicType(object)) {			
			doc.put("value", object);
			return doc;
		}
		
		//DBObject dbObject=getDBObjectFromCollectionMapBean(object);
		if(isComplexCollectionType(object)){
			
			DBObject dbObject=getDBObjectFromCollectionMapBean(object);
			doc.put("class", object.getClass().getName());
			doc.put("value",dbObject );
			
			
			return doc;
			
		}
			
		
		BeanInfo bi = Introspector.getBeanInfo(object.getClass());
		PropertyDescriptor[] pds = bi.getPropertyDescriptors();
		
		for (int i = 0; i < pds.length; i++) {
			PropertyDescriptor propertyDescriptor=pds[i];
			String propName = propertyDescriptor.getName();
			if ("class".equals(propName)) {
				doc.put("class", object.getClass().getName());
				continue;
			}
			Method readMethod = propertyDescriptor.getReadMethod();
			if(readMethod==null){
				continue;
			}
			
			Object returnObject = readMethod.invoke(object, new Object[0]);
			if (null == returnObject) {
				//doc.put(propName, returnObject);
				continue;
			}
			if (isBasicType(returnObject)) {
				doc.put(propName, returnObject);
				// doc.put(propName+":class",
				// returnObject.getClass().getName());
				continue;
			}
			if (isArrayOfBasicType(returnObject)) {
				doc.put(propName, returnObject);
				continue;
			}
			
			if(isComplexCollectionType(returnObject)){
				
				DBObject dbObject=getDBObjectFromCollectionMapBean(returnObject);
				doc.put(propName, dbObject);
				
			}
			
			doc.put(propName, getDBObjectFromBean(returnObject));
		}
		return doc;
	}
	
	
	
	protected static boolean isTypeOfCollection(Object object) {
		if (object instanceof Collection){
			return true;
		}
		return false;
	}
	protected static boolean isTypeOfMap(Object object) {
		if (object instanceof Map){
			return true;
		}
		return false;
	}
	protected static boolean isArrayOfBasicType(Object object) {
		if (!object.getClass().isArray()) {
			return false;
		}
		Class clazz = object.getClass().getComponentType();
		if (isBasicTypeByClass(clazz)) {
			return true;
		}
		return false;
	}
	protected static boolean isArrayOfComponentType(Object object) {
		if (!object.getClass().isArray()) {
			return false;
		}
		Class clazz = object.getClass().getComponentType();
		if (isBasicTypeByClass(clazz)) {
			return false;
		}
		return true;
	}
	protected static boolean isArrayOfComponentClass(Class clazz) {
		if (!clazz.isArray()) {
			return false;
		}
		Class compentClass = clazz.getComponentType();
		if (isBasicTypeByClass(compentClass)) {
			return false;
		}
		return true;
	}
	public static Object getBeanFromDBObject(DBObject dbObject) throws Exception {
		if (dbObject == null) {
			throw new IllegalArgumentException("getBeanFromDBObject(BasicDBObject dbObject)'s parameter 'object' can not be null.");
		}
		//System.out.println(dbObject);
		String className = dbObject.get("class").toString();
		
		if(null==className){
			//this must be an array
			return dbObject.get("value");
		}
		
		Class clazz=(Class)Class.forName(className);
		if(isBasicTypeByClass(clazz)){
			return dbObject.get("value");
		}
		
		if(clazz==URL.class){
			//{ "class" : "java.util.Stack" , "value" : 
			//[ { "class" : "com.terapico.naf.parameter.Parameter" , "lastUsedTime" : { "$date" : "2014-06-06T17:34:11.407Z"} , 
			//"usedCount" : 2 , "value" : { "authority" : "news.163.com" , "class" : "java.net.URL" , "content" : { "class" : "sun.net.www.protocol.http.HttpURLConnection$HttpInputStream"} , "defaultPort" : 80 , "file" : "" , "host" : "news.163.com" , "path" : "" , "port" : -1 , "protocol" : "http"}}]}
			String host=(String)dbObject.get("host");
			//URL(String protocol, String host, int port, String file) 
			String protocol=(String)dbObject.get("protocol");
			String file=(String)dbObject.get("file");			
			int  port=(Integer)dbObject.get("port");
			
			return new URL(protocol,host,port,file);
		}
		if(clazz==URI.class){
			//{ "class" : "java.util.Stack" , "value" : 
			//[ { "class" : "com.terapico.naf.parameter.Parameter" , "lastUsedTime" : { "$date" : "2014-06-06T17:34:11.407Z"} , 
			//"usedCount" : 2 , "value" : { "authority" : "news.163.com" , "class" : "java.net.URL" , "content" : { "class" : "sun.net.www.protocol.http.HttpURLConnection$HttpInputStream"} , "defaultPort" : 80 , "file" : "" , "host" : "news.163.com" , "path" : "" , "port" : -1 , "protocol" : "http"}}]}
			String host=(String)dbObject.get("host");
			//URL(String protocol, String host, int port, String file) 
			String scheme=(String)dbObject.get("scheme");
			String path=(String)dbObject.get("path");			
			int  port=(Integer)dbObject.get("port");
			String userInfo=(String)dbObject.get("userInfo");
			
			String query=(String)dbObject.get("query");
			String fragment=(String)dbObject.get("fragment");
			
			//URI(String scheme, String userInfo, String host, int port, String path, String query, String fragment)
			return new URI(scheme,userInfo,host,port,path,query,fragment);
		}
		
		
		Object object = clazz.newInstance();
		
		
		
		//need to initialize the element type the values, not the 
		if(isComplexCollectionByClass(clazz)){
			
			//return dbObject.get("value");
			//the code is not right here, the value should be parsed into object respectively.
			if(isTypeOfCollection(object)){
				Collection collection=(Collection)object;
				
				//getBeanFromDBObject
				BasicDBList dbList=(BasicDBList)dbObject.get("value");;
				for(Object obj:dbList){
					collection.add(getBeanFromDBObject((DBObject)obj));
				}
				return object;
				
			}
			if(isTypeOfMap(object)){
				Map mapBean=(Map)object;
				Map mapDBObject=(Map)dbObject.get("value");
				Collection keyCollection=mapDBObject.keySet();
				for(Object key:keyCollection){
					Object dbObjectValue=mapDBObject.get(key);
					if(isBasicType(dbObjectValue)){
						mapBean.put(key.toString(),dbObjectValue);
						continue;
					}
					mapBean.put(key.toString(),getBeanFromDBObject((DBObject)dbObjectValue));
				}	
				return object;
			}

			
		}
		
		
		
		BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

		for (int i = 0; i < propertyDescriptors.length; i++) {
			PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
			String propName = propertyDescriptor.getName();
			
			if ("class".equals(propName)) {
				continue;
			}
			
			
			
			
			Method writeMethod = propertyDescriptor.getWriteMethod();
			if(writeMethod==null){
				continue;
			}
			if ("persistantId".equals(propName)) {
				String internalId= dbObject.get("_id").toString();
				writeMethod.invoke(object, new Object[] { internalId });
				continue;
			}
			Object value = dbObject.get(propName);
			if (value == null) {
				continue;
			}
			if (isBasicType(value)) {
				writeMethod.invoke(object, new Object[] { value });
				continue;
			}
			
			Class propertyType = propertyDescriptor.getPropertyType();
			String componentClass=(String)dbObject.get("class");
			
			//System.out.println(propName+"//"+propertyType+"//"+componentClass);
			if (propertyType.isArray()) {
				// this includes set	
				//System.out.println(propertyType);
				BasicDBList dbList=(BasicDBList)value;
				Object arrayObject=Array.newInstance( propertyType.getComponentType(),dbList.size());
				int index=0;
				for(Object obj:dbList){
					if (isBasicType(obj)) {
						Array.set(arrayObject, index, obj);
						index++;
						continue;
					}
					Array.set(arrayObject, index, getBeanFromDBObject((DBObject)obj));
					
					index++;
				}
				writeMethod.invoke(object, new Object[] { arrayObject });
				continue;
			}
			
			if (Collection.class.isAssignableFrom(propertyType)) {
				// this includes set
				Collection collection=(Collection)Class.forName(componentClass).newInstance();
				BasicDBList dbList=(BasicDBList)value;
				for(Object obj:dbList){
					if (isBasicType(value)) {
						collection.add(obj);
						continue;
					}
					collection.add(getBeanFromDBObject((DBObject)obj));
				}
				writeMethod.invoke(object, new Object[] { collection });
				continue;
			}
			
			if (Map.class.isAssignableFrom(propertyType)) {
				// this includes set
				Map mapBean=(Map)Class.forName(componentClass).newInstance();
				Map mapDBObject=(Map)dbObject;
				Collection keyCollection=mapDBObject.keySet();
				for(Object key:keyCollection){
					Object dbObjectValue=mapDBObject.get(key);
					if(isBasicType(dbObjectValue)){
						mapBean.put(key.toString(),dbObjectValue);
						continue;
					}
					mapBean.put(key.toString(),getBeanFromDBObject((DBObject)dbObjectValue));
				}	
				writeMethod.invoke(object, new Object[] { mapBean });
				continue;
			}
			if (!(value instanceof DBObject)) {
				continue;
			}
			BasicDBObject subDBObject = (BasicDBObject) value;
			writeMethod.invoke(object, new Object[] { getBeanFromDBObject(subDBObject)  });

		}
		return object;
	}
	

	protected static boolean isBasicTypeByClass(Class clazz) {

		if (clazz == String.class) {
			return true;
		}
		if (clazz == Number.class) {
			return true;
		}
		if (clazz == Byte.class) {
			return true;
		}
		// java.lang.Byte (implements java.lang.Comparable<T>)
		// java.lang.Double (implements java.lang.Comparable<T>)
		// java.lang.Float (implements java.lang.Comparable<T>)
		// java.lang.Integer (implements java.lang.Comparable<T>)
		// java.lang.Long (implements java.lang.Comparable<T>)
		// java.lang.Short (implements java.lang.Comparable<T>)
		if (clazz == Double.class) {
			return true;
		}
		if (clazz == Float.class) {
			return true;
		}
		if (clazz == Integer.class) {
			return true;
		}
		if (clazz == Long.class) {
			return true;
		}
		if (clazz == Short.class) {
			return true;
		}
		if (clazz == Boolean.class) {
			return true;
		}
		if (clazz == java.util.Date.class) {
			return true;
		}
		if (clazz == java.sql.Date.class) {
			return true;
		}
		return false;

	}

	protected static boolean isBasicType(Object object) {

		if (object instanceof String) {
			return true;
		}
		if (object instanceof Number) {
			return true;
		}
		if (object instanceof Boolean) {
			return true;
		}
		if (object instanceof Date) {
			return true;
		}

		
		return false;
	}
}
