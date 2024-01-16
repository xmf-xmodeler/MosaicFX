package tool.clients.fmmlxdiagrams;

public class Level{

	private final Integer minLevel;
	private final Integer maxLevel;

	public Level(Integer minLevel, Integer maxLevel) {
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
	}
	
	public Level(Integer level) {
		this.minLevel = level;
		this.maxLevel = level;
	}
	
	public String toString() {
		if(maxLevel == null) return minLevel + "-?";
		if(maxLevel.equals(minLevel)) return minLevel.toString();
		return minLevel + "-" + maxLevel;
	}

	public boolean isClass() {
		return minLevel != 0;
	}

	public boolean isContingentLevelClass() {
		return !(minLevel.equals(maxLevel));
	}

	public boolean isFixedLevelClass() {
		return minLevel.equals(maxLevel) && minLevel > 0;
	}

	public boolean isEqual(int level) {
		return maxLevel == level && minLevel == level;
	}

	public boolean isNonIntrinsic(int level) {
		return maxLevel == -1 && minLevel == -1;
	}

	public int getMinLevel() {return minLevel == null ? -1 : minLevel;} 
	public int getMaxLevel() {return maxLevel == null ? -1 : maxLevel;} 
	
	@SuppressWarnings("serial")
	public static class UnparseableException extends Exception {
		public UnparseableException(String text, Throwable cause) {super(text, cause);}
		public UnparseableException(String text) {super(text);}
	}

	public static Level parseLevel(String s) throws UnparseableException{
		if(s.equals("-1")) {
			return new Level(-1, -1);
		} else {
			if(s.contains("-")) {
				String[] sa = s.split("-");
				if(sa.length > 2) { throw new UnparseableException("Too many levels"); } 
				if(sa.length < 2) { throw new UnparseableException("Too few levels"); }
				String minS = sa[0];
				String maxS = sa[1];
				Integer min = null;
				Integer max = null;
				try { 
					min = Integer.parseInt(minS); 
				} catch (NumberFormatException nfe) {
					throw new UnparseableException("minLevel unparseable", nfe);
				}
				try { 
					if(!("?".equals(maxS) || "*".equals(maxS))) {
						max = Integer.parseInt(maxS); 
					}
				} catch (NumberFormatException nfe) {
					throw new UnparseableException("maxLevel unparseable", nfe);
				}
				return new Level(min, max);
			}
			boolean plus = s.endsWith("+");
			if(plus) {
				s = s.substring(0, s.length()-1);
			}
			try{
				int i = Integer.parseInt(s);
				if(i >= 0) return plus?new Level(i,null):new Level(i);
				throw new UnparseableException("Level cannot be negative.");
			} catch (NumberFormatException nfe) {
				throw new UnparseableException("Level unparseable", nfe);
			}			
		}
	}

	public Level minusOne() {
		return new Level(this.minLevel-1, this.maxLevel == null?null:(this.maxLevel-1));
	}
}
