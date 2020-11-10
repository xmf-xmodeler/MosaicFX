package tool.clients.serializer;

import java.util.List;
import java.util.Vector;

import javax.xml.transform.TransformerException;

import javafx.geometry.Point2D;
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
    
    public Element createLabelElement(FmmlxDiagram diagram, DiagramEdgeLabel edgeLabel) {
        String text = edgeLabel.getText();
        String owner = edgeLabel.getOwner().getName();

        double x = edgeLabel.getRelativeX();
        double y = edgeLabel.getRelativeY();

        Element label = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_LABEL);
        label.setAttribute(XmlConstant.ATTRIBUTE_TEXT, text);
        label.setAttribute(XmlConstant.ATTRIBUTE_OWNER, owner);
        label.setAttribute(XmlConstant.ATTRIBUTE_DIAGRAM_OWNER, diagram.getDiagramLabel());
        label.setAttribute(XmlConstant.ATTRIBUTE_COORDINATE_X, x+"");
        label.setAttribute(XmlConstant.ATTRIBUTE_COORDINATE_Y, y+"");
        return label;
    }

	@Override
	public void add(Element element) {

        Node diagrams = xmlHandler.getDiagramsNode();
        NodeList diagramNodeList = diagrams.getChildNodes();

        for(int i=0 ; i<diagramNodeList.getLength(); i++){
            if(diagramNodeList.item(i).getNodeType()==Node.ELEMENT_NODE){
                Element diagram = (Element) diagramNodeList.item(i);
                if(diagram.getAttribute(XmlConstant.ATTRIBUTE_LABEL).equals(element.getAttribute(XmlConstant.ATTRIBUTE_DIAGRAM_OWNER))){
                    Element labels = (Element) getLabelsNode(diagram);
                    try {
                        xmlHandler.addLabelElement(labels, element);
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
	public void remove(Element element) {
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
            Point2D initCoordinate = new Point2D(label.getRelativeX(), label.getRelativeY());
            Point2D coordinate = getCoordinate(diagramNode, label.getText(), initCoordinate);
            if(validateName(label.getText())){
            	
                label.setRelativeX(coordinate.getX());
                label.setRelativeY(coordinate.getY());
                label.getOwner().updatePosition(label);
                fmmlxDiagram.getComm().storeLabelInfo(fmmlxDiagram, label);
            }

            label.getOwner().updatePosition(label);
            fmmlxDiagram.getComm().storeLabelInfo(fmmlxDiagram, label);
        }
        fmmlxDiagram.objectsMoved = true;
	}
	
	public boolean validateName(String name) {
		if (name.equals("")) {
			return false;
		} else if (checkFirstStringIsDigit(name)) {
			return false;
		} else return !name.contains(" ");
    }

	private boolean checkFirstStringIsDigit(String name) {
		char[] c = name.toCharArray();
        return Character.isDigit(c[0]);
    }
	
	private Point2D getCoordinate(Node diagramNone, String text, Point2D initCoordinate) {
        Node labelsNode = xmlHandler.getChildWithName(diagramNone, XmlConstant.TAG_NAME_LABELS);
        NodeList labelList = labelsNode.getChildNodes();

        for (int i = 0 ; i< labelList.getLength() ; i++){
            if (labelList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element label_tmp = (Element) labelList.item(i);
                if(validateName(text)) {
                	if(label_tmp.getAttribute(XmlConstant.ATTRIBUTE_TEXT).equals(text)){
                        double x = Double.parseDouble(label_tmp.getAttribute(XmlConstant.ATTRIBUTE_COORDINATE_X));
                        double y = Double.parseDouble(label_tmp.getAttribute(XmlConstant.ATTRIBUTE_COORDINATE_Y));
                        return new Point2D(x, y);
                    }
                }

            }
        }
        return initCoordinate;
    }


}
