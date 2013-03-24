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
        COMPOP
    }
    
    private static final Logger LOG = Logger.getLogger(ListenerFactory.class.getName());
    public static final Listener blankListener = new Listener();
    
    public static HashMap<String, Listener> listeners = new HashMap<>();        
    
    public static Listener getListener(String command) {
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
                }
                return new CombinatedListener(_listeners);
            }
                
        }
                    
    }
    
    private static Listener getListenerFromString(String command) {
        if (listeners.containsKey(command)) {
            return listeners.get(command);
        } else {
            Listener output = madeListener(command);
            if (!(output instanceof DataListener)) {
                listeners.put(command, output);
            }
            return output;
        }
    }
    
    private static Listener madeListener(String command) {        
        String[] parts = command.split("@");
        switch (parts.length) {
            case 0 : {
                //Prazdny listener
                LOG.log(Level.INFO, StringResource.getResource("_blistener"));
                return blankListener;
                
            }
            case 1 : {
                return new DataListener(parts[0]);                
            }                
            case 2 : {
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
                    }
                } catch (Exception e) {
                    LOG.log(Level.INFO, StringResource.getResource("_ndlistener"));
                    return blankListener;
                }
            }
            default : {
                //Nedefinovany listener
                LOG.log(Level.INFO, StringResource.getResource("_ndlistener"));
                return blankListener;
                
            }
        }
    }
}
