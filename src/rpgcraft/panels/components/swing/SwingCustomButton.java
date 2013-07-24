/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components.swing;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.panels.components.Container;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.types.ButtonType;
import rpgcraft.utils.TextUtils;

/**
 * Abstraktna trieda ktora dedi od SwingComponent vytvara interface pre tlacidla.
 * Tutot moznost vyuziva SwingImageButton. Zdruzuje v sebe zakladne metody pre spravne
 * fungovanie a ukazanie tlacidiel. 
 * @author Kirrie
 */
public abstract class SwingCustomButton extends SwingComponent {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    /**
     * Logger pre button
     */
    private static final Logger LOG = Logger.getLogger(SwingCustomButton.class.getName());
    /**
     * Preferovane velkosti ked nie su ziadne urcene
     */
    public static final Dimension prefferedDim = new Dimension(300,20);
    
    /**
     * Nazov v tlacidle
     */
    protected String title;     
    /**
     * Dlzka a sirka textu
     */
    protected int tw = -1,th = -1;
    /**
     * Ci je tlacidlo aktualne stlacene
     */
    protected boolean hit = false; 
    /**
     * Typ tlacidla 
     */
    protected ButtonType btnType;
    // </editor-fold>
   
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Prazdny konstruktor na vytvorenie instancie SwingCustomButton. Mozne volat
     * iba z podedenych tried.
     */
    protected SwingCustomButton() {
        super();
    } 
    
    /**
     * Konstruktor ktory vytvori instanciu SwingCustomButtom z kontajneru a AbstractMenu.
     * Mozne ho je volat iba z podedenych tried. Konstruktor inicializuje velkost text v komponente, nastavuje
     * btnType, nazov a font.
     * @param container Kontajner z ktoreho vytvarame tlacidlo
     * @param menu Abstraktne menu v ktorom je komponenta.
     */
    public SwingCustomButton (Container container, AbstractMenu menu) {  
        super(container,menu);  
        btnType = (ButtonType)container.getResource().getType();
        this.title = TextUtils.getResourceText(btnType.getText());  
        setFont(btnType.getFont());               
        setTextSize();        
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Refresh + Update ">
    /**
     * <i>{@inheritDoc}</i>
     * <p>
     * Na zrekonstruovanie je dolezite doplnit btnType, nazov a font s velkostami
     * textov. Vsetky udaje sa daju ziskat z kontajneru v ktorom je komponenta.
     * </p>
     */
    @Override
    protected void reconstructComponent() {
        btnType = (ButtonType)componentContainer.getResource().getType();
        this.title = TextUtils.getResourceText(btnType.getText());           
        setFont(btnType.getFont());    
        setTextSize();        
    }
        
    /**
     * Abstraktna metoda ktora je volana pri stlaceni/upusteni tlacidla. Metoda
     * ma nastavit farby komponenty, alebo pozicie pre pocit stlacenia.
     */
    public abstract void repaintBtnContent();
   
    
    @Override
    public void refresh() {
        super.refresh();
        int w = 0, h = 0;
        
        Dimension imgDim = prefferedDim;
                
        w = componentContainer.isAutoWidth() ? (tw == -1 ? imgDim.width : tw) : componentContainer.getWidth();
        h = componentContainer.isAutoHeight() ? (th == -1 ? imgDim.height : th) : componentContainer.getHeight();
        
        componentContainer.set(w, h);
        //setSize(w, h);  
             
        if (componentContainer.getParentContainer().isAutoWidth() || componentContainer.getParentContainer().isAutoHeight()) {  
            LOG.log(Level.INFO, StringResource.getResource("_rshabort"));
            componentContainer.getParentContainer().addPositionslessCont(componentContainer);
            return;
        }

        // startovacia pozicia pre vykreslenie resource do rodicovskeho kontajneru          
        refreshPositions(w, h, componentContainer.getParentWidth(), 
                componentContainer.getParentHeight()); 

        
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * <i>{@inheritDoc }</i>
     * @return Sirka komponenty
     */
    @Override
    public int getWidth() {
        if (componentContainer != null) {
            return componentContainer.getWidth();
        }
        
        if (tw == -1) {
            return prefferedDim.width;
        }
        
        return tw;
        
    }
    
    /**
     * <i>{@inheritDoc }</i>
     * @return Vyska komponenty
     */
    @Override
    public int getHeight() {
        if (componentContainer != null) {
            return componentContainer.getHeight();
        }
        
        if (th == -1) {
            return prefferedDim.height;
        }
        
        return th;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    /**
     * Metoda ktora nastavi text v tlacidle
     * @param text Text ktory bude v tlacidle
     */
    public void setText(String text) {
        this.title = text;
    }
    
    /**
     * Metoda ktora nastavi sirku a vysku priradeneho textu pomocou metody
     * getTextSize z TextUtils
     * @see TextUtils#getTextSize(java.awt.Font, java.lang.String) 
     */
    public void setTextSize() {
        int[] sizes = TextUtils.getTextSize(getFont(), title);
        th = sizes[1];
        tw = sizes[0];
    } 
    
    /**
     * Metoda ktora nastavi text aj s velkostami. Natoto nam sluzia uz vytvorene metody
     * setText a setTextSize.
     * @param text Text ktory nastavujeme
     */
    public void setTextWithSize(String text) {
        setText(text);
        setTextSize();
    }    
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Mouse Handling ">
    
    /**
     * <i>{@inheritDoc }</i>
     * <p>
     * Pri stlaceni nastavujeme premennu hit na true, ktora pri prekreslovani tlacidla
     * ukaze ze stlacame komponentu.
     * </p>
     * @param e {@inheritDoc }
     */
    @Override
    public void mousePressed(MouseEvent e) {  
        if (active) {
            hit=true; 
            repaintBtnContent();
        }
    }  
   
    /**
     * <i>{@inheritDoc }</i>
     * <p>
     * Pri upusteni nastavujeme premennu hit na false, ktora pri prekreslovani tlacidla
     * ukaze ze stlacame komponentu.
     * </p>
     * @param e {@inheritDoc }
     */
    @Override
    public void mouseReleased(MouseEvent e){  
        if (active) {
            hit=false;          
            repaintBtnContent();
        }
    }  
    
    /**
     * {@inheritDoc }
     * @param e {@inheritDoc }
     */
    @Override
    public void mouseEntered(MouseEvent e){
    }
    
    /**
     * {@inheritDoc }
     * @param e {@inheritDoc }
     */
    @Override
    public void mouseExited(MouseEvent e){
    }
    
    /**
     * {@inheritDoc }
     * @param e {@inheritDoc }
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        //isMouseSatisfied(new ActionEvent(this,0,e.getClickCount(),null, null));  
    }             
    // </editor-fold>
} 
