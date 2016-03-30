package hwcontrol;

import java.util.Date;

public class VersionInfo {
	public Date currentTime;
	public String version;
	public Date getCurrentTime() {
		return new java.util.Date();
	}
	public void setCurrentTime(Date currentTime) {
		this.currentTime = currentTime;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
}
