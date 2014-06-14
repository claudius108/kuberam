package ro.kuberam.libs.java.location.ipLocalisation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ro.kuberam.libs.java.location.Functions;

public class IpLocalisationTest {

	@Test
	public void validateIpAddressTest() {
		IpLocalisation.validateIpAddress("192.162.16.145");
		IpLocalisation.validateIpAddress("64.233.160.0");
	}

	@Test
	public void validateWrongIpAddressTest() {
		IpLocalisation.validateIpAddress("192.162.16.1450");
	}

	@Test
	public void ipAddressToLongTest() {
		assertTrue(IpLocalisation.ipAddressToLong("192.162.16.145") == 3231846545L);
		assertTrue(IpLocalisation.ipAddressToLong("64.233.160.0") == 1089052672L);
		assertTrue(IpLocalisation.ipAddressToLong("155.140.124.174") == 2609675438L);
	}

	@Test
	public void validateLanguageTagTest() {
		assertTrue(IpLocalisation.validateLanguageTag("fr-FR"));
		assertFalse(IpLocalisation.validateLanguageTag("fr-JP"));
	}

	@Test
	public void getCountryAlpha2CodeTest() {
		assertTrue(Functions.getCountryAlpha2Code("192.162.16.145").equals("RO"));
		assertTrue(Functions.getCountryAlpha2Code("64.233.160.0").equals("US"));
		assertTrue(Functions.getCountryAlpha2Code("155.140.124.174").equals("GB"));
	}

	@Test
	public void getCountryAlpha3CodeTest() {
		assertTrue(Functions.getCountryAlpha3Code("RO").equals("ROU"));
		assertTrue(Functions.getCountryAlpha3Code("US").equals("USA"));
		assertTrue(Functions.getCountryAlpha3Code("GB").equals("GBR"));
	}

	@Test
	public void getCountryAlpha3CodeWithWrongAlpha2CodeTest() {
		Functions.getCountryAlpha3Code("PO");
	}

	@Test
	public void getCountryNameTest() {
		assertTrue(Functions.getCountryName("RO").equals("Romania"));
		assertTrue(Functions.getCountryName("US").equals("United States"));
		assertTrue(Functions.getCountryName("GB").equals("United Kingdom"));
	}
	
	@Test
	public void getCountryNameWithWrongAlpha2CodeTest() {
		Functions.getCountryName("PO");
	}

	// public static Collection<Object[]> data() {
	// Object[][] data = new Object[][] { { "192.102.100", false }, // must
	// // have
	// // a 4
	// // '.'
	// // characters
	// { "a.b.c.d", false }, // you cannot have characters between '.'
	// { "1.2.3.800", false }, // you can have only until 255
	// { "1.2.3", false }, // you must have 4 digit parts
	//
	// { "192.168.1.1", true }, { "10.10.10.10", true }, { "127.0.0.1", true }
	// };
	//
	// return Arrays.asList(data);
	// }

}
