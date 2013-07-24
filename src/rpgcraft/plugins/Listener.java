/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.plugins;

import java.awt.event.ActionListener;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Container;
import rpgcraft.panels.components.swing.SwingInputText;
import rpgcraft.panels.components.swing.SwingText;
import rpgcraft.panels.listeners.Action;
import rpgcraft.panels.listeners.ActionEvent;
import rpgcraft.panels.listeners.ListenerFactory;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.UiResource;
import rpgcraft.utils.Cursor;
import rpgcraft.utils.DataUtils;
import rpgcraft.utils.Pair;

/**
 * Trieda Listener od ktorej dedia vsetky druhy listenerov ktore su nadefinovane v tomto packagi.
 * Taktiez ked vytvarame plugin na vytvorenie novych listenerov je dolezite implementovat/dedit
 * od tejto abstraktnej triedy. Trieda zdruzuje zakladne metody, ktore rozparsuju parametre 
 * v listeneri (setParams) a potom podla tychto rozparsovanych casti urcime pri vykonavani
 * listeneru co za parametre presne mame (setRunParams). Na vykonanie listeneru je dolezite
 * podedit metodu actionPerform ktora podla parsedObjects rozhodne co za akciu presne vykoname.
 * @author Surko
 */
public abstract class Listener implements ActionListener {                    
    
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    protected static final String INT = "INT";
    protected static final String LIST = "LIST";
    protected static final String VAR = "VAR";
    protected static final String CURSORPOSITION = "CURSORPOSITION";  
    protected static final String TEXT = "TEXT";
    protected static final String FIRST = "FIRST";
    protected static final String SECOND = "SECOND";
    protected static final String THIS = "THIS";
    protected static final String LOCAL = "LOCAL";
    protected static final String RETURN = "RETURN";
    
    protected static final String INTVAR = "INTVAR";
    protected static final String STRINGVAR = "STRINGVAR";
        
    private static final char PARAMDELIM = ',';
    private static final char STARTDEPTH = '(', ENDDEPTH = ')';
    
    /**
     * Rozparsovane a dosadene parametre podla behu listeneru. Pri kazdom behu moze dat listener ine objekty.
     */
    public Object[] parsedObjects;
    /**
     * Akcia ktorej patri listener a ktory hu zavolal.
     */
    public Action action;
    
    /**
     * Rozparsovane parametre
     */
    public String[] params;
    /**
     * Rozparsovane typy parametrov. Mozne typy su zadane vyssie ako finalne konstanty
     */
    public String[] types;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Pomocne metody pre ziskanie parametrov ">
    /**
     * Metoda ktora podla finalnych premennych zadanych v premennych rozhoduje co sa
     * dosadi do pola parsedObjects. Tieto finalne premenne porovnavame s typmi a ked 
     * pasuje typ na nejaku premennu tak podla parametre urcime dodatocne info.      
     * <p>
     * <b>Priklad</b> <br>
     * Nech mame nejaky listener XXX(LIST#_id) <br>
     * Vidime ze medzi zatvorkami mame LIST#_id, co je parameter listeneru. <br>
     * Po prebehnuti metody setParams sa rozparsovava na params a types polia <br>
     * types = [ LIST ] <br>
     * params = [ _id ] <br>
     * Nasa metoda podla typu LIST urci ze mame vybrat z parametra ActionEventu Cursor a
     * z kurzora vratit hodnotu _id. <br>
     * Pravdaze po kazdom behu listeneru moze byt hodnota pod _id rozna preto je nutne
     * volat tuto metodu pri kazdom actionPerform.
     * </p>
     * @param e ActionEvent z ktoreho ziskavame objekty ktore boli poslane.
     */
    protected void setRunParams(ActionEvent e) {
        if (types != null) {
            for (int i = 0; i < types.length; i++) {
               if (types[i] != null) {               
                    switch (types[i]) {
                        case RETURN : {
                            Listener list = ListenerFactory.getListener(params[i], isMemorizable());
                            if (list != null) {
                                list.actionPerformed(e);
                                parsedObjects[i] = e.getReturnValue();
                            }
                        } break;
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
                        case LOCAL : {
                            parsedObjects[i] = StringResource.getResource(params[i]);
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
    
    /**
     * Metoda ktora rozparsovava parametre zadane v parametri <b>args</b>. 
     * Rozparsovane casti este raz rozdelime podla rozdelovaca # ktory rozdeli parameter
     * na typ a typovy parameter. 
     * @param args Argumenty/Parametre listeneru
     */
    protected final void setParams(String args) {  
        
        String[] parts = DataUtils.split(args, PARAMDELIM, STARTDEPTH, ENDDEPTH);
        int length = parts == null ? 0 : parts.length;
        
        this.parsedObjects = new Object[length];
        this.params = new String[length];         
        this.types = new String[length];
        
        for (int i = 0; i < length; i++) {

            String[] paramParts = DataUtils.split(parts[i], '#', STARTDEPTH, ENDDEPTH);
            
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
    
    /**
     * Metoda ktory ma vratit cislo z objektu ktory je zadany v parametri <b>name</b>.
     * Ked je objekt cislo tak je to jasne. Ked to je nejaky listener tak volame listener
     * a vratime navratovu hodnotu ktora musi byt cislo. Ked to je text
     * tak sa snazim tento text rozparsovat na cislo.
     * @param name Meno objektu z ktoreho chcem ziskat cislo
     * @param e ActionEvent ktory vyvolal listener a ktory posuvame pri dalsom volani listeneru
     * @return Cislo zodpovedajuce objektu.
     */
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
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Vykonavace akcii ">
    /**
     * Metoda ktora vykona Listener podla toho co mame v parametri <b>e</b>.
     * Kazdy podedeny listener tuto metodu pretazuje na vykonavanie prislusnych operacii,
     * no vzdy je dolezite pred vyberom akcie vykonat metodu setRunParams aby sme zistili
     * co za parametre mame pri behu listeneru. Vacsinou vyber akcie podlieha porovnavaniu 
     * definovanej premennej op (v plugin listeneroch moze byt inak pomenovany alebo 
     * moze vyberat akcie totalne inym sposobom) s enumom Operations ktory je definovany
     * v kazdom Listenery.
     * @param e ActionEvent ktory vola Listener.
     */
    public void actionPerformed(ActionEvent e) {         
        setRunParams(e);                
                
    }
    
    /**
     * Metoda ktora vykona ActionEvent z awt packagu. Zatial neimplementovane
     * kedze pouzivame nase vlastne ActionEventy.
     * @param e ActionEvent ktory vykonava listener.
     */
    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
         
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora vrati AbstractMenu z parametre <b>src</b>. Ked je src typu
     * AbstractMenu tak je to jasne. Pri parametri typu SwingComponent vratime
     * originalne menu ktore je typu AbstractMenu. Ine typy objektov nie su povolene.
     * @param src Objekt z ktoreho ziskavame menu
     * @return AbstractMenu ziskany zo src objektu.
     */
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
        
    /**
     * Metoda ktora vrati ci si mame listener zapamatat v triede ListenerFactory
     * pre rychlejsi pristup ked listener volame viac krat.
     * @return True/false ci si mame listener pamatat.
     */
    public boolean isMemorizable() {
        if (action == null) return false;
        return action.isMemorizable();
    }
        
    /**
     * Abstraktna metoda ktora ma vratit meno pre Listener. Kazdy podedeny listener
     * musi tuto metodu implementovat pre spravne fungovanie listenerov, kedze 
     * hu vyuzivame pri rozhodovani ktory listener mame volat.
     * @return Text s menom listeneru
     */
    public abstract String getName();
    
    /**
     * Metoda ktora vrati Listener ako text
     * @return Text o info listenera.
     */
    @Override
    public String toString() {
        return "Parsed Operations =" + parsedObjects;        
    }           
    // </editor-fold>
    
}
