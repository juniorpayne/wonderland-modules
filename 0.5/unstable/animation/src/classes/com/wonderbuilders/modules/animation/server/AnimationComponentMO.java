/**
 * Copyright (c) 2012, WonderBuilders, Inc., All Rights Reserved
 */
package com.wonderbuilders.modules.animation.server;

import com.sun.sgs.app.ManagedReference;
import com.wonderbuilders.modules.animation.common.Animation;
import com.wonderbuilders.modules.animation.common.AnimationComponentClientState;
import com.wonderbuilders.modules.animation.common.AnimationComponentMessage;
import com.wonderbuilders.modules.animation.common.AnimationComponentServerState;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.server.cell.AbstractComponentMessageReceiver;
import org.jdesktop.wonderland.server.cell.CellComponentMO;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.ChannelComponentMO;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;

/**
 * Server-side of Animation component.
 * @author Vladimir Djurovic
 */
public class AnimationComponentMO extends CellComponentMO {
    
    /**
     * Current animation state.
     */
    private Animation animation;
    
    private String methodCall = null;
    
    private String parameter = null;
    
    /**
     * Creates new instance.
     * 
     * @param cell parent cell
     */
    public AnimationComponentMO(CellMO cell){
        super(cell);
    }

    @Override
    protected String getClientClass() {
        return "com.wonderbuilders.modules.animation.client.AnimationComponent";
    }

    @Override
    public CellComponentClientState getClientState(CellComponentClientState state, WonderlandClientID clientID, ClientCapabilities capabilities) {
        if(state == null){
            state = new AnimationComponentClientState();
            ((AnimationComponentClientState)state).setAnimation(animation);
            ((AnimationComponentClientState)state).setMethodCall(methodCall);
            ((AnimationComponentClientState)state).setParameter(parameter);
        }
        return super.getClientState(state, clientID, capabilities);
    }

    @Override
    public CellComponentServerState getServerState(CellComponentServerState state) {
        if(state == null){
            state = new AnimationComponentServerState();
            ((AnimationComponentServerState)state).setAnimation(animation);
            ((AnimationComponentServerState)state).setMethodCall(methodCall);
            ((AnimationComponentServerState)state).setParameter(parameter);
        }
        return super.getServerState(state);
    }

    @Override
    public void setServerState(CellComponentServerState state) {
        super.setServerState(state);
        animation = ((AnimationComponentServerState)state).getAnimation();
        methodCall = ((AnimationComponentServerState)state).getMethodCall();
        parameter = ((AnimationComponentServerState)state).getParameter();
    }

    @Override
    protected void setLive(boolean live) {
        super.setLive(live);
        ChannelComponentMO channel = cellRef.get().getComponent(ChannelComponentMO.class);
        if(live){
            channel.addMessageReceiver(AnimationComponentMessage.class, new AnimationComponentMessageReceiver(cellRef));
        } else {
            channel.removeMessageReceiver(AnimationComponentMessage.class);
        }
    }
    
    private static class AnimationComponentMessageReceiver 
                            extends AbstractComponentMessageReceiver {
        ManagedReference<CellMO> cellMO;
        public AnimationComponentMessageReceiver(ManagedReference<CellMO> cellMo){
            super(cellMo.get());
            this.cellMO = cellMo;
        }

        @Override
        public void messageReceived(WonderlandClientSender sender, WonderlandClientID clientID, CellMessage message) {
            AnimationComponentMessage msg = (AnimationComponentMessage)message;
            System.out.println("Message received.............");
            System.err.println("method = "+msg.getMethodCall()+": param = "+msg.getParameter());
            cellMO.get().getComponent(AnimationComponentMO.class).methodCall = msg.getMethodCall();
            cellMO.get().getComponent(AnimationComponentMO.class).parameter = msg.getParameter();
            // send message back to all client receivers
            getCell().sendCellMessage(clientID, msg);
        }
    }
}
