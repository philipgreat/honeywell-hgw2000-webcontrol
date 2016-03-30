package testing;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.terapico.naf.parameter.Parameter;
import com.terapico.naf.parameter.ParameterManager;

public class ListTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		Object[] object1 = { new URL("http://news.163.com"), new URL("http://news.163.com"), };
		// Parameter parameter = Parameter.newInstance(object1);

		ParameterManager manager = new ParameterManager();
		manager.saveParameters(object1);
		List<Parameter> parameterList = manager.getParametersByType(URL.class);

		
		
		
		assertNotNull(parameterList);
		assertEquals(1, parameterList.size());

		assertTrue(List.class.isAssignableFrom(ArrayList.class));
		assertTrue(List.class.isAssignableFrom(parameterList.getClass()));
		//assertTrue(type instanceof ParameterizedType);
		
		Method method=ParameterManager.class.getMethod("getParameters", new Class[]{});
		
		Type type=method.getGenericReturnType();
		if(type instanceof ParameterizedType){
			System.out.println("yea!");
		}
		ParameterizedType parameterReutrnType=(ParameterizedType)type;
		
		Type []types= parameterReutrnType.getActualTypeArguments();
		
		//Class<?>  paramType= (Class<?>) parameterReutrnType.getActualTypeArguments()[0];
		
		
		System.out.println(types[0]);
		System.out.println(types[1]);
		
		System.out.println(method.getReturnType().getSimpleName());
	
		
		
	}
	
	public String getCollectionTypeDesc(Object object)
	{
		Class clazz=object.getClass();
		if (clazz.isArray()) {
			return  clazz.getComponentType().getName()+"$Array";
		}
		if (Collection.class.isAssignableFrom(clazz)) {
			Collection collection	=(Collection) object;
			Iterator iter=collection.iterator();
			while(iter.hasNext()){
				Object element=iter.next();
				
				
			}
			
		}	
		
		return "";		
	}
	
	public Class electGenericClass(Collection collection){
		
		if(collection==null){
			throw new IllegalArgumentException("Method electMostSenirClass(Collection collection): collection can not be null");
		}
		if(collection.isEmpty()){
			return Object.class;
		}
		
		Iterator iter=collection.iterator();
		while(iter.hasNext()){
			Object element=iter.next();
			
			
		}
		
		return Object.class;
		
		
	}
	
	public String getCollectionType(Class clazz) {
		if (clazz.isArray()) {
			return "$Array";
		}
		if (Set.class.isAssignableFrom(clazz)) {
			return "$Set";
		}
		if (List.class.isAssignableFrom(clazz)) {
			return "$List";
		}
		if (Collection.class.isAssignableFrom(clazz)) {
			// most the case should not go here!
			return "$Collection";
		}
		if (Map.class.isAssignableFrom(clazz)) {
			return "$Map";
		}

		// clazz.isAssignableFrom(clazz)
		return null;

	}

}

class Demo {

	List<String> list = new ArrayList<String>();

	Collection<String> coll = new ArrayList<String>();

	public static void main(String args[]) {

		Class<Demo> clazz = Demo.class;
		Field[] fields = clazz.getDeclaredFields();

		for (Field field : fields) {

			Type type = field.getGenericType();

			if (type instanceof ParameterizedType) {

				ParameterizedType pType = (ParameterizedType) type;
				Type[] arr = pType.getActualTypeArguments();

				for (Type tp : arr) {
					Class<?> clzz = (Class<?>) tp;
					System.out.println(clzz.getName());
				}
			}
		}
	}
}
