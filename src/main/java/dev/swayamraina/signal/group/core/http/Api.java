package dev.swayamraina.signal.group.core.http;

import dev.swayamraina.signal.group.core.errors.SignalExecutionError;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;


public class Api {

    private static final String GET   =  "GET";
    private static final String POST  =  "POST";
    private static final String PUT   =  "PUT";
    private static final String PATCH =  "PATCH";


    public String call (Http http) {
        HttpRequestBase request = null;
        String url = http.host() + http.endpoint();

        switch (http.type().toUpperCase()) {
            case GET:
                request = new HttpGet (url);
                break;

            case POST:
                request = new HttpPost (url);
                try { ((HttpEntityEnclosingRequest) request).setEntity(new StringEntity(http.body())); }
                catch (UnsupportedEncodingException e) { throw new SignalExecutionError(e); }
                break;

            case PUT:
                request = new HttpPut (url);
                try { ((HttpEntityEnclosingRequest) request).setEntity(new StringEntity(http.body())); }
                catch (UnsupportedEncodingException e) { throw new SignalExecutionError(e); }
                break;

            case PATCH:
                request = new HttpPatch (url);
                try { ((HttpEntityEnclosingRequest) request).setEntity(new StringEntity(http.body())); }
                catch (UnsupportedEncodingException e) { throw new SignalExecutionError(e); }
                break;
        }
        request.setConfig (http.config());
        return response (request);
    }


    private String response (HttpRequestBase request) {
        String response;
        CloseableHttpClient client = HttpClients.createDefault();
        try (CloseableHttpResponse httpResponse = client.execute(request)) {
            response = extract (httpResponse.getEntity().getContent());
        } catch (IOException e) { throw new SignalExecutionError(e); }
        finally { HttpClientUtils.closeQuietly(client); }
        return response;
    }


    private String extract (InputStream is) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            StringBuilder sb = new StringBuilder(1024);
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
            return sb.toString();
        }
    }

}
