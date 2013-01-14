/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities;

import rpgcraft.entities.types.ArmorType;
import rpgcraft.entities.types.ItemType;
import rpgcraft.graphics.Colors;
import rpgcraft.graphics.inmenu.AbstractInMenu;
import rpgcraft.graphics.particles.BarParticle;
import rpgcraft.map.Map;
import rpgcraft.resource.EntityResource;
import rpgcraft.resource.StatResource.Stat;
/**
 *
 * @author Kirrie
 */
public class Player extends MovingEntity {        
    
    public Player(String name, Map map, EntityResource res) {
        super(name, map, res);
        this.poweringBar = new BarParticle();
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
            if (input.up.on) {
                doubleyGo -= fullSpeed;                
            }
            if (input.down.on) {
                doubleyGo += fullSpeed;  
            }
            if (input.left.on) {
                doublexGo -= fullSpeed; 
            }        
            if (input.right.on) {
                doublexGo += fullSpeed;  
            }    
            
            if ((input.attack.on)&&(stamina>0)) {
                if (!poweringBar.isActivated()) {
                    poweringBar.setActivated(Colors.getColor(Colors.healthColor));
                }
                if (activeItem.hpower < activeItem.maxPower) {
                    stamina-=doubleStats.get(Stat.ATKPOWER);
                    activeItem.hpower += doubleStats.get(Stat.ATKPOWER);
                    poweringBar.setValue(activeItem.hpower); 
                }
            }

            if ((input.defense.on)&&(stamina>0)) {
                if (!poweringBar.isActivated()) {
                    poweringBar.setActivated(Colors.getColor(Colors.defenseColor));                
                }
                if (activeItem.dpower < activeItem.maxPower) {
                    stamina-=doubleStats.get(Stat.DEFPOWER);
                    activeItem.dpower += doubleStats.get(Stat.DEFPOWER);
                    poweringBar.setValue(activeItem.dpower); 
                }
            }
            
            if (input.inventory.click) {  
                map.setMenu(AbstractInMenu.menuList.get("inventory"));                    
                input.freeKeys();
            }
            
            if (input.escape.on) {
                if (map.hasMenu()) {
                    
                }
            }                                    
    }     
    
    private void recalculatePositions() {               
        if (reload) {
            map.loadMapAround(this);
            reload = false;
        }       
    }
    
    @Override
    public boolean update() {
        if (isDestroyed()) return false;
        if (!disabled) {
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
            
            
            updateCoordinates();
            
            if ((activeItem.hpower > 0)&&(!input.attack.on)) {                                        
                                    
                    activeItem.poweringBar.setDeactivated();                    
                    
                    attack(activeItem.hpower / activeItem.maxPower);
                    
                    activeItem.hpower = 0;
            }
            
            
            if ((activeItem.dpower > 0)&&(!input.defense.on)) {

                    activeItem.poweringBar.setDeactivated();                    
                    
                    stamina -= defend();
                    
                    activeItem.dpower = 0;                
            }
            
            
            }
        recalculatePositions();
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
    public void setMap(Map map) {
        this.map = map;
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
    public void use(Entity item) {
        
    }
    
    
}
