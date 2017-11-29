package tool.clients.editors.texteditor;

public class FileError {
  
  int    start;
  int    end;
  String message;

  public FileError(int start, int end, String message) {
    super();
    this.start = start;
    this.end = end;
    this.message = message;
  }

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public int getEnd() {
    return end;
  }

  public void setEnd(int end) {
    this.end = end;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

}
