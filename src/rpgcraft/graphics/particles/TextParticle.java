/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics.particles;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import rpgcraft.graphics.Colors;

/**
 *
 * @author doma
 */
public class TextParticle extends Particle{
    private String msg;
    private Font font = new Font("Tahoma", Font.PLAIN, 15);
    
    public TextParticle(String msg, int x, int y) {
        this.x = x;
        this.y = y;
        this.msg = msg;
        this.height = 24;
        this.width = 24;
        this.activation = true;
        this.toDraw = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);        
        setGraphics();
    }
    
    
    private void setGraphics() {
        Graphics g = toDraw.getGraphics();
        g.setFont(font);
        g.setColor(Color.RED);
        g.drawString(msg, 3, 16);
    }
    
        
    /**
     * Metoda ktora simuluje zivot castice (premenna span) na obrazovke.  
     * Po prebehnuti zivota vrati ci sa podaril update.
     * @return True/False podla toho ci sa podaril update.
     */
    @Override
    public boolean update() {
        if (!activation) return false;
        span++;
        if (span > 100) {
            return false;
        }
        
        return true;
    }
    
}
