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
 * Trieda ktora vytvara instanciu pre zobrazovanie sprav na nejaku urcitu dobu.
 * Instancia tejto triedy sa da ziskat iba metodou getInstance (singleton vzor).
 * Viacero vlakien moze volat metodu na ziskanie instancie no takyto problem je osetreny.
 */
public final class MsgDialog extends SwingImagePanel {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    /**
     * Logger pre dialog.
     */
    public static final Dimension DIM = new Dimension(200, 150);
    
    /**
     * Instancia dialogu
     */
    private static MsgDialog instance;
    /**
     * Ci je dialog akurat vyuzivany
     */
    public static volatile boolean inUse;
    
    /**
     * Komponenta s textom
     */
    private SwingText msgText;
    
    /**
     * Dlzka zivota dialogu
     */
    private long lifeSpan;
    /**
     * Kedy bol dialog vytvoreny
     */
    private long creationTime;
    // </editor-fold>
    
    /**
     * Privatny konstruktor pre vytvorenie instancie msgDialogu. Inicializujeme ho
     * pomocou kontajneru, abstraktneho menu a pridanim SwingText do tejto komponenty.
     * Farby su podobne ako pri AbstractInMenu
     * @param container Kontajner v ktorom sa nachadza dialog
     * @param menu Abstraktne menu urcujuce kde je dialog.
     */
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
                
    /**
     * {@inheritDoc }
     * <p>
     * Metoda po case lifeSpan ukonci dialog.
     * </p>
     * @param g {@inheritDoc }
     */
    @Override
    public void paintComponent(Graphics g) {        
        super.paintComponent(g);         
        if (System.currentTimeMillis() - creationTime >= lifeSpan) {
            exitDialog();
        }   
    }
    
    /**
     * Metoda ktora ukonci dialog s textom. Ukoncenie prebieha vymazanim vsetky komponent
     * a nastavenim casovych premennych na 0.
     */
    public void exitDialog() {
        this.getParent().remove(this);   
        this.removeAll();
        lifeSpan = 0L;
        creationTime = 0L;
        synchronized (this) {  
            inUse = false;
            notifyAll();              
        }     
    }
    
    /**
     * {@inheritDoc }
     * @return Dlzka dialogu
     */
    @Override
    public int getWidth() {
        return DIM.width;
    }
    
    /**
     * {@inheritDoc }
     * @return Vyska dialogu
     */
    @Override
    public int getHeight() {
        return DIM.height;              
    }
    
    /**
     * Metoda ktora vrati velkost dialogu
     * @return Velkost dialogu
     */
    @Override
    public Dimension getSize() {
        return MsgDialog.DIM;
    }
    
    /**
     * Metoda ktora vrati maximalnu velkost dialogu
     * @return Maximalnu velkost dialogu
     */
    @Override
    public Dimension getMaximumSize() {
        return MsgDialog.DIM;
    }
    
    /**
     * Metoda ktora vrati minimalnu velkost dialogu.
     * @return Minimalna velkost dialogu
     */
    @Override
    public Dimension getMinimumSize() {
        return MsgDialog.DIM;
    }
    
    /**
     * Metoda ktora vrati preferovanu velkost dialogu
     * @return Preferovana velkost dialogu
     */
    @Override
    public Dimension getPreferredSize() {
        return MsgDialog.DIM;
    }      
    
    /**
     * Metoda ktora zobrazi dialog v rame a nastavi cas tohoto ukonu.
     */
    public void showOnPanel() {
        this.creationTime = System.currentTimeMillis();
        MainGameFrame.game.add(this, 1);
        MainGameFrame.game.validate();
    }
    
    /**
     * Metoda ktora nastavi text v dialoge na parameter <b>text</b> spolu
     * aj s velkostami a lokaciou textovej komponenty.
     * @param text Text ktory nastavujeme v dialogu. 
     */
    public void setText(String text) {        
        msgText.setParsedText(text, DIM.width - 2*wGap);
        msgText.setLocation((DIM.width - msgText.getTextW())/2, (DIM.height - msgText.getTextH())/2);
        this.add(msgText);
    }
    
    /**
     * Metoda ktora nastavi dlzku zivota dialogu
     * @param time Dlzka zivota dialogu
     */
    public void setLifeSpan(long time) {
        this.lifeSpan = time;        
    }
        
    /**
     * Metoda ktora vrati instanciu MsgDialogu (singleton vzor). Jedina moznost
     * ako vratit instanciu MsgDialogu. Tu sa ale naskyta problem ked viacero vlakien
     * chce zobrazit text. Pre tento problem synchronizujeme metodu a blok na ziskanie instancie.
     * @return Instanciu MsgDialog
     */
    public synchronized static MsgDialog getInstance() {
        //System.out.println(Thread.currentThread() + " trying to getInstance");
        if (instance == null) {
            //System.out.println(Thread.currentThread() + " creating Instance");
            instance = new MsgDialog(null, null);
        }
        
        synchronized(instance) {
            while (inUse) {
                try {                    
                    instance.wait();                    
                    
                } catch (Exception ex) {
                    Logger.getLogger(InputDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            }  
            inUse = true;
        }  
        
        inUse = true;
        return instance;
    }
                   
}
