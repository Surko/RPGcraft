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
import javax.swing.text.Position;
import rpgcraft.effects.Effect;
import rpgcraft.effects.Effect.EffectEvent;
import rpgcraft.entities.Armor.ArmorType;
import rpgcraft.entities.types.ItemLevelType;
import rpgcraft.graphics.ui.particles.BarParticle;
import rpgcraft.graphics.spriteoperation.Sprite;
import rpgcraft.graphics.spriteoperation.Sprite.Type;
import rpgcraft.map.SaveMap;
import rpgcraft.map.chunks.Chunk;
import rpgcraft.map.tiles.DefaultTiles;
import rpgcraft.map.tiles.Tile;
import rpgcraft.panels.listeners.Action;
import rpgcraft.panels.listeners.ActionEvent;
import rpgcraft.resource.ConversationGroupResource;
import rpgcraft.resource.ConversationResource;
import rpgcraft.resource.EntityResource;
import rpgcraft.resource.StatResource.Stat;
import rpgcraft.utils.DataUtils;
import rpgcraft.utils.MainUtils;
import rpgcraft.utils.Pair;

/**
 *
 * @author Kirrie
 */
public abstract class Entity implements Externalizable {    
    protected static final int STATMULT = 5;
    protected static final int BASESTAT = 5;
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
    protected double randomValue;
    
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
    // Univerzalne id z hash.
    protected long uniId;
    
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
    protected int level,xPix, yPix, x, y;
    //protected EntityPosition position;
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
    protected double healthRegen;
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
    protected int attackRating;
    protected int defenseRating;
    
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
    public abstract void unequip(Item e);    
    public abstract void equip(Item e);        
    public abstract void setImpassableTile(int tile);
    public abstract void use(Item item);
    public abstract void drop(Item item);
    public abstract double hit(double damage, rpgcraft.graphics.spriteoperation.Sprite.Type type);
    public abstract boolean updateCoordinates();
    public abstract void pushWith(Entity e);    
    public abstract double interactWithEntities(int x0, int y0, int x1, int y1, double modifier);    
    public abstract void knockback(rpgcraft.graphics.spriteoperation.Sprite.Type type);    
    
    // </editor-fold>
    
    public void initialize() {
        this.uniId = MainUtils.objectId++;
        this.activeEffects = new HashMap<>();        
        if (res.getEntityEffects() != null) {            
            initializeEffects();
        }
        
        doubleStats = new HashMap<>();
        intStats = new HashMap<>();
        /**
         * V tejto casti sa nachadzaju staty ulozene v hashmapach. Tento pristup som volil kvoli tomu
         * aby som potom mohol lahsie pridavat efekty ku zbraniam ci utokom, ktore by menili tieto staty.
         * Nasledne by som menil iba tie atributy ktore su ovplyvnene statom ktory sme menili.
         */
        
        // Premenna ktora urcuje vahu pre attackRating. #attackRating / #attackRater = #attackPower         
        intStats.put(Stat.ATKRATER, res.getAttackRater());
        // Premenna ktora urcuje vahu pre defenseRating. #defenseRating / #defenseRater = #defensePower        
        intStats.put(Stat.DEFRATER, res.getDefenseRater());
        // Premenna ktora urcuje vahu pre speed. #speedRating / #speedRater = #speedPower 
        intStats.put(Stat.SPDRATER, res.getSpeedRater());
        // Premenna ktora urcuje vahu pre damage. #Strength / #damageRater = #damagePower  
        intStats.put(Stat.DMGRATER, res.getDamageRater());
        // Premenna ktora urcuje vahu pre health a staminu. #Endurance / #rater = #regen.
        intStats.put(Stat.HPSPRATER, res.getRater());
        
        doubleStats.put(Stat.HEALTHMAXPER, res.getHealthPer());
        doubleStats.put(Stat.STAMINAMAXPER, res.getStaminaPer());
        doubleStats.put(Stat.SPEEDPER, res.getSpeedPer());
        doubleStats.put(Stat.STRENGTHPER, res.getStrengthPer());
        doubleStats.put(Stat.AGILITYPER, res.getAgilityPer());
        doubleStats.put(Stat.ENDURANCEPER, res.getEndurancePer());
        doubleStats.put(Stat.HEALTHREGENPER, res.getHealthRegenPer());
        doubleStats.put(Stat.STAMINAREGENPER, res.getStaminaRegenPer());
        doubleStats.put(Stat.ATKRATINGPER, res.getAtkRatingPer());
        doubleStats.put(Stat.DEFRATINGPER, res.getDefRatingPer());
        doubleStats.put(Stat.ATKRADIUSPER, res.getAtkRadiusPer());
        doubleStats.put(Stat.DAMAGEPER, res.getDamagePer());
            
        try {                                 
            intStats.put(Stat.STRENGTH, rollRandom(res.getMinStr(), res.getMaxStr()));
            intStats.put(Stat.AGILITY, rollRandom(res.getMinAgi(), res.getMaxAgi()));
            intStats.put(Stat.SPEED, rollRandom(res.getMinSpd(), res.getMaxSpd()));
            intStats.put(Stat.ENDURANCE, rollRandom(res.getMinEnd(), res.getMaxEnd()));
            
            // Hlavne Staty
            doubleStats.put(Stat.HEALTHMAX, rollRandom(res.getMinHealth(), res.getMaxHealth()));
            doubleStats.put(Stat.STAMINAMAX, rollRandom(res.getMinStamina(), res.getMaxStamina()));            
                        
            // Damage 
            doubleStats.put(Stat.DAMAGE, (double)intStats.get(Stat.STRENGTH) / getInt(Stat.DMGRATER, 1));
            doubleStats.put(Stat.DMGBONUS, (double)rollRandom(res.getMinDamage(), res.getMaxDamage()));
        } catch (Exception e) {
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, 
                "Xml file has min value greater than max :\n" + e.getMessage() );
        }
        // O kolko sa doplni stamina
        doubleStats.put(Stat.STAMINAREGENBONUS, res.getStaminaRegen());
        doubleStats.put(Stat.STAMINABONUS,  res.getStaminaBonus());
        doubleStats.put(Stat.STAMINAREGEN, res.getStaminaRegen() +
                intStats.get(Stat.ENDURANCE) / getInt(Stat.HPSPRATER, 1));
        // O kolko sa doplni zivot
        doubleStats.put(Stat.HEALTHBONUS, res.getHealthBonus());
        doubleStats.put(Stat.HEALTHREGEN, res.getHealthRegen());
        // Attack/Defense Rating
        intStats.put(Stat.ATKRATINGBONUS, res.getAttRatingBonus());
        intStats.put(Stat.ATKRATING, (intStats.get(Stat.AGILITY) * STATMULT) 
                + (intStats.get(Stat.SPEED) * (STATMULT - 3)) + 
                (activeItem != null ? activeItem.attackRating : 0) + intStats.get(Stat.ATKRATINGBONUS));
        intStats.put(Stat.DEFRATINGBONUS, res.getDefRatingBonus());
        intStats.put(Stat.DEFRATING, (intStats.get(Stat.AGILITY) * STATMULT)
                + (intStats.get(Stat.SPEED) * (STATMULT - 3)) +
                (activeItem != null ? activeItem.attackRating : 0) + intStats.get(Stat.DEFRATINGBONUS));            
        // Attack/Defense Power
        doubleStats.put(Stat.ATKPOWER, (double)intStats.get(Stat.ATKRATING) / getInt(Stat.ATKRATER, 1));
        doubleStats.put(Stat.DEFPOWER, (double)intStats.get(Stat.DEFRATING) / getInt(Stat.DEFRATER, 1));
        
        doubleStats.put(Stat.DBLFACTOR, res.getDoubleFactor());
        // Sanca na prerusenie - aku sancu ma entita ze prerusi cudzi utok
        doubleStats.put(Stat.INTCHANCE, res.getInterChance());
        // Koncetracia - kolko sa odcitava od nepriatelovej interruptionChance
        // Vzdialenost ako daleko dosiahne utok. Pri nule je schopna entita iba posuvat.
        intStats.put(Stat.ATKRADIUS, res.getAttackRadius());        
    }
    
    /**
     * Metoda ktora inicializuje efekty. Pri efekte typu ONSELF prida  efekt do listu
     * onSelfEffects. Pri ostatnych typoch pridava efekty do mapy.
     */
    protected void initializeEffects() {
        for (Effect effect : res.getEntityEffects()) {
            if (effect.getEvent().equals(EffectEvent.ONSELF)) {
                map.addEffect(effect);
                continue;
            }
            
            if (!activeEffects.containsKey(effect.getEvent())) {
                activeEffects.put(effect.getEvent(), new ArrayList<Effect>());
            }
            activeEffects.get(effect.getEvent()).add(new Effect(effect, this, this));
        } 
    }
    
    protected void reinitialize() {    
        this.res = EntityResource.getResource(id);
        if (res.getEntityEffects() != null) {
            if (activeEffects == null) {
                activeEffects = new HashMap<>();
            }
            for (Effect effect : res.getEntityEffects()) {
                if (effect.getEvent().equals(EffectEvent.ONSELF)) {                
                    continue;
                }
                
                
                
                if (!activeEffects.containsKey(effect.getEvent())) {
                    activeEffects.put(effect.getEvent(), new ArrayList<Effect>());
                }
                activeEffects.get(effect.getEvent()).add(new Effect(effect, this, this));
            }
        }
        
    }
    
    public String getInfo() {
        if (res != null) {
            return res.getInfo();
        } else {
            return null;
        }
    }
    
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
     * Metoda ktora vrati efekty z activeEffects pod klucom zadanym parametrom <b>event</b>
     * @param event Event ktoreho efekty vratime
     * @return Efekty podla eventu.
     */
    public ArrayList<Effect> getEffects(EffectEvent event) {
        return activeEffects.get(event);
    }
    
    /**
     * Metoda ktora vrati grupove konverzacie. Tieto konverzacie a ich nazvy su zobrazene 
     * v konverzacii. Pri oznaceni jednej grupy sa vyberie konverzacia ktora pasuje.
     * @return List s konverzacnymi grupami ktore su pre entitu mozne.
     */
    public ArrayList<ConversationGroupResource> getGroupConversations() {
        return res.getGroupConversations();
    }
    
    /*    
    public EntityPosition getPosition() {
        if (position == null) {
            position = new EntityPosition(x, y);
        }
        return position;
    }
    */
        
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
    
    /**
     * Metoda ktora prida/zvacsi counter predmetu v inventari na indexe zadanom parametrom <b>index</b>
     * @param index Index predmetu ktoreho pocet zvysujeme.
     */
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
            if (item.isActive()) {
                this.decStats(item);                
            }
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
                    case -1 : {
                        if (toRem.isActive()) {
                            this.decStats(toRem);
                            System.out.println(toString());
                        }                        
                        inventory.remove(index);
                    } break;
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
     * Metoda ktora vrati aktivne efekty (onSelfEffects) na entite.
     * @return Aktivne efekty
     */
    public ArrayList<Effect> getActiveEffects() {
        return map.getEffects(this);
    }
    
    /**
     * Metoda ktora vrati Sprite.Type => typ co entita robi.
     * @return SpriteType entity.
     */
    public Sprite.Type getType() {
        return spriteType;
    }
    
    /**
     * Metoda ktora vrati true/false podla toho ci sa entita akurat uklada na disk.
     * @return True/false ci sa entita uklada na disk.
     */
    protected boolean isSaving() {
        return saveInProgress;
    }
    
    /**
     * Metoda ktora vrati univerzalne id entity.
     * @return Univerzalne id.
     */
    public long getUniId() {
        return uniId;
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
        return tileX & Chunk.CHUNKMOD;
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
        return tileY & Chunk.CHUNKMOD;
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
     * Metoda ktora vrati predmet ktory je rovnaky ako ten zadany parametrom <b>item</b>
     * @param item Predmet ktory chcem z inventara vratit
     * @return Predmet z inventara podobny ako parameter
     */
    public Item getItem(Item item) {
        if (inventory != null) {
            for (Item _item : inventory) {
                if (_item.equals(item)) {
                    return item;
                }
            }
        }
        return null;
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
     * Metoda ktora vrati aktualnu staminu tejto entity.
     * @return Aktualna stamina entity
     */
    public double getStamina() {
        return 0d;
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
    
    /**
     * Metoda ktora vrati nahodnu hodnotu premennej. Random value byva
     * vacsinou vyuzita inteligenciou pri utoceni a obrane.
     * @return Nahodnu hodnotu premennej.
     */
    public double getRandomValue() {
        return randomValue;
    }
    
    public String getStatValue(Stat stat, boolean trim) {
        if (!trim) {
            return getStatValue(stat);
        } else {
            Integer value = doubleStats != null ? doubleStats.get(stat).intValue() : null;
            if (value == null) {
                value = intStats != null ? intStats.get(stat) : null;
            }
            if (value == null) {
                return "0";
            }
            return value.toString();
        }
    }
    
    /**
     * Metoda ktora vrati hodnotu statu ako String
     * @param stat Stat ktoreho hodnotu chceme
     * @return Stringova hodnota statu.
     */
    public String getStatValue(Stat stat) {
        Object value = doubleStats != null ? doubleStats.get(stat) : null;
        if (value == null) {
            value = intStats != null ? intStats.get(stat) : null;
        }
        if (value == null) {
            return "0";
        }
        return value.toString();
    }
    
    
    public int getInt(Stat stat, int ret) {
        Integer value = intStats.get(stat);
        if (value == null || value == 0d) {
            return ret;
        }
        return value;
    }
    
    public double getDouble(Stat stat, int ret) {
        Double value = doubleStats.get(stat);
        if (value == null || value == 0d) {
            return ret;
        }
        return value;
    }
    
    /**
     * Metoda ktora vrati cislo grupy do ktorej patri entita.
     * @return Cislo grupy entity
     */
    public int getGroup() {
        return group;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    
    /**
     * Metoda ktora nastavi hlucnost entity podla parametru <b>sound</b>
     * @param sound Hlucnost entity
     */
    public void setSound(int sound) {
        this.sound = sound;
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
     * Metoda ktora nastavi hodnotu randomValue na tu v parametri <b>value</b>. Random value byva
     * nastavena vacsinou inteligenciou pri utoceni a obrane.
     * @param value Hodnota nahodnej premennej
     */
    public void setRandomValue(double value) {
        this.randomValue= value;
    }
    
    public void clearPowers() {
        acumPower = 0;
        acumDefense = 0;
    }
    
    public void incAcumPower(double power, double modifier, double value) {
        acumPower += power * modifier + value;
    }
    
    public void incAcumDefense(double power, double modifier, double value) {
        acumDefense += power * modifier + value;
    }
    
    public void incHealth(double health) {
        this.health += health;
    }
    
    public void regenHealth(double health) {
        this.health += health;
        if (this.health > maxHealth) {
            this.health = maxHealth;
        }
    }
    
    public void regenStamina(double stamina) {
        
    }
    
    public void incStamina(double stamina) {
        
    }
    
    public void incMana(double mana) {
        
    }
    
    public void decLevel() {
        this.level--;
    }
    
    public void incLevel() {
        this.level++;
    }
    
    public void removeEffects(Item item, Effect.EffectEvent event) {
        if (item.activeEffects == null || item.activeEffects.isEmpty()) {
            return;
        }
        ArrayList<Effect> effects = item.activeEffects.get(event);
        if (effects == null || effects.isEmpty()) {
            return;
        }
        
        for (Effect effect : effects) {
            effect.quit();
        } 
    }
    
    public void addEffects(Item item, Effect.EffectEvent event) {
        if (item.activeEffects == null || item.activeEffects.isEmpty()) {
            return;
        }
        ArrayList<Effect> effects = item.activeEffects.get(event);
        if (effects == null || effects.isEmpty()) {
            return;
        }
        
        for (Effect effect : effects) {
            if (effect.isSelf()) {
                effect.reset(); 
                continue;
            }                                         
            effect.setEntities(item, this);
            effect.setSelf();
            map.addEffect(effect);
        } 
    }
    
    public void setActiveItem(Item item) {   
        if (this.activeItem != null && this.activeItem != item) {
            decStats(this.activeItem);
            this.activeItem.active = false;
        }
        if (item != null) {            
            incStats(item);
            item.active = true;
        }
        this.activeItem = item;
        
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
    
    public void activate(Entity e) {
        ArrayList<Action> activationActions = e.res.getActivateActions();
        if (activationActions != null) {
            ActionEvent event = new ActionEvent(map.getMenu(), 0, -1, null, new Pair(this, e));
            for (Action action : activationActions) {
                Action _action = new Action(action);
                _action.setActionEvent(event);
                DataUtils.execute(action);
            }
        }
    }
    
    public boolean update() {                
        return true;
    }
    
    /**
     * Metoda ktora pohne automaticky entitou do prislusnej strany podla premennej
     * <b>spriteType</b>. Metoda je vacsinou vyuzita pri prechode medzi poschodiami, kde je potrebne automaticky pohnut hracom
     * aby sa aktualizovali pohyby (mozne je znova skocit do jamy co znamena nasledne spadnutie naspat)
     * @return true/false ci sme sa mohli pohnut
     */
    protected boolean autoMove(int level) {
        if (spriteType == Type.RIGHT) {
            return canMove(2,0,level);
        }
        if (spriteType == Type.LEFT) {
            return canMove(-2,0,level);
        }
        if (spriteType == Type.DOWN) {
            return canMove(0,2,level);
        }
        if (spriteType == Type.UP) {
            return canMove(0,-2,level);
        }
        return false;
    }
    
    /**
     * Metoda ktora umiestni dlazdicu zadanu parametrom <b>tile</b> pod seba
     * @param tile 
     */
    public void placeTile(Tile tile, int modX, int modY, int modZ) {        
        int tileX = getTileX(), tileY = getTileY();                
        System.out.println(actualChunk.getTile(level - 1, tileX, tileY));
        if (!actualChunk.setTile(level+modZ, tileX + modX, tileY + modY, tile.getId(), tile == null ? 0 : tile.getHealth())) {            
            actualChunk.setTile(level, tileX + modX, tileY + modY, tile.getId(), tile == null ? 0 : tile.getHealth());
            this.level++;
            if (autoMove(0)) {
                map.setLevel(level);
            }
        }       
    }
    
    public void placeItem(Entity e) {
        if (e instanceof TileItem) {
            TileItem tileItem = (TileItem)e;
            placeTile(tileItem.getTile(), 0, 0, -1);
            return;
        }
    }
    
    protected int rollRandom(int min, int max) {
        int num = max - min;
        if (num <= 0) {
            return min;
        }
        return random.nextInt(max - min) + min; 
    }
    
    protected double rollRandom(double min, double max) {
        double num = max - min;
        if (num <= 0) {
            return min;
        }
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
        
        
        for (Entity e : map.getNearEntities()) {
            
            if (e == this || e.level != this.level) continue;                                                    
                                           
            if (!(e instanceof Item) && targetEntity == null && e.group != this.group) {
                this.targetEntity = e;
            }    
            
            if (e.isPushable()) {
                if (entityPush(e, x0, y0)) {
                    return false;
                }                
            }                         
            
        }
        
        Chunk chunk = actualChunk;        
        if ((actualChunk.getX() != regX)||(actualChunk.getY() != regY)) {              
            actualChunk = map.chunkXYExist(regX, regY);            
            reloadChunks = true;
        }
        if (chunk != null && actualChunk != null) {            
            return tryMove(x0, y0, z0, chunk);           
        } else {
            actualChunk = chunk;
            return false;
        }                                                                                                      
    }
    
    public boolean tryMove(int x0, int y0, int z0, Chunk backChunk) {
        
        int lTile = actualChunk.getTile(z0 - 1, x0 >> 5, y0 >> 5);
        int uTile = actualChunk.getTile(z0, x0 >> 5, y0 >> 5);
        //debugCoordinateswithTiles(x0, y0, tile);        
        //debugCoordinateswithTiles(xx, yy, tile);

        // ked hrac alebo entita obsahuje dlazdicu na prislusnej strany 
        // v nepriechodnych dlazdiciach tak sa nepohne           
        if (impassableTiles.contains(lTile) || !(uTile == DefaultTiles.BLANK_ID)) {
            actualChunk = backChunk;
            reloadChunks = false;
            return false; 
        }                       

        // skusa pohyb do prislusnej strany a vykona taku akciu 
        // ktora je definovana pri dlazdici
        setXYPix(x0, y0, z0);
        Tile.tiles.get(lTile).moveInto(this);
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
    protected boolean entityPush(Entity entity, int x0, int y0) {
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
        /*
        if (position.changePosition(x, y)) {
            map.removeFromPos(this);
        }
        */
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
    
    public void decStats(Entity item) {
        HashMap<Stat, Double> itemDoubleStats = item.doubleStats;
        HashMap<Stat, Integer> itemIntStats = item.intStats;                 
        
        intStats.put(Stat.ATKRADIUS, getInt(Stat.ATKRADIUS, 0) - item.getInt(Stat.ATKRADIUS, 0));
        intStats.put(Stat.STRENGTH, (int)((double)getInt(Stat.STRENGTH, 0) / (1 + getDouble(Stat.STRENGTHPER, 0))
                - item.getInt(Stat.STRENGTH, 0)));
        intStats.put(Stat.AGILITY, (int)((double)getInt(Stat.AGILITY, 0) / (1 + getDouble(Stat.AGILITYPER, 0))
                - item.getInt(Stat.AGILITY, 0)));
        intStats.put(Stat.SPEED, (int)((double)getInt(Stat.SPEED, 0) / (1 + getDouble(Stat.SPEEDPER, 0))
                - item.getInt(Stat.SPEED, 0)));
        intStats.put(Stat.ENDURANCE, (int)((double)getInt(Stat.ENDURANCE, 0) / (1 + getDouble(Stat.ENDURANCEPER, 0))
                - item.getInt(Stat.ENDURANCE, 0)));
        
        intStats.put(Stat.ATKRATINGBONUS, getInt(Stat.ATKRATINGBONUS, 0) + item.getInt(Stat.ATKRATINGBONUS, 0));
        intStats.put(Stat.ATKRATING, (int)((intStats.get(Stat.AGILITY) * STATMULT 
                    + (intStats.get(Stat.SPEED) * (STATMULT - 3)) + intStats.get(Stat.ATKRATINGBONUS)) *
                (1 + doubleStats.get(Stat.ATKRATINGPER)/100)));
        intStats.put(Stat.DEFRATINGBONUS, getInt(Stat.DEFRATINGBONUS, 0) + item.getInt(Stat.DEFRATINGBONUS, 0));
        intStats.put(Stat.DEFRATING, (int)((intStats.get(Stat.AGILITY) * STATMULT 
                    + (intStats.get(Stat.SPEED) * (STATMULT - 3)) + intStats.get(Stat.DEFRATINGBONUS)) *
                (1 + doubleStats.get(Stat.DEFRATINGPER)/100)));                
        doubleStats.put(Stat.DMGBONUS, (double)getInt(Stat.DMGBONUS,0) + item.getInt(Stat.DMGBONUS, 0));
        doubleStats.put(Stat.DAMAGE, intStats.get(Stat.STRENGTH) / getInt(Stat.DMGRATER, 1)
                + doubleStats.get(Stat.DMGBONUS));
        doubleStats.put(Stat.ATKPOWER, (double)intStats.get(Stat.ATKRATING) / getInt(Stat.ATKRATER, 1));
        doubleStats.put(Stat.DEFPOWER, (double)intStats.get(Stat.DEFRATING) / getInt(Stat.DEFRATER, 1));
        doubleStats.put(Stat.STAMINAREGENBONUS, getDouble(Stat.STAMINAREGENBONUS, 0)
                - item.getDouble(Stat.STAMINAREGENBONUS, 0));
        doubleStats.put(Stat.STAMINAREGEN, intStats.get(Stat.ENDURANCE) / getInt(Stat.HPSPRATER, 1) 
                + doubleStats.get(Stat.STAMINAREGENBONUS));                
          
        // Zivot a stamina staty
        doubleStats.put(Stat.HEALTHMAX, getDouble(Stat.HEALTHMAX, 0) - getInt(Stat.HEALTHBONUS, 0));
        doubleStats.put(Stat.STAMINAMAX, getDouble(Stat.STAMINAMAX, 0) - getInt(Stat.STAMINABONUS, 0));
        intStats.put(Stat.HEALTHBONUS, getInt(Stat.HEALTHBONUS, 0) - item.getInt(Stat.HEALTHBONUS, 0));
        intStats.put(Stat.STAMINABONUS, getInt(Stat.STAMINABONUS, 0) - item.getInt(Stat.STAMINABONUS, 0));        
        doubleStats.put(Stat.HEALTHREGEN, getDouble(Stat.HEALTHREGEN, 0) - item.getDouble(Stat.HEALTHREGEN, 0));  
        doubleStats.put(Stat.DBLFACTOR, getDouble(Stat.DBLFACTOR, 0) - item.getDouble(Stat.DBLFACTOR, 0));                
        doubleStats.put(Stat.STAMINAMAX, getDouble(Stat.STAMINAMAX, 0) + intStats.get(Stat.STAMINABONUS));
        doubleStats.put(Stat.HEALTHMAX, getDouble(Stat.HEALTHMAX, 0) + intStats.get(Stat.HEALTHBONUS));
        
        doubleStats.put(Stat.HEALTHMAXPER, getDouble(Stat.HEALTHMAXPER,0)
                - item.getDouble(Stat.HEALTHMAXPER, 0));
        doubleStats.put(Stat.STAMINAMAXPER, getDouble(Stat.STAMINAMAXPER, 0) - item.getDouble(Stat.STAMINAMAXPER, 0));
        doubleStats.put(Stat.SPEEDPER, getDouble(Stat.SPEEDPER, 0) - item.getDouble(Stat.SPEEDPER, 0));
        doubleStats.put(Stat.STRENGTHPER, getDouble(Stat.STRENGTHPER, 0) - item.getDouble(Stat.STRENGTHPER, 0));
        doubleStats.put(Stat.AGILITYPER, getDouble(Stat.AGILITYPER, 0) - item.getDouble(Stat.AGILITYPER, 0));
        doubleStats.put(Stat.ENDURANCEPER, getDouble(Stat.ENDURANCEPER, 0) - item.getDouble(Stat.ENDURANCEPER, 0));
        doubleStats.put(Stat.HEALTHREGENPER, getDouble(Stat.HEALTHREGENPER, 0) - item.getDouble(Stat.HEALTHREGENPER, 0));
        doubleStats.put(Stat.STAMINAREGENPER, getDouble(Stat.STAMINAREGENPER, 0) - item.getDouble(Stat.STAMINAREGENPER, 0));
        doubleStats.put(Stat.ATKRATINGPER, getDouble(Stat.ATKRATINGPER, 0) - item.getDouble(Stat.ATKRATINGPER, 0));
        doubleStats.put(Stat.DEFRATINGPER, getDouble(Stat.DEFRATINGPER, 0) - item.getDouble(Stat.DEFRATINGPER, 0));
        doubleStats.put(Stat.ATKRADIUSPER, getDouble(Stat.ATKRADIUSPER, 0) - item.getDouble(Stat.ATKRADIUSPER, 0));
        doubleStats.put(Stat.DAMAGEPER, getDouble(Stat.DAMAGEPER, 0) - item.getDouble(Stat.DAMAGEPER, 0));
        
        this.damage = doubleStats.get(Stat.DAMAGE) * (1 + doubleStats.get(Stat.DAMAGEPER)/100);        
        this.attackPower = doubleStats.get(Stat.DEFPOWER);
        this.defensePower = doubleStats.get(Stat.DEFPOWER);
        this.maxHealth = doubleStats.get(Stat.HEALTHMAX) * (1 + doubleStats.get(Stat.HEALTHMAXPER));
        this.attackRadius = (int)(intStats.get(Stat.ATKRADIUS) * (1 + doubleStats.get(Stat.ATKRADIUSPER))); 
    }
    
    public void incStats(Entity item) {                    
        
        // Percentualne bonusy musia byt pridane ako prve aby sme ich mohli nakonci pripocitat.
        doubleStats.put(Stat.HEALTHMAXPER, getDouble(Stat.HEALTHMAXPER,0)
                + item.getDouble(Stat.HEALTHMAXPER, 0));
        doubleStats.put(Stat.STAMINAMAXPER, getDouble(Stat.STAMINAMAXPER, 0) + item.getDouble(Stat.STAMINAMAXPER, 0));
        doubleStats.put(Stat.SPEEDPER, getDouble(Stat.SPEEDPER, 0) + item.getDouble(Stat.SPEEDPER, 0));
        doubleStats.put(Stat.STRENGTHPER, getDouble(Stat.STRENGTHPER, 0) + item.getDouble(Stat.STRENGTHPER, 0));
        doubleStats.put(Stat.AGILITYPER, getDouble(Stat.AGILITYPER, 0) + item.getDouble(Stat.AGILITYPER, 0));
        doubleStats.put(Stat.ENDURANCEPER, getDouble(Stat.ENDURANCEPER, 0) + item.getDouble(Stat.ENDURANCEPER, 0));
        doubleStats.put(Stat.HEALTHREGENPER, getDouble(Stat.HEALTHREGENPER, 0) + item.getDouble(Stat.HEALTHREGENPER, 0));
        doubleStats.put(Stat.STAMINAREGENPER, getDouble(Stat.STAMINAREGENPER, 0) + item.getDouble(Stat.STAMINAREGENPER, 0));
        doubleStats.put(Stat.ATKRATINGPER, getDouble(Stat.ATKRATINGPER, 0) + item.getDouble(Stat.ATKRATINGPER, 0));
        doubleStats.put(Stat.DEFRATINGPER, getDouble(Stat.DEFRATINGPER, 0) + item.getDouble(Stat.DEFRATINGPER, 0));
        doubleStats.put(Stat.ATKRADIUSPER, getDouble(Stat.ATKRADIUSPER, 0) + item.getDouble(Stat.ATKRADIUSPER, 0));
        doubleStats.put(Stat.DAMAGEPER, getDouble(Stat.DAMAGEPER, 0) + item.getDouble(Stat.DAMAGEPER, 0));
                
        intStats.put(Stat.ATKRADIUS, getInt(Stat.ATKRADIUS, 0) + item.getInt(Stat.ATKRADIUS, 0));
        // Nastavenie zakladnych statov rovno aj s percentom
        intStats.put(Stat.STRENGTH, (int)((getInt(Stat.STRENGTH, 0) + item.getInt(Stat.STRENGTH, 0))
                * (1 + doubleStats.get(Stat.STRENGTHPER))));
        intStats.put(Stat.AGILITY, (int)((getInt(Stat.AGILITY, 0) + item.getInt(Stat.AGILITY, 0))
                * (1 + doubleStats.get(Stat.AGILITYPER))));
        intStats.put(Stat.SPEED, (int)((getInt(Stat.SPEED, 0) + item.getInt(Stat.SPEED, 0))
                * (1 + doubleStats.get(Stat.SPEEDPER))));
        intStats.put(Stat.ENDURANCE, (int)((getInt(Stat.ENDURANCE, 0) + item.getInt(Stat.ENDURANCE, 0)) 
                * (1 + doubleStats.get(Stat.ENDURANCEPER))));        
        
        // Zivot a stamina staty
        intStats.put(Stat.HEALTHBONUS, getInt(Stat.HEALTHBONUS, 0) + item.getInt(Stat.HEALTHBONUS, 0));
        intStats.put(Stat.STAMINABONUS, getInt(Stat.STAMINABONUS, 0) + item.getInt(Stat.STAMINABONUS, 0));        
        doubleStats.put(Stat.HEALTHREGEN, getDouble(Stat.HEALTHREGEN, 0) + item.getDouble(Stat.HEALTHREGEN, 0));  
        doubleStats.put(Stat.DBLFACTOR, getDouble(Stat.DBLFACTOR, 0) + item.getDouble(Stat.DBLFACTOR, 0));                
        doubleStats.put(Stat.HEALTHMAX, getDouble(Stat.HEALTHMAX, 0) + intStats.get(Stat.HEALTHBONUS));
        doubleStats.put(Stat.STAMINAMAX, getDouble(Stat.STAMINAMAX, 0) + intStats.get(Stat.STAMINABONUS));
        
        
        // Nadvazajuce staty
        intStats.put(Stat.ATKRATINGBONUS, getInt(Stat.ATKRATINGBONUS, 0) + item.getInt(Stat.ATKRATINGBONUS, 0));
        intStats.put(Stat.ATKRATING, (int)((intStats.get(Stat.AGILITY) * STATMULT 
                    + (intStats.get(Stat.SPEED) * (STATMULT - 3)) + intStats.get(Stat.ATKRATINGBONUS)) *
                (1 + doubleStats.get(Stat.ATKRATINGPER)/100)));
        intStats.put(Stat.DEFRATINGBONUS, getInt(Stat.DEFRATINGBONUS, 0) + item.getInt(Stat.DEFRATINGBONUS, 0));
        intStats.put(Stat.DEFRATING, (int)((intStats.get(Stat.AGILITY) * STATMULT 
                    + (intStats.get(Stat.SPEED) * (STATMULT - 3)) + intStats.get(Stat.DEFRATINGBONUS)) *
                (1 + doubleStats.get(Stat.DEFRATINGPER)/100)));                
        doubleStats.put(Stat.DMGBONUS, (double)getInt(Stat.DMGBONUS,0) + item.getInt(Stat.DMGBONUS, 0));
        doubleStats.put(Stat.DAMAGE, intStats.get(Stat.STRENGTH) / getInt(Stat.DMGRATER, 1)
                + doubleStats.get(Stat.DMGBONUS));
        doubleStats.put(Stat.ATKPOWER, (double)intStats.get(Stat.ATKRATING) / getInt(Stat.ATKRATER, 1));
        doubleStats.put(Stat.DEFPOWER, (double)intStats.get(Stat.DEFRATING) / getInt(Stat.DEFRATER, 1));
        doubleStats.put(Stat.STAMINAREGENBONUS, getDouble(Stat.STAMINAREGENBONUS, 0)
                + item.getDouble(Stat.STAMINAREGENBONUS, 0));
        doubleStats.put(Stat.STAMINAREGEN, intStats.get(Stat.ENDURANCE) / getInt(Stat.HPSPRATER, 1) 
                + doubleStats.get(Stat.STAMINAREGENBONUS));                
            
        
        // Priame dosadzovanie
        this.damage = doubleStats.get(Stat.DAMAGE) * (1 + doubleStats.get(Stat.DAMAGEPER)/100);        
        this.attackPower = doubleStats.get(Stat.DEFPOWER);
        this.defensePower = doubleStats.get(Stat.DEFPOWER);
        this.maxHealth = doubleStats.get(Stat.HEALTHMAX) * (1 + doubleStats.get(Stat.HEALTHMAXPER));
        this.attackRadius = (int)(intStats.get(Stat.ATKRADIUS) * (1 + doubleStats.get(Stat.ATKRADIUSPER)));        
        
    }
    
    /**
     * Metoda ktora zvysi integerovy stat/hodnotu statu zadany parametrom <b>stat</b>. Ci sa pricitava alebo odcitava
     * hodnota value rozhoduje parameter signature. Posledny parameter urcuje ci sa pricitava alebo
     * odcitava nejaky percentualny parameter.
     * @param stat Stat ktory menime
     * @param signature +/-1 ako znamienko
     * @param value Hodnota ktora sa podla znamienka pricita/odcita
     */
    protected void incIntStat(Stat stat, int signature, int value) {                
        intStats.put(stat, (int)(intStats.get(stat) + signature * value));
    }
    
    /**
     * Metoda ktora zvysi double stat/hodnotu statu zadany parametrom <b>stat</b>.Ci sa pricitava alebo odcitava
     * hodnota value rozhoduje parameter signature. 
     * @param stat Stat ktory menime 
     * @param signature +/-1 ako znamienko
     * @param value Hodnota ktora sa podla znamienka pricita/odcita
     */
    protected void incDoubleStat(Stat stat, int signature, double value) {             
        doubleStats.put(stat, doubleStats.get(stat) + signature * value);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {  
        out.writeLong(uniId);
        out.writeUTF(name);
        out.writeBoolean(active);
        out.writeUTF(res == null ? id : res.getId());        
        out.writeInt(xPix);
        out.writeInt(yPix);        
        out.writeInt(group);
        out.writeInt(level);
        out.writeDouble(health);         
        out.writeBoolean(pushable);
        out.writeObject(intStats);
        out.writeObject(doubleStats);
        out.writeUTF(spriteType.name());
        out.writeInt(sprNum);
        out.writeObject(impassableTiles);        
        out.writeObject(inventory);
        if (activeItem == null) {
            out.writeInt(-1);
        } else {
            out.writeInt(inventory.indexOf(activeItem));
        }
        
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.uniId = in.readLong();
        this.name = in.readUTF();
        this.active = in.readBoolean();
        this.id = in.readUTF();
        this.xPix = in.readInt();
        this.yPix = in.readInt();                       
        this.group = in.readInt();
        this.level = in.readInt();
        this.health = in.readDouble();        
        this.pushable = in.readBoolean();
        this.intStats = (HashMap<Stat, Integer>) in.readObject();
        this.doubleStats = (HashMap<Stat, Double>) in.readObject();
        this.spriteType = Type.valueOf( in.readUTF() );
        this.sprNum = in.readInt();
        this.impassableTiles = (ArrayList<Integer>)in.readObject();             
        this.inventory = (ArrayList<Item>)in.readObject();
        int indexOfActive = in.readInt(); 
        if (indexOfActive == -1) {
            this.activeItem = null;                     
        } else {
            this.activeItem = inventory.get(indexOfActive);
        }
        
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
