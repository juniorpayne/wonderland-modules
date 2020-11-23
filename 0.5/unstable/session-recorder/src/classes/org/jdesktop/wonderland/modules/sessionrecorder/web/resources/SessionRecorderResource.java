/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
package org.jdesktop.wonderland.modules.sessionrecorder.web.resources;

import com.clickntap.vimeo.Vimeo;
import com.clickntap.vimeo.VimeoException;
import com.clickntap.vimeo.VimeoResponse;
import com.sun.enterprise.util.io.FileUtils;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilterReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentResource;
import org.jdesktop.wonderland.modules.contentrepo.web.spi.WebContentRepository;
import org.jdesktop.wonderland.modules.contentrepo.web.spi.WebContentRepositoryRegistry;
import org.jdesktop.wonderland.modules.sessionrecorder.common.SessionRecording;
import org.jdesktop.wonderland.modules.sessionrecorder.common.SessionRecordingConfiguration;
import org.jdesktop.wonderland.modules.sessionrecorder.common.GroupData;
import org.jdesktop.wonderland.modules.sessionrecorder.common.TranscribedData;
import org.json.JSONArray;

/**
 * Resource class to respond the rest calls
 *
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
@Path("/sessionRecordings")
public class SessionRecorderResource {

    private static final Logger LOGGER
            = Logger.getLogger(SessionRecorderResource.class.getName());
    private static JAXBContext context, configContext;
    private final WebContentRepository repo;
    private ContentCollection dir;
    private ServletContext servletContext;
//    private ResourceBundle bundle = ResourceBundle.getBundle("/my.module");
    private Vimeo vimeo = null;
    private boolean vimeoTokenUpdated = false;

    public SessionRecorderResource(@Context ServletContext context) {
        WebContentRepositoryRegistry reg = WebContentRepositoryRegistry.getInstance();
        repo = reg.getRepository(context);
        servletContext = context;

        // create directory if it doesn't exist
        try {
            dir = (ContentCollection) repo.getRoot().getChild("groups/users/" + SessionRecording.DIR_NAME + "/Data");
            if (dir == null) {
                throw new WebApplicationException(
                        Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Report directory not found").build());
            }
        } catch (ContentRepositoryException ce) {
            throw new WebApplicationException(ce, Response.Status.INTERNAL_SERVER_ERROR);
        }
//        System.err.println("reource string = "+bundle.getString("vimeo.key"));
    }

    /**
     * create audio and video
     *
     * @param recordingName
     * @param duration
     * @param participantsName
     * @param frameRate
     * @param frameCounter
     * @param username
     * @param tags
     * @param sessionRecordingID
     * @param repairImageSource
     */
    @POST
    @Path("/createAudioAndVideo")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void createAudioAndVideo(@FormParam("recordingName") final String recordingName,
            @FormParam("duration") final String duration, @FormParam("participantsName") final String participantsName,
            /*create movie parameters*/ @FormParam("frameRate") String frameRate, @FormParam("frameCounter") String frameCounter,
            @FormParam("username") String username, @FormParam("tags") String tags, @FormParam("sessionRecordingID") final String sessionRecordingID,
            @FormParam("repairImageSource") final String repairImageSource) {
        LOGGER.log(Level.INFO, "{0}:createAudioAndVideo():Start", this.getClass().getName());

        Thread t1 = new Thread(new Runnable() {

            public void run() {
                convertAudioFiles(recordingName, duration, participantsName);
            }
        });
        t1.start();
        try {
            //if files not coverted in 30 sec, leave it and create video without audio
            t1.join(30000);
        } catch (InterruptedException ex) {
            Logger.getLogger(SessionRecorderResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        createMovie(recordingName, frameRate, frameCounter, username, tags, sessionRecordingID, repairImageSource);
        LOGGER.log(Level.INFO, "{0}:createAudioAndVideo():End", this.getClass().getName());
    }

    private boolean findFiles(final String recordingName, final String participantsName) {
        LOGGER.log(Level.INFO, "participantsName : {0}", participantsName);
        JSONArray jsonArray = new JSONArray(participantsName);
        final List<String> participantlist = new ArrayList<String>();
        for (int i = 0; i < jsonArray.length(); i++) {
            participantlist.add(jsonArray.get(i).toString());
        }
        boolean filesFound = isFilesFound("a" + recordingName, participantlist);
        LOGGER.log(Level.INFO, "Finding files....");
        final Date d = new Date();
        int counter = 30;
        LOGGER.log(Level.INFO, "Before While....{0}", d);
        while (!filesFound) {
            LOGGER.log(Level.WARNING, "Inside while....counter == {0}", counter);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(SessionRecorderResource.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (counter == 0) {
                LOGGER.log(Level.WARNING, "counter is 0");
                break;
            }
            counter--;
            filesFound = isFilesFound(recordingName, participantlist);
        }
        LOGGER.log(Level.INFO, "Ater While....{0}", (d.getTime() - (new Date()).getTime()) / 1000);
        if (filesFound) {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException ex) {
                Logger.getLogger(SessionRecorderResource.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        LOGGER.log(Level.INFO, "files found == " + filesFound);
        return filesFound;
    }

    private boolean isFilesFound(String recordingName, List<String> participantlist) {
        boolean filesFound = true;
        String path = servletContext.getRealPath("../../") + "/content/system/AudioRecordings/";
        File auDir = new File(path);
        for (String user : participantlist) {
            boolean matchFound = false;
            for (String name : auDir.list()) {
                if (name.equals(recordingName + "-" + user + ".au")) {
                    LOGGER.log(Level.WARNING, "File found wih name == {0}", name);
                    matchFound = true;
                }
            }
            if (!matchFound) {
                filesFound = false;
                break;
            }

        }
        return filesFound;
    }

    /**
     * separate the audio files according to the user and convert them from .au
     * to .wav generates the common audio .wav file with length of the first
     * file
     *
     * @param recordingName
     * @param duration
     * @param participantsName
     */
    @POST
    @Path("/convertAuToWav")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void convertAudioFiles(@FormParam("recordingName") final String recordingName, @FormParam("duration") final String duration, @FormParam("participantsName") final String participantsName) {
        LOGGER.log(Level.INFO, "{0}:convertAudioFiles():Start", this.getClass().getName());
        if (!findFiles(recordingName, participantsName)) {
            System.out.println("no files found");
            return;
        }
        ContentNode node;
        //convert the date and time to the server timezone
        try {
            node = (ContentNode) dir.getChild(recordingName);
            if (node != null) {
                SessionRecording recording = read((ContentResource) node);
                Date d = recording.getStartTime();
                LOGGER.log(Level.INFO, "+++++++ Start time = {0}", d);
                recording.setStartTime(d);
                d = recording.getEndTime();
                LOGGER.log(Level.INFO, "------- End time = {0}", d);
                recording.setEndTime(d);
                write(recording);
            }
        } catch (ContentRepositoryException ex) {
            Logger.getLogger(SessionRecorderResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(SessionRecorderResource.class.getName()).log(Level.SEVERE, null, ex);
        }

        String path = servletContext.getRealPath("../../");

        LOGGER.log(Level.INFO, "participantsName : {0}", participantsName);
        JSONArray jsonArray = new JSONArray(participantsName);
        List<String> participantlist = new ArrayList<String>();
        for (int i = 0; i < jsonArray.length(); i++) {
            participantlist.add(jsonArray.get(i).toString());
        }
        //convert the .au files to .wav files
        for (String userName : participantlist) {
            convertAutoWav(path + "/content/system/AudioRecordings/" + recordingName + "-" + userName + ".au", path + "/content/groups/users/" + SessionRecording.DIR_NAME + "/Audio/" + recordingName + "-" + userName + ".wav", Integer.parseInt(duration), recordingName, userName);
        }
        //create the common audio file
        if (participantlist.size() == 1) {
            try {
                File source = new File(path + "/content/groups/users/" + SessionRecording.DIR_NAME + "/Audio/" + recordingName + "-" + participantlist.get(0) + ".wav");
                File dest = new File(path + "/content/groups/users/" + SessionRecording.DIR_NAME + "/Audio/" + recordingName + ".wav");
                FileUtils.copy(source, dest);
            } catch (IOException ex) {
                Logger.getLogger(SessionRecorderResource.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            String ffmpeg = getFFmpegLocation();
            List<String> commandList = new ArrayList<String>();
            commandList.add(ffmpeg);
            for (String userName : participantlist) {
                commandList.add("-i");
                commandList.add(path + "/content/groups/users/" + SessionRecording.DIR_NAME + "/Audio/" + recordingName + "-" + userName + ".wav");
            }
            commandList.add("-filter_complex");
            commandList.add("amix=inputs=" + participantlist.size() + ":duration=first");
            commandList.add(path + "/content/groups/users/" + SessionRecording.DIR_NAME + "/Audio/" + recordingName + ".wav");

            executeFFmpegCommand(commandList.toArray(new String[commandList.size()]));
        }
        LOGGER.log(Level.INFO, "{0}:convertAudioFiles():End", this.getClass().getName());
    }

    /**
     * convert au file to wav
     *
     * @param sourcePath au file path
     * @param destPath wav file path
     */
    private void convertAutoWav(String sourcePath, String destPath, int duration, String recordingName, String userName) {
        LOGGER.log(Level.INFO, "{0}:convertAutoWav():Start", this.getClass().getName());
        LOGGER.log(Level.INFO, "converting started : {0}", userName);
        long start = new Date().getTime();

        //get the au file
        File sourceAu = new File(sourcePath);
        while (!sourceAu.exists()) {
            sourceAu = new File(sourcePath);
        }

        //create wav file
        final File destWav = new File(destPath);
        int actualDuration = 0;
        final AudioFileFormat.Type outputType
                = AudioFileFormat.Type.WAVE;
        AudioInputStream audioInputStream = null;
        LOGGER.log(Level.INFO, "Before while loop audioInputStream = {0}", audioInputStream);
        while (audioInputStream == null) {
            try {
                audioInputStream = AudioSystem.getAudioInputStream(sourceAu);
                if (audioInputStream != null) {
                    javax.sound.sampled.AudioFormat format = audioInputStream.getFormat();
                    long frames = audioInputStream.getFrameLength();
                    actualDuration = (int) ((frames + 0.0) / format.getFrameRate());
                    AudioSystem.write(audioInputStream, outputType, destWav);
                    LOGGER.log(Level.INFO, "After while loop audioInputStream = {0}", audioInputStream);
                }
            } catch (UnsupportedAudioFileException ex) {
                Logger.getLogger(SessionRecorderResource.class.getName()).log(Level.SEVERE, null, ex);
                break;
            } catch (IOException ex) {
                Logger.getLogger(SessionRecorderResource.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
        }

        long end = new Date().getTime();
        LOGGER.log(Level.INFO, "converting finished : {0} : {1}", new Object[]{userName, end - start});
        LOGGER.log(Level.INFO, "{0}:convertAutoWav():End", this.getClass().getName());
    }

    /**
     * creates the list of the recording files
     *
     * @param content
     * @return
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response list(@QueryParam("content") String content) {
        LOGGER.log(Level.INFO, "{0}:list():Start", this.getClass().getName());
        try {
            List<SessionRecording> recordings = new ArrayList<SessionRecording>();

            for (ContentNode node : dir.getChildren()) {
                if ((node instanceof ContentResource) && !node.getName().contains("wav") && !node.getName().contains("mov") && !node.getName().equals("SessionRecorderConfiguration")) {
                    try {
                        SessionRecording report = read((ContentResource) node);
                        recordings.add(report);
                    } catch (JAXBException je) {
                        LOGGER.log(Level.SEVERE, "Error reading " + node.getName(), je);
                    }
                }
            }
            LOGGER.log(Level.INFO, "{0}:list():End", this.getClass().getName());
            return Response.ok(new SessionRecordingList(recordings)).build();
        } catch (Exception ce) {
            throw new WebApplicationException(ce, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * updates the recording name to be displayed on the web part
     *
     * @param id
     * @param recordingName
     * @return
     * @throws IOException
     */
    @POST
    @Path("updateRecordingName")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response updateRecordingName(@FormParam("id") String id,
            @FormParam("recordingName") String recordingName) throws IOException {
        LOGGER.log(Level.INFO, "{0}:updateRecordingName():Start", this.getClass().getName());

        LOGGER.log(Level.INFO, "updateRecordingName : {0}", recordingName);

        try {
            //get the recording
            ContentNode node = dir.getChild(id);
            if (node == null || !(node instanceof ContentResource)) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            final SessionRecording recording = read((ContentResource) node);
            recording.setRecordingName(recordingName);
            write(recording);
            LOGGER.log(Level.INFO, "{0}:updateRecordingName():End", this.getClass().getName());
            return Response.ok(recording).build();
        } catch (ContentRepositoryException ce) {
            throw new WebApplicationException(ce, Response.Status.INTERNAL_SERVER_ERROR);
        } catch (JAXBException ex) {
            throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * save or update the transcription data
     *
     * @param id
     * @param transcriptionId
     * @param startTime
     * @param endTime
     * @param top
     * @param note
     * @return
     * @throws IOException
     */
    @POST
    @Path("transcription/save")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response saveTranscriptionData(@FormParam("id") String id,
            @FormParam("transcriptionId") String transcriptionId,
            @FormParam("startTime") String startTime,
            @FormParam("endTime") String endTime,
            @FormParam("top") String top,
            @FormParam("note") String note) throws IOException {
        LOGGER.log(Level.INFO, "{0}:saveTranscriptionData():Start", this.getClass().getName());
        try {
            //get the recording
            ContentNode node = dir.getChild(id);
            if (node == null || !(node instanceof ContentResource)) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            final SessionRecording recording = read((ContentResource) node);
            TranscribedData transcriptionData = null;

            //decide save or update transcription?
            boolean update = false;
            if (transcriptionId != null && !transcriptionId.trim().equals("")) {
                for (TranscribedData tData : recording.getTranscribedData()) {
                    if (tData.getTranscriptionId().equals(transcriptionId.trim())) {
                        transcriptionData = tData;
                        update = true;
                        break;
                    }
                }
            }

            Calendar calendarST = Calendar.getInstance();
            calendarST.setTime(recording.getStartTime());
            calendarST.add(Calendar.MILLISECOND, (int) (Double.parseDouble(String.valueOf(startTime)) * 1000));

            Calendar calendarET = Calendar.getInstance();
            calendarET.setTime(recording.getStartTime());
            calendarET.add(Calendar.MILLISECOND, (int) (Double.parseDouble(String.valueOf(endTime)) * 1000));

            if (!update) {
                transcriptionData = new TranscribedData();
            } else {
                if (transcriptionData != null) {
                    if (transcriptionData.getStartTime().equals(calendarST.getTime())
                            && transcriptionData.getEndTime().equals(calendarET.getTime())
                            && transcriptionData.getTranscriptionNote().equals(note)
                            && transcriptionData.getTop() == Integer.parseInt(top)) {
                        return null;
                    }
                }
            }

            if (transcriptionData != null) {
                transcriptionData.setTranscriptionId(transcriptionId);
                transcriptionData.setStartTime(calendarST.getTime());
                transcriptionData.setEndTime(calendarET.getTime());
                transcriptionData.setTop(Integer.parseInt(top));
                transcriptionData.setTranscriptionNote(note);
            }

            if (!update) {
                recording.getTranscribedData().add(transcriptionData);
            }
            LOGGER.info("transcription updated...");
            write(recording);
            LOGGER.log(Level.INFO, "{0}:saveTranscriptionData():End", this.getClass().getName());
            return Response.ok(recording).build();
        } catch (Exception ex) {
            Logger.getLogger(SessionRecorderResource.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * sets the boolean transcribed flag in the session recording
     *
     * @param id
     * @param transcribed
     * @return
     * @throws IOException
     */
    @POST
    @Path("recording/transcribed")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response updateTranscribed(@FormParam("id") String id,
            @FormParam("transcribed") String transcribed) throws IOException {
        LOGGER.log(Level.INFO, "{0}:updateTranscribed():Start transcribed = {1}", new Object[]{this.getClass().getName(), transcribed});

        try {
            //get the recording
            ContentNode node = dir.getChild(id);
            if (node == null || !(node instanceof ContentResource)) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            final SessionRecording recording = read((ContentResource) node);
            recording.setTranscribed(Boolean.parseBoolean(transcribed));
            write(recording);
            LOGGER.log(Level.INFO, "{0}:updateTranscribed():End", this.getClass().getName());
            return Response.ok(recording).build();
        } catch (ContentRepositoryException ce) {
            throw new WebApplicationException(ce, Response.Status.INTERNAL_SERVER_ERROR);
        } catch (JAXBException ex) {
            throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * get the data of the recording with given id
     *
     * @param idWithXML
     * @return
     */
    @GET
    @Path("get/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response get(@PathParam("id") String idWithXML) {
        LOGGER.log(Level.INFO, "{0}:get():Start", this.getClass().getName());
        try {
            ContentNode node = dir.getChild(idWithXML);
            if (node == null || !(node instanceof ContentResource)) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            final SessionRecording recording = read((ContentResource) node);
            if (recording == null) {
                LOGGER.log(Level.INFO, "{0}:get():End", this.getClass().getName());
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("No object with id " + idWithXML).build();
            }
            LOGGER.log(Level.INFO, "{0}:get():End", this.getClass().getName());
            return Response.ok(recording).build();
        } catch (ContentRepositoryException ce) {
            throw new WebApplicationException(ce, Response.Status.INTERNAL_SERVER_ERROR);
        } catch (JAXBException je) {
            throw new WebApplicationException(je, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * delete a specific transcription
     *
     * @param id
     * @param transcriptionId
     * @return
     * @throws IOException
     */
    @POST
    @Path("transcription/delete")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response deleteTranscription(@FormParam("id") String id,
            @FormParam("transcriptionId") String transcriptionId) throws IOException {
        LOGGER.log(Level.INFO, "{0}:deleteTranscription():Start", this.getClass().getName());
        try {
            //get the recording
            ContentNode node = dir.getChild(id);
            if (node == null || !(node instanceof ContentResource)) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            final SessionRecording recording = read((ContentResource) node);
            Iterator<TranscribedData> tDataItr = recording.getTranscribedData().iterator();
            while (tDataItr.hasNext()) {
                TranscribedData tData = tDataItr.next();
                if (tData.getTranscriptionId().equals(transcriptionId)) {
                    LOGGER.log(Level.INFO, "transcription found : id : {0}", transcriptionId);
                    tDataItr.remove();
                }
            }
            write(recording);
            LOGGER.log(Level.INFO, "{0}:deleteTranscription():End", this.getClass().getName());
            return Response.ok(recording).build();
        } catch (Exception ex) {
            Logger.getLogger(SessionRecorderResource.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private SessionRecording read(ContentResource resource)
            throws JAXBException, ContentRepositoryException {
        Unmarshaller unmarshaller = getContext().createUnmarshaller();

        // reports may contain illegal characters. Add a filter to
        // ignore all these characters.
        Reader in = new EscapeBadCharsReader(new InputStreamReader(resource.getInputStream()));
        SessionRecording report = (SessionRecording) unmarshaller.unmarshal(in);
        return report;
    }

    private SessionRecordingConfiguration readConfig(ContentResource resource)
            throws JAXBException, ContentRepositoryException {
        Unmarshaller unmarshaller = getConfigContext().createUnmarshaller();

        // reports may contain illegal characters. Add a filter to
        // ignore all these characters.
        Reader in = new EscapeBadCharsReader(new InputStreamReader(resource.getInputStream()));
        SessionRecordingConfiguration report = (SessionRecordingConfiguration) unmarshaller.unmarshal(in);
        return report;
    }

    private void write(SessionRecording recording)
            throws JAXBException, ContentRepositoryException {
        Marshaller marshaller = getContext().createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        ContentResource resource = (ContentResource) dir.getChild(recording.getId());
        if (resource == null) {
            resource = (ContentResource) dir.createChild(recording.getId(),
                    ContentNode.Type.RESOURCE);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshaller.marshal(recording, baos);
        resource.put(baos.toByteArray());
    }

    private void writeConfig(SessionRecordingConfiguration config)
            throws JAXBException, ContentRepositoryException {
        Marshaller marshaller = getConfigContext().createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        ContentResource resource = (ContentResource) dir.getChild("SessionRecorderConfiguration");
        if (resource == null) {
            resource = (ContentResource) dir.createChild("SessionRecorderConfiguration",
                    ContentNode.Type.RESOURCE);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshaller.marshal(config, baos);
        resource.put(baos.toByteArray());
    }

    private synchronized static JAXBContext getContext() throws JAXBException {
        if (context == null) {
            context = JAXBContext.newInstance(SessionRecording.class);
        }

        return context;
    }

    private synchronized static JAXBContext getConfigContext() throws JAXBException {
        if (configContext == null) {
            configContext = JAXBContext.newInstance(SessionRecordingConfiguration.class);
        }

        return configContext;
    }

    /**
     * wrapper of the recording list
     */
    @XmlRootElement(name = "session-recording-list")
    public static class SessionRecordingList {

        private final List<SessionRecording> recordings = new ArrayList<SessionRecording>();

        public SessionRecordingList() {
        }

        public SessionRecordingList(List<SessionRecording> recordings) {
            this.recordings.addAll(recordings);
        }

        @XmlElement
        public List<SessionRecording> getRecordings() {
            return recordings;
        }
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
     * get vimeo object here set api key of vimeo account
     *
     * @return
     */
    private Vimeo getVimeo() {
        LOGGER.log(Level.INFO, "{0}:getVimeo():Start", this.getClass().getName());
        String vimeoToken = ""; //sets the vimeo api key from configuration file
        if (vimeo == null || vimeoTokenUpdated) {
            try {
                ContentNode node = dir.getChild("SessionRecorderConfiguration");
                if (node == null || !(node instanceof ContentResource)) {
                    throw new WebApplicationException(Response.Status.NOT_FOUND);
                }
                final SessionRecordingConfiguration config = readConfig((ContentResource) node);
                vimeoToken = config.getVimeoToken();
            } catch (JAXBException ex) {
                Logger.getLogger(SessionRecorderResource.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ContentRepositoryException ex) {
                Logger.getLogger(SessionRecorderResource.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (!vimeoToken.equals("") && !vimeoToken.isEmpty()) {
                vimeo = new Vimeo(vimeoToken);
            }
            if (vimeoTokenUpdated) {
                vimeoTokenUpdated = false;
            }
        }
        LOGGER.log(Level.INFO, "{0}:getVimeo():End", this.getClass().getName());
        return vimeo;
    }

    /**
     * delete recording
     *
     * @param formParams
     */
    @POST
    @Path("deleteRecordingData")
    public void deleteRecordingData(MultivaluedMap<String, String> formParams) {
        LOGGER.log(Level.INFO, "{0}:deleteRecordingData():Start", this.getClass().getName());
        String ids = formParams.get("ids").toString();
        if (!ids.equals("[]")) {
            ids = ids.substring(1, ids.lastIndexOf("]"));
            List<String> idList = Arrays.asList(ids.split(","));
            for (String id : idList) {
                delete(id);
            }
        } else {
            LOGGER.warning("No data found");
        }
        LOGGER.log(Level.INFO, "{0}:deleteRecordingData():End", this.getClass().getName());
    }

    /**
     * delete audio, video and data file of recording
     *
     * @param id
     */
    void delete(String id) {
        LOGGER.log(Level.INFO, "{0}:delete():Start", this.getClass().getName());
        String path = servletContext.getRealPath("../../");
        String videoUrl = null;
        File sessionDir = new File(path + "/content/groups/users/" + SessionRecording.DIR_NAME);
        try {
            ContentNode record = dir.getChild(id);
            if (record != null) {
                final SessionRecording recording = read((ContentResource) record);
                if (!recording.isOnlyAudio()) {
                    videoUrl = recording.getVideoURL();
                    //removing video file
                    File video = new File(sessionDir.getPath() + "/Video");
                    if (video.isDirectory()) {
                        for (File f : video.listFiles()) {
                            if (f.getName().equals(id + ".mov")) {
                                LOGGER.log(Level.INFO, "file found: {0}", f.getName());
                                if (f.delete()) {
                                    break;
                                } else {
                                    LOGGER.log(Level.WARNING, "{0} --- Could not delete the file..........", f.getName());
                                }
                            }
                        }
                    }
                }
            }
        } catch (ContentRepositoryException ex) {
            Logger.getLogger(SessionRecorderResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(SessionRecorderResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        //removing the audio files
        File audio = new File(sessionDir + "/Audio");
        if (audio.isDirectory()) {
            for (File f : audio.listFiles()) {
                if (f.getName().contains(id)) {
                    LOGGER.info("file found: " + f.getName());
                    if (f.delete()) {
                        LOGGER.info("deleting " + f.getPath());
                    }
                }
            }
        }
        //removing the data (xml) file
        File data = new File(sessionDir + "/Data");
        if (data.isDirectory()) {
            for (File f : data.listFiles()) {
                if (f.getName().contains(id)) {
                    LOGGER.info("file found: " + f.getName());
                    if (f.delete()) {
                        LOGGER.info("deleting " + f.getPath());
                        break;
                    }
                }
            }
        }

        if (videoUrl != null) {
            final String url = videoUrl;
            new Thread(new Runnable() {

                public void run() {
                    //removing video from vimeo
                    deleteVideoOnVimeo(url);
                }
            }).start();
        }
        LOGGER.log(Level.INFO, "{0}:delete():End", this.getClass().getName());
    }

    /**
     * delete video on vimeo
     *
     * @param videoURL
     */
    private void deleteVideoOnVimeo(String videoURL) {
        if (getVimeo() == null) {
            return;
        }
        try {
            String videoId = videoURL.substring(videoURL.lastIndexOf("/") + 1);
            LOGGER.log(Level.INFO, "Deleting video, response =>\n{0}", getVimeo().removeVideo("/videos/" + videoId));
        } catch (IOException ex) {
            Logger.getLogger(SessionRecorderResource.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * get the max length from the configuration file
     *
     * @return
     */
    @GET
    @Path("getMaxLength")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getMaxLength() {
        LOGGER.log(Level.INFO, "{0}:getMaxLength():Start", this.getClass().getName());
        try {
            ContentNode node = dir.getChild("SessionRecorderConfiguration");
            if (node == null || !(node instanceof ContentResource)) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            final SessionRecordingConfiguration config = readConfig((ContentResource) node);
            int maxLength = config.getMAX_LENGTH();
            return Response.ok(new MaxLength(maxLength)).build();
        } catch (ContentRepositoryException ex) {
            Logger.getLogger(SessionRecorderResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(SessionRecorderResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        LOGGER.log(Level.WARNING, "{0}:getMaxLength():End:No data found", this.getClass().getName());
        return Response.status(Response.Status.NO_CONTENT).entity("No data found").build();
    }

    /**
     * wrapper for max length
     */
    @XmlRootElement(name = "max-length")
    public static class MaxLength {

        private int maxLength = 1;

        public MaxLength() {
        }

        public MaxLength(int length) {
            if (maxLength < length) {
                maxLength = length;
            }
        }

        @XmlElement
        public int getMaxLength() {
            return maxLength;
        }
    }

    /**
     * save the max length to the configuration file
     *
     * @param formParams
     */
    @POST
    @Path("saveMaxLength")
    public void saveMaxLength(MultivaluedMap<String, String> formParams) {
        LOGGER.log(Level.INFO, "{0}:saveMaxLength():Start", this.getClass().getName());
        try {
            ContentNode node = dir.getChild("SessionRecorderConfiguration");
            if (node == null || !(node instanceof ContentResource)) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            final SessionRecordingConfiguration config = readConfig((ContentResource) node);
            String length = formParams.get("maxLength").toString();
            length = length.substring(1, length.lastIndexOf("]"));
            Integer MAX_LENGTH = Integer.parseInt(length);
            config.setMAX_LENGTH(MAX_LENGTH);
            writeConfig(config);
        } catch (JAXBException ex) {
            Logger.getLogger(SessionRecorderResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ContentRepositoryException ex) {
            Logger.getLogger(SessionRecorderResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        LOGGER.log(Level.INFO, "{0}:saveMaxLength():End", this.getClass().getName());
    }

    /**
     * get the abstract path to ffmpeg from the configuration file
     *
     * @return
     */
    @GET
    @Path("getffmpegPath")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getFfmpegPath() {
        LOGGER.log(Level.INFO, "{0}:getFfmpegPath():Start", this.getClass().getName());
        if (getFFmpegLocation() != null) {
            return Response.ok(new FfmpegPath(getFFmpegLocation())).build();
        }

        LOGGER.log(Level.WARNING, "{0}:getFfmpegPath():End:No data found", this.getClass().getName());
        return Response.status(Response.Status.NO_CONTENT)
                .entity("No data found").build();
    }

    /**
     * wrapper for ffmpeg abstract path
     */
    @XmlRootElement(name = "ffmpegPath")
    public static class FfmpegPath {

        private String ffmpegPath = "";

        public FfmpegPath() {
        }

        public FfmpegPath(String ffmpegPath) {
            this.ffmpegPath = ffmpegPath;
        }

        @XmlElement
        public String getFfmpegPath() {
            return ffmpegPath;
        }
    }

    /**
     * save the vimeo api access token or the key to the configuration file
     *
     * @param formParams
     */
    @POST
    @Path("saveffmpegPath")
    public void saveFfmpegPath(MultivaluedMap<String, String> formParams) {
        LOGGER.log(Level.INFO, "{0}:saveFfmpegPath():Start", this.getClass().getName());
        try {
            ContentNode node = dir.getChild("SessionRecorderConfiguration");
            if (node == null || !(node instanceof ContentResource)) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            final SessionRecordingConfiguration config = readConfig((ContentResource) node);
            String path = formParams.get("path").toString();
            path = path.substring(1, path.lastIndexOf("]"));
            config.setFfmpegPath(path);
            writeConfig(config);
            vimeoTokenUpdated = true;

        } catch (JAXBException ex) {
            Logger.getLogger(SessionRecorderResource.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (ContentRepositoryException ex) {
            Logger.getLogger(SessionRecorderResource.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        LOGGER.log(Level.INFO, "{0}:saveFfmpegPath():End", this.getClass().getName());
    }

    /**
     * get the vimeo access token from the configuration file
     *
     * @return
     */
    @GET
    @Path("getVimeoToken")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getVimeoToken() {
        LOGGER.log(Level.INFO, "{0}:getVimeoToken():Start", this.getClass().getName());
        try {
            ContentNode node = dir.getChild("SessionRecorderConfiguration");
            if (node == null || !(node instanceof ContentResource)) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            final SessionRecordingConfiguration config = readConfig((ContentResource) node);
            String vimeoToekn = config.getVimeoToken();
            return Response.ok(new VimeoToken(vimeoToekn)).build();
        } catch (ContentRepositoryException ex) {
            Logger.getLogger(SessionRecorderResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(SessionRecorderResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        LOGGER.log(Level.WARNING, "{0}:getVimeoToken():End:No data found", this.getClass().getName());
        return Response.status(Response.Status.NO_CONTENT).entity("No data found").build();
    }

    /**
     * wrapper for vimeo api token
     */
    @XmlRootElement(name = "vimeoToken")
    public static class VimeoToken {

        private String vimeoToken = "";

        public VimeoToken() {
        }

        public VimeoToken(String token) {
            vimeoToken = token;
        }

        @XmlElement
        public String getVimeoToken() {
            return vimeoToken;
        }
    }

    /**
     * save the vimeo api access token or the key to the configuration file
     *
     * @param formParams
     */
    @POST
    @Path("saveVimeoToken")
    public void saveVimeoToken(MultivaluedMap<String, String> formParams) {
        LOGGER.log(Level.INFO, "{0}:saveVimeoToken():Start", this.getClass().getName());
        try {
            ContentNode node = dir.getChild("SessionRecorderConfiguration");
            if (node == null || !(node instanceof ContentResource)) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            final SessionRecordingConfiguration config = readConfig((ContentResource) node);
            String key = formParams.get("key").toString();
            key = key.substring(1, key.lastIndexOf("]"));
            config.setVimeoToken(key);
            writeConfig(config);
            vimeoTokenUpdated = true;
        } catch (JAXBException ex) {
            Logger.getLogger(SessionRecorderResource.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (ContentRepositoryException ex) {
            Logger.getLogger(SessionRecorderResource.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        LOGGER.log(Level.INFO, "{0}:saveVimeoToken():End", this.getClass().getName());
    }

    /**
     * get the vimeo default group to upload the video, from the configuration
     * file
     *
     * @return
     */
    @GET
    @Path("getDefaultGroup")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getDefaultGroup() {
        LOGGER.log(Level.INFO, "{0}:getVimeoToken():Start", this.getClass().getName());
        LOGGER.log(Level.WARNING, "{0}:getDefaultGroup():End:", this.getClass().getName());
        return Response.ok(new DefaultGroup(getVimeoDefaultGroup())).build();
    }

    /**
     * wrapper for vimeo default group
     */
    @XmlRootElement(name = "vimeoToken")
    public static class DefaultGroup {

        private String defaultGroup = "";

        public DefaultGroup() {
        }

        public DefaultGroup(String group) {
            defaultGroup = group;
        }

        @XmlElement
        public String getDefaultGroup() {
            return defaultGroup;
        }
    }

    /**
     * save the vimeo api access token or the key to the configuration file
     *
     * @param formParams
     */
    @POST
    @Path("saveDefaultGroup")
    public void saveDefaultGroup(MultivaluedMap<String, String> formParams) {
        LOGGER.log(Level.INFO, "{0}:saveDefaultGroup():Start", this.getClass().getName());
        try {
            ContentNode node = dir.getChild("SessionRecorderConfiguration");
            if (node == null || !(node instanceof ContentResource)) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            final SessionRecordingConfiguration config = readConfig((ContentResource) node);
            String group = formParams.get("group").toString();
            group = group.substring(1, group.lastIndexOf("]"));
            config.setDefaultGroup(group);
            writeConfig(config);
        } catch (JAXBException ex) {
            Logger.getLogger(SessionRecorderResource.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (ContentRepositoryException ex) {
            Logger.getLogger(SessionRecorderResource.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        LOGGER.log(Level.INFO, "{0}:saveDefaultGroup():End", this.getClass().getName());
    }

    /**
     * get all the groups from the vimeo and pass them as response with the link
     * and the password
     *
     * @return
     */
    @GET
    @Path("getGroups")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getGroups() {
        LOGGER.log(Level.INFO, "{0}:getGroups():Start", this.getClass().getName());
        List<String> groupName = new ArrayList<String>();
        List<String> password = new ArrayList<String>();
        Map<String, GroupData> groupData = getAllGroupData();
        if (groupData != null) {
            for (String key : groupData.keySet()) {
                groupName.add(key);
                password.add(groupData.get(key).getPassword());
            }
            GroupList list = new GroupList(groupName, password);
            LOGGER.log(Level.INFO, "{0}:getGroups():End", this.getClass().getName());
            return Response.ok(list).build();
        }
        LOGGER.log(Level.WARNING, "{0}:getGroups():End:No data found", this.getClass().getName());
        return Response.status(Response.Status.NO_CONTENT).entity("No data found").build();

    }

    /**
     * wrapper for group list
     */
    @XmlRootElement(name = "group-list")
    public static class GroupList {

        private final List<String> groupList = new ArrayList<String>();
        private final List<String> password = new ArrayList<String>();

        public GroupList() {
        }

        public GroupList(List<String> groupName, List<String> pass) {
            groupList.clear();
            password.clear();
            groupList.addAll(groupName);
            password.addAll(pass);
        }

        @XmlElement
        public List<String> getGroupList() {
            return groupList;
        }

        @XmlElement
        public List<String> getPassword() {
            return password;
        }
    }

    /**
     * save the updated password for a given group and update the password for
     * all the videos in the group on vimeo
     *
     * @param formParams
     */
    @POST
    @Path("saveGroupPassword")
    public void saveGroupPassword(MultivaluedMap<String, String> formParams) {
        LOGGER.log(Level.INFO, "{0}:saveGroupPassword():Start", this.getClass().getName());
        try {
            ContentNode node = dir.getChild("SessionRecorderConfiguration");
            if (node == null || !(node instanceof ContentResource)) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            final SessionRecordingConfiguration config = readConfig((ContentResource) node);
            Map<String, GroupData> groupData = config.getGroupData();
            List<GroupData> updateGroup = new ArrayList<GroupData>();
            for (String key : groupData.keySet()) {
                String password = formParams.get(key).toString().substring(1, formParams.get(key).toString().lastIndexOf("]"));
                if (!password.isEmpty()) {
                    if (!groupData.get(key).getPassword().equals(password)) {
                        groupData.get(key).setPassword(password);
                        updateGroup.add(groupData.get(key));
                    }
                }
            }
            config.setGroupData(groupData);
            writeConfig(config);
            for (GroupData data : updateGroup) {
                updatePassword(data);
            }
        } catch (JAXBException ex) {
            Logger.getLogger(SessionRecorderResource.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (ContentRepositoryException ex) {
            Logger.getLogger(SessionRecorderResource.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        LOGGER.log(Level.INFO, "{0}:saveGroupPassword():End", this.getClass().getName());
    }

    /**
     * update the password of all the video in a given group
     *
     * @param data
     */
    private void updatePassword(GroupData data) {
        LOGGER.log(Level.INFO, "{0}:updatePassword():Start", this.getClass().getName());
        if (getVimeo() == null) {
            return;
        }
        List<ContentNode> recordings = null;
        ContentNode config = null;
        try {
            recordings = dir.getChildren();
            LOGGER.log(Level.INFO, "Total recordings = {0}", recordings.size());
            config = dir.getChild("SessionRecorderConfiguration");
            if (recordings == null) {
                return;
            }

            String link = data.getLink();
            String Id = link.substring((int) link.lastIndexOf("/") + 1);
            VimeoResponse response = getVimeo().getAllVideosInGroup(Id);
            JSONArray videoArray = response.getJson().getJSONArray("data");
            LOGGER.log(Level.INFO, "Updating Password of {0} videos in {1} group", new Object[]{videoArray.length(), data.getName()});
            for (int j = 0; j < videoArray.length(); j++) {
                for (int k = 0; k < recordings.size(); k++) {
                    if (recordings.get(k).getName().equals(config.getName())) {
                        recordings.remove(k);
                        continue;
                    }
                    SessionRecording recording = read((ContentResource) recordings.get(k));
                    String name = videoArray.getJSONObject(j).getString("name");
                    if (name.equals(recording.getId())) {
                        String videoLink = videoArray.getJSONObject(j).getString("link");
                        String videoId = videoLink.substring((int) videoLink.lastIndexOf("/") + 1);
                        response = getVimeo().updateVideoPassword(videoId, data.getPassword());
                        if (response.getStatusCode() == 200) {
                            LOGGER.log(Level.INFO, "{0} :- password updated sucessfully", name);
                            recordings.remove(k);
                            videoArray.remove(j);
                            break;
                        } else {
                            LOGGER.log(Level.WARNING, "Password update failed for {0}", videoLink);
                        }
                    }
                }

            }
        } catch (IOException ex) {
            Logger.getLogger(SessionRecorderResource.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(SessionRecorderResource.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (ContentRepositoryException ex) {
            Logger.getLogger(SessionRecorderResource.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        LOGGER.log(Level.INFO, "{0}:updatePassword():End", this.getClass().getName());
    }

    /**
     * repair images which is of zero bytes
     *
     * @param username
     * @param frameCounter
     */
    private void repairImageSource(String username, String frameCounter) {
        LOGGER.log(Level.INFO, "{0}:repairImageSource():Start", this.getClass().getName());
        int counter = 1000000;
        String path = servletContext.getRealPath("../../");
        for (int i = counter; i < Float.parseFloat(frameCounter) + counter; i++) {
            FileReader fr = null;
            FileWriter fw = null;
            try {
                File file = new File(path + "/content/groups/users/" + SessionRecording.DIR_NAME
                        + "/temp/" + username + "/" + i + ".jpg");
                if (file.length() == 0) {
                    File prev = new File(path + "/content/groups/users/" + SessionRecording.DIR_NAME
                            + "/temp/" + username + "/" + (i - 1) + ".jpg");
                    file.delete();
                    FileUtils.copyFile(prev, file);
                }
            } catch (Exception ex) {
                Logger.getLogger(SessionRecorderResource.class
                        .getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (fr != null) {
                    try {
                        fr.close();
                    } catch (IOException ex) {
                        Logger.getLogger(SessionRecorderResource.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (fw != null) {
                    try {
                        fw.close();
                    } catch (IOException ex) {
                        Logger.getLogger(SessionRecorderResource.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        LOGGER.log(Level.INFO, "{0}:repairImageSource():End", this.getClass().getName());
    }

    /**
     *
     * @return
     */
    private String getFFmpegLocation() {
        String ffmpeg = null;
        try {
            ContentNode node = dir.getChild("SessionRecorderConfiguration");
            if (node == null || !(node instanceof ContentResource)) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            final SessionRecordingConfiguration config = readConfig((ContentResource) node);
            if (!config.getFfmpegPath().equals("") && !config.getFfmpegPath().trim().equals("")) {
                ffmpeg = config.getFfmpegPath();
            }
        } catch (ContentRepositoryException ex) {
            Logger.getLogger(SessionRecorderResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(SessionRecorderResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ffmpeg;
    }

    /**
     * execute ffmpeg command and display output
     *
     * @param command
     */
    private void executeFFmpegCommand(String[] command) {
        LOGGER.log(Level.INFO, "{0}:executeFFmpegCommand():Start", this.getClass().getName());
        try {
            LOGGER.log(Level.INFO, "command : {0}", command);
            Process p = Runtime.getRuntime().exec(command);

            LOGGER.info("\nHere are the ffmpeg logs : ---------");

            BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line;
            while ((line = in.readLine()) != null) {
                LOGGER.info(line);
            }
            p.waitFor();
            LOGGER.info("Done-------------!\n");

            in.close();

        } catch (IOException ex) {
            Logger.getLogger(SessionRecorderResource.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(SessionRecorderResource.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * create the movie from the captured images and the converted audio files.
     * upload the video to a group on vimeo and update its metadata
     *
     * @param movieName
     * @param frameRate
     * @param frameCounter
     * @param username
     * @param tags
     * @param sessionRecordingID
     * @param repairImageSource
     * @return
     */
    @POST
    @Path("createMovies")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String createMovie(@FormParam("movieName") String movieName, @FormParam("frameRate") String frameRate, @FormParam("frameCounter") String frameCounter, @FormParam("username") String username, @FormParam("tags") String tags, @FormParam("sessionRecordingID")
            final String sessionRecordingID, @FormParam("repairImageSource")
            final String repairImageSource) {
        LOGGER.log(Level.INFO, "{0}:createMovie():Start", this.getClass().getName());
        try {
            repairImageSource(username, frameCounter);
            String path = servletContext.getRealPath("../../");
            File imageDir = new File(path + "/content/groups/users/" + SessionRecording.DIR_NAME + "/temp/" + username);
            File movieDirectory = new File(path + "/content/groups/users/" + SessionRecording.DIR_NAME + "/Video");
            File audioFile = new File(path + "/content/groups/users/" + SessionRecording.DIR_NAME + "/Audio/" + movieName + ".wav");

            LOGGER.log(Level.INFO, "Image Dir = {0}", imageDir);
            LOGGER.log(Level.INFO, "Audio File = {0}", audioFile.getPath());
            LOGGER.log(Level.INFO, "movie path = {0}/{1}", new Object[]{movieDirectory, movieName});
            LOGGER.log(Level.INFO, "Captured frame rate = {0}", frameRate);

            if (!movieDirectory.exists()) {
                LOGGER.info("Creating movie directory");
                movieDirectory.mkdirs();
            }

            //prepare command and execute to create video
            String movieFilePath = movieDirectory + File.separator + movieName + ".mov";
            String ffmpeg = getFFmpegLocation();
            String[] command = {};
            if (audioFile.exists()) {
                LOGGER.info("Audio exist...");
                command = new String[]{
                    ffmpeg,
                    "-start_number",
                    "1000000",
                    "-framerate",
                    frameRate,
                    "-i",
                    imageDir + "/%07d.jpg",
                    "-i",
                    audioFile.getPath(),
                    "-codec",
                    "copy",
                    "-shortest",
                    movieFilePath
                };
            } else {
                LOGGER.info("Audio does not exist...");
                command = new String[]{
                    ffmpeg,
                    "-start_number",
                    "1000000",
                    "-framerate",
                    frameRate,
                    "-i",
                    imageDir + "/%07d.jpg",
                    movieFilePath
                };
            }
            executeFFmpegCommand(command);

            //deleting the images captured for creating the movie
            if (!imageDir.delete()) {
                deleteImageDirectory(imageDir);
            }

            //Uploading the video to vimeo
            final File movieFile = new File(movieFilePath);
            final String videoName = movieName;
            final String tag = tags;
            if (getVimeo() != null) {
                new Thread(new Runnable() {

                    public void run() {
                        try {
                            uploadToVimeo(movieFile, videoName, tag, sessionRecordingID);
                        } catch (Exception ex) {
                            Logger.getLogger(SessionRecorderResource.class
                                    .getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }).start();
            } else {
                String videoUrl = System.getProperty("wonderland.web.server.url").toLowerCase()
                        + "/webdav/content/groups/users/" + SessionRecording.DIR_NAME
                        + "/Video/" + sessionRecordingID + ".mov";
                writeVideoData(sessionRecordingID, videoUrl, null);
            }
            LOGGER.log(Level.INFO, "{0}:createMovie():End", this.getClass().getName());
            return movieFilePath;
        } catch (Exception ex) {
            Logger.getLogger(SessionRecorderResource.class
                    .getName()).log(Level.SEVERE, null, ex);
            LOGGER.log(Level.INFO,
                    "{0}:createMovie():End", this.getClass().getName());

            return null;
        }
    }

    /**
     * delete images generated for creating video
     *
     * @param imageDir
     */
    private void deleteImageDirectory(File imageDir) {
        LOGGER.info("inside deleteImageDirectory");
        for (File file : imageDir.listFiles()) {
            file.delete();
        }
        imageDir.delete();
    }

    /**
     * upload the video to group,that is first match within the given set of
     * tags, on vimeo
     *
     * @param movieFile video file
     * @param movieName name of the video
     * @param password password for the file
     * @param tags tags of the video
     * @param id session recording file name, to get the required data for
     * uploading the video
     */
    private void uploadToVimeo(File movieFile, String movieName, String tags, String recordingId) {
        LOGGER.log(Level.INFO, "{0}:uploadToVimeo():Start", this.getClass().getName());
        if (getVimeo() == null) {
            return;
        }

        String defaultGroupNAME = getVimeoDefaultGroup();
        Map<String, GroupData> groupData = new TreeMap<String, GroupData>(String.CASE_INSENSITIVE_ORDER);

        try {
            //uploading the video
            long start = new Date().getTime();
            LOGGER.info("Uploading the video at time: " + start);
            LOGGER.info("Uploading the video at time: " + start);
            String videoEndpoint = getVimeo().addVideo(movieFile, false);

            //uploading done
            long end = new Date().getTime();
            LOGGER.info("upload complete at time = " + end + "\nDifference = " + ((end - start) / 1000) + " seconds");
            String videoId = videoEndpoint.substring(videoEndpoint.lastIndexOf("/") + 1);
            LOGGER.info("Video Link is : www.vimeo.com/" + videoId);

            //decide group id & name from tag
            String groupId = "";
            String groupName = "";
            groupData.putAll(getAllGroupData());
            LOGGER.info("TAGS ====" + tags);
            if (tags != null && !tags.isEmpty()) {
                tags = tags.replaceAll(", ", ",");
                String addTags = tags.replaceAll(" ", "%20");
                getVimeo().addTags(videoId, addTags);
                LOGGER.log(Level.INFO, "{0} Tags added", addTags);

                //decide group id to which we will add the video
                String tagsArray[] = tags.split(",");
                for (String tag : tagsArray) {
                    if (groupData.containsKey(tag)) {
                        String link = groupData.get(tag).getLink();
                        groupId = link.substring(link.lastIndexOf("/") + 1);
                        groupName = tag;
                        break;
                    }
                }

            }
            if (groupId.equals("")) {
                String link = groupData.get(defaultGroupNAME).getLink();
                groupId = link.substring(link.lastIndexOf("/") + 1);
                groupName = defaultGroupNAME;
            }
            getVimeo().addVideoToGroup(groupId, videoId);
            LOGGER.log(Level.INFO, "Video uploaded to group {0}", groupName);

            //update metadata of video
            String password = groupData.get(groupName).getPassword();
            if (password == null) {
                password = getDefaultPassword();
            }
            getVimeo().updateVideoMetadata(videoEndpoint, movieName, "Description : " + movieName, "password", password, "safe");
            LOGGER.info("updated Video Metadata with password = " + password);
            //update recording with video url
            writeVideoData(recordingId, "https://vimeo.com/" + videoId, groupName);
        } catch (IOException ex) {
            Logger.getLogger(SessionRecorderResource.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (VimeoException ex) {
            Logger.getLogger(SessionRecorderResource.class
                    .getName()).log(Level.SEVERE, null, ex);

            LOGGER.log(Level.INFO,
                    "{0}:uploadToVimeo():End", this.getClass().getName());
        }
    }

    private void writeVideoData(String recordingId, String videoUrl, String groupName) {
        try {
            ContentNode node = dir.getChild(recordingId);
            final SessionRecording recording = read((ContentResource) node);
            recording.setVideoURL(videoUrl);
            if (groupName != null) {
                recording.setGroupName(groupName);
            }
            write(recording);
        } catch (ContentRepositoryException ce) {
            throw new WebApplicationException(ce, Response.Status.INTERNAL_SERVER_ERROR);
        } catch (JAXBException ex) {
            throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    private String getVimeoDefaultGroup() {
        String group = "";
        try {
            ContentNode node = dir.getChild("SessionRecorderConfiguration");
            if (node == null || !(node instanceof ContentResource)) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            final SessionRecordingConfiguration config = readConfig((ContentResource) node);
            group = config.getDefaultGroup();

        } catch (ContentRepositoryException ex) {
            Logger.getLogger(SessionRecorderResource.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(SessionRecorderResource.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return group;
    }

    /**
     * provides the information about all the groups and their details on the
     * vimeo
     *
     * @return the map with the group name as the key and group data as its
     * value
     */
    private Map<String, GroupData> getAllGroupData() {
        LOGGER.log(Level.INFO, "{0}:getAllGroupData():Start", this.getClass().getName());
        if (getVimeo() == null) {
            return null;
        }
        Map<String, GroupData> groupInfo = new HashMap<String, GroupData>();
        try {
            ContentNode node = dir.getChild("SessionRecorderConfiguration");
            if (node == null || !(node instanceof ContentResource)) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            final SessionRecordingConfiguration config = readConfig((ContentResource) node);
            Map<String, GroupData> groupData = (HashMap<String, GroupData>) config.getGroupData();

            VimeoResponse response = getVimeo().getAllGroups();
            JSONArray array = response.getJson().getJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                String key = array.getJSONObject(i).getString("name");
                String value = array.getJSONObject(i).getString("link");
                GroupData groupDetails = new GroupData();
                groupDetails.setName(key);
                groupDetails.setLink(value);
                groupDetails.setPassword(getDefaultPassword());
                groupInfo.put(key, groupDetails);
                if (groupData.containsKey(key)) {
                    String pass = groupData.get(key).getPassword();
                    if (pass != null && !pass.isEmpty() && !pass.equals("")) {
                        groupInfo.get(key).setPassword(pass);
                    }
//                    else {
//                        updatePassword(groupDetails);
//                    }
                }
            }
            config.setGroupData(groupInfo);
            writeConfig(config);
            return groupInfo;
        } catch (ContentRepositoryException ex) {
            Logger.getLogger(SessionRecorderResource.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(SessionRecorderResource.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SessionRecorderResource.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        LOGGER.log(Level.INFO, "{0}:getAllGroupData():End", this.getClass().getName());
        return groupInfo;
    }

    /**
     * default password for all groups of vimeo
     *
     * @return
     */
    private String getDefaultPassword() {
        return ("Relax");
    }

}
