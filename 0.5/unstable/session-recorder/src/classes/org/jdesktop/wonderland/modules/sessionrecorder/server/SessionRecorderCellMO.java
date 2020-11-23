/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
package org.jdesktop.wonderland.modules.sessionrecorder.server;

import com.sun.mpk20.voicelib.app.ManagedCallStatusListener;
import com.sun.mpk20.voicelib.impl.service.voice.VoiceImpl;
import com.sun.voip.client.connector.CallStatus;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;
import org.jdesktop.wonderland.common.cell.state.CellClientState;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.modules.sessionrecorder.common.SessionRecorderCellClientState;
import org.jdesktop.wonderland.modules.sessionrecorder.common.SessionRecorderCellMessage;
import org.jdesktop.wonderland.modules.sessionrecorder.common.SessionRecorderCellServerState;
import org.jdesktop.wonderland.server.UserMO;
import org.jdesktop.wonderland.server.UserManager;
import org.jdesktop.wonderland.server.cell.AbstractComponentMessageReceiver;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.ChannelComponentMO;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;
import org.jdesktop.wonderland.server.eventrecorder.RecorderManager;

/**
 *
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
public class SessionRecorderCellMO extends CellMO implements ManagedCallStatusListener {

    private List<String> selectedAvatarIds;
    private List<String> selectedAvatarNames;
    private static final Logger LOGGER = Logger.getLogger(SessionRecorderCellMO.class.getName());

    public SessionRecorderCellMO() {
    }

    @Override
    protected void setLive(boolean live) {
        super.setLive(live);
        if (live) {
            ChannelComponentMO channel = getChannel();
            if (channel == null) {
                throw new IllegalStateException("Cell does not have a ChannelComponent");
            }
            //Add the message receiver to the channel
            channel.addMessageReceiver(SessionRecorderCellMessage.class,
                    (ChannelComponentMO.ComponentMessageReceiver) new SessionRecorderCellMOMessageReceiver(this));
        } else {
            getChannel().removeMessageReceiver(SessionRecorderCellMessage.class);
        }
    }

    private ChannelComponentMO getChannel() {
        return getComponent(ChannelComponentMO.class);
    }

    /**
     * start or stop recording
     *
     * @param startRecording
     * @param recordingName
     */
    private void setRecording(boolean startRecording, String recordingName) {
        LOGGER.log(Level.INFO, "{0}:setRecording():Start", this.getClass().getName());
        if (startRecording) {
            startRecording(recordingName);
        } else {
            stopRecording(recordingName);
        }
        LOGGER.log(Level.INFO, "{0}:setRecording():End", this.getClass().getName());
    }

    /**
     * this method records the call for each user, on start recording, to their
     * the audio data
     *
     * @param recordingName
     */
    private void startRecording(final String recordingName) {
        LOGGER.log(Level.INFO, "{0}:startRecording():Start", this.getClass().getName());
        try {
            for (int i = 0; i < selectedAvatarIds.size(); i++) {
                final int index = i;
                new Thread(new Runnable() {

                    public void run() {

                        String callId = selectedAvatarIds.get(index);
                        //records user + in world audio.
                        try {
                            String name = recordingName + "-" + selectedAvatarNames.get(index) + ".au";
                            VoiceImpl.getInstance().getBridgeManager().recordCall(callId, name, true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        } catch (Exception ex) {
            Logger.getLogger(SessionRecorderCellMO.class.getName()).log(Level.SEVERE, null, ex);
        }
        LOGGER.log(Level.INFO, "{0}:startRecording():End", this.getClass().getName());
    }

    /**
     * this method stops the call when the recording is stopped
     */
    private void stopRecording(final String recordingName) {
        LOGGER.log(Level.INFO, "{0}:stopRecording():Start", this.getClass().getName());
        try {
            for (final String id : selectedAvatarIds) {
                new Thread(new Runnable() {

                    public void run() {
                        try {
                            String name = recordingName + "-" + id + ".au";
                            VoiceImpl.getInstance().getBridgeManager().recordCall(id, name, false);
                        } catch (IOException ex) {
                            Logger.getLogger(SessionRecorderCellMO.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }).start();
            }
        } catch (Exception ex) {
            Logger.getLogger(SessionRecorderCellMO.class.getName()).log(Level.SEVERE, null, ex);
        }
        LOGGER.log(Level.INFO, "{0}:stopRecording():End", this.getClass().getName());
    }

    /**
     * processes the message for the recording
     *
     * @param clientID
     * @param arcm
     */
    private void processRecordMessage(WonderlandClientID clientID, SessionRecorderCellMessage arcm) {
        LOGGER.log(Level.INFO, "{0}:processRecordMessage():Start", this.getClass().getName());
        selectedAvatarIds = arcm.getSelectedAvatarIds();
        selectedAvatarNames = arcm.getSelectedAvatarNames();

        LOGGER.log(Level.INFO, "selectedAvatarIds : {0}", selectedAvatarIds);
        LOGGER.log(Level.INFO, "selectedAvatarNames : {0}", selectedAvatarNames);

        setRecording(arcm.isRecording(), arcm.getRecordingName());
        LOGGER.log(Level.INFO, "{0}:processRecordMessage():End", this.getClass().getName());
    }

    public void callStatusChanged(CallStatus status) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * message receiver
     */
    private static class SessionRecorderCellMOMessageReceiver extends AbstractComponentMessageReceiver {

        public SessionRecorderCellMOMessageReceiver(SessionRecorderCellMO cellMO) {
            super(cellMO);
        }

        public void messageReceived(WonderlandClientSender sender, WonderlandClientID clientID, CellMessage message) {
            SessionRecorderCellMO cellMO = (SessionRecorderCellMO) getCell();
            SessionRecorderCellMessage arcm = (SessionRecorderCellMessage) message;
            switch (arcm.getAction()) {
                case RECORD:
                    cellMO.processRecordMessage(clientID, arcm);
                    break;
            }
        }

        @Override
        protected void postRecordMessage(WonderlandClientSender sender, WonderlandClientID clientID, CellMessage message) {
            SessionRecorderCellMessage arcm = (SessionRecorderCellMessage) message;
            UserMO user = UserManager.getUserManager().getUser(clientID);
            RecorderManager.getDefaultManager().recordMetadata(message, arcm.getDescription() + " initiated by " + user.getUsername() + "[" + user.getIdentity().getFullName() + "]");
        }
    }

    @Override
    protected String getClientCellClassName(WonderlandClientID clientID, ClientCapabilities capabilities) {
        return "org.jdesktop.wonderland.modules.sessionrecorder.client.SessionRecorderCell";
    }

    @Override
    public CellClientState getClientState(CellClientState cellClientState,
            WonderlandClientID clientID, ClientCapabilities capabilities) {

        if (cellClientState == null) {
            cellClientState = new SessionRecorderCellClientState();
        }
        return super.getClientState(cellClientState, clientID, capabilities);
    }

    @Override
    public void setServerState(CellServerState cellServerState) {
        super.setServerState(cellServerState);
    }

    @Override
    public CellServerState getServerState(CellServerState cellServerState) {
        if (cellServerState == null) {
            cellServerState = new SessionRecorderCellServerState();
        }
        return super.getServerState(cellServerState);
    }

}
