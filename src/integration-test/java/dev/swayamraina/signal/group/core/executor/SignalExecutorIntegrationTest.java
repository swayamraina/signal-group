package dev.swayamraina.signal.group.core.executor;

import dev.swayamraina.signal.group.core.extractor.ExtractorKey;
import dev.swayamraina.signal.group.core.extractor.KeyDataExtractor;
import dev.swayamraina.signal.group.core.http.Api;
import dev.swayamraina.signal.group.core.http.Http;
import dev.swayamraina.signal.group.core.registry.Registry;
import dev.swayamraina.signal.group.core.signal.Signal;
import dev.swayamraina.signal.group.core.signal.SignalGroup;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;



public class SignalExecutorIntegrationTest {

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


    @Test public void testExecutionWithNoTimeout () {
        ExecuteRequest request; ExecuteResponse response;
        Signal s1, s2;  SignalGroup sg;  Http http;
        long timeout = TimeUnit.NANOSECONDS.convert(1000, TimeUnit.MILLISECONDS);

        ExecutorService e = Executors.newWorkStealingPool();

        http = null;  response = new ExecuteResponse();
        request = new ExecuteRequest("exists");
        s2 = new Signal("service-a", 1, http, Arrays.asList(), Arrays.asList(new ExtractorKey("data.profile.name")));
        s1 = new Signal("service-b", 1, http, Arrays.asList(s2), Arrays.asList(new ExtractorKey("data.profile.name")));
        sg = new SignalGroup(Arrays.asList(s1), timeout);
        registry.add("exists", sg);
        Mockito.when(api.call(http)).thenAnswer(invocation -> {
            Thread.sleep(100);
            return "{\"data\": {\"profile\": {\"name\": \"swayam\"}}}";
        });

        Future<ExecuteResponse> future = e.submit(() -> signalExecutor.execute(request, response));

        Set<String> consumed = new HashSet<>();
        short found = 0;
        do {
            if (response.available("service-a") && !consumed.contains("service-a")) {
                found++;
                consumed.add("service-a");
                Assert.assertEquals("swayam", response.get("service-a", "data.profile.name"));
            } else if (response.available("service-b") && !consumed.contains("service-b")) {
                found++;
                consumed.add("service-b");
                Assert.assertEquals("swayam", response.get("service-b", "data.profile.name"));
            }
        } while ((timeout > (System.nanoTime() - request.start())) && !future.isDone());
        if (found < 2) Assert.fail();
        long time = (System.nanoTime() - request.start());
        Assert.assertTrue(time <= timeout);
    }


    @Test public void testExecutionWithTimeout () {
        ExecuteRequest request; ExecuteResponse response;
        Signal s1, s2;  SignalGroup sg;  Http http;
        long timeout = TimeUnit.NANOSECONDS.convert(200, TimeUnit.MILLISECONDS);

        ExecutorService e = Executors.newWorkStealingPool();

        http = null;  response = new ExecuteResponse();
        request = new ExecuteRequest("exists");
        s2 = new Signal("service-a", 1, http, Arrays.asList(), Arrays.asList(new ExtractorKey("data.profile.name")));
        s1 = new Signal("service-b", 1, http, Arrays.asList(s2), Arrays.asList(new ExtractorKey("data.profile.name")));
        sg = new SignalGroup(Arrays.asList(s1), timeout);
        registry.add("exists", sg);
        Mockito.when(api.call(http)).thenAnswer(invocation -> {
            Thread.sleep(110);
            return "{\"data\": {\"profile\": {\"name\": \"swayam\"}}}";
        });

        Future<ExecuteResponse> future = e.submit(() -> signalExecutor.execute(request, response));

        Set<String> consumed = new HashSet<>();
        short found = 0;
        do {
            if (response.available("service-a") && !consumed.contains("service-a")) {
                found++;
                consumed.add("service-a");
                Assert.assertEquals("swayam", response.get("service-a", "data.profile.name"));
            } else if (response.available("service-b") && !consumed.contains("service-b")) {
                found++;
                consumed.add("service-b");
                Assert.assertEquals("swayam", response.get("service-b", "data.profile.name"));
            }
        } while ((timeout > (System.nanoTime() - request.start())) && !future.isDone());
        future.cancel(false);
        if (found == 2) Assert.fail();
        Assert.assertEquals(1, found);
        Assert.assertEquals("swayam", response.get("service-b", "data.profile.name"));
        long time = (System.nanoTime() - request.start());
        Assert.assertTrue(time > timeout);
    }


}
