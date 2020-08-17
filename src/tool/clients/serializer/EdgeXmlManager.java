package tool.clients.serializer;

import javafx.geometry.Point2D;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tool.clients.fmmlxdiagrams.*;
import tool.clients.serializer.interfaces.IXmlManager;

import javax.xml.transform.TransformerException;
import java.util.List;
import java.util.Vector;

public class EdgeXmlManager implements IXmlManager {

    private final XmlHandler xmlHandler;

    public EdgeXmlManager(){
        this.xmlHandler = new XmlHandler();
    }

    public Node createAssociationXmlNode(FmmlxDiagram fmmlxDiagram, FmmlxAssociation fmmlxAssociation) throws TransformerException {

        int id = fmmlxAssociation.getId();
        String name = fmmlxAssociation.getName();
        String type = "association";
        Vector<Point2D> intermediatePoints = fmmlxAssociation.getIntermediatePoints();
        int parentAssociationId = fmmlxAssociation.getParentAssociationId();
        int levelStartToEnd = fmmlxAssociation.getLevelStartToEnd();
        int levelEndToStart = fmmlxAssociation.getLevelEndToStart();
        FmmlxObject sourceNode = fmmlxAssociation.getSourceNode();
        FmmlxObject targetNode = fmmlxAssociation.getTargetNode();
        PortRegion sourcePort = fmmlxAssociation.getSourcePort();
        PortRegion targetPort = fmmlxAssociation.getTargetPort();
        String projectPath = fmmlxDiagram.getPackagePath();
        int owner = fmmlxDiagram.getID();
        Multiplicity multiplicityStartToEnd = fmmlxAssociation.getMultiplicityStartToEnd();
        Multiplicity multiplicityEndToStart = fmmlxAssociation.getMultiplicityEndToStart();

        Element edge = (Element) xmlHandler.createXmlElement(XmlConstant.TAG_NAME_EDGE);
        edge.setAttribute(XmlConstant.ATTRIBUTE_ID, id+"");
        edge.setAttribute(XmlConstant.ATTRIBUTE_OWNER, owner+"");
        edge.setAttribute(XmlConstant.ATTRIBUTE_REFERENCE, projectPath);
        edge.setAttribute(XmlConstant.ATTRIBUTE_TYPE, type);
        edge.setAttribute(XmlConstant.ATTRIBUTE_SOURCE_NODE, sourceNode.getId()+"");
        edge.setAttribute(XmlConstant.ATTRIBUTE_TARGET_NODE, targetNode.getId()+"");
        edge.setAttribute(XmlConstant.ATTRIBUTE_SOURCE_PORT, sourcePort+"");
        edge.setAttribute(XmlConstant.ATTRIBUTE_TARGET_PORT, targetPort+"");
        edge.setAttribute(XmlConstant.ATTRIBUTE_NAME, name);
        edge.setAttribute(XmlConstant.ATTRIBUTE_PARENT_ASSOCIATION, parentAssociationId+"");
        edge.setAttribute(XmlConstant.ATTRIBUTE_LEVEL_START_TO_END, levelStartToEnd+"");
        edge.setAttribute(XmlConstant.ATTRIBUTE_LEVEL_END_TO_START, levelEndToStart+"");
        edge.setAttribute(XmlConstant.ATTRIBUTE_MULTIPLICITY_START_TO_END, multiplicityStartToEnd.toString());
        edge.setAttribute(XmlConstant.ATTRIBUTE_MULTIPLICITY_END_TO_START, multiplicityEndToStart.toString());

        Node intermediatePointsNode = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_INTERMEDIATE_POINTS);
        for(Point2D point2D : intermediatePoints){
            Element intermediatePoint = (Element) xmlHandler.createXmlElement(XmlConstant.TAG_NAME_INTERMEDIATE_POINT);
            intermediatePoint.setAttribute(XmlConstant.ATTRIBUTE_COORDINATE_X, point2D.getX()+"");
            intermediatePoint.setAttribute(XmlConstant.ATTRIBUTE_COORDINATE_Y, point2D.getY()+"");
            intermediatePointsNode.appendChild(intermediatePoint);
        }

        xmlHandler.addIntermediatePointsElement(edge, intermediatePointsNode);
        return edge;
    }

    public Node createDelegationXmlNode(FmmlxDiagram fmmlxDiagram, DelegationEdge delegationEdge) throws TransformerException {

        int id = delegationEdge.getId();
        String type = "delegation";
        Vector<Point2D> intermediatePoints = delegationEdge.getIntermediatePoints();
        FmmlxObject childNode = delegationEdge.getChild();
        FmmlxObject parentNode = delegationEdge.getParent();
        PortRegion sourcePort = delegationEdge.getSourcePort();
        PortRegion targetPort = delegationEdge.getTargetPort();
        String projectPath = fmmlxDiagram.getPackagePath();
        int owner = fmmlxDiagram.getID();

        Element edge = (Element) xmlHandler.createXmlElement(XmlConstant.TAG_NAME_EDGE);
        edge.setAttribute(XmlConstant.ATTRIBUTE_ID, id+"");
        edge.setAttribute(XmlConstant.ATTRIBUTE_OWNER, owner+"");
        edge.setAttribute(XmlConstant.ATTRIBUTE_REFERENCE, projectPath);
        edge.setAttribute(XmlConstant.ATTRIBUTE_TYPE, type);
        edge.setAttribute(XmlConstant.ATTRIBUTE_SOURCE_NODE, childNode.getId()+"");
        edge.setAttribute(XmlConstant.ATTRIBUTE_TARGET_NODE, parentNode.getId()+"");
        edge.setAttribute(XmlConstant.ATTRIBUTE_SOURCE_PORT, sourcePort+"");
        edge.setAttribute(XmlConstant.ATTRIBUTE_TARGET_PORT, targetPort+"");

        Node intermediatePointsNode = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_INTERMEDIATE_POINTS);
        for(Point2D point2D : intermediatePoints){
            Element intermediatePoint = (Element) xmlHandler.createXmlElement(XmlConstant.TAG_NAME_INTERMEDIATE_POINT);
            intermediatePoint.setAttribute(XmlConstant.ATTRIBUTE_COORDINATE_X, point2D.getX()+"");
            intermediatePoint.setAttribute(XmlConstant.ATTRIBUTE_COORDINATE_Y, point2D.getY()+"");
            intermediatePointsNode.appendChild(intermediatePoint);
        }

        xmlHandler.addIntermediatePointsElement(edge, intermediatePointsNode);

        return edge;
    }

    public Node createInheritanceXmlNode(FmmlxDiagram fmmlxDiagram, InheritanceEdge inheritanceEdge) throws TransformerException {

        int id = inheritanceEdge.getId();
        String type = "inheritance";
        Vector<Point2D> intermediatePoints = inheritanceEdge.getIntermediatePoints();
        FmmlxObject childNode = inheritanceEdge.getChild();
        FmmlxObject parentNode = inheritanceEdge.getParent();
        PortRegion sourcePort = inheritanceEdge.getSourcePort();
        PortRegion targetPort = inheritanceEdge.getTargetPort();
        String projectPath = fmmlxDiagram.getPackagePath();
        int owner = fmmlxDiagram.getID();

        Element edge = (Element) xmlHandler.createXmlElement(XmlConstant.TAG_NAME_EDGE);
        edge.setAttribute(XmlConstant.ATTRIBUTE_ID, id+"");
        edge.setAttribute(XmlConstant.ATTRIBUTE_OWNER, owner+"");
        edge.setAttribute(XmlConstant.ATTRIBUTE_REFERENCE, projectPath);
        edge.setAttribute(XmlConstant.ATTRIBUTE_TYPE, type);
        edge.setAttribute(XmlConstant.ATTRIBUTE_SOURCE_NODE, childNode.getId()+"");
        edge.setAttribute(XmlConstant.ATTRIBUTE_TARGET_NODE, parentNode.getId()+"");
        edge.setAttribute(XmlConstant.ATTRIBUTE_SOURCE_PORT, sourcePort+"");
        edge.setAttribute(XmlConstant.ATTRIBUTE_TARGET_PORT, targetPort+"");

        Node intermediatePointsNode = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_INTERMEDIATE_POINTS);
        for(Point2D point2D : intermediatePoints){
            Element intermediatePoint = (Element) xmlHandler.createXmlElement(XmlConstant.TAG_NAME_INTERMEDIATE_POINT);
            intermediatePoint.setAttribute(XmlConstant.ATTRIBUTE_COORDINATE_X, point2D.getX()+"");
            intermediatePoint.setAttribute(XmlConstant.ATTRIBUTE_COORDINATE_Y, point2D.getY()+"");
            intermediatePointsNode.appendChild(intermediatePoint);
        }

        xmlHandler.addIntermediatePointsElement(edge, intermediatePointsNode);
        return edge;
    }

    public Node createLinkXmlNode(FmmlxDiagram fmmlxDiagram, FmmlxLink fmmlxLink) {

        int id = fmmlxLink.getId();
        String type = "link";
        int ofId = fmmlxLink.getOfId();
        Vector<Point2D> intermediatePoints = fmmlxLink.getIntermediatePoints();
        FmmlxObject childNode = fmmlxLink.getStartNode();
        FmmlxObject parentNode = fmmlxLink.getEndNode();
        PortRegion sourcePort = fmmlxLink.getSourcePort();
        PortRegion targetPort = fmmlxLink.getTargetPort();
        String projectPath = fmmlxDiagram.getPackagePath();
        int owner = fmmlxDiagram.getID();

        Element edge = (Element) xmlHandler.createXmlElement(XmlConstant.TAG_NAME_EDGE);
        edge.setAttribute(XmlConstant.ATTRIBUTE_ID, id+"");
        edge.setAttribute(XmlConstant.ATTRIBUTE_OWNER, owner+"");
        edge.setAttribute(XmlConstant.ATTRIBUTE_REFERENCE, projectPath);
        edge.setAttribute(XmlConstant.ATTRIBUTE_TYPE, type);
        edge.setAttribute(XmlConstant.TAG_NAME_INTERMEDIATE_POINTS, intermediatePoints.toString());
        edge.setAttribute(XmlConstant.ATTRIBUTE_SOURCE_NODE, childNode.getId()+"");
        edge.setAttribute(XmlConstant.ATTRIBUTE_TARGET_NODE, parentNode.getId()+"");
        edge.setAttribute(XmlConstant.ATTRIBUTE_SOURCE_PORT, sourcePort+"");
        edge.setAttribute(XmlConstant.ATTRIBUTE_TARGET_PORT, targetPort+"");
        edge.setAttribute(XmlConstant.ATTRIBUTE_OF, ofId+"");
        return edge;
    }

    @Override
    public void add(Node node) {
        if(node!=null){
            Element newEdge = (Element) node;

            Node diagrams = xmlHandler.getDiagramsNode();
            NodeList diagramNodeList = diagrams.getChildNodes();

            for(int i=0 ; i<diagramNodeList.getLength(); i++){
                if(diagramNodeList.item(i).getNodeType()==Node.ELEMENT_NODE){
                    Element diagram = (Element) diagramNodeList.item(i);
                    if(diagram.getAttribute(XmlConstant.ATTRIBUTE_ID).equals(newEdge.getAttribute(XmlConstant.ATTRIBUTE_OWNER))){
                        Element edges = (Element) getEdgesNode(diagram);
                        try {
                            xmlHandler.addEdgeElement(edges, newEdge);
                        } catch (TransformerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void remove(Node node) {
        //TODO
    }

    @Override
    public List<Node> getAll() {
        return null;
    }

    private Node getEdgesNode(Node diagramNode){
        return xmlHandler.getXmlHelper().getNodeByTag(diagramNode, XmlConstant.TAG_NAME_EDGES);
    }
}
