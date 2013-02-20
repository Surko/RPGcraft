/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels;

import java.awt.*;
import javax.swing.JPanel;
import rpgcraft.graphics.Colors;
import rpgcraft.handlers.InputHandle;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.ImageResource;
import rpgcraft.resource.UiResource;

/**
 * Trieda AboutPanel vytvori okno s informaciami o hre
 * a autorovi
 * @author Kirrie
 */

public class AboutMenu extends AbstractMenu {
    private Image img;
    int x;
    int y;
    
    /**
     * 
     * @param iFile 
     */
    public AboutMenu(UiResource res) {
        this.res = res;
        img = loadResourceImage(ImageResource.getResource("about"),Colors.getColor(Colors.menuError1),
               StringResource.getResource("_mimage") + getClass().getName());     
    }  

    @Override
    public void initialize(rpgcraft.panels.components.Container gameContainer, InputHandle input) {
        super.initialize(gameContainer, input);
        menuMap.put("about", this);
    }
    
    
    
    /**
     * 
     */
    @Override
    public void inputHandling() {
        if (input.escape.on) {
            setMenu(menuMap.get("mainMenu"));
        }
    }

    @Override
    public void setWidthHeight(int w, int h) {
        this.x = w;
        this.y = y;
    }   
}
