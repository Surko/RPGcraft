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
    int tw,th;
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
        setTextSize();
        addMouseListener(this); 
    }
    
    @Override
    protected void reconstructComponent() {
        btnType = (ButtonType)componentContainer.getResource().getType();
        this.title = TextUtils.getResourceText(btnType.getText());           
        setFont(btnType.getFont());    
        setTextSize();
        addMouseListener(this);
    }
    
    @Override
    public void refresh() {
        super.refresh();
        int w = 0, h = 0;
        
        Dimension imgDim = prefferedDim;
        
        w = componentContainer.isAutoWidth() ? imgDim.width : componentContainer.getWidth();
        h = componentContainer.isAutoHeight() ? imgDim.height : componentContainer.getHeight();
        
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
    
    public void setText(String text) {
        this.title = text;
    }
    
    public void setTextSize() {
        th = TextUtils.getTextHeight(getFont());          
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
        isMouseSatisfied(new ActionEvent(this,0,e.getClickCount(),null, null));  
    }             
    
} 
