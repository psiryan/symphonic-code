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
 * OmaDataBaseGen2.java
 *
 * Created on 25 february 2008, 20:44
 *
 */

package org.naurd.media.jsymphonic.system.sony.nw;

/**
 *
 * @author skiron
 * @author Daniel Žalar - added events to ensure GUI independancy
 */
public class OmaDataBaseGen2 extends OmaDataBase{
/* FIELDS */
    private java.io.File esys;
    //private Map titleKeys;
    
    
/* CONSTRUCTORS */
    /**
     * Creates a new instance of OmaDataBaseGen2
     */
    public OmaDataBaseGen2(java.io.File esys) {
        // Call the super contructor
        super();

        // TODO
        this.esys = esys;
    }
    

/* METHODS */ 
    /**
     * Update the database, complete all the other list (than the title list).
     *
     *@author nicolas_cardoso
     */
    public void update() {
        //TODO
    }

    /**
     * Write the database to the player.
     *
     *@param genNw The instance of the Net walkman.
     *
     *@author nicolas_cardoso
     */
    public void write(NWGeneric genNw) {
        // TODO
    }

    @Override
    public int getNumberOfFiles() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
