/*
 *  Copyright (C) 2008 Daniel Žalar (danizmax@gmail.com)
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
 * JSymphonicWindow.java
 *
 * Created on March 25, 2008, 9:59 PM
 */

package org.danizmax.jsymphonic.gui;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import javax.swing.UnsupportedLookAndFeelException;
import org.danizmax.jsymphonic.gui.device.DevicePanel;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.danizmax.jsymphonic.toolkit.ProfileElement;
import org.naurd.media.jsymphonic.toolBox.FFMpegToolBox;

/**
 * This Class is the main JSymphonic window, that contains all the command buttons and all the component
 * @author  danizmax - Daniel Žalar (danizmax@gmail.com)
 */
public class JSymphonicWindow extends javax.swing.JFrame {
    private static Logger logger = Logger.getLogger("org.danizmax.jsymphonic.gui");
    private static String configFile = "JSymphonic.xml";
    private static FileHandler fileLogHandler;
    
    //Log file settings
    private static String logFileName= "JSymphonic.log";
    private static int byteSizeLimit = 1000000; 
    private static int numOfLogFiles = 1;
    private static boolean append = true;
    
    private SettingsHandler sHandler = null;
    //private Log2Gui lgui = null;
    private TreeMap themeMap = null;
    private TreeMap langMap = null;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
    
    //configure logging format
    private Formatter logFormatter = new Formatter() {
  	  public String format(LogRecord record) {
  		  String lvlStr = " [" + record.getLevel() + "]"; 
		  while((lvlStr += " ").length() < 11);
		  return  dateFormatter.format(new Date()) + lvlStr
		  	+ record.getSourceClassName() + ":"
		  	+ record.getSourceMethodName() + " : "
		  	+ record.getMessage() + "\n";
	  }
	};
    
    
    /** Creates new form JSymphonicWindow */
    public JSymphonicWindow() {
        logger.setLevel(Level.ALL);
        //config file has to be loaded before anything!!!
        //lgui = new Log2Gui(logTextArea);   
        sHandler = new SettingsHandler(configFile);
        initLogger();
        logger.info("Initializing JSymphonic...");
        initLang(sHandler.getLanguage());

        try {
            initComponents();
        }
        catch(Exception e){
            logger.severe("Error while initializaing main window");
            e.printStackTrace();
        }
        
        initThemeMap();
        changeLAF((String)themeMap.get(sHandler.getTheme()));

        //logger.addHandler(lgui);
        SettingsHandler.setParentLogger(logger);
        DevicePanel.setParentLogger(logger);
        
        loadProfiles();
        loadNewConfig();

        logger.info("JSymphonic ready!");
    }
    
    /**
     * Start a thread to run the "loadProfilesInThread"
     */
    public void loadProfiles(){
        HashMap profiles = sHandler.getProfiles();
        Set keys = profiles.keySet();
        Iterator iter = keys.iterator();
        profilesComboBox.removeAllItems();
        
        while (iter.hasNext()) {
            profilesComboBox.addItem((String)iter.next());
        }
        profilesComboBox.setSelectedItem(((ProfileElement) profiles.get(sHandler.getSelectedProfile())).getProfileName());
        
        localPanel.setLocalPath(((ProfileElement) profiles.get(profilesComboBox.getSelectedItem())).getLocalPath());
        
        // Start a new thread to load the profile since scanning the local folder may take time
        Thread loadProfilesThread = new Thread(){
            @Override
            public void run(){
                try{
                    loadProfilesInThread();
                } catch(Exception e){}
            }
        };
        loadProfilesThread.setPriority(Thread.NORM_PRIORITY);
        loadProfilesThread.start();
        loadProfilesThread = null;
    }
    
    /**
     * Load profiles into profiles combobox
     */
    public void loadProfilesInThread(){
        localPanel.reloadTree();
    }
    
    public void initLogger(){
        if(sHandler.isLogToFile()){
            try {
                fileLogHandler = new FileHandler(logFileName,byteSizeLimit,numOfLogFiles,append);
                fileLogHandler.setFormatter(logFormatter);
            } catch (IOException ex) {
                Logger.getLogger(JSymphonicWindow.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(JSymphonicWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
            logger.addHandler(fileLogHandler);
        }
        
        Level lvl = Level.parse(sHandler.getLogLevel());

        LogManager lm = LogManager.getLogManager();
        Enumeration<String> loggers = lm.getLoggerNames();
        String loggerName = null;
        
        //set levels for all loggers
        while(loggers.hasMoreElements()){
            loggerName = loggers.nextElement();
            if(loggerName.contains("jsymphonic")){ //set log level only for "jsymphonic" classes
                //lm.getLogger(loggerName).setLevel(lvl);
            }
        }

        //set level for all handlers
        Handler[] handlers = Logger.getLogger( "" ).getHandlers();
        logger.setLevel(lvl);
        for ( int index = 0; index < handlers.length; index++ ) {
          //handlers[index].setLevel(lvl);
          handlers[index].setFormatter(logFormatter);
        }
    }
    
    public void changeLAF(String laf) {
        try {
            if(laf != null){
                UIManager.setLookAndFeel(laf);
                SwingUtilities.updateComponentTreeUI(this);
                SwingUtilities.updateComponentTreeUI(devicePanel);
                SwingUtilities.updateComponentTreeUI(localPanel);
                SwingUtilities.updateComponentTreeUI(devicePanel.getTreePopUp());
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JSymphonicWindow.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(JSymphonicWindow.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(JSymphonicWindow.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(JSymphonicWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
   }
    
   public void loadNewConfig(){
        HashMap profiles = sHandler.getProfiles();
        ProfileElement profile = (ProfileElement) profiles.get(profilesComboBox.getSelectedItem());

        //initLogger();

        devicePanel.setAlwaysTranscode(sHandler.isAlwaysTrascode());
        devicePanel.setTranscodeBitrate(sHandler.getBitrate());
        devicePanel.setDevicePath(profile.getDevicePath());
        FFMpegToolBox.setDevicePath(profile.getDevicePath()); // Inform FFMPEG toolbox about what is the device path to search FFMPEG into it
        devicePanel.setDeviceGeneration(profile.getDeviceGeneration());
        devicePanel.setTempPath(profile.getTranscodeTempPath());
        devicePanel.setExportPath(((ProfileElement) profiles.get(profilesComboBox.getSelectedItem())).getExportPath());

        devicePanel.mountDevice(true);
        
        localPanel.setLocalPath(((ProfileElement) profiles.get(profilesComboBox.getSelectedItem())).getLocalPath());
        localPanel.reloadTree();
        
        exportLocalPanel.setLocalPath(((ProfileElement) profiles.get(profilesComboBox.getSelectedItem())).getExportPath());
        exportLocalPanel.reloadTree();
   }
   
    private void initLang(String selectedLanguage){
        // Define existing languages
        langMap = new TreeMap();
        getLangMap().put("English (default)", new Locale("en", "GB"));
        getLangMap().put("Czech", new Locale("cs", "CS"));
        getLangMap().put("German", new Locale("de", "DE"));
        getLangMap().put("Français", new Locale("fr", "FR"));
        getLangMap().put("Portuguese", new Locale("pt", "PT"));
        getLangMap().put("Russian", new Locale("ru", "RU"));
        getLangMap().put("Spanish", new Locale("es", "ES"));
        getLangMap().put("Slovak", new Locale("sk", "SK"));
        getLangMap().put("Slovensko", new Locale("sl", "SI"));
        getLangMap().put("Svenska", new Locale("sv", "SV"));
        getLangMap().put("Turkçe", new Locale("tr", "TR"));

        //set selected locale
        if(getLangMap().get(selectedLanguage) != null)
            Locale.setDefault((Locale) getLangMap().get(selectedLanguage));
   }
   
   private void initThemeMap(){
        themeMap = new TreeMap();
        
        // Get LAF info from the system
        UIManager.LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();
        
        // Get java version
        int javaVersion = Integer.parseInt(System.getProperty("java.version").substring(2,3)); // Gives an answer like "5" for java 1.5 or "6" for java 1.6
        // If java version is more than 6, Windows and GTK LAf may be available, if they are supported, add them to the list
        if(javaVersion >= 6){
            for(int i = 0; i < lafInfo.length; i++ ) {
                if(lafInfo[i].getName().contains("Windows")) {
                    getThemeMap().put("Windows", "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                }
                if(lafInfo[i].getName().contains("GTK+")) {
                    getThemeMap().put("GTK+", "com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
                }
            }
        }
        
        // Then, add all cross-platform styles
        getThemeMap().put("Metal", "javax.swing.plaf.metal.MetalLookAndFeel");
//        getThemeMap().put("Lipstik", "com.lipstikLF.LipstikLookAndFeel");
//        getThemeMap().put("InfoNode", "net.infonode.gui.laf.InfoNodeLookAndFeel");
//        getThemeMap().put("TinyLaf", "de.muntjak.tinylookandfeel.TinyLookAndFeel");
//        getThemeMap().put("Nimbus", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
     /*  comented because of substance errors getThemeMap().put("Substance Raven Graphite", "org.jvnet.substance.skin.SubstanceRavenGraphiteLookAndFeel");
        getThemeMap().put("Substance Raven", "org.jvnet.substance.skin.SubstanceRavenLookAndFeel");
        getThemeMap().put("Substance Magma", "org.jvnet.substance.skin.SubstanceMagmaLookAndFeel");
        getThemeMap().put("Substance Emerald Dusk", "org.jvnet.substance.skin.SubstanceEmeraldDuskLookAndFeel");
        getThemeMap().put("Substance Business", "org.jvnet.substance.skin.SubstanceBusinessLookAndFeel");
        getThemeMap().put("Substance Business Blue Steel", "org.jvnet.substance.skin.SubstanceBusinessBlueSteelLookAndFeel");
        getThemeMap().put("Substance Business Black Steel", "org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel");
        getThemeMap().put("Substance Creme", "org.jvnet.substance.skin.SubstanceCremeLookAndFeel");
        getThemeMap().put("Substance Creme Coffee", "org.jvnet.substance.skin.SubstanceCremeCoffeeLookAndFeel");
        getThemeMap().put("Substance Sahara", "org.jvnet.substance.skin.SubstanceSaharaLookAndFeel");
        getThemeMap().put("Substance Moderate", "org.jvnet.substance.skin.SubstanceModerateLookAndFeel");
        getThemeMap().put("Substance Office Silver 2007", "org.jvnet.substance.skin.SubstanceOfficeSilver2007LookAndFeel");
        getThemeMap().put("Substance Nebula", "org.jvnet.substance.skin.SubstanceNebulaLookAndFeel");
        getThemeMap().put("Substance Nebula Brick Wall", "org.jvnet.substance.skin.SubstanceNebulaBrickWallLookAndFeel");
        getThemeMap().put("Substance Autumn", "org.jvnet.substance.skin.SubstanceAutumnLookAndFeel");
        getThemeMap().put("Substance Mist Silver", "org.jvnet.substance.skin.SubstanceMistSilverLookAndFeel");
        getThemeMap().put("Substance Mist Aqua", "org.jvnet.substance.skin.SubstanceMistAquaLookAndFeel");         */ 
   }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainToolBar = new javax.swing.JToolBar();
        buttonsToolBar = new javax.swing.JToolBar();
        reploadButton = new javax.swing.JButton();
        importButton = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        actionToolBar = new javax.swing.JToolBar();
        applyButton = new javax.swing.JButton();
        revertButton = new javax.swing.JButton();
        profileToolBar = new javax.swing.JToolBar();
        propertiesButton = new javax.swing.JButton();
        profilesComboBox = new javax.swing.JComboBox();
        loadProfileButton = new javax.swing.JButton();
        devicePanel = new org.danizmax.jsymphonic.gui.device.DevicePanel();
        localTabbedPane = new javax.swing.JTabbedPane();
        localPanel = new org.danizmax.jsymphonic.gui.local.LocalPanel();
        exportLocalPanel = new org.danizmax.jsymphonic.gui.local.LocalPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jsymphonicMenu = new javax.swing.JMenu();
        propertiesMenuItem = new javax.swing.JMenuItem();
        quitMenuItem = new javax.swing.JMenuItem();
        transferMenu = new javax.swing.JMenu();
        importMenuItem = new javax.swing.JMenuItem();
        exportMenuItem = new javax.swing.JMenuItem();
        deleteMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        applyMenuItem = new javax.swing.JMenuItem();
        revertMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        helpMenuItem = new javax.swing.JMenuItem();
        logMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("localization/language"); // NOI18N
        setTitle(bundle.getString("global.version")); // NOI18N
        setMinimumSize(new java.awt.Dimension(640, 480));
        setName("Form"); // NOI18N

        mainToolBar.setFloatable(false);
        mainToolBar.setRollover(true);
        mainToolBar.setName("mainToolBar"); // NOI18N

        buttonsToolBar.setRollover(true);
        buttonsToolBar.setName("buttonsToolBar"); // NOI18N

        reploadButton.setFont(new java.awt.Font("Dialog", 0, 12));
        reploadButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/reload.png"))); // NOI18N
        reploadButton.setToolTipText(bundle.getString("JSymphonicWindow.reploadButton.toolTipText")); // NOI18N
        reploadButton.setFocusable(false);
        reploadButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        reploadButton.setName("reploadButton"); // NOI18N
        reploadButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        reploadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reploadButtonActionPerformed(evt);
            }
        });
        buttonsToolBar.add(reploadButton);

        importButton.setFont(new java.awt.Font("Dialog", 0, 12));
        importButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/import.png"))); // NOI18N
        importButton.setToolTipText(bundle.getString("JSymphonicWindow.importMenuItem.text")); // NOI18N
        importButton.setFocusable(false);
        importButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        importButton.setName("importButton"); // NOI18N
        importButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        importButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importButtonActionPerformed(evt);
            }
        });
        buttonsToolBar.add(importButton);

        exportButton.setFont(new java.awt.Font("Dialog", 0, 12));
        exportButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/export.png"))); // NOI18N
        exportButton.setToolTipText(bundle.getString("JSymphonicWindow.exportMenuItem.text")); // NOI18N
        exportButton.setFocusable(false);
        exportButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        exportButton.setName("exportButton"); // NOI18N
        exportButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });
        buttonsToolBar.add(exportButton);

        deleteButton.setFont(new java.awt.Font("Dialog", 0, 12));
        deleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/remove.png"))); // NOI18N
        deleteButton.setToolTipText(bundle.getString("global.Delete")); // NOI18N
        deleteButton.setFocusable(false);
        deleteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        deleteButton.setName("deleteButton"); // NOI18N
        deleteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        buttonsToolBar.add(deleteButton);

        actionToolBar.setRollover(true);
        actionToolBar.setName("actionToolBar"); // NOI18N

        applyButton.setFont(new java.awt.Font("Dialog", 0, 12));
        applyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/transfer.png"))); // NOI18N
        applyButton.setToolTipText(bundle.getString("JSymphonicWindow.Apply_all_changes")); // NOI18N
        applyButton.setFocusable(false);
        applyButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        applyButton.setName("applyButton"); // NOI18N
        applyButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        applyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyButtonActionPerformed(evt);
            }
        });
        actionToolBar.add(applyButton);

        revertButton.setFont(new java.awt.Font("Dialog", 0, 12));
        revertButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/revert.png"))); // NOI18N
        revertButton.setToolTipText(bundle.getString("JSymphonicWindow.revertMenuItem.text")); // NOI18N
        revertButton.setFocusable(false);
        revertButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        revertButton.setName("revertButton"); // NOI18N
        revertButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        revertButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                revertButtonActionPerformed(evt);
            }
        });
        actionToolBar.add(revertButton);

        buttonsToolBar.add(actionToolBar);

        mainToolBar.add(buttonsToolBar);

        profileToolBar.setRollover(true);
        profileToolBar.setToolTipText(bundle.getString("JSymphonicWindow.profileToolBar.toolTipText")); // NOI18N
        profileToolBar.setMargin(new java.awt.Insets(0, 0, 0, 10));
        profileToolBar.setName("profileToolBar"); // NOI18N

        propertiesButton.setFont(new java.awt.Font("Dialog", 0, 12));
        propertiesButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/configure.png"))); // NOI18N
        propertiesButton.setToolTipText(bundle.getString("JSymphonicWindow.propertiesMenuItem.text")); // NOI18N
        propertiesButton.setFocusable(false);
        propertiesButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        propertiesButton.setName("propertiesButton"); // NOI18N
        propertiesButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        propertiesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                propertiesButtonActionPerformed(evt);
            }
        });
        profileToolBar.add(propertiesButton);

        profilesComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        profilesComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        profilesComboBox.setMaximumSize(new java.awt.Dimension(200, 23));
        profilesComboBox.setMinimumSize(new java.awt.Dimension(50, 23));
        profilesComboBox.setName("profilesComboBox"); // NOI18N
        profilesComboBox.setPreferredSize(new java.awt.Dimension(50, 23));
        profileToolBar.add(profilesComboBox);

        loadProfileButton.setFont(new java.awt.Font("Dialog", 0, 12));
        loadProfileButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/load.png"))); // NOI18N
        loadProfileButton.setToolTipText(bundle.getString("JSymphonicWindow.loadProfileButton.toolTipText")); // NOI18N
        loadProfileButton.setFocusable(false);
        loadProfileButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        loadProfileButton.setName("loadProfileButton"); // NOI18N
        loadProfileButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        loadProfileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadProfileButtonActionPerformed(evt);
            }
        });
        profileToolBar.add(loadProfileButton);

        mainToolBar.add(profileToolBar);

        devicePanel.setName("devicePanel"); // NOI18N

        localTabbedPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        localTabbedPane.setName("localTabbedPane"); // NOI18N

        localPanel.setName("localPanel"); // NOI18N
        localTabbedPane.addTab(bundle.getString("JSymphonicWindow.localPanel.TabConstraints.tabTitle"), localPanel); // NOI18N

        exportLocalPanel.setName("exportLocalPanel"); // NOI18N
        localTabbedPane.addTab(bundle.getString("JSymphonicWindow.exportLocalPanel.TabConstraints.tabTitle"), exportLocalPanel); // NOI18N

        jMenuBar1.setName("jMenuBar1"); // NOI18N

        jsymphonicMenu.setText(bundle.getString("JSymphonicWindow.jsymphonicMenu.text")); // NOI18N
        jsymphonicMenu.setFont(new java.awt.Font("Dialog", 0, 12));
        jsymphonicMenu.setName("jsymphonicMenu"); // NOI18N

        propertiesMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.ALT_MASK));
        propertiesMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        propertiesMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/configure.png"))); // NOI18N
        propertiesMenuItem.setText(bundle.getString("JSymphonicWindow.propertiesMenuItem.text")); // NOI18N
        propertiesMenuItem.setName("propertiesMenuItem"); // NOI18N
        propertiesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                propertiesMenuItemActionPerformed(evt);
            }
        });
        jsymphonicMenu.add(propertiesMenuItem);

        quitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.ALT_MASK));
        quitMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        quitMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/exit.png"))); // NOI18N
        quitMenuItem.setText(bundle.getString("JSymphonicWindow.quitMenuItem.text")); // NOI18N
        quitMenuItem.setName("quitMenuItem"); // NOI18N
        quitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitMenuItemActionPerformed(evt);
            }
        });
        jsymphonicMenu.add(quitMenuItem);

        jMenuBar1.add(jsymphonicMenu);

        transferMenu.setText(bundle.getString("JSymphonicWindow.transferMenu.text")); // NOI18N
        transferMenu.setFont(new java.awt.Font("Dialog", 0, 12));
        transferMenu.setName("transferMenu"); // NOI18N

        importMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.ALT_MASK));
        importMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        importMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/import.png"))); // NOI18N
        importMenuItem.setText(bundle.getString("JSymphonicWindow.importMenuItem.text")); // NOI18N
        importMenuItem.setName("importMenuItem"); // NOI18N
        importMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importMenuItemActionPerformed(evt);
            }
        });
        transferMenu.add(importMenuItem);

        exportMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.ALT_MASK));
        exportMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        exportMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/export.png"))); // NOI18N
        exportMenuItem.setText(bundle.getString("JSymphonicWindow.exportMenuItem.text")); // NOI18N
        exportMenuItem.setName("exportMenuItem"); // NOI18N
        exportMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportMenuItemActionPerformed(evt);
            }
        });
        transferMenu.add(exportMenuItem);

        deleteMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.ALT_MASK));
        deleteMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        deleteMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/remove.png"))); // NOI18N
        deleteMenuItem.setText(bundle.getString("global.Delete")); // NOI18N
        deleteMenuItem.setName("deleteMenuItem"); // NOI18N
        deleteMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteMenuItemActionPerformed(evt);
            }
        });
        transferMenu.add(deleteMenuItem);

        jSeparator1.setName("jSeparator1"); // NOI18N
        transferMenu.add(jSeparator1);

        applyMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.ALT_MASK));
        applyMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        applyMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/transfer.png"))); // NOI18N
        applyMenuItem.setText(bundle.getString("JSymphonicWindow.Apply_all_changes")); // NOI18N
        applyMenuItem.setName("applyMenuItem"); // NOI18N
        applyMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyMenuItemActionPerformed(evt);
            }
        });
        transferMenu.add(applyMenuItem);

        revertMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.ALT_MASK));
        revertMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        revertMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/revert.png"))); // NOI18N
        revertMenuItem.setText(bundle.getString("JSymphonicWindow.revertMenuItem.text")); // NOI18N
        revertMenuItem.setName("revertMenuItem"); // NOI18N
        revertMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                revertMenuItemActionPerformed(evt);
            }
        });
        transferMenu.add(revertMenuItem);

        jMenuBar1.add(transferMenu);

        helpMenu.setText(bundle.getString("global.Help")); // NOI18N
        helpMenu.setFont(new java.awt.Font("Dialog", 0, 12));
        helpMenu.setName("helpMenu"); // NOI18N

        helpMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.ALT_MASK));
        helpMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        helpMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/help.png"))); // NOI18N
        helpMenuItem.setText(bundle.getString("global.Help")); // NOI18N
        helpMenuItem.setName("helpMenuItem"); // NOI18N
        helpMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(helpMenuItem);

        logMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.ALT_MASK));
        logMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        logMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/run.png"))); // NOI18N
        logMenuItem.setText(bundle.getString("JSymphonicWindow.logMenuItem.text")); // NOI18N
        logMenuItem.setName("logMenuItem"); // NOI18N
        logMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(logMenuItem);

        aboutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK));
        aboutMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        aboutMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/about.png"))); // NOI18N
        aboutMenuItem.setText(bundle.getString("JSymphonicWindow.About_JSymphonic")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        jMenuBar1.add(helpMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(localTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(devicePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(mainToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, 742, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(mainToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(localTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE)
                    .addComponent(devicePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void reploadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reploadButtonActionPerformed
        if(devicePanel.Ismounted()){
            devicePanel.reloadTree();
            localPanel.reloadTree();
        }else{
            JOptionPane.showMessageDialog(this, java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicWindow.The_device_is_not_mounted"), java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicWindow.Mounting_the_device"), JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_reploadButtonActionPerformed

    private void propertiesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_propertiesButtonActionPerformed
        JSymphonicProperties jsp = new JSymphonicProperties(sHandler, this);
        jsp.setLocationRelativeTo(this);
        jsp.setVisible(true);
}//GEN-LAST:event_propertiesButtonActionPerformed

    private void loadProfileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadProfileButtonActionPerformed
        loadNewConfig();
    }//GEN-LAST:event_loadProfileButtonActionPerformed

private void importButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importButtonActionPerformed
    importMenuItemActionPerformed(evt);
}//GEN-LAST:event_importButtonActionPerformed

private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
    JSymphonicAbout ab = new JSymphonicAbout(java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicWindow.About_JSymphonic"), java.util.ResourceBundle.getBundle("localization/language").getString("global.version"),java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicWindow.Thanks_for_using"), java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicWindow.The_JSymphonic_team"), java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicWindow.aboutText01")
 +java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicWindow.aboutText02")
 +java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicWindow.aboutText03")
 +java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicWindow.aboutText04")
 +"\n\n" 
 +java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicWindow.aboutText05")
 +java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicWindow.aboutText06")
 +java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicWindow.aboutText07")
 +java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicWindow.aboutText08")
 +"\n\n"  
 +java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicWindow.aboutText09")
 +java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicWindow.aboutText10")
 +java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicWindow.aboutText11"));
    ab.setLocationRelativeTo(this);
    ab.setVisible(true);
}//GEN-LAST:event_aboutMenuItemActionPerformed

private void propertiesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_propertiesMenuItemActionPerformed
    propertiesButtonActionPerformed(evt);
}//GEN-LAST:event_propertiesMenuItemActionPerformed

private void quitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitMenuItemActionPerformed
    System.exit(0);
}//GEN-LAST:event_quitMenuItemActionPerformed

private void importMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importMenuItemActionPerformed
    //if(JOptionPane.showConfirmDialog(this, "Import Selected tracks to the device" +" "+  devicePanel.getDeviceName() + " " + "?" , "Import files...", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
        //start transfer
        devicePanel.scheduleTrackImport(localPanel.getSelectedTracks());
        devicePanel.reloadTree();
    //}
}//GEN-LAST:event_importMenuItemActionPerformed

private void exportMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportMenuItemActionPerformed
//    if(JOptionPane.showConfirmDialog(this, "Export Selected tracks from the device" +" "+  devicePanel.getDeviceName() + " " + "?" , "Export files...", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
        //start transfer
        devicePanel.scheduleTrackExport();
        devicePanel.reloadTree();
   // }
}//GEN-LAST:event_exportMenuItemActionPerformed

private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed
    exportMenuItemActionPerformed(evt);
}//GEN-LAST:event_exportButtonActionPerformed

private void revertButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_revertButtonActionPerformed
    revertMenuItemActionPerformed(evt);
}//GEN-LAST:event_revertButtonActionPerformed

private void applyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyButtonActionPerformed
    applyMenuItemActionPerformed(evt);
}//GEN-LAST:event_applyButtonActionPerformed

private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
    deleteMenuItemActionPerformed(evt);
}//GEN-LAST:event_deleteButtonActionPerformed

private void deleteMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteMenuItemActionPerformed
    devicePanel.deleteSelectedTracks();
    devicePanel.reloadTree();
}//GEN-LAST:event_deleteMenuItemActionPerformed

private void applyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyMenuItemActionPerformed
    if(JOptionPane.showConfirmDialog(this, java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicWindow.Do_you_want_to_apply_all_changes"), java.util.ResourceBundle.getBundle("localization/language").getString("JSymphonicWindow.Apply_all_changes"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
        devicePanel.applyChanges();
    }
}//GEN-LAST:event_applyMenuItemActionPerformed

private void revertMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_revertMenuItemActionPerformed
    devicePanel.cancelChanges();
}//GEN-LAST:event_revertMenuItemActionPerformed

private void helpMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpMenuItemActionPerformed
    HelpFrame hlpF = new HelpFrame("http://google.com");
    hlpF.setLocationRelativeTo(this);
    hlpF.setVisible(true);
}//GEN-LAST:event_helpMenuItemActionPerformed

private void logMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logMenuItemActionPerformed
   LogFrame lf = new LogFrame(System.getProperty("user.dir") + System.getProperty("file.separator") + logFileName);
   lf.setLocationRelativeTo(this);
   lf.setVisible(true);
}//GEN-LAST:event_logMenuItemActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JToolBar actionToolBar;
    private javax.swing.JButton applyButton;
    private javax.swing.JMenuItem applyMenuItem;
    private javax.swing.JToolBar buttonsToolBar;
    private javax.swing.JButton deleteButton;
    private javax.swing.JMenuItem deleteMenuItem;
    private org.danizmax.jsymphonic.gui.device.DevicePanel devicePanel;
    private javax.swing.JButton exportButton;
    private org.danizmax.jsymphonic.gui.local.LocalPanel exportLocalPanel;
    private javax.swing.JMenuItem exportMenuItem;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem helpMenuItem;
    private javax.swing.JButton importButton;
    private javax.swing.JMenuItem importMenuItem;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JMenu jsymphonicMenu;
    private javax.swing.JButton loadProfileButton;
    private org.danizmax.jsymphonic.gui.local.LocalPanel localPanel;
    private javax.swing.JTabbedPane localTabbedPane;
    private javax.swing.JMenuItem logMenuItem;
    private javax.swing.JToolBar mainToolBar;
    private javax.swing.JToolBar profileToolBar;
    private javax.swing.JComboBox profilesComboBox;
    private javax.swing.JButton propertiesButton;
    private javax.swing.JMenuItem propertiesMenuItem;
    private javax.swing.JMenuItem quitMenuItem;
    private javax.swing.JButton reploadButton;
    private javax.swing.JButton revertButton;
    private javax.swing.JMenuItem revertMenuItem;
    private javax.swing.JMenu transferMenu;
    // End of variables declaration//GEN-END:variables

    public TreeMap getThemeMap() {
        return themeMap;
    }

    public TreeMap getLangMap() {
        return langMap;
    }
    
}
