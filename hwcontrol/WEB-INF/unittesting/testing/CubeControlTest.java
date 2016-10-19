package testing;

import hwcontrol.CubeController;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import hwcontrol.CubeMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by philip on 16-10-19.
 */
public class CubeControlTest {

    @Test
    public void testHBusLights(){

        CubeMessage message=new CubeMessage();

        String body = "00000中0";

        byte[] result =  message.constructRequest(body);
        message.printBuffer(result);

        CubeMessage paredMsg= message.parse(result);
        assertEquals(body,paredMsg.getBody());
        assertEquals(body,paredMsg.getBody());
        message.buildHeartBeatRequest();
        log(message.body());


    }
    private void log(String msg)
    {
        System.out.println(msg);

    }

}
