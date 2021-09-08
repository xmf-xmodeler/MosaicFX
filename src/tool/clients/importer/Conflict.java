package tool.clients.importer;

import java.util.Map;

public class Conflict {
    private final String type;
    private final String description;
    private MyHashMap in;

    public Conflict(String type, String description){
        this.type = type;
        this.in = new MyHashMap();
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, String> getIn() {
        return in;
    }

    public void putIn(String key, String value) {
        this.in.put(key, value);
    }

    @Override
    public String toString() {
        return "Conflict{" +
                "type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", in=" + in +
                '}';
    }

    public void setIn(MyHashMap in) {
        this.in = in;
    }
}


