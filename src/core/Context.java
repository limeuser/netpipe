package core;

import java.util.HashMap;

public class Context {
    private HashMap<String, Object> map = new HashMap<String, Object>();
    
    public Object get(String tag) {
        return map.get(tag);
    }
    
    public void set(String tag, Object e) {
        map.put(tag, e);
    }
    
    public HashMap<String, Object> getMap() {
        return map;
    }
}
