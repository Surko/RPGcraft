/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components.swing;

import java.awt.AWTEvent;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JPanel;
import rpgcraft.graphics.inmenu.Menu;
import rpgcraft.panels.AbstractMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Container;
import rpgcraft.panels.listeners.ActionEvent;
import rpgcraft.panels.listeners.Listener;
import rpgcraft.panels.listeners.ListenerFactory;
import rpgcraft.resource.UiResource.Action;
import rpgcraft.resource.types.AbstractType;

/**
 *
 * @author Surko
 */
public abstract class SwingComponent extends JPanel implements Component {    
    protected Container componentContainer;
    protected AbstractMenu menu;
    protected boolean changed;
    protected AbstractType type;   
    protected boolean isSelected;
    protected ArrayList _listeners;
    
    protected SwingComponent() {        
    }
    
    public SwingComponent(Container container,AbstractMenu menu) {
        this.componentContainer = container;
        this.menu = menu;
        if (componentContainer != null) {
            this.type = componentContainer.getResource().getType();
        }
        changed = true;
    }
            
    public void setChanged(boolean changed) {
        this.changed = changed;
    }
    
    @Override
    public AbstractMenu getOriginMenu() {
        return menu;        
    }
    
    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);        
    }
    
    
    
    public void addOwnMouseListener() {
        addMouseListener(this);    
    }

    /**
     * Metoda ktora skuma ci su splnene podmienky pre zavolanie akcie z listeneru.
     * V tejto komponente je len zakladna implementacia s testovanim poctu klikov, cim sa stava
     * atribut clicks v xml povinny. Pravdaze ked pridavam komponente normalny ActionListener
     * tak tato metoda nie je ani volana. Po splneni podmienok je vybraty Listener
     * a zavolana akcia.
     * @param action Akcia ktora musi byt splnena.
     * @param event Udalost podla ktorej sa rozhodne ci bude akcia splnena.
     */
    protected void isActionSatisfied(Action action, ActionEvent event) {
        if (event.getClicks() >= action.getClicks()) {
            Listener list = ListenerFactory.getListener(action.getType());
            list.actionPerformed(event);                               
        }
    }
    
    @Override
    public void fireEvent(ActionEvent event) {
        for (int i = 0;i<_listeners.size() ;i++ ){  
            if (_listeners.get(i) instanceof ActionListener) {
                ActionListener listener = (ActionListener)_listeners.get(i);                
                listener.actionPerformed(event);  
                continue;
            }
            if (_listeners.get(i) instanceof Action) {
                Action action = (Action)_listeners.get(i);
                
                isActionSatisfied(action, event);
                
                if (action.isTransparent()) {
                    ((Component)this.getParent()).fireEvent(event);
                }
                continue;
            }            
        }
    }

    @Override
    public void addActionListener(ActionListener listener) {
        if (listener != null) {
            addOwnMouseListener();
            if (_listeners == null) {
                _listeners = new ArrayList();
            }
            _listeners.add(listener);
        }
    }

    @Override
    public void addActionListener(Action action) {
        if (action != null) {
            addOwnMouseListener();
            if (_listeners == null) {
                _listeners = new ArrayList();
            }
            _listeners.add(action);
        }
    }
    
    @Override
    public void addActionListeners(ArrayList<Action> actions) {
        if (actions != null) {
            addOwnMouseListener();
            if (_listeners == null) {
                _listeners = new ArrayList();
            }
            for (Action action : actions) {
                _listeners.add(action);
            }
        }
    }

    @Override
    public void removeActionListener(ActionListener listener) {
        _listeners.remove(listener);
        if (_listeners.size() == 0) {
            removeMouseListener(this);
        }
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
    
    @Override
    public void update() {};
    
    protected void paintImage(Graphics g, Image dbImage) {}
        
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
    }

    @Override
    public void updateUI() {
        super.updateUI(); 
    }
    
    
    
    /**
     * Metoda clone ktora skopiruje obsah Componenty a vrati novu instanciu tohoto objektu.
     * @return Nova instancia objektu SwingComponent
     */
    @Override
    public abstract Component copy(Container cont, AbstractMenu menu);    
    protected abstract void reconstructComponent();    
            
}
