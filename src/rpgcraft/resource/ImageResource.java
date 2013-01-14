/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource;

import java.awt.Image;
import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.xml.bind.annotation.XmlAccessOrder;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import rpgcraft.manager.PathManager;
import rpgcraft.xml.ImagesXML;
import rpgcraft.xml.XmlReader;

/**
 *
 * @author doma
 */
public class ImageResource extends AbstractResource<ImageResource>{        
    private static HashMap<String, ImageResource> imageResources = new HashMap<>();
    
    private String id;
    
    private int x, y, w, h, globalW, globalH;
    private Image backImage, topImage;
    
    public static ImageResource getResource(String name) {
        return imageResources.get(name);
    }    
    
    private ImageResource(Element elem) {
        parse(elem);        
        imageResources.put(id, this);
    }
    
    public static ImageResource newBundledResource(Element elem) {
        return new ImageResource(elem);                
    }      
    
    @Override
    protected void parse(Element elem) {
        
        NodeList nl = elem.getChildNodes(); 
                
        for (int i = 0; i< nl.getLength();i++) {
            Node eNode = nl.item(i);
            switch (eNode.getNodeName()) {   
                case ImagesXML.IMAGE : {
                    parse((Element)eNode);
                    
                }   break;
                case ImagesXML.BTEXTURE : {
                String textureName = eNode.getTextContent();
                try {
                        backImage = ImageIO.read(new File(PathManager.getInstance().getUiPath(),textureName));                       
                    } catch(Exception e) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, StringResource.getResource("_mimage"),id);            
                    }
                } break; 
                case ImagesXML.TTEXTURE : {
                String textureName = eNode.getTextContent();
                try {
                        topImage = ImageIO.read(new File(PathManager.getInstance().getUiPath(),textureName));                       
                    } catch(Exception e) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, StringResource.getResource("_mimage"),id);            
                    }
                } break;     
                case ImagesXML.ID : {
                    System.out.println(eNode.getTextContent());
                    id = eNode.getTextContent();                    
                } break;
                case ImagesXML.X : {                    
                    System.out.println(eNode.getTextContent());
                    x = (Integer.parseInt(eNode.getTextContent()));
                } break;
                case ImagesXML.Y : {
                    System.out.println(eNode.getTextContent());
                    y = (Integer.parseInt(eNode.getTextContent()));                    
                } break;
                case ImagesXML.W : {                    
                    w = (Integer.parseInt(eNode.getTextContent()));
                } break;
                case ImagesXML.H : {
                    h = (Integer.parseInt(eNode.getTextContent()));                    
                } break;
                case ImagesXML.GLOBALW : {
                    System.out.println(eNode.getTextContent());
                    globalW = Integer.parseInt(eNode.getTextContent());
                } break;
                case ImagesXML.GLOBALH : {
                    System.out.println(eNode.getTextContent());
                    globalH = Integer.parseInt(eNode.getTextContent());
                } break;
                default : break;
            }
        }
    }
    
    public int getX() {
        return x;        
    }
    
    public int getY() {
        return y;
    }
    
    public int getWidth() {
        return w;
    }
    
    public int getHeight() {
        return h;
    }
    
    public Image getBackImage() {
        return backImage;                
    }
    
    public Image getOnTopImage() {
        return topImage;
    }

    @Override
    protected void copy(ImageResource res) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
