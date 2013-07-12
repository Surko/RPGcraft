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
import rpgcraft.utils.Pair;
import rpgcraft.utils.TextUtils;

/**
 * Trieda dediaca od SwingComponenty ktora sa pri vytvoreni instancie stara o spravne zobrazenie
 * textu. Trieda obsahuje triedy pre zobrazenie text a refreshnutie
 * velkosti komponenty podla dlzky textu. Taktiez obsahuje moznost priradit komponente
 * akcie ktore sa vykonaju pri stlaceni mysi a moznost kopirovat komponentu do inej komponenty.
 * Pri instancovani, kedze trieda implementuje interface Component, je mozne pridat instanciu
 * ci do hracieho panelu ako aj do inych hracich panelov ktore si nadefinujeme. Z instancie je mozne vybrat
 * text a podla textu vykonavat akcie.
 */
public class SwingText extends SwingComponent{

    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    private static final Logger LOG = Logger.getLogger(SwingText.class.getName());
    private static final int GAP = 1;
    
    protected String title;
    protected ArrayList<Pair<String, Integer>> parsedTitle;
    protected int tw = 0,th = 0;
    protected int sx = 0, sy = 0;
    protected Color textColor;
    protected Color backColor;
    protected TextType txType;    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Konstruktor ktory vytvori instanciu typu SwingText. Pouzivane pri kopirovani
     * instancie.
     */
    public SwingText() {
        this.textColor = Color.BLACK;
        this.backColor = Colors.getColor(Colors.transparentColor);
        this.parsedTitle = new ArrayList<>();
    }
    
    /**
     * Metoda ktora vytvori instanciu typu SwingText. Konstruktor inicializuje 
     * typ textu, nazov, farbu, font, farbu pozadia a rozparsovane texty. Po vytvoreni
     * mozno komponentu pridat do nejakeho rodicovskeho panelu.
     * @param container Kontajner v ktorom je komponenta
     * @param menu Abstraktne menu v ktorom je komponenta zobrazena
     */
    public SwingText(Container container,AbstractMenu menu) {
        super(container, menu);        
        if (container != null) {    
            txType = (TextType)container.getResource().getType();
            this.title = TextUtils.getResourceText(txType.getText());     
            this.textColor = Colors.getColor(txType.getTextColor());
            setFont(txType.getFont()); 
            this.backColor = container.getResource().getBackgroundColorId();
            this.parsedTitle = new ArrayList<>();
            setBackground(backColor);                       
        }                                 
    }
    // </editor-fold>
            
    // <editor-fold defaultstate="collapsed" desc=" Update + Kresliace metody ">
    /**
     * <i>{@inheritDoc }</i>
     * <p>
     * Zrekonstruovanim musime priradit textovy typ, nazov, farbu textu, font a zadnu
     * farbu. Inicializujeme parsedTitle a nastavime pozadie
     * </p>
     */
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
    }
    
    /**
     * Metoda ktora vykresli komponentu do grafickeho kontextu. Vykreslujeme texty
     * zadane v liste parsedTitle podla daneho fontu.
     * @param g Graficky kontext do ktoreho vykreslujeme text
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int h = th;
        g.setFont(getFont());
        g.setColor(textColor);   
        if (parsedTitle != null) {
            for (Pair<String, Integer> pair : parsedTitle) {                                            
                g.drawString(pair.getFirst(), 0, h);                
                h += th + GAP;
            }        
        }
    
        if (isSelected) {
            g.setColor(Colors.getColor(Colors.selectedColor));
            g.fillRect(0, 0, getWidth(), getHeight());
        }       
    }   
    
    
    /**
     * Metoda ktora vykona refresh komponenty. Ked su dlzky komponenty nastavene na
     * auto tak vratim dlzky vnutorneho textu. Na refresh pozicii pouzivame metodu refreshPositions.
     */
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
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    /**
     * Metoda ktora nastavi startovaciu poziciu kreslenia textu
     * @param x X-ova startovacia pozicia
     * @param y Y-ova startovacia pozicia
     */
    public void setTextPositions(int x, int y) {
        this.sx = x;
        this.sy = y;
    }    
    
    /**
     * Metoda ktora nastavi farbu textu
     * @param color Farba textu
     */
    public void setColor(Color color) {
        this.textColor = color;
    }
    
    /**
     * Metoda ktora nastavi velkost textu. Pri priznaku parsing = true rozparsovavame
     * text pomocou metody setParsedText ktora doplni aj dlzky, inak doplname dlzky
     * priamo.
     * @param parsing True/false ci parsujeme text
     */
    public void setTextSize(boolean parsing) {
        parsedTitle.clear();
        if (parsing) {
            setParsedText(title, getWidth());             
        } else {                        
            int[] txtSize = TextUtils.getTextSize(getFont(), title);            
            tw = txtSize[0];
            th = txtSize[1];
            parsedTitle.add(new Pair(title, tw));
        }        
    }
    
    /**
     * Metoda ktora rozparsovava text podla urcitej dlzky zadanej v parametri <b>width</b>.
     * Text na rozparsovanie je v prvom parametri. Z rozparsovaneho textu urcime dlzky
     * tak ze sirku vyberieme priamo z pola ktory dostaneme metodou getTextSize a
     * vysku podla poctu rozparsovanych texto * vyska jedneho riadku + GAP ktory je medzi riadkami.
     * @param text Text na rozparsovanie
     * @param width Sirka do akej rozparsovavame text
     */
    public void setParsedText(String text, int width) { 
        tw = 0;
        this.title = text;
        TextUtils.parseToSize(parsedTitle, width, text, getFont());           
        th = TextUtils.getTextHeight(getFont());            
        for (Pair<String,Integer> pair : parsedTitle) {            
            if (pair.getSecond() > tw) {
                tw = w = pair.getSecond();
            }
        }
        h = parsedTitle.size() * (th + GAP);
        //System.out.println(h);
    }
    
    /**
     * Metoda ktora nastavi pozadie v komponente.
     * @param color Farba pozadia
     */
    @Override
    public void setBackground(Color color) {
        if (componentContainer != null) {
            super.setBackground(backColor);
        } else {
            super.setBackground(color);
        }
    }
    
    /**
     * Metoda ktora zresetuje rozparsovane texty v liste pomocou metody clear.
     */
    public void resetTitle() {
        parsedTitle.clear();
    }
            
    /**
     * Metoda ktora nastavi text v komponente. Po nastavenie volame bud priame nastavenie vysok
     * alebo ked ma komponenta kontajner tak refresh metodu, ktora sa postara o priradenie
     * vysok a sirok automaticky
     * @param text Text na rozparsovanie
     * @param parsing True/false ci sa v metode setTextSize parsuje text.
     */
    public void setText(String text, boolean parsing) {
        this.title= text;        
        this.isNoData = false;
        if (componentContainer != null) {
            refresh();
        } else {            
            setTextSize(parsing);            
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    
    /**
     * Metoda ktora vrati text
     * @return Text v komponente
     */
    public String getText() {
        return title;
    }
    
    /**
     * Metoda vrati sirku texu
     * @return Sirka textu
     */
    public int getTextW() {
        return tw;
    }
    
    /**
     * Metoda vrati vysku texu
     * @return Vyska textu
     */
    public int getTextH() {
        return th;
    }        
    
    /**
     * {@inheritDoc }
     * @return Sirka komponenty
     */
    @Override
    public int getWidth() {        
        return w;
    }
    
    /**
     * {@inheritDoc }
     * @return Vyska komponenty
     */
    @Override
    public int getHeight() {        
        return h;
    }
    
    /**
     * Metoda ktora vrati preferovanu velkost komponenty
     * @return Preferovana velkost komponenty
     */
    @Override
    public Dimension getPreferredSize() {        
        return new Dimension(w,h);
    }
    
    /**
     * Metoda ktora vrati velkost komponenty
     * @return Velkost komponenty
     */
    @Override
    public Dimension getSize() {        
        return new Dimension(w,h);
    }
    
    /**
     * Metoda ktora vrati minimalnu velkost komponenty
     * @return Minimalna velkost komponenty
     */
    @Override
    public Dimension getMinimumSize() {       
        return new Dimension(w,h);
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Copy ">
    /**
     * {@inheritDoc }
     * @param cont {@inheritDoc}
     * @param menu {@inheritDoc }
     * @return Novu SwingText komponentu
     */
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
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Handling ">
    /**
     * {@inheritDoc }
     * @param e {@inheritDoc }
     */
    @Override
    public void mouseClicked(MouseEvent e) {  
        if (_mlisteners != null) {
            isMouseSatisfied(new ActionEvent(this, 0, e.getClickCount(),null, null));
        }
    }

    /**
     * {@inheritDoc }
     * @param e {@inheritDoc }
     */
    @Override
    public void mousePressed(MouseEvent e) {
        
    }

    /**
     * {@inheritDoc }
     * @param e {@inheritDoc }
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        
    }

    /**
     * {@inheritDoc }
     * @param e {@inheritDoc }
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    /**
     * {@inheritDoc }
     * @param e {@inheritDoc }
     */
    @Override
    public void mouseExited(MouseEvent e) {
        
    }
    // </editor-fold>
    
}
