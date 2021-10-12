package tool.clients.importer;

import org.w3c.dom.Node;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.serializer.XmlManager;

import java.util.List;

//this class serves a importer when you want to add another project to the current project
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

    //this method is called if merge process starts,
    //first, all possible conflicts will be collected
    //if conflicts exist then all conflicts will be shown in a Dialog
    //if there is no conflict, merge method will be called.
    public void handleLogs() {
        Node logsNode = xmlManager.getLogs();

        protocolHandler.readLogs(logsNode);
        conflicts = protocolHandler.getConflicts();
        if(conflicts.size()==0){
            protocolHandler.executeMerge(logsNode, diagram.getComm());
        } else {
            ConflictsDialog conflictsDialog = new ConflictsDialog(conflicts);
            conflictsDialog.show();
        }
    }
}
