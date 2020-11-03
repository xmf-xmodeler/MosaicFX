package tool.clients.fmmlxdiagrams;

import java.util.Vector;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Issue {
	
	public static final Issue NOT_YET_IMPLEMENTED = new Issue("This feature has not been implemented yet.");

	private Issue() {}
	
	private Issue(String text) {this.text = text;}
	
	private String type;
	private String text;
	private Vector<Object> solution;
	private Vector<Integer> affectedObjects = new Vector<>();

	public static class IssueNotReadableException extends Exception {
		private static final long serialVersionUID = 1L;

		public IssueNotReadableException(String message, Throwable cause) {
			super(message, cause);
		}

		public IssueNotReadableException(String message) {
			super(message);
		}
	}

	public static Issue readIssue(Vector<Object> message) throws IssueNotReadableException {
		try {
			Issue i = new Issue();
			i.type = (String) message.get(0);
			i.text = (String) message.get(1);
			Vector<Object> objList = (Vector<Object>) (message.get(2));
			for(Object o : objList) {
				i.affectedObjects.add((Integer) o);
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
			if(issue.affectedObjects.contains(o.id)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isAffected(FmmlxObject o) {
		return affectedObjects.contains(o.id); 
	}

	public String getText() {
		return text;
	}
	
	public void performResolveAction(FmmlxDiagram diagram) {
		String actionName = (String) solution.get(0);
		if("setSlotValue".equals(actionName)) {
			FmmlxObject obj = diagram.getObjectById((Integer) solution.get(1));
			String slotName = (String) solution.get(2);
			FmmlxSlot slot = obj.getSlot(slotName);
			diagram.getActions().changeSlotValue(obj, slot);
		} else if("addMissingLink".equals(actionName)) { 
			FmmlxObject obj = diagram.getObjectById((Integer) solution.get(1));
			FmmlxAssociation assoc = diagram.getAssociationById((Integer) solution.get(2));
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
			FmmlxObject obj = diagram.getObjectById((Integer) solution.get(1));
			FmmlxAssociation assoc = diagram.getAssociationById((Integer) solution.get(2));
			Platform.runLater(()->{
		        Alert alert = new Alert(AlertType.ERROR);
		        alert.setTitle("Too many links");
		 
		        // Header Text: null
		        alert.setHeaderText(null);
		        alert.setContentText("The association "+assoc.getName()+" has too many links to object "+ obj.getName()+". One of them needs to be removed.");
		 
		        alert.showAndWait();
			});	        
		} else if("addRoleFiller".equals(actionName)) { 
			FmmlxObject obj = diagram.getObjectById((Integer) solution.get(1));
			FmmlxObject roleFillerOf = diagram.getObjectById((Integer) solution.get(2));
			Platform.runLater(()->{
		        Alert alert = new Alert(AlertType.ERROR);
		        alert.setTitle("Role filler required");
		 
		        // Header Text: null
		        alert.setHeaderText(null);
		        alert.setContentText("The object "+obj.getName()+" requires a role filler of type "+ roleFillerOf.getName()+". Use Delegation -> Set Rolefiller.");
		 
		        alert.showAndWait();
			});	
	    } else { System.err.println("Solution not recognized: " + solution.get(0));
			
//		} else { // NOT IN AUTO-MODE
//	        Alert alert = new Alert(AlertType.INFORMATION);
//	        alert.setTitle("Resolving Issue");
//	 
//	        // Header Text: null
//	        alert.setHeaderText(null);
//	        alert.setContentText("This issue cannot be resolved!");
//	 
//	        alert.showAndWait();
		}
	}

}
