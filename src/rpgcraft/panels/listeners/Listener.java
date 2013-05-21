/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.listeners;

import java.awt.event.ActionListener;
import rpgcraft.entities.Player;
import rpgcraft.plugins.AbstractMenu;
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
    
    private static final char PARAMDELIM = ',';
    
    public String[] parsedObjects;
    public Action action;
    
    public String[] params;
    public String[] types;
    
    protected void setRunParams(ActionEvent e) {
        if (types != null) {
            for (int i = 0; i < types.length; i++) {
               if (types[i] != null) {               
                    switch (types[i]) {
                        case "LIST" : {
                            Cursor c = (Cursor)e.getParam();
                            parsedObjects[i] = c.getString(c.getColumnIndex(params[i]));                        
                        } break;
                        case "TEXT" : {
                            Component src = (Component)e.getSource();
                            AbstractMenu menu = src.getOriginMenu();
                            Container cont = menu.getContainer(UiResource.getResource(params[i]));
                            Component c = cont.getComponent();
                            if (c instanceof SwingText) {
                                parsedObjects[i] = ((SwingText)c).getText();
                                return;
                            }
                            if (c instanceof SwingInputText) {
                                parsedObjects[i] = ((SwingInputText)c).getText();
                                return;
                            }
                        } break;
                        case "VAR" : {
                            parsedObjects[i] = DataUtils.getValueOfVariable(params[i]).toString();
                        } break;                        
                        default : parsedObjects[i] = params[i];
                    }
                } else {
                    parsedObjects[i] = params[i];
                } 
            }
        }
        else {
            parsedObjects = params;
        }
    }
    
    protected final void setParams(String args) {  
        
        String[] parts = DataUtils.split(args, PARAMDELIM);
        int length = parts == null ? 0 : parts.length;
        
        this.parsedObjects = new String[length];
        this.params = new String[length];         
        this.types = new String[length];
        
        for (int i = 0; i < length; i++) {

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
        //
        // System.out.println(this.toString());
        //
        setRunParams(e);                
                
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
         
    }
    
    protected AbstractMenu getMenu(Object src) {
        if (src instanceof AbstractMenu) {
            return (AbstractMenu) src;
        }
        if (src instanceof Component) {
            Component c = (Component)src;
            return c.getOriginMenu();
        }
        return null;       
    }
    
    public boolean isMemorizable() {
        if (action == null) return false;
        return action.isMemorizable();
    }
    
    @Override
    public String toString() {
        return new String("Parsed Operations =" + parsedObjects);
        
    }           
    
}
