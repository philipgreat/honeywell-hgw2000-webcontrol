package testing;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.terapico.mongodb.MangoTool;

public class MongoDBTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void insert() throws Exception {

		MongoClient mongoClient = new MongoClient("localhost", 27017);

		DB db = mongoClient.getDB("mydb");
		DBCollection coll = db.getCollection("testCollection");

		DBObject basicObject = MangoTool.getDBObjectFromBean("save simple string");
		coll.insert(basicObject);
		
		basicObject = MangoTool.getDBObjectFromBean(120);
		coll.insert(basicObject);
		
		basicObject = MangoTool.getDBObjectFromBean(120L);
		coll.insert(basicObject);
		
		basicObject = MangoTool.getDBObjectFromBean(120.8877);
		coll.insert(basicObject);
		
		basicObject = MangoTool.getDBObjectFromBean(120.8877D);
		coll.insert(basicObject);

		basicObject = MangoTool.getDBObjectFromBean(new java.util.Date());
		coll.insert(basicObject);

		basicObject = MangoTool.getDBObjectFromBean(new java.sql.Date(0));
		coll.insert(basicObject);

		
		mongoClient.close();

	}

	@Test
	public void test() throws Exception {

		MongoClient mongoClient = new MongoClient("localhost", 27017);

		DB db = mongoClient.getDB("mydb");
		DBCollection coll = db.getCollection("testCollection");

		DBCursor cursor = coll.find();
		try {
			while (cursor.hasNext()) {

				DBObject doc=cursor.next();
				System.out.println(doc);
				Object obj = MangoTool.getBeanFromDBObject(doc);
				System.out.println(obj.getClass().getName() + ":" + obj);
			}
		} finally {
			cursor.close();
		}
		
		coll.remove(new BasicDBObject());
	}

}
