/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.graphics.Colors;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.panels.GameMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Container;
import rpgcraft.resource.StringResource;
import rpgcraft.utils.ImageUtils;
import rpgcraft.utils.MathUtils;
import rpgcraft.utils.TextUtils;

/**
 *
 * @author kirrie
 */
public class SwingImage extends SwingComponent {

    private static final Logger LOG = Logger.getLogger(SwingImage.class.getName());
    
    protected Color topColor, backColor;
    protected Image backImage;   
    
    public SwingImage() { }
    
    public SwingImage(Container container, AbstractMenu menu) {
        if (componentContainer != null) {
            String sbackImage = componentContainer.getResource().getBackgroundTextureId();
            
            this.backImage = sbackImage != null ?
                    ImageUtils.operateImage(componentContainer, componentContainer.getResource()) :
                    null;        
            
            this.topColor = componentContainer.getResource().getTopColorId();
            this.backColor = componentContainer.getResource().getBackgroundColorId() != null ? 
                    componentContainer.getResource().getBackgroundColorId() :
                    Colors.getColor(Colors.transparentColor);    
            
        }
        this.changed = true;
    }        
    
    @Override
    protected void reconstructComponent() {
        if (componentContainer != null) {
            String sbackImage = componentContainer.getResource().getBackgroundTextureId();
            
            this.backImage = sbackImage != null ?
                    ImageUtils.operateImage(componentContainer, componentContainer.getResource()) :
                    null;        
            
            this.topColor = componentContainer.getResource().getTopColorId();
            this.backColor = componentContainer.getResource().getBackgroundColorId() != null ? 
                    componentContainer.getResource().getBackgroundColorId() :
                    Colors.getColor(Colors.transparentColor);    
            
        }
        this.changed = true;
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(backColor);          
        g.fillRect(0, 0, getWidth(), getHeight());        
        
        if (topColor != null) {
            g.setColor(topColor);            
            g.fillRect(5, 5, getWidth(), getHeight());        
        }
        
        if (backImage != null) {                    
            g.drawImage(backImage, 0, 0, null); 
        }
        
        if (isSelected) {
            g.setColor(Colors.getColor(Colors.selectedColor));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }        

    @Override
    public void refresh() {
        super.refresh();        
        int _w = 0, _h = 0;                                
                        
        _w = componentContainer.isAutoWidth() ? backImage.getWidth(null) : componentContainer.getWidth();
        _h = componentContainer.isAutoHeight() ? backImage.getHeight(null) : componentContainer.getHeight();
        
        //setSize(_w, _h);
        componentContainer.set(_w, _h);
         
        // startovacia pozicia pre vykreslenie resource do rodicovskeho kontajneru            
        if (componentContainer.getParentContainer().isAutoWidth() || componentContainer.getParentContainer().isAutoHeight()) {  
            LOG.log(Level.INFO, StringResource.getResource("_rshabort"));
            componentContainer.getParentContainer().addPositionslessCont(componentContainer);
            return;
        }

        // startovacia pozicia pre vykreslenie resource do rodicovskeho kontajneru          
         
        refreshPositions(_w, _h, componentContainer.getParentWidth(), 
            componentContainer.getParentHeight());  
    }
            
    @Override
    public Component copy(Container cont, AbstractMenu menu) {
        SwingImage result = new SwingImage();          
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
    
}
