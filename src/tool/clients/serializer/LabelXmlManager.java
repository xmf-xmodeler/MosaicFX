package tool.clients.serializer;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javafx.geometry.Point2D;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tool.clients.fmmlxdiagrams.DiagramEdgeLabel;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.xmlManipulator.XmlHandler;

public class LabelXmlManager {
	private final XmlHandler xmlHandler;

    protected LabelXmlManager(XmlHandler xmlHandler) {
        this.xmlHandler = xmlHandler;
    }

    public Element createLabelElement(String key, float x, float y) {
        Element label = xmlHandler.createXmlElement(SerializerConstant.TAG_NAME_LABEL);
        String[] refSplit = key.split("::");
        label.setAttribute(SerializerConstant.ATTRIBUTE_TEXT, refSplit[refSplit.length-1]);
        label.setAttribute(SerializerConstant.ATTRIBUTE_COORDINATE_X, x+"");
        label.setAttribute(SerializerConstant.ATTRIBUTE_COORDINATE_Y, y+"");
        return label;
    }

	public void add(Element diagramElement, Element newElement) {
        Element labels = getLabelsElement(diagramElement);
        xmlHandler.addXmlElement(labels, newElement);

	}

	public void remove(Element element) {
		// TODO Auto-generated method stub
		
	}

	public List<Node> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	public void back(int diagramId) {
		// TODO Auto-generated method stub
		
	}

	public void forward(int diagramId) {
		// TODO Auto-generated method stub
		
	}

	public void backToLatestSave(int diagramId, String diagramLabel) {
		// TODO Auto-generated method stub
		
	}

    public Element getDiagramsElement(){
        Element Root = xmlHandler.getRoot();
        return xmlHandler.getChildWithTag(Root, SerializerConstant.TAG_NAME_DIAGRAMS);
    }

    private Element getLabelsElement(Element diagramNode) {
        return xmlHandler.getChildWithTag(diagramNode, SerializerConstant.TAG_NAME_LABELS);
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

    @Deprecated
	private Point2D getCoordinate(Element diagramElement, DiagramEdgeLabel label, Point2D initCoordinate) {
        Element labelsNode = getLabelsElement(diagramElement);
        String text = label.getText();
        Vector<FmmlxObject> anchors = label.getAnchors();
        NodeList labelList = labelsNode.getChildNodes();

        for (int i = 0 ; i< labelList.getLength() ; i++){
            if (labelList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element label_tmp = (Element) labelList.item(i);
                String[] anchorsString = label_tmp.getAttribute(SerializerConstant.ATTRIBUTE_ANCHORS).split(",");
                if(validateName(text) && validateEdgeLabel(anchorsString, anchors)) {
                	if(label_tmp.getAttribute(SerializerConstant.ATTRIBUTE_TEXT).equals(text)){
                        double x = Double.parseDouble(label_tmp.getAttribute(SerializerConstant.ATTRIBUTE_COORDINATE_X));
                        double y = Double.parseDouble(label_tmp.getAttribute(SerializerConstant.ATTRIBUTE_COORDINATE_Y));
                        return new Point2D(x, y);
                    }
                }

            }
        }
        return initCoordinate;
    }

    private boolean validateEdgeLabel(String[] anchorsString, Vector<FmmlxObject> anchors) {
        List<String> anchorsList = Arrays.asList(anchorsString);

        for(FmmlxObject anchor : anchors){
            if(!anchorsList.contains(anchor.getName().trim())){
                return false;
            }
        }
        return true;
    }

    @Deprecated
    public void alignLabel2(Element diagramElement, FmmlxDiagramCommunicator communicator, int diagramID) {
        Node labelsNode = getLabelsElement(diagramElement);
        NodeList labelList = labelsNode.getChildNodes();

        for (int i = 0 ; i< labelList.getLength() ; i++){
            if (labelList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element label_tmp = (Element) labelList.item(i);

                double x = Double.parseDouble(label_tmp.getAttribute(SerializerConstant.ATTRIBUTE_COORDINATE_X));
                double y = Double.parseDouble(label_tmp.getAttribute(SerializerConstant.ATTRIBUTE_COORDINATE_Y));

                communicator.storeLabelInfoFromXml(diagramID, x, y);
            }
        }
    }

    public void alignLabel(Element diagramElement, FmmlxDiagram fmmlxDiagram) {
        Vector<DiagramEdgeLabel<?>>labels = fmmlxDiagram.getLabels();
        for(DiagramEdgeLabel<?> label : labels){
            Point2D initCoordinate = new Point2D(label.getRelativeX(), label.getRelativeY());
            Point2D coordinate = getCoordinate(diagramElement, label.getText(), initCoordinate);
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

    private Point2D getCoordinate(Element diagramElement, String text, Point2D initCoordinate) {
        Element labelsElement = getLabelsElement(diagramElement);
        NodeList labelList = labelsElement.getChildNodes();

        for (int i = 0 ; i< labelList.getLength() ; i++){
            if (labelList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element label_tmp = (Element) labelList.item(i);
                if(validateName(text)) {
                    if(label_tmp.getAttribute(SerializerConstant.ATTRIBUTE_TEXT).equals(text)){
                        double x = Double.parseDouble(label_tmp.getAttribute(SerializerConstant.ATTRIBUTE_COORDINATE_X));
                        double y = Double.parseDouble(label_tmp.getAttribute(SerializerConstant.ATTRIBUTE_COORDINATE_Y));
                        return new Point2D(x, y);
                    }
                }

            }
        }
        return initCoordinate;
    }
}
