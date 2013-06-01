/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities;

import java.awt.Image;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import rpgcraft.graphics.spriteoperation.Sprite;
import rpgcraft.map.SaveMap;
import rpgcraft.plugins.ItemGeneratorPlugin;
import rpgcraft.resource.EntityResource;
import rpgcraft.resource.StatResource.Stat;
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
        
        public String getValue();
        
    }
    
    public enum ItemType implements TypeofItems {
        WEAPON(StringResource.getResource("weapon")),
        ARMOR(StringResource.getResource("armor")),
        MISC(StringResource.getResource("misc")),
        TILE(StringResource.getResource("tile")),
        NOTYPE(StringResource.getResource("notype"));                
        
        private TypeofItems subType;
        private String value;
        
        private ItemType(String value) {
            this.value = value;
        }
        
        @Override
        public String getValue() {
            return value;
        }
        
        public void setSubType(TypeofItems subType) {
            this.subType = subType;
        }
        
        public TypeofItems getSubType() {
            return subType;
        }
        
        @Override
        public String toString() {
            return this.value + ":" +subType;
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
    
    protected boolean levelable = false;    

    public Item() {
    }   
    
    public Item(String name, EntityResource res) {
        this.name = name == null ? res.getName() : name;
        this.res = res;
        this.activable = true;
        this.id = res.getId();
        this.count = 1;
        this.spriteType = Sprite.Type.ITEM;
        this.itemSprites = res.getEntitySprites().get(spriteType);                
        initialize();
    }

    @Override
    public void initialize() {                
        super.initialize();        
        this.dropable = true;
        this.health = doubleStats.get(Stat.HEALTHMAX);
    }
    
    @Override
    public void reinitialize() {
        super.reinitialize();
        if (res != null) {
            this.itemSprites = res.getEntitySprites().get(spriteType);
        }
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
        if (res == null) {
            return null;
        }
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
        if (itemType == null) {
            return ItemType.NOTYPE;
        }
        return itemType;
    }
    
    @Override
    public boolean isDestroyed() {
        return health == 0;
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

    public void setDurability(int durability) {
        this.health = durability;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }
    
    public long getSeed() {
        return seed;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(itemType);        
        out.writeBoolean(active);
        out.writeBoolean(activable);
        out.writeBoolean(equipped);
        out.writeBoolean(equipable);
        out.writeBoolean(placeable);
        out.writeBoolean(dropable);
        out.writeBoolean(usable);
        out.writeLong(seed);
        out.writeInt(count);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        this.itemType = (ItemType) in.readObject();        
        active = in.readBoolean();
        activable = in.readBoolean();
        equipped = in.readBoolean();
        equipable = in.readBoolean();
        placeable = in.readBoolean();
        dropable = in.readBoolean();
        usable = in.readBoolean();
        this.seed = in.readLong();
        this.count = in.readInt();
        reinitialize();
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

        if (this.levelable != other.levelable) {
            return false;
        }        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (int) (this.seed ^ (this.seed >>> 32));
        hash = 23 * hash + (this.itemType != null ? this.itemType.hashCode() : 0);
        hash = 23 * hash + (this.equipable ? 1 : 0);
        hash = 23 * hash + (this.dropable ? 1 : 0);
        hash = 23 * hash + (this.usable ? 1 : 0);
        hash = 23 * hash + (this.activable ? 1 : 0);
        hash = 23 * hash + (this.placeable ? 1 : 0);
        hash = 23 * hash + (this.levelable ? 1 : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        return count + " " + name + " " + (equipped == true ? StringResource.getResource("_equipped") : "") +
                (active == true ? StringResource.getResource("_activated") : "");
    }
}
