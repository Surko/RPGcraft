/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.map.tiles;

import java.util.HashMap;
import rpgcraft.resource.TileResource;

/**
 * Trieda ktora obsahuje iba staticke metody a ma za ulohu inicializovat 
 * nacitane dlazdice z xml suborov ktore sme rozparsovali a vytvorili z nich TileResource.
 * Nacitane dlazdice si uchovavame v hashmape loadedTiles.
 */
public class LoadedTiles {
    /**
     * Hashmapa s nacitanymi dlazdicami
     */
    private static HashMap<Integer, Tile> loadedTiles;
    /**
     * Instancia LoadedTiles
     */
    private static LoadedTiles instance;        
    
    /**
     * Metoda ktora vytvori vsetky nacitane dlazdice a vsunie ich do hashmapy.
     * Treba volat pred zacatim hlavnej hry. Na ziskanie nacitanych dlazdic zavolame metodu
     * getResources z triedy TileResource, ktora vrati vsetky nacitane TileResource z xml suborov.
     * Kazdu dvojicu potom vsuvame do mapy loadedTiles a do mapy definedTiles, ktora v sebe obsahuje
     * uz nacitane dlazdice (napr. zakladne dlazdice).
     * @param definedTiles Hashmapa uz s nacitanymi dlazdicami
     * @return Hashmapa s dvojicou Integer (id dlazdice) a Tile (dlazdica vytvorena z TileResource) nacitanych dlazdic
     */
    public static HashMap<Integer, Tile> createLoadedTiles(HashMap<Integer, Tile> definedTiles) {
        loadedTiles = new HashMap<>();
        HashMap<Integer, TileResource> resources = TileResource.getResources();
        try {
            for (Integer key : resources.keySet()) {
                if (definedTiles == null || !definedTiles.containsKey(key)) {
                    Tile newTile = new Tile(key, TileResource.getResource(key));           
                    loadedTiles.put(key, newTile);
                    if (definedTiles != null) {
                        definedTiles.put(key, newTile);
                    }
            }
        }
        } catch (Exception e) {
            //System.out.println(e.getLocalizedMessage());
        }
        return loadedTiles;
    } 
}
