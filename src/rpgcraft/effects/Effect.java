/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.effects;

import java.awt.Image;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import rpgcraft.entities.Entity;
import rpgcraft.entities.Item;
import rpgcraft.map.SaveMap;
import rpgcraft.panels.listeners.Action;
import rpgcraft.resource.EffectResource;
import rpgcraft.resource.StatResource;
import rpgcraft.utils.DataUtils;

/**
 * Trieda Effects ktora zdruzuje informacie, stav efektu v entite a metody
 * na aktualizovanie statov entity. Efekt je priradeny vzdy dvom entitam (originalnej odkial efekt pochadza 
 * a cielovej na ktoru efekt aplikujeme).<br> Typ efektu ako aj spustac efektu su zadane v enumoch EffectEvent a
 * EffectType. <br> Update funkcia vykona aktualizovanie efektu ako zvysenie trvanlivosti efektu
 * a vykonanie prislusnej operacii na cielovej entite. Pri konci efektu vrati update funkcia false
 * co entita spracvava tak ze vymaze efekt z entity. <br> Trieda implementuje Externalizable
 * aby mohol byt efekt ulozeny do savu spolu s entitou ktora ho obsahuje. Tymto padom
 * nemoze dojst k oklamaniu hry (pri kazdom zlom efekty by som ulozil a nacital save a efekt by bol prec).
 * @author doma
 */
public class Effect implements Externalizable {
    
    // <editor-fold defaultstate="collapsed" desc=" Enumy ">
    /**
     * Enum s udalostou ktory spusta efekt
     */
    public enum EffectEvent {
        ONUSE,
        ONEQUIP,
        ONSTRUCK,
        ONDROP,        
        ONSELF
    }        
    
    /**
     * Enum s typom efektov
     */
    public enum EffectType {
        HEALING,
        HEALOVER,
        LEECHEALTH,
        LEECHSTAMINA,
        KNOCKBACK,
        RECHARGING,
        RECHARGEOVER,
        PERMANENTSTATSONCE,
        PERMANENTSTATS,
        INCSTATS
        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    
    /**
     * Resource pre tento efekt
     */
    private EffectResource res;
    /**
     * Effekt udalost ktora spusta Efekt
     */
    private EffectEvent event;
    /**
     * Typ efektu co sa vykona pri update
     */
    private EffectType type;
    /**
     * Akcie ktore sa vykonaju pri update efektu
     */
    private ArrayList<Action> actions;
    /**
     * Id originalnej a destinalnej entity
     */
    private long originLong = 0, destLong = 0;
    /**
     * Originalna entita z ktorej vykonavame efekt
     */
    private Entity originEntity;
    /**
     * Entita na ktoru efekt uplatnujeme
     */
    private Entity destEntity;   
    /**
     * Maximalna a aktualna dlzka zivota
     */
    private int maxSpan, lifeSpan;
    /**
     * Ci bol efekt ukonceny
     */
    private boolean quit;
    /**
     * Ci sa akurat uz takyto efekt na entite uplatnuje.
     */
    private boolean isSelf;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    
    /**
     * Prazdny konstruktor pre vytvorenie instancie efektu. Dolezity pri Externalizacii.
     */
    public Effect() {
        
    }
    
    /**
     * Konstruktor ktory vytvori instanciu efektu z existujuceho efektu zadany ako parameter
     * <b>effect</b>. Z efektu sa skopiruju vlastnosti ako resource z ktoreho je efekt vytvoreny,
     * event, typ, maxSpan. Parametre origin a dest sluzia k nastavenie originalnej a cielovej entity
     * pre efekt. 
     * @param effect Efekt ktory kopirujeme
     * @param origin Originalna entita ktora vytvorila efekt
     * @param dest Cielova entita na ktoru bude efekt posobit.
     */
    public Effect(Effect effect, Entity origin, Entity dest) {
        this.res = effect.res;
        this.event = effect.event;
        this.type = effect.type;
        this.maxSpan = effect.maxSpan;
        this.originEntity = origin;
        this.destEntity = dest;
        if (effect.actions != null) {
            for (Action action : effect.actions) {
                Action toAdd = new Action(action);                
                actions.add(toAdd);
            }
        }
    }
    
    /**
     * Konstruktor pre efekt ktory sa vytvori podla resource zadany parametrom <b>res</b>.
     * Z resource sa skopiruje typ, lifeSpan a akcie na vykonanie.
     * EffectEvent sluzi ako upresnenie kedy sa efekt prida k cielovej entite (ONUSE, ONEQUIP, ...)
     * @param res Resource z ktoreho vytvarame efekt
     * @param event Event podla ktoreho rozhodujeme kedy sa prida k cielovej entite.
     * @see EffectEvent
     */
    public Effect(EffectResource res, EffectEvent event) {
        this.res = res;
        this.event = event;  
        this.type = res.getType();
        this.maxSpan = res.getLifeSpan();
        if (res != null && res.getActions() != null) {
            for (Action action : res.getActions()) {
                Action toAdd = new Action(action);                
                actions.add(toAdd);
            }
        }
        
    }
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    
    /**
     * Metoda nastavi originalnu entitu od ktorej efekt posobi
     * @param e Entita ktora bude originalnou entitou pre efekt
     */
    public void setOriginEntity(Entity e) {
        this.originEntity = e;
    }
    
    /**
     * Metoda nastavi cielovu entitu na ktoru efekt posobi.
     * @param e Entita ktora bude cielovou entitou pre efekt
     */
    public void setDestEntity(Entity e) {
        this.destEntity = e;
    }
    
    /**
     * Metoda ktora nastavi originalnu a cielovu entitu na ktore efekt posobi.
     * @param orig Originalna entita ktora vytvorila efekt
     * @param dest Cielova entita na ktorej je efekt vykonany.
     */
    public void setEntities(Entity orig, Entity dest) {
        this.originEntity = orig;
        this.destEntity = dest;
    }
        
    /**
     * Metoda ktora nastavi isSelf priznak na true => efekt je v entite v liste aktivnych efektov
     * ktore sa vykonavaju.
     */
    public void setSelf() {
        this.isSelf = true;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    
    /**
     * Metoda ktora vrati effect event pre tento efekt.
     * @return EffectEvent tohoto efektu
     */
    public EffectEvent getEvent() {
        return event;
    }        
        
    /**
     * Metoda ktora vrati typ efektu zo Stringu zadaneho ako parameter.
     * @param s Stringova hodnota pre navratovy EffectType
     * @return EffectType zodpovedajuci parametru
     */
    public EffectType getType(String s) {
        return EffectType.valueOf(s);
    }
            
    /**
     * Metoda ktora vrati meno efektu.
     * @return Meno efektu.
     */
    public String getName() {
        return res.getName();
    }
    
    /**
     * Metoda ktora maximalny pocet sekund kolko je efekt aktivny
     * @return Maximalny pocet sekund
     */
    public int getMaxSpan() {
        return maxSpan;
    }
    
    /**
     * Metoda ktora vrati kolko sekund je efekt aktivny.
     * @return Pocet sekund aktivnosti
     */
    public int getLifeSpan() {
        return lifeSpan;
    }
    
    /**
     * Metoda ktora vrati ci je efekt v hashmape u entity v ONSELF liste bloku.
     * @return True/false ci sa efekt vykonava .
     */
    public boolean isSelf() {
        return isSelf;
    }
    
    /**
     * Metoda ktora vrati obrazok efektu z EffectResource.
     * @return Obrazok efektu
     */
    public Image getEffectImage() {
        return res.getImage();
    }
    
    /**
     * Vrati univerzalne id pre cielovu entitu. Lepsie ako vratit entitu ako celok kedze
     * pri ukladani efektov sa referencia zmeni
     * @return Id cielovej entity
     */
    public long getDestUniId() {
        return destEntity.getUniId();
    }
    
    /**
     * Vrati univerzalne id pre originalnu entitu. Lepsie ako vratit entitu ako celok kedze
     * pri ukladani efektov sa referencia zmeni.
     * @return Id originalnej entity.
     */
    public long getOriginUniId() {
        return originEntity.getUniId();
    }
    
    
    // </editor-fold>            
    
    // <editor-fold defaultstate="collapsed" desc=" Update ">
    /**
     * Metoda ktora zresetuje efekt do zaciatocneho stavu s nastavenim lifeSpan na 0
     * a priznaku quit na false => pri dalsom update sa vykona update normalne.
     */
    public void reset() {
        this.lifeSpan = 0;
        this.quit = false;
    }
    
    /**
     * Metoda ktora nastavi ukoncovaci priznak quit na true => pri dalsom update
     * skonci metoda false a efekt sa tym vymaze.
     */
    public void quit() {
        this.quit = true;
    }
    
    /**
     * Metoda ktora reinicializuje entity podla univerzalnych id.
     * Pri nacitavani efektu zo savu musime brat do uvahy aj univerzalne id predmet/entity
     * ktory efekt spracovava. Z destEntity si ziskame mapu a najdeme entitu s tymto univerzalnym id,
     * ked sme taku nenasli, tak skontrolujeme este inventar.
     */
    public void reinitEntities(SaveMap map) {
        destEntity = map.getEntity(destLong);        
        originEntity = map.getEntity(originLong);
        if (originEntity == null) {
            for (Item item : destEntity.getInventory()) {
                if (item.getUniId() == originLong) {
                    originEntity = item;
                }
            }
        }
    }
    
    /**
     * Metoda ktora reinicializuje efekt z resource ktory je zadany v efekte. Z resource
     * si vyberie typ efektu maximalny span a vytvori list s akciami na splnenie.
     */
    public void reinitialize() {
        if (res != null) {
            if (res.getActions() != null) {
                for (Action action : res.getActions()) {
                    Action toAdd = new Action(action);                
                    actions.add(toAdd);
                }
            }
            this.type = res.getType();
            this.maxSpan = res.getLifeSpan();
        }
    }
    
    /**
     * Metoda ktora spravne ukonci efekt nastavenim zivota na 0, 
     * priznakom isSelf na false a skontrolovanim typu a vykonanim prislusnej operacie.
     * Jedina operacia ktora dava zmysel je kontrola INCSTATS pri ktorej musime 
     * zmensit staty cielovej entity.
     */    
    public void exit() {
        lifeSpan = 0;
        isSelf = false;
        switch (type) {
            case INCSTATS : {
                destEntity.decStats(originEntity);
            } break;
            default : break;
        }
    }
    
    /**
     * Metoda ktora aktualizuje efekt. Ked bol efekt ukonceny, skoncila jeho doba
     * ucinku, alebo originalna/cielova entita je null tak vrati false cim sa efekt vymaze z aktivnych efektov.
     * Ked sa update neukoncila tak pokracuje kontrolovanim typu efektu a pridanim 
     * doby ucinku. Nakonci vratime true ze sa podarilo o update.
     * Pri nacitavani efektu zo savu musime brat do uvahy aj univerzalne id predmet/entity
     * ktory efekt spracovava. Z destEntity si ziskame mapu a najdeme entitu s tymto univerzalnym id,
     * ked sme taku nenasli, tak skontrolujeme este inventar. Po nenajdeni ukoncime efekt a pri najdeni
     * vykoname efekt podla normalnych podmienok => pokracujeme efekt z posledneho savu.
     * @return True/False ci sa podaril update.
     */    
    public boolean update() {
        if (quit || lifeSpan >= maxSpan) {
            exit();
            return false;            
        }
        
        if (originEntity == null ||  destEntity == null) {
            exit();
            return false;
        }                               
                        
        switch (type) {
            case HEALING : {
                destEntity.regenHealth(originEntity.getDouble(StatResource.Stat.HEALTHBONUS, 0));
            } break;
            case RECHARGING : {
                destEntity.regenStamina(originEntity.getDouble(StatResource.Stat.HEALTHBONUS, 0));
            } break;
            case HEALOVER : {
                destEntity.incHealth(originEntity.getDouble(StatResource.Stat.HEALTHBONUS, 0));
            } break;
            case RECHARGEOVER : {
                destEntity.incStamina(originEntity.getDouble(StatResource.Stat.HEALTHBONUS, 0));
            } break;            
            case INCSTATS : {
                destEntity.incStats(originEntity);
            } break;                
            case LEECHEALTH : {
                double health = originEntity.getDouble(StatResource.Stat.HEALTHBONUS, 0);
                destEntity.incHealth(-health);
                originEntity.regenHealth(health);
            } break;
            case LEECHSTAMINA : {
                double stamina = originEntity.getDouble(StatResource.Stat.STAMINABONUS, 0);
                destEntity.incHealth(-stamina);
                originEntity.regenHealth(stamina);
            } break;
            case KNOCKBACK : {
                destEntity.knockback(originEntity.getType());
            } break; 
            case PERMANENTSTATSONCE : {
                destEntity.incStats(originEntity);
                exit();
                return false;
            }
            case PERMANENTSTATS : {
                destEntity.incStats(originEntity);                                
            } break;                
            default : break;
        }
        
        if (actions != null) {
            for (Action action : actions) {
                DataUtils.execute(action);
            }
        }
        
        lifeSpan++;
        return true;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Externalizacne metody ">
    /**
     * Ukladanie efektu pri ulozeni savu. Uklada sa stav efektu ako quit premenna,
     * lifeSpan, id EffectResourcu, typ eventu a univerzalne id originalnej entity.
     * @param out Output Stream kam ukladame efekt.
     * @throws IOException 
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(quit);
        out.writeInt(lifeSpan);
        out.writeUTF(res.getId());
        out.writeObject(event);
        out.writeLong(originEntity.getUniId());
        out.writeLong(destEntity.getUniId());
    }

    /**
     * Nacitavanie efektu pri nacitani savu. Nacitava sa stav efektu ako quit premenna,
     * lifeSpan, id EffectResourcu, typ eventu a univerzalne id originalnej entity.
     * Nasledne zavolame reinitialize metodu ktora dosadi ostatne premenne.
     * @param in Input Stream odkial nacitavame efekt.
     * @throws IOException 
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.quit = in.readBoolean();
        this.lifeSpan = in.readInt();
        this.res = EffectResource.getResource(in.readUTF());
        this.event = (EffectEvent) in.readObject();
        this.originLong = in.readLong();  
        this.destLong = in.readLong();
        reinitialize();
    }
    // </editor-fold>
             
}
