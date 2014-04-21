package ro.kuberam.libs.java.ftclient.utils;

import java.io.InputStream;

import javax.xml.stream.XMLStreamWriter;

public class GenerateResourceElement {

	public static void run(InputStream is, XMLStreamWriter xmlWriter, String modulePrefix, String moduleNsUri, String resourceName, String resourceType,
			String resourceAbsolutePath, String lastModified, long resourceSize, String user, String userGroup, String permissions, String linkTo) throws Exception {

		xmlWriter.writeStartElement(modulePrefix + ":resource");
		xmlWriter.writeNamespace(modulePrefix, moduleNsUri);

		if (is != null) {
			byte[] resourceBytes = InputStream2ByteArray.convert(is);
			GenerateMetadataAttributes.run(xmlWriter, resourceName, resourceType, resourceAbsolutePath, lastModified, resourceSize, user, userGroup, permissions,
					linkTo, CalculateChecksum.crc32(resourceBytes));
			xmlWriter.writeCharacters(Base64.encodeToString(resourceBytes, true));
		} else {
			GenerateMetadataAttributes.run(xmlWriter, resourceName, resourceType, resourceAbsolutePath, lastModified, resourceSize, user, userGroup, permissions,
					linkTo, CalculateChecksum.crc32(null));
		}
		xmlWriter.writeEndElement();

	}

}
