package testing;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import hwcontrol.HGW2000Controller;

import org.junit.Test;

public class HGW2000Test {

	public void test() throws NoSuchAlgorithmException,
			UnsupportedEncodingException, IOException {

		HGW2000Controller controller = new HGW2000Controller();

		controller.connectToGateway("admin", "123456", "192.168.0.40", 10099);
		for (int i = 0; i < 10; i++) {
			controller.controlLight(1, 4, 1, 255);
			controller.controlLight(1, 4, 0, 255);

			controller.controlLight(2, 4, 1, 255);
			controller.controlLight(2, 4, 0, 255);

			controller.controlLight(3, 4, 1, 255);
			controller.controlLight(3, 4, 0, 255);

		}

	}

	//@Test
	public void testHBusLights() throws NoSuchAlgorithmException,
			UnsupportedEncodingException, IOException, InterruptedException {

		HGW2000Controller controller = new HGW2000Controller();

		controller.connectToGateway("admin", "123456", "192.168.0.40", 10099);

		for(int i=0;i<10;i++){
			controller.controlHBusLight(1,i%3+1,4,0,255);
			Thread.sleep(20);
			controller.controlHBusLight(1,i%3+1,4,1,255);	
			Thread.sleep(20);

			
		}
		//controller.controlLight(1, 4, 1, 255);

		
		controller.close();
	}
	@Test 
	public void testConstructCommand() throws IOException
	{
		
		String token=null;
		int area=10;
		int loop=1;
		int action=4;
		int onOrOff=1;
		int dimmer=255;
		
		String requestCommand = token + "$cfg,hbuslig," + area + ","
				+ loop + "," + action + "," + onOrOff + "," + dimmer + ",0"
				+ "\n";
		HGW2000Controller controller = new HGW2000Controller();
		String result= controller.construstRequest("cfg", "hbuslig",area,loop,action,onOrOff,dimmer,0);
		
		assertEquals(requestCommand,result);
		
	}
	
	//@Test
	public void testConfiguration() throws NoSuchAlgorithmException,
			UnsupportedEncodingException, IOException, InterruptedException {

		HGW2000Controller controller = new HGW2000Controller();
		controller.init();
		
		//controller.saveConfiguration("admin", "123456", "192.168.0.40", 10099);
		
		
		
		//controller.controlLight(1, 4, 1, 255);

		
		controller.close();
	}
	
	

}
