/*
 * DlgSystemFileSelector.java
 *
 * Created on 5 juin 2007, 20:06
 */

package org.naurd.media.jsymphonic.manager;
import java.util.Enumeration;
import org.naurd.media.jsymphonic.system.SystemFile;
/**
 *
 * @author  pballeux
 */
public class DlgSystemFileSelector extends javax.swing.JDialog {
    
    private SystemFile systemfile = null;
    java.util.ResourceBundle rb = null;
    java.util.Vector<String> classnames = new java.util.Vector<String>();
    /** Creates new form DlgSystemFileSelector */
    public DlgSystemFileSelector(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        fillSystemFile();
    }
    
    private void fillSystemFile(){
        javax.swing.DefaultComboBoxModel model = new javax.swing.DefaultComboBoxModel();
        String cname = "";
        try{
            rb = java.util.ResourceBundle.getBundle("org/naurd/media/symphonic/system/SystemFiles");
            
            java.util.Enumeration names = rb.getKeys();
            while(names.hasMoreElements()){
                cname = names.nextElement().toString();
                classnames.add(cname);
                model.addElement(rb.getString(cname));
            }
            
        } catch(Exception e){
            e.printStackTrace();
        }
        cboSystemFiles.setModel(model);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblTitle = new javax.swing.JLabel();
        cboSystemFiles = new javax.swing.JComboBox();
        txtURLPath = new javax.swing.JTextField();
        txtName = new javax.swing.JTextField();
        panButtons = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(java.util.ResourceBundle.getBundle("org/naurd/media/symphonic/manager/languages").getString("TITLE_ADDSYSTEMFILE"));
        setAlwaysOnTop(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        lblTitle.setText(java.util.ResourceBundle.getBundle("org/naurd/media/symphonic/manager/languages").getString("SELECT_SYSTEMEFILE"));
        getContentPane().add(lblTitle);

        cboSystemFiles.setBorder(javax.swing.BorderFactory.createTitledBorder(java.util.ResourceBundle.getBundle("org/naurd/media/symphonic/manager/languages").getString("SYSTEM_FILE")));
        getContentPane().add(cboSystemFiles);

        txtURLPath.setBorder(javax.swing.BorderFactory.createTitledBorder(java.util.ResourceBundle.getBundle("org/naurd/media/symphonic/manager/languages").getString("URLORPATH")));
        getContentPane().add(txtURLPath);

        txtName.setBorder(javax.swing.BorderFactory.createTitledBorder(java.util.ResourceBundle.getBundle("org/naurd/media/symphonic/manager/languages").getString("NAMEOFSYSTEMFILE")));
        getContentPane().add(txtName);

        panButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        btnAdd.setText(java.util.ResourceBundle.getBundle("org/naurd/media/symphonic/manager/languages").getString("ADD"));
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        panButtons.add(btnAdd);

        btnCancel.setText(java.util.ResourceBundle.getBundle("org/naurd/media/symphonic/manager/languages").getString("CANCEL"));
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        panButtons.add(btnCancel);

        getContentPane().add(panButtons);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        String name = txtName.getText();
        String path = txtURLPath.getText();
        String classname = classnames.get(cboSystemFiles.getSelectedIndex());
        try{
            SystemFile s = (SystemFile)Class.forName(classname).newInstance();
            s.setSource(path);
            s.setSourceName(name);
            s.setSourceDescription("");
            systemfile=s;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        dispose();
    }//GEN-LAST:event_btnAddActionPerformed
    
    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed
    
    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        
        
    }//GEN-LAST:event_formWindowClosed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DlgSystemFileSelector(new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }
    
    public SystemFile getSelection(){
        return systemfile;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JComboBox cboSystemFiles;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel panButtons;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtURLPath;
    // End of variables declaration//GEN-END:variables
    
}
