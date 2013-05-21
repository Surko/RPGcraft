/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics.ui.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.MainGameFrame;
import rpgcraft.graphics.Colors;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.panels.components.Container;
import rpgcraft.panels.components.swing.SwingImagePanel;
import rpgcraft.panels.components.swing.SwingText;

/**
 *
 * @author kirrie
 */
public final class MsgDialog extends SwingImagePanel {
        
    public static final Dimension DIM = new Dimension(150, 100);
    
    private static MsgDialog instance;
    public static volatile boolean inUse;
    
    private SwingText msgText;
    
    private long lifeSpan;
    private long creationTime;
    
    private MsgDialog(Container container, AbstractMenu menu) {
        super(container, menu);                                
                
        // Nastavenie layoutu pre dialog (null <=> ziadny layout)
        this.setLayout(null);
        
        // Nastavenie textu v dialogu
        this.msgText = new SwingText(); 
        this.msgText.setLayout(null);
        this.msgText.setBackground(Colors.getColor(Colors.transparentColor));
        this.add(msgText);
        
        // Nastavenie pozadia dialogu
        this.topColor = Colors.getColor(Colors.invOnTopColor);
        this.backColor = Colors.getColor(Colors.invBackColor);
                                
    }       
                
    @Override
    public void paintComponent(Graphics g) {        
        super.paintComponent(g);         
        if (System.currentTimeMillis() - creationTime >= lifeSpan) {
            exitDialog();
        }   
    }
    
    public void exitDialog() {
        this.getParent().remove(this);   
        this.removeAll();
        lifeSpan = 0L;
        creationTime = 0L;
        inUse = false;
        System.out.println(Thread.currentThread() + " cleared");
    }
    
    @Override
    public int getWidth() {
        return DIM.width;
    }
    
    @Override
    public int getHeight() {
        return DIM.height;              
    }
    
    @Override
    public Dimension getSize() {
        return MsgDialog.DIM;
    }
    
    @Override
    public Dimension getMaximumSize() {
        return MsgDialog.DIM;
    }
    
    @Override
    public Dimension getMinimumSize() {
        return MsgDialog.DIM;
    }
    
    @Override
    public Dimension getPreferredSize() {
        return MsgDialog.DIM;
    }      
    
    public void showOnPanel() {
        this.creationTime = System.currentTimeMillis();
        MainGameFrame.game.add(this, 1);
        MainGameFrame.game.validate();
    }
    
    public void setText(String text) {
        msgText.setText(text);           
        msgText.setLocation((DIM.width - msgText.getTextW())/2, (DIM.height - msgText.getTextH())/2);
        this.add(msgText);
    }
    
    public void setLifeSpan(long time) {
        this.lifeSpan = time;        
    }
        
    public synchronized static MsgDialog getInstance() {
        //System.out.println(Thread.currentThread() + " trying to getInstance");
        if (instance == null) {
            //System.out.println(Thread.currentThread() + " creating Instance");
            instance = new MsgDialog(null, null);
        }
        
        while (inUse) {
            try {
                //System.out.println(Thread.currentThread() + " waiting " + (System.currentTimeMillis() - instance.creationTime));
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(InputDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }  
        
        inUse = true;
        return instance;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("Clicked dialog");
    }
    
    
    
    
}
