package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.Multiplicity;

public class MultiplicityDialogResult extends DialogResult {

	private final String minimum;
	private final String maximum;
	private final boolean isOrdered;
	private final boolean allowDuplicates;
	private boolean upperLimit = true;

	public MultiplicityDialogResult(String minimum, String maximum, boolean isOrdered, boolean allowDuplicates) {
		this.minimum = minimum;
		this.maximum = maximum;
		this.isOrdered = isOrdered;
		this.allowDuplicates = allowDuplicates;

		if (maximum.equals("*")) {
			upperLimit = false;
		}
	}

	public Multiplicity convertToMultiplicity() {
		int min = Integer.parseInt(minimum);
		int max = -1;
		if (maximum.equals("*")) {
			max = 2;
		} else {
			max = Integer.parseInt(maximum);
		}

		return new Multiplicity(min, max, upperLimit, isOrdered, allowDuplicates);
	}
}
