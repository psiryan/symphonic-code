/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SettingsTransferPage.java
 *
 * Created on Jun 12, 2009, 5:29:09 PM
 */

package org.danizmax.jsymphonic.gui.settings;

import java.util.Iterator;
import java.util.Vector;
import javax.swing.JOptionPane;

/**
 *
 * @author danizmax
 */
public class SettingsTransferPage extends javax.swing.JPanel {

    private Vector tagFolderStructure;
    private String bitrate = "32 kbps";
    private boolean alwaysTranscode = false;
    private boolean readID3Tags = true;

    /** Creates new form SettingsTransferPage */
    public SettingsTransferPage() {
        initComponents();
        tagsLabel.setToolTipText("<html>Pattern respects the following rules:<br> - The character \"<b>/</b>\" is used to separate folders.<br> - The string \"<TagField>\" is used to identify a tag field, possible tag fields are: <br>     \"<b>Artist</b>\", \"<b>Album</b>\", \"<b>Genre</b>\", \"<b>Title</b>\", \"<b>TrackNumber</b>\", \"<b>Year</b>\" and \"<b>Ignore</b>\".<br> - Each tag field can appear only once.</html>");
        tagsComboBox.setToolTipText("<html>Pattern respects the following rules:<br> - The character \"<b>/</b>\" is used to separate folders.<br> - The string \"<TagField>\" is used to identify a tag field, possible tag fields are: <br>     \"<b>Artist</b>\", \"<b>Album</b>\", \"<b>Genre</b>\", \"<b>Title</b>\", \"<b>TrackNumber</b>\", \"<b>Year</b>\" and \"<b>Ignore</b>\".<br> - Each tag field can appear only once.</html>");
        tagFolderStructure = new Vector();
        tagFolderStructure.add("<Artist>");
        tagFolderStructure.add("/");
        tagFolderStructure.add("<Year>");
        tagFolderStructure.add("-");
        tagFolderStructure.add("<Album>");
        tagFolderStructure.add("/");
        tagFolderStructure.add("<TrackNumber>");
        tagFolderStructure.add("-");
        tagFolderStructure.add("<Title>");
        tagsLabel.setText("<Artist>/<Year>-<Album>/<TrackNumber>-<Title>");
    }

    private String  getTagFolderStructureString(Vector tagVector){
        String tagString = new String();
        Iterator it = tagVector.iterator();
        while(it.hasNext()){
            tagString += it.next();
        }
        return tagString;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        bitrateComboBox = new javax.swing.JComboBox();
        onlyNeededTranscodeRadioButton = new javax.swing.JRadioButton();
        alwaysTranscodeRadioButton = new javax.swing.JRadioButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        readTagsRadioButton = new javax.swing.JRadioButton();
        neverReadTagsRadioButton = new javax.swing.JRadioButton();
        tagsComboBox = new javax.swing.JComboBox();
        removeTagButton = new javax.swing.JButton();
        addTagButton = new javax.swing.JButton();
        tagsLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();

        jLabel1.setText("MP3 Bitrate Options");

        bitrateComboBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        bitrateComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "32 kbps", "48 kbps", "64 kbps", "96 kbps", "128 kbps", "160 kbps", "192 kbps", "256 kbps", "320 kbps" }));
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

        buttonGroup1.add(onlyNeededTranscodeRadioButton);
        onlyNeededTranscodeRadioButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        onlyNeededTranscodeRadioButton.setSelected(true);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("localization/jsymphonicproperties"); // NOI18N
        onlyNeededTranscodeRadioButton.setText(bundle.getString("JSymphonicProperties.onlyNeededTranscodeRadioButton.text")); // NOI18N
        onlyNeededTranscodeRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onlyNeededTranscodeRadioButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(alwaysTranscodeRadioButton);
        alwaysTranscodeRadioButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        alwaysTranscodeRadioButton.setText(bundle.getString("JSymphonicProperties.alwaysTranscodeRadioButton.text")); // NOI18N
        alwaysTranscodeRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alwaysTranscodeRadioButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("Tag utilization");

        buttonGroup2.add(readTagsRadioButton);
        readTagsRadioButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        readTagsRadioButton.setSelected(true);
        readTagsRadioButton.setText(bundle.getString("JSymphonicProperties.readTagsRadioButton.text")); // NOI18N
        readTagsRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readTagsRadioButtonActionPerformed(evt);
            }
        });

        buttonGroup2.add(neverReadTagsRadioButton);
        neverReadTagsRadioButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        neverReadTagsRadioButton.setText(bundle.getString("JSymphonicProperties.neverReadTagsRadioButton.text")); // NOI18N
        neverReadTagsRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                neverReadTagsRadioButtonActionPerformed(evt);
            }
        });

        tagsComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        tagsComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<Artist>", "<Year>", "<Album>", "<TrackNumber>", "<Title>", "<TagField>", "<Ignore>", "/", "-", "_", ".", "[", "]" }));

        removeTagButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        removeTagButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/icons/list-remove.png"))); // NOI18N
        removeTagButton.setToolTipText("Remove");
        removeTagButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeTagButtonActionPerformed(evt);
            }
        });

        addTagButton.setFont(new java.awt.Font("Dialog", 0, 12));
        addTagButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/icons/list-add.png"))); // NOI18N
        addTagButton.setToolTipText("Add");
        addTagButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTagButtonActionPerformed(evt);
            }
        });

        tagsLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        tagsLabel.setText("PATTERN");
        tagsLabel.setToolTipText("");

        jLabel3.setText("MP3 Transcode Options");

        jLabel4.setText("Tag pattern");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(bitrateComboBox, 0, 546, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 546, Short.MAX_VALUE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 546, Short.MAX_VALUE))
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(neverReadTagsRadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, 546, Short.MAX_VALUE)
                            .addComponent(readTagsRadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, 546, Short.MAX_VALUE)
                            .addComponent(jSeparator3, javax.swing.GroupLayout.DEFAULT_SIZE, 546, Short.MAX_VALUE)))
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(onlyNeededTranscodeRadioButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 546, Short.MAX_VALUE)
                            .addComponent(alwaysTranscodeRadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, 546, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(tagsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tagsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(removeTagButton, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addTagButton, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {addTagButton, removeTagButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(12, 12, 12)
                .addComponent(bitrateComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(onlyNeededTranscodeRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(alwaysTranscodeRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(readTagsRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(neverReadTagsRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(addTagButton)
                        .addComponent(removeTagButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tagsLabel)
                        .addComponent(tagsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {addTagButton, removeTagButton, tagsComboBox});

    }// </editor-fold>//GEN-END:initComponents

    private void bitrateComboBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bitrateComboBoxMouseClicked

}//GEN-LAST:event_bitrateComboBoxMouseClicked

    private void bitrateComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bitrateComboBoxActionPerformed
       // bitrate = Integer.parseInt(((String)bitrateComboBox.getSelectedItem()).substring(0, 2));
        bitrate = (String) bitrateComboBox.getSelectedItem();
}//GEN-LAST:event_bitrateComboBoxActionPerformed

    private void readTagsRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readTagsRadioButtonActionPerformed
        readID3Tags = readTagsRadioButton.isSelected();
}//GEN-LAST:event_readTagsRadioButtonActionPerformed

    private void neverReadTagsRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_neverReadTagsRadioButtonActionPerformed
        readID3Tags = readTagsRadioButton.isSelected();
}//GEN-LAST:event_neverReadTagsRadioButtonActionPerformed

    private void removeTagButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeTagButtonActionPerformed
        if(tagFolderStructure.size() > 0){
            tagFolderStructure.remove(tagFolderStructure.size()-1);
            tagsLabel.setText(getTagFolderStructureString(tagFolderStructure));
        }
}//GEN-LAST:event_removeTagButtonActionPerformed

    private void addTagButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTagButtonActionPerformed
        if((!tagFolderStructure.contains(tagsComboBox.getSelectedItem()) && !tagsComboBox.getSelectedItem().equals("-") && !tagsComboBox.getSelectedItem().equals("_")
                && !tagsComboBox.getSelectedItem().equals("/") && !tagsComboBox.getSelectedItem().equals(".")
                && !tagsComboBox.getSelectedItem().equals("[") && !tagsComboBox.getSelectedItem().equals("]"))
                || (tagsComboBox.getSelectedItem().equals("-")|| tagsComboBox.getSelectedItem().equals("_")
                || tagsComboBox.getSelectedItem().equals("/") || tagsComboBox.getSelectedItem().equals(".")
                || tagsComboBox.getSelectedItem().equals("[") || tagsComboBox.getSelectedItem().equals("]"))){
            tagFolderStructure.add(tagsComboBox.getSelectedItem());
            tagsLabel.setText(getTagFolderStructureString(tagFolderStructure));
        }else{
            JOptionPane.showMessageDialog(this, "This tag already exits in tag pattern!", "Tag use error...", JOptionPane.ERROR_MESSAGE);
        }
}//GEN-LAST:event_addTagButtonActionPerformed

    private void alwaysTranscodeRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alwaysTranscodeRadioButtonActionPerformed
        alwaysTranscode = alwaysTranscodeRadioButton.isSelected();
    }//GEN-LAST:event_alwaysTranscodeRadioButtonActionPerformed

    private void onlyNeededTranscodeRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onlyNeededTranscodeRadioButtonActionPerformed
        alwaysTranscode = alwaysTranscodeRadioButton.isSelected();
    }//GEN-LAST:event_onlyNeededTranscodeRadioButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addTagButton;
    private javax.swing.JRadioButton alwaysTranscodeRadioButton;
    private javax.swing.JComboBox bitrateComboBox;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JRadioButton neverReadTagsRadioButton;
    private javax.swing.JRadioButton onlyNeededTranscodeRadioButton;
    private javax.swing.JRadioButton readTagsRadioButton;
    private javax.swing.JButton removeTagButton;
    private javax.swing.JComboBox tagsComboBox;
    private javax.swing.JLabel tagsLabel;
    // End of variables declaration//GEN-END:variables


    /**
     * Parses one char tags and tags defines with <SOME_TAG>
     * @param tagFStructure
     */
    public void setTagFolderStructure(String tagFStructure) {
        String tag = "";
        boolean inTagString = false;
        char[] array = tagFStructure.toCharArray();
        tagFolderStructure.removeAllElements();
        for(int i=0;i<array.length;i++){
            if(inTagString){
                tag += array[i];
                if(array[i] == '>'){
                    tagFolderStructure.add(tag);
                    tag = "";
                    inTagString = false;
                }
            }else{
                if(array[i] == '<'){
                    inTagString = true;
                    tag += array[i];
                }else{
                    tagFolderStructure.add(array[i]);
                }
            }
        }
        tagsLabel.setText(getTagFolderStructureString(tagFolderStructure));
    }

    /**
     * @param tagFolderStructure the tagFolderStructure to set
     */
    public void setTagFolderStructure(Vector tagFolderStructure) {
        this.tagFolderStructure = tagFolderStructure;
        tagsLabel.setText(getTagFolderStructureString(tagFolderStructure));
    }

    /**
     * @return the bitrate
     */
    public String getBitrate() {
        return bitrate;
    }

    /**
     * @param bitrate the bitrate to set
     */
    public void setBitrate(String bitrate) {
        this.bitrate = bitrate;
        bitrateComboBox.setSelectedItem(bitrate);
    }

    /**
     * @return the alwaysTranscode
     */
    public boolean isAlwaysTranscode() {
        return alwaysTranscode;
    }

    /**
     * @param alwaysTranscode the alwaysTranscode to set
     */
    public void setAlwaysTranscode(boolean alwaysTranscode) {
        this.alwaysTranscode = alwaysTranscode;
        alwaysTranscodeRadioButton.setSelected(alwaysTranscode);
        onlyNeededTranscodeRadioButton.setSelected(!alwaysTranscode);
    }

    /**
     * @return the readID3Tags
     */
    public boolean isReadID3Tags() {
        return readID3Tags;
    }

    /**
     * @param readID3Tags the readID3Tags to set
     */
    public void setReadID3Tags(boolean readID3Tags) {
        this.readID3Tags = readID3Tags;
        readTagsRadioButton.setSelected(readID3Tags);
        neverReadTagsRadioButton.setSelected(!readID3Tags);
    }

    public String getTagFolderStructure() {
        return getTagFolderStructureString(tagFolderStructure);
    }

}