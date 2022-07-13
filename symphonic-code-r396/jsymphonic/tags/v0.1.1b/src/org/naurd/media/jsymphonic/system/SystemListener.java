/*
 * SystemListener.java
 *
 * Created on October 15, 2006, 11:16 AM
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
public interface SystemListener {
    public void WritingProgress(SystemFile system,Title t,long currentSize);
    public void WritingEvent(SystemFile system,Title t,Exception e);
}
