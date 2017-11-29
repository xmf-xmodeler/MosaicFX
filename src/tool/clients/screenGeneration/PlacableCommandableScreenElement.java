package tool.clients.screenGeneration;

import org.eclipse.swt.widgets.Composite;

public abstract class PlacableCommandableScreenElement extends CommandableScreenElement {

	protected Composite content; 
	
	public PlacableCommandableScreenElement(String id) {
		super(id);
		// TODO Auto-generated constructor stub
	}

	public Composite getContent() {
		return content;
	}

	public void setContent(Composite content) {
		this.content = content;
	}

}
