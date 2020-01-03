package com.istvn.speechrecognitionsystem.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class RecognitionListResponse {

    @SerializedName("result")
    private List<ResultList> result = null;
    @SerializedName("error")
    private Boolean error;
    @SerializedName("message")
    private String message;
    @SerializedName("code")
    private Integer code;

    public List<ResultList> getResult() {
        return result;
    }

    public void setResult(List<ResultList> result) {
        this.result = result;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
