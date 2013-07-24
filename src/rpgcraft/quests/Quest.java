/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.quests;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import rpgcraft.entities.Player;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.panels.listeners.Action;
import rpgcraft.panels.listeners.ActionEvent;
import rpgcraft.resource.AbstractResource.ActionType;
import rpgcraft.resource.QuestsResource;
import rpgcraft.utils.DataUtils;

/**
 * Trieda ktora v sebe obsahuje zakladne info a metody na spracovavanie Questov/uloh.
 * Quest je vytvoreny pomocou konstruktoru z QuestResource alebo id-cka tohoto resource.
 * Pri kazdom aktualizovani hraca ktory je s questom zviazany dochadza aj k aktualizacii uloh
 * definovane v hracovi. Pri prvom aktualizovani sa vykonaju akcie ktore su typu START,
 * po vykonani uz prechadzame iba akcie typu THROUGHT. Zmena stavu questu vyvola vykonanie
 * akcii typu END a nastavanie novych akcii podla stavu. Trieda je externalizable
 * takze musi mat metody writeExternal a readExternal ktore umoznuju ukladat ulohy
 * do savu.
 */
public class Quest implements Externalizable {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    /**
     * Resource z ktoreho je vytvorena uloha
     */
    private QuestsResource questRes;    
    /**
     * Stav ulohy
     */
    private int questState;
    /**
     * List s akciami na vykonanie pri update ulohy
     */
    private volatile ArrayList<Action> actions;
    /**
     * Hrac ktory plni ulohy
     */
    private Player player;
    /**
     * Ci je uloha skompletizovana
     */
    private boolean completed;
    /**
     * Premenna ci uz uloha vykonala zacinajuce akcie
     */
    private boolean started;
    /**
     * ActionEvent volany s questom
     */
    private ActionEvent questEvent = new ActionEvent(AbstractMenu.getMenuByName("gameMenu"), 0, 0, null, null);
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Dolezity konstruktor pri externalizacii na vytvorenie holej instancie questu.
     */
    public Quest() {}
    
    /**
     * Konstruktor ktory vytvori novu instanciu questu z QuestResource zadany v 
     * v parametri <b>res</b>. Zakladne nastavi stav na 0 a do listu actions
     * prida akcie pre takyto stav     
     * @param res QuestResource z ktoreho vytvarame ulohu
     */
    public Quest(QuestsResource res) {
        this.questRes = res;
        this.questState = 0;        
        this.completed = false;
        this.actions = new ArrayList();               
        
        if (res != null) {
            for (Action action : res.getStateActions(questState)) {
                Action toAdd = new Action(action);
                toAdd.setActionEvent(questEvent);
                actions.add(toAdd);
            }
        }                        
    }
    
    /**
     * Konstruktor ktory vytvori novu instanciu questu z id QuestResourcu zadany v 
     * v parametri <b>id</b>. QuestResource ziskame pomocou metody getQuest.
     * Zakladne nastavi stav na 0 a do listu actions
     * prida akcie pre takyto stav     
     * @param id Id QuestResourcu z ktoreho vytvarame ulohu
     */
    public Quest(String id) {
        this.questRes = QuestsResource.getQuest(id);
        this.questState = 0;
        this.actions = new ArrayList();         
        
        if (questRes != null) {
            for (Action action : questRes.getStateActions(questState)) {
                Action toAdd = new Action(action);
                toAdd.setActionEvent(questEvent);
                actions.add(toAdd);
            }
        }        
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Update ">
    /**
     * Metoda ktora zaktualizuje ulohu. Ked sa prvykrat aktualizuje od zmeny stavu
     * tak vykonane akcie ktore typ akcie START. Inak zacneme vykonavat akcie typu THROUGHT.
     */
    public void update() {   
        if (!started) {
            processPreStart();
            started = true;
            return;
        }
        
        for (Action action : actions) {
            if ((ActionType)action.getClickType() == ActionType.THROUGHT) {
                if (!action.isActive() && !action.isDone()) {                                        
                    questEvent.setAction(action);
                    action.setActive(true);
                    DataUtils.execute(action);                                    
                }
                //System.out.println("Updating "); 
            }
        }        
    }
    
    /**
     * Metoda ktora vykona akcie typu START. Volana je hlavne pri prvom
     * aktualizovani po zmene stavu questu.
     */
    private void processPreStart() {
        for (Action action : actions) {
            if ((ActionType)action.getClickType() == ActionType.START) {
                if (!action.isActive() && !action.isDone()) {
                    questEvent.setAction(action);
                    action.setActive(true);
                    DataUtils.execute(action);                                    
                }
            }                        
        }                        
    }
    
    /**
     * Metoda ktora zmeni stav questu podla parametru <b>state</b>. Pred zmenou musime vykonat
     * akcie typu END. Nasledne zmenime stav questu a nastavime do listu actions
     * nove akcie podla noveho stavu. Nastavenim premennej started donutime pri aktualizacii 
     * questu vykonat najprv akcie typu START.
     * @param state 
     */
    public void changeState(int state) {
        if (this.questState != state) {
            for (Action action : actions) {
                if ((ActionType)action.getClickType() == ActionType.END) {
                    if (!action.isActive() && !action.isDone()) {
                        action.setActive(true);
                        DataUtils.execute(action);                
                        action.setDone(true);
                    }
                }                        
            }

            this.questState = state;
            if (questRes != null) {
                actions.clear();
                for (Action action : questRes.getStateActions(questState)) {
                    Action toAdd = new Action(action);                
                    toAdd.setActionEvent(questEvent);
                    actions.add(toAdd);
                }
            } 
            this.started = false;  
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora vrati id questu.
     * @return Id questu.
     */
    public String getId() {
        return questRes.getId();
    }
    
    /**
     * Metoda ktora vrati nazov questu
     * @return Nazov questu
     */
    public String getLabel() {
        return questRes.getLabel();
    }
    
    /**
     * Metoda ktora vrati aktualny stav questu
     * @return Aktualny stav questu
     */
    public int getState() {
        return questState;
    }
    
    /**
     * Metoda ktora vrati globalny text questu. Globalny text je rovnaky
     * pre celu dobu questu.
     * @return Text questu
     */
    public String getText() {
        return questRes.getQuestText();
    }
    
    /**
     * Metoda ktora vrati stavovy text questu. Po kazdej zmene stavu sa meni
     * aj stavovy text.
     * @return Stavovy text questu.
     */
    public String getStateText() {
        return questRes.getStateText(questState);
    }
    
    /**
     * Metoda ktora vrati kolekciu so vsetkymi quest stavovy textmi.
     * @return Vsetky stavove quest v kolekcii
     */
    public Collection<String> getAllQuestTexts() {
        return questRes.getAllStateTexts();
    }
    
    /**
     * Metoda ktora vrati true/false podla toho ci je quest kompletny.
     * @return True/false ci je quest kompletny.
     */
    public boolean isCompleted() {
        return completed;
    }
    // </editor-fold>        
    
    // <editor-fold defaultstate="collapsed" desc=" Externalizacne metody ">
    /**
     * Metoda ktora zapise quest a jeho info do vystupneho suboru/streamu zadany v 
     * parametri <b>out</b>. Zapisujeme donho id QuestResource, stav, a priznaky
     * started a completed.
     * @param out Vystupny stream
     * @throws IOException Vynimka pri zapisovani suboru
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(questRes.getId());
        out.writeBoolean(started);
        out.writeInt(questState);        
        out.writeBoolean(completed);
    }

     /**
     * Metoda ktora nacita quest a jeho info z vstupneho suboru/streamu zadany v 
     * parametri <b>in</b>. Nacitame zneho id QuestResource, stav, a priznaky     
     * started a completed. Nakonci z id resource nacitame priamo QuestResource ktory zodpoveda 
     * id-cku.
     * @param in Vstupny stream
     * @throws IOException Vynimka pri nacitavani suboru
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.questRes = QuestsResource.getQuest(in.readUTF());        
        this.started = in.readBoolean();
        this.questState = in.readInt();
        this.completed = in.readBoolean();
        if (questRes != null) {
            this.actions = new ArrayList<>();
            for (Action action : questRes.getStateActions(questState)) {
                Action toAdd = new Action(action);
                toAdd.setActionEvent(questEvent);
                actions.add(toAdd);
            }
        }
    }
    // </editor-fold>
}
