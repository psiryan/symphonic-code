/*
 * GenericPodcast.java
 *
 * Created on 21 février 2007, 19:32
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.system.podcasts;
import org.naurd.media.jsymphonic.system.SystemListener;
import org.naurd.media.jsymphonic.title.Title;

/**
 *
 * @author pballeux
 */
public class GenericPodcast implements org.naurd.media.jsymphonic.system.SystemFile {
    private String name = "default";
    private String description = "";
    private String source = "";
    private java.util.Vector<Title> titles = new java.util.Vector<Title>();
    private SystemListener listener = null;
    private javax.swing.ImageIcon icon = null;
    /** Creates a new instance of GenericPodcast */
    public GenericPodcast() {
    }
    
    public Title[] getTitles(){
        int count = titles.size();
        org.naurd.media.jsymphonic.title.Title[] ts = new org.naurd.media.jsymphonic.title.Title[count];
        int index = 0;
        for (int i=0;i<titles.size();i++){
            ts[index++] = (org.naurd.media.jsymphonic.title.Title)titles.get(i);
        }
        return ts;
    }
    public void setListener(SystemListener l){
        listener = l;
    }
    
    public void removeTitles(Title t){
    }
    public int addTitle(Title t, Boolean transcodeAllFiles){
        return 0;
    }
    public void replaceTitle(Title oldTitle,Title newTitle){
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
        java.net.URL url = null;
        try{
            url = new java.net.URL(source);
        } catch(Exception e){}
        return url;
    }
    public String getSource(){
        return source;
    }
    public void setSource(String url){
        source = url;
    }
    public void writeTitles() throws java.io.IOException{
    }
    public void refreshTitles(){
        if (source.length()>0){
            try{
                java.net.URL url = new java.net.URL(source);
                org.w3c.dom.Document doc = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openStream());
                org.w3c.dom.NodeList nodes = doc.getElementsByTagName("item");
                org.w3c.dom.Node channel = doc.getElementsByTagName("channel").item(0);
                org.w3c.dom.NodeList urls = doc.getElementsByTagName("url");
                String title = "";
                //Finding the icon for that podcast...
                for (int i = 0;i<urls.getLength();i++){
                    if (urls.item(i).getParentNode().getNodeName().equals("image")){
                        java.net.URL logourl = new java.net.URL(urls.item(i).getTextContent());
                        icon = new javax.swing.ImageIcon(logourl);
                        break;
                    }
                }
                //Finding the title for that podcast as the description...
                for (int i = 0;i<channel.getChildNodes().getLength();i++){
                    org.w3c.dom.Node node = channel.getChildNodes().item(i);
                    if (node.getNodeName().equals("description")){
                        description = node.getTextContent();
                        break;
                    }
                }
                //Finding the host...
                for (int i = 0;i<channel.getChildNodes().getLength();i++){
                    org.w3c.dom.Node node = channel.getChildNodes().item(i);
                    if (node.getNodeName().equals("title")){
                        title = node.getTextContent();
                        break;
                    }
                }
                
                //Finding the publisher...
                for (int i = 0;i<channel.getChildNodes().getLength();i++){
                    org.w3c.dom.Node node = channel.getChildNodes().item(i);
                    if (node.getNodeName().equals("description")){
                        description = node.getTextContent();
                        break;
                    }
                }
                //Finding each title...
                for(int i = 0;i<nodes.getLength();i++){
                    //Each title in this podcast...
                    Title t = Title.getTitleFromPodcast(nodes.item(i),url.getHost(),title);
                    titles.add(t);
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        
    }
    public javax.swing.ImageIcon getIcon(){
        return icon;
    }
    public long getTotalSpace(){
        return -1;
    }
    public long getUsableSpace(){
        return -1;
    }
    public String toString(){
        return name;
    }

    public int addTitle(Title t, Boolean transcodeAllFiles, int transcodeBitrate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
