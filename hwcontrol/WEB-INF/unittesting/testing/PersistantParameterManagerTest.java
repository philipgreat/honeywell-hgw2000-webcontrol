package testing;

import static org.junit.Assert.*;

import java.net.UnknownHostException;
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

public class PersistantParameterManagerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetParametersByType() throws UnknownHostException {
		// fail("Not yet implemented");

		MongoClient mongoClient = new MongoClient("localhost", 27017);

		DB db = mongoClient.getDB("mydb");
		DBCollection coll = db.getCollection("testCollection");

		DBCursor cursor = coll.find();
		try {
			while (cursor.hasNext()) {

				DBObject doc = cursor.next();
				System.out.println(doc);

			}
		} finally {
			cursor.close();
		}
		//coll.remove(new BasicDBObject());
	}

	@Test
	public void testSaveParametersTypeArrayObjectArray() {
		// fail("Not yet implemented");
	}

	@Test
	public void testGetCollection() {
		// ail("Not yet implemented");
	}

}
