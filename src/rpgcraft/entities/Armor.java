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
public class Armor extends Item{
    
    public enum ArmorType implements TypeofItems {
           HEAD,
           TORSO,
           LEGGINGS,
           LEGS,
           HANDS;

        @Override
        public Object getValue() {
            return null;
        }
    }
    
    protected ArmorType armorType;

    // Dolezity konstruktor pri externalizacii
    public Armor() {
        
    }
    
    public Armor(String name, EntityResource res) {
        super(name, res);
    }            
    
    /**
     * Metoda ktora vrati konkretnejsi typ v ramci funkcnosti. Tato metoda dava zmysel iba vtedy ked je 
     * entita typu Armor.
     * @return Type obrany/armoru
     * @see ArmorType
     */
    public ArmorType getArmorType() {        
        return armorType;
    }
    
    
    /**
     * Metoda ktora nastavi typ brnenia.
     * @param armorType Typ brnenia
     */
    public void setArmorType(ArmorType armorType) {
           this.armorType = armorType;
    }
    
}
