/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities;

import org.omg.CORBA.INITIALIZE;
import rpgcraft.resource.EntityResource;
import rpgcraft.resource.StringResource;

/**
 *
 * @author kirrie
 */
public class Weapon extends Item{
    
    public enum WeaponType implements TypeofItems {
        ONEHSWORD(StringResource.getResource("onehsword")),
        TWOHSWORD(StringResource.getResource("twohsword"));

        private String value;
        
        private WeaponType(String value) {
            this.value = value;
        }
        
        @Override
        public String getValue() {
            return value;
        }
        
        @Override
        public String toString() {
            return value;
        }
        
    }    
    
    public Weapon() {
        
    }
    
    public Weapon(String name, EntityResource res) {
        super(name, res);
    }
    
    @Override
    public void initialize() {
        super.initialize();
        this.activable = true;   
        if (res != null) {
            for (ItemType type :res.getItemType()) {
                this.itemType = type;
            }
        }
    }        
    
}
