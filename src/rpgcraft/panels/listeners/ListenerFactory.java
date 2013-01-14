/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.listeners;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.GamePane;
import rpgcraft.resource.StringResource;

/**
 *
 * @author Surko
 */
public class ListenerFactory {
    
    public enum Commands {
        SETMENU        
    }
    
    private static final Logger LOG = Logger.getLogger(ListenerFactory.class.getName());
    public static final BlankListener blankListener = new BlankListener();
    
    public static HashMap<String, ActionListener> listeners = new HashMap<>();        
    
    public static ActionListener getListener(String command) {
        if (command == null) {
            return blankListener;
        }
        
        if (listeners.containsKey(command)) {
            return listeners.get(command);
        } else {
            ActionListener output = madeListener(command);
            listeners.put(command, madeListener(command));
            return output;
        }
    }
    
    private static ActionListener madeListener(String command) {        
        String[] parts = command.split("@");
        switch (parts.length) {
            case 0 : {
                //Prazdny listener
                LOG.log(Level.INFO, StringResource.getResource("_blistener"));
                return blankListener;
            }
            case 2 : {
                try {
                    switch (Commands.valueOf(parts[0])) {
                        case SETMENU : {
                            return new SetMenuListener(parts[1]);
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
