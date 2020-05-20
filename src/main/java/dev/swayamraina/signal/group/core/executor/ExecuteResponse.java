package dev.swayamraina.signal.group.core.executor;

import dev.swayamraina.signal.group.core.errors.NoDataFoundError;
import dev.swayamraina.signal.group.core.signal.SignalResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ExecuteResponse {

    private volatile Map<String, SignalResponse> data;
    public int size () { return data.size(); }


    public String raw (String key) {
        SignalResponse sr = data.get(key);
        return (null == sr || !sr.completed()) ? null : sr.raw();
    }


    public Object get (String key, String path) {
        SignalResponse sr = data.get(key);
        return (null == sr) ? null : sr.get(path);
    }


    public void add (String key, String raw) {
        data.put(key, new SignalResponse(raw));
    }


    public void add (String key, String path, Object value) {
        if (null == key || "".equals(key)) throw new IllegalArgumentException("empty key received");
        SignalResponse sr = data.get(key);
        if (null == sr) throw new NoDataFoundError(String.format("no signal-response found for %s", key));
        sr.add(path, value);
    }


    public void markCompleted (String key) {
        SignalResponse sr = data.get(key);
        sr.markCompleted();
    }




    public ExecuteResponse () { this.data = new ConcurrentHashMap<>(); }

}
