package com.terapico.naf;


import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

import com.google.gson.Gson;

public class ExpressionBeanTool {
	
	static final Map<String,Type> wrappedMap;
	static{
		wrappedMap=new HashMap<String,Type>();
		wrappedMap.put("byte", byte.class);
		wrappedMap.put("char", char.class);
		wrappedMap.put("short", short.class);
		wrappedMap.put("int", int.class);
		wrappedMap.put("long", long.class);
		wrappedMap.put("double", double.class);
		wrappedMap.put("float", float.class);
		
		
		
	}
	public static Type getTypeFromString(String typeName) throws ClassNotFoundException
	{
		Type val=wrappedMap.get(typeName);
		if(val==null){
			
			return Class.forName(typeName);
		}
		return val;
			
	}

	
	public static Object invokeExpr(Object object,String methodName, Object[] parameters) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method method = findMethod(object, methodName, parameters.length);
		if (method == null) {
			String message="'" + methodName+"' with "+parameters.length+" parameter(s) is not supported now!";
			throw new IllegalArgumentException(message);
		}
		Object[] params = getParameters(method.getGenericParameterTypes(), parameters);		
		return method.invoke(object, params);

	}
	public  static  Method findMethod(Object object,String name, int paramLength){
		
		Class clazz = object.getClass();
		Method[] methods = clazz.getDeclaredMethods();		
		for (Method method : methods) {
			if (method.getGenericParameterTypes().length!=paramLength) {
				continue;
			}
			if (!method.getName().equalsIgnoreCase(name)) {
				continue;
			}
			return method;		
		}
		return null;
	}
	

	public static Object[] getParameters(Type[] types, Object[] parameters) {

		int length = parameters.length;
		if (length == 0) {
			return new Object[] {};
		}
		Object[] ret = new Object[length];

		for (int i = 0; i < length; i++) {
			ret[i] = converExprToObject(types[i], parameters[i]);
			//System.out.println(ret[i].getClass() + "" + ret[i]);
		}
		return ret;
	}
	

	public static Object converExprToObject(Type type, Object parameter) {
		
		
		if (type == String.class) {
			return parameter;
		}
		if(!(parameter instanceof String)){
			return parameter;
			//this allow external service to initiate the object directly like File in the context of Web Container
			
		}
		String stringParameter=parameter.toString();
		if (type == int.class || type == Integer.class) {
			return Integer.parseInt(stringParameter);
		}
		if (type == long.class || type == Long.class) {
			return Long.parseLong(stringParameter);
		}
		if (type == float.class || type == Float.class) {
			return Float.parseFloat(stringParameter);
		}
		if (type == double.class || type == Double.class) {
			return Double.parseDouble(stringParameter);
		}
		
		if (type == byte.class || type == Byte.class) {
			return Byte.parseByte(stringParameter);
		}
		
		//if the class has a default constructor and the only parameter is String like URL
		Constructor constructor=getOneStringConstructor((Class)type);
		if(constructor!=null){			
			try {
				return constructor.newInstance(new Object[] { stringParameter });			
			} catch (Exception exception) {
				
			}
		}
		
		
		
		
		if (DateTime.class.isAssignableFrom((Class)type) ) {
			String defaultFormat=System.getProperty("system.types.datetime.format");
			if(defaultFormat==null){
				defaultFormat="yyyy-MM-DD HH:mm:ss";
			}
			DateFormat formatter = new SimpleDateFormat(defaultFormat);
			try {
				DateTime dateTime=new DateTime();				
				java.util.Date date=formatter.parse(stringParameter);
				dateTime.setTime(date.getTime());
				return dateTime;
			} catch (ParseException e) {
				return null;				
			}
		}
		if (java.util.Date.class.isAssignableFrom((Class)type) ) {
			String defaultFormat=System.getProperty("system.types.date.format");
			if(defaultFormat==null){
				defaultFormat="yyyy-MM-DD";
			}
			DateFormat formatter = new SimpleDateFormat(defaultFormat);
			try {
				return formatter.parse(stringParameter);
			} catch (ParseException e) {
				return null;				
			}
		}

		if(!isArrayType(type)){
			//other component type
			//parse it as json
			Gson gson = new Gson();		
			return gson.fromJson(stringParameter,(Class)type);
		}
		
		if(isArrayOfPrimaryType(type)){			
			String subParameters[]=stringParameter.split(";");
			int length=subParameters.length;
			Class typeClazz=(Class)type;
			Class componentClazz=typeClazz.getComponentType();
			Object object = Array.newInstance(componentClazz,length);
			for(int index=0;index<length;index++){				
				Array.set(object,
						index,
						converExprToObject(typeClazz.getComponentType(),subParameters[index]));
			}
			return object;
		}
		//any other should presents as json string, include objects, list of objects.
		//List<Video> videos = gson.fromJson(json, new TypeToken<List<Video>>(){}.getType());
		Gson gson = new Gson();		
		return gson.fromJson(stringParameter,(Class)type);
	}
	
	protected static boolean isArrayType(Type type) {
		Class typeClazz=(Class)type;
		if (typeClazz.isArray()) {
			return true;
		}
		return false;
	}
	
	protected static boolean isArrayOfPrimaryType(Type type) {
		Class typeClazz=(Class)type;
		if (!typeClazz.isArray()) {
			return false;
		}
		Class clazz = typeClazz.getComponentType();
		if (isPrimaryType(clazz)) {
			return true;
		}
		return false;
	}
	
	public static Constructor getOneStringConstructor(Class clazz) {
		Constructor constructors[] = clazz.getDeclaredConstructors();
		
		for(int i=0;i<constructors.length;i++){
			Constructor constructor=constructors[i];			
			Type[] types=constructor.getGenericParameterTypes();			
			if(types.length!=1){
				continue;
			}
			if(types[0]==java.lang.String.class){
				
				return constructor;
			}			
		}
		
		return null;
		
	}
	
	
	public static boolean isPrimaryType(Class clazz) {

		if(clazz.isPrimitive()){
			return true;
		}		
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
		if (clazz == DateTime.class) {
			return true;
		}
		return false;

	}
	public static void invokeAndPrint(Object object, Method method) throws IllegalArgumentException, InvocationTargetException {

		Object ret;
		try {
			ret = method.invoke(object, new Class[] {});
			System.out.println(method.getName() + "=" + ret);
		} catch (IllegalAccessException e) {
			System.out.println(method.getName() + "=@error@");
		}

	}
	
	public static void peekObject(Object object) throws IllegalArgumentException, InvocationTargetException {
		if (object == null) {
			return;
		}
		Class clazz = object.getClass();
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {

			if(!isPrimaryType(method.getReturnType())){
				continue;
			}
			if (method.getGenericParameterTypes().length != 0) {
				continue;
			}
			if (method.getName().startsWith("get")) {
				invokeAndPrint(object, method);
			}
			if (method.getName().startsWith("is")) {
				invokeAndPrint(object, method);
			}
			if (method.getName().startsWith("has")) {
				invokeAndPrint(object, method);
			}
		}

	}
	public static List<String> getParameterNames(Method method) throws IOException {
		Class<?> declaringClass = method.getDeclaringClass();
		ClassLoader declaringClassLoader = declaringClass.getClassLoader();

		 org.objectweb.asm.Type declaringType =  org.objectweb.asm.Type.getType(declaringClass);
		String url = declaringType.getInternalName() + ".class";

		InputStream classFileInputStream = declaringClassLoader.getResourceAsStream(url);
		if (classFileInputStream == null) {
			throw new IllegalArgumentException("The constructor's class loader cannot find the bytecode that defined the constructor's class (URL: " + url + ")");
		}

		ClassNode classNode;
		try {
			classNode = new ClassNode();
			ClassReader classReader = new ClassReader(classFileInputStream);
			classReader.accept(classNode, 0);
		} finally {
			classFileInputStream.close();
		}

		@SuppressWarnings("unchecked")
		List<MethodNode> methods = classNode.methods;
		for (MethodNode methodNode : methods) {
			
			if(!isMatch(methodNode,method)){
				continue;
			}
			
			 org.objectweb.asm.Type[] argumentTypes =  org.objectweb.asm.Type.getArgumentTypes(methodNode.desc);
			List<String> parameterNames = new ArrayList<String>(argumentTypes.length);

			@SuppressWarnings("unchecked")
			List<LocalVariableNode> localVariables = methodNode.localVariables;
			
			if(localVariables.isEmpty()){
				continue;
			}
			
			for (int i = 0; i < argumentTypes.length; i++) {
				// The first local variable actually represents the "this"
				// object
				LocalVariableNode lv=localVariables.get(i + 1);
				//parameterNames.add( org.objectweb.asm.Type.getType(lv.desc).getClassName()+" " + lv.name);
				parameterNames.add( lv.name);
				
			}

			return parameterNames;

		}

		return null;
	}
	public static boolean isMatch(MethodNode methodNode, Method method){
		
		if (!methodNode.name.equals(method.getName())) {
			return false;
		}
		
		org.objectweb.asm.Type [] types= org.objectweb.asm.Type.getArgumentTypes(methodNode.desc);
		Class[] parameterTypes=method.getParameterTypes();
		if(types.length!=parameterTypes.length){
			return false;
		}
		for(int i=0;i<types.length;i++){
			//Type type=types[i];
			
			if(types[i]!= org.objectweb.asm.Type.getType(parameterTypes[i])){
				//return false;
			}
			
		}
		
		return true;
		
		
		
	}
}
