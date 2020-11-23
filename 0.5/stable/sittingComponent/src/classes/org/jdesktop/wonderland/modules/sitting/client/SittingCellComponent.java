/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
/**
 * Project Wonderland
 *
 * Copyright (c) 2010 - 2012, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */

package org.jdesktop.wonderland.modules.sitting.client;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import imi.character.avatar.AvatarContext;
import imi.character.behavior.CharacterBehaviorManager;
import imi.character.behavior.GoSit;
import imi.character.statemachine.GameContext;
import imi.character.statemachine.GameState;
import imi.character.statemachine.GameStateChangeListener;
import imi.character.statemachine.GameStateChangeListenerRegisterar;
import imi.character.statemachine.corestates.SitState;
import imi.scene.PMatrix;
import imi.utils.MathUtils;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRootPane;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.Cell.RendererType;
import org.jdesktop.wonderland.client.cell.CellComponent;
import org.jdesktop.wonderland.client.cell.CellRenderer;
import org.jdesktop.wonderland.client.cell.ChannelComponent;
import org.jdesktop.wonderland.client.cell.annotation.UsesCellComponent;
import org.jdesktop.wonderland.client.cell.utils.CellPlacementUtils;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuActionListener;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuItem;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuItemEvent;
import org.jdesktop.wonderland.client.contextmenu.SimpleContextMenuItem;
import org.jdesktop.wonderland.client.contextmenu.cell.ContextMenuComponent;
import org.jdesktop.wonderland.client.contextmenu.spi.ContextMenuFactorySPI;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.input.EventClassListener;
import org.jdesktop.wonderland.client.input.InputManager;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.client.jme.MainFrameImpl;
import org.jdesktop.wonderland.client.jme.ViewManager;
import org.jdesktop.wonderland.client.jme.cellrenderer.CellRendererJME;
import org.jdesktop.wonderland.client.jme.input.MouseButtonEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseEvent3D.ButtonId;
import org.jdesktop.wonderland.client.scenemanager.event.ContextEvent;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;
import org.jdesktop.wonderland.modules.avatarbase.client.AvatarClientPlugin;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarCollisionChangeRequestEvent;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarImiJME;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.WlAvatarCharacter;
import org.jdesktop.wonderland.modules.sitting.common.SittingCellComponentClientState;
import org.jdesktop.wonderland.modules.sitting.common.VehicleThreadMessage;

/**
 * Client-side sitting cell component
 *
 * @author Morris Ford
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
public class SittingCellComponent extends CellComponent {

    private static Logger logger = Logger.getLogger(SittingCellComponent.class.getName());
    private MouseEventListener myListener = null;
    private WlAvatarCharacter myAvatar;
    private CellRendererJME ret = null;
    private Node localNode = null;

    static private JCheckBoxMenuItem collisionResponseEnabledMI = null;
    static private JCheckBoxMenuItem gravityResponseEnabledMI = null;
    static private boolean collision;
    static private boolean gravity;

    @UsesCellComponent
    private ContextMenuComponent contextComp = null;
    private ContextMenuFactorySPI menuFactory = null;

    private float sittingTranslationX;
    private float sittingTranslationY;
    private float sittingTranslationZ;
    private float sittingRotationX;
    private float sittingRotationY;
    private float sittingRotationZ;
    private Quaternion sittingRotation;

    private float lieDownTranslationX;
    private float lieDownTranslationY;
    private float lieDownTranslationZ;
    private float lieDownRotationX;
    private float lieDownRotationY;
    private float lieDownRotationZ;
    private Quaternion lieDownRotation;

    private boolean leftClick = true;
    private boolean lieDown = false;
    private boolean lieDownImmediately = true;
    private Quaternion sitGetUpDir;
    private Vector3f sitGetUpPos;
    /**
     * cell id of the owner
     */
    private String ownerCellId = "";
    private static String currentCellId = null;
    /**
     * id of cell on which avatar sit before sit on current cell
     */
    private static String oldCellId = null;

    private static ScheduledExecutorService ses = null;
    private Vector3f sitPosition;
    private WindowCloseListener winCloseListener = null;

    public SittingCellComponent(final Cell cell) {
        super(cell);

        //Workaround for dealing with gravity during sitting process.
        GameStateChangeListenerRegisterar.registerListener(new GameStateChangeListener() {

            String prevState = "";

            public void enterInState(GameState gs) {
                //If we enter in idle state and if previous state was sit then enable gravity.
                if (ClientContextJME.getViewManager().getPrimaryViewCell() != null) {
                    String thisUserId = ClientContextJME.getViewManager().getPrimaryViewCell().getCellID().toString();
                    String stateChangeUserId = gs.getContext().getCharacter().getCharacterParams().getId();
                    if (thisUserId.equals(stateChangeUserId)) {
                        if (gs.getName().equals("Idle") && prevState.equals("Sit")) {
                            enableCollisionAndGravity();
                        }
                        if (gs.getName().equals("Sit") && prevState.equals("Idle")) {
                            disableCollisionAndGravity();
                        }
                    }
                }
            }

            public void exitfromState(GameState gs) {
                String thisUserId = ClientContextJME.getViewManager().getPrimaryViewCell().getCellID().toString();
                String stateChangeUserId = gs.getContext().getCharacter().getCharacterParams().getId();
                if (thisUserId.equals(stateChangeUserId)) {
                    prevState = gs.getName();
                }
                if (gs instanceof SitState) {
                    //currentCellId = null;
                }
            }

            public void changeInState(GameState gs, String string, boolean bln, String string1) {
                String thisUserId = ClientContextJME.getViewManager().getPrimaryViewCell().getCellID().toString();
                String stateChangeUserId = gs.getContext().getCharacter().getCharacterParams().getId();
                if (thisUserId.equals(stateChangeUserId)) {
                    if (gs instanceof SitState) {
                        if (string.equals("sitting") && bln && string1.equals("enter")) {
                            //avatar sitting done
                        } else if (string.equals("liedown") && bln && string1.equals("enter")) {
                            //avatar lie down done
                        } else if (string.equals("liedown") && bln && string1.equals("exit")) {
                            //avatar lie down to sitting
                            if (ses != null) {
                                ses.shutdown();
                                ses = null;
                            }

                            //start vehicle thread for cell with name "MRI-MACHINE"
                            //we can make other cell as vehicle by attaching vehicle thread to that cell
                            if (cell.getName().equals("MRI-MACHINE")) {
                                Cell avatarCell = ClientContextJME.getViewManager().getPrimaryViewCell();
                                startPlatformThread(avatarCell, ((SitState) gs).getSittingDownPosition());
                                sendVehicleThreadMessage(((SitState) gs).getSittingDownPosition());
                                JmeClientMain.getFrame().getFrame().addWindowListener(getWindowCloseListener());
                            }
                        } else if (string.equals("sitting") && bln && string1.equals("exit")) {
                            //avatar stand up
                            ownerCellId = "";
                            VehicleThreadMessage msg = new VehicleThreadMessage(ownerCellId);
                            msg.setInitPosX(Vector3f.ZERO.x);
                            msg.setInitPosY(Vector3f.ZERO.y);
                            msg.setInitPosZ(Vector3f.ZERO.z);
                            removeWindowCloseListener();
                        }
                    }
                }
            }
        });
    }

    public void processSitting() {
        Cell avatarCell = ClientContextJME.getViewManager().getPrimaryViewCell();
        CellRenderer rend = avatarCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
        myAvatar = ((AvatarImiJME) rend).getAvatarCharacter();
        GameContext context = myAvatar.getContext();
        //first save the old cell id and then change current one
        oldCellId = currentCellId;
        if (currentCellId != null && !currentCellId.equals(cell.getCellID().toString())) {
            goSit();
        } else {
            if (!(context.getCurrentState() instanceof SitState)) {
                goSit();
            }
            if (lieDown && !lieDownImmediately) {
                //now trigger lie down
                if (context.getCurrentState() instanceof SitState) {

                    SitState ss = (SitState) context.getCurrentState();

                    if (ses != null) {
                        ses.shutdown();
                        ses = null;
                    }

                    //start vehicle thread
                    if (cell.getName().equals("MRI-MACHINE")) {
                        startPlatformThread(avatarCell, ss.getLieDownPosition());
                        sendVehicleThreadMessage(ss.getLieDownPosition());
                        JmeClientMain.getFrame().getFrame().addWindowListener(getWindowCloseListener());

                    }

                    context.triggerPressed(AvatarContext.TriggerNames.LieDownOnClick.ordinal());
                }
            }
        }
    }

    public void goSit() {
        currentCellId = cell.getCellID().toString();
        //Disable gravity before sitting.
        disableCollisionAndGravity();

        //get cell transform of sceneroot of cell
        ret = (CellRendererJME) cell.getCellRenderer(RendererType.RENDERER_JME);
        Entity mye = ret.getEntity();
        RenderComponent rc = (RenderComponent) mye.getComponent(RenderComponent.class);
        localNode = rc.getSceneRoot();
        Vector3f v3fa = localNode.getWorldTranslation();
        Quaternion quata = localNode.getWorldRotation();

        //calculate the y rotation for sitting
        Vector3f axis = new Vector3f();
        float angle;
        angle = quata.toAngleAxis(axis);
        if (axis.y < 0.0f) {
            angle = -angle;
        }

        float[] anglesxz = new float[3];
        quata.toAngles(anglesxz);

        float[] angles = new float[3];
        sittingRotation.toAngles(angles);
        angles[1] += angle + Math.toRadians(20.0f);
        //angles[0] += anglesxz[0];
        //angles[2] += anglesxz[2];

        //calculate x & z inclined for sitting
        float xSittingInc = (float) Math.sin(angles[1]) * 0.5f;
        float zSittingInc = (float) Math.cos(angles[1]) * 0.5f;

        Vector3f sitPos = new Vector3f(v3fa.x + sittingTranslationX + xSittingInc, v3fa.y + sittingTranslationY + (-0.5f), v3fa.z + sittingTranslationZ + zSittingInc);
        Vector3f liePos = null;
        Quaternion lieDir = null;
        if (lieDown) {
            float[] angles1 = new float[3];
            lieDownRotation.toAngles(angles1);
            angles1[1] += angle + Math.toRadians(20.0f);

            //calculate x & z inclined for sitting
            float xSittingInc1 = (float) Math.sin(angles1[1]) * 0.5f;
            float zSittingInc1 = (float) Math.cos(angles1[1]) * 0.5f;
            liePos = new Vector3f(v3fa.x + lieDownTranslationX + xSittingInc1, v3fa.y + lieDownTranslationY + (-0.5f), v3fa.z + lieDownTranslationZ + zSittingInc1);
            lieDir = new Quaternion(angles1);
        }

        //prepare sit position and direction
        Vector3f sitDir = getLookDirection1(new Quaternion(angles), null);
        SittingChair ac = new SittingChair(sitPos, sitDir);
        Cell avatarCell = ClientContextJME.getViewManager().getPrimaryViewCell();
        CellRenderer rend = avatarCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
        myAvatar = ((AvatarImiJME) rend).getAvatarCharacter();
        GameContext context = myAvatar.getContext();
        CharacterBehaviorManager helm = context.getBehaviorManager();
        helm.clearTasks();
        helm.setEnable(true);
        if (lieDown) {
            helm.addTaskToTop(new GoSit(ac, (AvatarContext) context, lieDown, lieDownImmediately, sitPos, new Quaternion(angles), liePos, lieDir, ClientContextJME.getViewManager().getPrimaryViewCell().getCellID().toString()));
        } else {
            helm.addTaskToTop(new GoSit(ac, (AvatarContext) context, lieDown, lieDownImmediately,
                    ClientContextJME.getViewManager().getPrimaryViewCell().getCellID().toString()));
        }

        //start vehicle thread
        if (cell.getName().equals("MRI-MACHINE")) {
            startPlatformThread(avatarCell, sitPos);
            sendVehicleThreadMessage(sitPos);
            JmeClientMain.getFrame().getFrame().addWindowListener(getWindowCloseListener());
        }
    }

    private void startPlatformThread(final Cell avatarCell, final Vector3f initPos) {
        //check if sitting/sleeping done?
        Thread th = new Thread(new Runnable() {

            public void run() {
                CellRenderer rend = avatarCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
                WlAvatarCharacter charr = ((AvatarImiJME) rend).getAvatarCharacter();
                SitState sitState = (SitState) charr.getContext().getState(SitState.class);
                while (sitState == null) {
                    sitState = (SitState) charr.getContext().getState(SitState.class);
                }
                while (!sitState.isSitting() && !sitState.isSleeping()) {
                    sitState = (SitState) charr.getContext().getState(SitState.class);
                }
                //get platform node
                Node platformNode = getTargetNode(cell, "MRI_Platform");

                //schedule platform thread
                ses = Executors.newSingleThreadScheduledExecutor();
                VehicleThread mriThread = new VehicleThread(avatarCell, platformNode, ses, initPos);
                ses.scheduleAtFixedRate(mriThread, 0, 1, TimeUnit.MILLISECONDS);
            }
        });
        th.start();

    }

    private Node getTargetNode(Cell cell, String name) {
        CellRendererJME rend = (CellRendererJME) cell.getCellRenderer(Cell.RendererType.RENDERER_JME);
        RenderComponent rc = rend.getEntity().getComponent(RenderComponent.class);
        return (Node) rc.getSceneRoot().getChild(name);
    }

    public void setGettingUpSitPosition(Vector3f v, Quaternion q) {
        sitGetUpPos = v;
        sitGetUpDir = q;
    }

    public Vector3f getSitGetUpPosition() {
        return sitGetUpPos;
    }

    public Quaternion getSitGetUpDirection() {
        return sitGetUpDir;
    }

    public static Vector3f getLookDirection1(Quaternion rotation, Vector3f v) {
        if (v == null) {
            v = new Vector3f(0, 0, 1);
        } else {
            v.set(0, 0, 1);
        }
        rotation.multLocal(v);
        v.normalizeLocal();
        return v;
    }

    public void goSitDirection() {
        ret = (CellRendererJME) cell.getCellRenderer(RendererType.RENDERER_JME);
        Entity mye = ret.getEntity();
        RenderComponent rc = (RenderComponent) mye.getComponent(RenderComponent.class);
        localNode = rc.getSceneRoot();
        Vector3f v3fa = localNode.getWorldTranslation();
        Quaternion quata = localNode.getWorldRotation();

        //calculate the y rotation for sitting
        Vector3f axis = new Vector3f();
        float angle;
        angle = quata.toAngleAxis(axis);
        if (axis.y < 0.0f) {
            angle = -angle;
        }

        float[] angles = new float[3];
        Quaternion rotation1 = new Quaternion(new float[]{0, sittingRotationY, 0});
        rotation1.toAngles(angles);
        angles[1] += angle + Math.toRadians(20.0f);
        //calculate x & z inclined for sitting
        float xSittingInc = (float) Math.sin(angles[1]) * 0.5f;
        float zSittingInc = (float) Math.cos(angles[1]) * 0.5f;

        //prepare sit position and direction
        Vector3f sitDir = CellPlacementUtils.getLookDirection(new Quaternion(angles), null);

        MathUtils.MathUtilsContext mathContext = MathUtils.getContext();
        Cell avatarCell = ClientContextJME.getViewManager().getPrimaryViewCell();
        CellRenderer rend = avatarCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
        myAvatar = ((AvatarImiJME) rend).getAvatarCharacter();
        GameContext context = myAvatar.getContext();

        PMatrix localMat = ((AvatarContext) context).getController().getTransform().getLocalMatrix(true);
        PMatrix look = new PMatrix();
        MathUtils.lookAt(Vector3f.ZERO.add(sitDir.mult(-1.0f)),
                Vector3f.ZERO,
                Vector3f.UNIT_Y,
                look,
                mathContext);
        localMat.set(look);
    }

    private void enableCollisionAndGravity() {
        if (collisionResponseEnabledMI != null) {
            collision = true;
            gravity = true;
            ClientContext.getInputManager().postEvent(new AvatarCollisionChangeRequestEvent(collision, gravity));
            collisionResponseEnabledMI.setSelected(collision);
            gravityResponseEnabledMI.setSelected(gravity);
        }
    }

    private void disableCollisionAndGravity() {
        initMenuItems();
        if (collisionResponseEnabledMI != null) {
            collision = false;
            gravity = false;
            ClientContext.getInputManager().postEvent(new AvatarCollisionChangeRequestEvent(collision, gravity));
            collisionResponseEnabledMI.setSelected(collision);
            gravityResponseEnabledMI.setSelected(gravity);
        }
    }

    private void initMenuItems() {
        MainFrameImpl frame = (MainFrameImpl) JmeClientMain.getFrame();
        JRootPane jrp = (JRootPane) frame.getComponent(0);
        JMenuBar jmb = jrp.getJMenuBar();
        JMenu jm = null;
        int index = findMenu(jmb, "Tools");
        if (index != -1) {
            jm = jmb.getMenu(index);
            index = findMenu(jm, "Collision Enabled");
            if (index != -1) {
                collisionResponseEnabledMI = (JCheckBoxMenuItem) jm.getSubElements()[0].getSubElements()[index];
            }
            index = findMenu(jm, "Gravity Enabled");
            if (index != -1) {
                gravityResponseEnabledMI = (JCheckBoxMenuItem) jm.getSubElements()[0].getSubElements()[index];
            }
        }
    }

    private int findMenu(Object jComponent, String menuText) {

        if (jComponent instanceof JMenuBar) {
            JMenuBar jBar = (JMenuBar) jComponent;
            JMenu jm;
            for (int i = 0; i < jBar.getMenuCount(); i++) {
                jm = jBar.getMenu(i);
                if (jm.getText().equals(menuText)) {
                    return i;
                }
            }
        } else if (jComponent instanceof JMenu) {
            JMenu jMenu = (JMenu) jComponent;
            JMenuItem jMenuItem;
            for (int i = 0; i < jMenu.getMenuComponentCount(); i++) {
                jMenuItem = jMenu.getItem(i);
                if (jMenuItem.getText().equals(menuText)) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public void setClientState(CellComponentClientState clientState) {
        super.setClientState(clientState);
        sittingTranslationX = ((SittingCellComponentClientState) clientState).getSittingTranslationX();
        sittingTranslationY = ((SittingCellComponentClientState) clientState).getSittingTranslationY();
        sittingTranslationZ = ((SittingCellComponentClientState) clientState).getSittingTranslationZ();
        sittingRotationX = (float) Math.toRadians(((SittingCellComponentClientState) clientState).getSittingRotationX());
        sittingRotationY = (float) Math.toRadians(((SittingCellComponentClientState) clientState).getSittingRotationY());
        sittingRotationZ = (float) Math.toRadians(((SittingCellComponentClientState) clientState).getSittingRotationZ());
        lieDownTranslationX = ((SittingCellComponentClientState) clientState).getLieDownTranslationX();
        lieDownTranslationY = ((SittingCellComponentClientState) clientState).getLieDownTranslationY();
        lieDownTranslationZ = ((SittingCellComponentClientState) clientState).getLieDownTranslationZ();
        lieDownRotationX = (float) Math.toRadians(((SittingCellComponentClientState) clientState).getLieDownRotationX());
        lieDownRotationY = (float) Math.toRadians(((SittingCellComponentClientState) clientState).getLieDownRotationY());
        lieDownRotationZ = (float) Math.toRadians(((SittingCellComponentClientState) clientState).getLieDownRotationZ());
        leftClick = ((SittingCellComponentClientState) clientState).isLeftClick();
        lieDown = ((SittingCellComponentClientState) clientState).isLieDown();
        lieDownImmediately = ((SittingCellComponentClientState) clientState).isLieDownImmediately();
        sittingRotation = new Quaternion(new float[]{sittingRotationX, sittingRotationY, sittingRotationZ});
        lieDownRotation = new Quaternion(new float[]{lieDownRotationX, lieDownRotationY, lieDownRotationZ});
        ownerCellId = ((SittingCellComponentClientState) clientState).getOwnerCellId();
        if (!ownerCellId.equals("")) {
            sitPosition = new Vector3f(((SittingCellComponentClientState) clientState).getInitPosX(), ((SittingCellComponentClientState) clientState).getInitPosY(), ((SittingCellComponentClientState) clientState).getInitPosZ());
            if (cell.getStatus().equals(CellStatus.VISIBLE)) {
                verifyOwner();
            }
        }
    }

    @Override
    protected void setStatus(CellStatus status, boolean increasing) {
        super.setStatus(status, increasing);
        logger.warning("Setting status on SittingCellComponent to " + status);
        switch (status) {
            case VISIBLE:
                /* Get local node */
                if (increasing) {
                    if (myListener == null) {
                        ret = (CellRendererJME) cell.getCellRenderer(RendererType.RENDERER_JME);
                        Entity mye = ret.getEntity();

                        myListener = new MouseEventListener();
                        myListener.addToEntity(mye);
                    }
                    if (menuFactory == null) {
                        final MenuItemListener l = new MenuItemListener();
                        menuFactory = new ContextMenuFactorySPI() {
                            public ContextMenuItem[] getContextMenuItems(ContextEvent event) {
                                return new ContextMenuItem[]{
                                    new SimpleContextMenuItem("Sit Here", l)
                                };
                            }
                        };

                        contextComp.addContextMenuFactory(menuFactory);
                    }
                    // add message receiver for synchronizing state
                    ChannelComponent channel = cell.getComponent(ChannelComponent.class);
                    channel.addMessageReceiver(VehicleThreadMessage.class, new VehicleThreadMessageReceiver());
                }
                //attaching vehicle thread
                if (!ownerCellId.equals("")) {
                    verifyOwner();
                }
                break;
            case DISK:
                if (myListener != null) {
                    ret = (CellRendererJME) cell.getCellRenderer(RendererType.RENDERER_JME);
                    Entity mye = ret.getEntity();

                    myListener.removeFromEntity(mye);
                    myListener = null;
                }
                if (menuFactory != null) {
                    contextComp.removeContextMenuFactory(menuFactory);
                    menuFactory = null;
                }
                break;
        }

    }

    private void verifyOwner() {
        if (cell.getName().equals("MRI-MACHINE")) {
            Cell ownerCell = null;
            try {
                TimeUnit.SECONDS.sleep(5);//Thread.sleep(500, 500);
            } catch (InterruptedException ex) {
                Logger.getLogger(SittingCellComponent.class.getName()).log(Level.SEVERE, null, ex);
            }
            for (Cell cell1 : cell.getCellCache().getRootCells()) {
                if (cell1.getCellID().toString().equals(ownerCellId)) {
                    ownerCell = cell1;
                    break;
                }
            }
            if (ownerCell != null) {
                if (ses != null) {
                    ses.shutdown();
                    ses = null;
                }

                startPlatformThread(ownerCell, sitPosition);
            } else {
                ownerCellId = "";
                VehicleThreadMessage msg = new VehicleThreadMessage(ownerCellId);
                msg.setInitPosX(Vector3f.ZERO.x);
                msg.setInitPosY(Vector3f.ZERO.y);
                msg.setInitPosZ(Vector3f.ZERO.z);

            }
        }
    }

    class MouseEventListener extends EventClassListener {

        @Override
        public Class[] eventClassesToConsume() {
            return new Class[]{MouseButtonEvent3D.class};
        }

        @Override
        public void commitEvent(Event event) {

        }

        @Override
        public void computeEvent(Event event) {
            if (leftClick) {
                MouseButtonEvent3D mbe = (MouseButtonEvent3D) event;
                MouseEvent awt = (MouseEvent) mbe.getAwtEvent();
                if (awt.getID() != MouseEvent.MOUSE_PRESSED) {
                    return;
                }

                ButtonId butt = mbe.getButton();

                if (awt.getID() == MouseEvent.MOUSE_PRESSED
                        && (butt == ButtonId.BUTTON1)
                        && !InputManager.isAnyKeyPressed()) {
                    processSitting();
                }
            }
        }
    }

    class MenuItemListener implements ContextMenuActionListener {

        public void actionPerformed(ContextMenuItemEvent event) {
            if (!leftClick) {
                processSitting();
            }
        }
    }

    public boolean onLeftClick() {
        return leftClick;
    }

    private class VehicleThreadMessageReceiver implements ChannelComponent.ComponentMessageReceiver {

        @Override
        public void messageReceived(CellMessage message) {
            VehicleThreadMessage msg = (VehicleThreadMessage) message;
            if (!msg.getSenderID().equals(cell.getCellCache().getSession().getID())) {

                if (ses != null) {
                    ses.shutdown();
                    ses = null;
                }
                if (!msg.getAvatarCellId().equals("")) //start vehicle platform thread
                {
                    startPlatformThread(AvatarClientPlugin.getAvatarCellByCellId(msg.getAvatarCellId()), new Vector3f(msg.getInitPosX(), msg.getInitPosY(), msg.getInitPosZ()));
                }
            }
        }
    }

    void sendVehicleThreadMessage(Vector3f initPos) {
        Cell avatarCell = ClientContextJME.getViewManager().getPrimaryViewCell();
        VehicleThreadMessage msg = new VehicleThreadMessage(avatarCell.getCellID().toString());
        msg.setInitPosX(initPos.x);
        msg.setInitPosY(initPos.y);
        msg.setInitPosZ(initPos.z);
        cell.sendCellMessage(msg);
    }

    public static String getCurrentCellId() {
        return currentCellId;
    }

    public static String getOldCellId() {
        return oldCellId;
    }

    private class WindowCloseListener extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            try {
                if (!ownerCellId.equals("") && !ownerCellId.isEmpty() && ownerCellId.equals(ViewManager.getViewManager().getPrimaryViewCell().getCellID().toString())) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    ownerCellId = "";
                    if (ses != null) {
                        ses.shutdown();
                        ses = null;
                    }

                    VehicleThreadMessage msg = new VehicleThreadMessage(ownerCellId);
                    msg.setInitPosX(Vector3f.ZERO.x);
                    msg.setInitPosY(Vector3f.ZERO.y);
                    msg.setInitPosZ(Vector3f.ZERO.z);
                    removeWindowCloseListener();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    WindowCloseListener getWindowCloseListener() {
        if (winCloseListener == null) {
            winCloseListener = new WindowCloseListener();
        }
        return winCloseListener;
    }

    void removeWindowCloseListener() {
        if (winCloseListener != null) {
            JmeClientMain.getFrame().getFrame().removeWindowListener(winCloseListener);
            winCloseListener = null;
        }
    }
}
    