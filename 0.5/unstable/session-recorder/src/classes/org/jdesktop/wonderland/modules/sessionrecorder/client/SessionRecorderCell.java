/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
package org.jdesktop.wonderland.modules.sessionrecorder.client;

import imi.character.statemachine.GameContext;
import imi.character.statemachine.GameState;
import imi.character.statemachine.GameStateChangeListenerRegisterar;
import imi.character.statemachine.corestates.CycleActionState;
import imi.character.statemachine.corestates.SitState;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.FilterReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellCache;
import org.jdesktop.wonderland.client.cell.CellRenderer;
import org.jdesktop.wonderland.client.cell.ChannelComponent;
import org.jdesktop.wonderland.client.cell.view.AvatarCell;
import org.jdesktop.wonderland.client.comms.SessionStatusListener;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.hud.CompassLayout;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDComponent;
import org.jdesktop.wonderland.client.hud.HUDManagerFactory;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.input.EventClassListener;
import org.jdesktop.wonderland.client.input.InputManager;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.client.jme.cellrenderer.CellRendererJME;
import org.jdesktop.wonderland.client.jme.input.MouseButtonEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseEvent3D;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.client.login.SessionLifecycleListener;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.common.cell.state.CellClientState;
import org.jdesktop.wonderland.modules.avatarbase.client.imi.character.behavior.AvatarInteractionListenerRegistrar;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarImiJME;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarImiJME.AvatarChangedListener;
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepository;
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepositoryRegistry;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentResource;
import org.jdesktop.wonderland.modules.sessionrecorder.common.GestureData;
import org.jdesktop.wonderland.modules.sessionrecorder.common.SessionRecorderCellMessage;
import org.jdesktop.wonderland.modules.sessionrecorder.common.SessionRecording;
import org.jdesktop.wonderland.modules.sessionrecorder.common.SessionRecordingConfiguration;
import org.jdesktop.wonderland.modules.sessionrecorder.common.SoundData;
import org.json.JSONArray;

/**
 *
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
public class SessionRecorderCell extends Cell implements SessionLifecycleListener, SessionStatusListener {

    private boolean recording = false;
    private String videoRecordingName;
    private SessionRecorderCellRenderer renderer;
    private static JAXBContext context, configContext;
    private MouseLeftClickListener mouseLeftClickListener = null;
    private HUDComponent recordingPanelHUD;
    private SessionRecorderControlPanel recordingPanel;
    private SessionRecording recordingData = null;
    private SessionRecorderGestureListener gestureListener;
    private SessionRecorderAvatarsInteractionListener avatarInteractionListener;
    private ImageDirCreationThread imageDirThread = null;
    private WindowCloseListener winCloseListener = null;
    private ChannelComponent channelComponent;
    private boolean soundStarted = false;
    private String audioName;
    private String cellId;
    private AvatarChangedListener avatarChangedListener = null;
    private static final SimpleDateFormat VIDEO_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HH.mm.ss");
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/sessionrecorder/client/resources/Bundle");
    private static final Logger LOGGER = Logger.getLogger(SessionRecorderCell.class.getName());

    {
        this.getCellCache().getSession().getSessionManager().addLifecycleListener(this);
    }

    public SessionRecorderCell(CellID cellID, CellCache cellCache) {
        super(cellID, cellCache);

    }

    @Override
    public void setStatus(CellStatus status, boolean increasing) {
        super.setStatus(status, increasing);

        if (increasing && status.equals(CellStatus.VISIBLE)) {

            //attach mouse event listener
            if (mouseLeftClickListener == null) {
                mouseLeftClickListener = new MouseLeftClickListener();
                CellRendererJME rend = (CellRendererJME) getCellRenderer(RendererType.RENDERER_JME);
                mouseLeftClickListener.addToEntity(rend.getEntity());
            }
        } else if (status.equals(CellStatus.DISK)) {
            //If the recording is On, stop it and take input from the user for the recorded data
            if (recording && recordingPanel != null) {
                recordingPanel.hudClosed(recordingPanelHUD);
            }
        } else if (!increasing && status.equals(CellStatus.VISIBLE)) {
            //detach mouse event listener
            CellRendererJME rend = (CellRendererJME) getCellRenderer(RendererType.RENDERER_JME);
            mouseLeftClickListener.removeFromEntity(rend.getEntity());
            mouseLeftClickListener = null;

            //remove HUD if opened
            if (recordingPanelHUD != null) {
                HUD mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");
                mainHUD.removeComponent(recordingPanelHUD);
                recordingPanelHUD.setVisible(false);
                recordingPanelHUD = null;
            }
        }
    }

    @Override
    protected CellRenderer createCellRenderer(RendererType rendererType) {
        if (rendererType == RendererType.RENDERER_JME) {
            this.renderer = new SessionRecorderCellRenderer(this);
            renderer.resetImageCounter();
            return this.renderer;
        } else {
            return super.createCellRenderer(rendererType);
        }
    }

    @Override
    public void setClientState(CellClientState cellClientState) {
        super.setClientState(cellClientState);
    }

    private void addGesturePlayedDuringRecording(SessionRecorderGestureListener gestureListener) {
        for (Cell selectedAvatarCell : recordingPanel.getSelectedCellList()) {
            AvatarImiJME rend = (AvatarImiJME) selectedAvatarCell.getCellRenderer(RendererType.RENDERER_JME);
            if (rend != null && rend.getAvatarCharacter() != null) {
                GameContext context = rend.getAvatarCharacter().getContext();
                GameState gs = context.getCurrentState();
                String user = ((imi.character.Character) rend.getAvatarCharacter()).getName();
                if (gs instanceof SitState) {
                    SitState ss = (SitState) gs;
                    if (ss.isSitting()) {
                        addGesture("Sit", user);
                    } else if (ss.isSleeping()) {
                        addGesture("LieDown", user);
                    }
                } else if (gs instanceof CycleActionState) {
                    CycleActionState cs = (CycleActionState) gs;
                    String anim = cs.getAnimationName();
                    if (context.isGesturePlayingInSitting()) {
                        addGesture("Sit", user);
                    }
                    for (String gest : gestureListener.getGestureList()) {
                        if (cs.getAnimationName().contains(gest)) {
                            anim = gest;
                            for (String intensity : gestureListener.getIntensityLevel()) {
                                if (cs.getAnimationName().contains(intensity)) {
                                    anim = anim + " " + intensity;
                                }
                            }
                        }
                    }
                    addGesture(anim, user);

                    Map<String, List<String>> backupAnims = cs.getBackupAnimations();
                    if (backupAnims != null && !backupAnims.isEmpty()) {
                        for (String backupAnim : backupAnims.keySet()) {
                            if (!backupAnim.equals(cs.getAnimationName())) {
                                for (String gest : gestureListener.getGestureList()) {
                                    if (backupAnim.contains(gest)) {
                                        backupAnim = gest;
                                        for (String intensity : gestureListener.getIntensityLevel()) {
                                            if (cs.getAnimationName().contains(intensity)) {
                                                backupAnim = backupAnim + " " + intensity;
                                            }
                                        }
                                    }
                                }
                                addGesture(backupAnim, user);
                            }
                        }
                    }
                }
            }
        }
    }

    private void addGesture(String gestureName, String user) {
        GestureData gesture = new GestureData();
        gesture.setGesture(gestureName);
        gesture.setUsername(user);
        gesture.setStartTime(getSessionRecordingData().getStartTime());
        getSessionRecordingData().getGestureData().add(gesture);
    }

    /**
     * start recording - add recording data, start capturing images, send
     * message to start audio recording
     */
    void startRecording() {
        LOGGER.log(Level.INFO, "{0}:startRecording():Start", this.getClass().getName());
        LOGGER.info(this.getClass().getName() + ":startRecording():Start@@@@");
        try {
            //update recording data
            Calendar calendar = Calendar.getInstance();
            videoRecordingName = "Wonderland_" + VIDEO_DATE_FORMAT.format(calendar.getTime());
            String student = recordingPanel.getStudentName();
            List<String> participants = recordingPanel.getSelectedNameList();
            boolean onlyAudio = recordingPanel.isOnlyAudio();
            Date d = new Date();
            recordingData = new SessionRecording();
            getSessionRecordingData().setId(videoRecordingName);
            getSessionRecordingData().setStartTime(d);
            getSessionRecordingData().setStudentName(student);
            getSessionRecordingData().setparticipants(participants);
            getSessionRecordingData().setOnlyAudio(onlyAudio);
            getSessionRecordingData().setUserData(recordingPanel.getUserData());
            if (soundStarted) {
                SoundData sound = new SoundData();
                sound.setAudioName(audioName);
                sound.setStartTime(d);
                sound.setCellId(cellId);
                getSessionRecordingData().getSoundData().add(sound);
            }
            String audioURL = LoginManager.getPrimary().getServerURL() + "webdav/content/groups/users/" + SessionRecording.DIR_NAME + "/Audio/" + videoRecordingName + ".wav";
            getSessionRecordingData().setAudioURL(audioURL);
            String csvParticipants = "";
            for (int i = 0; i < participants.size(); i++) {
                csvParticipants = csvParticipants + participants.get(i);
                if ((i + 1) != participants.size()) {
                    csvParticipants = csvParticipants + "-";
                }
            }
            String recordingName = d + " " + csvParticipants;
            getSessionRecordingData().setRecordingName(recordingName);

            //start process of capturing image
            ((SessionRecorderCellRenderer) renderer).resetImageCounter();
            ((SessionRecorderCellRenderer) renderer).resetFrameCounter();
            if (!onlyAudio) {
                imageDirThread = new ImageDirCreationThread();
                imageDirThread.setPriority(Thread.MAX_PRIORITY);
                imageDirThread.start();
            }
            recording = true;
            LOGGER.log(Level.INFO, "Recording = {0} name = {1}", new Object[]{recording, videoRecordingName});
            gestureListener = new SessionRecorderGestureListener(participants, this);
            //adding the previously playing gestures in the gesture dat
            addGesturePlayedDuringRecording(gestureListener);
            //send message to start audio recording
            SessionRecorderCellMessage msg = SessionRecorderCellMessage.recordingMessage(getCellID(), videoRecordingName, recording, recordingPanel.getSelectedIdList(), recordingPanel.getSelectedNameList());
            getChannel().send(msg);

            //attaching the required listeners
            if (winCloseListener == null) {
                winCloseListener = new WindowCloseListener();
            }
            JmeClientMain.getFrame().getFrame().addWindowListener(winCloseListener);

            GameStateChangeListenerRegisterar.registerListener(gestureListener);
            avatarInteractionListener = new SessionRecorderAvatarsInteractionListener(participants, this);
            AvatarInteractionListenerRegistrar.registerListener(avatarInteractionListener);
        } catch (Exception ex) {
            Logger.getLogger(SessionRecorderCell.class.getName()).log(Level.SEVERE, null, ex);
        }
        LOGGER.info(this.getClass().getName() + ":startRecording():End@@@@@@@");
        LOGGER.log(Level.INFO, "{0}:startRecording():End", this.getClass().getName());
    }

    /**
     * stop recording - stop capturing images, update recording data, send
     * message to stop audio recording
     */
    void stopRecording() {
        LOGGER.log(Level.INFO, "{0}:stopRecording():Start", this.getClass().getName());
        try {

            //stop capturing images by marking recording to false
            recording = false;
            //update recording data
            getSessionRecordingData().setEndTime(new Date());
            for (int i = 0; i < getSessionRecordingData().getGestureData().size(); i++) {
                if (getSessionRecordingData().getGestureData().get(i).getEndTime() == null) {
                    getSessionRecordingData().getGestureData().get(i).setEndTime(getSessionRecordingData().getEndTime());
                }
            }

            for (int i = 0; i < getSessionRecordingData().getSoundData().size(); i++) {
                if (getSessionRecordingData().getSoundData().get(i).getStopTime() == null) {
                    Date stop = new Date();
                    getSessionRecordingData().getSoundData().get(i).setStopTime(stop);
                }
            }

            if (soundStarted) {
                soundStarted = false;
                cellId = null;
            }

            //send message to stop audio recording
            SessionRecorderCellMessage msg = SessionRecorderCellMessage
                    .recordingMessage(getCellID(), videoRecordingName, recording, recordingPanel.getSelectedIdList(), recordingPanel.getSelectedNameList());
            getChannel().send(msg);
            LOGGER.log(Level.INFO, "Recording = {0} name = {1}", new Object[]{recording, videoRecordingName});
            //remove listeners
            JmeClientMain.getFrame().getFrame().removeWindowListener(winCloseListener);
            GameStateChangeListenerRegisterar.deRegisterListener(gestureListener);
            AvatarInteractionListenerRegistrar.deRegisterListener(avatarInteractionListener);
            gestureListener = null;
            avatarInteractionListener = null;
            winCloseListener = null;

        } catch (Exception ex) {
            Logger.getLogger(SessionRecorderCell.class.getName()).log(Level.SEVERE, null, ex);
        }
        LOGGER.log(Level.INFO, "{0}:stopRecording():End", this.getClass().getName());
    }

    /**
     * save recording data with audio & video
     */
    void saveRecordingData() {
        LOGGER.log(Level.INFO, "{0}:saveRecordingData():Start", this.getClass().getName());
        try {
            LOGGER.info("saving the Recording Data...");

            //save the data on server side
            submitFile(getSessionRecordingData());

            //rest call for creating wav file and video file
            Client client = ClientBuilder.newClient();
            WebTarget target = null;
            Form conversionForm = new Form();
            conversionForm.param("recordingName", videoRecordingName);
            long duration = 0;
            long diff = recordingData.getEndTime().getTime() - recordingData.getStartTime().getTime();
            duration = diff / 1000 % 60;
            conversionForm.param("duration", String.valueOf(duration));

            JSONArray jsonArray = new JSONArray();
            for (String p : recordingPanel.getSelectedNameList()) {
                jsonArray.put(p);
            }
            conversionForm.param("participantsName", jsonArray.toString());
            if (getSessionRecordingData().isOnlyAudio()) {
                target = client.target(LoginManager.getPrimary().getServerURL())
                        .path("session-recorder/session-recorder/resources/sessionRecordings/convertAuToWav");
                LOGGER.info("Only audio, creating the .wav files");
                target.request().post(Entity.entity(conversionForm, MediaType.APPLICATION_FORM_URLENCODED));
            } else {
                //create movie if recording audio + video
                target = client.target(LoginManager.getPrimary().getServerURL())
                        .path("session-recorder/session-recorder/resources/sessionRecordings/createAudioAndVideo");
                conversionForm.param("frameRate", Float.toString(calculateActualFrameRate(recordingData.getStartTime(), recordingData.getEndTime())));
                conversionForm.param("frameCounter", Float.toString(renderer.getFrameCounter()));
                conversionForm.param("username", LoginManager.getPrimary().getUsername());
                conversionForm.param("tags", getSessionRecordingData().getTagsCS());
                conversionForm.param("sessionRecordingID", recordingData.getId());
                conversionForm.param("repairImageSource", String.valueOf(renderer.needRepairImageSource()));
                LOGGER.info("audio + video, so Making the call for Creatng the audio &&& movie");
                target.request().post(Entity.entity(conversionForm, MediaType.APPLICATION_FORM_URLENCODED));
            }
            recordingData = null;
        } catch (Exception ex) {
            Logger.getLogger(SessionRecorderCell.class.getName()).log(Level.SEVERE, null, ex);
        }
        LOGGER.log(Level.INFO, "{0}:saveRecordingData():End", this.getClass().getName());
    }

    /**
     * discards the recorded data
     */
    void discardRecording() {
        LOGGER.log(Level.INFO, "{0}:discardRecording():Start", this.getClass().getName());
        LOGGER.warning("stopping streaming thread...");
        renderer.stopStreamingThread();
        recordingData = null;
        LOGGER.log(Level.INFO, "{0}:discardRecording():End", this.getClass().getName());
    }

    /**
     * remove captured images at server side
     */
    void removeCapturedImages() {
        LOGGER.log(Level.INFO, "{0}:removeCapturedImages():Start", this.getClass().getName());
        try {
            ContentCollection recordingDir = getSessionRecordingsDir(LoginManager.getPrimary());
            ContentCollection tempDir = (ContentCollection) recordingDir.getChild("temp");
            tempDir.removeChild(LoginManager.getPrimary().getUsername());
        } catch (ContentRepositoryException ex) {
            Logger.getLogger(SessionRecorderCell.class.getName()).log(Level.SEVERE, null, ex);
        }
        LOGGER.log(Level.INFO, "{0}:removeCapturedImages():End", this.getClass().getName());
    }

    /**
     * Determine the rate of frames per second that we recorded JPEGs
     */
    private float calculateActualFrameRate(Date startTime, Date endTime) {
        LOGGER.log(Level.INFO, "{0}:calculateActualFrameRate():Start", this.getClass().getName());
        long elapsedTimeMillis = endTime.getTime() - startTime.getTime();
        float elapsedTimeSec = elapsedTimeMillis / 1000F;
        float capturedFrameRate = renderer.getFrameCounter() / elapsedTimeSec;
        LOGGER.log(Level.INFO, "{0}:calculateActualFrameRate():End", this.getClass().getName());
        return capturedFrameRate;

    }

    boolean isRecording() {
        return recording;
    }

    /**
     * get channel component
     *
     * @return the channel component
     */
    private ChannelComponent getChannel() {
        if (channelComponent == null) {
            channelComponent = getComponent(ChannelComponent.class);
        }
        return channelComponent;
    }

    /**
     *
     * @return unique name generated for recording
     */
    String getVideoRecordingName() {
        return videoRecordingName;
    }

    public void sessionCreated(WonderlandSession session) {
    }

    public void primarySession(WonderlandSession session) {
        if (session != null) {
            session.addSessionStatusListener(this);

        }
    }

    public void sessionStatusChanged(WonderlandSession session, WonderlandSession.Status status) {
        LOGGER.log(Level.INFO, "{0}:sessionStatusChanged():Start", this.getClass().getName());
        if (status != WonderlandSession.Status.CONNECTED) {
            if (recordingPanel != null) {
                recordingPanel.hudClosed(recordingPanelHUD);
            }
        }
        LOGGER.log(Level.INFO, "{0}:sessionStatusChanged():End", this.getClass().getName());
    }

    /**
     * converting the pojo to xml file and saving it to content repository
     *
     * @param sessionData
     */
    public static void submitFile(SessionRecording sessionData) {
        LOGGER.info("SessionRecorderCell:submitFile():Start");
        try {
            ServerSessionManager ssm = LoginManager.getPrimary();
            ContentCollection dir = (ContentCollection) getSessionRecordingsDir(ssm).getChild("Data");
            ContentResource file;
            String fileName = sessionData.getId();
            file = (ContentResource) dir.createChild(fileName, ContentNode.Type.RESOURCE);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Marshaller m = getContext().createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            m.marshal(sessionData, baos);
            file.put(baos.toByteArray());
        } catch (ContentRepositoryException ex) {
            Logger.getLogger(SessionRecorderCell.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(SessionRecorderCell.class.getName()).log(Level.SEVERE, null, ex);
        }
        LOGGER.info("SessionRecorderCell:stopRecording():End");
    }

    /**
     * get content directory
     *
     * @param ssm
     * @return
     * @throws ContentRepositoryException
     */
    static ContentCollection getSessionRecordingsDir(ServerSessionManager ssm) {
        try {
            ContentRepository repo = ContentRepositoryRegistry.getInstance().getRepository(ssm);
            ContentCollection dir = (ContentCollection) repo.getRoot().getChild("groups/users/" + SessionRecording.DIR_NAME);
            if (dir == null) {
                dir = (ContentCollection) repo.getRoot().getChild("groups/users/" + SessionRecording.DIR_NAME);
            }
            return dir;
        } catch (ContentRepositoryException ex) {
            Logger.getLogger(SessionRecorderCell.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * get jaxb context
     *
     * @return
     * @throws JAXBException
     */
    private synchronized static JAXBContext getContext()
            throws JAXBException {
        if (context == null) {
            context = JAXBContext.newInstance(SessionRecording.class);
        }

        return context;
    }

    private synchronized JAXBContext getConfigContext() throws JAXBException {
        if (configContext == null) {
            configContext = JAXBContext.newInstance(SessionRecordingConfiguration.class);
        }

        return configContext;
    }

    public SessionRecordingConfiguration getRecorderConfiguration(ContentResource resource)
            throws JAXBException, ContentRepositoryException {
        Unmarshaller unmarshaller = getConfigContext().createUnmarshaller();

        // reports may contain illegal characters. Add a filter to
        // ignore all these characters.
        Reader in = new EscapeBadCharsReader(new InputStreamReader(resource.getInputStream()));
        SessionRecordingConfiguration report = (SessionRecordingConfiguration) unmarshaller.unmarshal(in);
        return report;
    }

    static class EscapeBadCharsReader extends FilterReader {

        public EscapeBadCharsReader(Reader r) {
            super(r);
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            int res = super.read(cbuf, off, len);
            for (int i = off; i < len; i++) {
                int idx = off + i;
                cbuf[idx] = escape(cbuf[idx]);
            }

            return res;
        }

        private char escape(char c) {
            if ((c == 0x9)
                    || (c == 0xA)
                    || (c == 0xD)
                    || ((c >= 0x20) && (c <= 0xD7FF))
                    || ((c >= 0xE000) && (c <= 0xFFFD))
                    || ((c >= 0x10000) && (c <= 0x10FFFF))) {
                // valid
                return c;
            } else {
                return '.';
            }
        }
    }

    /**
     * get current recording to add/update data in it
     *
     * @return
     */
    public SessionRecording getSessionRecordingData() {
        return recordingData;
    }

    public void addSound(boolean soundStarted, String audioName, String cellId) {

        this.soundStarted = soundStarted;
        this.audioName = audioName;
        this.cellId = cellId;
        if (isRecording()) {
            if (soundStarted) {
                Date start = new Date();
                SoundData soundData = new SoundData();
                soundData.setStartTime(start);
                soundData.setAudioName(this.audioName);
                soundData.setCellId(this.cellId);
                recordingData.getSoundData().add(soundData);
            } else {
                for (int i = 0; i < recordingData.getSoundData().size(); i++) {
                    if (recordingData.getSoundData().get(i).getStopTime() == null
                            && recordingData.getSoundData().get(i).getCellId().equals(cellId)) {
                        Date stop = new Date();
                        recordingData.getSoundData().get(i).setStopTime(stop);
                        break;
                    }
                }
            }
        }
    }

    /**
     * get all avatar cells from the world
     *
     * @return
     */
    public List<Cell> getAvatarCells() {
        LOGGER.log(Level.INFO, "{0}:getAvatarCells():Start", this.getClass().getName());
        List<Cell> avatarCells = new ArrayList<Cell>();
        Cell cell = ClientContextJME.getViewManager().getPrimaryViewCell();
        try {
            Collection<Cell> cellList = cell.getCellCache().getRootCells();
            for (Cell cell1 : cellList) {
                if (cell1 instanceof AvatarCell) {
                    avatarCells.add(cell1);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(SessionRecorderCell.class.getName()).log(Level.SEVERE, null, ex);
        }
        LOGGER.log(Level.INFO, "{0}:getAvatarCells():End", this.getClass().getName());
        return avatarCells;
    }

    /**
     * recursive method to filter the cells of given Cell Class from all the
     * cells in world
     *
     * @param worldCellList list of all the cells in the world
     * @param cellClass a class of whom the cells we want from the worldCellList
     * @return
     */
    public List<? extends Cell> filterCells(Collection<Cell> worldCellList, Class cellClass) {
        List<Cell> cellList = new ArrayList<Cell>();
        try {
            for (Cell cell1 : worldCellList) {
                if (cell1.getClass().equals(cellClass)) {
                    cellList.add(cell1);
                } else {
                    if (!cell1.getChildren().isEmpty()) {
                        cellList.addAll(filterCells(cell1.getChildren(), cellClass));
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(SessionRecorderCell.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cellList;
    }

    /**
     * close control panel
     */
    void closeControlPanel() {
        LOGGER.log(Level.INFO, "{0}:closeControlPanel():Start", this.getClass().getName());
        if (recordingPanelHUD != null) {
            recordingPanelHUD.setVisible(false);
            LOGGER.log(Level.INFO, "recording panel HUD visible = {0}", recordingPanelHUD.isVisible());
        }
        LOGGER.log(Level.INFO, "{0}:closeControlPanel():End", this.getClass().getName());
    }

    /**
     * open the control panel on top-right corner in HUD
     */
    private void openControlPanel() {
        LOGGER.log(Level.INFO, "{0}:openControlPanel():Start", this.getClass().getName());
        if (recordingPanelHUD == null) {
            HUD mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");
            recordingPanel = new SessionRecorderControlPanel(this);
            recordingPanelHUD = mainHUD.createComponent(recordingPanel);
            recordingPanelHUD.setName("Record Role Play");
            recordingPanelHUD.setIcon(new ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/sessionrecorder/client/resources/"
                    + "Role-Play-Camera-icon-32x32.png")));
            recordingPanel.addHUDEventListener(recordingPanelHUD);
            mainHUD.addComponent(recordingPanelHUD);

        }
        recordingPanelHUD.setPreferredLocation(CompassLayout.Layout.NORTHWEST);
        recordingPanelHUD.setVisible(true);
        LOGGER.log(Level.INFO, "recording panel HUD visible = {0}", recordingPanelHUD.isVisible());
        LOGGER.log(Level.INFO, "{0}:openControlPanel():End", this.getClass().getName());
    }

    /**
     * Used to open the control panel, on left click on the camera model
     */
    private class MouseLeftClickListener extends EventClassListener {

        @Override
        public Class[] eventClassesToConsume() {
            return new Class[]{MouseButtonEvent3D.class};
        }

        @Override
        public boolean consumesEvent(Event event) {
            if (!(event instanceof MouseButtonEvent3D)) {
                return false;
            }
            MouseButtonEvent3D mbe = (MouseButtonEvent3D) event;
            MouseEvent awt = (MouseEvent) mbe.getAwtEvent();
            MouseEvent3D.ButtonId butt = mbe.getButton();
            return awt.getID() == MouseEvent.MOUSE_PRESSED
                    && (butt == MouseEvent3D.ButtonId.BUTTON1)
                    && !InputManager.isAnyKeyPressed();
        }

        @Override
        public void commitEvent(Event event) {
            //open control panel
            openControlPanel();
        }
    }

    /**
     * set to true if all the captured images are uploaded to server
     *
     * @return
     */
    public boolean uploadDone() {
        return renderer.uploadDone();
    }

    /**
     *
     * @return the ImageDirCreationThread
     */
    public ImageDirCreationThread getImageDirThread() {
        return imageDirThread;
    }

    /**
     * create image dir for this user
     */
    public class ImageDirCreationThread extends Thread {

        private ContentCollection imageDir;

        @Override
        public void run() {
            imageDir = createImageDirectory();
        }

        public ContentCollection getImageDir() {
            return imageDir;
        }

    }

    /**
     * create image dir from server's content/groups/users/SessionRecordings
     * folder
     *
     * @return
     */
    private ContentCollection createImageDirectory() {
        LOGGER.log(Level.INFO, "{0}:createImageDirectory():Start", this.getClass().getName());
        String dir = "groups/users/" + SessionRecording.DIR_NAME + "/temp";
        ContentCollection imageDir = null, sessionDir, userDir;
        try {
            ContentRepositoryRegistry registry = ContentRepositoryRegistry.getInstance();
            ContentRepository repo = registry.getRepository(LoginManager.getPrimary());
            ContentCollection tempDir = (ContentCollection) repo.getRoot().getChild(dir);
            if (tempDir == null) {
                sessionDir = (ContentCollection) repo.getRoot().getChild("groups/users/" + SessionRecording.DIR_NAME);
                if (sessionDir == null) {
                    sessionDir = (ContentCollection) ((ContentCollection) repo.getRoot().getChild("groups/users")).createChild(SessionRecording.DIR_NAME, ContentNode.Type.COLLECTION);
                }
                tempDir = (ContentCollection) sessionDir.createChild("temp", ContentNode.Type.COLLECTION);
            }
            imageDir = (ContentCollection) tempDir.getChild(LoginManager.getPrimary().getUsername());
            if (imageDir != null) {
                tempDir.removeChild(imageDir.getName());
            }
            imageDir = (ContentCollection) tempDir.createChild(LoginManager.getPrimary().getUsername(), ContentNode.Type.COLLECTION);
        } catch (ContentRepositoryException ex) {
            Logger.getLogger(SessionRecorderCellRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
        LOGGER.log(Level.INFO, "{0}:createImageDirectory():End", this.getClass().getName());
        return imageDir;
    }

    /**
     * @return the buffered image to be displayed on the control panel
     */
    public JComponent getCaptureComponent() {
        return renderer.getCaptureComponent();
    }

    /**
     * removes the capture component so stop collection it from the panel
     */
    public void removeCaptureComponent() {
        renderer.removeCaptureComponent();
    }

    /**
     * this class is used to check whether the window is closed or not
     */
    private class WindowCloseListener extends WindowAdapter {

        /**
         * this method is called when windows close event occurs
         *
         * @param e
         */
        @Override
        public void windowClosing(WindowEvent e) {
            //checks wheter the recording initiated by the user and it is stoped or not
            //checks whether the data is saved or not
            LOGGER.log(Level.INFO, "{0}:windowClosing():End", this.getClass().getName());
            if (recording || recordingData != null) {
                recordingPanel.hudClosed(recordingPanelHUD);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SessionRecorderCell.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (winCloseListener != null) {
                JmeClientMain.getFrame().getFrame().removeWindowListener(winCloseListener);
                winCloseListener = null;
            }
            LOGGER.log(Level.INFO, "{0}:windowClosing():End", this.getClass().getName());
        }
    }
}
