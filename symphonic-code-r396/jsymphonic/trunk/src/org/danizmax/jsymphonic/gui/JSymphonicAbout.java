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
 * JSymphonicAbout.java
 *
 * Created on May 18, 2008, 9:02 PM
 * 
 */

package org.danizmax.jsymphonic.gui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 * This JFrame is used to show information about the application
 * @author  danizmax - Daniel Žalar (danizmax@gmail.com)
 */
public class JSymphonicAbout extends javax.swing.JFrame {

    /** Creates new form JSymphonicAbout */
    public JSymphonicAbout(String title, String name, String version, String comment, String credits) {
        initComponents();
        this.setIconImage(new ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/icons/js_logo16.png")).getImage());
        this.setTitle(title);
        AplicationNameAndVersionLabel.setText(name);
        versionLabel.setText(version);
        commentLabel.setText(comment);
        creditsTextArea.setText(credits);
        loadLicenseTextArea();
        creditsTextArea.moveCaretPosition(0);
        licenseTextArea.moveCaretPosition(0);
        jverLabel.setText(System.getProperty("java.version"));
        //sysLabel.setText(System.getProperty("os.name"));
        printSysInfo();
    }
    
    void printSysInfo(){
        Properties p = System.getProperties();
        Enumeration keys = p.keys();
        sysInfoTextArea.removeAll();
        while (keys.hasMoreElements()) {
          String key = (String)keys.nextElement();
          String value = (String)p.get(key);
          sysInfoTextArea.append(key + ": " + value + "\n");
        }
        sysInfoTextArea.moveCaretPosition(0);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        aboutPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        AplicationNameAndVersionLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        versionLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jverLabel = new javax.swing.JLabel();
        commentLabel = new javax.swing.JLabel();
        creditsPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        creditsTextArea = new javax.swing.JTextArea();
        licensePanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        licenseTextArea = new javax.swing.JTextArea();
        sysPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        sysInfoTextArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("localization/misc"); // NOI18N
        setTitle(bundle.getString("JSymphonicWindow.About_JSymphonic")); // NOI18N
        setName("Form"); // NOI18N

        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        jTabbedPane1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jTabbedPane1.setMinimumSize(new java.awt.Dimension(480, 260));
        jTabbedPane1.setName("jTabbedPane1"); // NOI18N
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(480, 260));

        aboutPanel.setName("aboutPanel"); // NOI18N

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/icons/js-logo-big.png"))); // NOI18N
        jLabel1.setText(bundle.getString("JSymphonicAbout.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        AplicationNameAndVersionLabel.setFont(new java.awt.Font("Bitstream Vera Sans", 1, 18)); // NOI18N
        AplicationNameAndVersionLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        AplicationNameAndVersionLabel.setText(bundle.getString("global.version")); // NOI18N
        AplicationNameAndVersionLabel.setName("AplicationNameAndVersionLabel"); // NOI18N

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel3.setText(bundle.getString("JSymphonicAbout.jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        versionLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        versionLabel.setText(bundle.getString("JSymphonicAbout.versionLabel.text")); // NOI18N
        versionLabel.setName("versionLabel"); // NOI18N

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel2.setText(bundle.getString("JSymphonicAbout.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jverLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jverLabel.setText(bundle.getString("JSymphonicAbout.jverLabel.text")); // NOI18N
        jverLabel.setName("jverLabel"); // NOI18N

        commentLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        commentLabel.setText(bundle.getString("JSymphonicAbout.commentLabel.text")); // NOI18N
        commentLabel.setName("commentLabel"); // NOI18N

        org.jdesktop.layout.GroupLayout aboutPanelLayout = new org.jdesktop.layout.GroupLayout(aboutPanel);
        aboutPanel.setLayout(aboutPanelLayout);
        aboutPanelLayout.setHorizontalGroup(
            aboutPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, aboutPanelLayout.createSequentialGroup()
                .add(40, 40, 40)
                .add(jLabel1)
                .add(aboutPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(aboutPanelLayout.createSequentialGroup()
                        .add(18, 18, 18)
                        .add(aboutPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(aboutPanelLayout.createSequentialGroup()
                                .add(jLabel2)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jverLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE))
                            .add(aboutPanelLayout.createSequentialGroup()
                                .add(jLabel3)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(versionLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE))
                            .add(commentLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)))
                    .add(aboutPanelLayout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(AplicationNameAndVersionLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)))
                .addContainerGap())
        );
        aboutPanelLayout.setVerticalGroup(
            aboutPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(aboutPanelLayout.createSequentialGroup()
                .add(30, 30, 30)
                .add(aboutPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel1)
                    .add(aboutPanelLayout.createSequentialGroup()
                        .add(6, 6, 6)
                        .add(AplicationNameAndVersionLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(aboutPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(versionLabel)
                            .add(jLabel3))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(aboutPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jverLabel)
                            .add(jLabel2))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(commentLabel)))
                .addContainerGap(105, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(bundle.getString("JSymphonicAbout.aboutPanel.TabConstraints.tabTitle"), aboutPanel); // NOI18N

        creditsPanel.setName("creditsPanel"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        creditsTextArea.setColumns(20);
        creditsTextArea.setEditable(false);
        creditsTextArea.setLineWrap(true);
        creditsTextArea.setRows(5);
        creditsTextArea.setWrapStyleWord(true);
        creditsTextArea.setAutoscrolls(false);
        creditsTextArea.setName("creditsTextArea"); // NOI18N
        jScrollPane1.setViewportView(creditsTextArea);

        org.jdesktop.layout.GroupLayout creditsPanelLayout = new org.jdesktop.layout.GroupLayout(creditsPanel);
        creditsPanel.setLayout(creditsPanelLayout);
        creditsPanelLayout.setHorizontalGroup(
            creditsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(creditsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
                .addContainerGap())
        );
        creditsPanelLayout.setVerticalGroup(
            creditsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(creditsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("JSymphonicAbout.creditsPanel.TabConstraints.tabTitle"), creditsPanel); // NOI18N

        licensePanel.setName("licensePanel"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        licenseTextArea.setColumns(20);
        licenseTextArea.setEditable(false);
        licenseTextArea.setLineWrap(true);
        licenseTextArea.setRows(5);
        licenseTextArea.setWrapStyleWord(true);
        licenseTextArea.setAutoscrolls(false);
        licenseTextArea.setName("licenseTextArea"); // NOI18N
        jScrollPane2.setViewportView(licenseTextArea);

        org.jdesktop.layout.GroupLayout licensePanelLayout = new org.jdesktop.layout.GroupLayout(licensePanel);
        licensePanel.setLayout(licensePanelLayout);
        licensePanelLayout.setHorizontalGroup(
            licensePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(licensePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
                .addContainerGap())
        );
        licensePanelLayout.setVerticalGroup(
            licensePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(licensePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("JSymphonicAbout.licensePanel.TabConstraints.tabTitle"), licensePanel); // NOI18N

        sysPanel.setName("sysPanel"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        sysInfoTextArea.setColumns(20);
        sysInfoTextArea.setEditable(false);
        sysInfoTextArea.setRows(5);
        sysInfoTextArea.setName("sysInfoTextArea"); // NOI18N
        jScrollPane3.setViewportView(sysInfoTextArea);

        org.jdesktop.layout.GroupLayout sysPanelLayout = new org.jdesktop.layout.GroupLayout(sysPanel);
        sysPanel.setLayout(sysPanelLayout);
        sysPanelLayout.setHorizontalGroup(
            sysPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(sysPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
                .addContainerGap())
        );
        sysPanelLayout.setVerticalGroup(
            sysPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(sysPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("JSymphonicAbout.sysPanel.TabConstraints.tabTitle"), sysPanel); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 480, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AplicationNameAndVersionLabel;
    private javax.swing.JPanel aboutPanel;
    private javax.swing.JLabel commentLabel;
    private javax.swing.JPanel creditsPanel;
    private javax.swing.JTextArea creditsTextArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel jverLabel;
    private javax.swing.JPanel licensePanel;
    private javax.swing.JTextArea licenseTextArea;
    private javax.swing.JTextArea sysInfoTextArea;
    private javax.swing.JPanel sysPanel;
    private javax.swing.JLabel versionLabel;
    // End of variables declaration//GEN-END:variables

    /**
     * Load the license file into the license text area.
     * The license file is stored in "org/danizmax/jsymphonic/resources". It is named "COPYING-xx_XX" where xx_XX is the language code. If the license is not translated into the current language (the file doesn't exist), load the english version named "COPYING.txt".
     */
    private void loadLicenseTextArea() {
        BufferedReader licenseReader = null;
    
        // Try to load the license file corresponding to the current language code
        try {
            licenseReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/org/danizmax/jsymphonic/resources/COPYING_"+Locale.getDefault().toString()+".txt")));
        }
        catch(Exception e){
            try {
                // If an exception was thrown, load the english version
                licenseReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/org/danizmax/jsymphonic/resources/COPYING.txt")));
            } catch (Exception ex) {
                ex.printStackTrace();
                Logger.getLogger(JSymphonicAbout.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Load the text from the file to the text area
        try {
            String str;
            while ((str = licenseReader.readLine()) != null) {
                licenseTextArea.append(str + "\n"); // Add each read line to the text area
            }
            licenseReader.close();
        } 
        catch (Exception e) {}
    }

}
