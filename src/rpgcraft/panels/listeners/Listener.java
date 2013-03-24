/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.listeners;

import java.awt.event.ActionListener;
import rpgcraft.panels.AbstractMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Container;
import rpgcraft.panels.components.Cursor;
import rpgcraft.panels.components.swing.SwingInputText;
import rpgcraft.panels.components.swing.SwingText;
import rpgcraft.resource.UiResource;
import rpgcraft.utils.DataUtils;

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
               if (types[i] != null) {               
                    switch (types[i]) {
                        case "LIST" : {
                            Cursor c = (Cursor)e.getParam();
                            parsedOp[i] = c.getString(c.getColumnIndex(params[i]));                        
                        } break;
                        case "TEXT" : {
                            Component src = (Component)e.getSource();
                            AbstractMenu menu = src.getOriginMenu();
                            Container cont = menu.getContainer(UiResource.getResource(params[i]));
                            Component c = cont.getComponent();
                            if (c instanceof SwingText) {
                                parsedOp[i] = ((SwingText)c).getText();
                                return;
                            }
                            if (c instanceof SwingInputText) {
                                parsedOp[i] = ((SwingInputText)c).getText();
                                return;
                            }
                        } break;
                        case "VAR" : {
                            parsedOp[i] = DataUtils.getValueOfVariable(params[i]);
                        } break;
                        default : parsedOp[i] = params[i];
                    }
                } else {
                    parsedOp[i] = params[i];
                } 
            }
        }
        else {
            parsedOp = params;
        }
    }
    
    protected final void setParams(String args) {                
        
        String[] parts = args.split(",");

        this.parsedOp = new String[parts.length];
        this.params = new String[parts.length];         
        this.types = new String[parts.length];
        
        for (int i = 0; i < parts.length; i++) {

            String[] paramParts = parts[i].split("#");
                
            switch (paramParts.length) {
                case 1 : {                    
                    this.params[i] = paramParts[0];                        
                } break;
                case 2 : {                    
                    this.types[i] = paramParts[0];
                    this.params[i] = paramParts[1];                   
                }
                default : break;
            } 
        }                
    }
    
    public void actionPerformed(ActionEvent e) { 
        setRunParams(e);                
                
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
         
    }
        
   
    
}
