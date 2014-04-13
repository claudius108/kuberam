package ro.kuberam.libs.java.ftclient.utils;

import javax.xml.stream.XMLStreamWriter;

public class GenerateMetadataAttributes {

	public static void run(XMLStreamWriter xmlWriter, String name, String type, String resourceAbsolutePath, String lastModified, long resourceSize,
			String user, String userGroup, String permissions, String linkTo, String checksum) throws Exception {

		xmlWriter.writeAttribute("name", name);
		xmlWriter.writeAttribute("type", type);
		xmlWriter.writeAttribute("absolute-path", resourceAbsolutePath);
		xmlWriter.writeAttribute("last-modified", lastModified);
		xmlWriter.writeAttribute("size", String.valueOf(resourceSize));
		xmlWriter.writeAttribute("human-readable-size", org.apache.commons.io.FileUtils.byteCountToDisplaySize(resourceSize));
		xmlWriter.writeAttribute("user", user);
		xmlWriter.writeAttribute("user-group", userGroup);
		xmlWriter.writeAttribute("permissions", permissions);
		if (linkTo != null) {
			xmlWriter.writeAttribute("link-to", linkTo);
		}
		xmlWriter.writeAttribute("checksum", checksum);
	}

}
