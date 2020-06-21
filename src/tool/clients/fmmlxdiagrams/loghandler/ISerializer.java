package tool.clients.fmmlxdiagrams.loghandler;

import org.w3c.dom.Element;

public interface ISerializer {
    public void saveState(String path);
    public void loadState(String path);
}
