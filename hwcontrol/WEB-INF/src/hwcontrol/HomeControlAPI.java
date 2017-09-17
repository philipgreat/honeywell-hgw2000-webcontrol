package hwcontrol;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public interface HomeControlAPI {

	public abstract Configuration viewCurrentConfiguration();

	public abstract String viewLog() throws IOException;

	public abstract ExecutionResult queryHBusLight(int area, int loop)
			throws IOException;

	public abstract ExecutionResult controlAirCondition(int id, int onOrOff,
			int mode, int fan, int windDirection, int tempToSet)
			throws IOException;

	public abstract ExecutionResult queryAirCondition(int id)
			throws IOException;

	public abstract ExecutionResult controlInfradRedAirCondition(int id,
			int irid) throws IOException;

	public abstract ExecutionResult controlUFHeat(int id, int onOrOff,
			int tempToSet) throws IOException;

	public abstract ExecutionResult queryUFHeat(int id) throws IOException;

	public abstract ExecutionResult controlRelay(int id, int onOrOff)
			throws IOException;

	public abstract ExecutionResult queryRelay(int id) throws IOException;

	public abstract ExecutionResult controlCurtain(int id, int action,
			int onOrOff, int position) throws IOException;

	public abstract ExecutionResult queryCurtain(int id) throws IOException;

	public abstract ExecutionResult controlHBusCurtain(int area, int loop,
			int action, int onOrOff, int position) throws IOException;

	public abstract ExecutionResult queryHBusCurtain(int area, int loop)
			throws IOException;

	public abstract ExecutionResult controlInfraRedDevice(int devid, int modid)
			throws IOException;

	public abstract ExecutionResult controlWifi2IR(int moduleid, int keyindex,
			int delay) throws IOException;

	public abstract ExecutionResult controlMusic(int area, int status,
			int volumn, int command, int parameter) throws IOException;

	public abstract ExecutionResult queryMusic(int area) throws IOException;

	public abstract ExecutionResult querySensor(int area, int loop,
			int sensortype, int subnetid, int deviceid, int logicNumber)
			throws IOException;

	public abstract ExecutionResult controlScenario(int scenarioId)
			throws IOException;

	public abstract ExecutionResult queryScenario(int areaid)
			throws IOException;

	public abstract ExecutionResult controlTriger(int id, int onOrOff)
			throws IOException;

	public abstract ExecutionResult queryTriger(int id) throws IOException;

	public abstract void connectToGateway(String username, String password,
			String hostname, int port) throws NoSuchAlgorithmException,
			UnsupportedEncodingException, IOException;

	public abstract ExecutionResult controlLight(int lightId, int action,
			int onOrOff, int dimmer) throws IOException;

	public abstract ExecutionResult controlHBusLight(int area, int loop,
			int action, int onOrOff, int dimmer) throws IOException;

	public abstract VersionInfo versionInfo() throws Exception;

}