package tool.helper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MultiHashMap<Key, Value> {
	
	private final HashMap<Key, Set<Value>> hashMap;
	
	public MultiHashMap() {
		hashMap = new HashMap<Key, Set<Value>>();
	}

	public void clear() {hashMap.clear();}
	public Set<Value> get(Key key) {return hashMap.get(key);}
	public Set<Key> keySet() {return hashMap.keySet();}
	
	public void put(Key key, Value value) {
		Set<Value> currentSet = hashMap.get(key);
		if(currentSet == null) currentSet = new HashSet<Value>();
		currentSet.add(value);
		hashMap.put(key, currentSet);
	}


}
