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

    public Node createEdgeXmlNode(FmmlxDiagram fmmlxDiagram, Edge edge) throws TransformerException {
        Vector<Point2D> intermediatePoints = edge.getIntermediatePoints();
        FmmlxObject sourceNode = edge.getSourceNode();
        FmmlxObject targetNode = edge.getTargetNode();
        PortRegion sourcePort = edge.getSourcePortRegion();
        PortRegion targetPort = edge.getTargetPortRegion();
        String projectPath = fmmlxDiagram.getPackagePath();
        String owner = fmmlxDiagram.getDiagramLabel();

        Element edgeElement = (Element) xmlHandler.createXmlElement(XmlConstant.TAG_NAME_EDGE);
        edgeElement.setAttribute(XmlConstant.ATTRIBUTE_OWNER, owner);
        edgeElement.setAttribute(XmlConstant.ATTRIBUTE_REFERENCE, projectPath);
        edgeElement.setAttribute(XmlConstant.ATTRIBUTE_SOURCE_NODE, sourceNode.getName()+"");
        edgeElement.setAttribute(XmlConstant.ATTRIBUTE_TARGET_NODE, targetNode.getName()+"");
        edgeElement.setAttribute(XmlConstant.ATTRIBUTE_SOURCE_PORT, sourcePort+"");
        edgeElement.setAttribute(XmlConstant.ATTRIBUTE_TARGET_PORT, targetPort+"");

        Node intermediatePointsNode = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_INTERMEDIATE_POINTS);
        for(Point2D point2D : intermediatePoints){
            Element intermediatePoint = (Element) xmlHandler.createXmlElement(XmlConstant.TAG_NAME_INTERMEDIATE_POINT);
            intermediatePoint.setAttribute(XmlConstant.ATTRIBUTE_COORDINATE_X, point2D.getX()+"");
            intermediatePoint.setAttribute(XmlConstant.ATTRIBUTE_COORDINATE_Y, point2D.getY()+"");
            intermediatePointsNode.appendChild(intermediatePoint);
        }
        xmlHandler.addIntermediatePointsElement(edgeElement, intermediatePointsNode);

        return edgeElement;
    }

    public Node createAssociationXmlNode(FmmlxDiagram fmmlxDiagram, FmmlxAssociation fmmlxAssociation) throws TransformerException {
        String name = fmmlxAssociation.getName();
        String type = XmlConstant.EdgeType.ASSOCIATION;
        String parentAssociationName = fmmlxAssociation.getParentAssociationName();
        int levelStartToEnd = fmmlxAssociation.getLevelStartToEnd();
        int levelEndToStart = fmmlxAssociation.getLevelEndToStart();
        Multiplicity multiplicityStartToEnd = fmmlxAssociation.getMultiplicityStartToEnd();
        Multiplicity multiplicityEndToStart = fmmlxAssociation.getMultiplicityEndToStart();

        Element edge = (Element) createEdgeXmlNode(fmmlxDiagram, fmmlxAssociation);
        edge.setAttribute(XmlConstant.ATTRIBUTE_TYPE, type);
        edge.setAttribute(XmlConstant.ATTRIBUTE_NAME, name);
        edge.setAttribute(XmlConstant.ATTRIBUTE_PARENT_ASSOCIATION, parentAssociationName+"");
        edge.setAttribute(XmlConstant.ATTRIBUTE_LEVEL_START_TO_END, levelStartToEnd+"");
        edge.setAttribute(XmlConstant.ATTRIBUTE_LEVEL_END_TO_START, levelEndToStart+"");
        edge.setAttribute(XmlConstant.ATTRIBUTE_MULTIPLICITY_START_TO_END, multiplicityStartToEnd.toString());
        edge.setAttribute(XmlConstant.ATTRIBUTE_MULTIPLICITY_END_TO_START, multiplicityEndToStart.toString());
        return edge;
    }

    public Node createDelegationXmlNode(FmmlxDiagram fmmlxDiagram, DelegationEdge delegationEdge) throws TransformerException {
        String type = XmlConstant.EdgeType.DELEGATION;

        Element edge = (Element) createEdgeXmlNode(fmmlxDiagram, delegationEdge);
        edge.setAttribute(XmlConstant.ATTRIBUTE_TYPE, type);
        return edge;
    }

    public Node createInheritanceXmlNode(FmmlxDiagram fmmlxDiagram, InheritanceEdge inheritanceEdge) throws TransformerException {
        String type = XmlConstant.EdgeType.INHERITANCE;

        Element edge = (Element) createEdgeXmlNode(fmmlxDiagram, inheritanceEdge);
        edge.setAttribute(XmlConstant.ATTRIBUTE_TYPE, type);
        return edge;
    }

    public Node createLinkXmlNode(FmmlxDiagram fmmlxDiagram, FmmlxLink fmmlxLink) throws TransformerException {

        String type = XmlConstant.EdgeType.LINK;
        String ofName = fmmlxLink.getOfName();

        Element edge = (Element) createEdgeXmlNode(fmmlxDiagram, fmmlxLink);
        edge.setAttribute(XmlConstant.ATTRIBUTE_TYPE, type);
        edge.setAttribute(XmlConstant.ATTRIBUTE_OF, ofName+"");
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
                    if(diagram.getAttribute(XmlConstant.ATTRIBUTE_LABEL).equals(newEdge.getAttribute(XmlConstant.ATTRIBUTE_OWNER))){
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

    public void alignEdges(FmmlxDiagram fmmlxDiagram){
        Node diagrams = xmlHandler.getDiagramsNode();
        NodeList diagramNodeList = diagrams.getChildNodes();
        Vector<Edge> edges = fmmlxDiagram.getEdges();

        for(Edge edge : edges){
            handleEdge(fmmlxDiagram, diagramNodeList, edge);

        }
    }

    private void handleEdge(FmmlxDiagram fmmlxDiagram, NodeList diagramNodeList, Edge edge) {
        for(int i=0 ; i<diagramNodeList.getLength(); i++){
            NodeList edgeList = getEdgeList(fmmlxDiagram, diagramNodeList.item(i));
            if(edgeList!=null){
                for (int j = 0 ; j< edgeList.getLength(); j++) {
                    if(edgeList.item(j).getNodeType()==Node.ELEMENT_NODE) {
                        Element edgeElement = (Element) edgeList.item(j);

                        if (edge instanceof FmmlxAssociation) {
                            String name = ((FmmlxAssociation) edge).getName();

                            if(edgeElement.getAttribute(XmlConstant.ATTRIBUTE_NAME).equals(name) &&
                                    edgeElement.getAttribute(XmlConstant.ATTRIBUTE_TYPE).equals("association")){
                                setDirectionsAndIntermediatePoints(fmmlxDiagram, edge, edgeElement);
                                break;
                            }
                        } else if (edge instanceof DelegationEdge){
                            String name_parent = ((DelegationEdge) edge).getParent().getName();
                            String name_child = ((DelegationEdge) edge).getChild().getName();

                            if(edgeElement.getAttribute(XmlConstant.ATTRIBUTE_SOURCE_NODE).equals(name_child) &&
                                    edgeElement.getAttribute(XmlConstant.ATTRIBUTE_TARGET_NODE).equals(name_parent) &&
                                    edgeElement.getAttribute(XmlConstant.ATTRIBUTE_TYPE).equals("delegation")){
                                setDirectionsAndIntermediatePoints(fmmlxDiagram, edge, edgeElement);
                                break;
                            }
                        } else if (edge instanceof FmmlxLink) {
                            String name_parent = ((FmmlxLink) edge).getEndNode().getName();
                            String name_child = ((FmmlxLink) edge).getStartNode().getName();
                            String ofName = ((FmmlxLink) edge).getOfName();
                            if(edgeElement.getAttribute(XmlConstant.ATTRIBUTE_SOURCE_NODE).equals(name_child) &&
                                    edgeElement.getAttribute(XmlConstant.ATTRIBUTE_OF).equals(ofName) &&
                                    edgeElement.getAttribute(XmlConstant.ATTRIBUTE_TARGET_NODE).equals(name_parent) &&
                                    edgeElement.getAttribute(XmlConstant.ATTRIBUTE_TYPE).equals("link")){
                                setDirectionsAndIntermediatePoints(fmmlxDiagram, edge, edgeElement);
                                break;
                            }
                        } else if (edge instanceof InheritanceEdge) {
                            String name_parent = ((InheritanceEdge) edge).getParent().getName();
                            String name_child = ((InheritanceEdge) edge).getChild().getName();

                            if(edgeElement.getAttribute(XmlConstant.ATTRIBUTE_SOURCE_NODE).equals(name_child) &&
                                    edgeElement.getAttribute(XmlConstant.ATTRIBUTE_TARGET_NODE).equals(name_parent) &&
                                    edgeElement.getAttribute(XmlConstant.ATTRIBUTE_TYPE).equals("inheritance")){
                                setDirectionsAndIntermediatePoints(fmmlxDiagram, edge, edgeElement);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private NodeList getEdgeList(FmmlxDiagram fmmlxDiagram, Node diagramNode) {
        if(diagramNode.getNodeType()==Node.ELEMENT_NODE) {
            Element diagram = (Element) diagramNode;

            if (diagram.getAttribute(XmlConstant.ATTRIBUTE_LABEL).equals(fmmlxDiagram.getDiagramLabel())) {
                Node edgesNode = xmlHandler.getChildWithName(diagram, XmlConstant.TAG_NAME_EDGES);
                return edgesNode.getChildNodes();
            }
        }
        return null;
    }

    private void setDirectionsAndIntermediatePoints(FmmlxDiagram fmmlxDiagram, Edge edge, Element edgeElement) {
        edge.getSourceNode().setDirectionForEdge(edge.sourceEnd, true, PortRegion.valueOf(edgeElement.getAttribute(XmlConstant.ATTRIBUTE_SOURCE_PORT)));
        edge.getTargetNode().setDirectionForEdge(edge.targetEnd, false, PortRegion.valueOf(edgeElement.getAttribute(XmlConstant.ATTRIBUTE_TARGET_PORT)));
        Node intermediatePointsNode = xmlHandler.getChildWithName(edgeElement, XmlConstant.TAG_NAME_INTERMEDIATE_POINTS);
        NodeList intermediatePointList = intermediatePointsNode.getChildNodes();

        Vector<Point2D> intermediatePoints = new Vector<>();
        for(int k = 0 ; k<intermediatePointList.getLength(); k++){
            if(intermediatePointList.item(k).getNodeType()==Node.ELEMENT_NODE){
                Element intermediatePointElement = (Element) intermediatePointList.item(k);
                double x = Double.parseDouble(intermediatePointElement.getAttribute(XmlConstant.ATTRIBUTE_COORDINATE_X));
                double y = Double.parseDouble(intermediatePointElement.getAttribute(XmlConstant.ATTRIBUTE_COORDINATE_Y));
                Point2D point2D = new Point2D(x, y);
                intermediatePoints.add(point2D);
            }
        }
        edge.setIntermediatePoints(intermediatePoints);
        fmmlxDiagram.getComm().sendCurrentPositions(fmmlxDiagram, edge);
    }

}