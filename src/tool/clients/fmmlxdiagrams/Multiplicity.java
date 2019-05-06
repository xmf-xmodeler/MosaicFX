package tool.clients.fmmlxdiagrams;

public class Multiplicity {
	
	private int min;
	private boolean unlimited;
	private boolean sorted;
	private boolean duplicate;
	
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
	public boolean isUnlimited() {
		return unlimited;
	}
	public void setUnlimited(boolean unlimited) {
		this.unlimited = unlimited;
	}
	public boolean isSorted() {
		return sorted;
	}
	public void setSorted(boolean sorted) {
		this.sorted = sorted;
	}
	public boolean hasDuplicate() {
		return duplicate;
	}
	public void setDuplicate(boolean duplicate) {
		this.duplicate = duplicate;
	}
	private int max;

}
