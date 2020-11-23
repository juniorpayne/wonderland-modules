/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
/**
 * Copyright (c) 2014, WonderBuilders, Inc., All Rights Reserved
 */
package org.jdesktop.wonderland.modules.bestview.client;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.CameraNode;
import com.wonderbuilders.modules.capabilitybridge.client.CapabilityBridge;
import imi.character.avatar.AvatarContext;
import imi.character.statemachine.GameContext;
import java.awt.event.MouseWheelEvent;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellComponent;
import org.jdesktop.wonderland.client.cell.CellRenderer;
import org.jdesktop.wonderland.client.cell.utils.CellPlacementUtils;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.input.EventClassFocusListener;
import org.jdesktop.wonderland.client.input.EventClassListener;
import org.jdesktop.wonderland.client.jme.CameraController;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.MainFrameImpl;
import org.jdesktop.wonderland.client.jme.input.MouseEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseWheelEvent3D;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.modules.avatarbase.client.AvatarClientPlugin;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarImiJME;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.WlAvatarCharacter;

/**
 * Best view camera
 *
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
public class BestViewCam extends EventClassFocusListener implements CameraController {

    private final WorldManager wm;
    private final CellTransform start;
    public ColorRGBA GLOW_COLOR = new ColorRGBA(ColorRGBA.yellow);
    private final CellTransform target;
    private final float distance;
    private final CameraController prevCam;
    private long startTime;
    private final long moveTime;
    private boolean doneMoving = false;
    private boolean dirty = false;
    private CellTransform transform;
    private CameraNode cameraNode;
    int i = 0;
    private float zoom = 0;
    boolean bestView = false;
    private Event genEvent;
    private Cell cell;
    private BestViewComponent.CameraType prevCameraType;

    public BestViewCam(CellTransform start, CellTransform target,
            float distance, long moveTime, CameraController prevCam,
            Event event, Cell cell, BestViewComponent.CameraType prevCameraType) {
        this.start = start;
        this.target = target;
        this.distance = distance;
        this.moveTime = moveTime;
        this.prevCam = prevCam;
        this.wm = WorldManager.getDefaultWorldManager();
        this.genEvent = event;
        this.cell = cell;
        this.prevCameraType = prevCameraType;
    }

    public void setEnabled(boolean enabled, CameraNode cameraNode) {
        if (enabled) {
            setCameraNode(cameraNode);
            setStartTime(System.currentTimeMillis());
            ClientContextJME.getInputManager().addGlobalEventListener(this);
        } else {
            setCameraNode(null);
            ClientContextJME.getInputManager().removeGlobalEventListener(this);
        }
    }

    public void compute() {

        if (doneMoving) {
            //enable listeners
            if (genEvent != null) {
                for (CellComponent comp : cell.getComponents()) {
                    if (comp instanceof CapabilityBridge && !(comp instanceof BestViewComponent)) {
                        CapabilityBridge bridge = (CapabilityBridge) comp;
                        EventClassListener listener = bridge.getMouseEventListener();
                        if (listener != null) {
                            listener.setEnabled(true);
                            listener.commitEvent(genEvent);
                        }
                    }
                }
                if (cell.getParent() != null) {
                    for (CellComponent comp : cell.getParent().getComponents()) {
                        if (comp instanceof CapabilityBridge && !(comp instanceof BestViewComponent)) {
                            CapabilityBridge bridge = (CapabilityBridge) comp;
                            EventClassListener listener = bridge.getMouseEventListener();
                            if (listener != null) {
                                listener.setEnabled(true);
                                listener.commitEvent(genEvent);
                            }
                        }
                    }
                }
            }
            genEvent = null;
            return;
        }
        // get the current time and location
        long relativeTime = System.currentTimeMillis() - getStartTime();
        float amt = (float) relativeTime / (float) moveTime;
        if (amt >= 1.0) {
            amt = 1.0f;
            doneMoving = true;
        }
        Quaternion t = target.getRotation(null);
        Vector3f distVec = CellPlacementUtils.getLookDirection(t, null);
        distVec.multLocal(distance);
        Vector3f origin = target.getTranslation(null);
        origin.subtractLocal(distVec);
        Vector3f st = start.getTranslation(null);
        st.interpolate(origin, amt);
        Quaternion sq = start.getRotation(null);
        sq.slerp(t, amt);
        transform = new CellTransform(sq, st);
        dirty = true;
    }

    @Override
    public Class[] eventClassesToConsume() {
        return new Class[]{MouseWheelEvent3D.class};
    }

    @Override
    public void commitEvent(Event event) {
        MouseWheelEvent me = (MouseWheelEvent) ((MouseEvent3D) event).getAwtEvent();
        int clicks = me.getWheelRotation();
        zoom -= clicks * 0.2f;
    }

    public void commit() {

        if (dirty && transform != null) {
            CameraNode camera = getCameraNode();
            // apply zoom
            Vector3f loc = transform.getTranslation(null);
            Quaternion look = transform.getRotation(null);
            Vector3f z = look.mult(new Vector3f(0, 0, zoom));
            loc.addLocal(z);
            camera.setLocalRotation(look);
            camera.setLocalTranslation(loc);
            wm.addToUpdateList(camera);
        }
    }

    private boolean didAvatarMoved() {
        Cell avatarCell = ClientContextJME.getViewManager().getPrimaryViewCell();
        CellRenderer rend = avatarCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
        WlAvatarCharacter myAvatar = ((AvatarImiJME) rend).getAvatarCharacter();
        GameContext context = myAvatar.getContext();
        return (context.getTriggerState().isKeyPressed(AvatarContext.TriggerNames.Move_Forward.ordinal())
            || context.getTriggerState().isKeyPressed(AvatarContext.TriggerNames.Move_Back.ordinal())
            || context.getTriggerState().isKeyPressed(AvatarContext.TriggerNames.Move_Left.ordinal())
            || context.getTriggerState().isKeyPressed(AvatarContext.TriggerNames.Move_Right.ordinal())
            || context.getTriggerState().isKeyPressed(AvatarContext.TriggerNames.Move_Strafe_Left.ordinal())
            || context.getTriggerState().isKeyPressed(AvatarContext.TriggerNames.Move_Strafe_Right.ordinal())
            || context.getTriggerState().isKeyPressed(AvatarContext.TriggerNames.Move_Up.ordinal())
            || context.getTriggerState().isKeyPressed(AvatarContext.TriggerNames.Move_Down.ordinal()));
    }
    
    public void viewMoved(CellTransform worldTransform) {
        // if the avatar moves, go back to the original camera
        if (doneMoving && didAvatarMoved()) {
            MainFrameImpl.getBestViewRB().setVisible(false);
            MainFrameImpl.getBestViewRB().setSelected(false);
            ClientContextJME.getViewManager().setCameraController(prevCam);

            //set checkbox according to previous view

            switch (prevCameraType) {
                case CHASE_CAMERA:
                    AvatarClientPlugin.getChaseCameraMI().setSelected(true);
                    break;
                case FIRST_PERSON:
                    MainFrameImpl.getFirstPersonRB().setSelected(true);
                    break;
                case THIRD_PERSON:
                    MainFrameImpl.getThirdPersonRB().setSelected(true);
                    break;
                case FRONT_CAMERA:
                    MainFrameImpl.getFrontPersonRB().setSelected(true);
                    break;
                default:
                    MainFrameImpl.getZoomRB().setSelected(true);

            }
        }

    }

    public CameraController getPrevCam() {
        return prevCam;
    }

    private synchronized void setCameraNode(CameraNode cameraNode) {
        this.cameraNode = cameraNode;
    }

    private synchronized CameraNode getCameraNode() {
        return cameraNode;
    }

    private synchronized void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    private synchronized long getStartTime() {
        return startTime;
    }
}
