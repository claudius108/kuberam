<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="xs"
    xpath-default-namespace="http://www.w3.org/1999/xhtml"
	version="2.0">

	<xsl:output method="html" indent="yes"/>

    <xsl:param name="outputDir" required="yes" as="xs:string"/>
	<xsl:param name="specsDir" as="xs:string"/>
	<xsl:param name="includeSpecs" as="xs:string"/>


	<xsl:template match="/">
		<xsl:result-document href="{concat($outputDir, '/index.html')}" method="html">
			<html xmlns="http://www.w3.org/1999/xhtml">
				<head>
					<title>EXPath Specifications Index</title>
				</head>
				<body>
					<h2>EXPath Specifications Index</h2>
					<xsl:for-each select="tokenize($includeSpecs, ',')">
						<xsl:variable name="spec-name" select="." />
						<xsl:variable name="spec-dir" select="concat($specsDir, '/', $spec-name, '/')" />
						<xsl:variable name="spec" select="document(concat('file://', $spec-dir, $spec-name, '.html'))/html/body" />
						<xsl:variable name="spec-in-html-format-path" select="concat($spec-name, '/', $spec-name, '.html')" />
						<h4>
							<a href="{$spec-in-html-format-path}">
								<xsl:value-of select="normalize-space($spec/div[@class = 'head']/h1)" />
							</a>
						</h4>
						<h5>
							<xsl:copy-of select="$spec/div[2]/p" />
						</h5>
					</xsl:for-each>
				</body>
			</html>
		</xsl:result-document>
	</xsl:template>
</xsl:stylesheet>
