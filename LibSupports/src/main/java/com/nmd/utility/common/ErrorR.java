package com.nmd.utility.common;

import com.nmd.utility.DebugLog;

import lombok.Getter;
import lombok.Setter;
import okhttp3.ResponseBody;

@Getter
@Setter
public class ErrorR {
    private ResponseBody body;
    private String content;
    private int code;
    private Throwable t;

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
}
