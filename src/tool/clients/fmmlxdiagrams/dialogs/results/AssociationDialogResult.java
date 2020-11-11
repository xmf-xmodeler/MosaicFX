package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.Multiplicity;

public class AssociationDialogResult {

	public final FmmlxAssociation selectedAssociation;
	public final FmmlxObject source;
	public final FmmlxObject target;
	public final int newInstLevelSource;
	public final int newInstLevelTarget;
	public final String newDisplayName;
	public final String newIdentifierSource;
	public final String newIdentifierTarget;
	public final Multiplicity multTargetToSource;
	public final Multiplicity multSourceToTarget;
	public final boolean sourceVisibleFromTarget;
	public final boolean targetVisibleFromSource;
	public final boolean symmetric;
	public final boolean transitive;	
	
public AssociationDialogResult(FmmlxAssociation selectedAssociation, FmmlxObject source, FmmlxObject target, 
		Integer newInstLevelSource, Integer  newInstLevelTarget, 
		String  newDisplayName,  			
		String  newIdentifierSource, String  newIdentifierTarget,		
		Multiplicity multTargetToSource, Multiplicity multSourceToTarget,		
		boolean sourceVisibleFromTarget,
		boolean targetVisibleFromSource,
		boolean symmetric,
		boolean transitive) {
	
	this.selectedAssociation = selectedAssociation;
	this.source = source;
	this.target = target;
	this.newInstLevelSource = newInstLevelSource;
	this.newInstLevelTarget = newInstLevelTarget;
	this.newDisplayName = newDisplayName;
	this.newIdentifierSource = newIdentifierSource;
	this.newIdentifierTarget = newIdentifierTarget;
	this.multTargetToSource = multTargetToSource;
	this.multSourceToTarget = multSourceToTarget;
	this.sourceVisibleFromTarget = sourceVisibleFromTarget;
	this.targetVisibleFromSource = targetVisibleFromSource;
	this.symmetric = symmetric;
	this.transitive = transitive;
	
	}

}
