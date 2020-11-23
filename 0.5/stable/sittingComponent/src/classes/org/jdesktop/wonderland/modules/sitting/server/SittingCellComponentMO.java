/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
/**
 * Open Wonderland
 *
 * Copyright (c) 2011, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above copyright and
 * this condition.
 *
 * The contents of this file are subject to the GNU General Public License,
 * Version 2 (the "License"); you may not use this file except in compliance
 * with the License. A copy of the License is available at
 * http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as subject to
 * the "Classpath" exception as provided by the Open Wonderland Foundation in
 * the License file that accompanied this code.
 */
package org.jdesktop.wonderland.modules.sitting.server;

import com.sun.sgs.app.ManagedReference;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.modules.sitting.common.SittingCellComponentClientState;
import org.jdesktop.wonderland.modules.sitting.common.SittingCellComponentServerState;
import org.jdesktop.wonderland.modules.sitting.common.VehicleThreadMessage;
import org.jdesktop.wonderland.server.cell.AbstractComponentMessageReceiver;
import org.jdesktop.wonderland.server.cell.CellComponentMO;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.ChannelComponentMO;
import org.jdesktop.wonderland.server.cell.InteractionComponentMO;
import org.jdesktop.wonderland.server.cell.annotation.UsesCellComponentMO;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;

/**
 *
 * @author Morris Ford
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
public class SittingCellComponentMO extends CellComponentMO {

    private static Logger logger = Logger.getLogger(SittingCellComponentMO.class.getName());

    private float sittingTranslationX;
    private float sittingTranslationY;
    private float sittingTranslationZ;
    private float sittingRotationX;
    private float sittingRotationY;
    private float sittingRotationZ;

    private float lieDownTranslationX;
    private float lieDownTranslationY;
    private float lieDownTranslationZ;
    private float lieDownRotationX;
    private float lieDownRotationY;
    private float lieDownRotationZ;

    private boolean leftClick = true;
    private boolean lieDown = false;
    private boolean lieDownImmediately = true;
    private String ownerCellId = "";
    private float initPosX;
    private float initPosY;
    private float initPosZ;
    @UsesCellComponentMO(InteractionComponentMO.class)
    private ManagedReference<InteractionComponentMO> interactionRef;

    @UsesCellComponentMO(ChannelComponentMO.class)
    private ManagedReference<ChannelComponentMO> channelRef;

    public SittingCellComponentMO(CellMO cell) {
        super(cell);
    }

    @Override
    protected String getClientClass() {
        return "org.jdesktop.wonderland.modules.sitting.client.SittingCellComponent";
    }

    @Override
    protected void setLive(boolean live) {
        super.setLive(live);
        logger.fine("Setting SittingCellComponentMO to live = " + live);

        // make sure the cell is not collidable
        if (live) {
            interactionRef.get().setCollidable(false);
        }

        ChannelComponentMO channelComponent = channelRef.get();
        if (live) {
            VehicleThreadMessageReceiverImpl receiver = new VehicleThreadMessageReceiverImpl(cellRef);
            channelComponent.addMessageReceiver(VehicleThreadMessage.class, receiver);
        } else {
            channelComponent.removeMessageReceiver(VehicleThreadMessage.class);
        }
    }

    @Override
    public CellComponentClientState getClientState(CellComponentClientState state, WonderlandClientID clientID, ClientCapabilities capabilities) {
        if (state == null) {
            state = new SittingCellComponentClientState();
        }
        ((SittingCellComponentClientState) state).setSittingTranslationX(sittingTranslationX);
        ((SittingCellComponentClientState) state).setSittingTranslationY(sittingTranslationY);
        ((SittingCellComponentClientState) state).setSittingTranslationZ(sittingTranslationZ);
        ((SittingCellComponentClientState) state).setSittingRotationX(sittingRotationX);
        ((SittingCellComponentClientState) state).setSittingRotationY(sittingRotationY);
        ((SittingCellComponentClientState) state).setSittingRotationZ(sittingRotationZ);

        ((SittingCellComponentClientState) state).setLieDownTranslationX(lieDownTranslationX);
        ((SittingCellComponentClientState) state).setLieDownTranslationY(lieDownTranslationY);
        ((SittingCellComponentClientState) state).setLieDownTranslationZ(lieDownTranslationZ);
        ((SittingCellComponentClientState) state).setLieDownRotationX(lieDownRotationX);
        ((SittingCellComponentClientState) state).setLieDownRotationY(lieDownRotationY);
        ((SittingCellComponentClientState) state).setLieDownRotationZ(lieDownRotationZ);

        ((SittingCellComponentClientState) state).setLeftClick(leftClick);
        ((SittingCellComponentClientState) state).setLieDown(lieDown);
        ((SittingCellComponentClientState) state).setLieDownImmediately(lieDownImmediately);
        ((SittingCellComponentClientState) state).setOwnerCellId(ownerCellId);
        ((SittingCellComponentClientState) state).setInitPosX(initPosX);
        ((SittingCellComponentClientState) state).setInitPosY(initPosY);
        ((SittingCellComponentClientState) state).setInitPosZ(initPosZ);
        return super.getClientState(state, clientID, capabilities);

    }

    @Override
    public CellComponentServerState getServerState(CellComponentServerState state) {
        if (state == null) {
            state = new SittingCellComponentServerState();
        }
        ((SittingCellComponentServerState) state).setSittingTranslationX(sittingTranslationX);
        ((SittingCellComponentServerState) state).setSittingTranslationY(sittingTranslationY);
        ((SittingCellComponentServerState) state).setSittingTranslationZ(sittingTranslationZ);
        ((SittingCellComponentServerState) state).setSittingRotationX(sittingRotationX);
        ((SittingCellComponentServerState) state).setSittingRotationY(sittingRotationY);
        ((SittingCellComponentServerState) state).setSittingRotationZ(sittingRotationZ);

        ((SittingCellComponentServerState) state).setLieDownTranslationX(lieDownTranslationX);
        ((SittingCellComponentServerState) state).setLieDownTranslationY(lieDownTranslationY);
        ((SittingCellComponentServerState) state).setLieDownTranslationZ(lieDownTranslationZ);
        ((SittingCellComponentServerState) state).setLieDownRotationX(lieDownRotationX);
        ((SittingCellComponentServerState) state).setLieDownRotationY(lieDownRotationY);
        ((SittingCellComponentServerState) state).setLieDownRotationZ(lieDownRotationZ);

        ((SittingCellComponentServerState) state).setLeftClick(leftClick);
        ((SittingCellComponentServerState) state).setLieDown(lieDown);
        ((SittingCellComponentServerState) state).setLieDownImmediately(lieDownImmediately);
        ((SittingCellComponentServerState) state).setOwnerCellId(ownerCellId);
        ((SittingCellComponentServerState) state).setInitPosX(initPosX);
        ((SittingCellComponentServerState) state).setInitPosY(initPosY);
        ((SittingCellComponentServerState) state).setInitPosZ(initPosZ);
        return super.getServerState(state);
    }

    @Override
    public void setServerState(CellComponentServerState state) {
        super.setServerState(state);
        sittingTranslationX = ((SittingCellComponentServerState) state).getSittingTranslationX();
        sittingTranslationY = ((SittingCellComponentServerState) state).getSittingTranslationY();
        sittingTranslationZ = ((SittingCellComponentServerState) state).getSittingTranslationZ();
        sittingRotationX = ((SittingCellComponentServerState) state).getSittingRotationX();
        sittingRotationY = ((SittingCellComponentServerState) state).getSittingRotationY();
        sittingRotationZ = ((SittingCellComponentServerState) state).getSittingRotationZ();

        lieDownTranslationX = ((SittingCellComponentServerState) state).getLieDownTranslationX();
        lieDownTranslationY = ((SittingCellComponentServerState) state).getLieDownTranslationY();
        lieDownTranslationZ = ((SittingCellComponentServerState) state).getLieDownTranslationZ();
        lieDownRotationX = ((SittingCellComponentServerState) state).getLieDownRotationX();
        lieDownRotationY = ((SittingCellComponentServerState) state).getLieDownRotationY();
        lieDownRotationZ = ((SittingCellComponentServerState) state).getLieDownRotationZ();

        leftClick = ((SittingCellComponentServerState) state).isLeftClick();
        lieDown = ((SittingCellComponentServerState) state).isLieDown();
        lieDownImmediately = ((SittingCellComponentServerState) state).isLieDownImmediately();
        ownerCellId = ((SittingCellComponentServerState) state).getOwnerCellId();
        initPosX = ((SittingCellComponentServerState) state).getInitPosX();
        initPosY = ((SittingCellComponentServerState) state).getInitPosY();
        initPosZ = ((SittingCellComponentServerState) state).getInitPosZ();
    }

    private static class VehicleThreadMessageReceiverImpl extends AbstractComponentMessageReceiver {

        ManagedReference<CellMO> cellMO;

        private VehicleThreadMessageReceiverImpl(ManagedReference<CellMO> cellMO) {
            super(cellMO.get());
            this.cellMO = cellMO;
        }

        public void messageReceived(WonderlandClientSender sender, WonderlandClientID clientID,
                CellMessage message) {
            if (message instanceof VehicleThreadMessage) {
                //send message to all clients
                VehicleThreadMessage msg = (VehicleThreadMessage) message;
                cellMO.get().getComponent(SittingCellComponentMO.class).ownerCellId = msg.getAvatarCellId();
                cellMO.get().getComponent(SittingCellComponentMO.class).initPosX = msg.getInitPosX();
                cellMO.get().getComponent(SittingCellComponentMO.class).initPosY = msg.getInitPosY();
                cellMO.get().getComponent(SittingCellComponentMO.class).initPosZ = msg.getInitPosZ();
                getCell().sendCellMessage(clientID, message);
            }
        }
    }
}
