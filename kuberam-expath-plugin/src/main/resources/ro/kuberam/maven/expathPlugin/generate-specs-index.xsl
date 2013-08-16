<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xpath-default-namespace="http://www.w3.org/1999/xhtml" xmlns="http://www.w3.org/1999/xhtml" version="2.0">

	<xsl:output method="html" />

	<xsl:param name="specsDir" />
	<xsl:param name="includeSpecIds" />
	<xsl:param name="outputDir" />

	<xsl:template match="/">
		<xsl:result-document href="{concat($outputDir, '/index.html')}" method="html">
			<html>
				<head>
					<title>EXPath Specifications Index</title>
				</head>
				<body>
					<h2>EXPath Specifications Index</h2>
					<xsl:for-each select="tokenize($includeSpecIds, ',')">
						<xsl:variable name="spec-name" select="." />
						<xsl:variable name="spec-dir" select="concat($specsDir, '/', $spec-name, '/')" />
						<xsl:variable name="spec" select="document(concat('file://', $spec-dir, $spec-name, '.html'))/html/body/div/h1" />
						<xsl:variable name="spec-in-html-format-path" select="concat($spec-name, '/', $spec-name, '.html')" />
						<h4>
							<a href="{$spec-in-html-format-path}">
								<xsl:value-of select="$spec" />
							</a>
						</h4>
<!-- 						<h5> -->
<!-- 							<xsl:value-of select="$spec//element()[local-name() = 'a' and id = 'abstract']" /> -->
<!-- 						</h5> -->
					</xsl:for-each>
				</body>
			</html>
		</xsl:result-document>
	</xsl:template>
</xsl:stylesheet>