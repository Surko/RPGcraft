/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.map.generators;

import java.awt.Color;
import rpgcraft.plugins.GeneratorPlugin;
import java.util.ArrayList;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.map.SaveMap;
import rpgcraft.map.chunks.ChunkContent;
import rpgcraft.map.tiles.Tile;
import rpgcraft.resource.StringResource;

/**
 * Trieda MapGenerator ako sucast tvorby terenu je zakladna trieda ktora je volana pri tvorbe terenu.
 * Vzdy pri neexistencii Chunku na mape sa vytvori instancie tejto triedy pomocou konstrukturu a metodou
 * generate sa zavola tvorba terenu podla generatorov ulozenych v statickom liste <b>generators</b>
 * @author Kirrie
 */

public class MapGenerator {        
    
    /**
     * List s generatormi podla ktorych vytvarame mapu.
     */
    public static ArrayList<GeneratorPlugin> generators;
    
    /**
     * X-ove a Y-ove pozicie chunku
     */
    private int x, y;
    /**
     * Hlbka so sirkou mapy aku vytvarame.
     */
    private int depth, size;
                
    /**
     * Samotna mapa ulozena v 3D poli.
     */
    private int[][][] mapArray,metaArray;
    
    /**
     * Konstruktor MapGenerator vytvori novu instanciu triedy MapGenerator vdaka ktorej 
     * nam umoznuje vytvorit nove kusy mapy do hry. 
     * @param size Sirka Chunku na vytvorenie ({@linkplain rpgcraft.map.chunks.Chunk#CHUNK_SIZE})
     * @param depth Hlbka Chunku na vytvorenie ({@linkplain rpgcraft.map.chunks.Chunk#DEPTH})
     * @param x X-ova pozicia generovaneho chunku
     * @param y Y-ova pozicia generovaneho chunku
     */
    public MapGenerator(int size, int depth, int x, int y) {
        this.depth = depth;
        this.size = size;
        this.x = x;
        this.y = y;
        mapArray = new int[depth][size][size];
        metaArray = new int[depth][size][size];
    }                
    
    /**
     * Metoda setTile nastavi hodnotu dlazdice zadanu parametrom <b>value</b> na suradnice takisto zadane parametrmi.
     * 
     * @param z Hlbka kde nastavujeme dlazdicu
     * @param x x suradnica kde nastavujeme dlazdicu
     * @param y y suradnica kde nastavujeme dlazdicu
     * @param value Hodnota dlazdice ktoreu nastavujeme
     */
    public void setTile(int z, int x, int y, int value) {
        mapArray[z][x][y] = value;
        Tile tile = Tile.tiles.get(value);        
        metaArray[z][x][y] = tile == null ? 0 : tile.getHealth();
    }
    
    /**
     * Metoda setTile nastavi meta hodnotu dlazdice zadanu parametrom <b>value</b> na suradnice takisto zadane parametrmi.
     * 
     * @param z Hlbka kde nastavujeme dlazdicu
     * @param x x suradnica kde nastavujeme dlazdicu
     * @param y y suradnica kde nastavujeme dlazdicu
     * @param value Meta hodnota dlazdice ktoreu nastavujeme
     */
    public void setMeta(int z, int x, int y, int value) {
        metaArray[z][x][y] = value;     
    }
        
    /**
     * Metoda getTile vrati hodnotu dlazdice na suradniciach zadanych parametrmi.
     * @param z Hlbka kde je dlazdica
     * @param x x suradnica kde je dlazdica
     * @param y y suradnica kde je dlazdica
     * @return Hodnota dlazdice na suradniciach
     */
    public int getTile(int z, int x, int y) {
        return mapArray[z][x][y];
    }
    
    /**
     * Metoda getTile vrati meta hodnotu dlazdice na suradniciach zadanych parametrmi.
     * @param z Hlbka kde je dlazdica
     * @param x x suradnica kde je dlazdica
     * @param y y suradnica kde je dlazdica
     * @return Meta hodnota dlazdice na suradniciach
     */
    public int getMeta(int z, int x, int y) {
        return metaArray[z][x][y];
    }
    
    /**
     * Metoda ktora vrati hlbku mapy.
     * @return Hlbka mapy
     */
    public int getDepth() {
        return depth;
    }
    
    /**
     * Metoda ktora vrati sirku mapy po x aj y-ovej suradnici
     * @return Sirka mapy
     */
    public int getSize() {
        return size;
    }
    
    /**
     * Metoda ktora navrati X-ovu poziciu chunku vo svete
     * @return X-ova pozicia chunku
     */
    public int getChunkX() {
        return x;
    }
    
    /**
     * Metoda ktora navrati Y-ovu poziciu chunku vo svete
     * @return Y-ova pozicia chunku
     */
    public int getChunkY() {
        return y;
    }
    
    /**
     * Metoda generate ktora generuje mapu podla zadanych generatorov definovanych 
     * v statickej premennej <b>generators</b>. Ked je null tak zavola default generator a jeho metodu generate
     * kde ako parameter posielame instanciu MapGenerator (spristupnujeme metody MapGenerator). Generator prepise mapu
     * podla jej metod. Ked je v premennej <b>generators</b> viacero generatorov, tak postupne
     * zavola metodu generate kazdeho generatoru.
     * @param map Mapa do ktorej vygenerujeme novy chunk
     * @return Vygenerovana mapa ulozena v objekte ChunkContent
     * @see GeneratorPlugin
     * @see DefaultGenerator
     */
    public ChunkContent generate(SaveMap map) {
        try {
            GeneratorPlugin gen = new DefaultGenerator();
            gen.generate(this, map);
            if (generators != null) {                
                for (GeneratorPlugin g : generators) {
                    g.generate(this, map);
                }
            }
        } catch (Exception e) {
            new MultiTypeWrn(e, Color.red, StringResource.getResource("_mapgenerror"),
                    null).renderSpecific(StringResource.getResource("_label_generror"));
        }
        
        return new ChunkContent(mapArray, metaArray);        
    }        
    
    /**
     * Metoda ktora prida do listu generatorov novy zadany v parametri <b>gen</b>
     * @param gen Novy generator podla ktoreho budeme vytvarat mapu.
     */
    public static void addGenerator(GeneratorPlugin gen) {
        if (generators == null) {
            generators = new ArrayList<>();                    
        }
        generators.add(gen);
    }
    
    /**
     * Metoda ktora vrati vsetky aktivne generatory
     * @return List s generatormi.
     */
    public static ArrayList<GeneratorPlugin> getGenerators() {
        return generators;
    }
    
    
    
}
