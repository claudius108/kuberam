package ro.kuberam.location.ipLocalisation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ro.kuberam.location.ErrorMessages;

public class IpLocalisation {

	private static ClassLoader classLoader = IpLocalisation.class.getClassLoader();
	private static String packagePath = IpLocalisation.class.getPackage().getName().replace(".", "/");

	public static long[] startRanges;
	public static String[] countryIsoAlpha2Codes;
	private static HashSet<String> languageTags;
	public static Properties countryIsoAlpha3CodesProperties = new Properties();
	public static Properties countryShortNamesGazetteerOrderEnUsProperties = new Properties();

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
			startRangeIpsInputStream = classLoader.getResourceAsStream(packagePath + "/startRangeIps.ser");
			startRangeIpsObjectInputStream = new ObjectInputStream(startRangeIpsInputStream);
			startRanges = (long[]) startRangeIpsObjectInputStream.readObject();

			countryAlpha2CodesInputStream = classLoader.getResourceAsStream(packagePath + "/countryIsoAlpha2Codes.ser");
			countryAlpha2CodesObjectInputStream = new ObjectInputStream(countryAlpha2CodesInputStream);
			countryIsoAlpha2Codes = (String[]) countryAlpha2CodesObjectInputStream.readObject();

			languageTagsInputStream = classLoader.getResourceAsStream(packagePath + "/languageTags.ser");
			languageTagsObjectInputStream = new ObjectInputStream(languageTagsInputStream);
			@SuppressWarnings("unchecked")
			HashSet<String> languageTagsObject = (HashSet<String>) languageTagsObjectInputStream.readObject();
			languageTags = languageTagsObject;

			countryIsoAlpha3CodesProperties.load(classLoader.getResourceAsStream(packagePath + "/country-iso-alpha-3-codes.properties"));
			countryShortNamesGazetteerOrderEnUsProperties.load(classLoader.getResourceAsStream(packagePath + "/country-short-names-gazetteer-order-en-US.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		pattern = Pattern.compile(IPADDRESS_PATTERN);
	}

	// ancillary functions
	public static long ipAddressToLong(String ipAddress) {
		String[] addrArray = ipAddress.split("\\.");

		long num = 0;
		for (int i = 0; i < addrArray.length; i++) {
			int power = 3 - i;

			num += ((Integer.parseInt(addrArray[i]) % 256 * Math.pow(256, power)));
		}
		return num;
	}

	public static void validateIpAddress(String ipAddress) {
		// InetAddress.getByName(ipAddress)
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
