/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.map.chunks;

/**
 *
 * @author doma
 */
public class ChunkContent {
    
    private static final int[][] BLANKLAYER = new int[Chunk.getSize()][Chunk.getDepth()];

    
    private int[][][] chunkArray, metaData;
    private final int size;

    
    public ChunkContent() {
        int width = Chunk.getSize();
        int height = Chunk.getSize();
        int depth = Chunk.getDepth();
        this.size = width * height * depth;
        chunkArray = new int[depth][width][height];
        metaData = new int[depth][width][height];
    }
    
    public ChunkContent(int[][][] chunkArray, int[][][] metaData) {        
        this.chunkArray = chunkArray;
        this.metaData = metaData;
        this.size = chunkArray.length;
    }
    
    public int getSize() {
        return size;
    }
    
    public int[][][] getContent() {
        return chunkArray;
    }
    
    public int getIntOnPosition(int d, int w, int h) {        
        return chunkArray[d][w][h];
    }
    
    public int setIntOnPosition(int d, int w, int h, int value) {
        return chunkArray[d][w][h] = value;
    } 
    
    public int getMetaData(int d, int w, int h) {        
        return metaData[d][w][h];
    }
    
    public int setMetaData(int d, int w, int h, int value) {
        return metaData[d][w][h] = value;
    }
    
    public int[][] getMetaDataLayer(int layer) {
        if (layer >= Chunk.getDepth() || layer < 0) {
            return BLANKLAYER;
        }
        return metaData[layer];
    }
    
    public int[][] getLayer(int layer) {  
        if (layer >= Chunk.getDepth() || layer < 0) {
            return BLANKLAYER;
        }
        return chunkArray[layer];
    }
}
