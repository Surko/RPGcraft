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
                        Component c = DataUtils.getComponentOfId(parsedObjects[0]);
                        if (c == null) {
                            throw new Exception(StringResource.getResource("_mmres", new String[] {parsedObjects[0]}));                            
                        }
                        c.setVisible(true);
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;
                case SET_INVISIBLE : {
                    if (parsedObjects.length == 1) {
                        Component c = DataUtils.getComponentOfId(parsedObjects[0]);
                        if (c == null) {
                            throw new Exception(StringResource.getResource("_mmres", new String[] {parsedObjects[0]}));                            
                        }
                        c.setVisible(false);
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;
                case IS_VISIBLE : {
                    if (parsedObjects.length == 1) {
                        Component c = DataUtils.getComponentOfId(parsedObjects[0]);
                        if (c == null) {
                            throw new Exception(StringResource.getResource("_mmres", new String[] {parsedObjects[0]}));                            
                        }
                        e.setReturnValue(c.isVisible());
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;
                case ADD_COMP : {
                    if (parsedObjects.length == 1) {
                        if (e.getSource() instanceof Component) {
                            Component c = (Component)e.getSource();
                            AbstractMenu menu = c.getOriginMenu();
                            UiResource resource = UiResource.getResource(parsedObjects[0]);
                            if (!menu.hasContainer(resource)) {
                                Container src = DataUtils.getComponentFromResource(resource, menu,
                                    null, c.getContainer());                
                                menu.addContainer(src);
                            }
                        }
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                }  break;  
                case ADD_COMP_TO : {
                    if (parsedObjects.length == 2) {                    
                        AbstractMenu menu = getMenu(e.getSource());

                        UiResource resource = UiResource.getResource(parsedObjects[0]);                                       
                        Container src = menu.getContainer(resource);

                        Container parent;
                        if (!parsedObjects[1].equals(MAINCONTAINER)) {
                            UiResource parentResource = UiResource.getResource(parsedObjects[1]); 
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
                                parent.addContainer(src);
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
                        UiResource resource = UiResource.getResource(parsedObjects[0]);
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
                        UiResource resource = UiResource.getResource(parsedObjects[0]);                                       
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
                            Listener listener = ListenerFactory.getListener(parsedObjects[0], isMemorizable());
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
                case REMOVE_MYLISTELEM : {
                    if (parsedObjects.length == 1) {
                        if (e.getSource() instanceof SwingImageList) {
                            Listener listener = ListenerFactory.getListener(parsedObjects[0], isMemorizable());
                            int removeIndex = -1;
                            if (listener == null) {
                                removeIndex = Integer.parseInt(parsedObjects[0]);
                            } else {
                                listener.actionPerformed(e);
                                removeIndex = (Integer)e.getReturnValue();
                            }

                            if (removeIndex >= 0) {
                                ((SwingImageList)e.getSource()).removeCompAtPosition(removeIndex);
                            }
                        }
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;
                case REMOVE_LISTELEMFROM : {
                    if (parsedObjects.length == 2) {                    
                        Component c = (Component) e.getSource();
                        AbstractMenu menu = c.getOriginMenu();
                        UiResource resource = UiResource.getResource(parsedObjects[1]);
                        Container dstContainer = menu.getContainer(resource);

                        if (dstContainer != null && dstContainer.getComponent() instanceof SwingImageList) {
                            Listener listener = ListenerFactory.getListener(parsedObjects[0], isMemorizable());
                            int removeIndex = -1;
                            if (listener == null) {
                                removeIndex = Integer.parseInt(parsedObjects[0]);
                            } else {
                                listener.actionPerformed(e);
                                removeIndex = (Integer)e.getReturnValue();
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
                        Component c = (Component)e.getSource();
                        AbstractMenu menu = c.getOriginMenu();
                        UiResource res = UiResource.getResource(parsedObjects[0]);
                        menu.deactivate(res); 
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;
                case DEACTIVATE_ALL : {
                    if (parsedObjects == null || parsedObjects.length == 0) {
                        Component c = (Component)e.getSource();
                        AbstractMenu menu = c.getOriginMenu();
                        menu.deactivateAll();                    
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;
                case ACTIVATE_ALL : {
                    if (parsedObjects == null || parsedObjects.length == 0) {
                        Component c = (Component)e.getSource();
                        AbstractMenu menu = c.getOriginMenu();
                        menu.activateAll();                    
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;
                case ACTIVATE : {
                    if (parsedObjects.length == 1) {
                        Component c = (Component)e.getSource();
                        AbstractMenu menu = c.getOriginMenu();
                        UiResource res = UiResource.getResource(parsedObjects[0]);
                        menu.activate(res); 
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;
                default : {
                    LOG.log(Level.WARNING, StringResource.getResource("_nslistener", new String[] {op.name()}));
                }           
            } 
        } catch (Exception exception) {
            new MultiTypeWrn(exception, Color.red, "", null).renderSpecific("");
        }
    }
    
    
    
    
    
}
