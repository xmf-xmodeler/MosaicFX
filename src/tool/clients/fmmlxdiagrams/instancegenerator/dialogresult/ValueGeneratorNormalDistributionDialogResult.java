package tool.clients.fmmlxdiagrams.instancegenerator.dialogresult;

import tool.clients.fmmlxdiagrams.dialogs.results.DialogResult;

import java.util.List;

public class ValueGeneratorNormalDistributionDialogResult extends DialogResult {

    private List<String> parameter;
    private String attributeType;

    public ValueGeneratorNormalDistributionDialogResult(String attributeType, List<String> parameter) {
        super();
        this.attributeType=attributeType;
        this.parameter=parameter;
    }

    public String getAttributeType() {
        return attributeType;
    }

    public List<String> getParameter(){
        return parameter;
    }

}
