/*  Copyright (C) 2008 Daniel Žalar (danizmax@gmail.com)
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
 * JSymphonicProperties.java
 *
 * Created on April 24, 2008, 6:41 PM
 */

package org.danizmax.jsymphonic.gui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.danizmax.jsymphonic.toolkit.ProfileElement;
import org.danizmax.jsymphonic.gui.device.DeviceManager;
import org.naurd.media.jsymphonic.toolBox.FFMpegToolBox;

/**
 * This class is a JFrame for changing JSymphonic settings
 * @author  danizmax - Daniel Žalar (danizmax@gmail.com)
 */
public class JSymphonicProperties extends javax.swing.JFrame {
    
    private SettingsHandler sHandler = null;
    private TreeMap themeMap = null;
    private HashMap profiles = null;
    private JSymphonicWindow mainWindow = null;
    private HashMap generationMap = new HashMap();
    
        
    
    /** Creates new form JSymphonicProperties */
    public JSymphonicProperties(SettingsHandler sh, JSymphonicWindow mainWindow) {
        
        //device generations
        generationMap.put(DeviceManager.Generation1, java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicProfilesDialog.generation1"));
        generationMap.put(DeviceManager.Generation2, java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicProfilesDialog.generation2"));
        generationMap.put(DeviceManager.Generation3, java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicProfilesDialog.generation3"));
        generationMap.put(DeviceManager.Generation4, java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicProfilesDialog.generation4"));
        generationMap.put(DeviceManager.Generation5, java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicProfilesDialog.generation5"));
        generationMap.put(DeviceManager.Generation6, java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicProfilesDialog.generation6"));
        generationMap.put(DeviceManager.Generation7, java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicProfilesDialog.generation7"));
        // Note: the list of generation name also appears in "JSymphonicProfilesDialog" class
    
        sHandler = sh;
        this.mainWindow = mainWindow;
        initComponents();
        initConstantValues();
        //load profiles
        profiles = sHandler.getProfiles();
        loadProfiles();
     //   updateSelectedProfileData(profiles, sHandler.getSelectedProfile());
        profileComboBox.setSelectedItem(sHandler.getSelectedProfile());
        
        //gui settings
        themeComboBox.setSelectedItem(sHandler.getTheme());
        languageComboBox.setSelectedItem(sHandler.getLanguage());
        logLevelComboBox.setSelectedItem(sHandler.getLogLevel());
        logPanelCheckBox.setSelected(sHandler.isLogToFile());
        
        //transfer
        bitrateComboBox.setSelectedItem(sHandler.getBitrate() + " kbps");
        alwaysTranscodeRadioButton.setSelected(sHandler.isAlwaysTrascode());
        alwaysTranscodeRadioButton.setEnabled(FFMpegToolBox.isFFMpegPresent());
        onlyNeededTranscodeRadioButton.setSelected(!sHandler.isAlwaysTrascode());
        readTagsRadioButton.setSelected(sHandler.isReadID3Tags());
        neverReadTagsRadioButton.setSelected(!sHandler.isReadID3Tags());
        tagReadStructureComboBox.setSelectedItem(sHandler.getReadTagFolderStructure());
        tagReadStructureComboBox.setEnabled(!sHandler.isReadID3Tags());
        
    }

    private void ShowSelectedProfileData(HashMap profiles, String selectedProfile) {
        if(profiles.containsKey(selectedProfile)){ 
            if(profiles != null && selectedProfile != null){
                ProfileElement sel = (ProfileElement) profiles.get(selectedProfile);
                if(sel != null){
                    localPathLabelData.setText(sel.getLocalPath());
                    exportPathLabelData.setText(sel.getExportPath());
                    devPathLabelData.setText(sel.getDevicePath());
                    genPathLabelData.setText((String) generationMap.get(sel.getDeviceGeneration()));
                    if(sel.isTempPathSameAsDevicePath()){
                        tempPathLabelData.setText(sel.getDevicePath());
                    }else{
                        tempPathLabelData.setText(sel.getTranscodeTempPath());
                    }
                }
            }
        }
    }

    
    /**
     * Loasd profiles into profiles combobox
     */
    private void loadProfiles(){
        Set keys = profiles.keySet();
        Iterator iter = keys.iterator();
        profileComboBox.removeAllItems();
        
        while (iter.hasNext()) {
            profileComboBox.addItem((String)iter.next());
        }
        profileComboBox.setSelectedItem(((ProfileElement) profiles.get(sHandler.getSelectedProfile())).getProfileName());
    }
          
    /***
     * This method populates comboboxes that do not change and creates Hasmaps for usage
     */
    private void initConstantValues(){
        themeComboBox.removeAllItems();
        languageComboBox.removeAllItems();
        
        //language
        TreeMap langs = mainWindow.getLangMap();
        Set keys = langs.keySet();
        Iterator iter = keys.iterator();
        languageComboBox.removeAllItems();
        while (iter.hasNext()){
            languageComboBox.addItem(iter.next());
        }
        
        //themes
        themeMap = mainWindow.getThemeMap();
        keys = themeMap.keySet();
        iter = keys.iterator();
           
        while (iter.hasNext()){
            themeComboBox.addItem(iter.next());
        }
        
        tagReadStructureComboBox.addItem(java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicProperties.Artist/Year-Album/TrackNumber-Title"));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        transcodebuttonGroup = new javax.swing.ButtonGroup();
        tagUtilGroup = new javax.swing.ButtonGroup();
        cancelButton = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        pathPanel = new javax.swing.JPanel();
        profileComboBox = new javax.swing.JComboBox();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        profileDataPanel = new javax.swing.JPanel();
        devPathLabel = new javax.swing.JLabel();
        localPathLabel = new javax.swing.JLabel();
        exportPathLabel = new javax.swing.JLabel();
        tempPathLabel = new javax.swing.JLabel();
        genPathLabel = new javax.swing.JLabel();
        genPathLabelData = new javax.swing.JLabel();
        tempPathLabelData = new javax.swing.JLabel();
        exportPathLabelData = new javax.swing.JLabel();
        localPathLabelData = new javax.swing.JLabel();
        devPathLabelData = new javax.swing.JLabel();
        editButton = new javax.swing.JButton();
        noteProfileLabel = new javax.swing.JLabel();
        interfacePanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        languageComboBox = new javax.swing.JComboBox();
        noteLangLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        themeComboBox = new javax.swing.JComboBox();
        logLevelPanel = new javax.swing.JPanel();
        logLevelComboBox = new javax.swing.JComboBox();
        logPanelCheckBox = new javax.swing.JCheckBox();
        transferPanel = new javax.swing.JPanel();
        mp3OptionsPanel = new javax.swing.JPanel();
        jPanelSpacer8 = new javax.swing.JPanel();
        bitrateLabel = new javax.swing.JLabel();
        bitrateComboBox = new javax.swing.JComboBox();
        onlyNeededTranscodeRadioButton = new javax.swing.JRadioButton();
        alwaysTranscodeRadioButton = new javax.swing.JRadioButton();
        tagutilPanel = new javax.swing.JPanel();
        readTagsRadioButton = new javax.swing.JRadioButton();
        neverReadTagsRadioButton = new javax.swing.JRadioButton();
        tagReadStructureComboBox = new javax.swing.JComboBox();
        applyButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("localization/language"); // NOI18N
        setTitle(bundle.getString("JSymphonicProperties.title")); // NOI18N
        setName("Form"); // NOI18N
        setResizable(false);

        cancelButton.setFont(new java.awt.Font("Dialog", 0, 12));
        cancelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/cancel.png"))); // NOI18N
        cancelButton.setText(bundle.getString("global.Cancel")); // NOI18N
        cancelButton.setName("cancelButton"); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane1.setFont(new java.awt.Font("Dialog", 0, 12));
        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        pathPanel.setName("pathPanel"); // NOI18N

        profileComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        profileComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        profileComboBox.setName("profileComboBox"); // NOI18N
        profileComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                profileComboBoxActionPerformed(evt);
            }
        });

        addButton.setFont(new java.awt.Font("Dialog", 0, 12));
        addButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/add.png"))); // NOI18N
        addButton.setText(bundle.getString("JSymphonicProperties.addButton.text")); // NOI18N
        addButton.setName("addButton"); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        removeButton.setFont(new java.awt.Font("Dialog", 0, 12));
        removeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/remove.png"))); // NOI18N
        removeButton.setText(bundle.getString("JSymphonicProperties.removeButton.text")); // NOI18N
        removeButton.setName("removeButton"); // NOI18N
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        profileDataPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        profileDataPanel.setName("profileDataPanel"); // NOI18N

        devPathLabel.setText(bundle.getString("JSymphonicProperties.devPathLabel.text")); // NOI18N
        devPathLabel.setName("devPathLabel"); // NOI18N

        localPathLabel.setText(bundle.getString("JSymphonicProperties.localPathLabel.text")); // NOI18N
        localPathLabel.setName("localPathLabel"); // NOI18N

        exportPathLabel.setText(bundle.getString("JSymphonicProperties.exportPathLabel.text")); // NOI18N
        exportPathLabel.setName("exportPathLabel"); // NOI18N

        tempPathLabel.setText(bundle.getString("JSymphonicProperties.tempPathLabel.text")); // NOI18N
        tempPathLabel.setName("tempPathLabel"); // NOI18N

        genPathLabel.setText(bundle.getString("JSymphonicProperties.genPathLabel.text")); // NOI18N
        genPathLabel.setName("genPathLabel"); // NOI18N

        genPathLabelData.setFont(new java.awt.Font("Dialog", 0, 12));
        genPathLabelData.setText(bundle.getString("JSymphonicProperties.none")); // NOI18N
        genPathLabelData.setName("genPathLabelData"); // NOI18N

        tempPathLabelData.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        tempPathLabelData.setText(bundle.getString("JSymphonicProperties.none")); // NOI18N
        tempPathLabelData.setName("tempPathLabelData"); // NOI18N

        exportPathLabelData.setFont(new java.awt.Font("Dialog", 0, 12));
        exportPathLabelData.setText(bundle.getString("JSymphonicProperties.none")); // NOI18N
        exportPathLabelData.setName("exportPathLabelData"); // NOI18N

        localPathLabelData.setFont(new java.awt.Font("Dialog", 0, 12));
        localPathLabelData.setText(bundle.getString("JSymphonicProperties.none")); // NOI18N
        localPathLabelData.setName("localPathLabelData"); // NOI18N

        devPathLabelData.setFont(new java.awt.Font("Dialog", 0, 12));
        devPathLabelData.setText(bundle.getString("JSymphonicProperties.none")); // NOI18N
        devPathLabelData.setName("devPathLabelData"); // NOI18N

        javax.swing.GroupLayout profileDataPanelLayout = new javax.swing.GroupLayout(profileDataPanel);
        profileDataPanel.setLayout(profileDataPanelLayout);
        profileDataPanelLayout.setHorizontalGroup(
            profileDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(profileDataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(profileDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(genPathLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tempPathLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(exportPathLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(localPathLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(devPathLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(profileDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(localPathLabelData, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                    .addComponent(exportPathLabelData, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                    .addComponent(genPathLabelData, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                    .addComponent(tempPathLabelData, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                    .addComponent(devPathLabelData, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        profileDataPanelLayout.setVerticalGroup(
            profileDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(profileDataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(profileDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(devPathLabel)
                    .addComponent(devPathLabelData))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(profileDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(localPathLabel)
                    .addComponent(localPathLabelData))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(profileDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(exportPathLabel)
                    .addComponent(exportPathLabelData))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(profileDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tempPathLabel)
                    .addComponent(tempPathLabelData))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(profileDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(genPathLabel)
                    .addComponent(genPathLabelData))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        editButton.setFont(new java.awt.Font("Dialog", 0, 12));
        editButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/edit.png"))); // NOI18N
        editButton.setText(bundle.getString("JSymphonicProperties.editButton.text")); // NOI18N
        editButton.setName("editButton"); // NOI18N
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        noteProfileLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        noteProfileLabel.setText(bundle.getString("JSymphonicProperties.noteProfileLabel.text")); // NOI18N
        noteProfileLabel.setName("noteProfileLabel"); // NOI18N

        javax.swing.GroupLayout pathPanelLayout = new javax.swing.GroupLayout(pathPanel);
        pathPanel.setLayout(pathPanelLayout);
        pathPanelLayout.setHorizontalGroup(
            pathPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pathPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pathPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(profileComboBox, 0, 625, Short.MAX_VALUE)
                    .addComponent(noteProfileLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 471, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pathPanelLayout.createSequentialGroup()
                        .addComponent(removeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editButton))
                    .addComponent(profileDataPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 625, Short.MAX_VALUE))
                .addContainerGap())
        );

        pathPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {addButton, editButton, removeButton});

        pathPanelLayout.setVerticalGroup(
            pathPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pathPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(profileComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(profileDataPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pathPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editButton)
                    .addComponent(addButton)
                    .addComponent(removeButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addComponent(noteProfileLabel)
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("JSymphonicProperties.pathPanel.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/device.png")), pathPanel); // NOI18N

        interfacePanel.setName("interfacePanel"); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("JSymphonicProperties.jPanel2.border.title"))); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N

        languageComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        languageComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        languageComboBox.setName("languageComboBox"); // NOI18N

        noteLangLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        noteLangLabel.setText(bundle.getString("JSymphonicProperties.noteLangLabel.text")); // NOI18N
        noteLangLabel.setName("noteLangLabel"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(noteLangLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 561, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(languageComboBox, 0, 591, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(languageComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addComponent(noteLangLabel))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("JSymphonicProperties.jPanel3.border.title"))); // NOI18N
        jPanel3.setName("jPanel3"); // NOI18N

        themeComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        themeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        themeComboBox.setName("themeComboBox"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(themeComboBox, 0, 591, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(themeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(35, Short.MAX_VALUE))
        );

        logLevelPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("JSymphonicProperties.logLevelPanel.border.title"))); // NOI18N
        logLevelPanel.setName("logLevelPanel"); // NOI18N

        logLevelComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        logLevelComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "OFF", "SEVERE", "WARNING", "INFO", "CONFIG", "FINE", "FINER", "FINEST ", "ALL" }));
        logLevelComboBox.setName("logLevelComboBox"); // NOI18N

        logPanelCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        logPanelCheckBox.setText(bundle.getString("JSymphonicProperties.logPanelCheckBox.text")); // NOI18N
        logPanelCheckBox.setName("logPanelCheckBox"); // NOI18N

        javax.swing.GroupLayout logLevelPanelLayout = new javax.swing.GroupLayout(logLevelPanel);
        logLevelPanel.setLayout(logLevelPanelLayout);
        logLevelPanelLayout.setHorizontalGroup(
            logLevelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, logLevelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(logLevelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(logPanelCheckBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 591, Short.MAX_VALUE)
                    .addComponent(logLevelComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, 591, Short.MAX_VALUE))
                .addContainerGap())
        );
        logLevelPanelLayout.setVerticalGroup(
            logLevelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(logLevelPanelLayout.createSequentialGroup()
                .addComponent(logLevelComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addComponent(logPanelCheckBox))
        );

        javax.swing.GroupLayout interfacePanelLayout = new javax.swing.GroupLayout(interfacePanel);
        interfacePanel.setLayout(interfacePanelLayout);
        interfacePanelLayout.setHorizontalGroup(
            interfacePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, interfacePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(interfacePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(logLevelPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        interfacePanelLayout.setVerticalGroup(
            interfacePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(interfacePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logLevelPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(bundle.getString("JSymphonicProperties.interfacePanel.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/gui.png")), interfacePanel); // NOI18N

        transferPanel.setName("transferPanel"); // NOI18N

        mp3OptionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("JSymphonicProperties.mp3OptionsPanel.border.title"))); // NOI18N
        mp3OptionsPanel.setMaximumSize(new java.awt.Dimension(410, 420));
        mp3OptionsPanel.setMinimumSize(new java.awt.Dimension(410, 420));
        mp3OptionsPanel.setName("mp3OptionsPanel"); // NOI18N

        jPanelSpacer8.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelSpacer8.setName("jPanelSpacer8"); // NOI18N
        jPanelSpacer8.setLayout(new javax.swing.BoxLayout(jPanelSpacer8, javax.swing.BoxLayout.LINE_AXIS));

        bitrateLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        bitrateLabel.setText(bundle.getString("JSymphonicProperties.bitrateLabel.text")); // NOI18N
        bitrateLabel.setName("bitrateLabel"); // NOI18N

        bitrateComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        bitrateComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "64 kbps", "96 kbps", "128 kbps", "160 kbps", "192 kbps", "256 kbps", "320 kbps" }));
        bitrateComboBox.setName("bitrateComboBox"); // NOI18N
        bitrateComboBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bitrateComboBoxMouseClicked(evt);
            }
        });
        bitrateComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bitrateComboBoxActionPerformed(evt);
            }
        });

        transcodebuttonGroup.add(onlyNeededTranscodeRadioButton);
        onlyNeededTranscodeRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        onlyNeededTranscodeRadioButton.setSelected(true);
        onlyNeededTranscodeRadioButton.setText(bundle.getString("JSymphonicProperties.onlyNeededTranscodeRadioButton.text")); // NOI18N
        onlyNeededTranscodeRadioButton.setName("onlyNeededTranscodeRadioButton"); // NOI18N

        transcodebuttonGroup.add(alwaysTranscodeRadioButton);
        alwaysTranscodeRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        alwaysTranscodeRadioButton.setText(bundle.getString("JSymphonicProperties.alwaysTranscodeRadioButton.text")); // NOI18N
        alwaysTranscodeRadioButton.setName("alwaysTranscodeRadioButton"); // NOI18N

        javax.swing.GroupLayout mp3OptionsPanelLayout = new javax.swing.GroupLayout(mp3OptionsPanel);
        mp3OptionsPanel.setLayout(mp3OptionsPanelLayout);
        mp3OptionsPanelLayout.setHorizontalGroup(
            mp3OptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mp3OptionsPanelLayout.createSequentialGroup()
                .addComponent(jPanelSpacer8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mp3OptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(mp3OptionsPanelLayout.createSequentialGroup()
                        .addComponent(bitrateLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bitrateComboBox, 0, 537, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, mp3OptionsPanelLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(mp3OptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(alwaysTranscodeRadioButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 579, Short.MAX_VALUE)
                            .addComponent(onlyNeededTranscodeRadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, 579, Short.MAX_VALUE))))
                .addContainerGap())
        );
        mp3OptionsPanelLayout.setVerticalGroup(
            mp3OptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mp3OptionsPanelLayout.createSequentialGroup()
                .addGroup(mp3OptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelSpacer8, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(mp3OptionsPanelLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(mp3OptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(bitrateLabel)
                            .addComponent(bitrateComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(onlyNeededTranscodeRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(alwaysTranscodeRadioButton)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tagutilPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("JSymphonicProperties.tagutilPanel.border.title"))); // NOI18N
        tagutilPanel.setName("tagutilPanel"); // NOI18N

        tagUtilGroup.add(readTagsRadioButton);
        readTagsRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        readTagsRadioButton.setSelected(true);
        readTagsRadioButton.setText(bundle.getString("JSymphonicProperties.readTagsRadioButton.text")); // NOI18N
        readTagsRadioButton.setName("readTagsRadioButton"); // NOI18N
        readTagsRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readTagsRadioButtonActionPerformed(evt);
            }
        });

        tagUtilGroup.add(neverReadTagsRadioButton);
        neverReadTagsRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        neverReadTagsRadioButton.setText(bundle.getString("JSymphonicProperties.neverReadTagsRadioButton.text")); // NOI18N
        neverReadTagsRadioButton.setName("neverReadTagsRadioButton"); // NOI18N
        neverReadTagsRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                neverReadTagsRadioButtonActionPerformed(evt);
            }
        });

        tagReadStructureComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        tagReadStructureComboBox.setName("tagReadStructureComboBox"); // NOI18N

        javax.swing.GroupLayout tagutilPanelLayout = new javax.swing.GroupLayout(tagutilPanel);
        tagutilPanel.setLayout(tagutilPanelLayout);
        tagutilPanelLayout.setHorizontalGroup(
            tagutilPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tagutilPanelLayout.createSequentialGroup()
                .addGroup(tagutilPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tagutilPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(tagutilPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(readTagsRadioButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)
                            .addComponent(neverReadTagsRadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)))
                    .addGroup(tagutilPanelLayout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(tagReadStructureComboBox, 0, 574, Short.MAX_VALUE)))
                .addContainerGap())
        );
        tagutilPanelLayout.setVerticalGroup(
            tagutilPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tagutilPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(readTagsRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(neverReadTagsRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tagReadStructureComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(48, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout transferPanelLayout = new javax.swing.GroupLayout(transferPanel);
        transferPanel.setLayout(transferPanelLayout);
        transferPanelLayout.setHorizontalGroup(
            transferPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(transferPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(transferPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mp3OptionsPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tagutilPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        transferPanelLayout.setVerticalGroup(
            transferPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(transferPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mp3OptionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tagutilPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("JSymphonicProperties.transferPanel.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/transfer.png")), transferPanel); // NOI18N

        applyButton.setFont(new java.awt.Font("Dialog", 0, 12));
        applyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/ok.png"))); // NOI18N
        applyButton.setText(bundle.getString("global.Apply")); // NOI18N
        applyButton.setName("applyButton"); // NOI18N
        applyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 654, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(applyButton, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {applyButton, cancelButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 382, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(applyButton))
                .addContainerGap())
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed

        JSymphonicProfilesDialog pd = new JSymphonicProfilesDialog(this, generationMap);
        pd.setLocationRelativeTo(this);
        ProfileElement pe = pd.createNewProfileElement(profiles);
        if(pe != null){
          profiles.put(pe.getProfileName(), pe);
          sHandler.setProfiles(profiles);
          sHandler.writeXMLFile();
          loadProfiles();
          mainWindow.loadProfiles();
        }
            

    }//GEN-LAST:event_addButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.dispose();
}//GEN-LAST:event_cancelButtonActionPerformed

    private void profileComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_profileComboBoxActionPerformed
      ShowSelectedProfileData(profiles, (String)profileComboBox.getSelectedItem());
    }//GEN-LAST:event_profileComboBoxActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        if(profileComboBox.getEditor().getItem().equals("Default")){
            JOptionPane.showMessageDialog(this, java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicProperties.default_profile_cannot_be_removed"));
        }else{
            if(JOptionPane.showConfirmDialog(this, java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicProperties.msg01.Do_you_want_to_delete_profile_named") + " " +profileComboBox.getSelectedItem() +" " + "?" , java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicProperties.msg01.Deleting_profile..."), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                if(profiles.containsKey(profileComboBox.getSelectedItem())){
                    profiles.remove(profileComboBox.getSelectedItem());
                    sHandler.setProfiles(profiles);
                    sHandler.writeXMLFile();
                    loadProfiles();
                    mainWindow.loadProfiles();
                }
            }
        } 
    }//GEN-LAST:event_removeButtonActionPerformed

    private void bitrateComboBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bitrateComboBoxMouseClicked

}//GEN-LAST:event_bitrateComboBoxMouseClicked

    private void bitrateComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bitrateComboBoxActionPerformed

}//GEN-LAST:event_bitrateComboBoxActionPerformed

    private void applyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyButtonActionPerformed
        sHandler.setLanguage((String)languageComboBox.getSelectedItem());
        sHandler.setProfiles(profiles);
        sHandler.setLogLevel((String) logLevelComboBox.getSelectedItem());
        sHandler.setLogToFile(logPanelCheckBox.isSelected());
        String brt = (String)bitrateComboBox.getSelectedItem();
        sHandler.setBitrate( Integer.valueOf(brt.substring(0, brt.length()-5)));
        sHandler.setAlwaysTrascode(alwaysTranscodeRadioButton.isSelected());
        sHandler.setAlwaysTrascode(alwaysTranscodeRadioButton.isSelected());
        //TODO load only the first check the SAX bug possiblity! use getSelectedItem(); 
        if(profiles.containsKey((String)profileComboBox.getEditor().getItem())){ //check if the selected profile has been saved
            sHandler.setSelectedProfile((String)profileComboBox.getEditor().getItem());
        }
        sHandler.setTheme((String)themeComboBox.getSelectedItem());
        mainWindow.changeLAF((String)themeMap.get(sHandler.getTheme()));
        SwingUtilities.updateComponentTreeUI(this);

        // Start a new thread to load the config since scanning the device may take time
        Thread loadNewConfigThread = new Thread(){
            @Override
            public void run(){
                try{
                    mainWindow.loadNewConfig();
                } catch(Exception e){}
            }
        };
        loadNewConfigThread.setPriority(Thread.NORM_PRIORITY);
        loadNewConfigThread.start();
        loadNewConfigThread = null;

        sHandler.writeXMLFile();
        this.dispose();
    }//GEN-LAST:event_applyButtonActionPerformed

private void readTagsRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readTagsRadioButtonActionPerformed
    sHandler.setReadID3Tags(true);
    tagReadStructureComboBox.setEnabled(false);
}//GEN-LAST:event_readTagsRadioButtonActionPerformed

private void neverReadTagsRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_neverReadTagsRadioButtonActionPerformed
    sHandler.setReadID3Tags(false);
    tagReadStructureComboBox.setEnabled(true);
}//GEN-LAST:event_neverReadTagsRadioButtonActionPerformed

private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
    JSymphonicProfilesDialog pd = new JSymphonicProfilesDialog(this, generationMap);
    pd.setLocationRelativeTo(this);
    if(pd.updateExistingProfileElement(profiles, (String)profileComboBox.getSelectedItem())){
        sHandler.setProfiles(profiles);
        sHandler.writeXMLFile();
        loadProfiles();
        mainWindow.loadProfiles();
    }
}//GEN-LAST:event_editButtonActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JRadioButton alwaysTranscodeRadioButton;
    private javax.swing.JButton applyButton;
    private javax.swing.JComboBox bitrateComboBox;
    private javax.swing.JLabel bitrateLabel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel devPathLabel;
    private javax.swing.JLabel devPathLabelData;
    private javax.swing.JButton editButton;
    private javax.swing.JLabel exportPathLabel;
    private javax.swing.JLabel exportPathLabelData;
    private javax.swing.JLabel genPathLabel;
    private javax.swing.JLabel genPathLabelData;
    private javax.swing.JPanel interfacePanel;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelSpacer8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JComboBox languageComboBox;
    private javax.swing.JLabel localPathLabel;
    private javax.swing.JLabel localPathLabelData;
    private javax.swing.JComboBox logLevelComboBox;
    private javax.swing.JPanel logLevelPanel;
    private javax.swing.JCheckBox logPanelCheckBox;
    private javax.swing.JPanel mp3OptionsPanel;
    private javax.swing.JRadioButton neverReadTagsRadioButton;
    private javax.swing.JLabel noteLangLabel;
    private javax.swing.JLabel noteProfileLabel;
    private javax.swing.JRadioButton onlyNeededTranscodeRadioButton;
    private javax.swing.JPanel pathPanel;
    private javax.swing.JComboBox profileComboBox;
    private javax.swing.JPanel profileDataPanel;
    private javax.swing.JRadioButton readTagsRadioButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JComboBox tagReadStructureComboBox;
    private javax.swing.ButtonGroup tagUtilGroup;
    private javax.swing.JPanel tagutilPanel;
    private javax.swing.JLabel tempPathLabel;
    private javax.swing.JLabel tempPathLabelData;
    private javax.swing.JComboBox themeComboBox;
    private javax.swing.ButtonGroup transcodebuttonGroup;
    private javax.swing.JPanel transferPanel;
    // End of variables declaration//GEN-END:variables
    
}
