package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.Multiplicity;

public class EditAssociationDialogResult extends DialogResult {
	
	private final FmmlxAssociation selectedAssociation;
	private FmmlxObject source;
	private FmmlxObject target;
	private int newInstLevelSource;
	private int newInstLevelTarget;
	private String newDisplayNameSource;
	private String newDisplayNameTarget;
	private String newIdentifierSource;
	private String newIdentifierTarget;
	private Multiplicity multiplicitySource;
	private Multiplicity multiplicityTarget;
	private boolean sourceVisibleFromTarget;
	private boolean targetVisibleFromSource;
	private boolean symmetric;
	private boolean transitive;



	public EditAssociationDialogResult(FmmlxAssociation selectedAssociation, FmmlxObject source, FmmlxObject target, 
			Integer newInstLevelSource, Integer  newInstLevelTarget, 
			String  newDisplayNameSource, String newDisplayNameTarget, 			
			String  newIdentifierSource, String  newIdentifierTarget,		
			Multiplicity multiplicitySource, Multiplicity multiplicityTarget,		
			boolean sourceVisibleFromTarget,
			boolean targetVisibleFromSource,
			boolean symmetric,
			boolean transitive) {
		
		this.selectedAssociation = selectedAssociation;
		this.source = source;
		this.target = target;
		this.newInstLevelSource = newInstLevelSource;
		this.newInstLevelTarget = newInstLevelTarget;
		this.newDisplayNameSource = newDisplayNameSource;
		this.newDisplayNameTarget = newDisplayNameTarget;
		this.newIdentifierSource = newIdentifierSource;
		this.newIdentifierTarget = newIdentifierTarget;
		this.multiplicitySource = multiplicitySource;
		this.multiplicityTarget = multiplicityTarget;
		this.sourceVisibleFromTarget = sourceVisibleFromTarget;
		this.targetVisibleFromSource = targetVisibleFromSource;
		this.symmetric = symmetric;
		this.transitive = transitive;
	}


	public FmmlxAssociation getAssociation() {
		return selectedAssociation;
	}


	public FmmlxObject getSource() {
		return source;
	}


	public FmmlxObject getTarget() {
		return target;
	}


	public int getNewInstLevelSource() {
		return newInstLevelSource;
	}


	public int getNewInstLevelTarget() {
		return newInstLevelTarget;
	}


	public String getNewDisplayNameSource() {
		return newDisplayNameSource;
	}


	public String getNewDisplayNameTarget() {
		return newDisplayNameTarget;
	}


	public String getNewIdentifierSource() {
		return newIdentifierSource;
	}


	public String getNewIdentifierTarget() {
		return newIdentifierTarget;
	}


	public Multiplicity getMultiplicitySource() {
		return multiplicitySource;
	}


	public Multiplicity getMultiplicityTarget() {
		return multiplicityTarget;
	}
	

	public boolean isSourceVisibleFromTarget() {
		return sourceVisibleFromTarget;
	}


	public boolean isTargetVisibleFromSource() {
		return targetVisibleFromSource;
	}


	public boolean isSymmetric() {
		return symmetric;
	}


	public boolean isTransitive() {
		return transitive;
	}
}
