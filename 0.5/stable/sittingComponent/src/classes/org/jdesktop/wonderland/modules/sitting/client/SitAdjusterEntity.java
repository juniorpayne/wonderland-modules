/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
package org.jdesktop.wonderland.modules.sitting.client;

import com.jme.scene.Node;
import com.jme.scene.state.RenderState.StateType;
import com.jme.scene.state.ZBufferState;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.mtgame.RenderManager;
import org.jdesktop.mtgame.RenderUpdater;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.TransformChangeListener;
import org.jdesktop.wonderland.client.cell.asset.AssetUtils;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.artimport.DeployedModel;
import org.jdesktop.wonderland.client.jme.artimport.LoaderManager;
import org.jdesktop.wonderland.common.cell.CellTransform;

/**
 * A visual Entity that displays the position of avatar's sitting/liedown
 *
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
public class SitAdjusterEntity extends Entity {

    private Cell cell = null;
    private Node rootNode = null;
    private Node adjusterNode = null;

    private Node rootNode_lieDown = null;
    private Node adjusterNode_lieDown = null;
    private TransformChangeListener updateListener = null;
    private TransformChangeListener updateListener_lieDown = null;
    private boolean entityInit = false;
    private boolean entityInit_lieDown = false;
    private static SitAdjusterEntity sitAdjusterEntity;
    private static SitAdjusterEntity lieDownAdjusterEntity;

    public SitAdjusterEntity(Cell cell) {
        super("Bounds Viewer");

        // Create a new Node that serves as the root for the bounds viewer
        // scene graph
        this.cell = cell;
    }

    public static SitAdjusterEntity getInstance(Cell cell) {
        if (sitAdjusterEntity == null) {
            sitAdjusterEntity = new SitAdjusterEntity(cell);
        }
        return sitAdjusterEntity;
    }

    public Node getRootNode() {
        return rootNode;
    }

    public static SitAdjusterEntity getInstance_lieDown(Cell cell) {
        if (lieDownAdjusterEntity == null) {
            lieDownAdjusterEntity = new SitAdjusterEntity(cell);
        }
        return lieDownAdjusterEntity;
    }

    public Node getRootNode_lieDown() {
        return rootNode_lieDown;
    }

    public void loadSitAdjuster() {
        try {
            rootNode = new Node("Bounds Viewer Node");
            DeployedModel m
                    = LoaderManager.getLoaderManager()
                    .getLoaderFromDeployment(AssetUtils
                            .getAssetURL("wla://Sittable/Sit-Adjuster-v4.kmz/Sit-Adjuster-v4.kmz.dep"));
            adjusterNode = m.getModelLoader().loadDeployedModel(m, null);
            RenderManager rm = ClientContextJME.getWorldManager().getRenderManager();
            RenderComponent rc = rm.createRenderComponent(rootNode);
            this.addComponent(RenderComponent.class, rc);

            // Set the Z-buffer state on the root node
            ZBufferState zbuf = (ZBufferState) rm.createRendererState(StateType.ZBuffer);
            zbuf.setEnabled(true);
            zbuf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
            rootNode.setRenderState(zbuf);

            rootNode.attachChild(adjusterNode);

            // Listen for changes to the cell's translation and apply the same
            // update to the root node of the bounds viewer. We also re-set the size
            // of the bounds: this handles the case where the bounds of the
            // scene graph has changed and we need to update the bounds viewer
            // accordingly.
            updateListener = new TransformChangeListener() {
                public void transformChanged(final Cell cell, TransformChangeListener.ChangeSource source) {
                    // We need to perform this work inside a proper updater, to
                    // make sure we are MT thread safe
                    final WorldManager wm = ClientContextJME.getWorldManager();
                    RenderUpdater u = new RenderUpdater() {
                        public void update(Object obj) {
                            CellTransform transform = cell.getWorldTransform();
                            rootNode.setLocalTranslation(transform.getTranslation(null));
                            rootNode.setLocalRotation(transform.getRotation(null));
                            // rootNode.setLocalScale(transform.getScaling(null));
                            wm.addToUpdateList(rootNode);
                        }
                    };
                    wm.addRenderUpdater(u, this);
                }
            };
            cell.addTransformChangeListener(updateListener);
            entityInit = true;
        } catch (IOException ex) {
            Logger.getLogger(SittingComponentProperties.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Sets whether the bounds viewer is visible (true) or invisible (false).
     *
     * @param visible True to make the affordance visible, false to not
     */
    public synchronized void setVisible(boolean visible) {
        WorldManager wm = ClientContextJME.getWorldManager();
        if (visible) {
            if (!entityInit) {
                loadSitAdjuster();
            }
            wm.addEntity(this);
        } else {
            wm.removeEntity(this);
        }
    }

    public void loadLieDownAdjuster() {
        try {
            rootNode_lieDown = new Node("Bounds Viewer Node");
            DeployedModel m
                    = LoaderManager.getLoaderManager()
                    .getLoaderFromDeployment(AssetUtils
                            .getAssetURL("wla://Sittable/LieDown-Adjuster-v3.kmz/LieDown-Adjuster-v3.kmz.dep"));
            adjusterNode_lieDown = m.getModelLoader().loadDeployedModel(m, null);
            RenderManager rm = ClientContextJME.getWorldManager().getRenderManager();
            RenderComponent rc = rm.createRenderComponent(rootNode_lieDown);
            this.addComponent(RenderComponent.class, rc);

            // Set the Z-buffer state on the root node
            ZBufferState zbuf = (ZBufferState) rm.createRendererState(StateType.ZBuffer);
            zbuf.setEnabled(true);
            zbuf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
            rootNode_lieDown.setRenderState(zbuf);

            rootNode_lieDown.attachChild(adjusterNode_lieDown);

            // Listen for changes to the cell's translation and apply the same
            // update to the root node of the bounds viewer. We also re-set the size
            // of the bounds: this handles the case where the bounds of the
            // scene graph has changed and we need to update the bounds viewer
            // accordingly.
            updateListener_lieDown = new TransformChangeListener() {
                public void transformChanged(final Cell cell, TransformChangeListener.ChangeSource source) {
                    // We need to perform this work inside a proper updater, to
                    // make sure we are MT thread safe
                    final WorldManager wm = ClientContextJME.getWorldManager();
                    RenderUpdater u = new RenderUpdater() {
                        public void update(Object obj) {
                            CellTransform transform = cell.getWorldTransform();
                            rootNode_lieDown.setLocalTranslation(transform.getTranslation(null));
                            rootNode_lieDown.setLocalRotation(transform.getRotation(null));
                            // rootNode.setLocalScale(transform.getScaling(null));
                            wm.addToUpdateList(rootNode_lieDown);
                        }
                    };
                    wm.addRenderUpdater(u, this);
                }
            };
            cell.addTransformChangeListener(updateListener_lieDown);
            entityInit_lieDown = true;
        } catch (IOException ex) {
            Logger.getLogger(SittingComponentProperties.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Sets whether the bounds viewer is visible (true) or invisible (false).
     *
     * @param visible True to make the affordance visible, false to not
     */
    public synchronized void setVisible_lieDown(boolean visible) {
        System.out.println("visible : " + visible);

        WorldManager wm = ClientContextJME.getWorldManager();
        if (visible) {
            if (!entityInit_lieDown) {
                loadLieDownAdjuster();
            }
            wm.addEntity(this);
        } else {
            wm.removeEntity(this);
        }
    }

    public void dispose() {
        // First, to make sure the affordance is no longer visible
        setVisible(false);
        setVisible_lieDown(false);
        // Clean up all of the listeners so this class gets properly garbage
        // collected.
        cell.removeTransformChangeListener(updateListener);
        updateListener = null;
        rootNode = null;
        adjusterNode = null;

        cell.removeTransformChangeListener(updateListener_lieDown);
        updateListener_lieDown = null;
        rootNode_lieDown = null;
        adjusterNode_lieDown = null;
    }

}
