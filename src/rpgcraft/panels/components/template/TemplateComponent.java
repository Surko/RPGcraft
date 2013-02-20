/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components.template;

import java.awt.AWTEvent;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JPanel;
import rpgcraft.panels.AbstractMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Container;
import rpgcraft.panels.listeners.ActionEvent;
import rpgcraft.panels.listeners.Listener;
import rpgcraft.panels.listeners.ListenerFactory;
import rpgcraft.resource.UiResource;
import rpgcraft.resource.types.AbstractType;

/**
 *
 * @author Surko
 */
public abstract class TemplateComponent extends JPanel implements Component {
    
    protected AbstractMenu menu;
    protected UiResource resource;
    protected Container cont;
    protected boolean visible;
    protected boolean changed;
    protected boolean isSelected;
    protected ArrayList _listeners;
    
    protected TemplateComponent() {}
    
    public TemplateComponent(UiResource resource, Container cont, AbstractMenu menu) {
        this.menu = menu;
        changed = true;
        this.cont = cont;
        this.resource = resource;
        cont.makeBufferedImage();
    } 
    
    @Override
    public void fireEvent(ActionEvent event) {
        for (int i = 0;i<_listeners.size() ;i++ ){  
            if (_listeners.get(i) instanceof Listener) {
                Listener listener = (Listener)_listeners.get(i);                
                listener.actionPerformed(event);  
                continue;
            }
            if (_listeners.get(i) instanceof UiResource.Action) {
                UiResource.Action action = (UiResource.Action)_listeners.get(i);
                if (event.getClicks() > action.getClicks()) {
                    ListenerFactory.getListener(action.getType()).actionPerformed(event);
                }
                continue;
            }
        }
    }

    @Override
    public void addActionListener(ActionListener listener) {
        _listeners.add(listener);
    }

    @Override
    public void addActionListener(UiResource.Action action) {
        _listeners.add(action);
    }
    
    @Override
    public void addActionListeners(ArrayList<UiResource.Action> actions) {
        for (UiResource.Action action : actions) {
            _listeners.add(action);
        }
    }
    
    @Override
    public void removeActionListener(ActionListener listener) {
        _listeners.remove(listener);
    }    
    
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
    public AbstractMenu getOriginMenu() {
        return null;
    }
        
    @Override
    public abstract Component copy(Container cont, AbstractMenu menu);
    
    protected abstract void reconstructComponent();
    
    @Override
    public void setVisible(boolean aFlag) {
        this.visible = aFlag;
    }
    
    @Override
    public boolean isSelected() {
        return isSelected;
    }
    
    @Override
    public void select() {
        isSelected = true;
    }
    
    @Override
    public void unselect() {
        isSelected = false;
    }
    
}
