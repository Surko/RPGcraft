/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.map.chunks;

import rpgcraft.map.tiles.DefaultTiles;

/**
 * Trieda ktora vytvara instanciu pre udrzanie a pracu s obsahom chunku.
 * Metody dovoluju nastavovat a vratit ako jednotlive dlazdice chunku tak aj meta data
 * chunku. Na vytvorenie  je treba zavola konstruktor ChunkContent, ktory vytvori 
 * kontajner pre novy chunk podla vysok a sirok zadanych v triede Chunk.
 */
public class ChunkContent {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    private static final int[][] BLANKLAYER = new int[Chunk.getSize()][Chunk.getDepth()];
    
    private int[][][] chunkArray, metaData;
    private final int size;
    // </editor-fold>
    
    /**
     * Konstruktor pre vytvorenie obsahu pre chunk. Inicializujeme sirky a vysku
     * spolu s velkostou celeho chunku a nastavenim poli chunkArray a metaArray
     */
    public ChunkContent() {
        int width = Chunk.getSize();
        int height = Chunk.getSize();
        int depth = Chunk.getDepth();
        this.size = width * height * depth;
        chunkArray = new int[depth][width][height];
        metaData = new int[depth][width][height];
    }
    
    /**
     * Konstruktor pre vytvorenie obsahu pre chunk. Priamo inicializujeme polia chunkArray
     * a metaArray podla zadanych parametrov.
     * @param chunkArray Chunk pole
     * @param metaData Meta pole
     */
    public ChunkContent(int[][][] chunkArray, int[][][] metaData) {        
        this.chunkArray = chunkArray;
        this.metaData = metaData;
        this.size = chunkArray.length;
    }
    
    /**
     * Metoda ktora vrati velkost celeho obsahu
     * @return Velkost obsahu
     */
    public int getSize() {
        return size;
    }
    
    /**
     * Metoda ktora vrati obsah ako 3D pole.
     * @return 3D pole s celkovym obsahom
     */
    public int[][][] getContent() {
        return chunkArray;
    }
    
    /**
     * Metoda ktora vrati hodnotu z pola na poziciach zadanych parametrami 
     * @param d Vyska z ktorej chceme dlazdicu
     * @param w X-ova pozicia z ktorej chceme dlazdicu
     * @param h Y-ova pozicia z ktorej chceme dlazdicu
     * @return Id dlazdice na tomto mieste
     */
    public int getIntOnPosition(int d, int w, int h) {        
        return chunkArray[d][w][h];
    }
    
    /**
     * Metoda ktora nastavi hodnotu v poli na poziciach zadanych parametrami.
     * Hodnota value bude v chunkArray ako id dlazdice a hodnota meta bude meta data
     * v metaArray. Pri existencii dlazdice na tychto poziciach nic nemenime.
     * @param d Vyska dlazdice
     * @param w X-ova dlazdice
     * @param h Y-ova dlazdice
     * @param value Hodnota/id dlazdice
     * @param meta Meta dlazdice
     * @return True/false ci sa podarilo zmenit dlazdicu.
     */
    public boolean setTileOnPosition(int d, int w, int h, int value, int meta) {        
        if (chunkArray[d][w][h] != DefaultTiles.BLANK_ID) {
            return false;
        }
        chunkArray[d][w][h] = value;
        metaData[d][w][h] = meta;
        return true;
    }
    
    /**
     * Metoda ktora nastavi hodnotu v poli na poziciach zadanych parametrami.
     * Hodnota value bude v chunkArray ako id dlazdice. Pri existencii dlazdice na tychto poziciach nic nemenime.
     * @param d Vyska dlazdice
     * @param w X-ova dlazdice
     * @param h Y-ova dlazdice
     * @param value Hodnota/id dlazdice
     * @return True/false ci sa podarilo zmenit dlazdicu.
     */
    public boolean setIntOnPosition(int d, int w, int h, int value) {
        if (chunkArray[d][w][h] != DefaultTiles.BLANK_ID) {
            return false;
        }
        chunkArray[d][w][h] = value;
        return true;
    }
    
    /**
     * Metoda ktora zmeni hodnotu v poli na poziciach zadanych parametrami.
     * Hodnota value bude v chunkArray ako id dlazdice.
     * @param d Vyska dlazdice
     * @param w X-ova dlazdice
     * @param h Y-ova dlazdice
     * @param value Hodnota/id dlazdice
     * @return Hodnota dlazdice na ktoru sme zmenili 
     */
    public int replaceIntOnPosition(int d, int w, int h, int value) {
        return chunkArray[d][w][h] = value;
    }
    
    /**
     * Metoda ktora vrati meta data z pozicii zadanej parametrami.
     * @param d Vyska dlazdice
     * @param w X-ova dlazdice
     * @param h Y-ova dlazdice
     * @return Meta hodnota dlazdice
     */
    public int getMetaData(int d, int w, int h) {        
        return metaData[d][w][h];
    }
    
    /**
     * Metoda ktora nastavi meta data na pozicii zadanej parametrami.
     * @param d Vyska dlazdice
     * @param w X-ova dlazdice
     * @param h Y-ova dlazdice
     * @param value Nova meta hodnota
     * @return Meta hodnota dlazdice
     */
    public int setMetaData(int d, int w, int h, int value) {
        return metaData[d][w][h] = value;
    }
    
    /**
     * Metoda ktora vrati 2D pole <=> jednu vrstvu mapy s meta hodnotami. Vysku vrstvy urcujeme 
     * podla parametra
     * @param layer Vyska vrstvy ktoru chceme
     * @return Vrstvu chunku s meta hodnotami
     */
    public int[][] getMetaDataLayer(int layer) {
        if (layer >= Chunk.getDepth() || layer < 0) {
            return BLANKLAYER;
        }
        return metaData[layer];
    }
    
    /**
     * Metoda ktora vrati 2D pole <=> jednu vrstvu mapy s id dlazdic. Vysku vrstvy urcujeme 
     * podla parametra
     * @param layer Vyska vrstvy ktoru chceme
     * @return Vrstvu chunku s id dlazdic
     */
    public int[][] getLayer(int layer) {  
        if (layer >= Chunk.getDepth() || layer < 0) {
            return BLANKLAYER;
        }
        return chunkArray[layer];
    }
}
