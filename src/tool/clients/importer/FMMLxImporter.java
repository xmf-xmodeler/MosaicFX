package tool.clients.importer;

import javafx.concurrent.Task;
import org.w3c.dom.Node;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.serializer.XmlManager;

import java.util.List;

public class FMMLxImporter {
    private final XmlManager xmlManager;
    private final AbstractPackageViewer diagram;
    ProtocolHandler protocolHandler;

    public FMMLxImporter(String sourcePath, AbstractPackageViewer diagram) {
        this.xmlManager = new XmlManager(sourcePath);
        this.diagram = diagram;
        protocolHandler = new ProtocolHandler(diagram);
        System.out.println(sourcePath);
    }

    public void handleLogs() {
        Node logsNode = xmlManager.getLogs();

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                List<String> problems;
                protocolHandler.readLogs(logsNode);
                problems = protocolHandler.getProblems();
                if(problems.size()==0){
                    protocolHandler.executeMerge(logsNode, diagram.getComm());
                } else {
                    System.out.println(problems);
                }
                return null;
            }
        };
        new Thread(task).start();
    }
}
