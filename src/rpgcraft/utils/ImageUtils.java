/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.graphics.ImageOperation;
import rpgcraft.manager.PathManager;
import rpgcraft.panels.components.Container;
import rpgcraft.resource.ImageResource;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.UiResource;


/**
 * Utility trieda ktora v sebe zdruzuje rozne metody pre pracu s obrazkami. Trieda je cela staticka =>
 * mozne k nej pristupovat z kazdej inej triedy ci instancie. 
 * Metody dokazy rotovat obrazky, prekreslovat obrazky v kontajnery, vytvarat aktualny
 * obrazok okna ci menit velkosti obrazku
 */
public class ImageUtils {
    // Uchovavanie docasnych obrazko.
    /**
     * Hashmapa s ulozenymi docasnymi obrazkami
     */
    private static HashMap<String, Image> images = new HashMap<>(); 
    /**
     * Logger pre Utilitu
     */
    private static final Logger LOG = Logger.getLogger(ImageUtils.class.getName());
    /**
     * Velkost thumb obrazku
     */
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
        cont.setImage(new BufferedImage(cont.getWidth(), cont.getHeight(), BufferedImage.TYPE_INT_ARGB));        

        if (resource.getBackgroundTextureId() != null) {
            Image img = ImageResource.getResource(resource.getBackgroundTextureId()).getBackImage();

            // startovacia pozicia pre vykreslenie obrazku do resource -- relativne ku kontaineru
            int[] _ipos = MathUtils.getStartPositions(resource.getImagePosition(), cont.getWidth(), cont.getHeight(), img.getWidth(null), img.getHeight(null));        

            cont.getImage().getGraphics().drawImage(rotatedImage(resource, resource.getImageOrientation()),
            _ipos[0], _ipos[1], null);
            // vykresli obrazok so vsetkymi prvkami
        } 
    
    }
    
    /**
     * Metoda ktora vykona prekresluje obrazok v kontajneri. Obrazok ziskame z resource,
     * po urceni pozicii vykreslime obrazok priamo do grafickeho kontextu podla ziskanych
     * pozicii.     
     * @param g Graficky kontext
     * @param resource UiResource kontajneru
     * @param cont Kontajner
     * @param pos Nepouzite pozicie. Do uvahy berieme nami vytvorene.
     * @deprecated Nepouzivane pre vykreslovani komponent skrz awt vykreslovanie
     */
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
            return getImage(res.getBackgroundTextureId()) == null ? makeTemporaryImage(res.getBackgroundTextureId(),orientation) : 
                    getImage(res.getBackgroundTextureId());
        } else {
            return ImageResource.getResource(res.getBackgroundTextureId()).getBackImage();
        }
    }
    
    /**
     * Metoda ktora vykona vykreslovanie obrazku v kontajneri podla modu zadanom v resource <b>res</b>.
     * Pri normalnom vykreslovani a nenulovom obrazku v kontajneri musime vykreslovat obrazok priamo do grafickeho kontextu
     * <b>g</b>. Pri nulovom obrazku najprv vykreslime obrazok do kontajneru a nasledne do grafickeho kontextu.
     * V takomto pripade beriem do uvahy pozicie <b>pos</b>
     * Pri type OVERLAP vykreslujeme kontajnerovy obrazok do grafickeho kontextu podla 
     * pozicii ziskanych v metode overPaintBackground.
     * @param g Graficky kontext
     * @param res UiResource kontajneru
     * @param cont Kontajner
     * @param pos Pozicie
     * @deprecated Nepouzivane pre vykreslovani komponent skrz awt vykreslovanie
     */
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
     * @see ImageOperation
     */
    public static Image makeTemporaryImage(String name,int o) {
        ImageOperation io = ImageOperation.getInstance();
        io.setOrigImage(ImageResource.getResource(name).getBackImage());
        io.createBufferedImages(BufferedImage.TYPE_INT_ARGB);
        io.rotate(o);
        return newImage(name,io.getShowImg());  
        
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
    
    /**
     * Metoda operateImage spracovava obrazok zadany parametrom <b>img</b>. Rotujeme
     * ho podla parametru <b>o</b> a zvacsujeme podla velkosti urcenej v poli <b>resize</b>.
     * Na tieto operaciu sluzi trieda ImageOperation.
     * @param img Obrazok ktory prerabame
     * @param o Stupne na zrotovanie
     * @param resize Pole s velkostmi na zvacsenie
     * @return Obrazok s nastavenymi parametrami.
     */
    public static BufferedImage operateImage(Image img, int o, int[] resize) {
        if (img != null) {
            ImageOperation io = ImageOperation.getInstance();
            io.setOrigImage(img);
            io.createBufferedImages(BufferedImage.TYPE_INT_ARGB);
            if (o > 0) {                
                io.rotate(o);
            }
            if (resize !=null) {
                if (resize.length == 2) {
                    int w = resize[0];
                    int h = resize[1];
                    if (w != img.getWidth(null) || h != img.getHeight(null)) {                          
                        io.betterresizeImage(w, h, BufferedImage.TYPE_INT_ARGB);                    
                    }
                }
            }
            return io.getShowImg();
        }
        return null;
    }

    /**
     * Metoda ktora vytvori maly obrazok z aktualneho obrazku aplikacie velkosti THUMBSIZE.
     * @param comp Komponenta z ktorej ziskame obrazok
     * @return Obrazok s vytvorenym thumbnail komponenty.
     */
    public static BufferedImage makeThumbnailImage(Component comp) {
        return operateImage(getScreenImage(comp),0, THUMBSIZE);
    }
    
    /**
     * Metoda ktora vytvori maly obrazok velkosti THUMBSIZE z obrazku zadaneho parametrom <b>image</b>
     * @param image Obrazok z ktoreho spravime maly obrazok
     * @return Zmenseny obrazok
     */
    public static BufferedImage makeThumbnailImage(Image image) {
        return operateImage(image, 0, THUMBSIZE);
    }
    
    /**
     * Metoda ktora ziska obrazok z komponenty zadanej parametrom <b>comp</b>.
     * O vykreslenie sa postara metoda getScreenImage s 2 parametrami.
     * @param comp Komponenta zktorej ziskavame obrazok
     * @return Zmenseny obrazok komponenty
     */
    public static BufferedImage getScreenImage(Component comp) {
        
        Dimension d = comp.getSize();
        
        if (d.width == 0||d.height == 0) {
            d = comp.getPreferredSize();
            comp.setSize(d);
        }
            
        Rectangle region = new Rectangle(0, 0, d.width, d.height);
        return getScreenImage(comp, region);        
    }
    
    /**
     * Metoda ktora vrati obrazok podla komponenty. Region urcuje odkial v obrazku vykreslujeme
     * komponentu. Na vykreslenie do obrazku pouzijeme metodu paint pri komponente.
     * @param comp Komponenta ktoru vykreslujeme
     * @param region Region ktory urcuje odkial vykreslujeme.
     * @return Obrazok s vykreslenou komponentou
     */
    public static BufferedImage getScreenImage(Component comp, Rectangle region) {
        BufferedImage image = new BufferedImage(region.width, region.height, BufferedImage.TYPE_INT_ARGB);   
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
        
    /**
     * Metoda ktora vrati obrazok podla mena ktory vystupuje ako kluc v hash mape images.
     * @param image Kluc obrazku ktory chceme
     * @return Obrazok z hashmapy
     */
    public static Image getImage(String image) {
        return images.get(image);
    }
    
    /**
     * Metoda ktora prida novy obrazok do hashmapy.
     * @param name Kluc pod ktorym bude obrazok vystupovat v hashmape
     * @param image Obrazok pod urcitym klucom
     * @return Obrazok ktory sme pridali
     */
    public static Image newImage(String name, Image image) {
        images.put(name, image);
        return image;
    }
    
    /**
     * Metoda ktora vymaze vsetky obrazky z hashmapy pre docasne ukladanie.
     */
    public static void removeAllImages() {
        images.clear();
    }
    
    /**
     * Metoda ktora vymaze obrazok z hashmapy
     * @param name Meno obrazku ktory vymazavame
     * @return True/false ci bol pod existuje takato dvojica
     */
    public static boolean deleteImage(String name) {
        return images.remove(name)!=null;
    }
    
    /**
     * Metoda ktora zapise zadany obrazok <b>image</b> na disk pod menom <b>name</b>.
     * Vypisujeme ho do nastaveneho root suboru.
     * @param name Meno suboru s obrazkom
     * @param image Obrazok na zapisanie.
     */
    public static void writeImageToDisc(String name, BufferedImage image) {        
        try {
            ImageIO.write(image, "PNG", new File(PathManager.getInstance().getRootPath(),name + ".png"));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }
    
}
