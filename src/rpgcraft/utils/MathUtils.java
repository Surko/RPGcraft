/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.utils;

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
    
    /**
     * Metoda getLengths ziskava do pola, ktore vrati ako navratovu hodnotu, dlzky
     * a sirky resource zadaneho ako parameter. Ked je to rodicovsky komponent ( tak 
     * ziska tieto hodnoty z tychto rodicov.
     * @param resource
     * @return 
     */
    public static int[] getLengths(UiResource resource, Container cont) {
        int[] _lengths = new int[4];
        if (resource.getWidth() == null) {
            _lengths[0] = getWidth(resource, cont);                                       
            _lengths[1] = resource.getMinWidth() == null ? 0 :
                    (resource.getMinWidth().equals(UiSize.FILL_PARENT) ? _lengths[0] : Integer.parseInt(resource.getMinWidth()));                
        } else {
            _lengths[0] = resource.getWidth().equals(UiSize.FILL_PARENT) ? getWidth(resource, cont)
                : Integer.parseInt(resource.getWidth());            
            _lengths[1] = resource.getMinWidth() == null ? 0 :
                    (resource.getMinWidth().equals(UiSize.FILL_PARENT) ? getWidth(resource, cont) : Integer.parseInt(resource.getMinWidth()));  
        }
        if (resource.getHeight() == null) {
            _lengths[2]= getHeight(resource, cont);
            _lengths[3] = resource.getMinHeight() == null ? 0 :
                    (resource.getMinHeight().equals(UiSize.FILL_PARENT) ? _lengths[2] : Integer.parseInt(resource.getMinHeight())); 
        } else {
            _lengths[2] = resource.getHeight().equals(UiSize.FILL_PARENT) ? getHeight(resource, cont)
                : Integer.parseInt(resource.getHeight());            
            _lengths[3] = resource.getMinHeight() == null ? 0 :
                    (resource.getMinHeight().equals(UiSize.FILL_PARENT) ? getHeight(resource, cont) : Integer.parseInt(resource.getMinHeight())); 
        }
        
        return _lengths;
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
    
    
}
