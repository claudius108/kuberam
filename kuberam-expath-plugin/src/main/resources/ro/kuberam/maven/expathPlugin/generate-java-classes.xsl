<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

	<xsl:output method="text" />

	<xsl:param name="javaPackageName" />
	<xsl:param name="specId" />
	<xsl:param name="libDirPath" />
	<xsl:param name="libVersion" />

	<xsl:variable name="javaPackageDirPath"
		select="concat($libDirPath, '/src/main/java/org/expath/', $javaPackageName, '/')" />
	<xsl:variable name="java-package-declaration" select="concat('package org.expath.', $javaPackageName, ';')" />

	<xsl:variable name="java-end-of-instruction-line">
		<xsl:text>";
</xsl:text>
	</xsl:variable>

	<xsl:template match="/">
		<xsl:variable name="module-namespace">
			<xsl:copy-of select="//element()[@id = 'module-namespace']" />
		</xsl:variable>
		<xsl:variable name="module-prefix">
			<xsl:copy-of select="//element()[@id = 'module-prefix']" />
		</xsl:variable>

		<xsl:result-document href="{concat($javaPackageDirPath, 'ErrorMessages.java')}" method="text">
			<xsl:value-of select="$java-package-declaration" />
			<xsl:text>
      
</xsl:text>
			<xsl:text>public class ErrorMessages {
</xsl:text>
			<xsl:for-each select="//element()[@id = 'summary-of-error-conditions']/*">
				<xsl:text>      public static String </xsl:text>
				<xsl:value-of select="replace(@key, ':', '_')" />
				<xsl:text> = "</xsl:text>
				<xsl:value-of select="concat(@key, ': ', .)" />
				<xsl:text>";
</xsl:text>
			</xsl:for-each>
			<xsl:text>}</xsl:text>
		</xsl:result-document>

		<xsl:result-document href="{concat($javaPackageDirPath, 'ModuleDescription.java')}" method="text">
			<xsl:value-of select="$java-package-declaration" />
			<xsl:text>

</xsl:text>
			<xsl:text>
/**
 * Module description.
 * 
 * @author Claudius Teodorescu &lt;claudius.teodorescu@gmail.com&gt;
 */      
</xsl:text>
			<xsl:text>public class ModuleDescription {
</xsl:text>
			<xsl:text>      public final static String VERSION = "</xsl:text>
			<xsl:value-of select="concat($libVersion, $java-end-of-instruction-line)" />
			<xsl:text>      public final static String NAMESPACE_URI = "</xsl:text>
			<xsl:value-of select="concat($module-namespace, $java-end-of-instruction-line)" />
			<xsl:text>      public final static String PREFIX = "</xsl:text>
			<xsl:value-of select="concat($module-prefix, $java-end-of-instruction-line)" />
			<xsl:text>      public final static String MODULE_NAME = "</xsl:text>
			<xsl:value-of select="concat('EXPath ', //element()[local-name() = 'title'], $java-end-of-instruction-line)" />
			<xsl:text>      public final static String MODULE_DESCRIPTION = "</xsl:text>
			<xsl:value-of select="concat('A ', //element()[@id = 'module-description'], $java-end-of-instruction-line)" />
			<xsl:text>}
</xsl:text>
		</xsl:result-document>

	</xsl:template>
</xsl:stylesheet>