/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.map.tiles;

import rpgcraft.map.chunks.Chunk;
import rpgcraft.resource.TileResource;

/**
 *
 * @author kirrie
 */
public class AttackedTile {
    int x,y,level,tileId;
    double health;
    Chunk chunk;

    public AttackedTile(Chunk chunk, int level, int x, int y) {        
        this.tileId = chunk.getTile(level, x, y);
        this.health = chunk.getMetaData(level, x, y);
        this.chunk = chunk;
        this.x = x;
        this.y = y;
        this.level = level;
    }

    public double hit(double damage) {
        this.health -= damage; 
        System.out.println(health);
        if (health <= 0) {
            chunk.destroyTile(level, x, y);
        } else {
            chunk.setMeta(level, x, y, (int)health);
        }
        return this.health;
    }

    public int getDurability() {
        return Tile.tiles.get(tileId).getTileStrength();
    }       
    
    public Tile getOriginTile() {
        return Tile.tiles.get(tileId);        
    }
    
    public int getId() {
        return tileId;
    }
    
    public double getHealth() {
        return health;
    }
    
    public boolean isDestroyed() {
        return health <=0 ? true : false;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }        
}
