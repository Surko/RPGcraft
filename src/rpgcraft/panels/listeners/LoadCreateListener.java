/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.listeners;

import rpgcraft.graphics.ui.menu.Menu;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.panels.GameMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Cursor;
import rpgcraft.utils.DataUtils;

/**
 *
 * @author kirrie
 */
public class LoadCreateListener extends Listener {
    
    public enum Operations {
        LOAD,
        CREATE
    }
    
    Operations op;
    
    public LoadCreateListener(String save) {
        int fstBracket = save.indexOf('(');
        
        String params = null;
        
        if (fstBracket == -1) {
            this.op = LoadCreateListener.Operations.valueOf(save);
        } else {
            this.op = LoadCreateListener.Operations.valueOf(save.substring(0, fstBracket));
            params = save.substring(fstBracket);
        }
                        
        if (params != null) {            
            setParams(params.substring(1, params.length() - 1));        
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);

        
        switch (op) {
            case LOAD : {                                                
                Component c = (Component)e.getSource();

                GameMenu gameMenu = (GameMenu) AbstractMenu.getMenuByName("gameMenu");
                gameMenu.loadMapInstance(parsedObjects[0]);
                c.getOriginMenu().setMenu(gameMenu);
            } break;
            case CREATE : {
                Component c = (Component)e.getSource();

                GameMenu gameMenu = (GameMenu) AbstractMenu.getMenuByName("gameMenu");
                if (gameMenu.newMapInstance(parsedObjects[0])) {
                    c.getOriginMenu().setMenu(gameMenu);
                }
                 
                    
            }
        }

    }
    
}
