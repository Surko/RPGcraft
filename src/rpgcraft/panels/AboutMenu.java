/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import rpgcraft.GamePane;
import rpgcraft.graphics.Colors;
import rpgcraft.graphics.ImageOperation;
import rpgcraft.handlers.InputHandle;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.ImageResource;
import rpgcraft.resource.UiResource;
import rpgcraft.utils.ImageUtils;

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
        img = loadResourceImage(ImageResource.getResource("about"), 
                StringResource.getResource("_mimage") + getClass().getName());     
    }  

    @Override
    public void initialize(rpgcraft.panels.components.Container gameContainer, InputHandle input) {
        super.initialize(gameContainer, input);
        menuMap.put("aboutMenu", this);
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
        this.y = y;
    }   

    @Override
    public void paintMenu(Graphics g) {        
    }
}
