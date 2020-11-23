package org.jdesktop.wonderland.modules.singaporeavatar.client;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.modules.avatarbase.client.registry.AvatarRegistry;
import org.jdesktop.wonderland.modules.avatarbase.client.registry.annotation.AvatarFactory;
import org.jdesktop.wonderland.modules.avatarbase.client.registry.spi.AvatarFactorySPI;
import org.jdesktop.wonderland.modules.avatarbase.client.registry.spi.AvatarSPI;

/**
 * Avatar factory that generates Singapore avatars
 * @author Jonathan Kaplan <jonathankap@wonderbuilders.com>
 */
@AvatarFactory
public class SingaporeAvatarFactory implements AvatarFactorySPI {
    private static final Logger LOGGER =
            Logger.getLogger(SingaporeAvatarFactory.class.getName());
    
    enum AvatarType { IMI, EVOLVER };
    enum Avatar {
        FEMALE1("FemalePlayer_1", "imi/FemalePlayer-1_1.xml", AvatarType.IMI),
        FEMALE2("FemalePlayer_2", "imi/FemalePlayer-2_1.xml", AvatarType.IMI),
        FEMALE3("FemalePlayer_3", "imi/FemalePlayer-3_1.xml", AvatarType.IMI),
        FEMALE_MEDICAL_1("Female_Medical_1", "multimesh-evolver/female_doctor.xml", 
                    AvatarType.EVOLVER),
        FEMALE_MEDICAL_2("Female_Medical_2", "multimesh-evolver/female_doctor_1.xml", 
                    AvatarType.EVOLVER),
        
        MALE1("MalePlayer_1", "imi/MalePlayer-1_2.xml", AvatarType.IMI),
        MALE2("MalePlayer_2", "imi/MalePlayer-2_2.xml", AvatarType.IMI),
        MALE3("MalePlayer_3", "imi/MalePlayer-3_1.xml", AvatarType.IMI),
        MALE_MEDICAL_1("Male_Medical_1", "multimesh-evolver/male_doctor.xml", 
                 AvatarType.EVOLVER),
        MALE_MEDICAL_2("Male_Medical_2", "multimesh-evolver/male_doctor_1.xml", 
                    AvatarType.EVOLVER);
        
        String name;
        String url;
        AvatarType type;
        
        Avatar(String name, String url, AvatarType type) {
            this.name = name;
            this.url = url;
            this.type = type;
        }
    }
 
    private final Set<AvatarSPI> registered = new LinkedHashSet<AvatarSPI>();
    
    /**
     * {@inheritDoc}
     */
    public void registerAvatars(ServerSessionManager session) {
        // Create the set of basic avatars from the hard-coded list of URLs
        AvatarRegistry registry = AvatarRegistry.getAvatarRegistry();
        for (Avatar a : Avatar.values()) {
            SingaporeAvatar avatar = new SingaporeAvatar(a);
            registry.registerAvatar(avatar, false);
            registered.add(avatar);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void unregisterAvatars(ServerSessionManager session) {
        // Look through and unregistry all of the basic avatars
        AvatarRegistry registry = AvatarRegistry.getAvatarRegistry();
        for (AvatarSPI avatar : registered) {
            registry.unregisterAvatar(avatar);
        }
        registered.clear();
    }
}
