/*
 * TitleRef.java
 *
 * Created on 27 juillet 2007, 21:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.system.sony.nw;

/**
 *
 * @author skiron
 */
public class TitleRef {
    int part1;
    int part2;
    int part3;
    int part4;
    int part5;
    int part6;
    
    /** Creates a new instance of TitleRef */
    public TitleRef(int part1, int part2, int part3, int part4, int part5, int part6) {
        this.part1 = part1;
        this.part2 = part2;
        this.part3 = part3;
        this.part4 = part4;
        this.part5 = part5;
        this.part6 = part6;
    }
    
    /** Methods **/
    public int getPart1(){
        return part1;
    }
    public int getPart2(){
        return part2;
    }
    public int getPart3(){
        return part3;
    }
    public int getPart4(){
        return part4;
    }
    public int getPart5(){
        return part5;
    }
    public int getPart6(){
        return part6;
    }
}
