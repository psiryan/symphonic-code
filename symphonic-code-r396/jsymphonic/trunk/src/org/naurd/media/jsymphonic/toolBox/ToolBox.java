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
 *****
 *
 * ToolBox.java
 *
 * Created on 3 juin 2009, 15:19:37
 *
 */

package org.naurd.media.jsymphonic.toolBox;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This class only contains static functions, which were missing but couldn't be attach to any other class.
 *
 * @author skiron
 */
public class ToolBox {
   /**
    * Copy a file from a source to a destination. This method concerns files only.
    *
    * @param src The source file.
    * @param dest The destination file.
    * @throws java.io.IOException
    */
   public static void copyFile(File src, File dest) throws IOException {
      if (!src.exists()) throw new IOException(
         "File not found '" + src.getAbsolutePath() + "'");
      BufferedOutputStream out = new BufferedOutputStream(
         new FileOutputStream(dest));
      BufferedInputStream in = new BufferedInputStream(
         new FileInputStream(src));

      byte[] read = new byte[128];
      int len = 128;
      while ((len = in.read(read)) > 0)
         out.write(read, 0, len);

      out.flush();
      out.close();
      in.close();
   }

   /*
    * Delete file or folder (and it sub files and sub folders)
    *
    * @param path The file or folder to delete.
    */
    public static void recursifDelete(File path) throws IOException {
        if (!path.exists()) throw new IOException("File not found '" + path.getAbsolutePath() + "'");
        if (path.isDirectory()) {
            File[] children = path.listFiles();
            for (int i=0; children != null && i<children.length; i++)
                recursifDelete(children[i]);
            if (!path.delete()) throw new IOException("No delete path '" + path.getAbsolutePath() + "'");
        }
        else if (!path.delete()) throw new IOException("No delete file '" + path.getAbsolutePath() + "'");
    }
}
