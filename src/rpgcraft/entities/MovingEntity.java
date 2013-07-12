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
import java.util.HashMap;
import rpgcraft.effects.Effect.EffectEvent;
import rpgcraft.plugins.Ai;
import rpgcraft.entities.ai.DefaultAi;
import rpgcraft.entities.ai.LuaAi;
import rpgcraft.entities.types.ItemLevelType;
import rpgcraft.graphics.ui.particles.TextParticle;
import rpgcraft.graphics.spriteoperation.Sprite;
import rpgcraft.graphics.spriteoperation.Sprite.Type;
import rpgcraft.handlers.InputHandle;
import rpgcraft.map.SaveMap;
import rpgcraft.map.chunks.Chunk;
import rpgcraft.map.tiles.AttackedTile;
import rpgcraft.map.tiles.Tile;
import rpgcraft.resource.EntityResource;
import rpgcraft.resource.StatResource.Stat;
import rpgcraft.resource.TileResource;

/**
 * Trieda MovingEntity ktora dedi od Entity. Zdruzuje vytvorenie pohyblivej entity a metody
 * zabezpecujuce pohyb a interakciu snimi. Pohybliva entita dedi od Entity vsetky premenne
 * no navyse su vnej doplnene dalsie premenne na pohyb a zlepsene metody umoznujuce pohyb.
 * @see Entity
 * 
 */
public class MovingEntity extends Entity {    
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    // Konstantna dlzka segmentu kam utocime
    protected static final int SEGMENTLENGTH = 32;   
    // Konstanta urcujuca maximalny pocet pokusov na vytvorenie entity
    protected static final int MAXSPAWN = 10;   
        
    // Premenne tykajuce sa dlzky kam dociahne svetlo.
    protected int lightRadius;    

    // Premenne tykajuce sa pozicii
    protected int originX, originY;
    protected int xGo, yGo;        
    
    // Premenne tykajuce sa ovladania
    protected InputHandle input;    
    
    // Premenne tykajuce sa priznakov utoku
    public boolean disabled = false;
    public boolean damageDisabled = false;
    private boolean attackStarted = false;    
    
    // Premenne tykajuce sa co entita robi
    protected boolean swimable;
    protected boolean swimming;
    protected int walking;        
    
    // Premenne tykajuce sa staminy a doplnenia staminy
    protected double staminaRegen;
    protected double staminaDischarger;
    protected double stamina;
    protected double maxStamina;                
       
    // Premenne tykajuce sa pohybu
    protected double fullSpeed;
    protected double xDelay, yDelay;
    protected Double doublexGo = 0d;
    protected Double doubleyGo = 0d;

    // Premenne tykajuce sa utoku
    protected AttackedTile targetedTile;
    
    // Premenne tykajuce sa statov.
    protected int skillPoints, statPoints;    
    
    protected HashMap<Sprite.Type, ArrayList<Sprite>> movingEntitySprites;       
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Prazdny konstruktor pre vytvorenie instancie Externalizaciou.
     */
    public MovingEntity() {}        
    
    /**
     * Konstruktor s parametrom predmetu. Vytvori novu instanciu MovingEntity kde si za zaklad
     * zoberie predmet.
     * @param item Predmet ako zaklad pre entitu.
     */
    public MovingEntity(Item item) {
        this.name = item.name;
        this.id = item.id;       
        this.spriteType = Sprite.Type.ITEM; 
        this.res = EntityResource.getResource(id);
        if (res == null) {
            this.movingEntitySprites = new HashMap<>();
            ArrayList<Sprite> sprites = new ArrayList<>();
            sprites.add(TileResource.getResource(id).getTileSprites().get(spriteType.getValue()));
            movingEntitySprites.put(spriteType, sprites);
        } else {
            this.movingEntitySprites = res.getEntitySprites();        
        }        
    }
    
    /**
     * Konstruktor ktory vytvori pohyblivu entitu z hraca. Vhodne pri buducej implementacii kuziel
     * ako ovladanie mysli, ovladanie hraca.
     * @param player Hrac z ktoreho vytvarame entitu.
     */
    public MovingEntity(Player player) {
        this.name = player.name;
        this.map = player.map;
        this.res = player.res;
                
        this.id = res == null ? "" : res.getId();   
        this.ai = res.getAi() == null ? DefaultAi.getInstance() : Ai.getAi(res.getAi());
        this.movingEntitySprites = player.movingEntitySprites;        
        this.doubleStats = player.doubleStats;
        this.intStats = player.intStats;
        this.activeItem = player.activeItem;
        this.activeEffects = player.activeEffects;
        this.group = player.group;
        this.concentration = player.concentration;
        this.attackable = player.attackable;
        this.pushable = player.pushable; 
        this.fullSpeed = doubleStats.get(Stat.DBLFACTOR) + (new Double(intStats.get(Stat.SPEED))/(new Double(intStats.get(Stat.SPDRATER))));
        this.spriteType = Sprite.Type.DOWN;
        this.maxStamina = doubleStats.get(Stat.STAMINAMAX);
        this.stamina = maxStamina;
        this.maxHealth = doubleStats.get(Stat.HEALTHMAX);
        this.staminaRegen = doubleStats.get(Stat.STAMINAREGEN);
        this.staminaDischarger = maxStamina;
        this.attackRadius = intStats.get(Stat.ATKRADIUS);        
        this.health = maxHealth;          
        
        this.xPix = player.xPix;
        this.yPix = player.yPix;
        this.actualChunk = player.actualChunk;
    }
    
    /**
     * Konstruktor ktory vytvori pohyblivu entitu podla parametrov. 
     * @param name Nove meno entity
     * @param map Mapa kde je entita.
     * @param res Resource ako zaklad pre entitu
     */
    public MovingEntity(String name, SaveMap map, EntityResource res) {
        this.map = map;        
        this.res = res;        
        this.name = name == null ? res.getId() : name;        
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Initializing ">
    /**
     * Metoda ktora inicializuje pohyblivu entitu podla ziskanych udajov z konstruktora.
     * Nastavi sa id, sprity/obrazky pre entitu, inteligencia, atd... <br>
     * Dolezite je volanie super metody initialize ktora prida do hashmap doubleStats a intStats vlastnosti
     * o entite.
     */
    @Override
    public void initialize() {        
        this.movingEntitySprites = res.getEntitySprites();
        this.id = res == null ? "" : res.getId();
        if (res.isLuaAi()) {
            this.ai = res.getAi() == null ? DefaultAi.getInstance() : LuaAi.getLuaAi(res.getAi());
        } else {
            this.ai = res.getAi() == null ? DefaultAi.getInstance() : Ai.getAi(res.getAi());
        }
        this.maxPower = new Double(24);
        this.attackable = true;
        this.pushable = true;   
        this.moveable = res.isMoveable();
        this.activeItem = res.getActiveItem() != null ? res.getActiveItem() : null;            
        this.group = res.getGroup();
        this.concentration = res.getConcentration();
        
        super.initialize();        
        
        /*
         * Najcastejsie pouzivane staty, ktore bude mat kazda entita. Pri zmene 
         * statov ulozenych v Hashmapach sa prepocitaju.
         */
        this.spriteType = Type.DOWN;
        this.fullSpeed = doubleStats.get(Stat.DBLFACTOR) + (new Double(intStats.get(Stat.SPEED))/(new Double(intStats.get(Stat.SPDRATER))));        
        this.maxStamina = doubleStats.get(Stat.STAMINAMAX);        
        this.maxHealth = doubleStats.get(Stat.HEALTHMAX);
        this.healthRegen = doubleStats.get(Stat.HEALTHREGEN);
        this.staminaRegen = doubleStats.get(Stat.STAMINAREGEN);
        this.attackRadius = (int)(intStats.get(Stat.ATKRADIUS) * (1 + doubleStats.get(Stat.ATKRADIUSPER)));            
        this.damage = doubleStats.get(Stat.DAMAGE);                      
        this.attackPower = doubleStats.get(Stat.ATKPOWER);       
        this.defensePower = doubleStats.get(Stat.DEFPOWER);
        this.stamina = maxStamina;
        this.staminaDischarger = maxStamina;
        this.health = maxHealth;                         
    }        
    
    /**
     * Metoda ktora je podobna metode initialize. Odlisnost je v tom ze tato metoda iba dokonci/preinitializuje
     * niekolko vlastnosti. Vacsinou je volana pri nacitavani entit zo save filov.
     */
    @Override
    protected void reinitialize() {
        super.reinitialize();        
        if (res.isLuaAi()) {
            this.ai = res.getAi() == null ? DefaultAi.getInstance() : LuaAi.getLuaAi(res.getAi());
        } else {
            this.ai = res.getAi() == null ? DefaultAi.getInstance() : Ai.getAi(res.getAi());
        }
        this.movingEntitySprites = res.getEntitySprites();        
        this.maxPower = new Double(24);
        this.spriteType = Type.DOWN;
        this.fullSpeed = getDouble(Stat.DBLFACTOR, 0) + ((double)getInt(Stat.SPEED,0))/(getInt(Stat.SPDRATER, 1));        
        this.maxStamina = getDouble(Stat.STAMINAMAX, 0);        
        this.maxHealth = getDouble(Stat.HEALTHMAX, 0);
        this.healthRegen = getDouble(Stat.HEALTHREGEN, 0);
        this.staminaRegen = getDouble(Stat.STAMINAREGEN, 0);
        this.attackRadius = (int)(getInt(Stat.ATKRADIUS, 0) * (1 + doubleStats.get(Stat.ATKRADIUSPER)));            
        this.damage = getDouble(Stat.DAMAGE, 0);                      
        this.attackPower = getDouble(Stat.ATKPOWER, 0);       
        this.defensePower = getDouble(Stat.DEFPOWER, 0);        
        this.staminaDischarger = maxStamina;                        
    }
    
    /**
     * Metoda ktora prepocita najcastejsie pouzivane staty podla tych ulozenych
     * v hashmapach.
     */
    public void recount() {
        this.attackRadius = intStats.get(Stat.ATKRADIUS);
    }
      
    // </editor-fold>
              
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    
    /**
     * Metoda ktora nastavi do nepriechodnych dlazdic novu dlazdicu s id zadany parametrom <b>id</b>.
     * @param id Id dlazdice ktora sa prida do nepriechodnych dlazdic.
     */
    @Override
    public void setImpassableTile(int id)  {
        if (Tile.tiles.containsKey(id)) {
            impassableTiles.add(id);
        }
    }
    
    /**
     * Metoda ktora nastavi ovladanie pre Entitu. Moznost pouzitia na ovladanie aj inych entit
     * nez len jedneho playera (logicke pohybovanie s nepriatelom aby spustil nejaku udalost)
     * @param input Ovladanie pre entitu
     * @see InputHandle
     */
    public void setHandling(InputHandle input) {
        this.input = input;
    }
    
    /**
     * Metoda ktora nastavuje meno entity na parameter <b>name</b>
     * @param name Nove meno entity.
     */
    @Override
    public void setName(String name) {
        super.setName(name);
    }
    
    /**
     * Metoda ktora nastavuje typ (spriteType) pre entitu na parameter <b>type</b>
     * @param type SpriteType entity. 
     */
    public void setType(Type type) {
        this.spriteType = type;
    }

    /**
     * Metoda ktora nastavuje vzdialenost pokial vidime na parameter <b>lightRadius</b>
     * @param lightRadius Vzdialenost svetla kam vidime.
     */
    public void setLightRadius(int lightRadius) {
        this.lightRadius = lightRadius;
    }
    
    /**
     * Metoda ktora nastavi entite tercovu entitu.
     * @param e Terc pre entitu.
     */
    public void setTarget(Entity e) {
        this.targetEntity = e;
    }
    
    /**
     * Metoda ktora nastavi modifikator zdrzania pri pohybu
     * @param value Hodnota zdrzania po x-ovej dlzke
     */
    public void setXDelay(double value) {
        this.xDelay = value;
    }        
    
    /**
     * Metoda ktora nastavi modofikator zdrzania pri pohybu
     * @param value Hodnota zdrzania po y-ovej dlzke.
     */
    public void setYDelay(double value) {
        this.yDelay = value;
    }        
    
    /**
     * Metoda ktora nastavi pohyb po x-ovej suradnici v double dlzke.
     * Pri dlzke dlhsej nez jednotka sa posuvame o pixel.
     * @param value Double hodnota pohybu po x-ovej suradnici.
     */
    public void setXGo(double value) {
        this.doublexGo = value;
    }
    
    /**
     * Metoda ktora nastavi pohyb po y-ovej suradnici v double dlzke.
     * Pri dlzke dlhsej nez jednotka sa posuvame o pixel.
     * @param value Double hodnota pohybu po y-ovej suradnici.
     */
    public void setYGo(double value) {
        this.doubleyGo = value;
    }
    
    /**
     * Metoda ktora zvysi pohyb po x-ovej suradnici o dlzku rovnu fullSpeed * modifikator + dlzka.
     * Neberieme do uvahu xDelay.
     * @param modifier Modifikator ktorym prenasobujeme celkovu rychlost
     * @param value Dlzka o ktoru sa urcite pohneme.
     */
    public void incxGo(double modifier, double value) {
        this.doublexGo += fullSpeed * modifier + value;
    }
    
    /**
     * Metoda ktora zvysi pohyb po y-ovej suradnici o dlzku rovnu fullSpeed * modifikator + dlzka.
     * Neberieme do uvahu yDelay.
     * @param modifier Modifikator ktorym prenasobujeme celkovu rychlost
     * @param value Dlzka o ktoru sa urcite pohneme.
     */
    public void incyGo(double modifier, double value) {
        this.doubleyGo += fullSpeed * modifier + value;
    }
    
    /**
     * Metoda ktora zvysi pohyb po x-ovej suradnici o dlzku rovnu fullSpeed * xDelay * modifikator + dlzka.
     * V tejto verzii pohybu berieme do uvahy aj xDelay co je percento pre zdrzanie entity.
     * @param modifier Modifikator ktorym prenasobujeme celkovu rychlost
     * @param value Dlzka o ktoru sa urcite pohneme.
     */
    public void incDoublexGo(double modifier, double value) {
        doublexGo += fullSpeed * xDelay * modifier + value;
    }
    
    /**
     * Metoda ktora zvysi pohyb po y-ovej suradnici o dlzku rovnu fullSpeed * yDelay * modifikator + dlzka.
     * V tejto verzii pohybu berieme do uvahy aj yDelay co je percento pre zdrzanie entity.
     * @param modifier Modifikator ktorym prenasobujeme celkovu rychlost
     * @param value Dlzka o ktoru sa urcite pohneme.
     */
    public void incDoubleyGo(double modifier, double value) {
        doubleyGo += fullSpeed * yDelay * modifier + value;
    }        
        
    /**
     * Metoda ktora nastavi staminu na parameter <b>stamina</b>
     * @param stamina Nova stamina pre entitu
     */
    public void setStamina(int stamina) {
        this.stamina = stamina;
    }        
       
    /**
     * Metoda ktora zvysi staminu o kombinaciu parametrov power * modifier + value.
     * @param power Sila doplnenia staminy
     * @param modifier Modifikator doplnenia
     * @param value Hodnota o ktoru sa urcite doplni
     */
    public void incStamina(double power, double modifier, double value) {
        this.stamina += power * modifier + value;
    }
    
    /**
     * Metoda ktora regeneruje staminu o parameter <b>stamina</b>
     * Stamina nemoze presiahnut maximalnu staminu.
     * @param stamina Stamina ktoru nastavujeme entite.
     */
    @Override
    public void regenStamina(double stamina) {
        this.stamina += stamina;
        if (this.stamina > maxStamina) {
            this.stamina = maxStamina;
        }
    }
    
    /**
     * Metoda ktora nastavuje maximalnu hodnotu staminy pre entitu.
     * @param maxStamina Maximalna stamina pre entitu.
     */
    public void setMaxStamina(int maxStamina) {
        this.maxStamina = maxStamina;
    }
            
    /**
     * Metoda ktora nastavi maximalnu silu ktoru mozme dosiahnut pri kumulovani sily.
     * @param maxPower Maximalna sila pre entitu.
     */
    public void setMaxPower(double maxPower) {
        this.maxPower = maxPower;
    }
     
    /**
     * Metoda ktora nastavi priznak attackStarted na parameter <b>state</b>
     * @param state True/false ci zacal utok.
     */
    public void setAttackStarted(boolean state) {
        this.attackStarted = state;
    }        
    
    /**
     * Metoda ktora nastavi grupu entity na tu zadanu parametrom <b>group</b>
     * @param group Grupa entity
     */
    public void setGroup(int group) {
        this.group = group;
    }
    
    /**
     * Metoda ktora nastavi chunk pre entitu na ten zadany parametrom <b>chunk</b>
     * @param chunk 
     */
    @Override
    public void setChunk(Chunk chunk) {
        super.setChunk(chunk);
    }  
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora vrati true/false ci entita moze plavat.
     * @return True/false ci mozeme plavat
     */
    public boolean canSwim() {
        return swimable;
    }
    
    /**
     * Metoda ktora vrati true/false ci entita plava.
     * @return True/false ci plavame.
     */
    public boolean isSwimming() {
        return swimming;
    }
    
    /**
     * Metoda ktora vrati true/false ci zacal utok.
     * @return True/false ci sme v utoku.
     */
    public boolean hasAttacked() {
        return attackStarted;
    }
           
    /**
     * Metoda ktora vrati aktualnu staminu entity.
     * @return Aktualna stamina entity.
     */
    @Override
    public double getStamina() {
        return stamina;
    }
    
    /**
     * Metoda ktora vrati maximalnu staminu entity.
     * @return Maximalna stamina entity.
     */
    public double getMaxStamina() {
        return maxStamina;
    }
    
    /**
     * Metoda ktora vrati aktualny obrazok entity na zobrazenie. Metoda testuje
     * ci prebehlo urcity pocet milisekund na prehodenie entity. Ked ano tak ziskame 
     * z movingEntitySprites novy sprite a nastavime waypointTime (cas ziskania sprite) 
     * currentTime (kolko je sprite v obehu) a maxTime (kolko moze byt sprite v obehu getDuration()).
     * @return Aktualny obrazok pre sprite.
     */
    @Override
    public Image getTypeImage() {
        if (currentTime >= maxTime) {
            sprNum++;
            if (movingEntitySprites.get(spriteType).size() <= sprNum) {
                sprNum = 0;
            } 
            currentSprite = movingEntitySprites.get(spriteType).get(sprNum);
            waypointTime = System.currentTimeMillis();
            currentTime = 0L;
            maxTime = currentSprite.getDuration();         
        }
        return currentSprite.getSprite();
    }
    
    /**
     * Metoda ktora vrati vzdialenost svetelnosti pre entitu. Dolezite hlavne pre playera.
     * V buducnosti mozne prerobit renderovanie svetla aby zobrazoval viacero svetiel.
     * @return Vzdialenost pokial siaha svetlo (v dlazdiciach)
     */
    public int getLightRadius() {
        return lightRadius;
    }        
    
    /**
     * Metoda ktora vrati stringovy retazec s info o entite.
     * @return Info o entite.
     */
    @Override
    public String toString() {
        String s = this.id + ":"+ this.name + " loaded with their characteristics \n" +
                intStats.get(Stat.STRENGTH) + " strength \n" +
                intStats.get(Stat.AGILITY) + " agility \n" +
                intStats.get(Stat.ENDURANCE) + " endurance \n" +
                intStats.get(Stat.SPEED) + " speed \n" +                
                this.damage + " damage \n" +
                this.maxHealth + " maxHealth \n" +
                this.maxStamina + " maxStamina \n" +
                intStats.get(Stat.ATKRATING) + " atkRating \n" +
                intStats.get(Stat.DEFRATING) + " defRating \n" +
                this.attackPower + " atkPower \n" +
                this.defensePower + " defPower \n" +
                this.maxPower + " maxPower \n" +
                "------------------------------------------------------------------------------ \n";
        return s;
        
    }           
   
    
    // </editor-fold>        
    
    // <editor-fold defaultstate="collapsed" desc=" Update ">
    
    /**
     * Metoda ktora sa pokusi o usadenie entity na mape okolo inej entity. 
     * Entita okolo ktorej vytvarame je zadana parametrom <b>entity</b>.
     * Parameter scatter urcuje ako daleko budu entity vytvorene. Metoda je dovtedy volana
     * dokym sa nepodari najst spravne miesto. Nakonci  hu pridame do mapy.
     * @param entity Entitu ktoru pridavame na mapu
     * @param scatter Rozptyl entity od zadanej entity
     */
    @Override
    public void trySpawn(Entity entity, int scatter) {
        this.xPix = entity.xPix;
        this.yPix = entity.yPix;
        this.level = entity.level < 0 ? 0 : (entity.level > 127 ? 127 : entity.level);
        Chunk chunk = map.chunkXYExist(xPix >> 9, yPix >> 9);
        int x0, y0, z0;
        
        int sgn = 1;
        if (random.nextInt(2) - 1 < 0) {
            sgn = -1;
        }
        int iTry = 0;
        do {
            x0 = xPix + sgn * (scatter + random.nextInt(scatter));
            y0 = yPix + sgn * (scatter + random.nextInt(scatter));
            z0 = level;
            this.actualChunk = map.chunkXYExist(x0 >> 9, y0 >> 9);
        } while (++iTry < MAXSPAWN && !tryMove(x0, y0, z0, chunk));
        
        if (iTry == MAXSPAWN) {
            return;
        }
        /*
        this.x = xPix << 5;
        this.y = yPix << 5;
        this.position = new EntityPosition(x, y);
        */        
        map.addEntity(this);
    }
    
    /**
     * Metoda ktora sa pokusi o usadenie entity na mape okolo inej pozicii zadane 
     * parametrmi <b>x,y,z</b>.      
     * Parameter scatter urcuje ako daleko budu entity vytvorene. Metoda je dovtedy volana
     * dokym sa nepodari najst spravne miesto. Nakonci  hu pridame do mapy.
     * @param x X-ova pozicia okolo ktorej vytvarame entitu
     * @param y Y-ova pozicia okolo ktorej vytvarame entitu
     * @param z Level mapy okolo ktorej vytvarame entitu
     * @param scatter Rozptyl entity od pozicii
     */
    @Override
    public void trySpawn(int x, int y, int z, int scatter) {
        this.xPix = x;
        this.yPix = y;
        this.level = z < 0 ? 0 : (z > 127 ? 127 : z);
        Chunk chunk = map.chunkXYExist(xPix >> 9, yPix >> 9);
        int x0, y0, z0;
        //System.out.println(xPix + " " + yPix);
        int sgn = 1;
        if (random.nextInt(2) - 1 < 0) {
            sgn = -1;
        }
        int iTry = 0;
        do {
            x0 = xPix + sgn * (scatter + random.nextInt(scatter));
            y0 = yPix + sgn * (scatter + random.nextInt(scatter));
            z0 = level;
            this.actualChunk = map.chunkXYExist(x0 >> 9, y0 >> 9);
        } while (++iTry < MAXSPAWN && !tryMove(x0, y0, z0, chunk));        
        if (iTry == MAXSPAWN) {
            return;
        }
        
        //System.out.println(xPix + " " + yPix);
        /*
        this.x = xPix << 5;
        this.y = yPix << 5;
        this.position = new EntityPosition(x, y);
        */
        map.addEntity(this);        
    }    
    
    /**
     * Metoda ktora vykona update/aktualizaciu entity. To znamena ze doplname staminu
     * a health kazdou aktualizaciou + volame inteligenciu entity aby vykonala pohyb.
     * Pri nepodareni aktualizacie vratim false => entitu vymazavame z mapy.
     * @return True/false ci sa podarilo update
     */
    @Override
    public boolean update() {
            if (isDestroyed()) {
                for (Item item : inventory) {
                    map.addEntity(item);
                }
                return false;
            }                                                   
            
            // Kazdym prechodom tejto metody doplni staminu, prerobit na time dependent            
            if (stamina<maxStamina) {
                stamina += staminaRegen;                
            }      
            if (health<maxHealth) {
                health += healthRegen;
            }
            
            // Pohyb podla inteligencie. 
            if (moveable) {
                ai.aiMove(this);
            }
                
            return true;        
    }
    
    /**
     * Metoda ktora zmeni sprite pre entitu na typ zadany parametrom <b>type</b>.
     * Sprite vybera z premennej movingEntitySprites. Pri kazdej zmene nastavujeme maximalny cas na 0
     * @param type Typ sprite na ktory nastavujeme entitu.
     */
    protected void changeSprite(Type type) {
        if (spriteType != type) {
            if (movingEntitySprites.containsKey(type)) {
                this.spriteType = type;  
            }
            maxTime = 0;
        }
    }
    
    /**
     * Metoda ktora ma docinenie so zmenou x-ovych a y-ovych suradnic podla parametrov
     * xGo a yGo, ktore su modifikatormi do ktorej strany sa entita hybe. 
     * Na zistenie ci sa da pohnut do zadanych smerov sa zavola funkcia canMove 
     * ktora po vykonani podla vysledku rozhodne ci sa podaril prechod alebo nie.
     * Vysledky o zdaru sa ulozi do premennej moveable.
     * @return True/False ci sa podarilo aktualizovanie suradnic entity.
     */
    @Override
    public boolean updateCoordinates() {                
        
        knockMove();
                        
        if ((doublexGo != 0d)||(doubleyGo != 0d)) {
            
            
            int ixGo = 0;
            int iyGo = 0;                        
            
            if ((doublexGo >= 1d)||((doublexGo <= -1d))) {
                doubleyGo = 0d;
                ixGo = doublexGo.intValue();
                doublexGo -= ixGo;                
            }
            if ((doubleyGo >= 1d)||(doubleyGo <= -1d)) {
                doublexGo = 0d;                
                iyGo = doubleyGo.intValue();
                doubleyGo -= iyGo;                
            }
                                                            
            
            if ((ixGo > 0)) {
                changeSprite(Sprite.Type.RIGHT);
            }
            if ((ixGo < 0)) {
                changeSprite(Sprite.Type.LEFT);
            }
            if ((iyGo > 0)) {
                changeSprite(Sprite.Type.DOWN);
            }
            if ((iyGo < 0)) {
                changeSprite(Sprite.Type.UP);
            }
                        
            
            if (canMove(ixGo, iyGo, 0)) {
                moveable = true;                
            } else {
                return false;
            }
            
            // Boolean moveable pouzity aj pri zmenach dlazdice po prejdeni.
            // Bude pouzite pre ucely pestovania obilia ako je tomu v originalnom minecraft alebo 
            // pre vizualne ucely, atd...
            
            return moveable;
        }
        return false;
        
    }
      
    /**
     * Metoda ktora znizi vsetky staty v entite podla predmetu <b>item</b>.
     * Funkcne iba prebiehame hashmapy doubleStats a intStats a menime v nich udaje.
     * @param item Predmet podla ktoreho znizujeme vlastnosti entity.
     */
    @Override
    public void decStats(Entity item) {
        super.decStats(item);
        this.maxStamina = doubleStats.get(Stat.STAMINAMAX) * (1 + doubleStats.get(Stat.STAMINAMAXPER)); 
        this.staminaRegen = doubleStats.get(Stat.STAMINAREGEN) * (1 + doubleStats.get(Stat.STAMINAREGENPER));
        this.fullSpeed = doubleStats.get(Stat.DBLFACTOR) + ((double)intStats.get(Stat.SPEED))/(getInt(Stat.SPDRATER, 1));
    }
    
    /**
     * Metoda ktora zvysi vsetky staty v entite podla predmetu <b>item</b>.
     * Funkcne iba prebiehame hashmapy doubleStats a intStats a menime v nich udaje.
     * @param item Predmet podla ktoreho zvysujeme vlastnosti entity.
     */
    @Override
    public void incStats(Entity item) {
        super.incStats(item); //To change body of generated methods, choose Tools | Templates.          
        this.maxStamina = doubleStats.get(Stat.STAMINAMAX) * (1 + doubleStats.get(Stat.STAMINAMAXPER));  
        this.staminaRegen = doubleStats.get(Stat.STAMINAREGEN) * (1 + doubleStats.get(Stat.STAMINAREGENPER));
        this.fullSpeed = doubleStats.get(Stat.DBLFACTOR) + ((double)intStats.get(Stat.SPEED))/(getInt(Stat.SPDRATER, 1));        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="Collapsed" desc=" Interakcie ">
    
    /**
     * Metoda ktora entite "ublizi". Velkost poskodenia je dana parametrom <b>damage</b>.
     * Ked sa da entita poskodit tak jej uberieme zivot a posunieme do prislusnej strany
     * ktora je zadana ako parameter <b>type</b>
     * Pri poskodeni ukazeme na mape textovu casticu s tymto poskodenim (vizualne zobrazenie 
     * poskodenia)
     * @param damage Velkost poskodenia
     * @param type Typ odkial utocime
     * @return Poskodenie entity (double)
     */
    @Override
    public double hit(double damage, Type type) {
        if (damageDisabled) {
            return 0;
        }
        
        health -= damage;
        
        TextParticle particle = null;
        
        knockback(type);
        
        if (damage > 1) {
            particle = new TextParticle(String.valueOf((int)damage), 24, -24);
        } else {
            particle = new TextParticle("1", 24, -24);
        }
        map.addParticle(particle);
        return damage;
    }    
    
    /**
     * Metoda ktora posunie entitu do nejakej strany ktora je urcena z parametru <b>type</b>.
     * Posunutie je urcene premennou knockback co v konecnom dosledku znamena posunutie
     * o 2 * knockback pixelov do prislusnej strany.
     * @param type Strana podla ktorej urcujeme posunutie.
     */
    @Override
    public void knockback(Type type) {
        if (type == Type.RIGHT) {
            xKnock = knockback;
        }
        if (type == Type.LEFT) {
            xKnock = -knockback;
        }
        if (type == Type.DOWN) {
            yKnock = knockback;
        }
        if (type == Type.UP) {
            yKnock = -knockback;
        }
    }
    
    /**
     * Metoda ktora zautoci na dlazdicu. Poskodenie dlazdice je urcene z parametrov.
     * Na zaciatku testujeme ci sme presiahli minimalnu hodnotu modifikatoru na zautocenie.
     * Nasledne volame podla SpriteType z ktorych pozicii vyberame dlazdicu. Vyber dlazdice je ovplyvneny
     * parametrami <b>radius, levelMod</b>. Potom volame interactWithTiles ktora blizsie vykona 
     * operaciu s dlazdicou. Nakonci testujeme ci je aktivny item stale schopny utoku.
     * Ked nie tak ho vymazeme.
     * @param modifier Modifikator utoku a poskodenia
     * @param radius Polomer utocenia (kam utocime)
     * @param levelMod Modifikator levelu kam utocime
     */
    public void attackTile(double modifier, int radius, int levelMod) {        
        if (modifier > 0.25) {

            // double staminaPen = modifier * staminaDischarger;                            

            if ((activeItem == null)||(activeItem.canAttack())) {
                if (spriteType == Type.UP) {
                    interactWithTiles(xPix , yPix, xPix, yPix-radius, levelMod, modifier);                
                    return;                    
                }
                if (spriteType == Type.DOWN) {
                    interactWithTiles(xPix, yPix + radius, xPix, yPix, levelMod, modifier);                
                    return;
                }
                if (spriteType == Type.RIGHT) {
                    interactWithTiles(xPix, yPix, xPix + radius, yPix, levelMod, modifier);                
                    return;
                }
                if (spriteType == Type.LEFT) {
                    interactWithTiles(xPix-radius, yPix, xPix, yPix, levelMod, modifier);  
                    return;
                }
            }

            if (activeItem != null && activeItem.isDestroyed()) {
                activeItem = null;
            }

        }        
    }            
    
    /**
     * Metoda ktora zautoci na entitu. Poskodenie entity je urcene z parametrov.
     * Na zaciatku testujeme ci sme presiahli minimalnu hodnotu modifikatoru na zautocenie.
     * Nasledne volame podla SpriteType z ktorych pozicii vyberame entitu. Vyber dlazdice je ovplyvneny
     * premennymi attackRadius. Potom volame interactWithEntities ktora blizsie vykona 
     * operaciu s entitami. Nakonci testujeme ci je aktivny item stale schopny utoku.
     * Ked nie tak ho vymazeme.
     * @param modifier Modifikator utoku a poskodenia
     */
    public void attack(double modifier) {                        
        
        if (modifier > 0.25) {
        
            // double staminaPen = modifier * staminaDischarger;            
            int radius = attackRadius;
        
            if ((activeItem == null)||(activeItem.canAttack())) {
                if (spriteType == Type.UP) {
                    interactWithEntities(xPix - SEGMENTLENGTH , yPix, xPix + SEGMENTLENGTH, yPix-radius, modifier);                
                    return;                    
                }
                if (spriteType == Type.DOWN) {
                    interactWithEntities(xPix - SEGMENTLENGTH, yPix + radius, xPix + SEGMENTLENGTH, yPix, modifier);                
                    return;
                }
                if (spriteType == Type.RIGHT) {
                    interactWithEntities(xPix, yPix + SEGMENTLENGTH, xPix + radius, yPix - SEGMENTLENGTH, modifier);                
                    return;
                }
                if (spriteType == Type.LEFT) {
                    interactWithEntities(xPix-radius, yPix + SEGMENTLENGTH, xPix, yPix - SEGMENTLENGTH, modifier);  
                    return;
                }
            }
                                    
            if (activeItem != null && activeItem.isDestroyed()) {
                activeItem = null;
            }
            
            }                        
    }
    
    /**
     * Metoda ktora dostava 4 prve parametre ako vymedzenie priestoru do ktoreho utocime.
     * Ked sa v tomto priestore nachadzaju nejake entity tak zavola metodu tryHurt
     * ktora entitu skusi poranit. Premenna done sluzi ako indikator ci sa podarilo
     * utocenie na entity.
     * @param x0 lava suradnica vymedzeneho priestoru
     * @param y0 dolna suradnica vymedzeneho priestoru
     * @param x1 prava suradnica vymedzeneho priestoru
     * @param y1 horna suradnica vymedzeneho priestoru
     * @param modifier modifikator pre vyhodnotenie utoku
     * @return Ake poskodenie sme vykonali dokopy na vsetkych entitach
     */
    @Override
    public double interactWithEntities(int x0, int y0, int x1, int y1, double modifier) {
        double entInteracted = 0d;        
        for (Entity e : map.getNearEntities()) {
            if (e != this && !(e instanceof Item)) {
                if ((e.xPix >= x0)&&(e.xPix <= x1)&&
                        (e.yPix <= y0)&&(e.yPix >= y1)) {
                    entInteracted += tryHurt(e, modifier);                    
                }               
            }
        }                
        
        return entInteracted;
    }
    
    /**
     * Metoda ktora podobne ako interactWithEntites dostava 4 prve parametre ako vymedzenie priestoru do ktoreho utocime.
     * Na tychto suradniciach vyberie dlazdicu ktora sa tam nachadza a zavola metodu tryHurt s dlazdicou na ktoru utocime.
     * @param x0 lava suradnica vymedzeneho priestoru
     * @param y0 dolna suradnica vymedzeneho priestoru
     * @param x1 prava suradnica vymedzeneho priestoru
     * @param y1 horna suradnica vymedzeneho priestoru
     * @param modifier modifikator pre vyhodnotenie utoku
     * @return Ake poskodenie sme vykonali
     */
    public double interactWithTiles(int x0, int y0, int x1, int y1, int levelMod, double modifier) {        
        int xTile = (x0 + x1)/2;
        int yTile = (y0 + y1)/2;
        
        Chunk chunk = map.chunkPixExist(xTile, yTile);
        int x = xTile >> 5, y = yTile >> 5;
        if (x < 0) {
            x = Chunk.getSize() + x;
        }
        if (y < 0) {
            y = Chunk.getSize() + y;
        }        
        
        
        if (targetedTile == null) {                    
            targetedTile = new AttackedTile(chunk, level + levelMod, x, y);            
        } else {
            if (targetedTile.getX() != x || targetedTile.getY() != y || targetedTile.getLevel() != level + levelMod) {
                targetedTile = new AttackedTile(chunk, level + levelMod, x, y);
            }           
        }
        
        return tryHurt(targetedTile, chunk, modifier);        
    }
    
    /**
     * Metoda ktora sa pokusi o ublizenie entite zadanej ako parameter <b>e</b>.
     * Do uvahy sa pri utoku berie attack terajsej entity a defense entity zadanej ako parameter.
     * Tieto dva parametre sa navzajom predelia a nasledne sa bude testovat ci je to vacsie alebo mensie
     * ako nahodna hodnota. Ked je to mensie tak sme minuli, inak sme trafili a utocime 
     * na entitu. (sila utorku je damage * modifier - pasivna obrana).
     * @param e Entita na ktoru utocime
     * @param modifier Modifikator pre utok aj silu utoku.
     * @return Hodnotu kolko sme entite ubrali.
     */
    protected double tryHurt(Entity e, double modifier) {
        double thisModAttack = attack * modifier * 2;
        double entityModDefense = e.defenseA * (e.acumDefense * 2 / e.maxPower);
        
        if ((thisModAttack / entityModDefense ) < random.nextDouble()) {
            map.addParticle(new TextParticle("Miss", 24, -24));
            return 0d;
        } else {            
            e.addAfterEffectsFrom(this, activeItem);            
            return e.hit(damage * modifier - e.defenseP, spriteType);                        
        }
    }
    
    /**
     * Metoda ktora sa pokusi o ublizenie dlazdici. Vacsinou ublizit dlazdici moze iba player
     * ale pre buduce verzie je mozne implementovat aj moznost utocenia pohyblivych entit.
     * @param tile Dlazdica na ktoru utocime
     * @param chunk Chunk kde utocime
     * @param modifier Modifikator kolko zo sily sa ubera/pribera pri utoku.
     * @return Hodnotu ktoru sme dlazdici ubrali
     */
    protected double tryHurt(AttackedTile tile, Chunk chunk, double modifier) {                        
        return 0d;        
    }                    
        
    /**
     * Tato metoda sa vykona iba vtedy ked je vzdialenost entit od seba 25 pixelov.
     * Potom posunie entitu zadanu parametrom entity podla metody 
     * pushWith. 
     * @param entity Entita ktoru chceme posunut.
     * @return True/False ci sa podarilo posunutie.
     */
    @Override
    protected boolean entityPush(Entity entity, int x0, int y0) {         
        int xP = entity.getXPix() - x0;
        int yP = entity.getYPix() - y0;
        if ((xP * xP + yP * yP) < 512) {
            pushWith(entity);
        } else {
            return false;
        }
        
        return true;
    }   
    
    /**
     * Metodad pushWith posunie entitu zadanu parametrom entity do prislusnej strany
     * pomocou dalsej metody knockback.
     * @param entity Entita ktoru posuvame
     */
    @Override
    public void pushWith(Entity entity) {
        entity.knockback(this.spriteType);        
    }

    //</editor-fold>
          
    // <editor-fold defaultstate="collapsed" desc=" Equipping/Using/Dropping ">
    /**
     * Metoda ktora vyzlecie z entity predmet zadany ako parameter <b>item</b>
     * Pri vyzleceni sa odstranuju efekty ktore sme ziskali pri obleceni, nastavi priznak vyzlecenia
     * predmetu na false a nastavi oblecenie na prislusnej pozicii na false.
     * @param item Predmet ktory vyzliekame
     */
    @Override
    public void unequip(Item item) {        
        removeEffects(item, EffectEvent.ONEQUIP);
        Armor armor = (Armor)item;
        armor.unequip();
        gear.put(armor.getArmorType(), null);      
    }

    /**
     * Metoda ktora oblecie entite predmet zadany ako parameter <b>item</b>
     * Pri obleceni sa testuje ci ma entita uz na prislusnom slote nejaky predmet. Ked
     * ano tak ho vyzlecie metodou unequip. Pre novy predmet nastavi priznak vyzlecenia
     * predmetu na true a nastavi oblecenie na prislusnej pozicii na dany predmet.
     * @param item Predmet ktory obliekame
     */
    @Override
    public void equip(Item item) {
        addEffects(item, EffectEvent.ONEQUIP);
        Armor armor = (Armor)item;
        Armor lastArmor = gear.get(armor.getArmorType());
        if (lastArmor != null) {
            unequip(lastArmor);
        }
        gear.put(armor.getArmorType(), armor);
        armor.equip();
    }
    
    /**
     * Metoda ktora pouzije predmet zadany parametrom <b>item</b> na entitu.
     * Metoda sa sklada z volania removeItem na odstranenie predmetu ktory pouzivame
     * a pridanie efektov z predmetu pomocou addEffects.
     * @param item Predmet ktory pouzivame
     */
    @Override
    public void use(Item item) {
        removeItem(item);
        addEffects(item, EffectEvent.ONUSE);
    }
    
    /**
     * Metoda ktora pouzije predmet zadany parametrom <b>item</b> na entitu.
     * Metoda je podobna metode use s tym rozdielom ze nevymazavame predmet z inventara.
     * @param item Predmet ktory pouzivame
     */
    public void useWithoutRemoving(Item item) {
        addEffects(item, EffectEvent.ONUSE);
    }

    /**
     * Metoda ktora vyhodi predmet zadany parametrom <b>item</b> na mapu. Metoda
     * odstrani predmet z inventara a prida ho na mapu.
     * @param item Predmet ktory vyhadzujeme.
     */
    @Override
    public void drop(Item item) {
        if (item.getCount() <= 1) {
            if (item.isActive()) {
                setActiveItem(null);                
            }
            if (item.isEquipped()) {
                unequip(item);
            }
            inventory.remove(item);
            item.setXYPix(xPix, yPix, level);
            map.addEntity(item);
            return;
        } else {
            item.decCount();            
            item.setXYPix(xPix, yPix, level);
            map.addEntity(item);
        }
    }
    
    // </editor-fold>    

    // <editor-fold defaultstate="collapsed" desc=" Handling ">
    /**
     * Prazdna metoda s ovladanim. Vacsinou hu implementuje iba Player.
     */
    public void inputHandling() {       
    }   
           
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Externalizacne metody ">
    /**
     * <i>{@inheritDoc }</i>
     * <p>
     * Doplnujuc k rodicovskemu writeExternal zapiseme do vystupneho streamu aj staminu.
     * </p>
     * @param out {@inheritDoc}
     * @throws IOException {@inheritDoc } 
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out); //To change body of generated methods, choose Tools | Templates.   
        out.writeBoolean(moveable);
        out.writeDouble(stamina);
    }

    /**
     * <i>{@inheritDoc }</i>
     * <p>
     * Doplnujuc k rodicovskemu readExternal nacitavame zo vstupneho streamu aj staminu.
     * Po nacitani reinicializujeme staty.
     * </p>
     * @param in {@inheritDoc}
     * @throws IOException {@inheritDoc } 
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in); //To change body of generated methods, choose Tools | Templates.        
        this.moveable = in.readBoolean();
        this.stamina = in.readDouble();
        reinitialize();
    }
    
    //</editor-fold>   
}
