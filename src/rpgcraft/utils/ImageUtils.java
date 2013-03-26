/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.utils;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.graphics.ImageOperation;
import rpgcraft.graphics.Images;
import rpgcraft.panels.components.Container;
import rpgcraft.resource.ImageResource;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.UiResource;

/**
 *
 * @author Surko
 */
public class ImageUtils {
    
    private static final Logger LOG = Logger.getLogger(ImageUtils.class.getName());
    private static final int[] THUMBSIZE = new int[] {128,128};
    
    /**
     * Metoda ktora vykresluje sposobom "kazdy resource v stvorci", vykresli obrazok na pozadi definovany v resource zadanom parametrom resource.
     * Parameter cont (Container) je prislusny container pre tento resource z vlastnostami o 
     * vykreslenom resource do menu (polohy, vysky, otcovske parametre, obrazok). 
     * 
     * @param resource Resource z ktoreho vykreslujem obrazok na pozadi
     * @param cont Kontainer v ktorom sa resource nachadza.
     */
    public static void paintBackgroundImage(UiResource resource, Container cont) {
        cont.setImage(new BufferedImage(cont.getWidth(), cont.getHeight(), BufferedImage.TYPE_4BYTE_ABGR));        

        if (resource.getBackgroundTextureId() != null) {
            Image img = ImageResource.getResource(resource.getBackgroundTextureId()).getBackImage();

            // startovacia pozicia pre vykreslenie obrazku do resource -- relativne ku kontaineru
            int[] _ipos = MathUtils.getStartPositions(resource.getImagePosition(), cont.getWidth(), cont.getHeight(), img.getWidth(null), img.getHeight(null));        

            cont.getImage().getGraphics().drawImage(rotatedImage(resource, resource.getImageOrientation()),
            _ipos[0], _ipos[1], null);
            // vykresli obrazok so vsetkymi prvkami
        } 
    
    }
    
    public static void overpaintBackgroundImage(Graphics g, UiResource resource, Container cont, int[] pos) {
                
        if (resource.getBackgroundTextureId() != null) {
            Image img = ImageResource.getResource(resource.getBackgroundTextureId()).getBackImage();

            // startovacia pozicia pre vykreslenie obrazku do resource
            int[] _ipos = MathUtils.getStartPositions(resource.getImagePosition(), cont.getWidth(), cont.getHeight(), img.getWidth(null), img.getHeight(null));        

            // vykresli obrazok s resource
            g.drawImage(rotatedImage(resource, resource.getImageOrientation()),
            pos[0] + _ipos[0] + cont.getX(), pos[1] + _ipos[1] + cont.getY(), null);
            
        } 
    
    } 
    
    /**
     * Metoda rotatedImage nam vrati obrazok zrotovany o parameter <b>orientation</b>.
     * Ked je orientacia 0 tak vrati obrazok priamo z resource <b>res</b>. Inak skontroluje
     * triedu <b>Images</b> s docasnymi obrazkami pre zhodu a ked taky obrazok 
     * neexistuje tak ho vytvori pomocou metody makeTemporaryImage.
     * @param res Resource z ktoreho ziska obrazok
     * @param orientation Orientacia obrazku
     * @return Zrotovany obrazok o parameter orientation
     */
    public static Image rotatedImage(UiResource res, int orientation) {
        if (orientation > 0) {
            return Images.getImage(res.getBackgroundColorId()) == null ? makeTemporaryImage(res.getBackgroundTextureId(),orientation) : 
                    Images.getImage(res.getBackgroundColorId());
        } else {
            return ImageResource.getResource(res.getBackgroundTextureId()).getBackImage();
        }
    }
    
    public void modePainting(Graphics g, UiResource res, Container cont, int[] pos) {
        Image img = cont.getImage();
        switch (res.getPaintMode()) {
            case NORMAL : {
                if (img != null) {
                    g.drawImage(img, pos[0] + cont.getX(), pos[1] + cont.getY(), null);
                } else {
                    paintBackgroundImage(res, cont);    
                    g.drawImage(cont.getImage(), pos[0] + cont.getX(), pos[1] + cont.getY(), null);                    
                }
            } break;
            case OVERLAP : {
                overpaintBackgroundImage(g, res, cont, pos);                
            } break;
            default : {
                return;
            }                        
        }
    }
    
    /**
     * Metoda ktora vytvori docasny obrazok zmeneny podla urcitych parametrov (
     * v tomto pripade zatial iba rotovany). Posluzia nam nato ImageOperation s metodami ako
     * je napriklad rotate, 
     * ktora dostava ako parameter orientaciu zadanu parametrom o. Metoda ma v mene temporary
     * pretoze tieto obrazky existuju len pocas zivotnosti tohoto Menu.
     * @param name Meno noveho obrazku
     * @param o O kolko bude otoceny obrazok
     * @return Novo inicializovany obrazok z triedy Images
     * @see Images
     * @see ImageOperation
     */
    public static Image makeTemporaryImage(String name,int o) {
        ImageOperation io = ImageOperation.getInstance();
        io.setOrigImage(ImageResource.getResource(name).getBackImage());
        io.createBufferedImages(BufferedImage.TYPE_4BYTE_ABGR);
        io.rotate(o);
        return Images.newImage(name,io.getShowImg());  
        
    }           
    
    /**
     * Metoda operateImage s parametrami container a resource ma za ulohu
     * upravit obrazok podla zadaneho resource a kontajneru v ktorom sa obrazok nachadza.
     * @param container Kontajner podla ktoreho upravujeme obrazok
     * @param res Resource podla ktoreho upravujeme obrazok
     * @return Pozmeneny obrazok podla zadanych parametrov
     */
    public static BufferedImage operateImage(Container container, UiResource res) {
        Image img = ImageResource.getResource(res.getBackgroundTextureId()).getBackImage();
        
        int o = res.getImageOrientation();
        int[] resize = null;
        
        try {
            resize = new int[2];
            resize[0] = MathUtils.getImageWidth(img, container, res.getImageWidth());

            resize[1] = MathUtils.getImageHeight(img, container, res.getImageHeight());
            if (resize[0] == 0 || resize[1] == 0) {
                LOG.log(Level.WARNING, StringResource.getResource("_zimage"));
                return null;
            }
        } catch (Exception e) {
            new MultiTypeWrn(e, Color.RED, "Problem to parse image lengths",null).renderSpecific(StringResource.getResource("_label_parsingerror"));
        }
        
        
        return operateImage(img, o, resize);
                        
    }
    
    public static BufferedImage operateImage(Image img, int o, int[] resize) {
        if (img != null) {
            ImageOperation io = ImageOperation.getInstance();
            io.setOrigImage(img);
            io.createBufferedImages(BufferedImage.TYPE_INT_RGB);
            if (o > 0) {                
                io.rotate(o);
            }
            if (resize !=null) {
                if (resize.length == 2) {
                    int w = resize[0];
                    int h = resize[1];
                    if (w != img.getWidth(null) || h != img.getHeight(null)) {                          
                        io.betterresizeImage(w, h, BufferedImage.TYPE_INT_RGB);                    
                    }
                }
            }
            return io.getShowImg();
        }
        return null;
    }

    public static BufferedImage makeThumbnailImage(Component comp) {
        return operateImage(getScreenImage(comp),0, THUMBSIZE);
    }
    
    public static BufferedImage makeThumbnailImage(Image image) {
        return operateImage(image, 0, THUMBSIZE);
    }
    
    public static BufferedImage getScreenImage(Component comp) {
        
        Dimension d = comp.getSize();
        
        if (d.width == 0||d.height == 0) {
            d = comp.getPreferredSize();
            comp.setSize(d);
        }
            
        Rectangle region = new Rectangle(0, 0, d.width, d.height);
        return getScreenImage(comp, region);        
    }
    
    public static BufferedImage getScreenImage(Component comp, Rectangle region) {
        BufferedImage image = new BufferedImage(region.width, region.height, BufferedImage.TYPE_INT_RGB);   
        Graphics g2d = image.createGraphics();
        
        if (!comp.isOpaque()) {
            g2d.setColor(comp.getBackground());
            g2d.fillRect(region.x, region.y, region.width, region.height);
        }
        
        g2d.translate(-region.x, -region.y);
        comp.paint(g2d);
        g2d.dispose();
        return image;
        
    }
    
    public static BufferedImage imageFromContainer(Container cont) {
        return null;
    }
    
}
