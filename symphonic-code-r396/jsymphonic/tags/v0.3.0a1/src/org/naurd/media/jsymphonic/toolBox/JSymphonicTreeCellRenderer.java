/*
 * MyTreeRenderer.java
 *
 * Created on 20 juillet 2007, 18:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.toolBox;

import java.awt.Color;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.UIManager;
import org.danizmax.jsymphonic.toolkit.JSymphonicMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.naurd.media.jsymphonic.title.Title;

/**
 *
 * @author skiron
 * @author danizmax - Daniel Žalar (danizmax@gmail.com) - add forground colors
 */
public class JSymphonicTreeCellRenderer extends DefaultTreeCellRenderer {
    
    void doColorIcons(){
    }
    
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);


        if( ((JSymphonicMutableTreeNode)value).getType() == JSymphonicMutableTreeNode.TYPE_USB ) { 
           setIcon(new ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/usb.png")));
        }
        else if( ((JSymphonicMutableTreeNode)value).getType() == JSymphonicMutableTreeNode.TYPE_ALBUM ) { 
           setIcon(new ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/album.png")));
        }
        else if( ((JSymphonicMutableTreeNode)value).getType() == JSymphonicMutableTreeNode.TYPE_ARTIST ) {
           setIcon(new ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/artist.png")));
        }
        else if( ((JSymphonicMutableTreeNode)value).getType() == JSymphonicMutableTreeNode.TYPE_GENRE ) {
           setIcon(new ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/genre.png")));
        }
        else if( ((JSymphonicMutableTreeNode)value).getType() == JSymphonicMutableTreeNode.TYPE_PLAYLIST ) {
           setIcon(new ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/playlist.png")));
        }
        else if( ((JSymphonicMutableTreeNode)value).getType() == JSymphonicMutableTreeNode.TYPE_TITLE ) {
           setIcon(new ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/title.png")));
        }
        else if( ((JSymphonicMutableTreeNode)value).getType() == JSymphonicMutableTreeNode.TYPE_FOLDER ) {
           setIcon(new ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/folder.png")));
        }
        else if( ((JSymphonicMutableTreeNode)value).getType() == JSymphonicMutableTreeNode.TYPE_AUDIOFILE ) {
           setIcon(new ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/audiofile.png")));
        }
        else {
           setIcon(new ImageIcon(getClass().getResource("/org/danizmax/jsymphonic/resources/defaut.png")));
        }
        
        //set the action color
        if( ((JSymphonicMutableTreeNode)value).getNodeAction() == Title.TODELETE) { 
            this.setForeground(Color.RED);
        }else if( ((JSymphonicMutableTreeNode)value).getNodeAction() ==Title.TOEXPORT ){ 
            this.setForeground(Color.BLUE);
        }else if( (((JSymphonicMutableTreeNode)value).getNodeAction() == Title.TOIMPORT ) || ( ((JSymphonicMutableTreeNode)value).getNodeAction() ==Title.TOENCODE )  || ( ((JSymphonicMutableTreeNode)value).getNodeAction() ==Title.TODECODE ) || ( ((JSymphonicMutableTreeNode)value).getNodeAction() ==Title.TOENCODEANDREMOVEFROMDISK ) || ( ((JSymphonicMutableTreeNode)value).getNodeAction() ==Title.TOADDANDREMOVEFROMDISK )){
            this.setForeground(Color.GREEN);
        }else if( ((JSymphonicMutableTreeNode)value).getNodeAction() == Title.NONE ) {    
            this.setForeground(UIManager.getColor(Color.BLACK));
        }

        return this;
    }
}
