/*  Copyright (C) 2008 Daniel Žalar (danizmax@gmail.com)
 * 
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
 * JSymphonicWindow.java
 *
 */

package org.danizmax.jsymphonic.gui;


/**
 * This is the main JSymphonic class that is used for starting the application
 * 
 * @author danizmax - Daniel Žalar (danizmax@gmail.com)
 */
public class JSymphonic {

        /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        JSymphonicWindow jsw = new JSymphonicWindow();
        jsw.setLocationByPlatform(true);
        jsw.setVisible(true);
    }
    
}
