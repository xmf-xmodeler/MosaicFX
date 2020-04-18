package tool.clients.fmmlxdiagrams.instancegenerator.dialogresult;

import tool.clients.fmmlxdiagrams.dialogs.results.DialogResult;

import java.util.ArrayList;
import java.util.List;

public class ValueGeneratorNormalDistributionDialogResult extends DialogResult {

    private String attributeType, meanValue, stdDevValue, rangeMinValue, rangeMaxValue;

    public ValueGeneratorNormalDistributionDialogResult(String attributeType, String meanValue, String stdDevValue, String rangeMinValue, String rangeMaxValue) {
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

    public List<String> getParameter(){
        List<String> result = new ArrayList<>();
        result.add(meanValue);
        result.add(stdDevValue);
        result.add(rangeMinValue);
        result.add(rangeMaxValue);

        return result;
    }

}
