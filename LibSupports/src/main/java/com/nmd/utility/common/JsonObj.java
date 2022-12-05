package com.nmd.utility.common;

import com.nmd.utility.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class JsonObj extends JSONObject {

    public JsonObj() {
    }

    public JsonObj(JSONObject jsonObject) {
        if (jsonObject == null) return;
        Iterator<String> keys = jsonObject.keys();
        while(keys.hasNext()) {
            try {
                String key = keys.next();
                put(key, jsonObject.get(key));
            } catch (Exception ignored) {  }
        }
    }

    public JsonObj(String content) {
        JSONObject jsonObject = JsonUtils.parseJsonObject(content);
        if (jsonObject == null) return;
        Iterator<String> keys = jsonObject.keys();
        while(keys.hasNext()) {
            try {
                String key = keys.next();
                put(key, jsonObject.get(key));
            } catch (Exception ignored) { }
        }
    }

    public Object getObjectByKey(String key) {
        return JsonUtils.getObjectInJsonObj(this, key);
    }

    public String getStringByKey(String key) {
        return JsonUtils.getStringInJsonObj(this, key);
    }

    public JsonObj getJsonObjByKey(Object key) {
        return new JsonObj(JsonUtils.getJSONObjectFromJSONObject(this, key));
    }

    public JSONObject getJSONObjectByKey(Object key) {
        return JsonUtils.getJSONObjectFromJSONObject(this, key);
    }

    public JSONArray getJSONArrayByKey(Object key) {
        return JsonUtils.getJSONArrayFromJSONObject(this, key);
    }

    public JsonObj putObj(String key, Object value) {
        return new JsonObj(JsonUtils.putObject(this, key, value));
    }

    public JSONObject putObject(String key, Object value) {
        return JsonUtils.putObject(this, key, value);
    }

    public String parseJsonToQuery() {
        return JsonUtils.parseJsonToQuery(this);
    }

    public ArrayList<String> getListKeyInJson() {
        return JsonUtils.getListKeyInJson(this);
    }
}
