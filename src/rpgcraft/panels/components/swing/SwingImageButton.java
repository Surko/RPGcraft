/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components.swing;

   
import java.awt.*;  
import java.awt.image.BufferedImage;
import rpgcraft.graphics.ImageOperation;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.resource.ImageResource;
import rpgcraft.panels.components.Container;
   
/**
 * Trieda SwingImageButton dediaca od SwingCustomButton nam zabezpecuje ze komponenta
 * bude sluzit ako tlacidlo. V triede dodefinovavame metodu repaintBtnContent,
 * ktora meni farbu pozadia komponenty. Na zmeny nam sluzi nami vytvorena trieda ImageOperation,
 * ktora sa dokaze o zmeny postarat. 
 * @see SwingCustomButton
 * @see ImageOperation
 */
public class SwingImageButton extends SwingCustomButton {  
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    protected Image img; 
    protected Color backColor;
    private float contrast;
    protected ImageOperation io;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Prazdny konstruktor pre vytvorenie instancie SwingImageButton.
     */
    protected SwingImageButton() {        
    }
    
    
    /**
     * Konstruktor ktory vytvori instanciu SwingImageButton podla danych parametrov.
     * Container v parametroch urcuje kde sa nachadza komponenta. Abstraktne menu
     * zas v akom menu pouzivame komponentu. Konstruktor zinicializuje obrazok v pozadi
     * a instanciu ImageOperation pre vykonavanie operacii nad obrazkom v tejto komponente.
     * @param container Kontajner v ktotom je tlacidlo
     * @param menu Menu v ktorom pouzivame komponentu
     */
    public SwingImageButton(Container container, AbstractMenu menu){        
        super(container, menu);
        if (container != null) {
            this.img = ImageResource.getResource(componentContainer.getResource().getBackgroundTextureId()).getBackImage();
            io = new ImageOperation(img);
            io.createBufferedImages(BufferedImage.TYPE_INT_RGB);        
            io.cropBufferedImage(0,0,getWidth(),getHeight());
            repaintBtnContent();
        }
    }  
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Update + Kresliace metody ">
    /**
     * <i>{@inheritDoc }</i>
     * <p>
     * Rekonstruujeme nastavenim obrazka v pozadi a nastavenim ImageOperation kedze 
     * tuto triedu budeme pouzivat pre zmeny pri stlaceni.
     * </p>
     */
    @Override
    protected void reconstructComponent() {
        super.reconstructComponent();
        if (componentContainer != null) {
            this.img = ImageResource.getResource(componentContainer.getResource().getBackgroundTextureId()).getBackImage();
            io = new ImageOperation(img);
            io.createBufferedImages(BufferedImage.TYPE_INT_RGB);        
            io.cropBufferedImage(0,0,getWidth(),getHeight());
            repaintBtnContent();
        }
    }
    
    /**
     * Metoda ktora vykresli komponentu do grafickeho kontextu. Vykreslujeme najprv 
     * farbu pozadia a nasledne obrazok ktory tvori tlacidlo. Nakonci musime vypisat
     * aky text sa ma v tlacidle ukazat. Vykreslujeme ho do prostriedky.
     * @param g 
     */
    @Override
    public void paintComponent(Graphics g) {    
        g.setColor(backColor);
        g.fillRect(0, 0, getWidth(), getHeight());        
        
        g.drawImage(img, 2, 2, null);
        
        g.setColor(Color.black);
        g.setFont(getFont());
        g.drawString(title, (getWidth() - tw)/2, th);
    }

    /**
     * <i>{@inheritDoc }</i>
     * <p>
     * Override metoda zo SwingCustomButton ktora vzdy musi byt v implementacii tlacidla.
     * Metoda je volana pri volani eventov stlacenia mysi, pricom ma za ulohu zmenit tlacidlo (farbu obrazka/pozadia)
     * pri takychto eventoch. Metoda je final pre udrzanie rovnakeho vyzoru pri dedeni
     * od tejto triedy.
     * </p>
     */
    @Override
    public final void repaintBtnContent() {
        if (hit==true) {            
            contrast = -50f;
            io.rescale(1f, contrast);
            img = io.getShowImg();
            backColor = Color.darkGray;
        } else {
            contrast = 20f;
            io.rescale(1f, contrast); 
            img = io.getShowImg();
            backColor = Color.lightGray;
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Copy ">
     /**
     * <i>{@inheritDoc }</i>
     * @param cont Kontajner ktory priradujeme novej komponente
     * @param menu Menu ktory priradujeme novej komponente
     * @return Novu SwingImageButton komponentu
     */
    @Override
    public rpgcraft.panels.components.Component copy(Container cont, AbstractMenu menu) {
        SwingImageButton result = new SwingImageButton();     
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
