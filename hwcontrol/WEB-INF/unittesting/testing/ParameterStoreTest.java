package testing;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.terapico.mongodb.MangoTool;
import com.terapico.naf.parameter.Parameter;
import com.terapico.naf.parameter.ParameterManager;

public class ParameterStoreTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		Object[] object1 = { new URI("http://news.163.com"), new URI("http://news.163.com"), };
		// Parameter parameter = Parameter.newInstance(object1);
		MangoTool.peekObject(object1[0], 10);
		ParameterManager manager = new ParameterManager();
		manager.saveParameters(object1);
		List<Parameter> parameterList = manager.getParametersByType(URI.class);
		
		MongoClient mongoClient = new MongoClient("localhost", 27017);

		DB db = mongoClient.getDB("mydb");
		DBCollection coll = db.getCollection("testCollection");
		
		coll.remove(new BasicDBObject());

		DBObject basicObject = MangoTool.getDBObjectFromBean(parameterList);
		//coll.insert(basicObject);
		System.out.println(parameterList);
		
		DBCursor cursor = coll.find();
		try {
			while (cursor.hasNext()) {

				DBObject doc=cursor.next();

				Object obj = MangoTool.getBeanFromDBObject(doc);
				System.out.println(obj.getClass().getName() + ":" + obj);
				
				List list=(List)obj;
				for(Object element:list){
					
					System.out.println(element.getClass().getName() + ":" + element);
					Parameter p=(Parameter)element;
					MangoTool.peekObject(p, 10);
					System.out.println(p.getValue().toString());
				}
				
				

			}
		} finally {
			cursor.close();
		}
		
		
		
		
	}

}
