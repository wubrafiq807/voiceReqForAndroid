package com.istvn.speechrecognitionsystem.model;

import com.google.gson.annotations.SerializedName;

public class ResultList {
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
    @SerializedName("user_id")
    private String userId;
    @SerializedName("record_type")
    private Integer recordType;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getRecordType() {
        return recordType;
    }

    public void setRecordType(Integer recordType) {
        this.recordType = recordType;
    }
}
