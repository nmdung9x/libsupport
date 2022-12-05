package com.nmd.utility.common;

import com.nmd.utility.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonArray extends JSONArray {

    public JsonArray() {
    }
    
    public JsonArray(JSONArray jsonArray) {
        try {
            if (jsonArray == null) return;
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    put(jsonArray.get(i));
                }
            }
        } catch (Exception ignored) { }
    }

    public JsonArray(String content) {
        JSONArray jsonArray = JsonUtils.parseJsonArray(content);
        try {
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    put(jsonArray.get(i));
                }
            }
        } catch (Exception ignored) { }
    }

    public ArrayList<Object> getListObject() {
        return JsonUtils.getListObjectInJsonArray(this);
    }

    public ArrayList<String> getListString() {
        return JsonUtils.getListStringInJsonArray(this);
    }

    public Object getObject(int i) {
        return JsonUtils.getObjectInJsonArray(this, i);
    }

    public String getString(int i) {
        return JsonUtils.getStringInJsonArray(this, i);
    }

    public String getString(int i, String keyJsonObj) {
        return JsonUtils.getStringInJsonArray(this, i, keyJsonObj);
    }

    public JsonArray remove(int pos) {
        return new JsonArray(JsonUtils.JSONArrayRemove(this, pos));
    }

    public JSONArray removeItem(int pos) {
        return JsonUtils.JSONArrayRemove(this, pos);
    }

    public ArrayList<JSONObject> parseToArrayListJSON() {
        return JsonUtils.parseJSONArrayToArrayListJSON(this);
    }

    public JsonObj getJsonObj(int pos) {
        return new JsonObj(JsonUtils.getJSONObjectFromJSONArray(this, pos));
    }

    public JSONObject getJSONObject(int pos) {
        return JsonUtils.getJSONObjectFromJSONArray(this, pos);
    }

    public JsonArray addAll(JSONArray this2) {
        return new JsonArray(JsonUtils.addAll(this, this2));
    }

    public JsonArray addAll(JsonArray this2) {
        return new JsonArray(JsonUtils.addAll(this, this2));
    }

    public JSONArray addAllArray(JSONArray this2) {
        return JsonUtils.addAll(this, this2);
    }

}
