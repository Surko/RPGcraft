/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components.template;

import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JPanel;
import rpgcraft.handlers.InputHandle;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Container;
import rpgcraft.panels.listeners.Action;
import rpgcraft.panels.listeners.ActionEvent;
import rpgcraft.panels.listeners.Listener;
import rpgcraft.panels.listeners.ListenerFactory;
import rpgcraft.resource.UiResource;

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
    protected boolean isNoData;
    protected boolean active;
    
    protected TemplateComponent() {}
    
    public TemplateComponent(UiResource resource, Container cont, AbstractMenu menu) {
        this.menu = menu;
        changed = true;
        this.cont = cont;
        this.resource = resource;
        cont.makeBufferedImage();
    } 
    
    @Deprecated
    public void isMouseSatisfied(ActionEvent event) {
        for (int i = 0;i<_listeners.size() ;i++ ){  
            if (_listeners.get(i) instanceof Listener) {
                Listener listener = (Listener)_listeners.get(i);                
                listener.actionPerformed(event);  
                continue;
            }
            if (_listeners.get(i) instanceof Action) {
                Action action = (Action)_listeners.get(i);
                if (event.getClicks() > action.getClicks()) {
                    ListenerFactory.getListener(action).actionPerformed(event);
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
    public void addActionListener(Action action) {
        _listeners.add(action);
    }
    
    @Override
    public void addActionListeners(ArrayList<Action> actions) {
        for (Action action : actions) {
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
    public boolean isNoData() {
        return isNoData;
    }
        
    @Override
    public void refresh() {
    }
    
    @Override
    public void refreshPositions(int w,int h, int pw, int ph) {
        
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
    public boolean select() {
        if (!isNoData) {
            isSelected = true;
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public boolean unselect() {
        return isSelected = false;
    }
    
    /**
     * Metoda aktivuje komponentu a je schopna reagovat na eventy.
     */
    @Override
    public void activate() {
        this.active = true;
    }
    
    /**
     * Metoda deaktivuje komponentu pre vsetky eventy.
     */
    @Override
    public void deactivate() {
        this.active = false;
    }
    
    @Override
    public void processKeyEvents(InputHandle input) {
        if (active) {
            
        }
    }

    @Override
    public void fireMouseEvent(Action action, ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void fireKeyEvent(Action action, ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }        
    
    @Override
    public Component getParentComponent() {
        return cont.getParentContainer().getComponent();
    }
    
    @Override
    public void addComponent(Component c) {
        if (c instanceof java.awt.Component) {
            this.add((java.awt.Component)c);
        }
    }
    
    @Override
    public void addComponent(Component c, Object constraints) {
        if (c instanceof java.awt.Component) {
            this.add((java.awt.Component)c,constraints);
        }
    }
    
    @Override
    public Container getContainer() {
        return cont;
    }
    
    @Override
    public void removeComponent(Component c) {
        if (c instanceof java.awt.Component) {
            this.remove((java.awt.Component)c);
        }
    }
    
}
