package tool.clients.fmmlxdiagrams.dialogs.results.instancegenerator;

import tool.clients.fmmlxdiagrams.dialogs.results.DialogResult;

public class AttributeGeneratorNormalDistributionDialogResult extends DialogResult {

    private String attributeType, meanValue, stdDevValue, rangeMinValue, rangeMaxValue;

    public AttributeGeneratorNormalDistributionDialogResult(String attributeType, String meanValue, String stdDevValue, String rangeMinValue, String rangeMaxValue) {
        super();
        this.attributeType = attributeType;
        this.meanValue= meanValue;
        this.stdDevValue= stdDevValue;
        this.rangeMinValue = rangeMinValue;
        this.rangeMaxValue = rangeMaxValue;
    }

    public String getAttributeType() {
        return attributeType;
    }

    public String getMeanValue() {
        return meanValue;
    }

    public String getStdDevValue() {
        return stdDevValue;
    }

    public String getRangeMinValue() {
        return rangeMinValue;
    }

    public String getRangeMaxValue() {
        return rangeMaxValue;
    }


}
