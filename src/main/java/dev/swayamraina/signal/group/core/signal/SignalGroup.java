package dev.swayamraina.signal.group.core.signal;

import dev.swayamraina.signal.group.annotations.Internal;

import java.util.List;
import java.util.Optional;

import static dev.swayamraina.signal.group.utils.Constants.EMPTY;

public final class SignalGroup {

    @Internal private String uid;
    public String uid () { return uid; }

    private List<Signal> signals;
    public List<Signal> signals () { return signals; }

    private Optional<String> description;
    private String description () { return description.isPresent() ? description.get() : EMPTY; }
    public void withDescription (String description) {
        this.description = Optional.of(description == null ? EMPTY : description);
    }



    public SignalGroup (List<Signal> signals) {
        this.signals = signals;
        this.withDescription(EMPTY);
    }

}
