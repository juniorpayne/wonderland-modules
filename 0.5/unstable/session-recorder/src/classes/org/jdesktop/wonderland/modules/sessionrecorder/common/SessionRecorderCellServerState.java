/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
package org.jdesktop.wonderland.modules.sessionrecorder.common;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.cell.state.annotation.ServerState;

/**
 *
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
@XmlRootElement(name="sessionrecorder-cell")
@XmlAccessorType(XmlAccessType.FIELD)
@ServerState
public class SessionRecorderCellServerState extends CellServerState implements Serializable {

    @Override
    public String getServerClassName() {
        return "org.jdesktop.wonderland.modules.sessionrecorder.server.SessionRecorderCellMO";
    }
    
}
