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
 * Tag.java
 *
 * Created on 12 avril 2008, 11:10
 *
 */

package org.naurd.media.jsymphonic.title;

import java.io.File;

/**
 *Describes an audio tag that should be read or write to an audio file. A tag is an embedded piece of information in a an audio file giving the name of the title, the artist, the album, and the genre, the year,... corresponding to the file.
 *
 * @author skiron
 */
public class Tag {
/* FIELDS */
    // Tag fields
    protected String titleName = "unknown title"; // Name of the title
    protected String artistName = "unknown artist"; // Name of the artist
    protected String albumName = "unknown album"; // Name of the album
    protected String genre = "unknown genre"; // Name of the genre
    protected int trackNumber = 0; // Track number of the title
    protected int year = 0000; // Year of the title

    
/* CONSTRUCTORS */
    /**
     *Creates an empty tag with default values.
     *
     * @author skiron
     */
    public Tag() {
        // All fields are already to default values, nothing more to do here
    }
    
    /**
     *Creates a tag from the file name and the folder structure of the file given as argument.
     *
     *@param file the file to read the information from (parent folder name may be used too).
     *
     * @author skiron
     */
    public Tag(File file) {
        // Get the rule to use to for reading info from file/folder structure
        //TODO do a selector in the preference window so that the user can choose the scheme to guess the information from.
        //I use default behavor which correspond to the way I'm saving my music : "Artist/Year-Album/##-TitleName"
        
        // Get file name:
        String fileName = file.getName();
        
        // Remove extension, split the fileName using "." as separator, without limit, last token found is the extension
        String[] tokens = fileName.split("\\.") ;
        fileName = fileName.replace("." + tokens[tokens.length - 1], "");
        
        // Read track number and title name from the file name
        // Split in file name in only 2 tokens using "-" as a separator (limit set to 2 prevent to split the title if it also contains "-" character)
        tokens = fileName.split("-", 2) ;
        try{
            // Try to use the first token as an integer (remove possible spaces)
            this.trackNumber = Integer.parseInt(tokens[0].replaceAll(" ", ""));
            // If no exception showed up, second token can be used as title
            this.titleName = tokens[1];
        }
        catch(Exception e){
            // If something went wrong, maybe the first token was not a number, so the whole file name is used as title name
            this.titleName = fileName;
            this.trackNumber = 0;
        }
        
        // Read the name of the parent folder of the file
        try{// Since parent may not exist, we should handle exception
            fileName = file.getParentFile().getName();
            // Split in file name in only 2 tokens using "-" as a separator (limit set to 2 prevent to split the album if it also contains "-" character)
            tokens = fileName.split("-", 2) ;
            try{
                // Try to use the first token as an integer (remove possible spaces)
                this.year = Integer.parseInt(tokens[0].replaceAll(" ", ""));
                // If no exception showed up, second token can be used as album
                this.albumName = tokens[1];
            }
            catch(Exception e){
                // If something went wrong, maybe the first token was not a number, so the whole file name is used as album name
                this.albumName = fileName;
                this.year = 0;
            }
        }
        catch(Exception e){
                this.albumName = "Unknown album";
                this.year = 0;            
        }
        
        // Read the name of the second parent folder of the file
        try{// Since parent.parent may not exist, we should handle exception
            fileName = file.getParentFile().getParentFile().getName();
            this.artistName = fileName;
        }
        catch(Exception e){
            this.artistName = "Unknown artist";
        }
        
        // In this scheme, genre is not known
        this.genre = "Other";
    }

    /**
     *Creates a tag with the given values provided as arguments.
     *
     *@param titleName the name of the title descrided by this tag.
     *@param artistName the name of the artist of the title descrided by this tag.
     *@param albumName the name of the album of the title descrided by this tag.
     *@param genre the genre of the title descrided by this tag.
     *@param trackNumber the trackNumber of the title descrided by this tag.
     *@param year the year of the title descrided by this tag.
     *
     * @author skiron
     */
    public Tag(String titleName, String artistName, String albumName, String genre, int trackNumber, int year) {
        // Fill all fields
        this.titleName = titleName;
        this.artistName = artistName;
        this.albumName = albumName;
        this.genre = genre;
        this.trackNumber = trackNumber;
        this.year = year;
    }
    
    /**
     *Creates a tag with the given values provided as arguments.
     *
     *@param titleName the name of the title descrided by this tag.
     *@param artistName the name of the artist of the title descrided by this tag.
     *@param albumName the name of the album of the title descrided by this tag.
     *@param genre the genre of the title descrided by this tag.
     *@param trackNumber the trackNumber of the title descrided by this tag.
     *
     * @author skiron
     */
    public Tag(String titleName, String artistName, String albumName, String genre, int trackNumber) {
        // Fill all fields
        this.titleName = titleName;
        this.artistName = artistName;
        this.albumName = albumName;
        this.genre = genre;
        this.trackNumber = trackNumber;
        // Year is kept to default value
    }
    
    /**
     *Creates a tag with the given values provided as arguments.
     *
     *@param titleName the name of the title descrided by this tag.
     *@param artistName the name of the artist of the title descrided by this tag.
     *@param albumName the name of the album of the title descrided by this tag.
     *@param genre the genre of the title descrided by this tag.
     *
     * @author skiron
     */
    public Tag(String titleName, String artistName, String albumName, String genre) {
        // Fill all fields
        this.titleName = titleName;
        this.artistName = artistName;
        this.albumName = albumName;
        this.genre = genre;
        // Track number is kept to default value
        // Year is kept to default value
    }
    
    /**
     *Creates a tag with the given values provided as arguments.
     *
     *@param titleName the name of the title descrided by this tag.
     *@param artistName the name of the artist of the title descrided by this tag.
     *@param albumName the name of the album of the title descrided by this tag.
     *
     * @author skiron
     */
    public Tag(String titleName, String artistName, String albumName) {
        // Fill all fields
        this.titleName = titleName;
        this.artistName = artistName;
        this.albumName = albumName;
        // Genre is kept to default value
        // Track number is kept to default value
        // Year is kept to default value
    }
    
    /**
     *Creates a tag with the given values provided as arguments.
     *
     *@param titleName the name of the title descrided by this tag.
     *@param artistName the name of the artist of the title descrided by this tag.
     *
     * @author skiron
     */
    public Tag(String titleName, String artistName) {
        // Fill all fields
        this.titleName = titleName;
        this.artistName = artistName;
        // Album is kept to default value
        // Genre is kept to default value
        // Track number is kept to default value
        // Year is kept to default value
    }
    
    /**
     *Creates a tag with the given values provided as arguments.
     *
     *@param titleName the name of the title descrided by this tag.
     *
     * @author skiron
     */
    public Tag(String titleName) {
        // Fill all fields
        this.titleName = titleName;
        // Artist is kept to default value
        // Album is kept to default value
        // Genre is kept to default value
        // Track number is kept to default value
        // Year is kept to default value
    }
    
    /**
     *Creates a tag from an existing tag.
     *For instance if an EA3Tag from an OMA file could be turned to a classic Tag and then written in a MP3 file as an ID3Tag.
     *
     *@param tag the tag to read the information from.
     *
     * @author skiron
     */
    public Tag(Tag tag) {
        // Fill all fields
        this.titleName = tag.getTitleName();
        this.artistName = tag.getArtistName();
        this.albumName = tag.getAlbumName();
        this.genre = tag.getGenre();
        this.trackNumber = tag.getTrackNumber();
        this.year = tag.getYear();
    }
    
    /**
     *Creates a tag from an existing Title.
     *Usefull while transcoding file, an EA3Tag from an OMA file could be transcoded to a classic Tag and then written in a MP3 file as an ID3Tag.
     *
     *@param title the title to read the information from.
     *
     * @author skiron
     */
    public Tag(Title title) {
        // Fill all fields
        this.titleName = title.getTitle();
        this.artistName = title.getArtist();
        this.albumName = title.getAlbum();
        this.genre = title.getGenre();
        this.trackNumber = title.getTitleNumber();
        this.year = title.getYear();
    }
    
    
    
/* GET METHODS */
    /**
    *Obtains the title name.
    *
    *@return The title name.
    */
    public String getTitleName(){
        return titleName;
    }

    /**
    *Obtains the artist name.
    *
    *@return The artist name.
    */
    public String getArtistName(){
        return artistName;
    }
    
    /**
    *Obtains the album name.
    *
    *@return The album name.
    */
    public String getAlbumName(){
        return albumName;
    }

    /**
    *Obtains the genre.
    *
    *@return The genre.
    */
    public String getGenre(){
        return genre;
    }

    /**
    *Obtains the track number.
    *
    *@return The track number.
    */
    public int getTrackNumber(){
        return trackNumber;
    }
    
    /**
    *Obtains the year.
    *
    *@return The year (YYYY).
    */
    public int getYear(){
        return year;
    }
    
/* SET METHODS */
    /**
    *Set the title name.
    *@param newTitleName The title name.
    */
    public void setTitleName(String newTitleName){
        titleName = newTitleName;
    }
    
    /**
    *Set the artist name.
    *
    *@param newArtistName the artist name.
    */
    public void setArtistName(String newArtistName){
       artistName = newArtistName;
    }
    
    /**
    *Set the album name.
    *
    *@param newAlbumName the album name.
    */
    public void setAlbumName(String newAlbumName){
       albumName = newAlbumName;
    }
    
    /**
    *Set the genre.
    *
    *@param newGenre the genre.
    */
    public void setGenre(String newGenre){
       genre = newGenre;
    }
    
    /**
    *Set the track number.
    *
    *@param newTrackNumber the track number.
    */
    public void setTrackNumber(int newTrackNumber){
       trackNumber = newTrackNumber;
    }
    
    /**
    *Set the year.
    *
    *@param newYear the year.
    */
    public void setYear(int newYear){
       year = newYear;
    }
    
/* METHODS */
}
