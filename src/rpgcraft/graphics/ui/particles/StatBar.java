/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics.ui.particles;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import rpgcraft.graphics.Colors;
import rpgcraft.resource.ImageResource;

/**
 *
 * @author doma
 */
public class StatBar extends BarParticle {
    
    ImageResource res;
    Image back,top;
    
    public StatBar() {
        res = ImageResource.getResource("StatBar");
        this.x = res.getX();
        this.y = res.getY();
        this.width = res.getWidth();
        this.height = res.getHeight();
        setGraphics();
    }
    
    private void setGraphics() {
        Graphics g = toDraw.getGraphics();
        if ((back = res.getBackImage()) != null) {
            g.drawImage(res.getBackImage(), 0, 0, null);
        } else {
            back = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
            g.setColor(Colors.getColor(Colors.fullBlack));
            g.fillRect(0, 0, width, height);
        }
        
        
    }
    
    @Override
    public void setValue(double value) {
        if (value != this.value) {
            this.value = value;
            activation = true;
        }
    }
    
    public void changeBar() {
        
    }
    
    public boolean update(double value) {
        setValue(value);
        if (activation) {
            changeBar();
        }
        
        return true;
    }
    
    
    
}
