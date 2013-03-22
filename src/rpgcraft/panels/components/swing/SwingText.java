/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components.swing;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.graphics.Colors;
import rpgcraft.panels.AbstractMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Container;
import rpgcraft.panels.listeners.ActionEvent;
import rpgcraft.resource.ImageResource;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.types.TextType;
import rpgcraft.utils.MathUtils;
import rpgcraft.utils.TextUtils;
import sun.font.FontDesignMetrics;

/**
 *
 * @author kirrie
 */
public class SwingText extends SwingComponent{

    private static final Logger LOG = Logger.getLogger(SwingText.class.getName());
    
    protected String title;
    protected int tw = 0,th = 0;
    protected Color textColor;
    protected Color backColor;
    protected TextType txType;    
        
    public SwingText() {}
    
    public SwingText(Container container,AbstractMenu menu) {
        super(container, menu);        
        if (container != null) {    
            txType = (TextType)container.getResource().getType();
            this.title = TextUtils.getResourceText(txType.getText());     
            this.textColor = Colors.getColor(txType.getTextColor());
            setFont(txType.getFont()); 
            this.backColor = Colors.getColor(container.getResource().getBackgroundColorId());
        }                     
        setBackground(backColor);        
        setTextSize();        
    }
    
    @Override
    protected void reconstructComponent() {
        if (componentContainer != null) { 
            txType = (TextType)componentContainer.getResource().getType();
            this.title = TextUtils.getResourceText(txType.getText());  
            this.textColor = Colors.getColor(txType.getTextColor());
            setFont(txType.getFont());            
            this.backColor = Colors.getColor(componentContainer.getResource().getBackgroundColorId());
        }            
        setBackground(backColor);
        setTextSize();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);                  
        if (title != null) {            
            g.setFont(getFont());
            g.setColor(textColor);
            g.drawString(title, 0, th);
        }        
        if (isSelected) {
            g.setColor(Colors.getColor(Colors.selectedColor));
            g.fillRect(0, 0, getWidth(), getHeight());
        }       
    }   
    
        
    
    public void setColor(Color color) {
        this.textColor = color;
    }
    
    public void setTextSize() {
        int[] txtSize = TextUtils.getTextSize(getFont(), title);  
        tw = txtSize[0];
        th = txtSize[1];
    }
    
    @Override
    public void setBackground(Color color) {
        if (componentContainer != null) {
            super.setBackground(backColor);
        } else {
            super.setBackground(color);
        }
    }
    
    public void setText(String text) {
        this.title= text;
        this.isNoData = false;
        if (componentContainer != null) {
            refresh();
        } else {
            setTextSize();       
        }
    }
    
    public String getText() {
        return title;
    }
    
    public int getTextW() {
        return tw;
    }
    
    public int getTextH() {
        return th;
    }    

    @Override
    public Component copy(Container cont, AbstractMenu menu) {        
        SwingText result = new SwingText();
        result.isNoData = true;
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
        if (_mlisteners != null) {
            isMouseSatisfied(new ActionEvent(this, 0, e.getClickCount(),null, null));
        }
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
    public void refresh() {
        super.refresh();        
        int _w = 0, _h = 0;
                        
        int[] txtSize = TextUtils.getTextSize(getFont(), title); 
        
        tw = txtSize[0];
        th = txtSize[1];
        
        _w = componentContainer.isAutoWidth() ? tw : componentContainer.getWidth();
        _h = componentContainer.isAutoHeight() ? th : componentContainer.getHeight();
        
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
}
