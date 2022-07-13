/*
/*
 * Copyright (C) 2007, 2008, 2009 Daniel Žalar (danizmax@gmail.com)
 *
 * This file is part of JSymphonic program.
 *
 * JSymphonic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JSymphonic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSymphonic. If not, see <http://www.gnu.org/licenses/>.
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danizmax.jsymphonic.gui.settings;

import java.util.Iterator;
import java.util.Vector;

/**
 *
 * @author danizmax
 */
public class ProfileElement {

    private String name;
    private String devicePath;
    private String exportPath;
    private boolean exportSameAsLocalPath;
    private String tempPath;
    private boolean tempSameAsDevicePath;
    private Vector localPaths;
    private int deviceGeneration;

    public ProfileElement(String name, String devicePath, String exportPath, boolean exportSameAsLocalPath, String tempPath, boolean tempSameAsDevicePath, Vector localPaths, int deviceGeneration){
        setName(name);
        setDevicePath(devicePath);
        setExportPath(exportPath);
        setExportSameAsLocalPath(exportSameAsLocalPath);
        setTempPath(tempPath);
        setTempSameAsDevicePath(tempSameAsDevicePath);
        if(localPaths != null){
            setLocalPaths(localPaths);
        }else{
            setLocalPaths(new Vector());
        }
        setDeviceGeneration(deviceGeneration);
    }

     public ProfileElement(String name, String devicePath, String exportPath, boolean exportSameAsLocalPath, String tempPath, boolean tempSameAsDevicePath, String localPaths, int deviceGeneration){
        setName(name);
        setDevicePath(devicePath);
        setExportPath(exportPath);
        setExportSameAsLocalPath(exportSameAsLocalPath);
        setTempPath(tempPath);
        setTempSameAsDevicePath(tempSameAsDevicePath);
        if(localPaths != null){
            setLocalPaths(localPaths);
        }else{
            setLocalPaths(new Vector());
        }
        setDeviceGeneration(deviceGeneration);
    }

    public String toString(){
        String locPaths = "";
        if(localPaths.size()>0 ){
            locPaths = (String) localPaths.get(0);
            for(int i=1;i<localPaths.size();i++){
                locPaths += ":"  + (String) localPaths.get(i);
            }
        }
        return getName() + ", " +getDevicePath() + ", " +getExportPath() + ", " +Boolean.toString(isExportSameAsLocalPath()) + ", " + getTempPath() + ", " + Boolean.toString(isTempSameAsDevicePath()) + ", {" + locPaths + "}, " + Integer.toString(getDeviceGeneration());
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the devicePath
     */
    public String getDevicePath() {
        return devicePath;
    }

    /**
     * @param devicePath the devicePath to set
     */
    public void setDevicePath(String devicePath) {
        this.devicePath = devicePath;
    }

    /**
     * @return the exportPath
     */
    public String getExportPath() {
        if(exportSameAsLocalPath){
            return (String) localPaths.get(0);
        }else{
            return exportPath;
        }
    }

    /**
     * @param exportPath the exportPath to set
     */
    public void setExportPath(String exportPath) {
        this.exportPath = exportPath;
    }

    /**
     * @return the exportSameAsLocalPath
     */
    public boolean isExportSameAsLocalPath() {
        return exportSameAsLocalPath;
    }

    /**
     * @param exportSameAsLocalPath the exportSameAsLocalPath to set
     */
    public void setExportSameAsLocalPath(boolean exportSameAsLocalPath) {
        this.exportSameAsLocalPath = exportSameAsLocalPath;
    }

    /**
     * @return the tempPath
     */
    public String getTempPath() {
        return tempPath;
    }

    /**
     * @param tempPath the tempPath to set
     */
    public void setTempPath(String tempPath) {
        this.tempPath = tempPath;
    }

    /**
     * @return the tempSameAsDevicePath
     */
    public boolean isTempSameAsDevicePath() {
        return tempSameAsDevicePath;
    }

    /**
     * @param tempSameAsDevicePath the tempSameAsDevicePath to set
     */
    public void setTempSameAsDevicePath(boolean tempSameAsDevicePath) {
        this.tempSameAsDevicePath = tempSameAsDevicePath;
    }

    /**
     * @return the deviceGeneration
     */
    public int getDeviceGeneration() {
        return deviceGeneration;
    }

    /**
     * @param deviceGeneration the deviceGeneration to set
     */
    public void setDeviceGeneration(int deviceGeneration) {
        this.deviceGeneration = deviceGeneration;
    }

    /**
     * @return the localPaths
     */
    public Vector getLocalPaths() {
        return localPaths;
    }

    /**
     * @param localPaths the localPaths to set
     */
    public void setLocalPaths(Vector localPaths) {
        if(localPaths != null){
            this.localPaths = localPaths;
        }else{
            this.localPaths = new Vector();
        }
    }

     /**
     * @param localPaths the localPaths to set
     */
    public void setLocalPaths(String localPaths) {
        if(localPaths.startsWith("{") && localPaths.endsWith("}")){
            localPaths = localPaths.substring(1, localPaths.length()-1);
            String locPaths[] = localPaths.split(":");
            this.localPaths = new Vector();
            for(int i=0; i<locPaths.length; i++){
                this.localPaths.add(locPaths[i]);
            }
        }else{
            System.out.println("Local path configuration string must start with \"{\" and end with \"}\"! Ignoring...");
        }
    }

}
