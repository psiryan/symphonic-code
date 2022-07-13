/*
 * SystemPanel.java
 *
 * Created on 31 mars 2007, 11:11
 */

package org.naurd.media.jsymphonic.manager;
import org.naurd.media.jsymphonic.system.SystemFile;
import org.naurd.media.jsymphonic.title.Title;

/**
 * This class is able to load a SystemFile and to show all of 
 * it's titles.
 * @author Patrick Balleux
 */
public class SystemPanel extends javax.swing.JPanel {
    /** Creates new form SystemPanel */
    private SystemFile currentSystemFile = null;
    private java.util.ArrayList genres = new java.util.ArrayList();
    private java.util.ArrayList artists = new java.util.ArrayList();
    private java.util.ArrayList albums = new java.util.ArrayList();
    private java.util.ArrayList titles = new java.util.ArrayList();
    
    
    /**
     * Main Constructor of the SystemPanel
     */
    public SystemPanel(){
        initComponents();
    }
    
    /**
     * Constructor that can initialize the content with a SystemFile.
     * 
     * This is the prefered way of instanciating this class
     * @param sf A SystemFile to load in the panel
     */
    public SystemPanel(org.naurd.media.jsymphonic.system.SystemFile sf) {
        initComponents();
        setSystemFile(sf);
    }
    
    /**
     * This method can load a SystemFile if the SystemFile
     * was not used in the Constructor.
     * @param sf The SystemFile to load
     */
    public void setSystemFile(SystemFile sf){
        currentSystemFile = sf;
        sf.refreshTitles();
        Title[] ts = currentSystemFile.getTitles();
        //insert titles
        for(int i = 0;i<ts.length;i++){
            titles.add(ts[i]);
            
            // while at it, insert albums
            if (!albums.contains(ts[i].getAlbum())){
                albums.add(ts[i].getAlbum());
            }
            // insert Artist list
            if (!artists.contains(ts[i].getAlbum())){
                artists.add(ts[i].getArtist());
            }
            if (!genres.contains(ts[i].getGenre())){
                genres.add(ts[i].getGenre());
            }
        }
        refreshTitleList();
    }
    
    /**
     * Called when the content needs to be refreshed.
     */
    public void refreshTitleList(){
        
        javax.swing.tree.DefaultMutableTreeNode root = new javax.swing.tree.DefaultMutableTreeNode("root");
        
        if (optArtistAlbum.isSelected()){
            fillTreeByArtistAlbum(root);
        }
        else if (optGenreArtist.isSelected()){
            fillTreeByGenreArtist(root);
        }
        javax.swing.tree.DefaultTreeModel model = new javax.swing.tree.DefaultTreeModel(root);
        treeTitles.setModel(model);
        treeTitles.revalidate();
        
    }
    
    private void fillTreeByArtistAlbum(javax.swing.tree.DefaultMutableTreeNode root){
        //sort artist name first...
        Object[] artistsSorted = artists.toArray();
        java.util.Arrays.sort(artistsSorted);
        
        for (int i = 0; i < artistsSorted.length;i++){
            javax.swing.tree.DefaultMutableTreeNode nodeArtist = new javax.swing.tree.DefaultMutableTreeNode(artistsSorted[i].toString());
            root.add(nodeArtist);
            java.util.Vector<Title> ts = new java.util.Vector<Title>();
            //Trying to find all title for that artists...
            for (int j=0;j<titles.size();j++){
                Title t = (Title)titles.get(j);
                if (t.getArtist().equals(artistsSorted[i])){
                    ts.add(t);
                }
            }
            //All titles were found for that artists...
            //now filtering only the titles
            java.util.Vector<String> as = new java.util.Vector<String>();
            for (int j=0;j<ts.size();j++){
                if (!as.contains(ts.get(j).getAlbum()))
                    as.add(ts.get(j).getAlbum());
            }
            // ok, albums is not filtered... 
            for (int j=0;j<as.size();j++){
                javax.swing.tree.DefaultMutableTreeNode nodeAlbum = new javax.swing.tree.DefaultMutableTreeNode(as.get(j));
                for (int k=0;k<ts.size();k++){
                    if (ts.get(k).getAlbum().equals(as.get(j))){
                        nodeAlbum.add(new javax.swing.tree.DefaultMutableTreeNode(ts.get(k)));
                    }
                }
                nodeArtist.add(nodeAlbum);
            }
        }
        
    }
    private void fillTreeByGenreArtist(javax.swing.tree.DefaultMutableTreeNode root){
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        grpSorting = new javax.swing.ButtonGroup();
        scrollTreeView = new javax.swing.JScrollPane();
        treeTitles = new javax.swing.JTree();
        panSearch = new javax.swing.JPanel();
        lblSearch = new javax.swing.JLabel();
        txtSearchText = new javax.swing.JTextField();
        btnDoSearch = new javax.swing.JButton();
        panSort = new javax.swing.JPanel();
        lblSort = new javax.swing.JLabel();
        optArtistAlbum = new javax.swing.JRadioButton();
        optGenreArtist = new javax.swing.JRadioButton();

        setLayout(new java.awt.BorderLayout());

        treeTitles.setRootVisible(false);
        treeTitles.setShowsRootHandles(true);
        scrollTreeView.setViewportView(treeTitles);

        add(scrollTreeView, java.awt.BorderLayout.CENTER);

        panSearch.setLayout(new java.awt.BorderLayout());

        lblSearch.setText(java.util.ResourceBundle.getBundle("org/naurd/media/symphonic/manager/languages").getString("SEARCH"));
        panSearch.add(lblSearch, java.awt.BorderLayout.WEST);

        txtSearchText.setText("jTextField1");
        panSearch.add(txtSearchText, java.awt.BorderLayout.CENTER);

        btnDoSearch.setText(java.util.ResourceBundle.getBundle("org/naurd/media/symphonic/manager/languages").getString("EXECUTE_SEARCH"));
        panSearch.add(btnDoSearch, java.awt.BorderLayout.EAST);

        add(panSearch, java.awt.BorderLayout.SOUTH);

        panSort.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lblSort.setText(java.util.ResourceBundle.getBundle("org/naurd/media/symphonic/manager/languages").getString("SORT_BY"));
        panSort.add(lblSort);

        grpSorting.add(optArtistAlbum);
        optArtistAlbum.setSelected(true);
        optArtistAlbum.setText(java.util.ResourceBundle.getBundle("org/naurd/media/symphonic/manager/languages").getString("SORT_ARTIST_ALBUM"));
        optArtistAlbum.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        optArtistAlbum.setMargin(new java.awt.Insets(0, 0, 0, 0));
        panSort.add(optArtistAlbum);

        grpSorting.add(optGenreArtist);
        optGenreArtist.setText(java.util.ResourceBundle.getBundle("org/naurd/media/symphonic/manager/languages").getString("SORT_GENRE_ARTIST"));
        optGenreArtist.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        optGenreArtist.setMargin(new java.awt.Insets(0, 0, 0, 0));
        panSort.add(optGenreArtist);

        add(panSort, java.awt.BorderLayout.NORTH);

    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDoSearch;
    private javax.swing.ButtonGroup grpSorting;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JLabel lblSort;
    private javax.swing.JRadioButton optArtistAlbum;
    private javax.swing.JRadioButton optGenreArtist;
    private javax.swing.JPanel panSearch;
    private javax.swing.JPanel panSort;
    private javax.swing.JScrollPane scrollTreeView;
    private javax.swing.JTree treeTitles;
    private javax.swing.JTextField txtSearchText;
    // End of variables declaration//GEN-END:variables
    
}
