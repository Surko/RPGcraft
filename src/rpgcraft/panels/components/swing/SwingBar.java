/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.graphics.Colors;
import rpgcraft.panels.AbstractMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Container;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.types.BarType;
import rpgcraft.utils.DataUtils;

/**
 *
 * @author kirrie
 */
public class SwingBar extends SwingComponent {

    private static final Logger LOG = Logger.getLogger(SwingBar.class.getName()); 
    
    private Dimension prefferedDim = new Dimension(100,20);
    
    BarType barType;
    Color backColor;
    
    public SwingBar() {}
    
    public SwingBar(Container container, AbstractMenu menu) {
        super(container, menu);
        
        this.barType = (BarType)container.getResource().getType();                     
        
        this.backColor = Colors.getColor(container.getResource().getBackgroundColorId());
    }

    @Override
    protected void reconstructComponent() {        
        
        this.barType = (BarType)componentContainer.getResource().getType();                     
        
        this.backColor = Colors.getColor(componentContainer.getResource().getBackgroundColorId());
    }

    @Override
    protected void paintComponent(Graphics g) {
        
        double min = (double) DataUtils.getData(this, barType.getMinData());
        double max = (double) DataUtils.getData(this, barType.getMaxData());
        
        double modif = min / max;
        
        g.setColor(backColor);
        g.fillRect(0, 0, ((int)(getWidth() * modif)), getHeight());
        
    }        

    @Override
    public void refresh() {
        super.refresh();
        int w = 0, h = 0;
        
        Dimension barDim = prefferedDim;
        
        w = componentContainer.isAutoWidth() ? barDim.width : componentContainer.getWidth();
        h = componentContainer.isAutoHeight() ? barDim.height : componentContainer.getHeight();
        
        componentContainer.set(w, h);
        //setSize(w, h);  
             
        if (componentContainer.getParentContainer().isAutoWidth() || componentContainer.getParentContainer().isAutoHeight()) {  
            LOG.log(Level.INFO, StringResource.getResource("_rshabort"));
            componentContainer.getParentContainer().addPositionslessCont(componentContainer);
            return;
        }

        // startovacia pozicia pre vykreslenie resource do rodicovskeho kontajneru          
        refreshPositions(w, h, componentContainer.getParentWidth(), 
                componentContainer.getParentHeight()); 

        
    }
    
    
    @Override
    public Component copy(Container cont, AbstractMenu menu) {
        SwingBar result = new SwingBar();          
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
    
}
