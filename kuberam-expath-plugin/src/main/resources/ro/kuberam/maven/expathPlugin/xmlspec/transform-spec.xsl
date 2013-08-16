<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

	<xsl:import href="xmlspec.xsl" />

	<xsl:output method="xhtml" version="1.0" omit-xml-declaration="yes" encoding="utf-8" indent="yes" />

	<xsl:template match="/">
		<xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="spec">
		<html>
			<xsl:if test="header/langusage/language">
				<xsl:attribute name="lang">
          <xsl:value-of select="header/langusage/language/@id" />
        </xsl:attribute>
			</xsl:if>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
				<title>
					<xsl:apply-templates select="header/title" />
					<xsl:if test="header/version">
						<xsl:text> </xsl:text>
						<xsl:apply-templates select="header/version" />
					</xsl:if>
					<xsl:if test="$additional.title != ''">
						<xsl:text> -- </xsl:text>
						<xsl:value-of select="$additional.title" />
					</xsl:if>
				</title>
				<xsl:call-template name="css" />
			</head>
			<body>
				<xsl:apply-templates />
				<xsl:if test="//footnote[not(ancestor::table)]">
					<hr />
					<div class="endnotes">
						<xsl:text>&#10;</xsl:text>
						<h3>
							<xsl:call-template name="anchor">
								<xsl:with-param name="conditional" select="0" />
								<xsl:with-param name="default.id" select="'endnotes'" />
							</xsl:call-template>
							<xsl:text>End Notes</xsl:text>
						</h3>
						<dl>
							<xsl:apply-templates select="//footnote[not(ancestor::table)]" mode="notes" />
						</dl>
					</div>
				</xsl:if>
			</body>
		</html>
	</xsl:template>

</xsl:stylesheet>
