package tool.clients.importer;

import java.util.HashMap;

public class MyHashMap extends HashMap<String, String> {

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i< this.size();i++ ){
            Object myKey = keySet().toArray()[i];
            stringBuilder.append(myKey.toString()).append(" : ").append(get(myKey));
            stringBuilder.append(", ");
        }

        return stringBuilder.substring(0, stringBuilder.toString().length() - 2);
    }
}
