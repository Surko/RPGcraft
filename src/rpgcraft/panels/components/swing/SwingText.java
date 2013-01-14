/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components.swing;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import rpgcraft.panels.AbstractMenu;
import rpgcraft.panels.components.Container;
import rpgcraft.utils.TextUtils;

/**
 *
 * @author kirrie
 */
public class SwingText extends SwingComponent{

    private String title;
    private int w;
    
    public SwingText(Container container,AbstractMenu menu) {
        super(container, menu);
        this.title = TextUtils.getResourceText(container.getResource().getText());
        
    }
    
    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);        
        if (changed) {
            FontMetrics fm = g.getFontMetrics();
            w = (fm.getAscent() - fm.getDescent());
            changed = false;
        }
        g.drawString(title, 0, w);
    }
    
    public void setText(String text) {
        this.title= text;
    }
    
    public String getText() {
        return title;
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

    
}
