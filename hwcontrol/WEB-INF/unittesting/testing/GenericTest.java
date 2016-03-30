package testing;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.terapico.naf.parameter.ParameterManager;
public class GenericTest {
	  List<String> stringList = new ArrayList<String>();
	    List<Integer> integerList = new ArrayList<Integer>();

	    public static void main2(String... args) throws Exception {
	        Field stringListField = GenericTest.class.getDeclaredField("stringList");
	        ParameterizedType stringListType = (ParameterizedType) stringListField.getGenericType();
	        Class<?> stringListClass = (Class<?>) stringListType.getActualTypeArguments()[0];
	        System.out.println(stringListClass); // class java.lang.String.

	        Field integerListField = GenericTest.class.getDeclaredField("integerList");
	        ParameterizedType integerListType = (ParameterizedType) integerListField.getGenericType();
	        Class<?> integerListClass = (Class<?>) integerListType.getActualTypeArguments()[0];
	        System.out.println(integerListClass); // class java.lang.Integer.
	        
	       
	        
	    }
	    public static void main(String... args) throws Exception {
	       

	        Field integerListField = GenericTest.class.getDeclaredField("integerList");
	        ParameterizedType integerListType = (ParameterizedType) integerListField.getGenericType();
	        Class<?> integerListClass = (Class<?>) integerListType.getActualTypeArguments()[0];
	        System.out.println(integerListClass); // class java.lang.Integer.
	        
	    	
	        
	    }
}


