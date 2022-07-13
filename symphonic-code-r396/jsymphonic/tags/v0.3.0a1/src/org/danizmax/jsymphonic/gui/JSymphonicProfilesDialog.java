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
 *
 * JSymphonicProfilesDialog.java
 *
 * Created on June 8, 2008, 4:47 PM
 */

package org.danizmax.jsymphonic.gui;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import org.danizmax.jsymphonic.toolkit.ProfileElement;

/**
 * This class is used to create new profiles, it creates a dialog for editing the JSymphonic profiles.
 * @author  danizmax - Daniel Žalar (danizmax@gmail.com)
 */
public class JSymphonicProfilesDialog extends javax.swing.JDialog {

    private HashMap generationMap = null;
    private HashMap profiles = null;
    private boolean updating = false;
    private boolean ok = false;
    
    /** Creates new form JSymphonicProfilesDialog */
    public JSymphonicProfilesDialog(java.awt.Frame parent, HashMap generationMap) {
        super(parent, true);
        this.generationMap = generationMap;
        initComponents();
        initConstantValues();
    }
    
     /***
     * This method populates comboboxes that do not change and creates Hasmaps for usage
     */
    private void initConstantValues(){
        generationComboBox.removeAllItems();

        generationComboBox.addItem(java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicProfilesDialog.generation3"));
        generationComboBox.addItem(java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicProfilesDialog.generation4"));
        generationComboBox.addItem(java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicProfilesDialog.generation5"));
        generationComboBox.addItem(java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicProfilesDialog.generation6"));
        generationComboBox.addItem(java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicProfilesDialog.generation7"));
        // Note: the list of generation name also appears in "JSymphonicProperties" class
    }
    
     /**
     * Get the key for for item that is stored in HasMap as value
     * @param item item name you have
     * @return the key you want
     */
    private int getKeyfromGenerationCombo(String item){
         //find generation key
           Set keys = generationMap.keySet();
           Iterator iter = keys.iterator();
           
           while (iter.hasNext()){
                Object key = iter.next();
                Object val = generationMap.get(key); 
                if(val.equals(item)){
                    return (Integer)key;
                }
           }
           return 0;
    }

    /**
     * create new profile element
     * @param profiles
     * @return new profile element
     */
    public ProfileElement createNewProfileElement(HashMap profiles){
        if(profiles != null){
            updating = false;
            this.profiles = profiles;
            nameTextField.setEnabled(true);
            this.setVisible(true);
            if(ok){
                return new ProfileElement(nameTextField.getText(), getKeyfromGenerationCombo((String)generationComboBox.getSelectedItem()), localPathTextField.getText(), exportPathTextField.getText(), devicePathTextField.getText(), tempTextField.getText(), tempCheckBox.isSelected());
            }else{
                return null;
            }
        }
        return null;
    }
    /**
     * Update existing profile element
     * @param profiles
     * @param selectedProfile
     * @return true if anything has been updated alse false
     */
    public boolean updateExistingProfileElement(HashMap profiles, String selectedProfile){
        if(profiles != null){
            this.profiles = profiles;
            if(profiles.containsKey(selectedProfile)){ //if true update old entry
                fillFields(profiles, selectedProfile);
                nameTextField.setEnabled(false);
                updating = true;
                this.setVisible(true);
                
                if(ok){
                   ProfileElement pEl = (ProfileElement) profiles.get(selectedProfile);

                   pEl.setDeviceGeneration(getKeyfromGenerationCombo((String)generationComboBox.getSelectedItem()));
                   pEl.setDevicePath(devicePathTextField.getText());
                   pEl.setLocalPath(localPathTextField.getText());
                   pEl.setExportPath(exportPathTextField.getText());
                   pEl.setTempPathSameAsDevicePath(tempCheckBox.isSelected());
                   pEl.setTranscodeTempPath(tempTextField.getText());
                   return true;

                }
            }else{
                     JOptionPane.showMessageDialog(this, java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicProfilesDialog.error01.ProfileWithName") + " " + selectedProfile + " " + java.util.ResourceBundle.getBundle("localization/language").getString("global.does_not_exist"), java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicProfilesDialog.error01.Non_existing_profile_name..."), JOptionPane.ERROR_MESSAGE);
            }
        }
        return false;
    }
    
    /**
     * fills profile fields
     * @param profiles 
     * @param selectedProfile the name of the selected profile
     */
    private void fillFields(HashMap profiles, String selectedProfile){
        if(profiles.containsKey(selectedProfile)){ 
            if(profiles != null && selectedProfile != null){
                ProfileElement sel = (ProfileElement) profiles.get(selectedProfile);
                if(sel != null){
                    nameTextField.setText(sel.getProfileName());
                    localPathTextField.setText(sel.getLocalPath());
                    exportPathTextField.setText(sel.getExportPath());
                    devicePathTextField.setText(sel.getDevicePath());
                    generationComboBox.setSelectedItem(generationMap.get(sel.getDeviceGeneration()));
                    tempBrowsButton.setEnabled(!sel.isTempPathSameAsDevicePath());
                    tempCheckBox.setSelected(sel.isTempPathSameAsDevicePath());
                    tempTextField.setEnabled(!sel.isTempPathSameAsDevicePath());
                    tempTextField.setText(sel.getTranscodeTempPath());
                }
            }
        }
    }
    
    /**
     * Opens a choose dialog for folders
     * @param dialogText the text you want to appera in the dialog
     * @param tf the JTextField you want to enter path into
     */
    private boolean OpenChooserDialog(String dialogText, JTextField tf){
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(dialogText);
        fc.setCurrentDirectory(new File(tf.getText()));
        fc.setDialogType(JFileChooser.OPEN_DIALOG);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setVisible(true);
        
        if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
            tf.setText(fc.getSelectedFile().getAbsolutePath());
            return true;
        }
        
        return false;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        nameTextField = new javax.swing.JTextField();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        deviceBrowseButton = new javax.swing.JButton();
        devicePathTextField = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        localBrowsButton = new javax.swing.JButton();
        localPathTextField = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        exportBrowsButton = new javax.swing.JButton();
        exportPathTextField = new javax.swing.JTextField();
        jPanelContainer4 = new javax.swing.JPanel();
        tempTextField = new javax.swing.JTextField();
        tempBrowsButton = new javax.swing.JButton();
        tempCheckBox = new javax.swing.JCheckBox();
        jPanel7 = new javax.swing.JPanel();
        generationComboBox = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setName("Form"); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("localization/language"); // NOI18N
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("JSymphonicProfilesDialog.jPanel2.border.title"))); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N

        nameTextField.setName("nameTextField"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(nameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        okButton.setFont(new java.awt.Font("Dialog", 0, 12));
        okButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/ok.png"))); // NOI18N
        okButton.setText(bundle.getString("global.OK")); // NOI18N
        okButton.setName("okButton"); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setFont(new java.awt.Font("Dialog", 0, 12));
        cancelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/cancel.png"))); // NOI18N
        cancelButton.setText(bundle.getString("global.Cancel")); // NOI18N
        cancelButton.setName("cancelButton"); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setName("jPanel1"); // NOI18N

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("JSymphonicProperties.devPathLabel.text"))); // NOI18N
        jPanel4.setName("jPanel4"); // NOI18N

        deviceBrowseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/folder.png"))); // NOI18N
        deviceBrowseButton.setName("deviceBrowseButton"); // NOI18N
        deviceBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deviceBrowseButtonActionPerformed(evt);
            }
        });

        devicePathTextField.setName("devicePathTextField"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(devicePathTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 598, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deviceBrowseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deviceBrowseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(devicePathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("JSymphonicProperties.localPathLabel.text"))); // NOI18N
        jPanel5.setName("jPanel5"); // NOI18N

        localBrowsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/folder.png"))); // NOI18N
        localBrowsButton.setName("localBrowsButton"); // NOI18N
        localBrowsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                localBrowsButtonActionPerformed(evt);
            }
        });

        localPathTextField.setName("localPathTextField"); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(localPathTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 598, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(localBrowsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(localBrowsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(localPathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("JSymphonicProperties.exportPathLabel.text"))); // NOI18N
        jPanel6.setName("jPanel6"); // NOI18N

        exportBrowsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/folder.png"))); // NOI18N
        exportBrowsButton.setName("exportBrowsButton"); // NOI18N
        exportBrowsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportBrowsButtonActionPerformed(evt);
            }
        });

        exportPathTextField.setName("exportPathTextField"); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(exportPathTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 598, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(exportBrowsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(exportBrowsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(exportPathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanelContainer4.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("JSymphonicProperties.tempPathLabel.text"))); // NOI18N
        jPanelContainer4.setName("jPanelContainer4"); // NOI18N

        tempTextField.setName("tempTextField"); // NOI18N

        tempBrowsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/folder.png"))); // NOI18N
        tempBrowsButton.setName("tempBrowsButton"); // NOI18N
        tempBrowsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tempBrowsButtonActionPerformed(evt);
            }
        });

        tempCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        tempCheckBox.setText(bundle.getString("JSymphonicProfilesDialog.tempCheckBox.text")); // NOI18N
        tempCheckBox.setName("tempCheckBox"); // NOI18N
        tempCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tempCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelContainer4Layout = new javax.swing.GroupLayout(jPanelContainer4);
        jPanelContainer4.setLayout(jPanelContainer4Layout);
        jPanelContainer4Layout.setHorizontalGroup(
            jPanelContainer4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelContainer4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelContainer4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelContainer4Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(tempCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelContainer4Layout.createSequentialGroup()
                        .addComponent(tempTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 598, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tempBrowsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanelContainer4Layout.setVerticalGroup(
            jPanelContainer4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelContainer4Layout.createSequentialGroup()
                .addGroup(jPanelContainer4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tempBrowsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tempTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tempCheckBox))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("JSymphonicProperties.genPathLabel.text"))); // NOI18N
        jPanel7.setName("jPanel7"); // NOI18N

        generationComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        generationComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        generationComboBox.setName("generationComboBox"); // NOI18N

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(generationComboBox, 0, 632, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(generationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelContainer4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelContainer4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(35, Short.MAX_VALUE))
        );

        jPanel7.getAccessibleContext().setAccessibleName(bundle.getString("JSymphonicWindow.Device_Generation")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void deviceBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deviceBrowseButtonActionPerformed
    OpenChooserDialog(java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicProfilesDialog.Select_device_path"), devicePathTextField);
}//GEN-LAST:event_deviceBrowseButtonActionPerformed

private void localBrowsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_localBrowsButtonActionPerformed
    OpenChooserDialog(java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicProfilesDialog.Select_local_path"), localPathTextField);
}//GEN-LAST:event_localBrowsButtonActionPerformed

private void tempBrowsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tempBrowsButtonActionPerformed
    OpenChooserDialog(java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicProfilesDialog.Select_temp_path"), tempTextField);
}//GEN-LAST:event_tempBrowsButtonActionPerformed

private void tempCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tempCheckBoxActionPerformed
if (tempCheckBox.isSelected() ) {
        tempTextField.setEnabled(false);
        tempBrowsButton.setEnabled(false);
    } else {
        tempTextField.setEnabled(true);
        tempBrowsButton.setEnabled(true);
    }
}//GEN-LAST:event_tempCheckBoxActionPerformed

private void exportBrowsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportBrowsButtonActionPerformed
    OpenChooserDialog(java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicProfilesDialog.Select_export_path"), exportPathTextField);
}//GEN-LAST:event_exportBrowsButtonActionPerformed

private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
    if( (profiles.containsKey(nameTextField.getText()) && updating) || (!profiles.containsKey(nameTextField.getText()) && !updating)){
        ok = true;
        this.dispose();
    }else{
        JOptionPane.showMessageDialog(this, java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicProfilesDialog.error02.Profile_with_name") + " " + nameTextField.getText() + " " + java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicProfilesDialog.error02.already_exist"), java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicProfilesDialog.error02.Existing_profile_name"), JOptionPane.ERROR_MESSAGE);
    }
}//GEN-LAST:event_okButtonActionPerformed

private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
    this.dispose();
}//GEN-LAST:event_cancelButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton deviceBrowseButton;
    private javax.swing.JTextField devicePathTextField;
    private javax.swing.JButton exportBrowsButton;
    private javax.swing.JTextField exportPathTextField;
    private javax.swing.JComboBox generationComboBox;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanelContainer4;
    private javax.swing.JButton localBrowsButton;
    private javax.swing.JTextField localPathTextField;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton okButton;
    private javax.swing.JButton tempBrowsButton;
    private javax.swing.JCheckBox tempCheckBox;
    private javax.swing.JTextField tempTextField;
    // End of variables declaration//GEN-END:variables

}
