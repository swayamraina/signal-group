package dev.swayamraina.signal.group.core.executor;

import dev.swayamraina.signal.group.core.extractor.KeyDataExtractor;
import dev.swayamraina.signal.group.core.errors.SignalExecutionError;
import dev.swayamraina.signal.group.core.http.Api;
import dev.swayamraina.signal.group.core.http.response.Response;
import dev.swayamraina.signal.group.core.registry.Registry;
import dev.swayamraina.signal.group.core.signal.Signal;
import dev.swayamraina.signal.group.core.signal.SignalGroup;
import dev.swayamraina.signal.group.core.extractor.ExtractorKey;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;



public final class SignalExecutor {

    private Registry registry;
    private Api api;
    private KeyDataExtractor extractor;

    private ExecutorService tpe;


    public ExecuteResponse execute (ExecuteRequest request) {
        ExecuteResponse response = new ExecuteResponse();
        return execute (request, response);
    }


    public ExecuteResponse execute (ExecuteRequest request, ExecuteResponse response) {
        if (null == response) throw new IllegalStateException ("response cannot be null");
        response = execute0 (request, response);
        return response;
    }


    private ExecuteResponse execute0 (ExecuteRequest request, ExecuteResponse response) {
        if (null == request) throw new IllegalArgumentException("null request received");
        SignalGroup sg = registry.get(request.uid());
        if (null == sg.signals() || 0 == sg.signals().size()) return response;
        Map<Signal, Future<Response>> results = new ConcurrentHashMap<>();
        Map<Signal, Future<Response>> completed;
        for (Signal s : sg.signals()) {
            Future<Response> f = tpe.submit(() -> api.call(s.http()));
            results.put(s, f);
        }
        while (!results.isEmpty()) {
            completed = check (results);
            if (null != completed && 0 != completed.size()) {
                for (Map.Entry<Signal, Future<Response>> c : completed.entrySet()) {
                    refreshResponse(c, response);
                    results.putAll(executeSignal(c.getKey(), response));
                }
            }
        }
        return response;
    }


    private Map<Signal, Future<Response>> executeSignal (final Signal sg, final ExecuteResponse response) {
        Map<Signal, Future<Response>> results = new ConcurrentHashMap<>();
        Iterator<Signal> iterator = sg.children().iterator();
        while (iterator.hasNext()) {
            Signal s = iterator.next();
            if (!response.available(sg.name())) {
                Future<Response> f = tpe.submit(() -> api.call(s.http()));
                results.put(s, f);
            }
        }
        return results;
    }


    private Map<Signal, Future<Response>> check (final Map<Signal, Future<Response>> results) {
        boolean init = false;
        Map<Signal, Future<Response>> completed = null;
        Iterator<Map.Entry<Signal, Future<Response>>> iterator = results.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Signal, Future<Response>> entry = iterator.next();
            if (entry.getValue().isDone()) {
                if (!init) {
                    init = true;
                    completed = new ConcurrentHashMap<>();
                }
                results.remove(entry.getKey());
                completed.put(entry.getKey(), entry.getValue());
            }
        }
        return completed;
    }


    private void refreshResponse (Map.Entry<Signal, Future<Response>> completed, ExecuteResponse response) {
        String raw;
        try {
            raw = completed.getValue().get().content();
        } catch (InterruptedException | ExecutionException e) {
            throw new SignalExecutionError(e.getMessage(), e);
        }
        response.add(completed.getKey().name(), raw);
        for (ExtractorKey ek : completed.getKey().keys()) {
            response.add (
                    completed.getKey().name(),
                    ek.path(),
                    extractor.extract(ek.path(), raw)
            );
        }
        response.markAvailable(completed.getKey().name());
    }






    public SignalExecutor (
            Registry registry,
            Api api,
            KeyDataExtractor extractor) {

        this.registry = registry;
        this.api = api;
        this.extractor = extractor;
        this.tpe = Executors.newWorkStealingPool();
    }

    public SignalExecutor (
            Registry registry,
            Api api,
            KeyDataExtractor extractor,
            ExecutorService tpe) {

        this.registry = registry;
        this.api = api;
        this.extractor = extractor;
        this.tpe = tpe;
    }

}
