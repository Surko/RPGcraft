/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import rpgcraft.panels.AbstractMenu;

/**
 *
 * @author Surko
 */
public interface Component extends MouseListener, Cloneable {
    
    public void fireEvent(ActionEvent event);
    
    @Override
    public void mouseClicked(MouseEvent e);
    
    public void paint(Graphics g);
    
    @Override
    public void mousePressed(MouseEvent e);
    
    @Override
    public void mouseReleased(MouseEvent e);

    @Override
    public void mouseEntered(MouseEvent e);

    @Override
    public void mouseExited(MouseEvent e);
    
    public void addActionListener(ActionListener listener);
    
    public void removeActionListener(ActionListener listener);
    
    public AbstractMenu getOriginMenu();    
 
    public void setVisible(boolean aFlag);
    
    public void update();
    
    public Component copy(Container cont, AbstractMenu menu);
    
    public boolean isShowing();
}
