/*
 * GenericFS.java
 *
 * Created on October 15, 2006, 10:02 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.system.files;
import java.io.File;
import org.naurd.media.jsymphonic.system.SystemListener;
import org.naurd.media.jsymphonic.title.Title;

/**
 *
 * @author Pat
 */
public class GenericFS implements org.naurd.media.jsymphonic.system.SystemFile {
    private File sourceDir = null;
    private String name = "default";
    private String description = "";
    private java.util.Vector<Title> titles = new java.util.Vector<Title>();
    private java.util.Vector<Title> titlesToAdd = new java.util.Vector<Title>();
    private java.util.Vector<Title> titlesToRemove = new java.util.Vector<Title>();
    private SystemListener listener = null;
    /** Creates a new instance of GenericFS */
    public GenericFS(java.io.File dir,String sourceName,String sourceDesc){
        name = sourceName;
        description = sourceDesc;
        sourceDir = dir;
        loadTitlesFromDir(dir);
    }
    public GenericFS(){
    }
    
    public javax.swing.ImageIcon getIcon(){
        return null;
    }
    public Title[] getTitles(){
        int count = titles.size() + titlesToAdd.size() + titlesToRemove.size();
        org.naurd.media.jsymphonic.title.Title[] ts = new org.naurd.media.jsymphonic.title.Title[count];
        int index = 0;
        for (int i=0;i<titles.size();i++){
            ts[index++] = (org.naurd.media.jsymphonic.title.Title)titles.get(i);
        }
        for (int i=0;i<titlesToAdd.size();i++){
            ts[index++] = (org.naurd.media.jsymphonic.title.Title)titlesToAdd.get(i);
        }
        for (int i=0;i<titlesToRemove.size();i++){
            ts[index++] = (org.naurd.media.jsymphonic.title.Title)titlesToRemove.get(i);
        }
        return ts;
    }
    public void setListener(SystemListener l){
        listener = l;
    }
    
    public void refreshTitles(){
        titles.clear();
        titlesToAdd.clear();
        titlesToRemove.clear();
        loadTitlesFromDir(sourceDir);
    }
    private void loadTitlesFromDir(java.io.File dir){
        java.io.File[] fs = dir.listFiles();
        for (int i = 0;dir.exists() && i<fs.length;i++){
            if (fs[i].isDirectory()){
                loadTitlesFromDir(fs[i]);
            } else{
                //Validate that the format is a title...
                Title t = Title.getTitleFromFile(fs[i]);
                if (t!=null){
                    titles.add(t);
                }
            }
        }
    }
    public void removeTitles(Title t){
        t.Status = t.Status.TOREMOVE;
        titlesToRemove.add(t);
    }
    public int addTitle(Title t){
        t.Status = t.Status.TOADD;
        titlesToAdd.add(t);
        
        return 0;
    }
    public void replaceTitle(Title oldTitle,Title newTitle){
        removeTitles(oldTitle);
        addTitle(newTitle);
    }
    public String getSourceName(){
        return name;
    }
    public void setSourceName(String n){
        name=n;
    }
    public String getSourceDescription(){
        return description;
    }
    public void setSourceDescription(String d){
        description = d;
    }
    public java.net.URL getSourceURL(){
        try{
            return sourceDir.toURI().toURL();
        } catch(Exception e){
            return null;
        }
    }
    
    public File getSource(){
        return sourceDir;
    }
    
    public void setSource(String source){
        sourceDir = new java.io.File(source);
    }
    
    public void writeTitles(){
        Thread t = new Thread(){
            public void run(){
                try{
                    writeTitlesInTread();
                } catch(Exception e){}
            }
        };
        t.setPriority(t.MIN_PRIORITY);
        t.start();
        t = null;
    }
    private void writeTitlesInTread() throws java.io.IOException{
        //Deleting files first...
        Title currentTitle = null;
        java.io.File f = null;
        for (int i = 0;i<titlesToRemove.size();i++){
            currentTitle = titlesToRemove.get(i);
            f = currentTitle.getSourceFile();
            if (f!=null){
                f.delete();
                if (listener!=null){
                    //-1 is for "Deleting this title...";
                    listener.WritingProgress(this,currentTitle,-1);
                }
            }
        }
        
        //Adding new titles...
        for (int i = 0;i<titlesToAdd.size();i++){
            currentTitle = titlesToAdd.get(i);
            java.io.File artistDir = new java.io.File(sourceDir,currentTitle.getArtist());
            java.io.File albumDir = new java.io.File(artistDir,currentTitle.getAlbum());
            String filename = currentTitle.getTitle();
            if (!filename.toLowerCase().endsWith(currentTitle.getExtention().toLowerCase())){
                filename = filename + currentTitle.getExtention();
            }
            java.io.File titleFile = new java.io.File(albumDir,filename);
            //Check if artist dir is there...
            if (!artistDir.exists()){
                artistDir.mkdir();
            }
            //Check if ablum dir is there
            if (!albumDir.exists()){
                albumDir.mkdir();
            }
            //Delete the file if it is already there...
            if (titleFile.exists()){
                titleFile.delete();
            }
            //Copy the file...
            java.io.InputStream in = currentTitle.getInputStream();
            java.io.RandomAccessFile raf = new java.io.RandomAccessFile(titleFile,"rw");
            byte[] buffer = new byte[4096];
            int count = 0;
            long totalCount = 0;
            while (count>=0){
                count = in.read(buffer);
                if (count>0){
                    totalCount+=count;
                    raf.write(buffer,0,count);
                    if (listener!=null){
                        listener.WritingProgress(this,currentTitle,totalCount);
                    }
                }
            }
            in.close();
            raf.close();
        }
        currentTitle=null;
        //Once completed, reload everything...
        refreshTitles();
    }
    
    public long getTotalSpace(){
        return sourceDir.getTotalSpace();
    }
    public long getUsableSpace(){
        return sourceDir.getUsableSpace();
    }
    public String toString(){
        return name;
    }
    
}
