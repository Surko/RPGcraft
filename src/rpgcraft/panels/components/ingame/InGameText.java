/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components.ingame;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import rpgcraft.graphics.Colors;
import rpgcraft.graphics.Images;
import rpgcraft.panels.AbstractMenu;
import rpgcraft.panels.components.Container;
import rpgcraft.resource.UiResource;
import rpgcraft.utils.MathUtils;
import rpgcraft.utils.TextUtils;

/**
 *
 * @author kirrie
 */
public class InGameText extends InGameComponent {
    
    private String title;
    private int[] rpos;
    
    public InGameText(UiResource resource, Container cont, AbstractMenu menu) {
        super(resource, cont, menu);
        if (resource.getText() != null) {
            this.title = TextUtils.getResourceText(resource.getText());
        }
        paint(cont.getImage().getGraphics());
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Colors.getColor(resource.getBackgroundColorId()));
        if (title != null) {
            if (changed) {                
                 rpos = MathUtils.getStartPositions(resource.getPosition(), cont.getWidth(), cont.getHeight(),
                         50, 10) ;
                 changed = false;
            }
            g.drawString(title, rpos[0], rpos[1]);
        }
    }

    public void setText(String text) {
        this.title = text;
        changed = true;
        paint(cont.getImage().getGraphics());
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
    public void update() {
    }
}
