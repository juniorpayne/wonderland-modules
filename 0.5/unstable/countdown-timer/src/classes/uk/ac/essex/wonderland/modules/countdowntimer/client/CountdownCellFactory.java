/*
 *  +Spaces Project, http://www.positivespaces.eu/
 *
 *  Copyright (c) 2012, University of Essex, UK, 2012, All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package uk.ac.essex.wonderland.modules.countdowntimer.client;

import java.awt.Image;
import java.util.Properties;
import java.util.ResourceBundle;
import org.jdesktop.wonderland.client.cell.registry.annotation.CellFactory;
import org.jdesktop.wonderland.client.cell.registry.spi.CellFactorySPI;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import uk.ac.essex.wonderland.modules.countdowntimer.common.CountdownCellServerState;

/**
 * 
 * @author Bernard Horan
 */
@CellFactory
public class CountdownCellFactory implements CellFactorySPI {
    private static final ResourceBundle bundle = ResourceBundle.getBundle("uk/ac/essex/wonderland/modules/countdowntimer/client/resources/Bundle");

    public String[] getExtensions() {
        return new String[] {};
    }

    @SuppressWarnings("unchecked")
    public <T extends CellServerState> T getDefaultCellServerState(Properties props) {
        CountdownCellServerState state = new CountdownCellServerState();
        state.setName(bundle.getString("COUNTDOWN TIMER"));
        return (T)state;
    }

    public String getDisplayName() {
        //Remove from 'insert object' menu
        return null;
    }

    public Image getPreviewImage() {
        return null;
   }
}