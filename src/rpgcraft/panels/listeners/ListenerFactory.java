/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.listeners;


import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.resource.StringResource;

/**
 *
 * @author Surko
 */
public class ListenerFactory {
    
    public enum Commands {
        MENUOP,
        LOAD,
        COMPOP,
        DATA,
        GAME
    }
    
    private static final Logger LOG = Logger.getLogger(ListenerFactory.class.getName());
    public static final Listener blankListener = new Listener();
    
    public static final int maxListeners = 100;    
    public static HashMap<String, Listener> listeners = new HashMap<>();        
    
    public static Listener getListener(Action action) {
        if (action == null) {
            return null;
        }
                        
        String[] actionParts = action.getAction().split("\n");
        
        switch (actionParts.length) {               
            case 1 : {
                return getListenerFromString(actionParts[0]);
            }
            default : {
                Listener[] _listeners = new Listener[actionParts.length];
                for (int i = 0; i < actionParts.length; i++) {
                    _listeners[i] = getListenerFromString(actionParts[i]);
                    
                    if (_listeners[i] == null) return null;
                    
                    if (action.isMemorizable()) {
                       listeners.put(actionParts[i], _listeners[i]);
                    }
                }
                return new CombinatedListener(_listeners);
            }
                
        }
                    
    }
    
    public static Listener getListener(String command, boolean memorizable) {
        if (command == null) {
            return null;
        }
                        
        String[] actionParts = command.split("\n");
        
        switch (actionParts.length) {               
            case 1 : {
                return getListenerFromString(actionParts[0]);
            }
            default : {
                Listener[] _listeners = new Listener[actionParts.length];
                for (int i = 0; i < actionParts.length; i++) {
                    _listeners[i] = getListenerFromString(actionParts[i]);
                    if (memorizable) {
                       listeners.put(actionParts[i], _listeners[i]);
                    }
                }
                return new CombinatedListener(_listeners);
            }
                
        }
                    
    }
    
    private static Listener getListenerFromString(String command) {
        if (listeners.containsKey(command)) {
            return listeners.get(command);
        } else {
            if (listeners.values().size() >= maxListeners) {
                listeners = new HashMap<>();
            }
            Listener output = madeListener(command);                        
                        
            return output;
        }
    }
    
    private static Listener madeListener(String command) { 
        try {
            String[] parts;
            int fstAt = command.indexOf("@");
            if (fstAt == -1) {
                parts = new String[] {command};                
            } else {
                parts = new String[2];
                parts[0] = command.substring(0, fstAt);
                parts[1] = command.substring(fstAt + 1);
            }
            
            switch (parts.length) {
                case 0 : {
                    //Prazdny listener
                    LOG.log(Level.INFO, StringResource.getResource("_blistener"));
                    return blankListener;

                }
                case 1 : {
                    LOG.log(Level.INFO, StringResource.getResource("_ndlistener", new String[] {command}));
                    return null;              
                }                
                default : {
                    try {
                        switch (Commands.valueOf(parts[0])) {
                            case MENUOP : {
                                return new MenuListener(parts[1]);                            
                            }
                            case LOAD : {
                                return new LoadCreateListener(parts[1]);                            
                            }
                            case COMPOP : {
                                return new ComponentListener(parts[1]);                           
                            }
                            case DATA : {
                                return new DataListener(parts[1]);
                            }
                            case GAME : {
                                return new GameListener(parts[1]);
                            }
                        }
                    } catch (Exception e) {
                        LOG.log(Level.INFO, StringResource.getResource("_ndlistener", new String[] {command}));
                        return blankListener;
                    }
                }                
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, StringResource.getResource("_ndlistener", new String[] {command}));
            return null;
        }
        return null;
    }
    
    
    public static void removeAll() {
        listeners = new HashMap<>();
    }
}
