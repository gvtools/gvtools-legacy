<?xml version="1.0" encoding="UTF-8"?>


<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html"/>

    <!-- Transformation rules
         syntax recommendation http://www.w3.org/TR/xslt
    -->
    
<xsl:template match='/'>
    <html>
        <xsl:apply-templates />
    </html>
</xsl:template>

<!-- Root Element / Node -->
<xsl:template name='root' match='root'>
   <!-- <head></head> -->
    <body bgcolor='{@bgColor}'>
		<xsl:apply-templates select='layer' />
		<xsl:choose>
    		<xsl:when test="@numGeom > 1">
				<xsl:for-each select="geometry">
					<p>
					<xsl:choose>
						<xsl:when test="@fontStyle[. = 'PLAIN']">
							<font face='{@fontType}' size='{@fontSyze}' color='{@fontColor}'><xsl:value-of select="@name"/>:</font>
						</xsl:when>
						<xsl:when test="@fontStyle[. = 'BOLD']">
							<b><font face='{@fontType}' size='{@fontSyze}' color='{@fontColor}'><xsl:value-of select="@name"/>:</font></b>
						</xsl:when>
						<xsl:when test="@fontStyle[. = 'ITALIC']">
							<i><font face='{@fontType}' size='{@fontSyze}' color='{@fontColor}'><xsl:value-of select="@name"/>:</font></i>
						</xsl:when>
						<xsl:when test="@fontStyle[. = 'BOLD_AND_ITALIC']">
							<b><i><font face='{@fontType}' size='{@fontSyze}' color='{@fontColor}'><xsl:value-of select="@name"/>:</font></i></b>
						</xsl:when>
						<xsl:otherwise>
            				<font face='{@fontType}' size='{@fontSyze}' color='{@fontColor}'><xsl:value-of select="@name"/>:</font>
						</xsl:otherwise>
					</xsl:choose>
					</p>					
					<xsl:apply-templates select='geomInfo'>
						<xsl:with-param name='tabs' select='1'/>
					</xsl:apply-templates>
				</xsl:for-each>	
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select='geomInfo'>
					<xsl:with-param name='tabs' select='1'/>
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</body>
</xsl:template>

<!-- layer Node -->
<xsl:template name='layer' match='layer'>
	<xsl:choose>
		<xsl:when test="@fontStyle[. = 'PLAIN']">
			<p><font face='{@fontType}' size='{@fontSyze}' color='{@fontColor}'><xsl:value-of select="@name"/></font></p>
		</xsl:when>
		<xsl:when test="@fontStyle[. = 'BOLD']">
			<p><b><font face='{@fontType}' size='{@fontSyze}' color='{@fontColor}'><xsl:value-of select="@name"/></font></b></p>
		</xsl:when>
		<xsl:when test="@fontStyle[. = 'ITALIC']">
			<p><i><font face='{@fontType}' size='{@fontSyze}' color='{@fontColor}'><xsl:value-of select="@name"/></font></i></p>
		</xsl:when>
		<xsl:when test="@fontStyle[. = 'BOLD_AND_ITALIC']">
			<p><b><i><font face='{@fontType}' size='{@fontSyze}' color='{@fontColor}'><xsl:value-of select="@name"/></font></i></b></p>
		</xsl:when>
		<xsl:otherwise>
			<p><font face='{@fontType}' size='{@fontSyze}' color='{@fontColor}'><xsl:value-of select="@name"/></font></p>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>  

<!-- geomInfo Node -->
<xsl:template name='geomInfo' match='geomInfo'>
	<xsl:param name='tabs'/>
	
	<p style='margin-left: {$tabs}'>
	<xsl:if test="@name != $empty_string">
		<xsl:choose>
			<xsl:when test="@n_fontStyle[. = 'PLAIN']">
				<font face='{@n_fontType}' size='{@n_fontSyze}' color='{@n_fontColor}'><xsl:value-of select="@name"/>: </font>
			</xsl:when>
			<xsl:when test="@n_fontStyle[. = 'BOLD']">
				<b><font face='{@n_fontType}' size='{@n_fontSyze}' color='{@n_fontColor}'><xsl:value-of select="@name"/>: </font></b>
			</xsl:when>
			<xsl:when test="@n_fontStyle[. = 'ITALIC']">
				<i><font face='{@n_fontType}' size='{@n_fontSyze}' color='{@n_fontColor}'><xsl:value-of select="@name"/>: </font></i>
			</xsl:when>
			<xsl:when test="@n_fontStyle[. = 'BOLD_AND_ITALIC']">
				<b><i><font face='{@n_fontType}' size='{@n_fontSyze}' color='{@n_fontColor}'><xsl:value-of select="@name"/>: </font></i></b>
			</xsl:when>
			<xsl:otherwise>
				<font face='{@n_fontType}' size='{@n_fontSyze}' color='{@n_fontColor}'><xsl:value-of select="@name"/>: </font>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:if>

	<xsl:choose>
		<xsl:when test="@v_fontStyle[. = 'PLAIN']">
			<font face='{@v_fontType}' size='{@v_fontSyze}' color='{@v_fontColor}'><xsl:value-of select="@value"/></font>
		</xsl:when>
		<xsl:when test="@v_fontStyle[. = 'BOLD']">
			<b><font face='{@v_fontType}' size='{@v_fontSyze}' color='{@v_fontColor}'><xsl:value-of select="@value"/></font></b>
		</xsl:when>
		<xsl:when test="@v_fontStyle[. = 'ITALIC']">
			<i><font face='{@v_fontType}' size='{@v_fontSyze}' color='{@v_fontColor}'><xsl:value-of select="@value"/></font></i>
		</xsl:when>
		<xsl:when test="@v_fontStyle[. = 'BOLD_AND_ITALIC']">
			<b><i><font face='{@v_fontType}' size='{@v_fontSyze}' color='{@v_fontColor}'><xsl:value-of select="@value"/></font></i></b>
		</xsl:when>
		<xsl:otherwise>
			<font face='{@v_fontType}' size='{@v_fontSyze}' color='{@v_fontColor}'><xsl:value-of select="@value"/></font>
		</xsl:otherwise>
	</xsl:choose>
	</p>
</xsl:template>

<xsl:variable name="empty_string"/>

</xsl:stylesheet>