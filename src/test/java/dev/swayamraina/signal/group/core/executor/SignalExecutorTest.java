package dev.swayamraina.signal.group.core.executor;

import dev.swayamraina.signal.group.core.KeyDataExtractor;
import dev.swayamraina.signal.group.core.errors.NoDataFoundError;
import dev.swayamraina.signal.group.core.errors.SignalExecutionError;
import dev.swayamraina.signal.group.core.http.Api;
import dev.swayamraina.signal.group.core.http.Http;
import dev.swayamraina.signal.group.core.registry.Registry;
import dev.swayamraina.signal.group.core.signal.Signal;
import dev.swayamraina.signal.group.core.signal.SignalGroup;
import dev.swayamraina.signal.group.entity.ExtractorKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Arrays;


public class SignalExecutorTest {

    private Registry registry;
    private KeyDataExtractor extractor;
    private Api api;
    private SignalExecutor signalExecutor;


    @Before public void  setup () {
        registry = new Registry();
        extractor = new KeyDataExtractor();
        api = Mockito.mock(Api.class);
        signalExecutor = new SignalExecutor(registry, api, extractor);
    }


    @Test public void testExecute () {
        ExecuteRequest request; ExecuteResponse response;
        Signal s1, s2;  SignalGroup sg;  Http http;

        // TC 1 : null request
        request = null;
        try {
            signalExecutor.execute(request);
            Assert.fail();
        } catch (IllegalArgumentException e) {}

        // TC 2 : null uid
        request = new ExecuteRequest(null);
        try {
            signalExecutor.execute(request);
            Assert.fail();
        } catch (IllegalArgumentException e) {}


        // TC 3 : empty uid
        request = new ExecuteRequest("");
        try {
            signalExecutor.execute(request);
            Assert.fail();
        } catch (IllegalArgumentException e) {}


        // TC 4 : uid does not exists in registry
        request = new ExecuteRequest("not-exists");
        try {
            signalExecutor.execute(request);
            Assert.fail();
        } catch (NoDataFoundError e) {}

        // TC 5 : signal group with null signals
        request = new ExecuteRequest("exists");
        sg = new SignalGroup(null);
        registry.add("exists", sg);
        response = signalExecutor.execute(request);
        Assert.assertNotNull(response);
        Assert.assertEquals(0, response.size());

        // TC 6 : single level with no extractors
        request = new ExecuteRequest("exists");
        http = null;
        s1 = new Signal("service-a", 1, http, Arrays.asList(), Arrays.asList());
        sg = new SignalGroup(Arrays.asList(s1));
        registry.add("exists", sg);
        Mockito.when(api.call(http)).thenReturn("{}");
        response = signalExecutor.execute(request);
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.raw("service-a"));

        // TC 6 : single level with extractors
        request = new ExecuteRequest("exists");
        http = null;
        s1 = new Signal("service-a", 1, http, Arrays.asList(), Arrays.asList(new ExtractorKey("data.profile.name")));
        sg = new SignalGroup(Arrays.asList(s1));
        registry.add("exists", sg);
        Mockito.when(api.call(http)).thenReturn("{\"data\": {\"profile\": {\"name\": \"swayam\"}}}");
        response = signalExecutor.execute(request);
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.raw("service-a"));
        Assert.assertNotNull(response.get("service-a", "data.profile.name"));
        Assert.assertEquals("swayam", response.get("service-a", "data.profile.name"));

        // TC 7 : multi level with no extractor
        request = new ExecuteRequest("exists");
        http = null;
        s2 = new Signal("service-a-a", 1, http, Arrays.asList(), Arrays.asList(new ExtractorKey("data.orders[1].amount")));
        s1 = new Signal("service-a", 1, http, Arrays.asList(s2), Arrays.asList(new ExtractorKey("data.profile.name")));
        sg = new SignalGroup(Arrays.asList(s1));
        registry.add("exists", sg);
        Mockito.when(api.call(http)).thenReturn (
                "{\"data\": {\"profile\": {\"name\": \"swayam\"}}}",
                "{\"data\": {\"orders\": [{\"amount\": 23}, {\"amount\": 46}]}}"
        );
        response = signalExecutor.execute(request);
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.raw("service-a"));
        Assert.assertNotNull(response.get("service-a", "data.profile.name"));
        Assert.assertEquals("swayam", response.get("service-a", "data.profile.name"));
        Assert.assertNotNull(response.raw("service-a-a"));
        Assert.assertNull(response.get("service-a-a", "data.orders[0].amount"));
        Assert.assertNotNull(response.get("service-a-a", "data.orders[1].amount"));
        Assert.assertEquals(46, response.get("service-a-a", "data.orders[1].amount"));

        // TC 8 : thread error
        request = new ExecuteRequest("exists");
        http = null;
        s1 = new Signal("service-a", 1, http, Arrays.asList(), Arrays.asList());
        sg = new SignalGroup(Arrays.asList(s1));
        registry.add("exists", sg);
        Mockito.when(api.call(http)).thenThrow(IOException.class);
        try {
            signalExecutor.execute(request);
            Assert.fail();
        } catch (SignalExecutionError e) {}
    }

}
