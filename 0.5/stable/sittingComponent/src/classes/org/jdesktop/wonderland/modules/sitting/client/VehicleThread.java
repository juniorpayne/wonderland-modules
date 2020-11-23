/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
package org.jdesktop.wonderland.modules.sitting.client;

import com.jme.math.Vector3f;
import com.jme.scene.Node;
import imi.character.statemachine.GameContext;
import imi.character.statemachine.GameState;
import imi.character.statemachine.GameStateChangeListener;
import imi.character.statemachine.GameStateChangeListenerRegisterar;
import imi.character.statemachine.corestates.IdleState;
import imi.scene.PTransform;
import java.util.concurrent.ScheduledExecutorService;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarImiJME;

/**
 * Change avatar position when vehicle/cell node animates
 * 
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
public class VehicleThread implements Runnable {

    private final Cell avatarCell;
    private final Node platformNode;
    private final Vector3f avatarInitPosition;
    private final Vector3f platformInitPosition;
    private final ScheduledExecutorService ses;
    
    public VehicleThread(Cell avatarCell, Node platformNode, ScheduledExecutorService ses, Vector3f initPos) {
        this.avatarCell = avatarCell;
        this.platformNode = platformNode;
        this.ses = ses;
        this.avatarInitPosition = avatarCell.getWorldTransform().getTranslation(null);
        this.platformInitPosition = platformNode.getWorldTranslation().clone();
        GameStateChangeListenerRegisterar.registerListener(new ThreadRemover());
    }
    
    public void run() {
        //find difference and calculate new avatar position
        Vector3f diff = platformNode.getWorldTranslation().subtract(platformInitPosition);
        Vector3f avatarNewPos = avatarInitPosition.add(diff);
        AvatarImiJME imiJME = (AvatarImiJME)avatarCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
        GameContext context = imiJME.getAvatarCharacter().getContext();
        //change avatar position
        imiJME.setInSync(false);
        PTransform xform = new PTransform(avatarCell.getWorldTransform().getRotation(null), avatarNewPos,
                            new Vector3f(1, 1, 1));
        context.getCharacter().getModelInst().setTransform(xform);
    }
    
    private class ThreadRemover implements GameStateChangeListener {

        public void enterInState(GameState gs) {
            String thisUserId = avatarCell.getCellID().toString();
            String stateChangeUserId = gs.getContext().getCharacter().getCharacterParams().getId();
            if (thisUserId.equals(stateChangeUserId) && (gs instanceof IdleState)) {
                ses.shutdownNow();
                GameStateChangeListenerRegisterar.deRegisterListener(this);
            }
        }

        public void exitfromState(GameState gs) {

        }

        public void changeInState(GameState gs, String string, boolean bln, String string1) {
            
        }
        
    }
    
}
