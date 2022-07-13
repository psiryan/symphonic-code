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
 * mainJSymphonicFrame.java
 *
 * Created on May 24, 2009, 8:57:41 PM
 */

package org.danizmax.jsymphonic.gui;

import java.awt.event.ActionEvent;
//import javazoom.jlgui.basicplayer.BasicPlayerException;
import java.awt.event.WindowEvent;
import org.danizmax.jsymphonic.gui.settings.SettingsDialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
//import javazoom.jlgui.basicplayer.BasicPlayer;
import net.infonode.tabbedpanel.TabbedPanel;
import net.infonode.tabbedpanel.titledtab.TitledTab;
import net.infonode.tabbedpanel.titledtab.TitledTabProperties;
import net.infonode.util.Direction;
import org.danizmax.jsymphonic.gui.device.DevicePanel;
import org.danizmax.jsymphonic.gui.device.TransferInterface;
import org.danizmax.jsymphonic.gui.local.LocalPanel;
import org.danizmax.jsymphonic.gui.settings.ProfileElement;
import org.danizmax.jsymphonic.gui.settings.SettingsHandler;
import org.naurd.media.jsymphonic.title.Title;

/**
 *
 * @author danizmax
 */
public class JSymphonicFrame extends javax.swing.JFrame implements ActionListener, TransferInterface, WindowListener {

    /**
     * @return the logger
     */
    public static Logger getLogger() {
        return logger;
    }

    private DevicePanel devicePanel;
    private TabbedPanel tabbedPanel;
    private LocalPanel localPanel;
    private LocalPanel exportPanel;
    private JSplitPane jSP;
    private Dimension windowSize;
    private SettingsHandler sHandler;
    private TreeMap langMap;
    private TreeMap themeMap;
    //private BasicPlayer player;

    private static Logger logger = Logger.getLogger("org.danizmax.jsymphonic.gui2.JSymphonicFrame");
    
    
    /** Creates new form mainJSymphonicFrame */
    public JSymphonicFrame(SettingsHandler sHandler, TreeMap langMap, TreeMap themeMap) {
        initComponents();
        this.addWindowListener(this);
        setTitle("JSymphonic" + " " + JSymphonic.version);
        this.setIconImage(new ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/icons/js_logo16.png")).getImage());
        this.sHandler = sHandler;
        this.langMap = langMap;
        this.themeMap = themeMap;

        ///JSymphonicMiniPlayer jsmp = new JSymphonicMiniPlayer();
        ///playerPanel.add(jsmp, BorderLayout.CENTER);
        ///jsmp.play("/home/danizmax/Music/Buddha-Bar/Buddha-Bar VIII (CD1 - Paris) (By Sam Popat) (2006)/12 - Nomadix - Chura Liya.mp3");

        tabbedPanel = new TabbedPanel();
        localPanel = new LocalPanel();
        exportPanel = new LocalPanel();
        devicePanel =  new DevicePanel();
        devicePanel.addTransferEventListener(this);
        localPanel.setBorder(null);
        exportPanel.setBorder(null);
        // Configuring titled tab
        TitledTabProperties tabProperties = new TitledTabProperties();
        tabProperties.setHighlightedRaised(0).
        getNormalProperties().
        setDirection(Direction.UP).
        getComponentProperties().
        setBackgroundColor(null);
        tabProperties.getHighlightedProperties().getComponentProperties().
        setBackgroundColor(Color.WHITE);

        TitledTab localTab = new TitledTab("Local Filesystem", new ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/icons/drive-local.png")), localPanel, null);
        TitledTab exportTab = new TitledTab("Export Filesystem", new ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/icons/folder-export.png")), exportPanel, null);
        localTab.getProperties().addSuperObject(tabProperties);
        tabbedPanel.addTab(localTab);
        exportTab.getProperties().addSuperObject(tabProperties);
        tabbedPanel.addTab(exportTab);
        tabbedPanel.getProperties().setTabAreaOrientation(Direction.LEFT);
        jSP = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabbedPanel, devicePanel);
        jSP.setBorder(new EmptyBorder(0, 0, 0, 0));
        jSP.setContinuousLayout(true);
        jSP.setDividerSize(5);
        contentPanel.setLayout(new BorderLayout(10, 0));
        contentPanel.add(jSP, BorderLayout.CENTER);

        loadNewConfig();
        logger.info("Ready.");
    }

    public void loadNewConfig(){

        //interface related
        changeLAF(sHandler.getTheme());
        setToolbarTextVisible(sHandler.isShowToolbarText());

        //profile data
        ProfileElement pe;
        try{
            pe =(ProfileElement) sHandler.getProfiles().get(sHandler.getActiveProfile());
        }
        catch(Exception e){
            logger.severe("Error while loading the settings. Please check your configuration.");
            return;
        }
        devicePanel.setAlwaysTranscode(sHandler.isAlwaysTranscode());
        devicePanel.setTranscodeBitrate(Integer.valueOf(((String)sHandler.getBitrate()).substring(0, 2).trim()));
        devicePanel.setDevicePath(pe.getDevicePath());
        devicePanel.setDeviceGeneration(pe.getDeviceGeneration());
        devicePanel.setExportPath(pe.getExportPath());
        devicePanel.setTempPath(pe.isTempSameAsDevicePath() ? pe.getDevicePath() : pe.getTempPath());



        Title.setTryReadTagInfo(!sHandler.isNeverReadTags());
        Title.setTryReadTagInfoPattern(sHandler.getTagStructure());

        Vector profiles = sHandler.getProfiles();
        Iterator it = profiles.iterator();
        profileMenuButtonGroup = new ButtonGroup();
        profilesMenu.removeAll();
        int i = 1;
        while(it.hasNext()){
            ProfileElement p = (ProfileElement) it.next();
            JRadioButtonMenuItem rbmi = new JRadioButtonMenuItem(p.getName(), (p.equals(pe) ? true : false));
            rbmi.addActionListener(this);
            rbmi.setAccelerator(KeyStroke.getKeyStroke ("alt " + i ));
            rbmi.setFont(new java.awt.Font("Dialog", 0, 12));
            rbmi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/icons/profile.png")));
            profileMenuButtonGroup.add(rbmi);
            profilesMenu.add(rbmi);
            i++;
        }

        this.setSize(sHandler.getWidth(), sHandler.getHeight());
        this.setWindowState(sHandler.getWindowState());
        jSP.setDividerLocation(sHandler.getDeviderLocation());

        transferMenu.add(saveMenuItem);

        devicePanel.mountDevice(true);

        localPanel.loadSupportedFileFormats(pe.getDeviceGeneration());
        localPanel.setLocalPaths(pe.getLocalPaths());
        localPanel.reloadTree();

        //true if export and local path are the same
        if(pe.isExportSameAsLocalPath() == true){
            tabbedPanel.getTabAt(1).setVisible(false);
        }else{
            tabbedPanel.getTabAt(1).setVisible(true);
            exportPanel.loadSupportedFileFormats(pe.getDeviceGeneration());
            Vector path = new Vector();
            path.add(pe.getExportPath());
            exportPanel.setLocalPaths(path);
            exportPanel.reloadTree();
        }

        JSymphonic.initLogger();
    }


    private void setToolbarTextVisible(boolean visible){
        if(visible){
            ImportButton.setText("Import");
            ExportButton.setText("Export");
            RemoveButton.setText("Remove");
            RevertButton.setText("Revert");
            SaveButton.setText("Save");
            compactButton.setText("Compact");
            settingsButton.setText("Settings");
        }else{
            ImportButton.setText("");
            ExportButton.setText("");
            RemoveButton.setText("");
            RevertButton.setText("");
            SaveButton.setText("");
            compactButton.setText("");
            settingsButton.setText("");
        }
    }

    public void changeLAF(String laf) {
        String look = (String)themeMap.get(laf);
        try {
            if(look != null){
                UIManager.setLookAndFeel(look);
                SwingUtilities.updateComponentTreeUI(this);
                SwingUtilities.updateComponentTreeUI(contentPanel);
                SwingUtilities.updateComponentTreeUI(devicePanel);
                SwingUtilities.updateComponentTreeUI(localPanel);
                SwingUtilities.updateComponentTreeUI(devicePanel.getTreePopUp());
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JSymphonicFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(JSymphonicFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(JSymphonicFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(JSymphonicFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
   }

    private void setWindowState(int state){
        if(state == SettingsHandler.WINDOW_STATE_MINI){
            jSP.setDividerSize(0);
            windowSize = this.getSize();
            this.setSize(sHandler.getWidth(), sHandler.getHeight());
            tabbedPanel.setVisible(false);
        }else if(state == SettingsHandler.WINDOW_STATE_NORMAL){
            this.setSize(sHandler.getWidth(), sHandler.getHeight());
            jSP.setDividerSize(5);
            jSP.setDividerLocation(-1);
            tabbedPanel.setVisible(true);
        }
        sHandler.setWindowState(state);
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        profileMenuButtonGroup = new javax.swing.ButtonGroup();
        iconToolBar = new javax.swing.JToolBar();
        ImportButton = new javax.swing.JButton();
        ExportButton = new javax.swing.JButton();
        RemoveButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        RevertButton = new javax.swing.JButton();
        SaveButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        compactButton = new javax.swing.JButton();
        settingsButton = new javax.swing.JButton();
        contentPanel = new javax.swing.JPanel();
        playerPanel = new javax.swing.JPanel();
        jSymphonicMenuBar = new javax.swing.JMenuBar();
        jSymphonicMenu = new javax.swing.JMenu();
        settingsMenuItem = new javax.swing.JMenuItem();
        resizeMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();
        transferMenu = new javax.swing.JMenu();
        importMenuItem = new javax.swing.JMenuItem();
        exportMenuItem = new javax.swing.JMenuItem();
        removeMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        revertMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        profilesMenu = new javax.swing.JMenu();
        helpMenu = new javax.swing.JMenu();
        helpContentsMenuItem = new javax.swing.JMenuItem();
        showLogMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("JSymphonic");
        setMinimumSize(new java.awt.Dimension(400, 500));

        iconToolBar.setFloatable(false);
        iconToolBar.setRollover(true);
        iconToolBar.setBorderPainted(false);

        ImportButton.setFont(new java.awt.Font("Dialog", 0, 12));
        ImportButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/icons/import.png"))); // NOI18N
        ImportButton.setText("Import");
        ImportButton.setToolTipText("Import");
        ImportButton.setFocusable(false);
        ImportButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ImportButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ImportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ImportButtonActionPerformed(evt);
            }
        });
        iconToolBar.add(ImportButton);

        ExportButton.setFont(new java.awt.Font("Dialog", 0, 12));
        ExportButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/icons/export.png"))); // NOI18N
        ExportButton.setText("Export");
        ExportButton.setToolTipText("Export");
        ExportButton.setFocusable(false);
        ExportButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ExportButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ExportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportButtonActionPerformed(evt);
            }
        });
        iconToolBar.add(ExportButton);

        RemoveButton.setFont(new java.awt.Font("Dialog", 0, 12));
        RemoveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/icons/remove.png"))); // NOI18N
        RemoveButton.setText("Remove");
        RemoveButton.setToolTipText("Remove");
        RemoveButton.setFocusable(false);
        RemoveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        RemoveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        RemoveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RemoveButtonActionPerformed(evt);
            }
        });
        iconToolBar.add(RemoveButton);
        iconToolBar.add(jSeparator2);

        RevertButton.setFont(new java.awt.Font("Dialog", 0, 12));
        RevertButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/icons/revert.png"))); // NOI18N
        RevertButton.setText("Revert");
        RevertButton.setToolTipText("Revert");
        RevertButton.setFocusable(false);
        RevertButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        RevertButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        RevertButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RevertButtonActionPerformed(evt);
            }
        });
        iconToolBar.add(RevertButton);

        SaveButton.setFont(new java.awt.Font("Dialog", 0, 12));
        SaveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/icons/save.png"))); // NOI18N
        SaveButton.setText("Save");
        SaveButton.setToolTipText("Save");
        SaveButton.setFocusable(false);
        SaveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        SaveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        SaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveButtonActionPerformed(evt);
            }
        });
        iconToolBar.add(SaveButton);
        iconToolBar.add(jSeparator1);

        compactButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        compactButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/icons/resize.png"))); // NOI18N
        compactButton.setText("Compact");
        compactButton.setToolTipText("Compact");
        compactButton.setFocusable(false);
        compactButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        compactButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        compactButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                compactButtonActionPerformed(evt);
            }
        });
        iconToolBar.add(compactButton);

        settingsButton.setFont(new java.awt.Font("Dialog", 0, 12));
        settingsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/icons/configure.png"))); // NOI18N
        settingsButton.setText("Settings");
        settingsButton.setToolTipText("Settings");
        settingsButton.setFocusable(false);
        settingsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        settingsButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        settingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsButtonActionPerformed(evt);
            }
        });
        iconToolBar.add(settingsButton);

        org.jdesktop.layout.GroupLayout contentPanelLayout = new org.jdesktop.layout.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 859, Short.MAX_VALUE)
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 429, Short.MAX_VALUE)
        );

        playerPanel.setLayout(new java.awt.BorderLayout());

        jSymphonicMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/icons/js_logo16.png"))); // NOI18N
        jSymphonicMenu.setMnemonic('J');
        jSymphonicMenu.setText("JSymphonic");
        jSymphonicMenu.setFont(new java.awt.Font("Dialog", 0, 12));

        settingsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.ALT_MASK));
        settingsMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        settingsMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/icons/configure.png"))); // NOI18N
        settingsMenuItem.setText("Settings");
        settingsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsMenuItemActionPerformed(evt);
            }
        });
        jSymphonicMenu.add(settingsMenuItem);

        resizeMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.ALT_MASK));
        resizeMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        resizeMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/icons/resize.png"))); // NOI18N
        resizeMenuItem.setText("Compact");
        resizeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resizeMenuItemActionPerformed(evt);
            }
        });
        jSymphonicMenu.add(resizeMenuItem);

        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.ALT_MASK));
        exitMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        exitMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/icons/exit.png"))); // NOI18N
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        jSymphonicMenu.add(exitMenuItem);

        jSymphonicMenuBar.add(jSymphonicMenu);

        transferMenu.setText("Transfer");
        transferMenu.setFont(new java.awt.Font("Dialog", 0, 12));

        importMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.ALT_MASK));
        importMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        importMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/icons/import.png"))); // NOI18N
        importMenuItem.setText("Import");
        importMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importMenuItemActionPerformed(evt);
            }
        });
        transferMenu.add(importMenuItem);

        exportMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.ALT_MASK));
        exportMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        exportMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/icons/export.png"))); // NOI18N
        exportMenuItem.setText("Export");
        exportMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportMenuItemActionPerformed(evt);
            }
        });
        transferMenu.add(exportMenuItem);

        removeMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.ALT_MASK));
        removeMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        removeMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/icons/remove.png"))); // NOI18N
        removeMenuItem.setText("Remove");
        removeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeMenuItemActionPerformed(evt);
            }
        });
        transferMenu.add(removeMenuItem);
        transferMenu.add(jSeparator3);

        revertMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.ALT_MASK));
        revertMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        revertMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/icons/revert.png"))); // NOI18N
        revertMenuItem.setText("Revert");
        revertMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                revertMenuItemActionPerformed(evt);
            }
        });
        transferMenu.add(revertMenuItem);

        saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK));
        saveMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        saveMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/icons/save.png"))); // NOI18N
        saveMenuItem.setText("Save");
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItemActionPerformed(evt);
            }
        });
        transferMenu.add(saveMenuItem);

        jSymphonicMenuBar.add(transferMenu);

        profilesMenu.setText("Profiles");
        profilesMenu.setFont(new java.awt.Font("Dialog", 0, 12));
        jSymphonicMenuBar.add(profilesMenu);

        helpMenu.setText("Help");
        helpMenu.setFont(new java.awt.Font("Dialog", 0, 12));

        helpContentsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.ALT_MASK));
        helpContentsMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        helpContentsMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/icons/help.png"))); // NOI18N
        helpContentsMenuItem.setText("Help Contents");
        helpContentsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpContentsMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(helpContentsMenuItem);

        showLogMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.ALT_MASK));
        showLogMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        showLogMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/icons/status-messags.png"))); // NOI18N
        showLogMenuItem.setText("Show Log");
        showLogMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showLogMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(showLogMenuItem);

        aboutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK));
        aboutMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        aboutMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/icons/about.png"))); // NOI18N
        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        jSymphonicMenuBar.add(helpMenu);

        setJMenuBar(jSymphonicMenuBar);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(iconToolBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 859, Short.MAX_VALUE)
            .add(playerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 859, Short.MAX_VALUE)
            .add(contentPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(iconToolBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(playerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(contentPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void compactButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_compactButtonActionPerformed
        resizeMenuItemActionPerformed(evt);
    }//GEN-LAST:event_compactButtonActionPerformed

    private void settingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsButtonActionPerformed
        settingsMenuItemActionPerformed(evt);
    }//GEN-LAST:event_settingsButtonActionPerformed

    private void ImportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ImportButtonActionPerformed
        importMenuItemActionPerformed(evt);
    }//GEN-LAST:event_ImportButtonActionPerformed

    private void ExportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportButtonActionPerformed
        exportMenuItemActionPerformed(evt);
    }//GEN-LAST:event_ExportButtonActionPerformed

    private void SaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveButtonActionPerformed
        saveMenuItemActionPerformed(evt);
    }//GEN-LAST:event_SaveButtonActionPerformed

    private void RevertButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RevertButtonActionPerformed
        revertMenuItemActionPerformed(evt);
    }//GEN-LAST:event_RevertButtonActionPerformed

    private void RemoveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RemoveButtonActionPerformed
        removeMenuItemActionPerformed(evt);
    }//GEN-LAST:event_RemoveButtonActionPerformed

    private void settingsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsMenuItemActionPerformed
        SettingsDialog sf = new SettingsDialog(sHandler, langMap, themeMap);
        sf.setLocationRelativeTo(this);
        sf.setModal(true);
        sf.setVisible(true);

        if(sf.isSuccess()){
            //update JSymphonic
            loadNewConfig();
        }

        sf.dispose();
    }//GEN-LAST:event_settingsMenuItemActionPerformed

    private void resizeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resizeMenuItemActionPerformed
        if(sHandler.getWindowState() == SettingsHandler.WINDOW_STATE_MINI){
            setWindowState(SettingsHandler.WINDOW_STATE_NORMAL);
        }else if(sHandler.getWindowState() == SettingsHandler.WINDOW_STATE_NORMAL){
            setWindowState(SettingsHandler.WINDOW_STATE_MINI);
        }
    }//GEN-LAST:event_resizeMenuItemActionPerformed

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void importMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importMenuItemActionPerformed
        devicePanel.scheduleTrackImport(localPanel.getSelectedTracks());
    }//GEN-LAST:event_importMenuItemActionPerformed

    private void exportMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportMenuItemActionPerformed
        devicePanel.scheduleTrackExport();
        devicePanel.reloadTree();
    }//GEN-LAST:event_exportMenuItemActionPerformed

    private void removeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeMenuItemActionPerformed
        devicePanel.deleteSelectedTracks();
        devicePanel.reloadTree();
    }//GEN-LAST:event_removeMenuItemActionPerformed

    private void revertMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_revertMenuItemActionPerformed
        devicePanel.cancelChanges();
    }//GEN-LAST:event_revertMenuItemActionPerformed

    private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItemActionPerformed
        if(JOptionPane.showConfirmDialog(this, java.util.ResourceBundle.getBundle("localization/jsymphonicwindow").getString("JSymphonicWindow.Do_you_want_to_apply_all_changes"), java.util.ResourceBundle.getBundle("localization/jsymphonicwindow").getString("JSymphonicWindow.Apply_all_changes"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
            devicePanel.applyChanges();
        }
    }//GEN-LAST:event_saveMenuItemActionPerformed

    private void helpContentsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpContentsMenuItemActionPerformed
        HelpFrame hlpF;
        File localREADME = new File("./README_v0.3.0b.html");

        // Try to find the local file
        if(localREADME.exists()){
            // If it exist, use it
            hlpF = new HelpFrame("file:"+File.separator+File.separator+File.separator+localREADME.getAbsolutePath().replace(File.separator+".", ""));    }
        else{
            // Else, use the online doc
            hlpF = new HelpFrame("http://symphonic.sourceforge.net/e107_themes/images/documentation/help.html");
        }
        hlpF.setLocationRelativeTo(this);
        hlpF.setVisible(true);
    }//GEN-LAST:event_helpContentsMenuItemActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        JSymphonicAbout ab = new JSymphonicAbout(java.util.ResourceBundle.getBundle("localization/jsymphonicwindow").getString("JSymphonicWindow.About_JSymphonic"),"JSymphonic", JSymphonic.version, java.util.ResourceBundle.getBundle("localization/jsymphonicwindow").getString("JSymphonicWindow.Thanks_for_using"), java.util.ResourceBundle.getBundle("localization/jsymphonicwindow").getString("JSymphonicWindow.The_JSymphonic_team"));
        ab.setLocationRelativeTo(this);
        ab.setVisible(true);
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void showLogMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showLogMenuItemActionPerformed
        LogFrame lf = new LogFrame(System.getProperty("user.dir") + System.getProperty("file.separator") + SettingsHandler.LOG_FILE_NAME , JSymphonic.glHandler);
       if(lf.isSourceValid()){
           lf.setLocationRelativeTo(this);
           lf.setVisible(true);
       }else{
           lf = null;
       }
    }//GEN-LAST:event_showLogMenuItemActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ExportButton;
    private javax.swing.JButton ImportButton;
    private javax.swing.JButton RemoveButton;
    private javax.swing.JButton RevertButton;
    private javax.swing.JButton SaveButton;
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JButton compactButton;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenuItem exportMenuItem;
    private javax.swing.JMenuItem helpContentsMenuItem;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JToolBar iconToolBar;
    private javax.swing.JMenuItem importMenuItem;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JMenu jSymphonicMenu;
    private javax.swing.JMenuBar jSymphonicMenuBar;
    private javax.swing.JPanel playerPanel;
    private javax.swing.ButtonGroup profileMenuButtonGroup;
    private javax.swing.JMenu profilesMenu;
    private javax.swing.JMenuItem removeMenuItem;
    private javax.swing.JMenuItem resizeMenuItem;
    private javax.swing.JMenuItem revertMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JButton settingsButton;
    private javax.swing.JMenuItem settingsMenuItem;
    private javax.swing.JMenuItem showLogMenuItem;
    private javax.swing.JMenu transferMenu;
    // End of variables declaration//GEN-END:variables

    
    @Override
    /**
     * Listens profiles selection in menu
     */
    public void actionPerformed(ActionEvent e) {
        Enumeration en = profileMenuButtonGroup.getElements();
        int i=0;
        while(en.hasMoreElements()){
            if(((JRadioButtonMenuItem)en.nextElement()).isSelected()){
                sHandler.setActiveProfile(i);
                loadNewConfig();
                break;
            }
            i++;
        }
    }

    public static void setLogger(Logger aLogger) {
        logger = aLogger;
    }

    public static void setParentLogger(Logger aLogger) {
        logger.setParent(aLogger);
    }

    @Override
    public void deviceManagerIsBusy(boolean busy) {
        jSymphonicMenu.setEnabled(!busy);
        transferMenu.setEnabled(!busy);
        profilesMenu.setEnabled(!busy);

        for(int i=0;i<iconToolBar.getComponentCount();i++){
            iconToolBar.getComponentAtIndex(i).setEnabled(!busy);
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void windowClosing(WindowEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
        sHandler.setWidth(this.getWidth());
        sHandler.setHeight(this.getHeight());
        sHandler.setDeviderLocation(jSP.getDividerLocation());
        sHandler.saveSettings(new File(SettingsHandler.CONFIG_FILE_NAME));
    }

    @Override
    public void windowClosed(WindowEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void windowIconified(WindowEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void windowActivated(WindowEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

}
