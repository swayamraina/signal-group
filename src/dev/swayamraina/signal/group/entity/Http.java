package dev.swayamraina.signal.group.entity;

import dev.swayamraina.signal.group.annotations.Internal;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static dev.swayamraina.signal.group.utils.Constants.EMPTY_LIST;

public final class Http {

    @Internal private String key;
    public String key () { return key; }

    private String host;
    public String host () { return host; }

    private String endpoint;
    public String endpoint () { return endpoint; }

    private Map<String, String> headers;
    public Map<String, String> headers () { return headers; }

    private Optional<List<String>> variables;
    public List<String> variables () { return variables.isPresent() ? variables.get() : EMPTY_LIST; }

    private Optional<List<String>> parameters;
    public List<String> parameters () { return parameters.isPresent() ? parameters.get() : EMPTY_LIST; }

}
