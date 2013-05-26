/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.graphics.Colors;
import rpgcraft.plugins.AbstractMenu;
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
public class SwingText extends SwingComponent{

    private static final Logger LOG = Logger.getLogger(SwingText.class.getName());
    private static final int GAP = 1;
    
    protected String title;
    protected ArrayList<String> parsedTitle;
    protected int tw = 0,th = 0, w = 0, h = 0;
    protected int sx = 0, sy = 0;
    protected Color textColor;
    protected Color backColor;
    protected TextType txType;    
        
    public SwingText() {
        this.textColor = Color.BLACK;
        this.backColor = Colors.getColor(Colors.transparentColor);
        this.parsedTitle = new ArrayList<>();
    }
    
    public SwingText(Container container,AbstractMenu menu) {
        super(container, menu);        
        if (container != null) {    
            txType = (TextType)container.getResource().getType();
            this.title = TextUtils.getResourceText(txType.getText());     
            this.textColor = Colors.getColor(txType.getTextColor());
            setFont(txType.getFont()); 
            this.backColor = container.getResource().getBackgroundColorId();
        }                     
        this.parsedTitle = new ArrayList<>();
        setBackground(backColor);        
        setText(title, false);       
    }
    
    @Override
    protected void reconstructComponent() {
        if (componentContainer != null) { 
            txType = (TextType)componentContainer.getResource().getType();
            this.title = TextUtils.getResourceText(txType.getText());  
            this.textColor = Colors.getColor(txType.getTextColor());
            setFont(txType.getFont());            
            this.backColor = componentContainer.getResource().getBackgroundColorId();
        }            
        this.parsedTitle = new ArrayList<>();
        setBackground(backColor);
        setText(title, false);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int h = th;
        g.setFont(getFont());
        g.setColor(textColor);   
        if (parsedTitle != null) {
            for (String s : parsedTitle) {                                            
                g.drawString(s, 0, h);                
                h += th + GAP;
            }        
        }
    
        if (isSelected) {
            g.setColor(Colors.getColor(Colors.selectedColor));
            g.fillRect(0, 0, getWidth(), getHeight());
        }       
    }   
    
    public void setTextPositions(int x, int y) {
        this.sx = x;
        this.sy = y;
    }    
    
    public void setColor(Color color) {
        this.textColor = color;
    }
    
    public void setTextSize(boolean parsing) {
        parsedTitle.clear();
        if (parsing) {
            setParsedText(title, getWidth());             
        } else {            
            parsedTitle.add(title);
            int[] txtSize = TextUtils.getTextSize(getFont(), title);            
            tw = txtSize[0];
            th = txtSize[1];
        }        
    }
    
    public void setParsedText(String text, int width) {                
        this.title = text;
        TextUtils.parseToSize(parsedTitle, width, text, getFont());          
        if (parsedTitle.size() > 0) {
            int[] txtSize = TextUtils.getTextSize(getFont(), parsedTitle.get(0));
            th = txtSize[1];
            tw = w = txtSize[0];
        }
        h = parsedTitle.size() * (th + GAP);
        //System.out.println(h);
    }
    
    public void resetTitle() {
        parsedTitle.clear();
    }
    
    @Override
    public void setBackground(Color color) {
        if (componentContainer != null) {
            super.setBackground(backColor);
        } else {
            super.setBackground(color);
        }
    }
    
    public void setText(String text, boolean parsing) {
        this.title= text;        
        this.isNoData = false;
        if (componentContainer != null) {
            refresh();
        } else {            
            setTextSize(parsing);            
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
    public int getWidth() {        
        return w;
    }
    
    @Override
    public int getHeight() {        
        return h;
    }
    
    @Override
    public Dimension getPreferredSize() {        
        return new Dimension(w,h);
    }
    
    @Override
    public Dimension getSize() {        
        return new Dimension(w,h);
    }
    
    @Override
    public Dimension getMinimumSize() {       
        return new Dimension(w,h);
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
                        
        setTextSize(false);
        
        if (componentContainer.isAutoWidth()) {
            w = Math.min(componentContainer.getParentWidth(), tw);            
        } else {
            w = componentContainer.getWidth();            
        }
        if (componentContainer.isAutoHeight()) {                                            
            if (!componentContainer.isAutoWidth()) {
                setParsedText(title, componentContainer.getWidth());
            }
            h = th;
        } else {
            h = componentContainer.getHeight();
        }
        
        //setSize(_w, _h);
        componentContainer.set(w, h);
         
        // startovacia pozicia pre vykreslenie resource do rodicovskeho kontajneru            
        if (componentContainer.getParentContainer().isAutoWidth() || componentContainer.getParentContainer().isAutoHeight()) {  
            LOG.log(Level.INFO, StringResource.getResource("_rshabort"));
            componentContainer.getParentContainer().addPositionslessCont(componentContainer);
            return;
        }

        // startovacia pozicia pre vykreslenie resource do rodicovskeho kontajneru          
         
        refreshPositions(w, h, componentContainer.getParentWidth(), 
            componentContainer.getParentHeight());  

    }
}
