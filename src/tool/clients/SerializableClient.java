package tool.clients;

import java.io.PrintStream;

import org.w3c.dom.Document;

public interface SerializableClient {

  public void writeXML(PrintStream out);

  public void inflateXML(Document doc);

}
