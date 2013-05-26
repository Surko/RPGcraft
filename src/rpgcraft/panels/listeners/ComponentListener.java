/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.listeners;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Container;
import rpgcraft.panels.components.Cursor;
import rpgcraft.panels.components.swing.SwingImageList;
import rpgcraft.resource.AbstractResource;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.UiResource;
import rpgcraft.utils.DataUtils;

/**
 *
 * @author kirrie
 */
public class ComponentListener extends Listener {
    
    private static final Logger LOG = Logger.getLogger(ComponentListener.class.getName());
    
    public static final String MAINCONTAINER = "MAINCONTAINER";
    
    public enum Operations {
        SET_VISIBLE,
        SET_INVISIBLE,
        IS_VISIBLE,
        ADD_COMP,
        ADD_COMP_TO,        
        REMOVE_COMP,
        REMOVE_COMP_ALL,
        REMOVE_COMP_ALLEXCEPTMENU,
        REMOVE_CHILDCOMP_FROM,
        REMOVE_COMP_TEMP,
        REINITIALIZE,
        UNSELECTME,
        SELECTME,
        DECMYSELECTION,
        INCMYSELECTION,
        SELECTEDACTION,
        GET_MYSELECTED,
        GET_SELECTED,
        REMOVE_MYLISTELEM,
        REMOVE_LISTELEMFROM,
        ACTIVATE_ALL,
        DEACTIVATE_ALL,
        ACTIVATE,
        DEACTIVATE
    }
    
    Operations op;

    
    public ComponentListener(String op) {                       
        int fstBracket = op.indexOf('(');
        
        String params = null;
        
        if (fstBracket == -1) {
            this.op = ComponentListener.Operations.valueOf(op);
        } else {
            this.op = ComponentListener.Operations.valueOf(op.substring(0, fstBracket));
            params = op.substring(fstBracket);
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
                case SET_VISIBLE : {  
                    if (parsedObjects.length == 1) {
                        Component c = null;
                        if (parsedObjects[0] instanceof String)
                            c = DataUtils.getComponentOfId((String)parsedObjects[0]);
                        if (parsedObjects[0] instanceof Component) {
                            c = (Component)parsedObjects[0];
                        }
                        if (c == null) {
                            throw new Exception(StringResource.getResource("_mmres", new String[] {parsedObjects[0].toString()}));                            
                        }
                        c.setVisible(true);
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;
                case SET_INVISIBLE : {
                    if (parsedObjects.length == 1) {
                        Component c = null;
                        if (parsedObjects[0] instanceof String)
                            c = DataUtils.getComponentOfId((String)parsedObjects[0]);
                        if (parsedObjects[0] instanceof Component) {
                            c = (Component)parsedObjects[0];
                        }
                        if (c == null) {
                            throw new Exception(StringResource.getResource("_mmres", new String[] {parsedObjects[0].toString()}));                            
                        }
                        c.setVisible(false);
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;
                case IS_VISIBLE : {
                    if (parsedObjects.length == 1) {
                        Component c = null;
                        if (parsedObjects[0] instanceof String)
                            c = DataUtils.getComponentOfId((String)parsedObjects[0]);
                        if (parsedObjects[0] instanceof Component) {
                            c = (Component)parsedObjects[0];
                        }
                        if (c == null) {
                            throw new Exception(StringResource.getResource("_mmres", new String[] {parsedObjects[0].toString()}));                            
                        }
                        e.setReturnValue(c.isVisible());
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;
                case ADD_COMP : {
                    switch (parsedObjects.length) {
                        case 1 : {
                            if (e.getSource() instanceof Component) {
                                Component c = (Component)e.getSource();
                                AbstractMenu menu = getMenu(e.getSource());
                                UiResource resource = null;                                
                                if (parsedObjects[0] instanceof String) {
                                    resource = UiResource.getResource((String)parsedObjects[0]);
                                }
                                if (parsedObjects[0] instanceof UiResource) {
                                    resource = (UiResource)parsedObjects[0];
                                }
                                if (!menu.hasContainer(resource)) {
                                    Container src = DataUtils.getComponentFromResource(resource, menu,
                                        null, c.getContainer());                
                                    menu.addContainer(src);
                                }
                            }
                        } break;
                        case 2 : {
                            if (e.getSource() instanceof Component) {
                                Listener condition = ListenerFactory.getListener((String)parsedObjects[0], isMemorizable());
                                condition.actionPerformed(e);
                            }
                        } break;
                        default : 
                            LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }                    
                }  break;  
                case ADD_COMP_TO : {
                    if (parsedObjects.length == 2) {                    
                        AbstractMenu menu = getMenu(e.getSource());

                        UiResource resource = null;                                
                        if (parsedObjects[0] instanceof String) {
                            resource = UiResource.getResource((String)parsedObjects[0]);
                        }
                        if (parsedObjects[0] instanceof UiResource) {
                            resource = (UiResource)parsedObjects[0];
                        }                                       
                        Container src = menu.getContainer(resource);

                        Container parent;
                        if (!parsedObjects[1].equals(MAINCONTAINER)) {
                            UiResource parentResource = null;                                
                            if (parsedObjects[0] instanceof String) {
                                resource = UiResource.getResource((String)parsedObjects[0]);
                            }
                            if (parsedObjects[0] instanceof UiResource) {
                                resource = (UiResource)parsedObjects[0];
                            }                            
                            parent = menu.getContainer(parentResource);
                        } else {
                            parent = menu.getGamePanel().getContainer();
                        }

                        if (parent != null) {                    
                            if (src != null) {
                                if (src.getParentContainer() == parent) {
                                    return;
                                }                        
                                parent.addContainer(src);                        
                                menu.ugChange(true);

                            } else {
                                src = DataUtils.getComponentFromResource(resource, menu,
                                    null, parent);                            
                                parent.addChildComponent(src);
                                menu.addContainer(src);

                            }
                        }
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }                                
                } break;
                case REMOVE_COMP : {
                    if (parsedObjects.length == 1) {                    
                        AbstractMenu menu = getMenu(e.getSource());
                        UiResource resource = null;                                
                        if (parsedObjects[0] instanceof String) {
                            resource = UiResource.getResource((String)parsedObjects[0]);
                        }
                        if (parsedObjects[0] instanceof UiResource) {
                            resource = (UiResource)parsedObjects[0];
                        }                         
                        menu.removeContainer(resource);                    
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;
                case REMOVE_COMP_ALL : {
                    if (parsedObjects == null || parsedObjects.length == 0) {
                        AbstractMenu menu = getMenu(e.getSource());
                        menu.removeAllContainers(true);         
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.name()}));
                    }
                } break;
                case REMOVE_COMP_ALLEXCEPTMENU : {
                    if (parsedObjects == null || parsedObjects.length == 0) {
                        AbstractMenu menu = getMenu(e.getSource());
                        menu.removeAllContainers(false);         
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.name()}));
                    }
                } break;
                case REMOVE_COMP_TEMP : {
                    if (parsedObjects.length == 1) {                    
                        AbstractMenu menu = getMenu(e.getSource());
                        UiResource resource = null;                                
                        if (parsedObjects[0] instanceof String) {
                            resource = UiResource.getResource((String)parsedObjects[0]);
                        }
                        if (parsedObjects[0] instanceof UiResource) {
                            resource = (UiResource)parsedObjects[0];
                        }                                     
                        menu.removeContainerTemp(resource);
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.name()}));
                    }
                } break;
                case REINITIALIZE : {
                    if (parsedObjects == null || parsedObjects.length == 0) {
                        Component c = (Component)e.getSource();
                        AbstractMenu menu = c.getOriginMenu();
                        menu.removeAllContainers(true);
                        menu.reinitializeMenu();         
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.name()}));
                    }
                } break;
                case SELECTME : {  
                    if (parsedObjects == null || parsedObjects.length == 0) {
                        Component c = (Component)e.getSource();
                        c.select();
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.name()}));
                    }
                } break;
                case UNSELECTME : {
                    if (parsedObjects == null || parsedObjects.length == 0) {
                       Component c = (Component)e.getSource();
                       c.unselect(); 
                    } else {
                       LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.name()}));
                    }
                } break; 
                case DECMYSELECTION : {
                    if (parsedObjects == null || parsedObjects.length == 0) {
                        if (e.getSource() instanceof SwingImageList) {
                            SwingImageList list = (SwingImageList)e.getSource();
                            list.decSelection();
                        }
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.name()}));
                    }
                } break;
                case INCMYSELECTION : {
                    if (parsedObjects == null || parsedObjects.length == 0) {
                        if (e.getSource() instanceof SwingImageList) {
                            SwingImageList list = (SwingImageList)e.getSource();
                            list.incSelection();
                        }
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.name()}));
                    }
                } break;
                case SELECTEDACTION : {
                    if (parsedObjects.length == 1) {
                        if (e.getSource() instanceof SwingImageList) {
                            Listener listener = null;
                            if (parsedObjects[0] instanceof String) {
                                listener = ListenerFactory.getListener((String)parsedObjects[0], isMemorizable());
                            }
                            if (parsedObjects[0] instanceof Listener) {
                                listener = (Listener)parsedObjects[0];
                            }
                            listener.actionPerformed(e);
                        }
                    } else {
                       LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.name()})); 
                    }
                } break;
                case GET_MYSELECTED : {
                    if (parsedObjects == null || parsedObjects.length == 0) {
                        if (e.getSource() instanceof SwingImageList) {
                            SwingImageList list = (SwingImageList)e.getSource();
                            e.setReturnValue(list.getSelectedIndex());
                        }
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;
                case GET_SELECTED : {
                    if (parsedObjects.length == 1) {
                        AbstractMenu menu = getMenu(e.getSource());
                        Container cont = null;                                
                        if (parsedObjects[0] instanceof String) {
                            cont = menu.getContainer((String)parsedObjects[0]);
                        }
                        if (parsedObjects[0] instanceof Container) {
                            cont = (Container)parsedObjects[0];
                        }                         
                        if (cont.getComponent() instanceof SwingImageList) {
                            SwingImageList list = (SwingImageList)cont.getComponent();
                            e.setReturnValue(list.getSelectedIndex());      
                        }
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;    
                case REMOVE_MYLISTELEM : {
                    if (parsedObjects.length == 1) {
                        if (e.getSource() instanceof SwingImageList) {                            
                            Listener listener = null;
                            int removeIndex = -1;
                            if (parsedObjects[0] instanceof String) {
                                listener = ListenerFactory.getListener((String)parsedObjects[0], isMemorizable());
                                if (listener == null) {
                                    removeIndex = Integer.parseInt((String)parsedObjects[0]);
                                } else {
                                    listener.actionPerformed(e);
                                    removeIndex = (Integer)e.getReturnValue();
                                }
                            }
                            if (parsedObjects[0] instanceof Listener) {
                                listener = (Listener)parsedObjects[0];
                                listener.actionPerformed(e);
                                removeIndex = (Integer)e.getReturnValue();
                            }
                            if (parsedObjects[0] instanceof Integer) {                                
                                removeIndex = (Integer)parsedObjects[0];
                            } 

                            if (removeIndex >= 0) {
                                ((SwingImageList)e.getSource()).removeCompAtPosition(removeIndex);
                            }
                        }
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;
                /*
                 * Vymaze element z menu. Kontajner je ziskany z resource. Cislo elementu
                 * ziskany z prveho parametru
                 * REMOVE_LISTELEMFROM(RESOURCENAME/RESOURCE, INDEXFUNKCIA/INDEX)
                 */    
                case REMOVE_LISTELEMFROM : {
                    if (parsedObjects.length == 2) {                                            
                        AbstractMenu menu = getMenu(e.getSource());
                        UiResource resource = null;                                
                        if (parsedObjects[0] instanceof String) {
                            resource = UiResource.getResource((String)parsedObjects[1]);
                        }
                        if (parsedObjects[0] instanceof UiResource) {
                            resource = (UiResource)parsedObjects[1];
                        } 
                        Container dstContainer = menu.getContainer(resource);

                        if (dstContainer != null && dstContainer.getComponent() instanceof SwingImageList) {
                            int removeIndex = -1;
                            Listener listener = null;
                            if (parsedObjects[0] instanceof String) {
                                listener = ListenerFactory.getListener((String)parsedObjects[0], isMemorizable());
                                if (listener == null) {
                                    removeIndex = Integer.parseInt((String)parsedObjects[0]);
                                } else {
                                    listener.actionPerformed(e);
                                    removeIndex = (Integer)e.getReturnValue();
                                }
                            }
                            if (parsedObjects[0] instanceof Listener) {
                                listener = (Listener)parsedObjects[0];
                                listener.actionPerformed(e);
                                removeIndex = (Integer)e.getReturnValue();
                            }
                            if (parsedObjects[0] instanceof Integer) {                                
                                removeIndex = (Integer)parsedObjects[0];
                            } 

                            if (removeIndex >= 0) {
                                ((SwingImageList)dstContainer.getComponent()).removeCompAtPosition(removeIndex);
                            }
                        }
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;
                case DEACTIVATE : {
                    if (parsedObjects.length == 1) {                        
                        AbstractMenu menu = getMenu(e.getSource());
                        UiResource resource = null;                                
                        if (parsedObjects[0] instanceof String) {
                            resource = UiResource.getResource((String)parsedObjects[1]);
                        }
                        if (parsedObjects[0] instanceof UiResource) {
                            resource = (UiResource)parsedObjects[1];
                        }
                        menu.deactivate(resource); 
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;
                case DEACTIVATE_ALL : {
                    if (parsedObjects == null || parsedObjects.length == 0) {                        
                        AbstractMenu menu = getMenu(e.getSource());
                        menu.deactivateAll();                    
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;
                case ACTIVATE_ALL : {
                    if (parsedObjects == null || parsedObjects.length == 0) {                        
                        AbstractMenu menu = getMenu(e.getSource());
                        menu.activateAll();                    
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;
                case ACTIVATE : {
                    if (parsedObjects.length == 1) {                        
                        AbstractMenu menu = getMenu(e.getSource());
                        UiResource resource = null;                                
                        if (parsedObjects[0] instanceof String) {
                            resource = UiResource.getResource((String)parsedObjects[1]);
                        }
                        if (parsedObjects[0] instanceof UiResource) {
                            resource = (UiResource)parsedObjects[1];
                        }
                        menu.activate(resource); 
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;
                default : {
                    LOG.log(Level.WARNING, StringResource.getResource("_nslistener", new String[] {op.toString()}));
                }           
            } 
        } catch (Exception exception) {
            new MultiTypeWrn(exception, Color.red, StringResource.getResource("_elistener",
                    new String[] {op.toString(), ""}), null).renderSpecific("_label_listenererror");
        }
    }
    
    
    
    
    
}
