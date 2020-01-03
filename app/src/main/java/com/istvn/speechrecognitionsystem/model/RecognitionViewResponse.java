package com.istvn.speechrecognitionsystem.model;

import com.google.gson.annotations.SerializedName;

public class RecognitionViewResponse {

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
        @SerializedName("voice_req_id")
        private String voiceReqId;
        @SerializedName("text")
        private String text;
        @SerializedName("audio_file_name")
        private String audioFileName;
        @SerializedName("record_start_time")
        private String recordStartTime;
        @SerializedName("record_end_time")
        private String recordEndTime;
        @SerializedName("created_date")
        private String createdDate;
        @SerializedName("record_type")
        private Integer recordType;
        @SerializedName("phones")
        private String phones;
        @SerializedName("names")
        private String names;
        @SerializedName("emails")
        private String emails;
        @SerializedName("caller_phone_no")
        private String callerPhoneNo;
        @SerializedName("receiver_phone_no")
        private String receiverPhoneNo;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getVoiceReqId() {
            return voiceReqId;
        }

        public void setVoiceReqId(String voiceReqId) {
            this.voiceReqId = voiceReqId;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getAudioFileName() {
            return audioFileName;
        }

        public void setAudioFileName(String audioFileName) {
            this.audioFileName = audioFileName;
        }

        public String getRecordStartTime() {
            return recordStartTime;
        }

        public void setRecordStartTime(String recordStartTime) {
            this.recordStartTime = recordStartTime;
        }

        public String getRecordEndTime() {
            return recordEndTime;
        }

        public void setRecordEndTime(String recordEndTime) {
            this.recordEndTime = recordEndTime;
        }

        public String getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        public Integer getRecordType() {
            return recordType;
        }

        public void setRecordType(Integer recordType) {
            this.recordType = recordType;
        }

        public String getPhones() {
            return phones;
        }

        public void setPhones(String phones) {
            this.phones = phones;
        }

        public String getNames() {
            return names;
        }

        public void setNames(String names) {
            this.names = names;
        }

        public String getEmails() {
            return emails;
        }

        public void setEmails(String emails) {
            this.emails = emails;
        }

        public String getCallerPhoneNo() {
            return callerPhoneNo;
        }

        public void setCallerPhoneNo(String callerPhoneNo) {
            this.callerPhoneNo = callerPhoneNo;
        }

        public String getReceiverPhoneNo() {
            return receiverPhoneNo;
        }

        public void setReceiverPhoneNo(String receiverPhoneNo) {
            this.receiverPhoneNo = receiverPhoneNo;
        }
    }


}
