/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels;

import java.awt.Graphics;
import javax.swing.JPanel;
import rpgcraft.handlers.InputHandle;
import rpgcraft.panels.components.Container;
import rpgcraft.resource.UiResource;

/**
 *
 * @author Surko
 */
public class BlankMenu extends AbstractMenu {
    
    public BlankMenu() {
        this.changedGr = false;
        this.changedUi = false;
    }

    @Override
    public void initialize(Container gameContainer, InputHandle input) {
        menuMap.put("blank", this);
    }           
    
    @Override
    public void paintMenu(Graphics g) {}
    
    @Override
    public void update() {}
    
    @Override
    public void inputHandling() {}

    @Override
    public void setWidthHeight(int w, int h) {
        
    }

}
