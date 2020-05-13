package dev.swayamraina.signal.group.entity;

import dev.swayamraina.signal.group.annotations.Internal;

import java.util.List;

public final class SignalGroup {

    @Internal private String uid;
    public String uid () { return uid; }

    private Meta meta;
    public Meta meta () { return meta; }

    private List<Signal> signals;
    public List<Signal> signals () { return signals; }



    public SignalGroup (Meta meta, List<Signal> signals) {
        this.meta = meta;
        this.signals = signals;
    }

}
