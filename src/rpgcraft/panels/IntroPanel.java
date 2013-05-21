/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels;

import rpgcraft.plugins.AbstractMenu;
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
        menuMap.put(res.getId(), this);
    }        
    
    public IntroPanel(BufferedImage image) {
        this.contImage = image;
        this.w = image.getWidth();
        this.h = image.getHeight();
    }

    @Override
    public void initialize(Container gameContainer, InputHandle input) {
        super.initialize(gameContainer, input);        
    }
                
    public String getName() {
        return res.getId();
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
        super.inputHandling();
        if (input.runningKeys.contains(input.enter.getKeyCode())||
                input.runningKeys.contains(input.escape.getKeyCode())) {            
            setMenu(menuMap.get("mainMenu"));            
        }

        if (input.runningKeys.contains(input.x.getKeyCode())&&
                input.runningKeys.contains(input.q.getKeyCode())) {
            System.exit(0);
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
