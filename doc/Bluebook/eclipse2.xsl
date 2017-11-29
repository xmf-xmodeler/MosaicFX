<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        version="1.0">

<!-- Import of the original stylesheet which "just" creates 
     a bunch of HTML files from any valid DocBook instance -->
<!-- <xsl:import href="http://docbook.sourceforge.net/release/xsl/current/html/chunk.xsl"/> -->

<!-- THE FOLLOWING CODE IS XACTIUM CUSTOMISTION OF THIS TEMPLATE -->

<!-- Turn off contents in document -->

<!-- <xsl:param name="generate.toc"></xsl:param> -->

<!-- Chunk the first section (doesn't happen by default) -->

<!-- <xsl:param name="chunk.first.sections">1</xsl:param> -->

<!-- Don't put numbers on chapters -->

<!-- <xsl:param name="chapter.autolabel">0</xsl:param> -->

<!-- Surpress navigation bars -->

<xsl:param name="suppress.navigation">1</xsl:param>

<!-- Include css stylesheet -->

<xsl:param name ="html.stylesheet">book.css</xsl:param>

<!-- Custom copyright footer -->

<xsl:template name="user.footer.navigation">
    <p/>
    <p> <img src="copyright.gif"/>
    </p>
</xsl:template>

<!-- XACTIUM CUSTOMISATION ENDS HERE -->

<!-- You must plug-in your custom templates here --> 
<xsl:template match="/">
  <!-- Call original code from the imported stylesheet -->
  <xsl:apply-imports/>

  <!-- Call custom templates for the ToC and the manifest -->
  <xsl:call-template name="etoc"/>

  <!-- <xsl:call-template name="plugin.xml"/> -->
</xsl:template>

<!-- Template for creating auxiliary ToC file -->
<xsl:template name="etoc">
  <xsl:call-template name="write.chunk">
    <xsl:with-param name="filename" select="'toc.xml'"/>
    <xsl:with-param name="method" select="'xml'"/>
    <xsl:with-param name="encoding" select="'utf-8'"/>
    <xsl:with-param name="indent" select="'yes'"/>
    <xsl:with-param name="content">

      <!-- Get the title of the root element -->
      <xsl:variable name="title">
        <xsl:apply-templates select="/*" mode="title.markup"/>
      </xsl:variable>
    
      <!-- Get HTML filename for the root element -->
      <xsl:variable name="href">
        <xsl:call-template name="href.target.with.base.dir">
          <xsl:with-param name="object" select="/*"/>

        </xsl:call-template>
      </xsl:variable>
      
      <!-- Create root element of ToC file -->
      <toc label="{$title}" topic="{$href}">
        <!-- Get ToC for all children of the root element -->
        <xsl:apply-templates select="/*/*" mode="etoc"/>
      </toc>
      
    </xsl:with-param>
  </xsl:call-template>

</xsl:template>

<!-- Template which converts all DocBook containers into 
     one entry in the ToC file -->
<xsl:template match="book|part|reference|preface|chapter|
                     bibliography|appendix|article|glossary|
                     section|sect1|sect2|sect3|sect4|sect5|
                     refentry|colophon|bibliodiv|index" 
              mode="etoc">
  <!-- Get the title of the current element -->
  <xsl:variable name="title">
    <xsl:apply-templates select="." mode="title.markup"/>
  </xsl:variable>

  <!-- Get HTML filename for the current element -->
  <xsl:variable name="href">

    <xsl:call-template name="href.target.with.base.dir"/>
  </xsl:variable>

  <!-- Create ToC entry for the current node and process its 
       container-type children further -->
  <topic label="{$title}" href="{$href}">
    <xsl:apply-templates select="part|reference|preface|chapter|
                                 bibliography|appendix|article|
                                 glossary|section|sect1|sect2|
                                 sect3|sect4|sect5|refentry|
                                 colophon|bibliodiv|index" 
                         mode="etoc"/>
  </topic>

</xsl:template>

<!-- Default processing in the etoc mode is no processing -->

<xsl:template match="text()" mode="etoc"/>

<!-- Template for generating the manifest file -->
<xsl:template name="plugin.xml">
  <xsl:call-template name="write.chunk">
    <xsl:with-param name="filename" select="'plugin.xml'"/>
    <xsl:with-param name="method" select="'xml'"/>
    <xsl:with-param name="encoding" select="'utf-8'"/>
    <xsl:with-param name="indent" select="'yes'"/>
    <xsl:with-param name="content">
      <plugin name="{$eclipse.plugin.name}"
              id="{$eclipse.plugin.id}"
              version="1.0"
              provider-name="{$eclipse.plugin.provider}">

        <extension point="org.eclipse.help.toc">
          <toc file="toc.xml" primary="true"/>
        </extension>
      </plugin>
    </xsl:with-param>
  </xsl:call-template>
</xsl:template>

<!-- Customization parameters for the manifest file -->
<xsl:param name="eclipse.plugin.name">DocBook Online Help Sample</xsl:param>

<xsl:param name="eclipse.plugin.id">com.example.help</xsl:param>
<xsl:param name="eclipse.plugin.provider">Example provider</xsl:param>

</xsl:stylesheet>
