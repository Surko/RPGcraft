/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components;

import java.awt.AWTEvent;
import java.awt.Graphics;
import rpgcraft.panels.listeners.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import rpgcraft.panels.AbstractMenu;
import rpgcraft.resource.UiResource.Action;

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
    
    public void addActionListeners(ArrayList<Action> actions);
    
    public void addActionListener(Action action);            
    
    public void removeActionListener(ActionListener listener);
    
    public AbstractMenu getOriginMenu();    
 
    public void setVisible(boolean aFlag);
    
    public void update();
    
    public Component copy(Container cont, AbstractMenu menu);
    
    public boolean isShowing();
    
    public boolean isSelected();
    
    public void select();
    
    public void unselect();
    
    public void dispatchEvent(AWTEvent event);
    
    public void setBounds(int x, int y, int width, int height);
    
    public void updateUI();
    
    public java.awt.Component getParent();
}
