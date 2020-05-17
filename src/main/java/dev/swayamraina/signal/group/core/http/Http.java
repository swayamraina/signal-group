package dev.swayamraina.signal.group.core.http;

import dev.swayamraina.signal.group.annotations.Internal;
import org.apache.http.client.config.RequestConfig;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static dev.swayamraina.signal.group.utils.Constants.EMPTY;
import static dev.swayamraina.signal.group.utils.Constants.EMPTY_LIST;

public final class Http {

    @Internal private String key;
    public String key () { return key; }

    private String type;
    public String type () { return type; }
    public Http ofType (String type) { this.type = type; return this; }

    private String host;
    public String host () { return host; }
    public Http withHost (String host) { this.host = host; return this; }

    private String endpoint;
    public String endpoint () { return endpoint; }
    public Http withEndpoint (String endpoint) { this.endpoint = endpoint; return this; }

    private Map<String, String> headers;
    public Map<String, String> headers () { return headers; }
    public Http withHeaders (Map<String, String> headers) { this.headers = headers; return this; }

    private Optional<List<String>> variables;
    public List<String> variables () { return variables.isPresent() ? variables.get() : EMPTY_LIST; }
    public Http withVariables (List<String> variables) { this.variables = Optional.ofNullable(variables); return this; }

    private Optional<List<String>> parameters;
    public List<String> parameters () { return parameters.isPresent() ? parameters.get() : EMPTY_LIST; }
    public Http withParameters (List<String> parameters) { this.parameters = Optional.ofNullable(parameters); return this; }

    private Optional<String> body;
    public String body () { return body.isPresent() ? body.get() : EMPTY; }
    public Http withBody (String body) { this.body = Optional.ofNullable(body); return this; }

    private RequestConfig config;
    public RequestConfig config () { return config; }
    public Http withConfig (int st, int ct, int crt) {
        RequestConfig.custom()
            .setSocketTimeout(st)
            .setConnectTimeout(ct)
            .setConnectionRequestTimeout(crt)
            .build();
        return this;
    }

}
