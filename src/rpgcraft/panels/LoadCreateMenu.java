/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels;

import rpgcraft.handlers.InputHandle;
import rpgcraft.panels.components.Container;
import rpgcraft.resource.UiResource;

/**
 *
 * @author kirrie
 */
public class LoadCreateMenu extends AbstractMenu {

    public LoadCreateMenu(UiResource res) {
        this.res = res;       
    }   
    
    @Override
    public void initialize(Container gameContainer, InputHandle input) {
         super.initialize(gameContainer, input);
         menuMap.put("loadcreateMenu", this);
    }
    
    
    
    @Override
    public void inputHandling() {
        super.inputHandling();                
        if (input.runningKeys.contains(input.escape.getKeyCode())) {
            setMenu(menuMap.get("mainMenu"));
        }                
    }

    @Override
    public void setWidthHeight(int w, int h) {
        
    }
    
}
