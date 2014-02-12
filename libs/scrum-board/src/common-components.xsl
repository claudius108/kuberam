<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:cpnz="http://www.componize.com/xslt/" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0" exclude-result-prefixes="cpnz xs">
    <xsl:output method="html"/>
    <xsl:param name="api-key" select="''"/>
    <xsl:param name="hostname" select="''"/>
    <xsl:param name="project" select="''"/>
    <xsl:param name="version"/>
    <xsl:function name="cpnz:get-issues">
        <xsl:param name="page"/>
        <xsl:variable name="current-page" select="doc(concat($url, '&amp;page=', $page))"/>
        <xsl:copy-of select="$current-page"/>
        <xsl:if test="count($current-page/issues/issue) = 100">
            <xsl:copy-of select="cpnz:get-issues($page + 1)"/>
        </xsl:if>
    </xsl:function>
    <xsl:template match="meta[@name = 'page-head-components-placeholder']">
        <link href="http://fonts.googleapis.com/css?family=Reenie+Beanie" rel="stylesheet" type="text/css"/>
        <link href="redmine-scrum-board.css" rel="stylesheet" type="text/css"/>
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js">/**/</script>
        <script src="redmine-scrum-board.js">/**/</script>
    </xsl:template>
    <xsl:template match="element()">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
    <xsl:template name="display-issue">
        <xsl:variable name="id" select="id"/>
        <xsl:message>
      ID:
      <xsl:value-of select="$id"/>
        </xsl:message>
        <xsl:variable name="class">
            <xsl:value-of select="lower-case(tracker/@name)"/>
        </xsl:variable>
        <div class="post-it {$class} {replace(lower-case(status/@name), ' ', '-')} priority{priority/@id}">
            <a href="http://{$hostname}/issues/{id}" target="_blank">
                <span class="estimate">
                    <xsl:value-of select="custom_fields/custom_field[@name='Estimate']"/>
                </span>
                <span class="issue_id">
                    <xsl:value-of select="concat('#',id)"/>
                </span>
                <br/>
                <span class="subject">
                    <xsl:value-of select="subject"/>
                </span>
                <span class="assigned_to">
                    <xsl:value-of select="assigned_to/@name"/>
                </span>
            </a>
        </div>
    </xsl:template>
</xsl:stylesheet>