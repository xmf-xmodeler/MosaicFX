package tool.clients.fmmlxdiagrams.serializer;

public class Serializer implements ISerializer{
    private final XmlHandler xmlHandler;

    public Serializer() {
        this.xmlHandler = new XmlHandler("logTest..xml");
    }

    @Override
    public synchronized void saveState() {
        this.xmlHandler.saveState();
    }

    @Override
    public synchronized void loadState(int diagramId) {
        this.xmlHandler.getLatestSave(diagramId);

        //TODO bind to Diagram
    }
}
