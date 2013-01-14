/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import rpgcraft.entities.items.ItemGenerator;
import rpgcraft.entities.types.ArmorType;
import rpgcraft.entities.types.Type;
import rpgcraft.resource.EntityResource;

/**
 *
 * @author Kirrie
 */
public class Item extends StaticEntity {
       

       private static ItemGenerator gen;
              
       private boolean equipable;
       private boolean dropable;
       private boolean usable;
       private boolean activable;
       
       private boolean equipped;
       
       private int baseDamage = 0;       
       private int baseMaxDurability = 0;
       private int durability = 0;

       private boolean levelable = false;
       private boolean flame = false;
       private boolean ice = false;
       private boolean paralyze = false;
       private boolean poison = false;
       
       public Item() {
           
       }
       
       public Item(String name, EntityResource res) {
           this.name = name;
       }
       
       /**
        * 
        * @param switcher Urcuje ci sa bude predmet generovat podla daneho mena
        */
       public static Item randomGen(String name) {
           gen = new ItemGenerator(name);
           return gen.generateAll();
       }
                   
       
        @Override
       public boolean isDestroyed() {
           return durability == 0;
       }
       
       public boolean isUsable() {
           return usable;
       }
       
       public boolean isEquipable() {
           return equipable;
       }
       
       public boolean isDropable() {
           return dropable;
       }
       
       public boolean isActivable() {
           return activable;
       }
              
       public boolean isEquipped() {
           return equipped;
       }
       
       public void equip() {
           this.equipped = true;
       }
       
       public void unequip() {
           this.equipped = false;
       }            
       
       public void setEquipped() {
           this.equipped = true;
       }
       
       public void setEquipable() {
           this.equipable = true;
       }
       
       public void setDropable() {
           this.dropable = true;
       }
       
       public void setUsable() {
           this.usable = true;
       }
       
       public void setLevelable(boolean levelable) {
           this.levelable = levelable;
       }
       
       @Override
       public void setName(String name) {
           this.name = name;
       }
       
       public void setBaseDamage(int damage) {
           this.baseDamage = damage;
       }
       
       public void setBaseMaxDurability(int durability){
           this.baseMaxDurability = durability;
       }
       
       public void setDurability(int durability) {
           this.durability = durability;
       }
       
       public void setElement(boolean fl, boolean ic, boolean pa, boolean po) {
           this.flame = fl;
           this.ice = ic;
           this.paralyze = pa;
           this.poison = po;
       }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
    }
       
       
       
}
