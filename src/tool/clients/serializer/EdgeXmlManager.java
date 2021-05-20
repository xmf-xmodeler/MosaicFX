package tool.clients.serializer;

import javafx.geometry.Point2D;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tool.clients.fmmlxdiagrams.*;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class EdgeXmlManager {

    private final XmlHandler xmlHandler;

    protected EdgeXmlManager(XmlHandler xmlHandler) {
        this.xmlHandler = xmlHandler;
    }

    public Element createEdgeXmlElement(Vector<Object> intermediatePoints, String ref, Vector<Object>ports){
        PortRegion sourcePort = getSourcePort(ports);
        PortRegion targetPort = getTargetPort(ports);

        Element edgeElement = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_EDGE);
        edgeElement.setAttribute(XmlConstant.ATTRIBUTE_SOURCE_PORT, sourcePort+"");
        edgeElement.setAttribute(XmlConstant.ATTRIBUTE_TARGET_PORT, targetPort+"");
        edgeElement.setAttribute(XmlConstant.ATTRIBUTE_REFERENCE, ref);

        Element intermediatePointsNode = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_INTERMEDIATE_POINTS);
        for(Object intermediatePointObject : intermediatePoints){
            Vector<Object> points = (Vector<Object>) intermediatePointObject;
            Element intermediatePoint = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_INTERMEDIATE_POINT);
            intermediatePoint.setAttribute(XmlConstant.ATTRIBUTE_COORDINATE_X, points.get(1)+"");
            intermediatePoint.setAttribute(XmlConstant.ATTRIBUTE_COORDINATE_Y, points.get(2)+"");
            intermediatePointsNode.appendChild(intermediatePoint);
        }
        xmlHandler.addXmlElement(edgeElement, intermediatePointsNode);
        return edgeElement;
    }

    public Element createAssociationXmlElement(String name, String diagramPath, HashMap<String, Object> edgeInfo) {
        String ref = diagramPath+"::"+name;
        String type = XmlConstant.EdgeType.ASSOCIATION;

        Element edge = createEdgeXmlElement((Vector<Object>) edgeInfo.get("IntermediatePoints"), ref, (Vector<Object>) edgeInfo.get("Ports"));
        edge.setAttribute(XmlConstant.ATTRIBUTE_TYPE, type);
        edge.setAttribute(XmlConstant.ATTRIBUTE_NAME, name);
        edge.setAttribute(XmlConstant.ATTRIBUTE_PARENT_ASSOCIATION, "VOID");
        return edge ;
    }

    public Element createLinkXmlElement(String ref, HashMap<String, Object> edgeInfo) {
        String type = XmlConstant.EdgeType.LINK;

        Element edge = createEdgeXmlElement((Vector<Object>) edgeInfo.get("IntermediatePoints"), ref, (Vector<Object>) edgeInfo.get("Ports"));
        edge.setAttribute(XmlConstant.ATTRIBUTE_TYPE, type);
        return edge;
    }

    public Element createInheritanceXmlElement(String ref, HashMap<String, Object> edgeInfo) {
        String type = XmlConstant.EdgeType.INHERITANCE;

        Element edge = createEdgeXmlElement((Vector<Object>) edgeInfo.get("IntermediatePoints"), ref, (Vector<Object>) edgeInfo.get("Ports"));
        edge.setAttribute(XmlConstant.ATTRIBUTE_TYPE, type);
        return edge;
    }

    public Element createRoleFillerEdgeXmlElement(String ref, HashMap<String, Object> edgeInfo) {
        String type = XmlConstant.EdgeType.ROLEFILLEREDGE;

        Element edge = createEdgeXmlElement((Vector<Object>) edgeInfo.get("IntermediatePoints"), ref, (Vector<Object>) edgeInfo.get("Ports"));
        edge.setAttribute(XmlConstant.ATTRIBUTE_TYPE, type);
        return edge;
    }

    public Element createDelegationXmlElement(String ref, HashMap<String, Object> edgeInfo) {
        String type = XmlConstant.EdgeType.DELEGATION;

        Element edge = createEdgeXmlElement((Vector<Object>) edgeInfo.get("IntermediatePoints"), ref, (Vector<Object>) edgeInfo.get("Ports"));
        edge.setAttribute(XmlConstant.ATTRIBUTE_TYPE, type);
        return edge;
    }

    public void add(Element diagramElement, Element newElement) {
        if(newElement!=null){
            Element edges = getEdgesElement(diagramElement);
            xmlHandler.addXmlElement(edges, newElement);
        }
    }

    public void remove(Element element) {
        //TODO
    }

    public List<Node> getAll() {
        return null;
    }

    private Element getEdgesElement(Element diagramNode){
        return xmlHandler.getChildWithTag(diagramNode, XmlConstant.TAG_NAME_EDGES);
    }

    /**
     * @deprecated replaced by {@link #alignEdges(Element, int, FmmlxDiagramCommunicator)}
     */
    public void alignEdges2(Element diagramElement, FmmlxDiagram fmmlxDiagram){
        Vector<Edge<?>> edges = fmmlxDiagram.getEdges();

        for(Edge<?> edge : edges){
            handleEdge(fmmlxDiagram, diagramElement, edge);
        }
    }

    @Deprecated
    private void handleEdge(FmmlxDiagram fmmlxDiagram, Element diagramElement, Edge<?> edge) {
            Node edges = xmlHandler.getChildWithTag(diagramElement, XmlConstant.TAG_NAME_EDGES);
            NodeList edgeList = edges.getChildNodes();
            if(edgeList!=null){
                for (int j = 0 ; j< edgeList.getLength(); j++) {
                    if(edgeList.item(j).getNodeType()==Node.ELEMENT_NODE) {
                        Element edgeElement = (Element) edgeList.item(j);

                        if (edge instanceof FmmlxAssociation) {
                            String name = edge.getName();
                            if(edgeElement.getAttribute(XmlConstant.ATTRIBUTE_NAME).equals(name) &&
                                    edgeElement.getAttribute(XmlConstant.ATTRIBUTE_TYPE).equals(XmlConstant.EdgeType.ASSOCIATION)){
                                setDirectionsAndIntermediatePoints(fmmlxDiagram, edge, edgeElement);
                                break;
                            }
                        } else if (edge instanceof DelegationEdge){
                            if(edgeElement.getAttribute(XmlConstant.ATTRIBUTE_REFERENCE).equals(edge.getPath()) &&
                                    edgeElement.getAttribute(XmlConstant.ATTRIBUTE_TYPE).equals(XmlConstant.EdgeType.DELEGATION)){
                                setDirectionsAndIntermediatePoints(fmmlxDiagram, edge, edgeElement);
                                break;
                            }
                        } else if (edge instanceof FmmlxLink) {
                            if(edgeElement.getAttribute(XmlConstant.ATTRIBUTE_REFERENCE).equals(edge.getPath()) &&
                                    edgeElement.getAttribute(XmlConstant.ATTRIBUTE_TYPE).equals(XmlConstant.EdgeType.LINK)){
                                setDirectionsAndIntermediatePoints(fmmlxDiagram, edge, edgeElement);
                                break;
                            }
                        } else if (edge instanceof InheritanceEdge) {
                            if(edgeElement.getAttribute(XmlConstant.ATTRIBUTE_REFERENCE).equals(edge.getPath()) &&
                                    edgeElement.getAttribute(XmlConstant.ATTRIBUTE_TYPE).equals(XmlConstant.EdgeType.INHERITANCE)){
                                setDirectionsAndIntermediatePoints(fmmlxDiagram, edge, edgeElement);
                                break;
                            }
                        } else if (edge instanceof RoleFillerEdge) {
                            if(edgeElement.getAttribute(XmlConstant.ATTRIBUTE_REFERENCE).equals(edge.getPath()) &&
                                    edgeElement.getAttribute(XmlConstant.ATTRIBUTE_TYPE).equals(XmlConstant.EdgeType.ROLEFILLEREDGE)){
                                setDirectionsAndIntermediatePoints(fmmlxDiagram, edge, edgeElement);
                            }
                        }
                    }
                }
            }
    }

    @Deprecated
    private void setDirectionsAndIntermediatePoints(FmmlxDiagram fmmlxDiagram, Edge edge, Element edgeElement) {
        edge.getSourceNode().setDirectionForEdge(edge.sourceEnd, true,
                PortRegion.valueOf(edgeElement.getAttribute(XmlConstant.ATTRIBUTE_SOURCE_PORT)));
        edge.getTargetNode().setDirectionForEdge(edge.targetEnd, false,
                PortRegion.valueOf(edgeElement.getAttribute(XmlConstant.ATTRIBUTE_TARGET_PORT)));
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
        edge.setIntermediatePoints(intermediatePoints);
        fmmlxDiagram.getComm().sendCurrentPositions(fmmlxDiagram.getID(), edge);
    }


    public void alignEdges(Element diagramElement, int diagramID, FmmlxDiagramCommunicator communicator) {

        Element edgesNode = getEdgesElement(diagramElement);
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
                if(intermediatePointList.getLength()>0 || !sourcePort.equals("null") || !targetPort.equals("null")) {
                    communicator.sendEdgePositionsFromXml(diagramID, edgePath, intermediatePoints, sourcePort, targetPort);
                }
            }
        }
    }


    private PortRegion getTargetPort(Vector<Object> ports) {
        if(ports!=null && ports.size()>0) {
            for (Object port : ports) {
                Vector<Object> portInfo = (Vector<Object>) port;
                if (((String) portInfo.get(0)).trim().equals("endNode")) {
                    return PortRegion.valueOf(((String) portInfo.get(1)).trim());
                }
            }
        }
        return null;
    }

    private PortRegion getSourcePort(Vector<Object> ports) {
        if(ports!=null && ports.size()>0){
            for (Object port : ports){
                Vector<Object> portInfo = (Vector<Object>) port;
                if(((String)portInfo.get(0)).trim().equals("startNode")){
                    return PortRegion.valueOf(((String) portInfo.get(1)).trim());
                }
            }
        }
        return null;
    }


}