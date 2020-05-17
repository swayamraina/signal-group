package dev.swayamraina.signal.group.core.signal;

import dev.swayamraina.signal.group.core.extractor.ExtractorKey;
import dev.swayamraina.signal.group.core.http.Http;

import java.util.List;
import java.util.Optional;

import static dev.swayamraina.signal.group.utils.Constants.EMPTY;

public final class Signal {

    private String name;
    public String name () { return name; }

    private int priority;
    public int priority () { return priority; }

    private Http http;
    public Http http () { return http; }

    private List<Signal> children;
    public List<Signal> children () { return children; }

    private List<ExtractorKey> keys;
    public List<ExtractorKey> keys () { return keys; }

    private Optional<String> description;
    private String description () { return description.isPresent() ? description.get() : EMPTY; }
    public void withDescription (String description) {
        this.description = Optional.of(description == null ? EMPTY : description);
    }




    public Signal (String name, int priority, Http http, List<Signal> children, List<ExtractorKey> keys) {
        if (null == children || null == keys)
            throw new IllegalArgumentException("non nullable properties");
        if (0 > priority)
            throw new IllegalArgumentException("priority should be > 0");
        if (null == name || EMPTY.equals(name))
            throw new IllegalArgumentException("signal name cannot be null/empty");

        this.name = name;
        this.priority = priority;
        this.http = http;
        this.children = children;
        this.keys = keys;
        this.withDescription(EMPTY);
    }

}
