package com.nmd.utility;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nmd.utility.other.Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

public class JsonUtils {

    public static Object getObjectInJsonObj(JSONObject jsonObject, String key) {
        if (jsonObject == null) return null;
        if (key == null) return null;
        if (key.trim().isEmpty()) return null;
        if (jsonObject.has(key)) {
            try {
                return jsonObject.get(key);
            } catch (JSONException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public static String getStringInJsonObj(JSONObject jsonObject, String key) {
        if (jsonObject == null) return "";
        if (key == null) return "";
        if (key.trim().isEmpty()) return "";
        Object result = getObjectInJsonObj(jsonObject, key);
        if (result != null) return String.valueOf(result);
        return "";
    }

    public static ArrayList<Object> getListObjectInJsonArray(JSONArray jsonArray) {
        ArrayList<Object> arrayList = new ArrayList<Object>();
        int length = jsonArray.length();
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                try {
                    arrayList.add(jsonArray.get(i));
                } catch (JSONException e) {
                    arrayList.add("");
                }
            }
        }

        return arrayList;
    }

    public static ArrayList<String> getListStringInJsonArray(JSONArray jsonArray) {
        ArrayList<String> arrayList = new ArrayList<String>();
        int length = jsonArray.length();
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                try {
                    arrayList.add(String.valueOf(jsonArray.get(i)));
                } catch (JSONException e) {
                    arrayList.add("");
                }
            }
        }

        return arrayList;
    }

    public static Object getObjectInJsonArray(JSONArray jsonArray, int i) {
        try {
            return jsonArray.get(i);
        } catch (JSONException e) {
            return null;
        }
    }

    public static String getStringInJsonArray(JSONArray jsonArray, int i) {
        try {
            return String.valueOf(jsonArray.get(i));
        } catch (JSONException e) {
            return "";
        }
    }

    public static String getStringInJsonArray(JSONArray jsonArray, int i, String keyJsonObj) {
        try {
            if (jsonArray.getJSONObject(i).has(keyJsonObj)) {
                return String.valueOf(jsonArray.getJSONObject(i).get(keyJsonObj));
            }
        } catch (JSONException e) {
        }
        return "";
    }

    public static ArrayList<String> parseJSONArrayStringToArray(String data) {
        ArrayList<String> arr = new ArrayList<String>();
        try {
            JSONArray ja = new JSONArray(data);
            if (ja.length() > 0) {
                for (int i = 0; i < ja.length(); i++) {
                    arr.add(ja.get(i).toString());
                }
            }
        } catch (Exception e) {
            DebugLog.loge(e);
        }
        return arr;
    }

    public static String parseJSONArrayStringToString(String data) {
        StringBuilder builder = new StringBuilder();
        try {
            JSONArray ja = new JSONArray(data);
            if (ja.length() > 0) {
                for (int i = 0; i < ja.length(); i++) {
                    if (i == 0) {
                        builder.append(ja.get(i).toString());
                    } else {
                        builder.append(";").append(ja.get(i).toString());
                    }
                }
            }
        } catch (Exception e) {
            DebugLog.loge(e);
        }
        return builder.toString();
    }

    public static JSONArray JSONArrayRemove(JSONArray ja, int pos) {
        JSONArray result = new JSONArray();
        if (ja != null) {
            for (int i = 0; i < ja.length(); i++) {
                if (i != pos) {
                    try {
                        result.put(ja.getJSONObject(i));
                    } catch (JSONException e) {
                        DebugLog.loge(e);
                    }
                }
            }
        }
        return result;
    }

    public static JSONObject getJSONObjectFromJSONObject(JSONObject obj, Object key) {
        if (obj == null) return null;
        if (key == null) return null;
        try {
            return obj.has(String.valueOf(key)) ? obj.getJSONObject(String.valueOf(key)) : null;
        } catch (Exception e) {
            DebugLog.loge(e);
        }
        return null;
    }

    public static JSONArray getJSONArrayFromJSONObject(JSONObject obj, Object key) {
        if (obj == null) return new JSONArray();
        if (key == null) return null;
        try {
            return obj.has(String.valueOf(key)) ? obj.getJSONArray(String.valueOf(key)) : new JSONArray();
        } catch (Exception e) {
            DebugLog.logi(e);
        }
        return new JSONArray();
    }

    public static ArrayList<JSONObject> parseJSONArrayToArrayListJSON(JSONArray jsonArray) {
        if (jsonArray == null) return new ArrayList<>();
        try {
            if (jsonArray.length() > 0) {
                ArrayList<JSONObject> results = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    results.add(jsonArray.getJSONObject(i));
                }
                return results;
            }
        } catch (Exception e) {
            DebugLog.loge(e);
        }
        return new ArrayList<>();
    }

    public static JSONObject putObject(JSONObject jsonObject, String key, Object value) {
        try {
            if (jsonObject != null) {
                jsonObject.put(key, value);
            }
        } catch (Exception e) { DebugLog.logi(e); }
        return jsonObject;
    }

    public static JSONObject getJSONObjectFromJSONArray(JSONArray array, int pos) {
        try {
            if (pos < array.length()) {
                return array.getJSONObject(pos);
            }
        } catch (Exception e) { DebugLog.logi(e); }
        return null;
    }

    public static JSONArray addAll(JSONArray jsonArray, JSONArray jsonArray2) {
        try {
            if (jsonArray2.length() > 0) {
                for (int i = 0; i < jsonArray2.length(); i++) {
                    jsonArray.put(jsonArray2.get(i));
                }
            }
        } catch (Exception e) { DebugLog.logi(e); }
        return jsonArray;
    }

    public static String convertArrayListDataToJson(ArrayList<Data> arrayList) {
        JSONObject jsonParam = new JSONObject();
        try {
            if (arrayList.size() > 0) {
                for (int i = 0; i < arrayList.size(); i++) {
                    jsonParam.put(arrayList.get(i).getKey(), arrayList.get(i).getValue());
                }
                return jsonParam.toString();
            }
        } catch (Exception e) {
        }
        return "";
    }

    public static JSONObject parseQueryToJson(String query) {
        JSONObject object = new JSONObject();
        try {
            String tmp = query.trim().replaceAll("\\?", "");
            if (!tmp.isEmpty()) {
                if (tmp.contains("&")) {
                    ArrayList<String> arrayList = UtilLibs.splitComme2(tmp, "&");
                    if (arrayList.size() > 0) {
                        for (String item : arrayList) {
                            if (item.contains("=")) {
                                String[] arr = UtilLibs.splitComme(item, "=");
                                if (arr.length == 2) {
                                    object.put(arr[0], arr[1]);
                                }
                            }
                        }
                    }
                } else {
                    if (tmp.contains("=")) {
                        String[] arr = UtilLibs.splitComme(tmp, "=");
                        if (arr.length == 2) {
                            object.put(arr[0], arr[1]);
                        }
                    }
                }
            }
        } catch (Exception e) {
            DebugLog.loge(e);
        }
        return object;
    }

    public static String parseJsonToQuery(JSONObject jsonObject) {
        StringBuilder result = new StringBuilder();
        try {
            Iterator<String> keys = jsonObject.keys();

            while(keys.hasNext()) {
                String key = keys.next();
                if (!result.toString().trim().isEmpty()) {
                    result.append("&");
                }
                result.append(key);
                result.append("=");
                result.append(getStringInJsonObj(jsonObject, key));
            }
        } catch (Exception e) {
            DebugLog.loge(e);
        }
        return result.toString();
    }

    public static int checkPosition(ArrayList<JSONObject> arrayList, String check, String key) {
        if (check.trim().isEmpty()) return -1;
        int selected = 0;
        boolean isSelected = false;

        if (check.length() > 0) {
            for (JSONObject obj : arrayList) {
                String title = UtilLibs.getStringInJsonObj(obj, key).trim();

                if (title.equals(check.trim())) {
                    isSelected = true;
                    break;
                }
                selected++;
            }
        }
        if (!isSelected) selected = -1;
        return selected;
    }

    //TODO ======================== NEW ========================

    public static <T> Object toObject(String s, Class<T> t) {
        if (s != null) {
            Gson gson = new Gson();
            return gson.fromJson(s, t);
        }
        return null;
    }

    public static <T> JSONObject toJsonFromList(ArrayList<T> list) {
        Gson gson = new Gson();
        try {
            Type type = new TypeToken<ArrayList<T>>() {}.getType();
            return new JSONObject(gson.toJson(list, type));
        } catch (Exception e) { DebugLog.logi(e); }
        return new JSONObject();
    }


    public static <T> ArrayList<T> toListFromJSONArray(String json, Class<T> clazz) {
        ArrayList<T> lst = new ArrayList<T>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i ++) {
                lst.add(new Gson().fromJson(jsonArray.get(i).toString(), clazz));
            }
        } catch (Exception e) { DebugLog.logi(e); }
        return lst;
    }
}
