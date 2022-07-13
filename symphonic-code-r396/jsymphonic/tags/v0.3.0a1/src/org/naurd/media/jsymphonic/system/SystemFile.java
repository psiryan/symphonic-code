/*
 * SystemFile.java
 *
 * Created on October 7, 2006, 12:07 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.system;
import org.naurd.media.jsymphonic.title.Title;
/**
 *
 * @author Pat
 */
public interface SystemFile {
    public Title[] getTitles();
    public void removeTitles(Title t);
    public int addTitle(Title t, Boolean transcodeAllFiles);
//    public void replaceTitle(Title oldTitle,Title newTitle);
    public String getSourceName();
    public void setSourceName(String n);
    public String getSourceDescription();
    public void setSourceDescription(String d);
    public java.net.URL getSourceURL();
    public Object getSource();
    public void setSource(String url);
    public void writeTitles() throws java.io.IOException;
    public void refreshTitles();
    public long getTotalSpace();
    public long getUsableSpace();
    public void setListener(SystemListener l);
    public javax.swing.ImageIcon getIcon();
}
