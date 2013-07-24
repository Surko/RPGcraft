/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import rpgcraft.errors.MissingFile;
import rpgcraft.graphics.ImageOperation;
import rpgcraft.manager.PathManager;
import rpgcraft.xml.ImagesXML;

/**
 * ImageResource ktore dedi od AbstraktnehoResource je trieda ktora umoznuje
 * vytvorit obrazkove resources z xml suborov, ktore sa daju v dalsich resource podporujuce obrazky pouzit
 * ako mozne obrazky. Na vytvorenie nam pomaha metoda parse,
 * ktorej predavame jeden vrchol z xmlka na rozparsovanie. Vytvaranie noveho resource
 * prevadzame volanim metody newBundledResource. Navrat nejakeho resource pomocou metody
 * getResource. 
 * @see AbstractResource
 */
public class ImageResource extends AbstractResource<ImageResource>{        
    // <editor-fold defaultstate="collapsed" desc=" Pomocne triedy/enumy ">
    public interface ImageLengths {
        public static final String ORIG = "ORIG";
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    private static HashMap<String, ImageResource> imageResources = new HashMap<>();
    
    private String id;
    
    private int x, y, w, h, globalW, globalH;
    private Image backImage, topImage;
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Konstruktor (privatny) ktory vytvori instanciu ImageResource z xml suboru.
     * Prvy element ktory parsujeme/prechadzame je zadany v parametri <b>elem</b>.
     * Po rozparsovani zvalidujeme obrazok a vlozime ho do listu k dalsim.
     * @param elem Xml element z ktoreho vytvarame resource
     */
    private ImageResource(Element elem) {
        parse(elem); 
        validate();
        imageResources.put(id, this);
    }
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Staticke metody ">
    /**
     * Metoda ktora vytvori novy objekt typu ImageResource. Kedze je staticka a public
     * tak sa tymto padom stava jediny sposob ako vytvorit instanciu ImageResource.
     * @param elem Element z ktoreho vytvarame obrazkovy resource
     * @return ImageResource z daneeho elementu
     */
    public static ImageResource newBundledResource(Element elem) {
        return new ImageResource(elem);                
    }     
    
    /**
     * Metoda ktora vrati ImageResource podla parametru <b>name</b>.
     * @param name Meno obrazkoveho resource ktory hladame (meno je id v xml)
     * @return ImageResource s danym menom
     */
    public static ImageResource getResource(String name) {
        return imageResources.get(name);
    }            
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Parsovanie ">
    /**
     * Metoda ktora rozparsuvava xml subor takym sposobom ze dostava ako parameter <b>elem</b>
     * co je jeden elem z xml suboru aj so vsetkymi jeho podelementami. Ulohou je prejst vsetky
     * tieto podelementy a podla mien tychto podelemntov vykonat definovane akcie.
     * (ImageXML.BTEXTURE -> rozparsuje to co je medzi tagmi. Z informacii ziska obrazok nacitanim
     * do resource.)
     * @param elem Element z xml ktory rozparsovavame do ConversationGroupResource
     */
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

                            io.createBufferedImages(BufferedImage.TYPE_INT_ARGB);        
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

                            io.createBufferedImages(BufferedImage.TYPE_INT_ARGB);        
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
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora vrati x-ovu poziciu v sheete
     * @return X-ova pozicia v sheete
     */
    public int getX() {
        return x;        
    }
    
    /**
     * Metoda ktora vrati y-ovu poziciu v sheete
     * @return Y-ova pozicia v sheete
     */
    public int getY() {
        return y;
    }
    
    /**
     * Metoda ktora vrati sirku obrazku
     * @return Sirka obrazku
     */
    public int getWidth() {
        return w;
    }
    
    /**
     * Metoda ktora vrati vysku obrazku
     * @return Vyska obrazku
     */
    public int getHeight() {
        return h;
    }
    
    /**
     * Metoda ktora vrati obrazok tohoto resource
     * @return Zadny obrazok resource
     */
    public Image getBackImage() {
        return backImage;                
    }
    
    /**
     * Metoda ktora vrati predny obrazok tohoto resource
     * @return Predny obrazok resource
     */
    public Image getOnTopImage() {
        return topImage;
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Pomocne metody ">
    
    /**
     * Metoda ktora okopiruje image resource z parametra <b>res</b> do tohoto resource.
     * @param res Resource z ktoreho kopirujeme
     * @throws Exception Chyba pri kopirovanie
     */
    @Override
    protected void copy(ImageResource res) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Validacna metoda ktora zvaliduje resource aby bol pouzitelny.
     */
    private void validate() {
        
    }
 
    // </editor-fold>
}
