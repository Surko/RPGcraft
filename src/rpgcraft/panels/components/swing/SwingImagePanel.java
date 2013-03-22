/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.graphics.Colors;
import rpgcraft.panels.AbstractMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Container;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.types.PanelType;
import rpgcraft.utils.ImageUtils;
import rpgcraft.utils.MathUtils;

/**
 *
 * @author Surko
 */
public class SwingImagePanel extends SwingComponent{
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
    public void setBackground(Color color) {
        if (componentContainer != null) {
            super.setBackground(backColor);
        } else {
            super.setBackground(color);
        }
    }
    
    @Override
    protected void reconstructComponent() {        
        if (componentContainer != null) {
            this.backImage = componentContainer.getResource().getBackgroundTextureId() != null ?
                    ImageUtils.operateImage(componentContainer, componentContainer.getResource()) :
                    null;        
            this.backColor = componentContainer.getResource().getBackgroundColorId() != null ? 
                    Colors.getColor(componentContainer.getResource().getBackgroundColorId()) :
                    Color.BLACK;    
        }
        this.changed = true;
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
        if (_mlisteners != null && !_mlisteners.isEmpty()) {
            result.addOwnMouseListener();
        }
        result._mlisteners = _mlisteners;        
        result._klisteners = _klisteners;
        result.reconstructComponent();
        
        return result;
    }

    
    @Override
    public void refresh() { 
        super.refresh();                
        
        int w = 0, h = 0;
                        
        if (componentContainer.getChildContainer() != null) {
            for (Container cont : componentContainer.getChildContainer()) {
                if (componentContainer.isAutoWidth()) {
                    w += cont.getWidth();
                }
                if (componentContainer.isAutoHeight()) {
                    h += cont.getHeight();
                }
            }                
        }
        
        w = componentContainer.isAutoWidth() ? (w > backImage.getWidth(null) ? w : 
                backImage.getWidth(null)) : componentContainer.getWidth();
        h = componentContainer.isAutoHeight() ? ((h > backImage.getHeight(null) ? h :
                backImage.getHeight(null))) : componentContainer.getHeight();
                
        //System.out.println("Resizing :" + this);
        //setSize(w, h);
        //setPreferredSize(new Dimension(w,h));
        //System.out.println("Size :" + this);
        componentContainer.set(w, h);
        
        if (componentContainer.getParentContainer().isAutoWidth() || componentContainer.getParentContainer().isAutoHeight()) {  
            LOG.log(Level.INFO, StringResource.getResource("_rshabort"));
            componentContainer.getParentContainer().addPositionslessCont(componentContainer);
            return;
        }
        
        // startovacia pozicia pre vykreslenie resource do rodicovskeho kontajneru          
        
        refreshPositions(w, h, componentContainer.getParentWidth(), componentContainer.getParentHeight());
        reconstructComponent();
        
        if (componentContainer.getPositionslessCont() != null) {
            for (Container cont : componentContainer.getPositionslessCont()) {
                LOG.log(Level.INFO, StringResource.getResource("_rshcontinue", new String[] {cont.getResource().getId()}));
                cont.getComponent().refreshPositions(cont.getWidth(), cont.getHeight(), w, h);
            }
            componentContainer.clearPositionsless();
        }        
    }        

    
    
}
