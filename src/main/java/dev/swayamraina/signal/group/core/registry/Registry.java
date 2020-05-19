package dev.swayamraina.signal.group.core.registry;

import dev.swayamraina.signal.group.core.errors.NoDataFoundError;
import dev.swayamraina.signal.group.core.signal.SignalGroup;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



public class Registry {

    private Map<String, SignalGroup> registry;

    public SignalGroup get (String uid) {
        if (null == uid || "".equals(uid)) throw new IllegalArgumentException("empty uid");
        SignalGroup sg = registry.get(uid);
        if (null == sg) throw new NoDataFoundError(String.format("signal-group %s not registered", uid));
        return sg;
    }

    public void add (String uid, SignalGroup sg) {
        if (null == uid || "".equals(uid)) throw new IllegalArgumentException("empty uid");
        if (null == sg) throw new IllegalArgumentException("null values cannot be added to signal-group");
        registry.put(uid, sg);
    }

    public boolean exists (String uid) {
        if (null == uid || "".equals(uid)) throw new IllegalArgumentException("empty uid");
        return null != registry.get(uid);
    }




    public Registry () { this.registry = new ConcurrentHashMap<>(); }

}
