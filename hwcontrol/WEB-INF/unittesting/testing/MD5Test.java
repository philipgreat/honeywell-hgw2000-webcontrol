package testing;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;

public class MD5Test {

	
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
	
	public String getKeyedDigest(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        
		if(password==null){
			throw new IllegalArgumentException("the password can not be null");
		}
		
		if(password.trim().length()==0){
			throw new IllegalArgumentException("the password can not be empty or can not be emptry after trimed.");
		}
		
		byte [] utf16leBytes=password.getBytes("UTF-16LE");
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(utf16leBytes);
        byte[] digistedBytes = md5.digest();
        return toHexString(digistedBytes);
    
	}
	
	@Test
	public void test() throws NoSuchAlgorithmException, UnsupportedEncodingException {

		
		System.out.println(getKeyedDigest("admin"));
		
		//fail("x");
	}

}
