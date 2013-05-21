/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics.ui.menu;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

/**
 *
 * @author Surko
 */
public interface Menu<T extends Menu> {    
    
    public void inputHandling();
    public void mouseHandling(MouseEvent e);
    public void paintMenu(Graphics g);
    public void update();
    public void setMenu(T menu);
    public int getWidth();
    public int getHeight();
}
