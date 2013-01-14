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

public class AboutPanel extends AbstractMenu {
    private Image img;
    int x;
    int y;
    
    /**
     * 
     * @param iFile 
     */
    public AboutPanel(UiResource res) {
        this.res = res;
        img = loadResourceImage(ImageResource.getResource("about"),Colors.getColor(Colors.menuError1),
               StringResource.getResource("_mimage") + getClass().getName());     
    }  

    @Override
    public void initialize(rpgcraft.panels.components.Container gameContainer, InputHandle input) {
        super.initialize(gameContainer, input);
        menuMap.put("about",this);
    }
    
    
    
    /**
     * 
     */
    @Override
    public void inputHandling() {
        if (input.escape.on) {
            setMenu(menuMap.get("menu"));
        }
    }
    
    /**
     * 
     * @param g 
     */
    @Override
    public void paintMenu(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, super.getGamePanel().getWidth(), super.getGamePanel().getHeight());
        //g.drawImage(img, (gamePane.getWidth()+gamePane.get("".getWidth())/2, 0, null);
        
        if (y>=gamePane.getHeight()+15) {
            y=img.getHeight(null)+10;
        } else {
            y = y + 2; 
            x = (gamePane.getWidth()-100) / 2;
            g.setColor(Color.WHITE);
            g.drawString("RPGcraft! Project", x, y);
            g.drawString("Creator : LUKAS SURIN", x, y+15);
        }
        
    }

    /**
     * 
     */
    @Override
    public void update() {
        super.update();                                        
    }

    @Override
    public void setWidthHeight(int w, int h) {
        this.x = w;
        this.y = y;
    }
}
