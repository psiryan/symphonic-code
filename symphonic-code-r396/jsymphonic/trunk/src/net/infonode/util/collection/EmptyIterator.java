/*
 * Copyright (C) 2004 NNL Technology AB
 * Visit www.infonode.net for information about InfoNode(R) 
 * products and how to contact NNL Technology AB.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, 
 * MA 02111-1307, USA.
 */


// $Id: EmptyIterator.java,v 1.2 2005/05/13 15:07:02 johan Exp $
package net.infonode.util.collection;

import java.util.Iterator;

/**
 * @author $Author: johan $
 * @version $Revision: 1.2 $
 */
public class EmptyIterator implements Iterator {
  public static final EmptyIterator INSTANCE = new EmptyIterator();

  private EmptyIterator() {
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }

  public boolean hasNext() {
    return false;
  }

  public Object next() {
    return null;
  }
}
