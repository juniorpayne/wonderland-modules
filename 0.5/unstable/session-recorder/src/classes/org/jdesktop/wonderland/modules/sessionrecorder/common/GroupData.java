/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
package org.jdesktop.wonderland.modules.sessionrecorder.common;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class is used to save the information about the group of vimeo
 * 
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
@XmlRootElement(name="group-data")
public class GroupData {
    private String name;
    private String link;
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
