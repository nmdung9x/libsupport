package com.nmd.utility.common;

import com.nmd.utility.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonObj extends JSONObject {
    private final JSONObject jsonObject;

    public JsonObj(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JsonObj(String content) {
        this.jsonObject = JsonUtils.parseJsonObject(content);
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public Object getObjectByKey(String key) {
        return JsonUtils.getObjectInJsonObj(jsonObject, key);
    }

    public String getStringByKey(String key) {
        return JsonUtils.getStringInJsonObj(jsonObject, key);
    }

    public JsonObj getJsonObjByKey(Object key) {
        return new JsonObj(JsonUtils.getJSONObjectFromJSONObject(jsonObject, key));
    }

    public JSONObject getJSONObjectByKey(Object key) {
        return JsonUtils.getJSONObjectFromJSONObject(jsonObject, key);
    }

    public JSONArray getJSONArrayByKey(Object key) {
        return JsonUtils.getJSONArrayFromJSONObject(jsonObject, key);
    }

    public JsonObj putObj(String key, Object value) {
        return new JsonObj(JsonUtils.putObject(jsonObject, key, value));
    }

    public JSONObject putObject(String key, Object value) {
        return JsonUtils.putObject(jsonObject, key, value);
    }

    public String parseJsonToQuery() {
        return JsonUtils.parseJsonToQuery(jsonObject);
    }

    public ArrayList<String> getListKeyInJson() {
        return JsonUtils.getListKeyInJson(jsonObject);
    }
}
