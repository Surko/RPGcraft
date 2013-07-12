/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Container;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.types.BarType;
import rpgcraft.utils.DataUtils;

/**
 * Bar komponenta ktora dedi od SwingComponent implementujuc interface Component. Tvori zakladnu triedu pre vytvorenie
 * instancie progress baru. Vykreslenie je volane z metody paint v GamePane.
 * Obsahuje metody pre refresh a rekonstrukciu komponent. Na vykreslenie potrebujeme co
 * za data vykreslujeme. Data ziskavame z BarType pomocou triedy DataUtils. 
 * @see SwingComponent
 */
public class SwingBar extends SwingComponent {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    private static final Logger LOG = Logger.getLogger(SwingBar.class.getName()); 
    
    private Dimension prefferedDim = new Dimension(100,20);
    
    BarType barType;
    Color backColor;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Prazdny konstruktor pre vytvorenie instancie baru.
     */
    public SwingBar() {}
    
    /**
     * Konstruktor ktory vytvori komponentu z kontajneru a priradi jej menu v ktorom
     * bola komponenta vytvorena.
     * @param container Kontajner v ktorom sa nachadza komponenta
     * @param menu Menu v ktorom je komponenta
     */
    public SwingBar(Container container, AbstractMenu menu) {
        super(container, menu);
        
        this.barType = (BarType)container.getResource().getType();                     
        
        this.backColor = container.getResource().getBackgroundColorId();
    }

    // </editor-fold>
       
    // <editor-fold defaultstate="collapsed" desc=" Update + Kresliace metody ">
    /**
     * Metoda ktora vykresli komponentu do grafickeho kontextu zadaneho parametrom <b>g</b>.
     * Najprv si ziskame min a max hodnoty pre data ktore zobrazuje komponenta.
     * Potom vykreslime obdlznik podla dlzky
     * @param g Graficky kontext do ktoreho vykreslujeme komponentu.
     */
    @Override
    protected void paintComponent(Graphics g) {
        
        double min = (double) DataUtils.getData(this, barType.getMinData());
        double max = (double) DataUtils.getData(this, barType.getMaxData());
        
        double modif = min / max;
        
        g.setColor(backColor);
        g.fillRect(0, 0, ((int)(getWidth() * modif)), getHeight());
        
    }        

    /**
     * Metoda ktora spravi refresh komponenty podla toho ako je komponenta usadena
     * v ostatnych rodicovskych kontajnerov. Na refresh pozicii pouzivame metodu refreshPositions.
     * Sirka a vyska komponenty je urcena podla toho ci ma byt komponenta predlzovana automaticky
     * alebo ma pevne dlzky.
     */
    @Override
    public void refresh() {
        super.refresh();
        w = h = 0;
        
        Dimension barDim = prefferedDim;
        
        w = componentContainer.isAutoWidth() ? barDim.width : componentContainer.getWidth();
        h = componentContainer.isAutoHeight() ? barDim.height : componentContainer.getHeight();
        
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
         
    /**
     * <i>{@inheritDoc }</i>
     * <p>
     * Pri rekonstrukcii komponenty je dolezite ziskat barType a zadnu farbu komponenty.
     * Vsetko sa da ziskat z kontajneru v ktorom je komponenta
     * </p>     
     */
    @Override
    protected void reconstructComponent() {        
        
        this.barType = (BarType)componentContainer.getResource().getType();                     
        
        this.backColor = componentContainer.getResource().getBackgroundColorId();
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Copy ">
    /**
     * <i>{@inheritDoc }</i>
     * @param cont Kontajner ktory priradujeme novej komponente
     * @param menu Menu ktory priradujeme novej komponente
     * @return Novu SwingBar komponentu
     */
    @Override
    public Component copy(Container cont, AbstractMenu menu) {
        SwingBar result = new SwingBar();          
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
    
    // <editor-fold defaultstate="collapsed" desc=" Mouse Handling ">
    /**
     * {@inheritDoc }
     * @param e {@inheritDoc }
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        
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
