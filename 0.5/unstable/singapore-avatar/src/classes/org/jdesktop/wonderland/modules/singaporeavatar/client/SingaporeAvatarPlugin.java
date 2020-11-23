/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jdesktop.wonderland.modules.singaporeavatar.client;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import org.jdesktop.wonderland.client.BaseClientPlugin;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.common.annotation.Plugin;
import org.jdesktop.wonderland.modules.avatarbase.client.registry.AvatarRegistry;
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepositoryRegistry;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;

/**
 *
 * @author Jonathan Kaplan <jonathankap@wonderbuilders.com>
 */
@Plugin
public class SingaporeAvatarPlugin extends BaseClientPlugin {
    private static final Logger LOGGER =
            Logger.getLogger(SingaporeAvatarPlugin.class.getName());
    
    @Override
    protected void activate() {
        // wait for the local repository to be activated, then instantiate
        // the window
        SwingWorker<ContentCollection, Object> worker = 
                new SwingWorker<ContentCollection, Object>() 
        {

            @Override
            protected ContentCollection doInBackground() throws Exception {
                ContentRepositoryRegistry registry = 
                        ContentRepositoryRegistry.getInstance();
                ContentCollection localRepo = registry.getLocalRepository();
                
                try {
                    while (localRepo == null) {
                        Thread.sleep(250);
                    }
                } catch (InterruptedException ie) {
                }
                
                if (localRepo != null) {
                    // find the avatar registry collection
                    return AvatarRegistry.getAvatarRegistry().getAvatarCollection();
                }
                
                return null;
            }

            @Override
            protected void done() {
                // on AWT thread, with local repository. Check if we have
                // already selected a Singapore avatar
                try {
                    ContentCollection localRepo = this.get();
                    if (localRepo == null) {
                        // not working?
                        return;
                    }
                    
                    if (localRepo.getChild("avatar_settings.xml") == null) {
                        AvatarChooserPanel panel = new AvatarChooserPanel();
                        panel.setLocationRelativeTo(JmeClientMain.getFrame().getFrame());
                
                        panel.pack();
                        panel.setVisible(true);
                    }
                } catch (InterruptedException ie) {
                } catch (ExecutionException ee) {
                    LOGGER.log(Level.WARNING, "Error in execution", ee);
                } catch (ContentRepositoryException ce) {
                    LOGGER.log(Level.WARNING, "Error in content repository", ce);
                }
            }
        };
        
        worker.execute();
    }
}
