package ro.kuberam.location;

import ro.kuberam.location.ipLocalisation.IpLocalisation;

public class GetCountryName {

	public static String run(String countryAlpha2Code) {
		String countryName = IpLocalisation.countryShortNamesGazetteerOrderEnUsProperties.getProperty(countryAlpha2Code, "");

		if (countryName.equals("")) {
			try {
				throw new Exception(ErrorMessages.err_LOC04);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		return countryName;
	}
}
