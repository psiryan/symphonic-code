/*
 * Vector.java
 *
 * Created on September 27, 2007, 10:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.aingoppa.javastage;

/**
 *
 * @author aingoppa
 */
public class Vector<E> extends java.util.Vector<E>{
	
	public void add(int index, E element) {
		if (index >= size())
			setSize(index +1);
		super.add(index, element);
	}
	public E set(int index, E element) {
		if (index >= size())
			setSize(index +1);
		return super.set(index, element);		
	}
	public E get(int index) {
		if (index >= size())
			setSize(index +1);
		return super.get(index);		
	}
	public int indexNull(int after) {
		int i = after;
		while (i < size()) {
			if (null == super.get(i))
				return i;
			i++;
		}
		return i;
	}
	public int sizeNotNull() {
		int size = 0;
		for (int i = 0; i < size(); i++) {
			if (null != super.get(i)) {
				size++;
			}
		}
		return size;
	}
	
	static public boolean isEmpty(Vector v) {
		return (null == v || 0 == v.sizeNotNull());
	}
}
