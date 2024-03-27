package tool.clients.fmmlxdiagrams;

public abstract class AbstractFmmlxObjectDisplay {
	
	protected final FmmlxDiagramView diagram;
	protected final FmmlxObject object;

	public AbstractFmmlxObjectDisplay(FmmlxDiagramView diagram, FmmlxObject object) {
		this.object = object;
		this.diagram = diagram;
	}
	
}
