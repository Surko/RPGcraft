/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.map;

/**
 *
 * @author Kirrie
 */

public class MapGenerator{
    
    private int depth;
    private int x;
    private int y;
    
    private int[][][] mapTile;
    
    public MapGenerator(int size, int depth) {
        mapTile = new int[depth][size][size];
    }
    
    public int[][][] generate(int x, int y, int depth) {
        this.x = x;
        this.y = y;
        this.depth = depth;        
        return mapTile;
    }

    
}
