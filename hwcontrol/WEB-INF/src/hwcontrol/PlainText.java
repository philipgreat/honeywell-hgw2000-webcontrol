package hwcontrol;

public class PlainText {
	private StringBuilder buffer;

	public String getValue() {
		if(buffer==null){
			return "";
		}		
		return buffer.toString();
	}

	public void setValue(String value) {
		this.append(value);
	}
	
	public void append(String value) {
		if(this.buffer==null){
			this.buffer=new StringBuilder();
		}
		this.buffer.append(value);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		if(buffer==null){
			return "";
		}	
		return buffer.toString();
	}
	
}
