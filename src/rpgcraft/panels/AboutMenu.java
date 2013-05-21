/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels;

import rpgcraft.plugins.AbstractMenu;
import java.awt.*;
import rpgcraft.handlers.InputHandle;
import rpgcraft.resource.UiResource;

/**
 * Trieda AboutPanel vytvori okno s informaciami o hre
 * a autorovi
 * @author Kirrie
 */

public class AboutMenu extends AbstractMenu {
    int x;
    int y;
    
    /**
     * 
     * @param iFile 
     */
    public AboutMenu(UiResource res) {
        this.res = res;  
        menuMap.put(res.getId(), this);
    }  

    @Override
    public void initialize(rpgcraft.panels.components.Container gameContainer, InputHandle input) {
        super.initialize(gameContainer, input);
        
    }
    
    
    
    /**
     * 
     */
    @Override
    public void inputHandling() {
        super.inputHandling();
        if (input.clickedKeys.contains(input.escape.getKeyCode())) {
            setMenu(menuMap.get("mainMenu"));
        }                
    }

    @Override
    public void setWidthHeight(int w, int h) {
        this.x = w;
        this.y = h;
    }   

    @Override
    public void paintMenu(Graphics g) {        
    }   
}
