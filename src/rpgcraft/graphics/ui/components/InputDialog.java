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
 * Trieda ktora vytvara instanciu pre zobrazovanie interaktivnych sprav (mozne aj na nejaku dobu)
 * Instancia tejto triedy sa da ziskat iba metodou getInstance (singleton vzor).
 */
public class InputDialog extends SwingImagePanel{
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    public static final Dimension DIM = new Dimension(200, 150);
    public static final Dimension BTN_DIM = new Dimension(30, 20);
    
    // Mozne nazvy tlacidiel
    public static final String DONE = "Done";
    public static final String YES = "Yes";
    public static final String NO = "No";
    public static final String CANCEL = "Cancel";
    
    /**
     * Typ vstupneho dialogu.
     */
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
    
    // Instancia
    private static volatile InputDialog instance;
            
    // Komponenty v dialogu
    private SwingText msgText;
    private SwingInputText inputText;
    private MsgBtns posBut,negBut,cancelBut;
    
    private static Image msgImg;    
    public static volatile boolean inUse;
    
    // Typ dialogu
    private Type dialType = Type.DONE;
    private long lifeSpan = 0L;    
    private long creationTime = 0L;
    // Odpoved pri interakcii s menu
    private int answer = -1;        
    // </editor-fold>
    
    /**
     * Trieda ktora vytvara tlacidlovu komponentu v tomto dialogu => dedime od SwingImageButton.
     * Jedina zaujima metoda je mouseClicked ktora ukonci dialog a nastavi odpoved
     * na tu zadanu pri vytvarani instancie takehoto tlacidla
     */
    private final class MsgBtns extends SwingImageButton{                
        // Odpoved ktora sa nastavi pri stlaceni tlacidla
        private int _answer;        
        
        /**
         * Konstruktor pre dialogove tlacidlo s nastavenou odpovedou pri stlaceni
         * tlacidla
         * @param answer Odpoved ktora sa nastavi pri stlaceni tlacidla
         */
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
        
        /**
         * {@inheritDoc }
         * @return Dlzka tlacidla
         */
        @Override
        public int getWidth() {        
            return BTN_DIM.width;
        }
        
        /**
         * {@inheritDoc }
         * @return Vyska tlacidla
         */
        @Override
        public int getHeight() {
            return BTN_DIM.height;
        }
        
        /**
         * Metoda vrati velkost tlacidla
         * @return Velkost tlacidla
         */
        @Override
        public Dimension getSize() {
            return BTN_DIM;
        }    
        
        /**
         * Metoda vrati preferovanu velkost tlacidla
         * @return Preferovana velkost tlacidla
         */
        @Override
        public Dimension getPreferredSize() {
            return BTN_DIM;
        }  
        
        /**
         * Metoda vrati minimalna velkost tlacidla
         * @return Minimalna velkost tlacidla
         */
        @Override
        public Dimension getMinimumSize() {
            return BTN_DIM;
        }   
        
        /**
         * Metoda ktora reaguje na stlacenie tohoto tlacidla co vykona ukoncenie dialogu 
         * a nastavenie odpovede na premennu _answer inicializovanu v konstruktore.
         * @param e MouseEvent s ktorym bola zavolana metoda
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            //System.out.println("Clicked");            
            answer = _answer;
            exitDialog();
        }                                                           
                
    }
    
    /**
     * Privatny konstruktor pre vytvorenie instancie InputDialogu. Inicializujeme ho
     * pomocou kontajneru, abstraktneho menu a pridanim SwingText do tejto komponenty.
     * Taktiez zinicializujeme mozne tlacidla ktore sa mozu v dialogu vyskytovat.
     * Farby su podobne ako pri AbstractInMenu
     * @param container Kontajner v ktorom sa nachadza dialog
     * @param menu Abstraktne menu urcujuce kde je dialog.
     */
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
             
    /**
     * {@inheritDoc }
     * @param g {@inheritDoc }
     */
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
    
    /**
     * Metoda ktora nastavi typ tohoto dialogu pomocou metody setType s parametrom typu
     * int.
     * @param type Typ dialogu
     */
    public void setType(Type type) {
        setType(type.getIndex());
    }
    
    /**
     * Metoda ktora nastavi typ dialogu a podla tohoto typu prida do dialogu
     * take tlacidla ktore vystihuju tento typ.
     * @param type Ciselna hodnota typu
     */
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
    
    /**
     * Metoda ktora spravne ukoncuje dialog resetovanim nazvu v dialogu, odstranenim vsetkych
     * komponent a v synchronizovanom bloku uvolnenie cakajucich vlakien aby si mohli ziskat 
     * instanciu InputDialogu.
     */
    public void exitDialog() {
        msgText.resetTitle();                
        this.getParent().remove(this);   
        MainGameFrame.game.validate();
        this.removeAll();    
        synchronized (this) {  
            inUse = false;
            notifyAll();              
        }                                
    }
    
    /**
     * {@inheritDoc }
     * @return Sirka dialogu
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
     * Metoda vrati velkost dialogu
     * @return Velkost dialogu
     */
    @Override
    public Dimension getSize() {
        return DIM;
    }
    
     /**
     * Metoda vrati maximalnu velkost dialogu
     * @return Maximalna velkost dialogu
     */
    @Override
    public Dimension getMaximumSize() {
        return DIM;
    }
    
     /**
     * Metoda vrati minimalnu velkost dialogu
     * @return Minimalna velkost dialogu
     */
    @Override
    public Dimension getMinimumSize() {
        return DIM;
    }
    
     /**
     * Metoda vrati preferovanu velkost dialogu
     * @return Preferovana velkost dialogu
     */
    @Override
    public Dimension getPreferredSize() {
        return DIM;
    }                   
    
    /**
     * Metoda ktora zobrazi panel v rame.
     */
    public void showOnPanel() {      
        this.creationTime = System.currentTimeMillis();
        MainGameFrame.game.add(this, 0);      
        MainGameFrame.game.validate();         
    }
    
    /**
     * Metoda ktora kontroluje stav, ci uz bolo nejake tlacidlo stlacene.
     * Vlakno vykonavacie tento proces uspavame a az pri ukonceni dialogu
     * sa prebudi na vratenie odpovede.
     * @return Odpoved dialogu
     */
    public int checkStatus() {        
        synchronized (this) {
            if (answer == -1) {
                try {
                    wait();
                } catch (Exception e) {
                    
                }
            }
        }
        return answer;
    }
    
    /**
     * Metoda ktora nastavi nazov pozitivneho tlacidla
     * @param text Nazov pozitivneho tlacidla
     */
    public void setPosBtnText(String text) {
        posBut.setText(text);        
    }
    
    /**
     * Metoda ktora nastavi nazov negativneho tlacidla
     * @param text Nazov negativneho tlacidla
     */
    public void setNegBtnText(String text) {
        negBut.setText(text);
    }
    
    /**
     * Metoda ktora nastavi nazov tlacidla na zrussenie
     * @param text Nazov tlacidla na zrusenie
     */
    public void setCancelBtnText(String text) {
        cancelBut.setText(text);
    }
    
    /**
     * Metoda ktora nastavi text v dialoge na parameter <b>text</b> spolu
     * aj s velkostami a lokaciou textovej komponenty.
     * @param text Text ktory nastavujeme v dialogu. 
     */
    public void setText(String text) {
        msgText.setParsedText(text, DIM.width - 2*wGap);        
        msgText.setLocation((DIM.width - msgText.getWidth())/2, (DIM.height - msgText.getHeight())/2);         
    }
    
    /**
     * Metoda ktora nastavi dlzku zivota dialogu.
     * @param time Dlzka zivota dialogu
     */
    public void setLifeSpan(long time) {
        this.lifeSpan = time;        
    }
       
    /**
     * Metoda ktora vrati instanciu InputDialogu (singleton vzor). Jedina moznost
     * ako vratit instanciu MsgDialogu. Tu sa ale naskyta problem ked viacero vlakien
     * chce zobrazit text. Pre tento problem synchronizujeme metodu a blok na ziskanie instancie.
     * @return Instanciu InputDialog
     */
    public static InputDialog getInstance() {
        if (instance == null) {
            instance = new InputDialog(null, null);
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
                  
        instance.lifeSpan = 0L;
        instance.creationTime = 0L;
        return instance;
        
    }
    
}
