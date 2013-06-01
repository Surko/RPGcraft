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
import rpgcraft.MainGameFrame;
import rpgcraft.effects.Effect;
import rpgcraft.entities.Armor.ArmorType;
import rpgcraft.entities.Item.ItemType;
import rpgcraft.entities.types.ItemLevelType;
import rpgcraft.graphics.Colors;
import rpgcraft.graphics.spriteoperation.Sprite;
import rpgcraft.graphics.ui.particles.BarParticle;
import rpgcraft.map.SaveMap;
import rpgcraft.map.chunks.Chunk;
import rpgcraft.map.tiles.AttackedTile;
import rpgcraft.panels.listeners.Action;
import rpgcraft.panels.listeners.ActionEvent;
import rpgcraft.quests.Quest;
import rpgcraft.resource.EffectResource;
import rpgcraft.resource.EntityResource;
import rpgcraft.resource.QuestsResource;
import rpgcraft.resource.StatResource.Stat;
import rpgcraft.utils.DataUtils;
import rpgcraft.utils.Pair;
/**
 * Trieda Player ktora dedi od MovingEntity je kontajner pre hraca a metody
 * ktore sa s hracom daju robit. Oproti MovingEntity ma pridane Questy a podla hraca sa taktiez 
 * nacitava okolie mapy. Hrac je na mape vacsinou len jeden.
 * @author Kirrie
 */
public class Player extends MovingEntity {     
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    private static final long serialVersionUID = 912804676578087866L;
    
    private double acumTilePower;
    private HashMap<String,Quest> activeQuests;
    private HashMap<String,Quest> completedQuests;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Prazdny konstruktor pre vytvorenie instancie Externalizaciou.
     */
    public Player() {
        //System.out.println("Player constructor called");
    }
    
    /**
     * Konstruktor na vytvorenie hraca z pohyblivej entity.
     * @param entity Entita z ktorej vytvarame hraca.
     */
    public Player(MovingEntity entity) {
        this.name = entity.name;
        this.map = entity.map;
        this.res = entity.res;
        
        this.id = res == null ? "" : res.getId();   
        this.movingEntitySprites = entity.movingEntitySprites;
        this.poweringBar = new BarParticle();
        this.activeQuests = new HashMap();
        this.completedQuests = new HashMap();
        this.doubleStats = entity.doubleStats;
        this.intStats = entity.intStats;
        this.activeItem = entity.activeItem;
        this.activeEffects = entity.activeEffects;
        this.group = entity.group;
        this.concentration = entity.concentration;
        this.attackable = entity.attackable;
        this.pushable = entity.pushable; 
        this.fullSpeed = doubleStats.get(Stat.DBLFACTOR) + (new Double(intStats.get(Stat.SPEED))/(new Double(intStats.get(Stat.SPDRATER))));
        this.spriteType = Sprite.Type.DOWN;
        this.maxStamina = doubleStats.get(Stat.STAMINAMAX);
        this.stamina = maxStamina;
        this.maxHealth = doubleStats.get(Stat.HEALTHMAX);
        this.staminaRegen = doubleStats.get(Stat.STAMINAREGEN);
        this.staminaDischarger = maxStamina;
        this.attackRadius = intStats.get(Stat.ATKRADIUS);        
        this.health = maxHealth;  
        map.addParticle(poweringBar);
        
        this.xPix = entity.xPix;
        this.yPix = entity.yPix;
        this.actualChunk = entity.actualChunk;
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
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Inicializacie ">
    /**
     * Metoda ktora inicializuje hraca. Metoda vola super metodu ktora nastavi 
     * staty spolocne pre moving entity a player. V tejto podobe sa nastavuje iba fullSpeed.
     */
    @Override
    public void initialize() {
        super.initialize();          
        this.fullSpeed  = 2d;  
    }
                      
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora vrati quest z aktivnych questov s id rovnym parametru <b>id</b>.
     * @param id Id questu ktory vratime
     * @return Quest s danym id.
     */
    public Quest getQuest(String id) {
        return activeQuests.get(id);        
    }
    
    /**
     * Metoda ktora vrati list s aktivnymi questami.
     * @return Aktivne questy.
     */
    public ArrayList<Quest> getActiveQuests() {
        return new ArrayList(activeQuests.values());
    }
    
    /**
     * Metoda vrati Stringovu hodnotu o hracovi.
     * @return Text s info o hracovi
     */
    @Override
    public String toString() {
        return "Player: " + super.toString();
    }
                   
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    /**
     * Metoda ktora nastavi mapu v ktorej sa hrac nachadza na tu zadanu parametrom <b>map</b>.
     * Do mapy sa prida aj playerov poweringBar ktory ukazuje nahromadenu silu (utocnu/ defenzivnu/ kuzelnu?)
     * @param map Mapa v ktorej sa hrac nachadza.
     */
    @Override
    public void setMap(SaveMap map) {
        this.map = map;
        if (poweringBar == null) {
            poweringBar = new BarParticle();
        }
        map.addParticle(poweringBar);
    }
        
    /**
     * Metoda ktora nastavi aktualny chunk v ktorom sa player nachadza.
     * Zaroven nastavi premennu reloadChunks na true, co donuti aktualizovanie chunkov okolo
     * playera.
     * @param chunk Chunk v ktorom je player.
     */
    @Override
    public void setChunk(Chunk chunk) {
        super.setChunk(chunk);
        reloadChunks = true;
    }    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" Update ">
    
    /**
     * Override metoda ktora aktualizuje playera. Pri aktualizovani prebiehame aktivne effekty a na kazdy
     * z nich volame metodu update (pri neaktualizovani vymazavame efekt). Vacsina metody
     * je ale zlozena z obsluhy utocenia a obrany. Po aktualizovani utocenia je narade
     * aktualizovanie pozicii (ci sa player pohol) a aktualizovanie questov.
     * @return True/False ci sa podaril update.
     */
    @Override
    public boolean update() {
        if (isDestroyed()) {            
            return false;            
        }
        
        if (!active) {                        

            if (stamina<maxStamina) {                
                stamina += staminaRegen;                
            }

            if (health<maxHealth) {
                health += healthRegen;
            }
                        
            /*
            if (isSwimming()) {                
                if (stamina > 0) {                    
                    stamina -= staminaRegen;                    
                } else {                    
                    hit(1, rpgcraft.graphics.spriteoperation.Sprite.Type.TILE);                    
                }
                
            }*/
            
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
                attackTile(acumTilePower / maxPower, 0, -1);                      
                acumTilePower = 0d;
            }                                   
            
            if (updateCoordinates()) {
                recalculatePositions();
            }
            
            updateQuests();
                        
        }
        
        return true;
    }
        
    /**
     * Metoda ktora aktualizuje questy => zavola pre kazdy aktivny quest metodu update.
     * Ked bol quest dokonceny tak ho premiestni do kompletnych questov.
     */
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
    
    /**
     * Metoda ktora skomplletizuje quest => odstrani z aktivnych a prida
     * ku skompletizovanym questom.
     * @param sQuest Quest ktory ukoncujeme
     * @return True/False ci sa podarilo ukoncit quest.
     */
    public boolean completeQuest(String sQuest) {
        Quest quest = activeQuests.get(sQuest);
        if (quest != null) {
            completedQuests.put(sQuest, quest);
            activeQuests.remove(sQuest);
            return true;
        }
        
        
        return false;
    }
    
    /**
     * Metoda ktora vymaze aktivny quest s menom zadanym parametrom <b>sQuest</b>. 
     * Quest nie je presuvany do kompletnych questo => nemame ziadne info ze quest
     * existoval.
     * @param sQuest Quest ktory vymazavame od entity.
     * @return True/False ci sa podarilo vymazat quest.
     */
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
    
    /**
     * Metoda ktora prida quest s menom ktore je zadane ako parameter <b>sQuest</b>
     * Quest na pridanie vytvarame konstruktorom s parametrom string ktory ho vyberie 
     * z QuestResource.
     * @param sQuest Meno questu na pridanie.
     */
    public void addQuest(String sQuest) {        
        Quest quest = new Quest(sQuest);        
        activeQuests.put(sQuest,quest);        
    }
    
    /**
     * Metoda ktora prida novu ulohu zadanu parametrom <b>quest</b>
     * @param quest Quest ktory pridavame ku questom.
     */
    public void addQuest(Quest quest) {
        activeQuests.put(quest.getId(), quest);
    }
    
    /**
     * Metoda ktora prida novu ulohu k uloham. Ulohu ziska z parametru <b>res</b>
     * co je QuestResource => volame konstruktor na vytvorenie instancie questu a
     * pridame k aktivnym questom.
     * @param res QuestResource z ktoreho vytvarame quest.
     */
    public void addQuest(QuestsResource res) {
        Quest quest = new Quest(res);        
        activeQuests.put(res.getId(),quest);
    }
        
    /**
     * Metoda ktora vykona akciu use na predmete zadanom parametrom <b>item</b>
     * @param item Predmet ktory chceme pouzit
     */
    @Override
    public void use(Item item) {
        super.use(item);
    }

    /**
     * Metoda ktora vykonan akciu equip na predmete zadanom parametrom <b>item</b>      
     * @param item Premdet ktory chceme obliect.
     */
    @Override
    public void equip(Item item) {
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

    /**
     * Metoda ktora zoberie predmet zadany parametrom <b>item</b>
     * @param item Predmet ktory zberame.
     */
    public void grabItem(Item item) {
        inventory.add(item);        
    }    
    
    /**
     * Metoda ktora vykona obrannu funkciu a vrati silu nanu vynalozenu.
     * @return Silu vynalozenu na obranu
     */
    protected int defend() {                
        return 0;
    }
    
    /**
     * Metoda ktora sa pokusi o chytenie predmetov nachadzajucich sa pod hracom (na rovnakej dlazdici)
     * Ked sa tam nachadzaju tak ich odstranime z mapy a pridame do inventara.
     */
    public void tryGrabItems() {
        for (Entity e : map.getNearEntities()) {
            if (e instanceof Item && e.getLevel() == this.level
                    && e.getX()== this.getX() && e.getY() == this.getY()) {
                this.addItem((Item)e);
                map.removeEntity(e);
                return;
            }
        }
    }
    
    /**
     * Metoda ktora sa snazi o "ublizenie" dlazdici zadanej parametrom <b>tile</b> (dlazdica je typu AttackedTile).
     * Najprv sa kontroluje typ dlazdice ci ho je mozne znicit typom predmetu aky drzime.
     * Nasledne utocime na dlazdicu metodou hit kde sila utoku je damage * parameter <b>modifier</b>.
     * @param tile Dlazdica ktoru nicime
     * @param chunk Chunk na ktorom sme
     * @param modifier Modifikator utoku akym utocime na dlazdicu.
     * @return True/False ci sme zautocili na dlazdicu.
     */
    @Override
    protected double tryHurt(AttackedTile tile, Chunk chunk, double modifier) {         
        if ((levelType.getValue() & tile.getDurability()) > 0) {
            double tileDmg = damage * modifier;
            if (tile.hit(tileDmg) <= 0) {
                addItem(new TileItem(null, tile));
                targetedTile = null;
                if (tile.getX() == getTileX() && tile.getY() == getTileY() &&
                        tile.getLevel() == level - 1) {
                    level--;
                    map.setLevel(level);
                }
            }            
            return tileDmg;
        }
        return 0d;
    }
    
    /**
     * Metoda ktora sa pokusi o posadenie hraca na mapu a aktulizovanie okolia hraca.
     */
    public void trySpawn() {
        this.xPix = 0;
        this.yPix = 0;
        this.level = 64;
        this.actualChunk = map.chunkPixExist(xPix, yPix);        
        /*
        this.x = xPix << 5;
        this.y = yPix << 5;        
        this.position = new EntityPosition(x, y);
        */
        map.addEntity(this);
    }    
    
    /**
     * Metoda ktora rekalkuluje pozicie/chunky. Ked je premenna reloadChunks rovna true
     * tak sme donuteny zavolat nacitanie mapy okolo hraca.
     */
    private void recalculatePositions() {               
        if (reloadChunks) {
            map.loadMapAround(this);
            reloadChunks = false;
        }       
    }
    
    /**
     * Metora ktora sa pokusi o aktivovanie entity nejakym sposobom. Entita ktoru aktivujeme
     * je na poziciach kde sme otoceny. Na aktivovanie entity volame metodu activateEntities.
     */
    public void activate() {
        int radius = attackRadius;
        
        if (spriteType == Sprite.Type.UP) {
            activateEntity(xPix - SEGMENTLENGTH , yPix, xPix + SEGMENTLENGTH, yPix-radius);                
            return;                    
        }
        if (spriteType == Sprite.Type.DOWN) {
            activateEntity(xPix - SEGMENTLENGTH, yPix + radius, xPix + SEGMENTLENGTH, yPix);                
            return;
        }
        if (spriteType == Sprite.Type.RIGHT) {
            activateEntity(xPix, yPix + SEGMENTLENGTH, xPix + radius, yPix - SEGMENTLENGTH);                
            return;
        }
        if (spriteType == Sprite.Type.LEFT) {
            activateEntity(xPix-radius, yPix + SEGMENTLENGTH, xPix, yPix - SEGMENTLENGTH);  
            return;
        }
    }
    
    /**
     * Metoda ktora dostava 4 prve parametre ako vymedzenie priestoru kde chceme aktivovat entitu.
     * Ked sa v tomto priestore nachadzaju nejake entity tak zavola metodu activate 
     * s parametrom entity ktora zavola action listenery definovane v entity resource.
     * @param x0 lava suradnica vymedzeneho priestoru
     * @param y0 dolna suradnica vymedzeneho priestoru
     * @param x1 prava suradnica vymedzeneho priestoru
     * @param y1 horna suradnica vymedzeneho priestoru             
     */    
    public void activateEntity(int x0, int y0, int x1, int y1) {
        double entInteracted = 0d;        
        for (Entity e : map.getNearEntities()) {
            if (e != this && e.getLevel()==level) {
                if ((e.xPix >= x0)&&(e.xPix <= x1)&&
                        (e.yPix <= y0)&&(e.yPix >= y1)) {
                    activate(e);
                    return;
                }               
            }
        }                                
    }
    
    /**
     * Metoda ktora aktivuje entitu zadanu parametrom <b>e</b>. K Aktivacii dochadza tak ze 
     * vyberieme z entity aktivacne akcie a postupne ich zavolame => aktivovanie entity
     * moze nieco iteragovat so svetom ale napriklad moze spustit aj nejaky panel (ako napriklad konverzacia).
     * @param e Entita ktoru aktivujeme.
     */
    @Override
    public void activate(Entity e) {
        ArrayList<Action> activationActions = e.res.getActivateActions();
        if (activationActions != null) {
            ActionEvent event = new ActionEvent(map.getMenu(), 0, -1, null, e);
            for (Action action : activationActions) {
                Action _action = new Action(action);
                _action.setActionEvent(event);
                DataUtils.execute(_action);
            }
        }
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
            
            if ((input.clickedKeys.contains(input.active.getKeyCode()))) {
                activate();
            }
            
            if ((input.runningKeys.contains(input.attack.getKeyCode())) && stamina>0) {
                if (!poweringBar.isActivated()) {
                    poweringBar.setActivated(Colors.getColor(Colors.healthColor));
                }
                if (acumPower < maxPower) {
                    stamina-= attackPower;
                    acumPower += attackPower;
                    poweringBar.setValue(acumPower); 
                    return;
                }
            }
            
            if ((input.runningKeys.contains(input.tileattack.getKeyCode())) && stamina>0) {
                if (!poweringBar.isActivated()) {
                    poweringBar.setActivated(Colors.getColor(Colors.tileAttackColor));
                }
                if (acumTilePower < maxPower) {                    
                    stamina-= attackPower;
                    acumTilePower += attackPower;
                    poweringBar.setValue(acumTilePower);
                    return;
                }
            }

            if (input.runningKeys.contains(input.defense.getKeyCode())&&(stamina>0)) {
                if (!poweringBar.isActivated()) {
                    poweringBar.setActivated(Colors.getColor(Colors.defenseColor));                
                }
                if (acumDefense < maxPower) {
                    stamina-= defensePower;
                    acumPower += defensePower;
                    poweringBar.setValue(acumDefense); 
                    return;
                }
            } 
            
            if (input.clickedKeys.contains(input.enter.getKeyCode())) {                
                tryGrabItems();
            }
                        
            if (input.clickedKeys.contains(input.jump.getKeyCode())) {                                                
                if (autoMove(1)) {
                    map.setLevel(level);
                }
            } 
    }          
      
    
    // </editor-fold>        
    
    // <editor-fold defaultstate="collapsed" desc=" Externalizacne metody ">
    /**
     * Externalizacna metoda writeExternal. Metoda je volana pri ukladani playera do savu.
     * Volana je super metoda z MovingEntity ktora ulozi spolocne udaje pre moving entity a player.
     * Ostatok udajov dolezite len pre playera su ulozene priamo tu.
     * @param out Output file kam ukladame data.
     * @throws IOException Ked doslo k chybe v streame.
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out); //To change body of generated methods, choose Tools | Templates.
        out.writeInt(lightRadius);
        out.writeInt(sound);
        out.writeObject(activeQuests);        
    }

    /**
     * Externalizacna metoda readExternal. Metoda je volana pri nacitavani playera zo savu.
     * Volana je super metoda z MovingEntity ktora nacita spolocne udaje pre moving entity a player.
     * Ostatok udajov dolezite len pre playera su nacitane priamo tu. Udaje su rovnake a v rovnakom
     * poradi ako pri writeExternal.
     * @param out Output file odkial nacitavame data.
     * @throws IOException Ked doslo k chybe v streame.
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in); //To change body of generated methods, choose Tools | Templates.
        this.lightRadius = in.readInt();
        this.sound = in.readInt();
        this.activeQuests = (HashMap<String, Quest>) in.readObject();        
    }
    
    // </editor-fold>
    
}
