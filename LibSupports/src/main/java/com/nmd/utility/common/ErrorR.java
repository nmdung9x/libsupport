package com.nmd.utility.common;

import com.nmd.utility.DebugLog;

import okhttp3.ResponseBody;

public class ErrorR {
    private ResponseBody body;
    private String content;
    private int code;
    private Throwable t;


    public ErrorR(String content, int code, Throwable t) {
        this.content = content;
        this.code = code;
        this.t = t;
    }

    public ErrorR(ResponseBody body, int code, Throwable t) {
        this.body = body;
        this.code = code;
        this.t = t;
        try {
            if (body != null) {
                content = body.string();
                DebugLog.loge(content);
            } else content = "";
        } catch (Exception e) {
            DebugLog.logi(e);
        }
    }

    public ResponseBody getBody() {
        return body;
    }

    public void setBody(ResponseBody body) {
        this.body = body;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Throwable getThrowable() {
        return t;
    }

    public void setThrowable(Throwable t) {
        this.t = t;
    }
}
