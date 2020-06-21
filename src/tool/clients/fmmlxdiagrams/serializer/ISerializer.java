package tool.clients.fmmlxdiagrams.serializer;

public interface ISerializer {
    void saveState();
    void loadState(int diagramId);
}
