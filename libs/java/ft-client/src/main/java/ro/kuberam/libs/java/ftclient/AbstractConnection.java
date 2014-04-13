package ro.kuberam.libs.java.ftclient;


public abstract class AbstractConnection {

	protected boolean checkIsDirectory(String directoryPath) throws Exception {
		Boolean result = true;
		if (!directoryPath.endsWith("/")) {
			result = false;
		}
		
		return result;
	}

}
