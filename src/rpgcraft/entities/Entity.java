/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities;

import rpgcraft.entities.ai.Ai;
import java.awt.Image;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.effects.Effect;
import rpgcraft.entities.types.ArmorType;
import rpgcraft.entities.types.ItemLevelType;
import rpgcraft.entities.types.ItemType;
import rpgcraft.graphics.ui.particles.BarParticle;
import rpgcraft.graphics.spriteoperation.Sprite;
import rpgcraft.graphics.spriteoperation.Sprite.Type;
import rpgcraft.map.SaveMap;
import rpgcraft.map.chunks.Chunk;
import rpgcraft.map.tiles.BlankTile;
import rpgcraft.map.tiles.Tile;
import rpgcraft.resource.EffectResource.EffectEvent;
import rpgcraft.resource.EntityResource;
import rpgcraft.resource.StatResource;
import rpgcraft.resource.StatResource.Stat;

/**
 *
 * @author Kirrie
 */
public abstract class Entity implements Externalizable {    
    private static final long serialVersionUID = 912804676578087866L;
    private static final Logger LOG = Logger.getLogger(Entity.class.getName()); 
    public enum InventoryIndexes {
        
        ID(0),
        NAME(0),
        VALUE(1),
        IMAGE(2);
        
        
        private int index;
        
        private InventoryIndexes(int i) {
            this.index = i;
        }
        
        public int getIndex() {
            return index;
        }                        
    }
    
    protected Ai ai;
    
    // Premenna pre vypocty atributov entit.    
    protected Random random = new Random();    
    // Premenne zahrnajuce inventar
        // Inventar ulozene v Liste predmetov
    protected ArrayList<Item> inventory = new ArrayList<>();   
        // Hashmapa s namapovanym oblecenim
    protected HashMap<ArmorType, Entity> gear;
    //      
    
    // Aktualny Chunk pre Entitu
    protected Chunk actualChunk;
    
    // Meno entity
    protected String name;
    // Resource entity z ktorej entita cerpa
    protected EntityResource res;
    
    // Premenna pre neskorsie pouzitie. Keby mali sprite vacsie hodnoty nez 24 => postavy cez viacero
    // dlazdic.    
    protected int spriteRadius;
    // Typ aktualneho sprite 
    protected Sprite.Type spriteType;
    // Terajsi Sprite
    protected Sprite currentSprite;
    // Id entity    
    protected String id;
    // Stav entity
    private boolean saveInProgress;
    // Aktualny sprite z Mapy spritov 
    protected int sprNum;
    // Bool ci je entita posunutelna
    protected boolean pushable;
    // Mapa prisluchajuca k entite
    protected SaveMap map;
    // Level na ktorej sa nachadza entita s x-ovou a y-ovou poziciou vo svete.
    protected int level,xPix, yPix;
    // Aktivny predmet entity ktory drzi akurat v rukach.
    protected Entity activeItem = this;
    // Bool ci je entita aktivna
    protected boolean active;
    // Vzdialenost odkial pocuje ina entita aktualnu entitu
    protected int sound;
    // Entita na ktoru je zamerany utok
    protected Entity targetEntity;
    // List s nepriechodnymi dlazdicami
    protected ArrayList<Integer> impassableTiles = new ArrayList<>();
    // List s priatelskymi grupami pre kazdeho jednotlivca (bude nahradena listom pre kazdu entitu)
    protected ArrayList<Integer> allyGroups = new ArrayList<>();
    
    // Odhodenie entity do x,y smeru od nejakych vonkajsich vplyvov (NPC, predmety, atd...)
    protected int yKnock, xKnock;
    
    // Premenna indikujuca stav ci sa maju nacitat Chunky okolo entity. Moznost MP.
    protected boolean reload = false;
    
    // Atributy
    protected HashMap<Stat,Double> doubleStats;
    protected HashMap<Stat,Integer> intStats;
    
    // aktivne effekty na entite. Kluc je kedy sa efekt spusta, obsah su efekty pre kluc.
    protected HashMap<EffectEvent, ArrayList<Effect>> activeEffects;
    
    protected double health;
    protected double maxHealth;
    protected double damage;    
    //    
    // Agresivita entity - vzdialenost pri ktorej sa entita zacne spravat agresivne
    protected int aggresivity;
    // Grupa entity
    protected int group;    
    // Sanca na prerusenie - aku sancu ma entita ze prerusi cudzi utok
    protected double interruptionChance;
    // Koncetracia - kolko sa odcitava od nepriatelovej interruptionChance
    protected double concentration;
    
    //
    protected double attack;
    protected double defenseA;
    protected double defenseP;
    protected int attackRadius;
    protected double attackRating;
    protected double defenseRating;
    
    protected boolean moveable;
    protected boolean attackable;
    
    protected BarParticle poweringBar;   
    
    protected double acumDefense;
    protected double acumPower;
    protected double attackPower;
    protected double defensePower;
    protected Double maxPower;
    
    protected ItemType itemType;
    protected ArmorType armorType;
    protected ItemLevelType levelType;
    
    protected long waypointTime;
    protected long currentTime;
    protected long maxTime;
    
    
    // <editor-fold defaultstate="collapsed" desc=" Abstraktne metody ">
    // Abstraktne metody 
    public abstract void unequip(Entity e);    
    public abstract void equip(Entity e);        
    public abstract void setImpassableTile(int tile);
    public abstract void use(Entity item);
    public abstract void hit(double damage, rpgcraft.graphics.spriteoperation.Sprite.Type type);
    public abstract boolean updateCoordinates();
    public abstract void pushWith(Entity e);    
    public abstract int interactWith(int x0, int y0, int x1, int y1, double modifier);    
    public abstract void knockback(rpgcraft.graphics.spriteoperation.Sprite.Type type);    
    
    // </editor-fold>
    
    public static ArrayList<ArrayList<Object>> getInventoryItemsParam(Entity e, ArrayList<String> params) {
        ArrayList<ArrayList<Object>> resultInf = new ArrayList<>();
        for (Item item : e.inventory) {
                ArrayList<Object> record = new ArrayList<>();
                Object[] saveData = new Object[InventoryIndexes.values().length];

                saveData[InventoryIndexes.NAME.getIndex()] = item.name;
                saveData[InventoryIndexes.VALUE.getIndex()] = item.getCount();
                saveData[InventoryIndexes.IMAGE.getIndex()] = item.getTypeImage();

                record.add(saveData[InventoryIndexes.ID.getIndex()]);

                for (String p : params) {
                    switch (InventoryIndexes.valueOf(p)) {
                        case NAME : {
                            record.add(saveData[InventoryIndexes.NAME.getIndex()]);
                        } break;
                        case VALUE : {
                            record.add(saveData[InventoryIndexes.VALUE.getIndex()]);
                        } break;
                        case IMAGE : {
                            record.add(saveData[InventoryIndexes.IMAGE.getIndex()]);
                        } break;
                        default : LOG.log(Level.WARNING,p + " is not implemented parameter");
                    }
                }
                resultInf.add(record);            
        }        
        return resultInf;          
    }
    
    public Entity() {
    }
    /**
     * Metoda ktora nastavi entite meno
     * @param name Text s menom
     */
    public void setName(String name) {
        this.name = name;       
    }
    
    /**
     * Metoda ktora vracia meno entity
     * @return Text s menom
     */
    public String getName() {
        return name;
    }
    
    /**
     * Metoda ktora nastavi id entity
     * @param id Text s id
     */
    public void setId(String id) {
        this.id = id;
    }
                
    /**
     * Metoda ktora nastavi aktualny level/mapu pre entity
     * @param map Map pre aktualnu entity
     * @see Map
     */
    public void setMap(SaveMap map) {
        this.map = map;        
    }
    
    /**
     * Metoda ktora navrati id entity ktorej prislucha resource z entity resource.
     * @return Id entity 
     * 
     */
    
    public String getId() {
        return id;
    }
    
    public Entity getTargetEntity() {
        return targetEntity;
    }
    
    /**
     * Metoda ktora vracia x-ovu poziciu tzv. poziciu dlazdice vo svete. Pouzivam bitovy posun
     * dole o 5 cim delime celkovu poziciu v pixeloch cislom 32.
     * @return X-ova pozicia dlazdice
     */    
    public int getX() {
        return xPix << 5;        
    }
    
    /**
     * Metoda ktora vracia y-ovu poziciu tzv. poziciu dlazdice vo svete. Pouzivam bitovy posun
     * dole o 5 cim delime celkovu poziciu v pixeloch cislom 32.
     * @return Y-ova pozicia dlazdice
     */
    public int getY() {
        return yPix << 5;
    }
    
    public int getRegX() {
        return xPix >> 9;
    }
    
    public int getRegY() {
        return yPix >> 9;
    }
    
    public int getXOffset() {
        return xPix & 31;
    }
    
    public int getYOffset() {
        return xPix & 31;
    }
    
    public int getLevel() {
        return level;
    }
    
    public int getTileX() {
        int res = xPix >> 5;
        return res < 0 ? Chunk.getSize() + res : res;
    }
    
    public int getTileY() {
        int res = yPix >> 5;
        return res < 0 ? Chunk.getSize() + res : res;
    }
    
    public boolean getSaved() {
        return saveInProgress;
    }        
    
    public int getXPix() {
        return xPix; 
    }
    
    public int getYPix() {
        return yPix;
    }    

    /**
     * Metoda ktora prida item do inventara na specialne miesto zadane parametrom <b>index</b>
     * @param index Index kde sa prida item
     * @param item Item na pridanie
     */
    public void addItem(int index, Item item) {
        
        while (index >= inventory.size()) {
            inventory.add(null);
        }
        
        if (inventory.get(index) == null)
            inventory.add(index, item);
    }        
    
    /**
     * Metoda ktora prida item do inventara entite. Ked sa tam uz taky predmet nachadza
     * tak iba zvacsi count/pocet itemov.
     * @param item Item na pridanie.
     */
    public void addItem(Item item) {
        int index = inventory.indexOf(item);
        
        if (index == -1) {
            inventory.add(item);
        } else {
            inventory.get(index).incCount();
        }                        
    }
    
    public void removeItem(Item item) {                
        int index = inventory.indexOf(item);        
        
        if (item.getCount() <= 1) {
            inventory.remove(index);
            return;
        }
        
        if (index != -1) {
            inventory.get(index).decCount();
        }
        
    }
    
    public void removeItem(int index, int value) {
        if (index < inventory.size()) {
            if (value < 0) {
                inventory.remove(index);
            } else {
                Item toRem = inventory.get(index);
                switch (Integer.compare(toRem.getCount(), value)) {
                    case 0 :                     
                    case -1 :
                        inventory.remove(index);
                        break;
                    case 1 :
                        inventory.get(index).decCount(value);
                        break;
                }
            }
        }
    }
    
    public void removeItem(int index) {
        if (index < inventory.size() && index >= 0) {            
            Item toRem = inventory.get(index);
            
            if (toRem.getCount() <= 1) {
                inventory.remove(index);
            } else {
                toRem.decCount();
            }                                    
        }
    }
     
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    
    public Item getItem(int index) {
        if (index >= inventory.size())
            return null;
        
        return inventory.get(index);
    }
    
    public SaveMap getMap() {
        return map;
    }
    
    public ArrayList<Item> getInventory() {
        return inventory;
    }
    
    public int getSound() {
        return sound;
    }
    
    public int getAttackRadius() {
        return attackRadius;        
    }        
    
    public double getAcumPower() {
        return acumPower;
    }
    
    public double getAcumDefense() {
        return acumDefense;
    }
    
    public double getAttackPower() {
        return attackPower;
    }
    
    public double getDefensePower() {
        return defensePower;
    }
    
    public double getMaxPower() {
        return maxPower;
    }
    
    public double getHealth() {
        return health;
    }
    
    public double getMaxHealth() {
        return maxHealth;
    }
    
    public ItemType getItemType() {
        return itemType;
    }
    
    public ArmorType getArmorType() {
        return armorType;
    }
    
    public ItemLevelType getLevelType() {
        return levelType;
    }
         
    public Entity getActiveItem() {
        return activeItem;
    }
    
    public boolean isPushable() {
        return pushable;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public Image getTypeImage() {        
        return null;
    }
    
    public int getCount() {
        return 1;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    public void setSound(int sound) {
        this.sound = sound;
    }
    
    public void incAcumPower(double modifier, double value) {
        acumPower += doubleStats.get(StatResource.Stat.ATKPOWER) * modifier + value;
    }
    
    public void incAcumDefense(double modifier, double value) {
        acumDefense += doubleStats.get(StatResource.Stat.ATKPOWER) * modifier + value;
    }
    
    public void decLevel() {
        this.level--;
    }
    
    public void incLevel() {
        this.level++;
    }
    
    public void setActiveItem(Entity item) {
        this.activeItem = item;
        if (this != item) {
            this.attackPower += item.attackPower;
        }
    }
    
    public void setSaved(Boolean saved) {
        this.saveInProgress = saved;
    }        
    
    public void setArmorType(ArmorType armorType) {
           this.armorType = armorType;
    }
    
    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }
    
    public void setItemLevelType(ItemLevelType levelType) {
        this.levelType = levelType;
    }
    
    public void setChunk(Chunk chunk) {
        this.actualChunk = chunk;
    }               
    
    // </editor-fold>
    
    public boolean update() {
        return true;
    }
    
    protected int rollRandom(int min, int max) {
        return random.nextInt(max - min) + min; 
    }
    
    protected double rollRandom(double min, double max) {
        Double num = max - min;
        return (random.nextDouble() * num) + num;
    }
    
    /**
     * 
     * @param xGo
     * @param yGo
     * @return 
     */
    protected boolean canMove(int xGo, int yGo) {
        if ((xGo == 0)&&(yGo == 0)) {
            return true;
        }
        
        // Ked je smerovy vektor pohybu nenulovy v nejakom smere tak aktualizuje cas pre entitu
        currentTime = System.currentTimeMillis() - waypointTime;
        
        int xdirModifier = 0;
        int ydirModifier = 0;
        
        if (xGo > 0) {
            xdirModifier = 24;
        }
        
        if (yGo > 0) {
            ydirModifier = 24;
        }
        int x0 = xPix + xGo;
        int y0 = yPix + yGo;
        
        // ziskanie regionalnej pozicie x a y(chunk v smere pohybe) 
        int regX = x0 >> 9;
        int regY = y0 >> 9;
        if (actualChunk == null) return false;                        
        
        for (Entity e : map.getEntities()) {
            
            if (e == this) continue;
            
            if (!(this instanceof Player)) {
                if ((targetEntity != e) && (e.group != this.group)) {
                    targetEntity = e;
                }
            }
            if (e.isPushable()) {
                entityPush(e);                
            }
        }
        
        Chunk chunk = actualChunk;
        if ((actualChunk.getX() != regX)||(actualChunk.getY() != regY)) {              
            actualChunk = map.chunkPixExist(regX, regY);
        }
        if (actualChunk != null) {            
            Tile lTile = Tile.tiles.get(actualChunk.getTile(level - 1, x0 >> 5, y0 >> 5));
            Tile uTile = Tile.tiles.get(actualChunk.getTile(level, x0 >> 5, y0 >> 5));
            //debugCoordinateswithTiles(x0, y0, tile);        
            //debugCoordinateswithTiles(xx, yy, tile);

            // ked hrac alebo entita obsahuje dlazdicu na prislusnej strany 
            // v nepriechodnych dlazdiciach tak sa nepohne           
            if (impassableTiles.contains(lTile.getId()) || !(uTile instanceof BlankTile)) { 
                actualChunk = chunk;
                return false; 
            }

            // skusa pohyb do prislusnej strany a vykona taku akciu 
            // ktora je definovana pri dlazdici
            lTile.moveInto(this);

            reload = true;

        } else {
            return false;
        }                                                               

        calibratePix(x0, y0);
        return true;                
    }
    
    public void updateHeight() {               
        Tile tile = Tile.tiles.get(actualChunk.getTile(level - 1, xPix >> 5, yPix >> 5));        
                
        tile.moveInto(this);
    }
    
    /**
     * Metoda ktore je nutne Overridnut v podedenych metodach. Bude sluzit
     * k posuvaniu entity zadanej parametrom aktualnou entitou ktora vola tuto metodu.
     * @param entity Entita na posunutie
     * @return True/False ci sa podarilo posunutie
     */
    protected boolean entityPush(Entity entity) {
        return false;
    }         
    
    /**
     * Metoda ktora vrati ci entita moze utocit na inu
     * @return True/False ci moze utocit.
     */
    protected boolean canAttack() {                
        return attackable;
    }                    
    
    /**
     * Metoda calibratePix ma za ulohy nastavit x-ove a y-ove suradnice entity.
     * @param xPix Parameter na aku sa ma zmenit surandnica x
     * @param yPix Parameter na aku sa ma zmenit surandnica y
     */
    protected void calibratePix(int xPix, int yPix) {
        this.xPix = xPix;
        this.yPix = yPix;
    }
    
    /**
     * Metoda calibrateX ma za ulohu zvysit alebo znizit x-ove suradnice entity.
     * @param x Parameter o kolko sa ma zmenit suradnica
     */
    private void calibrateX(int x) {
        xPix += x;
    }
    
    /**
     * Metoda calibrateY ma za ulohu zvysit alebo znizit y-ove suradnice entity.
     * @param y Parameter o kolko sa ma zmenit suradnica
     */
    private void calibrateY(int y) {
        yPix += y;
    }
            
        
    /**
     * Metoda ktora prida dodatocne efekty a upravi atributy ako hpower a dpower
     * tejto entity. Teraz su tam natvrdo dane hodnoty (12), ktore budu nahradene
     * predmetovymi atributmi. Pomocou premennej interruptionChance u entit sa rozhodne ci 
     * sa znizenie statov vykona alebo nie.
     * Takisto tato metoda obsluhuje ci sa efekt prida alebo nie.
     * 
     * @param e Entita ktorej sa znizia atributy. V tomto pripade 
     * sa znizuje iba defense a attack
     */
    protected void addAfterEffectsFrom(Entity entity, Entity item) {
        if (random.nextDouble() < entity.interruptionChance - this.concentration) {
            this.acumPower -= this.acumPower < 12 ? this.acumPower : 12;
            this.acumDefense -= this.acumDefense < 12 ? this.acumDefense : 12;
        }
    }
    
    /**
     * Metoda hasInRadius s parametrom entity testuje ci sa entita nachadza v
     * utocnom dosahu entity.
     * @param entity Entita na ktoru sa utoci
     * @return True/False ci sa Entita v dosahu nachadza.
     */
    protected boolean hasInRadius(Entity entity) {
        return (entity.xPix * entity.xPix + entity.yPix * entity.yPix < attackRadius * attackRadius);
        
    }
    
    /**
     * Metoda ktora kontroluje ci je entita znicena co v pripade entit ako takych
     * znamena ze zivot je mensi alebo rovny nule.
     * @return True/False ci je entita znicena
     */
    protected boolean isDestroyed() {
        return health <= 0;
    }
    
    protected boolean isSaving() {
        return saveInProgress;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {                        
        out.writeUTF(name);
        out.writeBoolean(active);
        out.writeUTF(res.getId());        
        out.writeInt(xPix);
        out.writeInt(yPix);
        if (activeItem == this) {
            out.writeInt(-1);
        }
        out.writeInt(group);
        out.writeInt(level);
        out.writeDouble(health);        
        out.writeObject(intStats);
        out.writeObject(doubleStats);
        out.writeUTF(spriteType.name());
        out.writeInt(sprNum);
        out.writeObject(impassableTiles);
        out.writeObject(activeEffects);
        out.writeObject(inventory);
        
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.name = in.readUTF();
        this.active = in.readBoolean();
        this.res = EntityResource.getResource(in.readUTF());
        this.xPix = in.readInt();
        this.yPix = in.readInt();
        
        int activeitem = in.readInt();
        if (activeitem == -1) {
            this.activeItem = this;                     
        } else {
            this.activeItem = inventory.get(activeitem);
        }
        this.group = in.readInt();
        this.level = in.readInt();
        this.health = in.readDouble();
        this.intStats = (HashMap<Stat, Integer>) in.readObject();
        this.doubleStats = (HashMap<Stat, Double>) in.readObject();
        this.spriteType = Type.valueOf( in.readUTF() );
        this.sprNum = in.readInt();
        this.impassableTiles = (ArrayList<Integer>)in.readObject();
        this.activeEffects = (HashMap<EffectEvent, ArrayList<Effect>>) in.readObject();
        this.inventory = (ArrayList<Item>)in.readObject();
        
    }
    
    
    /**
     * Debugovacia metoda pre dlazdice ktora vypisuje kde sa hrac nachadza a aka
     * dlazdica (menom) je podnim.
     * @param xx Miesto hraca ,x-ova suradnica,  v jednej dlazdici (0-32)
     * @param yy Miesto hraca ,y-ove suradnica, v jednej dlazdici (0-32)
     * @param tile Dlazdica z ktorej sa vyberie meno
     */
    public void debugCoordinateswithTiles(int xx, int yy, Tile tile) {
        System.out.println(name + " " + xx + " " + yy + " " + tile.getName());
    }
    
    
}
