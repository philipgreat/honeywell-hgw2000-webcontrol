package hwcontrol;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MethodIndex {

	private List<Method> methodList;


	public List<Method> getList(Method[] methods) {
		if(this.methodList==null){
			this.methodList=new ArrayList<Method>();
		}
		for(Method method:methods){
			if(isIgnoreMethod(method)){
				continue;
			}
			if(this.methodList.contains(method)){
				continue;
			}
			this.methodList.add(method);
		}
		Collections.sort(this.methodList, new Comparator<Method>() {
	        public int compare (Method d1, Method d2) {	        	
	            return d1.getName().compareTo(d2.getName());
	        }
	    });
		return methodList;
	}
	public List<Method> getList(Class clazz) {
		
		Method[] methods = clazz.getMethods();			
		return getList(methods);
	}
	final static String []IGNORE_METHOS={"equals","getClass","hashCode","notify","notifyAll","toString","wait",};
	protected static boolean isIgnoreMethod(Method method)
	{
		//if(method.getModifiers())
		
		String modifiers = Modifier.toString(method.getModifiers());
		//System.out.println(modifiers);
		
		if(modifiers.contains("static")){
			return true;
		}
		
		
		String name=method.getName();
		int index=Arrays.binarySearch(IGNORE_METHOS, name);
		if(index<0){
			return false;			
		}	
		return true;
		
	}
	public List<Method> getItems() {
		return methodList;
	}
	
}
