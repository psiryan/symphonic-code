/*
/*
 * Copyright (C) 2007, 2008, 2009 Daniel Žalar (danizmax@gmail.com)
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
 */

package org.danizmax.jsymphonic.gui;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.TreeMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.danizmax.jsymphonic.gui.settings.SettingsDialog;
import org.danizmax.jsymphonic.gui.settings.SettingsHandler;

/**
 *
 * @author danizmax
 */
public class JSymphonic {

    private static SettingsHandler sHandler;
    private JSymphonicFrame JSf;
    private TreeMap themeMap = null;
    private static TreeMap langMap = null;
    private static FileHandler fileLogHandler;
    public static GuiLogHandler glHandler;
    public static ConsoleHandler consoleHandler;
    public static String version = "trunk(23082009)";

    private static Logger logger = Logger.getLogger("org.danizmax.jsymphonic.gui2.JSymphonic");

    public JSymphonic(){
        sHandler = new SettingsHandler();

        initThemeMap();
        initLangMap();

        //set locale
        if(sHandler.getLanguage() == null)
            Locale.setDefault((Locale) new Locale("en", "GB"));

        if(!new File(SettingsHandler.CONFIG_FILE_NAME).exists()){
            SettingsDialog sf = new SettingsDialog(sHandler, langMap, themeMap);
            sf.setLocationRelativeTo(null);
            sf.setModal(true);
            sf.setVisible(true);

            if(!sf.isSuccess()){
                System.exit(0);
            }

            sf.dispose();
        }

        
        sHandler.loadSettings(new File(SettingsHandler.CONFIG_FILE_NAME));
        //initLogger();
        if(sHandler.getTheme() != null){
            changeLAF(sHandler.getTheme(), null);
        }

       // initLogger();
        logger.severe("Preparing to run..");
        JSf = new JSymphonicFrame(sHandler, langMap, themeMap);
        JSymphonicFrame.setParentLogger(logger);
        JSf.setLocationByPlatform(true);
        JSf.setVisible(true);
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
                    themeMap.put("Windows", "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                    themeMap.put("Office 2003", "org.fife.plaf.Office2003.Office2003LookAndFeel");
                    themeMap.put("Office XP", "org.fife.plaf.OfficeXP.OfficeXPLookAndFeel");
                    themeMap.put("Visual Studio 2005", "org.fife.plaf.VisualStudio2005.VisualStudio2005LookAndFeel");
                }
                if(lafInfo[i].getName().contains("GTK+")) {
                    themeMap.put("GTK+", "com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
                }
            }
        }

        // Then, add all cross-platform styles
        themeMap.put("Metal", "javax.swing.plaf.metal.MetalLookAndFeel");
        //themeMap.put("GTK","com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        //themeMap.put("Windows","com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        themeMap.put("Lipstik", "com.lipstikLF.LipstikLookAndFeel");
//        themeMap.put("InfoNode", "net.infonode.gui.laf.InfoNodeLookAndFeel");
          themeMap.put("TinyLaf", "de.muntjak.tinylookandfeel.TinyLookAndFeel");
        themeMap.put("Nimbus", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        themeMap.put("Plastic XP", "com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
        themeMap.put("Pgs Look", "com.pagosoft.plaf.PgsLookAndFeel");

       //comented because of substance errors themeMap.put("Substance Raven Graphite", "org.jvnet.substance.skin.SubstanceRavenGraphiteLookAndFeel");
        themeMap.put("Substance Raven", "org.jvnet.substance.skin.SubstanceRavenLookAndFeel");
        themeMap.put("Substance Magma", "org.jvnet.substance.skin.SubstanceMagmaLookAndFeel");
        themeMap.put("Substance Emerald Dusk", "org.jvnet.substance.skin.SubstanceEmeraldDuskLookAndFeel");
        themeMap.put("Substance Business", "org.jvnet.substance.skin.SubstanceBusinessLookAndFeel");
        themeMap.put("Substance Business Blue Steel", "org.jvnet.substance.skin.SubstanceBusinessBlueSteelLookAndFeel");
        themeMap.put("Substance Business Black Steel", "org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel");
        themeMap.put("Substance Creme", "org.jvnet.substance.skin.SubstanceCremeLookAndFeel");
        themeMap.put("Substance Creme Coffee", "org.jvnet.substance.skin.SubstanceCremeCoffeeLookAndFeel");
        themeMap.put("Substance Sahara", "org.jvnet.substance.skin.SubstanceSaharaLookAndFeel");
        themeMap.put("Substance Moderate", "org.jvnet.substance.skin.SubstanceModerateLookAndFeel");
        themeMap.put("Substance Office Silver 2007", "org.jvnet.substance.skin.SubstanceOfficeSilver2007LookAndFeel");
        themeMap.put("Substance Office Blue 2007",  "org.jvnet.substance.skin.SubstanceOfficeBlue2007LookAndFeel");
        themeMap.put("Substance Nebula", "org.jvnet.substance.skin.SubstanceNebulaLookAndFeel");
        themeMap.put("Substance Nebula Brick Wall", "org.jvnet.substance.skin.SubstanceNebulaBrickWallLookAndFeel");
        themeMap.put("Substance Autumn", "org.jvnet.substance.skin.SubstanceAutumnLookAndFeel");
        themeMap.put("Substance Mist Silver", "org.jvnet.substance.skin.SubstanceMistSilverLookAndFeel");
        themeMap.put("Substance Mist Aqua", "org.jvnet.substance.skin.SubstanceMistAquaLookAndFeel");
   }

    public void changeLAF(String laf, Component c) {
        String look = (String)themeMap.get(laf);
        try {
            if(look != null){
                UIManager.setLookAndFeel(look);
                if(c != null){
                    SwingUtilities.updateComponentTreeUI(c);
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JSymphonic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(JSymphonic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(JSymphonic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(JSymphonic.class.getName()).log(Level.SEVERE, null, ex);
        }
   }

    private void initLangMap(){
        // Define existing languages
        langMap = new TreeMap();
        langMap.put("English (default)", new Locale("en", "GB"));
        langMap.put("Chinese", new Locale("zh", "TW"));
//        langMap.put("Deutsch", new Locale("de", "DE"));
        langMap.put("Español", new Locale("es", "ES"));
        langMap.put("Français", new Locale("fr", "FR"));
//        langMap.put("Italiano", new Locale("it", "IT"));
//        langMap.put("Português", new Locale("pt", "PT"));
//        langMap.put("Slovenčina ", new Locale("sk", "SK"));
//        langMap.put("Slovenščina", new Locale("sl", "SI"));
        langMap.put("Svenska", new Locale("sv", "SV"));
        langMap.put("Türkçe", new Locale("tr", "TR"));
//        langMap.put("Česky", new Locale("cs", "CS"));
//        langMap.put("Русский", new Locale("ru", "RU"));
    }

    public static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

    //configure logging format
    public static  Formatter logFormatter = new Formatter() {
  	  public String format(LogRecord record) {
  		  String lvlStr = " [" + record.getLevel() + "]";
		  while((lvlStr += " ").length() < 11);
		  return  dateFormatter.format(new Date()) + lvlStr
		  	+ record.getSourceClassName() + ":"
		  	+ record.getSourceMethodName() + " : "
		  	+ record.getMessage() + "\n";
	  }
    };
    
    public static void initLogger(){
        //Enum logers LogManager.getLogManager().getLoggerNames();

        glHandler = new GuiLogHandler();
        glHandler.setFormatter(logFormatter);
        logger.addHandler(glHandler);
        consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(logFormatter);
        logger.addHandler(consoleHandler);
        Logger.getLogger( "" ).addHandler(glHandler);
        if(sHandler.isLogToFile()){
            try {
                fileLogHandler = new FileHandler(SettingsHandler.LOG_FILE_NAME,SettingsHandler.byteSizeLimit,SettingsHandler.numOfLogFiles, false);
                fileLogHandler.setFormatter(logFormatter);
            } catch (IOException ex) {
                Logger.getLogger(JSymphonicFrame.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(JSymphonicFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            logger.addHandler(fileLogHandler);
            Logger.getLogger( "" ).addHandler(fileLogHandler);
        }else{
            if(fileLogHandler != null){
                logger.removeHandler(fileLogHandler);
                Logger.getLogger( "" ).removeHandler(fileLogHandler);
                fileLogHandler = null;
            }
        }

        Level lvl = Level.parse(sHandler.getLogLevel());

        LogManager lm = LogManager.getLogManager();
        // Add logger from jaudiotagger
        lm.addLogger(Logger.getLogger("org.jaudiotagger.audio"));
        lm.addLogger(Logger.getLogger("org.jaudiotagger.audio.asf.tag"));
        lm.addLogger(Logger.getLogger("org.jaudiotagger.audio.flac"));
        lm.addLogger(Logger.getLogger("org.jaudiotagger.audio.flac.MetadataBlockDataPicture"));
        lm.addLogger(Logger.getLogger("org.jaudiotagger.audio.flac.MetadataBlockDataStreamInfo"));
        lm.addLogger(Logger.getLogger("org.jaudiotagger.audio.generic"));
        lm.addLogger(Logger.getLogger("org.jaudiotagger.audio.generic"));
        lm.addLogger(Logger.getLogger("org.jaudiotagger.audio.mp3"));
        lm.addLogger(Logger.getLogger("org.jaudiotagger.audio.mp4"));
        lm.addLogger(Logger.getLogger("org.jaudiotagger.audio.mp4.atom"));
        lm.addLogger(Logger.getLogger("org.jaudiotagger.audio.ogg"));
        lm.addLogger(Logger.getLogger("org.jaudiotagger.audio.ogg.atom"));
        lm.addLogger(Logger.getLogger("org.jaudiotagger.tag.datatype"));
        lm.addLogger(Logger.getLogger("org.jaudiotagger.tag.id3"));
        lm.addLogger(Logger.getLogger("org.jaudiotagger.tag.mp4"));
        lm.addLogger(Logger.getLogger("org.jaudiotagger.tag.vorbiscomment.VorbisCommentReader"));

        Enumeration<String> loggers = lm.getLoggerNames();
        String loggerName = null;

        //set levels for all loggers
        while(loggers.hasMoreElements()){
            loggerName = loggers.nextElement();
            if(loggerName.contains("jsymphonic")){ //set log level only for "jsymphonic" classes
                lm.getLogger(loggerName).setLevel(lvl);
            }
            if(loggerName.contains("jaudiotagger")){ //set log level only for "jaudiotagger" classes
                // We don't care about jaudiotagger log, only severe are shown
                lm.getLogger(loggerName).setLevel(Level.SEVERE);
            }
        }

        //set level for all handlers

        Handler[] handlers = Logger.getLogger( "" ).getHandlers();
        //Handler[] handlers = logger.getHandlers();
        logger.setLevel(lvl);
        for ( int index = 0; index < handlers.length; index++ ) {
            handlers[index].setLevel(lvl);
            handlers[index].setFormatter(logFormatter);
        }
    }


    /**
    * @param args the command line arguments
    */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JSymphonic();
            }
        });
    }
}
