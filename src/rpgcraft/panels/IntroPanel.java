/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import rpgcraft.graphics.Colors;
import rpgcraft.handlers.InputHandle;
import rpgcraft.panels.components.Container;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.ImageResource;
import rpgcraft.resource.UiResource;

/**
 *
 * @author Kirrie
 */
public class IntroPanel extends AbstractMenu {
    
    private int x;
    private int y; 
            
    public IntroPanel(UiResource res) { 
        this.res = res;
    }        
    
    public IntroPanel(BufferedImage image) {
        this.contImage = image;
        this.x = image.getWidth();
        this.y = image.getHeight();
    }

    @Override
    public void initialize(Container gameContainer, InputHandle input) {
        super.initialize(gameContainer, input);
        menuMap.put("intro", this);
    }
                
    
    
    public Image getIntroImage() {
        return contImage;
    }
    
    public int getWidth() {
        return x;
    }
    
    public int getHeight() {
        return y;
    }

    @Override
    public void inputHandling() {
        if ((input.enter.on)||(input.escape.on)) {            
            setMenu(menuMap.get("mainmenu"));            
        }

        if ((input.x.on==true)&&(input.q.on==true)) {
            System.exit(0);
        }
        
        if ((input.defense.on == true)) {
            try {
            ImageIO.write(contImage, "png", new File("null.png"));            
            } catch (Exception e) {}
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
    }

    
}
