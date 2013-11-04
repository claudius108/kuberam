package ro.kuberam.ipLocalisation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IpLocalisationFunctions {

	private static ClassLoader classLoader = IpLocalisationFunctions.class.getClassLoader();

	private static long[] startRanges;
	private static String[] countryIsoAlpha2Codes;
	private static HashSet<String> languageTags;
	private static Properties countryIsoAlpha3CodesProperties = new Properties();
	private static Properties countryShortNamesGazetteerOrderEnUsProperties = new Properties();

	private static Pattern pattern;
	private static Matcher matcher;

	private static String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	static {
		final InputStream startRangeIpsInputStream;
		ObjectInputStream startRangeIpsObjectInputStream = null;
		final InputStream countryAlpha2CodesInputStream;
		ObjectInputStream countryAlpha2CodesObjectInputStream = null;
		final InputStream languageTagsInputStream;
		ObjectInputStream languageTagsObjectInputStream = null;

		try {
			startRangeIpsInputStream = classLoader.getResourceAsStream("ro/kuberam/ipLocalisation/startRangeIps.ser");
			startRangeIpsObjectInputStream = new ObjectInputStream(startRangeIpsInputStream);
			startRanges = (long[]) startRangeIpsObjectInputStream.readObject();

			countryAlpha2CodesInputStream = classLoader.getResourceAsStream("ro/kuberam/ipLocalisation/countryIsoAlpha2Codes.ser");
			countryAlpha2CodesObjectInputStream = new ObjectInputStream(countryAlpha2CodesInputStream);
			countryIsoAlpha2Codes = (String[]) countryAlpha2CodesObjectInputStream.readObject();

			languageTagsInputStream = classLoader.getResourceAsStream("ro/kuberam/ipLocalisation/languageTags.ser");
			languageTagsObjectInputStream = new ObjectInputStream(languageTagsInputStream);
			@SuppressWarnings("unchecked")
			HashSet<String> languageTagsObject = (HashSet<String>) languageTagsObjectInputStream.readObject();
			languageTags = languageTagsObject;

			countryIsoAlpha3CodesProperties.load(classLoader.getResourceAsStream("ro/kuberam/ipLocalisation/country-iso-alpha-3-codes.properties"));
			countryShortNamesGazetteerOrderEnUsProperties.load(classLoader.getResourceAsStream("ro/kuberam/ipLocalisation/country-short-names-gazetteer-order-en-US.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		pattern = Pattern.compile(IPADDRESS_PATTERN);
	}

	public static String getCountryAlpha2Code(String ipAddress) {

		validateIpAddress(ipAddress);

		long ipV4AddressAsLong = ipAddressToLong(ipAddress);

		int ipRangeIndex = Arrays.binarySearch(startRanges, ipV4AddressAsLong);

		ipRangeIndex = (ipRangeIndex >= 0) ? ipRangeIndex : ((-1) * ipRangeIndex) - 2;

		String countryAlpha2Code = countryIsoAlpha2Codes[ipRangeIndex];

		return countryAlpha2Code;
	}

	public static String getCountryAlpha3Code(String countryAlpha2Code) {
		String countryAlpha3Code = countryIsoAlpha3CodesProperties.getProperty(countryAlpha2Code, "");

		if (countryAlpha3Code.equals("")) {
			try {
				throw new Exception(ErrorMessages.err_LOC04);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		return countryAlpha3Code;
	}
	
	public static String getCountryName(String countryAlpha2Code) {
		String countryName = countryShortNamesGazetteerOrderEnUsProperties.getProperty(countryAlpha2Code, "");

		if (countryName.equals("")) {
			try {
				throw new Exception(ErrorMessages.err_LOC04);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		return countryName;
	}

	// ancillary functions
	protected static long ipAddressToLong(String ipAddress) {
		String[] addrArray = ipAddress.split("\\.");

		long num = 0;
		for (int i = 0; i < addrArray.length; i++) {
			int power = 3 - i;

			num += ((Integer.parseInt(addrArray[i]) % 256 * Math.pow(256, power)));
		}
		return num;
	}

	protected static void validateIpAddress(String ipAddress) {
		// InetAddress.
		matcher = pattern.matcher(ipAddress);
		if (!matcher.matches()) {
			try {
				throw new Exception(ErrorMessages.err_LOC01);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected static Boolean validateLanguageTag(String languageTag) {
		return languageTags.contains(languageTag);
	}

}
