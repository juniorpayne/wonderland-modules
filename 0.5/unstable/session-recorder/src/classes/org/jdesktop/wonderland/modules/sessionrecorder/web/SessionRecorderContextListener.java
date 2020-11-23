/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
package org.jdesktop.wonderland.modules.sessionrecorder.web;

import java.io.ByteArrayOutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.jdesktop.wonderland.front.admin.AdminRegistration;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentResource;
import org.jdesktop.wonderland.modules.contentrepo.web.spi.WebContentRepository;
import org.jdesktop.wonderland.modules.contentrepo.web.spi.WebContentRepositoryRegistry;
import org.jdesktop.wonderland.modules.sessionrecorder.common.SessionRecording;
import org.jdesktop.wonderland.modules.sessionrecorder.common.SessionRecordingConfiguration;

/**
 * Register and unregister the session recorder menu item Also check if the
 * required folders are available and if not, create them
 *
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
public class SessionRecorderContextListener implements ServletContextListener {

    private static final Logger LOGGER
            = Logger.getLogger(SessionRecorderContextListener.class.getName());

    private static final String SESSION_RECORDER_KEY = "__sessionRecorderLogRegistration";
    private static final String TIMER_KEY = "__sessionRecorderTimer";

    private ServletContext context;

    public void contextInitialized(ServletContextEvent sce) {
        context = sce.getServletContext();

        // register with the UI
        AdminRegistration ar = new AdminRegistration("Role Play Recordings",
                "/session-recorder/session-recorder/SessionRecordings.html");
        ar.setFilter(AdminRegistration.ADMIN_FILTER);
        AdminRegistration.register(ar, context);
        context.setAttribute(SESSION_RECORDER_KEY, ar);

        // try to create the library path
        tryRepo();
    }

    public void contextDestroyed(ServletContextEvent sce) {
        AdminRegistration ar = (AdminRegistration) context.getAttribute(SESSION_RECORDER_KEY);
        if (ar != null) {
            AdminRegistration.unregister(ar, context);
        }

        Timer timer = (Timer) context.getAttribute(TIMER_KEY);
        if (timer != null) {
            timer.cancel();
        }
    }

    /**
     * Try to create the content repo directory. Schedule a task to retry in 5
     * seconds if not found
     */
    private void tryRepo() {
        Timer timer = (Timer) context.getAttribute(TIMER_KEY);

        try {
            if (createRepoPath()) {
                // create succeeded, cancel any timers
                if (timer != null) {
                    timer.cancel();
                    context.removeAttribute(TIMER_KEY);
                }
            } else {
                // try again later
                if (timer == null) {
                    timer = new Timer();
                    context.setAttribute(TIMER_KEY, timer);
                }

                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        tryRepo();
                    }
                }, 5000);
            }
        } catch (ContentRepositoryException ce) {
            LOGGER.log(Level.WARNING, "Error creating content repository", ce);
        }
    }

    /**
     * Guarantee that the content repo path we need is available.
     *
     * @return false if the content repository is not available yet
     */
    private boolean createRepoPath() throws ContentRepositoryException {
        WebContentRepositoryRegistry reg = WebContentRepositoryRegistry.getInstance();
        WebContentRepository repo = reg.getRepository(context);

        if (repo == null) {
            // not ready yet
            return false;
        }

        // create directory if it doesn't exist
        ContentCollection groups = (ContentCollection) repo.getRoot().getChild("groups");
        ContentCollection users = (ContentCollection) groups.getChild("users");
        ContentCollection dir = (ContentCollection) users.getChild(SessionRecording.DIR_NAME);
        if (dir == null) {
            dir = (ContentCollection) users.createChild(SessionRecording.DIR_NAME, ContentNode.Type.COLLECTION);
        }

        ContentCollection audioDir = (ContentCollection) dir.getChild("Audio");
        if (audioDir == null) {
            audioDir = (ContentCollection) dir.createChild("Audio", ContentNode.Type.COLLECTION);
        }
        ContentCollection videoDir = (ContentCollection) dir.getChild("Video");
        if (videoDir == null) {
            videoDir = (ContentCollection) dir.createChild("Video", ContentNode.Type.COLLECTION);
        }
        ContentCollection dataDir = (ContentCollection) dir.getChild("Data");
        if (dataDir == null) {
            dataDir = (ContentCollection) dir.createChild("Data", ContentNode.Type.COLLECTION);
        }
        ContentResource globalData = (ContentResource) dataDir.getChild("SessionRecorderConfiguration");
        if (globalData == null) {
            try {
                globalData = (ContentResource) dataDir.createChild("SessionRecorderConfiguration", ContentNode.Type.RESOURCE);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                JAXBContext context = JAXBContext.newInstance(SessionRecordingConfiguration.class);
                Marshaller m = context.createMarshaller();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
                m.marshal(new SessionRecordingConfiguration(), baos);
                globalData.put(baos.toByteArray());
            } catch (JAXBException ex) {
                Logger.getLogger(SessionRecorderContextListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        ContentCollection tempDir = (ContentCollection) dir.getChild("temp");
        if (tempDir == null) {
            tempDir = (ContentCollection) dir.createChild("temp", ContentNode.Type.COLLECTION);
        }
        return true;
    }

}
