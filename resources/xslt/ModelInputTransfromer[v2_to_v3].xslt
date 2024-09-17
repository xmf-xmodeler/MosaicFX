<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:redirect="http://xml.apache.org/xalan/redirect" extension-element-prefixes="redirect" xmlns:xalan="http://xml.apache.org/xslt">
	<xsl:output method="xml" indent="yes" xalan:indent-amount="4"/>
	<!-- The main difference between Verson 2 and 3 is that the diagram DispalyProperties were moved from the attributes to an own element -->
	<xsl:template match="/">
		<xsl:element name="XModeler">
			<xsl:comment>This document was transformed from version 2 to version 3</xsl:comment>
			<xsl:element name="Version">3</xsl:element>
			<!-- Categories is never used later so it will not be copied-->
			<xsl:copy-of select="/XModeler/Projects"/>
			<xsl:copy-of select="/XModeler/Logs"/>
			<xsl:element name="Diagrams">
				<xsl:apply-templates select="//Diagrams/Diagram"/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	<xsl:template match="Diagram">
	<!-- Categories and Owners are not used later and will not be transferred-->
		<xsl:element name="Diagram">
			<xsl:attribute name="package_path">
				<xsl:value-of select="@package_path"/>
			</xsl:attribute>
			<xsl:attribute name="label">
				<xsl:value-of select="@label"/>
			</xsl:attribute>
			<xsl:copy-of select="Objects"/>
			<xsl:copy-of select="Edges"/>
			<xsl:copy-of select="Labels"/>
			<xsl:copy-of select="View"/>
			<xsl:element name="DiagramDisplayProperties">
				<!-- Models from this version can not have a value for concrete syntax because the feature was later implemented. False was chosen as default-->
				<xsl:attribute name="CONCRETESYNTAX">false</xsl:attribute>
				<xsl:attribute name="CONSTRAINTREPORTS">
					<xsl:value-of select="@showConstraintReports"/>
				</xsl:attribute>
				<xsl:attribute name="CONSTRAINTS">
					<xsl:value-of select="@showConstraints"/>
				</xsl:attribute>
				<xsl:attribute name="DERIVEDATTRIBUTES">
					<xsl:value-of select="@showDerivedAttributes"/>
				</xsl:attribute>
				<xsl:attribute name="DERIVEDOPERATIONS">
					<xsl:value-of select="@showDerivedOperations"/>
				</xsl:attribute>
				<xsl:attribute name="GETTERSANDSETTERS">
					<xsl:value-of select="@showGettersAndSetters"/>
				</xsl:attribute>
				<!--See first comment-->
				<xsl:attribute name="ISSUETABLE">false</xsl:attribute>
				<xsl:attribute name="METACLASSNAME">
					<xsl:value-of select="@showMetaClassName"/>
				</xsl:attribute>
				<xsl:attribute name="OPERATIONS">
					<xsl:value-of select="@showOperations"/>
				</xsl:attribute>
				<xsl:attribute name="OPERATIONVALUES">
					<xsl:value-of select="@showOperationValues"/>
				</xsl:attribute>
				<xsl:attribute name="SLOTS">
					<xsl:value-of select="@showSlots"/>
				</xsl:attribute>
			</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
