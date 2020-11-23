/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
package org.jdesktop.wonderland.modules.sessionrecorder.common;

import java.util.List;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;

/**
 *
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
public class SessionRecorderCellMessage extends CellMessage {

    private SessionRecorderCellMessage(CellID cellID) {
        super(cellID);
    }
    
    public enum SessionRecorderAction {
        RECORD
    };

    private SessionRecorderAction action;
    private boolean recording;
    private String recordingName;
    private List<String> selectedAvatarIds;
    private List<String> selectedAvatarNames;

    public SessionRecorderAction getAction() {
        return action;
    }

    public void setAction(SessionRecorderAction action) {
        this.action = action;
    }

    public boolean isRecording() {
        return recording;
    }

    public void setRecording(boolean recording) {
        this.recording = recording;
    }

    public String getRecordingName() {
        return recordingName;
    }

    public void setRecordingName(String recordingName) {
        this.recordingName = recordingName;
    }

    public List<String> getSelectedAvatarIds() {
        return selectedAvatarIds;
    }

    public void setSelectedAvatarIds(List<String> selectedAvatarIds) {
        this.selectedAvatarIds = selectedAvatarIds;
    }

    public List<String> getSelectedAvatarNames() {
        return selectedAvatarNames;
    }

    public void setSelectedAvatarNames(List<String> selectedAvatarNames) {
        this.selectedAvatarNames = selectedAvatarNames;
    }

    /**
     * Static method used to create an instance of SessionRecorderCellChangeMessage that has an action type
     * <code>RECORD</code>.
     * @param cellID The id of the cell for which this message is created
     * @param recording boolean to indicate the state of the recorder
     * @param recordingName the name of the tape to record
     * @param selectedAvatarIds
     * @param selectedAvatarNames
     * @param userdata the list of the selected users
     * @return a message with appropriate state
     */
    public static SessionRecorderCellMessage recordingMessage(CellID cellID
            , String recordingName, boolean recording, List<String> selectedAvatarIds, List<String> selectedAvatarNames) {
        SessionRecorderCellMessage msg = new SessionRecorderCellMessage(cellID);
        msg.action = SessionRecorderAction.RECORD;
        msg.recording = recording;
        msg.recordingName = recordingName;
        msg.selectedAvatarIds = selectedAvatarIds;
        msg.selectedAvatarNames = selectedAvatarNames;
        return msg;
    }

    public String getDescription() {
        StringBuilder builder = new StringBuilder();
        builder.append(action);
        builder.append(": ");
        switch (action) {
            case RECORD:
                builder.append("isRecording: " + recording);
                builder.append(" recordingName: " + recordingName);
                break;
            default:
                throw new RuntimeException("Invalid action");
        }
        return builder.toString();
    }
    
}
