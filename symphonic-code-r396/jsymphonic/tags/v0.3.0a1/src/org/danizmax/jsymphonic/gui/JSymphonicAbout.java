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
 * JSymphonicAbout.java
 *
 * Created on May 18, 2008, 9:02 PM
 */

package org.danizmax.jsymphonic.gui;

import java.util.Enumeration;
import java.util.Properties;

/**
 * This JFrame is used to show information about the application
 * @author  danizmax - Daniel Žalar (danizmax@gmail.com)
 */
public class JSymphonicAbout extends javax.swing.JFrame {

    /** Creates new form JSymphonicAbout */
    public JSymphonicAbout(String title, String nameAndVersion, String comment, String credits, String license) {
        initComponents();
        this.setTitle(title);
        AplicationNameAndVersionLabel.setText(nameAndVersion);
        commentLabel.setText(comment);
        creditsTextArea.setText(credits);
        licenseTextArea.setText(license);
        creditsTextArea.moveCaretPosition(0);
        licenseTextArea.moveCaretPosition(0);
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

        AplicationNameAndVersionLabel = new javax.swing.JLabel();
        commentLabel = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
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
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("localization/language"); // NOI18N
        setTitle(bundle.getString("JSymphonicWindow.About_JSymphonic")); // NOI18N
        setName("Form"); // NOI18N

        AplicationNameAndVersionLabel.setFont(new java.awt.Font("Bitstream Vera Sans", 1, 18));
        AplicationNameAndVersionLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        AplicationNameAndVersionLabel.setText(bundle.getString("global.version")); // NOI18N
        AplicationNameAndVersionLabel.setName("AplicationNameAndVersionLabel"); // NOI18N

        commentLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        commentLabel.setText(bundle.getString("JSymphonicAbout.commentLabel.text")); // NOI18N
        commentLabel.setName("commentLabel"); // NOI18N

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

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

        javax.swing.GroupLayout creditsPanelLayout = new javax.swing.GroupLayout(creditsPanel);
        creditsPanel.setLayout(creditsPanelLayout);
        creditsPanelLayout.setHorizontalGroup(
            creditsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(creditsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
                .addContainerGap())
        );
        creditsPanelLayout.setVerticalGroup(
            creditsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(creditsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
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

        javax.swing.GroupLayout licensePanelLayout = new javax.swing.GroupLayout(licensePanel);
        licensePanel.setLayout(licensePanelLayout);
        licensePanelLayout.setHorizontalGroup(
            licensePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(licensePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
                .addContainerGap())
        );
        licensePanelLayout.setVerticalGroup(
            licensePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(licensePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
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

        javax.swing.GroupLayout sysPanelLayout = new javax.swing.GroupLayout(sysPanel);
        sysPanel.setLayout(sysPanelLayout);
        sysPanelLayout.setHorizontalGroup(
            sysPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sysPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
                .addContainerGap())
        );
        sysPanelLayout.setVerticalGroup(
            sysPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sysPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("JSymphonicAbout.sysPanel.TabConstraints.tabTitle"), sysPanel); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 463, Short.MAX_VALUE)
                    .addComponent(commentLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 463, Short.MAX_VALUE)
                    .addComponent(AplicationNameAndVersionLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 463, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(AplicationNameAndVersionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(commentLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AplicationNameAndVersionLabel;
    private javax.swing.JLabel commentLabel;
    private javax.swing.JPanel creditsPanel;
    private javax.swing.JTextArea creditsTextArea;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel licensePanel;
    private javax.swing.JTextArea licenseTextArea;
    private javax.swing.JTextArea sysInfoTextArea;
    private javax.swing.JPanel sysPanel;
    // End of variables declaration//GEN-END:variables

}