/*
 * JSymphonicListCellRenderer.java
 *
 * Created on 12 octobre 2007, 19:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.toolBox;

import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;

/**
 *
 * @author skiron
 */
public class JSymphonicListCellRenderer extends DefaultListCellRenderer {
    
    /**
     * Creates a new instance of JSymphonicListCellRenderer
     */
    public JSymphonicListCellRenderer() {
    }
   
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus){
        Component retValue = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        //retValue.setBackground(new Color(255,0,0));
        //setIcon(new ImageIcon(getClass().getResource("/org/naurd/media/symphonic/ressources/usb.png")));
        return retValue;
    }
}
