/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components.swing;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;
import rpgcraft.graphics.inmenu.Menu;
import rpgcraft.panels.AbstractMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Container;
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
    
    @Override
    public void update() {};
    
    protected void paintImage(Graphics g, Image dbImage) {}
        
    
    /**
     * Metoda clone ktora skopiruje obsah Componenty a vrati novu instanciu tohoto objektu.
     * @return Nova instancia objektu SwingComponent
     */
    @Override
    public abstract Component copy(Container cont, AbstractMenu menu);    
    protected abstract void reconstructComponent();
    
}
