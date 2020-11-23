/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */

package org.jdesktop.wonderland.modules.sessionrecorder.common;

import java.util.Date;

/**
 * This class is used to save the transcription information
 * 
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
public class TranscribedData {
    
    private String transcriptionId;
    private Date startTime;
    private Date endTime;
    private String username;
    private String transcriptionNote;
    private int top;

    public String getTranscriptionId() {
        return transcriptionId;
    }

    public void setTranscriptionId(String transcriptionId) {
        this.transcriptionId = transcriptionId;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTranscriptionNote() {
        return transcriptionNote;
    }

    public void setTranscriptionNote(String transcriptionNote) {
        this.transcriptionNote = transcriptionNote;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }
    
}
