package ro.kuberam.location;

import java.util.Arrays;

import ro.kuberam.location.ipLocalisation.IpLocalisation;

public class GetCountryAlpha2Code {

	public static String run(String ipAddress) {
		IpLocalisation.validateIpAddress(ipAddress);

		long ipV4AddressAsLong = IpLocalisation.ipAddressToLong(ipAddress);

		int ipRangeIndex = Arrays.binarySearch(IpLocalisation.startRanges, ipV4AddressAsLong);

		ipRangeIndex = (ipRangeIndex >= 0) ? ipRangeIndex : ((-1) * ipRangeIndex) - 2;

		String countryAlpha2Code = IpLocalisation.countryIsoAlpha2Codes[ipRangeIndex];

		return countryAlpha2Code;
	}
}
