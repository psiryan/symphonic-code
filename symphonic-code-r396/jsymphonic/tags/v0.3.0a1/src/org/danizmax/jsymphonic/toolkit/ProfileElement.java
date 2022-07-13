/*
 *  Copyright (C) 2008 Daniel Žalar (danizmax@gmail.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */

package org.danizmax.jsymphonic.toolkit;

/**
 * This class is a profile element
 * @author danizmax - Daniel Žalar (danizmax@gmail.com)
 */
public class ProfileElement extends Object{

    private String devicePath = "";
    private String localPath = "";
    private String exportPath = "";
    private String transcodeTempPath = "";
    private boolean tempPathSameAsDevicePath = true;
    private int deviceGeneration = 0;
    private String profileName = "NoName";

    
    public ProfileElement(String profileName, int deviceGeneration, String localPath, String exportPath, String devicePath, String transcodeTempPath, boolean tempPathSameAsDevicePath){
        this.profileName = profileName;
        this.deviceGeneration = deviceGeneration;
        this.localPath = localPath;
        this.exportPath = exportPath;
        this.devicePath = devicePath;
        this.transcodeTempPath = transcodeTempPath;
        this.tempPathSameAsDevicePath = tempPathSameAsDevicePath;
    }

    public String getDevicePath() {
        return devicePath;
    }

    public void setDevicePath(String devicePath) {
        this.devicePath = devicePath;
    }
    
    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public int getDeviceGeneration() {
        return deviceGeneration;
    }

    public void setDeviceGeneration(int deviceGeneration) {
        this.deviceGeneration = deviceGeneration;
    }

    public String getTranscodeTempPath() {
        if(tempPathSameAsDevicePath){
            return devicePath;
        }
        else {
            return transcodeTempPath;
        }
    }

    public void setTranscodeTempPath(String transcodeTempPath) {
        this.transcodeTempPath = transcodeTempPath;
    }

    public boolean isTempPathSameAsDevicePath() {
        return tempPathSameAsDevicePath;
    }

    public void setTempPathSameAsDevicePath(boolean tempPathSameAsDevicePath) {
        this.tempPathSameAsDevicePath = tempPathSameAsDevicePath;
    }

    public String getExportPath() {
        return exportPath;
    }

    public void setExportPath(String exportPath) {
        this.exportPath = exportPath;
    }
}
