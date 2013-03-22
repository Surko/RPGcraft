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
    
    public enum Operations {
        SET_VISIBLE,
        SET_INVISIBLE,
        ADD_COMP,
        ADD_COMP_TO
    }
    
    public Operations op;
    public String parsedOp;
    
    public ComponentListener(String op) {
        this.parsedOp = op;
        String[] parts = op.split("#");
        
        switch (parts.length) {
            case 1 : {
                
            } break;
            case 2 : {
                this.op = Operations.valueOf(parts[0]);
                this.param = parts[1];                        
            } break;
            case 3 : {
                this.op = Operations.valueOf(parts[0]);
                this.type = parts[0];
                this.param = parts[1];                   
            }
            default : break;
        }                        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (type != null) {
            switch (type) {
                case "LIST" : {
                    Cursor c = (Cursor)e.getParam();
                    parsedOp = c.getString(c.getColumnIndex(param));                        
                } break;                    
                default : parsedOp = param;
            }                        
        } else {
            parsedOp = param;
        }
        
        switch (op) {
            case SET_VISIBLE : {                                                
                Component c = DataUtils.getComponentOfId(parsedOp);
                c.setVisible(true);
            } break;
            case SET_INVISIBLE : {
                Component c = DataUtils.getComponentOfId(parsedOp);
                c.setVisible(false);
            } break;
            case ADD_COMP : {
                Component c = (Component)e.getSource();
                AbstractMenu menu = c.getOriginMenu(); 
                UiResource resource = UiResource.getResource(parsedOp);
                if (!menu.hasContainer(resource)) {
                    Container src = DataUtils.getComponentFromResource(resource, menu,
                        null, c.getContainer());                
                    menu.addContainer(src);
                }
            }  break;  
                
            default : {
                LOG.log(Level.WARNING, StringResource.getResource("_ndlistener"));
            }
        }                
    }
    
    
    
    
    
}
