/*
 * OmaDataBase.java
 *
 * Created on 20 mai 2007, 09:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.system.sony.nw;

import org.naurd.media.jsymphonic.manager.JSymphonic;
import org.naurd.media.jsymphonic.title.Title;

/**
 *Describes the methods to be implemented for a DataBase in Sony Devices.
 *
 *@version 06.06.2007
 *@author Nicolas Cardoso
 */
public interface OmaDataBase {
    public void addTitleWithTitleId(Title title, int titleId);
    public void removeTitle(Title titleToRemove);
    public int getTitleId(Title title);
    public SymphonicMap getTitles();
    public int getFreeTitleId();
    public void write(JSymphonic jsymphonic, Double increment);
    public void update();
    public void clear();
}
