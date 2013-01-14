/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components.swing;

import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JPanel;
import rpgcraft.graphics.inmenu.Menu;
import rpgcraft.panels.AbstractMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Container;
import rpgcraft.resource.AbstractResource;
import rpgcraft.resource.UiResource;

/**
 *
 * @author Surko
 */
public abstract class SwingComponent extends JPanel implements Component {    
    protected Container componentContainer;
    protected AbstractMenu menu;
    protected boolean changed;
    
    public SwingComponent() {
        super();
    }
    
    public SwingComponent(Container container,AbstractMenu menu) {
        this.componentContainer = container;
        this.menu = menu;
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
    
    @Override
    public SwingComponent copy() {    
        return null;
    }
                                
    public void addOwnMouseListener() {
        addMouseListener(this);
    }
    
    @Override
    public void update() {};
    
    
}
