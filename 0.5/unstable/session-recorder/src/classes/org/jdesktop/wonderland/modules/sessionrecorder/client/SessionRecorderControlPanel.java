/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
package org.jdesktop.wonderland.modules.sessionrecorder.client;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.xml.bind.JAXBException;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.hud.HUDComponent;
import org.jdesktop.wonderland.client.hud.HUDEvent;
import org.jdesktop.wonderland.client.hud.HUDEventListener;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.common.cell.messages.CellServerStateRequestMessage;
import org.jdesktop.wonderland.common.cell.messages.CellServerStateResponseMessage;
import org.jdesktop.wonderland.common.cell.messages.CellServerStateUpdateMessage;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.cell.state.PositionComponentServerState;
import org.jdesktop.wonderland.common.messages.ResponseMessage;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarImiJME;
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepository;
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepositoryRegistry;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentResource;
import static org.jdesktop.wonderland.modules.sessionrecorder.client.SessionRecorderCell.getSessionRecordingsDir;
import org.jdesktop.wonderland.modules.sessionrecorder.common.SessionRecordingConfiguration;

/**
 *
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
public class SessionRecorderControlPanel extends javax.swing.JPanel {

    private SessionRecorderCell recorderCell = null;
    private String studentName = "";
    private final List<String> selectedNameList = new ArrayList<String>();
    private final List<String> selectedIdList = new ArrayList<String>();
    private final List<Cell> selectedCellList = new ArrayList<Cell>();
    private String role = "";
    private Clip[] clips = null;
    private long clipTime = 0;
    private int maxLength = 1;
    private Timer timer = null;
    private boolean pause = false;
    private static final String[] roles = {"Patient", "Medical Provider", "Medical Staff", "Family/Friend of Patient", "Other"};
    private final HashMap<String, String> userData = new HashMap<String, String>();
    //used for not repopulating the users while previewing the output of the camera
    private boolean forPreview = false;
    private Thread t1 = null;
    private static final Logger LOGGER = Logger.getLogger(SessionRecorderControlPanel.class.getName());

    /**
     * Creates new form RolePlaySetupPanel
     *
     * @param recorderCell
     */
    public SessionRecorderControlPanel(final SessionRecorderCell recorderCell) {
        this.recorderCell = recorderCell;
        t1 = new Thread(new Runnable() {

            public void run() {
                try {
            ContentNode node = (ContentNode) getSessionRecordingsDir(LoginManager.getPrimary()).getChild("Data/SessionRecorderConfiguration");
            if (node != null && node instanceof ContentResource) {
                SessionRecordingConfiguration config = recorderCell.getRecorderConfiguration((ContentResource) node);
                maxLength = config.getMAX_LENGTH();
                LOGGER.log(Level.INFO, "maxLength === {0}", maxLength);
            }
        } catch (ContentRepositoryException ex) {
            Logger.getLogger(SessionRecorderCell.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(SessionRecorderCell.class.getName()).log(Level.SEVERE, null, ex);
        }
            }
        });
        t1.start();
        
        initComponents();
        initials();
    }

    /**
     * populate data and modify components
     */
    public final void initials() {
        LOGGER.log(Level.INFO, "{0}:initials():Start", this.getClass().getName());
        //populate logged in avatars
        cameraNameTextField.setText(recorderCell.getName());
        populateCameraUsers();
        previewPanel.setVisible(false);
        recordingPanel.setVisible(false);
        audioVideoRadioButton.setSelected(true);
        playButton.setEnabled(false);
        pauseButton.setEnabled(false);
        submitButton.setEnabled(false);
        recordButton.setEnabled(true);
        playButton.setEnabled(false);
        pauseButton.setEnabled(false);
        loadingLabel.setVisible(false);
        LOGGER.log(Level.INFO, "{0}:initials():End", this.getClass().getName());
        
    }

    /**
     * this method provides the list of avatar available in world and displays
     * it in the @param userListPanel
     */
    private void populateCameraUsers() {
        LOGGER.log(Level.INFO, "{0}:populateCameraUsers():Start", this.getClass().getName());
        userListPanel.removeAll();
        List<Cell> avatarCells = recorderCell.getAvatarCells();
        userListPanel.add(new ParticipantHeadingPanel());
        for (Cell cell : avatarCells) {
            AvatarImiJME rend = (AvatarImiJME) cell.getCellRenderer(Cell.RendererType.RENDERER_JME);
            imi.character.Character avatar = rend.getAvatarCharacter();
            ParticipantPanel participantPanel = new ParticipantPanel(cell, roles);

            if (!selectedNameList.isEmpty()
                    && selectedNameList.contains(avatar.getCharacterParams().getName())) {
                participantPanel.getCellCheckbox().setSelected(true);
                participantPanel.getRoleCombobox()
                        .setSelectedItem(userData.get(avatar.getCharacterParams().getName()));
            }
            userListPanel.add(participantPanel);
        }
        
        userListPanel.revalidate();
        userListPanel.repaint();
        userListScrollPane.repaint();
        LOGGER.log(Level.INFO, "{0}:populateCameraUsers():End", this.getClass().getName());
    }

    /**
     * stop recording when we close HUD
     *
     * @param panelHUD
     */
    void addHUDEventListener(final HUDComponent panelHUD) {
        panelHUD.addEventListener(new HUDEventListener() {

            public void HUDObjectChanged(HUDEvent event) {
                LOGGER.log(Level.INFO, "{0}:HUDObjectChanged():Start", this.getClass().getName());
                if (event.getEventType().equals(HUDEvent.HUDEventType.CLOSED)) {
                    hudClosed(panelHUD);
                } else if (event.getEventType().equals(HUDEvent.HUDEventType.RESIZED)) {
                    if (!forPreview) {
                        populateCameraUsers();
                    }
                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            if (cameraNameTextField != null && cameraNameTextField.isVisible()) {
                                cameraNameTextField.setText(recorderCell.getName());
                            }
                        }
                    });
                } else if (event.getEventType().equals(HUDEvent.HUDEventType.APPEARED)) {
                    populateCameraUsers();
                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            if (cameraNameTextField != null && cameraNameTextField.isVisible()) {
                                cameraNameTextField.setText(recorderCell.getName());
                            }
                        }
                    });
                }
                LOGGER.log(Level.INFO, "{0}:HUDObjectChanged():End", this.getClass().getName());
            }
        });
    }

    /**
     * this method is called when the HUD is closed
     *
     * @param hudComp
     */
    void hudClosed(HUDComponent hudComp) {
        LOGGER.log(Level.INFO, "{0}:hudClosed():Start", this.getClass().getName());
        if (recorderCell.getSessionRecordingData() != null) {
            if (!recordButton.getText().equalsIgnoreCase("Record")) {
                recording("Stop");
            }
            int r = confirmationDialog();
            if (r == 0) {
                saveRecordingData();
            } else if (r == 1) {
                new Thread(new Runnable() {

                    public void run() {
                        discardRecording();
                    }
                }).start();
            } else if (r == 2) {
                hudComp.setVisible(true);
            }
        }
        LOGGER.log(Level.INFO, "{0}:hudClosed():End", this.getClass().getName());
    }

    /**
     * modify student and participants when click on next
     */
    private void next() {
        LOGGER.log(Level.INFO, "{0}:next():Start", this.getClass().getName());
        studentName = "";
        selectedNameList.clear();
        selectedIdList.clear();
        selectedCellList.clear();

        role = "";
        Cell primaryCell = ClientContextJME.getViewManager().getPrimaryViewCell();
        AvatarImiJME rend = (AvatarImiJME) primaryCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
        imi.character.Character avatar = rend.getAvatarCharacter();
        studentName = avatar.getCharacterParams().getName();

        for (int j = 0; j < userListPanel.getComponentCount(); j++) {
            Component comp = userListPanel.getComponent(j);
            if (comp instanceof ParticipantPanel) {
                ParticipantPanel panel = (ParticipantPanel) comp;
                if ((panel.getCellCheckbox()).isSelected()) {
                    if (panel.getRoleCombobox().getName().equals(panel.getCellCheckbox().getText())) {
                        if (panel.getCellCheckbox().getText().equals(studentName)) {
                            selectedIdList.add(0, panel.getCell().getCellID().toString());
                            selectedNameList.add(0, studentName);
                            selectedCellList.add(0,panel.getCell());
                            role = (panel.getRoleCombobox().getSelectedItem().toString()) + "-" + role;
                        } else {
                            selectedIdList.add(panel.getCell().getCellID().toString());
                            selectedNameList.add(panel.getCellCheckbox().getText());
                            selectedCellList.add(panel.getCell());
                            role = role + (panel.getRoleCombobox().getSelectedItem().toString()) + "-";
                        }
                        userData.put(panel.getCellCheckbox().getText(), panel.getRoleCombobox().getSelectedItem().toString());
                    }
                }
            }
        }

        LOGGER.log(Level.INFO, "selectedParticipantsId : {0}", selectedIdList);
        LOGGER.log(Level.INFO, "selectedParticipantsName : {0}", selectedNameList);

        if (previewButton.getText().contains("Hide")) {
            previewButton.setText("See Preview");
            hidePreview();
        }
        if(t1 != null)
        {
            try {
                t1.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SessionRecorderControlPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        jLabel7.setText("("+maxLength+" minute maximum)");
        setupPanel.setVisible(false);
        recordingPanel.setVisible(true);
        forPreview = false;
        LOGGER.log(Level.INFO, "{0}:next():End", this.getClass().getName());
    }

    /**
     * change the recording name from the control panel and save that name for
     * that role play recorder
     */
    private void changeCellName() {
        LOGGER.log(Level.INFO, "{0}:changeCellName():Start", this.getClass().getName());
        String cameraName = cameraNameTextField.getText();
        LOGGER.log(Level.INFO, "Camera name = {0}", cameraName);
        if (!recorderCell.getName().equals(cameraName)) {
            recorderCell.setName(cameraName);
            LOGGER.log(Level.INFO, "Setting recorder cell to {0}", cameraName);
            CellServerState serverState = getServerState(recorderCell);
            serverState.setName(cameraName);
            serverState.removeComponentServerState(PositionComponentServerState.class);
            CellServerStateUpdateMessage message
                    = new CellServerStateUpdateMessage(recorderCell.getCellID(), serverState, null);
            recorderCell.sendCellMessageAndWait(message);
        }
        LOGGER.log(Level.INFO, "{0}:createSceneGraph():End", this.getClass().getName());
    }

    /**
     * this method is used to fetch the server state of cell
     */
    private CellServerState getServerState(Cell cell) {
        LOGGER.log(Level.INFO, "{0}:getServerState():Start", this.getClass().getName());
        try {
            ResponseMessage rm = cell.sendCellMessageAndWait(
                    new CellServerStateRequestMessage(cell.getCellID()));
            if (rm == null) {
                return null;
            }
            CellServerStateResponseMessage stateMessage
                    = (CellServerStateResponseMessage) rm;
            CellServerState state = stateMessage.getCellServerState();
            LOGGER.log(Level.INFO, "{0}:getServerState():End", this.getClass().getName());
            return state;
        } catch (Exception ex) {
            Logger.getLogger(SessionRecorderControlPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        LOGGER.log(Level.INFO, "{0}:getServerState():End", this.getClass().getName());
        return null;
    }

    /**
     * hide & show appropriate panels when click on back
     */
    private void back() {
        LOGGER.log(Level.INFO, "{0}:back():Start", this.getClass().getName());
        if (recorderCell.getSessionRecordingData() != null) {
            int r = confirmationDialog();
            if (r == 0) {
                saveRecordingData();
                recordingPanel.setVisible(false);
                setupPanel.setVisible(true);
            } else if (r == 1) {
                new Thread(new Runnable() {

                    public void run() {
                        discardRecording();
                        recordingPanel.setVisible(false);
                        setupPanel.setVisible(true);
                    }
                }).start();
            }
        } else {
            recordingPanel.setVisible(false);
            setupPanel.setVisible(true);
        }
        LOGGER.log(Level.INFO, "{0}:back():End", this.getClass().getName());
    }

    /**
     * displays the confirm dialog with save, discard and cancel as the options
     *
     * @return the value according to the option selected by the users
     */
    private int confirmationDialog() {
        String[] options = new String[3];
        options[0] = new String("Save");
        options[1] = new String("Discard");
        options[2] = new String("Cancel");
        return JOptionPane.showOptionDialog(JmeClientMain.getFrame().getFrame(), "The previous recording is not saved. Kindly select from the following options", "Are you sure?", 1, JOptionPane.WARNING_MESSAGE, null, options, null);
    }

    /**
     * just close panel
     */
    void cancel() {
        LOGGER.log(Level.INFO, "{0}:cancel():Start", this.getClass().getName());
        if (recorderCell.getSessionRecordingData() != null) {
            int r = confirmationDialog();
            if (r == 0) {
                saveRecordingData();
                recorderCell.closeControlPanel();
            } else if (r == 1) {
                new Thread(new Runnable() {

                    public void run() {
                        discardRecording();
                        recorderCell.closeControlPanel();
                    }
                }).start();
            }
        } else {
            recorderCell.closeControlPanel();
        }
        LOGGER.log(Level.INFO, "{0}:cancel():End", this.getClass().getName());
    }

    /**
     * start or stop recording based on action command
     *
     * @param actionCommand
     */
    void recording(String actionCommand) {
        LOGGER.log(Level.INFO, "{0}:recording():Start", this.getClass().getName());
        if (actionCommand.equals("Record")) {
            if (recorderCell.getSessionRecordingData() == null) {
                startRecording();
            } else {
                int result = confirmationDialog();
                if (result == 0) {
                    saveRecordingData();
                    startRecording();
                } else if (result == 1) {
                    new Thread(new Runnable() {

                        public void run() {
                            discardRecording();
                            startRecording();
                        }
                    }).start();
                } else if (result == 2) {

                }
            }

        } else {
            //stop recording
            LOGGER.info("STOPPING THE RECORDING");
            recordButton.setText("Record");
            recordButton.setIcon(new ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/sessionrecorder/client/resources/start-recording-icon.png")));
            recordButton.setBorder(null);
            recorderCell.stopRecording();
            backButton.setEnabled(true);
            cancelButton.setEnabled(true);
            playButton.setEnabled(true);
            submitButton.setEnabled(true);
            statusLabel.setText("Click \"Record\" to re-record or \"Play\" to listen");
            timer.stop();
        }
        LOGGER.log(Level.INFO, "{0}:recording():End", this.getClass().getName());
    }

    /**
     * updates the control panel when clicked on start recording
     */
    private void startRecording() {
        LOGGER.log(Level.INFO, "{0}:startRecording():Start", this.getClass().getName());
        //start recording
        recordButton.setText("Stop");
        recordButton.setIcon(new ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/sessionrecorder/client/resources/stop-recording-icon.png")));
        recordButton.setBorder(null);
        recorderCell.startRecording();
        backButton.setEnabled(false);
        cancelButton.setEnabled(false);
        playButton.setEnabled(false);
        submitButton.setEnabled(false);
        clips = null;
        startTimer();
        LOGGER.log(Level.INFO, "{0}:startRecording():End", this.getClass().getName());
    }

    /**
     * playback recorded audio, first locally get the data and play
     */
    private void play() {
        LOGGER.log(Level.INFO, "{0}:play():Start", this.getClass().getName());
        try {
            if (clips == null) {
                int i = 0;
                clips = new Clip[selectedNameList.size()];
                for (String user : selectedNameList) {
                    ContentRepository repo = ContentRepositoryRegistry.getInstance().getRepository(LoginManager.getPrimary());
                    ContentCollection audioDir = (ContentCollection) repo.getSystemRoot().getChild("AudioRecordings");
                    ContentResource audioFileResource = (ContentResource) audioDir.getChild(recorderCell.getVideoRecordingName() + "-" + user + ".au");
                    InputStream inputStream = audioFileResource.getInputStream();
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    int nRead;
                    byte[] data = new byte[16384];
                    while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }
                    buffer.flush();

                    AudioInputStream localInputStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(buffer.toByteArray()));
                    AudioFormat format = localInputStream.getFormat();
                    DataLine.Info info = new DataLine.Info(Clip.class, format);
                    clips[i] = (Clip) AudioSystem.getLine(info);
                    clips[i].open(localInputStream);
                    if (i == 0) {
                        clips[i].addLineListener(new PlaybackClipListener(clips[i]));
                    }
                    clipTime = 0;
                    i++;
                }

            }
            if (clipTime < clips[0].getMicrosecondLength()) {
                for (int i = 0; i < selectedNameList.size(); i++) {
                    clips[i].setMicrosecondPosition(clipTime);
                }
            } else {
                for (int i = 0; i < selectedNameList.size(); i++) {
                    clips[i].setMicrosecondPosition(0);
                }
            }

            for (int i = 0; i < selectedNameList.size(); i++) {
                clips[i].start();
            }

            playButton.setEnabled(false);
            pauseButton.setEnabled(true);
        } catch (ContentRepositoryException ex) {
            Logger.getLogger(SessionRecorderControlPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(SessionRecorderControlPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SessionRecorderControlPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(SessionRecorderControlPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        LOGGER.log(Level.INFO, "{0}:play():End", this.getClass().getName());
    }

    /**
     * stop the audio and get the current position from clip
     */
    private void pause() {
        LOGGER.log(Level.INFO, "{0}:pause():Start", this.getClass().getName());
        clipTime = clips[0].getMicrosecondPosition();
        pause = true;
        for (int i = 0; i < selectedNameList.size(); i++) {
            clips[i].stop();
        }
        playButton.setEnabled(true);
        pauseButton.setEnabled(false);
        LOGGER.log(Level.INFO, "{0}:pause():End", this.getClass().getName());
    }

    /**
     * Submit the data to server
     */
    private void saveRecordingData() {
        LOGGER.log(Level.INFO, "{0}:saveRecordingData():Start", this.getClass().getName());
        submitButton.setEnabled(false);
        new Thread(new Runnable() {

            public void run() {
                if (audioVideoRadioButton.isSelected()) {
                    loadingLabel.setVisible(true);
                    boolean done = recorderCell.uploadDone();
                    while (!done) {
                        done = recorderCell.uploadDone();
                    }
                    loadingLabel.setVisible(false);
                }
                recorderCell.getSessionRecordingData().setTagsCS(tagsTextfield.getText());
                recorderCell.saveRecordingData();
            }
        }).start();
        LOGGER.log(Level.INFO, "{0}:saveRecordingData():End", this.getClass().getName());
    }

    /**
     * discards the current recording and resets the panel accordingly
     */
    private void discardRecording() {
        LOGGER.log(Level.INFO, "{0}:discardRecording():Start", this.getClass().getName());
        loadingLabel.setVisible(true);
        loadingLabel.setText("Discarding...");
        recorderCell.discardRecording();
        recordButton.setEnabled(true);
        playButton.setEnabled(false);
        pauseButton.setEnabled(false);
        loadingLabel.setVisible(false);
        loadingLabel.setText("Uploading...");
        LOGGER.log(Level.INFO, "{0}:discardRecording():End", this.getClass().getName());
    }

    String getStudentName() {
        return studentName;
    }

    List<String> getSelectedNameList() {
        return selectedNameList;
    }

    List<String> getSelectedIdList() {
        return selectedIdList;
    }

    public List<Cell> getSelectedCellList() {
        return selectedCellList;
    }

    boolean isOnlyAudio() {
        return onlyAudioRadioButton.isSelected();
    }

    public HashMap<String, String> getUserData() {
        return userData;
    }

    /**
     * attached the timer listener and checks the duration with the max length
     */
    private void startTimer() {
        LOGGER.log(Level.INFO, "{0}:startTimer():Start", this.getClass().getName());
        //start timer
        statusLabel.setText("<html>RECORDING..<b>00:00</b>..Click \"Stop\" to end.</html>");
        TimerListener tl = new TimerListener();
        timer = new Timer(1000, tl);
        timer.start();
        LOGGER.log(Level.INFO, "{0}:startTimer():End", this.getClass().getName());
    }

    /**
     * to know if click has finished. Then update some components
     */
    private class PlaybackClipListener implements LineListener {

        Clip clip;

        public PlaybackClipListener(Clip clip) {
            this.clip = clip;
        }

        public void update(LineEvent event) {
            if (!clip.isRunning()) {
                playButton.setEnabled(true);
                pauseButton.setEnabled(false);
                if (!pause) {
                    clipTime = 0;
                } else {
                    pause = false;
                }
            }
        }
    }

    /**
     * Timer listener
     */
    private class TimerListener extends AbstractAction {

        private int currSec = 1;
        private final long maxSec;

        public TimerListener() {
            maxSec = maxLength * 60;
        }

        public void actionPerformed(ActionEvent e) {
            long diff = maxSec - currSec;
            if (diff < 0) {
                //stop recording
                recording("Stop");
            } else {
                long currMin = currSec / 60;
                String currMinS = "";
                if (currMin < 10) {
                    currMinS = "0" + String.valueOf(currMin);
                } else {
                    currMinS = String.valueOf(currMin);
                }

                long currSecc = currSec % 60;
                String currSecS = "";
                if (currSecc < 10) {
                    currSecS = "0" + String.valueOf(currSecc);
                } else {
                    currSecS = String.valueOf(currSecc);
                }
                statusLabel.setText("<html>RECORDING..<b>" + currMinS + ":" + currSecS + "</b>..Click \"Stop\" to end.</html>");
            }
            currSec++;
        }
    }

    /**
     * previews the captured frame from camera
     */
    private void showPreview() {
        LOGGER.log(Level.INFO, "{0}:showPreview():Start", this.getClass().getName());
        forPreview = true;
        previewPanel.setLayout(new GridBagLayout());
        JComponent preview = recorderCell.getCaptureComponent();
        previewPanel.add(preview);
        previewPanel.setVisible(true);
        LOGGER.log(Level.INFO, "{0}:showPreview():End", this.getClass().getName());
    }

    /**
     * remove the attached preview
     */
    private void hidePreview() {
        LOGGER.log(Level.INFO, "{0}:hidePreview():Start", this.getClass().getName());
        recorderCell.removeCaptureComponent();
        previewPanel.setVisible(false);
        previewPanel.removeAll();
        LOGGER.log(Level.INFO, "{0}:hidePreview():End", this.getClass().getName());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        setupPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        userListScrollPane = new javax.swing.JScrollPane();
        userListPanel = new javax.swing.JPanel();
        nextButton = new javax.swing.JButton();
        previewPanel = new javax.swing.JPanel();
        previewButton = new javax.swing.JButton();
        recordingPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        onlyAudioRadioButton = new javax.swing.JRadioButton();
        audioVideoRadioButton = new javax.swing.JRadioButton();
        statusLabel = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        tagsTextfield = new javax.swing.JTextField();
        submitButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        recordButton = new javax.swing.JButton();
        playButton = new javax.swing.JButton();
        pauseButton = new javax.swing.JButton();
        backButton = new javax.swing.JButton();
        loadingLabel = new javax.swing.JLabel();
        cameraLabel = new javax.swing.JLabel();
        cameraNameTextField = new javax.swing.JTextField();

        setupPanel.setPreferredSize(new java.awt.Dimension(360, 100));

        jLabel1.setFont(new java.awt.Font("Verdana", 0, 13)); // NOI18N
        jLabel1.setText("Select Participants to record : ");

        userListPanel.setLayout(new javax.swing.BoxLayout(userListPanel, javax.swing.BoxLayout.Y_AXIS));
        userListScrollPane.setViewportView(userListPanel);

        nextButton.setFont(new java.awt.Font("Verdana", 0, 13)); // NOI18N
        nextButton.setText("Next");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        previewPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        previewPanel.setPreferredSize(new java.awt.Dimension(345, 0));

        javax.swing.GroupLayout previewPanelLayout = new javax.swing.GroupLayout(previewPanel);
        previewPanel.setLayout(previewPanelLayout);
        previewPanelLayout.setHorizontalGroup(
            previewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 329, Short.MAX_VALUE)
        );
        previewPanelLayout.setVerticalGroup(
            previewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        previewButton.setFont(new java.awt.Font("Verdana", 0, 13)); // NOI18N
        previewButton.setText("See Preview");
        previewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previewButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout setupPanelLayout = new javax.swing.GroupLayout(setupPanel);
        setupPanel.setLayout(setupPanelLayout);
        setupPanelLayout.setHorizontalGroup(
            setupPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(setupPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(setupPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, setupPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(previewButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nextButton))
                    .addComponent(userListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(setupPanelLayout.createSequentialGroup()
                        .addGroup(setupPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(previewPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        setupPanelLayout.setVerticalGroup(
            setupPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(setupPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(userListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(setupPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nextButton)
                    .addComponent(previewButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(previewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 5, Short.MAX_VALUE)
                .addGap(9, 9, 9))
        );

        jLabel3.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel3.setText("Use the controls below to record your session. ");

        jLabel5.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N
        jLabel6.setText("Record");

        jLabel7.setFont(new java.awt.Font("Verdana", 0, 13)); // NOI18N
        jLabel7.setText("("+maxLength+" minute maximum)");

        buttonGroup1.add(onlyAudioRadioButton);
        onlyAudioRadioButton.setFont(new java.awt.Font("Verdana", 0, 13)); // NOI18N
        onlyAudioRadioButton.setText("Record Audio Only");
        onlyAudioRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onlyAudioRadioButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(audioVideoRadioButton);
        audioVideoRadioButton.setFont(new java.awt.Font("Verdana", 0, 13)); // NOI18N
        audioVideoRadioButton.setSelected(true);
        audioVideoRadioButton.setText("Record Audio + Video");
        audioVideoRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                audioVideoRadioButtonActionPerformed(evt);
            }
        });

        statusLabel.setFont(new java.awt.Font("Verdana", 2, 13)); // NOI18N
        statusLabel.setForeground(new java.awt.Color(51, 51, 51));
        statusLabel.setText("Click \"Record\" to begin");

        jLabel9.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N
        jLabel9.setText("Tags : ");

        submitButton.setFont(new java.awt.Font("Verdana", 0, 13)); // NOI18N
        submitButton.setText("Submit");
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButtonActionPerformed(evt);
            }
        });

        cancelButton.setFont(new java.awt.Font("Verdana", 0, 13)); // NOI18N
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        recordButton.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        recordButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/sessionrecorder/client/resources/start-recording-icon.png"))); // NOI18N
        recordButton.setText("Record");
        recordButton.setBorder(null);
        recordButton.setContentAreaFilled(false);
        recordButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        recordButton.setPreferredSize(new java.awt.Dimension(64, 78));
        recordButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        recordButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recordButtonActionPerformed(evt);
            }
        });

        playButton.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        playButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/sessionrecorder/client/resources/play-icon.png"))); // NOI18N
        playButton.setText("Play");
        playButton.setBorder(null);
        playButton.setContentAreaFilled(false);
        playButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        playButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        playButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playButtonActionPerformed(evt);
            }
        });

        pauseButton.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        pauseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/sessionrecorder/client/resources/pause-icon.png"))); // NOI18N
        pauseButton.setText("Pause");
        pauseButton.setBorder(null);
        pauseButton.setContentAreaFilled(false);
        pauseButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pauseButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pauseButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pauseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(recordButton, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(playButton, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pauseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(playButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(recordButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pauseButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        backButton.setFont(new java.awt.Font("Verdana", 0, 13)); // NOI18N
        backButton.setText("Back");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        loadingLabel.setText("Uploading...");

        javax.swing.GroupLayout recordingPanelLayout = new javax.swing.GroupLayout(recordingPanel);
        recordingPanel.setLayout(recordingPanelLayout);
        recordingPanelLayout.setHorizontalGroup(
            recordingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(recordingPanelLayout.createSequentialGroup()
                .addComponent(onlyAudioRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(audioVideoRadioButton)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(recordingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(recordingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(recordingPanelLayout.createSequentialGroup()
                        .addGap(204, 204, 204)
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(recordingPanelLayout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tagsTextfield))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, recordingPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(loadingLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(submitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(recordingPanelLayout.createSequentialGroup()
                        .addGroup(recordingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(recordingPanelLayout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7))
                            .addComponent(statusLabel))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        recordingPanelLayout.setVerticalGroup(
            recordingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(recordingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(recordingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(recordingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(onlyAudioRadioButton)
                    .addComponent(audioVideoRadioButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(recordingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(tagsTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(recordingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(submitButton)
                    .addComponent(cancelButton)
                    .addComponent(backButton)
                    .addComponent(loadingLabel))
                .addGap(8, 8, 8))
        );

        cameraLabel.setFont(new java.awt.Font("Verdana", 1, 13)); // NOI18N
        cameraLabel.setText("Camera : ");

        cameraNameTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cameraNameTextFieldFocusLost(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(setupPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 347, Short.MAX_VALUE)
                    .addComponent(recordingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(cameraLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cameraNameTextField)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cameraLabel)
                    .addComponent(cameraNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(setupPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(recordingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        // TODO add your handling code here:
        next();
    }//GEN-LAST:event_nextButtonActionPerformed

    private void onlyAudioRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onlyAudioRadioButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_onlyAudioRadioButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        // TODO add your handling code here:
        back();
    }//GEN-LAST:event_backButtonActionPerformed

    private void recordButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recordButtonActionPerformed
        // TODO add your handling code here:
        recording(evt.getActionCommand());
    }//GEN-LAST:event_recordButtonActionPerformed

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed
        // TODO add your handling code here:
        saveRecordingData();
    }//GEN-LAST:event_submitButtonActionPerformed

    private void audioVideoRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_audioVideoRadioButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_audioVideoRadioButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // TODO add your handling code here:
        cancel();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void playButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playButtonActionPerformed
        // TODO add your handling code here:
        play();
    }//GEN-LAST:event_playButtonActionPerformed

    private void pauseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseButtonActionPerformed
        // TODO add your handling code here:
        pause();
    }//GEN-LAST:event_pauseButtonActionPerformed

    private void previewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previewButtonActionPerformed
        if (previewButton.getText().contains("See")) {
            showPreview();
            previewButton.setText("Hide Preview");
        } else {
            hidePreview();
            previewButton.setText("See Preview");
        }
    }//GEN-LAST:event_previewButtonActionPerformed

    private void cameraNameTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cameraNameTextFieldFocusLost
        // TODO add your handling code here:
        changeCellName();
    }//GEN-LAST:event_cameraNameTextFieldFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton audioVideoRadioButton;
    private javax.swing.JButton backButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel cameraLabel;
    private javax.swing.JTextField cameraNameTextField;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel loadingLabel;
    private javax.swing.JButton nextButton;
    private javax.swing.JRadioButton onlyAudioRadioButton;
    private javax.swing.JButton pauseButton;
    private javax.swing.JButton playButton;
    private javax.swing.JButton previewButton;
    private javax.swing.JPanel previewPanel;
    private javax.swing.JButton recordButton;
    private javax.swing.JPanel recordingPanel;
    private javax.swing.JPanel setupPanel;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JButton submitButton;
    private javax.swing.JTextField tagsTextfield;
    private javax.swing.JPanel userListPanel;
    private javax.swing.JScrollPane userListScrollPane;
    // End of variables declaration//GEN-END:variables
}
