/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities;

import rpgcraft.resource.EntityResource;

/**
 *
 * @author kirrie
 */
public class Weapon extends Item{
    
    public enum WeaponType implements TypeofItems {
        ONEHSWORD,
        TWOHSWORD;

        @Override
        public Object getValue() {
            return null;
        }
        
    }
    
    public Weapon(String name, EntityResource res) {
        super(name, res);
    }
    
}
