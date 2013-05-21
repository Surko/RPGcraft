/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities;

import rpgcraft.entities.Entity;
import rpgcraft.entities.types.ArmorType;
import rpgcraft.entities.types.ItemType;
import rpgcraft.graphics.spriteoperation.Sprite.Type;
import rpgcraft.resource.EntityResource;

/**
 *
 * @author Kirrie
 */
public class StaticEntity extends Entity {    

    @Override
    public void unequip(Entity e) {        
    }

    @Override
    public void equip(Entity e) {        
    }
    
    @Override
    public void use(Entity item) {
    }

    @Override
    public ItemType getItemType() {
        return null;
    }

    @Override
    public ArmorType getArmorType() {
        return null;
    }

    @Override
    public void setImpassableTile(int tile) {
    }

    @Override
    public void hit(double damage, Type type) {
    }

    @Override
    public void pushWith(Entity e) {
    }

    @Override
    public int interactWith(int x0, int y0, int x1, int y1, double modifier) {
        return 0;
    }

    @Override
    public void knockback(Type type) {
    }

    @Override
    public boolean updateCoordinates() {
        return false;
    }

    
    
    
}
