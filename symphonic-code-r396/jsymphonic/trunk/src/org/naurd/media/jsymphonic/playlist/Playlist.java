/*
 * Copyright (C) 2007, 2008, 2009 Patrick Balleux, Nicolas Cardoso De Castro
 * (nicolas_cardoso@users.sourceforge.net), Daniel Žalar (danizmax@gmail.com)
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
 *
 *****
 *
 * Playlist.java
 *
 * Created on 9 juin 2009, 14:30:40
 *
 */

package org.naurd.media.jsymphonic.playlist;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;
import org.naurd.media.jsymphonic.title.Title;

/**
 * This class describes a playlist in a device. It has a name and a list of titles.
 *
 * @author skiron
 */
public class Playlist {
/* FIELDS */
    private String name; // The name of the playlist
    private List<Title> titleList; // The list of titles in the playlist
    private int status = Title.ON_DEVICE; // The status of the playlist, as titles: ON DEVICE, TO EXPORT, TO IMPORT,...
    private File sourceFile;
    protected static Logger logger = Logger.getLogger("org.naurd.media.jsymphonic.title.Title");

/* CONSTRUCTOR */
    /**
     * Create a new playlist from its name. The list of title is then empty.
     *
     * @param playlistName The name of the playlist.
     */
    public Playlist(String playlistName){
        if(playlistName.length() > 60) {playlistName = (String)playlistName.subSequence(0,59);} // Names are limited to 60 chars
        name = playlistName;
        titleList = new ArrayList();
    }

    /**
     * Create a new playlist from its name and a list of titles.
     *
     * @param playlistName The name of the playlist.
     * @param titles The list of titles which should be used.
     */
    public Playlist(String playlistName, List titles){
        if(playlistName.length() > 60) {playlistName = (String)playlistName.subSequence(0,59);} // Names are limited to 60 chars
        name = playlistName;
        titleList = new ArrayList(titles);
    }

/* GET METHODS */
    /**
     * Obtain the name of the list
     *
     * @return The name of the list.
     */
    public String getName(){
        return name;
    }

    /**
     * Obtain the list of titles in the list
     *
     * @return The list of titles in the list.
     */
    public List getTitles(){
        return titleList;
    }

    /**
     * Obtain the status of the playlist.
     *
     * @return The status of the playlist as it is defined for the title, by the constant from the Title class "Title.ON_DEVICE" (ON DEVICE, TO EXPORT, TO IMPORT).
     */
    public int getStatus(){
        return status;
    }


/* SET METHODS */
    /**
     * Define the source file, the playlist file, which contains info about the playlist (the list of titles).
     *
     * @param newSourceFile the playlist file.
     */
    public void setSourceFile(File newSourceFile){
        sourceFile = newSourceFile;
    }

    /**
     * Define the name of the playlist.
     *
     * @param newName the new name to use as the playlist's name.
     */
    public void setName(String newName){
        name = newName;
    }

    /**
     * Define the status of the playlist.
     *
     * @param newStatus the new status of the playlist. Should be the same as the title's status: Title.ON_DEVICE, Title.TO_IMPORT, Title.TO_DELETE, Title.TO_EXPORT.
     */
    public void setStatus(int newStatus){
        status = newStatus;
    }


/* METHODS */
    @Override
    public String toString(){
        return getName();
    }

    /**
     * Obtain a map with the list of the possible extensions for playlist files.
     *
     * @param deviceGeneration The generation of the device.
     * @return The list of possible extensions.
     */
    public static Map GetSupportedPlaylistFormats(int deviceGeneration){
        // Store the file formats supported in an associative Map (with their names in the correct language and their extentions)
        Map playlistExtentionMap = new TreeMap();
        // Files always supported, whatever the generation or FFMPEG presence:

        // Playlists are only available for modeles from generations greater than 2
        if(deviceGeneration > 2) {
            playlistExtentionMap.put(java.util.ResourceBundle.getBundle("localization/localpanel").getString("LocalPanel.M3u_Playlists"), M3uReader.getFileExtentionsList());
            playlistExtentionMap.put(java.util.ResourceBundle.getBundle("localization/localpanel").getString("LocalPanel.Pls_Playlists"), PlsReader.getFileExtentionsList());
            playlistExtentionMap.put(java.util.ResourceBundle.getBundle("localization/localpanel").getString("LocalPanel.Xspf_Playlists"), XspfReader.getFileExtentionsList());

        }

        // Return the map
        return playlistExtentionMap;
    }

    /**
     * Return the list of extentions (separated by comas) of playlist file supported.
     *
     * @param deviceGeneration The generation of the device.
     * @return A list of extentions separated by comas of playlist file supported.
     */
    public static String GetSupportedPlaylistFormatsAsList(int deviceGeneration){
        String list = "";

        // Playlists are only available for modeles from generations greater than 2
        if(deviceGeneration > 2) {
            list += M3uReader.getFileExtentionsList();
            list += ",";
            list += PlsReader.getFileExtentionsList();
            list += ",";
            list += XspfReader.getFileExtentionsList();
        }

        return list;
    }
}
