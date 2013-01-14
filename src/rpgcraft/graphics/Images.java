/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics;

import java.awt.Image;
import java.util.HashMap;

/**
 *
 * @author Surko
 */
public class Images {
    
    private static HashMap<String, Image> images = new HashMap<>(); 
        
    public static Image getImage(String image) {
        return images.get(image);
    }
    
    public static Image newImage(String name, Image image) {
        images.put(name, image);
        return image;
    }
    
    public static boolean deleteImage(String name) {
        return images.remove(name)!=null;
    }
}
