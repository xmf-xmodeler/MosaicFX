<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:redirect="http://xml.apache.org/xalan/redirect" extension-element-prefixes="redirect" xmlns:xalan="http://xml.apache.org/xslt">
	<xsl:output method="xml" indent="yes" xalan:indent-amount="4"/>
	<xsl:variable name="packagePath" select="//Project/@name"/>
	<xsl:template match="XModeler">
		<xsl:element name="XModelerPackage">
			<xsl:attribute name="Version">4</xsl:attribute>
			<xsl:attribute name="path">
				<xsl:value-of select="$packagePath"/>
			</xsl:attribute>
			<xsl:copy-of select="comment()"/>
			<xsl:comment>This document was transformed from version 3 to version 4</xsl:comment>
			<xsl:element name="Model">
				<!--In older versions of custom created models a packages has exactly one model-->
				<xsl:attribute name="name">
					<xsl:value-of select="$packagePath"/>
				</xsl:attribute>
				<xsl:apply-templates select="//Logs"/>
			</xsl:element>
			<xsl:element name="Diagrams">
				<xsl:apply-templates select="//Diagrams/Diagram"/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	<xsl:template match="Logs">
		<xsl:copy-of select="node()"/>
	</xsl:template>
	<xsl:template match="Diagram">
		<xsl:element name="Diagram">
			<xsl:attribute name="name">
				<xsl:value-of select="@label"/>
			</xsl:attribute>
			<xsl:element name="Instances">
				<xsl:apply-templates select="Objects/Object"/>
			</xsl:element>
			<xsl:element name="Edges">
				<xsl:apply-templates select="Edges/Edge"/>
			</xsl:element>
			<xsl:copy-of select="Labels"/>
			<xsl:copy-of select="View"/>
			<!--In the earlier versions there was only an export of the DiagramDisplayProperties for the first diagram. To compensate the missing information default values are inserted.-->
			<xsl:element name="DiagramDisplayProperties">
				
				<xsl:element name="CONSTRAINTREPORTS">
					<xsl:choose>
						<xsl:when test="DiagramDisplayProperties/@CONSTRAINTREPORTS"><xsl:value-of select="DiagramDisplayProperties/@CONSTRAINTREPORTS"/></xsl:when>
						<xsl:otherwise>true</xsl:otherwise>
					</xsl:choose>
				</xsl:element>
				
				
				<xsl:element name="CONSTRAINTS">	
					<xsl:choose>
						<xsl:when test="DiagramDisplayProperties/@CONSTRAINTS != ''"><xsl:value-of select="DiagramDisplayProperties/@CONSTRAINTS"/></xsl:when>
						<xsl:otherwise>true</xsl:otherwise>
					</xsl:choose>
				</xsl:element>
				<xsl:element name="DERIVEDATTRIBUTES">
					<xsl:choose>
						<xsl:when test="DiagramDisplayProperties/@DERIVEDATTRIBUTES != ''"><xsl:value-of select="DiagramDisplayProperties/@DERIVEDATTRIBUTES"/></xsl:when>
						<xsl:otherwise>true</xsl:otherwise>
					</xsl:choose>
				</xsl:element>
				<xsl:element name="DERIVEDOPERATIONS">
					<xsl:choose>
						<xsl:when test="DiagramDisplayProperties/@DERIVEDOPERATIONS != ''"><xsl:value-of select="DiagramDisplayProperties/@DERIVEDOPERATIONS"/></xsl:when>
						<xsl:otherwise>true</xsl:otherwise>
					</xsl:choose>
				</xsl:element>
				<xsl:element name="GETTERSANDSETTERS">
					<xsl:choose>
						<xsl:when test="DiagramDisplayProperties/@GETTERSANDSETTERS != ''"><xsl:value-of select="DiagramDisplayProperties/@GETTERSANDSETTERS"/></xsl:when>
						<xsl:otherwise>true</xsl:otherwise>
					</xsl:choose>
				</xsl:element>
				<xsl:element name="ISSUETABLE">
					<xsl:choose>
						<xsl:when test="DiagramDisplayProperties/@ISSUETABLE != ''"><xsl:value-of select="DiagramDisplayProperties/@ISSUETABLE"/></xsl:when>
						<xsl:otherwise>false</xsl:otherwise>
					</xsl:choose>
				</xsl:element>
				<xsl:element name="METACLASSNAME">
					<xsl:choose>
						<xsl:when test="DiagramDisplayProperties/@METACLASSNAME != ''"><xsl:value-of select="DiagramDisplayProperties/@METACLASSNAME"/></xsl:when>
						<xsl:otherwise>false</xsl:otherwise>
					</xsl:choose>
				</xsl:element>
				<xsl:element name="OPERATIONS">
					<xsl:choose>
						<xsl:when test="DiagramDisplayProperties/@OPERATIONS != ''"><xsl:value-of select="DiagramDisplayProperties/@OPERATIONS"/></xsl:when>
						<xsl:otherwise>true</xsl:otherwise>
					</xsl:choose>
				</xsl:element>
				<xsl:element name="OPERATIONVALUES">
					<xsl:choose>
						<xsl:when test="DiagramDisplayProperties/@OPERATIONVALUES != ''"><xsl:value-of select="DiagramDisplayProperties/@OPERATIONVALUES"/></xsl:when>
						<xsl:otherwise>true</xsl:otherwise>
					</xsl:choose>
				</xsl:element>
				<xsl:element name="SLOTS">
					<xsl:choose>
						<xsl:when test="DiagramDisplayProperties/@SLOTS != ''"><xsl:value-of select="DiagramDisplayProperties/@SLOTS"/></xsl:when>
						<xsl:otherwise>true</xsl:otherwise>
					</xsl:choose>
				</xsl:element>
				<xsl:element name="CONCRETESYNTAX">
					<xsl:choose>
						<xsl:when test="DiagramDisplayProperties/@CONCRETESYNTAX != ''"><xsl:value-of select="DiagramDisplayProperties/@CONCRETESYNTAX"/></xsl:when>
						<xsl:otherwise>true</xsl:otherwise>
					</xsl:choose>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	<xsl:template match="Object">
		<xsl:element name="Instance">
			<xsl:attribute name="hidden">
				<xsl:value-of select="@hidden"/>
			</xsl:attribute>
			<xsl:attribute name="path">
				<xsl:value-of select="@ref"/>
			</xsl:attribute>
			<xsl:attribute name="xCoordinate">
				<xsl:value-of select="@x"/>
			</xsl:attribute>
			<xsl:attribute name="yCoordinate">
				<xsl:value-of select="@y"/>
			</xsl:attribute>
		</xsl:element>
	</xsl:template>
	<xsl:template match="Edge">
		<xsl:element name="Edge">
			<xsl:copy-of select="@*[name()!='ref']"/>
			<xsl:attribute name="path">
				<xsl:value-of select="@ref"/>
			</xsl:attribute>
			<xsl:copy-of select="IntermediatePoints"/>
			<xsl:element name="Labels">
				<xsl:apply-templates select="ancestor::Diagram/Labels/Label">
					<xsl:with-param name="edgePath">
						<xsl:value-of select="@ref"/>
					</xsl:with-param>
				</xsl:apply-templates>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	<xsl:template match="Label">
		<xsl:param name="edgePath"/>
		<xsl:if test="@ownerID = $edgePath">
			<xsl:copy-of select="."/>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
