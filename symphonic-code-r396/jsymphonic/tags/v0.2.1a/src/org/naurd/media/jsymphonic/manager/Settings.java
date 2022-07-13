/*
 * Settings.java
 *
 * Created on 19 février 2007, 14:42
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.manager;
import java.io.File;
import org.naurd.media.jsymphonic.system.SystemFile;
/**
 *
 * @author pballeux
 */
public class Settings {
    private java.util.prefs.Preferences prefs = null;
    private final String preferencesFileName = "symphonic_settings.xml";
    private final String nodeNameForSystemFiles = "SystemFiles";
    /** Creates a new instance of Settings */
    public Settings() {
        
        java.io.File f = new java.io.File(preferencesFileName);
        if (f.exists()){
            try{
                //Clean the local settings...
                java.util.prefs.Preferences.userNodeForPackage(JSymphonic.class).removeNode();
                //Import settings from the XML file
                java.util.prefs.Preferences.importPreferences(f.toURI().toURL().openStream());
                
            } catch(Exception e){
                e.printStackTrace();
            }
            
        }
        prefs = java.util.prefs.Preferences.userNodeForPackage(JSymphonic.class);
        try{
            prefs.sync();
            
        } catch(Exception e){
        }
    }
    public void savePreferences(){
        java.io.File f = new java.io.File(preferencesFileName);
        try{
            if (f.exists()){
                f.delete();
            }
            java.io.FileOutputStream fout = new java.io.FileOutputStream(f,false);
            prefs.exportSubtree(fout);
            fout.close();
            fout = null;
            prefs.sync();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    public void putValue(String key, String value){
        prefs.put(key,value);
    }
    public String getValue(String key,String defaultValue){
        return prefs.get(key,defaultValue);
    }
    public void putIntValue(String key,int value){
        prefs.putInt(key,value);
    }
    public int getIntValue(String key,int defaultValue){
        return prefs.getInt(key,defaultValue);
    }
    public void putCategoryValue(String categoryName,String key,String value){
        prefs.node(categoryName).put(key,value);
    }
    public String getCategoryValue(String categoryName,String key,String defaultValue){
        return prefs.node("categoryName").get(key,defaultValue);
    }
    
    public void putSystemFile(org.naurd.media.jsymphonic.system.SystemFile sf){
        if (sf!=null){
            java.util.prefs.Preferences p = prefs.node(nodeNameForSystemFiles).node(sf.getSourceName());
            p.put("sf_class",sf.getClass().getName());
            p.put("sf_source",((File)sf.getSource()).getPath());
            p.put("sf_name",sf.getSourceName());
            p.put("sf_desc",sf.getSourceDescription());
        }
    }
    public void removeSystemFile(org.naurd.media.jsymphonic.system.SystemFile sf){
        java.util.prefs.Preferences p = prefs.node(nodeNameForSystemFiles);
        try{
            if (p.nodeExists(sf.getSourceName())){
                p.node(sf.getSourceName()).removeNode();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    public org.naurd.media.jsymphonic.system.SystemFile getSystemFile(String name){
        org.naurd.media.jsymphonic.system.SystemFile sf = null;
        java.util.prefs.Preferences p = prefs.node(nodeNameForSystemFiles);
        String className = p.node(name).get("sf_class","");
        if (className.length()>0){
            try{
                sf = (SystemFile)Class.forName(className).newInstance();
            } catch(Exception e){
                e.printStackTrace();
            }
            if (sf!=null){
                sf.setSource(p.node(name).get("sf_source",""));
                sf.setSourceName(p.node(name).get("sf_name",""));
                sf.setSourceDescription(p.node(name).get("sf_desc",""));
            }
        }
        return sf;
    }
    public org.naurd.media.jsymphonic.system.SystemFile[] getAllSystemFiles(){
        String[] sfName;
        java.util.prefs.Preferences p = prefs.node(nodeNameForSystemFiles);
        try{
            sfName = p.childrenNames();
        } catch(Exception e){
            e.printStackTrace();
            sfName = new String[0];
        }
        org.naurd.media.jsymphonic.system.SystemFile[] sfs = new org.naurd.media.jsymphonic.system.SystemFile[sfName.length];
        for(int i = 0;i<sfName.length;i++){
            String className = p.node(sfName[i]).get("sf_class","");
            SystemFile sf = null;
            try{
                sf = (SystemFile)Class.forName(className).newInstance();
            } catch(Exception e){
                //e.printStackTrace();
            }
            if (sf!=null){
                sf.setSource(p.node(sfName[i]).get("sf_source",""));
                sf.setSourceName(p.node(sfName[i]).get("sf_name",""));
                sf.setSourceDescription(p.node(sfName[i]).get("sf_desc",""));
            }
            sfs[i] = sf;
        }
        return sfs;
        
    }
}
