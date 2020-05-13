package dev.swayamraina.signal.group.core.signal;

import dev.swayamraina.signal.group.annotations.Internal;
import dev.swayamraina.signal.group.entity.Meta;

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
