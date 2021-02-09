package tool.clients.serializer;

import javafx.geometry.Point2D;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tool.clients.fmmlxdiagrams.*;
import tool.clients.serializer.interfaces.XmlManager;

import java.util.List;
import java.util.Vector;

public class EdgeXmlManager implements XmlManager {

    private final XmlHandler xmlHandler;

    protected EdgeXmlManager(XmlHandler xmlHandler) {
        this.xmlHandler = xmlHandler;
    }

    public Element createEdgeXmlElement(FmmlxDiagram fmmlxDiagram, Edge edge) {
        Vector<Point2D> intermediatePoints = edge.getIntermediatePoints();
        FmmlxObject sourceNode = edge.getSourceNode();
        FmmlxObject targetNode = edge.getTargetNode();
        PortRegion sourcePort = edge.getSourcePortRegion();
        PortRegion targetPort = edge.getTargetPortRegion();
        String projectPath = fmmlxDiagram.getPackagePath();
        String owner = fmmlxDiagram.getDiagramLabel();
        String reference =edge.getPath();
        Element edgeElement = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_EDGE);
        edgeElement.setAttribute(XmlConstant.ATTRIBUTE_OWNER, owner);
        edgeElement.setAttribute(XmlConstant.ATTRIBUTE_REFERENCE, projectPath);
        edgeElement.setAttribute(XmlConstant.ATTRIBUTE_SOURCE_NODE, sourceNode.getName()+"");
        edgeElement.setAttribute(XmlConstant.ATTRIBUTE_TARGET_NODE, targetNode.getName()+"");
        edgeElement.setAttribute(XmlConstant.ATTRIBUTE_SOURCE_PORT, sourcePort+"");
        edgeElement.setAttribute(XmlConstant.ATTRIBUTE_TARGET_PORT, targetPort+"");
        edgeElement.setAttribute(XmlConstant.ATTRIBUTE_REFERENCE, reference);
        Element intermediatePointsNode = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_INTERMEDIATE_POINTS);
        for(Point2D point2D : intermediatePoints){
            Element intermediatePoint = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_INTERMEDIATE_POINT);
            intermediatePoint.setAttribute(XmlConstant.ATTRIBUTE_COORDINATE_X, point2D.getX()+"");
            intermediatePoint.setAttribute(XmlConstant.ATTRIBUTE_COORDINATE_Y, point2D.getY()+"");
            intermediatePointsNode.appendChild(intermediatePoint);
        }
        xmlHandler.addXmlElement(edgeElement, intermediatePointsNode);

        return edgeElement;
    }

    public Element createAssociationXmlElement(FmmlxDiagram fmmlxDiagram, FmmlxAssociation fmmlxAssociation)  {
        String name = fmmlxAssociation.getName();
        String type = XmlConstant.EdgeType.ASSOCIATION;
//        String parentAssociationName = fmmlxAssociation.getParentAssociationName();
        int levelTarget = fmmlxAssociation.getLevelTarget();
        int levelSource = fmmlxAssociation.getLevelSource();
        Multiplicity multiplicityStartToEnd = fmmlxAssociation.getMultiplicityStartToEnd();
        Multiplicity multiplicityEndToStart = fmmlxAssociation.getMultiplicityEndToStart();

        Element edge = createEdgeXmlElement(fmmlxDiagram, fmmlxAssociation);
        edge.setAttribute(XmlConstant.ATTRIBUTE_TYPE, type);
        edge.setAttribute(XmlConstant.ATTRIBUTE_NAME, name);
        edge.setAttribute(XmlConstant.ATTRIBUTE_PARENT_ASSOCIATION, "VOID");
        edge.setAttribute(XmlConstant.ATTRIBUTE_LEVEL_TARGET, levelTarget+"");
        edge.setAttribute(XmlConstant.ATTRIBUTE_LEVEL_SOURCE, levelSource+"");
        edge.setAttribute(XmlConstant.ATTRIBUTE_MULTIPLICITY_START_TO_END, multiplicityStartToEnd.toString());
        edge.setAttribute(XmlConstant.ATTRIBUTE_MULTIPLICITY_END_TO_START, multiplicityEndToStart.toString());
        return edge;
    }

    public Element createDelegationXmlElement(FmmlxDiagram fmmlxDiagram, DelegationEdge delegationEdge) {
        String type = XmlConstant.EdgeType.DELEGATION;

        Element edge = createEdgeXmlElement(fmmlxDiagram, delegationEdge);
        edge.setAttribute(XmlConstant.ATTRIBUTE_TYPE, type);
        return edge;
    }

    public Element createRoleFillerEdgeXmlElement(FmmlxDiagram diagram, RoleFillerEdge roleFillerEdge) {
        String type = XmlConstant.EdgeType.ROLEFILLEREDGE;

        Element edge = createEdgeXmlElement(diagram, roleFillerEdge);
        edge.setAttribute(XmlConstant.ATTRIBUTE_TYPE, type);
        return edge;
    }


    public Element createInheritanceXmlElement(FmmlxDiagram fmmlxDiagram, InheritanceEdge inheritanceEdge)  {
        String type = XmlConstant.EdgeType.INHERITANCE;

        Element edge = createEdgeXmlElement(fmmlxDiagram, inheritanceEdge);
        edge.setAttribute(XmlConstant.ATTRIBUTE_TYPE, type);
        return edge;
    }

    public Element createLinkXmlElement(FmmlxDiagram fmmlxDiagram, FmmlxLink fmmlxLink)  {

        String type = XmlConstant.EdgeType.LINK;
        String ofName = fmmlxLink.getOfName();

        Element edge = createEdgeXmlElement(fmmlxDiagram, fmmlxLink);
        edge.setAttribute(XmlConstant.ATTRIBUTE_TYPE, type);
        edge.setAttribute(XmlConstant.ATTRIBUTE_OF, ofName+"");
        return edge;
    }



    @Override
    public void add(Element diagramElement, Element newElement) {
        if(newElement!=null){
            Element edges = (Element) getEdgesElement(diagramElement);
            xmlHandler.addXmlElement(edges, newElement);
        }
    }

    @Override
    public void remove(Element element) {
        //TODO
    }

    @Override
    public List<Node> getAll() {
        return null;
    }

    public Element getDiagramsElement(){
        Element Root = xmlHandler.getRoot();
        return xmlHandler.getChildWithTag(Root, XmlConstant.TAG_NAME_DIAGRAMS);
    }

    private Element getEdgesElement(Element diagramNode){
        return xmlHandler.getChildWithTag(diagramNode, XmlConstant.TAG_NAME_EDGES);
    }

//    public void alignEdges(Element diagramElement, FmmlxDiagram fmmlxDiagram){
//        Vector<Edge<?>> edges = fmmlxDiagram.getEdges();
//
//        for(Edge<?> edge : edges){
//            handleEdge(fmmlxDiagram, diagramElement, edge);
//        }
//    }

//    private void handleEdge(FmmlxDiagram fmmlxDiagram, Element diagramElement, Edge<?> edge) {
//            Node edges = xmlHandler.getChildWithTag(diagramElement, XmlConstant.TAG_NAME_EDGES);
//            NodeList edgeList = edges.getChildNodes();
//            if(edgeList!=null){
//                for (int j = 0 ; j< edgeList.getLength(); j++) {
//                    if(edgeList.item(j).getNodeType()==Node.ELEMENT_NODE) {
//                        Element edgeElement = (Element) edgeList.item(j);
//                        String name_parent = edge.getTargetNode().getName();
//                        String name_child = edge.getSourceNode().getName();
//
//                        if (edge instanceof FmmlxAssociation) {
//                            String name = edge.getName();
//                            if(edgeElement.getAttribute(XmlConstant.ATTRIBUTE_NAME).equals(name) &&
//                                    edgeElement.getAttribute(XmlConstant.ATTRIBUTE_TYPE).equals(XmlConstant.EdgeType.ASSOCIATION)){
//                                setDirectionsAndIntermediatePoints(fmmlxDiagram, edge, edgeElement);
//                                break;
//                            }
//                        } else if (edge instanceof DelegationEdge){
//                            if(edgeElement.getAttribute(XmlConstant.ATTRIBUTE_SOURCE_NODE).equals(name_child) &&
//                                    edgeElement.getAttribute(XmlConstant.ATTRIBUTE_TARGET_NODE).equals(name_parent) &&
//                                    edgeElement.getAttribute(XmlConstant.ATTRIBUTE_TYPE).equals(XmlConstant.EdgeType.DELEGATION)){
//                                setDirectionsAndIntermediatePoints(fmmlxDiagram, edge, edgeElement);
//                                break;
//                            }
//                        } else if (edge instanceof FmmlxLink) {
//                            String ofName = ((FmmlxLink) edge).getOfName();
//                            if(edgeElement.getAttribute(XmlConstant.ATTRIBUTE_SOURCE_NODE).equals(name_child) &&
//                                    edgeElement.getAttribute(XmlConstant.ATTRIBUTE_OF).equals(ofName) &&
//                                    edgeElement.getAttribute(XmlConstant.ATTRIBUTE_TARGET_NODE).equals(name_parent) &&
//                                    edgeElement.getAttribute(XmlConstant.ATTRIBUTE_TYPE).equals(XmlConstant.EdgeType.LINK)){
//                                setDirectionsAndIntermediatePoints(fmmlxDiagram, edge, edgeElement);
//                                break;
//                            }
//                        } else if (edge instanceof InheritanceEdge) {
//                            if(edgeElement.getAttribute(XmlConstant.ATTRIBUTE_SOURCE_NODE).equals(name_child) &&
//                                    edgeElement.getAttribute(XmlConstant.ATTRIBUTE_TARGET_NODE).equals(name_parent) &&
//                                    edgeElement.getAttribute(XmlConstant.ATTRIBUTE_TYPE).equals(XmlConstant.EdgeType.INHERITANCE)){
//                                setDirectionsAndIntermediatePoints(fmmlxDiagram, edge, edgeElement);
//                                break;
//                            }
//                        } else if (edge instanceof RoleFillerEdge) {
//                            if(edgeElement.getAttribute(XmlConstant.ATTRIBUTE_SOURCE_NODE).equals(name_child) &&
//                                    edgeElement.getAttribute(XmlConstant.ATTRIBUTE_TARGET_NODE).equals(name_parent) &&
//                                    edgeElement.getAttribute(XmlConstant.ATTRIBUTE_TYPE).equals(XmlConstant.EdgeType.ROLEFILLEREDGE)){
//                                setDirectionsAndIntermediatePoints(fmmlxDiagram, edge, edgeElement);
//                            }
//                        }
//                    }
//                }
//            }
//    }

//    private void setDirectionsAndIntermediatePoints(FmmlxDiagram fmmlxDiagram, Edge edge, Element edgeElement) {
//        edge.getSourceNode().setDirectionForEdge(edge.sourceEnd, true,
//                PortRegion.valueOf(edgeElement.getAttribute(XmlConstant.ATTRIBUTE_SOURCE_PORT)));
//        edge.getTargetNode().setDirectionForEdge(edge.targetEnd, false,
//                PortRegion.valueOf(edgeElement.getAttribute(XmlConstant.ATTRIBUTE_TARGET_PORT)));
//        Node intermediatePointsNode = xmlHandler.getChildWithTag(edgeElement, XmlConstant.TAG_NAME_INTERMEDIATE_POINTS);
//        NodeList intermediatePointList = intermediatePointsNode.getChildNodes();
//
//        Vector<Point2D> intermediatePoints = new Vector<>();
//        for(int k = 0 ; k<intermediatePointList.getLength(); k++){
//            if(intermediatePointList.item(k).getNodeType()==Node.ELEMENT_NODE){
//                Element intermediatePointElement = (Element) intermediatePointList.item(k);
//                double x = Double.parseDouble(intermediatePointElement.getAttribute(XmlConstant.ATTRIBUTE_COORDINATE_X));
//                double y = Double.parseDouble(intermediatePointElement.getAttribute(XmlConstant.ATTRIBUTE_COORDINATE_Y));
//                Point2D point2D = new Point2D(x, y);
//                intermediatePoints.add(point2D);
//            }
//        }
//        edge.setIntermediatePoints(intermediatePoints);
//        fmmlxDiagram.getComm().sendCurrentPositions(fmmlxDiagram.getID(), edge);
//    }


    public void alignEdges(String diagramName, FmmlxDiagramCommunicator communicator) {
        Element diagrams = getDiagramsElement();
        NodeList diagramNodeList = diagrams.getChildNodes();
        int diagramId = FmmlxDiagramCommunicator.getDiagramIdFromName(diagramName);
        Element diagramElement = null;

        for (int i = 0 ; i< diagramNodeList.getLength(); i++){
            if(diagramNodeList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element tmp = (Element) diagramNodeList.item(i);
                if (tmp.getAttribute(XmlConstant.ATTRIBUTE_LABEL).equals(diagramName)){
                    diagramElement = tmp;
                }
            }
        }

        Element edgesNode = xmlHandler.getChildWithTag(diagramElement, XmlConstant.TAG_NAME_EDGES);
        NodeList edgeList = edgesNode.getChildNodes();

        for(int i = 0 ; i < edgeList.getLength(); i++){
            if(edgeList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element edgeElement = (Element) edgeList.item(i);
                String edgePath = edgeElement.getAttribute(XmlConstant.ATTRIBUTE_REFERENCE);
                String sourcePort = edgeElement.getAttribute(XmlConstant.ATTRIBUTE_SOURCE_PORT);
                String targetPort = edgeElement.getAttribute(XmlConstant.ATTRIBUTE_TARGET_PORT);
                Node intermediatePointsNode = xmlHandler.getChildWithTag(edgeElement, XmlConstant.TAG_NAME_INTERMEDIATE_POINTS);
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
                communicator.sendEdgePositionsFromXml(diagramId,
                        edgePath,
                        intermediatePoints,
                        sourcePort,
                        targetPort);
            }
        }
        System.out.println("align edges in "+diagramName+" : finished ");
    }
}