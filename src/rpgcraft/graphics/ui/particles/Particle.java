/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics.ui.particles;

import java.awt.Image;

/**
 * Abstraktna trieda ktora tvori interface pre rozne castice definovane v tomto packagi.
 * Instancia zduzuje informacie o castice (pozicie, obrazok castice,...) + metody
 * pre pracu s casticou (nastavenie priznaku na vymazanie castice, aktivovanost castice,...)
 */
public abstract class Particle {    
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    /**
     * X a Y- ove pozicie vzhladom od hracovej entity
     */
    protected int x, y;
    /**
     * Obrazok castice na vykreslenie
     */
    protected Image toDraw;
    
    /**
     * Dlzka zivota castice
     */
    protected int span;
    
    /**
     * Sirka, vyska castice
     */
    protected int width, height;
    
    /**
     * Ci sa ma castica vymazat
     */
    protected boolean removed;   
    /**
     * Ci je castica aktivovana
     */
    protected boolean activation;
    // </editor-fold>        
    
    // <editor-fold defaultstate="collapsed" desc=" Abstraktne metody ">
    /**
     * Metoda ktora simuluje zivot castice (premenna span) na obrazovke.
     * Definovane v podedenych triedach.
     * @return True/False podla toho ci prebehol cely zivotny cyklus.
     */
    public abstract boolean update();
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    /**
     * Metoda ktora nastavi priznak castice remove na true => mame vymazat casticu
     */
    public void setRemoved() {
        removed = true;
    }
    
    /**
     * Metoda ktora aktivuje casticu
     */
    public void setActivated() {
        this.activation = true;
    }
    
    /**
     * Metoda ktora deaktivuje casticu
     */
    public void setDeactivated() {
        this.activation = false;        
    }   
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora ktora vrati ci sa ma castica vymazat
     * @return True/false ci sa ma castica vymazat
     */
    public boolean isRemoved() {
        return removed;
    }
        
    /**
     * Metoda ktora vrati ci je castica aktivovana.
     * @return Aktivovanost castice
     */
    public boolean isActivated() {
        return activation;
    }
    
    /**
     * Metoda ktora vrati obrazok castice.
     * @return Obrazok castice
     */
    public Image getImage() {
        return toDraw;
    }
    
    /**
     * Metoda ktora vrati x-ovu poziciu castice vzhladom od hracovej entity 
     * @return Relativna x-pozicia vzhladom k hracovej entite
     */
    public int getX() {
        return x;
    }
    
    /**
     * Metoda ktora vrati y-ovu poziciu castice vzhladom od hracovej entity 
     * @return Relativna y-pozicia vzhladom k hracovej entite
     */
    public int getY() {
        return y;
    }
    
    /**
     * Metoda ktora vrati zivot castice
     * @return Zivot castice
     */
    public int getSpan() {
        return span;
    }
    // </editor-fold>
}
