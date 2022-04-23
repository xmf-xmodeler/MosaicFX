package tool.clients.fmmlxdiagrams.classbrowser;

import java.lang.instrument.IllegalClassFormatException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxLink;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;
import tool.clients.fmmlxdiagrams.FmmlxOperationValue;
import tool.clients.fmmlxdiagrams.FmmlxSlot;

public class ControllerLanguageInterpreter2 {
	private AbstractPackageViewer underlyingDiagram;
	private String packageName;
	
	private String nameOfReference = "";
	private Boolean isMain = false; // evtl. obsolet wg. MainListView?
	private String classComponent = ""; // Names of Operations or Attributes
	private Boolean isAction = false;
	private Boolean isActionInjection = false;
	private Boolean isInjection = false;
	private Boolean isInstanceFiller = false;
	
	private ListView<String> mainListView;
	
	private String crefName;
	
	private HashMap<AssociationInstanceMapping, List<String>> linkHierarchy;
	private HashMap<String, ListView<String>> listViewsToAssociations;
	
	// memory
	private ArrayList<String> visitedAssocs = new ArrayList<>();
	
	public ControllerLanguageInterpreter2( String idOfElement, AbstractPackageViewer diagram, ListView<String> mainListView, HashMap<AssociationInstanceMapping, List<String>> linksPerAssociationPerInstance, HashMap<String, ListView<String>> listViewsToAssociations) {
		// determine operation
		int opACTINJ = idOfElement.indexOf("ACTINJ");
		int opACT = idOfElement.indexOf("ACT");
		int opINJ = idOfElement.indexOf("INJ");
		
		isActionInjection = opACTINJ == -1 ? false : true;
		
		if( ! isActionInjection ) {
			isAction = opACT == -1 ? false : true;
			isInjection = opINJ == -1 ? false : true;
		}
		
		int firstSplit = isActionInjection ? opACTINJ : isAction ? opACT : opINJ;
		isInstanceFiller = firstSplit == -1 ? true : false;
		
		// determine parameters / references for operations
		if( isInstanceFiller ) {
			// analyze left part
			isMain = idOfElement.contains("CREF") ? true : false;
			nameOfReference = idOfElement.split("REF")[1];
		} else {
			String leftPart = idOfElement.substring(0, firstSplit);
			String rightPart = idOfElement.substring(firstSplit, idOfElement.length());
			
			// analyze left part
			isMain = leftPart.contains("CREF") ? true : false;
			nameOfReference = leftPart.split("REF")[1];
			
			// determine class component
			classComponent = rightPart.contains("INJ") ? rightPart.split("INJ")[1] : rightPart.split("ACT")[1];	
		}
		
		// TBD: Exception if all four possible operations are false
		// TBD: Determine if namesofReference and classComponents do exist in the model
		
		// Handle diagram
		underlyingDiagram = diagram;
		packageName = diagram.getPackagePath();
		
		// Relevant data
		this.mainListView = mainListView;
		
		crefName = mainListView.getId().split("CREF")[1];
		
		linkHierarchy = linksPerAssociationPerInstance;
		this.listViewsToAssociations = listViewsToAssociations;
	}
	
	public boolean isResultList() {
		return isInstanceFiller;
	}
	
	private ListView<String> determineReference() {
		// TBD: Das funktioniert niemals..
		// 1) Klasse ist im Gegensatz zur Assoziation nicht eindeutig
		// 2) Das Vorwärtslaufen klappt, aber das Rückwärts laufen nicht
		// 3) Wie erkennt man Zirkel bei den Assoziationen => keine Assoziation mehr als einmal durchlaufen!
		// 4) Mehrfache Assoziationen zwischen zwei Klassen?
		// Funktioniert das aber für den Prototyp?
		
		Stack<String> path = new Stack<>();
		Stack<String> realPath = new Stack<>();
		
		path.push(nameOfReference);		
		String currNode = "";
		while( ! currNode.equals(crefName) ) {
			currNode = path.pop();
			
			if( realPath.contains(currNode) ) {
				realPath.pop();
				continue;
			} else {
				realPath.push(currNode);
			}
			
			FmmlxObject currObject = underlyingDiagram.getObjectByPath(packageName + "::" + currNode);
			Vector<FmmlxAssociation> relations = currObject.getAllRelatedAssociations();	
			for( FmmlxAssociation currRel : relations ) {
				FmmlxObject otherNode = currRel.getSourceNode().getName().equals(currObject.getName()) ? currRel.getTargetNode() : currRel.getSourceNode();	
				path.push(otherNode.getName());
			}
		}
		
		// Now follow the path from main to target and only select the relevant instances depending on the list view
		Vector<FmmlxAssociation> pathAssocs = new Vector<>();
		String oldNode = realPath.pop();
		while( ! realPath.isEmpty() ){
			String newNode = realPath.pop();

			Vector<FmmlxAssociation> assocs = underlyingDiagram.getAssociations();
			for( FmmlxAssociation assoc : assocs ) {
				if( assoc.getSourceNode().getName().equals(newNode) &&
						assoc.getTargetNode().getName().equals(oldNode) ) {
					pathAssocs.add(assoc);
				}
				
				if( assoc.getTargetNode().getName().equals(newNode) &&
						assoc.getSourceNode().getName().equals(oldNode) ) {
					pathAssocs.add(assoc);
				}
			}
					
			oldNode = newNode;
		}
		
		// Get first relevant list view
		ListView lastView = mainListView; //.getSelectionModel().getSelectedItem();
		for( FmmlxAssociation assoc : pathAssocs ) {
			ListView currView = listViewsToAssociations.get(assoc.getName());
			
			// Select Items depending on last view
			String lastSource = "";
			try {
				lastSource = lastView.getSelectionModel().getSelectedItem().toString();
			} catch( Exception e ) {
				//System.err.println("No item selected! Using first value in list.");
				lastSource = lastView.getItems().get(0).toString();
			}

			FmmlxObject relObject = underlyingDiagram.getObjectByPath(packageName + "::" + lastSource);
			
			Vector<String> possNodes = new Vector<>();
			for( FmmlxLink link : underlyingDiagram.getRelatedLinksByObject(relObject)) {
				String otherObject = link.getSourceNode().getName().equals(lastSource) ? link.getTargetNode().getName() : link.getSourceNode().getName();
				if( assoc.getName().equals(link.getAssociation().getName())) {
					possNodes.add(otherObject);
				}
			}
			
			if( currView != null && currView.getSelectionModel().isEmpty() ) {
				currView.getSelectionModel().select(0);
			}
			
			// if no list view can be identified, this is due to the cardinality of the current association
			// 1:1 cardinality = no list view; 1:n/m:n cardinality = list view
			// in this case only the relevant entry has to be returned in an initial list view
			if( currView == null ) {
				currView = new ListView<>();
			}
			
			// Set items and return them through the list view
			try {
				currView.setItems(FXCollections.observableArrayList(possNodes));
			} catch( Exception e ) {
				System.err.println("Ups");
			}
			
			// remember for next iteration
			lastView = currView;
		}
		
		return lastView;
	}
	
	public Object fetchResult() throws IllegalClassFormatException{
		// Handle CREF
		//if( isMain && isInstanceFiller ) {
		//	FmmlxObject mainObj = underlyingDiagram.getObjectByPath(packageName + "::" + nameOfReference);
		//	return mainObj.getInstances(); // Vektor <FmmlxObjects>
		//}
		
		// TBD: Fetch results from diagram and not from the objectStructure!
		
//		underlyingDiagram.get
//		underlyingDiagram.getObjects()
//		underlyingDiagram.getObjectByPath()
//		underlyingDiagram.getAllMetaClass()
		
		// Handle (C)REF
		visitedAssocs.clear();
		ListView<String> refList = determineReference();
		if( refList.getItems().size() == 0 ) {
			throw new IllegalClassFormatException(); // reference cannot be resolved
		}
		
		if( isInstanceFiller ) {
			// return all referenced instances
			return refList.getItems();
		}
		
		// determine referenced element in listView
		String selEl = refList.getSelectionModel().getSelectedItem();
		if( selEl == null ) { // in case of dummy listView for 1:1-relations
			selEl = refList.getItems().get(0);
		}
		
		FmmlxObject currEl = underlyingDiagram.getObjectByPath(packageName + "::" + selEl);
		
		// Handle REF for INJ
		if( isInjection ) {
			FmmlxSlot injSlot = currEl.getSlot(classComponent);
			// try to discover attribute on a higher level
			while( injSlot == null ) {
				currEl = underlyingDiagram.getObjectByPath( currEl.getOfPath() );
				if( currEl.getOfPath().equals("Root::FMML::MetaClass")) {
					break;
				}
				injSlot = currEl.getSlot(classComponent);

			}
			return injSlot.getValue();
		}
		
		if( isAction ) {
			FmmlxOperation actOp = currEl.getOperationByName(classComponent);
			
			// underlyingDiagram.getComm().runOperation(underlyingDiagram.getID(), "");
			// Parameter über neues Sprachelement "USE.."
			
			// Konsolen inhalt, ggf. mit Semikolon
			// Qualified name: Invoicing::i1.invoiceTotal()
			// TBD: Wie muss hier der Befehl aussehen, um eine Operation auszuführen?
			// XMF-Statement als String?
			// For Now: No-arg-Actions?
			return "";
		}
		
		if( isActionInjection ) {
			FmmlxOperationValue actinjOpVal = currEl.getOperationValue(classComponent);
			return actinjOpVal.getValue();
		}
		
		// no result could be fetched
		throw new IllegalClassFormatException();		
	}
}
