package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.Multiplicity;

public class AddAssociationDialogResult extends DialogResult {

	private FmmlxObject source;
	private FmmlxObject target;
	private int instLevelSource;
	private int instLevelTarget;
	private String displayNameSource;
	private String displayNameTarget;
	private String identifierSource;
	private String identifierTarget;
	private Multiplicity multiplicitySource;
	private Multiplicity multiplicityTarget;
	private Boolean sourceVisible;
	private Boolean targetVisible;
	private Boolean isSymmetric;
	private Boolean isTransitive;


	public AddAssociationDialogResult(FmmlxObject source, FmmlxObject target,
									  Integer instLevelSource, Integer instLevelTarget,
									  String displayNameSource, String displayNameTarget,
									  String identifierSource, String identifierTarget,
									  Multiplicity multiplicitySource, Multiplicity multiplicityTarget,
									  Boolean sourceVisible, Boolean targetVisible, Boolean isSymmetric, Boolean isTransitive) {
		this.source = source;
		this.target = target;
		this.instLevelSource = instLevelSource;
		this.instLevelTarget = instLevelTarget;
		this.displayNameSource = displayNameSource;
		this.displayNameTarget = displayNameTarget;
		this.identifierSource = identifierSource;
		this.identifierTarget = identifierTarget;
		this.multiplicitySource = multiplicitySource;
		this.multiplicityTarget = multiplicityTarget;
		this.sourceVisible = sourceVisible;
		this.targetVisible = targetVisible;
		this.isSymmetric = isSymmetric;
		this.isTransitive = isTransitive;
	}

	public FmmlxObject getSource() {
		return source;
	}

	public FmmlxObject getTarget() {
		return target;
	}

	public int getInstLevelSource() {
		return instLevelSource;
	}

	public int getInstLevelTarget() {
		return instLevelTarget;
	}

	public String getDisplayNameSource() {
		return displayNameSource;
	}

	public String getDisplayNameTarget() {
		return displayNameTarget;
	}

	public String getIdentifierSource() {
		return identifierSource;
	}

	public String getIdentifierTarget() {
		return identifierTarget;
	}

	public Multiplicity getMultiplicitySource() {
		return multiplicitySource;
	}

	public Multiplicity getMultiplicityTarget() {
		return multiplicityTarget;
	}
	
	public Boolean sourceVisible() {
		return sourceVisible;
	}
	
	public Boolean targetVisible() {
		return targetVisible;
	}
	
	public Boolean isSymmetric() {
		return isSymmetric;
	}
	
	public Boolean isTransitive() {
		return isTransitive;
	}

	@Override
	public String toString() {
		return "AddAssociationDialogResult{" +
				"source=" + source.getName() +
				", target=" + target.getName() +
				", instLevelSource=" + instLevelSource +
				", instLevelTarget=" + instLevelTarget +
				", displayNameSource='" + displayNameSource + '\'' +
				", displayNameTarget='" + displayNameTarget + '\'' +
				", identifierSource='" + identifierSource + '\'' +
				", identifierTarget='" + identifierTarget + '\'' +
				", multiplicitySource=" + multiplicitySource +
				", multiplicityTarget=" + multiplicityTarget +
				'}';
	}
}
