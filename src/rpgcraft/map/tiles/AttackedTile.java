/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.map.tiles;

import rpgcraft.entities.types.ItemLevelType;

/**
 *
 * @author kirrie
 */
public class AttackedTile {
    int x;
    int y;
    double health;
    Tile tile;

    public AttackedTile(Tile tile, int x, int y) {
        this.tile = tile;
        this.health = tile.health;
        this.x = x;
        this.y = y;
    }

    public double hit(double damage) {
        this.health -= damage;
        return this.health;
    }

    public int getMaterialType() {
        return tile.materialType;
    }       
    
    public Tile getOriginTile() {
        return tile;        
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
