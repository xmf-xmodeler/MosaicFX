package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.Multiplicity;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

public class MultiplicityDialogResult extends DialogResult {

	private FmmlxObject object;
	private FmmlxAttribute selectedAttribute;
	private PropertyType type;
	private final int minimum;
	private final int maximum;
	private final boolean isOrdered;
	private final boolean allowDuplicates;
	private boolean upperLimit;
	
	public MultiplicityDialogResult(int minimum, int maximum, boolean upperLimit, boolean isOrdered, boolean allowDuplicates) {
		this.minimum = minimum;
		this.maximum = maximum;
		this.upperLimit = upperLimit;
		this.isOrdered = isOrdered;
		this.allowDuplicates = allowDuplicates;
	}

	public MultiplicityDialogResult(FmmlxObject object, FmmlxAttribute selectedAttribute, PropertyType type, int minimum, int maximum, boolean upperLimit, boolean isOrdered, boolean allowDuplicates) {
		this.object = object;
		this.selectedAttribute = selectedAttribute;
		this.type = type;
		this.minimum = minimum;
		this.maximum = maximum;
		this.upperLimit = upperLimit;
		this.isOrdered = isOrdered;
		this.allowDuplicates = allowDuplicates;
	}


	public MultiplicityDialogResult(FmmlxObject object, FmmlxAttribute selectedItem, PropertyType type,
			Multiplicity multiplicity) {
		this.object = object;
		this.selectedAttribute = selectedItem;
		this.type =type;
		this.minimum = multiplicity.min;
		this.maximum = multiplicity.max;
		this.upperLimit = multiplicity.upperLimit;
		this.isOrdered = multiplicity.ordered;
		this.allowDuplicates = multiplicity.duplicates;
	}



	public Multiplicity convertToMultiplicity() {
		return new Multiplicity(minimum, maximum, upperLimit, isOrdered, allowDuplicates);
	}

	public boolean isUpperLimit() {
		return upperLimit;
	}

	public void setUpperLimit(boolean upperLimit) {
		this.upperLimit = upperLimit;
	}

	public FmmlxObject getObject() {
		return object;
	}

	public FmmlxAttribute getSelectedAttribute() {
		return selectedAttribute;
	}

	public PropertyType getType() {
		return type;
	}

	public int getMinimum() {
		return minimum;
	}

	public int getMaximum() {
		return maximum;
	}

	public boolean isOrdered() {
		return isOrdered;
	}

	public boolean isAllowDuplicates() {
		return allowDuplicates;
	}
	
	
}
