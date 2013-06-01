/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.listeners;

import java.awt.event.ActionListener;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Container;
import rpgcraft.panels.components.Cursor;
import rpgcraft.panels.components.swing.SwingInputText;
import rpgcraft.panels.components.swing.SwingText;
import rpgcraft.resource.UiResource;
import rpgcraft.utils.DataUtils;
import rpgcraft.utils.Pair;

/**
 *
 * @author Surko
 */
public abstract class Listener implements ActionListener {                    
    
    protected static final String INT = "INT";
    protected static final String LIST = "LIST";
    protected static final String VAR = "VAR";
    protected static final String CURSORPOSITION = "CURSORPOSITION";  
    protected static final String TEXT = "TEXT";
    protected static final String FIRST = "FIRST";
    protected static final String SECOND = "SECOND";
    protected static final String THIS = "THIS";
    
    protected static final String INTVAR = "INTVAR";
    protected static final String STRINGVAR = "STRINGVAR";
    
    
    private static final char PARAMDELIM = ',';
    
    public Object[] parsedObjects;    
    public Action action;
    
    public String[] params;
    public String[] types;
    
    protected void setRunParams(ActionEvent e) {
        if (types != null) {
            for (int i = 0; i < types.length; i++) {
               if (types[i] != null) {               
                    switch (types[i]) { 
                        case THIS : {
                            parsedObjects[i] = e.getParam();
                        } break;
                        case FIRST : {
                            Pair pair = (Pair)e.getParam();
                            parsedObjects[i] = pair.getFirst();
                        } break;
                        case SECOND : {
                            Pair pair = (Pair)e.getParam();
                            parsedObjects[i] = pair.getSecond();
                        } break;
                        case LIST : {
                            Cursor c = (Cursor)e.getParam();
                            parsedObjects[i] = c.getString(c.getColumnIndex(params[i]));                        
                        } break;
                        case TEXT : {
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
                        case VAR : {
                            parsedObjects[i] = DataUtils.getValueOfVariable(params[i]);
                        } break;
                        case INT : {
                            parsedObjects[i] = Integer.parseInt(params[i]);
                        } break;
                        case INTVAR : {
                            parsedObjects[i] = Integer.parseInt(DataUtils.getValueOfVariable(params[i]).toString());                            
                        } break;
                        case STRINGVAR : {
                            parsedObjects[i] = DataUtils.getValueOfVariable(params[i]).toString();
                        } break;    
                        case CURSORPOSITION : {
                            if (e.getParam() instanceof Cursor) {
                                Cursor c = (Cursor)e.getParam();
                                parsedObjects[i] = Integer.toString(c.getPosition());
                            }
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
        
        this.parsedObjects = new Object[length];
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
    
    protected int _intParse(Object name,ActionEvent e) {
        if (name instanceof Integer) {
                return (Integer)name;
        }
        if (name instanceof String) {
            Listener posListener = ListenerFactory.getListener((String)name, isMemorizable());
            if (posListener == null) {                            
                return Integer.parseInt((String)name);                
            } else {
                posListener.actionPerformed(e);
                if (e.getReturnValue() instanceof Integer) {
                    return (Integer)e.getReturnValue();
                }
                if (e.getReturnValue() instanceof String) {
                    return Integer.parseInt((String)e.getReturnValue());
                }
            }
        }                
        
        return 0;
    }        
    
    public abstract String getName();
    
    @Override
    public String toString() {
        return "Parsed Operations =" + parsedObjects;        
    }           
    
}
