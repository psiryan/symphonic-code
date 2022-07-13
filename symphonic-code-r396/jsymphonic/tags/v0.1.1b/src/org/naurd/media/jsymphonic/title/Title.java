/*
 * Title.java
 *
 * Created on October 2, 2006, 10:21 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.title;

/**
 *
 * @author Pat
 */
public class Title {
    protected String albumName= "";
    protected String artistName = "";
    protected String titleName = "";
    protected String genre = "";
    protected int titleNumber = 0;
    protected int year = 0000;
    protected java.net.URL sourceURL = null;
    protected int bitRate = 128;
    protected double frequency = 44100;
    protected long fileSize = 0;
    protected int nbChannels = 2;
    protected long length = 0;
    protected String extention = ".audio";
    
    public enum TitleStatus{
        NONE,
        TOADD,
        TOREMOVE,
        SAVING,
        ERRORSAVING,
        TOTRANSCODE
    }
    public TitleStatus Status = TitleStatus.NONE;
    
    /* Constructors */
    /** Creates a new instance of Title */
    protected Title(java.io.File file) {
        try{
            sourceURL = file.toURI().toURL();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    protected Title(java.net.URL url) {
        sourceURL = url;
    }
    
    protected Title(){
    }
    
    /* Methods */    
    public String getExtention(){
        return extention;
    }
    
    public long size(){
        return fileSize;
    }
    public String toString(){
        return titleName;
    }
    public String getAlbum(){
        return albumName;
    }
    public String getArtist(){
        return artistName;
    }
    public String getTitle(){
        return titleName;
    }
    public int getTitleNumber(){
        return titleNumber;
    }
    public int getYear(){
        return year;
    }
    public java.io.InputStream getInputStream() throws java.io.IOException{
        java.io.InputStream in = null;
        if (sourceURL!=null){
            in = sourceURL.openStream();
        }
        return in;
    }
    public java.io.File getSourceFile(){
        java.io.File f = null;
        try{
            f = new java.io.File(sourceURL.toURI());
        } catch(Exception e){
            e.printStackTrace();
        }
        return f;
    }
    
    public String getGenre(){
        return genre;
    }
    
    public int getBitRate(){
        return bitRate;
    }
    
    public void setGenre(String g){
        genre = g;
    }
    public java.net.URL getSourceURL(){
        return this.sourceURL;
    }
    public static Title getTitleFromFile(java.io.File f){
        Title retValue = null;
        String filename = f.getName().toUpperCase();
        if (filename.endsWith(".MP3")){
            retValue = new Mp3(f);
        } else if (filename.endsWith(".WMA")){
            retValue = new Wma(f);
        } else if (filename.endsWith(".OMA")){
            retValue = new Oma(f);
        } else if (filename.endsWith(".OGG")){
            retValue = new Ogg(f);
        } else if (filename.endsWith(".WAV")){
            retValue = new Wav(f);
        }
        return retValue;
    }
    public static Title getTitleFromPodcast(org.w3c.dom.Node podcastItem,String host, String publisher){
        Title retValue = null;
        java.net.URL url = null;
        String date = "";
        String title = "";
        String album = "";
        String artist = "";
        long size = 0;
        org.w3c.dom.Node nodeTitle = Title.getChildNode("title",podcastItem);
        
        org.w3c.dom.Node nodeEnclosure = Title.getChildNode("enclosure",podcastItem);
        org.w3c.dom.Node nodeDate = Title.getChildNode("pubDate",podcastItem);
        try{
            date = nodeDate.getTextContent().replaceAll("00:00:00 GMT","");
            title = nodeTitle.getTextContent();
            url = new java.net.URL(nodeEnclosure.getAttributes().getNamedItem("url").getTextContent());
        } catch(Exception e){
            e.printStackTrace();
        }
        try{
            size = new Long(nodeEnclosure.getAttributes().getNamedItem("length").getTextContent()).longValue();
        } catch(Exception e){
            System.out.print("Exception : Could not find title size..." + title);
        }
        
        
        if (url!=null){
            if (url.getFile().toUpperCase().endsWith(".MP3")){
                retValue = new Mp3(url);
            } else if (url.getFile().toUpperCase().endsWith(".WMA")){
                retValue = new Wma(url);
            } else if (url.getFile().toUpperCase().endsWith(".OMA")){
                retValue = null;        //Not supported for now...
            } else if (url.getFile().toUpperCase().endsWith(".OGG")){
                retValue = new Ogg(url);
            } else if (url.getFile().toUpperCase().endsWith(".WAV")){
                retValue = new Wav(url);
            } else{
                retValue = new Mp3(url);
            }
        }
        retValue.albumName = publisher;
        retValue.artistName = host;
        retValue.titleName = title;
        retValue.fileSize = size;
        return retValue;
    }
    private static org.w3c.dom.Node getChildNode(String name,org.w3c.dom.Node node){
        org.w3c.dom.Node retValue = null;
        org.w3c.dom.NodeList list = node.getChildNodes();
        for(int i=0;i<list.getLength();i++){
            if(list.item(i).getNodeName().equals(name)){
                retValue = list.item(i);
                break;
            }
        }
        return retValue;
    }

    public long getLength() {
        //TODO
        return 0;
    }
    
}
