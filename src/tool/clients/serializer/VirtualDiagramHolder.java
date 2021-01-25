package tool.clients.serializer;

import java.util.Vector;

public class VirtualDiagramHolder {
    Vector<VirtualFmmlxDiagram> virtualFmmlxDiagrams = new Vector<>();

    public Vector<VirtualFmmlxDiagram> getVirtualFmmlxDiagrams() {
        return virtualFmmlxDiagrams;
    }

    public void add(VirtualFmmlxDiagram virtualFmmlxDiagram){
        virtualFmmlxDiagrams.add(virtualFmmlxDiagram);
    }

    public void remove(int id){
        virtualFmmlxDiagrams.removeIf(virtualFmmlxDiagram -> virtualFmmlxDiagram.getId() == id);
    }
}
