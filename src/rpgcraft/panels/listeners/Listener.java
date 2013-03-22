/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.listeners;

import java.awt.event.ActionListener;
import rpgcraft.panels.components.Cursor;

/**
 *
 * @author Surko
 */
public class Listener implements ActionListener {

    
    public String[] parsedOp;
    
    public String[] params;
    public String[] types;
    
    protected void setRunParams(ActionEvent e) {
        if (types != null) {
            for (int i = 0; i < types.length; i++) {            
                switch (types[i]) {
                    case "LIST" : {
                        Cursor c = (Cursor)e.getParam();
                        parsedOp[i] = c.getString(c.getColumnIndex(params[i]));                        
                    } break;                    
                    default : parsedOp[i] = params[i];
                }                        
            }
        } else {
            parsedOp = params;
        }  
    }
    
    protected final void setParams(String args) {
        
        String[] parts = args.split(",");

        this.parsedOp = new String[parts.length];
        this.params = new String[parts.length];         

        for (int i = 0; i < parts.length; i++) {

            String[] paramParts = parts[i].split("#");

            switch (paramParts.length) {
                case 1 : {                    
                    this.params[i] = paramParts[0];                        
                } break;
                case 2 : {
                    this.types = new String[parts.length];
                    this.types[i] = paramParts[0];
                    this.params[i] = paramParts[1];                   
                }
                default : break;
            } 
        }                
    }
    
    public void actionPerformed(ActionEvent e) {        
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {        
    }
        
   
    
}
