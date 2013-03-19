/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.utils;

import java.awt.Image;
import javax.swing.JPanel;
import rpgcraft.MainGameFrame;
import rpgcraft.panels.components.Container;
import rpgcraft.resource.UiResource;
import rpgcraft.resource.UiResource.UiSize;

/**
 *
 * @author Surko
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
    public static int[] getStartPositions(UiResource.UiPosition position,int w1, int h1, int w2, int h2) {
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
                    positions[1] = h2 - h1;
                } break;
                case BOTTOM_RIGHT : {
                    positions[0] = w1 - w2;
                    positions[1] = h2 - h1;
                } break;
                default : break;
            }
        } else {
            positions[0] = 0;
            positions[1] = 0;
        }      
        return positions;
    }
    
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
     * @return 
     */
    public static int[] getLengths(UiResource resource, Container cont) {
        int[] _lengths = new int[4];

        _lengths[1] = getSpecificLength(resource, cont, resource.getMinWidth(), 0, true);
        _lengths[0] = getSpecificLength(resource, cont, resource.getWidth(), _lengths[1], true);
        _lengths[3] = getSpecificLength(resource,cont, resource.getMinHeight(), 0, false);
        _lengths[2] = getSpecificLength(resource, cont, resource.getHeight(), _lengths[3], false);
        
        return _lengths;
    }
    
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
    
    
    public static double radToAngle(double rad) {
        return (rad / Math.PI)*180;
    }
    
}
