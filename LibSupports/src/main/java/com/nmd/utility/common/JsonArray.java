package com.nmd.utility.common;

import com.nmd.utility.DebugLog;
import com.nmd.utility.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonArray extends JSONArray {

    public ArrayList<Object> getListObjectInJsonArray() {
        return JsonUtils.getListObjectInJsonArray(this);
    }

    public ArrayList<String> getListStringInJsonArray() {
        return JsonUtils.getListStringInJsonArray(this);
    }

    public Object getObjectInJsonArray(int i) {
        return JsonUtils.getObjectInJsonArray(this, i);
    }

    public String getStringInJsonArray(int i) {
        return JsonUtils.getStringInJsonArray(this, i);
    }

    public String getStringInJsonArray(JSONArray jsonArray, int i, String keyJsonObj) {
        return JsonUtils.getStringInJsonArray(this, i, keyJsonObj);
    }

    public JSONArray JSONArrayRemove(int pos) {
        return JsonUtils.JSONArrayRemove(this, pos);
    }

    public ArrayList<JSONObject> parseJSONArrayToArrayListJSON() {
        return JsonUtils.parseJSONArrayToArrayListJSON(this);
    }

    public JSONObject getJSONObjectFromJSONArray(int pos) {
        return JsonUtils.getJSONObjectFromJSONArray(this, pos);
    }

    public JSONArray addAll(JSONArray jsonArray2) {
        return JsonUtils.addAll(this, jsonArray2);
    }

}
