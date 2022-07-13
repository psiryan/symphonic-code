/*
 * JSymphonicMutableTreeNode.java
 *
 * Created on 20 juillet 2007, 19:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.manager;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author skiron
 */
public class JSymphonicMutableTreeNode extends DefaultMutableTreeNode {
    /* Constants */
    public static final int ARTIST = 1;
    public static final int ALBUM = 2;
    public static final int GENRE = 3;
    public static final int PLAYLIST = 4;
    public static final int TITLE = 5;
    public static final int USB = 6;
    public static final int FOLDER = 7;
    public static final int AUDIOFILE = 8;
    
    /* Fields */
    private int type; //Add a new field to handle a different icon per type
    
    /* Constructors */
    /**
     * Creates a new instance of JSymphonicMutableTreeNode
     */
    public JSymphonicMutableTreeNode(int type) {
        super();
        this.type = type; 
    }
    
    public JSymphonicMutableTreeNode(Object userObject, int type) {
        super(userObject);
        this.type = type; 
    }
    
    /* Methods */
    //Add a method to know the type
    public int getType() {
        return type;
    }
    
}
