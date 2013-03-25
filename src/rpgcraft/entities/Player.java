/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import rpgcraft.entities.types.ArmorType;
import rpgcraft.entities.types.ItemLevelType;
import rpgcraft.entities.types.ItemType;
import rpgcraft.graphics.Colors;
import rpgcraft.graphics.inmenu.AbstractInMenu;
import rpgcraft.graphics.particles.BarParticle;
import rpgcraft.map.SaveMap;
import rpgcraft.map.chunks.Chunk;
import rpgcraft.map.tiles.AttackedTile;
import rpgcraft.resource.EntityResource;
import rpgcraft.resource.StatResource.Stat;
/**
 *
 * @author Kirrie
 */
public class Player extends MovingEntity {        
    private static final long serialVersionUID = 912804676578087866L;
    /**
     * Prazdny konstruktor pre vytvorenie instancie Externalizaciou.
     */
    public Player() {
        System.out.println("Player constructor called");
    }
    
    public Player(String name, SaveMap map, EntityResource res) {
        super(name, map, res);
        System.out.println("Player constructor called");
        this.poweringBar = new BarParticle();
        this.levelType = ItemLevelType.HAND;
        map.addParticle(poweringBar);
    }
    
    @Override
    public void initialize() {
        super.initialize();
        this.group = 2;        
    }

    
    /**
     * Metoda inputHandling zabezpecuje vstup uzivatela z klavesnice na ovladanie hraca.
     * Pri stlaceni jednotlivych tlacidiel definovanych v triede InputHandle sa menia
     * x a y-nove offsety na dlazdici takisto ako vseobecna pozicia vo svete (xPix, yPix).
     * Po kazdom vykonani tejto funkcie sa vykona aktualizovanie suradnic (x-ova a y-ova
     * suradnica urcujuca dlazdicu a xReg a yReg suradnica urcujuca region. V neskorsich 
     * castiach sa pravdepodobne vytrati cele menenie offsetov a vykreslovanie sa bude
     * urcovat iba pomocou vseobecnych suradnic.
     * @see InputHandle
     * 
     */
    @Override
    public void inputHandling() { 
            if (input.runningKeys.contains(input.up.getKeyCode())) {
                doubleyGo -= fullSpeed;                
            }
            if (input.runningKeys.contains(input.down.getKeyCode())) {
                doubleyGo += fullSpeed;  
            }
            if (input.runningKeys.contains(input.left.getKeyCode())) {
                doublexGo -= fullSpeed; 
            }        
            if (input.runningKeys.contains(input.right.getKeyCode())) {
                doublexGo += fullSpeed;  
            }    
            
            if ((input.runningKeys.contains(input.attack.getKeyCode())) && stamina>0) {
                if (!poweringBar.isActivated()) {
                    poweringBar.setActivated(Colors.getColor(Colors.healthColor));
                }
                if (activeItem.hpower < activeItem.maxPower) {
                    stamina-=doubleStats.get(Stat.ATKPOWER);
                    activeItem.hpower += doubleStats.get(Stat.ATKPOWER);
                    poweringBar.setValue(activeItem.hpower); 
                }
            }

            if (input.runningKeys.contains(input.defense.getKeyCode())&&(stamina>0)) {
                if (!poweringBar.isActivated()) {
                    poweringBar.setActivated(Colors.getColor(Colors.defenseColor));                
                }
                if (activeItem.dpower < activeItem.maxPower) {
                    stamina-=doubleStats.get(Stat.DEFPOWER);
                    activeItem.dpower += doubleStats.get(Stat.DEFPOWER);
                    poweringBar.setValue(activeItem.dpower); 
                }
            }
            
            if ((input.clickedKeys.contains(input.inventory.getKeyCode()))) {  
                map.setMenu(AbstractInMenu.menuList.get("inventory"));                    
                input.freeKeys();
            }                                   
    }     
    
    private void recalculatePositions() {               
        if (reload) {
            map.loadMapAround(this);
            reload = false;
        }       
    }
    
    @Override
    public void setChunk(Chunk chunk) {
        super.setChunk(chunk);
        map.loadMapAround(this);
    }
    
    
    @Override
    public boolean update() {
        if (isDestroyed()) return false;
        
        if (!active) {
            
            if (invulnerability > 0 ) invulnerability--;
                
            if (stamina<maxStamina) {
                
                stamina += staminaRegen;
                
            }
            
            if (isSwimming()) {
                
                if (stamina > 0) {
                    
                    stamina -= staminaRegen;
                    
                } else {
                    
                    hit(1, rpgcraft.graphics.spriteoperation.Sprite.Type.TILE);
                    
                }
                
            }
            
            
            if (updateCoordinates())
                recalculatePositions();
            
            if ((activeItem.hpower > 0)&&(!input.runningKeys.contains(input.attack.getKeyCode()))) {                                        
                                    
                    activeItem.poweringBar.setDeactivated();                    
                    
                    attack(activeItem.hpower / activeItem.maxPower);
                    
                    activeItem.hpower = 0;
            }
            
            
            if ((activeItem.dpower > 0)&&(!input.runningKeys.contains(input.defense.getKeyCode()))) {

                    activeItem.poweringBar.setDeactivated();                    
                    
                    stamina -= defend();
                    
                    activeItem.dpower = 0;                
            }
            
            
            }
        
        return true;
    }

    
    public void grabItem(Item item) {
        inventory.add(item);        
    }    

    @Override
    public String toString() {
        return "Player: " + super.toString();
    }
           
    
    protected int defend() {
        
        
        return 0;
    }
    
    @Override
    public void setMap(SaveMap map) {
        this.map = map;
        if (poweringBar == null) {
            poweringBar = new BarParticle();
        }
        map.addParticle(poweringBar);
    }
    
    
    @Override
    public void equip(Entity item) {
        ItemType iType = item.getItemType();
        
        if (iType.equals(ItemType.ARMOR)) {
            
            ArmorType aType = item.getArmorType();
            
            if (gear.containsKey(aType)) {
                unequip(gear.get(aType));
                gear.put(aType, item);
            }
        }
    }

    @Override
    protected boolean tryHurt(AttackedTile tile, Chunk chunk, double modifier) {        
        if ((levelType.getValue() & tile.getMaterialType()) > 0) {
            if (tile.hit(damage * modifier) <= 0) {
                chunk.destroyTile(level, tile.getX(), tile.getY());
                return true;
            }
            return true;
        }
        return false;
    }
    
    
    
    @Override
    public void use(Entity item) {
        
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out); //To change body of generated methods, choose Tools | Templates.
        out.writeInt(lightRadius);
        out.writeInt(sound);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in); //To change body of generated methods, choose Tools | Templates.
        this.lightRadius = in.readInt();
        this.sound = in.readInt();        
    }
    
    
    
    
}
