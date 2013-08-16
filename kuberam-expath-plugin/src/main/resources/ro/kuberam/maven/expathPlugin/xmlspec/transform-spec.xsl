<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

	<xsl:import href="xmlspec.xsl" />

	<xsl:output method="xhtml" version="1.0" omit-xml-declaration="yes" encoding="utf-8" />

	<xsl:template match="/">
		<xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text>
		<xsl:apply-templates />
	</xsl:template>

</xsl:stylesheet>
