package dev.swayamraina.signal.group.core;

import dev.swayamraina.signal.group.core.extractor.KeyDataExtractor;
import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.internal.WhiteboxImpl;


public class KeyDataExtractorTest {

    private KeyDataExtractor keyDataExtractor;


    @Before public void setup () {
        keyDataExtractor = new KeyDataExtractor();
    }


    @Test public void testContainsListOp () throws Exception {
        String method = "containsListOp";
        String path; boolean containsListOp;

        // TC 1 : null path
        path = null;
        try {
            WhiteboxImpl.invokeMethod(keyDataExtractor, method, path);
            Assert.fail();
        } catch (IllegalArgumentException e) {}

        // TC 2 : empty path
        path = "";
        containsListOp = WhiteboxImpl.invokeMethod(keyDataExtractor, method, path);
        Assert.assertFalse(containsListOp);

        // TC 3 : list op with wildcard
        path = "data.order[*].failed";
        containsListOp = WhiteboxImpl.invokeMethod(keyDataExtractor, method, path);
        Assert.assertTrue(containsListOp);

        // TC 4 : list op with index
        path = "data.order[0].amount";
        containsListOp = WhiteboxImpl.invokeMethod(keyDataExtractor, method, path);
        Assert.assertTrue(containsListOp);

        // TC 5 : list op with multi wildcard
        path = "data.order[*].item[*].instant";
        containsListOp = WhiteboxImpl.invokeMethod(keyDataExtractor, method, path);
        Assert.assertTrue(containsListOp);

        // TC 6 : list op with multi index
        path = "data.order[0].item[3].instant";
        containsListOp = WhiteboxImpl.invokeMethod(keyDataExtractor, method, path);
        Assert.assertTrue(containsListOp);

        // TC 7 : list op with index and wildcard
        path = "data.order[0].item[*].instant";
        containsListOp = WhiteboxImpl.invokeMethod(keyDataExtractor, method, path);
        Assert.assertTrue(containsListOp);
    }


    @Test public void testGetListOp () throws Exception {
        String key; Pair<String, Integer> pair;
        String method = "getListOp";

        // TC 1 : null path
        key = null;
        try {
            WhiteboxImpl.invokeMethod(keyDataExtractor, method, key);
            Assert.fail();
        } catch (IllegalArgumentException e) {}

        // TC 2 : single digit array
        key = "profile[0]";
        pair = WhiteboxImpl.invokeMethod(keyDataExtractor, method, key);
        Assert.assertEquals("profile", pair.getKey());
        Assert.assertEquals(0, pair.getValue().intValue());

        // TC 3 : two digit array
        key = "data[11]";
        pair = WhiteboxImpl.invokeMethod(keyDataExtractor, method, key);
        Assert.assertEquals("data", pair.getKey());
        Assert.assertEquals(11, pair.getValue().intValue());

        // TC 4 : invalid array key
        key = "profile";
        try {
            WhiteboxImpl.invokeMethod(keyDataExtractor, method, key);
            Assert.fail();
        } catch (StringIndexOutOfBoundsException e) {}

    }


    @Test public void testExtract () {
        String path, json;
        Object value;

        // TC 1 : null path
        path = null;  json = null;
        try {
            keyDataExtractor.extract(path, json);
            Assert.fail();
        } catch (IllegalArgumentException e) {}

        // TC 2 : null json
        path = "";  json = null;
        try {
            keyDataExtractor.extract(path, json);
            Assert.fail();
        } catch (IllegalArgumentException e) {}

        // TC 3 : simple path
        path = "data.profile.name";
        json = "{\"data\": {\"profile\": {\"name\":\"swayam\", \"age\":26}}}";
        value = keyDataExtractor.extract(path, json);
        Assert.assertEquals("swayam", value);

        // TC 4 : array index path - start
        path = "data.profile[0].name";
        json = "{\"data\": {\"profile\": [{\"name\":\"swayam\", \"age\":26}, {\"name\":\"ujjwal\", \"age\":25}]}}";
        value = keyDataExtractor.extract(path, json);
        Assert.assertEquals("swayam", value);

        // TC 5 : array index path - end
        path = "data.profile[1].age";
        json = "{\"data\": {\"profile\": [{\"name\":\"swayam\", \"age\":26}, {\"name\":\"ujjwal\", \"age\":25}]}}";
        value = keyDataExtractor.extract(path, json);
        Assert.assertEquals(25, value);

        // TC 6 : array index path - not exists
        path = "data.profile[2].name";
        json = "{\"data\": {\"profile\": [{\"name\":\"swayam\", \"age\":26}, {\"name\":\"ujjwal\", \"age\":25}]}}";
        value = keyDataExtractor.extract(path, json);
        Assert.assertNull(value);

        // TC 7 : array path
        path = "data.profile";
        json = "{\"data\": {\"profile\": [{\"name\":\"swayam\", \"age\":26}, {\"name\":\"ujjwal\", \"age\":25}]}}";
        value = keyDataExtractor.extract(path, json);
        Assert.assertTrue(value instanceof JSONArray);
        Assert.assertEquals(2, ((JSONArray) value).length());
        Assert.assertTrue(((JSONArray) value).get(0) instanceof JSONObject);
        Assert.assertEquals(26, ((JSONObject)((JSONArray) value).get(0)).get("age"));
        Assert.assertEquals("ujjwal", ((JSONObject)((JSONArray) value).get(1)).get("name"));
    }

}
