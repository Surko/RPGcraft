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
import rpgcraft.graphics.ui.menu.CraftingMenu;
import rpgcraft.graphics.ui.menu.InventoryMenu;
import rpgcraft.map.SaveMap;
import rpgcraft.map.chunks.Chunk;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.plugins.AbstractInMenu;
import rpgcraft.panels.GameMenu;
import rpgcraft.plugins.Listener;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.EntityResource;

/**
 * Trieda dediaca od Listeneru je dalsi typ listeneru mozny vygenerovat v ListenerFactory,
 * ktory ma za ulohu vykonavat Game akcie => akcie ktore su vseobecne pre beh hry.
 */
public class GameListener extends Listener {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    private static final Logger LOG = Logger.getLogger(GameListener.class.getName());
        
    /**
     * Enum s moznymi operaciami v tomto listenery. V metode actionPerform sa
     * podla tychto operacii vykonavaju prislusne metody
     */
    public enum Operations {
        ADD_QUEST,
        REMOVE_QUEST,
        COMPLETE_QUEST,
        SET_QUESTSTATE,     
        CHECK_STATUS,
        SHOW_MSG_DIALOG,
        SHOW_YESNO_DIALOG,
        SHOW_DONE_DIALOG,
        SHOW_YESNOCANCEL_DIALOG,
        SHOW_DIALOG,
        SHOW_INMENU,
        SHOW_CRAFTING,
        SHOW_INVENTORY,
        SHOW_JOURNAL,
        SHOW_CHARACTER,
        SHOW_CONVERSATION,
        GET_ENTITY,
        REMOVE_ENTITY,
        ADD_ENTITY,
        GET_ITEMFROMINVENTORY,
        ADD_ITEM,
        CREATEITEM,
        REMOVE_ITEM,
        SPAWN_ENTITY,
        GET_ITEMINFOLIST,
        SET_TILEXY,        
        
    }
    
    Operations op;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Vytvorenie instancie listeneru pomocou textu zadaneho v parametri <b>data</b>.
     * Konstruktor rozparsuje text, urci operaciu aka sa bude vykonavat a parametre
     * pre tuto operaciu pomocou metody setParams
     * @param data Text s funkciou ktoru vykonavame
     */
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
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Vykonavanie + pomocne metody ">
    /**
     * {@inheritDoc }
     * @param e {@inheritDoc }
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e); 
        
        try {
            switch (op) {   
                case CHECK_STATUS : {
                    if (parsedObjects == null || parsedObjects.length == 0) {
                        if (e.getReturnValue() instanceof InputDialog) {
                            InputDialog input = (InputDialog)e.getReturnValue();
                            e.setReturnValue(input.checkStatus());
                            //System.out.println(e.getReturnValue());
                            break;
                        }
                        if (e.getReturnValue() instanceof MsgDialog) {
                            MsgDialog input = (MsgDialog)e.getReturnValue();                            
                            break;
                        }
                    }
                } break;
                case SHOW_MSG_DIALOG : {
                    if (parsedObjects.length == 2) {                    
                        try {
                            int lifeSpan = _intParse(parsedObjects[1], e);
                            MsgDialog dial = MsgDialog.getInstance();
                            //System.out.println("setting parameters");
                            dial.setText(parsedObjects[0].toString());                    
                            dial.setLifeSpan(lifeSpan);   
                            dial.showOnPanel();
                            e.setReturnValue(dial);
                        } catch (Exception ex) {
                            LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                        }                    
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }                
                } break;
                case SHOW_DONE_DIALOG : {                
                    switch (parsedObjects.length) {
                          case 1 :
                          case 2 : {    
                            AbstractMenu menu = getMenu(e.getSource());
                            InputDialog dial = InputDialog.getInstance();
                            dial.setType(InputDialog.Type.DONE);
                            dial.setText(parsedObjects[0].toString());                                                   
                            dial.showOnPanel();
                            e.setReturnValue(dial);
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
                            e.setReturnValue(dial);
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
                case SHOW_CRAFTING : {
                    AbstractMenu menu = getMenu(e.getSource());
                    if (menu instanceof GameMenu) {
                        GameMenu gameMenu = (GameMenu)menu;
                        Entity e1 = null;                        
                        switch (parsedObjects.length) {
                            case 0 : {                                
                                e1 = gameMenu.player;                                    
                                InventoryMenu inventory = gameMenu.showInventory(e1); 
                                inventory.addSubMenu(new CraftingMenu(inventory, 2, 2));                                
                            } break;                            
                            case 2 : {
                                e1 = gameMenu.player;
                                int gridx = _intParse(parsedObjects[0], e);
                                int gridy = _intParse(parsedObjects[1], e);
                                InventoryMenu inventory = gameMenu.showInventory(e1); 
                                inventory.addSubMenu(new CraftingMenu(inventory, gridx, gridy));
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
                case SHOW_CONVERSATION : {
                    AbstractMenu menu = getMenu(e.getSource());
                    if (menu instanceof GameMenu) {
                        GameMenu gameMenu = (GameMenu)menu;
                        Entity e1 = null;
                        Entity e2 = null;
                        switch (parsedObjects.length) {
                            case 0 : {
                                if (e.getParam() instanceof Entity) {
                                    e1 = gameMenu.player;
                                    e2 = (Entity)e.getParam();
                                    gameMenu.showConversation(e1, e2);
                                }
                            } break;
                            case 1 : {
                                e1 = gameMenu.player;
                                e2 = _getEntity(parsedObjects[0], gameMenu, e);
                                if (e2 != null) {
                                    gameMenu.showConversation(e1, e2);
                                }
                            } break;
                            case 2 : {
                                e1 = _getEntity(parsedObjects[0], gameMenu, e);
                                e2 = _getEntity(parsedObjects[1], gameMenu, e);
                                if (e2 != null) {
                                    gameMenu.showConversation(e1, e2);
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
                case SHOW_INMENU : {
                    if (parsedObjects.length == 1) {
                        
                        AbstractMenu menu = getMenu(e.getSource());
                        if (menu instanceof GameMenu) {
                            GameMenu gameMenu = (GameMenu)menu;
                            if (parsedObjects[0] instanceof String) {
                                gameMenu.showInMenu((String)parsedObjects[0], null, null);
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
                case SPAWN_ENTITY : {
                    AbstractMenu menu = getMenu(e.getSource());
                    if (menu instanceof GameMenu) {
                        GameMenu game = (GameMenu)menu;                    
                        Entity entityToSpawn = null; 
                        switch (parsedObjects.length) {                            
                            case 4 : {
                                int level = _intParse(parsedObjects[0], e);
                                int xPix = _intParse(parsedObjects[1], e);
                                int yPix = _intParse(parsedObjects[2], e);
                                if (parsedObjects[3] instanceof Entity) {                                    
                                    entityToSpawn = (Entity)parsedObjects[3];
                                    entityToSpawn.setXYPix(xPix, yPix, level);
                                    game.getMap().addEntity(entityToSpawn);
                                    //System.out.println(game.getMap().getEntity("TutorialHealing"));
                                    Chunk chunk = game.getMap().chunkXYExist(xPix, yPix);
                                }                                
                            } break;                            
                            default : {
                                LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                            } 
                        }
                    }
                } break; 
                case SET_TILEXY : {
                    AbstractMenu menu = getMenu(e.getSource());
                    if (menu instanceof GameMenu) {
                        GameMenu game = (GameMenu)menu;                    
                        switch (parsedObjects.length) {                            
                            case 4 : {
                                int level = _intParse(parsedObjects[0], e);
                                int tileX = _intParse(parsedObjects[1], e) << 5;
                                int tileY = _intParse(parsedObjects[2], e) << 5;
                                int tileValue = _intParse(parsedObjects[3], e);
                                Chunk chunk = game.getMap().chunkXYExist(tileX, tileY);
                                chunk.setTile(level, tileX, tileY, tileValue);
                            } break;
                            case 5 : {
                                int level = _intParse(parsedObjects[0], e);
                                int tileX = _intParse(parsedObjects[1], e) << 5;
                                int tileY = _intParse(parsedObjects[2], e) << 5;
                                int tileValue = _intParse(parsedObjects[3], e);
                                int tileMeta = _intParse(parsedObjects[4], e);
                                Chunk chunk = game.getMap().chunkXYExist(tileX, tileY);
                                chunk.setTile(level, tileX, tileY, tileValue, tileMeta);                                
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
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * {@inheritDoc }
     * return Meno listeneru
     */
    @Override
    public String getName() {
        return ListenerFactory.Commands.GAME.toString();
    }
    // </editor-fold>
}
