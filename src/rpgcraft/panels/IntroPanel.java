/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import rpgcraft.handlers.InputHandle;
import rpgcraft.panels.components.Container;
import rpgcraft.resource.UiResource;

/**
 *
 * @author Kirrie
 */
public class IntroPanel extends AbstractMenu {
    
    private int w;
    private int h; 
            
    public IntroPanel(UiResource res) { 
        this.res = res;
    }        
    
    public IntroPanel(BufferedImage image) {
        this.contImage = image;
        this.w = image.getWidth();
        this.h = image.getHeight();
    }

    @Override
    public void initialize(Container gameContainer, InputHandle input) {
        super.initialize(gameContainer, input);
        menuMap.put("introMenu", this);
    }
                
    
    
    public Image getIntroImage() {
        return contImage;
    }
    
    public int getWidth() {
        return w;
    }
    
    public int getHeight() {
        return h;
    }

    @Override
    public void inputHandling() {
        if ((input.enter.on)||(input.escape.on)) {            
            setMenu(menuMap.get("mainMenu"));            
        }

        if ((input.x.on==true)&&(input.q.on==true)) {
            System.exit(0);
        }
        
        if ((input.defense.on == true)) {
            
        }
        
    }
            
    @Override
    public void paintMenu(Graphics g) {          
        super.paintMenu(g);
    }
    
    @Override
    public void update() {
        super.update();                
    }    

    @Override
    public void setWidthHeight(int w, int h) {
        this.w = w;
        this.h = h;
    }

    
}
