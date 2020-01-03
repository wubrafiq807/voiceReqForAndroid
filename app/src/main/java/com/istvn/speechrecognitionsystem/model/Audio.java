package com.istvn.speechrecognitionsystem.model;

public class Audio {
    private String phoneNo;
    private String path;
    private String dateTime;
    private String callType;
    private String duration;

    public Audio() {
    }

    public Audio(String phoneNo, String path, String dateTime, String callType, String duration) {
        this.phoneNo = phoneNo;
        this.path = path;
        this.dateTime = dateTime;
        this.callType = callType;
        this.duration = duration;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
