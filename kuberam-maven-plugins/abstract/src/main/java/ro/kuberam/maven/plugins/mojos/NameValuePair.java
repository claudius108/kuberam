package ro.kuberam.maven.plugins.mojos;

public class NameValuePair {
	private String name, value;
	
	/**
	 * Constructor.
	 */
	public NameValuePair(String pName, String pValue) {
		this.name = pName;
		this.value = pValue;
	}	

	/**
	 * Returns the parameter name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the parameter name.
	 */
	public void setName(String pName) {
		name = pName;
	}

	/**
	 * Returns the parameter value.
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Sets the parameter value.
	 */
	public void setValue(String pValue) {
		value = pValue;
	}
}
