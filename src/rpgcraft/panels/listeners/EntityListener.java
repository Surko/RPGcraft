/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.listeners;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.entities.Entity;
import rpgcraft.entities.Item;
import rpgcraft.entities.MovingEntity;
import rpgcraft.entities.Player;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.panels.GameMenu;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.quests.Quest;
import rpgcraft.resource.EntityResource;
import rpgcraft.resource.StringResource;

/**
 *
 * @author kirrie
 */
public class EntityListener extends Listener {
    
    private static final Logger LOG = Logger.getLogger(GameListener.class.getName());
    
    public enum Operations {
        ADD_QUEST,
        REMOVE_QUEST,
        COMPLETE_QUEST,
        SET_QUESTSTATE,                
        GET_ITEMFROMINVENTORY,
        ADD_ITEM,        
        REMOVE_ITEM,
        TELEPORT,
        SAFE_TELEPORT,
        GETX,
        GETY,
        GETZ
        
    }
    
    Operations op;
    
    public EntityListener(String data) {
        int fstBracket = data.indexOf('(');
        
        String params = null;
        
        if (fstBracket == -1) {
            this.op = EntityListener.Operations.valueOf(data);
        } else {
            this.op = EntityListener.Operations.valueOf(data.substring(0, fstBracket));
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
                case ADD_QUEST : {
                    if (parsedObjects.length == 1) {
                        AbstractMenu menu = getMenu(e.getSource());
                        if (menu instanceof GameMenu) {
                            GameMenu game = (GameMenu) menu;
                            if (game.player instanceof Player) {
                                Quest quest = null;
                                if (parsedObjects[0] instanceof Quest) {
                                    quest = (Quest)quest;
                                }
                                if (parsedObjects[0] instanceof String) {
                                    quest = new Quest((String)parsedObjects[0]);
                                }                            
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
                                if (parsedObjects[0] instanceof Quest) {
                                    Quest quest = null;
                                    quest = (Quest)quest;
                                    ((Player)game.player).removeQuest(quest.getId());   
                                }
                                if (parsedObjects[0] instanceof String) {                                
                                    ((Player)game.player).removeQuest((String)parsedObjects[0]);   
                                }

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
                 * Skompletizuje quest
                 * COMPLETE_QUEST(QUEST)
                 * COMPLETE_QUEST(QUESTNAME)
                 */    
                case COMPLETE_QUEST : {
                    if (parsedObjects.length == 1) {
                        AbstractMenu menu = getMenu(e.getSource());
                        if (menu instanceof GameMenu) {
                            GameMenu game = (GameMenu) menu;
                            if (game.player instanceof Player) {                            
                                if (parsedObjects[0] instanceof Quest) {
                                    Quest quest = null;
                                    quest = (Quest)quest;
                                    ((Player)game.player).completeQuest(quest.getId());   
                                }
                                if (parsedObjects[0] instanceof String) {                                
                                    ((Player)game.player).completeQuest((String)parsedObjects[0]);   
                                }
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
                 * Nastavenie stavu questu
                 * SET_QUESTSTATE(QUEST,STAVFUNKCIA-STAVINT)#QUEST
                 * SET_QUESTSTATE(QUESTMENO,STAVFUNKCIA-STAVINT)#QUEST
                 */
                case SET_QUESTSTATE : {
                    if (parsedObjects.length == 2) {
                        AbstractMenu menu = getMenu(e.getSource());
                        if (menu instanceof GameMenu) {
                            GameMenu game = (GameMenu) menu;
                            if (game.player instanceof Player) {
                                Player player = (Player)game.player;
                                if (parsedObjects[0] instanceof Quest) {
                                    Quest quest = (Quest)parsedObjects[0];
                                    int state = _intParse(parsedObjects[1], e);
                                    quest.changeState(state);
                                    e.setReturnValue(quest);
                                    return;
                                }
                                if (parsedObjects[0] instanceof String) {                                
                                    Quest quest = player.getQuest((String)parsedObjects[0]);
                                    int state = _intParse(parsedObjects[1], e);
                                    quest.changeState(state);
                                    e.setReturnValue(quest);
                                }
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
                 * Funkcia vrati predmet 
                 * GET_ITEMFROMINVENTORY(INDEX)
                 * GET_ITEMFROMINVENTORY(INDEXFUNKCIA)
                 * GET_ITEMFROMINVENTORY(MENOENTITY, INDEX)
                 * GET_ITEMFROMINVENTORY(ENTITAFUNKCIA, INDEX)
                 */
                case GET_ITEMFROMINVENTORY : {
                    AbstractMenu menu = getMenu(e.getSource());
                    if (menu instanceof GameMenu) {
                        switch (parsedObjects.length) {
                            case 1 : {
                                MovingEntity entity =((GameMenu)menu).player;
                                try {
                                    if (parsedObjects[0] instanceof Integer) {
                                        int itemIndex = (Integer)parsedObjects[0]; 
                                        e.setReturnValue(entity.getInventory().get(itemIndex));
                                    }
                                    if (parsedObjects[0] instanceof String) {
                                        Listener indexFunction = ListenerFactory.getListener((String)parsedObjects[0], isMemorizable());
                                        if (indexFunction == null) {
                                            int itemIndex = (Integer)parsedObjects[0];
                                            e.setReturnValue(entity.getInventory().get(itemIndex));
                                        } else {
                                            indexFunction.actionPerformed(e);
                                            if (e.getReturnValue() instanceof Integer) {
                                                int itemIndex = (Integer)e.getReturnValue();
                                                e.setReturnValue(entity.getInventory().get(itemIndex));
                                            }
                                        }
                                    }                                                                                                                                                                          
                                } catch (NumberFormatException | IndexOutOfBoundsException ex) {
                                    if (ex instanceof NumberFormatException) {
                                       LOG.log(Level.WARNING, StringResource.getResource("_pmismatchlistener", new String[] {
                                        parsedObjects[0].toString(), op.toString(), Integer.class.getName()})); 
                                    }                                                                
                                }                            
                            } break;
                            case 2 : {
                                Entity entity = null;
                                try {
                                    entity = _getEntity(parsedObjects[0],(GameMenu)menu, e);

                                    if (parsedObjects[1] instanceof Integer) {
                                        int itemIndex = (Integer)parsedObjects[1]; 
                                        e.setReturnValue(entity.getInventory().get(itemIndex));
                                    }
                                    if (parsedObjects[1] instanceof String) {
                                        Listener indexFunction = ListenerFactory.getListener((String)parsedObjects[1], isMemorizable());
                                        if (indexFunction == null) {
                                            int itemIndex = (Integer)parsedObjects[0];
                                            e.setReturnValue(entity.getInventory().get(itemIndex));
                                        } else {
                                            indexFunction.actionPerformed(e);
                                            if (e.getReturnValue() instanceof Integer) {
                                                int itemIndex = (Integer)e.getReturnValue();
                                                e.setReturnValue(entity.getInventory().get(itemIndex));
                                            }
                                        }
                                    }                                                                                                                   
                                } catch (NumberFormatException | IndexOutOfBoundsException ex) {
                                    if (ex instanceof NumberFormatException) {
                                       LOG.log(Level.WARNING, StringResource.getResource("_pmismatchlistener", new String[] {
                                        parsedObjects[1].toString(), op.toString(), Integer.class.getName()})); 
                                    }                                                                
                                } 
                            } break;
                            default : {
                                LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                            }                                                            
                        }
                        System.out.println(e.getReturnValue());
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_bplistener",
                                new String[] {GameMenu.class.getName(), menu == null ? "null" : menu.getClass().getName()}));
                    }
                } break;
                /*
                 * Pridavanie predmetu nejakej entite. 
                 * ADDITEM(FUNKCIA)
                 * ADDITEM(INDEX)
                 * ADDITEM(INDEXFUNKCIA)
                 * ADDITEM(MENO, MENORESOURCE)              
                 * ADDITEM(FUNKCIA, MENOENTITY)
                 * ADDITEM(FUNKCIA, ENTITAFUNKCIA)
                 * ADDITEM(MENO, MENORESOURCE, MENOENTITY)
                 * ADDITEM(MENO, MENORESOURCE, ENTITAFUNKCIA)
                 */
                case ADD_ITEM : {
                    AbstractMenu menu = getMenu(e.getSource());
                    if (menu instanceof GameMenu) {
                        Item item = null;
                        Entity entity = null;
                        Listener itemFunction = null;
                        switch (parsedObjects.length) {
                            case 1 : {
                                entity =((GameMenu)menu).player;
                                if (parsedObjects[0] instanceof Integer) {
                                    entity.addItem((Integer)parsedObjects[0]);
                                }
                                if (parsedObjects[0] instanceof String) {
                                    itemFunction = ListenerFactory.getListener((String)parsedObjects[1], isMemorizable());
                                }
                                if (parsedObjects[0] instanceof Listener) {
                                    itemFunction = (Listener)parsedObjects[0];
                                }
                                if (itemFunction == null) {
                                    if (parsedObjects[0] instanceof String) {
                                        entity.addItem(Integer.parseInt((String)parsedObjects[0]));
                                    }
                                } else {
                                    _addItem(entity, itemFunction, e);
                                }
                            } break;
                            case 2 : {
                                Listener entFunction = null;
                                if (parsedObjects[0] instanceof String) {
                                    itemFunction = ListenerFactory.getListener((String)parsedObjects[1], isMemorizable());
                                }
                                if (parsedObjects[0] instanceof Listener) {
                                    itemFunction = (Listener)parsedObjects[0];
                                }
                                if (parsedObjects[1] instanceof Listener) {
                                    entFunction = (Listener)parsedObjects[1];
                                }
                                if (parsedObjects[1] instanceof String) {
                                    entFunction = ListenerFactory.getListener((String)parsedObjects[1], isMemorizable());                                                               
                                }

                                if (entFunction == null && itemFunction == null) {

                                    entity =((GameMenu)menu).player; 
                                    item = Item.createItem((String)parsedObjects[0], EntityResource.getResource((String)parsedObjects[1]));  
                                    entity.addItem(item);
                                } else {
                                    if (entFunction == null && itemFunction != null) {
                                        if (parsedObjects[1] instanceof Entity) {
                                            entity = (Entity)parsedObjects[1];
                                            entity.addItem(item); 
                                        }
                                        if (parsedObjects[1] instanceof String) {
                                            entity = ((GameMenu)menu).getMap().getEntity((String)parsedObjects[1]);
                                            entity.addItem(item); 
                                        } 
                                        _addItem(entity, itemFunction, e);
                                        return;
                                    }
                                    if (entFunction != null && itemFunction != null) {
                                        entFunction.actionPerformed(e);
                                        if (e.getReturnValue() instanceof Entity) {
                                            _addItem((Entity)e.getReturnValue(), itemFunction, e);
                                        }
                                    }


                                }                                                                                                              
                            } break;
                            case 3 : {
                                item = Item.createItem((String)parsedObjects[0], EntityResource.getResource((String)parsedObjects[1]));
                                Listener entFunction = null;
                                entity = _getEntity(parsedObjects[0],(GameMenu)menu, e);
                                entity.addItem(item);
                            } break;
                            default : {
                                LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                            }                                                            
                        }
                        System.out.println(e.getReturnValue());
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_bplistener",
                                new String[] {GameMenu.class.getName(), menu == null ? "null" : menu.getClass().getName()}));
                    }
                } break;
                /*
                 * Odobranie predmetu nejakej entite. Mozne druhy
                 * REMOVEITEM(ITEMFUNKCIA)
                 * REMOVEITEM(INTEGERFUNKCIA)
                 * REMOVEITEM(ITEMFUNKCIA, MENOENTITY)
                 * REMOVEITEM(INTEGERFUNKCIA, MENOENTITY)
                 * REMOVEITEM(ITEMFUNKCIA, ENTITAFUNKCIA)
                 * REMOVEITEM(INTEGERFUNKCIA, ENTITAFUNKCIA)
                 */
                case REMOVE_ITEM : {
                    AbstractMenu menu = getMenu(e.getSource());
                    if (menu instanceof GameMenu) {                    
                        Entity entity = null;
                        switch (parsedObjects.length) {
                            case 1 : {
                                entity =((GameMenu)menu).player;
                                if (parsedObjects[0] instanceof String) {
                                    _remove(entity, (String)parsedObjects[0], e);
                                }
                                if (parsedObjects[0] instanceof Integer) {
                                    entity.removeItem((Integer)parsedObjects[0]);
                                }
                                if (parsedObjects[0] instanceof Item) {
                                    entity.removeItem((Item)parsedObjects[0]);
                                }
                            } break;
                            case 2 : {
                                Listener entFunction = null;
                                if (parsedObjects[1] instanceof Listener) {
                                    entFunction = (Listener)parsedObjects[1];
                                }
                                if (parsedObjects[1] instanceof String) {
                                    entFunction = ListenerFactory.getListener((String)parsedObjects[1], isMemorizable());                                                               
                                }           

                                if (entFunction == null) {
                                    if (parsedObjects[1] instanceof Entity) {
                                        entity = (Entity)parsedObjects[1];
                                    }
                                    if (parsedObjects[1] instanceof String) {
                                        entity = ((GameMenu)menu).getMap().getEntity((String)parsedObjects[1]);
                                    }
                                    if (parsedObjects[0] instanceof String) {
                                        _remove(entity, (String)parsedObjects[0], e);
                                    }
                                    if (parsedObjects[0] instanceof Integer) {
                                        entity.removeItem((Integer)parsedObjects[0]);
                                    }
                                    if (parsedObjects[0] instanceof Item) {
                                        entity.removeItem((Item)parsedObjects[0]);
                                    }
                                } else {
                                    entFunction.actionPerformed(e);
                                    if (e.getReturnValue() instanceof Entity) {
                                        entity = (Entity)e.getReturnValue();
                                        if (parsedObjects[0] instanceof String) {
                                            _remove(entity, (String)parsedObjects[0], e);
                                        }
                                        if (parsedObjects[0] instanceof Integer) {
                                            entity.removeItem((Integer)parsedObjects[0]);
                                        }
                                        if (parsedObjects[0] instanceof Item) {
                                            entity.removeItem((Item)parsedObjects[0]);
                                        }                                    
                                    }
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
                 * Teleportuje hraca na ine miesto
                 * TELEPORT(X,Y,Z)
                 * TELEPORT(ENTNAME, X, Y, Z)
                 * TELEPORT(ENTFUNKCIA, X,Y,Z)
                 */    
                case TELEPORT : {
                    AbstractMenu menu = getMenu(e.getSource());
                    if (menu instanceof GameMenu) {
                        Entity entity = null;                    
                        switch (parsedObjects.length) {                            
                            case 3 : {
                                entity = ((GameMenu)menu).player;
                                int x,y,z;
                                x = _intParse(parsedObjects[0], e);
                                y = _intParse(parsedObjects[1], e);
                                z = _intParse(parsedObjects[2], e);                               
                                entity.setXYPix(x, y, z);
                                entity.forceChunk();
                            } break;
                            case 4 : {
                                entity = _getEntity(parsedObjects[0],(GameMenu)menu, e);
                                
                                int x,y,z;
                                x = _intParse(parsedObjects[1], e);
                                y = _intParse(parsedObjects[2], e);
                                z = _intParse(parsedObjects[3], e);                                
                                entity.setXYPix(x, y, z);
                                entity.forceChunk();
                            } break;
                            default : {
                                LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                            } 
                        }
                    }
                } break; 
                /*
                 * Teleportuje hraca na ine miesto
                 * SAFE_TELEPORT(X,Y,Z)
                 * SAFE_TELEPORT(ENTNAME, X, Y, Z)
                 * SAFE_TELEPORT(ENTFUNKCIA, X,Y,Z)
                 */      
                case SAFE_TELEPORT : {
                    AbstractMenu menu = getMenu(e.getSource());
                    if (menu instanceof GameMenu) {
                        Entity entity = null;                    
                        switch (parsedObjects.length) {                            
                            case 3 : {
                                entity = ((GameMenu)menu).player;
                                int x,y,z;
                                x = _intParse(parsedObjects[0], e);
                                y = _intParse(parsedObjects[1], e);
                                z = _intParse(parsedObjects[2], e);                                
                                entity.canMove(x - entity.getXPix(), y - entity.getYPix(), z - entity.getLevel());
                            } break;
                            case 4 : {
                                entity = _getEntity(parsedObjects[0],(GameMenu)menu, e);
                                
                                int x,y,z;
                                x = _intParse(parsedObjects[1], e);
                                y = _intParse(parsedObjects[2], e);
                                z = _intParse(parsedObjects[3], e);                                
                                entity.canMove(x - entity.getXPix(), y - entity.getYPix(), z - entity.getLevel());
                            } break;
                            default : {
                                LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                            } 
                        }
                    }
                } break;
                /*
                 * X-ova pozicia entity
                 * GETX()
                 * GETX(ENTNAME)
                 * GETX(ENTFUNKCIA)
                 * 
                 */
                case GETX : {     
                    AbstractMenu menu = getMenu(e.getSource());
                    Entity entity = null;
                    if (menu instanceof GameMenu)  {
                        switch (parsedObjects.length) {
                            case 0 : {
                                entity = ((GameMenu)menu).player;
                                e.setReturnValue(entity.getXPix());
                            } break;
                            case 1 : {
                                entity = _getEntity(parsedObjects[0],(GameMenu)menu, e);
                                e.setReturnValue(entity.getXPix());
                            } break;
                            default : {
                                LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                            }
                        }
                    }
                } break;
                /*
                 * Y-ova pozicia entity
                 * GETY()
                 * GETY(ENTNAME)
                 * GETY(ENTFUNKCIA)
                 * 
                 */    
                case GETY : {
                    AbstractMenu menu = getMenu(e.getSource());
                    Entity entity = null;
                    if (menu instanceof GameMenu)  {
                        switch (parsedObjects.length) {
                            case 0 : {
                                entity = ((GameMenu)menu).player;
                                e.setReturnValue(entity.getYPix());
                            } break;
                            case 1 : {
                                entity = _getEntity(parsedObjects[0],(GameMenu)menu, e);
                                e.setReturnValue(entity.getYPix());
                            } break;
                            default : {
                                LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                            }
                        }
                    }
                } break;
                /*
                 * Z-ova pozicia entity
                 * GETZ()
                 * GETZ(ENTNAME)
                 * GETZ(ENTFUNKCIA)
                 * 
                 */    
                case GETZ : {
                    AbstractMenu menu = getMenu(e.getSource());
                    Entity entity = null;
                    if (menu instanceof GameMenu)  {
                        switch (parsedObjects.length) {
                            case 0 : {
                                entity = ((GameMenu)menu).player;
                                e.setReturnValue(entity.getLevel());
                            } break;
                            case 1 : {
                                entity = _getEntity(parsedObjects[0],(GameMenu)menu, e);
                                e.setReturnValue(entity.getLevel());
                            } break;
                            default : {
                                LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                            }
                        }
                    }
                } break;
            }
        } catch (Exception exception) {
            new MultiTypeWrn(exception, Color.red, StringResource.getResource("_elistener",
                    new String[] {op.toString(), ""}), null).renderSpecific("_label_listenererror");
        }
    }
    
    /**
     * Metoda ktora prida predmet ziskany z listeneru. Listener vykona co ma a 
     * v navratovej hodnote mame predmet v roznom stave (Id predmetu, Predmet samotny).
     * Nakoniec pridame predmet entity pomocou metod addItem.
     * @param entity Entita ktora pridavame predmet
     * @param listener Listener z ktoreho ziskavame predmet
     * @param e ActionEvent ktory predavame funkcii.
     */
    private void _addItem(Entity entity, Listener listener, ActionEvent e) {
        Item item = null;
        listener.actionPerformed(e);
        if (e.getReturnValue() instanceof Item) {
            item = (Item)e.getReturnValue();
            entity.addItem(item);
        }
        if (e.getReturnValue() instanceof Integer) {
            entity.addItem((Integer)e.getReturnValue());
        }
    }
    
    /**
     * Pomocna metoda ku REMOVE_ITEM vetve. Ked mame priamo index tak vymazavame na indexe.
     * Ked sa vykonala vnutorna funkcia tak zistujeme typ navratovej hodnoty. Ked je typu Integer
     * tak vymazavame na indexe, ked typu Item tak vymazavame tento item. Mozne doplnit o hodnotu.
     * @param entity Entita pre ktoru budeme vymazavat predmet.
     * @param cmd Prikaz z ktoreho vytvarame listener
     * @param e ActionEvent ktory posielame listeneru.
     */
    private void _remove(Entity entity, String cmd, ActionEvent e) {
        Item item = null;
        Listener itemFunction = ListenerFactory.getListener(cmd, isMemorizable());
        if (itemFunction == null) {
            int index = Integer.parseInt(cmd);
            entity.removeItem(index);
        } else {
            itemFunction.actionPerformed(e);
            if (e.getReturnValue() instanceof Item) {
                item = (Item)e.getReturnValue();
                entity.removeItem(item);
            }
            if (e.getReturnValue() instanceof Integer) {
                entity.removeItem((Integer)e.getReturnValue());                                
            }
        }
    }
    
    /**
     * Metoda ktora vrati entitu podla parametru <b>object</b>. Menu parameter sluzi
     * nato ked object nie je funkcia a musime vratit entitu z listu entit. ActionEvent sluzi
     * na vykonanie funkcie na ziskanie entity.
     * @param object Objekt podla ktoreho ziskavame entitu
     * @param menu Menu z ktoreho potencialne mozme ziskat entitu.
     * @param e ActionEvent ktory posuvame do funkcie.
     * @return  Entita podla danych parametrov.
     */
    private Entity _getEntity(Object object, GameMenu menu, ActionEvent e) {
        if (object instanceof Entity) {
            return (Entity)object;
        }
        
        Listener entFunction = ListenerFactory.getListener(object, isMemorizable());
        if (entFunction == null) {
            if (object instanceof String) {
                return menu.getMap().getEntity((String)object);
            }
        } else {
            entFunction.actionPerformed(e);
            if (e.getReturnValue() instanceof Entity) {
                return (Entity) e.getReturnValue();
            }
            if (e.getReturnValue() instanceof String) {
                return menu.getMap().getEntity((String)object);
            }
        }
        return null;
    }
    
}
