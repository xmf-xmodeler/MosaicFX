package tool.clients.importer;

import java.util.Map;

public class Conflict {
    private String description;
    private Map<String, String> in;

    public Conflict(String description){
        this.description = description;
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
                "description='" + description + '\'' +
                ", in = " + in +
                '}';
    }

    public void setIn(Map<String, String> in) {
        this.in = in;
    }
}


