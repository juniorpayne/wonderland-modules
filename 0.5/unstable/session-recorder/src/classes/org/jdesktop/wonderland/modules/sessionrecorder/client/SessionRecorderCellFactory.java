/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */

package org.jdesktop.wonderland.modules.sessionrecorder.client;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;
import org.jdesktop.wonderland.client.cell.registry.annotation.CellFactory;
import org.jdesktop.wonderland.client.cell.registry.spi.CellFactorySPI;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.modules.sessionrecorder.common.SessionRecorderCellServerState;

/**
 *
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
@CellFactory
public class SessionRecorderCellFactory implements CellFactorySPI {

    private static final ResourceBundle bundle = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/sessionrecorder/client/resources/Bundle");

    public String[] getExtensions() {
        return new String[]{};
    }

    public <T extends CellServerState> T getDefaultCellServerState(Properties props) {
        CellServerState state = new SessionRecorderCellServerState();
        state.setName(bundle.getString("ROLE_PLAY_CAMERA") + "_" + new Date().getTime());
        return (T) state;
    }

    public String getDisplayName() {
        return bundle.getString("CAMERA_DISPLAY_NAME");
    }

    public Image getPreviewImage() {
        URL url = this.getClass().getResource("resources/camera-lens-preview.png");
        return Toolkit.getDefaultToolkit().createImage(url);
    }

}
