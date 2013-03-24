/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.listeners;

import rpgcraft.utils.DataUtils;

/**
 *
 * @author kirrie
 */
public class DataListener extends Listener {
    
    public enum Operations {
        ASSIGN
    }
    
    Operations op;
    
    public DataListener(String data) {
        String[] mainOp = data.split("[(]");
        this.op = DataListener.Operations.valueOf(mainOp[0]);

        if (mainOp.length > 1) {            
            setParams(mainOp[1].substring(0, mainOp[1].length() - 1));        
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);  
        
        switch (op) {
            case ASSIGN : {
                if (params.length == 2) {
                    DataUtils.setValueOfVariable(params[0], params[1]);                    
                } break;
            }
        }
    }
    
    
    
}
