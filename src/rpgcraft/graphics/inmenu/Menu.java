/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics.inmenu;

import java.awt.Graphics;
import java.util.HashMap;

/**
 *
 * @author Surko
 */
public interface Menu<T extends Menu> {    
    
    public void inputHandling();
    public void paintMenu(Graphics g);
    public void update();
    public void setMenu(T menu);
}
