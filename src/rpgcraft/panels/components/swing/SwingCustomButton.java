/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import rpgcraft.panels.listeners.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.panels.AbstractMenu;
import rpgcraft.panels.components.Container;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.types.ButtonType;
import rpgcraft.utils.MathUtils;
import rpgcraft.utils.TextUtils;

/**
 *
 * @author Kirrie
 */
public abstract class SwingCustomButton extends SwingComponent {
    private static final Logger LOG = Logger.getLogger(SwingCustomButton.class.getName());
    public static final Dimension prefferedDim = new Dimension(300,20);
    
    String title;        
    int w,h;
    boolean hit = false; 
    ButtonType btnType;
   
    protected SwingCustomButton() {
        super();
    } 
    
    public SwingCustomButton (Container container, AbstractMenu menu) {  
        super(container,menu);  
        btnType = (ButtonType)container.getResource().getType();
        this.title = TextUtils.getResourceText(btnType.getText());  
        setFont(btnType.getFont());         
        _listeners = new ArrayList();        
        setTextSize();
        addMouseListener(this); 
    }
    
    @Override
    protected void reconstructComponent() {
        btnType = (ButtonType)componentContainer.getResource().getType();
        this.title = TextUtils.getResourceText(btnType.getText());           
        setFont(btnType.getFont());
        _listeners = new ArrayList();         
        setTextSize();
        addMouseListener(this);
    }
    
    @Override
    public void refresh() {
        super.refresh();
        int _w = 0, _h = 0;
        
        Dimension imgDim = prefferedDim;
        
        _w = componentContainer.getWidth() == -1 ? imgDim.width : componentContainer.getWidth();
        _h = componentContainer.getHeight() == -1 ? imgDim.height : componentContainer.getHeight();
        
        componentContainer.set(_w, _h);
        setSize(_w, _h);  
        
        if (componentContainer.getParentWidth() == -1 || componentContainer.getParentHeight() == -1) {  
            LOG.log(Level.INFO, StringResource.getResource("_rshabort"));
            componentContainer.getParentContainer().addPositionslessCont(componentContainer);
            return;
        }
        
        // startovacia pozicia pre vykreslenie resource do rodicovskeho kontajneru          
        
        refreshPositions(_w, _h, componentContainer.getParentWidth(), 
                componentContainer.getParentHeight()); 
    }
    
    public void setText(String text) {
        this.title = text;
    }
    
    public void setTextSize() {
        h = TextUtils.getTextHeight(getFont());          
    }
    
    @Override
    public abstract void paintComponent(Graphics g);
     
    
  
    @Override
    public void mousePressed(MouseEvent e){  
        hit=true;          
        repaint();  
    }  
   
    @Override
    public void mouseReleased(MouseEvent e){  
        hit=false;  
        repaint();  
    }  
   
    @Override
    public void mouseEntered(MouseEvent e){
    }
    
    @Override
    public void mouseExited(MouseEvent e){
    }
    
    @Override
    public void mouseClicked(MouseEvent e){  
        fireEvent(new ActionEvent(this,0,e.getClickCount(),null, null));  
    }             
    
} 
