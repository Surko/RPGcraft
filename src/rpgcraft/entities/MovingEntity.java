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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;
import rpgcraft.effects.Effect;
import rpgcraft.entities.ai.Ai;
import rpgcraft.entities.ai.DefaultAi;
import rpgcraft.entities.types.ItemLevelType;
import rpgcraft.graphics.ui.particles.BarParticle;
import rpgcraft.graphics.ui.particles.TextParticle;
import rpgcraft.graphics.spriteoperation.Sprite;
import rpgcraft.graphics.spriteoperation.Sprite.Type;
import rpgcraft.handlers.InputHandle;
import rpgcraft.map.SaveMap;
import rpgcraft.map.chunks.Chunk;
import rpgcraft.map.tiles.AttackedTile;
import rpgcraft.map.tiles.BlankTile;
import rpgcraft.map.tiles.Tile;
import rpgcraft.resource.EffectResource;
import rpgcraft.resource.EffectResource.EffectEvent;
import rpgcraft.resource.EntityResource;
import rpgcraft.resource.StatResource;
import rpgcraft.resource.StatResource.Stat;

/**
 *
 * @author Kirrie
 */
public class MovingEntity extends Entity {                       
    
    private static final int STATMULT = 5;
    
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
        
    private HashMap<Sprite.Type, ArrayList<Sprite>> movingEntitySprites;       
    
    /**
     * Prazdny konstruktor pre vytvorenie instancie Externalizaciou.
     */
    public MovingEntity() {}
    
    protected MovingEntity(Element elem) {         
        this.aggresivity = 50;
        this.defenseP = 0;
        this.defenseA = 20;
        this.attack = 10;
        
                         
    }
    
    public MovingEntity(String name, SaveMap map, EntityResource res) {
        this.map = map;        
        this.res = res;        
        this.name = name == null ? res.getName() : name;                
    }
    
    public void initialize() {
        doubleStats = new HashMap<>();
        intStats = new HashMap<>();    
        activeEffects = new HashMap<>();
        this.movingEntitySprites = res.getEntitySprites();
        this.id = res.getId();
        this.ai = res.getAi() == null ? DefaultAi.getInstance() : Ai.getAi(res.getAi());
        this.maxPower = new Double(24);
        this.attackable = true;
        this.pushable = true;        
        this.activeItem = res.getActiveItem() != null ? res.getActiveItem() : null;            
        this.group = res.getGroup();
        this.concentration = res.getConcentration();
        
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
        
        try {
            // Hlavne Staty
            doubleStats.put(Stat.HEALTHMAX, rollRandom(res.getMinHealth(), res.getMaxHealth()));
            doubleStats.put(Stat.STAMINAMAX, rollRandom(res.getMinStamina(), res.getMaxStamina()));
            intStats.put(Stat.STRENGTH, rollRandom(res.getMinStr(), res.getMaxStr()));
            intStats.put(Stat.AGILITY, rollRandom(res.getMinAgi(), res.getMaxAgi()));
            intStats.put(Stat.SPEED, rollRandom(res.getMinSpd(), res.getMaxSpd()));
            
            // O kolko sa doplni stamina
            doubleStats.put(Stat.STAMINAREGEN, res.getStaminaRegen());
            // O kolko sa doplni zivot
            doubleStats.put(Stat.HEALTHREGEN, res.getHealthRegen());
            
            // Damage 
            
            // Attack/Defense Rating
            doubleStats.put(Stat.ATKRATING, (intStats.get(Stat.AGILITY) * STATMULT) 
                    + (intStats.get(Stat.SPEED) * (STATMULT - 3)) + 
                    (activeItem != null ? activeItem.attackRating : 0) + res.getAttRatingBonus());
            doubleStats.put(Stat.DEFRATING, (intStats.get(Stat.AGILITY) * STATMULT)
                    + (intStats.get(Stat.SPEED) * (STATMULT - 3)) +
                    (activeItem != null ? activeItem.attackRating : 0) + res.getDefRatingBonus());
            System.out.println(name + " " + doubleStats.get(Stat.ATKRATING));
            // Attack/Defense Power
            doubleStats.put(Stat.ATKPOWER, doubleStats.get(Stat.ATKRATING) / intStats.get(Stat.ATKRATER));
            doubleStats.put(Stat.DEFPOWER, doubleStats.get(Stat.DEFRATING) / intStats.get(Stat.DEFRATER));
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, 
                    "Xml file has min value greater than max :\n" + e.getMessage() );
        }
        doubleStats.put(Stat.DBLFACTOR, res.getDoubleFactor());
        // Sanca na prerusenie - aku sancu ma entita ze prerusi cudzi utok
        doubleStats.put(Stat.INTCHANCE, res.getInterChance());
        // Koncetracia - kolko sa odcitava od nepriatelovej interruptionChance
        
        // O kolko sa doplni stamina
        doubleStats.put(Stat.STAMINAREGEN, res.getStaminaRegen());
        // Vzdialenost ako daleko dosiahne utok. Pri nule je schopna entita iba posuvat.
        intStats.put(Stat.ATKRADIUS, res.getAttackRadius());
        
        /*
         * Najcastejsie pouzivane staty, ktore bude mat kazda entita. Pri zmene 
         * statov ulozenych v Hashmapach sa prepocitaju.
         */
        this.fullSpeed = doubleStats.get(Stat.DBLFACTOR) + (new Double(intStats.get(Stat.SPEED))/(new Double(intStats.get(Stat.SPDRATER))));
        this.spriteType = Type.DOWN;
        this.maxStamina = doubleStats.get(Stat.STAMINAMAX);
        this.stamina = maxStamina;
        this.maxHealth = doubleStats.get(Stat.HEALTHMAX);
        this.staminaRegen = doubleStats.get(Stat.STAMINAREGEN);
        this.staminaDischarger = maxStamina;
        this.attackRadius = intStats.get(Stat.ATKRADIUS);        
        this.health = maxHealth;        
        this.damage = 5;
        this.attackRadius = 32;
    }
    
    private void reinitialize() {
        this.ai = res.getAi() == null ? DefaultAi.getInstance() : Ai.getAi(res.getAi());
        this.movingEntitySprites = res.getEntitySprites();
        this.id = res.getId();
        this.maxStamina = doubleStats.get(Stat.STAMINAMAX);
        this.staminaRegen = doubleStats.get(Stat.STAMINAREGEN);
        this.maxHealth = doubleStats.get(Stat.HEALTHMAX);
        this.maxPower = new Double(24);
        this.fullSpeed = doubleStats.get(Stat.DBLFACTOR) + (new Double(intStats.get(Stat.SPEED))/(new Double(intStats.get(Stat.SPDRATER))));        
        this.damage = 5;
        this.attackRadius = 32;
        
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
    
    /**
     * Metoda ktora nastavi ovladanie pre Entitu. Moznost pouzitia na ovladanie aj inych entit
     * nez len jedneho playera (logicke pohybovanie s nepriatelom aby spustil nejaku udalost)
     * @param input Ovladanie pre entitu
     * @see InputHandle
     */
    public void setHandling(InputHandle input) {
        this.input = input;
    }
    
    public void inputHandling() {
        
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
        
    public double getSpeed() {
        return intStats.get(Stat.SPEED);
    }
        
    public void setStamina(int stamina) {
        this.stamina = stamina;
    }
    
    public double getStamina() {
        return stamina;
    }
    
    public void incStamina(double modifier, double value) {
        this.stamina += doubleStats.get(StatResource.Stat.ATKPOWER) * modifier + value;
    }
    
    public void setMaxStamina(int maxStamina) {
        this.maxStamina = maxStamina;
    }
    
    
    public double getMaxStamina() {
        return maxStamina;
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
    
    public boolean canSwim() {
        return swimable;
    }
    
    public boolean isSwimming() {
        return swimming;
    }
    
    public boolean hasAttacked() {
        return attackStarted;
    }
    
    public void setGroup(int group) {
        this.group = group;
    }
    
    @Override
    public String toString() {
        String s = this.id + ":"+ this.name + " loaded with their characteristics \n"
                + "------------------------------------------------------------------------------ \n";
        return s;
        
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
    
    @Override
    public void setChunk(Chunk chunk) {
        super.setChunk(chunk);
    }
    
    public void trySpawn() {
        this.actualChunk = map.chunkXYExist(xPix >> 9, yPix >> 9);
        this.level = 64;
    }
    
    @Override
    public void hit(double damage, Type type) {
            doHit(damage, type);
    }
    
    protected void doHit(Double damage, Type type) {
        if (damageDisabled) return;
        
        health -= damage;
        
        TextParticle particle = null;
        
        knockback(type);
        
        if (damage > 1) {
            particle = new TextParticle(String.valueOf(damage.intValue()), 24, -24);
        } else {
            particle = new TextParticle("1", 24, -24);
        }
        map.addParticle(particle);
        
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
                  
            /* Riesenie cez iterator ktory vymaze z aktivnych efektov taky ktory
             * uz skoncil alebo tam nejakym sposobom nepatri
             * Bez iteratora, vymazavanim rovno z Listu by vyhodilo ConcurrentModificationException
             */
            ArrayList<Effect> effects = activeEffects.get(EffectEvent.ONSELF);
            if (effects != null) {
                for (Iterator iter = effects.iterator(); iter.hasNext();) {
                        if (!((Effect)iter.next()).update()) {
                            iter.remove();                    
                    }
                }
            }
            
            // Kazdym prechodom tejto metody doplni staminu, prerobit na time dependent
            if (stamina<maxStamina) {
                stamina += staminaRegen;                
            }      
            
            ai.aiMove(this);
                
            return true;
        

    }
    
    public void attackTile(double modifier) {
        if (modifier > 0.25) {
            if (modifier > 0.25) {
        
                // double staminaPen = modifier * staminaDischarger;            
                int radius = activeItem == null ? attackRadius : activeItem.attackRadius;

                if ((activeItem == null)||(activeItem.canAttack())) {
                    if (spriteType == Type.UP) {
                        interactWithTiles(xPix , yPix, xPix, yPix-radius, modifier);                
                        return;                    
                    }
                    if (spriteType == Type.DOWN) {
                        interactWithTiles(xPix, yPix + radius, xPix, yPix, modifier);                
                        return;
                    }
                    if (spriteType == Type.RIGHT) {
                        interactWithTiles(xPix, yPix, xPix + radius, yPix, modifier);                
                        return;
                    }
                    if (spriteType == Type.LEFT) {
                        interactWithTiles(xPix-radius, yPix, xPix, yPix, modifier);  
                        return;
                    }
                }



                if (activeItem != null && activeItem.isDestroyed()) {
                    activeItem = null;
                }
            
            }
        }
    }            
    
    public void attack(double modifier) {                        
        
        if (modifier > 0.25) {
        
            // double staminaPen = modifier * staminaDischarger;            
            int radius = activeItem == null ? attackRadius : activeItem.attackRadius;
        
            if ((activeItem == null)||(activeItem.canAttack())) {
                if (spriteType == Type.UP) {
                    interactWithEntities(xPix , yPix, xPix, yPix-radius, modifier);                
                    return;                    
                }
                if (spriteType == Type.DOWN) {
                    interactWithEntities(xPix, yPix + radius, xPix, yPix, modifier);                
                    return;
                }
                if (spriteType == Type.RIGHT) {
                    interactWithEntities(xPix, yPix, xPix + radius, yPix, modifier);                
                    return;
                }
                if (spriteType == Type.LEFT) {
                    interactWithEntities(xPix-radius, yPix, xPix, yPix, modifier);  
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
     * @return 
     */
    @Override
    public int interactWithEntities(int x0, int y0, int x1, int y1, double modifier) {
        boolean entInteracted = false;
        for (Entity e : map.getEntities()) {
            if (e != this) {
                if ((e.xPix >= x0)&&(e.xPix <= x1)&&
                        (e.yPix <= y0)&&(e.yPix >= y1)) {
                    entInteracted = tryHurt(e, modifier);                    
                }               
            }
        }                
        if (entInteracted) {
            return 0;
        }
        return 0;
    }
    
    /**
     * Metoda ktora podobne ako interactWithEntites dostava 4 prve parametre ako vymedzenie priestoru do ktoreho utocime.
     * Na tychto suradniciach vyberie dlazdicu ktora sa tam nachadza a zavola metodu tryHurt s dlazdicou na ktoru utocime.
     * @param x0 lava suradnica vymedzeneho priestoru
     * @param y0 dolna suradnica vymedzeneho priestoru
     * @param x1 prava suradnica vymedzeneho priestoru
     * @param y1 horna suradnica vymedzeneho priestoru
     * @param modifier modifikator pre vyhodnotenie utoku
     * @return 
     */
    public int interactWithTiles(int x0, int y0, int x1, int y1, double modifier) {        
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
            targetedTile = new AttackedTile(chunk, level - 1, x, y);            
        } else {
            if (targetedTile.getX() != x || targetedTile.getY() != y) {
                targetedTile = new AttackedTile(chunk, level - 1, x, y);
            }           
        }
        
        tryHurt(targetedTile, chunk, modifier);
        return 0; 
    }
    
    protected boolean tryHurt(Entity e, double modifier) {
        double thisModAttack = attack * (modifier * 2);
        double entityModDefense = e.defenseA * (e.acumDefense * 2 / e.maxPower);
        
        if ((thisModAttack / entityModDefense ) < random.nextDouble()) {
            map.addParticle(new TextParticle("Miss", 24, -24));
            return true;
        } else {
            System.out.println(modifier);
            e.hit(damage * modifier - e.defenseP, spriteType);            
            e.addAfterEffectsFrom(this, activeItem);
            return true;
        }
    }
    
    protected boolean tryHurt(AttackedTile tile, Chunk chunk, double modifier) {                        
        return false;        
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
                if (spriteType != Sprite.Type.RIGHT) {
                    this.spriteType = Sprite.Type.RIGHT;  
                    maxTime = 0;
                }
            }
            if ((ixGo < 0)) {
                if (spriteType != Sprite.Type.LEFT) {
                    this.spriteType = Sprite.Type.LEFT;  
                    maxTime = 0;
                }
            }
            if ((iyGo > 0)) {
                if (spriteType != Sprite.Type.DOWN) {
                    this.spriteType = Sprite.Type.DOWN;  
                    maxTime = 0;
                }
            }
            if ((iyGo < 0)) {
                if (spriteType != Sprite.Type.UP) {
                    this.spriteType = Sprite.Type.UP;  
                    maxTime = 0;
                }
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
    protected boolean entityPush(Entity entity) {  
       
        int xP = entity.getXPix() - xPix;
        int yP = entity.getYPix() - yPix;
        if ((xP * xP + yP * yP) < 576) {
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
    public void unequip(Entity e) {
        
    }

    @Override
    public void equip(Entity e) {
        
    }
    
    @Override
    public void use(Entity item) {
        
    }

    @Override
    public void setImpassableTile(int tile)  {
        if (Tile.tiles.containsKey(tile)) {
            impassableTiles.add(tile);
        }
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
    
    
}
