package hwcontrol;

public class CustomException extends Exception {

	public  CustomException(String message)
	{
		super(message);
	}
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return "定制化的失败："+super.getMessage();
	}
	
}
