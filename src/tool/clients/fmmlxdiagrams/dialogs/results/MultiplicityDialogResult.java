package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.Multiplicity;

public class MultiplicityDialogResult extends DialogResult {

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

	public Multiplicity convertToMultiplicity() {
		return new Multiplicity(minimum, maximum, upperLimit, isOrdered, allowDuplicates);
	}
}
