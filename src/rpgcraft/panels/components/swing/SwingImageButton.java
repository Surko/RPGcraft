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
   
public class SwingImageButton extends SwingCustomButton {  
    protected Image img; 
    protected Color backColor;
    private float contrast;
    protected ImageOperation io;
    
    protected SwingImageButton() {        
    }
    
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
     * Override metoda zo SwingCustomButton ktora vzdy musi byt v implementacii tlacidla.
     * Metoda je volana pri volani eventov stlacenia mysi, pricom ma za ulohu zmenit tlacidlo (farbu obrazka/pozadia)
     * pri takychto eventoch.     
     */
    @Override
    public void repaintBtnContent() {
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
    
   
}
