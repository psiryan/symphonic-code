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
 * Created on June 7, 2008, 9:57 PM
 */

package org.danizmax.jsymphonic.gui.local;

import java.io.IOException;
import java.util.logging.Level;
import org.danizmax.jsymphonic.toolkit.*;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * This class implements functions that are used on a local filesystem
 * @author danizmax - Daniel Žalar (danizmax@gmail.com)
 */
public class localManager {

    private static Logger logger = Logger.getLogger("org.danizmax.jsymphonic.toolkit.localManager");    
    private String localPath;
    private JTree localTree;
    private String filterString = "";
    private String fileExtention = "";
    
    /***
     * Constroct a manager for local filesystem
     * @param localPath path to filesystem location
     * @param localTree the JTree in wich to show data
     */
    public localManager(String localPath, JTree localTree){
        this.localPath = localPath;
        this.localTree = localTree;
        logger.setUseParentHandlers(true);
    }
    
    public localManager(){
        logger.setUseParentHandlers(true);
    }
    
    /**
     * Adds children to the parent node
     * @param currPath path of the current node
     * @param parentNode the parent node to wich attach children
     * @return returns the parent node with children
     */
    private JSymphonicMutableTreeNode scanFilesystem(File currPath, String filter, String fileExtention, JSymphonicMutableTreeNode parentNode){
        
        File[] childs = new File(currPath.getAbsolutePath()).listFiles();
        if (childs == null) {
            // by herchu: this can happen if there is a permission problem on dir currPath (e.g. /mp3/lost+found
            return parentNode;
        }
        for(int i=0; i<childs.length;i++){ //iterate through childs
            if(childs[i].isDirectory() ){
                       JSymphonicMutableTreeNode child = new JSymphonicMutableTreeNode(childs[i], JSymphonicMutableTreeNode.TYPE_FOLDER);
                       JSymphonicMutableTreeNode subDir = scanFilesystem(childs[i], filter, fileExtention, child);
                       if(subDir.getDepth() != 0) //only add folders that are not empty
                           parentNode.add(subDir);
            }else{ //add a leaf element
                // If several extension are embedded in the "fileExtention" variable, split them into a tab
                String[] fileExtensions = fileExtention.toLowerCase().split(",");

                // For each extension, add a leaf if necessary
                for(int j=0; j<fileExtensions.length; j++){
                    // Read current file extension
                    String currentFileExtention = fileExtensions[j];
                    if(childs[i].getName().toLowerCase().contains(filter.toLowerCase()) && childs[i].getName().toLowerCase().endsWith(currentFileExtention)){
                        parentNode.add(new JSymphonicMutableTreeNode(childs[i], JSymphonicMutableTreeNode.TYPE_AUDIOFILE));
                    }
                }
            }    
        }
        return parentNode;
    }
    
    /**
     * This method loads all files and folders into the HashMap
     * @param currPath path to be scanned
     * @param filter filter string to match file names
     * @param selectedFileList root of currently scanned path
     * @return all files and folders in the HashMap
     */
    private HashMap getFilesFromSelection(File currPath, String filter, HashMap selectedFileList){
        File[] files = new File(currPath.getAbsolutePath()).listFiles();
        for(int i=0; i<files.length;i++){ //iterate through childs
            if(files[i].isDirectory() ){
                 selectedFileList.putAll(getFilesFromSelection(files[i], filter, selectedFileList));
            }else{ //add a leaf element

                // If several extension are embedded in the "fileExtention" variable, split them into a tab
                String[] fileExtensions = fileExtention.toLowerCase().split(",");

                // For each extension, add a file if it's correct
                for(int j=0; j<fileExtensions.length; j++){
                    // Read current file extension
                    String currentFileExtention = fileExtensions[j];
                    if(files[i].getName().toLowerCase().contains(filter.toLowerCase()) && files[i].getName().toLowerCase().endsWith(currentFileExtention)){
                        selectedFileList.put(files[i], null);
                    }
                }
            }    
        }
        return selectedFileList;
    }
    
    /**
     * Refresh the content of the tree
     */
    public void refreshTree(){
        File local = new File(localPath);
        if(local.exists() ){
          if(local.isDirectory() ){
            DefaultTreeModel model = (DefaultTreeModel) localTree.getModel() ;
            JSymphonicMutableTreeNode root = new JSymphonicMutableTreeNode(new File(localPath), JSymphonicMutableTreeNode.TYPE_USB);
            root = scanFilesystem(local, getFilterString(), getFileExtention(), root);
            model.setRoot(root);
            model.reload(root);
          }else{
              logger.warning(localPath + " " + "is not a directory!");
          }
        }else{
            logger.warning("The directory" + " " + localPath + " " + "does not exist!");
        }
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void setLogger(Logger aLogger) {
        logger = aLogger;
    }
    
    public static void setParentLogger(Logger aLogger) {
        logger.setParent(aLogger);
    }
    
    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public JTree getLocalTree() {
        return localTree;
    }

    public void setLocalTree(JTree localTree) {
        this.localTree = localTree;
    }
    
    public TreePath[] getSelectedTracks() {
        return localTree.getSelectionPaths();
    }
    
    public File[] getSelectedFiles() {
        HashMap fileList = new HashMap();
        TreePath[] tp = getSelectedTracks();
        
         for(int i=0; i<tp.length;i++){
            File currFile = ((JSymphonicMutableTreeNode)tp[i].getLastPathComponent()).getAsFile();
            if( currFile.isDirectory()){
                fileList.putAll(getFilesFromSelection(currFile, "", fileList));
            }else{
                fileList.put(currFile, null);
            }
            
         }
        
        File [] selectedFiles = new File[fileList.size()];
        
        Set s = fileList.keySet();
        Iterator it = s.iterator();
        int i = 0;
        while(it.hasNext()){
            selectedFiles[i] = (File) it.next();
            try {
                logger.fine(selectedFiles[i].getCanonicalPath());
            } catch (IOException ex) {
                Logger.getLogger(localManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            i++;
        }
        
        return selectedFiles;
    }

    public String getFilterString() {
        return filterString;
    }

    public void setFilterString(String filterString) {
        this.filterString = filterString;
    }

    public String getFileExtention() {
        return fileExtention;
    }

    public void setFileExtention(String fileExtention) {
        this.fileExtention = fileExtention;
    }
}
