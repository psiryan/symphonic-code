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
 * OmaDataBase.java
 *
 * Created on 20 mai 2007, 09:15
 *
 */

package org.naurd.media.jsymphonic.system.sony.nw;

import org.naurd.media.jsymphonic.title.Title;
import org.naurd.media.jsymphonic.toolBox.JSymphonicMap;

/**
 * This class includes methods for building the dataBase and writing it to the config files in the device.
 *
 * This class is abstract as it implement just things common to all generations (all generation have a list of title for instance...)
 *
 *@version 03.23.2008
 *@author Nicolas Cardoso
 *@author Daniel Žalar - added events to ensure GUI independancy
 */
public abstract class OmaDataBase {
/* FIELDS */
    protected JSymphonicMap titles; // List of titles
    protected JSymphonicMap artists; // List of artits
    protected JSymphonicMap albums; // List of albums
    protected JSymphonicMap genres; // List of genres

    
    
/* CONSTRUCTORS */
    /**
     * Allows to create an instance of OmaDataBase from an existing device. 
     * The database, once created is empty. Titles list can be filled with the method "addTitleWithTitleId". Before been written to the device, the database should be updated, with "update" method, to fill in the artists, albums,... lists.
     *
     * @author nicolas_cardoso
     */
    public OmaDataBase() {
        titles = new JSymphonicMap();
        artists = new JSymphonicMap();
        albums = new JSymphonicMap();
        genres = new JSymphonicMap();
    }
    
    
    
/* ABSTRACT METHODS */ 
    /**
     * Update the database, complete all the other list (than the title list).
     * This method is abstract as it depends on the contents of the database...
     *
     *@author nicolas_cardoso
     */
    public abstract void update();
    
    /**
     * Write the database to the player.
     * This method is abstract as it depends on the contents of the database...
     *
     *@param genericNw The instance of the Net walkman.
     *
     *@author nicolas_cardoso
     */
    public abstract void write(NWGeneric genericNw);
    
    /**
     * Obtains the number of database files to be written. This method is usefull to inform the user throught the GUI
     * 
     * @return the number of database files.
     */
    public abstract int getNumberOfFiles();
    
/* METHODS */ 
    /**
     * Add a title to the database (the title is added to the titles list, other list are not changed) with a given title ID.
     * A title ID is a unique number representing the title in the database (it's also the name of the file in the 10FXX folders).
     *
     * @param title The title to add to the database.
     * @param titleId The title ID which will be associated to the title in the database. A free ID can be obtain using "getFreeTitleId" method.
     *
     * @author nicolas_cardoso
     */
    public void addTitleWithTitleId(Title title, int titleId) {
        titles.put(title, titleId);
    }
    
    
    /**
     * Add a playlist to the device.
     */
    public void addPlaylist() {
        //TODO
    }
   
    
    /**
     * Remove a title from the database (the title is removed from the titles list, other list are not changed)
     *
     *@param titleToRemove The title to remove.
     *
     *@author nicolas_cardoso
     */
    public void removeTitle(Title titleToRemove) {
        titles.remove(titleToRemove);
    }
    
    
    
    /**
     * Obtain the list of the titles currently saved in the database.
     * 
     *@return The list of the titles.
     *
     *@author nicolas_cardoso
     */
    public JSymphonicMap getTitles() {
        return titles;
    }
    
    
    
    /**
     * Obtain the title ID associated to a title.
     *
     *@param title the title which the title ID is wanted.
     *@return The title ID.
     *
     *@author nicolas_cardoso
     */   
    public int getTitleId(Title title) {
        return (Integer)titles.getValue(title);
    }

    
    
    /**
     * Obtain the next free ID in the title list.
     *
     *@return A free ID for a title in the title list
     *
     *@author nicolas_cardoso
     */
    public int getFreeTitleId() {
        int i = 1;
        
        while(titles.containsValue(i)) { //search the first ID unused
            i++;
        }
        
        return i;
    }
    
    

    /**
     * Clear the database. This methods empty all the lists.
     *
     *@author nicolas_cardoso
     */
    public void clear() {
        // Empty all the lists
        titles.clear();
        artists.clear();
        albums.clear();
        genres.clear();
    }
    

    
    /**
     * Obtain the next free ID in the artists list.
     *
     *@return A free ID for an artist in the artists list
     *
     *@author nicolas_cardoso
     */    
    protected int getFreeArtistId() {
        int i = 1;
        
        while(artists.containsValue(i)) { //search the first ID unused
            i++;
        }
        
        return i;
    }
    
    
    
    /**
     * Obtain the next free ID in the albums list.
     *
     *@return A free ID for an album in the albums list
     *
     *@author nicolas_cardoso
     */  
    protected int getFreeAlbumId() {
        int i = 1;
        
        while(albums.containsValue(i)) { //search the first ID unused
            i++;
        }
        
        return i;
    }
    
    
    
    /**
     * Obtain the next free ID in the genres list.
     *
     *@return A free ID for an genre in the genres list
     *
     *@author nicolas_cardoso
     */  
    protected int getFreeGenreId() {
        int i = 1;
        
        while(genres.containsValue(i)) { //search the first ID unused
            i++;
        }
        
        return i;
    }
}
