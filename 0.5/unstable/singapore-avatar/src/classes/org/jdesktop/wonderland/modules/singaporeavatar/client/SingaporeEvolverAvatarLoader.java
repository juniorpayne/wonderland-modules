/*
 /* 
 */
package org.jdesktop.wonderland.modules.singaporeavatar.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.asset.AssetUtils;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.WlAvatarCharacter;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.WlAvatarCharacter.WlAvatarCharacterBuilder;
import org.jdesktop.wonderland.modules.avatarbase.client.loader.spi.AvatarLoaderSPI;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.AvatarConfigInfo;

/**
 * @author Jonathan Kaplan <jonathankap@wonderbuilders.com>
 */
public class SingaporeEvolverAvatarLoader implements AvatarLoaderSPI {
    private static final Logger LOGGER =
            Logger.getLogger(SingaporeEvolverAvatarLoader.class.getName());
    
    public WlAvatarCharacter getAvatarCharacter(Cell viewCell, String userName,
                                                AvatarConfigInfo info)
    {
        try {
            URL u = new URL(info.getAvatarConfigURL());
       
            String hAndP = LoginManager.getPrimary().getServerNameAndPort();
            URL assetURL = AssetUtils.getAssetURL("wla://singapore-avatar/", 
                                                  hAndP);
            String base = assetURL.toExternalForm();
       
            WorldManager wm = ClientContextJME.getWorldManager();
            return new WlAvatarCharacterBuilder(u, wm).baseURL(base).
                                                       addEntity(false).build();
        } catch (MalformedURLException ex) {
            LOGGER.log(Level.WARNING, "Errir creating URL", ex);
            return null;
        }
    }
}
