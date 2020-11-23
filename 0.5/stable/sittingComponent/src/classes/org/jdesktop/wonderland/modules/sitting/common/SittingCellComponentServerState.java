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
package org.jdesktop.wonderland.modules.sitting.common;

import javax.xml.bind.annotation.XmlRootElement;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.common.cell.state.annotation.ServerState;

/**
 * Server state for sitting cell component
 *
 * @author Morris Ford
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
@ServerState
@XmlRootElement(name = "sitting-cell-component")
public class SittingCellComponentServerState extends CellComponentServerState {

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

    /**
     * Default constructor
     */
    public SittingCellComponentServerState() {
    }

    @Override
    public String getServerComponentClassName() {
        return "org.jdesktop.wonderland.modules.sitting.server.SittingCellComponentMO";
    }

    public float getSittingTranslationX() {
        return sittingTranslationX;
    }

    public void setSittingTranslationX(float sittingTranslationX) {
        this.sittingTranslationX = sittingTranslationX;
    }

    public float getSittingTranslationY() {
        return sittingTranslationY;
    }

    public void setSittingTranslationY(float sittingTranslationY) {
        this.sittingTranslationY = sittingTranslationY;
    }

    public float getSittingTranslationZ() {
        return sittingTranslationZ;
    }

    public void setSittingTranslationZ(float sittingTranslationZ) {
        this.sittingTranslationZ = sittingTranslationZ;
    }

    public float getSittingRotationX() {
        return sittingRotationX;
    }

    public void setSittingRotationX(float sittingRotationX) {
        this.sittingRotationX = sittingRotationX;
    }

    public float getSittingRotationY() {
        return sittingRotationY;
    }

    public void setSittingRotationY(float sittingRotationY) {
        this.sittingRotationY = sittingRotationY;
    }

    public float getSittingRotationZ() {
        return sittingRotationZ;
    }

    public void setSittingRotationZ(float sittingRotationZ) {
        this.sittingRotationZ = sittingRotationZ;
    }

    public float getLieDownTranslationX() {
        return lieDownTranslationX;
    }

    public void setLieDownTranslationX(float lieDownTranslationX) {
        this.lieDownTranslationX = lieDownTranslationX;
    }

    public float getLieDownTranslationY() {
        return lieDownTranslationY;
    }

    public void setLieDownTranslationY(float lieDownTranslationY) {
        this.lieDownTranslationY = lieDownTranslationY;
    }

    public float getLieDownTranslationZ() {
        return lieDownTranslationZ;
    }

    public void setLieDownTranslationZ(float lieDownTranslationZ) {
        this.lieDownTranslationZ = lieDownTranslationZ;
    }

    public float getLieDownRotationX() {
        return lieDownRotationX;
    }

    public void setLieDownRotationX(float lieDownRotationX) {
        this.lieDownRotationX = lieDownRotationX;
    }

    public float getLieDownRotationY() {
        return lieDownRotationY;
    }

    public void setLieDownRotationY(float lieDownRotationY) {
        this.lieDownRotationY = lieDownRotationY;
    }

    public float getLieDownRotationZ() {
        return lieDownRotationZ;
    }

    public void setLieDownRotationZ(float lieDownRotationZ) {
        this.lieDownRotationZ = lieDownRotationZ;
    }

    public boolean isLeftClick() {
        return leftClick;
    }

    public void setLeftClick(boolean leftClick) {
        this.leftClick = leftClick;
    }

    public boolean isLieDown() {
        return lieDown;
    }

    public void setLieDown(boolean lieDown) {
        this.lieDown = lieDown;
    }

    public boolean isLieDownImmediately() {
        return lieDownImmediately;
    }

    public void setLieDownImmediately(boolean lieDownImmediately) {
        this.lieDownImmediately = lieDownImmediately;
    }

    public String getOwnerCellId() {
        return ownerCellId;
    }

    public void setOwnerCellId(String ownerCellId) {
        this.ownerCellId = ownerCellId;
    }

    public float getInitPosX() {
        return initPosX;
    }

    public void setInitPosX(float initPosX) {
        this.initPosX = initPosX;
    }

    public float getInitPosY() {
        return initPosY;
    }

    public void setInitPosY(float initPosY) {
        this.initPosY = initPosY;
    }

    public float getInitPosZ() {
        return initPosZ;
    }

    public void setInitPosZ(float initPosZ) {
        this.initPosZ = initPosZ;
    }

}
