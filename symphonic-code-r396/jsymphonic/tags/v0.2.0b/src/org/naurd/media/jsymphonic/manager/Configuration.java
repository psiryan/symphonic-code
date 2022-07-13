/*
 * Configuration.java
 *
 * Created on 12 octobre 2007, 19:00
 */

package org.naurd.media.jsymphonic.manager;

import java.awt.CardLayout;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author  skiron
 */
public class Configuration extends javax.swing.JFrame {
    public static final int PANEL1 = 0;
    public static final int PANEL2 = 1;
    public static final int INVALID_DEVICE_PATH = -1;
    public static final int INVALID_MUSIC_PATH = -2;
    public static final int INVALID_EXPORTED_PATH = -3;
    
    private boolean LAFchange = false;
    private boolean devicePathChange = false;
    private boolean musicPathChange = false;
    private boolean exportedPathChange = false;
    
    private int newLAF;
    
    private JSymphonic symphonic;
    private Settings settings;
    
    /** Creates new form Configuration */
    public Configuration(JSymphonic symphonic, Settings settings, int activeConfigPanel) {
        this.symphonic = symphonic;
        this.settings = settings;
        initComponents();
        
        // Add an option panel
        changeActivePanel(activeConfigPanel);
        
        // Configure elements
        jListOptions.setCellRenderer(new JSymphonicListCellRenderer());
        // Selected current LAF
        updateCurrentLAFradioButton();
                
        // Set current paths
        jTextFieldDevicePath.setText(settings.getValue("DevicePath", "invalid path"));
        jTextFieldMusicPath.setText(settings.getValue("MusicPath", "invalid path"));
        jTextFieldExportedPath.setText(settings.getValue("ExportedMusicPath", "invalid path"));
        
        // If music path and exported music path are the same, the checkbox should be selected, the text fiel disable and the button Change disable, and vice versa is they are not the same
        if(jTextFieldMusicPath.getText().compareTo(jTextFieldExportedPath.getText()) == 0){
            jCheckBoxExportedPathIdentic.setSelected(true);
            jTextFieldExportedPath.setEnabled(false);
            jButtonChangeExported.setEnabled(false);
        }
        else {
            jCheckBoxExportedPathIdentic.setSelected(false);
            jTextFieldExportedPath.setEnabled(true);            
            jButtonChangeExported.setEnabled(true);
        }

        // Show window
        setVisible(true);
    }
    
    private void updateCurrentLAFradioButton(){
        // Enable available look'n'feel
        UIManager.LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();
        
        // First don't enable any LAF
        jRadioButtonWindows.setEnabled(false);
        jRadioButtonLinux.setEnabled(false);
        
        // Then, enable the ones which are installed on the computer
        for(int i = 0; i < lafInfo.length; i++ ) {
            if(lafInfo[i].getName() == "Windows") {
                jRadioButtonWindows.setEnabled(true);
            }
            if(lafInfo[i].getName() == "GTK+") {
                jRadioButtonLinux.setEnabled(true);
            }
        }

        String currentLAF = UIManager.getLookAndFeel().getName();
        if(currentLAF.contains("Windows")) {
            jRadioButtonWindows.setSelected(true);
        }
        else if (currentLAF.contains("GTK")) {
            jRadioButtonLinux.setSelected(true);
        }
        else {
            jRadioButtonMetal.setSelected(true);
        }

    }
    
    public Configuration(JSymphonic symphonic, Settings settings) {
        new Configuration(symphonic, settings, PANEL1);
    }
    
    private int applyChanges() {
        if(LAFchange) {
            symphonic.changeLAF(newLAF);
            settings.putIntValue("LAF", newLAF);
            settings.savePreferences();
            
            this.changeLAF(newLAF);
            updateCurrentLAFradioButton();
        }
        
        if(devicePathChange) {
            // Get new omgaudioDir
            File omgaudioDir = new File(jTextFieldDevicePath.getText() + "/OMGAUDIO");
            
            if(!omgaudioDir.exists()) { // Check existance
                omgaudioDir = new File(jTextFieldDevicePath.getText() + "/omgaudio"); // if not, try in lowercase
            }
            
            if(!omgaudioDir.exists()) { // Check existance
                return INVALID_DEVICE_PATH;
            }
            
            settings.putValue("DevicePath", jTextFieldDevicePath.getText());
            settings.putValue("OMGAUDIOpath", omgaudioDir.getPath());
            settings.savePreferences();
            
            symphonic.setPlayer(omgaudioDir); // Update the player
            symphonic.upDateGUI(JSymphonic.UPDATEGUI_RIGHT); // Refresh GUI
        }
        
        if(musicPathChange) {
            if(new File(jTextFieldDevicePath.getText()).exists()) { // Check existance
                settings.putValue("MusicPath", jTextFieldMusicPath.getText()); // Save preference
                settings.savePreferences();
                
                symphonic.upDateGUI(JSymphonic.UPDATEGUI_GLOBAL); // Update GUI
                if(jCheckBoxExportedPathIdentic.isSelected()) { // If the Checkbox is selected, the path of the exported text field should also be changed
                    jTextFieldExportedPath.setText(jTextFieldMusicPath.getText()); // Change text is the exported path text field 
                }
            }
            else {
                return INVALID_MUSIC_PATH;
            }
        }

        if(exportedPathChange) {
            if(jCheckBoxExportedPathIdentic.isSelected()) {
                // If is selected, we use the text in Music path text field
                if(new File(jTextFieldMusicPath.getText()).exists()) { // Check existance
                    settings.putValue("ExportedMusicPath", jTextFieldMusicPath.getText()); // Save preference
                    settings.savePreferences();

                    symphonic.upDateGUI(JSymphonic.UPDATEGUI_GLOBAL); // Update GUI
                    jTextFieldExportedPath.setText(jTextFieldMusicPath.getText()); // Change text is the exported path text field 
                }
                else {
                    return INVALID_MUSIC_PATH;
                }
            }
            else { // If not, we use the path in exported path text field
                if(new File(jTextFieldExportedPath.getText()).exists()) { // Check existance
                    settings.putValue("ExportedMusicPath", jTextFieldExportedPath.getText()); // Save preference
                    settings.savePreferences();

                    symphonic.upDateGUI(JSymphonic.UPDATEGUI_GLOBAL); // Update GUI
                }
                else {
                    return INVALID_EXPORTED_PATH;
                }
            }
        }

        
        
        return 0;
    }
    
    private void changeActivePanel(int newPanel) {
        // First remove the current panel
        CardLayout cl = (CardLayout)(jPanelConfigPanel.getLayout());
        
        // Then add the right one
        switch(newPanel) {
            case 1:
                cl.show(jPanelConfigPanel, "Paths");
                break;
                
            case 2:
                cl.show(jPanelConfigPanel, "Transfer");
                break;
                
            default:
                cl.show(jPanelConfigPanel, "Display");
        }
    }
    
    private void displayErrorMessage(int error) {
        switch(error) {
            case INVALID_DEVICE_PATH:
                jLabelError.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("INVALID_DEVICE_PATH"));
                break;
            case INVALID_MUSIC_PATH:
                jLabelError.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("INVALID_MUSIC_PATH"));
                break;
            case INVALID_EXPORTED_PATH:
                jLabelError.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("INVALID_EXPORTED_PATH"));
                break;
        }
    }
    
    public void changeLAF(int newLAF) {
        boolean problem = false;
        
        switch(newLAF) {
            case JSymphonic.WINDOWS_LAF:
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                } 
                catch (Exception ex) {
                    problem = true;
                }
                break;
                
            case JSymphonic.LINUX_LAF:
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
                } 
                catch (Exception ex) {
                    problem = true;
                }
                break;
                
            case JSymphonic.NATIVE_LAF:
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                } 
                catch (Exception ex) {
                    try {
                        UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
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
            } 
            catch (Exception ex) {
                System.out.println("Problem occured while trying to change the look and feel");
            }
        }
        SwingUtilities.updateComponentTreeUI(this);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        buttonGroupLAF = new javax.swing.ButtonGroup();
        buttonGroupTransfer = new javax.swing.ButtonGroup();
        jListOptions = new javax.swing.JList();
        jPanelMain = new javax.swing.JPanel();
        jPanelButtons = new javax.swing.JPanel();
        jButtonOK = new javax.swing.JButton();
        jButtonApply = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jLabelError = new javax.swing.JLabel();
        jPanelConfigPanel = new javax.swing.JPanel();
        jPanelDisplay = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jRadioButtonWindows = new javax.swing.JRadioButton();
        jRadioButtonLinux = new javax.swing.JRadioButton();
        jRadioButtonMetal = new javax.swing.JRadioButton();
        jPanelPaths = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldDevicePath = new javax.swing.JTextField();
        jButtonChange1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldMusicPath = new javax.swing.JTextField();
        jButtonChange2 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jCheckBoxExportedPathIdentic = new javax.swing.JCheckBox();
        jTextFieldExportedPath = new javax.swing.JTextField();
        jButtonChangeExported = new javax.swing.JButton();
        jPanelTransfer = new javax.swing.JPanel();
        jRadioButtonOnlyNeededTranscode = new javax.swing.JRadioButton();
        jRadioButtonAlwaysTranscode = new javax.swing.JRadioButton();
        jLabelMP3options = new javax.swing.JLabel();
        jLabelBitrate = new javax.swing.JLabel();
        jComboBoxBitrate = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("CONFIGURATION_W"));
        jListOptions.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jListOptions.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Display", "Paths", "Transfer" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jListOptions.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListOptionsMouseClicked(evt);
            }
        });

        getContentPane().add(jListOptions, java.awt.BorderLayout.WEST);

        jPanelMain.setLayout(new java.awt.BorderLayout());

        jPanelButtons.setPreferredSize(new java.awt.Dimension(50, 70));
        jButtonOK.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("OK"));
        jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOKActionPerformed(evt);
            }
        });

        jButtonApply.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("APPLY"));
        jButtonApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonApplyActionPerformed(evt);
            }
        });

        jButtonCancel.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("CANCEL"));
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jLabelError.setForeground(new java.awt.Color(255, 0, 0));

        javax.swing.GroupLayout jPanelButtonsLayout = new javax.swing.GroupLayout(jPanelButtons);
        jPanelButtons.setLayout(jPanelButtonsLayout);
        jPanelButtonsLayout.setHorizontalGroup(
            jPanelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelButtonsLayout.createSequentialGroup()
                .addContainerGap(245, Short.MAX_VALUE)
                .addGroup(jPanelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelButtonsLayout.createSequentialGroup()
                        .addComponent(jButtonOK)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonApply)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonCancel))
                    .addComponent(jLabelError, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        jPanelButtonsLayout.setVerticalGroup(
            jPanelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelButtonsLayout.createSequentialGroup()
                .addContainerGap(27, Short.MAX_VALUE)
                .addComponent(jLabelError)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonOK)
                    .addComponent(jButtonApply)
                    .addComponent(jButtonCancel))
                .addContainerGap())
        );
        jPanelMain.add(jPanelButtons, java.awt.BorderLayout.SOUTH);

        jPanelConfigPanel.setLayout(new java.awt.CardLayout());

        jPanelDisplay.setBorder(javax.swing.BorderFactory.createTitledBorder(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("DISPLAY")));
        jLabel1.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("CHOOSE_LAF"));

        buttonGroupLAF.add(jRadioButtonWindows);
        jRadioButtonWindows.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("WINDOWS_XP"));
        jRadioButtonWindows.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonWindows.setEnabled(false);
        jRadioButtonWindows.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButtonWindows.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonWindowsActionPerformed(evt);
            }
        });

        buttonGroupLAF.add(jRadioButtonLinux);
        jRadioButtonLinux.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("LINUX"));
        jRadioButtonLinux.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonLinux.setEnabled(false);
        jRadioButtonLinux.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButtonLinux.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonLinuxActionPerformed(evt);
            }
        });

        buttonGroupLAF.add(jRadioButtonMetal);
        jRadioButtonMetal.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("METAL"));
        jRadioButtonMetal.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonMetal.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButtonMetal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMetalActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelDisplayLayout = new javax.swing.GroupLayout(jPanelDisplay);
        jPanelDisplay.setLayout(jPanelDisplayLayout);
        jPanelDisplayLayout.setHorizontalGroup(
            jPanelDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDisplayLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButtonWindows)
                    .addComponent(jRadioButtonLinux)
                    .addComponent(jLabel1)
                    .addComponent(jRadioButtonMetal))
                .addContainerGap(191, Short.MAX_VALUE))
        );
        jPanelDisplayLayout.setVerticalGroup(
            jPanelDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDisplayLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonWindows)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonLinux)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonMetal)
                .addContainerGap(223, Short.MAX_VALUE))
        );
        jPanelConfigPanel.add(jPanelDisplay, "Display");

        jPanelPaths.setBorder(javax.swing.BorderFactory.createTitledBorder(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("PATHS")));
        jLabel2.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("DEVICE_PATH"));

        jTextFieldDevicePath.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextFieldDevicePathMousePressed(evt);
            }
        });
        jTextFieldDevicePath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldDevicePathActionPerformed(evt);
            }
        });

        jButtonChange1.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("CHANGE"));
        jButtonChange1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChange1ActionPerformed(evt);
            }
        });

        jLabel3.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("MUSIC_PATH"));

        jTextFieldMusicPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldMusicPathActionPerformed(evt);
            }
        });

        jButtonChange2.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("CHANGE"));
        jButtonChange2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChange2ActionPerformed(evt);
            }
        });

        jLabel4.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("EXPORTED_MUSIC_PATH"));

        jCheckBoxExportedPathIdentic.setSelected(true);
        jCheckBoxExportedPathIdentic.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("SAME_AS_MUSIC_PATH"));
        jCheckBoxExportedPathIdentic.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxExportedPathIdentic.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxExportedPathIdentic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxExportedPathIdenticActionPerformed(evt);
            }
        });

        jTextFieldExportedPath.setEnabled(false);
        jTextFieldExportedPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldExportedPathActionPerformed(evt);
            }
        });

        jButtonChangeExported.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("CHANGE"));
        jButtonChangeExported.setEnabled(false);
        jButtonChangeExported.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChangeExportedActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelPathsLayout = new javax.swing.GroupLayout(jPanelPaths);
        jPanelPaths.setLayout(jPanelPathsLayout);
        jPanelPathsLayout.setHorizontalGroup(
            jPanelPathsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPathsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPathsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPathsLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jTextFieldDevicePath, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonChange1))
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPathsLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jTextFieldMusicPath, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonChange2))
                    .addComponent(jLabel4)
                    .addGroup(jPanelPathsLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanelPathsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPathsLayout.createSequentialGroup()
                                .addComponent(jTextFieldExportedPath, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonChangeExported))
                            .addComponent(jCheckBoxExportedPathIdentic))))
                .addContainerGap())
        );
        jPanelPathsLayout.setVerticalGroup(
            jPanelPathsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPathsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelPathsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonChange1)
                    .addComponent(jTextFieldDevicePath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelPathsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonChange2)
                    .addComponent(jTextFieldMusicPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxExportedPathIdentic)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelPathsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonChangeExported)
                    .addComponent(jTextFieldExportedPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(98, Short.MAX_VALUE))
        );
        jPanelConfigPanel.add(jPanelPaths, "Paths");

        jPanelTransfer.setBorder(javax.swing.BorderFactory.createTitledBorder(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("TRANSFER")));
        buttonGroupTransfer.add(jRadioButtonOnlyNeededTranscode);
        jRadioButtonOnlyNeededTranscode.setSelected(true);
        jRadioButtonOnlyNeededTranscode.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("ONLY_NEEDED_TRANSCODE"));
        jRadioButtonOnlyNeededTranscode.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonOnlyNeededTranscode.setEnabled(false);
        jRadioButtonOnlyNeededTranscode.setMargin(new java.awt.Insets(0, 0, 0, 0));

        buttonGroupTransfer.add(jRadioButtonAlwaysTranscode);
        jRadioButtonAlwaysTranscode.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("ALWAYS_TRANSCODE"));
        jRadioButtonAlwaysTranscode.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonAlwaysTranscode.setEnabled(false);
        jRadioButtonAlwaysTranscode.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabelMP3options.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("MP3_OPTIONS"));
        jLabelMP3options.setEnabled(false);

        jLabelBitrate.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("BITRATE"));
        jLabelBitrate.setEnabled(false);

        jComboBoxBitrate.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "64 kpbs", "96 kpbs", "128 kpbs", "196 kpbs", "256 kpbs" }));
        jComboBoxBitrate.setEnabled(false);

        javax.swing.GroupLayout jPanelTransferLayout = new javax.swing.GroupLayout(jPanelTransfer);
        jPanelTransfer.setLayout(jPanelTransferLayout);
        jPanelTransferLayout.setHorizontalGroup(
            jPanelTransferLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTransferLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelTransferLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButtonOnlyNeededTranscode)
                    .addComponent(jLabelMP3options)
                    .addGroup(jPanelTransferLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabelBitrate)
                        .addGap(34, 34, 34)
                        .addComponent(jComboBoxBitrate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jRadioButtonAlwaysTranscode))
                .addContainerGap(205, Short.MAX_VALUE))
        );
        jPanelTransferLayout.setVerticalGroup(
            jPanelTransferLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTransferLayout.createSequentialGroup()
                .addComponent(jRadioButtonOnlyNeededTranscode)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonAlwaysTranscode)
                .addGap(23, 23, 23)
                .addComponent(jLabelMP3options)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelTransferLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelBitrate)
                    .addComponent(jComboBoxBitrate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(209, Short.MAX_VALUE))
        );
        jPanelConfigPanel.add(jPanelTransfer, "Transfer");

        jPanelMain.add(jPanelConfigPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanelMain, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldDevicePathMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextFieldDevicePathMousePressed
        devicePathChange = true;
    }//GEN-LAST:event_jTextFieldDevicePathMousePressed

    private void jTextFieldExportedPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldExportedPathActionPerformed
        exportedPathChange = true;
    }//GEN-LAST:event_jTextFieldExportedPathActionPerformed

    private void jTextFieldMusicPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldMusicPathActionPerformed
        musicPathChange = true;
    }//GEN-LAST:event_jTextFieldMusicPathActionPerformed

    private void jTextFieldDevicePathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldDevicePathActionPerformed
        devicePathChange = true;
    }//GEN-LAST:event_jTextFieldDevicePathActionPerformed

    private void jCheckBoxExportedPathIdenticActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxExportedPathIdenticActionPerformed
        if (jCheckBoxExportedPathIdentic.isSelected() ) {
            jTextFieldExportedPath.setEnabled(false);
            jButtonChangeExported.setEnabled(false);
        }
        else {
            jTextFieldExportedPath.setEnabled(true);
            jButtonChangeExported.setEnabled(true);
        }
        
        exportedPathChange = true;
    }//GEN-LAST:event_jCheckBoxExportedPathIdenticActionPerformed

    private void jButtonChangeExportedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChangeExportedActionPerformed
        JFileChooser fileChoice = new JFileChooser();

        fileChoice.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        // If the current path is valid, we use it a current directory
        File currentPath = new File(jTextFieldExportedPath.getText());
        if(currentPath.exists()) {
            fileChoice.setCurrentDirectory(currentPath);
        }

        
        if(fileChoice.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { // Display window dialogue
            jTextFieldExportedPath.setText(fileChoice.getSelectedFile().getPath());
            exportedPathChange = true;
        }
    }//GEN-LAST:event_jButtonChangeExportedActionPerformed

    private void jButtonChange2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChange2ActionPerformed
        JFileChooser fileChoice = new JFileChooser();

        fileChoice.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        // If the current path is valid, we use it a current directory
        File currentPath = new File(jTextFieldMusicPath.getText());
        if(currentPath.exists()) {
            fileChoice.setCurrentDirectory(currentPath);
        }

        
        if(fileChoice.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { // Display window dialogue
            jTextFieldMusicPath.setText(fileChoice.getSelectedFile().getPath());
            musicPathChange = true;
        }
    }//GEN-LAST:event_jButtonChange2ActionPerformed

    private void jButtonChange1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChange1ActionPerformed
        JFileChooser fileChoice = new JFileChooser();
        
        // Only directories can be chosen
        fileChoice.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        // If the current path is valid, we use it a current directory
        File currentPath = new File(jTextFieldDevicePath.getText());
        if(currentPath.exists()) {
            fileChoice.setCurrentDirectory(currentPath);
        }
        
        if(fileChoice.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { // Display window dialogue
            jTextFieldDevicePath.setText(fileChoice.getSelectedFile().getPath());
            devicePathChange = true;
        }
    }//GEN-LAST:event_jButtonChange1ActionPerformed

    private void jRadioButtonMetalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMetalActionPerformed
        LAFchange = true;
        newLAF = JSymphonic.METAL_LAF;
    }//GEN-LAST:event_jRadioButtonMetalActionPerformed

    private void jRadioButtonLinuxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonLinuxActionPerformed
        LAFchange = true;
        newLAF = JSymphonic.LINUX_LAF;
    }//GEN-LAST:event_jRadioButtonLinuxActionPerformed

    private void jRadioButtonWindowsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonWindowsActionPerformed
        LAFchange = true;
        newLAF = JSymphonic.WINDOWS_LAF;
    }//GEN-LAST:event_jRadioButtonWindowsActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonApplyActionPerformed
        int ret = applyChanges();
        
        jLabelError.setText("");
                
        if(ret < 0){
            displayErrorMessage(ret);
        }
    }//GEN-LAST:event_jButtonApplyActionPerformed

    private void jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOKActionPerformed
        int ret = applyChanges();
        
        if(ret < 0){
            displayErrorMessage(ret);
        }
        else {
            dispose();
        }
    }//GEN-LAST:event_jButtonOKActionPerformed

    private void jListOptionsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListOptionsMouseClicked
        int selected = jListOptions.getSelectedIndex();
        changeActivePanel(selected);
    }//GEN-LAST:event_jListOptionsMouseClicked
    
    /**
     * @param args the command line arguments
     *
    public static void main() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Configuration().setVisible(true);
            }
        });
    }*/

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupLAF;
    private javax.swing.ButtonGroup buttonGroupTransfer;
    private javax.swing.JButton jButtonApply;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonChange1;
    private javax.swing.JButton jButtonChange2;
    private javax.swing.JButton jButtonChangeExported;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JCheckBox jCheckBoxExportedPathIdentic;
    private javax.swing.JComboBox jComboBoxBitrate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelBitrate;
    private javax.swing.JLabel jLabelError;
    private javax.swing.JLabel jLabelMP3options;
    private javax.swing.JList jListOptions;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelConfigPanel;
    private javax.swing.JPanel jPanelDisplay;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelPaths;
    private javax.swing.JPanel jPanelTransfer;
    private javax.swing.JRadioButton jRadioButtonAlwaysTranscode;
    private javax.swing.JRadioButton jRadioButtonLinux;
    private javax.swing.JRadioButton jRadioButtonMetal;
    private javax.swing.JRadioButton jRadioButtonOnlyNeededTranscode;
    private javax.swing.JRadioButton jRadioButtonWindows;
    private javax.swing.JTextField jTextFieldDevicePath;
    private javax.swing.JTextField jTextFieldExportedPath;
    private javax.swing.JTextField jTextFieldMusicPath;
    // End of variables declaration//GEN-END:variables
    
}
