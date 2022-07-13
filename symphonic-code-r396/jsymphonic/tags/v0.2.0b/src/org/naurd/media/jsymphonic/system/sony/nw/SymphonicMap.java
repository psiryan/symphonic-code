/*
 * SymphonicMap.java
 *
 * Created on 26 juillet 2007, 16:44
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.system.sony.nw;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import java.util.Set;
import org.naurd.media.jsymphonic.title.Mp3;
import org.naurd.media.jsymphonic.title.Title;

/**
 *
 * @author skiron
 */
public class SymphonicMap extends HashMap {
    
    /** Constructors */
    public SymphonicMap() {
        super();
    }

    public SymphonicMap(int initialCapacity) {
        super(initialCapacity);
    }
    
    public SymphonicMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }
    
    /** Overwritten methods */
    public Integer put(Object key, Integer value) {
        if( this.containsValue(value) ) {
            System.err.println("This value is already associated in this map. No changes are made to the map.");
            return null;
        }
        else {
            return (Integer)super.put(key, value);
        }
    }
    
    public void putAll(Map m){
        System.err.println("This method isn't implemented in this class.");
    }
    
    /** New methods */
    public Object getKey(Integer value) {
        if( !this.containsValue(value) ) {
            return null;
        }
        
        Object valueInTheMap, key;
        Iterator it = this.keySet().iterator();
        while( it.hasNext() ) {
            key = it.next();
            valueInTheMap = this.get(key);
            if( value.equals(valueInTheMap)) {
                return key;
            }
        }
        
        return null;
    }
    
    public Integer getValue(Object key) {
        if( !this.containsKey(key) ) {
            System.err.println("This key isn't in this map. 0 is returned.");
            return 0;
        }
        
        return (Integer)super.get(key);
    }
    
    public Integer maxValue() {
        Integer valueToReturn, currentValue;
        Collection values = this.values();
        Iterator it = values.iterator();
        
        if( it.hasNext() ) {
           valueToReturn = (Integer)it.next();
        }
        else{
           valueToReturn = null;
        }
        
        while( it.hasNext() ) {
            currentValue = (Integer)it.next();
            
            if( currentValue.compareTo(valueToReturn) > 0 ) {
                valueToReturn = currentValue;
            }
        }
        
        return valueToReturn;
    }
}
