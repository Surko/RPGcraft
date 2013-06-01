/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.utils;

/**
 *
 * @author kirrie
 */
public class Pair {
    
    private Object A;
    private Object B;
    
    public Pair(Object A, Object B) {
        this.A = A;
        this.B = B;
    }
    
    public Object getFirst() {
        return A;
    }
    
    public Object getSecond() {
        return B;
    }
    
}
