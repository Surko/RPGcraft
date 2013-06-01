/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities;

import rpgcraft.graphics.spriteoperation.Sprite.Type;

/**
 *
 * @author Kirrie
 */
public class StaticEntity extends Entity {    

    public StaticEntity() {
        
    }
    
    @Override
    public void unequip(Item e) {        
    }

    @Override
    public void equip(Item e) {        
    }
    
    @Override
    public void use(Item item) {
    }   

    @Override
    public void setImpassableTile(int tile) {
    }

    @Override
    public double hit(double damage, Type type) {
        return 0d;
    }

    @Override
    public void pushWith(Entity e) {
    }

    @Override
    public double interactWithEntities(int x0, int y0, int x1, int y1, double modifier) {
        return 0;
    }

    @Override
    public void knockback(Type type) {
    }

    @Override
    public boolean updateCoordinates() {
        return false;
    }

    @Override
    public void drop(Item item) {
        
    }

    
    
    
}
