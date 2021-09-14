package tool.clients.importer;

import org.w3c.dom.Node;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.serializer.XmlManager;

import java.util.List;

public class FMMLxImporter {
    private final XmlManager xmlManager;
    private final AbstractPackageViewer diagram;
    ProtocolHandler protocolHandler;
    List<Conflict> conflicts;

    public FMMLxImporter(String sourcePath, AbstractPackageViewer diagram) {
        this.xmlManager = new XmlManager(sourcePath);
        this.diagram = diagram;
        protocolHandler = new ProtocolHandler(diagram);
    }

    public void handleLogs() {
        Node logsNode = xmlManager.getLogs();

        protocolHandler.readLogs(logsNode);
        conflicts = protocolHandler.getConflicts();
        if(conflicts.size()==0){
            protocolHandler.executeMerge(logsNode, diagram.getComm());
        } else {
            System.out.println("Conflict list : ");
            for(int i = 0; i< conflicts.size(); i++){
                System.out.println((i)+". "+ conflicts.get(i));
            }
            ConflictsDialog conflictsDialog = new ConflictsDialog(conflicts);
            conflictsDialog.show();
        }
    }
}
