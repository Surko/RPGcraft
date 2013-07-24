/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.listeners;

import java.util.HashMap;

/**
 * Trieda ActionEvent ktora dedi od awt-ckoveho ActionEventu. Instancia triedy je vytvarana
 * z nasej hry pri roznych odpovediach na eventy. Narozdiel od ActionEvent z awt moze drzat
 * ake vlakno drzi nejaky ActionEvent, aku akciu vykonavame s tymto eventom a najdolezitejsie
 * pre vykonavanie v listeneroch su moznosti navratu a nastavenie navratovych hodnot.
 * Na vytvorenie instancie mame konstruktor ktoreho parametre id a command su nevyuzite
 * takze pri vytvarani ActionEventu staci mat v tychto parametroch defaultne/zakladne hodnoty.
 */
public class ActionEvent extends java.awt.event.ActionEvent{
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">

    private static HashMap<Long, ActionEvent> threadActionEvents = new HashMap<>();
    
    /**
     * Pocet klikov
     */
    private int clicks;
    /**
     * Parameter predany
     */
    private Object param;
    /**
     * Akcia na vykonanie
     */
    private Action action;
    /**
     * Navratova hodnota
     */
    private Object returnValue;
    /**
     * O kolko preskocime pri kombinovanom listenery
     */
    private int jumpValue = 1;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Konstruktor ktory vytvori novu instanciu ActionEventu. Na inicializovanie volame podedeny
     * konstruktor pomocou super, ktora nastavi premenne podla parametrov source, id, command.
     * Pocet klikov a parameter sa urci az pri navrate z rodicovskeho konstruktora.
     * @param source Zdroj kde bol vytvoreny ActionEvent
     * @param id Id eventu (nepouzite)
     * @param clicks Pocet klikov ktore vykonali event
     * @param command Prikaz ktory bol poslany (nepouzite)
     * @param param Parameter predavany do vykonavaca akcii (napr Cursor ci iny objekt)
     */
    public ActionEvent(Object source, int id, int clicks, String command, Object param) {
        super(source, id, command);
        this.clicks = clicks;        
        this.param = param;        
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    /**
     * Metoda ktora nastavi navratovu hodnotu v tomto evente.
     * @param value Objekt ktory sa nastavi ako navratova hodnota
     */
    public void setReturnValue(Object value) {
        this.returnValue = value;
    }
    
    /**
     * Metoda ktora nastavi akciu ktora je vykonavana s tymto eventom.
     * @param action Akcia ktora je vykonavana s eventom
     */
    public void setAction(Action action) {
        this.action = action;
    }
    
    /**
     * Metoda ktora nastavi kolko sa preskoci pri kombinovanom listenery.
     * @param jumpValue Hodnota preskocenia.
     */
    public void setJumpValue(int jumpValue) {
        this.jumpValue = jumpValue;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora vrati kolko skaceme v kombinovanom listenery.
     * @return Hodnota preskocenia
     */
    public int getJumpValue() {        
        return jumpValue; 
    }
    
    /**
     * Metoda ktora vrati pocet klikov s ktorymi bola vytvoreny event.
     * @return Pocet klikov
     */
    public int getClicks() {
        return clicks;
    }    
    
    /**
     * Metoda ktora vrati akciu ktora je vykonavana s tymto eventom.
     * @return Akciu vykonavanu s eventom.
     */
    public Action getAction() {
        return action;
    }
    
    /**
     * Metoda ktora vrati parameter s ktorym bol event vytvoreny. Moze to by lubovolny
     * objekt s ktorym dokaze neskor pracovat listener.
     * @return Parameter predavany cez Event do akcii.
     */
    public Object getParam() {
        return param;
    }
    
    /**
     * Metoda ktora vrati navratovu hodnotu
     * @return Navratova hodnota (objekt)
     */
    public Object getReturnValue() {
        return returnValue;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Staticke metody ">
    /**
     * Metoda ktora nastavi do hashmapy threadActionEvents id vlakna ktore
     * akurat drzi event zadany parametrom <b>event</b>
     * @param id Id vlakna
     * @param event Event ktory vykonava vlakno s id
     */
    public static void setScriptActionEvent(long id, ActionEvent event) {
        threadActionEvents.put(id, event);
    }
    
    /**
     * Metoda ktora vrati velkost threadActionEvents => kolko Threadov drzi nejaky
     * ActionEvent.
     * @return Pocet registrovanych ActionEventov ktore sa vykonavaju
     */
    public static int getScriptActionEvents() {
        return threadActionEvents.size();
    }
    
    /**
     * Metoda ktora vrati ActionEvent ktory vykonava vlakno s id rovnym <b>id</b>.
     * @param id Id vlakna ktore drzi event
     * @return ActionEvent ktory drzi vlakno s id
     */
    public static ActionEvent getScriptActionEvent(long id) {
        return threadActionEvents.get(id);
    }
    
    /**
     * Metoda ktora vymaze z hashmapy threadActionEvents dvojicu s klucom danym parametrom
     * <b>id</b> => Vlakno uz dlhsie nedrzi Event.
     * @param id Id vlakna ktoreho ActionEvent vymazavame.
     */
    public static void removeActionEvent(long id) {
        threadActionEvents.remove(id);
    }
    // </editor-fold>
}
