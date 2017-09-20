package hwcontrol;

import com.challenge.cube.CubeReponseBody;

public class ExecutionResult {
	protected String webCommand;
	protected String sentCommand;
	protected String receivedResponse;
	protected String webResult;
	protected long exectionTime=0;
	protected CubeReponseBody responseBody;
	public String getWebCommand() {
		return webCommand;
	}
	public void setWebCommand(String webCommand) {
		this.webCommand = webCommand;
	}
	public String getSentCommand() {
		return sentCommand;
	}
	public void setSentCommand(String sentCommand) {
		
		exectionTime=System.currentTimeMillis();
		
		this.sentCommand = sentCommand;
	}
	public String getReceivedResponse() {
		return receivedResponse;
	}
	public void setReceivedResponse(String receivedResponse) {
		
		exectionTime=System.currentTimeMillis()-exectionTime;
		this.receivedResponse = receivedResponse;
	}

	public String getWebResult() {
		return webResult;
	}
	public void setWebResult(String webResult) {
		this.webResult = webResult;
	}
	public long getExectionTime() {
		return exectionTime;
	}
	public void setExectionTime(long exectionTime) {
		this.exectionTime = exectionTime;
	}
	public int getErrorCode() {
		
		if(receivedResponse ==null){
			//there must be a network error
			return 255;
		}

		
		String responseParameters[]=receivedResponse.trim().split(",");
		
		int length=responseParameters.length;
		
		String lastPart=responseParameters[length-1];
		/*
		 * This applies to the $verify,error too.
		 * if(receivedResponse.startsWith("$verify,error")){
			
			return 0;
		}
		 * 
		 * 
		 * */
		int errorCodeOrResult=Integer.parseInt(lastPart);
		
		if(isSystemResponse(receivedResponse)){
			/*token$ack,scenario,sid,result\n
命令各项参数意义如下：
sid：  场景 id 号，从系统数据中获得；
result:  控制结果，1 为成功，其他值为失败*/
			return errorCodeOrResult-1;
		}

		
		// TODO Auto-generated method stub
		return errorCodeOrResult;
	}
	protected boolean isSystemResponse(String receivedResponse) {
		// TODO Auto-generated method stub
		/*token$ack,scenario,sid,result\n
		命令各项参数意义如下：
		sid：  场景 id 号，从系统数据中获得；
		result:  控制结果，1 为成功，其他值为失败
		触发器是result，其他是errorcode
		 *
		 */
		if(receivedResponse.contains("scenario")){
			return true;
		}
		
		if(receivedResponse.contains("trigger")){
			return true;
		}
		
		return false;
	}
	
}
