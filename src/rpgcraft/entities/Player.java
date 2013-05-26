/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import rpgcraft.entities.Armor.ArmorType;
import rpgcraft.entities.Item.ItemType;
import rpgcraft.entities.types.ItemLevelType;
import rpgcraft.graphics.Colors;
import rpgcraft.graphics.ui.particles.BarParticle;
import rpgcraft.map.SaveMap;
import rpgcraft.map.chunks.Chunk;
import rpgcraft.map.tiles.AttackedTile;
import rpgcraft.quests.Quest;
import rpgcraft.resource.EntityResource;
import rpgcraft.resource.QuestsResource;
import rpgcraft.resource.StatResource.Stat;
/**
 *
 * @author Kirrie
 */
public class Player extends MovingEntity {        
    private static final long serialVersionUID = 912804676578087866L;
    
    private double acumTilePower;
    private HashMap<String,Quest> activeQuests;
    private HashMap<String,Quest> completedQuests;
    
    /**
     * Prazdny konstruktor pre vytvorenie instancie Externalizaciou.
     */
    public Player() {
        //System.out.println("Player constructor called");
    }
    
    public Player(String name, SaveMap map, EntityResource res) {
        super(name, map, res);
        //System.out.println("Player constructor called");
        this.poweringBar = new BarParticle();
        this.activeQuests = new HashMap();
        this.completedQuests = new HashMap();
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
                if (acumPower < maxPower) {
                    stamina-=doubleStats.get(Stat.ATKPOWER);
                    acumPower += doubleStats.get(Stat.ATKPOWER);
                    poweringBar.setValue(acumPower); 
                    return;
                }
            }
            
            if ((input.runningKeys.contains(input.tileattack.getKeyCode())) && stamina>0) {
                if (!poweringBar.isActivated()) {
                    poweringBar.setActivated(Colors.getColor(Colors.tileAttackColor));
                }
                if (acumTilePower < maxPower) {                    
                    stamina-=doubleStats.get(Stat.ATKPOWER);
                    acumTilePower += doubleStats.get(Stat.ATKPOWER);
                    poweringBar.setValue(acumTilePower);
                    return;
                }
            }

            if (input.runningKeys.contains(input.defense.getKeyCode())&&(stamina>0)) {
                if (!poweringBar.isActivated()) {
                    poweringBar.setActivated(Colors.getColor(Colors.defenseColor));                
                }
                if (acumDefense < maxPower) {
                    stamina-=doubleStats.get(Stat.DEFPOWER);
                    acumDefense += doubleStats.get(Stat.DEFPOWER);
                    poweringBar.setValue(acumDefense); 
                    return;
                }
            }                                                          
    }          
    
    private void recalculatePositions() {               
        if (reloadChunks) {
            map.loadMapAround(this);
            reloadChunks = false;
        }       
    }
    
    @Override
    public void setChunk(Chunk chunk) {
        super.setChunk(chunk);
        reloadChunks = true;
    }
    
    
    @Override
    public boolean update() {
        if (isDestroyed()) {
            return false;
        }
        
        if (!active) {
            
            if (invulnerability > 0 ) {
                invulnerability--;
            }
                
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
            
            if ((acumPower > 0)&&(!input.runningKeys.contains(input.attack.getKeyCode()))) {                                                                            
                    poweringBar.setDeactivated();                                        
                    attack(acumPower / maxPower);                    
                    acumPower = 0;
            }
            
            
            if ((acumDefense > 0)&&(!input.runningKeys.contains(input.defense.getKeyCode()))) {
                poweringBar.setDeactivated();                                        
                stamina -= defend();                    
                acumDefense = 0;                
            }
            
            if ((acumTilePower > 0)&&(!input.runningKeys.contains(input.tileattack.getKeyCode()))) {                
                poweringBar.setDeactivated();                                        
                attackTile(acumTilePower / maxPower);                      
                acumTilePower = 0d;
            }
            
            if (input.clickedKeys.contains(input.jump.getKeyCode())) {                
                this.level++;
                map.setLevel(level);
                autoMove();
            }
            
            if (updateCoordinates())
                recalculatePositions();
            
            updateQuests();
            
            
            }
        
        return true;
    }
        
    private void updateQuests() {
        if (activeQuests != null) {
            Iterator<Quest> iter = activeQuests.values().iterator();
            while (iter.hasNext()) {
                Quest quest = iter.next();
                quest.update();
                if (quest.isCompleted()) {
                    iter.remove();
                    completedQuests.put(quest.getId(),quest);
                }
            }
        }
    }
    
    public ArrayList<Quest> getActiveQuests() {
        return new ArrayList(activeQuests.values());
    }
    
    /**
     * Metoda ktora vrati quest z aktivnych questov s id rovnym parametru <b>id</b>.
     * @param id Id questu ktory vratime
     * @return Quest s danym id.
     */
    public Quest getQuest(String id) {
        return activeQuests.get(id);        
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
        if (!(item instanceof Item)) {
            return;
        }
        Item _item = (Item)item;
        ItemType iType = _item.getItemType();
        
        switch (iType) {
            case ARMOR : {
                Armor _armor = (Armor)_item;
                ArmorType aType = _armor.getArmorType();
                if (gear.containsKey(aType)) {
                    unequip(gear.get(aType));                    
                }
                gear.put(aType, _armor);
            } break;
            default : break;
        }                                                                       
    }

    @Override
    protected boolean tryHurt(AttackedTile tile, Chunk chunk, double modifier) {         
        if ((levelType.getValue() & tile.getDurability()) > 0) {
            if (tile.hit(damage * modifier) <= 0) {
                addItem(new TileItem(null, tile));
            }            
            return true;
        }
        return false;
    }
    
    public boolean completeQuest(String sQuest) {
        Quest quest = activeQuests.get(sQuest);
        if (quest != null) {
            completedQuests.put(sQuest, quest);
            activeQuests.remove(sQuest);
            return true;
        }
        
        
        return false;
    }
    
    public boolean removeQuest(String sQuest) {
        Iterator<Quest> iter = activeQuests.values().iterator();
        while (iter.hasNext()) {
            Quest quest = iter.next();
            if (quest.getId().equals(sQuest)) {
                iter.remove();
                return true;
            }
        }
        return false;
    }
    
    public void addQuest(String sQuest) {        
        Quest quest = new Quest(sQuest);        
        activeQuests.put(sQuest,quest);        
    }
    
    public void addQuest(Quest quest) {
        activeQuests.put(quest.getId(), quest);
    }
    
    public void addQuest(QuestsResource res) {
        Quest quest = new Quest(res);        
        activeQuests.put(res.getId(),quest);
    }
    
    
    @Override
    public void use(Entity item) {
        
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out); //To change body of generated methods, choose Tools | Templates.
        out.writeInt(lightRadius);
        out.writeInt(sound);
        out.writeObject(activeQuests);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in); //To change body of generated methods, choose Tools | Templates.
        this.lightRadius = in.readInt();
        this.sound = in.readInt();
        this.activeQuests = (HashMap<String, Quest>) in.readObject();
    }            
    
}
