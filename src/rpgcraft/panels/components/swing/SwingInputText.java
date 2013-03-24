/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.graphics.Colors;
import rpgcraft.handlers.InputHandle;
import rpgcraft.panels.AbstractMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Container;
import rpgcraft.panels.listeners.ActionEvent;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.types.TextType;
import rpgcraft.utils.TextUtils;

/**
 *
 * @author kirrie
 */
public class SwingInputText extends SwingComponent {

    private static final Logger LOG = Logger.getLogger(SwingInputText.class.getName());
    
    private String text;
    private TextType txType;
    private Color textColor,backColor;
    private int w,h;        
    
    
    public SwingInputText(Container container,AbstractMenu menu) {
        super(container, menu);        
        if (container != null) {    
            txType = (TextType)container.getResource().getType();
            this.text = TextUtils.getResourceText(txType.getText());     
            this.textColor = Color.WHITE;            
            setFont(txType.getFont()); 
            this.backColor = Colors.getColor(container.getResource().getBackgroundColorId());
        }
        setBackground(backColor);
        setTextSize();        
    }        
    
    public void setTextSize() {
        int[] txtSize = TextUtils.getTextSize(getFont(), text);  
        w = txtSize[0];
        h = txtSize[1];
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
    public void paintComponent(Graphics g) {
        super.paintComponent(g);                  
        if (text != null) {            
            g.setFont(getFont());
            g.setColor(textColor);
            g.drawString(text, 0, h);
        }                     
    } 
    
    public String getText() {
        return text;
    }
    
    @Override
    public Component copy(Container cont, AbstractMenu menu) {
        return null;        
    }

    @Override
    protected void reconstructComponent() {
        
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
                        
        int[] txtSize = TextUtils.getTextSize(getFont(), text); 
        
        w = txtSize[0];
        h = txtSize[1];
        
        _w = componentContainer.isAutoWidth() ? txtSize[0] : componentContainer.getWidth();
        _h = componentContainer.isAutoHeight() ? txtSize[1] : componentContainer.getHeight();
        
        //setSize(_w, _h);
        componentContainer.set(_w, _h);
        
        if (componentContainer.getParentContainer().getComponent().getLayout() == null) { 
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
    
    @Override
    public void processKeyEvents(InputHandle input) {                
        boolean upper = false;
        char c = '\0';
        for (int keyCode : input.runningKeys) {
            
            switch (keyCode) {
                case KeyEvent.VK_BACK_SPACE :
                    if (text.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                    }
                    break;
                case KeyEvent.VK_SHIFT : {
                    upper = true;
                } break;
                default :
                    break;
            }
            
        }
        
        for (int keyCode : input.clickedKeys) {
            c = input.getChar(keyCode);
        }

        if (c != '\0')
            text += (upper == true ? Character.toUpperCase(c) : c);
        
        refresh();
        updateUI();
    }
            
}
