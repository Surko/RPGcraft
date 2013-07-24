/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.graphics.Colors;
import rpgcraft.handlers.InputHandle;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.panels.components.Container;
import rpgcraft.panels.listeners.ActionEvent;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.types.TextType;
import rpgcraft.utils.Pair;
import rpgcraft.utils.TextUtils;

/**
 * Trieda dediaca od SwingComponenty ktora sa pri vytvoreni instancie stara o spravne zobrazenie
 * interaktivneho textu. Trieda obsahuje triedy pre zobrazenie, pisanie textu a snim aj nasledne refreshnutie
 * velkosti komponenty podla dlzky textu. Taktiez obsahuje moznost priradit komponente
 * akcie ktore sa vykonaju pri stlaceni mysi a moznost kopirovat komponentu do inej komponenty.
 * Pri instancovani, kedze trieda implementuje interface Component, je mozne pridat instanciu
 * ci do hracieho panelu ako aj do inych hracich panelov ktore si nadefinujeme. Z instancie je mozne vybrat
 * text a podla textu vykonavat akcie.
 */
public class SwingInputText extends SwingComponent {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    /**
     * Logger pre input text
     */
    private static final Logger LOG = Logger.getLogger(SwingInputText.class.getName());
    /**
     * Odsadenie textu
     */
    private static final int GAP = 1;
    
    /**
     * Text v texte :D
     */
    private String title;
    /**
     * Textovy typ
     */
    private TextType txType;
    /**
     * Farba textu a pozadia
     */
    private Color textColor,backColor;
    /**
     * Rozparsovany text v texte.
     */
    protected ArrayList<Pair<String,Integer>> parsedTitle;
    /**
     * Velkost textu v komponente
     */
    protected int tw = 0,th = 0;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Konstruktor ktory vytvori instanciu typu SwingInputText. Pouzivane pri kopirovani
     * instancie.
     */
    public SwingInputText() {
        
    }
    
    /**
     * Metoda ktora vytvori instanciu typu SwingInputText. Konstruktor inicializuje 
     * typ textu, nazov, farbu, font, farbu pozadia a rozparsovane texty. Po vytvoreni
     * mozno komponentu pridat do nejakeho rodicovskeho panelu.
     * @param container Kontajner v ktorom je komponenta
     * @param menu Abstraktne menu v ktorom je komponenta zobrazena
     */
    public SwingInputText(Container container,AbstractMenu menu) {
        super(container, menu);        
        if (container != null) {    
            txType = (TextType)container.getResource().getType();
            this.title = TextUtils.getResourceText(txType.getText());     
            this.textColor = Color.WHITE;            
            setFont(txType.getFont()); 
            this.backColor = container.getResource().getBackgroundColorId();
            this.parsedTitle = new ArrayList<>();
            setBackground(backColor);                   
        }
               
    }        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    /**
     * Metoda ktora nastavi text v komponente. Po nastavenie volame bud priame nastavenie vysok
     * alebo ked ma komponenta kontajner tak refresh metodu, ktora sa postara o priradenie
     * vysok a sirok automaticky
     * @param text Text na rozparsovanie
     * @param parsing True/false ci sa v metode setTextSize parsuje text.
     */
    public final void setText(String text, boolean parsing) {
        this.title= text;        
        this.isNoData = false;
        if (componentContainer != null) {
            refresh();
        } else {            
            setTextSize(parsing);            
        }
    }
    
    /**
     * Metoda ktora zresetuje rozparsovane texty v liste pomocou metody clear.
     */
    public void resetTitle() {
        parsedTitle.clear();
    }
    
    /**
     * Metoda ktora rozparsovava text podla urcitej dlzky zadanej v parametri <b>width</b>.
     * Text na rozparsovanie je v prvom parametri. Z rozparsovaneho textu urcime dlzky
     * tak ze sirku vyberieme priamo z pola ktory dostaneme metodou getTextSize a
     * vysku podla poctu rozparsovanych texto * vyska jedneho riadku + GAP ktory je medzi riadkami.
     * @param text Text na rozparsovanie
     * @param width Sirka do akej rozparsovavame text
     */
    public final void setParsedText(String text, int width) {
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
     * Metoda ktora nastavi velkost textu. Pri priznaku parsing = true rozparsovavame
     * text pomocou metody setParsedText ktora doplni aj dlzky, inak doplname dlzky
     * priamo.
     * @param parsing True/false ci parsujeme text
     */
    public final void setTextSize(boolean parsing) {
        parsedTitle.clear();         
        if (parsing) {
            setParsedText(title, getWidth());                         
        } else {                        
            int[] txtSize = TextUtils.getTextSize(getFont(), title);            
            tw = txtSize[0];
            th = txtSize[1];
            parsedTitle.add(new Pair<>(title, tw));
        }               
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
      
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Update + Kresliace metody ">
    /**
     * Metoda ktora vykresli komponentu do grafickeho kontextu. Vykreslujeme texty
     * zadane v liste parsedTitle podla daneho fontu.
     * @param g Graficky kontext do ktoreho vykreslujeme text
     */
    @Override
    public void paintComponent(Graphics g) {
        //System.out.println("input " + Framer.debugint);
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
     * <i>{@inheritDoc }</i>
     * <p>
     * Zrekonstruovanim musime priradit textovy typ, nazov, farbu textu, font a zadnu
     * farbu. Inicializujeme parsedTitle a nastavime pozadie
     * </p>
     */
    @Override
    protected void reconstructComponent() {
        
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
        
        
        if (componentContainer.getParentContainer().getComponent().getLayout() == null) { 
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
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora vrati text zadany v komponente
     * @return Text z komponenty
     */
    public String getText() {
        return title;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Handling ">
    /**
     * Metoda ktora vyplnuje vstupny text podla toho co sa stlaca. Stlacene klavesy ziskame
     * z InputHadnle. Kazde tlacidlo v InputHadnle ma priradny char ktory sa vypise
     * pri stlaceni, pri neexistencii tlacidla to je prazdny znak.
     * Pri stlaceni SHIFT nastavujeme premennu upper ktora donuti vykreslenie 
     * velkeho pisma a pri stlaceni BACKSPACE vymazavame text. Po kazdej zmene textu 
     * volame metodu refresh, ktora zaktualizuje vysky a pozicie komponenty.
     * @param input InputHandle podla ktoreho spracovavame vstup.
     */
    @Override
    public void processKeyEvents(InputHandle input) {                
        boolean upper = false;
        char c = '\0';
        for (int keyCode : input.runningKeys) {
            
            switch (keyCode) {
                case KeyEvent.VK_BACK_SPACE :
                    if (title.length() > 0) {
                        title = title.substring(0, title.length() - 1);
                        //System.out.println(Thread.currentThread()); 
                        refresh();
                        if (getLayout() != null) {
                            updateUI();
                        }
                    }
                    break;
                case KeyEvent.VK_SHIFT : {
                    upper = true;
                } break;
                default :
                    break;
            }
            
        }        
        for (int keyCode : input.clickedKeys) {
            c = input.getChar(keyCode);
        }

        if (c != '\0') {
            title += (upper == true ? Character.toUpperCase(c) : c);
            //System.out.println(Thread.currentThread()); 
            refresh();
            if (getLayout() != null) {
                updateUI();
            } 
        }
        
               
    }
    
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
     
    // <editor-fold defaultstate="collapsed" desc=" Copy ">
    /**
     * {@inheritDoc }
     * @param cont {@inheritDoc}
     * @param menu {@inheritDoc }
     * @return Novu SwingInputText komponentu
     */
    @Override
    public SwingInputText copy(Container cont, AbstractMenu menu) {
        SwingInputText result = new SwingInputText();
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
    
}
