<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns="http://expath.org/ns/pkg" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:dc="http://dublincore.org/documents/dces/" xmlns:pkg="http://cxan.org/ns/package" xmlns:xs="http://www.w3.org/2001/XMLSchema"
	exclude-result-prefixes="dc pkg xs" version="2.0">

	<xsl:output method="xml" />

	<xsl:param name="package-dir" />
	<xsl:variable name="package-type" select="/*/dc:type" />
	<xsl:variable name="cxan.org-id" select="/*/dc:creator/@id" />
	<xsl:variable name="module-prefix" select="/*/pkg:module-prefix" />
	<xsl:variable name="module-namespace" select="/*/*[local-name() = 'module-namespace']" />
	<xsl:variable name="package-version" select="/*/@version" />

	<xsl:variable name="abbrev" select="/*/@abbrev" />
	<xsl:variable name="name" select="/*/@name" />
	<xsl:variable name="title" select="/*/*[local-name() = 'title']" />
	<xsl:variable name="author" select="/*/dc:creator" />
	<xsl:variable name="components" select="collection(concat('file://', $package-dir, '?select=components.xml'))/element()" />

	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="/*/*[local-name() = 'dependency' and @processor = 'http://exist-db.org/']">
				<!-- generate exist.xml -->
				<xsl:result-document href="{concat($package-dir, '/exist.xml')}">
					<package xmlns="http://exist-db.org/ns/expath-pkg">
						<xsl:for-each select="$components/element()">
							<xsl:choose>
								<xsl:when test="element()[1] = 'http://exist-db.org/ns/expath-pkg/module-main-class'">
									<java>
										<namespace>
											<xsl:value-of select="$module-namespace" />
										</namespace>
										<class>
											<xsl:value-of select="element()[2]" />
										</class>
									</java>
								</xsl:when>
								<xsl:otherwise>
									<jar>
										<xsl:value-of select="element()[2]" />
									</jar>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
					</package>
				</xsl:result-document>
			</xsl:when>
		</xsl:choose>
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

		<!-- generate cxan.xml -->
		<xsl:result-document href="{concat($package-dir, '/cxan.xml')}">
			<xsl:variable name="tags" as="xs:string*" select="tokenize(/*/pkg:cxan-tags, ',')" />
			<package xmlns="http://cxan.org/ns/package" id="{$abbrev}" name="{$name}" version="{$package-version}">
				<author id="{$cxan.org-id}">
					<xsl:value-of select="$author" />
				</author>
				<xsl:for-each select="/*/pkg:cxan-category">
					<category id="{@id}">
						<xsl:value-of select="." />
					</category>
				</xsl:for-each>
				<xsl:for-each select="$tags">
					<tag>
						<xsl:value-of select="." />
					</tag>
				</xsl:for-each>
			</package>
		</xsl:result-document>

		<!-- generate expath-pkg.xml -->
		<xsl:result-document href="{concat($package-dir, '/expath-pkg.xml')}">
			<package xmlns="http://expath.org/ns/pkg" name="{$name}" abbrev="{$abbrev}" version="{$package-version}"
				spec="1.0">
				<title>
					<xsl:value-of select="$title" />
				</title>
				<xsl:copy-of select="/*/*[local-name() = 'dependency']" copy-namespaces="no" />
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
				<website>
					<xsl:value-of select="/*/dc:identifier" />
				</website>
				<status>
					<xsl:value-of select="/*/*[local-name() = 'status']" />
				</status>
				<license>
					<xsl:value-of select="/*/dc:rights" />
				</license>
				<copyright>
					<xsl:value-of select="/*/*[local-name() = 'copyright']" />
				</copyright>

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