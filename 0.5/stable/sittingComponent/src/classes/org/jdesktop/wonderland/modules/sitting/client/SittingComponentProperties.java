/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
/**
 * Open Wonderland
 *
 * Copyright (c) 2010 - 2011, Open Wonderland Foundation, All Rights Reserved
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
import java.util.ResourceBundle;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.mtgame.RenderUpdater;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.TransformChangeListener;
import org.jdesktop.wonderland.client.cell.properties.CellPropertiesEditor;
import org.jdesktop.wonderland.client.cell.properties.annotation.PropertiesFactory;
import org.jdesktop.wonderland.client.cell.properties.spi.PropertiesFactorySPI;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.cellrenderer.CellRendererJME;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.modules.sitting.common.SittingCellComponentServerState;

/**
 *
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
@PropertiesFactory(SittingCellComponentServerState.class)
public class SittingComponentProperties extends javax.swing.JPanel implements PropertiesFactorySPI {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("org/jdesktop/wonderland/modules/sitting/client/resources/Bundle");
    private CellPropertiesEditor editor = null;
    
    private float currentSittingTranslationX;
    private float currentSittingTranslationY;
    private float currentSittingTranslationZ;
    private float currentSittingRotationX;
    private float currentSittingRotationY;
    private float currentSittingRotationZ;
    private float currentLieDownTranslationX;
    private float currentLieDownTranslationY;
    private float currentLieDownTranslationZ;
    private float currentLieDownRotationX;
    private float currentLieDownRotationY;
    private float currentLieDownRotationZ;
    private boolean currentLeftClick = true;
    private boolean currentLieDown = false;
    private boolean currentLieDownImmediately = true;
    
    private float origSittingTranslationX;
    private float origSittingTranslationY;
    private float origSittingTranslationZ;
    private float origSittingRotationX;
    private float origSittingRotationY;
    private float origSittingRotationZ;
    private float origLieDownTranslationX;
    private float origLieDownTranslationY;
    private float origLieDownTranslationZ;
    private float origLieDownRotationX;
    private float origLieDownRotationY;
    private float origLieDownRotationZ;
    private boolean origLeftClick = false;
    private boolean origLieDown = false;
    private boolean origLieDownImmediately = false;

    private MyTransformChangeListener myTransChangeListener = null;

    /**
     * Creates new form NewJPanel
     */
    public SittingComponentProperties() {
        initComponents();
        //Translation spinners configuration
        Float value = new Float(0);
        Float min = new Float(Float.NEGATIVE_INFINITY);
        Float max = new Float(Float.POSITIVE_INFINITY);
        Float step = new Float(0.1);
        SpinnerNumberModel xTranslationModel = new SpinnerNumberModel(value, min, max, step);
        SpinnerNumberModel yTranslationModel = new SpinnerNumberModel(value, min, max, step);
        SpinnerNumberModel zTranslationModel = new SpinnerNumberModel(value, min, max, step);
        sittingTranslationXTF.setModel(xTranslationModel);
        sittingTranslationYTF.setModel(yTranslationModel);
        sittingTranslationZTF.setModel(zTranslationModel);
        sittingTranslationXTF.setEditor(
                new JSpinner.NumberEditor(sittingTranslationXTF, "########0.00"));
        sittingTranslationYTF.setEditor(
                new JSpinner.NumberEditor(sittingTranslationYTF, "########0.00"));
        sittingTranslationZTF.setEditor(
                new JSpinner.NumberEditor(sittingTranslationZTF, "########0.00"));

        //Rotation spinners configuration
        value = new Float(0);
        min = new Float(-360);
        max = new Float(360);
        step = new Float(1);
        SpinnerNumberModel xRotationModel = new SpinnerNumberModel(value, min, max, step);
        SpinnerNumberModel yRotationModel = new SpinnerNumberModel(value, min, max, step);
        SpinnerNumberModel zRotationModel = new SpinnerNumberModel(value, min, max, step);
        sittingRotationXTF.setModel(xRotationModel);
        sittingRotationYTF.setModel(yRotationModel);
        sittingRotationZTF.setModel(zRotationModel);
        sittingRotationXTF.setEditor(
                new JSpinner.NumberEditor(sittingRotationXTF, "########0.00"));
        sittingRotationYTF.setEditor(
                new JSpinner.NumberEditor(sittingRotationYTF, "########0.00"));
        sittingRotationZTF.setEditor(
                new JSpinner.NumberEditor(sittingRotationZTF, "########0.00"));
        
         //LieOffset spinners configuration
        value = new Float(0);
        min = new Float(Float.NEGATIVE_INFINITY);
        max = new Float(Float.POSITIVE_INFINITY);
        step = new Float(0.1);
        SpinnerNumberModel xLieOffsetModel = new SpinnerNumberModel(value, min, max, step);
        SpinnerNumberModel yLieOffsetModel = new SpinnerNumberModel(value, min, max, step);
        SpinnerNumberModel zLieOffsetModel = new SpinnerNumberModel(value, min, max, step);
        lieDownTranslationXTF.setModel(xLieOffsetModel);
        lieDownTranslationYTF.setModel(yLieOffsetModel);
        lieDownTranslationZTF.setModel(zLieOffsetModel);
        lieDownTranslationXTF.setEditor(
                new JSpinner.NumberEditor(lieDownTranslationXTF, "########0.00"));
        lieDownTranslationYTF.setEditor(
                new JSpinner.NumberEditor(lieDownTranslationYTF, "########0.00"));
        lieDownTranslationZTF.setEditor(
                new JSpinner.NumberEditor(lieDownTranslationZTF, "########0.00"));
        
        //Heading spinners configuration
        value = new Float(0);
        min = new Float(-360);
        max = new Float(360);
        step = new Float(1);
        SpinnerNumberModel xHeadingModel = new SpinnerNumberModel(value, min, max, step);
        SpinnerNumberModel yHeadingModel = new SpinnerNumberModel(value, min, max, step);
        SpinnerNumberModel zHeadingModel = new SpinnerNumberModel(value, min, max, step);
        lieDownRotationXTF.setModel(xHeadingModel);
        lieDownRotationYTF.setModel(yHeadingModel);
        lieDownRotationZTF.setModel(zHeadingModel);
        lieDownRotationXTF.setEditor(
                new JSpinner.NumberEditor(lieDownRotationXTF, "########0.00"));
        lieDownRotationYTF.setEditor(
                new JSpinner.NumberEditor(lieDownRotationYTF, "########0.00"));
        lieDownRotationZTF.setEditor(
                new JSpinner.NumberEditor(lieDownRotationZTF, "########0.00"));

    }

    /**
     * @inheritDoc()
     */
    public String getDisplayName() {
        return BUNDLE.getString("Sitting_Component");
    }

    /**
     * @inheritDoc()
     */
    public JPanel getPropertiesJPanel() {
        return this;
    }

    /**
     * @inheritDoc()
     */
    public void setCellPropertiesEditor(CellPropertiesEditor editor) {
        this.editor = editor;
    }

    /**
     * @inheritDoc()
     */
    public void open() {
        CellServerState state = editor.getCellServerState();
        CellComponentServerState compState = state.getComponentServerState(SittingCellComponentServerState.class);
        if (compState != null) {
            SittingCellComponentServerState sittingCellComponentServerState = (SittingCellComponentServerState) compState;
            origSittingTranslationX = sittingCellComponentServerState.getSittingTranslationX();
            origSittingTranslationY = sittingCellComponentServerState.getSittingTranslationY();
            origSittingTranslationZ = sittingCellComponentServerState.getSittingTranslationZ();
            origSittingRotationX = sittingCellComponentServerState.getSittingRotationX();
            origSittingRotationY = sittingCellComponentServerState.getSittingRotationY();
            origSittingRotationZ = sittingCellComponentServerState.getSittingRotationZ();
            origLieDownTranslationX = sittingCellComponentServerState.getLieDownTranslationX();
            origLieDownTranslationY = sittingCellComponentServerState.getLieDownTranslationY();
            origLieDownTranslationZ = sittingCellComponentServerState.getLieDownTranslationZ();
            origLieDownRotationX = sittingCellComponentServerState.getLieDownRotationX();
            origLieDownRotationY = sittingCellComponentServerState.getLieDownRotationY();
            origLieDownRotationZ = sittingCellComponentServerState.getLieDownRotationZ();
            origLeftClick = sittingCellComponentServerState.isLeftClick();
            origLieDown = sittingCellComponentServerState.isLieDown();
            origLieDownImmediately = sittingCellComponentServerState.isLieDownImmediately();
            
            if (origLeftClick) {
                leftClickRadioButton.setSelected(true);
                rightClickRadioButton.setSelected(false);
            } else {
                leftClickRadioButton.setSelected(false);
                rightClickRadioButton.setSelected(true);
            }
            lieDownCheckbox.setSelected(origLieDown);
            if(origLieDownImmediately) {
                lieDownImmediatelyCheckbox.setSelected(true);
                sitLieDownCheckbox.setSelected(false);
            } else {
                lieDownImmediatelyCheckbox.setSelected(false);
                sitLieDownCheckbox.setSelected(true);
            }
            sittingAdjusterCheckbox.setSelected(false);
            lieDownAdjusterCheckbox.setSelected(false);
            sittingTranslationXTF.setValue(origSittingTranslationX);
            sittingTranslationYTF.setValue(origSittingTranslationY);
            sittingTranslationZTF.setValue(origSittingTranslationZ);
            sittingRotationXTF.setValue(origSittingRotationX);
            sittingRotationYTF.setValue(origSittingRotationY);
            sittingRotationZTF.setValue(origSittingRotationZ);
            lieDownTranslationXTF.setValue(origLieDownTranslationX);
            lieDownTranslationYTF.setValue(origLieDownTranslationY);
            lieDownTranslationZTF.setValue(origLieDownTranslationZ);
            lieDownRotationXTF.setValue(origLieDownRotationX);
            lieDownRotationYTF.setValue(origLieDownRotationY);
            lieDownRotationZTF.setValue(origLieDownRotationZ);
            
            currentSittingTranslationX = sittingCellComponentServerState.getSittingTranslationX();
            currentSittingTranslationY = sittingCellComponentServerState.getSittingTranslationY();
            currentSittingTranslationZ = sittingCellComponentServerState.getSittingTranslationZ();
            currentSittingRotationX = sittingCellComponentServerState.getSittingRotationX();
            currentSittingRotationY = sittingCellComponentServerState.getSittingRotationY();
            currentSittingRotationZ = sittingCellComponentServerState.getSittingRotationZ();
            currentLieDownTranslationX = sittingCellComponentServerState.getLieDownTranslationX();
            currentLieDownTranslationY = sittingCellComponentServerState.getLieDownTranslationY();
            currentLieDownTranslationZ = sittingCellComponentServerState.getLieDownTranslationZ();
            currentLieDownRotationX = sittingCellComponentServerState.getLieDownRotationX();
            currentLieDownRotationY = sittingCellComponentServerState.getLieDownRotationY();
            currentLieDownRotationZ = sittingCellComponentServerState.getLieDownRotationZ();
            currentLeftClick = sittingCellComponentServerState.isLeftClick();
            currentLieDown = sittingCellComponentServerState.isLieDown();
            currentLieDownImmediately = sittingCellComponentServerState.isLieDownImmediately();
        }

        //update sit adjuater when cell transform changes
        myTransChangeListener = new MyTransformChangeListener();
        editor.getCell().addTransformChangeListener(myTransChangeListener);
    }

    private class MyTransformChangeListener implements TransformChangeListener {

        public void transformChanged(Cell cell, ChangeSource source) {
            if (sittingAdjusterCheckbox.isSelected()) {
                changePositionOfAdjuster(); 
            }
            if (lieDownAdjusterCheckbox.isSelected()) {
                changePositionOfLieDownAdjuster();
            }
        }

    }

    /**
     * @inheritDoc()
     */
    public void close() {
        // Do nothing for now.
        SitAdjusterEntity.getInstance(editor.getCell())
                .setVisible(false);
        SitAdjusterEntity.getInstance_lieDown(editor.getCell())
                .setVisible_lieDown(false);
        editor.getCell().removeTransformChangeListener(myTransChangeListener);
        myTransChangeListener = null;

    }

    /**
     * @inheritDoc()
     */
    public void apply() {
        // Fetch the latest from the info text field and set it.
        CellServerState state = editor.getCellServerState();
        CellComponentServerState compState = state.getComponentServerState(SittingCellComponentServerState.class);
        ((SittingCellComponentServerState) compState).setLeftClick(currentLeftClick);
        ((SittingCellComponentServerState) compState).setLieDown(currentLieDown);
        ((SittingCellComponentServerState) compState).setLieDownImmediately(currentLieDownImmediately);
        ((SittingCellComponentServerState) compState).setSittingTranslationX(currentSittingTranslationX);
        ((SittingCellComponentServerState) compState).setSittingTranslationY(currentSittingTranslationY);
        ((SittingCellComponentServerState) compState).setSittingTranslationZ(currentSittingTranslationZ);
        ((SittingCellComponentServerState) compState).setSittingRotationX(currentSittingRotationX);
        ((SittingCellComponentServerState) compState).setSittingRotationY(currentSittingRotationY);
        ((SittingCellComponentServerState) compState).setSittingRotationZ(currentSittingRotationZ);
        ((SittingCellComponentServerState) compState).setLieDownTranslationX(currentLieDownTranslationX);
        ((SittingCellComponentServerState) compState).setLieDownTranslationY(currentLieDownTranslationY);
        ((SittingCellComponentServerState) compState).setLieDownTranslationZ(currentLieDownTranslationZ);
        ((SittingCellComponentServerState) compState).setLieDownRotationX(currentLieDownRotationX);
        ((SittingCellComponentServerState) compState).setLieDownRotationY(currentLieDownRotationY);
        ((SittingCellComponentServerState) compState).setLieDownRotationZ(currentLieDownRotationZ);
        sittingAdjusterCheckbox.setSelected(false);
        lieDownAdjusterCheckbox.setSelected(false);
        editor.addToUpdateList(compState);
    }

    /**
     * @inheritDoc()
     */
    public void restore() {
        // Restore from the original state stored.
        if (origLeftClick) {
            leftClickRadioButton.setSelected(false);
            rightClickRadioButton.setSelected(true);
        } else {
            leftClickRadioButton.setSelected(true);
            rightClickRadioButton.setSelected(false);
        }
        if(origLieDownImmediately) {
            lieDownImmediatelyCheckbox.setSelected(true);
            sitLieDownCheckbox.setSelected(false);
        } else {
            lieDownImmediatelyCheckbox.setSelected(false);
            sitLieDownCheckbox.setSelected(true);
        }
        lieDownCheckbox.setSelected(origLieDown);
        lieDownImmediatelyCheckbox.setSelected(origLieDownImmediately);
        sittingAdjusterCheckbox.setSelected(false);
        lieDownAdjusterCheckbox.setSelected(false);
        lieDownTranslationXTF.setValue(origLieDownTranslationX);
        lieDownTranslationYTF.setValue(origLieDownTranslationY);
        lieDownTranslationZTF.setValue(origLieDownTranslationZ);
        lieDownRotationXTF.setValue(origLieDownRotationX);
        lieDownRotationYTF.setValue(origLieDownRotationY);
        lieDownRotationZTF.setValue(origLieDownRotationZ);
        sittingTranslationXTF.setValue(origSittingTranslationX);
        sittingTranslationYTF.setValue(origSittingTranslationY);
        sittingTranslationZTF.setValue(origSittingTranslationZ);
        sittingRotationXTF.setValue(origSittingRotationX);
        sittingRotationYTF.setValue(origSittingRotationY);
        sittingRotationZTF.setValue(origSittingRotationZ);
    }

    private boolean isDirty() {
        
        if (origLeftClick != currentLeftClick) {
            return true;
        }
        
        if(origLieDownImmediately != currentLieDownImmediately) {
            return true;
        }
        
        if (origLieDown != currentLieDown) {
            return true;
        }
      
        if (currentSittingTranslationX!=origSittingTranslationX) {
            return true;
        }
        
        if (currentSittingTranslationY!=origSittingTranslationY) {
            return true;
        }
        
        if (currentSittingTranslationZ!=origSittingTranslationZ) {
            return true;
        }
        
        if (currentSittingRotationX!=origSittingRotationX) {
            return true;
        }
        
        if (currentSittingRotationY!=origSittingRotationY) {
            return true;
        }
        
        if (currentSittingRotationZ!=origSittingRotationZ) {
            return true;
        }
        
        if (currentLieDownTranslationX!=origLieDownTranslationX) {
            return true;
        }
        
        if (currentLieDownTranslationY!=origLieDownTranslationY) {
            return true;
        }
        
        if (currentLieDownTranslationZ!=origLieDownTranslationZ) {
            return true;
        }
        
        if (currentLieDownRotationX!=origLieDownRotationX) {
            return true;
        }
        
        if (currentLieDownRotationY!=origLieDownRotationY) {
            return true;
        }
        
        if (currentLieDownRotationZ!=origLieDownRotationZ) {
            return true;
        }

        return false;
    }

    private void checkAndSetDirty() {
        if (editor != null && isDirty()) {
            editor.setPanelDirty(SittingComponentProperties.class, true);
        } else {
            editor.setPanelDirty(SittingComponentProperties.class, false);
        }
    }

    public void changePositionOfAdjuster() {

        //get all values from the spinners
        final float txOffset = currentSittingTranslationX;
        final float tyOffset = currentSittingTranslationY;
        final float tzOffset = currentSittingTranslationZ;
        float rxOffset = currentSittingRotationX;
        float ryOffset = currentSittingRotationY;
        float rzOffset = currentSittingRotationZ;
      
        //get cell transform of the sceneroot of cell
        CellRendererJME ret = (CellRendererJME) editor.getCell().getCellRenderer(Cell.RendererType.RENDERER_JME);
        Entity mye = ret.getEntity();
        RenderComponent rc = (RenderComponent) mye.getComponent(RenderComponent.class);
        Node localNode = rc.getSceneRoot();
        Vector3f v3fa = localNode.getWorldTranslation();
        Quaternion quata = localNode.getWorldRotation();
        float[] angles = new float[3];
        angles = quata.toAngles(angles);

        //prepare the translation values
        final float tx = v3fa.x + txOffset;
        final float ty = v3fa.y + tyOffset;
        final float tz = v3fa.z + tzOffset;

        //prepare the rotation values
        //add 20 degree to y as an adjuster
        final float rx = (float) Math.toRadians((rxOffset + Math.toDegrees(angles[0])));
        final float ry = (float) Math.toRadians((ryOffset + Math.toDegrees(angles[1]) + 20.0f));
        final float rz = (float) Math.toRadians((rzOffset + Math.toDegrees(angles[2])));

        //apply the changes
        final WorldManager wm = ClientContextJME.getWorldManager();
        RenderUpdater u = new RenderUpdater() {
            public void update(Object obj) {
                SitAdjusterEntity.getInstance(editor.getCell()).getRootNode()
                        .setLocalTranslation(tx, ty, tz);
                SitAdjusterEntity.getInstance(editor.getCell()).getRootNode()
                        .setLocalRotation(new Quaternion(new float[]{rx, ry, rz}));
                WorldManager wm = ClientContextJME.getWorldManager();
                wm.addToUpdateList(SitAdjusterEntity.getInstance(editor.getCell()).getRootNode());
            }
        };
        wm.addRenderUpdater(u, this);
    }

    public void changePositionOfLieDownAdjuster() {

        final float lxOffset = currentLieDownTranslationX;
        final float lyOffset = currentLieDownTranslationY;
        final float lzOffset = currentLieDownTranslationZ;
        float hxOffset = currentLieDownRotationX;
        float hyOffset = currentLieDownRotationY;
        float hzOffset = currentLieDownRotationZ;
        
        //get cell transform of the sceneroot of cell
        CellRendererJME ret = (CellRendererJME) editor.getCell().getCellRenderer(Cell.RendererType.RENDERER_JME);
        Entity mye = ret.getEntity();
        RenderComponent rc = (RenderComponent) mye.getComponent(RenderComponent.class);
        Node localNode = rc.getSceneRoot();
        Vector3f v3fa = localNode.getWorldTranslation();
        Quaternion quata = localNode.getWorldRotation();
        float[] angles = new float[3];
        angles = quata.toAngles(angles);

        //prepare the translation values
        final float tx = v3fa.x + lxOffset;
        final float ty = v3fa.y + lyOffset + (-0.10f);
        final float tz = v3fa.z + lzOffset;

        //prepare the rotation values
        //add 20 degree to y as an adjuster
        final float rx = (float) Math.toRadians((hxOffset + Math.toDegrees(angles[0])));
        final float ry = (float) Math.toRadians((hyOffset + Math.toDegrees(angles[1]) + 20.0f));
        final float rz = (float) Math.toRadians((hzOffset + Math.toDegrees(angles[2])));

        //apply the changes
        final WorldManager wm = ClientContextJME.getWorldManager();
        RenderUpdater u = new RenderUpdater() {
            public void update(Object obj) {
                SitAdjusterEntity.getInstance_lieDown(editor.getCell()).getRootNode_lieDown()
                        .setLocalTranslation(tx, ty, tz);
                SitAdjusterEntity.getInstance_lieDown(editor.getCell()).getRootNode_lieDown()
                        .setLocalRotation(new Quaternion(new float[]{rx, ry, rz}));
                WorldManager wm = ClientContextJME.getWorldManager();
                wm.addToUpdateList(SitAdjusterEntity.getInstance_lieDown(editor.getCell()).getRootNode_lieDown());
            }
        };
        wm.addRenderUpdater(u, this);
    }

    private void enableLieDown(boolean enable) {
        System.out.println("enableLieDown : "+enable);
        //disable all components
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        leftClickRadioButton = new javax.swing.JRadioButton();
        rightClickRadioButton = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        sittingCheckbox = new javax.swing.JCheckBox();
        lieDownCheckbox = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        sittingRotationYTF = new javax.swing.JSpinner();
        jLabel8 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        sittingRotationXTF = new javax.swing.JSpinner();
        jLabel12 = new javax.swing.JLabel();
        sittingAdjusterCheckbox = new javax.swing.JCheckBox();
        jLabel20 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        sittingTranslationZTF = new javax.swing.JSpinner();
        jLabel17 = new javax.swing.JLabel();
        sittingTranslationYTF = new javax.swing.JSpinner();
        jLabel16 = new javax.swing.JLabel();
        sittingTranslationXTF = new javax.swing.JSpinner();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        sittingRotationZTF = new javax.swing.JSpinner();
        jLabel10 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        lieDownPanel = new javax.swing.JPanel();
        lieDownRotationLabel = new javax.swing.JLabel();
        lieDownRotationXLabel = new javax.swing.JLabel();
        lieDownRotationZTF = new javax.swing.JSpinner();
        lieDownMetersXLabel = new javax.swing.JLabel();
        lieDownTranslationXTF = new javax.swing.JSpinner();
        lieDownLocationXLabel = new javax.swing.JLabel();
        lieDownLocationYLabel = new javax.swing.JLabel();
        lieDownDegreesZLabel = new javax.swing.JLabel();
        lieDownTranslationYTF = new javax.swing.JSpinner();
        lieDownMetersYLabel = new javax.swing.JLabel();
        lieDownTranslationZTF = new javax.swing.JSpinner();
        lieDownRotationYTF = new javax.swing.JSpinner();
        lieDownLocationZLabel = new javax.swing.JLabel();
        lieDownDegreesXLabel = new javax.swing.JLabel();
        lieDownDegreesYLabel = new javax.swing.JLabel();
        lieDownMetersZLabel = new javax.swing.JLabel();
        lieDownLocationLabel = new javax.swing.JLabel();
        lieDownRotationZLabel = new javax.swing.JLabel();
        lieDownRotationXTF = new javax.swing.JSpinner();
        lieDownRotationYLabel = new javax.swing.JLabel();
        reclineLabel = new javax.swing.JLabel();
        reclineTF = new javax.swing.JSpinner();
        reclineDegreesLabel = new javax.swing.JLabel();
        lieDownImmediatelyCheckbox = new javax.swing.JRadioButton();
        sitLieDownCheckbox = new javax.swing.JRadioButton();
        lieDownAdjusterCheckbox = new javax.swing.JCheckBox();

        buttonGroup1.add(leftClickRadioButton);
        leftClickRadioButton.setSelected(true);
        leftClickRadioButton.setText("Left Click");
        leftClickRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                leftClickRadioButtonItemStateChanged(evt);
            }
        });

        buttonGroup1.add(rightClickRadioButton);
        rightClickRadioButton.setText("Right Click");
        rightClickRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rightClickRadioButtonItemStateChanged(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel5.setText("Trigger:");

        sittingCheckbox.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        sittingCheckbox.setSelected(true);
        sittingCheckbox.setText("Sit");

        lieDownCheckbox.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        lieDownCheckbox.setText("Lie Down");
        lieDownCheckbox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                lieDownCheckboxItemStateChanged(evt);
            }
        });
        lieDownCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lieDownCheckboxActionPerformed(evt);
            }
        });

        jLabel21.setText("Y:");

        jLabel22.setText("degrees");

        sittingRotationYTF.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sittingRotationYTFStateChanged(evt);
            }
        });

        jLabel8.setText("degrees");

        jLabel11.setText("X:");

        jLabel15.setText("degrees");

        sittingRotationXTF.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sittingRotationXTFStateChanged(evt);
            }
        });

        jLabel12.setText("Z:");

        sittingAdjusterCheckbox.setText("Show Sitting Adjuster");
        sittingAdjusterCheckbox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                sittingAdjusterCheckboxItemStateChanged(evt);
            }
        });

        jLabel20.setText("meters");

        jLabel18.setText("Z:");

        sittingTranslationZTF.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sittingTranslationZTFStateChanged(evt);
            }
        });

        jLabel17.setText("Y:");

        sittingTranslationYTF.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sittingTranslationYTFStateChanged(evt);
            }
        });

        jLabel16.setText("X:");

        sittingTranslationXTF.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sittingTranslationXTFStateChanged(evt);
            }
        });

        jLabel13.setText("meters");

        jLabel14.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel14.setText("Location");

        sittingRotationZTF.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sittingRotationZTFStateChanged(evt);
            }
        });

        jLabel10.setText("meters");

        jLabel37.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel37.setText("Rotation");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(sittingAdjusterCheckbox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(186, 186, 186))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel18)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(sittingTranslationZTF, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel17)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(sittingTranslationYTF, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel16)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(sittingTranslationXTF, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel20)
                                    .addComponent(jLabel13)
                                    .addComponent(jLabel10))))
                        .addGap(20, 20, 20)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel37)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(sittingRotationZTF, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel21)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(sittingRotationYTF, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel11)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(sittingRotationXTF, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(4, 4, 4)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(sittingTranslationXTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(sittingTranslationYTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(sittingTranslationZTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18)
                            .addComponent(jLabel20))
                        .addGap(8, 8, 8))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel37)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(sittingRotationXTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(sittingRotationYTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(sittingRotationZTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12)
                            .addComponent(jLabel15))
                        .addGap(7, 7, 7)))
                .addComponent(sittingAdjusterCheckbox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lieDownRotationLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        lieDownRotationLabel.setText("Rotation");

        lieDownRotationXLabel.setText("X:");

        lieDownRotationZTF.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                lieDownRotationZTFStateChanged(evt);
            }
        });

        lieDownMetersXLabel.setText("meters");

        lieDownTranslationXTF.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                lieDownTranslationXTFStateChanged(evt);
            }
        });

        lieDownLocationXLabel.setText("X:");

        lieDownLocationYLabel.setText("Y:");

        lieDownDegreesZLabel.setText("degrees");

        lieDownTranslationYTF.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                lieDownTranslationYTFStateChanged(evt);
            }
        });

        lieDownMetersYLabel.setText("meters");

        lieDownTranslationZTF.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                lieDownTranslationZTFStateChanged(evt);
            }
        });

        lieDownRotationYTF.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                lieDownRotationYTFStateChanged(evt);
            }
        });

        lieDownLocationZLabel.setText("Z:");

        lieDownDegreesXLabel.setText("degrees");

        lieDownDegreesYLabel.setText("degrees");

        lieDownMetersZLabel.setText("meters");

        lieDownLocationLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        lieDownLocationLabel.setText("Location");

        lieDownRotationZLabel.setText("Z:");

        lieDownRotationXTF.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                lieDownRotationXTFStateChanged(evt);
            }
        });

        lieDownRotationYLabel.setText("Y:");

        reclineLabel.setText("Recline: ");

        reclineTF.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                reclineTFStateChanged(evt);
            }
        });

        reclineDegreesLabel.setText("degrees");

        buttonGroup2.add(lieDownImmediatelyCheckbox);
        lieDownImmediatelyCheckbox.setSelected(true);
        lieDownImmediatelyCheckbox.setText("Lie down immediately");
        lieDownImmediatelyCheckbox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                lieDownImmediatelyCheckboxItemStateChanged(evt);
            }
        });

        buttonGroup2.add(sitLieDownCheckbox);
        sitLieDownCheckbox.setText("Sit on first click/ Lie down on second click");
        sitLieDownCheckbox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                sitLieDownCheckboxItemStateChanged(evt);
            }
        });

        lieDownAdjusterCheckbox.setText("Show LieDown Adjuster");
        lieDownAdjusterCheckbox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                lieDownAdjusterCheckboxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout lieDownPanelLayout = new javax.swing.GroupLayout(lieDownPanel);
        lieDownPanel.setLayout(lieDownPanelLayout);
        lieDownPanelLayout.setHorizontalGroup(
            lieDownPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lieDownPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lieDownPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(lieDownPanelLayout.createSequentialGroup()
                        .addGroup(lieDownPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lieDownLocationLabel)
                            .addGroup(lieDownPanelLayout.createSequentialGroup()
                                .addGroup(lieDownPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(lieDownPanelLayout.createSequentialGroup()
                                        .addComponent(lieDownLocationZLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lieDownTranslationZTF, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(lieDownPanelLayout.createSequentialGroup()
                                        .addComponent(lieDownLocationYLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lieDownTranslationYTF, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(lieDownPanelLayout.createSequentialGroup()
                                        .addComponent(lieDownLocationXLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lieDownTranslationXTF, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(lieDownPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lieDownMetersYLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lieDownMetersXLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lieDownMetersZLabel, javax.swing.GroupLayout.Alignment.TRAILING))))
                        .addGap(20, 20, 20)
                        .addGroup(lieDownPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lieDownRotationLabel)
                            .addGroup(lieDownPanelLayout.createSequentialGroup()
                                .addGroup(lieDownPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lieDownRotationXLabel)
                                    .addComponent(lieDownRotationZLabel)
                                    .addComponent(lieDownRotationYLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(lieDownPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, lieDownPanelLayout.createSequentialGroup()
                                        .addComponent(lieDownRotationXTF, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lieDownDegreesXLabel))
                                    .addGroup(lieDownPanelLayout.createSequentialGroup()
                                        .addGroup(lieDownPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(lieDownRotationZTF, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lieDownRotationYTF, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(lieDownPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lieDownDegreesYLabel)
                                            .addComponent(lieDownDegreesZLabel)))))))
                    .addComponent(sitLieDownCheckbox)
                    .addComponent(lieDownImmediatelyCheckbox)
                    .addGroup(lieDownPanelLayout.createSequentialGroup()
                        .addComponent(reclineLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(reclineTF, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(reclineDegreesLabel))
                    .addComponent(lieDownAdjusterCheckbox))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        lieDownPanelLayout.setVerticalGroup(
            lieDownPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lieDownPanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(lieDownPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, lieDownPanelLayout.createSequentialGroup()
                        .addComponent(lieDownLocationLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(lieDownPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lieDownLocationXLabel)
                            .addComponent(lieDownTranslationXTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lieDownMetersXLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(lieDownPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lieDownTranslationYTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lieDownLocationYLabel)
                            .addComponent(lieDownMetersYLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(lieDownPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lieDownTranslationZTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lieDownLocationZLabel)
                            .addComponent(lieDownMetersZLabel)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, lieDownPanelLayout.createSequentialGroup()
                        .addComponent(lieDownRotationLabel)
                        .addGap(7, 7, 7)
                        .addGroup(lieDownPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lieDownRotationXTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lieDownRotationXLabel)
                            .addComponent(lieDownDegreesXLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(lieDownPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lieDownRotationYTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lieDownRotationYLabel)
                            .addComponent(lieDownDegreesYLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(lieDownPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lieDownRotationZTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lieDownRotationZLabel)
                            .addComponent(lieDownDegreesZLabel))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lieDownAdjusterCheckbox)
                .addGap(2, 2, 2)
                .addGroup(lieDownPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(reclineLabel)
                    .addComponent(reclineTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(reclineDegreesLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lieDownImmediatelyCheckbox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sitLieDownCheckbox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rightClickRadioButton)
                            .addComponent(leftClickRadioButton)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lieDownPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(lieDownCheckbox, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(sittingCheckbox, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(leftClickRadioButton)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rightClickRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(sittingCheckbox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lieDownCheckbox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lieDownPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void rightClickRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rightClickRadioButtonItemStateChanged
        currentLeftClick = false;
        checkAndSetDirty();
    }//GEN-LAST:event_rightClickRadioButtonItemStateChanged

    private void leftClickRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_leftClickRadioButtonItemStateChanged
        currentLeftClick = true;
        checkAndSetDirty();
    }//GEN-LAST:event_leftClickRadioButtonItemStateChanged

    private void sittingAdjusterCheckboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sittingAdjusterCheckboxItemStateChanged
        SitAdjusterEntity.getInstance(editor.getCell()).setVisible(sittingAdjusterCheckbox.isSelected());
        changePositionOfAdjuster();
    }//GEN-LAST:event_sittingAdjusterCheckboxItemStateChanged

    private void sittingTranslationYTFStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sittingTranslationYTFStateChanged
        currentSittingTranslationY = Float.parseFloat(sittingTranslationYTF.getValue().toString());
        checkAndSetDirty();
        if (sittingAdjusterCheckbox.isSelected()) {
            changePositionOfAdjuster();
        }
    }//GEN-LAST:event_sittingTranslationYTFStateChanged

    private void sittingTranslationZTFStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sittingTranslationZTFStateChanged
        currentSittingTranslationZ = Float.parseFloat(sittingTranslationZTF.getValue().toString());
        checkAndSetDirty();
        if (sittingAdjusterCheckbox.isSelected()) {
            changePositionOfAdjuster();
        }
    }//GEN-LAST:event_sittingTranslationZTFStateChanged

    private void sittingTranslationXTFStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sittingTranslationXTFStateChanged
        currentSittingTranslationX = Float.parseFloat(sittingTranslationXTF.getValue().toString());
        checkAndSetDirty();
        if (sittingAdjusterCheckbox.isSelected()) {
            changePositionOfAdjuster();
        }
    }//GEN-LAST:event_sittingTranslationXTFStateChanged

    private void sittingRotationZTFStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sittingRotationZTFStateChanged
        currentSittingRotationZ = Float.parseFloat(sittingRotationZTF.getValue().toString());
        checkAndSetDirty();
        if (sittingAdjusterCheckbox.isSelected()) {
            changePositionOfAdjuster();
        }
    }//GEN-LAST:event_sittingRotationZTFStateChanged

    private void sittingRotationYTFStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sittingRotationYTFStateChanged
        currentSittingRotationY = Float.parseFloat(sittingRotationYTF.getValue().toString());
        checkAndSetDirty();
        if (sittingAdjusterCheckbox.isSelected()) {
            changePositionOfAdjuster();
        }
    }//GEN-LAST:event_sittingRotationYTFStateChanged

    private void sittingRotationXTFStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sittingRotationXTFStateChanged
        currentSittingRotationX = Float.parseFloat(sittingRotationXTF.getValue().toString());
        checkAndSetDirty();
        if (sittingAdjusterCheckbox.isSelected()) {
            changePositionOfAdjuster();
        }
    }//GEN-LAST:event_sittingRotationXTFStateChanged

    private void lieDownRotationXTFStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_lieDownRotationXTFStateChanged
        currentLieDownRotationX = Float.parseFloat(lieDownRotationXTF.getValue().toString());
        checkAndSetDirty();
        if (lieDownAdjusterCheckbox.isSelected()) {
            changePositionOfLieDownAdjuster();
        }
    }//GEN-LAST:event_lieDownRotationXTFStateChanged

    private void lieDownRotationYTFStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_lieDownRotationYTFStateChanged
        currentLieDownRotationY = Float.parseFloat(lieDownRotationYTF.getValue().toString());
        checkAndSetDirty();
        if (lieDownAdjusterCheckbox.isSelected()) {
            changePositionOfLieDownAdjuster();
        }
    }//GEN-LAST:event_lieDownRotationYTFStateChanged

    private void lieDownRotationZTFStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_lieDownRotationZTFStateChanged
        currentLieDownRotationZ = Float.parseFloat(lieDownRotationZTF.getValue().toString());
        checkAndSetDirty();
        if (lieDownAdjusterCheckbox.isSelected()) {
            changePositionOfLieDownAdjuster();
        }
    }//GEN-LAST:event_lieDownRotationZTFStateChanged

    private void lieDownTranslationZTFStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_lieDownTranslationZTFStateChanged
        currentLieDownTranslationZ = Float.parseFloat(lieDownTranslationZTF.getValue().toString());
        checkAndSetDirty();
        if (lieDownAdjusterCheckbox.isSelected()) {
            changePositionOfLieDownAdjuster();
        }
    }//GEN-LAST:event_lieDownTranslationZTFStateChanged

    private void lieDownTranslationYTFStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_lieDownTranslationYTFStateChanged
       currentLieDownTranslationY = Float.parseFloat(lieDownTranslationYTF.getValue().toString());
        checkAndSetDirty();
       if (lieDownAdjusterCheckbox.isSelected()) {
           changePositionOfLieDownAdjuster();
       }
    }//GEN-LAST:event_lieDownTranslationYTFStateChanged

    private void lieDownTranslationXTFStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_lieDownTranslationXTFStateChanged
        currentLieDownTranslationX = Float.parseFloat(lieDownTranslationXTF.getValue().toString());
        checkAndSetDirty();
        if (lieDownAdjusterCheckbox.isSelected()) {
            changePositionOfLieDownAdjuster();
        }
    }//GEN-LAST:event_lieDownTranslationXTFStateChanged

    private void reclineTFStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_reclineTFStateChanged
//        checkAndSetDirty();
//        if (sittingAdjusterCheckbox.isSelected()) {
//            changePositionOfAdjuster();
//        }
    }//GEN-LAST:event_reclineTFStateChanged

    private void lieDownAdjusterCheckboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_lieDownAdjusterCheckboxItemStateChanged
        // TODO add your handling code here:
         SitAdjusterEntity.getInstance_lieDown(editor.getCell()).setVisible_lieDown(lieDownAdjusterCheckbox.isSelected());
        changePositionOfLieDownAdjuster();
    }//GEN-LAST:event_lieDownAdjusterCheckboxItemStateChanged

    private void lieDownCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lieDownCheckboxActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_lieDownCheckboxActionPerformed

    private void lieDownCheckboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_lieDownCheckboxItemStateChanged
        // TODO add your handling code here:
        currentLieDown = lieDownCheckbox.isSelected();
        checkAndSetDirty();
        enableLieDown(lieDownCheckbox.isSelected());
    }//GEN-LAST:event_lieDownCheckboxItemStateChanged

    private void lieDownImmediatelyCheckboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_lieDownImmediatelyCheckboxItemStateChanged
        // TODO add your handling code here:
        currentLieDownImmediately = true;
        checkAndSetDirty();
    }//GEN-LAST:event_lieDownImmediatelyCheckboxItemStateChanged

    private void sitLieDownCheckboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sitLieDownCheckboxItemStateChanged
        // TODO add your handling code here:
        currentLieDownImmediately = false;
        checkAndSetDirty();
    }//GEN-LAST:event_sitLieDownCheckboxItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton leftClickRadioButton;
    private javax.swing.JCheckBox lieDownAdjusterCheckbox;
    private javax.swing.JCheckBox lieDownCheckbox;
    private javax.swing.JLabel lieDownDegreesXLabel;
    private javax.swing.JLabel lieDownDegreesYLabel;
    private javax.swing.JLabel lieDownDegreesZLabel;
    private javax.swing.JRadioButton lieDownImmediatelyCheckbox;
    private javax.swing.JLabel lieDownLocationLabel;
    private javax.swing.JLabel lieDownLocationXLabel;
    private javax.swing.JLabel lieDownLocationYLabel;
    private javax.swing.JLabel lieDownLocationZLabel;
    private javax.swing.JLabel lieDownMetersXLabel;
    private javax.swing.JLabel lieDownMetersYLabel;
    private javax.swing.JLabel lieDownMetersZLabel;
    private javax.swing.JPanel lieDownPanel;
    private javax.swing.JLabel lieDownRotationLabel;
    private javax.swing.JLabel lieDownRotationXLabel;
    private javax.swing.JSpinner lieDownRotationXTF;
    private javax.swing.JLabel lieDownRotationYLabel;
    private javax.swing.JSpinner lieDownRotationYTF;
    private javax.swing.JLabel lieDownRotationZLabel;
    private javax.swing.JSpinner lieDownRotationZTF;
    private javax.swing.JSpinner lieDownTranslationXTF;
    private javax.swing.JSpinner lieDownTranslationYTF;
    private javax.swing.JSpinner lieDownTranslationZTF;
    private javax.swing.JLabel reclineDegreesLabel;
    private javax.swing.JLabel reclineLabel;
    private javax.swing.JSpinner reclineTF;
    private javax.swing.JRadioButton rightClickRadioButton;
    private javax.swing.JRadioButton sitLieDownCheckbox;
    private javax.swing.JCheckBox sittingAdjusterCheckbox;
    private javax.swing.JCheckBox sittingCheckbox;
    private javax.swing.JSpinner sittingRotationXTF;
    private javax.swing.JSpinner sittingRotationYTF;
    private javax.swing.JSpinner sittingRotationZTF;
    private javax.swing.JSpinner sittingTranslationXTF;
    private javax.swing.JSpinner sittingTranslationYTF;
    private javax.swing.JSpinner sittingTranslationZTF;
    // End of variables declaration//GEN-END:variables
}
