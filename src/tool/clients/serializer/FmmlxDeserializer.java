package tool.clients.serializer;

import javafx.concurrent.Task;
import javafx.scene.transform.Affine;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.FmmlxDiagram.DiagramViewPane;
import tool.xmodeler.XModeler;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Vector;

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
        NodeList diagramNodes = getDiagramsElement().getChildNodes();
        fmmlxDiagramCommunicator.createProject(projectName, xmlManager.getAllDiagramNames(), this.xmlManager.getSourcePath());
        
        boolean projectPopulated = false;
        for(int i =0; i< diagramNodes.getLength(); i++){
            Node diagramNode = diagramNodes.item(i);
            if(diagramNode.getNodeType()==Node.ELEMENT_NODE){
            	String diagramName = ((Element) diagramNode).getAttribute(SerializerConstant.ATTRIBUTE_LABEL);
            	Integer diagramId = fmmlxDiagramCommunicator.createDiagram(projectName, diagramName, this.xmlManager.getSourcePath(), FmmlxDiagramCommunicator.DiagramType.ClassDiagram);
            	sendDiagramViewStatus(diagramId, diagramNode, fmmlxDiagramCommunicator);
            	if(!projectPopulated) {
                    fmmlxDiagramCommunicator.preparePositionInfo(diagramId, diagramNode);
            		populateDiagram(diagramId);
            		projectPopulated = true;
            	}
            }    
        }
    }

    private void sendDiagramViewStatus(Integer diagramID, 
    								   Node diagramNode,
    								   FmmlxDiagramCommunicator fmmlxDiagramCommunicator) {
		Vector<String> names = new Vector<>();
		Vector<Affine> transformations = new Vector<>();
		NodeList logList = diagramNode.getChildNodes();
		for(int i = 0; i < logList.getLength(); i++) {
			try{
				Element view = (Element)(logList.item(i));
				if("View".equals(view.getNodeName())) {
					names.add(view.getAttribute("name"));
		        	double xx = 1., tx = 0., ty = 0.;
		        	try{ xx = Double.parseDouble(view.getAttribute("xx")); } catch (Exception e) {System.err.println("Cannot read xx: " + e.getMessage() + " Using default instead");}
		        	try{ tx = Double.parseDouble(view.getAttribute("tx")); } catch (Exception e) {System.err.println("Cannot read xx: " + e.getMessage() + " Using default instead");}
		        	try{ ty = Double.parseDouble(view.getAttribute("ty")); } catch (Exception e) {System.err.println("Cannot read xx: " + e.getMessage() + " Using default instead");}
		        	transformations.add(new Affine(xx, 0, tx, 0, xx, ty));
				}
			} catch (Exception e) {} // Ignore the stuff we don't need to worry about
		}    	
    	fmmlxDiagramCommunicator.sendViewStatus(diagramID, names, transformations);
    	NamedNodeMap attributes = diagramNode.getAttributes();
    	HashMap<String, Boolean> map = new HashMap<>();
    	for(int i = 0; i < attributes.getLength(); i++) { 
    		// this adds all attributes to the options list.
    		// However those we don't care about will be dropped later on anyway
    		Node attribute = attributes.item(i);
    		map.put(attribute.getNodeName(), "true".equals(attribute.getNodeValue()));
    	}
    	fmmlxDiagramCommunicator.sendViewOptions(diagramID, map);
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
        xmlManager.alignLabels(diagramElement, diagram);
	}

    public Element getDiagramsElement(){
        Element Root = xmlManager.getRoot();
        return xmlManager.getChildWithTag(Root, SerializerConstant.TAG_NAME_DIAGRAMS);
    }
}
