/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics.ui.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.MainGameFrame;
import rpgcraft.graphics.Colors;
import rpgcraft.graphics.ImageOperation;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.panels.components.Container;
import rpgcraft.panels.components.swing.SwingImageButton;
import rpgcraft.panels.components.swing.SwingImagePanel;
import rpgcraft.panels.components.swing.SwingInputText;
import rpgcraft.panels.components.swing.SwingText;
import rpgcraft.resource.ImageResource;

/**
 *
 * @author kirrie
 */
public class InputDialog extends SwingImagePanel{
    
    public static final Dimension DIM = new Dimension(150, 120);
    public static final Dimension BTN_DIM = new Dimension(30, 20);
    
    public static final String DONE = "Done";
    public static final String YES = "Yes";
    public static final String NO = "No";
    public static final String CANCEL = "Cancel";
    
    public enum Type {
        YES_NO(1),
        DONE(0),
        YES_NO_CANCEL(2);
        
        private int index;
        
        private Type(int index) {
            this.index = index;
        }
        
        public int getIndex() {
            return index;
        }
        
        
    }
    
    private static InputDialog instance;
    
    private SwingText msgText;
    private SwingInputText inputText;
    private MsgBtns posBut,negBut,cancelBut;
    
    private static Image msgImg;    
    public static volatile boolean inUse;
    
    private Type dialType = Type.DONE;
    private long lifeSpan = 0L;    
    private long creationTime = 0L;
    private int answer = -1;        
    
    private final class MsgBtns extends SwingImageButton{                
        
        private int _answer;        
        
        public MsgBtns(int answer) {           
            this.setLayout(null);
            
            this.active = true;
            this.io = new ImageOperation(msgImg); 
            this.io.createBufferedImages(BufferedImage.TYPE_INT_RGB);
            this.io.cropBufferedImage(0, 0, BTN_DIM.width, BTN_DIM.height);
            repaintBtnContent();
            this._answer = answer;    
            addOwnMouseListener();
        }
        
        @Override
        public int getWidth() {        
            return BTN_DIM.width;
        }
        
        @Override
        public int getHeight() {
            return BTN_DIM.height;
        }
        
        @Override
        public Dimension getSize() {
            return BTN_DIM;
        }    
        
        @Override
        public Dimension getPreferredSize() {
            return BTN_DIM;
        }  
        
        @Override
        public Dimension getMinimumSize() {
            return BTN_DIM;
        }   
        
        @Override
        public void mouseClicked(MouseEvent e) {
            //System.out.println("Clicked");
            answer = _answer;
            exitDialog();
        }                                                           
                
    }
    
    private InputDialog(Container container, AbstractMenu menu) {
        super(container, menu);                                        
        this.setLayout(null);         
        // Nastavenie pozadia dialogu
        setBackground(Colors.getColor(Colors.transparentColor));
        this.topColor = Colors.getColor(Colors.invOnTopColor);
        this.backColor = Colors.getColor(Colors.invBackColor);
        
        // Nastavenie pozadia tlacidiel dialogu
        ImageOperation io = new ImageOperation(ImageResource.getResource("mainButtonTexture").getBackImage());
        io.createBufferedImages(BufferedImage.TYPE_INT_RGB);
        io.cropBufferedImage(0, 0, BTN_DIM.width, BTN_DIM.height);
        io.finalizeOp();
        msgImg = io.getShowImg();
        
        // Nastavenie textu v dialogu.
        this.msgText = new SwingText(); 
        this.msgText.setLayout(null);
        this.msgText.setBackground(Colors.getColor(Colors.transparentColor));
        this.add(msgText); 
        
        // Nastavenie moznych tlacidiel
        this.posBut = new MsgBtns(1);        
        this.negBut = new MsgBtns(2);
        this.cancelBut = new MsgBtns(0);    
        
        setBounds(0, 0, DIM.width, DIM.height);
                                
    }   
                                
    @Override
    public void paintComponent(Graphics g) {        
        super.paintComponent(g);                         
        if (lifeSpan > 0L) {
            if (System.currentTimeMillis() - creationTime >= lifeSpan) {
                exitDialog();
                answer = 0;
            }
        }        
    }
    
    public void setType(Type type) {
        setType(type.getIndex());
    }
    
    public void setType(int type) {
        this.add(msgText);
        switch (type) {
            case 0 : {                  
                this.posBut.setBounds((DIM.width - BTN_DIM.width)/2, DIM.height - BTN_DIM.height - 2, BTN_DIM.width, BTN_DIM.height);
                this.posBut.setTextWithSize(DONE);                                 
                this.add(posBut);                                          
            } break;
            case 1 : {                
                int eq = DIM.width / 2;
                this.posBut.setBounds((eq - BTN_DIM.width)/2, DIM.height - BTN_DIM.height - 2, BTN_DIM.width, BTN_DIM.height);
                this.negBut.setBounds(eq + (eq - BTN_DIM.width)/2, DIM.height - BTN_DIM.height - 2, BTN_DIM.width, BTN_DIM.height);
                this.posBut.setTextWithSize(YES);                
                this.negBut.setTextWithSize(NO);                
                this.add(posBut);
                this.add(negBut);
            } break;
            case 2 : {                
                int eq = DIM.width / 3;
                this.posBut.setBounds((eq - BTN_DIM.width)/2, DIM.height - BTN_DIM.height - 2, BTN_DIM.width, BTN_DIM.height);
                this.negBut.setBounds(eq + (eq - BTN_DIM.width)/2, DIM.height - BTN_DIM.height - 2, BTN_DIM.width, BTN_DIM.height);
                this.cancelBut.setBounds(2*eq + (eq - BTN_DIM.width)/2, DIM.height - BTN_DIM.height - 2, BTN_DIM.width, BTN_DIM.height);
                this.posBut.setTextWithSize(YES);
                this.negBut.setTextWithSize(NO);
                this.cancelBut.setTextWithSize(CANCEL);
                this.add(posBut);
                this.add(negBut);
                this.add(cancelBut);
            } break;
        }
    }
    
    public void exitDialog() {
        msgText.resetTitle();
        this.getParent().remove(this);   
        MainGameFrame.game.validate();
        this.removeAll();        
        inUse = false;          
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
        return DIM;
    }
    
    @Override
    public Dimension getMaximumSize() {
        return DIM;
    }
    
    @Override
    public Dimension getMinimumSize() {
        return DIM;
    }
    
    @Override
    public Dimension getPreferredSize() {
        return DIM;
    }                   
    
    public void showOnPanel() {
        this.creationTime = System.currentTimeMillis();
        MainGameFrame.game.add(this, 1);      
        MainGameFrame.game.validate();         
    }
    
    public void setPosBtnText(String text) {
        posBut.setText(text);        
    }
    
    public void setNegBtnText(String text) {
        negBut.setText(text);
    }
    
    public void setCancelBtnText(String text) {
        cancelBut.setText(text);
    }
    
    public void setText(String text) {
        msgText.setParsedText(text, DIM.width - 2*wGap);        
        msgText.setLocation((DIM.width - msgText.getWidth())/2, (DIM.height - msgText.getHeight())/2);         
    }
    
    public void setLifeSpan(long time) {
        this.lifeSpan = time;        
    }
    
    public int checkStatus() {
        try {
            while (answer == -1) {
                Thread.sleep(5);
            }
        } catch (Exception e) {}
        return answer;        
    }
        
    public synchronized static InputDialog getInstance() {
        if (instance == null) {
            instance = new InputDialog(null, null);
        }
        
        while (inUse) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(InputDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }  
        
        inUse = true;
        instance.lifeSpan = 0L;
        instance.creationTime = 0L;
        return instance;
        
    }
    
}
