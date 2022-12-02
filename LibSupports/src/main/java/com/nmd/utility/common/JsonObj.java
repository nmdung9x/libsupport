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

    public Object getObjectInJsonObj(String key) {
        return JsonUtils.getObjectInJsonObj(jsonObject, key);
    }

    public String getStringInJsonObj(String key) {
        return JsonUtils.getStringInJsonObj(jsonObject, key);
    }

    public JSONObject getJSONObjectFromJSONObject(Object key) {
        return JsonUtils.getJSONObjectFromJSONObject(jsonObject, key);
    }

    public JSONArray getJSONArrayFromJSONObject(Object key) {
        return JsonUtils.getJSONArrayFromJSONObject(jsonObject, key);
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
