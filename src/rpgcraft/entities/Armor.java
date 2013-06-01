/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities;

import rpgcraft.resource.EntityResource;
import rpgcraft.resource.StringResource;

/**
 *
 * @author kirrie
 */
public class Armor extends Item{
    
    public enum ArmorType implements TypeofItems {
           HEAD(StringResource.getResource("head")),
           TORSO(StringResource.getResource("torso")),
           LEGGINGS(StringResource.getResource("leggings")),
           LEGS(StringResource.getResource("legs")),
           HANDS(StringResource.getResource("hands"));

        private String value;
        
        private ArmorType(String value) {
            this.value = value;
        }
        
        @Override
        public String getValue() {
            return value;
        }
    }
    

    // Dolezity konstruktor pri externalizacii
    public Armor() {
        
    }
    
    public Armor(String name, EntityResource res) {
        super(name, res);
        for (ItemType type :res.getItemType()) {
            this.itemType = type;
        }
    }            
    
    @Override
    public void initialize() {
        super.initialize();
        this.equipable = true;        
    }        
    
    
    /**
     * Metoda ktora nastavi typ brnenia.
     * @param armorType Typ brnenia
     */
    public void setArmorType(ArmorType armorType) {
           this.itemType.setSubType(armorType);
    }
    
    public ArmorType getArmorType() {
        return (ArmorType)itemType.getSubType();
    }
    
}
