/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import rpgcraft.graphics.Colors;
import rpgcraft.panels.AbstractMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Container;
import rpgcraft.resource.ImageResource;
import rpgcraft.resource.types.TextType;
import rpgcraft.utils.TextUtils;
import sun.font.FontDesignMetrics;

/**
 *
 * @author kirrie
 */
public class SwingText extends SwingComponent{

    protected String title;
    protected int w = 0;
    protected int h = 0;
    protected Color textColor;
    protected Color backColor;
    protected TextType txType;
        
    public SwingText() {}
    
    public SwingText(Container container,AbstractMenu menu) {
        super(container, menu);        
        if (container != null) {    
            txType = (TextType)container.getResource().getType();
            this.title = TextUtils.getResourceText(txType.getText());      
            setFont(txType.getFont()); 
            this.backColor = Colors.getColor(container.getResource().getBackgroundColorId());
        }        
        this.textColor = Color.BLACK;        
        setBackground(backColor);
        setTextSize();        
    }
    
    @Override
    protected void reconstructComponent() {
        if (componentContainer != null) { 
            txType = (TextType)componentContainer.getResource().getType();
            this.title = TextUtils.getResourceText(txType.getText());      
            setFont(txType.getFont());            
            this.backColor = Colors.getColor(componentContainer.getResource().getBackgroundColorId());
        }        
        this.textColor = Color.BLACK;        
        setBackground(backColor);
        setTextSize();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); 
        g.setColor(textColor);  
        g.setFont(getFont());
        if (title != null) {
            g.drawString(title, 0, h);
        }
    }   
    
    private void setTextSize() {
        if ((title == null)||(getFont() == null)) return;
        FontMetrics fm = FontDesignMetrics.getMetrics(getFont());
        h = (fm.getAscent() - fm.getDescent());
        w = fm.stringWidth(title);
        changed = false;
    }
        
    
    public void setColor(Color color) {
        this.textColor = color;
    }
    
    public void setText(String text) {
        this.title= text;
        setTextSize();
    }
    
    public String getText() {
        return title;
    }
    
    public int getW() {
        return w;
    }
    
    public int getH() {
        return h;
    }
    
    @Override
    public void fireEvent(ActionEvent event) {
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
    public void addActionListener(ActionListener listener) {
    }

    @Override
    public void removeActionListener(ActionListener listener) {
    }

    @Override
    public Component copy(Container cont, AbstractMenu menu) {        
        SwingText result = new SwingText();
        
        result.componentContainer = cont;
        result.menu = menu;
        
        result.reconstructComponent();
        
        return result;
        
    }
}
