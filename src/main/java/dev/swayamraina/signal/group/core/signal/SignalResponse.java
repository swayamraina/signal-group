package dev.swayamraina.signal.group.core.signal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class SignalResponse {

    private String raw;
    public String raw () { return raw; }

    private Map<String, Object> processed;
    public Map<String, Object> processed () { return processed; }
    public Object get (String path) { return processed.get(path); }
    public void add (String path, Object value) { processed.put(path, value); }



    public SignalResponse (String raw) {
        this.raw = raw;
        this.processed = new ConcurrentHashMap<>();
    }

}
