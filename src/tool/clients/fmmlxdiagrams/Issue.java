package tool.clients.fmmlxdiagrams;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import org.w3c.dom.Element;
import tool.clients.exporter.svg.SvgConstant;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.graphics.View;
import tool.clients.xmlManipulator.XmlHandler;

import java.util.Vector;

public class Issue implements FmmlxProperty{
	
	public static final Issue NOT_YET_IMPLEMENTED = new Issue("This feature has not been implemented yet.");
	public int issueNumber;

	private Issue() {}
	
	private Issue(String text) {this.text = text;}
	
	private String type;
	private Color color = new Color(1., .8, 0., 1.);
	private String text;
	private Vector<Object> solution;
	private Vector<String> affectedObjects = new Vector<>();

	public void paintToSvg(XmlHandler xmlHandler, Element group, int xOffset, int yOffset, int x, double y) {
		String textColor = this.color.toString().split("x")[1].substring(0,6);
		Element textElement = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_TEXT);
		textElement.setAttribute(SvgConstant.ATTRIBUTE_FONT_FAMILY, "Arial");

		textElement.setAttribute(SvgConstant.ATTRIBUTE_FONT_SIZE, 13+"");
		textElement.setAttribute(SvgConstant.ATTRIBUTE_FONT_OPACITY, 1+"");
		textElement.setAttribute(SvgConstant.ATTRIBUTE_FILL, "#"+textColor);
		textElement.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_X, (x  + xOffset)+"");
		textElement.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_Y, (y + yOffset)+"");
		textElement.setTextContent("issue ["+issueNumber+"] : "+this.text);
		xmlHandler.addXmlElement(group, textElement);
	}

	public void setIssueNumber(int issueNumber) {
		this.issueNumber=issueNumber;
	}

	public static class IssueNotReadableException extends Exception {
		private static final long serialVersionUID = 1L;

		public IssueNotReadableException(String message, Throwable cause) {
			super(message, cause);
		}

		public IssueNotReadableException(String message) {
			super(message);
		}
	}

	@SuppressWarnings("unchecked")
	public static Issue readIssue(Vector<Object> message) throws IssueNotReadableException {
		try {
			Issue i = new Issue();
			i.type = (String) message.get(0);
			i.text = (String) message.get(1);
			Vector<Object> objList = (Vector<Object>) (message.get(2));
			for(Object o : objList) {
				i.affectedObjects.add((String) o);
			}
			i.solution = (Vector<Object>) message.get(3);
						
			return i;
//		} catch (IssueNotReadableException e) {
//			throw e;
		} catch (Exception e) {
			throw new IssueNotReadableException("Could not read message.", e);
		}
	}
	
	public static boolean isAffected(Vector<Issue> issues, FmmlxObject o) {
		for(Issue issue : issues) {
			if(issue.affectedObjects.contains(o.getPath())) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isAffected(FmmlxObject o) {
		return affectedObjects.contains(o.getPath());
	}

	public String getText() {
		return text;
	}
	
	public boolean isSoluble() {
		return !"no solution available".equals(solution.get(0));
	}
	
	public void performResolveAction(AbstractPackageViewer diagram) {
		String actionName = (String) solution.get(0);
		if("setSlotValue".equals(actionName)) {
			FmmlxObject obj = diagram.getObjectByPath((String) solution.get(1));
			String slotName = (String) solution.get(2);
			FmmlxSlot slot = obj.getSlot(slotName);
			diagram.getActions().changeSlotValue(obj, slot);
		} else if("addMissingLink".equals(actionName)) { 
			FmmlxObject obj = diagram.getObjectByPath((String) solution.get(1));
			FmmlxAssociation assoc = diagram.getAssociationByPath((String) solution.get(2));
			diagram.getActions().addMissingLink(obj, assoc);
//			Platform.runLater(()->{
//		        Alert alert = new Alert(AlertType.INFORMATION);
//		        alert.setTitle("Resolving Issue");
//		 
//		        // Header Text: null
//		        alert.setHeaderText(null);
//		        alert.setContentText("The association "+(String) solution.get(2)+" has too few links to object "+ diagram.getObjectById((Integer) solution.get(1)).getName()+". A new link needs to be added.");
//		 
//		        alert.showAndWait();
//			});
		} else if("removeTooManyLinks".equals(actionName)) { 
			FmmlxObject obj = diagram.getObjectByPath((String) solution.get(1));
			FmmlxAssociation assoc = diagram.getAssociationByPath((String) solution.get(2));
			Platform.runLater(()->{
		        Alert alert = new Alert(AlertType.ERROR);
		        alert.setTitle("Too many links");
		 
		        // Header Text: null
		        alert.setHeaderText(null);
		        alert.setContentText("The association "+assoc.getName()+" has too many links to object "+ obj.getName()+". One of them needs to be removed.");
		 
		        alert.showAndWait();
			});	        
		} else if("addRoleFiller".equals(actionName)) {
			FmmlxObject obj = diagram.getObjectByPath((String) solution.get(1));
			diagram.getActions().setRoleFiller(obj, null);
//	    } else { System.err.println("Solution not recognized: " + solution.get(0));
			
		} else { // NOT IN AUTO-MODE
	        Alert alert = new Alert(AlertType.INFORMATION);
	        alert.setTitle("Resolving Issue");
	 
	        // Header Text: null
	        alert.setHeaderText("This issue cannot be resolved!");
	        alert.setContentText(text);
	 
	        alert.showAndWait();
		}
	}
	
	@Override
	public String toString() { 
		return text;
		}

	@Override
	public PropertyType getPropertyType() {
		return PropertyType.Issue;
	}

	@Override
	public String getName() {
		return text;
	}

}
