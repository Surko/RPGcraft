/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.resource.StringResource;
import rpgcraft.plugins.Listener;

/**
 * Trieda ListenerFactory, ktora ma za ulohu vytvarat instancie typu Listener podla 
 * toho aku akciu mu dame. Na toto sluzia metody getListener s roznymi parametrami.
 * Taktiez berie do uvahy listener pluginy a vytvara ich podla toho ako treba.
 * Vygenerovane listenery pri volani akcii ukladame v mape listeners. Po dosiahnuti
 * velkosti maxListeners mapu vymazeme.
 */
public class ListenerFactory {
        
    // <editor-fold defaultstate="collapsed" desc=" Pomocne triedy a enumy ">
    /**
     * Prikazy ktore je mozne pouzit pre vytvorenie listeneru
     */
    public enum Commands {
        MENUOP,
        LOAD,
        COMPOP,
        DATA,
        GAME,
        ENTITY,
        ITEM,        
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    private static final Logger LOG = Logger.getLogger(ListenerFactory.class.getName());    
    private static ArrayList<Listener> plugListeners = new ArrayList<>();
    private static final String DELIM = "[\n]";
    
    public static final int maxListeners = 100;    
    public static HashMap<String, Listener> listeners = new HashMap<>();        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Vytvarace/Gettery Listenerov">
    /**
     * Metoda ktora vrati listener z akcie zadanej v parametri <b>action</b>. Z akcie 
     * nas hlavne zaujima text s listenerom ktory ziskame metodou getAction z akcie.
     * Tento text s listenerom rozparsovavame pomocou rozdelovaca (newline).
     * Kazdy riadok potom tvori jeden listener ktory vytvarame metodou getListenerFromString.
     * Pri viacerych riadkoch vytvorime viacero listenerov a vsetky listenery
     * posunieme do CombinatedListener. Tento CombinatedListener potom sekvencne vykonava listenery
     * (mozeme ale aj skakat dopredu a dozadu a vytvarat cykly atd...).
     * @param action Akcia z ktorej vytvarame listener
     * @return Vygenerovany listener z akcie
     */
    public static Listener getListener(Action action) {
        if (action == null) {
            return null;
        }
                        
        String[] actionParts = action.getAction().split(DELIM);
        
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
    
    /**
     * Staticka metoda ktora vygeneruje Listener z objektu zadaneho parametrom <b>command</b>.
     * Ked je objekt priamo listener tak vratime objekt. Pri textovej podobe objektu
     * volame metodu {@link ListenerFactory#getListener(java.lang.String, boolean) }
     * @param command Objekt z ktoreho vytvorim listener
     * @param memorizable True/false ci si listener zapamatame
     * @return Listener ktory sme vytvorili z objektu
     */
    public static Listener getListener(Object command, boolean memorizable) {
        if (command instanceof Listener) {
            return (Listener)command;
        }
        
        if (command instanceof String) {
            return getListener((String)command, memorizable);
        }
        
        return null;
    }
    
    /**
     * Metoda ktora vrati listener z textu zadaneho v parametri <b>command</b>.
     * Tento text s listenerom rozparsovavame pomocou rozdelovaca (newline).
     * Kazdy riadok potom tvori jeden listener ktory vytvarame metodou getListenerFromString.
     * Pri viacerych riadkoch vytvorime viacero listenerov a vsetky listenery
     * posunieme do CombinatedListener. Tento CombinatedListener potom sekvencne vykonava listenery
     * (mozeme ale aj skakat dopredu a dozadu a vytvarat cykly atd...).
     * @param command Akcia z ktorej vytvarame listener
     * @param memorizable True/false ci si listener chceme zapamatat
     * @return Vygenerovany listener z textu
     */
    public static Listener getListener(String command, boolean memorizable) {
        if (command == null) {
            return null;
        }
                        
        String[] actionParts = command.split(DELIM);
        
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
    
    /**
     * Metoda ktora vrati uz vygenerovany listener z mapy ktorej kluc je parameter <b>command</b>
     * Pri neexistencii listeneru vytvarame listener metodou madeListener.
     * @param command Text podla ktoreho vytvarame listener
     * @return Vytvoreny listener vyhovujuci parametrom.
     */
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
    
    /**
     * Metoda ktora vytvori listener z textu zadanom parametrom <b>command</b>.
     * Na zaciatku v texte (napr. GAME@SHOW_JOURNAL(xxx)) najdeme poziciu "@".
     * Text pred touto poziciou (GAME) nam urcuje aky listener budeme vytvarat. Text
     * za touto poziciou (SHOW_JOURNAL(xxx)) urcuje co za akciu v tomto listenery
     * vykonavame. Pre funkcnost pluginov musime najprv kontrolovat list s nacitanymi
     * listener pluginmi kde kontrolujeme meno pluginu s textom (tymto mozme pretvorit
     * uz vytvorene listenery). Pri nenajdeni vhodneho kandidata vratime null.
     * @param command Text s listenerom aky chcem vytvorit
     * @return Listener vytvoreny z textu.
     */
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
                    return null;

                }
                case 1 : {
                    LOG.log(Level.INFO, StringResource.getResource("_ndlistener", new String[] {command}));
                    return null;              
                }                
                default : {
                    try {
                        for (Listener list : plugListeners) {
                            if (list.getName().equals(parts[0])) {
                                return list.getClass().newInstance();
                            }
                        }
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
                            case ENTITY : {
                                return new EntityListener(parts[1]);
                            }
                            case ITEM : {
                                return new ItemListener(parts[1]);
                            }
                        }
                    } catch (Exception e) {
                        LOG.log(Level.INFO, StringResource.getResource("_ndlistener", new String[] {command}));
                        return null;
                    }
                }                
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, StringResource.getResource("_ndlistener", new String[] {command}));
            return null;
        }
        return null;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Pomocne metody ">
    /**
     * Staticka metoda ktora prida do pluginovych listenerov novy listener 
     * zadany v parametri <b>listener</b>
     * @param listener Listener ktory pridavame do plugin listenerov
     */
    public static void addListener(Listener listener) {
        if (!plugListeners.contains(listener)) {
            plugListeners.add(listener);
        }
    }
    
    /**
     * Staticka metoda ktora odoberie z pluginovych listenerov listener 
     * zadany v parametri <b>listener</b>
     * @param listener Listener ktory odoberame z plugin listenerov
     */
    public static void removeListener(Listener listener) {
        plugListeners.remove(listener);
    }
    
    /**
     * Metoda ktora vymaze vsetky vygenerovane listenery z hashMapy.
     */
    public static void removeAll() {
        listeners = new HashMap<>();
    }
    // </editor-fold>
}
