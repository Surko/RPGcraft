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
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.panels.GameMenu;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.resource.EntityResource;
import rpgcraft.resource.StringResource;

/**
 *
 * @author kirrie
 */
public class ItemListener extends Listener {
    
    private static final Logger LOG = Logger.getLogger(GameListener.class.getName());

    @Override
    public String getName() {
        return ListenerFactory.Commands.ITEM.toString();
    }
    
    public enum Operations {        
        CREATEITEM,        
        GET_ITEMINFOLIST,
        ITEM_OPERATION,
        IS_EQUIPPED,
        UNEQUIP,
        EQUIP,
        IS_ACTIVE,
        UNACTIVE,
        ACTIVE,
        IS_USABLE,
        USE,
        IS_PLACEABLE,
        PLACE,
        INFO,
        DESTROY,
    }
    
    Operations op;
    
    public ItemListener(String data) {
        int fstBracket = data.indexOf('(');
        
        String params = null;
        
        if (fstBracket == -1) {
            this.op = ItemListener.Operations.valueOf(data);
        } else {
            this.op = ItemListener.Operations.valueOf(data.substring(0, fstBracket));
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
                /*
                 * Funkcia vrati item do navratovej hodnoty
                 * CREATEITEM(MENO, MENORESOURCE)
                 */
                case CREATEITEM : {
                    switch (parsedObjects.length) {
                        case 2 : {                                                
                            Item newItem = null;
                            try {                            
                                newItem = Item.createItem((String)parsedObjects[0],
                                        EntityResource.getResource((String)parsedObjects[1]));                             
                                e.setReturnValue(newItem);
                            } catch (Exception ex) {
                                LOG.log(Level.WARNING, StringResource.getResource("_pmismatchlistener", new String[] {
                                    parsedObjects[0].toString() + parsedObjects[1].toString(),
                                    op.toString(), Integer.class.getName()}));                                                                                                
                            }                            
                        } break;                        
                        default : {
                            LOG.log(Level.WARNING, StringResource.getResource("_pelistener",
                                    new String[] {op.toString()}));
                        }                                                            
                    } 
                } break;
                /*
                 * Operacia nad predmetom
                 * ITEMOPERATION(ENTITANAME, INDEX, CMDNAME) 
                 * ITEMOPERATION(ENTITANAME, INDEXFUNKCIA, CMDNAME) 
                 * ITEMOPERATION(ENTITANAME, ITEMFUNKCIA, CMDNAME)
                 * ITEMOPERATION(ENTFUNKCIA, INDEX, CMDNAME)
                 * ITEMOPERATION(ENTFUNKCIA, INDEXFUNKCIA, CMDNAME)
                 * ITEMOPERATION(ENTFUNKCIA, ITEMFUNKCIA, CMDNAME)
                 */
                case ITEM_OPERATION : {
                    AbstractMenu menu = getMenu(e.getSource());
                    if (menu instanceof GameMenu) {
                        Entity entity = null;
                        switch (parsedObjects.length) {
                            case 3 : {
                                Listener entFunction = ListenerFactory.getListener(parsedObjects[0], isMemorizable());                             
                                if (entFunction == null) {
                                    if (parsedObjects[0] instanceof Entity) {
                                        entity = (Entity)parsedObjects[0];
                                    }
                                    if (parsedObjects[0] instanceof String) {
                                        entity = ((GameMenu)menu).getMap().getEntity((String)parsedObjects[0]);
                                    }

                                    Item.Command itemCmd = null;
                                    if (parsedObjects[2] instanceof Item.Command) {
                                        itemCmd = (Item.Command) parsedObjects[2];
                                    }
                                    if (parsedObjects[2] instanceof String) {
                                        itemCmd = Item.Command.valueOf((String)parsedObjects[2]);
                                    }
                                    _itemOperation(entity, (String)parsedObjects[1], itemCmd, e);
                                } else {
                                    entFunction.actionPerformed(e);
                                    if (e.getReturnValue() instanceof Entity) {
                                        entity = (Entity)e.getReturnValue();
                                        Item.Command itemCmd = null;
                                        if (parsedObjects[2] instanceof Item.Command) {
                                            itemCmd = (Item.Command) parsedObjects[2];
                                        }
                                        if (parsedObjects[2] instanceof String) {
                                            itemCmd = Item.Command.valueOf((String)parsedObjects[2]);
                                        }
                                        _itemOperation(entity, (String)parsedObjects[1], itemCmd, e);
                                    }
                                }
                            } break;
                            default : {
                                LOG.log(Level.WARNING, StringResource.getResource("_pelistener",
                                        new String[] {op.toString()}));
                            }
                        }
                    }
                } break;
                /*
                 * Je obleceny premdet?
                 * IS_EQUIPPED()
                 * IS_EQUIPPED(INDEXFUNCTION
                 */
                case IS_EQUIPPED : {

                } break;
                /*
                 * Oblecenie predmetu.
                 * EQUIP(ENTITANAME, INDEX)
                 * EQUIP(ENTITANAME, ITEMFUNCTION)
                 * EQUIP(ENTITANAME, INDEXFUNCTION)
                 * EQUIP(ENTFUNCTION, INDEX)
                 * EQUIP(ENTFUNCTION, ITEMFUNCTION)
                 * EQUIP(ENTFUNCTION, INDEXFUNCTION)
                 */
                case EQUIP : {
                    AbstractMenu menu = getMenu(e.getSource());
                    if (menu instanceof GameMenu) {
                        Entity entity = null;
                        switch (parsedObjects.length) {
                            case 2 : {
                                Listener entFunction = ListenerFactory.getListener(parsedObjects[0], isMemorizable());                             
                                if (entFunction == null) {
                                    if (parsedObjects[0] instanceof Entity) {
                                        entity = (Entity)parsedObjects[0];
                                    }
                                    if (parsedObjects[0] instanceof String) {
                                        entity = ((GameMenu)menu).getMap().getEntity((String)parsedObjects[0]);
                                    }

                                    Item.Command itemCmd = Item.Command.EQUIP;
                                    _itemOperation(entity, (String)parsedObjects[1], itemCmd, e);
                                } else {
                                    entFunction.actionPerformed(e);
                                    if (e.getReturnValue() instanceof Entity) {
                                        entity = (Entity)e.getReturnValue();
                                        Item.Command itemCmd = Item.Command.EQUIP;                                    
                                        _itemOperation(entity, (String)parsedObjects[1], itemCmd, e);
                                    }
                                }
                            } break;
                            default : {
                                LOG.log(Level.WARNING, StringResource.getResource("_pelistener",
                                        new String[] {op.toString()}));
                            }
                        }
                    }
                } break;
                /*
                 * Vyzlecenie predmetu.
                 * UNEQUIP(ENTITANAME, INDEX)
                 * UNEQUIP(ENTITANAME, ITEMFUNCTION)
                 * UNEQUIP(ENTITANAME, INDEXFUNCTION)
                 * UNEQUIP(ENTFUNCTION, INDEX)
                 * UNEQUIP(ENTFUNCTION, ITEMFUNCTION)
                 * UNEQUIP(ENTFUNCTION, INDEXFUNCTION)
                 */
                case UNEQUIP : {
                    AbstractMenu menu = getMenu(e.getSource());
                    if (menu instanceof GameMenu) {
                        Entity entity = null;
                        switch (parsedObjects.length) {
                            case 2 : {
                                Listener entFunction = ListenerFactory.getListener(parsedObjects[0], isMemorizable());                             
                                if (entFunction == null) {
                                    if (parsedObjects[0] instanceof Entity) {
                                        entity = (Entity)parsedObjects[0];
                                    }
                                    if (parsedObjects[0] instanceof String) {
                                        entity = ((GameMenu)menu).getMap().getEntity((String)parsedObjects[0]);
                                    }

                                    Item.Command itemCmd = Item.Command.UNEQUIP;
                                    _itemOperation(entity, (String)parsedObjects[1], itemCmd, e);
                                } else {
                                    entFunction.actionPerformed(e);
                                    if (e.getReturnValue() instanceof Entity) {
                                        entity = (Entity)e.getReturnValue();
                                        Item.Command itemCmd = Item.Command.UNEQUIP;                                    
                                        _itemOperation(entity, (String)parsedObjects[1], itemCmd, e);
                                    }
                                }
                            } break;
                            default : {
                                LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                            }
                        }
                    }
                } break;
                /*
                 * Aktivovanie predmetu ako hlavny
                 * ACTIVE(ENTITANAME, INDEX)
                 * ACTIVE(ENTITANAME, ITEMFUNCTION)
                 * ACTIVE(ENTITANAME, INDEXFUNCTION)
                 * ACTIVE(ENTFUNCTION, INDEX)
                 * ACTIVE(ENTFUNCTION, ITEMFUNCTION)
                 * ACTIVE(ENTFUNCTION, INDEXFUNCTION)
                 */
                case ACTIVE : {
                    AbstractMenu menu = getMenu(e.getSource());
                    if (menu instanceof GameMenu) {
                        Entity entity = null;
                        switch (parsedObjects.length) {
                            case 2 : {
                                Listener entFunction = ListenerFactory.getListener(parsedObjects[0], isMemorizable());                             
                                if (entFunction == null) {
                                    if (parsedObjects[0] instanceof Entity) {
                                        entity = (Entity)parsedObjects[0];
                                    }
                                    if (parsedObjects[0] instanceof String) {
                                        entity = ((GameMenu)menu).getMap().getEntity((String)parsedObjects[0]);
                                    }

                                    Item.Command itemCmd = Item.Command.ACTIVE;
                                    _itemOperation(entity, (String)parsedObjects[1], itemCmd, e);
                                } else {
                                    entFunction.actionPerformed(e);
                                    if (e.getReturnValue() instanceof Entity) {
                                        entity = (Entity)e.getReturnValue();
                                        Item.Command itemCmd = Item.Command.ACTIVE;                                    
                                        _itemOperation(entity, (String)parsedObjects[1], itemCmd, e);
                                    }
                                }
                            } break;
                            default : {
                                LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                            }
                        }
                    }
                } break;
                /*
                 * Deaktivovanie predmetu ako hlavny
                 * UNACTIVE(ENTITANAME, INDEX)
                 * UNACTIVE(ENTITANAME, ITEMFUNCTION)
                 * UNACTIVE(ENTITANAME, INDEXFUNCTION)
                 * UNACTIVE(ENTFUNCTION, INDEX)
                 * UNACTIVE(ENTFUNCTION, ITEMFUNCTION)
                 * UNACTIVE(ENTFUNCTION, INDEXFUNCTION)
                 */
                case UNACTIVE : {
                    AbstractMenu menu = getMenu(e.getSource());
                    if (menu instanceof GameMenu) {
                        Entity entity = null;
                        switch (parsedObjects.length) {
                            case 2 : {
                                Listener entFunction = ListenerFactory.getListener(parsedObjects[0], isMemorizable());                             
                                if (entFunction == null) {
                                    if (parsedObjects[0] instanceof Entity) {
                                        entity = (Entity)parsedObjects[0];
                                    }
                                    if (parsedObjects[0] instanceof String) {
                                        entity = ((GameMenu)menu).getMap().getEntity((String)parsedObjects[0]);
                                    }

                                    Item.Command itemCmd = Item.Command.UNACTIVE;
                                    _itemOperation(entity, (String)parsedObjects[1], itemCmd, e);
                                } else {
                                    entFunction.actionPerformed(e);
                                    if (e.getReturnValue() instanceof Entity) {
                                        entity = (Entity)e.getReturnValue();
                                        Item.Command itemCmd = Item.Command.UNACTIVE;                                    
                                        _itemOperation(entity, (String)parsedObjects[1], itemCmd, e);
                                    }
                                }
                            } break;
                            default : {
                                LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                            }
                        }
                    }
                } break;  
                case INFO : {
                    AbstractMenu menu = getMenu(e.getSource());
                    if (menu instanceof GameMenu) {
                        Entity entity = null;
                        switch (parsedObjects.length) {
                            case 2 : {
                                Listener entFunction = ListenerFactory.getListener(parsedObjects[0], isMemorizable());                             
                                if (entFunction == null) {
                                    if (parsedObjects[0] instanceof Entity) {
                                        entity = (Entity)parsedObjects[0];
                                    }
                                    if (parsedObjects[0] instanceof String) {
                                        entity = ((GameMenu)menu).getMap().getEntity((String)parsedObjects[0]);
                                    }

                                    Item.Command itemCmd = Item.Command.INFO;
                                    _itemOperation(entity, (String)parsedObjects[1], itemCmd, e);
                                } else {
                                    entFunction.actionPerformed(e);
                                    if (e.getReturnValue() instanceof Entity) {
                                        entity = (Entity)e.getReturnValue();
                                        Item.Command itemCmd = Item.Command.INFO;                                    
                                        _itemOperation(entity, (String)parsedObjects[1], itemCmd, e);
                                    }
                                }
                            } break;
                            default : {
                                LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                            }
                        }
                    }
                } break;
                case GET_ITEMINFOLIST : {
                    AbstractMenu menu = getMenu(e.getSource());
                    if (menu instanceof GameMenu) {
                        switch (parsedObjects.length) {
                            case 1 : {
                                MovingEntity entity =((GameMenu)menu).player;
                                try {
                                    int itemIndex = -1;
                                    if (parsedObjects[0] instanceof String) {
                                        itemIndex = Integer.parseInt((String)parsedObjects[0]);
                                    }
                                    if (parsedObjects[0] instanceof Integer) {
                                        itemIndex = (Integer)parsedObjects[0];
                                    }
                                    e.setReturnValue(Item.getItemInfo(entity.getInventory().get(itemIndex)));
                                } catch (NumberFormatException | IndexOutOfBoundsException ex) {
                                    if (ex instanceof NumberFormatException) {
                                       LOG.log(Level.WARNING, StringResource.getResource("_pmismatchlistener", new String[] {
                                        parsedObjects[0].toString(), op.toString(), Integer.class.getName()})); 
                                    }                                                                
                                }                            
                            } break;
                            case 2 : {
                                Entity entity = null;
                                if (parsedObjects[1] instanceof String) {
                                    entity =((GameMenu)menu).getMap().getEntity((String)parsedObjects[1]);
                                }
                                if (parsedObjects[1] instanceof Entity) {
                                    entity = (Entity)parsedObjects[1];
                                }
                                try {
                                    int itemIndex = -1;
                                    if (parsedObjects[0] instanceof String) {
                                        itemIndex = Integer.parseInt((String)parsedObjects[0]);
                                    }
                                    if (parsedObjects[0] instanceof Integer) {
                                        itemIndex = (Integer)parsedObjects[0];
                                    }                                                                                  
                                    e.setReturnValue(Item.getItemInfo(entity.getInventory().get(itemIndex)));
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
            }
        } catch (Exception exception) {
            new MultiTypeWrn(exception, Color.red, StringResource.getResource("_elistener",
                    new String[] {op.toString(), ""}), null).renderSpecific("_label_listenererror");
        }
    }
    
    private void _itemOperation(Entity entity, String funcCmd, Item.Command itemCmd, ActionEvent e) {
        Item item = null;
        Listener itemFunction = ListenerFactory.getListener(funcCmd, isMemorizable());
        if (itemFunction == null) {
            int index = Integer.parseInt(funcCmd);
            item = entity.getItem(index);            
            _itemOpCmd(entity, item, itemCmd);
        } else {
            itemFunction.actionPerformed(e);
            if (e.getReturnValue() instanceof Item) {
                item = (Item)e.getReturnValue();
                _itemOpCmd(entity, item, itemCmd);
            }
            if (e.getReturnValue() instanceof Integer) {
                int index = (Integer)e.getReturnValue();
                item = entity.getItem(index);            
                _itemOpCmd(entity, item, itemCmd);
            }
        }
    }
    
    private void _itemOpCmd(Entity entity, Item item, Item.Command itemCmd) {        
        switch (itemCmd) {
            case EQUIP : {
                entity.equip(item);
            } break;
            case UNEQUIP : {
                entity.unequip(item);
            } break;
            case ACTIVE : {
                entity.setActiveItem(item);
            } break;
            case UNACTIVE : {
                entity.setActiveItem(null);
            } break;
            case DESTROY : {
                entity.removeItem(item);
            } break;
            case PLACE : {
                entity.placeItem(item);
            } break;
            case USE : {
                entity.use(item);
            } break;
            default : return;
        }
        
    }
    
}
