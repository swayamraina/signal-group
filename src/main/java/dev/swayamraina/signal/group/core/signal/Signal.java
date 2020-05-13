package dev.swayamraina.signal.group.entity;

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
