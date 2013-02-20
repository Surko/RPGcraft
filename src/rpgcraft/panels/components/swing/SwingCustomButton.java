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
import rpgcraft.panels.AbstractMenu;
import rpgcraft.panels.components.Container;
import rpgcraft.resource.types.ButtonType;
import rpgcraft.utils.TextUtils;

/**
 *
 * @author Kirrie
 */
public abstract class SwingCustomButton extends SwingComponent {
    String title;         
    boolean hit = false; 
    ButtonType btnType;
   
    protected SwingCustomButton() {
        super();
    } 
    
    public SwingCustomButton (Container container, AbstractMenu menu) {  
        super(container,menu);  
        btnType = (ButtonType)container.getResource().getType();
        this.title = TextUtils.getResourceText(btnType.getText());   
        _listeners = new ArrayList();        
        addMouseListener(this); 
    }
    
    @Override
    protected void reconstructComponent() {
        this.title = TextUtils.getResourceText(btnType.getText());   
        _listeners = new ArrayList();        
        addMouseListener(this);
    }
    
    @Override
    public Dimension getPreferredSize(){
        return new Dimension(300,20);
    }
    
    public void setText(String text) {
        this.title = text;
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
