/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.graphics.Colors;
import rpgcraft.graphics.inmenu.Menu;
import rpgcraft.panels.AbstractMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Container;
import rpgcraft.panels.listeners.ActionEvent;
import rpgcraft.resource.ImageResource;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.UiResource.Action;
import rpgcraft.resource.types.PanelType;
import rpgcraft.utils.ImageUtils;
import rpgcraft.utils.MathUtils;

/**
 *
 * @author Surko
 */
public class SwingImagePanel extends SwingComponent {
    private static final Logger LOG = Logger.getLogger(SwingImagePanel.class.getName());
    
    protected Image backImage;
    protected Color backColor;
    protected int[] rpos;
    protected PanelType pType;
    
    protected SwingImagePanel() {
        
    }
    
    public SwingImagePanel(Container container, AbstractMenu menu) {
        super(container, menu);
        if (container != null) {
            this.backImage = container.getResource().getBackgroundTextureId() != null ?
                    ImageUtils.operateImage(componentContainer, componentContainer.getResource()) :
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
                    ImageUtils.operateImage(componentContainer, componentContainer.getResource()) :
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
        
        if (isSelected) {
            g.setColor(Colors.getColor(Colors.selectedColor));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        
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
    public Component copy(Container cont, AbstractMenu menu) {
        SwingImagePanel result = new SwingImagePanel();          
        result.componentContainer = cont;
        result.menu = menu;
        if (!_listeners.isEmpty()) {
            result.addOwnMouseListener();
        }
        result._listeners = _listeners;
        result.reconstructComponent();
        
        return result;
    }

    
    @Override
    public void refresh() { 
        super.refresh();                
        
        int w = 0, h = 0;
                        
        if (componentContainer.getChildContainer() != null) {
            for (Container cont : componentContainer.getChildContainer()) {
                if (componentContainer.getWidth() == -1) {
                    w += cont.getWidth();
                }
                if (componentContainer.getHeight() == -1) {
                    h += cont.getHeight();
                }
            }                
        }
        
        w = componentContainer.getWidth() == -1 ? (w > backImage.getWidth(null) ? w : 
                backImage.getWidth(null)) : componentContainer.getWidth();
        h = componentContainer.getHeight() == -1 ? ((h > backImage.getHeight(null) ? h :
                backImage.getHeight(null))) : componentContainer.getHeight();
                
        setSize(w, h);        
        
        if (componentContainer.getParentWidth() == -1 || componentContainer.getParentHeight() == -1) {  
            LOG.log(Level.INFO, StringResource.getResource("_rshabort"));
            componentContainer.getParentContainer().addPositionslessCont(componentContainer);
            return;
        }
        
        // startovacia pozicia pre vykreslenie resource do rodicovskeho kontajneru          
        
        refreshPositions(w, h, componentContainer.getParentWidth(), componentContainer.getParentHeight()); 
        
        if (componentContainer.getPositionslessCont() != null) {
            for (Container cont : componentContainer.getPositionslessCont()) {
                LOG.log(Level.INFO, StringResource.getResource("_rshcontinue", new String[] {cont.getResource().getId()}));
                cont.getComponent().refreshPositions(cont.getWidth(), cont.getHeight(), w, h);
            }
            componentContainer.clearPositionsless();
        }
    }        
    
}
