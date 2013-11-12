package ro.kuberam.location.ipLocalisation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ro.kuberam.location.ipLocalisation.IpLocalisationFunctions;

public class IpLocalisationFunctionsTest {
	
	@Test
	public void getPackageNameTest() {
		System.out.println(IpLocalisationFunctionsTest.class.getPackage().getName().replace(".", "/"));
	}

	@Test
	public void validateIpAddressTest() {
		IpLocalisationFunctions.validateIpAddress("192.162.16.145");
		IpLocalisationFunctions.validateIpAddress("64.233.160.0");
	}

	@Test
	public void validateWrongIpAddressTest() {
		IpLocalisationFunctions.validateIpAddress("192.162.16.1450");
	}

	@Test
	public void ipAddressToLongTest() {
		assertTrue(IpLocalisationFunctions.ipAddressToLong("192.162.16.145") == 3231846545L);
		assertTrue(IpLocalisationFunctions.ipAddressToLong("64.233.160.0") == 1089052672L);
		assertTrue(IpLocalisationFunctions.ipAddressToLong("155.140.124.174") == 2609675438L);
	}

	@Test
	public void validateLanguageTagTest() {
		assertTrue(IpLocalisationFunctions.validateLanguageTag("fr-FR"));
		assertFalse(IpLocalisationFunctions.validateLanguageTag("fr-JP"));
	}

	@Test
	public void getCountryAlpha2CodeTest() {
		assertTrue(IpLocalisationFunctions.getCountryAlpha2Code("192.162.16.145").equals("RO"));
		assertTrue(IpLocalisationFunctions.getCountryAlpha2Code("64.233.160.0").equals("US"));
		assertTrue(IpLocalisationFunctions.getCountryAlpha2Code("155.140.124.174").equals("GB"));
	}

	@Test
	public void getCountryAlpha3CodeTest() {
		assertTrue(IpLocalisationFunctions.getCountryAlpha3Code("RO").equals("ROU"));
		assertTrue(IpLocalisationFunctions.getCountryAlpha3Code("US").equals("USA"));
		assertTrue(IpLocalisationFunctions.getCountryAlpha3Code("GB").equals("GBR"));
	}

	@Test
	public void getCountryAlpha3CodeWithWrongAlpha2CodeTest() {
		IpLocalisationFunctions.getCountryAlpha3Code("PO");
	}

	@Test
	public void getCountryNameTest() {
		assertTrue(IpLocalisationFunctions.getCountryName("RO").equals("Romania"));
		assertTrue(IpLocalisationFunctions.getCountryName("US").equals("United States"));
		assertTrue(IpLocalisationFunctions.getCountryName("GB").equals("United Kingdom"));
	}
	
	@Test
	public void getCountryNameWithWrongAlpha2CodeTest() {
		IpLocalisationFunctions.getCountryName("PO");
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
