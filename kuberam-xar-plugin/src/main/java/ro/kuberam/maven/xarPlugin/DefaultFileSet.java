package ro.kuberam.maven.xarPlugin;

public class DefaultFileSet extends org.codehaus.plexus.archiver.util.DefaultFileSet {
	
	public String[] includes;
	
	public String[] getIncludes() {
		return includes;
	}
	
	public void setIncludes(String includesString) {
		includes = includesString.split(",");
	}
}
