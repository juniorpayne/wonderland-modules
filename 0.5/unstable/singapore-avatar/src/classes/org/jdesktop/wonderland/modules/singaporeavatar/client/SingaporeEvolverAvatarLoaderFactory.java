/**
 */
package org.jdesktop.wonderland.modules.singaporeavatar.client;

import org.jdesktop.wonderland.modules.avatarbase.client.loader.annotation.AvatarLoaderFactory;
import org.jdesktop.wonderland.modules.avatarbase.client.loader.spi.AvatarLoaderFactorySPI;
import org.jdesktop.wonderland.modules.avatarbase.client.loader.spi.AvatarLoaderSPI;

/**
 * @author Jonathan Kaplan <jonathankap@wonderbuilders.com>
 */
@AvatarLoaderFactory
public class SingaporeEvolverAvatarLoaderFactory implements AvatarLoaderFactorySPI {
    public AvatarLoaderSPI getAvatarLoader() {
        return new SingaporeEvolverAvatarLoader();
    }
}
