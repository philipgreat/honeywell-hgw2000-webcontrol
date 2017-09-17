package com.challenge.cube;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class CubeReponseBodyDeserializer extends StdDeserializer<CubeReponseBody> {
	
	public CubeReponseBodyDeserializer() { 
        this(null); 
    } 
	protected CubeReponseBodyDeserializer(Class<?> vc) {
		super(vc);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public CubeReponseBody deserialize(JsonParser parser, DeserializationContext context)
			throws IOException, JsonProcessingException {
		CubeReponseBody body = new CubeReponseBody();
		JsonNode node = parser.getCodec().readTree(parser);
		body.setAction(node.get("action").asText());
		body.setSubaction(node.get("subaction").asText());
		JsonNode errorNode =node.get("errorcode");
		if(errorNode!=null){
			body.setErrorCode(node.get("errorcode").asInt(Integer.MAX_VALUE));
			return body;
		}
		//when it null
		
		JsonNode deviceLoopMapNode =node.get("deviceloopmap");
		if(deviceLoopMapNode==null){
			return body;
		}
		if(!deviceLoopMapNode.isArray()){
			return body;
		}
		if(deviceLoopMapNode.size()<1){
			return body;
		}
		
		JsonNode firstDeviceNode = deviceLoopMapNode.get(0);
		//首先要支持只有一个设备的情况，所以有一个为0 的结果就认为是正确的
		
		JsonNode subErrorNode =firstDeviceNode.get("errorcode");
		if(subErrorNode == null){
			return body;
		}
		body.setErrorCode(subErrorNode.asInt(Integer.MAX_VALUE));
		return body;

	}
	
}
