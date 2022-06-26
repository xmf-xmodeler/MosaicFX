package tool.clients.fmmlxdiagrams.classbrowser;

import java.lang.instrument.IllegalClassFormatException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxLink;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;
import tool.clients.fmmlxdiagrams.FmmlxOperationValue;
import tool.clients.fmmlxdiagrams.FmmlxSlot;

public class ControllerLanguageInterpreter {
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
	private HashMap<AssociationInstanceMapping, List<String>> linkHierarchy;
	private HashMap<String, ListView<String>> listViewsToAssociations;
	
	// memory
	private ArrayList<String> visitedAssocs = new ArrayList<>();
	
	public ControllerLanguageInterpreter( String idOfElement, AbstractPackageViewer diagram, ListView<String> mainListView, HashMap<AssociationInstanceMapping, List<String>> linksPerAssociationPerInstance, HashMap<String, ListView<String>> listViewsToAssociations) {
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
		linkHierarchy = linksPerAssociationPerInstance;
		this.listViewsToAssociations = listViewsToAssociations;
	}
	
	public boolean isResultList() {
		return isInstanceFiller;
	}
	
	private ListView<String> determineReference(ListView<String> lastListView) {
		if( lastListView.getSelectionModel().getSelectedItem() == null ) {
			lastListView.getSelectionModel().select(0);
		}
		
		String selEl = lastListView.getSelectionModel().getSelectedItem();
		
		FmmlxObject currEl = underlyingDiagram.getObjectByPath(packageName + "::" + selEl);
		Vector<FmmlxAssociation> assocs = currEl.findAssociationsForLinks();
		
		FmmlxObject fetchEl = underlyingDiagram.getObjectByPath(packageName + "::" + selEl);
		while( true ) {
			if( fetchEl.getMetaClassName().equals(nameOfReference) ) {
				return lastListView;
			}
			
			fetchEl = underlyingDiagram.getObjectByPath( fetchEl.getOfPath() );
			if( fetchEl.getOfPath().equals("Root::FMML::MetaClass")) {
				break;
			}
		}
				
		// analyze subnodes
		for( FmmlxAssociation assoc : assocs ) {
			ListView<String> subView = listViewsToAssociations.get(assoc.getName());
			
			
			if( subView != null && ! visitedAssocs.contains(assoc.getName())) {
				visitedAssocs.add(assoc.getName());
				 
				 ListView<String> curr = determineReference(subView);
				 if( curr.getItems().size() > 0) {
					 return curr;
				 }
			}
			
			if( assoc.getTargetNode().getName().equals(currEl.getMetaClassName())) {
				ListView<String> res = new ListView<>();
				String resVal = "";
				
				Vector<FmmlxLink> test = underlyingDiagram.getAssociationInstance();
				for( FmmlxLink link : test ) {
					if( link.getAssociation().getName().equals(assoc.getName()) ) {
						if( link.getTargetNode().getName().equals(currEl.getName())){
							// only consider this link if it corresponds to the desired reference!
							String maxMetaClass = maxParent.max( underlyingDiagram, link.getSourceNode() );
							if( maxMetaClass.equals(nameOfReference)) {
								resVal = link.getSourceNode().getName();
							}
						} else {
							visitedAssocs.add(assoc.getName());
							String newVal =  link.getTargetNode().getName();
							
							ListView<String> newValList = new ListView<>();
							newValList.setItems(FXCollections.observableArrayList(newVal));
							
							ListView<String> curr = determineReference(newValList);
							if( curr.getItems().size() > 0) {
								return curr;
							}
						}
					}
				}
				
				
				
				res.setItems(FXCollections.observableArrayList(resVal));
				if( ! resVal.equals("")) {
					return res;
				}
			}
			
			if( assoc.getSourceNode().getName().equals(currEl.getMetaClassName() )) {
				ListView<String> res = new ListView<>();
				String resVal = "";
				
				Vector<FmmlxLink> test = underlyingDiagram.getAssociationInstance();
				for( FmmlxLink link : test ) {
					if( link.getAssociation().getName().equals(assoc.getName()) ) {
						if( link.getSourceNode().getName().equals(currEl.getName())){
							// only consider this link if it corresponds to the desired reference!
							String maxMetaClass = maxParent.max( underlyingDiagram, link.getTargetNode() );
							if( maxMetaClass.equals(nameOfReference)) {
								resVal = link.getTargetNode().getName();
							} else {
								visitedAssocs.add(assoc.getName());
								String newVal =  link.getTargetNode().getName();
								
								ListView<String> newValList = new ListView<>();
								newValList.setItems(FXCollections.observableArrayList(newVal));
								
								ListView<String> curr = determineReference(newValList);
								if( curr.getItems().size() > 0) {
									return curr;
								}
							}
						}
						
					}
				}
				
				res.setItems(FXCollections.observableArrayList(resVal));
				if( ! resVal.equals("")) {
					return res;
				}
			}
		}
		
		// if nothing was found return an initial set
		return new ListView<String>();
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
		ListView<String> refList = determineReference(mainListView);
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
			if( injSlot == null ) { // when attributes is instantiated on a higher level..
				injSlot = underlyingDiagram.getObjectByPath( currEl.getOfPath() ).getSlot(classComponent);
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
