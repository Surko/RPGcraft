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
    
    ImageOperation io;
    
    public SwingImageButton(Container container, AbstractMenu menu){                                        
        super(container,menu);
        this.img = ImageResource.getResource(container.getResource().getBackgroundTextureId()).getBackImage();
        io = new ImageOperation(img);
        io.createBufferedImages(BufferedImage.TYPE_INT_RGB);        
        io.cropBufferedImage(4,244,300,20);
    }  
   
    @Override
    public Dimension getPreferredSize(){  
        if (img!=null){  
            return new Dimension(300,20);  
        } else { 
            return new Dimension(300,20);  
        }  
    }  
   
    
    @Override
    public void paintComponent(Graphics g) {    
        Graphics2D g2D = (Graphics2D) g;
        
        if (hit==true) {            
            io.changeContrast(-50f);
            io.rescale();
            g2D.setColor(Color.darkGray);
        } else {
            io.changeContrast(20f);
            io.rescale(); 
            g2D.setColor(Color.lightGray);
        }
        
        g.fillRect(0, 0, getWidth(), getHeight());        
        
        g2D.drawImage(io.getShowImg(), 2, 2, null);
        
        g.setColor(Color.black);
        g.drawString(title, 100, 15);
    }

    
    
   
}
