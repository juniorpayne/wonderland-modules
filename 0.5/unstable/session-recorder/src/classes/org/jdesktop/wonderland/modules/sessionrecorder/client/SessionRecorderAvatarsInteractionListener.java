/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
package org.jdesktop.wonderland.modules.sessionrecorder.client;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.view.AvatarCell;
import org.jdesktop.wonderland.modules.avatarbase.client.imi.character.behavior.AvatarInteractionListener;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarImiJME;
import org.jdesktop.wonderland.modules.sessionrecorder.common.GestureData;

/**
 * Listener for avatar interactions
 *
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
public class SessionRecorderAvatarsInteractionListener implements AvatarInteractionListener {

    private final List<String> users;
    private final SessionRecorderCell recorderCell;
    private static final Logger LOGGER = Logger.getLogger(SessionRecorderAvatarsInteractionListener.class.getName());

    public SessionRecorderAvatarsInteractionListener(List<String> users, SessionRecorderCell recorderCell) {
        this.recorderCell = recorderCell;
        this.users = users;
    }

    public void avatarsInteract(Cell sourceCell, Cell targetCell, String interaction, boolean started) {
        LOGGER.log(Level.INFO, "{0}:avatarsInteract():Start", this.getClass().getName());
        AvatarImiJME sourceRend = (AvatarImiJME) sourceCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
        String sourceName = sourceRend.getAvatarCharacter().getName();
        AvatarImiJME targetRend;
        String targetName = null;
        if (targetCell instanceof AvatarCell) {
            targetRend = (AvatarImiJME) targetCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
            targetName = targetRend.getAvatarCharacter().getName();
        } else {
            targetName = targetCell.getName();
        }

        //give proper names
        if (interaction.contains("_TouchArm")) {
            interaction = "Touch Arm - " + targetName;
        } else if (interaction.contains("_TouchShoulder")) {
            interaction = "Touch Shoulder - " + targetName;
        } else if (interaction.contains("ShakeHands")) {
            interaction = "Shake Hands";
        }

        if (users.contains(sourceName)) {
            if (started) {
                LOGGER.log(Level.INFO, "{0} - started, by user = {1} with target = {2}", new Object[]{interaction, sourceName, targetName});
                GestureData gesture = new GestureData();
                gesture.setGesture(interaction);
                gesture.setStartTime(new Date());
                gesture.setUsername(sourceName);
                recorderCell.getSessionRecordingData().getGestureData().add(gesture);
            } else {
                //used to check wether the interaction is playing on starting the recording
                boolean gesturePlayedDuringRecording = true;
                for (GestureData gestureData : recorderCell.getSessionRecordingData().getGestureData()) {
                    if (gestureData.getEndTime() == null
                            && gestureData.getUsername().equals(sourceName)
                            && gestureData.getGesture().equals(interaction)) {
                        gestureData.setEndTime(new Date());
                        gesturePlayedDuringRecording = false;
                    }
                }

                //if gesturePlayedDuringRecording then make the entry of it the gesture list 
                //with the recording start time as the start time of the gestures
                if (gesturePlayedDuringRecording) {
                    LOGGER.log(Level.INFO, "Adding {0} gesture played during the starting of recording", interaction);
                    GestureData gesture = new GestureData();
                    gesture.setGesture(interaction);
                    gesture.setStartTime(recorderCell.getSessionRecordingData().getStartTime());
                    gesture.setUsername(sourceName);
                    gesture.setEndTime(new Date());
                    recorderCell.getSessionRecordingData().getGestureData().add(gesture);
                }
                LOGGER.log(Level.INFO, "{0} - stopped, by user = {1} with target = {2}", new Object[]{interaction, sourceName, targetName});
            }
        }

        if (interaction.equals("Shake Hands")) {
            if (users.contains(targetName)) {
                if (started) {
                    LOGGER.log(Level.INFO, "Started:- saving the {0} data for the target = {1}", new Object[]{interaction, targetName});
                    GestureData gesture = new GestureData();
                    gesture.setGesture(interaction);
                    gesture.setStartTime(new Date());
                    gesture.setUsername(targetName);
                    recorderCell.getSessionRecordingData().getGestureData().add(gesture);
                } else {
                    //used to check wether the interaction is playing on starting the recording
                    boolean gesturePlayedDuringRecording = true;
                    for (GestureData gestureData : recorderCell.getSessionRecordingData().getGestureData()) {
                        if (gestureData.getEndTime() == null
                                && gestureData.getUsername().equals(targetName)
                                && gestureData.getGesture().equals(interaction)) {
                            gesturePlayedDuringRecording = false;
                            gestureData.setEndTime(new Date());
                        }
                    }

                    //if gesturePlayedDuringRecording then make the entry of it the gesture list 
                    //with the recording start time as the start time of the gestures
                    if (gesturePlayedDuringRecording) {
                        LOGGER.log(Level.INFO, "Adding {0} gesture in target, played during the starting of recording", interaction);
                        GestureData gesture = new GestureData();
                        gesture.setGesture(interaction);
                        gesture.setStartTime(recorderCell.getSessionRecordingData().getStartTime());
                        gesture.setUsername(targetName);
                        gesture.setEndTime(new Date());
                        recorderCell.getSessionRecordingData().getGestureData().add(gesture);
                    }
                    LOGGER.log(Level.INFO, "Stopped:- saving the {0} data for the target = {1}", new Object[]{interaction, targetName});
                }
            }
        }
        LOGGER.log(Level.INFO, "{0}:avatarsInteract():End", this.getClass().getName());
    }

}
