/*
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
 *****
 * 
 * Title.java
 *
 * Created on October 2, 2006, 10:21 PM
 *
 */

package org.naurd.media.jsymphonic.title;

import java.io.File;
import java.util.logging.Logger;

/**
 *Describes an audio file without regarding the format of the file.
 *
 * @author Pat
 * @author nicolas_cardoso
 */
public class Title {
/* FIELDS */
    // Title tag
    protected Tag tag;
    
    // Title information
    protected java.net.URL sourceURL = null; // Source URL of the file (for Podcast)
    protected File sourceFile = null; // The file (for local file)
    protected long fileSize = 0; // Size of the file in octet
    protected int format = Title.AUDIO;
    protected int bitRate = 128; // Bitrate of the title
    protected double frequency = 44.1; // Frequency of the title, given in kHz
    protected boolean vbr = false; // Indicates if the title uses variable or fix bitrate, true = vbr, false = cbr
    protected int nbChannels = 2; // Number of channel
    protected int length = 210000; // Length of the title in milliseconds
    protected int status = Title.NONE;    // Title status defined by the following constant
    
    //Other
    private static Logger logger = Logger.getLogger("org.naurd.media.jsymphonic.title.Title");

    
/* CONSTANT */
    // File format constant
    public static final int AUDIO = 0; // Undefined format
    public static final int OMA = 1; // Format with a number between 1 and 9 can be play on all walkman
    public static final int MP3 = 2;
    public static final int AAC = 10; // Format with a number between 10 and 19 can be play on some walkmans and must be transcoded on other walkmans
    public static final int WAV = 11;
    public static final int OGG = 20; // Format with a number greated than 20 must be transcoded on all walkmans
    public static final int MPC = 21;
    public static final int FLAC = 22;
    public static final int WMA = 23;

    // Base status used in the GUI (for the tree)
    public static final int NONE = 0;
    public static final int TOIMPORT = 1;
    public static final int TODELETE = 2;
    public static final int TOEXPORT = 3;
    // Advanced status used when transfer has started to know what should be done with the title
    public static final int TODECODE = 4;
    public static final int TOENCODE = 5;
    public static final int TOENCODEANDREMOVEFROMDISK = 6;
    public static final int TOADDANDREMOVEFROMDISK = 7;

    
/* CONSTRUCTORS */
    /**
     *Creates a new instance of Title from a given file.
     *
     *@param file the file to create the title from
     */
    public Title(java.io.File file) {
        sourceFile = file;
        tag = new Tag(file);
    }
    
    /**
     *Creates a new instance of Title from a given URL.
     *
     *@param url the URL to create the title from
     */
    protected Title(java.net.URL url) {
        sourceURL = url;
    }
    
    /**
     *Creates an empty instance of Title.
     *
     *This method could lead to not handle exception and should not be used.
     */
    protected Title(){
    }
    
    
/* STATIC METHODS */
    /**
     * Allows to instanciate a Title using the correct extended class (class Mp3 for MP3 files,...)
     *
     *@param f the file to create the object from.
     *
     *@return the object from the class corresponding to the title type. If no suitable class has been found, a Title object is returned.
     */
    public static Title getTitleFromFile(java.io.File f){
        Title retValue;
        
        // Get the file name
        String filename = f.getName().toLowerCase();
        
        // If the extension of the file is unknown, create a object of the corresponding class.
        if (filename.endsWith(".mp3")){
            retValue = new Mp3(f);
        } else if (filename.endsWith(".wma")){
            retValue = new Wma(f);
        } else if (filename.endsWith(".oma")){
            retValue = new Oma(f);
        } else if (filename.endsWith(".ogg")){
            retValue = new Ogg(f);
        } else if (filename.endsWith(".wav")){
            retValue = new Wav(f);
        } else if (filename.endsWith(".flac")){
            retValue = new Flac(f);
        } else if (filename.endsWith(".mpc")){
            retValue = new Title(f);
            retValue.setFormat(MPC);
        } else if (filename.endsWith(".wav") || filename.endsWith(".wave")){
            retValue = new Title(f);
            retValue.setFormat(WAV);
        } else{
            retValue = new Title(f);
        }
        return retValue;
    }
    
    /**
     * Creates a Title object from a Podcast.
     *
     *@param podcastItem ??
     *@param host ??
     *@param publisher ??
     *
     *@return the title object.
     */
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
            logger.warning("Exception : Could not find title size..." + title);
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
        retValue.setAlbumName(publisher);
        retValue.setArtistName(host);
        retValue.setTitleName(title);
        retValue.fileSize = size;
        return retValue;
    }
    
    /**
     * Get the child from a node.
     *
     *@param name ??
     *@param node ??
     *
     *@return the node.
     */
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
    
    
/* GET METHODS */    
    /**
     *Obtains the album name corresponding to the title.
     *
     *@return the album name.
     */
    public String getAlbum(){
        return tag.getAlbumName();
    }
    
    /**
     *Obtains the artist name corresponding to the title.
     *
     *@return the artist name.
     */
    public String getArtist(){
        return tag.getArtistName();
    }
    
    /**
     *Obtains the title name corresponding to the title.
     *
     *@return the title name.
     */
    public String getTitle(){
        return tag.getTitleName();
    }
        
    /**
     *Obtains the genre corresponding to the title.
     *
     *@return the genre.
     */
    public String getGenre(){
        return tag.getGenre();
    }
    
    /**
     *Obtains the track number corresponding to the title.
     *
     *@return the track number.
     */
    public int getTitleNumber(){
        return tag.getTrackNumber();
    }
    
    /**
     *Obtains the year corresponding to the title.
     *
     *@return the year.
     */
    public int getYear(){
        return tag.getYear();
    }
    
    /**
     *Obtains encoding method (variable or constant bitrate) corresponding to the title.
     *
     *@return true if VBR and false if CBR.
     */
    public boolean getVbr(){
        return vbr;
    }
    
    /**
     *Obtains frequency used to encode the title.
     *
     *@return the frequency in kHz.
     */
    public double getFrequency(){
        return frequency;
    }
    
    /**
     *Obtains the tag of the title.
     *
     *@return the tag assigned to the title.
     */
    public Tag getTag(){
        return tag;
    }
    
    /**
     *Obtains a stream to read the title from.
     *
     *@return the stream.
     */    
    public java.io.InputStream getInputStream() throws java.io.IOException{
        java.io.InputStream in = null;
        if (sourceURL!=null){
            in = sourceURL.openStream();
        }
        return in;
    }
    
    /**
     *Obtains the file described by this title.
     *
     *@return the corresponding file.
     */
    public File getSourceFile(){
        return sourceFile;
    }
    
    /**
     *Obtains the bitrate this title.
     *
     *@return the bitrate.
     */
    public int getBitRate(){
        return bitRate;
    }
    
    /**
     *Obtains the length of the title in millisecond.
     *
     *@return the length of the title.
     */
    public int getLength() {
        return length;
    }
    
    /**
     *Obtains the format of the title (OMA, MP3,...).
     *Constant are defined in the class "Title" to associated format to number. For instance Title.MP3 = 2.
     *
     *@return the format of the title as an int. 
     */
    public int getFormat() {
        return format;
    }
    
    /**
     *Obtains the status of the title (NONE, TOIMPORT,...).
     *Constant are defined in the class "Title" to associated status to number. For instance Title.NONE = 0.
     *
     *@return the format of the title as an int. 
     */
    public int getStatus() {
        return status;
    }
    
    /**
     * Obtains the source URL of the title.
     *
     *@return the URL.
     *
    public java.net.URL getSourceURL(){
        return this.sourceURL;
    } temporary commented, could be uncomment when podcast will be implemented*/
    
    
    
/* SET METHODS */
    /**
     * Change the name of the title.
     *
     *@param newTitleName the new title name.
     */
    public void setTitleName(String newTitleName){
        tag.setTitleName(newTitleName);
    }

    /**
     * Change the artist of the title.
     *
     *@param newArtistName the new artist name.
     */
    public void setArtistName(String newArtistName){
        tag.setArtistName(newArtistName);
    }

    /**
     * Change the album of the title.
     *
     *@param newAlbumName the new album name.
     */
    public void setAlbumName(String newAlbumName){
        tag.setAlbumName(newAlbumName);
    }
    
    /**
     * Change the genre of the title.
     *
     *@param newGenre the new genre.
     */
    public void setGenre(String newGenre){
        tag.setGenre(newGenre);
    }

    /**
     * Change the format of the title.
     * Constant are defined in the class "Title" to associated format to number. For instance Title.MP3 = 2.
     *
     *@param newFormat the new format to be assigned to the title.
     */
    public void setFormat(int newFormat) {
        format = newFormat;
    }

    /**
     * Change the status of the title.
     *Constant are defined in the class "Title" to associated status to number. For instance Title.NONE = 0.
     *
     *@param newStatus the new status to be assigned to the title.
     */
    public void setStatus(int newStatus) {
        status = newStatus;
    }
    /**
     * Change the tag of the title.
     * Usefull to assign in one operation artist name, album,... to a newly created Title.
     *
     *@param newTag the new tag to be assigned to the title.
     */
    public void setTag(Tag newTag) {
        tag = newTag;
    }
    
    
    
/* METHODS */
    
    /**
     *Obtains the size of the title in octet (TODO to be checked).
     *
     *@return the size of the title.
     */
    public long size(){
        return fileSize;
    }
    
    /**
     *Obtains a "string" view of the title to be printed.
     *
     *@return a string describing the title.
     */
    @Override
    public String toString(){
        return tag.getArtistName() + " - " + tag.getTitleName();
    }
    
    /**
     *Write the tag in a Title object. This method should be overide by each subclass to really write the tag to the file (write the ID3Tag in MP3 case for instance)
     * 
     * @param newTag the tag to be written in the title.
     */
    public void writeTag(Tag newTag) {
        tag = newTag;
    }
    
    /**
     *Tell if the title is compatible, i.e. if it can be transfered as it is without transcodage. The answer depends on the configuration chosen by the user. For instance, if a title is in MP3@128kbps-44,1Hz (playable by all Sony devices after generation1) and if in the configuration "transcode all" is set with a bitrate of 64kbps, answer will be FALSE. With the same title, if the configuration is on "transcode all" with a bitrate of 128kbps, the answer will be TRUE,... ...
     *
     * @param transcodeAllFiles should be true is the configuration is set to "always transcode", false otherwise
     * @param transcodeBitrate indicates the bitrate set in the configuration to be used to transcode.
     * 
     *@return true is the title can be transfered without been transcoded, false otherwise.
     */
    public boolean isCompatible(Boolean transcodeAllFiles, int transcodeBitrate){
        // If the transcodeAllFiles variable is true, file must be converted if it's not a MP3 at the default bitrate.
        if(transcodeAllFiles){
            //All files should be transcode, but if the file is already a MP3 at the good bitrate or if it's an OMA, it should not be transcoded
            if((format != Title.MP3 || bitRate != transcodeBitrate) && format != Title.OMA){
                // Title is not an oma nor a mp3 at the right bitrate, it must be transcode
                logger.warning("Transcode all files is on. File \"" + tag.getArtistName() + "-" + tag.getTitleName() + "\" must be transcoded. (Bitrate or format don't correspond to default settings.)");
                return false;
            }
            else{
                // Title is a MP3 at the right bitrate, it can be transfered without been transcoded
//logger.warning("Transcode all files is on. File is already at default MP3 config or file is an OMA:"+newTitle.getSourceFile().getPath());
                return true;
            }
        }
        else{
            // Only unplayed files should be transcode
            if(format >= 10) {//TODO, some format between 10 and 20 can be played on some players...
                // if format is greater than 10 (file is neither OMA nor MP3), file has to be converted
                logger.warning("Transcode all files is off. File \"" + tag.getArtistName() + "-" + tag.getTitleName() + "\" must be transcoded. (Format is not supported by Sony devices.)");
                return false;
            }
            else if(format == Title.OMA){
                // if file is an OMA, it can't be transcoded
                return true;
            }
            else{
                // file is an MP3, bitrate and frequency should be checked
                if(frequency != 44.1) { // Frequence is given in kHz
                    // frequency is not good, no need to check the bitrate, file must be converted
                logger.warning("Transcode all files is off. File \"" + tag.getArtistName() + "-" + tag.getTitleName() + "\" must be transcoded. (Frequency is not supported by Sony devices.)");
                    return false;
                }
                else if(vbr) {
                    // If file is VBR, bitrate doesn't have to be checked.
                    return true;
                }
                else if(( bitRate != 64) && ( bitRate != 96) && ( bitRate != 128) && ( bitRate != 160) && ( bitRate != 192) && ( bitRate != 256) && ( bitRate != 320) ) {
                    // bitrate is not good
                logger.warning("Transcode all files is off. File \"" + tag.getArtistName() + "-" + tag.getTitleName() + "\" must be transcoded. (Bitrate is not supported by Sony devices.)");
                    return false;
                }
                else{
                    // all is OK, file can be transfer as it is !
                    return true;
                }
            }
        }
    }
    
    /**
     *Tell if the title is directly encodable, without been decoded before. This depends on the format handled by FFMPEG.
     *
     *@return true is the title can be directly encoded without been decoded, false otherwise.
     */
    public boolean isEncodable(){
        // If the format of the file is handled by FFMPEG, return true
        if(format == Title.MP3 || format == Title.MPC || format == Title.OGG || format == Title.WMA || format == Title.WAV)  // TODO add AAC
            return true;
        else 
            return false;
    }
    
}
