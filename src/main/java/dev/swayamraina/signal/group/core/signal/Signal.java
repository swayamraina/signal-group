package dev.swayamraina.signal.group.core.signal;

import dev.swayamraina.signal.group.entity.ExtractorKey;
import dev.swayamraina.signal.group.core.http.Http;
import dev.swayamraina.signal.group.entity.Meta;

import java.util.List;

public final class Signal {

    private int priority;
    public int priority () { return priority; }

    private Http http;
    public Http http () { return http; }

    private List<Signal> children;
    public List<Signal> children () { return children; }

    private Meta meta;
    public Meta meta () { return meta; }

    private List<ExtractorKey> keys;
    public List<ExtractorKey> keys () { return keys; }

}
