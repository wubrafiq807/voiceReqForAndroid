package com.istvn.speechrecognitionsystem.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("result")
    private ResponseResult result = null;
    @SerializedName("error")
    private Boolean error;
    @SerializedName("message")
    private String message;
    @SerializedName("code")
    private Integer code;

    public ResponseResult getResult() {
        return result;
    }

    public void setResult(ResponseResult result) {
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

    public class ResponseResult {

        @SerializedName("user_id")
        private String userId;
        @SerializedName("name")
        private String name;
        @SerializedName("email")
        private String email;
        @SerializedName("phone")
        private String phone;
        @SerializedName("password")
        private String password;
        @SerializedName("login_type")
        private Integer loginType;
        @SerializedName("login_counter")
        private Integer loginCounter;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Integer getLoginType() {
            return loginType;
        }

        public void setLoginType(Integer loginType) {
            this.loginType = loginType;
        }

        public Integer getLoginCounter() {
            return loginCounter;
        }

        public void setLoginCounter(Integer loginCounter) {
            this.loginCounter = loginCounter;
        }

    }

}
