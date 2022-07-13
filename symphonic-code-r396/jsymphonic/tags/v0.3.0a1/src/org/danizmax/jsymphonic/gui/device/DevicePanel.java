/*
 *  Copyright (C) 2008 Daniel Žalar (danizmax@yahoo.com)
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
 * JSymphonicWindow.java
 *
 * DevicePanel.java
 *
 * Created on March 25, 2008, 9:50 PM
 */

package org.danizmax.jsymphonic.gui.device;

import org.danizmax.jsymphonic.toolkit.DynamicDeviceTreePopUp;
import java.io.File;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import org.naurd.media.jsymphonic.system.sony.nw.NWGenericListener;


/**
 * This class is a JPanel component shows the state of the Sony (c) device
 * @author  Daniel Žalar (danizmax@yahoo.com)
 */
public class DevicePanel extends javax.swing.JPanel implements NWGenericListener{
        
    private static Logger logger = Logger.getLogger("org.danizmax.gui.DevicePanel");
    private DeviceManager deviceManager;
    private String devicePath = null;
    private int deviceGeneration = 0;
    private String deviceName = "Walkman";
    
    public static String FILTER_ARALT = "Artist/Album/Title";
    public static String FILTER_ART = "Artist/Title";
    public static String FILTER_ALT = "Album/Title";
    public static String FILTER_GARALT = "Genres/Artists/Albums/Titles";

    private int transferProgressValue;
    private int exportProgressValue;
    private int deleteProgressValue;
    private int importProgressValue;
    private int decodeProgressValue;
    private int encodeProgressValue;
    private int updateProgressValue;
    
    private DynamicDeviceTreePopUp treePopUp;
    /** Creates new instance of DevicePanel */
    public DevicePanel() {
        treePopUp = new DynamicDeviceTreePopUp(this,deviceTree);
        try {
            initComponents();
        }
        catch(Exception e){
            logger.severe("Error while initializaing device panel");
            e.printStackTrace();
        }
        initTransferFrame();
        
        transferPanel.setVisible(false);
        deviceContentPanel.setVisible(true); // Nicolas: I added this line because nothing was shown at startup
        deviceTree.setVisible(false); // Nicolas: hide the device tree, it will be showed when filled
        deviceTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
       
        deviceManager  = new DeviceManager(deviceTree, this, "",0, "", "");
        treePopUp.setTree(deviceTree);
        deviceTree.setComponentPopupMenu(treePopUp);
    }
        
    public String getSelectedFilterProfile(){
        return (String) viewComboBox.getSelectedItem();
    }

    private void initTransferFrame() {
        // Hide all progress bar (and the corresponding labels)...
        importLabel.setVisible(false);
        importProgressBar.setVisible(false);
        exportLabel.setVisible(false);
        exportProgressBar.setVisible(false);
        deleteLabel.setVisible(false);
        deleteProgressBar.setVisible(false);
        encodeLabel.setVisible(false);
        encodeProgressBar.setVisible(false);
        decodeLabel.setVisible(false);
        decodeProgressBar.setVisible(false);
        updateLabel.setVisible(false);
        updateProgressBar.setVisible(false);
        fileLabel.setVisible(false);
        fileProgressBar.setVisible(false);
        decodedFileLabel.setVisible(false);
        decodedFileProgressBar.setVisible(false);
        encodedFileLabel.setVisible(false);
        encodedFileProgressBar.setVisible(false);

        // Show the transfer panel (with all the progress bars)
        transferPanel.setVisible(true);
        // Hide the device content panel
        deviceContentPanel.setVisible(false);
        // Disable the "OK" button (only enabled when the transfer is over)
        closeButton.setEnabled(false);
    }
    
    private void loadFilterProfiles(){
        viewComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { FILTER_ARALT, FILTER_ART, FILTER_ALT, FILTER_GARALT }));
    }
    
    /**
     * Set the GUI state as if mounted
     * @param isMounted if true set the GUI state as if mounted
     */
    private void setMountedGuiState(boolean isMounted){
        if(!isMounted){
                deviceActionProgressBar.setString(java.util.ResourceBundle.getBundle("localization/language").getString("DevicePanel.Device_is_not_mounted"));
                deviceActionProgressBar.setValue(0);
                ((DefaultTreeModel)deviceTree.getModel()).setRoot(null);
        }
        
        mountToggleButton.setSelected(isMounted);
    }
    
    /**
     * Mounts or unmounts the device
     * @param mount if true mount the device else unmount
     */
    public void mountDevice(final boolean mount){
        Thread mountDeviceThread = new Thread(){
            @Override
            public void run(){
                try{
                    mountDeviceInThread(mount);
                } catch(Exception e){}
            }
        };
        mountDeviceThread.setPriority(Thread.NORM_PRIORITY);
        mountDeviceThread.start();
        mountDeviceThread = null;
    }
    
    /**
     * Start a thread to mount the device
     */
    public void mountDeviceInThread(boolean mount){
        if(getManager() != null){
            if(!getManager().isInImportState()){ //we must not let unmount while in import state
                setMountedGuiState(false);
                    if(mount){
                        getManager().mountDevice(true);
                        reloadTree();
                    }else{
                        getManager().mountDevice(false);
                    }
                    setMountedGuiState(getManager().isMounted());
            }else{
                JOptionPane.showMessageDialog(this, java.util.ResourceBundle.getBundle("localization/language").getString("DevicePanel.msg03.Cannot_unmount"), java.util.ResourceBundle.getBundle("localization/language").getString("DevicePanel.msg03.Mounting_error"), JOptionPane.INFORMATION_MESSAGE);
                setMountedGuiState(getManager().isMounted());}
        }
    }
    
    /**
     * 
     * @param files files for import
     */
    public void scheduleTrackImport(File files[]){
        int ret = getManager().scheduleTrackImport(files);
        //if ret >0 some files have been scheduled, 0 if no errors occurs or ni files scheduled, -1 if a file's extension is not recognized and -2 the device is full
        /*if( ret == -1 ){
            JOptionPane.showMessageDialog(this, "bla", "dss", JOptionPane.ERROR_MESSAGE);
        }else */if( ret == -2){
            JOptionPane.showMessageDialog(this, "The device is full!", "Storage error...", JOptionPane.ERROR_MESSAGE);
        }/*else if( ret > 0){
        }*/
        
    }
    
    public void scheduleTrackExport(){
        getManager().scheduleTrackExport();
    }
    
    public void deleteSelectedTracks(){
        getManager().scheduleTrackDeletion();
    }
    
    public void applyChanges(){
      //  transferStatusFrame.setVisible(true);
        getManager().applyChanges();
    }
    
    public void cancelChanges(){
        getManager().cancelChanges();
        reloadTree();
    }
    
     /** Load data into the tree  */
     public void reloadTree(){
       if(getManager().isOmgPathIntialized()){
           if(getDeviceManager().isMounted()){
                deviceName =getManager().getOmgaudioDir().getParentFile().getName(); // Get the name from the folder
                //If genericDevice folder isn't correct, display configuration window 
                if( !deviceManager.getOmgaudioDir().exists() ) {
                    JOptionPane.showMessageDialog(this,java.util.ResourceBundle.getBundle("localization/language").getString("DevicePanel.msg01.The_device_path") + "" + devicePath + "" + java.util.ResourceBundle.getBundle("localization/language").getString("DevicePanel.msg01.does_not_exist"));
                }
                else { // Else, we can save the value and load the music from the genericDevice            
                    // Create a new thread to load the info of the genericDevice
                    enableGUI(false);
                    deviceActionProgressBar.setString(java.util.ResourceBundle.getBundle("localization/language").getString("DevicePanel.Loading_items"));
                    //mountDevice(true);  
                    Thread t = new Thread(){
                        @Override
                        public void run(){
                            try{
                                // Create new genericDevice

                                //load the tree
                                getManager().refreshDeviceTree(viewComboBox.getSelectedIndex(), filterTextField.getText());
                                displayDeviceSpace();

                            } catch(Exception e){
                                logger.warning(e.getMessage());
                            }
                        }
                    };
                    t.setPriority(Thread.MIN_PRIORITY);
                    t.start();

                    t = null;
                    enableGUI(true);        
                }
           }else{
               getLogger().warning("The device is not mounted!");
           }
       }else{
            logger.warning("Cannot find OMG path. The device path is probably wrong!");
            JOptionPane.showMessageDialog(this, java.util.ResourceBundle.getBundle("localization/language").getString("DevicePanel.msg02.Cannot_find_OMG_path"), java.util.ResourceBundle.getBundle("localization/language").getString("DevicePanel.msg02.Reloading_tree"), JOptionPane.ERROR_MESSAGE);
       }
     }
     
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        deviceContentPanel = new javax.swing.JPanel();
        deviceActionProgressBar = new javax.swing.JProgressBar();
        clearButton = new javax.swing.JButton();
        viewComboBox = new javax.swing.JComboBox();
        filterTextField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        deviceTree = new javax.swing.JTree();
        filterButton = new javax.swing.JButton();
        mountToggleButton = new javax.swing.JToggleButton();
        transferPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        exportLabel = new javax.swing.JLabel();
        exportProgressBar = new javax.swing.JProgressBar();
        deleteLabel = new javax.swing.JLabel();
        deleteProgressBar = new javax.swing.JProgressBar();
        importLabel = new javax.swing.JLabel();
        importProgressBar = new javax.swing.JProgressBar();
        decodeLabel = new javax.swing.JLabel();
        decodeProgressBar = new javax.swing.JProgressBar();
        encodeLabel = new javax.swing.JLabel();
        encodeProgressBar = new javax.swing.JProgressBar();
        updateLabel = new javax.swing.JLabel();
        updateProgressBar = new javax.swing.JProgressBar();
        spacerPanel = new javax.swing.JPanel();
        fileLabel = new javax.swing.JLabel();
        fileProgressBar = new javax.swing.JProgressBar();
        decodedFileLabel = new javax.swing.JLabel();
        decodedFileProgressBar = new javax.swing.JProgressBar();
        encodedFileLabel = new javax.swing.JLabel();
        encodedFileProgressBar = new javax.swing.JProgressBar();
        currentFileLabel = new javax.swing.JLabel();
        transferProgressBar = new javax.swing.JProgressBar();
        closeButton = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setName("Form"); // NOI18N

        deviceContentPanel.setName("deviceContentPanel"); // NOI18N

        deviceActionProgressBar.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        deviceActionProgressBar.setName("deviceActionProgressBar"); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("localization/language"); // NOI18N
        deviceActionProgressBar.setString(bundle.getString("DevicePanel.Device_is_not_mounted")); // NOI18N
        deviceActionProgressBar.setStringPainted(true);

        clearButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/clear_right.png"))); // NOI18N
        clearButton.setName("clearButton"); // NOI18N
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });

        viewComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        viewComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Artist/Album/Title", "Artists/Titles", "Albums/Titles", "Genres/Artists/Albums/Titles" }));
        viewComboBox.setName("viewComboBox"); // NOI18N
        viewComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewComboBoxActionPerformed(evt);
            }
        });

        filterTextField.setMinimumSize(new java.awt.Dimension(4, 28));
        filterTextField.setName("filterTextField"); // NOI18N
        filterTextField.setPreferredSize(new java.awt.Dimension(4, 20));
        filterTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                filterTextFieldKeyPressed(evt);
            }
        });

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        deviceTree.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        deviceTree.setName("deviceTree"); // NOI18N
        deviceTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                deviceTreeMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                deviceTreeMouseReleased(evt);
            }
        });
        deviceTree.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                deviceTreeComponentShown(evt);
            }
        });
        deviceTree.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                deviceTreeMouseDragged(evt);
            }
        });
        jScrollPane1.setViewportView(deviceTree);

        filterButton.setFont(new java.awt.Font("Dialog", 0, 12));
        filterButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/filter.png"))); // NOI18N
        filterButton.setText(bundle.getString("global.Filter")); // NOI18N
        filterButton.setMinimumSize(new java.awt.Dimension(81, 28));
        filterButton.setName("filterButton"); // NOI18N
        filterButton.setPreferredSize(new java.awt.Dimension(81, 28));
        filterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterButtonActionPerformed(evt);
            }
        });

        mountToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/device_not_mounted.png"))); // NOI18N
        mountToggleButton.setToolTipText(bundle.getString("DevicePanel.mountToggleButton.toolTipText")); // NOI18N
        mountToggleButton.setName("mountToggleButton"); // NOI18N
        mountToggleButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/device_mounted.png"))); // NOI18N
        mountToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mountToggleButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout deviceContentPanelLayout = new javax.swing.GroupLayout(deviceContentPanel);
        deviceContentPanel.setLayout(deviceContentPanelLayout);
        deviceContentPanelLayout.setHorizontalGroup(
            deviceContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, deviceContentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(deviceContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
                    .addGroup(deviceContentPanelLayout.createSequentialGroup()
                        .addComponent(deviceActionProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mountToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, deviceContentPanelLayout.createSequentialGroup()
                        .addComponent(clearButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(filterTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(filterButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(viewComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, 356, Short.MAX_VALUE))
                .addContainerGap())
        );
        deviceContentPanelLayout.setVerticalGroup(
            deviceContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(deviceContentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(deviceContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(deviceContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(filterButton, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(filterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(clearButton, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(viewComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 672, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(deviceContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(mountToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deviceActionProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        deviceContentPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {filterButton, filterTextField});

        transferPanel.setName("transferPanel"); // NOI18N

        jPanel1.setAlignmentX(0.06410257F);
        jPanel1.setName("jPanel1"); // NOI18N

        exportLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        exportLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/export.png"))); // NOI18N
        exportLabel.setText(bundle.getString("DevicePanel.Exporting")); // NOI18N
        exportLabel.setName("exportLabel"); // NOI18N

        exportProgressBar.setName("exportProgressBar"); // NOI18N

        deleteLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        deleteLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/remove.png"))); // NOI18N
        deleteLabel.setText(bundle.getString("DevicePanel.Deleting")); // NOI18N
        deleteLabel.setName("deleteLabel"); // NOI18N

        deleteProgressBar.setName("deleteProgressBar"); // NOI18N

        importLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        importLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/import.png"))); // NOI18N
        importLabel.setText(bundle.getString("DevicePanel.Importing")); // NOI18N
        importLabel.setName("importLabel"); // NOI18N

        importProgressBar.setName("importProgressBar"); // NOI18N

        decodeLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        decodeLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/run.png"))); // NOI18N
        decodeLabel.setText(bundle.getString("DevicePanel.Decoding")); // NOI18N
        decodeLabel.setName("decodeLabel"); // NOI18N

        decodeProgressBar.setName("decodeProgressBar"); // NOI18N

        encodeLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        encodeLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/run.png"))); // NOI18N
        encodeLabel.setText(bundle.getString("DevicePanel.Encoding")); // NOI18N
        encodeLabel.setName("encodeLabel"); // NOI18N

        encodeProgressBar.setName("encodeProgressBar"); // NOI18N

        updateLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        updateLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/load.png"))); // NOI18N
        updateLabel.setText(bundle.getString("DevicePanel.Updating_database")); // NOI18N
        updateLabel.setName("updateLabel"); // NOI18N

        updateProgressBar.setName("updateProgressBar"); // NOI18N

        spacerPanel.setName("spacerPanel"); // NOI18N

        javax.swing.GroupLayout spacerPanelLayout = new javax.swing.GroupLayout(spacerPanel);
        spacerPanel.setLayout(spacerPanelLayout);
        spacerPanelLayout.setHorizontalGroup(
            spacerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 265, Short.MAX_VALUE)
        );
        spacerPanelLayout.setVerticalGroup(
            spacerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 155, Short.MAX_VALUE)
        );

        fileLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        fileLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/file.png"))); // NOI18N
        fileLabel.setText(bundle.getString("DevicePanel.File_in_progress:")); // NOI18N
        fileLabel.setName("fileLabel"); // NOI18N

        fileProgressBar.setName("fileProgressBar"); // NOI18N

        decodedFileLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        decodedFileLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/file.png"))); // NOI18N
        decodedFileLabel.setText(bundle.getString("DevicePanel.File_currently_decoded")); // NOI18N
        decodedFileLabel.setName("decodedFileLabel"); // NOI18N

        decodedFileProgressBar.setName("decodedFileProgressBar"); // NOI18N

        encodedFileLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        encodedFileLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/file.png"))); // NOI18N
        encodedFileLabel.setText(bundle.getString("DevicePanel.File_currently_encoded")); // NOI18N
        encodedFileLabel.setName("encodedFileLabel"); // NOI18N

        encodedFileProgressBar.setName("encodedFileProgressBar"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spacerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(exportLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                    .addComponent(exportProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                    .addComponent(deleteLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                    .addComponent(deleteProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                    .addComponent(importLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                    .addComponent(importProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                    .addComponent(decodeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                    .addComponent(decodeProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                    .addComponent(encodeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                    .addComponent(encodeProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                    .addComponent(updateLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                    .addComponent(updateProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                    .addComponent(fileLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                    .addComponent(fileProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                    .addComponent(decodedFileLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                    .addComponent(decodedFileProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                    .addComponent(encodedFileLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                    .addComponent(encodedFileProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(exportLabel)
                .addGap(7, 7, 7)
                .addComponent(exportProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteLabel)
                .addGap(7, 7, 7)
                .addComponent(deleteProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(importLabel)
                .addGap(7, 7, 7)
                .addComponent(importProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(decodeLabel)
                .addGap(7, 7, 7)
                .addComponent(decodeProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(encodeLabel)
                .addGap(7, 7, 7)
                .addComponent(encodeProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(updateLabel)
                .addGap(7, 7, 7)
                .addComponent(updateProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fileLabel)
                .addGap(7, 7, 7)
                .addComponent(fileProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(decodedFileLabel)
                .addGap(7, 7, 7)
                .addComponent(decodedFileProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(encodedFileLabel)
                .addGap(7, 7, 7)
                .addComponent(encodedFileProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spacerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        currentFileLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        currentFileLabel.setText(bundle.getString("DevicePanel.currentFileLabel.text")); // NOI18N
        currentFileLabel.setName("currentFileLabel"); // NOI18N

        transferProgressBar.setName("transferProgressBar"); // NOI18N

        closeButton.setFont(new java.awt.Font("Dialog", 0, 12));
        closeButton.setText(bundle.getString("global.Close")); // NOI18N
        closeButton.setName("closeButton"); // NOI18N
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout transferPanelLayout = new javax.swing.GroupLayout(transferPanel);
        transferPanel.setLayout(transferPanelLayout);
        transferPanelLayout.setHorizontalGroup(
            transferPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, transferPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(transferPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(currentFileLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
                    .addComponent(transferProgressBar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
                    .addComponent(closeButton))
                .addContainerGap())
        );
        transferPanelLayout.setVerticalGroup(
            transferPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(transferPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(currentFileLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(transferProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(closeButton)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(deviceContentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(transferPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(transferPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(deviceContentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void filterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterButtonActionPerformed
        reloadTree();
    }//GEN-LAST:event_filterButtonActionPerformed

    private void viewComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewComboBoxActionPerformed
        getManager().refreshDeviceTree(viewComboBox.getSelectedIndex(), filterTextField.getText());
    }//GEN-LAST:event_viewComboBoxActionPerformed

    private void deviceTreeComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_deviceTreeComponentShown
        
    }//GEN-LAST:event_deviceTreeComponentShown

    private void deviceTreeMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deviceTreeMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_deviceTreeMouseDragged

    private void filterTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filterTextFieldKeyPressed
        if(evt.getKeyCode() == 10){
           filterButton.doClick();
        }
    }//GEN-LAST:event_filterTextFieldKeyPressed

    private void deviceTreeMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deviceTreeMouseReleased
    //   deviceTreeMousePressed(evt); //for cross-platform compatibility
    }//GEN-LAST:event_deviceTreeMouseReleased

    private void deviceTreeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deviceTreeMousePressed
       // deviceTree.setSelectionPath(deviceTree.getPathForLocation(evt.getX(), evt.getY()));
        if ( evt.isPopupTrigger()) {
            getTreePopUp().setLocation(evt.getXOnScreen(), evt.getYOnScreen());
            getTreePopUp().setVisible(true);
        }else{
            getTreePopUp().setVisible(false);
        }
    }//GEN-LAST:event_deviceTreeMousePressed

private void mountToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mountToggleButtonActionPerformed
   mountDevice(mountToggleButton.isSelected());
}//GEN-LAST:event_mountToggleButtonActionPerformed

private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
    filterTextField.setText("");
    reloadTree();
}//GEN-LAST:event_clearButtonActionPerformed

private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
    transferPanel.setVisible(false);
    deviceContentPanel.setVisible(true);
    this.reloadTree();
}//GEN-LAST:event_closeButtonActionPerformed
    
    public void displayDeviceSpace() {
        if(getManager() != null){
            deviceActionProgressBar.setString(getManager().getSpaceLeftInText());
            deviceActionProgressBar.setValue(getManager().getSpaceLeftInRatio());
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton clearButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel currentFileLabel;
    private javax.swing.JLabel decodeLabel;
    private javax.swing.JProgressBar decodeProgressBar;
    private javax.swing.JLabel decodedFileLabel;
    private javax.swing.JProgressBar decodedFileProgressBar;
    private javax.swing.JLabel deleteLabel;
    private javax.swing.JProgressBar deleteProgressBar;
    private javax.swing.JProgressBar deviceActionProgressBar;
    private javax.swing.JPanel deviceContentPanel;
    private javax.swing.JTree deviceTree;
    private javax.swing.JLabel encodeLabel;
    private javax.swing.JProgressBar encodeProgressBar;
    private javax.swing.JLabel encodedFileLabel;
    private javax.swing.JProgressBar encodedFileProgressBar;
    private javax.swing.JLabel exportLabel;
    private javax.swing.JProgressBar exportProgressBar;
    private javax.swing.JLabel fileLabel;
    private javax.swing.JProgressBar fileProgressBar;
    private javax.swing.JButton filterButton;
    private javax.swing.JTextField filterTextField;
    private javax.swing.JLabel importLabel;
    private javax.swing.JProgressBar importProgressBar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToggleButton mountToggleButton;
    private javax.swing.JPanel spacerPanel;
    private javax.swing.JPanel transferPanel;
    private javax.swing.JProgressBar transferProgressBar;
    private javax.swing.JLabel updateLabel;
    private javax.swing.JProgressBar updateProgressBar;
    private javax.swing.JComboBox viewComboBox;
    // End of variables declaration//GEN-END:variables

    public static Logger getLogger() {
        return logger;
    }

    public static void setLogger(Logger aLogger) {
        logger = aLogger;
    }
    
    public static void setParentLogger(Logger aLogger) {
        logger.setParent(aLogger);
    }
    
    public String getDevicePath() {
        return devicePath;
    }

    public void setDevicePath(String devicePath) {
        this.devicePath = devicePath;
        getManager().setDevicePath(devicePath);
        setMountedGuiState(getManager().isOmgPathIntialized());
    }
    
    public int getDeviceGeneration() {
        return deviceGeneration;
    }

    public void setDeviceGeneration(int deviceGen) {
        this.deviceGeneration = deviceGen;
        getManager().setDeviceGeneration(deviceGen);
    }
        
    public String getDeviceName() {
        return deviceName;
    }
    
    public String getExportPath() {
        return getDeviceManager().getExportPath();
    }

    public void setExportPath(String exportPath) {
        getDeviceManager().setExportPath(exportPath);
    }

    public void setTempPath(String tempPath) {
        getDeviceManager().setTempPath(tempPath);
    }

    public boolean isAlwaysTranscode() {
        return getDeviceManager().isAlwaysTranscode();
    }

    public void setAlwaysTranscode(boolean AlwaysTranscode) {
        getDeviceManager().setAlwaysTranscode(AlwaysTranscode);
    }

    public int getTranscodeBitrate() {
        return getDeviceManager().getTranscodeBitrate();
    }

    public void setTranscodeBitrate(int TranscodeBitrate) {
        getDeviceManager().setTranscodeBitrate(TranscodeBitrate);
    }
    
    /**
     * Enable or disable GUI
     * @param enabled if true the GUI is enabled else disabled
     */
    public void enableGUI(boolean enabled){
            this.setEnabled(enabled);
    }

    public DynamicDeviceTreePopUp getTreePopUp() {
        return treePopUp;
    }

    public DeviceManager getManager() {
        return getDeviceManager();
    }
    
    public javax.swing.JProgressBar getDeviceActionProgressBar() {
        return deviceActionProgressBar;
    }

    public boolean Ismounted() {
        return getDeviceManager().isMounted();
    }
    
    // Javadoc is given in the implemented interface "NWGenericListener"
    public void transferInitialization(int numberOfExportFiles, int numberOfDeleteFiles, int numberOfDecodeFiles, int numberOfEncodeFiles, int numberOfTransferFiles, int numberOfDbFiles) {
        // Initialize progress bar values
        transferProgressValue = 0;
        exportProgressValue = 0;
        deleteProgressValue = 0;
        importProgressValue = 0;
        decodeProgressValue = 0;
        encodeProgressValue = 0;
        updateProgressValue = 0;

        // Initialize progress bars
        transferProgressBar.setValue(0);
        exportProgressBar.setValue(0);
        deleteProgressBar.setValue(0);
        importProgressBar.setValue(0);
        decodeProgressBar.setValue(0);
        encodeProgressBar.setValue(0);
        updateProgressBar.setValue(0);
        fileProgressBar.setValue(0);

        // Initialize label
        currentFileLabel.setText(java.util.ResourceBundle.getBundle("localization/language").getString("DevicePanel.Transfer_in_progress"));
        exportLabel.setText(java.util.ResourceBundle.getBundle("localization/language").getString("DevicePanel.Exporting"));
        deleteLabel.setText(java.util.ResourceBundle.getBundle("localization/language").getString("DevicePanel.Deleting"));
        importLabel.setText(java.util.ResourceBundle.getBundle("localization/language").getString("DevicePanel.Importing"));
        decodeLabel.setText(java.util.ResourceBundle.getBundle("localization/language").getString("DevicePanel.Decoding"));
        encodeLabel.setText(java.util.ResourceBundle.getBundle("localization/language").getString("DevicePanel.Encoding"));
        updateLabel.setText(java.util.ResourceBundle.getBundle("localization/language").getString("DevicePanel.Updating_database"));

        // Set the max values of the progress bars
        exportProgressBar.setMaximum(numberOfExportFiles + 1); // "+1" is used to not have a full progress bar when the last file is in progress
        deleteProgressBar.setMaximum(numberOfDeleteFiles + 1);
        importProgressBar.setMaximum(numberOfDecodeFiles*3 + numberOfEncodeFiles*2 + numberOfTransferFiles + 1); // Files to decode counts three times: once to decode, once to encode and once to transfer
        decodeProgressBar.setMaximum(numberOfDecodeFiles + 1);
        encodeProgressBar.setMaximum(numberOfEncodeFiles + 1);
        updateProgressBar.setMaximum(numberOfDbFiles + 1);
        transferProgressBar.setMaximum(exportProgressBar.getMaximum() + deleteProgressBar.getMaximum() + importProgressBar.getMaximum() + decodeProgressBar.getMaximum() + encodeProgressBar.getMaximum() + transferProgressBar.getMaximum() + - 3); // as we add 1 unity in each 5 bars (update bar doesn't count here, add 1 to consider it as a single file), to have +1 on this one, we should substract 3
        fileProgressBar.setMaximum(100);

        // Show and hide component for startup
        initTransferFrame();
    }

    // Javadoc is given in the implemented interface "NWGenericListener"
    public void transferTermination() {
        closeButton.setEnabled(true);
        transferProgressBar.setValue(transferProgressBar.getMaximum());
        currentFileLabel.setText(java.util.ResourceBundle.getBundle("localization/language").getString("DevicePanel.All_finished"));
    }

    // Javadoc is given in the implemented interface "NWGenericListener"
    public void transferStepStarted(int step) {
        switch(step){
            case NWGenericListener.EXPORTING:
                exportLabel.setVisible(true);
                exportProgressBar.setVisible(true);
                break;
            case NWGenericListener.IMPORTING:
                importLabel.setVisible(true);
                importProgressBar.setVisible(true);
                break;
            case NWGenericListener.DELETING:
                deleteLabel.setVisible(true);
                deleteProgressBar.setVisible(true);
                break;
            case NWGenericListener.ENCODING:
                encodeLabel.setVisible(true);
                encodeProgressBar.setVisible(true);
                break;
            case NWGenericListener.DECODING:
                decodeLabel.setVisible(true);
                decodeProgressBar.setVisible(true);
                break;
            case NWGenericListener.UPDATING:
                updateLabel.setVisible(true);
                updateProgressBar.setVisible(true);
                break;
        }
    }
    
    // Javadoc is given in the implemented interface "NWGenericListener"
    public void transferStepFinished(int step, int success) {
        switch(step){
            case NWGenericListener.EXPORTING:
                exportProgressBar.setValue(exportProgressBar.getMaximum());
                if(success != NWGenericListener.EXPORTATION_ERROR){
                    exportLabel.setText(java.util.ResourceBundle.getBundle("localization/language").getString("DevicePanel.Export_finished"));
                    exportLabel.setVisible(false);
                    exportProgressBar.setVisible(false);
                }
                else{
                    exportLabel.setText(java.util.ResourceBundle.getBundle("localization/language").getString("DevicePanel.Export_finished_with_errors"));
                    exportProgressBar.setVisible(false);
                }
                break;
            case NWGenericListener.IMPORTING:
                importProgressBar.setValue(importProgressBar.getMaximum());
                if(success != NWGenericListener.IMPORTING_ERROR){
                    importLabel.setText(java.util.ResourceBundle.getBundle("localization/language").getString("DevicePanel.Import_finished"));
                    importLabel.setVisible(false);
                    importProgressBar.setVisible(false);
                }
                else{
                    importLabel.setText(java.util.ResourceBundle.getBundle("localization/language").getString("DevicePanel.Import_finished_with_errors"));
                    importProgressBar.setVisible(false);
                }
                break;
            case NWGenericListener.DELETING:
                deleteProgressBar.setValue(deleteProgressBar.getMaximum());
                if(success != NWGenericListener.DELETING_ERROR){
                    deleteLabel.setText(java.util.ResourceBundle.getBundle("localization/language").getString("DevicePanel.Deleting_finished"));
                    deleteLabel.setVisible(false);
                    deleteProgressBar.setVisible(false);
                }
                else{
                    deleteLabel.setText(java.util.ResourceBundle.getBundle("localization/language").getString("DevicePanel.Deleting_finished_with_errors"));
                    deleteProgressBar.setVisible(false);
                }
                break;
            case NWGenericListener.ENCODING:
                encodeProgressBar.setValue(encodeProgressBar.getMaximum());
                if(success != NWGenericListener.ENCODING_ERROR){
                    encodeLabel.setText(java.util.ResourceBundle.getBundle("localization/language").getString("DevicePanel.Encoding_finished"));
                    encodeLabel.setVisible(false);
                    encodeProgressBar.setVisible(false);
                }
                else{
                    encodeLabel.setText(java.util.ResourceBundle.getBundle("localization/language").getString("DevicePanel.Encoding_finished_with_errors"));
                    encodeProgressBar.setVisible(false);
                }
                break;
            case NWGenericListener.DECODING:
                decodeProgressBar.setValue(decodeProgressBar.getMaximum());
                if(success != NWGenericListener.DECODING_ERROR){
                    decodeLabel.setText(java.util.ResourceBundle.getBundle("localization/language").getString("DevicePanel.Decoding_finished"));
                    decodeLabel.setVisible(false);
                    decodeProgressBar.setVisible(false);
                }
                else{
                    decodeLabel.setText(java.util.ResourceBundle.getBundle("localization/language").getString("DevicePanel.Decoding_finished_with_errors"));
                    decodeProgressBar.setVisible(false);
                }
                break;
            case NWGenericListener.UPDATING:
                updateProgressBar.setValue(updateProgressBar.getMaximum());
                if(success == NWGenericListener.NO_ERROR){
                    updateLabel.setText(java.util.ResourceBundle.getBundle("localization/language").getString("DevicePanel.Database_is_up_to_date"));
                    updateLabel.setVisible(false);
                    updateProgressBar.setVisible(false);
                }
                else{
                    updateLabel.setText(java.util.ResourceBundle.getBundle("localization/language").getString("DevicePanel.Database_has_not_been_correctly_created"));
                    updateProgressBar.setVisible(false);
                }
                break;
        }
    }

    /**
    * This method shortens text to wisth of the control
    * @param text
    * @param label
    * @return shortened text
    */
    private String wrapText(String text, JLabel label){
        Boolean hasBeenChanged = false;
        while (label.getWidth() < label.getFontMetrics(label.getFont()).stringWidth(text + "...")){
            if(text.length()>3){
                text = (String) text.subSequence(0, text.length()-3);
                hasBeenChanged = true;
            }else{
                break;
            }
        }
        if (hasBeenChanged)
            return text + "...";
        else 
            return text;
    }

    // Javadoc is given in the implemented interface "NWGenericListener"
    public void fileChanged(int step, String name) {
        // Update the overall progress bar (except in update stage)
        if(step != NWGenericListener.UPDATING) {
            transferProgressBar.setValue(transferProgressValue);
            transferProgressValue++; // Increase value for next file
        }

        // Update file in progress field (this step is different for decode and encode step since they have their own progress bars)
        switch(step){
            case NWGenericListener.EXPORTING:
            case NWGenericListener.DELETING:
            case NWGenericListener.IMPORTING:
            case NWGenericListener.UPDATING:
                // Update the file name
                fileLabel.setText(wrapText(java.util.ResourceBundle.getBundle("localization/language").getString("DevicePanel.File_in_progress:") + " " + name,fileLabel));
                // Reset the file progress bar
                fileProgressBar.setValue(0);
                // Show label and progress bar
                fileLabel.setVisible(true);
                fileProgressBar.setVisible(true);
                break;
            case NWGenericListener.DECODING:
                // Update the file name
                decodedFileLabel.setText(wrapText(java.util.ResourceBundle.getBundle("localization/language").getString("DevicePanel.File_currently_decoded") + " "+ name,fileLabel));
                // Reset the file progress bar
                decodedFileProgressBar.setValue(0);
                // Show label and progress bar
                decodedFileLabel.setVisible(true);
                decodedFileProgressBar.setVisible(true);
                break;
            case NWGenericListener.ENCODING:
                // Update the file name
                encodedFileLabel.setText(wrapText(java.util.ResourceBundle.getBundle("localization/language").getString("DevicePanel.File_currently_encoded") + " "+ name,fileLabel));
                // Reset the file progress bar
                encodedFileProgressBar.setValue(0);
                // Show label and progress bar
                encodedFileLabel.setVisible(true);
                encodedFileProgressBar.setVisible(true);
                break;
        }

        // Search for the particular bar to be increased
        switch(step){
            case NWGenericListener.EXPORTING:
                exportProgressBar.setValue(exportProgressValue);
                exportProgressValue++;
                break;
            case NWGenericListener.DELETING:
                deleteProgressBar.setValue(deleteProgressValue);
                deleteProgressValue++;
                break;
            case NWGenericListener.IMPORTING:
                importProgressBar.setValue(importProgressValue);
                importProgressValue++;
                break;
            case NWGenericListener.DECODING:
                decodeProgressBar.setValue(decodeProgressValue);
                decodeProgressValue++;
                importProgressBar.setValue(importProgressValue);
                importProgressValue++;
                break;
            case NWGenericListener.ENCODING:
                encodeProgressBar.setValue(encodeProgressValue);
                encodeProgressValue++;
                importProgressBar.setValue(importProgressValue);
                importProgressValue++;
                break;
            case NWGenericListener.UPDATING:
                updateProgressBar.setValue(updateProgressValue);
                updateProgressValue++;
                break;
        }
    }

    // Javadoc is given in the implemented interface "NWGenericListener"
    public void fileProgressChanged(int step, double value, double speed) {
        // The progress bar to be updated depends on the step
        switch(step){
            case NWGenericListener.EXPORTING:
            case NWGenericListener.DELETING:
            case NWGenericListener.IMPORTING:
            case NWGenericListener.UPDATING:
                // Only update the progress bar if the task is not finished (<99.9%)
                if(value < 99.9) {
                    // Update the value of the progress bar
                    fileProgressBar.setValue((int) value);
                    // Update the speed of the progress bar
                    if(speed > 0) { fileProgressBar.setString(((int) speed) + " kb/s"); }
                    else { fileProgressBar.setString(" "); }
                }
                else {
                    // Else, hide the progress bar and the corresponding label
                    fileLabel.setVisible(false);
                    fileProgressBar.setVisible(false);
                }
                break;
            case NWGenericListener.DECODING:
                // Only update the progress bar if the task is not finished (<99.9%)
                if(value < 99.9) {
                    // Update the value of the progress bar
                    decodedFileProgressBar.setValue((int) value);
                    // Update the speed of the progress bar
                    if(speed > 0) {decodedFileProgressBar.setString(((int) speed) + " kb/s");}
                    else { decodedFileProgressBar.setString(" ");}
                }
                else {
                    // Else, hide the progress bar and the corresponding label
                    decodedFileLabel.setVisible(false);
                    decodedFileProgressBar.setVisible(false);
                }
                break;
            case NWGenericListener.ENCODING:
                // Only update the progress bar if the task is not finished (<99.9%)
                if(value < 99.9) {
                    // Update the value of the progress bar
                    encodedFileProgressBar.setValue((int) value);
                    // Update the speed of the progress bar
                    if(speed > 0) { encodedFileProgressBar.setString(((int) speed) + " kb/s");}
                    else {encodedFileProgressBar.setString(" ");}
                }
                else {
                    // Else, hide the progress bar and the corresponding label
                    encodedFileLabel.setVisible(false);
                    encodedFileProgressBar.setVisible(false);
                }
                break;
        }
    }

    // Javadoc is given in the implemented interface "NWGenericListener"
    public void loadingProgresChanged(double value) {
        deviceActionProgressBar.setValue((int) value);
        
        // If the loading is over, show the tree
        if(value >= deviceActionProgressBar.getMaximum()) {
            deviceTree.setVisible(true);
            deviceActionProgressBar.setMaximum(100);
        }
    }

    // Javadoc is given in the implemented interface "NWGenericListener"
    public void loadingInitialization(int numberOfFile) {
        // Hide the device tree
        deviceTree.setVisible(false);

        // Set the maximum of the bar
        deviceActionProgressBar.setMaximum(numberOfFile);
    }

    public DeviceManager getDeviceManager() {
        return deviceManager;
    }
}
