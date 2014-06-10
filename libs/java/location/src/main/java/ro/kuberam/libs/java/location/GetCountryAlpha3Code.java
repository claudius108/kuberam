package ro.kuberam.location;

import ro.kuberam.location.ipLocalisation.IpLocalisation;

public class GetCountryAlpha3Code {

	public static String run(String countryAlpha2Code) {
		String countryAlpha3Code = IpLocalisation.countryIsoAlpha3CodesProperties.getProperty(countryAlpha2Code, "");

		if (countryAlpha3Code.equals("")) {
			try {
				throw new Exception(ErrorMessages.err_LOC04);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		return countryAlpha3Code;
	}
}
