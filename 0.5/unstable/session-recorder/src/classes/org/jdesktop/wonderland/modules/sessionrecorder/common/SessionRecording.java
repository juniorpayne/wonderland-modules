/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
package org.jdesktop.wonderland.modules.sessionrecorder.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class stores the data for the recording
 * 
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
@XmlRootElement(name="session-recording")
public class SessionRecording {
    public static final String DIR_NAME = "SessionRecordings";
    private String recordingName = "";
    private String id;
    private Date startTime;
    private Date endTime;
    private Date serverStartTime;
    private Date serverEndTime;
    private String studentName;
    private List<String> participants;
    private String tagsCS;
    private boolean transcribed;
    private String videoURL="";
    private String groupName="";
    private String audioURL;
    private boolean onlyAudio;
    private List<GestureData> gestureData = new ArrayList<GestureData>();
    private List<TranscribedData> transcribedData = new ArrayList<TranscribedData>();
    private List<SoundData> soundData = new ArrayList<SoundData>();
    private Map<String,String> userData = new HashMap<String, String>();

    public String getRecordingName() {
        return recordingName;
    }

    public void setRecordingName(String recordingName) {
        this.recordingName = recordingName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public List<String> getparticipants() {
        return participants;
    }

    public void setparticipants(List<String> participants) {
        this.participants = participants;
    }
    
    public Map<String, String> getUserData() {
        return userData;
    }
    
    public void setUserData(Map<String, String> userData) {
        this.userData = userData;
    }

    public String getTagsCS() {
        return tagsCS;
    }

    public void setTagsCS(String tagsCS) {
        this.tagsCS = tagsCS;
    }

    public boolean isTranscribed() {
        return transcribed;
    }

    public void setTranscribed(boolean transcribed) {
        this.transcribed = transcribed;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getAudioURL() {
        return audioURL;
    }

    public void setAudioURL(String audioURL) {
        this.audioURL = audioURL;
    }

    public List<GestureData> getGestureData() {
        return gestureData;
    }

    public void setGestureData(List<GestureData> gestureData) {
        this.gestureData = gestureData;
    }

    public List<TranscribedData> getTranscribedData() {
        return transcribedData;
    }

    public void setTranscribedData(List<TranscribedData> transcribedData) {
        this.transcribedData = transcribedData;
    }

    public List<SoundData> getSoundData() {
        return soundData;
    }

    public void setSoundData(List<SoundData> soundData) {
        this.soundData = soundData;
    }

    public boolean isOnlyAudio() {
        return onlyAudio;
    }

    public void setOnlyAudio(boolean onlyAudio) {
        this.onlyAudio = onlyAudio;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Date getServerStartTime() {
        return serverStartTime;
    }

    public void setServerStartTime(Date serverStartTime) {
        this.serverStartTime = serverStartTime;
    }

    public Date getServerEndTime() {
        return serverEndTime;
    }

    public void setServerEndTime(Date serverEndTime) {
        this.serverEndTime = serverEndTime;
    }
    
}
