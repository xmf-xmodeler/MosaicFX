package tool.clients.serializer.interfaces;

public interface ISerializer {
    void saveState();
    void loadState(int diagramId, String diagramLabel);
}
