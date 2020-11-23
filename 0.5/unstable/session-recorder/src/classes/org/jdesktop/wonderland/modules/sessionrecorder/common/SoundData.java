/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */

package org.jdesktop.wonderland.modules.sessionrecorder.common;

import java.util.Date;

/**
 *
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
public class SoundData {
    private Date startTime;
    private Date stopTime;
    private String audioName;
    private String cellId;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStopTime() {
        return stopTime;
    }

    public void setStopTime(Date stopTime) {
        this.stopTime = stopTime;
    }

    public String getAudioName() {
        return audioName;
    }

    public void setAudioName(String audioName) {
        this.audioName = audioName;
    }

    public String getCellId() {
        return cellId;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
    }
    
}
