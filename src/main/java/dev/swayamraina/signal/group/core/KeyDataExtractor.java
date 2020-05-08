package dev.swayamraina.signal.group.core;

import javafx.util.Pair;
import org.json.JSONObject;

public final class KeyDataExtractor {

    private static final String JSON_PATH_SPLITTER = "\\.";
    private static final String ARRAY_START = "[";
    private static final String ARRAY_END = "]";

    public Object extract (final String path, final JSONObject json) {
        if (null == path || null == json)
            throw new IllegalArgumentException();

        boolean invalid = false;
        JSONObject temp = json;
        String[] walk = path.split(JSON_PATH_SPLITTER);
        for (int i=0; i<walk.length-1; i++) {
            if (containsListOp(walk[i])) {
                Pair<String, Integer> pair = getListOp(walk[i]);
                invalid = (pair.getValue() >= temp.getJSONArray(pair.getKey()).length());
                if (invalid) break;
                temp = temp.getJSONArray(pair.getKey()).getJSONObject(pair.getValue());
                continue;
            }
            temp = temp.getJSONObject(walk[i]);
        }
        return invalid ? null : temp.get(walk[walk.length-1]);
    }


    private boolean containsListOp (String path) {
        if (null == path)
            throw new IllegalArgumentException();

        return path.contains(ARRAY_START) && path.contains(ARRAY_END);
    }


    private static Pair<String, Integer> getListOp (String path) {
        if (null == path)
            throw new IllegalArgumentException();

        int sptr = path.indexOf(ARRAY_START);
        int eptr = path.indexOf(ARRAY_END);
        Integer index = Integer.parseInt(path.substring(sptr+1, eptr));
        String key = path.substring(0, sptr);
        return new Pair<>(key, index);
    }

}
