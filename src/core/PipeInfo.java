package core;

import java.lang.reflect.Type;

public class PipeInfo {
    public Type elementType;
    public String name;
    
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof PipeInfo)) {
            return false;
        }
        
        PipeInfo p = (PipeInfo) o;
        return p.elementType.equals(elementType) && name.equals(p.name);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
