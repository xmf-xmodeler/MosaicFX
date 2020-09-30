package tool.clients.serializer;

import java.util.List;
import java.util.Vector;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tool.clients.fmmlxdiagrams.DiagramEdgeLabel;
import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.serializer.interfaces.ILog;
import tool.clients.serializer.interfaces.IXmlManager;

public class LabelXmlManager implements ILog, IXmlManager{
	private final XmlHandler xmlHandler;
    
    public LabelXmlManager() {
        this.xmlHandler = new XmlHandler();
    }
    
    public Node createLabel(FmmlxDiagram diagram, DiagramEdgeLabel edgeLabel) {
        String text = edgeLabel.getText();
        String owner = "-1";
        FmmlxAssociation association = diagram.getAssociationById(edgeLabel.getOwner().getId());
        if(association!= null) {
        	owner = association.getName();
        }
        double x = edgeLabel.getRelativeX();
        double y = edgeLabel.getRelativeY();

        Element label = (Element) xmlHandler.createXmlElement(XmlConstant.TAG_NAME_LABEL);
        label.setAttribute(XmlConstant.ATTRIBUTE_TEXT, text);
        label.setAttribute(XmlConstant.ATTRIBUTE_OWNER, owner);
        label.setAttribute(XmlConstant.ATTRIBUTE_DIAGRAM_OWNER, diagram.getDiagramLabel());
        label.setAttribute(XmlConstant.ATTRIBUTE_COORDINATE_X, x+"");
        label.setAttribute(XmlConstant.ATTRIBUTE_COORDINATE_Y, y+"");
        return label;
    }

	@Override
	public void add(Node node) throws TransformerException {
		Element newLabel = (Element) node;

        Node diagrams = xmlHandler.getDiagramsNode();
        NodeList diagramNodeList = diagrams.getChildNodes();

        for(int i=0 ; i<diagramNodeList.getLength(); i++){
            if(diagramNodeList.item(i).getNodeType()==Node.ELEMENT_NODE){
                Element diagram = (Element) diagramNodeList.item(i);
                if(diagram.getAttribute(XmlConstant.ATTRIBUTE_LABEL).equals(newLabel.getAttribute(XmlConstant.ATTRIBUTE_DIAGRAM_OWNER))){
                    Element labels = (Element) getLabelsNode(diagram);
                    try {
                        xmlHandler.addLabelElement(labels, newLabel);
                    } catch (TransformerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
		
	}

	private Node getLabelsNode(Element diagramNode) {
		return xmlHandler.getXmlHelper().getNodeByTag(diagramNode, XmlConstant.TAG_NAME_LABELS);
	}

	@Override
	public void remove(Node node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Node> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void back(int diagramId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void forward(int diagramId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void backToLatestSave(int diagramId, String diagramLabel) {
		// TODO Auto-generated method stub
		
	}

	public void alignLabel(FmmlxDiagram fmmlxDiagram) {
		Node diagrams = xmlHandler.getDiagramsNode();
        NodeList diagramList = diagrams.getChildNodes();

        Node diagramNode = null;

        for (int i = 0 ; i< diagramList.getLength(); i++){
            if(diagramList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element tmp = (Element) diagramList.item(i);
                if (tmp.getAttribute(XmlConstant.ATTRIBUTE_LABEL).equals(fmmlxDiagram.getDiagramLabel())){
                    diagramNode = tmp;
                }
            }
        }

        Vector<DiagramEdgeLabel>labels = fmmlxDiagram.getLabels();
        for(DiagramEdgeLabel label : labels){
            Coordinate initCoordingate = new Coordinate(label.getRelativeX(), label.getRelativeY());
            Coordinate coordinate = getCoordinate(diagramNode, label.getText(), initCoordingate);
            label.moveTo(coordinate.getX(), coordinate.getY(), fmmlxDiagram);
        }
        fmmlxDiagram.objectsMoved = true;
		
	}
	
	private Coordinate getCoordinate(Node diagramNone, String text, Coordinate initCoordingate) {
        Node labelsNode = xmlHandler.getChildWithName(diagramNone, XmlConstant.TAG_NAME_LABELS);
        NodeList labelList = labelsNode.getChildNodes();
        Coordinate coordinate = initCoordingate;

        for (int i = 0 ; i< labelList.getLength() ; i++){
            if (labelList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element object_tmp = (Element) labelList.item(i);
                if(object_tmp.getAttribute(XmlConstant.ATTRIBUTE_TEXT).equals(text)){
                    double x = Double.parseDouble(object_tmp.getAttribute(XmlConstant.ATTRIBUTE_COORDINATE_X));
                    double y = Double.parseDouble(object_tmp.getAttribute(XmlConstant.ATTRIBUTE_COORDINATE_Y));
                    coordinate.setX(x);
                    coordinate.setY(y);
                }
            }
        }
        return coordinate;
    }
	
	private class Coordinate {
        double x;
        double y;

        public Coordinate(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        @Override
        public String toString() {
            return "Coordinat{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

}
