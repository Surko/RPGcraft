/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.listeners;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.resource.StringResource;
import rpgcraft.utils.DataUtils;
import rpgcraft.utils.ScriptUtils;

/**
 *
 * @author kirrie
 */
public class DataListener extends Listener {
    
    private static final Logger LOG = Logger.getLogger(DataListener.class.getName());
    
    public enum Operations {
        ASSIGN,
        ACTION,
        IF,
        TIME_SLEEP,
        TICK_SLEEP,
        JUMP,
        OP,
        LESS,
        GREATER,
        EQUAL,
        REMOVE_LISTENERS,
        DONE,
        END,
        LUA,
        LOG
    }
    
    Operations op;
    
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

    @Override
    public void actionPerformed(ActionEvent e) {        
        
        super.actionPerformed(e);  
        
        switch (op) {
            // Assign hodnoty zadanej v parsedOp[0] do premennej s menom zadanej v parsedOp[1]
            case ASSIGN : {
                if (parsedObjects.length == 2) {
                    String srcVal;
                    Listener val = ListenerFactory.getListener(parsedObjects[0], isMemorizable());
                    if (val == null) {
                        srcVal = parsedObjects[0];
                    } else {
                        val.actionPerformed(e);
                        srcVal = (String)e.getReturnValue();
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
                    
                    switch (parsedObjects[2]) {
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
                if (params.length == 3) {
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
                } else {
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
                    int ticks = Integer.parseInt(parsedObjects[0]);
                    e.getAction().setSleepTicks(ticks);
                } else {
                    LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                }      
            } break;
            case TIME_SLEEP : {
                if (parsedObjects.length == 1) {
                    int time = Integer.parseInt(parsedObjects[0]);
                    e.getAction().setSleepTime(time);
                } else {
                    LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                }      
            }
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
                    int jump = Integer.parseInt(parsedObjects[0]);
                    e.setJumpValue(jump);
                } else {
                    LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                }
            } break;
            case LUA : {
                if (parsedObjects.length == 1) {
                    try {
                        ScriptUtils.loadScript(parsedObjects[0], e);
                    } catch (IOException ex) {
                        LOG.log(Level.SEVERE, StringResource.getResource("_mluascript", new String[] {ex.getMessage()}));
                    }
                } else {
                    LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                }
            } break;
            case LOG : {
                if (parsedObjects.length == 1) {
                    System.out.println(parsedObjects[0]);
                    LOG.log(Level.INFO, parsedObjects[0]);
                } else {
                    LOG.log(Level.WARNING, StringResource.getResource("_pelistener", new String[] {op.toString()}));
                }
            } break;
            default : {
                LOG.log(Level.WARNING, StringResource.getResource("_nslistener", new String[] {op.toString()}));  
            }            
        }
    }
    
    
    
}
