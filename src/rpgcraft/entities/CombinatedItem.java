/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities;

import rpgcraft.entities.Armor.ArmorType;
import rpgcraft.entities.Misc.MiscType;
import rpgcraft.entities.Weapon.WeaponType;
import rpgcraft.resource.EntityResource;

/**
 *
 * @author kirrie
 */
public class CombinatedItem extends Item{
    
    private ArmorType armorType;
    private WeaponType weaponType;
    private MiscType miscType;
    
    
    public CombinatedItem(String name, EntityResource res) {
        this.name = name;
        this.res = res;
    }
    
}
