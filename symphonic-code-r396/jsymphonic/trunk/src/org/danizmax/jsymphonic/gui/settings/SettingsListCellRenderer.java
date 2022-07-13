/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danizmax.jsymphonic.gui.settings;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author danizmax
 */
public class SettingsListCellRenderer implements ListCellRenderer{

    protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        renderer.setVerticalTextPosition(JLabel.BOTTOM);
        renderer.setHorizontalTextPosition(JLabel.CENTER);
        renderer.setHorizontalAlignment(JLabel.CENTER);

        //TODO this may be temporary solution
        if(index == 0){
            renderer.setIcon(new ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/icons/profiles.png")));
        }else if(index == 1){
            renderer.setIcon(new ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/icons/interface.png")));
        }else if(index == 2){
            renderer.setIcon(new ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/icons/transfer.png")));
        }
        return renderer;
    }

}
