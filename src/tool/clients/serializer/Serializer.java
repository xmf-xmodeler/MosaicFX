package tool.clients.serializer;

import tool.clients.serializer.interfaces.ISerializer;

public class Serializer implements ISerializer {
    private final XmlHandler xmlHandler;

    public Serializer() {
        this.xmlHandler = new XmlHandler();
    }

    @Override
    public synchronized void saveState() {
        this.xmlHandler.saveState();
    }

    @Override
    public synchronized void loadState(int diagramId, String diagramLabel) {
        this.xmlHandler.getLatestSave(diagramId, diagramLabel);

        //TODO bind to Diagram
    }
}
