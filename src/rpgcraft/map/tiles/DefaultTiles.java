/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.map.tiles;

import java.util.HashMap;
import rpgcraft.resource.TileResource;

/**
 * Trieda ktora obsahuje iba staticke metody a  ma za ulohu inicializovat 
 * zakladne dlazdice podla urcitych resourcov.
 * Zakladne dlazdice si uchovavame v hashmape defaultTiles.
 */
public class DefaultTiles {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    private static HashMap<Integer, Tile> defaultTiles;
    private static DefaultTiles instance;
    
    public static final int GRASS_ID = 1;
    public static final int BLANK_ID = 0;
    public static final int ROCK_ID = 2;
    public static final int RIBBON_ID = 255;
    // </editor-fold>           
    
    /**
     * Metoda ktora vrati vsetky zakladne dlazdice ktore boli nacitane
     * @return Hashmapa so vsetkymi zakladnymi dlazdicami
     */
    public static HashMap<Integer, Tile> getDefaultTiles() {
        return defaultTiles;
    }
    
    /**
     * Metoda ktora vytvori vsetky zakladne dlazdice a vsunie ich do hashmapy.
     * Treba volat pred zacatim hlavnej hry
     * @return Hashmapa s dvojicou Integer (id dlazdice) a Tile (dlazdica vytvorena z TileResource)
     */
    public static HashMap<Integer, Tile> createDefaultTiles() {
        defaultTiles = new HashMap<>();
        HashMap<Integer, Tile> returnTiles = new HashMap<>();
        try {
            defaultTiles.put(RIBBON_ID, new Tile(RIBBON_ID, TileResource.getResource(RIBBON_ID)));
            defaultTiles.put(GRASS_ID, new Tile(GRASS_ID, TileResource.getResource(GRASS_ID)));      
            defaultTiles.put(BLANK_ID, new BlankTile(BLANK_ID, null));
        } catch (Exception e) {
            //System.out.println(e);
        }
        returnTiles.putAll(defaultTiles);
        return returnTiles;
    }   

    
}
