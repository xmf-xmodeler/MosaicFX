12.07.2018 To be able to display text in red, add the following to the end of docbook.xsl
(.ant4docbook\docbook-xsl-1.78.1\fo\docbook.xsl):

<!-- 2018-07-12 Melanie Constantin: read font function extension to emphasis added -->
 <xsl:template match="emphasis[@role='red']">
    <fo:inline color="red">
      <xsl:apply-templates/>
    </fo:inline>
  </xsl:template>
