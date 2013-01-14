/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components.ingame;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import rpgcraft.panels.AboutPanel;
import rpgcraft.panels.AbstractMenu;
import rpgcraft.panels.components.Container;
import rpgcraft.resource.UiResource;

/**
 *
 * @author Surko
 */
public class InGameButton extends InGameComponent {
    ArrayList<InGameButton> buttons;
    ArrayList<ActionListener> _listeners;
    boolean hit = false;
    String title;
    Container cont;
    UiResource res;
    
    
    public InGameButton(UiResource button, Container cont, AbstractMenu menu) {
        super(button, cont, menu);
    }
    
    @Override
    public void fireEvent(ActionEvent event) {  
        for (int i = 0;i<_listeners.size() ;i++ ){  
            ActionListener listener = (ActionListener)_listeners.get(i);  
            listener.actionPerformed(event);  
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        int x = cont.getX();
        int y = cont.getY();
        int w = cont.getWidth();
        int h = cont.getHeight();
        if ((e.getX() > x)&&(e.getX() < x + w)&&
                (e.getY() > y)&&(e.getY() < y + h)) {          
        fireEvent(new ActionEvent(this, 0, null));
        }
    }
    
    @Override
    public void paint(Graphics g) {
        g.drawImage(null, 0, 0, null);    
    }
    
    public void setText(String text) {
        this.title = text;
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        hit = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        hit = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
    
    @Override
    public void addActionListener(ActionListener listener){  
        _listeners.add(listener);  
    }  
   
    @Override
    public void removeActionListener(ActionListener listener){  
        _listeners.remove(listener);  
    }         

    @Override
    public void update() {       
    }

    
}
