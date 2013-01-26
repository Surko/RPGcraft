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
import rpgcraft.graphics.inmenu.Menu;
import rpgcraft.panels.AbstractMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Container;
import rpgcraft.resource.ImageResource;
import rpgcraft.resource.types.PanelType;
import rpgcraft.utils.ImageUtils;
import rpgcraft.utils.MathUtils;

/**
 *
 * @author Surko
 */
public class SwingImagePanel extends SwingComponent {
    
    protected Image backImage;
    protected Color backColor;
    protected int[] rpos;
    protected PanelType pType;
    
    protected SwingImagePanel() {
        
    }
    
    public SwingImagePanel(Container container, AbstractMenu menu) {
        super(container, menu);
        if (container != null) {
            System.out.println(this.getClass());
            this.backImage = container.getResource().getBackgroundTextureId() != null ?
                    ImageUtils.operateImage(ImageResource.getResource(container.getResource().getBackgroundTextureId()).getBackImage(),
                        container.getResource().getImageOrientation(), null) :
                    null;        
            this.backColor = container.getResource().getBackgroundColorId() != null ? 
                    Colors.getColor(container.getResource().getBackgroundColorId()) :
                    Color.BLACK;    
        }
    }
    
    @Override
    protected void reconstructComponent() {
        this.changed = true;
        if (componentContainer != null) {
            this.backImage = componentContainer.getResource().getBackgroundTextureId() != null ?
                    ImageUtils.operateImage(ImageResource.getResource(componentContainer.getResource().getBackgroundTextureId()).getBackImage(),
                        componentContainer.getResource().getImageOrientation(), null) :
                    null;        
            this.backColor = componentContainer.getResource().getBackgroundColorId() != null ? 
                    Colors.getColor(componentContainer.getResource().getBackgroundColorId()) :
                    Color.BLACK;    
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {       
        // Kontrola Threadu !!
        //System.out.println(Thread.currentThread().getName());
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

    @Override
    public Component copy(Container cont, AbstractMenu menu) {
        SwingImagePanel result = new SwingImagePanel();          
        result.componentContainer = cont;
        result.menu = menu;
        
        result.reconstructComponent();
        
        return result;
    }

    
}
