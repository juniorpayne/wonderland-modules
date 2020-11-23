/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jdesktop.wonderland.modules.singaporeavatar.client;

import imi.character.CharacterParams;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.cell.asset.AssetUtils;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.modules.avatarbase.client.imi.ImiAvatar;
import org.jdesktop.wonderland.modules.avatarbase.client.imi.ImiAvatarDetailsJDialog;
import org.jdesktop.wonderland.modules.avatarbase.client.imi.ImiAvatarLoaderFactory;
import org.jdesktop.wonderland.modules.avatarbase.client.imi.WonderlandCharacterParams;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.WlAvatarCharacter;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.WlAvatarCharacter.WlAvatarCharacterBuilder;
import org.jdesktop.wonderland.modules.avatarbase.client.registry.spi.AvatarSPI;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.AvatarConfigInfo;
import org.jdesktop.wonderland.modules.singaporeavatar.client.SingaporeAvatarFactory.Avatar;
import org.jdesktop.wonderland.modules.singaporeavatar.client.SingaporeAvatarFactory.AvatarType;

/**
 *
 * @author jkaplan
 */
class SingaporeAvatar implements AvatarSPI {
    private static final Logger LOGGER =
            Logger.getLogger(SingaporeAvatar.class.getName());
    private static final ResourceBundle BUNDLE =
            ResourceBundle.getBundle("org/jdesktop/wonderland/modules/" +
                                     "singaporeavatar/client/resources/Bundle");
    
    private final Avatar avatar;

    SingaporeAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return BUNDLE.getString(avatar.name);
    }

    public boolean canDelete() {
        return false;
    }

    public void delete() {
    }

    public boolean canConfigure() {
        return (avatar.type == AvatarType.IMI);
    }

    public void configure() {
        if (avatar.type == AvatarType.IMI) {
            // create a real IMI avatar with the configuration based on
            // this avatar
            String name = "My" + getName();
            name = name.replaceAll("\\s+", "");
            final ImiAvatar imiAvatar = new ImiAvatar(name, 1);
            
            // get the character params for the current avatar
            String baseURL;
            URL avatarURL;
            
            try {
                String uri = "wla://avatarbaseart/";
                baseURL = AssetUtils.getAssetURL(uri).toString();
                avatarURL = getAvatarURL(null);
            } catch (MalformedURLException ex) {
                LOGGER.log(Level.WARNING, "Error getting base url", ex);
                return;
            }

            WorldManager wm = ClientContextJME.getWorldManager();
            WlAvatarCharacter wlChar = new WlAvatarCharacterBuilder(avatarURL, wm).
                    baseURL(baseURL).addEntity(false).build();
            
            CharacterParams cp = wlChar.getCharacterParams();
            WonderlandCharacterParams wcp = null;
            try {
                if (cp.isMale() == true) {
                    wcp = WonderlandCharacterParams.loadMale();
                }
                else {
                    wcp = WonderlandCharacterParams.loadFemale();
                }
            } catch (IOException excp) {
                LOGGER.log(Level.WARNING, "Unable to load male/female params", excp);
                return;
            }
            wcp.setCharacterParams(cp);
            imiAvatar.setAvatarParams(wcp);
            
            // Fetch the configuration dialog. There is only a single instance of
            // it. Spawn the call to setAvatar() in a new thread, since it may
            // take a while.
            // visible dialog after avatar is set as it is opening behind other windows
            final ImiAvatarDetailsJDialog dialog =
                    ImiAvatarDetailsJDialog.getImiAvatarDetailsJDialog();
            
            new Thread() {
                @Override
                public void run() {
                    dialog.setAvatar(imiAvatar);
                    dialog.setVisible(true);
                }
            }.start();
        }
    }

    public boolean isHighResolution() {
        return true;
    }

    public AvatarConfigInfo getAvatarConfigInfo(ServerSessionManager session) {
        String className = null;

        switch (avatar.type) {
            case IMI:
                className = ImiAvatarLoaderFactory.class.getName();
                break;
            case EVOLVER:
                className = SingaporeEvolverAvatarLoaderFactory.class.getName();
                break;
        }

        
        try {
            URL url = getAvatarURL(session);
            return new AvatarConfigInfo(url.toExternalForm(), className);
        } catch (MalformedURLException ex) {
            LOGGER.log(Level.WARNING, null, ex);
            return null;
        }
    }
    
    private URL getAvatarURL(ServerSessionManager session) 
            throws MalformedURLException 
    {
        String avatarURL = "wla://singapore-avatar/avatars/" + avatar.url;
        
        if (session != null) {
            return AssetUtils.getAssetURL(avatarURL, 
                                          session.getServerNameAndPort());
        } else {
            return AssetUtils.getAssetURL(avatarURL);
        }
    }
}
