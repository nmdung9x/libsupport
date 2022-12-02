package com.nmd.utility.common;

import com.nmd.utility.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonArray extends JSONArray {
    private final JSONArray jsonArray;

    public JsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    public JsonArray(String content) {
        this.jsonArray = JsonUtils.parseJsonArray(content);
    }

    public JSONArray getJSONArray() {
        return jsonArray;
    }

    public ArrayList<Object> getListObject() {
        return JsonUtils.getListObjectInJsonArray(jsonArray);
    }

    public ArrayList<String> getListString() {
        return JsonUtils.getListStringInJsonArray(jsonArray);
    }

    public Object getObject(int i) {
        return JsonUtils.getObjectInJsonArray(jsonArray, i);
    }

    public String getString(int i) {
        return JsonUtils.getStringInJsonArray(jsonArray, i);
    }

    public String getString(int i, String keyJsonObj) {
        return JsonUtils.getStringInJsonArray(jsonArray, i, keyJsonObj);
    }

    public JsonArray remove(int pos) {
        return new JsonArray(JsonUtils.JSONArrayRemove(jsonArray, pos));
    }

    public JSONArray removeItem(int pos) {
        return JsonUtils.JSONArrayRemove(jsonArray, pos);
    }

    public ArrayList<JSONObject> parseToArrayListJSON() {
        return JsonUtils.parseJSONArrayToArrayListJSON(jsonArray);
    }

    public JsonObj getJsonObj(int pos) {
        return new JsonObj(JsonUtils.getJSONObjectFromJSONArray(jsonArray, pos));
    }

    public JSONObject getJSONObject(int pos) {
        return JsonUtils.getJSONObjectFromJSONArray(jsonArray, pos);
    }

    public JsonArray addAll(JSONArray jsonArray2) {
        return new JsonArray(JsonUtils.addAll(jsonArray, jsonArray2));
    }

    public JsonArray addAll(JsonArray jsonArray2) {
        return new JsonArray(JsonUtils.addAll(jsonArray, jsonArray2));
    }

    public JSONArray addAllArray(JSONArray jsonArray2) {
        return JsonUtils.addAll(jsonArray, jsonArray2);
    }

}
