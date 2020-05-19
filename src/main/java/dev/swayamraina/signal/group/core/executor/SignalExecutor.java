package dev.swayamraina.signal.group.core.executor;

import dev.swayamraina.signal.group.core.extractor.KeyDataExtractor;
import dev.swayamraina.signal.group.core.errors.SignalExecutionError;
import dev.swayamraina.signal.group.core.http.Api;
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


    public ExecuteResponse execute (final ExecuteRequest request) {
        if (null == request) throw new IllegalArgumentException("null request received");
        SignalGroup sg = registry.get(request.uid());
        ExecuteResponse response = new ExecuteResponse();
        if (null == sg.signals() || 0 == sg.signals().size()) return response;
        Map<Signal, Future<String>> results = new ConcurrentHashMap<>();
        Map<Signal, Future<String>> completed;
        for (Signal s : sg.signals()) {
            Future<String> f = tpe.submit(() -> api.call(s.http()));
            results.put(s, f);
        }
        while (!results.isEmpty()) {
            completed = check(results);
            if (null != completed && 0 != completed.size()) {
                for (Map.Entry<Signal, Future<String>> c : completed.entrySet()) {
                    refreshResponse(c, response);
                    results.putAll(execute0(c.getKey()));
                }
            }
        }
        return response;
    }


    private Map<Signal, Future<String>> execute0 (final Signal sg) {
        Map<Signal, Future<String>> results = new ConcurrentHashMap<>();
        Iterator<Signal> iterator = sg.children().iterator();
        while (iterator.hasNext()) {
            Signal s = iterator.next();
            Future<String> f = tpe.submit(() -> api.call(s.http()));
            results.put(s, f);
        }
        return results;
    }


    private Map<Signal, Future<String>> check (final Map<Signal, Future<String>> results) {
        boolean init = false;
        Map<Signal, Future<String>> completed = null;
        Iterator<Map.Entry<Signal, Future<String>>> iterator = results.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Signal, Future<String>> entry = iterator.next();
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


    private void refreshResponse (Map.Entry<Signal, Future<String>> completed, ExecuteResponse response) {
        String raw;
        try {
            raw = completed.getValue().get();
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
