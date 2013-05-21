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
import rpgcraft.entities.Player;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.panels.listeners.Action;
import rpgcraft.panels.listeners.ActionEvent;
import rpgcraft.resource.AbstractResource.ActionType;
import rpgcraft.resource.QuestsResource;
import rpgcraft.utils.DataUtils;

/**
 *
 * @author kirrie
 */
public class Quest implements Externalizable {
    
    private QuestsResource questRes;    
    private int questState;
    private ArrayList<Action> actions;
    private Player player;
    private boolean completed;
    
    private ActionEvent questEvent;
    
    public Quest() {}
    
    public Quest(QuestsResource res) {
        this.questRes = res;
        this.questState = 0;        
        this.completed = false;
        this.actions = new ArrayList();       
        this.questEvent = new ActionEvent(AbstractMenu.getMenuByName("gameMenu"), 0, 0, null, null);
        
        if (res != null) {
            for (Action action : res.getStateActions(questState)) {
                Action toAdd = new Action(action);
                toAdd.setActionEvent(questEvent);
                actions.add(toAdd);
            }
        }
        
        
        processPreStart();
    }
    
    public Quest(String id) {
        this.questRes = QuestsResource.getQuest(id);
        this.questState = 0;
        this.actions = new ArrayList(); 
        this.questEvent = new ActionEvent(AbstractMenu.getMenuByName("gameMenu"), 0, 0, null, null);
        
        if (questRes != null) {
            for (Action action : questRes.getStateActions(questState)) {
                Action toAdd = new Action(action);
                toAdd.setActionEvent(questEvent);
                actions.add(toAdd);
            }
        }                        
    }
    
    public void update() {        
        for (Action action : actions) {
            if ((ActionType)action.getClickType() == ActionType.THROUGHT) {
                if (!action.isActive() && !action.isDone()) {
                    //System.out.println(Executing THROUGHT Quest");
                    action.setActive(true);
                    DataUtils.execute(action);                                    
                }
                //System.out.println("Updating "); 
            }
        }        
    }
    
    public String getName() {
        return questRes.getId();
    }
    
    public boolean isCompleted() {
        return completed;
    }
    
    private void processPreStart() {
        for (Action action : actions) {
            if ((ActionType)action.getClickType() == ActionType.START) {
                if (!action.isActive() && !action.isDone()) {
                    //System.out.println("Executing Quest");
                    action.setActive(true);
                    DataUtils.execute(action);                                    
                }
            }                        
        }                        
    }
    
    public void changeState(int state) {
        
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
        this.actions = new ArrayList(questRes.getStateActions(questState));
        
        processPreStart();
    }
    
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(questRes.getId());
        out.writeInt(questState);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.questRes = QuestsResource.getQuest(in.readUTF());
        this.questState = in.readInt();
    }
    
}
