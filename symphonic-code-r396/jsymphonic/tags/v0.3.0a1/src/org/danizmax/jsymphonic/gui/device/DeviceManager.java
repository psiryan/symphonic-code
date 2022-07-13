/*
 *  Copyright (C) 2008 Daniel Žalar đ
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

package org.danizmax.jsymphonic.gui.device;

import org.danizmax.jsymphonic.toolkit.*;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.naurd.media.jsymphonic.system.sony.nw.NWGen3;
import org.naurd.media.jsymphonic.system.sony.nw.NWGen5;
import org.naurd.media.jsymphonic.system.sony.nw.NWGeneric;
import org.naurd.media.jsymphonic.system.sony.nw.NWGenericListener;
import org.naurd.media.jsymphonic.title.Title;
import org.naurd.media.jsymphonic.toolBox.JSymphonicIoFileFilter;
import org.naurd.media.jsymphonic.toolBox.JSymphonicMap;
import org.naurd.media.jsymphonic.toolBox.JSymphonicTreeCellRenderer;
import org.naurd.media.jsymphonic.toolBox.OmaDataBaseToolBox;

/**
 * It implements the use of  NWGeneric class. It's main idea is to ease use of the JSymphonic API that provides managing the contents of a Sony (c) Walkman (c) device, in a GUI.
 * @author danizmax - Daniel Žalar (danizmax@gmail.com)
 * @author Nicolas Cardoso - refreshTreeDevice, exportSelectedTracks
 */
public class DeviceManager{

    public static final int ARTISTALBUMMODE = 0;
    public static final int ARTISTMODE = 1;
    public static final int ALBUMMODE = 2;
    public static final int GENREMODE = 3;
    
    private NWGeneric genericDevice;
    private int deviceGeneration = 0;
    private String devicePath = null;
    private String tempPath = null;
    private boolean mounted = false;
    private String exportPath = null;
    private String deviceName = "Walkman";
    private File omgaudioDir =null;
  //  private Map titlesInTree;
    private JTree deviceTree;
    private int TranscodeBitrate=128;
    private boolean omgPathIntialized = false;
    private boolean AlwaysTranscode = false;
    private boolean inImportState = false;
    private NWGenericListener listener;
    private static Logger logger = Logger.getLogger("org.danizmax.gui.DevicePanel");
    
/* CONSTANT */
    // Generation
    public static final int Generation1 = 1;
    public static final int Generation2 = 2;
    public static final int Generation3 = 3;
    public static final int Generation4 = 4;
    public static final int Generation5 = 5;
    public static final int Generation6 = 6;
    public static final int Generation7 = 7;
    
    public DeviceManager(JTree deviceTree, NWGenericListener listener, String devicePath, int  deviceGeneration, String exportPath, String tempPath){
        this.exportPath = exportPath;
        this.listener = listener;
        this.devicePath = devicePath;
        this.deviceGeneration = deviceGeneration;
        this.deviceTree = deviceTree;
        this.tempPath = tempPath;
    }
    
     /**
     * Mount or unmount the device
     * @param mount if true mount the device else unmount
     */
    public void mountDevice(boolean mount) {
        mounted = false;
        if(mount){
            getLogger().info("Mounting the device" + "  "+ deviceName);
            if(omgPathIntialized){
                javax.swing.ImageIcon sourceIcon = new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/vignette.png"));
                // Create the NWGeneric object according to the generation
                switch(deviceGeneration) {
                    case Generation1: 
                        logger.severe("Generation 1 is not implemented for the moment.");
                        break;
                    case Generation2:
                        logger.severe("Generation 2 is not implemented for the moment.");
                        return;
                    case Generation3:
                        genericDevice = new NWGen3(omgaudioDir, deviceName, "", sourceIcon, getListener(),getExportPath());   
                        break;
                    case Generation4:
                        genericDevice = new NWGen5(omgaudioDir, deviceName, "", sourceIcon, getListener(),getExportPath());
                        break;
                    case Generation5:
                        genericDevice = new NWGen5(omgaudioDir, deviceName, "", sourceIcon, getListener(),getExportPath());   
                        break;
                    case Generation6:
                        genericDevice = new NWGen5(omgaudioDir, deviceName, "", sourceIcon, getListener(),getExportPath());
                        break;
                    case Generation7:
                        genericDevice = new NWGen5(omgaudioDir, deviceName, "", sourceIcon, getListener(),getExportPath());
                        break;
                    default:
                        logger.severe("Invalid generation.");
                        return;
                }
                
                //pass on the parent logger
                if(genericDevice != null)
                    NWGeneric.setParentLogger(logger);
                
                genericDevice.setAlwaysTranscode(AlwaysTranscode);
                genericDevice.setTranscodeBitrate(TranscodeBitrate);
                genericDevice.setDevicePath(devicePath);
                genericDevice.setTempPath(tempPath);
                mounted = true;
            }else{
                getLogger().warning("The OMG path is not set! Please set the device path!");
            }
        }else{
            genericDevice = null;
            getLogger().fine("Unmounting the device" + "  "+ deviceName);
        }
    }
   
    
      /**
      * Load items into the tree
      * @param treeDeviceView selected view of the tree
      */
     public void refreshDeviceTree(int treeDeviceView, String filterString) {
         if(isMounted()){
             getLogger().info("Refreshing the device tree");
            //Local variables
            String titleName, artistName, albumName, genre;
            int titleNumber, year;

            if(genericDevice == null) { //If no instance of the sony device exist, just return
                return;
            }

            JSymphonicMap titles = genericDevice.getTitlesInMap();
            HashMap artistsList = new HashMap(); //hasmap variables are used to save node while the tree is building
            HashMap albumsList = new HashMap(); 
            HashMap genresList = new HashMap(); 
            List sortedTitles;
            Title title;

            // Create root node:
            DefaultMutableTreeNode root = new JSymphonicMutableTreeNode(new File(genericDevice.getDevicePath()), JSymphonicMutableTreeNode.TYPE_USB);
            switch(treeDeviceView) { // the data displayed in the tree depend on the chosen view
                case ARTISTMODE:
                    sortedTitles = OmaDataBaseToolBox.sortByArtistTitle(titles); // Sort the title according to the selected mode

               //     titlesInTree = new HashMap(); // Create a new hashMap to store relationship between paths in JTree and the real titles

                    Iterator itD = sortedTitles.iterator();
                    while(itD.hasNext()){
                        // Get the current title
                        title = (Title)itD.next();
                        // Get its information

                        int action = JSymphonicMutableTreeNode.ACTION_LEAVE;
                        if((title.getStatus() == Title.TOIMPORT) || (title.getStatus() == Title.TODECODE) || (title.getStatus() == Title.TOENCODE) )
                            action = JSymphonicMutableTreeNode.ACTION_IMPORT;
                        else if( title.getStatus() == Title.TOEXPORT)
                            action = JSymphonicMutableTreeNode.ACTION_EXPORT;
                        else if(title.getStatus() == Title.TODELETE)
                            action = JSymphonicMutableTreeNode.ACTION_DELETE;

                        titleName = title.getTitle();
                        artistName = title.getArtist();
                        titleNumber = title.getTitleNumber();

                        // added filtering (danizmax)
                        if(titleName.toLowerCase().contains(filterString.toLowerCase()) || artistName.toLowerCase().contains(filterString.toLowerCase())){
                            if( !(artistsList.containsKey(artistName)) ){
                                // If key doesn't exist, it means that the node corresponding to this entry doesn't exist, let's create it
                                JSymphonicMutableTreeNode tempNode = new JSymphonicMutableTreeNode(title, JSymphonicMutableTreeNode.TYPE_ARTIST, action);
                                artistsList.put(artistName, tempNode);
                                root.add(tempNode);
                            }
                            //Then, add the node
                            //To do so, let's read the node corresponding to the artist in the hasmap
                            JSymphonicMutableTreeNode tempTreeNode = (JSymphonicMutableTreeNode)artistsList.get(artistName);
                            //Add a leaf
                            // commented to adapt to a new org.danizmax.jsymphonic.toolkit.JsymphonicMutableTreeNode
                           /* if( titleNumber != 0 ) {
                                if( titleNumber < 10 ) {
                                    titleName = "0" + titleNumber + " - " + titleName;
                                } else {
                                    titleName = titleNumber + " - " + titleName;
                                }
                            }*/
                            tempTreeNode.add(new JSymphonicMutableTreeNode(title, JSymphonicMutableTreeNode.TYPE_TITLE, action)); // Add the leaf
                         //   getTitlesInTree().put("[" +rootName + ", " + artistName + ", " + titleName + "]", title); // Add the correspondance in the global hashmap
                            //The node with the new leaf is put back in the hasmap
                            artistsList.put(artistName, tempTreeNode);
                        }
                    }
                    break;

                case ALBUMMODE:
                    sortedTitles = OmaDataBaseToolBox.sortByAlbumTitleNumber(titles); // Sort the title according to the selected mode

              //      titlesInTree = new HashMap(); // Create a new hashMap to store relationship between paths in JTree and the real titles

                    Iterator itAl = sortedTitles.iterator();
                    while(itAl.hasNext()){
                        // Get the current title
                        title = (Title)itAl.next();
                        // Get its information

                        int action = JSymphonicMutableTreeNode.ACTION_LEAVE;
                        if((title.getStatus() == Title.TOIMPORT) || (title.getStatus() == Title.TODECODE) || (title.getStatus() == Title.TOENCODE) )
                            action = JSymphonicMutableTreeNode.ACTION_IMPORT;
                        else if( title.getStatus() == Title.TOEXPORT)
                            action = JSymphonicMutableTreeNode.ACTION_EXPORT;
                        else if(title.getStatus() == Title.TODELETE)
                            action = JSymphonicMutableTreeNode.ACTION_DELETE;

                        titleName = title.getTitle();
                        albumName = title.getAlbum();
                        titleNumber = title.getTitleNumber();

                        // added filtering (danizmax)
                        if(titleName.toLowerCase().contains(filterString.toLowerCase()) || albumName.toLowerCase().contains(filterString.toLowerCase())){
                            if( !(albumsList.containsKey(albumName)) ){
                                // If key doesn't exist, it means that the node corresponding to this entry doesn't exist, let's create it
                                JSymphonicMutableTreeNode tempNode = new JSymphonicMutableTreeNode(title, JSymphonicMutableTreeNode.TYPE_ALBUM, action);
                                albumsList.put(albumName, tempNode);
                                root.add(tempNode);
                            }
                            //Then, add the node
                            //To do so, let's read the node corresponding to the artist in the hasmap
                            JSymphonicMutableTreeNode tempTreeNode = (JSymphonicMutableTreeNode)albumsList.get(albumName);
                            // Add the leaf
                            // commented to adapt to a new org.danizmax.jsymphonic.toolkit.JsymphonicMutableTreeNode
                            /*if( titleNumber != 0 ) {
                                if( titleNumber < 10 ) {
                                    titleName = "0" + titleNumber + " - " + titleName;
                                } else {
                                    titleName = titleNumber + " - " + titleName;
                                }
                            }*/
                            tempTreeNode.add(new JSymphonicMutableTreeNode(title, JSymphonicMutableTreeNode.TYPE_TITLE, action)); // Add the leaf
                       ///     getTitlesInTree().put("[" +rootName + ", " + albumName + ", " + titleName + "]", title); // Add the correspondance in the global hashmap
                            //The node with the new leaf is put back in the hasmap
                            albumsList.put(albumName, tempTreeNode);

                        }
                    }
                    break;

                case GENREMODE:
                    sortedTitles = OmaDataBaseToolBox.sortByGenreArtistAlbumTitle(titles);
                 //   titlesInTree = new HashMap(); // Create a new hashMap to store relationship between paths in JTree and the real titles

                    Iterator itG = sortedTitles.iterator();
                    while(itG.hasNext()){

                        // Get the current title
                        title = (Title)itG.next();
                        // Get its information

                        int action = JSymphonicMutableTreeNode.ACTION_LEAVE;
                        if((title.getStatus() == Title.TOIMPORT) || (title.getStatus() == Title.TODECODE) || (title.getStatus() == Title.TOENCODE) )
                            action = JSymphonicMutableTreeNode.ACTION_IMPORT;
                        else if( title.getStatus() == Title.TOEXPORT)
                            action = JSymphonicMutableTreeNode.ACTION_EXPORT;
                        else if(title.getStatus() == Title.TODELETE)
                            action = JSymphonicMutableTreeNode.ACTION_DELETE;

                        titleName = title.getTitle();
                        artistName = title.getArtist();
                        albumName = title.getAlbum();
                        titleNumber = title.getTitleNumber();
                        genre =  title.getGenre();


                        // added filtering (danizmax)
                        if(titleName.toLowerCase().contains(filterString.toLowerCase()) || albumName.toLowerCase().contains(filterString.toLowerCase()) || artistName.toLowerCase().contains(filterString.toLowerCase())){
                            if( !(genresList.containsKey(genre)) ){
                                // If key doesn't exist, it means that the node corresponding to this entry doesn't exist, let's create it
                                JSymphonicMutableTreeNode tempNode = new JSymphonicMutableTreeNode(title, JSymphonicMutableTreeNode.TYPE_GENRE, action);
                                genresList.put(genre, tempNode);
                                root.add(tempNode);
                            }

                            //Then, add the node artist if it doesn't exist
                            if( !(artistsList.containsKey(artistName)) ){
                                // If key doesn't exist, it means that the node corresponding to this entry doesn't exist, let's create it
                                //To do so, let's read the node corresponding to the genre in the hasmap
                                JSymphonicMutableTreeNode tempTreeNode = (JSymphonicMutableTreeNode)genresList.get(genre);

                                JSymphonicMutableTreeNode tempNode = new JSymphonicMutableTreeNode(title, JSymphonicMutableTreeNode.TYPE_ARTIST, action);
                                tempTreeNode.add(tempNode);// Add the node

                                artistsList.put(artistName, tempNode); // Update the hashmap
                            }

                            ////Then, add the node album if it doesn't exist
                            if( !(albumsList.containsKey(albumName)) ){
                                // If key doesn't exist, it means that the node corresponding to this entry doesn't exist, let's create it
                                //To do so, let's read the node corresponding to the artist in the hasmap
                                JSymphonicMutableTreeNode tempTreeNode = (JSymphonicMutableTreeNode)artistsList.get(artistName);

                                JSymphonicMutableTreeNode tempNode = new JSymphonicMutableTreeNode(title, JSymphonicMutableTreeNode.TYPE_ALBUM, action);
                                tempTreeNode.add(tempNode);// Add the node

                                albumsList.put(albumName, tempNode); // Update the hashmap
                            }

                            ////Then, add the leaf
                            //To do so, let's read the node corresponding to the album in the hasmap
                            JSymphonicMutableTreeNode tempTreeNode = (JSymphonicMutableTreeNode)albumsList.get(albumName);
                            // commented to adapt to a new org.danizmax.jsymphonic.toolkit.JsymphonicMutableTreeNode
                            /*if( titleNumber != 0 ) {
                                if( titleNumber < 10 ) {
                                    titleName = "0" + titleNumber + " - " + titleName;
                                } else {
                                    titleName = titleNumber + " - " + titleName;
                                }
                            }*/
                            tempTreeNode.add(new JSymphonicMutableTreeNode(title, JSymphonicMutableTreeNode.TYPE_TITLE, action)); // Add the node
                            //The node with the new leaf is put back in the hasmap
                            albumsList.put(albumName, tempTreeNode);

                            // Add the correspondance in the global hashmap
                       ///     getTitlesInTree().put("[" +rootName + ", " + genre + ", " + artistName + ", " + albumName + ", " + titleName + "]", title); 
                        }
                    }
                    break;
                default:
                    sortedTitles = OmaDataBaseToolBox.sortByArtistAlbumTitleNumber(titles);
               //     titlesInTree = new HashMap(); // Create a new hashMap to store relationship between paths in JTree and the real titles

                    Iterator itAA = sortedTitles.iterator();
                    while(itAA.hasNext()){

                        // Get the current title
                        title = (Title)itAA.next();
                        // Get its information

                        int action = JSymphonicMutableTreeNode.ACTION_LEAVE;
                        if((title.getStatus() == Title.TOIMPORT) || (title.getStatus() == Title.TODECODE) || (title.getStatus() == Title.TOENCODE) )
                            action = JSymphonicMutableTreeNode.ACTION_IMPORT;
                        else if( title.getStatus() == Title.TOEXPORT)
                            action = JSymphonicMutableTreeNode.ACTION_EXPORT;
                        else if(title.getStatus() == Title.TODELETE)
                            action = JSymphonicMutableTreeNode.ACTION_DELETE;

                        titleName = title.getTitle();
                        artistName = title.getArtist();
                        albumName = title.getAlbum();
                        titleNumber = title.getTitleNumber();


                         // added filtering (danizmax)
                        if(titleName.toLowerCase().contains(filterString.toLowerCase()) || albumName.toLowerCase().contains(filterString.toLowerCase()) || artistName.toLowerCase().contains(filterString.toLowerCase())){
                            if( !(artistsList.containsKey(artistName)) ){
                                // If key doesn't exist, it means that the node corresponding to this entry doesn't exist, let's create it
                                JSymphonicMutableTreeNode tempNode = new JSymphonicMutableTreeNode(title, JSymphonicMutableTreeNode.TYPE_ARTIST,action);
                                artistsList.put(artistName, tempNode);
                                root.add(tempNode);
                            }

                            ////Then, add the node album if it doesn't exist
                            if( !(albumsList.containsKey(albumName)) ){
                                // If key doesn't exist, it means that the node corresponding to this entry doesn't exist, let's create it
                                //To do so, let's read the node corresponding to the artist in the hasmap
                                JSymphonicMutableTreeNode tempTreeNode = (JSymphonicMutableTreeNode)artistsList.get(artistName);

                                JSymphonicMutableTreeNode tempNode = new JSymphonicMutableTreeNode(title, JSymphonicMutableTreeNode.TYPE_ALBUM, action);
                                tempTreeNode.add(tempNode);// Add the node

                                albumsList.put(albumName, tempNode); // Update the hashmap
                            }

                            ////Then, add the leaf
                            //To do so, let's read the node corresponding to the album in the hasmap
                            JSymphonicMutableTreeNode tempTreeNode = (JSymphonicMutableTreeNode)albumsList.get(albumName);
                            // commented to adapt to a new org.danizmax.jsymphonic.toolkit.JsymphonicMutableTreeNode
                           /* if( titleNumber != 0 ) {
                                if( titleNumber < 10 ) {
                                    titleName = "0" + titleNumber + " - " + titleName;
                                } else {
                                    titleName = titleNumber + " - " + titleName;
                                }
                            }*/
                            tempTreeNode.add(new JSymphonicMutableTreeNode(title, JSymphonicMutableTreeNode.TYPE_TITLE, action)); // Add the node
                            //The node with the new leaf is put back in the hasmap
                            albumsList.put(albumName, tempTreeNode);

                            // Add the correspondance in the global hashmap
                         ///   getTitlesInTree().put("[" +rootName + ", " + artistName + ", " + albumName + ", " + titleName + "]", title); 
                        }
                    }
            }
            // Associate the tree model to the model and display it
            DefaultTreeModel model = (DefaultTreeModel) deviceTree.getModel() ;
            model.setRoot(root);
            model.reload(root);

            // Put some style
            // Build default renderer
            deviceTree.setCellRenderer(new JSymphonicTreeCellRenderer());
         }else{
             getLogger().warning("The device is not mounted!");
         }
    }
      
     
     
     
     /**
      * Schedule selected tracks fot deletion.
      */
     public void scheduleTrackDeletion(){
          if(isMounted()){
                try{  
                    //TreePath currentPath;
                    // Get the paths of the title to remove
                  //  TreePath[] pathsToRemove = deviceTree.getSelectionPaths();
                    Title[] tl = getSelectedTitles();
                    // Get all the existing paths
              //      Set pathsSet = titlesInTree.keySet();

                    // For each path, remove the titles
                    for(int i = 0; i < tl.length; i++){
                        //currentPath = pathsToRemove[i];
                        //JSymphonicMutableTreeNode tn = (JSymphonicMutableTreeNode) pathsToRemove[i].getLastPathComponent();
                         //logger.fine(pathsToRemove[i].toString());
                       // tn.scheduleTrackDeletion();
                        logger.fine(tl[i].toString());
                        genericDevice.removeTitles(tl[i]); 
                        deviceTree.repaint();
                        // As the current path can design a part of a real path (i.e. it can design an entire album, and not only a title), we need to search in all the path the ones which have to be deleted
                     //   Iterator it = pathsSet.iterator();

                    /*    while(it.hasNext()) { // For each existing saved path, if the current path is include in this path, the current path represent a title to delete 
                            String pathInSavedPathsList = (String)it.next();
                            if(pathInSavedPathsList.startsWith(currentPath.toString().replace("]", ""))) { // If the current title contains in its path the current path, it sould be deleted
                                    // Get the title from the global HashMap
                                    Title titleToRemove = (Title)titlesInTree.get(pathInSavedPathsList);                    

                                    // Remove title
                                    genericDevice.removeTitles(titleToRemove);  
                            }
                        }*/
                    }

                } catch (NullPointerException ex) {
                    logger.warning("Nothing to delete");
                }
            }
     }
     
     /**
      * This method applies all changes made to the device. This includes exports, imports and deletion.
      */
     public void applyChanges(){
         if(isMounted()){
            try{ 
                genericDevice.writeTitles(); //here actually adds the tracks!
            } catch (NullPointerException ex) { 
                logger.warning("Sony device not connected!");
            }
        }
     }
    
     /**
      * Schedule selected tracks for import 
      * @param files field of selected files 
      * @return >0 some files have been scheduled, 0 if no errors occurs or ni files scheduled, -1 if a file's extension is not recognized and -2 the device is full
      */
     public int scheduleTrackImport(File files[]){
        int res = 0;
         if(isMounted()){
            for(int i = 0; i < files.length; i++) {
                int err = scanAndAddTitles(files[i]);
                if( err == 0){ //0 if no errors occurs, -1 if a file's extension is not recognized and -2 the device is full
                    res++;
                }else if(err == -1){
                    try {
                        logger.warning("For file" + " " + files[i].getCanonicalPath() + " " + "file's extension is not recognized");
                    } catch (IOException ex) {
                        Logger.getLogger(DeviceManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else if(err == -2){
                    logger.warning("The device is full");
                    return -1;
                }
            }
        }
        
        if(res > 0)
            inImportState = true;
        
        return res;
     }
     /**
      * Schedule selected tracks for export. The tracks are taken from DevicePanels JTree.
      * Tracks will be exported to the export path that can be set with method setExportPath(String exportPath).
      */
     public void scheduleTrackExport(){
        if(isMounted()){
            try{
                //TreePath currentPath;
                // Get the paths of the title to export
                //TreePath[] pathsToExport = deviceTree.getSelectionPaths();
                Title[] tl = getSelectedTitles();
                // Get all the existing paths
               /// Set pathsSet = titlesInTree.keySet();

                // For each path, export the titles
                for(int i = 0; i < tl.length; i++){
                  //  currentPath = pathsToExport[i];

                    
                     //   tn.scheduleTrackExport();
                        logger.fine(tl[i].toString());
                        genericDevice.exportTitle(tl[i]); 
                        deviceTree.repaint();
                    // As the current path can design a part of a real path (i.e. it can design an entire album, and not only a title), we need to search in all the path the ones which have to be exported
                 ///   Iterator it = pathsSet.iterator();
/*
                    while(it.hasNext()) { // For each existing saved path, if the current path is include in this path, the current path represent a title to export 
                        String pathInSavedPathsList = (String)it.next();
                        if(pathInSavedPathsList.startsWith(currentPath.toString().replace("]", ""))) { // If the current title contains in its path the current path, it sould be exported
                                // Get the title from the global HashMap
                                Title titleToExport = (Title)titlesInTree.get(pathInSavedPathsList);                    

                                // Export title
                                genericDevice.exportTitle(titleToExport);  
                        }
                    }*/
                }
            } catch (NullPointerException ex) {
               logger.warning("Nothing to export");
            }
        }
     }
     
     /**
      * Cancels all changes from the device
      */
     public void cancelChanges(){
         if(isMounted()){
            // Cancel changes in the devices
            inImportState = false; 
            genericDevice.refreshTitles();
         }
     }
     
      /**
     * Define a recursive function to scan folders, this method instance a new title according to its extension (MP3, OGG,...) and check if the title should be converted.
     * @author Nicolas Cardoso 
     * @author Daniel Žalar - added logging functionality and ported to this calss
     * @param file The file to add to the list of title to import to the Sony device, or (if it's a folder) the folder to scan to look for titles to add.
     *
     * @return 0 if no errors occurs, -1 if a file's extension is not recognized and -2 the device is full
     */
    private int scanAndAddTitles(File file) {
        if(isMounted()){
            if (file.isFile()){
                // We have a file, we should instance a new Title, check if it should be transcode and add it to the list of titles to add.
                Title newTitle; // the title currently scanned
                newTitle = Title.getTitleFromFile(file);
                
                // Change the status of the file
                newTitle.setStatus(Title.TOIMPORT);
                        
                if(genericDevice.addTitle(newTitle, isAlwaysTranscode()) < 0 ) {
                    JOptionPane.showInternalMessageDialog(deviceTree,"Player is full!", "Warning", JOptionPane.WARNING_MESSAGE);
                    return -2;
                }
            } 
            else {
                File[] fileList = file.listFiles(new JSymphonicIoFileFilter());
                for (int i = 0; i<fileList.length; i++){
                    if(scanAndAddTitles(fileList[i]) == -2){return -2;} // scan the folder, if player is full, stop the method.
                }
            }
        }
        return 0;
    }
      
     /**
     * This method loads all nodes into the HashMap
     * @param currPath path to be scanned
     * @param filter filter string to match file names
     * @param selectedFileList root of currently scanned path
     * @return all selected nodes in the HashMap
     */
    private HashMap getNodesFromSelection(JSymphonicMutableTreeNode currPath, String filter, HashMap selectedFileList){
        Enumeration children =  currPath.children();
        while(children.hasMoreElements()){ //iterate through childs
            JSymphonicMutableTreeNode ch = (JSymphonicMutableTreeNode) children.nextElement();
            if(ch.getAsTitle().getTitle().toLowerCase().contains(filter.toLowerCase())){
                if(ch.isLeaf()){
                    selectedFileList.put(ch, null); 
                }else{ //add a leaf element
                    selectedFileList.putAll(getNodesFromSelection(ch, filter, selectedFileList));
                }    
            }
        }
        return selectedFileList;
    }
    
      /** 
      * Initialize the OMG path   
      * @return true if omg path is valid
      */
     public boolean initOmgPath(){
         getLogger().info("Initializing OMG path");
         if(getDevicePath() != null){
                omgaudioDir = new File(devicePath + File.separatorChar + "OMGAUDIO");
                if (!omgaudioDir.exists()) {
                    setOmgaudioDir(new File(devicePath + File.separatorChar + "omgaudio"));
                    if (!omgaudioDir.exists()) {
                        logger.warning("The device path " + omgaudioDir.getAbsolutePath() + " does not exist!");
                        return false;
                    }
                }
                try {
                     getLogger().info("Selected OMG path is " + getOmgaudioDir().getCanonicalPath());
                } catch (IOException ex) {
                    Logger.getLogger(DeviceManager.class.getName()).log(Level.SEVERE, null, ex);
                }
                deviceName = devicePath; //TODO this will have to be changed in the future
                return true;
         }else{
             getLogger().warning("The device path has not been set!");
             return false;
         }
    }

    public String getDeviceName(){
        return deviceName;
    }
     
    public String getDevicePath() {
        return devicePath;
    }
     
    public void setDevicePath(String devicePath) {
        this.devicePath = devicePath;
        omgPathIntialized = initOmgPath();
    }

    public int getDeviceGeneration() {
        return deviceGeneration;
    }

    public void setDeviceGeneration(int deviceGeneration) {
        this.deviceGeneration = deviceGeneration;
    }
    
    public File getOmgaudioDir() {
        return omgaudioDir;
    }

    public void setOmgaudioDir(File omgaudioDir) {
        this.omgaudioDir = omgaudioDir;
    }

  /*  public Map getTitlesInTree() {
        return titlesInTree;
    }*/
    
    public String getSpaceLeftInText(){
        return genericDevice.getSpaceLeftInText();
    }
    
    public int getSpaceLeftInRatio(){
        return genericDevice.getSpaceLeftInRatio();
    }

    /**
    * Returns selected Title objects
    * @return selected Title objects
    */    
    public Title[] getSelectedTitles(){
        TreePath[] tp = deviceTree.getSelectionPaths();
        HashMap pathList = new HashMap();
        
         //add all nodes and subnodes (childs) to pathList hashmap
         for(int i=0; i<tp.length;i++){
             JSymphonicMutableTreeNode tn = (JSymphonicMutableTreeNode)tp[i].getLastPathComponent();
            if( tn.isLeaf() ){
                pathList.put(tn, null);
            }else{
                pathList.putAll(getNodesFromSelection(tn, "", pathList));
            }
            
         }
        
        Title [] selectedTitles = new Title[pathList.size()];
        
        Set s = pathList.keySet();
        Iterator it = s.iterator();
        int i = 0;
        while(it.hasNext()){
            selectedTitles[i] = ((JSymphonicMutableTreeNode) it.next()).getAsTitle();
            //logger.fine(selectedTitles[i].toString());
            i++;
        }
        
        return selectedTitles;
    }
    
    public JSymphonicMutableTreeNode getLastSelectedNode(){
         return (JSymphonicMutableTreeNode)deviceTree.getLastSelectedPathComponent();
    }
    
    public String getExportPath() {
        return exportPath;
    }

    public void setExportPath(String exportPath) {
        this.exportPath = exportPath;
    }

    public void setTempPath(String tempPath) {
        this.tempPath = tempPath;
    }

    public boolean isOmgPathIntialized() {
        return omgPathIntialized;
    }
    
    public static Logger getLogger() {
        return logger;
    }

    public static void setLogger(Logger aLogger) {
        logger = aLogger;
    }

    public boolean isAlwaysTranscode() {
        return AlwaysTranscode;
    }

    public void setAlwaysTranscode(boolean AlwaysTranscode) {
        this.AlwaysTranscode = AlwaysTranscode;
        if(genericDevice != null)
            genericDevice.setAlwaysTranscode(AlwaysTranscode);
    }

    public int getTranscodeBitrate() {
        return TranscodeBitrate;
    }

    public void setTranscodeBitrate(int TranscodeBitrate) {
        this.TranscodeBitrate = TranscodeBitrate;
        if(genericDevice != null)
           genericDevice.setTranscodeBitrate(TranscodeBitrate);
    }

    public boolean isMounted() {
        return mounted;
    }

    public NWGenericListener getListener() {
        return listener;
    }

    public void setListener(NWGenericListener listener) {
        this.listener = listener;
    }

    public boolean isInImportState() {
        return inImportState;
    }

}