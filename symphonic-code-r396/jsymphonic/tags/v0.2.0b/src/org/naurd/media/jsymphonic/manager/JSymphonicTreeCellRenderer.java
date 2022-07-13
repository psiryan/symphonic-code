/*
 * MyTreeRenderer.java
 *
 * Created on 20 juillet 2007, 18:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.manager;

import java.awt.Color;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author skiron
 */
public class JSymphonicTreeCellRenderer extends DefaultTreeCellRenderer {
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

//        if( ((DefaultMutableTreeNode)value).isRoot() ) {  
//            setIcon(new ImageIcon(getClass().getResource("/org/naurd/media/symphonic/ressources/usb.png")));
//        }
//        else if( ((DefaultMutableTreeNode)value).isLeaf() ) {  
//            setIcon(new ImageIcon(getClass().getResource("/org/naurd/media/symphonic/ressources/title.png")));
//        }
//        else {
        if( ((JSymphonicMutableTreeNode)value).getType() == JSymphonicMutableTreeNode.USB ) { 
           setIcon(new ImageIcon(getClass().getResource("/org/naurd/media/jsymphonic/ressources/usb.png")));
//           setTextNonSelectionColor(new Color(0x00,0x00,0xFF));
//           setTextSelectionColor(new Color(0x00,0x00,0xFF));
//           setForeground(new Color(0x00,0x00,0xFF));
        }
        else if( ((JSymphonicMutableTreeNode)value).getType() == JSymphonicMutableTreeNode.ALBUM ) { 
           setIcon(new ImageIcon(getClass().getResource("/org/naurd/media/jsymphonic/ressources/album.png")));
        }
        else if( ((JSymphonicMutableTreeNode)value).getType() == JSymphonicMutableTreeNode.ARTIST ) {
           setIcon(new ImageIcon(getClass().getResource("/org/naurd/media/jsymphonic/ressources/artist.png")));
        }
        else if( ((JSymphonicMutableTreeNode)value).getType() == JSymphonicMutableTreeNode.GENRE ) {
           setIcon(new ImageIcon(getClass().getResource("/org/naurd/media/jsymphonic/ressources/genre.png")));
        }
        else if( ((JSymphonicMutableTreeNode)value).getType() == JSymphonicMutableTreeNode.PLAYLIST ) {
           setIcon(new ImageIcon(getClass().getResource("/org/naurd/media/jsymphonic/ressources/playlist.png")));
        }
        else if( ((JSymphonicMutableTreeNode)value).getType() == JSymphonicMutableTreeNode.TITLE ) {
           setIcon(new ImageIcon(getClass().getResource("/org/naurd/media/jsymphonic/ressources/title.png")));
        }
        else if( ((JSymphonicMutableTreeNode)value).getType() == JSymphonicMutableTreeNode.FOLDER ) {
           setIcon(new ImageIcon(getClass().getResource("/org/naurd/media/jsymphonic/ressources/folder.png")));
        }
        else if( ((JSymphonicMutableTreeNode)value).getType() == JSymphonicMutableTreeNode.AUDIOFILE ) {
           setIcon(new ImageIcon(getClass().getResource("/org/naurd/media/jsymphonic/ressources/audiofile.png")));
        }
        else {
           setIcon(new ImageIcon(getClass().getResource("/org/naurd/media/jsymphonic/ressources/defaut.png")));
        }

        return this;
    }
}
