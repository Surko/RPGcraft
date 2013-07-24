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
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Container;
import rpgcraft.resource.StringResource;
import rpgcraft.utils.ImageUtils;

/**
 * Trieda SwingImage vytvara komponentu ktora sa sklada iba zo samotneho obrazku.
 * Zatial nema az take vyuzitie kedze podobneho ucinku dosiahneme aj vytvorenim panelu
 * ktoremu nastavime pozadie. Pre buduce moznosti hu tu ale ponechavam.
 * @see SwingComponent
 */
public class SwingImage extends SwingComponent {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    /**
     * Logger pre Obrazok
     */
    private static final Logger LOG = Logger.getLogger(SwingImage.class.getName());
    
    /**
     * Vrchna a spodna farba pozadia
     */
    protected Color topColor, backColor;
    /**
     * Obrazok ktory vykreslujeme
     */
    protected Image backImage;   
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Prazdny konstruktor ktory vytvori instanciu SwingImage.
     */
    public SwingImage() { }
    
    /**
     * Konstruktor ktory vytvori novu instanciu SwingImage z kontajneru <b>cont</b>
     * ktory sa nachadza v menu <b>menu</b>. Kedze je komponenta o obrazkoch tak
     * inicializujeme obrazok a prednu a zadnu farbu za obrazkom.
     * @param container Kontajner v ktorom sa nachadza komponenta
     * @param menu Abstraktne menu v ktorom mame tuto komponentu
     */
    public SwingImage(Container container, AbstractMenu menu) {
        if (componentContainer != null) {
            String sbackImage = componentContainer.getResource().getBackgroundTextureId();
            
            this.backImage = sbackImage != null ?
                    ImageUtils.operateImage(componentContainer, componentContainer.getResource()) :
                    null;        
            
            this.topColor = componentContainer.getResource().getTopColorId();
            this.backColor = componentContainer.getResource().getBackgroundColorId() != null ? 
                    componentContainer.getResource().getBackgroundColorId() :
                    Colors.getColor(Colors.transparentColor);    
            
        }
        this.changed = true;
    }                
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Update + Kresliace metody ">
    /**
     * <i>{@inheritDoc }</i>
     * <p>
     * Rekonstruujeme nastavenim obrazka v pozadi, vrchnej a spodnej farby za obrazkom.
     * </p>
     */
    @Override
    protected void reconstructComponent() {
        if (componentContainer != null) {
            String sbackImage = componentContainer.getResource().getBackgroundTextureId();
            
            this.backImage = sbackImage != null ?
                    ImageUtils.operateImage(componentContainer, componentContainer.getResource()) :
                    null;        
            
            this.topColor = componentContainer.getResource().getTopColorId();
            this.backColor = componentContainer.getResource().getBackgroundColorId() != null ? 
                    componentContainer.getResource().getBackgroundColorId() :
                    Colors.getColor(Colors.transparentColor);    
            
        }
        this.changed = true;
    }
    
    /**
     * Metoda ktora vykresli komponentu do grafickeho kontextu <b>g</b>. Najprv
     * vykreslujeme farbu uplne v pozadi, potom farbu v popredi a nasledne az obrazok
     * ktory je dany v komponente.
     * @param g Graficky kontext.
     */
    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(backColor);          
        g.fillRect(0, 0, getWidth(), getHeight());        
        
        if (topColor != null) {
            g.setColor(topColor);            
            g.fillRect(5, 5, getWidth(), getHeight());        
        }
        
        if (backImage != null) {                    
            g.drawImage(backImage, 0, 0, null); 
        }
        
        if (isSelected) {
            g.setColor(Colors.getColor(Colors.selectedColor));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }        

    /**
     * Metoda ktora vykona refresh komponenty. Ked su dlzky komponenty nastavene na
     * auto tak vratim dlzky obrazku. Na refresh pozicii pouzivame metodu refreshPositions.
     */
    @Override
    public void refresh() {
        super.refresh();        
        int _w = 0, _h = 0;                                
                        
        _w = componentContainer.isAutoWidth() ? backImage.getWidth(null) : componentContainer.getWidth();
        _h = componentContainer.isAutoHeight() ? backImage.getHeight(null) : componentContainer.getHeight();
        
        //setSize(_w, _h);
        componentContainer.set(_w, _h);
         
        // startovacia pozicia pre vykreslenie resource do rodicovskeho kontajneru            
        if (componentContainer.getParentContainer().isAutoWidth() || componentContainer.getParentContainer().isAutoHeight()) {  
            LOG.log(Level.INFO, StringResource.getResource("_rshabort"));
            componentContainer.getParentContainer().addPositionslessCont(componentContainer);
            return;
        }

        // startovacia pozicia pre vykreslenie resource do rodicovskeho kontajneru          
         
        refreshPositions(_w, _h, componentContainer.getParentWidth(), 
            componentContainer.getParentHeight());  
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
    public Component copy(Container cont, AbstractMenu menu) {
        SwingImage result = new SwingImage();          
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
