<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:cpnz="http://www.componize.com/xslt/" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0" exclude-result-prefixes="cpnz xs">
    <xsl:include href="common-components.xsl"/>
    <xsl:output method="html"/>
    <xsl:variable name="months">
        <month name="Jan" value="01"/>
        <month name="Feb" value="02"/>
        <month name="Mar" value="03"/>
        <month name="Apr" value="04"/>
        <month name="May" value="05"/>
        <month name="Jun" value="06"/>
        <month name="Jul" value="07"/>
        <month name="Aug" value="08"/>
        <month name="Sep" value="09"/>
        <month name="Oct" value="10"/>
        <month name="Nov" value="11"/>
        <month name="Dec" value="12"/>
    </xsl:variable>
    <xsl:variable name="url">
        <xsl:if test="not($hostname)">
            <xsl:message terminate="yes">
                <xsl:text>Missing mandatory parameter: hostname</xsl:text>
            </xsl:message>
        </xsl:if>
        <xsl:if test="not($project)">
            <xsl:message terminate="yes">
                <xsl:text>Missing mandatory parameter: project</xsl:text>
            </xsl:message>
        </xsl:if>
        <xsl:text>http://</xsl:text>
        <xsl:value-of select="$hostname"/>
        <xsl:text>/issues.xml?project_id=</xsl:text>
        <xsl:value-of select="$project"/>
        <xsl:text>&amp;status_id=open&amp;per_page=100</xsl:text>
        <xsl:if test="$api-key != ''">
            <xsl:text>&amp;key=</xsl:text>
            <xsl:value-of select="$api-key"/>
        </xsl:if>
    </xsl:variable>
    <xsl:variable name="issues-unsorted" select="cpnz:get-issues(1)"/>
    <xsl:variable name="project-name" select="$project"/>
    <xsl:template match="title">
        <title>
            <xsl:value-of select="$project-name"/>
        </title>
    </xsl:template>
    <xsl:template match="div[@class = 'issues']">
        <xsl:message>
            <xsl:value-of select="concat('&#xA;hostname = ', $hostname)"/>
            <xsl:value-of select="concat('&#xA;project = ', $project)"/>
            <xsl:value-of select="concat('&#xA;url = ', $url)"/>
            <xsl:value-of select="concat('&#xA;number of issues = ', count($issues-unsorted/issues/issue))"/>
        </xsl:message>
        <xsl:variable name="projects">
            <xsl:for-each select="$issues-unsorted/issues/issue/project">
                <xsl:sort select="@name"/>
                <xsl:copy-of select="."/>
            </xsl:for-each>
        </xsl:variable>
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <table>
                <tr>
                    <th class="project-name">Project</th>
                    <th class="todo">TODO</th>
                    <th class="doing">Doing</th>
                    <th class="review">Review</th>
                </tr>
                <xsl:for-each select="$projects/project[not(@name=preceding-sibling::project/@name)]">
                    <xsl:variable name="project-name" select="@name"/>
                    <tr>
                        <td>
                            <xsl:call-template name="display-project"/>
                        </td>
                        <td class="todo">
                            <xsl:for-each select="$issues-unsorted/issues/issue[status/@name = 'New' and project[@name=$project-name] ]">
                                <xsl:call-template name="display-issue"/>
                            </xsl:for-each>
                        </td>
                        <td class="doing">
                            <xsl:for-each select="$issues-unsorted/issues/issue[ (status/@name = 'Assigned' or status/@name = 'Feedback' or status/@name = 'On Hold') and  project[@name=$project-name] ]">
                                <xsl:call-template name="display-issue"/>
                            </xsl:for-each>
                        </td>
                        <td class="review">
                            <xsl:for-each select="$issues-unsorted/issues/issue[status/@name = 'Resolved'  and project[@name=$project-name] ]">
                                <xsl:call-template name="display-issue"/>
                            </xsl:for-each>
                        </td>
                    </tr>
                </xsl:for-each>
            </table>
        </xsl:copy>
    </xsl:template>
    <xsl:template name="display-project">
        <xsl:variable name="project-name" select="@name"/>
        <div class="post-it project">
            <a href="http://{$hostname}/$project-name" target="_blank">
                <span class="subject">
                    <xsl:value-of select="$project-name"/>
                </span>
            </a>
        </div>
    </xsl:template>
</xsl:stylesheet>