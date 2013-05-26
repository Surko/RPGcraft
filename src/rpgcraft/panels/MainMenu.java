/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels;

import rpgcraft.plugins.AbstractMenu;
import rpgcraft.handlers.InputHandle;
import rpgcraft.panels.components.Container;
import rpgcraft.resource.UiResource;

/**
 *
 * @author Kirrie
 */
public class MainMenu extends AbstractMenu{
    
    public MainMenu(UiResource res) {  
        this.res = res;
        menuMap.put(res.getId(), this);
    }

    @Override
    public void initialize(Container gameContainer, InputHandle input) {
        super.initialize(gameContainer, input);        
    }
    
    @Override
    public void update() {
       super.update();             
    }

    @Override
    public void inputHandling() {
        super.inputHandling();
    }         

    @Override
    public void setWidthHeight(int w, int h) {}   

    
    
} 
