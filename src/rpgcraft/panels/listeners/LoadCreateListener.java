/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.listeners;

import rpgcraft.graphics.inmenu.Menu;
import rpgcraft.panels.AbstractMenu;
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
        String[] mainOp = save.split("[(]");
        this.op = Operations.valueOf(mainOp[0]);

        if (mainOp.length > 1) {            
            setParams(mainOp[1].substring(0, mainOp[1].length() - 1));        
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);

        
        switch (op) {
            case LOAD : {                                                
                Component c = (Component)e.getSource();

                GameMenu gameMenu = (GameMenu) AbstractMenu.getMenuByName("gameMenu");
                gameMenu.loadMapInstance(parsedOp[0]);
                c.getOriginMenu().setMenu(gameMenu);
            } break;
            case CREATE : {
                Component c = (Component)e.getSource();

                GameMenu gameMenu = (GameMenu) AbstractMenu.getMenuByName("gameMenu");
                gameMenu.newMapInstance(parsedOp[0]);
                c.getOriginMenu().setMenu(gameMenu);
            }
        }

    }
    
}
