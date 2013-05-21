/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.xml.bind.annotation.XmlAccessOrder;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import rpgcraft.errors.MissingFile;
import rpgcraft.graphics.Colors;
import rpgcraft.graphics.ImageOperation;
import rpgcraft.manager.PathManager;
import rpgcraft.xml.ImagesXML;
import rpgcraft.xml.XmlReader;

/**
 *
 * @author doma
 */
public class ImageResource extends AbstractResource<ImageResource>{        
    private static HashMap<String, ImageResource> imageResources = new HashMap<>();
    
    public interface ImageLengths {
        public static final String ORIG = "ORIG";
    }
    
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
                        Image img = ImageIO.read(new File(PathManager.getInstance().getImagePath(),textureName));                          
                        if (globalW == -1 && globalH == -1) {
                            backImage = img;
                        } else {
                            if (globalW == -1) {
                                globalW = img.getWidth(null);
                            }
                            if (globalH == -1) {
                                globalH = img.getHeight(null);
                            }
                            ImageOperation io = new ImageOperation(img);

                            io.createBufferedImages(BufferedImage.TYPE_INT_RGB);        
                            io.cropBufferedImage(x,y,globalW,globalH);
                            io.finalizeOp();
                            backImage = io.getShowImg();
                            //ImageIO.write(io.getShowImg(), "jpg", new File(id + ".jpg"));
                        }
                    } catch(Exception e) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, StringResource.getResource("_mimage", new String[] {id}));
                        new MissingFile(e, StringResource.getResource("_mimage", new String[] {id})).render();
                    }
                } break; 
                case ImagesXML.TTEXTURE : {
                    String textureName = eNode.getTextContent();
                    try {                         
                        Image img = ImageIO.read(new File(PathManager.getInstance().getImagePath(),textureName));  
                        if (globalW == -1 && globalH == -1) {
                            topImage = img;
                        } else {
                            if (globalW == -1) {
                                globalW = img.getWidth(null);
                            }
                            if (globalH == -1) {
                                globalH = img.getHeight(null);
                            }
                            ImageOperation io = new ImageOperation(img);

                            io.createBufferedImages(BufferedImage.TYPE_INT_RGB);        
                            io.cropBufferedImage(x,y,globalW,globalH);
                            io.finalizeOp();
                            topImage = io.getShowImg();
                            //ImageIO.write(io.getShowImg(), "jpg", new File(id + ".jpg"));
                        }
                    } catch(Exception e) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, StringResource.getResource("_mimage", new String[] {id}));
                        new MissingFile(e, StringResource.getResource("_mimage", new String[] {id})).render();
                    }
                } break;     
                case ImagesXML.ID : {
                    //System.out.println(eNode.getTextContent());
                    id = eNode.getTextContent();                    
                } break;
                case ImagesXML.X : {                    
                    //System.out.println(eNode.getTextContent());
                    x = (Integer.parseInt(eNode.getTextContent()));
                } break;
                case ImagesXML.Y : {
                    //System.out.println(eNode.getTextContent());
                    y = (Integer.parseInt(eNode.getTextContent()));                    
                } break;
                case ImagesXML.W : {                    
                    w = (Integer.parseInt(eNode.getTextContent()));
                } break;
                case ImagesXML.H : {
                    h = (Integer.parseInt(eNode.getTextContent()));                    
                } break;
                case ImagesXML.GLOBALW : {
                    //System.out.println(eNode.getTextContent());
                    String content = eNode.getTextContent();
                    switch (content) {
                        case ImageLengths.ORIG : {
                            globalW = -1;
                        } break;
                        default :
                            globalW = Integer.parseInt(eNode.getTextContent());
                    }
                } break;
                case ImagesXML.GLOBALH : {
                    //System.out.println(eNode.getTextContent());
                    String content = eNode.getTextContent();
                    switch (content) {
                        case ImageLengths.ORIG : {
                            globalH = -1;
                        } break;
                        default :
                            globalH = Integer.parseInt(eNode.getTextContent());
                    }
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
