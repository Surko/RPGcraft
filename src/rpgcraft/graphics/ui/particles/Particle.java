/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics.ui.particles;

import java.awt.Image;
import java.util.Random;

/**
 *
 * @author doma
 */
public class Particle {    
    
    protected int x, y;
    protected Image toDraw;
    
    protected int span;
    
    protected int width, height;
    
    protected boolean removed;
    
    protected boolean activation;
            
    /**
     * Metoda ktora simuluje zivot castice (premenna span) na obrazovke.
     * Definovane v podedenych triedach.
     * @return True/False podla toho ci prebehol cely zivotny cyklus.
     */
    public boolean update() {
        return false;
    }
    
    public boolean isRemoved() {
        return removed;
    }
    
    public void setRemoved() {
        removed = true;
    }
    
    public void setActivated() {
        this.activation = true;
    }
    
    public void setDeactivated() {
        this.activation = false;        
    }
    
    public void resetImage() {
        
    }
    
    public boolean isActivated() {
        return activation;
    }
    
    public Image getImage() {
        return toDraw;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getSpan() {
        return span;
    }
}
