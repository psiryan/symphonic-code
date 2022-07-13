/*
 * Configuration.java
 *
 * Created on 12 octobre 2007, 19:00
 */

package org.naurd.media.jsymphonic.manager;

//import com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalElement;
import java.awt.CardLayout;
import java.io.File;
import java.util.Locale;
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
    private boolean languageChange = false;
    
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
        jTextFieldDevicePath.setText(settings.getValue("DevicePath", JSymphonic.languages.getString("INVALID_PATH")));
        jTextFieldMusicPath.setText(settings.getValue("MusicPath", JSymphonic.languages.getString("INVALID_PATH")));
        jTextFieldExportedPath.setText(settings.getValue("ExportedMusicPath", JSymphonic.languages.getString("INVALID_PATH")));
        
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
        
        // Then, enable the ones which are installed on the computer IF java version >= 6 !!!
        if(JSymphonic.javaVersion >= 6){
            for(int i = 0; i < lafInfo.length; i++ ) {
                if(lafInfo[i].getName() == "Windows") {
                    jRadioButtonWindows.setEnabled(true);
                }
                if(lafInfo[i].getName() == "GTK+") {
                    jRadioButtonLinux.setEnabled(true);
                }
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
        
        if(languageChange) {
            Locale newLanguage; // new language that will be read from the conbo list
            
            // Read what is the new language
            switch(jComboLanguage.getSelectedIndex()){
                case 0: // O is english
                    newLanguage = Locale.ENGLISH;
                    break;
                case 1: // 1 is french
                    newLanguage = Locale.FRENCH;
                    break;
                default:
                    newLanguage = Locale.ENGLISH;
            }
            
            // Save the new language into the settings
            settings.putValue("Language", newLanguage.toString());
            
            // Update the language with method from main windows
            symphonic.updateLocale();
        }
        
        // Enable main window
        symphonic.setMainWindowEnable(true);
        
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
                jLabelError.setText(JSymphonic.languages.getString("INVALID_DEVICE_PATH"));
                break;
            case INVALID_MUSIC_PATH:
                jLabelError.setText(JSymphonic.languages.getString("INVALID_MUSIC_PATH"));
                break;
            case INVALID_EXPORTED_PATH:
                jLabelError.setText(JSymphonic.languages.getString("INVALID_EXPORTED_PATH"));
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
                System.out.println(JSymphonic.languages.getString("PROBLEM_LOADING_LAF"));
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
        jPanelButtonsContainer = new javax.swing.JPanel();
        jPanelError = new javax.swing.JPanel();
        jLabelError = new javax.swing.JLabel();
        jPanelBoutons = new javax.swing.JPanel();
        jButtonOK = new javax.swing.JButton();
        jButtonApply = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jPanelConfigPanel = new javax.swing.JPanel();
        jPanelDisplay = new javax.swing.JPanel();
        jPanelContainer5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanelSpacer1 = new javax.swing.JPanel();
        jPanelLAF = new javax.swing.JPanel();
        jRadioButtonMetal = new javax.swing.JRadioButton();
        jRadioButtonWindows = new javax.swing.JRadioButton();
        jRadioButtonLinux = new javax.swing.JRadioButton();
        jPanelContainer6 = new javax.swing.JPanel();
        jLabelLanguage = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jComboLanguage = new javax.swing.JComboBox();
        jPanel5 = new javax.swing.JPanel();
        jPanelPaths = new javax.swing.JPanel();
        jPanelContainer1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldDevicePath = new javax.swing.JTextField();
        jButtonChange1 = new javax.swing.JButton();
        jPanelSpacer3 = new javax.swing.JPanel();
        jPanelSpacer2 = new javax.swing.JPanel();
        jPanelContainer2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jButtonChange2 = new javax.swing.JButton();
        jTextFieldMusicPath = new javax.swing.JTextField();
        jPanelSpacer4 = new javax.swing.JPanel();
        jPanelSpacer5 = new javax.swing.JPanel();
        jPanelContainer3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanelSubContainer1 = new javax.swing.JPanel();
        jCheckBoxExportedPathIdentic = new javax.swing.JCheckBox();
        jPanelSubSubContainer1 = new javax.swing.JPanel();
        jTextFieldExportedPath = new javax.swing.JTextField();
        jButtonChangeExported = new javax.swing.JButton();
        jPanelSpacer6 = new javax.swing.JPanel();
        jPanelSpacer7 = new javax.swing.JPanel();
        jPanelTransfer = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jRadioButtonOnlyNeededTranscode = new javax.swing.JRadioButton();
        jRadioButtonAlwaysTranscode = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        jLabelMP3options = new javax.swing.JLabel();
        jPanelContainer4 = new javax.swing.JPanel();
        jLabelBitrate = new javax.swing.JLabel();
        jComboBoxBitrate = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        jPanelSpacer8 = new javax.swing.JPanel();
        jPanelSpacer9 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("CONFIGURATION_W"));
        setMinimumSize(new java.awt.Dimension(500, 500));
        jListOptions.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jListOptions.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Display", "Paths", "Transfer" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jListOptions.setMaximumSize(new java.awt.Dimension(80, 500));
        jListOptions.setMinimumSize(new java.awt.Dimension(80, 500));
        jListOptions.setPreferredSize(new java.awt.Dimension(80, 500));
        jListOptions.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListOptionsMouseClicked(evt);
            }
        });

        getContentPane().add(jListOptions, java.awt.BorderLayout.WEST);

        jPanelMain.setLayout(new java.awt.BorderLayout());

        jPanelMain.setMaximumSize(new java.awt.Dimension(420, 500));
        jPanelMain.setMinimumSize(new java.awt.Dimension(420, 500));
        jPanelMain.setPreferredSize(new java.awt.Dimension(420, 500));
        jPanelButtonsContainer.setLayout(new java.awt.BorderLayout());

        jPanelButtonsContainer.setMaximumSize(new java.awt.Dimension(420, 55));
        jPanelButtonsContainer.setMinimumSize(new java.awt.Dimension(420, 55));
        jPanelButtonsContainer.setPreferredSize(new java.awt.Dimension(420, 55));
        jPanelButtonsContainer.setRequestFocusEnabled(false);
        jPanelError.setPreferredSize(new java.awt.Dimension(100, 20));
        jLabelError.setForeground(new java.awt.Color(255, 0, 0));
        jPanelError.add(jLabelError);

        jPanelButtonsContainer.add(jPanelError, java.awt.BorderLayout.NORTH);

        jPanelBoutons.setMaximumSize(new java.awt.Dimension(32767, 30));
        jPanelBoutons.setMinimumSize(new java.awt.Dimension(218, 25));
        jPanelBoutons.setPreferredSize(new java.awt.Dimension(100, 35));
        jButtonOK.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("OK"));
        jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOKActionPerformed(evt);
            }
        });

        jPanelBoutons.add(jButtonOK);

        jButtonApply.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("APPLY"));
        jButtonApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonApplyActionPerformed(evt);
            }
        });

        jPanelBoutons.add(jButtonApply);

        jButtonCancel.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("CANCEL"));
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jPanelBoutons.add(jButtonCancel);

        jPanelButtonsContainer.add(jPanelBoutons, java.awt.BorderLayout.CENTER);

        jPanelMain.add(jPanelButtonsContainer, java.awt.BorderLayout.SOUTH);

        jPanelConfigPanel.setLayout(new java.awt.CardLayout());

        jPanelConfigPanel.setMaximumSize(new java.awt.Dimension(420, 445));
        jPanelConfigPanel.setMinimumSize(new java.awt.Dimension(420, 445));
        jPanelConfigPanel.setPreferredSize(new java.awt.Dimension(420, 445));
        jPanelDisplay.setLayout(new javax.swing.BoxLayout(jPanelDisplay, javax.swing.BoxLayout.Y_AXIS));

        jPanelDisplay.setBorder(javax.swing.BorderFactory.createTitledBorder(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("DISPLAY")));
        jPanelDisplay.setMaximumSize(new java.awt.Dimension(420, 445));
        jPanelDisplay.setMinimumSize(new java.awt.Dimension(420, 445));
        jPanelDisplay.setPreferredSize(new java.awt.Dimension(420, 445));
        jPanelContainer5.setLayout(new java.awt.BorderLayout());

        jPanelContainer5.setMaximumSize(new java.awt.Dimension(420, 70));
        jPanelContainer5.setMinimumSize(new java.awt.Dimension(420, 70));
        jPanelContainer5.setPreferredSize(new java.awt.Dimension(420, 70));
        jLabel1.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("CHOOSE_LAF"));
        jLabel1.setMaximumSize(new java.awt.Dimension(410, 15));
        jLabel1.setMinimumSize(new java.awt.Dimension(410, 15));
        jLabel1.setPreferredSize(new java.awt.Dimension(410, 15));
        jPanelContainer5.add(jLabel1, java.awt.BorderLayout.NORTH);

        jPanelContainer5.add(jPanelSpacer1, java.awt.BorderLayout.WEST);

        jPanelLAF.setLayout(new javax.swing.BoxLayout(jPanelLAF, javax.swing.BoxLayout.Y_AXIS));

        jPanelLAF.setMaximumSize(new java.awt.Dimension(410, 40));
        jPanelLAF.setMinimumSize(new java.awt.Dimension(410, 40));
        jPanelLAF.setPreferredSize(new java.awt.Dimension(410, 40));
        buttonGroupLAF.add(jRadioButtonMetal);
        jRadioButtonMetal.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("METAL"));
        jRadioButtonMetal.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonMetal.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButtonMetal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMetalActionPerformed(evt);
            }
        });

        jPanelLAF.add(jRadioButtonMetal);

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

        jPanelLAF.add(jRadioButtonWindows);

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

        jPanelLAF.add(jRadioButtonLinux);

        jPanelContainer5.add(jPanelLAF, java.awt.BorderLayout.CENTER);

        jPanelDisplay.add(jPanelContainer5);

        jPanelContainer6.setLayout(new java.awt.BorderLayout());

        jPanelContainer6.setMaximumSize(new java.awt.Dimension(420, 385));
        jPanelContainer6.setMinimumSize(new java.awt.Dimension(420, 385));
        jPanelContainer6.setPreferredSize(new java.awt.Dimension(420, 380));
        jLabelLanguage.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("LANGUAGE"));
        jPanelContainer6.add(jLabelLanguage, java.awt.BorderLayout.NORTH);

        jPanel4.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelContainer6.add(jPanel4, java.awt.BorderLayout.WEST);

        jComboLanguage.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "English", "French" }));
        jComboLanguage.setMaximumSize(new java.awt.Dimension(70, 20));
        jComboLanguage.setMinimumSize(new java.awt.Dimension(70, 20));
        jComboLanguage.setPreferredSize(new java.awt.Dimension(70, 20));
        jComboLanguage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboLanguageActionPerformed(evt);
            }
        });

        jPanelContainer6.add(jComboLanguage, java.awt.BorderLayout.CENTER);

        jPanel5.setMaximumSize(new java.awt.Dimension(420, 340));
        jPanel5.setMinimumSize(new java.awt.Dimension(420, 340));
        jPanel5.setPreferredSize(new java.awt.Dimension(420, 340));
        jPanelContainer6.add(jPanel5, java.awt.BorderLayout.SOUTH);

        jPanelDisplay.add(jPanelContainer6);

        jPanelConfigPanel.add(jPanelDisplay, "Display");

        jPanelPaths.setLayout(new java.awt.GridLayout(6, 1));

        jPanelPaths.setBorder(javax.swing.BorderFactory.createTitledBorder(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("PATHS")));
        jPanelPaths.setMaximumSize(new java.awt.Dimension(420, 445));
        jPanelPaths.setMinimumSize(new java.awt.Dimension(420, 445));
        jPanelPaths.setPreferredSize(new java.awt.Dimension(420, 445));
        jPanelContainer1.setLayout(new java.awt.BorderLayout());

        jPanelContainer1.setMaximumSize(new java.awt.Dimension(410, 50));
        jPanelContainer1.setMinimumSize(new java.awt.Dimension(410, 50));
        jPanelContainer1.setPreferredSize(new java.awt.Dimension(410, 50));
        jLabel2.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("DEVICE_PATH"));
        jPanelContainer1.add(jLabel2, java.awt.BorderLayout.NORTH);

        jTextFieldDevicePath.setMaximumSize(new java.awt.Dimension(2147483647, 20));
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

        jPanelContainer1.add(jTextFieldDevicePath, java.awt.BorderLayout.CENTER);

        jButtonChange1.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("CHANGE"));
        jButtonChange1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChange1ActionPerformed(evt);
            }
        });

        jPanelContainer1.add(jButtonChange1, java.awt.BorderLayout.EAST);

        jPanelSpacer3.setLayout(new javax.swing.BoxLayout(jPanelSpacer3, javax.swing.BoxLayout.X_AXIS));

        jPanelSpacer3.setMaximumSize(new java.awt.Dimension(100, 28));
        jPanelSpacer3.setMinimumSize(new java.awt.Dimension(100, 28));
        jPanelSpacer3.setPreferredSize(new java.awt.Dimension(100, 28));
        jPanelContainer1.add(jPanelSpacer3, java.awt.BorderLayout.SOUTH);

        jPanelSpacer2.setLayout(new javax.swing.BoxLayout(jPanelSpacer2, javax.swing.BoxLayout.X_AXIS));

        jPanelSpacer2.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelContainer1.add(jPanelSpacer2, java.awt.BorderLayout.WEST);

        jPanelPaths.add(jPanelContainer1);

        jPanelContainer2.setLayout(new java.awt.BorderLayout());

        jPanelContainer2.setMaximumSize(new java.awt.Dimension(410, 35));
        jPanelContainer2.setMinimumSize(new java.awt.Dimension(410, 35));
        jPanelContainer2.setPreferredSize(new java.awt.Dimension(410, 35));
        jLabel3.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("MUSIC_PATH"));
        jPanelContainer2.add(jLabel3, java.awt.BorderLayout.NORTH);

        jButtonChange2.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("CHANGE"));
        jButtonChange2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChange2ActionPerformed(evt);
            }
        });

        jPanelContainer2.add(jButtonChange2, java.awt.BorderLayout.EAST);

        jTextFieldMusicPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldMusicPathActionPerformed(evt);
            }
        });

        jPanelContainer2.add(jTextFieldMusicPath, java.awt.BorderLayout.CENTER);

        jPanelSpacer4.setLayout(new javax.swing.BoxLayout(jPanelSpacer4, javax.swing.BoxLayout.X_AXIS));

        jPanelSpacer4.setPreferredSize(new java.awt.Dimension(10, 28));
        jPanelContainer2.add(jPanelSpacer4, java.awt.BorderLayout.SOUTH);

        jPanelSpacer5.setLayout(new javax.swing.BoxLayout(jPanelSpacer5, javax.swing.BoxLayout.X_AXIS));

        jPanelSpacer5.setPreferredSize(new java.awt.Dimension(10, 100));
        jPanelContainer2.add(jPanelSpacer5, java.awt.BorderLayout.WEST);

        jPanelPaths.add(jPanelContainer2);

        jPanelContainer3.setLayout(new java.awt.BorderLayout());

        jPanelContainer3.setMaximumSize(new java.awt.Dimension(410, 380));
        jPanelContainer3.setMinimumSize(new java.awt.Dimension(410, 380));
        jPanelContainer3.setPreferredSize(new java.awt.Dimension(410, 380));
        jLabel4.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("EXPORTED_MUSIC_PATH"));
        jPanelContainer3.add(jLabel4, java.awt.BorderLayout.NORTH);

        jPanelSubContainer1.setLayout(new java.awt.GridLayout(2, 1));

        jCheckBoxExportedPathIdentic.setSelected(true);
        jCheckBoxExportedPathIdentic.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("SAME_AS_MUSIC_PATH"));
        jCheckBoxExportedPathIdentic.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxExportedPathIdentic.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxExportedPathIdentic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxExportedPathIdenticActionPerformed(evt);
            }
        });

        jPanelSubContainer1.add(jCheckBoxExportedPathIdentic);

        jPanelSubSubContainer1.setLayout(new javax.swing.BoxLayout(jPanelSubSubContainer1, javax.swing.BoxLayout.X_AXIS));

        jTextFieldExportedPath.setEnabled(false);
        jTextFieldExportedPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldExportedPathActionPerformed(evt);
            }
        });

        jPanelSubSubContainer1.add(jTextFieldExportedPath);

        jButtonChangeExported.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("CHANGE"));
        jButtonChangeExported.setEnabled(false);
        jButtonChangeExported.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChangeExportedActionPerformed(evt);
            }
        });

        jPanelSubSubContainer1.add(jButtonChangeExported);

        jPanelSubContainer1.add(jPanelSubSubContainer1);

        jPanelContainer3.add(jPanelSubContainer1, java.awt.BorderLayout.CENTER);

        jPanelSpacer6.setLayout(new javax.swing.BoxLayout(jPanelSpacer6, javax.swing.BoxLayout.X_AXIS));

        jPanelSpacer6.setPreferredSize(new java.awt.Dimension(10, 100));
        jPanelContainer3.add(jPanelSpacer6, java.awt.BorderLayout.WEST);

        jPanelSpacer7.setLayout(new javax.swing.BoxLayout(jPanelSpacer7, javax.swing.BoxLayout.X_AXIS));

        jPanelSpacer7.setPreferredSize(new java.awt.Dimension(100, 8));
        jPanelContainer3.add(jPanelSpacer7, java.awt.BorderLayout.SOUTH);

        jPanelPaths.add(jPanelContainer3);

        jPanelConfigPanel.add(jPanelPaths, "Paths");

        jPanelTransfer.setLayout(new java.awt.GridLayout(5, 1));

        jPanelTransfer.setBorder(javax.swing.BorderFactory.createTitledBorder(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("TRANSFER")));
        jPanelTransfer.setMaximumSize(new java.awt.Dimension(420, 445));
        jPanelTransfer.setMinimumSize(new java.awt.Dimension(420, 445));
        jPanelTransfer.setPreferredSize(new java.awt.Dimension(420, 445));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

        jPanel1.setMaximumSize(new java.awt.Dimension(420, 30));
        jPanel1.setMinimumSize(new java.awt.Dimension(420, 30));
        jPanel1.setPreferredSize(new java.awt.Dimension(420, 30));
        buttonGroupTransfer.add(jRadioButtonOnlyNeededTranscode);
        jRadioButtonOnlyNeededTranscode.setSelected(true);
        jRadioButtonOnlyNeededTranscode.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("ONLY_NEEDED_TRANSCODE"));
        jRadioButtonOnlyNeededTranscode.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonOnlyNeededTranscode.setEnabled(false);
        jRadioButtonOnlyNeededTranscode.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jPanel1.add(jRadioButtonOnlyNeededTranscode);

        buttonGroupTransfer.add(jRadioButtonAlwaysTranscode);
        jRadioButtonAlwaysTranscode.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("ALWAYS_TRANSCODE"));
        jRadioButtonAlwaysTranscode.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonAlwaysTranscode.setEnabled(false);
        jRadioButtonAlwaysTranscode.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jPanel1.add(jRadioButtonAlwaysTranscode);

        jPanelTransfer.add(jPanel1);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel2.setMaximumSize(new java.awt.Dimension(410, 420));
        jPanel2.setMinimumSize(new java.awt.Dimension(410, 420));
        jPanel2.setPreferredSize(new java.awt.Dimension(410, 420));
        jLabelMP3options.setText(java.util.ResourceBundle.getBundle("org/naurd/media/jsymphonic/manager/languages").getString("MP3_OPTIONS"));
        jLabelMP3options.setEnabled(false);
        jPanel2.add(jLabelMP3options, java.awt.BorderLayout.NORTH);

        jPanelContainer4.setLayout(new javax.swing.BoxLayout(jPanelContainer4, javax.swing.BoxLayout.X_AXIS));

        jPanelContainer4.setMaximumSize(new java.awt.Dimension(410, 15));
        jPanelContainer4.setMinimumSize(new java.awt.Dimension(410, 15));
        jPanelContainer4.setPreferredSize(new java.awt.Dimension(410, 15));
        jLabelBitrate.setText("Bitrate  ");
        jLabelBitrate.setEnabled(false);
        jPanelContainer4.add(jLabelBitrate);

        jComboBoxBitrate.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "64 kpbs", "96 kpbs", "128 kpbs", "196 kpbs", "256 kpbs" }));
        jComboBoxBitrate.setEnabled(false);
        jPanelContainer4.add(jComboBoxBitrate);

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.X_AXIS));

        jPanelContainer4.add(jPanel3);

        jPanel2.add(jPanelContainer4, java.awt.BorderLayout.CENTER);

        jPanelSpacer8.setLayout(new javax.swing.BoxLayout(jPanelSpacer8, javax.swing.BoxLayout.X_AXIS));

        jPanelSpacer8.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanel2.add(jPanelSpacer8, java.awt.BorderLayout.WEST);

        jPanelSpacer9.setLayout(new javax.swing.BoxLayout(jPanelSpacer9, javax.swing.BoxLayout.X_AXIS));

        jPanelSpacer9.setMaximumSize(new java.awt.Dimension(410, 30));
        jPanelSpacer9.setMinimumSize(new java.awt.Dimension(410, 30));
        jPanelSpacer9.setPreferredSize(new java.awt.Dimension(410, 30));
        jPanel2.add(jPanelSpacer9, java.awt.BorderLayout.SOUTH);

        jPanelTransfer.add(jPanel2);

        jPanelConfigPanel.add(jPanelTransfer, "Transfer");

        jPanelMain.add(jPanelConfigPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanelMain, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboLanguageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboLanguageActionPerformed
        languageChange = true;
    }//GEN-LAST:event_jComboLanguageActionPerformed

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
    private javax.swing.JComboBox jComboLanguage;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelBitrate;
    private javax.swing.JLabel jLabelError;
    private javax.swing.JLabel jLabelLanguage;
    private javax.swing.JLabel jLabelMP3options;
    private javax.swing.JList jListOptions;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanelBoutons;
    private javax.swing.JPanel jPanelButtonsContainer;
    private javax.swing.JPanel jPanelConfigPanel;
    private javax.swing.JPanel jPanelContainer1;
    private javax.swing.JPanel jPanelContainer2;
    private javax.swing.JPanel jPanelContainer3;
    private javax.swing.JPanel jPanelContainer4;
    private javax.swing.JPanel jPanelContainer5;
    private javax.swing.JPanel jPanelContainer6;
    private javax.swing.JPanel jPanelDisplay;
    private javax.swing.JPanel jPanelError;
    private javax.swing.JPanel jPanelLAF;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelPaths;
    private javax.swing.JPanel jPanelSpacer1;
    private javax.swing.JPanel jPanelSpacer2;
    private javax.swing.JPanel jPanelSpacer3;
    private javax.swing.JPanel jPanelSpacer4;
    private javax.swing.JPanel jPanelSpacer5;
    private javax.swing.JPanel jPanelSpacer6;
    private javax.swing.JPanel jPanelSpacer7;
    private javax.swing.JPanel jPanelSpacer8;
    private javax.swing.JPanel jPanelSpacer9;
    private javax.swing.JPanel jPanelSubContainer1;
    private javax.swing.JPanel jPanelSubSubContainer1;
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
