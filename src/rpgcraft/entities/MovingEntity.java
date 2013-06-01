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
import java.util.Iterator;
import rpgcraft.effects.Effect;
import rpgcraft.effects.Effect.EffectEvent;
import rpgcraft.entities.ai.Ai;
import rpgcraft.entities.ai.DefaultAi;
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
import rpgcraft.utils.MainUtils;

/**
 *
 * @author Kirrie
 */
public class MovingEntity extends Entity {                               
    protected static final int SEGMENTLENGTH = 32;    
    
    protected double xDelay, yDelay;
    protected int lightRadius;    

    protected int originX, originY;
    protected int xGo, yGo;        
    
    protected InputHandle input;    
    public boolean disabled = false;
    public boolean damageDisabled = false;
    private boolean attackStarted = false;
    private double randomValue;
    
    protected boolean swimable;
    protected boolean swimming;
    protected int walking;
    
    protected int invulnerability;
    
    protected double staminaRegen;
    protected double staminaDischarger;
    protected double stamina;
    protected double maxStamina;                
       
    protected double fullSpeed;
    protected Double doublexGo = 0d;
    protected Double doubleyGo = 0d;

    protected AttackedTile targetedTile;
    protected int skillPoints, statPoints;    
    
    protected HashMap<Sprite.Type, ArrayList<Sprite>> movingEntitySprites;       
    
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
        this.name = name == null ? res.getName() : name;        
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Initializing ">
    @Override
    public void initialize() {        
        this.movingEntitySprites = res.getEntitySprites();
        this.id = res == null ? "" : res.getId();
        this.ai = res.getAi() == null ? DefaultAi.getInstance() : Ai.getAi(res.getAi());
        this.maxPower = new Double(24);
        this.attackable = true;
        this.pushable = true;        
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
    
    @Override
    protected void reinitialize() {
        super.reinitialize();        
        this.ai = res.getAi() == null ? DefaultAi.getInstance() : Ai.getAi(res.getAi());
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
        
        if (activeItem == null) {
            levelType = ItemLevelType.HAND;
        } else if (activeItem != null) {
            levelType = activeItem.levelType;
        }
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
     * Metoda ktora nastavi ovladanie pre Entitu. Moznost pouzitia na ovladanie aj inych entit
     * nez len jedneho playera (logicke pohybovanie s nepriatelom aby spustil nejaku udalost)
     * @param input Ovladanie pre entitu
     * @see InputHandle
     */
    public void setHandling(InputHandle input) {
        this.input = input;
    }
    
    @Override
    public void setName(String name) {
        super.setName(name);
    }
    
    public void setType(Type type) {
        this.spriteType = type;
    }

    public void setLightRadius(int lightRadius) {
        this.lightRadius = lightRadius;
    }
    
    public void setxDelay(double value) {
        this.xDelay = value;
    }
    
    public void setTarget(Entity e) {
        this.targetEntity = e;
    }
    
    public void setyDelay(double value) {
        this.yDelay = value;
    }
    
    public void setxGo(double value) {
        this.doublexGo = value;
    }
    
    public void setyGo(double value) {
        this.doubleyGo = value;
    }
    
    public void incxGo(double modifier, double value) {
        this.doublexGo += fullSpeed * modifier + value;
    }
    
    public void incyGo(double modifier, double value) {
        this.doubleyGo += fullSpeed * modifier + value;
    }
    
    public void setSpeed(int speed) {
        intStats.put(Stat.SPEED, speed);
    }
        
     
    public void setStamina(int stamina) {
        this.stamina = stamina;
    }        
       
    public void incStamina(double power, double modifier, double value) {
        this.stamina += power * modifier + value;
    }
    
    @Override
    public void regenStamina(double stamina) {
        this.stamina += stamina;
        if (this.stamina > maxStamina) {
            this.stamina = maxStamina;
        }
    }
    
    public void setMaxStamina(int maxStamina) {
        this.maxStamina = maxStamina;
    }
            
    public void setMaxPower(double maxPower) {
        this.maxPower = maxPower;
    }
     
    public void setAttackStarted(boolean state) {
        this.attackStarted = state;
    }
    
    public void incDoublexGo(double modifier, double value) {
        doublexGo += fullSpeed * xDelay * modifier + value;
    }
    
    public void incDoubleyGo(double modifier, double value) {
        doubleyGo += fullSpeed * yDelay * modifier + value;
    }
    
    public void setGroup(int group) {
        this.group = group;
    }
    
    @Override
    public void setChunk(Chunk chunk) {
        super.setChunk(chunk);
    }  
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    public boolean canSwim() {
        return swimable;
    }
    
    public boolean isSwimming() {
        return swimming;
    }
    
    public boolean hasAttacked() {
        return attackStarted;
    }
           
    @Override
    public double getStamina() {
        return stamina;
    }
    
    public double getMaxStamina() {
        return maxStamina;
    }
    
    public double getSpeed() {
        return intStats.get(Stat.SPEED);
    }
    
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
    
    public int getLightRadius() {
        return lightRadius;
    }        
    
    // </editor-fold>        
    
    public void trySpawn(MovingEntity entity, int scatter) {
        this.xPix = entity.xPix;
        this.yPix = entity.yPix;
        this.level = entity.level < 0 ? 0 : (entity.level > 127 ? 127 : entity.level);
        Chunk chunk = map.chunkXYExist(xPix >> 9, yPix >> 9);
        int x0, y0, z0;
        do {
            x0 = scatter + random.nextInt(scatter);
            y0 = scatter + random.nextInt(scatter);
            z0 = scatter + random.nextInt(scatter);
            this.actualChunk = map.chunkXYExist(x0 >> 9, y0 >> 9);
        } while (!tryMove(x0, y0, z0, chunk));
        /*
        this.x = xPix << 5;
        this.y = yPix << 5;
        this.position = new EntityPosition(x, y);
        */
        map.addEntity(this);
    }
    
    public void trySpawn(int x, int y, int z, int scatter) {
        this.xPix = x;
        this.yPix = y;
        this.level = z < 0 ? 0 : (z > 127 ? 127 : z);
        Chunk chunk = map.chunkXYExist(xPix >> 9, yPix >> 9);
        int x0, y0, z0;
        
        do {
            x0 = scatter + random.nextInt(scatter);
            y0 = scatter + random.nextInt(scatter);
            z0 = scatter + random.nextInt(scatter);
            this.actualChunk = map.chunkXYExist(x0 >> 9, y0 >> 9);
        } while (!tryMove(x0, y0, z0, chunk));
        
        /*
        this.x = xPix << 5;
        this.y = yPix << 5;
        this.position = new EntityPosition(x, y);
        */
        map.addEntity(this);
    }
    
    @Override
    public double hit(double damage, Type type) {
        if (damageDisabled) return 0;
        
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
    
    @Override
    public void knockback(Type type) {
        if (type == Type.RIGHT) {
            xKnock = 6;
        }
        if (type == Type.LEFT) {
            xKnock = -6;
        }
        if (type == Type.DOWN) {
            yKnock = 6;
        }
        if (type == Type.UP) {
            yKnock = -6;
        }
    }
    
    
    @Override
    public boolean update() {
            if (isDestroyed() || isSaving()) {
                return false;
            }
                                       
            
            // Kazdym prechodom tejto metody doplni staminu, prerobit na time dependent            

            if (stamina<maxStamina) {
                stamina += staminaRegen;                
            }      

            if (health<maxHealth) {
                health += healthRegen;
            }
            
            
            ai.aiMove(this);
                
            return true;
        

    }
    
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
            if (e != this) {
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
    
    protected double tryHurt(Entity e, double modifier) {
        double thisModAttack = attack * (modifier * 2);
        double entityModDefense = e.defenseA * (e.acumDefense * 2 / e.maxPower);
        
        if ((thisModAttack / entityModDefense ) < random.nextDouble()) {
            map.addParticle(new TextParticle("Miss", 24, -24));
            return 0d;
        } else {            
            e.addAfterEffectsFrom(this, activeItem);
            return e.hit(damage * modifier - e.defenseP, spriteType);                                    
        }
    }
    
    protected double tryHurt(AttackedTile tile, Chunk chunk, double modifier) {                        
        return 0d;        
    }
        
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
     * @param xGo Smer entity po x-ovych suradnicach.
     * @param yGo Smer entity po y-ovych suradniciach.
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

    @Override
    public void unequip(Item item) {        
        removeEffects(item, EffectEvent.ONEQUIP);
        Armor armor = (Armor)item;
        armor.unequip();
        gear.put(armor.getArmorType(), null);      
    }

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
    
    @Override
    public void use(Item item) {
        removeItem(item);
        addEffects(item, EffectEvent.ONUSE);
    }

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
    
    @Override
    public void setImpassableTile(int tile)  {
        if (Tile.tiles.containsKey(tile)) {
            impassableTiles.add(tile);
        }
    }

    /**
     * Prazdna metoda s ovladanim. Vacsinou hu implementuje iba Player.
     */
    public void inputHandling() {
        
    }   

    @Override
    public void decStats(Entity item) {
        super.decStats(item);
        this.maxStamina = doubleStats.get(Stat.STAMINAMAX) * (1 + doubleStats.get(Stat.STAMINAMAXPER)); 
        this.staminaRegen = doubleStats.get(Stat.STAMINAREGEN) * (1 + doubleStats.get(Stat.STAMINAREGENPER));
        this.fullSpeed = doubleStats.get(Stat.DBLFACTOR) + ((double)intStats.get(Stat.SPEED))/(getInt(Stat.SPDRATER, 1));
    }
    
    @Override
    public void incStats(Entity item) {
        super.incStats(item); //To change body of generated methods, choose Tools | Templates.          
        this.maxStamina = doubleStats.get(Stat.STAMINAMAX) * (1 + doubleStats.get(Stat.STAMINAMAXPER));  
        this.staminaRegen = doubleStats.get(Stat.STAMINAREGEN) * (1 + doubleStats.get(Stat.STAMINAREGENPER));
        this.fullSpeed = doubleStats.get(Stat.DBLFACTOR) + ((double)intStats.get(Stat.SPEED))/(getInt(Stat.SPDRATER, 1));        
    }
    
    
    
    
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out); //To change body of generated methods, choose Tools | Templates.        
        out.writeDouble(stamina);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in); //To change body of generated methods, choose Tools | Templates.        
        this.stamina = in.readDouble();
        reinitialize();
    }
    
    
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
   
}
