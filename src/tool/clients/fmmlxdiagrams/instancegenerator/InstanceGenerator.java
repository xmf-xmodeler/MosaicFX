package tool.clients.fmmlxdiagrams.instancegenerator;

import javafx.collections.ObservableList;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.instancegenerator.dialog.InstanceGeneratorDialog;
import tool.clients.fmmlxdiagrams.instancegenerator.dialogresult.InstanceGeneratorDialogResult;
import tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator.ValueGenerator;

import java.util.*;

public class InstanceGenerator {

    private final FmmlxDiagram diagram;
    private final FmmlxObject object;
    private int numberOfInstance;
    private ObservableList<FmmlxObject> selectedParent;
    private List<String> generatedName;

    private HashMap<FmmlxAttribute, ValueGenerator> value;

    public InstanceGenerator(FmmlxDiagram diagram, FmmlxObject object) {
        this.diagram = diagram;
        this.object = object;
    }

    public void openDialog(){
        InstanceGeneratorDialog dlg = new InstanceGeneratorDialog(diagram, object);
        setDialogResult(dlg);
    }

    private void setDialogResult(InstanceGeneratorDialog dlg) {
        Optional<InstanceGeneratorDialogResult> igd = dlg.showAndWait();

        if(igd.isPresent()){
            InstanceGeneratorDialogResult result =igd.get();
            setNumberOfInstance(result.getNumberOfInstance());
            generateName();
            setSelectedParent(result.getSelectedParent());
            setValue(result.getValue());
        }
    }

    public FmmlxDiagram getDiagram() {
        return diagram;
    }

    public FmmlxObject getObject() {
        return object;
    }

    public int getNumberOfInstance() {
        return numberOfInstance;
    }

    public void setNumberOfInstance(int numberOfInstance) {
        this.numberOfInstance = numberOfInstance;
    }

    public ObservableList<FmmlxObject> getSelectedParent() {
        return selectedParent;
    }

    public void setSelectedParent(ObservableList<FmmlxObject> selectedParent) {
        this.selectedParent = selectedParent;
    }

    private void generateName(){
        this.generatedName = new ArrayList<>();
        int j = 1;
        for(int i=0; i<getNumberOfInstance();i++){
            String objectName = getObject().getName();
            if (getObject().getLevel() == 1) {
                objectName = objectName.substring(0, 1).toLowerCase() + objectName.substring(1);
            }
            String instanceName;
            boolean ok;
            do {
                instanceName = objectName + j;
                ok = diagram.isNameAvailable(instanceName);
                j++;
            } while (!ok);
            this.generatedName.add(instanceName);
        }
    }

    public List<String> getGeneratedInstanceName() {
        return generatedName;
    }

    public HashMap<FmmlxAttribute, ValueGenerator> getValue() {
        return value;
    }

    public Vector<Integer> getParentIDs(){

        Vector<Integer> parentIds = new Vector<>();

        if (!getSelectedParent().isEmpty()) {
            for (FmmlxObject o : getSelectedParent()) {
                parentIds.add(o.getId());
            }
        }
        return parentIds;
    }

    public void setValue(HashMap<String, ValueGenerator> value) {
        HashMap<FmmlxAttribute, ValueGenerator> result = new HashMap<>();
        for(Map.Entry<String, ValueGenerator> pair : value.entrySet()){
            result.put(object.getAttributeByName(pair.getKey()), pair.getValue());
        }
        this.value = result;
    }

    public void generateInstance(String name, int positionX, int positionY){

        //TODO
        // this communicator is originally for normal add instance
        // still need another communicator that include slotValue

        diagram.getComm().addNewInstance(diagram, object.getId(), name, object.getLevel() - 1,
                getParentIDs(), false, positionX, positionY);
    }
}
