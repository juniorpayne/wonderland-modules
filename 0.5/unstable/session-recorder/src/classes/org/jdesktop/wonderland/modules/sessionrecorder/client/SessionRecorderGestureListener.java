/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
package org.jdesktop.wonderland.modules.sessionrecorder.client;

import imi.character.statemachine.GameState;
import imi.character.statemachine.GameStateChangeListener;
import imi.character.statemachine.corestates.CycleActionState;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.modules.sessionrecorder.common.GestureData;

/**
 * Add gesture data when this listener called
 *
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
public class SessionRecorderGestureListener implements GameStateChangeListener {

    private final List<String> users;
    private final SessionRecorderCell recorderCell;
    private final ArrayList<String> gestureList = new ArrayList<String>();
    private final ArrayList<String> intensityLevel = new ArrayList<String>();
    private String gestureName;
    private Logger LOGGER = Logger.getLogger(SessionRecorderGestureListener.class.getName());

    public SessionRecorderGestureListener(List<String> users, SessionRecorderCell recorderCell) {
        this.recorderCell = recorderCell;
        this.users = users;
        //settinh the gesture list
        gestureList.add("Yes");
        gestureList.add("No");
        gestureList.add("Wink");
        gestureList.add("AnswerCell");
        gestureList.add("Bow");
        gestureList.add("Breathing");
        gestureList.add("Cheer");
        gestureList.add("Clap");
        gestureList.add("CrossHands");
        gestureList.add("Crying");
        gestureList.add("FoldArms");
        gestureList.add("Follow");
        gestureList.add("Gesticulating");
        gestureList.add("Hunch");
        gestureList.add("Laugh");
        gestureList.add("PublicSpeaking");
        gestureList.add("RaiseHand");
        gestureList.add("Wave");
        gestureList.add("CrossAnkles");
        gestureList.add("CrossLegs");

        //setting the intensity level
        intensityLevel.add("Low");
        intensityLevel.add("Medium");
        intensityLevel.add("High");
    }

    public void enterInState(GameState gs) {
    }

    public void exitfromState(GameState gs) {
    }

    public void changeInState(GameState gs, String animName, boolean animFinished, String enterexit) {
        LOGGER.log(Level.INFO, "{0}:changeInState():Start", this.getClass().getName());
        LOGGER.log(Level.INFO, "gs : {0}", gs);
        LOGGER.log(Level.INFO, "animName : {0}", animName);
        LOGGER.log(Level.INFO, "animFinished : {0}", animFinished);
        LOGGER.log(Level.INFO, "enterexit : {0}", enterexit);
        //check if the gesture played by the user is participants in the recording or not
        for (String user : users) {
            if (gs.getContext().getCharacter().getName().equals(user)) {
                if (animName.equals("sitting") || animName.equals("liedown")) {
                    gestureName = animName.equals("sitting") ? "Sit" : "LieDown";

                    if (gs instanceof CycleActionState) {
                        CycleActionState cs = (CycleActionState) gs;
                        if (cs.getContext().isGesturePlayingInSitting()) {
                            return;
                        }
                    }

                    if (enterexit.equals("enter")) {
                        boolean add = true;
                        for (int i = 0; i < recorderCell.getSessionRecordingData().getGestureData().size(); i++) {
                            if (recorderCell.getSessionRecordingData().getGestureData().get(i).getEndTime() == null
                                    && recorderCell.getSessionRecordingData().getGestureData().get(i).getGesture().equals(gestureName)
                                    && recorderCell.getSessionRecordingData().getGestureData().get(i).getUsername().equals(user)) {
                                add = false;
                            }
                        }
                        if (add) {
                            createAndSetupGestureData(user);
                        }
                    } else {
                        setEndTime(user);
                    }
                } else {

                    for (String gesture : gestureList) {
                        if (animName.contains(gesture)) {
                            gestureName = gesture;
                            for (String intensity : intensityLevel) {
                                if (animName.contains(intensity)) {
                                    gestureName = gesture + " " + intensity;
                                }
                            }
                        }
                    }
                    if (!animFinished) {
                        createAndSetupGestureData(user);
                    } else {
                        setEndTime(user);
                    }

                }
            }
        }
        LOGGER.log(Level.INFO, "{0}:changeInState():End", this.getClass().getName());
    }

    private void createAndSetupGestureData(String user) {
        LOGGER.log(Level.INFO, "{0}:createAndSetupGestureData():Start", this.getClass().getName());
        GestureData gesture = new GestureData();
        gesture.setGesture(gestureName);
            gesture.setStartTime(new Date());
        gesture.setUsername(user);
        recorderCell.getSessionRecordingData().getGestureData().add(gesture);
        LOGGER.log(Level.INFO, "{0}:createAndSetupGestureData():End", this.getClass().getName());
    }

    private void setEndTime(String user) {
        LOGGER.log(Level.INFO, "{0}:setEndTime():Start", this.getClass().getName());
        //get gesture data with null endtime and with same animation name and update it
        for (int i = 0; i < recorderCell.getSessionRecordingData().getGestureData().size(); i++) {
            if (recorderCell.getSessionRecordingData().getGestureData().get(i).getEndTime() == null
                    && recorderCell.getSessionRecordingData().getGestureData().get(i).getGesture().equals(gestureName) && recorderCell.getSessionRecordingData().getGestureData().get(i).getUsername().equals(user)) {
                recorderCell.getSessionRecordingData().getGestureData().get(i).setEndTime(new Date());
                    }
                }
        LOGGER.log(Level.INFO, "{0}:setEndTime():End", this.getClass().getName());
    }
    public ArrayList<String> getGestureList() {
        return gestureList;
    }

    public ArrayList<String> getIntensityLevel() {
        return intensityLevel;
    }
    
}
