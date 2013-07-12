/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.utils;

/**
 * Trieda Pair ktora pri vytvoreni instancie zdruzuje dva objekty (par objektov), ktore
 * sa daju jednotlivo metodou getFirst ci getSecond vratit. Vyuzitie pri vytvarani InMenu
 * pre dve entity (ako napriklad konverzacny panel).
 */
public class Pair<T,V> {
    
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    private T A;
    private V B;
    // </editor-fold>
    
    /**
     * Konstruktor pre vytvorenie paru
     * @param A Prvy objekt
     * @param B Druhy objekt
     */
    public Pair(T A, V B) {
        this.A = A;
        this.B = B;
    }
    
    /**
     * Metoda vrati prvy objekt v pare
     * @return Prvy objekt
     */
    public T getFirst() {
        return A;
    }
    
    /**
     * Metoda vrati druhy objekt v pare
     * @return Druhy objekt
     */
    public V getSecond() {
        return B;
    }
    
}
