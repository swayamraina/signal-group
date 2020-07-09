package dev.swayamraina.signal.group.core.http;

import dev.swayamraina.signal.group.annotations.Internal;
import org.apache.http.client.config.RequestConfig;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static dev.swayamraina.signal.group.utils.Constants.EMPTY;

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

    private Optional<Map<String, String>> variables;
    public Map<String, String> variables () { return variables.isPresent() ? variables.get() : Collections.emptyMap(); }
    public Http withVariables (Map<String, String> variables) { this.variables = Optional.ofNullable(variables); return this; }

    private Optional<Map<String, String>> parameters;
    public Map<String, String> parameters () { return parameters.isPresent() ? parameters.get() : Collections.emptyMap(); }
    public Http withParameters (Map<String, String> parameters) { this.parameters = Optional.ofNullable(parameters); return this; }

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
