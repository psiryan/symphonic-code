/*
 * Copyright (C) 2007, 2008, 2009 Patrick Balleux, Nicolas Cardoso De Castro
 * (nicolas_cardoso@users.sourceforge.net), Daniel Žalar (danizmax@gmail.com)
 *
 * This file is part of JSymphonic program.
 *
 * JSymphonic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JSymphonic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSymphonic. If not, see <http://www.gnu.org/licenses/>.
 *
 *****/

package org.danizmax.jsymphonic.toolkit;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import org.danizmax.jsymphonic.gui.device.DevicePanel;
import org.danizmax.jsymphonic.gui.local.LocalPanel;

/**
 *
 * @author danizmax
 */
public class FileTextTransferHandler  extends TransferHandler {
    private static final long serialVersionUID = 1L;
    private static final String URI_LIST_MIME_TYPE = "text/uri-list;class=java.lang.String";
    private DataFlavor fileFlavor, stringFlavor;
    private DataFlavor uriListFlavor;


  public FileTextTransferHandler() {
    fileFlavor = DataFlavor.javaFileListFlavor;
    stringFlavor = DataFlavor.stringFlavor;

    try {
      uriListFlavor = new DataFlavor(URI_LIST_MIME_TYPE);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  private void doImport(JComponent c, List files){
      Object o = c.getParent().getParent().getParent().getParent();
      if(o instanceof DevicePanel){
          
          File f[] =  new File[files.size()];
          for(int i=0;i<files.size();i++){
              f[i] = (File)files.get(i);
          }
          ((DevicePanel)o).scheduleTrackImport(f);
      }/*else{
          o = c.getParent().getParent().getParent();
          if(o instanceof LocalPanel){
              //TODO
          }
      }*/

      /*for(int i=0;i<files.size();i++){
            System.out.println("Process:  " + ((File)files.get(i)).getName());
      }*/
  }


  @Override
  public boolean importData(JComponent c, Transferable t) {
    if (!canImport(c, t.getTransferDataFlavors())) {
      return false;
    }
    try {
      // Windows
      if (hasFileFlavor(t.getTransferDataFlavors())) {
        final java.util.List files = (java.util.List) t.getTransferData(fileFlavor);
        doImport(c, files);
        return true;
      // Linux
      }else if(hasURIListFlavor(t.getTransferDataFlavors())){
        final List<File> files = textURIListToFileList((String) t.getTransferData(uriListFlavor));
        if(files.size()>0){
            doImport(c, files);
        }
      }else if (hasStringFlavor(t.getTransferDataFlavors())) {
        final List<File> files = textURIListToFileList(((String) t.getTransferData(stringFlavor)));
        if(files.size()>0){
            doImport(c, files);
        }
        //System.out.println(str);
        return true;
      }
    } catch (UnsupportedFlavorException ufe) {
      System.out.println("importData: unsupported data flavor");
    } catch (IOException ieo) {
      System.out.println("importData: I/O exception");
    }
    return false;
  }

  @Override
  public int getSourceActions(JComponent c) {
    return COPY;
  }

  @Override
  public boolean canImport(JComponent c, DataFlavor[] flavors) {
    if (hasFileFlavor(flavors)) {
      return true;
    }
    if (hasStringFlavor(flavors)) {
      return true;
    }
    return false;
  }


    @Override
    public Transferable createTransferable(JComponent c) {
      /* Transferable t = null;
        if(c instanceof JTree){
           t = new StringSelection(((JTree)c).getSelectionPath().toString());
           //System.out.println(((JTree)c).getSelectionPath());
       }*/

       Transferable t = null;
       Object o = c.getParent().getParent().getParent();
       if(o instanceof LocalPanel){

          File[] files = ((LocalPanel)o).getSelectedTracks();
          String s = "";
          for(int i=0;i<files.length;i++){
            s += files[i].toURI() + "\r\n";
          }
          t = new StringSelection(s);
      }else{
           o = c.getParent().getParent().getParent().getParent();
           if(o instanceof DevicePanel){
                File[] files = ((DevicePanel)o).getSelectedTracks();
                String s = "";
                for(int i=0;i<files.length;i++){
                    s += files[i].toURI() + "\r\n";
                }
                t = new StringSelection(s);
               // t = new StringSelection(((JTree)c).getSelectionPath().toString());
             
           }
      }
      // t = new StringSelection(((JTree)c).getSelectionPath().toString());
        return t;
    }

    @Override
    public void exportDone(JComponent c, Transferable t, int action) {
        /*if (action == COPY) {
            //TODO 
        }*/
        Object o = c.getParent().getParent().getParent().getParent();
        if(o instanceof DevicePanel){
                ((DevicePanel)o).scheduleTrackExport();
                ((DevicePanel)o).reloadTree();
        }
    }


  private boolean hasFileFlavor(DataFlavor[] flavors) {
    for (int i = 0; i < flavors.length; i++) {
      if (fileFlavor.equals(flavors[i])) {
        return true;
      }
    }
    return false;
  }

  private boolean hasStringFlavor(DataFlavor[] flavors) {
    for (int i = 0; i < flavors.length; i++) {
      if (stringFlavor.equals(flavors[i])) {
        return true;
      }
    }
    return false;
  }

  private boolean hasURIListFlavor(DataFlavor[] flavors) {
    for (int i = 0; i < flavors.length; i++) {
      if (uriListFlavor.equals(flavors[i])) {
        return true;
      }
    }
    return false;
  }

  /** Your helpfull function */
  private static List<File> textURIListToFileList(String data) {
    List<File> list = new ArrayList<File>(1);
    for (StringTokenizer st = new StringTokenizer(data, "\r\n"); st.hasMoreTokens();) {
      String s = st.nextToken();
      if (s.startsWith("#")) {// the line is a comment (as per the RFC 2483)
        continue;
      }
      try {
        URI uri = new URI(s);
        File file = new File(uri);
        list.add(file);
      } catch (URISyntaxException e) {
        e.printStackTrace();
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      }
    }
    return list;
  }

}
