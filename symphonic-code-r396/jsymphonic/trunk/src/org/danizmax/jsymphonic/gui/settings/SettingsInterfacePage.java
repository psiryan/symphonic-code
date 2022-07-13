/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SettingsInterfacePage.java
 *
 * Created on Jun 12, 2009, 5:07:53 PM
 */

package org.danizmax.jsymphonic.gui.settings;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author danizmax
 */
public class SettingsInterfacePage extends javax.swing.JPanel {

    private String language;
    private String theme;
    private String logLevel;
    private boolean logToFile;
    private boolean showToolbarText = true;

    /** Creates new form SettingsInterfacePage */
    public SettingsInterfacePage(TreeMap langMap, TreeMap themeMap) {
        initComponents();

        //language
        Set keys = langMap.keySet();
        Iterator iter = keys.iterator();
        languageComboBox.removeAllItems();
        while (iter.hasNext()){
            languageComboBox.addItem(iter.next());
        }

        //themes
        keys = themeMap.keySet();
        iter = keys.iterator();

        while (iter.hasNext()){
            themeComboBox.addItem(iter.next());
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolbarbuttonGroup = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        languageComboBox = new javax.swing.JComboBox();
        noteLangLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        themeComboBox = new javax.swing.JComboBox();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        logLevelComboBox = new javax.swing.JComboBox();
        logToFileCheckBox = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        showButtonTextRadioButton = new javax.swing.JRadioButton();
        hideButtonTextRadioButton = new javax.swing.JRadioButton();
        jSeparator3 = new javax.swing.JSeparator();

        jLabel1.setText("Language");

        languageComboBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        languageComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                languageComboBoxActionPerformed(evt);
            }
        });

        noteLangLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("localization/jsymphonicproperties"); // NOI18N
        noteLangLabel.setText(bundle.getString("JSymphonicProperties.noteLangLabel.text")); // NOI18N

        jLabel2.setText("Theme");

        themeComboBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        themeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                themeComboBoxActionPerformed(evt);
            }
        });

        jLabel3.setText("Log Level");

        logLevelComboBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        logLevelComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "OFF", "SEVERE", "WARNING", "INFO", "CONFIG", "FINE", "FINER", "FINEST ", "ALL" }));
        logLevelComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logLevelComboBoxActionPerformed(evt);
            }
        });

        logToFileCheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        logToFileCheckBox.setText(bundle.getString("JSymphonicProperties.log_to_file")); // NOI18N
        logToFileCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logToFileCheckBoxActionPerformed(evt);
            }
        });

        jLabel4.setText("Toolbar Buttons");

        toolbarbuttonGroup.add(showButtonTextRadioButton);
        showButtonTextRadioButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        showButtonTextRadioButton.setSelected(true);
        showButtonTextRadioButton.setText("Show Button Text");
        showButtonTextRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showButtonTextRadioButtonActionPerformed(evt);
            }
        });

        toolbarbuttonGroup.add(hideButtonTextRadioButton);
        hideButtonTextRadioButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        hideButtonTextRadioButton.setText("Hide Button Text");
        hideButtonTextRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hideButtonTextRadioButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                    .addComponent(noteLangLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(languageComboBox, 0, 323, Short.MAX_VALUE))
                .addGap(12, 12, 12))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
                .addContainerGap(12, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator3, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                    .addComponent(showButtonTextRadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                    .addComponent(hideButtonTextRadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(themeComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, 323, Short.MAX_VALUE)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE))
                .addGap(12, 12, 12))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(logToFileCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(logLevelComboBox, 0, 323, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(languageComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(noteLangLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(12, 12, 12)
                .addComponent(themeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showButtonTextRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(hideButtonTextRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addGap(12, 12, 12)
                .addComponent(logLevelComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logToFileCheckBox)
                .addContainerGap(12, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void logToFileCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logToFileCheckBoxActionPerformed
        logToFile = logToFileCheckBox.isSelected();
}//GEN-LAST:event_logToFileCheckBoxActionPerformed

    private void languageComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_languageComboBoxActionPerformed
        language = (String) languageComboBox.getSelectedItem();
    }//GEN-LAST:event_languageComboBoxActionPerformed

    private void themeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_themeComboBoxActionPerformed
        theme = (String) themeComboBox.getSelectedItem();
    }//GEN-LAST:event_themeComboBoxActionPerformed

    private void logLevelComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logLevelComboBoxActionPerformed
        logLevel = (String) logLevelComboBox.getSelectedItem();
    }//GEN-LAST:event_logLevelComboBoxActionPerformed

    private void showButtonTextRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showButtonTextRadioButtonActionPerformed
        showToolbarText = showButtonTextRadioButton.isSelected();
    }//GEN-LAST:event_showButtonTextRadioButtonActionPerformed

    private void hideButtonTextRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hideButtonTextRadioButtonActionPerformed
        showToolbarText = showButtonTextRadioButton.isSelected();
    }//GEN-LAST:event_hideButtonTextRadioButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton hideButtonTextRadioButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JComboBox languageComboBox;
    private javax.swing.JComboBox logLevelComboBox;
    private javax.swing.JCheckBox logToFileCheckBox;
    private javax.swing.JLabel noteLangLabel;
    private javax.swing.JRadioButton showButtonTextRadioButton;
    private javax.swing.JComboBox themeComboBox;
    private javax.swing.ButtonGroup toolbarbuttonGroup;
    // End of variables declaration//GEN-END:variables

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
        languageComboBox.setSelectedItem(language);
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
        themeComboBox.setSelectedItem(theme);
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
        logLevelComboBox.setSelectedItem(logLevel);
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
        logToFileCheckBox.setSelected(logToFile);
    }

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
        showButtonTextRadioButton.setSelected(showToolbarText);
        hideButtonTextRadioButton.setSelected(!showToolbarText);
    }

}