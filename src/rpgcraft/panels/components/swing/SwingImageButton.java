/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components.swing;

   
import java.awt.*;  
import java.awt.image.BufferedImage;
import rpgcraft.graphics.ImageOperation;
import rpgcraft.panels.AbstractMenu;
import rpgcraft.resource.ImageResource;
import rpgcraft.panels.components.Container;
   
public class SwingImageButton extends SwingCustomButton {  
    private Image img;   
    private float contrast;
    ImageOperation io;
    
    protected SwingImageButton() {        
    }
    
    public SwingImageButton(Container container, AbstractMenu menu){        
        super(container, menu);
        this.img = ImageResource.getResource(componentContainer.getResource().getBackgroundTextureId()).getBackImage();
        io = new ImageOperation(img);
        io.createBufferedImages(BufferedImage.TYPE_INT_RGB);        
        io.cropBufferedImage(4,244,300,20);
    }  
   
    @Override
    protected void reconstructComponent() {
        super.reconstructComponent();
        this.img = ImageResource.getResource(componentContainer.getResource().getBackgroundTextureId()).getBackImage();
        io = new ImageOperation(img);
        io.createBufferedImages(BufferedImage.TYPE_INT_RGB);        
        io.cropBufferedImage(4,244,300,20);
    }
    
    @Override
    public void paintComponent(Graphics g) {    
        Graphics2D g2D = (Graphics2D) g;
        
        if (hit==true) {            
            contrast = -50f;
            io.rescale(1f, contrast);
            g2D.setColor(Color.darkGray);
        } else {
            contrast = 20f;
            io.rescale(1f, contrast); 
            g2D.setColor(Color.lightGray);
        }
        
        g.fillRect(0, 0, getWidth(), getHeight());        
        
        g2D.drawImage(io.getShowImg(), 2, 2, null);
        
        g.setColor(Color.black);
        g.setFont(getFont());
        g.drawString(title, 100, h);
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
