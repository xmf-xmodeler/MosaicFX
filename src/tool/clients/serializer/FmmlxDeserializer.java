package tool.clients.serializer;

import javafx.concurrent.Task;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.xmodeler.XModeler;

import java.nio.file.Files;
import java.nio.file.Paths;

/*This Class is an abstract layer over XML to allow loading FmmlxDiagram-data from Xml file
 *And this Class contains the main method to load FmmlxDiagram data
 */
public class FmmlxDeserializer {
    private final XmlManager xmlManager;

    public FmmlxDeserializer(XmlManager xmlManager) {
        this.xmlManager = xmlManager;
    }
    /*
    * This is the main method for the load process.
    * This process is divided into several parts.
    *   First, the Project data will be read then the Project data will be sent to the XMF to create.
    *   After the project is made, each diagram-data is children from a project will be sent to XMF and recreated.
    *   After the diagram is made, all components in the XML diagrams will also be created and the position of each component is set according to the data stored in XML.*/
    public void loadProject(FmmlxDiagramCommunicator fmmlxDiagramCommunicator) {

        String projectPath = xmlManager.getProjectPath();
        String projectName = xmlManager.getProjectName(projectPath);
        NodeList diagramsNode = getDiagramsElement().getChildNodes();
        fmmlxDiagramCommunicator.createProject(projectName, xmlManager.getAllDiagramNames(), this.xmlManager.getSourcePath());
        
        boolean populated = false;
        for(int i =0; i< diagramsNode.getLength(); i++){
            Node diagramNode = diagramsNode.item(i);
            if(diagramNode.getNodeType()==Node.ELEMENT_NODE){
            	String diagramName = ((Element) diagramNode).getAttribute(SerializerConstant.ATTRIBUTE_LABEL);
            	Integer diagramId = fmmlxDiagramCommunicator.createDiagram(projectName, diagramName, this.xmlManager.getSourcePath(), FmmlxDiagramCommunicator.DiagramType.ClassDiagram);
            	if(!populated) {
                    fmmlxDiagramCommunicator.preparePositionInfo(diagramId, diagramNode);
            		populateDiagram(diagramId);
            		populated = true;
            	}
            }    
        }
    }

    //This methode works to recreate all the existing components on a diagram-data in the xml file.
    //The sequence of its reproduction corresponds to the log order stored in XML.
    public void populateDiagram(Integer diagramID) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    if(Files.exists(Paths.get(xmlManager.getSourcePath()))){
                        xmlManager.reproduceFromLog(diagramID);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("load xml file failed");
                }
                XModeler.finishOpenDiagramFromXml();
                return null;
            }
        };
        new Thread(task).start();
    }


//    public void createModelElementsFromLogfile(Integer newDiagramID) {
//        if(Files.exists(Paths.get(xmlManager.getSourcePath()))){
//            xmlManager.reproduceFromLog(newDiagramID);
//        }
//    }

    //This method is tasked with regulating the adjusting position of each component according to the data that stored in XML.
	public void alignElements(FmmlxDiagram diagram, Element diagramElement) {
        xmlManager.alignObjects(diagramElement, diagram.getID(), diagram.getComm());
        xmlManager.alignEdges(diagramElement, diagram.getID(), diagram.getComm());
        xmlManager.alignLabel(diagramElement, diagram);
	}

    public Element getDiagramsElement(){
        Element Root = xmlManager.getRoot();
        return xmlManager.getChildWithTag(Root, SerializerConstant.TAG_NAME_DIAGRAMS);
    }
}
