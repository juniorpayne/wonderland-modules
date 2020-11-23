/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
package org.jdesktop.wonderland.modules.sitting.common;

import org.jdesktop.wonderland.common.cell.messages.CellMessage;

/**
 * To pass info about this vehicle
 * 
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
public class VehicleThreadMessage extends CellMessage {

    private String avatarCellId = "";
    private float initPosX;
    private float initPosY;
    private float initPosZ;
    
    public VehicleThreadMessage(String avatarCellId) {
        this.avatarCellId = avatarCellId;
    }

    public String getAvatarCellId() {
        return avatarCellId;
    }

    public void setAvatarCellId(String avatarCellId) {
        this.avatarCellId = avatarCellId;
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

