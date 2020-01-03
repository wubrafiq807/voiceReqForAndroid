package com.istvn.speechrecognitionsystem.utility;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

public class FileAnalyser {

    private String fileName;

    /**
     * default constructor
     * @param fileName: audio file name
     */
    public FileAnalyser(String fileName){
        this.fileName = fileName;
    }

    /**
     * Substring phone number from file name
     * @return phone number
     */
    public String getPhoneNo(){
        return fileName.substring(fileName.indexOf("_", 7) + 1, fileName.lastIndexOf("_"));
    }

    /**
     * @param date: audio date
     * @return date time in string format
     */
    public String getDateTime(String date){
        return date +" "+ getTime();
    }

    /**
     * Substring time from audio name
     * @return time
     */
    public String getTime(){
        String time = fileName.substring(fileName.lastIndexOf("_") + 1, fileName.lastIndexOf("."));
        return time.substring(0, 2) + ":" + time.substring(2, 4);
    }

    /**
     * Substring call type from audio file name
     * @return call type string
     */
    public String getCallType(){

        if (fileName.contains("MANUAL"))
            return "MANUAL";

        return fileName.contains("IN") ? "IN" : "OUT";
    }

    /**
     * Calculate total audio duration
     * @param context: current context
     * @param length: total audio length
     * @param path: audio path
     * @return duration in string format
     */
    public String getAudioDuration(Context context, long length, String path) {

        String totalTime = null;
        String duration = "0 sec";

        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        if (length > 10) {
            try {
                mediaMetadataRetriever.setDataSource(context, Uri.parse(path));
                totalTime = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mediaMetadataRetriever.release();
            }

            if (totalTime != null) {
                long timeInMilliSec = Long.parseLong(totalTime);
                long durationInSecond = timeInMilliSec / 1000;
                long hours = durationInSecond / 3600;
                long minutes = (durationInSecond - hours * 3600) / 60;
                long seconds = durationInSecond - (hours * 3600 + minutes * 60);

                if (seconds > 0) {
                    duration = seconds + " sec ";
                }
                if (minutes > 0) {
                    duration = minutes + " min " + duration;
                }
                if (hours > 0) {
                    duration = hours + " hr " + duration;
                }
            }
        }
        return duration;
    }
}
