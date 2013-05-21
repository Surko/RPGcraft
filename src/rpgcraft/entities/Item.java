/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import rpgcraft.entities.items.ItemGenerator;
import rpgcraft.graphics.spriteoperation.Sprite;
import rpgcraft.map.tiles.Tile;
import rpgcraft.resource.EntityResource;
import rpgcraft.utils.ImageUtils;

/**
 *
 * @author Kirrie
 */
public class Item extends StaticEntity {
       
    // Generator itemov
    private static ItemGenerator gen;
    
    // Sprity pre item
    private ArrayList<Sprite> itemSprites;
        
    // Pocet itemov v stacku
    protected int count;
    
    // vlastnosti itemu - vacsinou z resource. Mozne zmenit.
    private boolean equipable;
    private boolean dropable;
    private boolean usable;
    private boolean activable;
    private boolean equipped;
    
    private int aBaseDamage = 0;
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
        this.name = name == null ? res.getName() : name;
        this.res = res;
        this.id = res.getId();
        this.count = 1;
        this.spriteType = Sprite.Type.ITEM;
        this.itemSprites = res.getEntitySprites().get(spriteType);
        System.out.println("Konstruktor");
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
    public Image getTypeImage() {
        if (itemSprites == null) {
            return null;
        } else {
            if (System.currentTimeMillis() - waypointTime >= maxTime) {
                sprNum++;
                if (itemSprites.size() <= sprNum) {
                    sprNum = 0;
                }
                currentSprite = itemSprites.get(sprNum);
                waypointTime = System.currentTimeMillis();
                maxTime = currentSprite.getDuration();
            }
            //ImageUtils.writeImageToDisc(name, (BufferedImage)itemSprites.get(sprNum).getSprite());
            return currentSprite.getSprite();
        }
    }

    @Override
    public int getCount() {
        return count;
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

    public void incCount() {
        this.count++;
    }

    public void decCount() {
        this.count--;
    }
    
    public void decCount(int value) {
        this.count -= value;
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
        this.aBaseDamage = damage;
    }

    public void setBaseMaxDurability(int durability) {
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
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + this.durability;
        hash = 73 * hash + (this.levelable ? 1 : 0);
        hash = 73 * hash + (this.flame ? 1 : 0);
        hash = 73 * hash + (this.ice ? 1 : 0);
        hash = 73 * hash + (this.paralyze ? 1 : 0);
        hash = 73 * hash + (this.poison ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Item other = (Item) obj;

        if (!this.name.equals(other.name)) {
            return false;
        }

        if (this.durability != other.durability) {
            return false;
        }
        if (this.levelable != other.levelable) {
            return false;
        }
        if (this.flame != other.flame) {
            return false;
        }
        if (this.ice != other.ice) {
            return false;
        }
        if (this.paralyze != other.paralyze) {
            return false;
        }
        if (this.poison != other.poison) {
            return false;
        }
        return true;
    }
}
