/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.utils;

import java.awt.Dimension;
import java.awt.Graphics;
import rpgcraft.graphics.Colors;
import rpgcraft.panels.AbstractMenu;
import rpgcraft.panels.components.Container;
import rpgcraft.panels.components.swing.SwingText;

/**
 *
 * @author Surko
 */
public class Framer {
    // JLabel pre frame pocitadlo
    public static final FramePanel frameLabel;        
    public static volatile int tick = 0;
    //FPS junk
    public static int fShow;
    public static final boolean SHOWFPS = true;
    public static final long fpsProhibitor = 10;
    public static volatile int framing; 
    public static volatile long fpsTimer;         
    
   
    public static class FramePanel extends SwingText {
    
        protected FramePanel(Container container,AbstractMenu menu) {
            super(container, menu);
            setBackground(Colors.getColor(Colors.transparentColor));
        }

        @Override
        public void paintComponent(Graphics g) {
            g.setColor(textColor);  
            g.setFont(getFont());
            if (title != null) {
                g.drawString(title, 0, th);
            }
            updateFPS();
        }       
        
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(tw,th);
        }
        
        @Override
        public Dimension getMinimumSize() {
           return new Dimension(tw,th); 
        }
        
        @Override
        public Dimension getSize() {
           return new Dimension(tw,th); 
        }       
        
        private void updateFPS() {
        
            if (Framer.SHOWFPS) {
                Framer.framing++;
            } 
        
            if (Framer.SHOWFPS) {
                if (System.currentTimeMillis() - fpsTimer > 1000) {
                    fpsTimer += 1000;                    
                    fShow = Framer.framing;
                    framing = 0; 
                    setText(fShow+ " FPS");
                }                    
            }                    
        }   
    }
    
    static {      
        frameLabel = new FramePanel(null, null);
        frameLabel.setText("000 FPS");
        frameLabel.setFont(null);
        frameLabel.setColor(Colors.getColor(Colors.fpsColor));        
        frameLabel.setBounds(0, 0, frameLabel.getTextW(), frameLabel.getTextH());    
    }
    
    
}
