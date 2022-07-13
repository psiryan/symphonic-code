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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author danizmax
 */
public class SettingsHandler {

    public static String CONFIG_FILE_NAME = "JSymphonic.conf";
    public static int WINDOW_STATE_MINI = 0;
    public static int WINDOW_STATE_NORMAL = 1;

    //profile settings
    private Vector profiles;
    private int defaultProfile = 0;

    //interface settings
    private String language = "English";
    private String theme = "Nimbus";
    private String logLevel = "OFF";
    private boolean logToFile = false;
    private boolean showToolbarText = true;
    private int height = 400, width = 500, windowstate = 1, deviderLocation = 20;

    //transfer settings
    private String bitrate = "64 kbps";
    private boolean alwaysTranscode = false;
    private boolean neverReadTags =  false;
    private String tagStructure = "qwert";

   // private HashMap playerGenerations= NWGeneric.getGenerationMap();

    //Log file settings
    public static String LOG_FILE_NAME= "JSymphonic.log";
    public static int byteSizeLimit = 1000000;
    public static int numOfLogFiles = 1;
    //private static boolean append = true;

    //private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

    private Properties defaultProps;
    private Properties applicationProps;

    public SettingsHandler(){
        defaultProps = new Properties();
        defaultProps.setProperty("Language", language);
        defaultProps.setProperty("Theme", theme);
        defaultProps.setProperty("LogLevel", logLevel);
        defaultProps.setProperty("Log_filename", LOG_FILE_NAME);
        defaultProps.setProperty("Log_to_file", new Boolean(logToFile).toString());
        defaultProps.setProperty("Show_Toolbar_Text", new Boolean(isShowToolbarText()).toString());
        defaultProps.setProperty("Always_transcode", Boolean.toString(alwaysTranscode));
        defaultProps.setProperty("Transcode_Bitrate", getBitrate());
        defaultProps.setProperty("Never_read_tags", Boolean.toString(neverReadTags));
        defaultProps.setProperty("Tag_structure", tagStructure);
        defaultProps.setProperty("DefaultProfile", String.valueOf(defaultProfile));
        defaultProps.setProperty("Height", Integer.toString(getHeight()));
        defaultProps.setProperty("Width", Integer.toString(getWidth()));
        defaultProps.setProperty("State", Integer.toString(getWindowState()));
        defaultProps.setProperty("DeviderLocation", Integer.toString(getDeviderLocation()));
        //Vector userHome = new Vector();
        //TODO profileItem does not add
        //userHome.add(System.getProperty("user.home"));
        //defaultProps.setProperty("ProfileItem0", new ProfileElement("qwertz", "devPath", "exPath", false, "tempPath", false, userHome, 0).toString());
        profiles = new Vector();
    }

    public void saveSettings(File inputFile){
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(inputFile);
            applicationProps = new Properties();
            applicationProps.setProperty("Language",getLanguage());
            applicationProps.setProperty("Theme",getTheme());
            applicationProps.setProperty("LogLevel",getLogLevel());
            applicationProps.setProperty("Log_filename", LOG_FILE_NAME);
            applicationProps.setProperty("Log_to_file", new Boolean(logToFile).toString());
            applicationProps.setProperty("Show_Toolbar_Text", new Boolean(isShowToolbarText()).toString());
            applicationProps.setProperty("Always_transcode", Boolean.toString(isAlwaysTranscode()));
            applicationProps.setProperty("Transcode_Bitrate", getBitrate());
            applicationProps.setProperty("Never_read_tags", Boolean.toString(isNeverReadTags()));
            applicationProps.setProperty("Tag_structure",getTagStructure());
            applicationProps.setProperty("DefaultProfile", String.valueOf(getActiveProfile()));
            applicationProps.setProperty("Height", Integer.toString(getHeight()));
            applicationProps.setProperty("Width", Integer.toString(getWidth()));
            applicationProps.setProperty("State", Integer.toString(getWindowState()));
            applicationProps.setProperty("DeviderLocation", Integer.toString(getDeviderLocation()));

            for(int i=0;i<getProfiles().size();i++){
                applicationProps.setProperty("ProfileItem" + i, ((ProfileElement)getProfiles().get(i)).toString());
            }
           
            applicationProps.store(out, "This is JSymphonic configuration file. Change at your own risk!");
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(SettingsHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(SettingsHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void loadSettings(File inputFile){
        FileInputStream in = null;
        try {
            //load default properties
            applicationProps = new Properties();
            // now load saved properties
            in = new FileInputStream(inputFile);
            applicationProps.load(in);
            getProfiles().clear();
            Set keys = applicationProps.stringPropertyNames();
            Iterator it = keys.iterator();
            while(it.hasNext()){
                String key = (String) it.next();
                if(key.equals("Language")){
                    setLanguage(applicationProps.getProperty(key).trim());
                }else if(key.equals("Theme")){
                    setTheme(applicationProps.getProperty(key).trim());
                }else if(key.equals("LogLevel")){
                    setLogLevel(applicationProps.getProperty(key).trim());
                }else if(key.equals("Log_filename")){
                    LOG_FILE_NAME = applicationProps.getProperty(key).trim();
                }else if(key.equals("Log_to_file")){
                    logToFile = new Boolean(applicationProps.getProperty(key).trim());
                }else if(key.equals("Show_Toolbar_Text")){
                    setShowToolbarText((boolean) new Boolean(applicationProps.getProperty(key).trim()));
                }else if(key.equals("Always_transcode")){
                    setAlwaysTranscode((boolean) Boolean.valueOf(applicationProps.getProperty(key).trim()));
                }else if(key.equals("Transcode_Bitrate")){
                    setBitrate((String)applicationProps.getProperty(key).trim());
                }else if(key.equals("Never_read_tags")){
                    setNeverReadTags((boolean) Boolean.valueOf(applicationProps.getProperty(key).trim()));
                }else if(key.equals("Tag_structure")){
                    setTagStructure(applicationProps.getProperty(key).trim());
                }else if(key.equals("DefaultProfile")){
                    setActiveProfile(Integer.valueOf(applicationProps.getProperty(key).trim()));
                }else if(key.contains("ProfileItem")){
                    String profileData[] = applicationProps.getProperty(key).split(",");
                    getProfiles().add(0, new ProfileElement(profileData[0].trim(), profileData[1].trim(), profileData[2].trim(), Boolean.valueOf(profileData[3].trim()), profileData[4].trim(), Boolean.valueOf(profileData[5].trim()), profileData[6].trim(), Integer.valueOf(profileData[7].trim())));
                }else if(key.equals("Height")){
                    setHeight(Integer.valueOf(applicationProps.getProperty(key).trim()));
                }else if(key.equals("Width")){
                    setWidth(Integer.valueOf(applicationProps.getProperty(key).trim()));
                }else if(key.equals("State")){
                    setWindowState(Integer.valueOf(applicationProps.getProperty(key).trim()));
                }else if(key.equals("DeviderLocation")){
                    setDeviderLocation(Integer.valueOf(applicationProps.getProperty(key).trim()));
                }
            }
            in.close();
            
        } catch (IOException ex) {
           Logger.getLogger("No configuration file found!");
           // saveSettings(inputFile);
        } finally {
            try {
                if(in != null){
                    in.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(SettingsHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


    }

    /**
     * @return the profiles
     */
    public Vector getProfiles() {
        return profiles;
    }

    /**
     * @param profiles the profiles to set
     */
    public void setProfiles(Vector profiles) {
        this.profiles = profiles;
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return the theme
     */
    public String getTheme() {
        return theme;
    }

    /**
     * @param theme the theme to set
     */
    public void setTheme(String theme) {
        this.theme = theme;
    }

    /**
     * @return the logLevel
     */
    public String getLogLevel() {
        return logLevel;
    }

    /**
     * @param logLevel the logLevel to set
     */
    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    /**
     * @return the logToFile
     */
    public boolean isLogToFile() {
        return logToFile;
    }

    /**
     * @param logToFile the logToFile to set
     */
    public void setLogToFile(boolean logToFile) {
        this.logToFile = logToFile;
    }

    /**
     * @return the bitrate
     */
    public String getBitrate() {
        return bitrate;
    }

    /**
     * @param bitrate the bitrate to set
     */
    public void setBitrate(String bitrate) {
        this.bitrate = bitrate;
    }

    /**
     * @return the alwaysTranscode
     */
    public boolean isAlwaysTranscode() {
        return alwaysTranscode;
    }

    /**
     * @param alwaysTranscode the alwaysTranscode to set
     */
    public void setAlwaysTranscode(boolean alwaysTranscode) {
        this.alwaysTranscode = alwaysTranscode;
    }

    /**
     * @return the neverReadTags
     */
    public boolean isNeverReadTags() {
        return neverReadTags;
    }

    /**
     * @param neverReadTags the neverReadTags to set
     */
    public void setNeverReadTags(boolean neverReadTags) {
        this.neverReadTags = neverReadTags;
    }

    /**
     * @return the tagStructure
     */
    public String getTagStructure() {
        return tagStructure;
    }

    /**
     * @param tagStructure the tagStructure to set
     */
    public void setTagStructure(String tagStructure) {
        this.tagStructure = tagStructure;
    }

    /**
     * @return the defaultProfile
     */
    public int getActiveProfile() {
        return defaultProfile;
    }

    /**
     * @param defaultProfile the defaultProfile to set
     */
    public void setActiveProfile(int activeProfile) {
        this.defaultProfile = activeProfile;
    }

    /*public void setActiveProfile(String activeProfilename) {
        Iterator it = profiles.iterator();
        int i =0;
        while(it.hasNext()){
            if(((ProfileElement)it.next()).getName().equals(activeProfilename)){
                this.defaultProfile = i;
                break;
            }
            i++;
        }
    }*/

    /**
     * @return the showToolbarText
     */
    public boolean isShowToolbarText() {
        return showToolbarText;
    }

    /**
     * @param showToolbarText the showToolbarText to set
     */
    public void setShowToolbarText(boolean showToolbarText) {
        this.showToolbarText = showToolbarText;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the state
     */
    public int getWindowState() {
        return windowstate;
    }

    /**
     * @param state the state to set
     */
    public void setWindowState(int state) {
        this.windowstate = state;
    }

    /**
     * @return the deviderLocation
     */
    public int getDeviderLocation() {
        return deviderLocation;
    }

    /**
     * @param deviderLocation the deviderLocation to set
     */
    public void setDeviderLocation(int deviderLocation) {
        this.deviderLocation = deviderLocation;
    }
}
