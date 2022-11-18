package tool.clients.fmmlxdiagrams;

public abstract class AbstractFmmlxObjectDisplay {
	
	protected final FmmlxDiagram diagram;
	protected final FmmlxObject object;

	public AbstractFmmlxObjectDisplay(FmmlxDiagram diagram, FmmlxObject object) {
		this.object = object;
		this.diagram = diagram;
	}
	
}
