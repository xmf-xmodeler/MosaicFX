package tool.clients.fmmlxdiagrams;

import java.util.Vector;

import xos.Value;

public class Multiplicity {
	public final int min;
	public final int max;
	public final boolean upperLimit;
	public final boolean ordered;
	@Deprecated // unused for now, maybe used later
	public final boolean duplicates;

	/**
	 * @param min
	 * @param max
	 * @param upperLimit: false if unlimited
	 * @param ordered:    true if order matters
	 * @param duplicates: true if duplicates are allowed
	 */
	public Multiplicity(int min, int max, boolean upperLimit, boolean ordered, boolean duplicates) {
		super();
		this.min = min;
		this.max = max;
		this.upperLimit = upperLimit;
		this.ordered = ordered;
		this.duplicates = duplicates;
	}

	public Multiplicity(Multiplicity old) {
		super();
		this.min = old.min;
		this.max = old.max;
		this.upperLimit = old.upperLimit;
		this.ordered = old.ordered;
		this.duplicates = old.duplicates;
	}

	public static Multiplicity OPTIONAL = new Multiplicity(0, 1, true, true, true);
	public static Multiplicity MANDATORY = new Multiplicity(1, 1, true, true, true);

	@Override
	public String toString() {
		return ((ordered ? "$" : "") + min + ".." + (upperLimit ? max : "*"));
//		return (max > 2 ? duplicates ? "[" : "{" : "") + (ordered ? "$" : "") + min + ".." + (upperLimit ? max : "*") + (max > 2 ? duplicates ? "]" : "}" : "");
	}

	public static Multiplicity parseMultiplicity(Vector<Object> xmfValue) {
		try{
			return new Multiplicity(
				(Integer) xmfValue.get(0), 
				(Integer) xmfValue.get(1), 
				(Boolean) xmfValue.get(2), 
				(Boolean) xmfValue.get(3), 
				true); // unused
		} catch (Exception e) {
			System.err.println("Multiplicity unparseable: " + xmfValue);
			return OPTIONAL;
		}
	}
	
	public Value[] toValue() {
		return new Value[]{
			new Value(min),
			new Value(max),
			new Value(upperLimit),
			new Value(ordered),
			new Value(duplicates)};
	}


}

