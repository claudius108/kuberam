package ro.kuberam.libs.java.ftclient.utils;

import java.io.IOException;
import java.util.zip.CRC32;

public class CalculateChecksum {
	
	public static String crc32(byte[] resourceBytes) throws IOException, Exception {

		long checksum = 0;		
		if (resourceBytes != null) {
			CRC32 crc32 = new CRC32();
			crc32.update(resourceBytes, 0, resourceBytes.length);
			checksum = crc32.getValue();			
		}
		
		return Long.toHexString(checksum);
	}	

}
