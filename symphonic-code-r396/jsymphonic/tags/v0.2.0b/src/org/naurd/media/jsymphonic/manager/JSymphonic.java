/*
 * JSymphonic.java
 *
 * Created on 31 mars 2007, 10:23
 */

package org.naurd.media.jsymphonic.manager;
import java.awt.CardLayout;
import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.TreePath;
import org.naurd.media.jsymphonic.system.sony.nw.GenericNW;
import org.naurd.media.jsymphonic.system.sony.nw.OmaDataBaseTools;
import org.naurd.media.jsymphonic.system.sony.nw.SymphonicMap;
import org.naurd.media.jsymphonic.title.Mp3;
import org.naurd.media.jsymphonic.title.Title;
import java.io.*;
/**
 *
 * @author  pballeux
 */
public class JSymphonic extends javax.swing.JFrame {
    public static final int ARTISTALBUMMODE = 0;
    public static final int ARTISTMODE = 1;
    public static final int ALBUMMODE = 2;
    public static final int GENREMODE = 3;
    
    public static final int WINDOWS_LAF = 0;
    public static final int LINUX_LAF = 1;
    public static final int METAL_LAF = 2;
    public static final int NATIVE_LAF = 3;
    
    public static final int UPDATEGUI_GLOBAL = 0;
    public static final int UPDATEGUI_RIGHT = 1;

    public static boolean debug;
    
    private GenericNW player;
    private Settings settings;
    private File localFileSystem;
    private int jTreeDeviceView = 0;
    private Map titlesInJTree;
    private Double transfertProgressBarValue;
    
    
    /**
     * Creates new form JSymphonic
     */
    public JSymphonic(boolean debug) {
        //Set the debug mode (true or flase)
        this.debug = debug;
        
        //Set the settings file
        settings = new Settings();
        
        // Update GUI
        initComponents();
        changeLAF(settings.getIntValue("LAF", WINDOWS_LAF)); // Update LAF
        
        // Choose loading panel
        CardLayout cl = (CardLayout)(panUSBFileSystem.getLayout());
        cl.show(panUSBFileSystem, "cardLoading");
        
        setVisible(true);
        if(debug) {System.out.println("GUI components initialised.");}

        //Read settings file to find the path of the OMGAUDIO folder (default "omgaudio" or "OMGAUDIO"or "../omgaudio" or "../OMGAUDIO")
        settings = new Settings();
        String omgaudioPath = settings.getValue("OMGAUDIOpath","omgaudio");
        File omgaudioDir = new File(omgaudioPath);
        
        //If player folder isn't correct, try in uppercase
        if( !omgaudioDir.exists() ) {
            omgaudioPath = "OMGAUDIO";
            omgaudioDir = new File(omgaudioPath);
        }
        //If player folder isn't correct, try in parent folder
        if( !omgaudioDir.exists() ) {
            omgaudioPath = "../omgaudio";
            omgaudioDir = new File(omgaudioPath);
        }
        //If player folder isn't correct, try in parent folder in uppercase
        if( !omgaudioDir.exists() ) {
            omgaudioPath = "../OMGAUDIO";
            omgaudioDir = new File(omgaudioPath);
        }
        //Before asking to put the good path to the device if this last is not the good, search for the local music path
        
        
        // Read settings file to find the path of the default local file system folder
        String localFileSystemPath = settings.getValue("MusicPath", java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("INVALID_PATH"));
        localFileSystem = new File(localFileSystemPath);
        settings.putValue("MusicPath",localFileSystem.getPath());
        settings.savePreferences();
        
        // Update GUI
        jFileChooser1.setFileFilter(new JSymphonicFileFilter());
        jFileChooser1.setAcceptAllFileFilterUsed(false);
        upDateGUI(UPDATEGUI_GLOBAL);
        // Main window should not be enable now
        setMainWindowEnable(false);

        //If player folder isn't correct, display configuration window 
        if( !omgaudioDir.exists() ) {
            JOptionPane.showMessageDialog(this,java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("WARNING_DEVICE_PATH"));
            Configuration configuration = new Configuration(this, settings, Configuration.PANEL2);
            jLabelLoading.setText("No device found.");
            
            // Enable main window
            setMainWindowEnable(true);
            if(debug) {System.out.println("GUI enable.\nApplication is ready.");}
        }
        else { // Else, we can save the value and load the music from the player
            //Save the settings
            settings.putValue("OMGAUDIOpath",omgaudioDir.getPath());
            settings.putValue("DevicePath",omgaudioDir.getParentFile().getPath());
            settings.savePreferences();

            if(debug) {System.out.println("Folders initialised. Preferences saved.");}
            
            final boolean debugf = debug;
            
            // Create a new thread to load the info of the player
            Thread t = new Thread(){
                public void run(){
                    try{
                        // Choose loading panel
                        CardLayout cl = (CardLayout)(panUSBFileSystem.getLayout());
                        cl.show(panUSBFileSystem, "cardLoading");
        
                        // Create new player
                        File omgaudioDir = new File(settings.getValue("OMGAUDIOpath","omgaudio"));
                        setPlayer(omgaudioDir);
                        if(debugf) {System.out.println("Player's information read.");}

                        upDateGUI(UPDATEGUI_RIGHT);
                        
                        // Enable main window
                        setMainWindowEnable(true);
                        if(debugf) {System.out.println("GUI enable.\nApplication is ready.");}
                    } catch(Exception e){}
                }
            };
            t.setPriority(t.MIN_PRIORITY);
            t.start();

            t = null;
        }

    }
    
    public void setPlayer(File omgaudioDir) {
        String name = omgaudioDir.getParentFile().getName(); // Get the name from the folder

        // If the name is not valid, change it to "Walkman"
        if (name.compareToIgnoreCase(".") == 0) {name = "Walkman";}
        if (name.compareToIgnoreCase("..") == 0) {name = "Walkman";}
        if (name.compareToIgnoreCase("") == 0) {name = "Walkman";}

        javax.swing.ImageIcon sourceIcon = new javax.swing.ImageIcon(getClass().getResource("/org/naurd/media/jsymphonic/ressources/vignette.png"));
        player = new GenericNW(omgaudioDir, name, "", 4, sourceIcon, this);
    }
    
    public void upDateGUI(int action) {
        switch(action) {
            case UPDATEGUI_GLOBAL:
                setMainWindowEnable(true);
                jFileChooser1.setCurrentDirectory(new File(settings.getValue("MusicPath","invalid path")));
                if(debug) {System.out.println("GUI tree device refreshed.");}
                if(debug) {System.out.println("GUI displaySpaceLeft updated.");}
                lblMainMsgLeft.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("READY"));
                jProgressBarLoading.setValue(0);
                setApplyCancelEnable(false);

                break;
                
            case UPDATEGUI_RIGHT:
                jLabelLoading.setText("Loading... Please wait.");
                lblDeviceName.setText(player.getSourceName());  //put the name of the device in the main windows
                displaySpaceLeft();
                refreshJtreeDevice();
                // Display transfert panel
                CardLayout cardL = (CardLayout)(panUSBFileSystem.getLayout());
                cardL.show(panUSBFileSystem, "playerContent");
            default :                
        }
    }

    
    private void setMainWindowEnable(boolean enable) {
        // En/Dis able the buttons
        btnImportTitle.setEnabled(enable);
        btnExportTitle.setEnabled(enable);
        jButtonDelete.setEnabled(enable);
        if(!enable){ btnApplyImport.setEnabled(enable); }
        if(!enable){ btnCancelImport.setEnabled(enable); }
        
        // En/Dis able the file chooser
        jFileChooser1.setEnabled(enable);
        
        // En/Dis able the jTreeDevice
        jTreeDevice.setEnabled(enable);
    }
     
    private void setApplyCancelEnable(boolean b) {
        btnApplyImport.setEnabled(b);
        btnCancelImport.setEnabled(b);
    }
    
    public String getExportPath() {
        return settings.getValue("ExportedMusicPath", settings.getValue("MusicPath", "invalid path") );
    }
    
    public void changeLAF(int newLAF) {
        boolean problem = false;
        
        switch(newLAF) {
            case WINDOWS_LAF:
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                    settings.putIntValue("LAF", WINDOWS_LAF);
                } 
                catch (Exception ex) {
                    problem = true;
                }
                break;
                
            case LINUX_LAF:
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
                    settings.putIntValue("LAF", LINUX_LAF);
                } 
                catch (Exception ex) {
                    problem = true;
                }
                break;
                
            case NATIVE_LAF:
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                    settings.putIntValue("LAF", WINDOWS_LAF);
                } 
                catch (Exception ex) {
                    try {
                        UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
                        settings.putIntValue("LAF", LINUX_LAF);
                    } 
                    catch (Exception e) {
                        problem = true;
                    }
                }
                break;
            default:
                problem = true;
        }
        
        if(problem) {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                settings.putIntValue("LAF", METAL_LAF);
            } 
            catch (Exception ex) {
                System.out.println("Problem occured while trying to change the look and feel");
            }
        }
        
        SwingUtilities.updateComponentTreeUI(this);
    }
        
    public void refreshJtreeDevice() {
        //variable locales
        String titleName, artistName, albumName, genre;
        int titleNumber, year;
        
        if(player.equals(null)) { //If no instance of the sony device exist, just return
            return;
        }
        
        SymphonicMap titles = player.getTitlesInMap();
        
        HashMap artistsList = new HashMap(); //les hashmaps servent à contenir les noeuds pdt que l'on remplit l'arbre
        HashMap albumsList = new HashMap(); 
        HashMap genresList = new HashMap(); 
        List sortedTitles;
        Title title;
        String rootName = player.getSourceName();
        
        //On crée un noeud racine :
        DefaultMutableTreeNode root = new JSymphonicMutableTreeNode(rootName, JSymphonicMutableTreeNode.USB);
        
        switch(jTreeDeviceView) { //les infos affichées dépendent du mode choisi
            case ARTISTMODE:
                sortedTitles = OmaDataBaseTools.sortByArtistTitle(titles); // Sort the title according to the selected mode
                
                titlesInJTree = new HashMap(); // Create a new hashMap to store relationship between paths in JTree and the real titles
                
                Iterator itD = sortedTitles.iterator();
                while(itD.hasNext()){
                    // Get the current title
                    title = (Title)itD.next();
                    // Get its information
                    titleName = title.getTitle();
                    artistName = title.getArtist();
                    titleNumber = title.getTitleNumber();
                    
                    if( !(artistsList.containsKey(artistName)) ){
                        //Si la clé n'exista pas, le noeud de cet artiste n'existe pas, il faut l'ajouter
                        JSymphonicMutableTreeNode tempNode = new JSymphonicMutableTreeNode(artistName, JSymphonicMutableTreeNode.ARTIST);
                        artistsList.put(artistName, tempNode);
                        root.add(tempNode);
                    }
                    //Ensuite, on ajoute le noeud
                    //Pour cela, on extrait le noeud correspondant à l'ariste du HashMap
                    JSymphonicMutableTreeNode tempTreeNode = (JSymphonicMutableTreeNode)artistsList.get(artistName);
                    //On ajoute une feuille
                    if( titleNumber != 0 ) {
                        if( titleNumber < 10 ) {
                            titleName = "0" + titleNumber + " - " + titleName;
                        } else {
                            titleName = titleNumber + " - " + titleName;
                        }
                    }
                    tempTreeNode.add(new JSymphonicMutableTreeNode(titleName, JSymphonicMutableTreeNode.TITLE)); // Add the leaf
                    titlesInJTree.put("[" +rootName + ", " + artistName + ", " + titleName + "]", title); // Add the correspondance in the global hashmap
                    //On remet le noeud avec la nouvelle feuille dans le HashMap
                    artistsList.put(artistName, tempTreeNode);
                    
                }
                break;
                
            case ALBUMMODE:
                sortedTitles = OmaDataBaseTools.sortByAlbumTitleNumber(titles); // Sort the title according to the selected mode
                
                titlesInJTree = new HashMap(); // Create a new hashMap to store relationship between paths in JTree and the real titles
                
                Iterator itAl = sortedTitles.iterator();
                while(itAl.hasNext()){
                    // Get the current title
                    title = (Title)itAl.next();
                    // Get its information
                    titleName = title.getTitle();
                    albumName = title.getAlbum();
                    titleNumber = title.getTitleNumber();
                    
                    if( !(albumsList.containsKey(albumName)) ){
                        //Si la clé n'exista pas, le noeud de cet artiste n'existe pas, il faut l'ajouter
                        JSymphonicMutableTreeNode tempNode = new JSymphonicMutableTreeNode(albumName, JSymphonicMutableTreeNode.ALBUM);
                        albumsList.put(albumName, tempNode);
                        root.add(tempNode);
                    }
                    //Ensuite, on ajoute le noeud
                    //Pour cela, on extrait le noeud correspondant à l'ariste du HashMap
                    JSymphonicMutableTreeNode tempTreeNode = (JSymphonicMutableTreeNode)albumsList.get(albumName);
                    //On ajoute une feuille
                    if( titleNumber != 0 ) {
                        if( titleNumber < 10 ) {
                            titleName = "0" + titleNumber + " - " + titleName;
                        } else {
                            titleName = titleNumber + " - " + titleName;
                        }
                    }
                    tempTreeNode.add(new JSymphonicMutableTreeNode(titleName, JSymphonicMutableTreeNode.TITLE)); // Add the leaf
                    titlesInJTree.put("[" +rootName + ", " + albumName + ", " + titleName + "]", title); // Add the correspondance in the global hashmap
                    //On remet le noeud avec la nouvelle feuille dans le HashMap
                    albumsList.put(albumName, tempTreeNode);
                    
                }

                break;
                
            case GENREMODE:
                sortedTitles = OmaDataBaseTools.sortByGenreArtistAlbumTitle(titles);
                titlesInJTree = new HashMap(); // Create a new hashMap to store relationship between paths in JTree and the real titles
                
                Iterator itG = sortedTitles.iterator();
                while(itG.hasNext()){
                    boolean toAdd = false;
                    
                    // Get the current title
                    title = (Title)itG.next();
                    // Get its information
                    titleName = title.getTitle();
                    artistName = title.getArtist();
                    albumName = title.getAlbum();
                    titleNumber = title.getTitleNumber();
                    genre =  title.getGenre();
                    if(title.Status == title.Status.TOADD) {
                        toAdd = true;
                    }
                    
                    if( !(genresList.containsKey(genre)) ){
                        //Si la clé n'exista pas, le noeud de cet artiste n'existe pas, il faut l'ajouter
                        JSymphonicMutableTreeNode tempNode = new JSymphonicMutableTreeNode(genre, JSymphonicMutableTreeNode.GENRE);
                        genresList.put(genre, tempNode);
                        root.add(tempNode);
                    }
                    
                    //Ensuite, on ajoute le noeud artiste s'il n'existe pas
                    if( !(artistsList.containsKey(artistName)) ){
                        //Si la clé n'exista pas, le noeud de cet artiste n'existe pas, il faut l'ajouter
                        //Pour cela, on extrait le noeud correspondant au genre du HashMap
                        JSymphonicMutableTreeNode tempTreeNode = (JSymphonicMutableTreeNode)genresList.get(genre);
                        
                        JSymphonicMutableTreeNode tempNode = new JSymphonicMutableTreeNode(artistName, JSymphonicMutableTreeNode.ARTIST);
                        if(toAdd) { // Set the color to blue if the corresponding title have to be added
                        }
                        tempTreeNode.add(tempNode);// Add the node
                        
                        artistsList.put(artistName, tempNode); // Update the hashmap
                    }
                    
                    //Ensuite, on ajoute le noeud album s'il n'existe pas
                    if( !(albumsList.containsKey(albumName)) ){
                        //Si la clé n'exista pas, le noeud de cet album n'existe pas, il faut l'ajouter
                        //Pour cela, on extrait le noeud correspondant à l'artiste du HashMap
                        JSymphonicMutableTreeNode tempTreeNode = (JSymphonicMutableTreeNode)artistsList.get(artistName);
                        
                        JSymphonicMutableTreeNode tempNode = new JSymphonicMutableTreeNode(albumName, JSymphonicMutableTreeNode.ALBUM);
                        tempTreeNode.add(tempNode);// Add the node
                        
                        albumsList.put(albumName, tempNode); // Update the hashmap
                    }
                    
                    //Ensuite, on ajoute la feuille
                    //Pour cela, on extrait le noeud correspondant à l'album du HashMap
                    JSymphonicMutableTreeNode tempTreeNode = (JSymphonicMutableTreeNode)albumsList.get(albumName);
                    if( titleNumber != 0 ) {
                        if( titleNumber < 10 ) {
                            titleName = "0" + titleNumber + " - " + titleName;
                        } else {
                            titleName = titleNumber + " - " + titleName;
                        }
                    }
                    tempTreeNode.add(new JSymphonicMutableTreeNode(titleName, JSymphonicMutableTreeNode.TITLE)); // Add the node
                    //On remet le noeud avec la nouvelle feuille dans le HashMap
                    albumsList.put(albumName, tempTreeNode);
                    
                    // Add the correspondance in the global hashmap
                    titlesInJTree.put("[" +rootName + ", " + genre + ", " + artistName + ", " + albumName + ", " + titleName + "]", title); 
                }
                
                break;
            default:
                sortedTitles = OmaDataBaseTools.sortByArtistAlbumTitleNumber(titles);
                titlesInJTree = new HashMap(); // Create a new hashMap to store relationship between paths in JTree and the real titles
                
                Iterator itAA = sortedTitles.iterator();
                while(itAA.hasNext()){
                    boolean toAdd = false;
                    
                    // Get the current title
                    title = (Title)itAA.next();
                    // Get its information
                    titleName = title.getTitle();
                    artistName = title.getArtist();
                    albumName = title.getAlbum();
                    titleNumber = title.getTitleNumber();
                    if(title.Status == title.Status.TOADD) {
                        toAdd = true;
                    }
                    
                    if( !(artistsList.containsKey(artistName)) ){
                        //Si la clé n'exista pas, le noeud de cet artiste n'existe pas, il faut l'ajouter
                        JSymphonicMutableTreeNode tempNode = new JSymphonicMutableTreeNode(artistName, JSymphonicMutableTreeNode.ARTIST);
                        artistsList.put(artistName, tempNode);
                        root.add(tempNode);
                    }
                    
                    //Ensuite, on ajoute le noeud album s'il n'existe pas
                    if( !(albumsList.containsKey(albumName)) ){
                        //Si la clé n'exista pas, le noeud de cet album n'existe pas, il faut l'ajouter
                        //Pour cela, on extrait le noeud correspondant à l'artiste du HashMap
                        JSymphonicMutableTreeNode tempTreeNode = (JSymphonicMutableTreeNode)artistsList.get(artistName);
                        
                        JSymphonicMutableTreeNode tempNode = new JSymphonicMutableTreeNode(albumName, JSymphonicMutableTreeNode.ALBUM);
                        tempTreeNode.add(tempNode);// Add the node
                        
                        albumsList.put(albumName, tempNode); // Update the hashmap
                    }
                    
                    //Ensuite, on ajoute la feuille
                    //Pour cela, on extrait le noeud correspondant à l'album du HashMap
                    JSymphonicMutableTreeNode tempTreeNode = (JSymphonicMutableTreeNode)albumsList.get(albumName);
                    if( titleNumber != 0 ) {
                        if( titleNumber < 10 ) {
                            titleName = "0" + titleNumber + " - " + titleName;
                        } else {
                            titleName = titleNumber + " - " + titleName;
                        }
                    }
                    tempTreeNode.add(new JSymphonicMutableTreeNode(titleName, JSymphonicMutableTreeNode.TITLE)); // Add the node
                    //On remet le noeud avec la nouvelle feuille dans le HashMap
                    albumsList.put(albumName, tempTreeNode);
                    
                    // Add the correspondance in the global hashmap
                    titlesInJTree.put("[" +rootName + ", " + artistName + ", " + albumName + ", " + titleName + "]", title); 
                }
        }
                
        // associer l’abre modèle au modèle et provoquer son affichage
        DefaultTreeModel model = (DefaultTreeModel) jTreeDevice.getModel() ;
        model.setRoot(root);
        model.reload(root);
        
        //On met du style :
        // Construction d'un afficheur par défaut.
        jTreeDevice.setCellRenderer(new JSymphonicTreeCellRenderer());
    }
        
    private void displaySpaceLeft() {
        // Update label X/Y
        lblTxtSpaceLeft.setText(player.getSpaceLeftInText());
        // Update bar
        pgSpaceLeft.setValue(player.getSpaceLeftInRatio());
    }

    public void displayLoadingProgress(int ratio) {
        if(ratio == 0) {
            // Update bar
            jProgressBarLoading.setValue(ratio);
            // Set text if it hasn't been already made
            lblMainMsgLeft.setText(" Ready");
        }
        else {
            // Update bar
            jProgressBarLoading.setValue(ratio);
            // Set text if it hasn't been already made
            lblMainMsgLeft.setText(" Loading...");
        }
    }
    

    public void initializeTransfer() {
        jProgressBarTransfert.setValue(0);
        transfertProgressBarValue = 0.0;
        
        jLabelExport.setText("...");
        jLabelDelete.setText("...");
        jLabelAdd.setText("...");
        jLabelUpdate.setText("...");
        jLabelComplete.setText("...");
    }

    public void increaseTransfertProgressBar(Double n) {
        jProgressBarTransfert.setValue((transfertProgressBarValue += n).intValue());
    }

    public void setTransfertStatus(int status, int numberOfTrack) {
        switch(status) {
            case 1:
                jLabelExport.setText("Exporting files...");
                break;
                
            case 2:
                if(numberOfTrack > 1) {
                    jLabelExport.setText(numberOfTrack + " files have been exported.");
                }
                else if(numberOfTrack > 0) {
                    jLabelExport.setText(numberOfTrack + " file has been exported.");
                }
                else {
                    jLabelExport.setText("No files exported.");
                }
                break;
            
            case 3:
                jLabelDelete.setText("Deleting files...");
                break;
                
            case 4:
                if(numberOfTrack > 1) {
                    jLabelDelete.setText(numberOfTrack + " files have been deleted.");
                }
                else if(numberOfTrack > 0) {
                    jLabelDelete.setText(numberOfTrack + " file has been deleted.");
                }
                else {
                    jLabelDelete.setText("No files deleted.");
                }
                break;
                
            case 5:
                jLabelAdd.setText("Adding files...");
                break;
                
            case 6:
                if(numberOfTrack > 1) {
                    jLabelAdd.setText(numberOfTrack + " files have been added.");
                }
                else if(numberOfTrack > 0) {
                    jLabelAdd.setText(numberOfTrack + " file has been added.");
                }
                else {
                    jLabelAdd.setText("No files added.");
                }
                break;                

            case 7:
                jLabelUpdate.setText("Updating database...");
                break;
                
            case 8:
                jLabelUpdate.setText("Database updated.");
                break;
                
            case 9:
                jLabelComplete.setText("Transfert complete !!");
                jProgressBarTransfert.setValue(100); //Make sur the progress bar to be full
                jButtonTransfertOK.setEnabled(true);
                break;
        }
    }
        
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        splitMain = new javax.swing.JSplitPane();
        panPlayerAndSource = new javax.swing.JPanel();
        panSources = new javax.swing.JPanel();
        panSourceTitles = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jFileChooser1 = new javax.swing.JFileChooser();
        panSourceTitlesButtons = new javax.swing.JPanel();
        spacer2 = new javax.swing.JLabel();
        btnImportTitle = new javax.swing.JButton();
        btnExportTitle = new javax.swing.JButton();
        btnSyncTitles = new javax.swing.JButton();
        spacer = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        btnApplyImport = new javax.swing.JButton();
        btnCancelImport = new javax.swing.JButton();
        spacer1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jButtonDelete = new javax.swing.JButton();
        jButtonDeleteAll = new javax.swing.JButton();
        spacer3 = new javax.swing.JLabel();
        panUSBPlayer = new javax.swing.JPanel();
        panUSBPlayerDetails = new javax.swing.JPanel();
        lblDeviceName = new javax.swing.JLabel();
        pgSpaceLeft = new javax.swing.JProgressBar();
        lblTxtSpaceLeft = new javax.swing.JLabel();
        panUSBFileSystem = new javax.swing.JPanel();
        jPanelPlayerContent = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTreeDevice = new javax.swing.JTree();
        jPanelTransfert = new javax.swing.JPanel();
        jLabelTransferInformation = new javax.swing.JLabel();
        jLabelExport = new javax.swing.JLabel();
        jLabelDelete = new javax.swing.JLabel();
        jLabelAdd = new javax.swing.JLabel();
        jLabelUpdate = new javax.swing.JLabel();
        jLabelComplete = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jProgressBarTransfert = new javax.swing.JProgressBar();
        jPanel3 = new javax.swing.JPanel();
        jButtonTransfertOK = new javax.swing.JButton();
        jPanelLoading = new javax.swing.JPanel();
        jLabelLoading = new javax.swing.JLabel();
        panMainMenu = new javax.swing.JPanel();
        panMainStatus = new javax.swing.JPanel();
        jProgressBarLoading = new javax.swing.JProgressBar();
        lblMainMsgLeft = new javax.swing.JLabel();
        lblMainMsgRight = new javax.swing.JLabel();
        mainMenuBar = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemQuit = new javax.swing.JMenuItem();
        jMenuOptions = new javax.swing.JMenu();
        jMenuItemConfiguration = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("TITLE"));
        splitMain.setDividerLocation(550);
        panPlayerAndSource.setLayout(new java.awt.BorderLayout());

        panSources.setLayout(new java.awt.BorderLayout());

        panSourceTitles.setLayout(new java.awt.BorderLayout());

        panSourceTitles.setBorder(javax.swing.BorderFactory.createTitledBorder(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("TITLE_SOURCE")));
        panSourceTitles.setEnabled(false);
        jPanel1.setLayout(new java.awt.BorderLayout());

        panSourceTitles.add(jPanel1, java.awt.BorderLayout.NORTH);

        jFileChooser1.setControlButtonsAreShown(false);
        jFileChooser1.setFileSelectionMode(javax.swing.JFileChooser.FILES_AND_DIRECTORIES);
        jFileChooser1.setBorder(null);
        jFileChooser1.setMultiSelectionEnabled(true);
        panSourceTitles.add(jFileChooser1, java.awt.BorderLayout.CENTER);

        panSources.add(panSourceTitles, java.awt.BorderLayout.CENTER);

        panSourceTitlesButtons.setLayout(new java.awt.GridLayout(13, 1, 0, 10));

        panSourceTitlesButtons.setMinimumSize(new java.awt.Dimension(90, 458));
        panSourceTitlesButtons.setPreferredSize(new java.awt.Dimension(150, 110));
        spacer2.setText(" ");
        panSourceTitlesButtons.add(spacer2);

        btnImportTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/naurd/media/jsymphonic/ressources/import.png")));
        btnImportTitle.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("IMPORT"));
        btnImportTitle.setEnabled(false);
        btnImportTitle.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnImportTitleMouseClicked(evt);
            }
        });

        panSourceTitlesButtons.add(btnImportTitle);

        btnExportTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/naurd/media/jsymphonic/ressources/export.png")));
        btnExportTitle.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("EXPORT"));
        btnExportTitle.setEnabled(false);
        btnExportTitle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportTitleActionPerformed(evt);
            }
        });

        panSourceTitlesButtons.add(btnExportTitle);

        btnSyncTitles.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("SYNC"));
        btnSyncTitles.setEnabled(false);
        panSourceTitlesButtons.add(btnSyncTitles);

        spacer.setText(" ");
        panSourceTitlesButtons.add(spacer);

        panSourceTitlesButtons.add(jSeparator2);

        btnApplyImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/naurd/media/jsymphonic/ressources/apply.png")));
        btnApplyImport.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("APPLY"));
        btnApplyImport.setEnabled(false);
        btnApplyImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyImportActionPerformed(evt);
            }
        });

        panSourceTitlesButtons.add(btnApplyImport);

        btnCancelImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/naurd/media/jsymphonic/ressources/cancel.png")));
        btnCancelImport.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("CANCEL"));
        btnCancelImport.setEnabled(false);
        btnCancelImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelImportActionPerformed(evt);
            }
        });

        panSourceTitlesButtons.add(btnCancelImport);

        spacer1.setText(" ");
        panSourceTitlesButtons.add(spacer1);

        panSourceTitlesButtons.add(jSeparator1);

        jButtonDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/naurd/media/jsymphonic/ressources/delete.png")));
        jButtonDelete.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("DELETE"));
        jButtonDelete.setEnabled(false);
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });

        panSourceTitlesButtons.add(jButtonDelete);

        jButtonDeleteAll.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("DELETE_ALL"));
        jButtonDeleteAll.setEnabled(false);
        panSourceTitlesButtons.add(jButtonDeleteAll);

        spacer3.setText(" ");
        panSourceTitlesButtons.add(spacer3);

        panSources.add(panSourceTitlesButtons, java.awt.BorderLayout.EAST);

        panPlayerAndSource.add(panSources, java.awt.BorderLayout.CENTER);

        splitMain.setLeftComponent(panPlayerAndSource);

        panUSBPlayer.setLayout(new java.awt.BorderLayout());

        panUSBPlayerDetails.setLayout(new java.awt.BorderLayout());

        panUSBPlayerDetails.setBorder(javax.swing.BorderFactory.createTitledBorder(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("DEVICE_DETAIL")));
        lblDeviceName.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDeviceName.setText("Sony Device");
        panUSBPlayerDetails.add(lblDeviceName, java.awt.BorderLayout.NORTH);

        pgSpaceLeft.setMaximum(1000);
        panUSBPlayerDetails.add(pgSpaceLeft, java.awt.BorderLayout.SOUTH);

        lblTxtSpaceLeft.setFont(new java.awt.Font("Dialog", 0, 12));
        lblTxtSpaceLeft.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTxtSpaceLeft.setText(".../...");
        panUSBPlayerDetails.add(lblTxtSpaceLeft, java.awt.BorderLayout.CENTER);

        panUSBPlayer.add(panUSBPlayerDetails, java.awt.BorderLayout.NORTH);

        panUSBFileSystem.setLayout(new java.awt.CardLayout());

        panUSBFileSystem.setBorder(javax.swing.BorderFactory.createTitledBorder(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("DEVICE_TITLES")));
        jPanelPlayerContent.setLayout(new javax.swing.BoxLayout(jPanelPlayerContent, javax.swing.BoxLayout.Y_AXIS));

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.X_AXIS));

        jPanel2.setMaximumSize(new java.awt.Dimension(32818, 24));
        jLabel1.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("SORT_BY"));
        jPanel2.add(jLabel1);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Artist/Album/Title", "Artists/Titles", "Albums/Titles", "Genres/Artists/Albums/Titles" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jPanel2.add(jComboBox1);

        jPanelPlayerContent.add(jPanel2);

        jTreeDevice.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jTreeDevice.setEnabled(false);
        jTreeDevice.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jTreeDeviceComponentShown(evt);
            }
        });
        jTreeDevice.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jTreeDeviceMouseDragged(evt);
            }
        });

        jScrollPane1.setViewportView(jTreeDevice);

        jPanelPlayerContent.add(jScrollPane1);

        panUSBFileSystem.add(jPanelPlayerContent, "playerContent");

        jLabelTransferInformation.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("TRANSFER_INFORMATION"));

        jLabelExport.setText("...");

        jLabelDelete.setText("...");

        jLabelAdd.setText("...");

        jLabelUpdate.setText("...");

        jLabelComplete.setText("...");

        jLabel5.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("TOTAL_PROGRESS"));

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.X_AXIS));

        jProgressBarTransfert.setMaximumSize(new java.awt.Dimension(32767, 15));
        jProgressBarTransfert.setMinimumSize(new java.awt.Dimension(10, 10));
        jProgressBarTransfert.setPreferredSize(new java.awt.Dimension(250, 15));
        jPanel4.add(jProgressBarTransfert);

        jButtonTransfertOK.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("OK"));
        jButtonTransfertOK.setEnabled(false);
        jButtonTransfertOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTransfertOKActionPerformed(evt);
            }
        });

        jPanel3.add(jButtonTransfertOK);

        javax.swing.GroupLayout jPanelTransfertLayout = new javax.swing.GroupLayout(jPanelTransfert);
        jPanelTransfert.setLayout(jPanelTransfertLayout);
        jPanelTransfertLayout.setHorizontalGroup(
            jPanelTransfertLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTransfertLayout.createSequentialGroup()
                .addGroup(jPanelTransfertLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelTransferInformation, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelTransfertLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanelTransfertLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabelAdd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelExport, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelDelete, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)))
                    .addGroup(jPanelTransfertLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabelComplete, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelTransfertLayout.createSequentialGroup()
                            .addGap(12, 12, 12)
                            .addComponent(jLabelUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanelTransfertLayout.setVerticalGroup(
            jPanelTransfertLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTransfertLayout.createSequentialGroup()
                .addComponent(jLabelTransferInformation, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelExport, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelComplete, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panUSBFileSystem.add(jPanelTransfert, "transfert");

        jPanelLoading.setLayout(new javax.swing.BoxLayout(jPanelLoading, javax.swing.BoxLayout.X_AXIS));

        jLabelLoading.setText("Loading... Please wait.");
        jPanelLoading.add(jLabelLoading);

        panUSBFileSystem.add(jPanelLoading, "cardLoading");

        panUSBPlayer.add(panUSBFileSystem, java.awt.BorderLayout.CENTER);

        splitMain.setRightComponent(panUSBPlayer);

        getContentPane().add(splitMain, java.awt.BorderLayout.CENTER);

        panMainMenu.setLayout(new java.awt.BorderLayout());

        getContentPane().add(panMainMenu, java.awt.BorderLayout.NORTH);

        panMainStatus.setLayout(new javax.swing.BoxLayout(panMainStatus, javax.swing.BoxLayout.X_AXIS));

        jProgressBarLoading.setMaximumSize(new java.awt.Dimension(100, 14));
        panMainStatus.add(jProgressBarLoading);

        lblMainMsgLeft.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("LOADING"));
        panMainStatus.add(lblMainMsgLeft);

        lblMainMsgRight.setText(" ");
        panMainStatus.add(lblMainMsgRight);

        getContentPane().add(panMainStatus, java.awt.BorderLayout.SOUTH);

        jMenuFile.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("FILE"));
        jMenuItemQuit.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("QUIT"));
        jMenuItemQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemQuitActionPerformed(evt);
            }
        });

        jMenuFile.add(jMenuItemQuit);

        mainMenuBar.add(jMenuFile);

        jMenuOptions.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("OPTIONS"));
        jMenuItemConfiguration.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("CONFIGURATION"));
        jMenuItemConfiguration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemConfigurationActionPerformed(evt);
            }
        });

        jMenuOptions.add(jMenuItemConfiguration);

        mainMenuBar.add(jMenuOptions);

        setJMenuBar(mainMenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonTransfertOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTransfertOKActionPerformed
        // Display player content panel
        CardLayout cl = (CardLayout)(panUSBFileSystem.getLayout());
        cl.show(panUSBFileSystem, "playerContent");
        
        // Update GUI
        refreshJtreeDevice();
        setApplyCancelEnable(false);
        displaySpaceLeft();
        lblMainMsgLeft.setText(" Ready");
        jProgressBarLoading.setValue(0); 
        setMainWindowEnable(true);
        
        // Disable the OK button for next time
        jButtonTransfertOK.setEnabled(false);
    }//GEN-LAST:event_jButtonTransfertOKActionPerformed

    private void jMenuItemQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemQuitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jMenuItemQuitActionPerformed

    private void jMenuItemConfigurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConfigurationActionPerformed
        Configuration configuration = new Configuration(this, settings);
    }//GEN-LAST:event_jMenuItemConfigurationActionPerformed

    private void jTreeDeviceMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTreeDeviceMouseDragged
// TODO add your handling code here:
    }//GEN-LAST:event_jTreeDeviceMouseDragged

    private void btnCancelImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelImportActionPerformed
        // Cancel changes in the devices
        player.refreshTitles();
        
        // Update GUI
        upDateGUI(0);
    }//GEN-LAST:event_btnCancelImportActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        jTreeDeviceView = jComboBox1.getSelectedIndex();
        refreshJtreeDevice();
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        TreePath currentPath;
                
        // Get the paths of the title to remove
        TreePath[] pathsToRemove = jTreeDevice.getSelectionPaths();
        
        // Get all the existing paths
        Set pathsSet = titlesInJTree.keySet();
        
        // For each path, remove the titles
        for(int i = 0; i < pathsToRemove.length; i++){
            currentPath = pathsToRemove[i];
            
            // As the current path can design a part of a real path (i.e. it can design an entire album, and not only a title), we need to search in all the path the ones which have to be deleted
            Iterator it = pathsSet.iterator();
            
            while(it.hasNext()) { // For each existing saved path, if the current path is include in this path, the current path represent a title to delete 
                String pathInSavedPathsList = (String)it.next();
                if(pathInSavedPathsList.startsWith(currentPath.toString().replace("]", ""))) { // If the current title contains in its path the current path, it sould be deleted
                        // Get the title from the global HashMap
                        Title titleToRemove = (Title)titlesInJTree.get(pathInSavedPathsList);                    
                        
                        // Remove title
                        player.removeTitles(titleToRemove);  
                }
            }
        }
        
        // Refresh GUI
        refreshJtreeDevice();
        setApplyCancelEnable(true);
        displaySpaceLeft();
        lblMainMsgLeft.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("CHANGES_NO_APPLY"));
    }//GEN-LAST:event_jButtonDeleteActionPerformed
    
    private void btnExportTitleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportTitleActionPerformed
        TreePath currentPath;
                
        // Get the paths of the title to export
        TreePath[] pathsToExport = jTreeDevice.getSelectionPaths();
        
        // Get all the existing paths
        Set pathsSet = titlesInJTree.keySet();
        
        // For each path, export the titles
        for(int i = 0; i < pathsToExport.length; i++){
            currentPath = pathsToExport[i];
            
            // As the current path can design a part of a real path (i.e. it can design an entire album, and not only a title), we need to search in all the path the ones which have to be exported
            Iterator it = pathsSet.iterator();
            
            while(it.hasNext()) { // For each existing saved path, if the current path is include in this path, the current path represent a title to export 
                String pathInSavedPathsList = (String)it.next();
                if(pathInSavedPathsList.startsWith(currentPath.toString().replace("]", ""))) { // If the current title contains in its path the current path, it sould be exported
                        // Get the title from the global HashMap
                        Title titleToExport = (Title)titlesInJTree.get(pathInSavedPathsList);                    
                        
                        // Export title
                        player.exportTitle(titleToExport);  
                }
            }
        }
        
        // Refresh GUI
//        refreshJtreeDevice();
        setApplyCancelEnable(true);
        lblMainMsgLeft.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("CHANGES_NO_APPLY"));
    }//GEN-LAST:event_btnExportTitleActionPerformed
    
    private void btnApplyImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyImportActionPerformed
        // Lock the main windows while transferring
        setMainWindowEnable(false);
        
        // Display transfert panel
        CardLayout cl = (CardLayout)(panUSBFileSystem.getLayout());
        cl.show(panUSBFileSystem, "transfert");
        
        // Start operations
        player.writeTitles(this);
    }//GEN-LAST:event_btnApplyImportActionPerformed
    
    private void btnImportTitleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnImportTitleMouseClicked
        File files[] = jFileChooser1.getSelectedFiles();
        
        
        for(int i = 0; i < files.length; i++) {
            scanAndAddTitles(files[i]);
        }

        // Update GUI
        refreshJtreeDevice();
        setApplyCancelEnable(true);
        displaySpaceLeft();
        lblMainMsgLeft.setText(" Changes haven't been applied");
    }//GEN-LAST:event_btnImportTitleMouseClicked

    // Define a recursive function to scan folders
    private void scanAndAddTitles(File file) {
            if (file.isFile()){
                Mp3 newTitle = new Mp3(file);
                if(player.addTitle(newTitle) < 0 ) {
                    JOptionPane.showMessageDialog(this,java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("WARNING_PLAYER_FULL"));
                    return;
                }
            } 
            else {
                File[] fileList = file.listFiles(new JSymphonicIoFileFilter());
                for (int i = 0; i<fileList.length; i++){
                    scanAndAddTitles(fileList[i]);
                }
            }
    }
        
    private void jTreeDeviceComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jTreeDeviceComponentShown
            }//GEN-LAST:event_jTreeDeviceComponentShown
            
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        // Check if arguments have been passed
        if(args.length > 0) {
            String arg1 = args[1];

            // If the argument "-debug" has been passed in argument
            if(arg1.equals("-help") || arg1.equals("-h") || arg1.equals("--help")) {
                System.out.println("\n JSymphonic version 0.2 beta \n");
                System.out.println("Options:");
                System.out.println("\t -debug \t Run the program in debug mode, displaying actions performed.");
                System.out.println("\t -help \t Print this help.");
                System.out.println("\n");
                System.out.println("In case of problems or comments, please visit our SourceForge page at 'https://sourceforge.net/projects/symphonic'.");
                System.out.println("");
            }
            
            // If the argument "-debug" has been passed in argument
            else if(arg1.equals("-debug")) {
                System.out.println("JSymphonic is run in debug mode.");
                // Run the program normaly
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        new JSymphonic(true);
                    }
                });
            }
            
            else {
                System.out.println("JSymphonic:  Option <" + arg1 + "> unknown.");
                System.out.println("JSymphonic:  Please use -help to get the list of correct options.");
            }
        }
        else {
            // Run the program normaly
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new JSymphonic(false); // True for debug mode, false otherwise
                }
            });
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApplyImport;
    private javax.swing.JButton btnCancelImport;
    private javax.swing.JButton btnExportTitle;
    private javax.swing.JButton btnImportTitle;
    private javax.swing.JButton btnSyncTitles;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonDeleteAll;
    private javax.swing.JButton jButtonTransfertOK;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabelAdd;
    private javax.swing.JLabel jLabelComplete;
    private javax.swing.JLabel jLabelDelete;
    private javax.swing.JLabel jLabelExport;
    private javax.swing.JLabel jLabelLoading;
    private javax.swing.JLabel jLabelTransferInformation;
    private javax.swing.JLabel jLabelUpdate;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenuItem jMenuItemConfiguration;
    private javax.swing.JMenuItem jMenuItemQuit;
    private javax.swing.JMenu jMenuOptions;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanelLoading;
    private javax.swing.JPanel jPanelPlayerContent;
    private javax.swing.JPanel jPanelTransfert;
    private javax.swing.JProgressBar jProgressBarLoading;
    private javax.swing.JProgressBar jProgressBarTransfert;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTree jTreeDevice;
    private javax.swing.JLabel lblDeviceName;
    private javax.swing.JLabel lblMainMsgLeft;
    private javax.swing.JLabel lblMainMsgRight;
    private javax.swing.JLabel lblTxtSpaceLeft;
    private javax.swing.JMenuBar mainMenuBar;
    private javax.swing.JPanel panMainMenu;
    private javax.swing.JPanel panMainStatus;
    private javax.swing.JPanel panPlayerAndSource;
    private javax.swing.JPanel panSourceTitles;
    private javax.swing.JPanel panSourceTitlesButtons;
    private javax.swing.JPanel panSources;
    private javax.swing.JPanel panUSBFileSystem;
    private javax.swing.JPanel panUSBPlayer;
    private javax.swing.JPanel panUSBPlayerDetails;
    private javax.swing.JProgressBar pgSpaceLeft;
    private javax.swing.JLabel spacer;
    private javax.swing.JLabel spacer1;
    private javax.swing.JLabel spacer2;
    private javax.swing.JLabel spacer3;
    private javax.swing.JSplitPane splitMain;
    // End of variables declaration//GEN-END:variables
    
}
