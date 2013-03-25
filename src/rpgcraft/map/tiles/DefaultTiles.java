/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.map.tiles;

import java.util.HashMap;
import rpgcraft.resource.TileResource;

/**
 *
 * @author Kirrie
 */
public class DefaultTiles {
    
    private static HashMap<Integer, Tile> defaultiles;
    private static DefaultTiles instance;
    
    public static final int GRASS_ID = 1;
    public static final int BLANK_ID = -10;
    public static final int STAIRWUP_ID = -10;
    public static final int STAIRNUP_ID = -11;
    public static final int STAIREUP_ID = -12;
    public static final int STAIRSUP_ID = -13;
    public static final int STAIRWDOWN_ID = -14;
    public static final int STAIRNDOWN_ID = -15;
    public static final int STAIREDOWN_ID = -16;
    public static final int STAIRSDOWN_ID = -17;
    public static final int ROCK_ID = 2;
    public static final int RIBBON_ID = 255;
    
    private DefaultTiles() {
        this.defaultiles = new HashMap<>();
    }      
    
    public static DefaultTiles getInstance() {
        if (instance == null) {
            instance = new DefaultTiles();
        }
        return instance;
    }
    
    public HashMap<Integer, Tile> getDefaultTiles() {
        return defaultiles;
    }
    
    public HashMap<Integer, Tile> createDefaultTiles() {
        try {
        defaultiles.put(RIBBON_ID, new Tile(RIBBON_ID, TileResource.getResource(RIBBON_ID)));
        defaultiles.put(GRASS_ID, new Tile(GRASS_ID, TileResource.getResource(GRASS_ID)));           
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        return defaultiles;
    }   

    
}
