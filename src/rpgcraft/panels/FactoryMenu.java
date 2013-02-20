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
public final class FactoryMenu extends AbstractMenu {

    public FactoryMenu(UiResource res) {
        this.res = res;
        initialize(Container.mainContainer,InputHandle.getInstance());        
    }   
    
    @Override
    public void initialize(Container gameContainer, InputHandle input) {
        super.initialize(gameContainer, input);
        menuMap.put(res.getId(), this);
    }
    
    @Override
    public void inputHandling() {
        
    }

    @Override
    public void setWidthHeight(int w, int h) {
        
    }
    
}
