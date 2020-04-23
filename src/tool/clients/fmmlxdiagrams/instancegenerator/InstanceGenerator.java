package tool.clients.fmmlxdiagrams.instancegenerator;

import javafx.collections.ObservableList;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.instancegenerator.dialog.InstanceGeneratorDialog;
import tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator.ValueGenerator;

import java.util.*;

public class InstanceGenerator {

    private FmmlxDiagram diagram;
    private final FmmlxObject object;
    private int numberOfInstance;
    private boolean isAbstract;
    private ObservableList<FmmlxObject> selectedParent;
    private List<String> generatedName;

    private HashMap<FmmlxAttribute, ValueGenerator> value;

    public InstanceGenerator(FmmlxObject object) {
        this.object = object;
    }

    public void openDialog(FmmlxDiagram diagram){
        this.diagram = diagram;
        InstanceGeneratorDialog dlg = new InstanceGeneratorDialog(this);
        dlg.showAndWait();
        generateName();
    }

    public FmmlxDiagram getDiagram() {
        return diagram;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
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

    public List<String> getGeneratedInstanceName() {
        return generatedName;
    }

    public HashMap<FmmlxAttribute, String> getSlotValue(int instanceNumber) {
        HashMap<FmmlxAttribute, String> slotValue = new HashMap<>();
        for (Map.Entry<FmmlxAttribute, ValueGenerator> pair1 : value.entrySet()) {
            slotValue.put(pair1.getKey(), pair1.getValue().getGeneratedValue().get(instanceNumber));
        }
        return slotValue;
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

    public HashMap<FmmlxAttribute, ValueGenerator> getValue() {
        return this.value;
    }

    public void setValue(HashMap<FmmlxAttribute, ValueGenerator> value) {  
        this.value = value;
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

    public void generateInstance(int instanceNumber, String name, int positionX, int positionY){

//        diagram.getComm().addNewInstance(diagram, object.getId(), name, object.getLevel()-1, getParentIDs(), false, positionX, positionY);
        //TODO ask : instanceName;
//        diagram.getComm().addNewInstanceWithSlots(diagram, object.getId(),
//                getParentIDs(), getSlotValue(instanceNumber), positionX, positionY);
    }


}
