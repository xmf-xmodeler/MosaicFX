package tool.clients.fmmlxdiagrams.dialogs.results;

public class MultiplicityDialogResult extends DialogResult{
	
	private int min;
	private int max;
	private boolean ordered;
	private boolean duplicates;
	
	public MultiplicityDialogResult(int min, int max, boolean ordered, boolean duplicates) {
		this.min = min;
		this.max = max;
		this.ordered = ordered;
		this.duplicates=duplicates;
	}
	
	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public boolean isOrdered() {
		return ordered;
	}

	public void setOrdered(boolean ordered) {
		this.ordered = ordered;
	}

	public boolean isDuplicates() {
		return duplicates;
	}

	public void setDuplicates(boolean duplicates) {
		this.duplicates = duplicates;
	}


}
