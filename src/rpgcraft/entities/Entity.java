/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities;

import rpgcraft.plugins.Ai;
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
import rpgcraft.effects.Effect.EffectEvent;
import rpgcraft.entities.Armor.ArmorType;
import rpgcraft.entities.Weapon.WeaponType;
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
import rpgcraft.resource.EntityResource;
import rpgcraft.resource.StatResource.Stat;
import rpgcraft.utils.DataUtils;
import rpgcraft.utils.MainUtils;
import rpgcraft.utils.Pair;

/**
 * Abstraktna trieda Entity implementujuca Externalizable tvori kontajner pre vsetky entity nachadzajuce sa v mape.
 * Kazdy druh entity dedi od tejto triedy. Trieda obsahuje zakladne premenne ako je zivot entity, pozicie
 * entity, univerzalneId, originalny resource pre entitu,
 * vlastnosti entity ulozene v doubleStats a intStats, priznaky pre existencie, nesmrtelnost, atd.
 * Navyse trieda zdruzuje zakladne metody pre pracu s entitou ako su poskodenie entity, alebo
 * aj donuteny pohyb (aj ked nie je entita pohybliva). Kedze je trieda abstraktna tak nie je moznost
 * vytvorit instanciu len Entity => tato trieda vacsinou sluzi ako spolocny interface pre entity.
 * Entity su priamo zobrazovane v mape pri vykreslovani. Univerzalne id umoznuju lahsiu 
 * pracu pri tvoreni questov ako aj priradovanie efektov pri nacitavani map.
 * Kazda entita musi mat implementovane najdolezitejsie abstraktne metody ako je reagovanie
 * na aktualizovanie entity, aktualizovanie koordinatov, ako sa entita sprava pri posuvanie, atd... (viac pri jednotlivych metodach) 
 * @author Kirrie
 */
public abstract class Entity implements Externalizable {  
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    // Staticke konstantne premenne
    protected static final int STATMULT = 5;
    protected static final int BASESTAT = 5;
    private static final long serialVersionUID = 912804676578087866L;
    private static final Logger LOG = Logger.getLogger(Entity.class.getName());        
    
    /**
     * Indexy pre vytvaranie dat z inventaru.
     */
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
    
    /**
     * Typ entity
     */
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
    // Velkost odsunutia
    protected int knockback;
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
    // Bool ci je entita znicena
    protected boolean destroyed;        
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
    
    // Atributy v hashmapach
    protected HashMap<Stat,Double> doubleStats;
    protected HashMap<Stat,Integer> intStats;
    
    // aktivne effekty na entite. Kluc je kedy sa efekt spusta, obsah su efekty pre kluc.    
    protected HashMap<EffectEvent, ArrayList<Effect>> activeEffects;         
    
    // Atributy zivota
    protected double health;    
    protected double healthRegen;
    protected double maxHealth;    
    //    
    // Agresivita entity - vzdialenost pri ktorej sa entita zacne spravat agresivne
    protected int aggresivity;
    // Grupa entity
    protected int group;    
    // Sanca na prerusenie - aku sancu ma entita ze prerusi cudzi utok
    protected double interruptionChance;
    // Koncetracia - kolko sa odcitava od nepriatelovej interruptionChance
    protected double concentration;
        
    //Atributy utoku, obrany, vzdialenosti utoku a obrany a sily utoku
    protected double attack;
    protected double defenseA;
    protected double defenseP;
    protected double acumDefense;
    protected double acumPower;
    protected double attackPower;
    protected double defensePower;
    protected Double maxPower;
    protected int attackRadius;
    protected int attackRating;
    protected int defenseRating;
    protected double damage;     
    
    // Premenne urcujuce ci sa entita moze hybat a ci nanu mozme utocit
    protected boolean moveable;
    protected boolean attackable;
    
    // Premenna vytvarajuca particle pre nabijanie sily utoku ci obrany
    protected BarParticle poweringBar;   
    
    // Premenna urcujuca typ entity v ramci materialu z coho je entita vytvorena.
    protected int levelType;
    
    // Premenne tykajuce sa casu. 
    /**
     * WayPoint time - cas kedy bola naposledy zmenena sprite
     */
    protected long waypointTime;
    /**
     * Aktualny cas
     */
    protected long currentTime;
    /**
     * Maximalny cas kolko vydrzi jedna sprite.
     */
    protected long maxTime;    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Konstruktor dolezity pri externalizacii.
     */
    public Entity() {
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Abstraktne metody ">
    // Abstraktne metody 
    /**
     * Metoda na aktualizovanie entity. Dediace triedy hu musia implementovat
     * @return True/false ci sa podaril utok
     */
    public abstract boolean update();
    /**
     * Metoda na vyzlecenie predmetu zadany ako parameter <b>e</b>.
     * Dediace triedy hu musia implementovat     
     */
    public abstract void unequip(Item e);  
    /**
     * Metoda na oblecenie predmetu zadany ako parameter <b>e</b>.
     * Dediace triedy hu musia implementovat     
     */
    public abstract void equip(Item e); 
    /**
     * Metoda na na nastavenie nepriechodnej dlazdice zadanej ako parameter <b>tile</b>.
     * Dediace triedy hu musia implementovat     
     */
    public abstract void setImpassableTile(int tile);
    /**
     * Metoda na pouzitie predmetu zadany ako parameter <b>item</b>.
     * Dediace triedy hu musia implementovat     
     */
    public abstract void use(Item item);
    /**
     * Metoda na vyhodenie predmetu zadany ako parameter <b>item</b>.
     * Dediace triedy hu musia implementovat     
     */
    public abstract void drop(Item item);
    /**
     * Metoda ktora porani entitu.
     * Dediace triedy hu musia implementovat     
     */
    public abstract double hit(double damage, rpgcraft.graphics.spriteoperation.Sprite.Type type);
    /**
     * Metoda na aktualizovanie koordinatov entity. Dediace triedy hu musia implementovat
     * @return True/false ci sa podarilo aktualizovanie
     */
    public abstract boolean updateCoordinates();
    /**
     * Metoda ktora posunie entitu zadanu ako parameter <b>e</b>
     * Dediace triedy hu musia implementovat     
     */
    public abstract void pushWith(Entity e); 
    /**
     * Metoda ktora ovplyvnuje entity na poziciach zadane vnutornymi parametrami.
     * Dediace triedy hu musia implementovat   
     * @param x0 Lava spodna x-ova pozicia
     * @param y0 Lava spodna y-ova pozicia
     * @param x1 Prava vrchna x-ova pozicia
     * @param y1 Prava vrchna y-ova pozicia
     * @param modifier Modifikator pre rozne operacie
     * @return Sila nejakej interakcie. Vacsinou vyuzite pri utoku ako pocet odobratych
     * zivotov
     */
    public abstract double interactWithEntities(int x0, int y0, int x1, int y1, double modifier); 
    /**
     * Metoda ktora nasilu odsunie entitu
     * Dediace triedy hu musia implementovat     
     */
    public abstract void knockback(rpgcraft.graphics.spriteoperation.Sprite.Type type);    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Staticke metody ">
    /**
     * Metoda ktora vytvori entitu s danym menom ako parameter <b>name</b>. Entita
     * sa vytvori z resource zadany ako parameter <b>res</b>. Podla typu entity sa zavola 
     * prislusny konstruktor ci metoda na vytvorenie entity. 
     * @param name Meno entity
     * @param map Mapa v ktorej bude entita
     * @param res Resource z ktoreho sa entita vytvara
     * @return Vytvorena entita
     */
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
    
    /**
     * Metoda ktora vrati data z inventara do listu listov podla parametru <b>params</b>.
     * Prechadzame kazdy predmet z inventara a vytvarame data.
     * Na vytvorenie dat pouzivame enumy InventoryIndexes ktore nam urcuju na ktore miesta sa ukladaju
     * data z predmetu. Po vytvoreni pola prechadzanim parametrov pridame do listu record data z predmetu.
     * Nakonci pridame tento list do resultInf a vratime ho ako navratovu hodnotu metody.     
     * @param e Entita z ktorej inventara vytvarame data
     * @param params Parametre ktore urcuju ktore data sa objavia v navratovom liste
     * @return List so datami urcenymi parametrom params.
     */
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
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Inicializacie ">
    /**
     * Metoda ktora inicializuje entitu po vytvoreni konstruktorom. Tiez ako metoda reinitialize je volana
     * z potomkov ako super metoda. V metode sa inicializuju spolocne vlastnosti
     * pre vsetky entity ako je univerzalne id, inicializacia efektov a nacitanie statov/vlastnosti
     * do hashmap doubleStats a intStats.
     */
    public void initialize() {
        this.uniId = MainUtils.objectId++;
        this.activeEffects = new HashMap<>();        
        if (res.getEntityEffects() != null) {            
            initializeEffects();
        }        
        
        if (res.getInventory() != null) {
            for (String s : res.getInventory()) {
                inventory.add(Item.createItem(null, EntityResource.getResource(s)));
            }
        }
        if (res != null) {
            levelType = res.getItemLevelType();
        } else {
            levelType = ItemLevelType.HAND.getValue();
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
    
    /**
     * Metoda ktora reinitializuje entitu pri nacitavani zo savu. Metoda je volana 
     * z potomkov ako super metoda. Najdolezitejsia sucast metody je inicializacia resource
     * a ziskanie efektov do entity.
     */
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
        this.knockback = res.getKnockback();        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery " >
    /**
     * Metoda ktora vracia meno entity
     * @return Text s menom
     */
    public String getName() {
        return name;
    }  
    
    /**
     * Metoda ktora ziska info o entite. 
     * @return Info o entite
     */
    public String getInfo() {
        if (res != null) {
            return res.getInfo();
        } else {
            return null;
        }
    }
    
    /**
     * Vrati ci je entitu mozne despawnut
     * @return True/false ci sa entita da despawnut
     */
    public boolean isDespawnable() {
        if (res != null) {
            return res.isDespawnable();
        }
        return true;
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
        return health <= 0 || destroyed;
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
    public int getLevelType() {
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
    
    /**
     * Metoda ktora vrati hodnotu statu/atributu zadaneho parametrami.
     * @param stat Parameter ktoreho hodnotu hladame
     * @param trim Ci sa hodnota vrati podla metody getStatValue
     * @return Textova hodnota s atributom
     */
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
    
    /**
     * Metoda ktora vrati intovu hodnotu atributu.
     * @param stat Stat/atribut ktory chceme vratit
     * @param ret Defaultna/zakladna hodnota statu
     * @return Intova hodnota statu
     */
    public int getInt(Stat stat, int ret) {
        Integer value = intStats.get(stat);
        if (value == null || value == 0d) {
            return ret;
        }
        return value;
    }
    
    /**
     * Metoda ktora vrati double hodnotu atributu.
     * @param stat Stat/atribut ktory chceme vratit
     * @param ret Defaultna/zakladna hodnota statu
     * @return Double hodnota statu
     */
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
    
    /**
     * Metoda ktora vrati ci entita moze utocit na inu
     * @return True/False ci moze utocit.
     */
    protected boolean canAttack() {                
        return attackable;
    }                    
     
    /**
     * Metoda ktora vrati ci batoh obsahuje predmet.
     * @param item Predmet ktory hladame
     * @return True/false ci sme nasli predmet
     */
    public boolean hasItem(Item item) {
        return inventory.contains(item);
    }
    
    /**
     * Metoda ktora vrati ci batoh obsahuje predmet s urcitym id.
     * @param itemId Id predmetu ktory hladame
     * @return True/false ci sme nasli predmet
     */
    public boolean hasItem(String itemId) {
        for (Item item : inventory) {
            if (item.getId().equals(itemId) == true) {
                return true;    
            }
        }
        return false;
    }
    
    /**
     * Metoda ktora vrati ci batoh obsahuje predmet s urcitym id a menom.
     * @param itemId Id predmetu ktory hladame
     * @param itemName Meno predmetu ktory hladame
     * @return True/false podla toho ci existuje predmet
     */
    public boolean hasItem(String itemId, String itemName) {
        for (Item item : inventory) {
            if (item.getId().equals(itemId) == true && item.getName().equals(itemName)) {
                return true;    
            }
        }
        return false;
    }
    /*    
    public EntityPosition getPosition() {
        if (position == null) {
            position = new EntityPosition(x, y);
        }
        return position;
    }
    */
    // </editor-fold>                  
            
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    
           
    /**
     * Metoda ktora nastavi entite meno
     * @param name Text s menom
     */
    public void setName(String name) {
        this.name = name;       
    }
          
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
     * @see SaveMap
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
    
    /**
     * Metoda ktora vynuluje nahromadene sily (defense aj attack)
     */
    public void clearPowers() {
        acumPower = 0;
        acumDefense = 0;
    }
    
    /**
     * Metoda ktora zvysi nahromadenu utocnu silu o hodnotu rovnu power * modifier + value.     
     * @param power Hodnota zvysovania
     * @param modifier Modifikator zvysovanie
     * @param value Hodnota o ktoru sa nahromadena sila vzdy zvacsi
     */
    public void incAcumPower(double power, double modifier, double value) {
        acumPower += power * modifier + value;
    }
    
    /**
     * Metoda ktora zvysi nahromadenu defenzivnu silu o hodnotu rovnu power * modifier + value.     
     * @param power Hodnota zvysovania
     * @param modifier Modifikator zvysovanie
     * @param value Hodnota o ktoru sa nahromadena defenziva vzdy zvacsi
     */
    public void incAcumDefense(double power, double modifier, double value) {
        acumDefense += power * modifier + value;
    }
    
    /**
     * Metoda ktora zvysi zivot entity o parameter <b>health</b>. Zivot sa zvacsuje
     * aj cez maximalny zivot.
     * @param health Hodnota zvysenia zivota
     */
    public void incHealth(double health) {
        this.health += health;
    }
    
    /**
     * Metoda ktora regeneruje zivot entity o parameter <b>health</b>. Zivot sa zvacsuje
     * iba po maximalny zivot.
     * @param health O kolko sa regeneruje zivot
     */
    public void regenHealth(double health) {
        this.health += health;
        if (this.health > maxHealth) {
            this.health = maxHealth;
        }
    }
    
    /**
     * Metoda ktora regeneruje staminu entity o parameter <b>stamina</b>. Zivot sa zvacsuje
     * iba po maximalnu staminu.
     * @param stamina O kolko sa regeneruje stamina
     */
    public void regenStamina(double stamina) {
        
    }
    
    /**
     * Metoda ktora zvysi staminu entity o parameter <b>stamina</b>. Zivot sa zvacsuje
     * aj cez maximalny stamina.
     * @param stamina Hodnota zvysenia staminy
     */
    public void incStamina(double stamina) {
        
    }
    
    /**
     * Metoda ktora zvysi manu o parameter <b>mana<b>. Zatial nepouzite.
     * @param mana Hodnota zvysenia many
     */
    public void incMana(double mana) {
        
    }
    
    /**
     * Metoda ktora znizi level poziciu entity v mape.
     */
    public void decLevel() {
        this.level--;
    }
    
    /**
     * Metoda ktora zvysi level poziciu entity v mape.
     */
    public void incLevel() {
        this.level++;
    }
    
    /**
     * Metoda ktora vymaze efekty nastavenim efektu na false z predmetu zadaneho parametrom <b>item</b>.
     * Vymazavame iba take efekty ktore zodpovedaju parametru <b>event</b>.
     * @param entity Predmet z ktoreho vyberame efekty a nastavujeme na false
     * @param event EffectEvent podla ktoreho vyberame efekty z predmetu
     */
    public void removeEffects(Entity entity, Effect.EffectEvent event) {
        if (entity.activeEffects == null || entity.activeEffects.isEmpty()) {
            return;
        }
        ArrayList<Effect> effects = entity.activeEffects.get(event);
        if (effects == null || effects.isEmpty()) {
            return;
        }
        
        for (Effect effect : effects) {
            effect.quit();
        } 
    }
    
    /**
     * Metoda ktora prida efekty z predmetu zadaneho parametrom <b>item</b> do entity.
     * Pridavaju sa iba take efekty ktore maju EffectEvent rovny parametru <b>event</b>.
     * Pri najdeni vsetkych zodpovedajucich efektov nastavime entity s ktorymi je efekt spojeny 
     * a taktiez nastavime ze je efekt v onSelfEfektoch metodou setSelf. Efekt pridame
     * do mapy (do onSelfEfektov)
     * @param entity Predmet z ktoreho pridavame efekty do entity
     * @param event EffectEvent ktory musi mat efekt aby sme ho pridali entite
     */
    public void addEffects(Entity entity, Effect.EffectEvent event) {
        if (entity.activeEffects == null || entity.activeEffects.isEmpty()) {
            return;
        }
        ArrayList<Effect> effects = entity.activeEffects.get(event);
        if (effects == null || effects.isEmpty()) {
            return;
        }
        
        for (Effect effect : effects) {
            if (effect.isSelf() && effect.getDestUniId() == uniId && 
                    effect.getOriginUniId() == entity.uniId) {
                effect.reset(); 
                continue;
            }                                         
            effect.setEntities(entity, this);
            effect.setSelf();
            map.addEffect(effect);
        } 
    }
    
    /**
     * Metoda ktora odoberie efekty z aktualnej entity ktore sa nachadzaju v predmete zadanom parametrom <b>item</b>
     * @param entity Predmet z ktoreho ziskavame efekty
     * @param event EffectEvent ktory musi mat efekt aby sme ho odobrali z aktivnych efektov.
     */
    public void removeEffectsFromEntity(Entity entity, Effect.EffectEvent event) {
        if (entity.activeEffects == null || entity.activeEffects.isEmpty()) {
            return;
        }
        ArrayList<Effect> effects = entity.activeEffects.get(event);
        if (effects == null || effects.isEmpty()) {
            return;
        }
        
        ArrayList<Effect> newEffects = activeEffects.get(event);
        if (newEffects == null) {
            return;
        }
        
        for (Effect effect : effects) {
            newEffects.remove(effect);
        }
    }
    
    /**
     * Metoda ktora prida z predmetu zadaneho parametrom <b>item</b> efekty ktore maju 
     * EffectEvent rovny parametru <b>event</b> do aktualnej entity.
     * @param entity Predmet z ktoreho ziskavame efekty
     * @param event EffectEvent ktory musi mat efekt aby sme ho pridali do aktivnych efektov.
     */
    public void addEffectsToEntity(Entity entity, Effect.EffectEvent event) {
        if (entity.activeEffects == null || entity.activeEffects.isEmpty()) {
            return;
        }
        ArrayList<Effect> effects = entity.activeEffects.get(event);
        if (effects == null || effects.isEmpty()) {
            return;
        }
        
        ArrayList<Effect> newEffects = activeEffects.get(event);
        if (newEffects == null) {
            newEffects = new ArrayList<>();
            activeEffects.put(event, newEffects);
        }
        for (Effect effect : effects) {
            if (!newEffects.contains(effect)) {
                newEffects.add(effect);            
            }
        }
    }
    
    /**
     * Metoda ktora nastavi aktivny predmet entity na ten zadany v parametre <b>item</b>.
     * Na spravne nastavenie predmetu musime zvysit staty podla predmetovych statov.
     * @param item Predmet ktory je novym aktivnym efektom. 
     */
    public void setActiveItem(Item item) { 
        if (item == null) {
            if (res != null) {
                levelType = res.getItemLevelType();
            } else {
                levelType = ItemLevelType.HAND.getValue();
            }
        } else {
            levelType = item.getLevelType();
        }
        
        if (this.activeItem != null && this.activeItem != item) {
            removeEffectsFromEntity(activeItem, EffectEvent.ONSTRUCK);
            decStats(this.activeItem);            
            this.activeItem.active = false;
        }
        
        if (item != null) {            
            incStats(item);
            addEffectsToEntity(item, EffectEvent.ONSTRUCK);
            item.active = true;
        }
        this.activeItem = item;        
    }
    
    /**
     * Metoda ktora nastavi priznak saved u entity na ten v parametri <b>saved</b>
     * @param saved 
     */
    public void setSaved(boolean saved) {
        this.saveInProgress = saved;
    }                
    
    /**
     * Metoda ktora nastavi z coho je entita vyrobena (nieco ako minimalna odolnost).
     * @param levelType LevelType pre entitu
     * @see ItemLevelType
     */
    public void setItemLevelType(ItemLevelType levelType) {
        this.levelType = levelType.getValue();
    }
    
    /**
     * Metoda ktora nastavi chunk v ktorom sa entita nachadza
     * @param chunk Chunk v ktorom sa nachadza entita.
     */
    public void setChunk(Chunk chunk) {
        this.actualChunk = chunk;
    } 
    
    /**
     * Metoda ktora nastavi aktualny chunk v ktorej by sa mala entita nachadzat.
     * Force v mene metody znamena ze nasilu menime aktualny chunk pre entitu nacitanim
     * pomocou metody chunkXYExit.
     */
    public void forceChunk() {
        this.actualChunk = map.chunkXYExist(xPix, yPix);
    }
    
    /**
     * Metoda ktora nastavi na akom leveli v mape sa nachadza entita.
     * @param level Pozicie entity v mape
     */
    public void setLevel(int level) {
        this.level = level;
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
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Update ">
    
    // <editor-fold defaultstate="collapsed" desc=" Predmetove funkcie ">
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
                            //System.out.println(toString());
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
    
    /**
     * Metoda ktora znizi atributy/staty tejto entity o atributy/staty v parametri
     * item.
     * @param item Entita podla ktorej znizujeme staty
     */
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
        doubleStats.put(Stat.HEALTHMAX, getDouble(Stat.HEALTHMAX, 0) - getDouble(Stat.HEALTHBONUS, 0));
        doubleStats.put(Stat.STAMINAMAX, getDouble(Stat.STAMINAMAX, 0) - getDouble(Stat.STAMINABONUS, 0));
        doubleStats.put(Stat.HEALTHBONUS, getDouble(Stat.HEALTHBONUS, 0) - item.getDouble(Stat.HEALTHBONUS, 0));
        doubleStats.put(Stat.STAMINABONUS, getDouble(Stat.STAMINABONUS, 0) - item.getDouble(Stat.STAMINABONUS, 0));        
        doubleStats.put(Stat.HEALTHREGEN, getDouble(Stat.HEALTHREGEN, 0) - item.getDouble(Stat.HEALTHREGEN, 0));  
        doubleStats.put(Stat.DBLFACTOR, getDouble(Stat.DBLFACTOR, 0) - item.getDouble(Stat.DBLFACTOR, 0));                
        doubleStats.put(Stat.STAMINAMAX, getDouble(Stat.STAMINAMAX, 0) + doubleStats.get(Stat.STAMINABONUS));
        doubleStats.put(Stat.HEALTHMAX, getDouble(Stat.HEALTHMAX, 0) + doubleStats.get(Stat.HEALTHBONUS));
        
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
    
    /**
     * Metoda ktora zvysi atributy/staty tejto entity o atributy/staty v parametri
     * item.
     * @param item Entita podla ktorej zvysujeme staty
     */
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
        doubleStats.put(Stat.HEALTHBONUS, getDouble(Stat.HEALTHBONUS, 0) + item.getDouble(Stat.HEALTHBONUS, 0));
        doubleStats.put(Stat.STAMINABONUS, getDouble(Stat.STAMINABONUS, 0) + item.getDouble(Stat.STAMINABONUS, 0));        
        doubleStats.put(Stat.HEALTHREGEN, getDouble(Stat.HEALTHREGEN, 0) + item.getDouble(Stat.HEALTHREGEN, 0));  
        doubleStats.put(Stat.DBLFACTOR, getDouble(Stat.DBLFACTOR, 0) + item.getDouble(Stat.DBLFACTOR, 0));                
        doubleStats.put(Stat.HEALTHMAX, getDouble(Stat.HEALTHMAX, 0) + doubleStats.get(Stat.HEALTHBONUS));
        doubleStats.put(Stat.STAMINAMAX, getDouble(Stat.STAMINAMAX, 0) + doubleStats.get(Stat.STAMINABONUS));
        
        
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

    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Interakcie ">
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
     * Metoda ktora aktivuje entitu a vykona prislusne akcie definovane pri entite.
     * Aktivovanie vacsinou vola pri normalnom vstupe iba hrac no nachadza sa v tejto triede keby 
     * nahodou z listeneru zavolame aktivovanie entity nejakou inou entitou odlisnou od hraca.
     * @param e Entita ktoru aktivujeme
     */
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
    
    /**
     * Metoda ktora umiestni dlazdicu zadanu parametrom <b>tile</b> pod seba
     * @param tile 
     */
    public void placeTile(Tile tile, int modX, int modY, int modZ) {        
        int tileX = getTileX(), tileY = getTileY();                
        //System.out.println(actualChunk.getTile(level - 1, tileX, tileY));
        if (!actualChunk.setTile(level+modZ, tileX + modX, tileY + modY, tile.getId(), tile == null ? 0 : tile.getHealth())) {            
            actualChunk.setTile(level, tileX + modX, tileY + modY, tile.getId(), tile == null ? 0 : tile.getHealth());
            this.level++;
            if (autoMove(0)) {
                map.setLevel(level);
            }
        }       
    }
    
    /**
     * Metoda ktora umiestni entitu na zem v mape. Umiestnenie na zem prebieha iba
     * ked je entita typu TileItem.
     * @param e Entita ktoru umiestnujeme na zem.
     */
    public void placeItem(Entity e) {
        if (e instanceof TileItem) {
            TileItem tileItem = (TileItem)e;
            placeTile(tileItem.getTile(), 0, 0, -1);
            return;
        }
    }
    
                    
    /**
     * Metoda ktora prida dodatocne efekty a upravi atributy ako hpower a dpower
     * tejto entity. Teraz su tam natvrdo dane hodnoty (12), ktore budu nahradene
     * predmetovymi atributmi. Pomocou premennej interruptionChance u entit sa rozhodne ci 
     * sa znizenie statov vykona alebo nie.
     * Takisto tato metoda obsluhuje ci sa efekt prida alebo nie.     
     * @param entity Entita z ktorej sa znizuju atributy. V tomto pripade 
     * sa znizuje iba defense a attack
     */
    protected void addAfterEffectsFrom(Entity entity, Entity item) {
        if (random.nextDouble() < entity.interruptionChance - this.concentration) {
            this.acumPower -= this.acumPower < 12 ? this.acumPower : 12;
            this.acumDefense -= this.acumDefense < 12 ? this.acumDefense : 12;
        }
        
        addEffects(entity, EffectEvent.ONSTRUCK);
            
    }
     
    
    // </editor-fold>
    
    /**
     * Metoda sa pokusi o vytvorenie entity v okoli zadanej entity vo vzdialenosti scatter.
     * @param e Entita okolo ktorej vytvarame novu entitu
     * @param scatter Rozptyl entity
     */
    public void trySpawn(Entity e, int scatter) {
        
    }
    
    /**
     * Metoda sa pokusi o vytvorenie entity v okoli zadanych pozicii vo vzdialenosti scatter.
     * @param e Entita okolo ktorej vytvarame novu entitu
     * @param z Poschodie v ktorom vytvarame
     * @param x X-ova pozicia 
     * @param y Y-ova pozicia
     * @param scatter Rozptyl entity 
     */
    public void trySpawn(int z, int x, int y, int scatter) {
        
    }
    
    /**
     * Metoda ktora aktualizuje vysku podla dlazdic ktore su pod entitou. Aktualizovanie 
     * vysky prebieha ked vkrocime do jamy => klesneme o jeden level, no na znizenom leveli
     * moze byt tiez jama => musime klesnut o dalsi level. Metoda vykonava v podstate tuto operaciu.
     */
    public void updateHeight() { 
        if (level <= 0) {
            destroyed = true;
            return;
        }
        Tile tile = Tile.tiles.get(actualChunk.getTile(level - 1, xPix >> 5, yPix >> 5));                        
        tile.moveInto(this);
    }
    
    /**
     * Meteda ktora vrati double hodnotu nahodne vygenerovanu z intervalu <min,max>
     * @param min Minimalna hodnota odkial generujeme
     * @param max Maximalna hodnota odkial generujeme
     * @return Vygenerovane cislo z intervalu
     */
    protected int rollRandom(int min, int max) {
        int num = max - min;
        if (num <= 0) {
            return min;
        }
        return random.nextInt(num) + min; 
    }
    
    /**
     * Meteda ktora vrati int hodnotu nahodne vygenerovanu z intervalu <min,max>
     * @param min Minimalna hodnota odkial generujeme
     * @param max Maximalna hodnota odkial generujeme
     * @return Vygenerovane cislo z intervalu
     */
    protected double rollRandom(double min, double max) {
        double num = max - min;
        if (num <= 0) {
            return min;
        }
        return (random.nextDouble() * num) + num;
    }            
         
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Pohybove metody ">
    
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
        if (actualChunk == null) {
            actualChunk = map.chunkXYExist(regX, regY);
        }                                       
        
        
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
    
    /**
     * Metoda ktora sa pokusa o pohyb do prislusnych stran dane parametrami <b>x0,y0,z0</b>.
     * Aby bol pohyb uspesny kontrolujeme vrchnu a spodnu dlazdicu. Ked dlazdica patri
     * medzi nepriestupne ci prazdne tak nehybeme entitou. Inak volame metodu setXYPix ktora
     * pohne entitu do strany a metodu moveInto z dlazdice ktora vykona dodatocne akcie
     * na entite pri pohybe po dlazdici. 
     * @param x0 X-ova suradnica kam sa hybeme
     * @param y0 Y-ova suradnica kam sa hybeme
     * @param z0 Level-suradnica kam sa hybeme
     * @param backChunk Chunk ktory nastavime ked sa nam nepodari pohyb od strany.
     * @return True/false ci sa podaril pohyb.
     */
    public boolean tryMove(int x0, int y0, int z0, Chunk backChunk) {
        if (z0 <= 0) {            
            return false;                        
        }        
        int lTile = actualChunk.getTile(z0 - 1, x0 >> 5, y0 >> 5);
        int uTile = actualChunk.getTile(z0, x0 >> 5, y0 >> 5);
        //debugCoordinateswithTiles(x0, y0, tile);                

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
     * Metoda ktora vykona pohyb odsotenia podla premennej xKnock a yKnock.
     * Tato moznost je tu pre skutocnejsiu atmosferu boju.
     */
    public void knockMove() {
        if (xKnock < 0) {
            canMove(-2, 0, 0);
            xKnock ++;
        }
        if (xKnock > 0) {
            canMove(2, 0, 0); 
            xKnock --;
        }
        if (yKnock < 0) {
            canMove(0, -2, 0);
            yKnock ++;
        }
        if (yKnock > 0) {
            canMove(0, 2, 0); 
            yKnock --;
        }
    }
      
    // </editor-fold>
                                 
    // <editor-fold defaultstate="collapsed" desc=" Externalizacne metody ">
    /**
     * Metoda ktora zapise do vystupneho suboru zadaneho parametrom <b>out</b> udaje z entity.
     * Kazda dediaca trieda vacsinou pretazuje tuto metodu a vola hu z nej.
     * Do suboru zapisujeme udaje ktore su spolocne pre podedene entity ako je univerzalne id,
     * meno, aktivovanost predmetu, pozicie,... 
     * @param out Vystupny stream/subor kam vypisujeme data
     * @throws IOException Chybajuci subor pri neexistencii vystupu
     */
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

    /**
     * Metoda ktora nacita z vstupneho suboru zadaneho parametrom <b>in</b> udaje do entity.
     * Kazda dediaca trieda vacsinou pretazuje tuto metodu a vola hu z nej.
     * Zo suboru nacitavame udaje ktore su spolocne pre podedene entity ako je univerzalne id,
     * meno, aktivovanost predmetu, pozicie,... 
     * @param in Vstupny stream/subor odkial nacitavame data
     * @throws IOException Chybajuci subor pri neexistencii vstupu
     */
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
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Debug ">
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
    
    // </editor-fold>
    
}
