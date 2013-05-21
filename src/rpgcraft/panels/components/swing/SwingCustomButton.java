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
import rpgcraft.plugins.AbstractMenu;
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
    
    protected String title;     
    protected int tw = -1,th = -1;
    protected boolean hit = false; 
    protected ButtonType btnType;
   
    @Override
    public abstract void paintComponent(Graphics g); 
    
    protected SwingCustomButton() {
        super();
    } 
    
    public SwingCustomButton (Container container, AbstractMenu menu) {  
        super(container,menu);  
        btnType = (ButtonType)container.getResource().getType();
        this.title = TextUtils.getResourceText(btnType.getText());  
        setFont(btnType.getFont());               
        setTextSize();        
    }
    
    @Override
    protected void reconstructComponent() {
        btnType = (ButtonType)componentContainer.getResource().getType();
        this.title = TextUtils.getResourceText(btnType.getText());           
        setFont(btnType.getFont());    
        setTextSize();        
    }
        
    
    @Override
    public void refresh() {
        super.refresh();
        int w = 0, h = 0;
        
        Dimension imgDim = prefferedDim;
                
        w = componentContainer.isAutoWidth() ? (tw == -1 ? imgDim.width : tw) : componentContainer.getWidth();
        h = componentContainer.isAutoHeight() ? (th == -1 ? imgDim.height : th) : componentContainer.getHeight();
        
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
    public int getWidth() {
        if (componentContainer != null) {
            return componentContainer.getWidth();
        }
        
        if (tw == -1) {
            return prefferedDim.width;
        }
        
        return tw;
        
    }
    
    @Override
    public int getHeight() {
        if (componentContainer != null) {
            return componentContainer.getHeight();
        }
        
        if (th == -1) {
            return prefferedDim.height;
        }
        
        return th;
    }
    
    public void setText(String text) {
        this.title = text;
    }
    
    public void setTextSize() {
        int[] sizes = TextUtils.getTextSize(getFont(), title);
        th = sizes[1];
        tw = sizes[0];
    }                
  
    public void setTextWithSize(String text) {
        setText(text);
        setTextSize();
    }
    @Override
    public void mousePressed(MouseEvent e) {  
        if (active) {
            hit=true; 
            repaintBtnContent();
        }
    }  
   
    @Override
    public void mouseReleased(MouseEvent e){  
        if (active) {
            hit=false;          
            repaintBtnContent();
        }
    }  
    
    public abstract void repaintBtnContent();
   
    @Override
    public void mouseEntered(MouseEvent e){
    }
    
    @Override
    public void mouseExited(MouseEvent e){
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        //isMouseSatisfied(new ActionEvent(this,0,e.getClickCount(),null, null));  
    }             
    
} 
