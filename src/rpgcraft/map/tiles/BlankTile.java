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

    
    public BlankTile(Integer id, TileResource res) {
        super(id, res);
    }

    @Override
    public void moveInto(Entity e) {
        e.decLevel();
        e.updateHeight();
    }
    
    
    
}
