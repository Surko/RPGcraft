/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities;

import java.awt.Image;
import java.util.ArrayList;
import rpgcraft.graphics.spriteoperation.Sprite;
import rpgcraft.map.SaveMap;
import rpgcraft.plugins.ItemGeneratorPlugin;
import rpgcraft.resource.EntityResource;
import rpgcraft.resource.StringResource;

/**
 *
 * @author Kirrie
 */
public abstract class Item extends StaticEntity {                  
    
    public enum Command {
        INFO(StringResource.getResource("_info")),
        USE(StringResource.getResource("_use")),
        EQUIP(StringResource.getResource("_equip")),
        UNEQUIP(StringResource.getResource("_unequip")),
        ACTIVE(StringResource.getResource("_active")),
        UNACTIVE(StringResource.getResource("_unactive")),
        DESTROY(StringResource.getResource("_destroy")),
        CLOSE(StringResource.getResource("_close")),
        PLACE(StringResource.getResource("_place")),
        DROP(StringResource.getResource("_drop"));
                
        private String sCommand;
        
        private Command(String sCommand) {
            this.sCommand = sCommand;
        }
        
        public String getValue() {
            return sCommand;
        }
    }
    
    
    /**
     * Interface pre rozne type predmetov. Implementuju ho enumy ktore sa nachadzaju v triedach
     * dediacich od Item
     */
    public interface TypeofItems {                                
        
        public Object getValue();
        
    }
    
    public enum ItemType implements TypeofItems {
        WEAPON,
        ARMOR,
        MISC,
        TILE;
        
        private TypeofItems subType;
        
        @Override
        public Object getValue() {
            return null;
        }
        
        public void setSubType(TypeofItems subType) {
            this.subType = subType;
        }
        
        public TypeofItems getSubType() {
            return subType;
        }
    }
    
    private long seed;
    
    // Sprity pre item
    private ArrayList<Sprite> itemSprites;
        
    // Pocet itemov v stacku
    protected int count;
    
    // vlastnosti itemu - vacsinou z resource. Mozne zmenit.
    protected ItemType itemType;
    protected boolean equipable;
    protected boolean dropable;
    protected boolean usable;
    protected boolean activable;
    protected boolean equipped;
    protected boolean placeable;
    
    protected int aBaseDamage = 0;
    protected int baseMaxDurability = 0;
    protected int durability = 0;
    
    protected boolean levelable = false;
    protected boolean flame = false;
    protected boolean ice = false;
    protected boolean paralyze = false;
    protected boolean poison = false;

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
    public static Item randomGen(String genName, String name, ItemType type) {
        ItemGeneratorPlugin gen = ItemGeneratorPlugin.getGenerator(genName);
        gen.initialize(name, type);
        return gen.generateAll();
    }

    /**
     * 
     * @return 
     */
    public static Item createItem(String name, SaveMap map, EntityResource res) {
        ArrayList<ItemType> types = res.getItemType();
        Item item = null;
        if (types.size() > 1) {            
            item = new CombinatedItem(name, res);
        }
        if (types.size() == 1) {
            switch (types.get(0)) {
                case ARMOR : {
                    item = new Armor(name, res);                    
                } break;
                case WEAPON : {
                    item = new Weapon(name, res);                                        
                } break;
                case MISC : {
                    item = new Misc(name, res);
                } break;
                case TILE : {
                    item = new TileItem(name, res);
                } break;
                default : return null;
            }
            if (item != null) {
                item.setMap(map);
            }
        }
        return item;
    }        
    
    public static Item createItem(String name, EntityResource res) {
        ArrayList<ItemType> types = res.getItemType();
        Item item = null;
        if (types.size() > 1) {            
            item = new CombinatedItem(name, res);
        }
        if (types.size() == 1) {
            switch (types.get(0)) {
                case ARMOR : {
                    item = new Armor(name, res);                    
                } break;
                case WEAPON : {
                    item = new Weapon(name, res);                                        
                } break;
                case MISC : {
                    item = new Misc(name, res);
                } break;
                case TILE : {
                    item = new TileItem(name, res);
                } break;
                default : return null;
            }           
        }
        return item;
    }  
    
    private static void _addItemInfo(Object data, ArrayList<ArrayList<Object>> resultInf) {
        ArrayList<Object> record = new ArrayList<>(); 
        record.add(data);
        record.add(data);
        resultInf.add(record);  
    }
    
    public static ArrayList<ArrayList<Object>> getItemInfo(Item item) {
        ArrayList<ArrayList<Object>> resultInf = new ArrayList<>();        
               
        // Info o predmetoch
        _addItemInfo(StringResource.getResource("_info"), resultInf);    
        
        if (item.isUsable()) {
        _addItemInfo(StringResource.getResource("_use"), resultInf);            
        }        
        if (item.isEquipable()) {
            if (item.isEquipped()) {
                _addItemInfo(StringResource.getResource("_unequip"), resultInf);            
            } else {
                _addItemInfo(StringResource.getResource("_equip"), resultInf);    
            }            
        }
        if (item.isActivable()) {
            if (item.isActive()) {
                _addItemInfo(StringResource.getResource("_unactive"), resultInf);            
            } else {
                _addItemInfo(StringResource.getResource("_active"), resultInf);    
            }            
        }
        if (item.isDropable()) {
        _addItemInfo(StringResource.getResource("_drop"), resultInf);            
        } 
        if (item.isPlaceable()) {
        _addItemInfo(StringResource.getResource("_place"), resultInf);            
        }
        _addItemInfo(StringResource.getResource("_destroy"), resultInf);          
        _addItemInfo(StringResource.getResource("_close"), resultInf);  
           
        return resultInf;          
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

    /**
     * Metoda ktora vrati typ entity v ramci funkcnosti. Typ tohoto predmetu su vacsinou nazvy pre potomkov 
     * co dedia od tejto triedy (WEAPON, ARMOR,...)
     * @return Typ entity.     
     * @see ItemType
     */    
    public ItemType getItemType() {          
        return itemType;
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
    
    public boolean isPlaceable() {
        return placeable;
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
    
    /**
     * Metoda ktora prenastavi typ tohoto predmetu v ramci funkcnosti na typ zadany parametrom
     * <b>itemType</b>
     * @param itemType Typ predmetu
     * @see ItemType
     */
    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
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

    public void setSeed(long seed) {
        this.seed = seed;
    }
    
    public long getSeed() {
        return seed;
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
