/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.listeners;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.entities.Entity;
import rpgcraft.entities.MovingEntity;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.graphics.ui.components.InputDialog;
import rpgcraft.graphics.ui.components.MsgDialog;
import rpgcraft.map.SaveMap;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.plugins.AbstractInMenu;
import rpgcraft.panels.GameMenu;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.EntityResource;

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
        SHOW_INMENU,
        SHOW_INVENTORY,
        SHOW_JOURNAL,
        SHOW_CHARACTER,
        GET_ENTITY,
        REMOVE_ENTITY,
        ADD_ENTITY,
        GET_ITEMFROMINVENTORY,
        ADD_ITEM,
        CREATEITEM,
        REMOVE_ITEM,
        GET_ITEMINFOLIST,
        ITEM_OPERATION,
        IS_EQUIPPED,
        UNEQUIP,
        EQUIP,
        IS_ACTIVE,
        UNACTIVE,
        ACTIVE,
        
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
        
        try {
            switch (op) {            
                case SHOW_MSG_DIALOG : {
                    if (parsedObjects.length == 2) {                    
                        try {
                            int lifeSpan = _intParse(parsedObjects[1], e);
                            MsgDialog dial = MsgDialog.getInstance();
                            //System.out.println("setting parameters");
                            dial.setText(parsedObjects[0].toString());                    
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
                            dial.setType(InputDialog.Type.DONE);
                            dial.setText(parsedObjects[0].toString());                                                   
                            dial.showOnPanel();                         
                        } break; 
                        default : 
                            LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;
                case SHOW_DIALOG : {
                    switch (parsedObjects.length) {
                        case 2 : {    
                            AbstractMenu menu = getMenu(e.getSource());
                            InputDialog dial = InputDialog.getInstance();
                            dial.setType(InputDialog.Type.DONE);  
                            dial.setText(parsedObjects[0].toString());                           
                            dial.setPosBtnText(parsedObjects[1].toString());                                              
                            dial.showOnPanel();                         
                        } break;
                        case 3 : {
                            AbstractMenu menu = getMenu(e.getSource());
                            InputDialog dial = InputDialog.getInstance();
                            dial.setType(InputDialog.Type.YES_NO);  
                            dial.setText(parsedObjects[0].toString());   
                            dial.setPosBtnText(parsedObjects[1].toString());
                            dial.setNegBtnText(parsedObjects[2].toString());
                            dial.showOnPanel();  
                        } break;
                        case 4 : {
                            AbstractMenu menu = getMenu(e.getSource());
                            InputDialog dial = InputDialog.getInstance();
                            dial.setType(InputDialog.Type.YES_NO_CANCEL); 
                            dial.setText(parsedObjects[0].toString());   
                            dial.setPosBtnText(parsedObjects[1].toString());
                            dial.setNegBtnText(parsedObjects[2].toString());
                            dial.setCancelBtnText(parsedObjects[3].toString());                                               
                            dial.showOnPanel();  
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
                            gameMenu.showInventory(null);
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
                            gameMenu.showJournal();
                        } else {
                            LOG.log(Level.WARNING, StringResource.getResource("_bplistener",
                                    new String[] {GameMenu.class.getName(), menu == null ? "null" : menu.getClass().getName()}));
                        }
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;
                case SHOW_CHARACTER : {
                    if (parsedObjects == null || parsedObjects.length == 0) {
                        AbstractMenu menu = getMenu(e.getSource());
                        if (menu instanceof GameMenu) {
                            GameMenu gameMenu = (GameMenu)menu;
                            gameMenu.showCharacter(null);
                        } else {
                            LOG.log(Level.WARNING, StringResource.getResource("_bplistener",
                                    new String[] {GameMenu.class.getName(), menu == null ? "null" : menu.getClass().getName()}));
                        }
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;            
                case SHOW_INMENU : {
                    if (parsedObjects.length == 1) {
                        
                        AbstractMenu menu = getMenu(e.getSource());
                        if (menu instanceof GameMenu) {
                            GameMenu gameMenu = (GameMenu)menu;
                            if (parsedObjects[0] instanceof String) {
                                gameMenu.showInMenu((String)parsedObjects[0], null);
                            }
                            if (parsedObjects[0] instanceof AbstractInMenu) {
                                gameMenu.setInMenu((AbstractInMenu)parsedObjects[0]);
                            }
                        } else {
                            LOG.log(Level.WARNING, StringResource.getResource("_bplistener",
                                    new String[] {GameMenu.class.getName(), menu == null ? "null" : menu.getClass().getName()}));
                        }
                        
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;                         
                /*
                 * Funkcia vrati entitu do navratovej hodnoty
                 * GET_ENTITY(MENO)
                 */
                case GET_ENTITY : {
                    AbstractMenu menu = getMenu(e.getSource());
                    if (menu instanceof GameMenu) {
                        switch (parsedObjects.length) {                        
                            case 1 : {
                                Entity entity = null;
                                if (parsedObjects[0] instanceof String) {
                                    entity = ((GameMenu)menu).getMap().getEntity((String)parsedObjects[0]);
                                    e.setReturnValue(entity);  
                                }
                                if (parsedObjects[0] instanceof Entity) {
                                    e.setReturnValue((Entity)parsedObjects[0]);
                                }                                                          
                            } break;
                            default : {
                                LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                            }                                                            
                        }                    
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_bplistener",
                                new String[] {GameMenu.class.getName(), menu == null ? "null" : menu.getClass().getName()}));
                    }
                } break;
                /*
                 * Funkcia prida entitu s menom  a meno resource na x,y,z pozicie.
                 * ADD_ENTITY(MENO, MENORESOURCE, X, Y, Z)
                 */
                case ADD_ENTITY : {
                    AbstractMenu menu = getMenu(e.getSource());
                    if (menu instanceof GameMenu) {
                        switch (parsedObjects.length) {
                            case 5 : {
                                MovingEntity entity =((GameMenu)menu).player;
                                SaveMap map = ((GameMenu)menu).getMap();
                                Entity newEntity = null;
                                try {
                                    Listener posListener = null;
                                    int x,y,z;                                    
                                    x = _intParse(parsedObjects[2], e);
                                    y = _intParse(parsedObjects[3], e);
                                    z = _intParse(parsedObjects[4], e);                                
                                    newEntity = Entity.createEntity((String)parsedObjects[0], map, EntityResource.getResource((String)parsedObjects[1])); 
                                    newEntity.setXYPix(x, y, z);
                                    map.addEntity(newEntity);                            
                                } catch (Exception ex) {
                                    LOG.log(Level.WARNING, StringResource.getResource("_pmismatchlistener", new String[] {
                                        parsedObjects[2].toString() + parsedObjects[3].toString()
                                            + parsedObjects[4].toString(), op.toString(), Integer.class.getName()}));                                                                                                
                                }                            
                            } break;                        
                            default : {
                                LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                            }                                                            
                        }                    
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_bplistener",
                                new String[] {GameMenu.class.getName(), menu == null ? "null" : menu.getClass().getName()}));
                    }               
                } break;                                                       
            }
        } catch (Exception exception) {
            new MultiTypeWrn(exception, Color.red, StringResource.getResource("_elistener",
                    new String[] {op.toString(), ""}), null).renderSpecific("_label_listenererror");
        }
        
        
    }         
    
}
