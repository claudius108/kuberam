<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dc="http://dublincore.org/documents/dces/"
	xmlns:pkg="http://cxan.org/ns/package" exclude-result-prefixes="dc pkg" version="2.0">

	<xsl:output method="xml" />

	<xsl:param name="package-dir" />
	<xsl:variable name="package-type" select="/*/dc:type" />
	<xsl:variable name="cxan.org-id" select="/*/dc:creator/@id" />
	<xsl:variable name="module-prefix" select="/*/pkg:module-prefix" />
	<xsl:variable name="module-namespace" select="/*/pkg:module-namespace" />
	<xsl:variable name="package-version" select="/*/@version" />

	<xsl:variable name="abbrev" select="/*/@abbrev" />
	<xsl:variable name="name" select="/*/@name" />
	<xsl:variable name="processor-ns">
		<xsl:text>http://exist-db.org/</xsl:text>
	</xsl:variable>
	<xsl:variable name="title" select="/*/*[local-name() = 'title']" />
	<xsl:variable name="author" select="/*/dc:creator" />

	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="$package-type = 'application'">
				<xsl:result-document href="{concat($package-dir, '/controller.xql')}" omit-xml-declaration="yes">
					xquery version "1.0";

					declare variable $exist:path external;
					declare variable $exist:resource external;
					declare
					variable
					$exist:controller external;
					declare variable $exist:prefix external;
					declare
					variable $exist:root external;

					(:

					Enter the
					following in the eXist client. $EXIST_HOME/bin/client.sh
					cd /db/apps/eco-meta
					chmod controller.xql
					user=+execute
					chmod
					controller.xql group=+execute
					chmod
					controller.xql other=+execute

					Use this to verify the $exist:path
					let $log :=
					util:log-system-out(concat('$exist:path=', $exist:path))
					return
					:)

					(: if we have a slash or a null then
					redirect to the
					index
					page. Note that null seems to be broken :)
					if ($exist:path = ('/', '')) then
					(: forward root path
					to index.xq :)
					<dispatch xmlns="http://exist.sourceforge.net/NS/exist">
						<redirect url="index.xml" />
					</dispatch>
					else
					(: everything else is passed through :)
					<dispatch xmlns="http://exist.sourceforge.net/NS/exist">
						<cache-control cache="yes" />
					</dispatch>
				</xsl:result-document>
			</xsl:when>
		</xsl:choose>

		<!-- generate exist.xml -->
		<xsl:result-document href="{concat($package-dir, '/exist.xml')}">
			<package xmlns="http://exist-db.org/ns/expath-pkg">
				<xsl:copy-of select="/*/*[contains('jar java', local-name())]" copy-namespaces="no" />
			</package>
		</xsl:result-document>

		<!-- generate cxan.xml -->
		<xsl:result-document href="{concat($package-dir, '/cxan.xml')}">
			<package xmlns="http://cxan.org/ns/package" id="{$abbrev}" name="{$name}" version="{$package-version}">
				<author id="{$cxan.org-id}">
					<xsl:value-of select="$author" />
				</author>
				<category id="libs">Libraries</category>
				<category id="exist">eXist extensions</category>
				<tag>
					<xsl:value-of select="$module-prefix" />
				</tag>
				<tag>expath</tag>
				<tag>
					<xsl:value-of select="$package-type" />
				</tag>
				<tag>exist</tag>
			</package>
		</xsl:result-document>

		<!-- generate expath-pkg.xml -->
		<xsl:result-document href="{concat($package-dir, '/expath-pkg.xml')}">
			<package xmlns="http://expath.org/ns/pkg" name="{$name}" abbrev="{$abbrev}" version="{$package-version}"
				spec="1.0">
				<title>
					<xsl:value-of select="$title" />
				</title>
				<dependency processor="{$processor-ns}" />
				<xsl:choose>
					<xsl:when test="$package-type = 'application'">
						<xsl:copy-of select="/*/*[local-name() = 'dependency']" copy-namespaces="no" />
					</xsl:when>
				</xsl:choose>
			</package>
		</xsl:result-document>

		<!-- generate repo.xml -->
		<xsl:result-document href="{concat($package-dir, '/repo.xml')}">
			<meta xmlns="http://exist-db.org/xquery/repo">
				<description>
					<xsl:value-of select="$title" />
				</description>
				<author>
					<xsl:value-of select="$author" />
				</author>
				<website />
				<status>stable</status>
				<license>GNU-LGPL</license>
				<copyright>true</copyright>
				<type>
					<xsl:value-of select="$package-type" />
				</type>
				<xsl:choose>
					<xsl:when test="$package-type = 'application'">
						<target>
							<xsl:value-of select="concat('expath-', $module-prefix)" />
						</target>
					</xsl:when>
				</xsl:choose>
			</meta>
		</xsl:result-document>


	</xsl:template>

</xsl:stylesheet>