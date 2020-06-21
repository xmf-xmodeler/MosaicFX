package tool.clients.fmmlxdiagrams.serializer;

public class Serializer implements ISerializer{
    private final XmlHandler xmlHandler;

    public Serializer() {
        this.xmlHandler = XmlHandler.getInstance();
    }

    @Override
    public void saveState() {
        this.xmlHandler.saveState();
    }

    @Override
    public void loadState(int diagramId) {
        this.xmlHandler.getLatestSave(diagramId);

        //TODO bind to Diagram
    }
}
