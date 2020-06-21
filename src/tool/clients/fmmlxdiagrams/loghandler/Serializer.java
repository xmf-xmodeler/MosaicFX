package tool.clients.fmmlxdiagrams.loghandler;

public class Serializer implements ISerializer{
    private final XmlHandler xmlHandler;

    public Serializer() {
        this.xmlHandler = XmlHandler.getInstance();
    }

    @Override
    public void saveState(String path) {
        this.xmlHandler.saveState();
    }

    @Override
    public void loadState(String path) {
        this.xmlHandler.getLatestSave();

        //TODO bind to Diagram
    }
}
