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
import rpgcraft.entities.Armor.ArmorType;
import rpgcraft.entities.types.ItemLevelType;
import rpgcraft.graphics.ui.particles.BarParticle;
import rpgcraft.graphics.spriteoperation.Sprite;
import rpgcraft.graphics.spriteoperation.Sprite.Type;
import rpgcraft.map.SaveMap;
import rpgcraft.map.chunks.Chunk;
import rpgcraft.map.tiles.DefaultTiles;
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
        IMAGE(2),
        TYPE(3);
        
        
        private int index;
        
        private InventoryIndexes(int i) {
            this.index = i;
        }
        
        public int getIndex() {
            return index;
        }                        
    }
    
    public enum EntityType {
        STATIC,
        ITEM,
        MOVING
    }
    
    protected Ai ai;
    
    // Premenna pre vypocty atributov entit.    
    protected Random random = new Random();    
    
    // Premenne zahrnajuce inventar
    // Inventar ulozene v Liste predmetov
    protected ArrayList<Item> inventory = new ArrayList<>();   
    // Hashmapa s namapovanym oblecenim
    protected HashMap<ArmorType, Armor> gear;
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
    protected Item activeItem = null;
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
    protected boolean reloadChunks = false;
    
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
    public abstract int interactWithEntities(int x0, int y0, int x1, int y1, double modifier);    
    public abstract void knockback(rpgcraft.graphics.spriteoperation.Sprite.Type type);    
    
    // </editor-fold>
    
    
    public static Entity createEntity(String name, SaveMap map, EntityResource res) {
        switch (res.getEntityType()) {
            case ITEM : {
                return Item.createItem(name, map, res);
            }
            case MOVING : {
                return new MovingEntity(name, map, res);
            }
            default : return null;
        }       
    }
    
    public static ArrayList<ArrayList<Object>> getInventoryItemsParam(Entity e, ArrayList<String> params) {
        ArrayList<ArrayList<Object>> resultInf = new ArrayList<>();
        for (Item item : e.inventory) {
                ArrayList<Object> record = new ArrayList<>();
                Object[] saveData = new Object[InventoryIndexes.values().length];

                saveData[InventoryIndexes.NAME.getIndex()] = item.name;
                saveData[InventoryIndexes.VALUE.getIndex()] = item.getCount();
                saveData[InventoryIndexes.IMAGE.getIndex()] = item.getTypeImage();
                saveData[InventoryIndexes.TYPE.getIndex()] = item.getItemType();

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
                        case TYPE : {
                            record.add(saveData[InventoryIndexes.TYPE.getIndex()]);
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
     * Metoda ktora prida item do inventara na specialne miesto zadane parametrom <b>index</b>
     * @param index Index kde sa prida item
     * @param item Item na pridanie
     */
    public void addItem(int index, Item item) {
        
        if (item == null) {
            return;
        }
        
        if (index >= inventory.size()) {
            return;
        }
                
        inventory.add(index, item);
    }        
    
    public void addItem(int index) {
        if (index > inventory.size()) {
            return;
        }
        
        inventory.get(index).incCount();
    }
    
    /**
     * Metoda ktora prida item do inventara entite. Ked sa tam uz taky predmet nachadza
     * tak iba zvacsi count/pocet itemov.
     * @param item Item na pridanie.
     */
    public void addItem(Item item) {
        if (item == null) {
            return;
        }
        
        int index = inventory.indexOf(item);
        
        if (index == -1) {
            inventory.add(item);
        } else {
            inventory.get(index).incCount();
        }                        
    }
    
    /**
     * Metoda ktora vymaze predmet zadany parametrom <b>item</b> z inventara.
     * Metoda si najde index tohoto predmetu a ked je pocet vacsi ako 1 tak iba znici cislo
     * a ked je pocet mensi alebo rovny 1 tak vymaze cely predmet.
     * @param item Item na vymazanie z inventaru tejto entity.
     */
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
    
    /**
     * Metoda ktora vymaze predmet na pozicii zadanej parametrom <b>index</b> z inventara.
     * Metoda skontroluje ci index patri do velkosti inventara. Nasledne ked je parameter <b>value</b>
     * mensi ako 0 tak vymaze vsetky predmety na tomto mieste. Pri hodnote value vacsej ako 0 porovna tuto hodnotu
     * s poctom rovnakych predmetov. Ked je vacsia tak vymaze vsetky predmety a ked mensia tak iba tento pocet.     
     * @param index Index predmetu ktory vymazavame.
     * @param value Pocet predmetov ktore vymazava z inventara
     * 
     */
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
    
    /**
     * Metoda ktora vymaze predmet na indexe zadanom parametrom <b>index</b>.
     * Ked je pocet predmetov na tomto indexu mensi alebo rovny ako 1 tak vymaze cely slot s predmetom.
     * Pri hodnote vacsej znici pocet predmetov o jedna.
     * 
     * @param index Index predmetu ktory vymazavame.
     */
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
    
    /**
     * Metoda ktora vrati true/false podla toho ci sa entita akurat uklada na disk.
     * @return True/false ci sa entita uklada na disk.
     */
    protected boolean isSaving() {
        return saveInProgress;
    }
    
    /**
     * Metoda ktora navrati id entity ktorej prislucha resource z entity resource.
     * @return Id entity 
     * 
     */    
    public String getId() {
        return id;
    }
    
    /**
     * Metoda ktora vrati entitu na ktoru je terajsia entita zamerana a bude nanu utocit. 
     * Vyuzitelna premenna pri rozhodovani NPC na koho sa maju zamerat pri utoceni.
     * @return Entita na ktoru je terajsia entita zamerana.
     */
    public Entity getTargetEntity() {
        return targetEntity;
    }
    
    /**
     * Metoda ktora vrati x-ovu poziciu vo svete v pixeloch.
     * @return x-ova pozicia vo svete.
     */
    public int getXPix() {
        return xPix; 
    }
    
    /**
     * Metoda ktora vrati y-ovu poziciu vo svete v pixeloch.
     * @return y-ova pozicia.
     */
    public int getYPix() {
        return yPix;
    } 
    
    /**
     * Metoda ktora vracia x-ovu poziciu tzv. poziciu dlazdice vo svete. Pouzivam bitovy posun
     * dole o 5 cim delime celkovu poziciu v pixeloch cislom 32.
     * @return X-ova pozicia dlazdice
     */    
    public int getX() {
        return xPix >> 5;        
    }
    
    /**
     * Metoda ktora vracia y-ovu poziciu tzv. poziciu dlazdice vo svete. Pouzivam bitovy posun
     * dole o 5 cim delime celkovu poziciu v pixeloch cislom 32.
     * @return Y-ova pozicia dlazdice
     */
    public int getY() {
        return yPix >> 5;
    }
    
    /**
     * Metoda ktora vracia x-ovu poziciu regionu. Region ziskavame predelenim aktualnej suradnice xPix cislom 512 
     * (kedze jeden chunk ma 16 dlazdic a jedna dlazdica ma 32 pixelov => 512 pixelov).
     * @return X-ova pozicia regionu kde je entita
     */
    public int getRegX() {
        return xPix >> 9;
    }
    
    /**
     * Metoda ktora vracia y-ovu poziciu regionu. Region ziskavame predelenim aktualnej suradnice yPix cislom 512 
     * (kedze jeden chunk ma 16 dlazdic a jedna dlazdica ma 32 pixelov => 512 pixelov).
     * @return Y-ova pozicia regionu kde je entita
     */
    public int getRegY() {
        return yPix >> 9;
    }
    
    /**
     * Metoda ktora vrati aktualnu x-ovu poziciu v aktualnej dlazdici (0-31). Dlazdica ma sirku 32
     * => staci modulovat aktualnu x-ovu suradnicu cislom 32 alebo pouzit bitove operacie &.
     * @return x-ova pozicia v aktualnej dlazdici (0-31).
     */
    public int getXOffset() {
        return xPix & 31;
    }
    
    /**
     * Metoda ktora vrati aktualnu y-ovu poziciu v aktualnej dlazdici (0-31). Dlazdica ma sirku 32
     * => staci modulovat aktualnu y-ovu suradnicu cislom 32 alebo pouzit bitove operacie &.
     * @return y-ova pozicia v aktualnej dlazdici (0-31).
     */
    public int getYOffset() {
        return xPix & 31;
    }
    
    /**
     * Metoda ktora vrati x-ovu poziciu dlazdice v chunku v ktorom sa entita nachadza (0-15).
     * Kedze chunk ma v sebe 16 dlazdic dlzky 32 pixelov tak nam staci predelit x-ovu poziciu
     * entity vo svete cislom 32 a dostaneme cislo dlazdice. Pri zapornom cisle iba odcitame od sirky
     * chunku.
     * @return x-ova pozicia dlazdice na ktorej sa nachadza entita.
     */
    public int getTileX() {
        int tileX = xPix >> 5;
        return tileX < 0 ? Chunk.getSize() + tileX : tileX % Chunk.getSize();
    }
    
    /**
     * Metoda ktora vrati y-ovu poziciu dlazdice v chunku v ktorom sa entita nachadza (0-15).
     * Kedze chunk ma v sebe 16 dlazdic dlzky 32 pixelov tak nam staci predelit y-ovu poziciu
     * entity vo svete cislom 32 a dostaneme cislo dlazdice. Pri zapornom cisle iba odcitame od sirky
     * chunku.
     * @return y-ova pozicia dlazdice na ktorej sa nachadza entita.
     */
    public int getTileY() {
        int tileY = yPix >> 5;
        return tileY < 0 ? Chunk.getSize() + tileY : tileY % Chunk.getSize();
    }
    
    /**
     * Metoda ktora vrati aktualnu vysku entity v mape.
     * @return Vyska entity v mape (0-127).
     */    
    public int getLevel() {
        return level;
    }        
        
    /**
     * Metoda ktora vrati predmet na indexe zadanom parametrom <b>index</b>
     * @param index Index predmetu ktory chceme
     * @return Predmet na indexe.
     */
    public Item getItem(int index) {
        if (index >= inventory.size())
            return null;
        
        return inventory.get(index);
    }
    
    /**
     * Metoda ktora vrati mapu na ktorej sa entita nachadza.
     * @return Mapu kde sa entita nachadza.
     */
    public SaveMap getMap() {
        return map;
    }
    
    /**
     * Metoda ktora vrati inventar tejto entity.
     * @return ArrayList s predmetmi <=> inventar.
     */
    public ArrayList<Item> getInventory() {
        return inventory;
    }
    
    /**
     * Metoda ktora vrati hodnotu sound (ako daleko je entita pocut).
     * @return Hlucnost entity.
     */
    public int getSound() {
        return sound;
    }
    
    /**
     * Metoda ktora vrati vzdialenost utoku kam entita dociahne.
     * @return Vzdialenost utoku.
     */
    public int getAttackRadius() {
        return attackRadius;        
    }        
    
    /**
     * Metoda ktora vrati naakumulovanu silu utoku.
     * @return Naakumulovana sila utoku.
     */
    public double getAcumPower() {
        return acumPower;
    }
    
    /**
     * Metoda ktora vrati naakumulovanu silu obrany.
     * @return Naakumulovana sila obrany.
     */
    public double getAcumDefense() {
        return acumDefense;
    }
    
    /**
     * Metoda ktora vrati vynalozenu silu do utoku ktory entita vykonava (Hodnota ktora sa pridava do acumPower, acumTilePower)
     * @return Sila utoku.
     */
    public double getAttackPower() {
        return attackPower;
    }
    
    /**
     * Metoda ktora vrati vynalozenu silu do obrany ktory entita vykonava (Hodnota ktora sa pridava do acumDefense)
     * @return Sila obrany.
     */
    public double getDefensePower() {
        return defensePower;
    }
    
    /**
     * Metoda ktora vrati maximalnu silu ktora je vynalozena pri utoku.
     * @return Maximalna sila utoku.
     */
    public double getMaxPower() {
        return maxPower;
    }
    
    /**
     * Metoda ktora vrati aktualny zivot tejto entity.
     * @return Aktualny zivot entity
     */
    public double getHealth() {
        return health;
    }
    
    /**
     * Metoda ktora vrati maximalny zivot tejto entity.
     * @return Maximalny zivot entity.
     */
    public double getMaxHealth() {
        return maxHealth;
    }                
    
    /**
     * Metoda ktora vrati typ entity v ramci sily. Pohyblive entity ako napriklad hrac
     * ma tuto hodnotu nastavenu na HAND. Metoda je dolezitou sucastou pri utoku na dlazdice aj entity.
     * Ked tento typ nedosahuje uroven entity na ktoru utocime tak sa ziadny utok nevykona.
     * @return Typ entity v ramci sily
     * @see ItemLevelType
     */
    public ItemLevelType getLevelType() {
        return levelType;
    }
         
    /**
     * Metoda ktora vrati aktivny predmet tejto entity. Aktivny predmet moze byt aj sama entita. 
     * To znamena ze entita nema nic a utoci holymi rukami.
     * @return Aktivny predmet tejto entity.
     */
    public Item getActiveItem() {        
        return activeItem;
    }
    
    /**
     * Metoda ktora vrati ci je mozne entitu posuvat.
     * @return True/false ci je entita posuvatelna.
     */
    public boolean isPushable() {
        return pushable;
    }
    
    /**
     * Metoda ktora vrati ci je entita aktivna.
     * @return Aktivnost entity.
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Metoda ktora vrati obrazok entity v aktualnom stave. Vacsinou je tuto metodu
     * nutne pretazit.     
     * @return Obrazok entity v aktualnom stave (teraz null kedze kazda entita si pretazuje metodu sama)
     */
    public Image getTypeImage() {        
        return null;
    }
    
    /**
     * Metoda ktora vrati pocet entit v sebe. Mozne pretazit roznymi sposobmi. V hracovom ponimani
     * to moze predstavovat pocet zivotov. V predmetovom ponimani to predstavuje pocet predmetov
     * v staku jedneho predmetu.
     * @return Pocet entit v jednom staku.
     */
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
    
    public void setActiveItem(Item item) {
        this.activeItem = item;
        if (this != item) {
            this.attackPower += item.attackPower;
        }
    }
    
    public void setSaved(Boolean saved) {
        this.saveInProgress = saved;
    }                
    
    public void setItemLevelType(ItemLevelType levelType) {
        this.levelType = levelType;
    }
    
    /**
     * Metoda ktora nastavi chunk v ktorom sa entita nachadza
     * @param chunk Chunk v ktorom sa nachadza entita.
     */
    public void setChunk(Chunk chunk) {
        this.actualChunk = chunk;
    } 
    
    public void forceChunk() {
        this.actualChunk = map.chunkXYExist(xPix, yPix);
    }
    
    public void setLevel(int level) {
        this.level = level;
    }
    
    // </editor-fold>
    
    public boolean update() {
        return true;
    }
    
    /**
     * Metoda ktora pohne automaticky entitou do prislusnej strany podla premennej
     * <b>spriteType</b>. Metoda je vacsinou vyuzita pri prechode medzi poschodiami, kde je potrebne automaticky pohnut hracom
     * aby sa aktualizovali pohyby (mozne je znova skocit do jamy co znamena nasledne spadnutie naspat)
     */
    protected void autoMove() {
        if (spriteType == Type.RIGHT) {
            canMove(2,0,0);
        }
        if (spriteType == Type.LEFT) {
            canMove(-2,0,0);
        }
        if (spriteType == Type.DOWN) {
            canMove(0,2,0);
        }
        if (spriteType == Type.UP) {
            canMove(0,-2,0);
        }
    }
    
    public void placeTile(Tile tile) {        
        int tileX = getTileX(), tileY = getTileY();                
        System.out.println(actualChunk.getTile(level - 1, tileX, tileY));
        if (actualChunk.setTile(level-1, tileX, tileY, tile.getId())) {            
            actualChunk.setMeta(level-1, tileX, tileY, tile == null ? 0 : tile.getHealth());
        } else {
            actualChunk.setTile(level, tileX, tileY, tile.getId());
            actualChunk.setMeta(level, tileX, tileY, tile == null ? 0 : tile.getHealth());
            this.level++;
            autoMove();
        }        
    }
    
    public void placeItem(Entity e) {
        if (e instanceof TileItem) {
            TileItem tileItem = (TileItem)e;
            placeTile(tileItem.getTile());
            return;
        }
    }
    
    protected int rollRandom(int min, int max) {
        return random.nextInt(max - min) + min; 
    }
    
    protected double rollRandom(double min, double max) {
        Double num = max - min;
        return (random.nextDouble() * num) + num;
    }
    
    /**
     * Metoda ktora posunie entitu o x-ovu aj y-ovu poziciu. Posuny su dane parametrami
     * <b>xGo</b> a <b>yGo</b>. Posuny sa pripocitaju k terajsim suradniciam a nasledne
     * testuje ci je mozny pohyb do tejto strany. Test pohybu je uspesny ked v smere pohybu neexistuje ziadna
     * prekazka v zmysle nepriechodnej dlazdice ci  dlazdice vytrcajucej zvrchneho poschodia.
     * @param xGo Posun po x-ovej suradnici
     * @param yGo Posun po y-ovej suradnici.
     * @return True/False ci sa podarilo pohnut
     */
    public boolean canMove(int xGo, int yGo, int z) {
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
        int z0 = level + z;
        
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
            actualChunk = map.chunkXYExist(regX, regY);            
            reloadChunks = true;
        }
        if (chunk != null && actualChunk != null) {            
            int lTile = actualChunk.getTile(z0 - 1, x0 >> 5, y0 >> 5);
            int uTile = actualChunk.getTile(z0, x0 >> 5, y0 >> 5);
            //debugCoordinateswithTiles(x0, y0, tile);        
            //debugCoordinateswithTiles(xx, yy, tile);

            // ked hrac alebo entita obsahuje dlazdicu na prislusnej strany 
            // v nepriechodnych dlazdiciach tak sa nepohne           
            if (impassableTiles.contains(lTile) || !(uTile == DefaultTiles.BLANK_ID)) {
                actualChunk = chunk;
                reloadChunks = false;
                return false; 
            }

            // skusa pohyb do prislusnej strany a vykona taku akciu 
            // ktora je definovana pri dlazdici
            Tile.tiles.get(lTile).moveInto(this);           

        } else {
            actualChunk = chunk;
            return false;
        }                                                               

        setXYPix(x0, y0, z0);        
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
     * Metoda setXYPix ma za ulohy nastavit x-ove a y-ove suradnice entity.
     * @param xPix Parameter na aku sa ma zmenit surandnica x
     * @param yPix Parameter na aku sa ma zmenit surandnica y
     */
    public void setXYPix(int xPix, int yPix, int level) {
        this.xPix = xPix;
        this.yPix = yPix;
        this.level = level;
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

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {                        
        out.writeUTF(name);
        out.writeBoolean(active);
        out.writeUTF(res.getId());        
        out.writeInt(xPix);
        out.writeInt(yPix);
        if (activeItem == null) {
            out.writeInt(-1);
        } else {
            out.writeInt(inventory.indexOf(activeItem));
        }
        out.writeInt(group);
        out.writeInt(level);
        out.writeDouble(health); 
        out.writeBoolean(pushable);
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
        
        int indexOfActive = in.readInt();
        if (indexOfActive == -1) {
            this.activeItem = null;                     
        } else {
            this.activeItem = inventory.get(indexOfActive);
        }
        this.group = in.readInt();
        this.level = in.readInt();
        this.health = in.readDouble();
        this.pushable = in.readBoolean();
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
