/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.utils;

import java.awt.Image;
import rpgcraft.panels.components.Container;
import rpgcraft.resource.UiResource;
import rpgcraft.resource.UiResource.UiSize;

/**
 * Utility trieda ktora v sebe zdruzuje rozne metody na matematicke ukony. Trieda je cela staticka =>
 * mozne k nej pristupovat z kazdej inej triedy ci instancie. 
 * Metoda dokaze urcity pozicie komponent/kontajnerov, ziskat specificke dlzky tychto komponent
 * ci premienat radiany na stupne.
 */
public class MathUtils {
    /**
     * Metoda getStartPositions vrati pole s dvoma hodnotami (x-ova, y-ova pozicia),
     * od ktorej vykreslujeme nejaky resource. Hodnoty w1 a h1 je dlzka a vyska
     * otcovskeho kontajneru, pricom w2 a h2 toho mensieho
     * @param w1 Dlzka otcovskeho kontajneru v ktorom je resource
     * @param h1 Vyska otcovskeho kontajneru v ktorom je resource
     * @param w2 Dlzka resource
     * @param h2 Vyska resource
     * @return Pozicie od ktorych vykreslujeme
     */
    public static int[] getStartPositions(UiResource.UiPosition position, int w1, int h1, int w2, int h2) {

        int[] positions = new int[2];
        if (position != null) {
            switch (position) {
                case CENTER : {
                    positions[0] = (w1 - w2)/2;
                    positions[1] = (h1 - h2)/2;
                } break;
                case LEFT : {
                    positions[0] = 0;
                    positions[1] = (h1 - h2)/2;
                } break;
                case RIGHT : {
                    positions[0] = w1 - w2;
                    positions[1] = (h1 - h2)/2;
                } break;
                case TOP : {
                    positions[0] = (w1 - w2)/2;
                    positions[1] = 0;
                } break;
                case BOTTOM : {
                    positions[0] = (w1 - w2)/2;
                    positions[1] = h1 - h2;
                } break;
                case TOP_RIGHT: {
                    positions[0] = w1 - w2;
                    positions[1] = 0;
                } break;
                case TOP_LEFT: {
                    positions[0] = 0;
                    positions[1] = 0;
                } break;
                case BOTTOM_LEFT : {
                    positions[0] = 0;
                    positions[1] = h1 - h2;
                } break;
                case BOTTOM_RIGHT : {
                    positions[0] = w1 - w2;
                    positions[1] = h1 - h2;
                } break;
                default : break;
            }
        } else {
            positions[0] = 0;
            positions[1] = 0;
        }      
        return positions;
    }
    
    /**
     * Metoda getStartPositions vrati pole s dvoma hodnotami (x-ova, y-ova pozicia),
     * od ktorej vykreslujeme nejaky resource. Hodnoty w1 a h1 je dlzka a vyska
     * otcovskeho kontajneru, pricom w2 a h2 toho mensieho
     * @param w1 Dlzka otcovskeho kontajneru v ktorom je resource
     * @param h1 Vyska otcovskeho kontajneru v ktorom je resource
     * @param w2 Dlzka resource
     * @param h2 Vyska resource
     * @return Pozicie od ktorych vykreslujeme
     */
    public static int[] getStartPositions(UiResource resource, int w1, int h1, int w2, int h2) {

        int[] positions = getStartPositions(resource.getPosition(), w1, h1, w2, h2);        
        positions[0] += resource.getTransPosX();
        positions[1] += resource.getTransPosY();
        return positions;
    }
    
    /**
     * Metoda ktora vrati specificke dlzky pre komponentu zadanu v kontajneri <b>cont</b>,
     * vytvorenu pomocou UiResource <b>resource</b>. Dlzky urcujeme podla parametru <b>length</b>.
     * Parameter state urcuje ci chceme dlzku alebo vysku komponenty
     * @param resource UiResource z ktoreho je vytvorena komponenta ktorej dlzku chceme
     * @param cont Kontajner v ktorom je komponenta
     * @param length Textova dlzka podla ktorej urcujeme ciselnu dlzku
     * @param minLength Minimalna dlzka ktoru musi mat komponenta
     * @param state Stav ci chceme sirku alebo vysku
     * @return Danu dlzku komponenty
     */
    private static int getSpecificLength(UiResource resource, Container cont, String length, int minLength, boolean state) {
        
        switch (length) {
            
            case UiSize.AUTO : {
                return -1;
            }
            case UiSize.FILL_PARENT : {
                int result;
                if (state) {
                    result = getWidth(resource, cont);
                } else {
                    result = getHeight(resource, cont);
                }
                return minLength > result ? minLength : result;                
            }
            case UiSize.BLANK : {
                return minLength;
            } 
            default : {
                int result = Integer.parseInt(length);
                return minLength > result ? minLength : result;
            }
                
        }
        
    }
    
    /**
     * Metoda getLengths ziskava do pola, ktore vrati ako navratovu hodnotu, dlzky
     * a sirky resource zadaneho ako parameter. Ked je to rodicovsky komponent ( tak 
     * ziska tieto hodnoty z tychto rodicov.
     * @param resource
     * @return Specificke dlzky (width/height) ziskane z resource a kontajneru
     */
    public static int[] getLengths(UiResource resource, Container cont) {
        int[] _lengths = new int[4];

        _lengths[1] = getSpecificLength(resource, cont, resource.getMinWidth(), 0, true);
        _lengths[0] = getSpecificLength(resource, cont, resource.getWidth(), _lengths[1], true);
        _lengths[3] = getSpecificLength(resource,cont, resource.getMinHeight(), 0, false);
        _lengths[2] = getSpecificLength(resource, cont, resource.getHeight(), _lengths[3], false);
        
        return _lengths;
    }
    
    /**
     * Metoda ktora vrati dlzku obrazku zadaneho parametrom <b>img</b>. Typ na urcenie dlzky
     * mame v parametri <b>sWidth</b>. Pri FILL vratime dlzku kontajneru, pri ORIGINAL dlzku obrazku
     * inak vratime ciselnu hodnotu sWidth (v takomto pripade moze ako jediny predstavovat dlzku)
     * @param img Obrazok ktoreho dlzku chceme
     * @param cont Kontajner z ktoreho urcujeme dlzky
     * @param sWidth Textova hodnota dlzka
     * @return Ciselna dlzka obrazku
     * @throws Exception Pri zlom rozparsovani textu
     */
    public static int getImageWidth(Image img, Container cont, String sWidth) throws Exception {
        switch (sWidth) {
            case "FILL" : {
                return cont.getWidth();
            }
            case "ORIGINAL" : {
                return img.getWidth(null);
            }    
            default :
                return Integer.parseInt(sWidth);            
            }
    }
    
    /**
     * Metoda ktora vrati vysku obrazku zadaneho parametrom <b>img</b>. Typ na urcenie vysky
     * mame v parametri <b>sHeight</b>. Pri FILL vratim vysku kontajneru, pri ORIGINAL vysku obrazku
     * inak vratime ciselnu hodnotu sHeight (v takomto pripade moze ako jediny predstavovat vysku)
     * @param img Obrazok ktoreho vysku chceme
     * @param cont Kontajner z ktoreho urcujeme vysku
     * @param sHeight Textova hodnota vysky
     * @return Ciselna vyska obrazku
     * @throws Exception Pri zlom rozparsovani textu
     */
    public static int getImageHeight(Image img, Container cont, String sHeight) throws Exception {
        switch (sHeight) {
            case "FILL" : {
                return cont.getHeight();
            }
            case "ORIGINAL" : {
                return img.getHeight(null);
            }    
            default :
                return Integer.parseInt(sHeight);            
            }
    }    
    
    /**
     * Tato metoda vrati dlzku ziskanu z UiResource zadaneho ako parameter. 
     * Ked je rodic tohoto resource null tak vrati dlzku panelu. Naopak skontroluje
     * ci nie je dlzka rovna fill-parent, tym padom rekurzivne zavola tuto metodu. 
     * V pripade ze nie je rovne retazcu fill-parent tak vyparsuje velkosti.      
     * @param res Resource z ktoreho ziskava udaje
     * @return Dlzku zadaneho resource
     */
    public static int getWidth(UiResource res, Container cont) {            
        return (((res.getParent() == null) || (res.getParent().getWidth().equals(UiResource.UiSize.FILL_PARENT)))
                ? cont.getWidth() : Integer.parseInt(res.getParent().getWidth())); 
    }
    
    /**
     * Tato metoda vrati vysku ziskanu z UiResource zadaneho ako parameter. 
     * Ked je rodic tohoto resource null tak vrati vysku panelu. Naopak skontroluje
     * ci nie je vyska rovna fill-parent, tym padom rekurzivne zavola tuto metodu. 
     * V pripade ze nie je rovne retazcu fill-parent tak vyparsuje velkosti.      
     * @param res Resource z ktoreho ziskava udaje
     * @return Vyska zadaneho resource
     */
    public static int getHeight(UiResource res, Container cont) {
        return (((res.getParent() == null) || (res.getParent().getHeight().equals(UiResource.UiSize.FILL_PARENT)))
                ? cont.getHeight() : Integer.parseInt(res.getParent().getHeight())); 
    }
    
    /**
     * Metoda ktora prekonvertuje radiany na stupne.
     * @param rad Double hodnota v radianoch
     * @return Hodnota radianov v stupnoch.
     */
    public static double radToAngle(double rad) {
        return (rad / Math.PI)*180;
    }
    
}
