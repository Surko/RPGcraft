/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components.ingame;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import rpgcraft.panels.AbstractMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Container;
import rpgcraft.resource.UiResource;

/**
 *
 * @author Surko
 */
public abstract class InGameComponent implements Component {
    
    protected AbstractMenu menu;
    protected UiResource resource;
    protected Container cont;
    protected boolean visible;
    protected boolean changed;
    
    public InGameComponent(UiResource resource, Container cont, AbstractMenu menu) {
        this.menu = menu;
        changed = true;
        this.cont = cont;
        this.resource = resource;
        cont.makeBufferedImage();
    } 
    
    @Override
    public void fireEvent(ActionEvent event) {}
    
    @Override
    public abstract void mouseClicked(MouseEvent e);
    
    @Override
    public abstract void paint(Graphics g);
    
    @Override
    public abstract void mousePressed(MouseEvent e);
    
    @Override
    public abstract void mouseReleased(MouseEvent e);

    @Override
    public abstract void mouseEntered(MouseEvent e);

    @Override
    public abstract void mouseExited(MouseEvent e);
    
    @Override
    public void addActionListener(ActionListener listener) {}
    
    @Override
    public void removeActionListener(ActionListener listener) {}
        
    @Override
    public InGameComponent copy() {
        return null;
    }
    
    @Override
    public AbstractMenu getOriginMenu() {
        return null;
    }
        
    @Override
    public void setVisible(boolean aFlag) {
        this.visible = aFlag;
    }
    
}
