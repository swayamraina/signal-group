package dev.swayamraina.signal.group.core.signal;

import dev.swayamraina.signal.group.annotations.Internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class SignalResponse {

    /**
     * This internal field when set to true indicates the response has been
     **/
    @Internal private volatile boolean completed;
    public boolean completed () { return completed; }
    public void markCompleted () { completed = true; }

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
