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
import rpgcraft.graphics.particles.BarParticle;
import rpgcraft.graphics.particles.TextParticle;
import rpgcraft.graphics.spriteoperation.Sprite;
import rpgcraft.graphics.spriteoperation.Sprite.Type;
import rpgcraft.handlers.InputHandle;
import rpgcraft.map.SaveState;
import rpgcraft.map.chunks.Chunk;
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
    
    private int randomxDelay;
    private int randomyDelay;
    protected int lightRadius;    

    protected int originX, originY;
    protected int xGo, yGo;        
    
    protected InputHandle input;    
    public boolean disabled = false;
    public boolean damageDisabled = false;
    private boolean attackStart = false;
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
    
    public MovingEntity(String name, SaveState map, EntityResource res) {
        this.map = map;        
        this.res = res;        
        this.name = res.getName() != null ? res.getName() : name;                
    }
    
    public void initialize() {
        doubleStats = new HashMap<>();
        intStats = new HashMap<>();    
        activeEffects = new HashMap<>();
        this.movingEntitySprites = res.getEntitySprites();
        this.id = res.getId();
        this.maxPower = new Double(24);
        this.attackable = true;
        this.pushable = true;        
        this.activeItem = res.getActiveItem() != null ? res.getActiveItem() : this;            
        
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
            
            // Attack/Defense Rating
            doubleStats.put(Stat.ATKRATING, (intStats.get(Stat.AGILITY) * STATMULT) 
                    + (intStats.get(Stat.SPEED) * (STATMULT - 3)) + 
                    (activeItem != this ? activeItem.attackRating : 0) + res.getAttRatingBonus());
            doubleStats.put(Stat.DEFRATING, (intStats.get(Stat.AGILITY) * STATMULT)
                    + (intStats.get(Stat.SPEED) * (STATMULT - 3)) +
                    (activeItem != this ? activeItem.attackRating : 0) + res.getDefRatingBonus());
            
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
        this.concentration = 0d;
        this.health = maxHealth;
        this.group = 1;        
    }
    
    private void reinitialize() {
        movingEntitySprites = res.getEntitySprites();
        this.id = res.getId();
        this.maxStamina = doubleStats.get(Stat.STAMINAMAX);
        this.staminaRegen = doubleStats.get(Stat.STAMINAREGEN);
        this.maxHealth = doubleStats.get(Stat.HEALTHMAX);
        this.maxPower = new Double(24);
        this.fullSpeed = doubleStats.get(Stat.DBLFACTOR) + (new Double(intStats.get(Stat.SPEED))/(new Double(intStats.get(Stat.SPDRATER))));
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
    
    public void setMaxStamina(int maxStamina) {
        this.maxStamina = maxStamina;
    }
    
    
    public double getMaxStamina() {
        return maxStamina;
    }
    
    public void setMaxPower(double maxPower) {
        this.maxPower = maxPower;
    }
        
    
    public boolean canSwim() {
        return swimable;
    }
    
    public boolean isSwimming() {
        return swimming;
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
        if (currDur >= maxDur) {
            sprNum++;
            if (movingEntitySprites.get(spriteType).size() <= sprNum) sprNum = 0;        
            currDur = 0;
            maxDur = movingEntitySprites.get(spriteType).get(sprNum).getDuration();         
        }
        return movingEntitySprites.get(spriteType).get(sprNum).getSprite();
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
    protected void knockback(Type type) {
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
            
            // Kazdym prechodom tejto metody doplni staminu
            if (stamina<maxStamina) {
                stamina += staminaRegen;                
            }
            
            if ((targetEntity != null)) {
                int xP = targetEntity.getXPix() - xPix;
                int yP = targetEntity.getYPix() - yPix;
                int circleCoor = xP * xP + yP* yP;
                if (circleCoor < targetEntity.sound * targetEntity.sound) {                    
                    if (circleCoor < activeItem.attackRadius * activeItem.attackRadius) {
                        if (stamina > 0) { 
                            if (!attackStart) {
                                randomValue = random.nextDouble();
                                attackStart = true;                                   
                            } else {
                                 stamina -= doubleStats.get(Stat.ATKPOWER);                             
                                 activeItem.hpower += doubleStats.get(Stat.ATKPOWER);
                                 //poweringBar.setValue(activeItem.hpower);
                                 if (activeItem.hpower >= randomValue * activeItem.maxPower) {
                                     attack(randomValue);
                                     activeItem.hpower = 0;
                                     attackStart = false;
                                 }
                            }
                        }
                        knockMove();
                        return true;
                    } else {
                       attackStart = false;
                    }
                    
                    if (xP > 0) {
                        doublexGo += fullSpeed;
                    }
                    if (xP < 0) {
                        doublexGo -= fullSpeed;
                    }
                    if (yP < 0) {
                        doubleyGo -= fullSpeed;
                    }
                    if (yP > 0) {
                        doubleyGo += fullSpeed;
                    }                    
                } else {
                    doublexGo = 0d;
                    doubleyGo = 0d;
                    targetEntity = null;
                }
            } else {
                if (random.nextInt(50) == 0) {
                    randomxDelay = (random.nextInt(3) -1); 
                    randomyDelay = (random.nextInt(3) -1);
                } 
                doublexGo +=  randomxDelay * fullSpeed;
                doubleyGo +=  randomyDelay * fullSpeed;
            }
            
            
            if ((!updateCoordinates())) {
                
                     /* Aby boli zahrnute vsetky moznosti pri pohybe
                     * tak vygenerujeme nahodne cislo 0-2. Pricitame -1 a 
                     * potom vynasobime pre moment nahody kedy sa entita nepohne.
                     */
               if (random.nextInt(100) == 0) {
                    randomxDelay = (random.nextInt(3) -1); 
                    randomyDelay = (random.nextInt(3) -1);
               } 
               doublexGo +=  randomxDelay * fullSpeed;
               doubleyGo +=  randomyDelay * fullSpeed;
               }
                
            return true;
        

    }
    protected void attack(double modifier) {                        
        
        if (modifier > 0.25) {
        
            // double staminaPen = modifier * staminaDischarger;            
            double radius = activeItem.attackRadius;
        
            if ((activeItem == this)||(activeItem.canAttack())) {
                if (spriteType == Type.UP) {
                    interactWith(xPix - 8, yPix, xPix + 24, yPix-activeItem.attackRadius, modifier);                
                    return;                    
                }
                if (spriteType == Type.DOWN) {
                    interactWith(xPix - 8, yPix + activeItem.attackRadius, xPix + 24, yPix, modifier);                
                    return;
                }
                if (spriteType == Type.RIGHT) {
                    interactWith(xPix, yPix + 24, xPix + activeItem.attackRadius, yPix, modifier);                
                    return;
                }
                if (spriteType == Type.LEFT) {
                    interactWith(xPix-activeItem.attackRadius, yPix + 24, xPix, yPix, modifier);  
                    return;
                }
            }
            
            
            
            if (activeItem.isDestroyed()) {
                activeItem = this;
            }
            
            }            
            
    }
    
    /**
     * Metoda ktora dostava 4 prve parametre ako vymedzenie priestoru do ktoreho utocime.
     * Ked sa v tomto priestore nachadzaju nejake entity tak zavola metodu tryHurt
     * ktora entitu skusi poranit. Premenna done sluzi ako indikator ci sa podarilo
     * utocenie na entity. Ked tomu tak nie je tak pokracuje utocenie na dlazdice ktore 
     * entita rozbije.
     * @param x0 lava suradnica vymedzeneho priestoru
     * @param y0 dolna suradnica vymedzeneho priestoru
     * @param x1 prava suradnica vymedzeneho priestoru
     * @param y1 horna suradnica vymedzeneho priestoru
     * @param modifier modifikator pre vyhodnotenie utoku
     * @return 
     */
    @Override
    protected int interactWith(int x0, int y0, int x1, int y1, double modifier) {
        boolean done = false;
        for (Entity e : map.getEntities()) {
            if (e != this) {
                if ((e.xPix >= x0)&&(e.xPix <= x1)&&
                        (e.yPix <= y0)&&(e.yPix >= y1)) {
                    done = tryHurt(e, modifier);
                    
                }               
            }
        }                
        if (done) return 0;
        
        int xTile = (x0 + x1)/2;
        int yTile = (y0 + y1)/2;
        
        Chunk chunk = map.chunkPixExist(xTile, yTile);        
        Tile tile = map.tiles.get(chunk.getTile(level, xTile >> 5, yTile >> 5));
        
        tryHurt(tile, modifier);
        return 0;        
    }
    
    protected boolean tryHurt(Entity e, double modifier) {
        double thisModAttack = attack * (modifier * 2);
        double entityModDefense = e.defenseA * (e.dpower * 2 / e.maxPower);
        
        if ((thisModAttack / entityModDefense ) < random.nextDouble()) {
            map.addParticle(new TextParticle("Miss", 24, -24));
            return true;
        } else {
            e.hit(damage * modifier - e.defenseP, spriteType);            
            e.addAfterEffectsFrom(this, activeItem);
            return true;
        }
    }
    
    protected boolean tryHurt(Tile tile, double modifier) {                
        
        if (levelType == tile.getMaterialType())
            tile.hit(damage * modifier);        
        return true;
    }
    
    
    protected void knockMove() {
        if (xKnock < 0) {
            canMove(-2, 0);
            xKnock ++;
        }
        if (xKnock > 0) {
            canMove(2, 0); 
            xKnock --;
        }
        if (yKnock < 0) {
            canMove(0, -2);
            yKnock ++;
        }
        if (yKnock > 0) {
            canMove(0, 2); 
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
    protected boolean updateCoordinates() {                
        
        knockMove();
                                
        if ((doublexGo != 0d)||(doubleyGo != 0d)) {
            int ixGo = 0;
            int iyGo = 0;
            
            
            
            if ((doublexGo >= 1d)||((doublexGo <= -1d))) {
                doubleyGo = 0d;
                ixGo = doublexGo.intValue();
                doublexGo -= ixGo;
                currDur++;
            }
            if ((doubleyGo >= 1d)||(doubleyGo <= -1d)) {
                doublexGo = 0d;                
                iyGo = doubleyGo.intValue();
                doubleyGo -= iyGo;
                currDur++;
            }
                                                            
            
            if ((ixGo > 0)) this.spriteType = Sprite.Type.RIGHT;
            if ((ixGo < 0)) this.spriteType =Sprite.Type.LEFT;
            if ((iyGo > 0)) this.spriteType = Sprite.Type.DOWN;
            if ((iyGo < 0)) this.spriteType =Sprite.Type.UP;
                        
            
            if (canMove(ixGo, iyGo)) {
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
    protected void pushWith(Entity entity) {
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
        if (map.tiles.containsKey(tile)) {
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
