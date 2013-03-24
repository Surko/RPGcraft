/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.listeners;

import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.panels.AbstractMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Container;
import rpgcraft.panels.components.Cursor;
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
        ADD_COMP,
        ADD_COMP_TO,
        REMOVE_COMP,
        REMOVE_COMP_ALL,
        REMOVE_CHILDCOMP_FROM,
        REMOVE_COMP_TEMP,
        REINITIALIZE
    }
    
    Operations op;

    
    public ComponentListener(String op) {                       
        String[] mainOp = op.split("[(]");
        this.op = ComponentListener.Operations.valueOf(mainOp[0]);
        
        if (mainOp.length > 1) {            
            setParams(mainOp[1].substring(0, mainOp[1].length() - 1));        
        }
    }

    
    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        
        switch (op) {
            case SET_VISIBLE : {                                                
                Component c = DataUtils.getComponentOfId(parsedOp[0]);
                c.setVisible(true);
            } break;
            case SET_INVISIBLE : {
                Component c = DataUtils.getComponentOfId(parsedOp[0]);
                c.setVisible(false);
            } break;
            case ADD_COMP : {
                if (parsedOp.length == 1) {
                    Component c = (Component)e.getSource();
                    AbstractMenu menu = c.getOriginMenu(); 
                    UiResource resource = UiResource.getResource(parsedOp[0]);
                    if (!menu.hasContainer(resource)) {
                        Container src = DataUtils.getComponentFromResource(resource, menu,
                            null, c.getContainer());                
                        menu.addContainer(src);
                    }
                } else {
                    LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                }
            }  break;  
            case ADD_COMP_TO : {
                if (parsedOp.length == 2) {
                    Component c = (Component)e.getSource();
                    AbstractMenu menu = c.getOriginMenu();
                    UiResource resource = UiResource.getResource(parsedOp[0]);                                       
                    Container src = menu.getContainer(resource);
                    
                    Container parent;
                    if (!parsedOp[1].equals(MAINCONTAINER)) {
                        UiResource parentResource = UiResource.getResource(parsedOp[1]); 
                        parent = menu.getContainer(parentResource);
                    } else {
                        parent = menu.getGamePanel().getContainer();
                    }

                    if (src != null) {
                        if (src.getParentContainer() == parent) return;                        
                        parent.addContainer(src);
                        parent.getComponent().addComponent(src.getComponent());
                        menu.ugChange(true);
                        
                    } else {
                        src = DataUtils.getComponentFromResource(resource, menu,
                            null, parent);
                        menu.addContainer(src);
                    }
                } else {
                    LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                }                                
            } break;
            case REMOVE_COMP : {
                if (parsedOp.length == 1) {
                    Component c = (Component)e.getSource();
                    AbstractMenu menu = c.getOriginMenu();
                    UiResource resource = UiResource.getResource(parsedOp[0]);
                    menu.removeContainer(resource);                    
                } else {
                    LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                }
            } break;
            case REMOVE_COMP_ALL : {
                if (parsedOp == null) {
                    Component c = (Component)e.getSource();
                    AbstractMenu menu = c.getOriginMenu();
                    menu.removeAllContainers();         
                } else {
                    LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.name()}));
                }
            } break;
            case REMOVE_COMP_TEMP : {
                if (parsedOp.length == 1) {
                    Component c = (Component)e.getSource();
                    AbstractMenu menu = c.getOriginMenu();
                    UiResource resource = UiResource.getResource(parsedOp[0]);                                       
                    menu.removeContainerTemp(resource);
                } else {
                    LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.name()}));
                }
            } break;
            case REINITIALIZE : {
                if (parsedOp == null) {
                    Component c = (Component)e.getSource();
                    AbstractMenu menu = c.getOriginMenu();
                    menu.removeAllContainers();
                    menu.reinitializeMenu();         
                } else {
                    LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.name()}));
                }
            } break;
                
            default : {
                LOG.log(Level.WARNING, StringResource.getResource("_ndlistener"));
            }
        }                
    }
    
    
    
    
    
}
