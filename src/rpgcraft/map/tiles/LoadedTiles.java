/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.map.tiles;

import java.util.HashMap;
import rpgcraft.resource.TileResource;

/**
 *
 * @author doma
 */
public class LoadedTiles {
    private static HashMap<Integer, Tile> loadedTiles;
    private static LoadedTiles instance;
    
    public LoadedTiles() {
        loadedTiles = new HashMap<>();
    }
    
    public static LoadedTiles getInstance() {
        if (instance == null) {
            instance = new LoadedTiles();
        }
        return instance;
    }
    
    public HashMap<Integer, Tile> createLoadedTiles(HashMap<Integer, Tile> definedTiles) {
        HashMap<Integer, TileResource> resources = TileResource.getResources();
        try {
            for (Integer key : resources.keySet()) {
                if (!definedTiles.containsKey(key)) {
                    Tile newTile = new Tile(key, TileResource.getResource(key));           
                    loadedTiles.put(key, newTile);
                    definedTiles.put(key, newTile);
            }
        }
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        return loadedTiles;
    } 
}
