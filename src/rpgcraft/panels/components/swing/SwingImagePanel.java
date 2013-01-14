/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import rpgcraft.graphics.Colors;
import rpgcraft.panels.AbstractMenu;
import rpgcraft.panels.components.Container;
import rpgcraft.resource.ImageResource;
import rpgcraft.utils.ImageUtils;
import rpgcraft.utils.MathUtils;

/**
 *
 * @author Surko
 */
public class SwingImagePanel extends SwingComponent {
    
    Image backImage;
    Color backColor;
    boolean top;
    int[] rpos;
    
    public SwingImagePanel(Container componentContainer, AbstractMenu menu) {
        super(componentContainer, menu);
        this.backImage = componentContainer.getResource().getBackgroundTextureId() != null ?
                ImageUtils.operateImage(ImageResource.getResource(componentContainer.getResource().getBackgroundTextureId()).getBackImage(),
                    componentContainer.getResource().getImageOrientation(), null) :
                null;        
        this.backColor = componentContainer.getResource().getBackgroundColorId() != null ? 
                Colors.getColor(componentContainer.getResource().getBackgroundColorId()) :
                Color.BLACK;        
    }
    
    @Override
    public void paintComponent(Graphics g) {    
        if (!componentContainer.isVisible()) return;
        g.setColor(backColor);        
        g.fillRect(0, 0, getWidth(), getHeight());
        if (backImage != null) {
            if (changed) {
                rpos = MathUtils.getStartPositions(componentContainer.getResource().getPosition(), getWidth(), getHeight(),
                        backImage.getWidth(null), backImage.getHeight(null));
                changed = false;
            }
        
            g.drawImage(backImage, rpos[0], rpos[1], null); 
        }
        /*
        if (this.contains(0,0)) {
            if (Framer.SHOWFPS) {
            Framer.framing++;
            } 
            if (Framer.SHOWFPS) {
                g.setColor(Colors.getColor(Colors.fpsColor));
                if (System.currentTimeMillis() - Framer.fpsTimer > 1000) {
                    Framer.fpsTimer += 1000;                    
                    Framer.fShow = Framer.framing;
                    Framer.framing = 0;                       
                }    
                g.drawString("FPS: "+Framer.fShow,0,10);
            }
        }
        */
    }      


    
    @Override
    public void fireEvent(ActionEvent event) {
        
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        
    }

    @Override
    public void mousePressed(MouseEvent e) {

            
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void addActionListener(ActionListener listener) {

    }

    @Override
    public void removeActionListener(ActionListener listener) {

    }

    
}
