/*
 * JSymphonicMap.java
 *
 * Created on 26 juillet 2007, 16:44
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.toolBox;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import java.util.logging.Logger;

/**
 *
 * @author skiron
 */
public class JSymphonicMap extends HashMap {
    //Other
    private static Logger logger = Logger.getLogger("org.naurd.media.jsymphonic.toolBox.JSymphonicMap");
    
    /** Constructors */
    public JSymphonicMap() {
        super();
    }

    public JSymphonicMap(int initialCapacity) {
        super(initialCapacity);
    }
    
    public JSymphonicMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }
    
    /** Overwritten methods */
    public Integer put(Object key, Integer value) {
        if( this.containsValue(value) ) {
            logger.warning("This value is already associated in this map. No changes are made to the map.");
            return null;
        }
        else {
            return (Integer)super.put(key, value);
        }
    }
    
    @Override
    public void putAll(Map m){
        logger.severe("This method isn't implemented in this class.");
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
            logger.warning("This key isn't in this map. 0 is returned.");
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
