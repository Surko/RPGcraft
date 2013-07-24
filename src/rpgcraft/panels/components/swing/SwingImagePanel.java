/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.graphics.Colors;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.panels.GameMenu;
import rpgcraft.panels.components.Container;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.types.PanelType;
import rpgcraft.utils.ImageUtils;
import rpgcraft.utils.MathUtils;

/**
 * Trieda dediaca od SwingComponenty ktora sa pri vytvoreni instancie stara o spravne zobrazenie
 * panelu. Trieda obsahuje triedy pre zobrazenie panelu, ci refreshnutie
 * velkosti komponenty podla dlzky vnutorneho obrazku. Taktiez obsahuje moznost priradit komponente
 * akcie ktore sa vykonaju pri stlaceni mysi a moznost kopirovat komponentu do inej komponenty.
 * Pri instancovani, kedze trieda implementuje interface Component, je mozne pridat instanciu
 * ci do hracieho panelu ako aj do inych hracich panelov ktore si nadefinujeme. V paneli sa mozu
 * nachadzat dalsie komponenty typu Component.
 * @see SwingComponent
 */
public class SwingImagePanel extends SwingComponent {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    /**
     * Logger pre Panel
     */
    private static final Logger LOG = Logger.getLogger(SwingImagePanel.class.getName());
    /**
     * Odsadenie pri kresleni farieb. Spodna farba sa vykresli po celej velkosti. Vrchna
     * je odsadena
     */
    protected static final int wGap = 5, hGap = 5;
    /**
     * Obrazok v pozadi panelu
     */
    protected Image backImage;
    /**
     * Farba pozadia v pozadi
     */
    protected Color backColor;
    /**
     * Farba pozadia v popredi
     */
    protected Color topColor;
    /**
     * Pozicie obrazku v komponente
     */
    protected int[] rpos;
    /**
     * Typ panelu
     */
    protected PanelType pType;
    /**
     * Ci je panel povazovany za hraci => je v nom vykreslovana hra.
     */
    protected boolean gamePanel;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Prazdny konstruktor ktory vytvori instanciu SwingImagePanel.
     */
    protected SwingImagePanel() {
    }
    
    /**
     * Konstruktor ktory vytvori novu instanciu SwingImagePanel z kontajneru <b>cont</b>
     * ktory sa nachadza v menu <b>menu</b>. V paneli urcime obrazok aj farby v pozadi aj v popredi.
     * Taktiez urcime ci je panel hraci.
     * @param container Kontajner v ktorom sa nachadza komponenta
     * @param menu Abstraktne menu v ktorom mame tuto komponentu
     */
    public SwingImagePanel(Container container, AbstractMenu menu) {
        super(container, menu);
        if (container != null) {
            String sbackImage = container.getResource().getBackgroundTextureId();
            if (sbackImage != null && sbackImage.equals("GAME")) {
                if (menu instanceof GameMenu) {
                    this.backImage=  null;
                    this.gamePanel = true;
                }
            } else {
            this.backImage = sbackImage != null ?
                    ImageUtils.operateImage(componentContainer, componentContainer.getResource()) :
                    null;        
            }
            this.topColor = container.getResource().getTopColorId();
            this.backColor = container.getResource().getBackgroundColorId() != null ? 
                    componentContainer.getResource().getBackgroundColorId() :
                    Colors.getColor(Colors.transparentColor);   
            
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Update + Kresliace metody ">       
    /**
     * Metoda reconstructComponent ma za ulohu zrekonstruovat komponentu podla 
     * resource z ktoreho komponenta cerpa. Na ziskanie resource potrebujeme aby
     * componentContainer nebol null. Ostatne casti su rovnake ako v konstruktore
     * @see SwingImagePanel#SwingImagePanel(rpgcraft.panels.components.Container, rpgcraft.plugins.AbstractMenu) 
     */
    @Override
    protected void reconstructComponent() {        
        if (componentContainer != null) {
            String sbackImage = componentContainer.getResource().getBackgroundTextureId();
            if (sbackImage != null && sbackImage.equals("GAME")) {
                if (menu instanceof GameMenu) {
                    this.gamePanel = true;                    
                }
                this.backImage=  null;
            } else {
            this.backImage = sbackImage != null ?
                    ImageUtils.operateImage(componentContainer, componentContainer.getResource()) :
                    null;        
            }
            this.topColor = componentContainer.getResource().getTopColorId();
            this.backColor = componentContainer.getResource().getBackgroundColorId() != null ? 
                    componentContainer.getResource().getBackgroundColorId() :
                    Colors.getColor(Colors.transparentColor);    
            
        }
        this.changed = true;
    }
    
    /**
     * Override metoda paintComponent s parametrom <b>Graphics</b> (graficky kontext pre komponentu)
     * prekresluje komponentu tak ze najprv vyplni celu komponentu farbou <b>backColor</b> zadanou
     * pri konstrukcii komponenty. Nasledne skontroluje ci je <b>backImage</b> prazdny kontajner s obrazkom. <br>
     * - Ked nie tak bol obrazok zadany v xml subore s resource. Ked doslo k zmene komponenty
     * tak prepocitame pozicie obrazku a vykreslime obrazok podla tychto pozicii. <br>
     * - Ked ano tak neexistuje bud ziadny obrazok => cierne pole, alebo doslo k pripadu,
     * ze vykreslujeme samotnu hru (viz. Konstruktor). Na vykreslenie hry pouzivame 
     * metodu paintMenu definovanu v menu v ktorom sa komponent nachadza. <br>
     * Pri oznaceni komponenty bude cely graficky kontext prekresleny tmavsim odstienom farby.
     * @param g Graficky kontext komponenty
     */
    @Override
    public void paintComponent(Graphics g) {       
        // Kontrola Threadu !!
        //System.out.println(Thread.currentThread().getName());
        //System.out.println("panel " + Framer.debugint);
        g.setColor(backColor);          
        g.fillRect(0, 0, getWidth(), getHeight());        
        
        if (topColor != null) {
            g.setColor(topColor);            
            g.fillRect(wGap, hGap, getWidth(), getHeight());        
        }
        
        if (backImage != null) {
            if (changed) {
                rpos = MathUtils.getStartPositions(componentContainer.getResource().getPosition(), getWidth(), getHeight(),
                        backImage.getWidth(null), backImage.getHeight(null));
                changed = false;
            }
        
            g.drawImage(backImage, rpos[0], rpos[1], null); 
        } else {
            if (gamePanel) {
                menu.paintMenu(g);
            }
        }
        
        if (isSelected) {
            g.setColor(Colors.getColor(Colors.selectedColor));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        
    }    
    
    /**
     * Metoda ktora spravi refresh komponenty podla toho ako je komponenta usadena
     * v ostatnych rodicovskych kontajnerov. Na refresh pozicii pouzivame metodu refreshPositions.
     * Sirka a vyska komponenty je urcena podla toho ci ma byt komponenta predlzovana automaticky
     * alebo ma pevne dlzky. Pri automatickom predlzovanie skontrolujeme velkost
     * vsetkych vnutornych komponent v tomto paneli a s porovnanim s dlzkami obrazku
     * urcime ake ma komponenta dlzky. Oproti inym refresh metodam v inych komponentach
     * je navyse doplnena o kontrolovanie a nastavenie pozicii pre bezpozicne komponenty ktore sme nahromadili pri
     * refreshovani vnutornych komponent. Bezpozicne komponenty su take, ktore
     * sme nemohli urcit lebo boli odkazane na sirky a vysky panelu (FILL_PARENT).
     * Po urceni bezpozicnych komponent ich vymazeme z kontajneru.
     */
    @Override
    public void refresh() { 
        super.refresh();                
        
        w = h = 0;
                        
        if (componentContainer.getChildContainer() != null) {
            for (Container cont : componentContainer.getChildContainer()) {
                if (componentContainer.isAutoWidth()) {
                    w += cont.getWidth();
                }
                if (componentContainer.isAutoHeight()) {
                    h += cont.getHeight();
                }
            }                
        }
        
        w = componentContainer.isAutoWidth() ? (w > backImage.getWidth(null) ? w : 
                backImage.getWidth(null)) : componentContainer.getWidth();
        h = componentContainer.isAutoHeight() ? ((h > backImage.getHeight(null) ? h :
                backImage.getHeight(null))) : componentContainer.getHeight();
                
        //System.out.println("Resizing :" + this);
        //setSize(w, h);
        //setPreferredSize(new Dimension(w,h));
        //System.out.println("Size :" + this);
        componentContainer.set(w, h);
        
        if (componentContainer.getParentContainer().isAutoWidth() || componentContainer.getParentContainer().isAutoHeight()) {  
            LOG.log(Level.INFO, StringResource.getResource("_rshabort"));
            componentContainer.getParentContainer().addPositionslessCont(componentContainer);
            return;
        }
        
        // startovacia pozicia pre vykreslenie resource do rodicovskeho kontajneru          
            
        refreshPositions(w, h, componentContainer.getParentWidth(), componentContainer.getParentHeight());
        reconstructComponent();

        
        if (componentContainer.getPositionslessCont() != null) {
            for (Container cont : componentContainer.getPositionslessCont()) {
                LOG.log(Level.INFO, StringResource.getResource("_rshcontinue", new String[] {cont.getResource().getId()}));
                cont.getComponent().refreshPositions(cont.getWidth(), cont.getHeight(), w, h);
            }
            componentContainer.clearPositionsless();
        }  
        
    }  
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
     /**
     * Override metoda zo AWT ktora nastavi pozadie komponenty podla farby.
     * Ked ma komponenta definovany kontajner tak zoberie farbu z neho, inak 
     * pouzije metodu z predka.
     * @param color Farba ako sa ma zafarbit pozadie
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
    
    // <editor-fold defaultstate="collapsed" desc=" Copy ">
    /**
     * <i>{@inheritDoc }</i>
     * @param cont Kontajner ktory priradujeme novej komponente
     * @param menu Menu ktory priradujeme novej komponente
     * @return Novu SwingImage komponentu
     */
    @Override
    public SwingImagePanel copy(Container cont, AbstractMenu menu) {
        SwingImagePanel result = new SwingImagePanel();          
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
