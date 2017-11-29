package tool.clients.screenGeneration;

import org.eclipse.swt.custom.CTabFolder;

public abstract class Window extends CommandableScreenElement {

	protected static CTabFolder tabFolder;

	public Window(String id, ScreenGenerationClient client) {
		super(id);
	}

	public static void start(CTabFolder tabFolder) {
		Window.tabFolder = tabFolder;
	}

	public static Window windowFactory(final String id, final ScreenGenerationClient client, final int type,
			final String label, final boolean selected) {
		if (type == ScreenGenerationClient.TAB) {
			return new Tab(id, client, type, label, selected);
		} else {
			return null;
		}
	}
	
	public abstract void refresh();

}
