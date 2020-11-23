/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
package org.jdesktop.wonderland.modules.sessionrecorder.common;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class provides the basic data for the video like Max length and the 
 * group information of the vimeo
 * 
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
@XmlRootElement(name="session-recorder-configuration")
public class SessionRecordingConfiguration {
    /**
     * map for key = GroupName,value = Password
     */
    private Map<String, GroupData> groupData = new HashMap<String, GroupData>();
    private int MAX_LENGTH = 1;
    private String vimeoToken = "";
    private String defaultGroup = "";
    private String ffmpegPath = "";

    public Map<String, GroupData> getGroupData() {
        return groupData;
    }

    public void setGroupData(Map<String, GroupData> groupData) {
        this.groupData = groupData;
    }

    public int getMAX_LENGTH() {
        return MAX_LENGTH;
    }

    public void setMAX_LENGTH(int MAX_LENGTH) {
        this.MAX_LENGTH = MAX_LENGTH;
    }

    public String getVimeoToken() {
        return vimeoToken;
    }

    public void setVimeoToken(String VimeoToken) {
        this.vimeoToken = VimeoToken;
    }

    public String getDefaultGroup() {
        return defaultGroup;
    }

    public void setDefaultGroup(String defaultGroup) {
        this.defaultGroup = defaultGroup;
    }

    public String getFfmpegPath() {
        return ffmpegPath;
    }

    public void setFfmpegPath(String ffmpegPath) {
        this.ffmpegPath = ffmpegPath;
    }
    
}
