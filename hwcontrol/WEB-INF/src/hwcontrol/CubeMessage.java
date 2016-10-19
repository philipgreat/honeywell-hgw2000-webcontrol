package hwcontrol;

import com.google.gson.JsonObject;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Created by philip on 16-10-19.
 */
public class CubeMessage {


    final String FIXED_HEAD="PHONE_CUBE_PROTOCOL";

    private String messageBody;
    private String messageId;
    private String action;
    private String subAction;

    private StringBuffer bodyBuffer;
    private JsonObject bodyJson;

    protected int getReqSize(String jsonString){
        int fixedHeaderSize = FIXED_HEAD.getBytes(StandardCharsets.US_ASCII).length;
        int encryptFlagSize = 1;
        int bodyLengthSize=4;
        int bodyLength = jsonString.getBytes(StandardCharsets.UTF_8).length;

        return fixedHeaderSize + encryptFlagSize + bodyLengthSize + bodyLength;


    }

    private static String toHexString(byte[] fieldData) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fieldData.length; i++) {
            int v = (fieldData[i] & 0xFF);
            if (v <= 0xF) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString();
    }

    public void printBuffer(byte [] bytes)
    {
        System.out.println(toHexString(bytes));
    }
    public byte [] constructRequest(String jsonString){

        /*Cube 接口文档
        固定头PHONE_CUBE_PROTOCOL:
        加密算法(1 Bytes)
        Bodylength(4Bytes)
        Json protocol*/

        ByteBuffer buffer= ByteBuffer.allocate(getReqSize(jsonString));

        buffer.put(FIXED_HEAD.getBytes(StandardCharsets.US_ASCII));
        buffer.put((byte)0); //fixed value for now
        int bodyLength = jsonString.getBytes(StandardCharsets.UTF_8).length;
        buffer.putInt(bodyLength);
        buffer.put(jsonString.getBytes(StandardCharsets.UTF_8));
        return buffer.array();
    }


    /*{
  "msgid":"{messageid}",
  "action":"request",
  "subaction":" heartbeat"
}
*/
    public byte[] buildHeartBeatRequest(){

        return  withMessageId().withRequest().withSubAction("heartbeat").done();
    }

    public byte[] done()
    {

        final String body = body();
        return constructRequest(body);
    }
    public String body()
    {
        if(bodyJson == null){
            throw new IllegalStateException("Not having any parameter in it yet! bodyJson = null");
        }
        return this.bodyJson.toString();

    }

    protected CubeMessage withMessageId(String value){
        return this.withProperty("msgid",value);
    }
    protected CubeMessage withMessageId(){
        return this.withMessageId(System.currentTimeMillis()+"");
    }
    protected CubeMessage withAction(String value){
        return this.withProperty("action",value);
    }
    protected CubeMessage withRequest(){
        return this.withAction("request");
    }
    protected CubeMessage withSubAction(String subaction){
        return this.withProperty("subaction",subaction);
    }

    protected CubeMessage withProperty(String property, String value){

        if(bodyJson == null){
            bodyJson = new JsonObject();
        }
        bodyJson.addProperty(property,value);
        return this;
    }

    public void setBody(String body)
    {
        this.messageBody = body;
    }
    public CubeMessage parse(byte[] messageBytes){


        CubeMessage message = new CubeMessage();

        ByteBuffer buffer = ByteBuffer.wrap(messageBytes);

        byte[] fixHeadBytes = new byte[FIXED_HEAD.length()];

        buffer.get(fixHeadBytes);

        String fixHead = new String(fixHeadBytes, StandardCharsets.US_ASCII);

        if(!FIXED_HEAD.equals(fixHead)){
            throw new IllegalArgumentException("Received a broken message, the fix header is: "+ fixHead);
        }

        //System.out.println(fixHead);
        byte encryptIndicator = buffer.get();

        if(encryptIndicator != 0){//not supported other than 0;
            throw new IllegalArgumentException("Received a broken message, the encrypt way "+encryptIndicator+" is not supported. It is: "+ fixHead);
        }
        int messageLength = buffer.getInt();

        byte[] bodyBytes = new byte[messageLength];

        buffer.get(bodyBytes);

        String bodyValue = new String(bodyBytes, StandardCharsets.UTF_8);
        message.setBody(bodyValue);
        //System.out.println(bodyValue);

        return message;
    }



    public String getBody()
    {

        return this.messageBody;
    }



}
