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
    
    private int[][][] chunkArray;
    private final int size;

    
    public ChunkContent() {
        int width = Chunk.getSize();
        int height = Chunk.getSize();
        int depth = Chunk.getDepth();
        this.size = width * height * depth;
        chunkArray = new int[depth][width][height];
    }
    
    public ChunkContent(int[][][] chunkArray) {        
        this.chunkArray = chunkArray;
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
    
    public int[][] getLayer(int layer) {        
        return chunkArray[layer];
    }
}
