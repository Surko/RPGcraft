/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.listeners;

import java.awt.Color;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.plugins.Listener;
import rpgcraft.resource.StringResource;
import rpgcraft.utils.DataUtils;
import rpgcraft.utils.MainUtils;
import rpgcraft.utils.ScriptUtils;

/**
 * Trieda dediaca od Listeneru je dalsi typ listeneru mozny vygenerovat v ListenerFactory,
 * ktory ma za ulohu vykonavat Data akcie => akcie ktore su vseobecne pre hru ako taku. 
 * Obsahuje v sebe IF, JUMP a dalsie ovladacie prikazy
 */
public class DataListener extends Listener {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    /**
     * Logger pre Listener
     */
    private static final Logger LOG = Logger.getLogger(DataListener.class.getName());
        
    /**
     * Enum s moznymi operaciami v tomto listenery. V metode actionPerform sa
     * podla tychto operacii vykonavaju prislusne metody
     */
    public enum Operations {
        ASSIGN,
        ACTION,
        IF,
        TIME_SLEEP,
        TICK_SLEEP,
        SLEEP,
        JUMP,
        OP,
        LESS,
        GREATER,
        EQUAL,
        REMOVE_LISTENERS,
        DONE,
        END,
        LUA,
        LOG,
        DEBUG
    }
    
    /**
     * Operacia na vykonanie
     */
    Operations op;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Vytvorenie instancie listeneru pomocou textu zadaneho v parametri <b>data</b>.
     * Konstruktor rozparsuje text, urci operaciu aka sa bude vykonavat a parametre
     * pre tuto operaciu pomocou metody setParams
     * @param data Text s funkciou ktoru vykonavame
     */
    public DataListener(String data) {
        int fstBracket = data.indexOf('(');
        
        String params = null;
        
        if (fstBracket == -1) {
            this.op = DataListener.Operations.valueOf(data);
        } else {
            this.op = DataListener.Operations.valueOf(data.substring(0, fstBracket));
            params = data.substring(fstBracket);
        }
                        
        if (params != null) {            
            setParams(params.substring(1, params.length() - 1));        
        }        
    }

    /**
     * {@inheritDoc }
     * @param e {@inheritDoc }
     */
    @Override
    public void actionPerformed(ActionEvent e) {        
        
        super.actionPerformed(e);  
        
        try {
            switch (op) {
                // Assign hodnoty zadanej v parsedOp[0] do premennej s menom zadanej v parsedOp[1]
                case ASSIGN : {
                    if (parsedObjects.length == 2) {
                        Object srcVal;                        
                        Listener val = ListenerFactory.getListener(parsedObjects[0], isMemorizable());
                        if (val == null) {
                            srcVal = parsedObjects[0];
                        } else {
                            val.actionPerformed(e);
                            srcVal = e.getReturnValue();
                        }                    
                        DataUtils.setValueOfVariable(srcVal, parsedObjects[1]);                    
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;
                // Vykonanie akcie s parametrom v parsedOp[0]    
                case ACTION : {
                    if (parsedObjects.length == 1) {
                        Listener listener = ListenerFactory.getListener(parsedObjects[0], isMemorizable());
                        listener.actionPerformed(e);
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;
                case OP : {
                    /*
                     * Rovnake pre EQUAL, LESS, ... 
                     * Volanim getComparableValues sa volaju listenery s menami definovanymi v parsedOp
                     * (mozu to byt dalsie listener alebo priamo hodnoty). Ked sa vytvaraju listenery 
                     * tak pri normalnom tvoreni listenerov sa berie do uvahy ci sa ma zapamatat v pamati.
                     * Volanim tejto metody ale vytvarame 2 listenery, ktore nebudu ukladane do pamati (volame
                     * metodu v ListeneryFactory#getListener(string,false).
                     */
                    if (parsedObjects.length == 3) {
                        Comparable[] compValues = DataUtils.getComparableValues(parsedObjects, e);

                        switch ((String)parsedObjects[2]) {
                            case "<" : {
                                if (compValues[0].compareTo(compValues[1]) < 0) {
                                    e.setReturnValue(true);
                                } else {
                                    e.setReturnValue(false);
                                }    
                            } break;
                            case ">" : {
                                if (compValues[0].compareTo(compValues[1]) > 0) {
                                    e.setReturnValue(true);
                                } else {
                                    e.setReturnValue(false);
                                }    
                            } break;
                            case "=" : {
                                if (compValues[0].compareTo(compValues[1]) == 0) {
                                    e.setReturnValue(true);
                                } else {
                                    e.setReturnValue(false);
                                }    
                            } break;
                            case ">=" : {
                                if (compValues[0].compareTo(compValues[1]) >= 0) {
                                    e.setReturnValue(true);
                                } else {
                                    e.setReturnValue(false);
                                }    
                            } break;
                            case "<=" : {
                                if (compValues[0].compareTo(compValues[1]) <= 0) {
                                    e.setReturnValue(true);
                                } else {
                                    e.setReturnValue(false);
                                }    
                            } break;
                            default : {
                                LOG.log(Level.WARNING, StringResource.getResource("_ndoperlistener", new String[] {op.name()}));
                            }                                                
                        }

                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;
                case EQUAL : {
                    if (params.length == 2) {                    
                        Comparable[] compValues = DataUtils.getComparableValues(parsedObjects, e);

                        if (compValues[0].compareTo(compValues[1]) == 0) {
                            e.setReturnValue(true);
                        } else {
                            e.setReturnValue(false);
                        }                    
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;
                case LESS : {
                    if (params.length == 2) {                    
                        Comparable[] compValues = DataUtils.getComparableValues(parsedObjects, e);

                        if (compValues[0].compareTo(compValues[1]) < 0) {
                            e.setReturnValue(true);
                        } else {
                            e.setReturnValue(false);
                        }
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;
                case GREATER : {
                    if (params.length == 2) {
                        Comparable[] compValues = DataUtils.getComparableValues(parsedObjects, e);

                        if (compValues[0].compareTo(compValues[1]) > 0) {
                            e.setReturnValue(true);
                        } else {
                            e.setReturnValue(false);
                        }
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;
                case IF : {
                    switch (params.length) {
                        case 2 : {
                            Listener condition = ListenerFactory.getListener(parsedObjects[0], isMemorizable());
                            condition.actionPerformed(e);

                            if (e.getReturnValue() instanceof Boolean || e.getReturnValue() instanceof Integer) {
                                if ((Boolean)e.getReturnValue()) {
                                    Listener action = ListenerFactory.getListener(parsedObjects[1], isMemorizable());
                                    action.actionPerformed(e);
                                }
                            } else {
                                LOG.log(Level.SEVERE, StringResource.getResource("_boolistener"));
                            }
                        } break;                            
                        case 3 : {
                            Listener condition = ListenerFactory.getListener(parsedObjects[0], isMemorizable());
                            condition.actionPerformed(e);

                            if (e.getReturnValue() instanceof Boolean || e.getReturnValue() instanceof Integer) {
                                if ((Boolean)e.getReturnValue()) {
                                    Listener action = ListenerFactory.getListener(parsedObjects[1], isMemorizable());
                                    action.actionPerformed(e);
                                } else {
                                    Listener action = ListenerFactory.getListener(parsedObjects[2], isMemorizable());
                                    action.actionPerformed(e);
                                }
                            } else {
                                LOG.log(Level.SEVERE, StringResource.getResource("_boolistener"));
                            } 
                        } break;
                        default :
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;
                case REMOVE_LISTENERS : {
                    if (parsedObjects == null || parsedObjects.length == 0) {
                        ListenerFactory.removeAll();
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;   
                case TICK_SLEEP : {
                    if (parsedObjects.length == 1) { 
                        int ticks = _intParse(parsedObjects[0], e);                                                                                                
                        e.getAction().setSleepTicks(ticks);
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }      
                } break;
                case TIME_SLEEP : {
                    if (parsedObjects.length == 1) {
                        int time = _intParse(parsedObjects[0], e); 
                        e.getAction().setSleepTime(time);
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }      
                } break;
                case SLEEP : {
                    if (parsedObjects.length == 1) {
                        int time = _intParse(parsedObjects[0], e); 
                        try {
                            Thread.sleep(time);
                        } catch (Exception intEx) {
                            
                        }
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }      
                } break;    
                case END :
                case DONE : {
                    if (parsedObjects == null || parsedObjects.length == 0) {
                        e.getAction().setDone(true);
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }       
                } break;
                case JUMP : {
                    if (parsedObjects.length == 1) {
                        int jump = _intParse(parsedObjects[0], e);
                        e.setJumpValue(jump);
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;
                case LUA : {
                    if (parsedObjects.length == 1) {
                        try {
                            ScriptUtils.callLoadScript((String)parsedObjects[0], e);
                        } catch (IOException ex) {
                            LOG.log(Level.SEVERE, StringResource.getResource("_mluascript", new String[] {ex.getMessage()}));
                        }
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;
                case LOG : {
                    if (parsedObjects.length == 1) {                        
                        LOG.log(Level.INFO, parsedObjects[0].toString());
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                    }
                } break;
                case DEBUG : {
                    if (parsedObjects.length == 1) {
                        if (parsedObjects[0] instanceof Boolean) {
                            MainUtils.DEBUG = (Boolean)parsedObjects[0];
                        }
                        if (parsedObjects[0] instanceof String) {
                            MainUtils.DEBUG = Boolean.parseBoolean((String)parsedObjects[0]);
                        }
                    }
                } break;
                default : {
                    LOG.log(Level.WARNING, StringResource.getResource("_nslistener", new String[] {op.toString()}));  
                }            
            }
        } catch (Exception exception) {
            new MultiTypeWrn(exception, Color.red, StringResource.getResource("_elistener",
                    new String[] {op.toString(), ""}), null).renderSpecific("_label_listenererror");
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * {@inheritDoc }
     * @return Meno listeneru
     */
    @Override
    public String getName() {
        return ListenerFactory.Commands.DATA.toString();
    }
    // </editor-fold>
}
