<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:h="http://www.w3.org/1999/xhtml" exclude-result-prefixes="#all" version="2.0">

	<xsl:import href="xmlspec.xsl" />

	<xsl:output method="xhtml" version="1.0" omit-xml-declaration="yes" encoding="utf-8" indent="yes" />

	<xsl:template match="/">
		<xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text>
		<xsl:next-match />
	</xsl:template>

	<xsl:template match="h:html/h:body[exists($analytics-id)]" mode="postproc">
		<xsl:copy>
			<xsl:copy-of select="@*" />
			<xsl:apply-templates select="node()" mode="postproc" />
			<script type="text/javascript">
				(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
				(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
				m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
				})(window,document,'script','//www.google-analytics.com/analytics.js','ga');
				ga('create', 'UA-43293529-1', 'kuberam.ro');
				ga('send', 'pageview');
			</script>
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>
