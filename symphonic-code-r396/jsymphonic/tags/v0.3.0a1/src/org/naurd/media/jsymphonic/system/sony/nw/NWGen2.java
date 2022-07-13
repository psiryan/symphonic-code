/*
 * NWGen2.java
 *
 * Created on 25 février 2008, 21:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.system.sony.nw;

import java.io.File;
import java.util.Vector;
import java.util.logging.Logger;
import org.naurd.media.jsymphonic.system.SystemListener;
import org.naurd.media.jsymphonic.title.Title;

/**
 *
 * @author skiron
 */
public class NWGen2 { //TODO implements org.naurd.media.jsymphonic.system.SystemFile {
    private File sourceDir = null;
    private String name = "default";
    private String description = "";
    private OmaDataBase dataBase;
    private Vector<Title> titlesToAdd = new Vector<Title>();
    private Vector<Title> titlesToRemove = new Vector<Title>();
    private Vector<Title> titlesToExport = new Vector<Title>();
    private SystemListener listener = null;
    private javax.swing.ImageIcon icon;
    private long usableSpace = 0; //in octet
    private long totalSpace = 0; //in octet
    private long titleToAddSpace = 0; //in octet
    private long titleToRemoveSpace = 0; //in octet
    private NWGeneric genNw;

    //Other
    private static Logger logger = Logger.getLogger("org.naurd.media.jsymphonic.system.sony.nw.NWGen2");
    
    /**
     * Creates a new instance of NWGen2
     */
    public NWGen2(File esys, String sourceName, String sourceDesc, javax.swing.ImageIcon sourceIcon, NWGeneric genNw/*, JSymphonic jsymphonic*/){
        this.genNw = genNw;
        name = sourceName;
        description = sourceDesc;
        if( !esys.exists() ) {
            logger.severe("Invalid OMGAUDIO directory.\nExiting program.");
            System.exit(-1);
        }
        sourceDir = esys;
        this.icon = sourceIcon;
      //  this.jsymphonic = jsymphonic;
        
        // Create the database
        //TODO TO REDO ENTIRELY
        //dataBase = new OmaDataBaseGen2(esys);
        
        // Fill in the title's list
        //loadTitlesFromDevice();
        
        // Update space
        usableSpace = sourceDir.getUsableSpace();
        totalSpace = sourceDir.getTotalSpace();
    }
/*TODO to review entirely
    public Title[] getTitles() {
        //Get the titles in a JSymphonic Map
        JSymphonicMap titles = dataBase.getTitles();
        
        //Get the number of titles
        int count = titles.size();
        //Create the vector to return
        Title[] titlesToReturn = new org.naurd.media.jsymphonic.title.Title[count];
        
        // Get all the title IDs from titles (= all the titles)
        Set tmpTitles = titles.keySet();
        
        //Convert the set to an array
        Iterator it = tmpTitles.iterator();
        int i = 0;
        while( it.hasNext() ) {
            titlesToReturn[i] = (Title)it.next();
            i++;
        }

        return titlesToReturn; 
    }

    public void removeTitles(Title t) {
        if(titlesToAdd.contains(t)) {
            titlesToAdd.remove(t);
            titleToAddSpace -= t.size();
        }
        else {
            t.Status = t.Status.TOREMOVE;
            titlesToRemove.add(t);
            titleToRemoveSpace += t.size();
        }
    }

    public int addTitle(Title t) {
        long titleSpace = t.size();
        if(titleToAddSpace+titleSpace-titleToRemoveSpace >= usableSpace) { 
            // Disk full
            return -1;
        }
        else {
            t.Status = t.Status.TOADD;
        //        if(!isCompatible(t)){
        //            t = makeCompatible(t);
        //        }
            titlesToAdd.add(t);
            titleToAddSpace += titleSpace;
            
            return 0;
        }
    }

    public void replaceTitle(Title oldTitle, Title newTitle) {
        removeTitles(oldTitle);
        addTitle(newTitle);
    }

    public String getSourceName() {
        return name;
    }

    public void setSourceName(String n) {
        name=n;
    }

    public String getSourceDescription() {
        return description;
    }

    public void setSourceDescription(String d) {
        description = d;
    }

    public URL getSourceURL() {
        try{
            return sourceDir.toURI().toURL();
        } catch(Exception e){
            return null;
        }
    }

    public Object getSource() {
        return sourceDir;
    }

    public void setSource(String source) {
        sourceDir = new File(source);
    }
    
    public void writeTitles(){
        //this.jsymphonic = jsymphonic;
        
        Thread t = new Thread(){
            public void run(){
                try{
                    writeTitlesInTread();
                } catch(Exception e){}
            }
        };
        t.setPriority(t.MIN_PRIORITY);
        t.start();
        
        t = null;
    }
    
    private void writeTitlesInTread() {
        int titlesNumber;
        // Initialize components
        //jsymphonic.initializeTransfer();
        genNw.sendTransferInitializationEvent();

        // Calculate the increment for the progress bar un the transfertDialog
        int exportWeight = 20;
        int rmWeight = 2;
        int addWeight = 20;
        int dbWeight = 1;
        
        Double n = 100.0/(titlesToExport.size()*exportWeight + titlesToRemove.size()*rmWeight + titlesToAdd.size()*addWeight + 2*dbWeight); // 2 stands for the number of config files in the database which must be written
        
        //Export files fisrt
        if(JSymphonic.debug) {System.out.println("Exportation started.");}
        //jsymphonic.setTransfertStatus(1, 0);
        genNw.sendTransfertStatusChange(1, 0);
        //titlesNumber = copyMp3FromToDevice(titlesToExport, n*exportWeight);
//TODO
System.err.println("Exportation is not supported yet");
//        jsymphonic.setTransfertStatus(2, titlesNumber);
        //jsymphonic.setTransfertStatus(2, 0);
        genNw.sendTransfertStatusChange(2, 0);
        
        //Delete files
        if(JSymphonic.debug) {System.out.println("Deleting files started.");}
        //jsymphonic.setTransfertStatus(3, 0);
        genNw.sendTransfertStatusChange(3, 0);
        titlesNumber = deleteFilesFromDevice(titlesToRemove, n*rmWeight);
        //jsymphonic.setTransfertStatus(4, titlesNumber);
        genNw.sendTransfertStatusChange(4, titlesNumber);
        
        //Add new titles
        if(JSymphonic.debug) {System.out.println("Adding files stated.");}
        //jsymphonic.setTransfertStatus(5, 0);
        genNw.sendTransfertStatusChange(5, 0);
        titlesNumber = addFilesToDevice(titlesToAdd, n*addWeight);
        //jsymphonic.setTransfertStatus(6, titlesNumber);
        genNw.sendTransfertStatusChange(6, titlesNumber);

        //Update database
        if(JSymphonic.debug) {System.out.println("Updating database started.");}
        //jsymphonic.setTransfertStatus(7, 0);
        genNw.sendTransfertStatusChange(7, 0);
        dataBase.update(); 
        
        //Write the database to the config file
        if(JSymphonic.debug) {System.out.println("Updating database started (2).");}
        dataBase.write(genNw, n*dbWeight);
        //jsymphonic.setTransfertStatus(8, 0);
        genNw.sendTransfertStatusChange(8, 0);
        
        //The transfert is completed
        refreshTitles(); //reload everything...
        //jsymphonic.setTransfertStatus(9, 0);
        genNw.sendTransfertStatusChange(9, 0);
    }

    
    public void refreshTitles() {
        titlesToAdd.clear(); //Empty list of title to add
        titlesToRemove.clear(); //Empty list of title to remove
        dataBase.clear();
        
        // Update space
        usableSpace = sourceDir.getUsableSpace();
        totalSpace = sourceDir.getTotalSpace();
        titleToRemoveSpace = 0;
        titleToAddSpace = 0;
        
        loadTitlesFromDevice(); // Fill in the title's list
    }

    public long getTotalSpace() {
        return sourceDir.getTotalSpace();
    }

    public long getUsableSpace() {
        return sourceDir.getUsableSpace();
    }

    public void setListener(SystemListener l) {
        listener = l;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    private void loadTitlesFromDevice() {
        //TODO
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private int deleteFilesFromDevice(Vector<Title> titlesToRemove, Object object) {
        //TODO
        return 0;
    }

    private int addFilesToDevice(Vector<Title> titlesToAdd, Object object) {
        //TODO
        return 0;
    }
    */
}
