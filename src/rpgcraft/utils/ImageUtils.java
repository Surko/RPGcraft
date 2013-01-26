/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.utils;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import rpgcraft.graphics.ImageOperation;
import rpgcraft.graphics.Images;
import rpgcraft.panels.components.Container;
import rpgcraft.resource.ImageResource;
import rpgcraft.resource.UiResource;

/**
 *
 * @author Surko
 */
public class ImageUtils {
    
    
    
    /**
     * Metoda ktora vykresluje sposobom "kazdy resource v stvoci", vykresli obrazok na pozadi definovany v resource zadanom parametrom resource.
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
    
    public static BufferedImage operateImage(Image img, int o, int[] resize) {
        if (img != null) {
            ImageOperation io = ImageOperation.getInstance();
            io.setOrigImage(img);
            io.createBufferedImages(BufferedImage.TYPE_4BYTE_ABGR);
            if (o > 0) {                
                io.rotate(o);
            } else {
                return (BufferedImage)img;
            }
            if (resize !=null) {
                if (resize.length == 2) {
                    int w = resize[0];
                    int h = resize[1];
                    io.betterresizeImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);                    
                }
            }
            return io.getShowImg();
        }
        return null;
    }
    
    public static BufferedImage imageFromContainer(Container cont) {
        return null;
    }
    
}
