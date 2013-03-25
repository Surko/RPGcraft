/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.map.tiles;

import rpgcraft.entities.Entity;
import rpgcraft.resource.TileResource;

/**
 *
 * @author kirrie
 */
public class BlankTile extends Tile {

    boolean up;
    
    public BlankTile(Integer id, TileResource res, boolean up) {
        super(id, res);
        this.up = up;
    }

    @Override
    public void moveInto(Entity e) {
        
    }
    
    
    
}
