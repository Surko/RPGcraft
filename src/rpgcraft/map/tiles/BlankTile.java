/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.map.tiles;

import java.awt.Image;
import rpgcraft.entities.Entity;
import rpgcraft.entities.Player;
import rpgcraft.resource.ImageResource;
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
        if (e instanceof Player) {
            e.getMap().decLevel();
        }
        e.updateHeight();
    }

    @Override
    public Image getImage(int meta) {
        return null;
    }
    
    public Image getUpperImage() {
        return null;
    }
    
    
    
    
    
}
