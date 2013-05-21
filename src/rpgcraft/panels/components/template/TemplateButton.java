/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components.template;

import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import rpgcraft.panels.AboutMenu;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Container;
import rpgcraft.panels.listeners.ActionEvent;
import rpgcraft.resource.UiResource;
import rpgcraft.resource.types.ButtonType;

/**
 *
 * @author Surko
 */
public class TemplateButton extends TemplateComponent {
    ArrayList<TemplateButton> buttons;
    ArrayList<ActionListener> _listeners;
    boolean hit = false;
    String title;
    ButtonType btnType;
    Container cont;
    UiResource res;
    
    protected TemplateButton() {}
    
    public TemplateButton(UiResource button, Container cont, AbstractMenu menu) {
        super(button, cont, menu);
        btnType = (ButtonType)resource.getType();
    }
    
    @Override
    protected void reconstructComponent() {
        // Doplnit Rovnaky kod ako v konstruktore
    }
    
    @Override
    public void isMouseSatisfied(ActionEvent event) {  
        for (int i = 0;i<_listeners.size() ;i++ ){  
            ActionListener listener = (ActionListener)_listeners.get(i);  
            listener.actionPerformed(event);  
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {                 
        isMouseSatisfied(new ActionEvent(this, 0,e.getClickCount(), null, null));
    }
    
    @Override
    public void paint(Graphics g) {
        g.drawImage(null, 0, 0, null);    
    }
    
    public void setText(String text) {
        this.title = text;
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        hit = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        hit = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }          

    @Override
    public void update() {       
    }

    @Override
    public Component copy(Container cont, AbstractMenu menu) {
        TemplateButton result = new TemplateButton();
        return result;
    }

    
}
