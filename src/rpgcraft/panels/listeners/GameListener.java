/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.listeners;

import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.MainGameFrame;
import rpgcraft.entities.Player;
import rpgcraft.graphics.ui.components.InputDialog;
import rpgcraft.graphics.ui.components.MsgDialog;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.panels.GameMenu;
import rpgcraft.quests.Quest;
import rpgcraft.resource.StringResource;

/**
 *
 * @author kirrie
 */
public class GameListener extends Listener {
    private static final Logger LOG = Logger.getLogger(GameListener.class.getName());
    
    public enum Operations {
        ADD_QUEST,
        REMOVE_QUEST,
        COMPLETE_QUEST,
        SET_QUESTSTATE,
        SHOW_MSG_DIALOG,
        SHOW_YESNO_DIALOG,
        SHOW_DONE_DIALOG,
        SHOW_YESNOCANCEL_DIALOG,
        SHOW_DIALOG,
        SHOW_INVENTORY,
        SHOW_JOURNAL
    }
    
    Operations op;
    
    public GameListener(String data) {
        int fstBracket = data.indexOf('(');
        
        String params = null;
        
        if (fstBracket == -1) {
            this.op = GameListener.Operations.valueOf(data);
        } else {
            this.op = GameListener.Operations.valueOf(data.substring(0, fstBracket));
            params = data.substring(fstBracket);
        }
                        
        if (params != null) {            
            setParams(params.substring(1, params.length() - 1));        
        }
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e); 
        
        switch (op) {
            case ADD_QUEST : {
                if (parsedObjects.length == 1) {
                    AbstractMenu menu = getMenu(e.getSource());
                    if (menu instanceof GameMenu) {
                        GameMenu game = (GameMenu) menu;
                        if (game.player instanceof Player) {                             
                            Quest quest = new Quest(parsedObjects[0]);
                            ((Player)game.player).addQuest(quest);
                        }                        
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_bplistener",
                                new String[] {GameMenu.class.getName(), menu == null ? "null" : menu.getClass().getName()}));
                    }                                            
                } else {
                    LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                }
            } break;
            case REMOVE_QUEST : {
                if (parsedObjects.length == 1) {
                    AbstractMenu menu = getMenu(e.getSource());
                    if (menu instanceof GameMenu) {
                        GameMenu game = (GameMenu) menu;
                        if (game.player instanceof Player) {
                            ((Player)game.player).removeQuest(parsedObjects[0]);                        
                        }                        
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_bplistener",
                                new String[] {GameMenu.class.getName(), menu == null ? "null" : menu.getClass().getName()}));
                    }
                } else {
                    LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                }
            } break;
            case SHOW_MSG_DIALOG : {
                if (parsedObjects.length == 2) {                    
                    try {
                        int lifeSpan = Integer.parseInt(parsedObjects[1]);
                        MsgDialog dial = MsgDialog.getInstance();
                        //System.out.println("setting parameters");
                        dial.setText(parsedObjects[0]);                    
                        dial.setLifeSpan(lifeSpan);   
                        dial.showOnPanel();
                    } catch (Exception ex) {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }                    
                } else {
                    LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                }                
            } break;
            case SHOW_DONE_DIALOG : {
                switch (parsedObjects.length) {
                    case 2 : {    
                        AbstractMenu menu = getMenu(e.getSource());
                        InputDialog dial = InputDialog.getInstance();
                        dial.setText(parsedObjects[0]);                    
                        dial.setType(InputDialog.Type.DONE);                        
                        dial.showOnPanel();                         
                    } break;
                    case 3 : {
                        
                    } break;
                    default : 
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                }
            } break;
            case SHOW_INVENTORY : {
                if (parsedObjects == null || parsedObjects.length == 0) {
                    AbstractMenu menu = getMenu(e.getSource());
                    if (menu instanceof GameMenu) {
                        GameMenu gameMenu = (GameMenu)menu;
                        gameMenu.showInventory();
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_bplistener",
                                new String[] {GameMenu.class.getName(), menu == null ? "null" : menu.getClass().getName()}));
                    }
                } else {
                    LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                }
            } break; 
            case SHOW_JOURNAL : {
                if (parsedObjects == null || parsedObjects.length == 0) {
                    AbstractMenu menu = getMenu(e.getSource());
                    if (menu instanceof GameMenu) {
                        GameMenu gameMenu = (GameMenu)menu;
                        gameMenu.showInventory();
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_bplistener",
                                new String[] {GameMenu.class.getName(), menu == null ? "null" : menu.getClass().getName()}));
                    }
                } else {
                    LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                }
            } break;
        }
        
        
    }
    
}
