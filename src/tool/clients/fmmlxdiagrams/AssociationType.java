package tool.clients.fmmlxdiagrams;

public class AssociationType implements Comparable<AssociationType> {

	public String  displayName;
	public String  path;
	public String  color; 
	public Integer strokeWidth; 
	public String  dashArray; 
	public String  startDeco;
	public String  endDeco;
	public String  colorLink; 
	public Integer strokeWidthLink; 
	public String  dashArrayLink; 
	public String  startDecoLink;
	public String  endDecoLink;
	public String  sourcePath;
	public String  targetPath;
	public Level  sourceLevel;
	public Level  targetLevel;
	public String  sourceMult;
	public String  targetMult;

	public AssociationType(
			String  displayName, 
			String  path, 
			String  color, 
			Integer strokeWidth, 
			String  dashArray, 
			String  startDeco,
			String  endDeco,
			String  colorLink, 
			Integer strokeWidthLink, 
			String  dashArrayLink, 
			String  startDecoLink,
			String  endDecoLink,
			String  sourcePath,
			String  targetPath,
			Level   sourceLevel,
			Level   targetLevel,
			String  sourceMult,
			String  targetMult) {

		this.displayName = displayName;
		this.path = path;
		this.color = color;
		this.strokeWidth = strokeWidth;
		this.dashArray = dashArray;
		this.startDeco = startDeco;
		this.endDeco = endDeco;
		this.colorLink = colorLink;
		this.strokeWidthLink = strokeWidthLink;
		this.dashArrayLink = dashArrayLink;
		this.startDecoLink = startDecoLink;
		this.endDecoLink = endDecoLink;
		this.sourcePath = sourcePath;
		this.targetPath = targetPath;
		this.sourceLevel = sourceLevel;
		this.targetLevel = targetLevel;
		this.sourceMult = sourceMult;
		this.targetMult = targetMult;		
	}

//	@Override
//	public String toString() {
//		return "AssociationType [displayName=" + displayName + ", path=" + color + ", path=" + color + ", strokeWidth=" + strokeWidth + ", dashArray="
//				+ dashArray + ", startDeco=" + startDeco + ", endDeco=" + endDeco + "]";
//	}
	

	public String toString() {
		return displayName;// + "(" + color + "/" + strokeWidth + "/" + dashArray + ")";
	}
	
	public transient String _error_Mgs_;

	@Override
	public int compareTo(AssociationType that) {
		if(this.displayName.equals("DefaultAssociation")) return -1;
		if(that.displayName.equals("DefaultAssociation")) return 1;
		if(this.displayName.equals("Aggregation")) return -1;
		if(that.displayName.equals("Aggregation")) return 1;
		if(this.displayName.equals("Composition")) return -1;
		if(that.displayName.equals("Composition")) return 1;
		return this.displayName.compareTo(that.displayName);
	}

	public String getDisplayName() {
		return displayName;
	}
}
