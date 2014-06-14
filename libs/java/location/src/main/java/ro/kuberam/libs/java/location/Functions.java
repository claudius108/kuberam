package ro.kuberam.libs.java.location;


public class Functions {
	
	public static String getCountryAlpha2Code(String ipAddress) {
		return GetCountryAlpha2Code.run(ipAddress);
	}
	
	public static String getCountryAlpha3Code(String countryAlpha2Code) {
		return GetCountryAlpha3Code.run(countryAlpha2Code);
	}
	public static String getCountryName(String countryAlpha2Code) {
		return GetCountryName.run(countryAlpha2Code);
	}
	
}
