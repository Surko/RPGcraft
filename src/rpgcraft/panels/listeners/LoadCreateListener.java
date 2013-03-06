/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.listeners;

import rpgcraft.graphics.inmenu.Menu;
import rpgcraft.panels.AbstractMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Cursor;

/**
 *
 * @author kirrie
 */
public class LoadCreateListener extends Listener {
    
    private String save;
    
    public LoadCreateListener(String save) {
        this.save = save;
        String[] parts = save.split("#");
        switch (parts.length) {
            case 2 : {
                this.param = parts[1];
                this.type = parts[0];
            } break;                
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (type != null) {
            switch (type) {
                case "LIST" : {
                    Cursor c = (Cursor)e.getParam();
                    save = c.getString(c.getColumnIndex(param));                        
                } break;                    
            }
        }

        if (e.getSource() instanceof Component) {
            Component c = (Component)e.getSource();
            
            ((Menu)c.getOriginMenu()).setMenu(AbstractMenu.getMenuByName("loadcreateMenu"));                
        }

    }
    
}
