/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.listeners;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.luaj.vm2.lib.jse.JsePlatform;
import rpgcraft.handlers.InputHandle;
import rpgcraft.handlers.InputHandle.Keys;
import rpgcraft.resource.UiResource;
import rpgcraft.utils.ScriptUtils;

/**
 * Trieda definovana na poskytnutie zakladnej informacie o akcii. Tuto triedu pouziva hlavne 
 * UiResource, ktora do listu uklada viacero instancii tejto triedy. Dalej tieto vytvorene objekty
 * vyuzivaju instancie gui komponent, ktore testuju podmienky ako je pocet klikov, typ akcie
 * a podla toho rozhodne ci nejaky event vyhovuje akcii a vykona hu podla dat ktore 
 * su ulozene v premennej </b>action</b>. Tymto padom je tato trieda len datova polozka, ktora 
 * sa nepouziva na nic dalsie.
 * @author kirrie
 */
public class Action implements Runnable {
                   
        /**
         * Vypis roznych typov pre akciu. Zatial dovoluje iba klavesnicove a mys akcie.
         */         
        public enum Type {
            // Mys akcia, stlacenie pohyb mysi
            MOUSE,
            // Klaves. akcia, stlacenie opustenie klavesy
            KEY,
            // Event. Neprestajna akcia prebiehajuca vselikedy. Ked je zavolana, alebo kazdym tiknutim.
            // Pre takyto typ akcie je definovany lAction aby nedochadzalo k vytvaraniu novych listenerov.            
            // 
            EVENT
        }
    
        // <editor-fold defaultstate="collapsed" desc=" Premenne ">
        // Typ kliknutia (na list elementov, atd...)
        Object eventType;
        //            
        // Event podla ktoreho sa vykona akcia
        ActionEvent event;
        // Typ akcie (mys, klavesnica)
        Type type = Type.MOUSE;
        // Textove pole s akciou co sa ma vykonat. Treba nejaky parser, ktory vytvori akciu.
        String sAction;
        // Pocet klikov aby sa vykonala akcia.
        int clicks = 0;
        // Ci je akcia lua skript
        boolean lua;        
        // Ci je akcia necinna
        boolean done;
        // Ci sa akcia akurat vykonava
        volatile boolean active;
        // Terajsi pocet tiknuti od zaciatku, pocet tiknuti na prespanie.
        int currentTick,sleepingTicks;
        // Cas kedy sa akcia uspala, terajsi cas, a kolko sekund ma akcia spat
        long markTime, currentTime, sleepingTime;
        // Ulozeny listener v akcii. Rychlejsi pristup k vykonaniu operacie kedze nemusi byt parsovane sAction aby sme dostali listener.
        Listener lAction;
        // Transparentna akcia => akcia preteka komponentami az do rodicovskeho komponentu, memorizable akcia => akcia je zapamatena v listeneroch ked to je listener
        boolean trans = false,memorizable = false;
        //
        // Klavesa ktora musi byt stlacena na spustenie
        int keyCode;
        
        // </editor-fold>
        
        /**
         * Kopirovaci konstruktor pre akciu.
         * @param action Akcia od ktorej kopirujeme data.
         */
        public Action(Action action) {
            this.eventType = action.eventType;
            this.clicks = action.clicks;
            this.trans = action.trans;
            this.memorizable = action.memorizable;
            this.lAction = action.lAction;
            this.type = action.type;
            this.sAction = action.sAction;
            this.lua = action.lua;
            this.keyCode = action.keyCode;                    
        }
           
        /**
         * DEFAULT Constructor
         */
        public Action() {}
        /**
         * Metoda ktora nastavuje akciu na lua akciu.
         * @param lua True/false ci je akcia LUA skript.
         */
        public void setLua(boolean lua) {
            this.lua = lua;
        }
        
        /**
         * Metoda nastavi typ akcie podla parametru typ.
         * @param type Typ akcie (MOUSE,KEY)
         */
        public void setType(Type type) {            
            this.type = type;            
        }
        
        /**
         * Metoda nastavi transparentnost akcie. Vysvetlenie pri definicii premennej.
         * @param state true/false podla toho ci je alebo nie je transparentna.
         */
        public void setActionTransparency(boolean state) {
            this.trans = state;
        }
        
        /**
         * Metoda nastavi typ kliknutia. Zatial je dolezite len typ onListElement.
         * @param clickType Typ kliknutia
         */
        public void setClickType(Object clickType) {
            this.eventType = clickType;
        }
        
        /**
         * Metoda ktora nastavi pocet kliknuti aby sa akcia vykonala.
         * @param clicks Pocet klikov na vykonanie.
         */
        public void setClicks(int clicks) {
            this.clicks = clicks;
        }
        
        /**
         * Metoda ktora nastavi v textovom retazci co akcia pri vyvolani by mala vykonat.
         * @param action Text s akciou na vykonanie.
         */
        public void setAction(String action) {
            this.sAction = action;
        }
                   
        /**
         * Nastavi aby bola akcia zapamatena medzi listenermi.
         * @param state Stav ci je alebo nie je akcia zapamatovatelna
         */
        public void setMemorizable(boolean state) {
            this.memorizable = state;
        }
        
        /**
         * Metoda ktora nastavi klavesovy kod pri ktorom sa akcia vykona.
         * @param code Kod na vykonanie akcie
         */
        public void setCode(String code) {
            this.keyCode = Keys.valueOf(code).getCode();
        }
        
        /**
         * Metoda ktora nastavi cinnost/necinnost akcie.
         * @param done True/false ci bude/nebude akcia necinna
         */
        public void setDone(boolean done) {
            this.done = done;
        }
        
        /**
         * Metoda ktora nastavi ActionEvent podla ktoreho sa vykonava akcia.
         * Metoda hra dolezitu sucast iba ked vyuzivame multi thread vykonavanie akcii.
         * @param event 
         */
        public void setActionEvent(ActionEvent event) {
            this.event = event;
        }
        
        /**
         * Metoda ktora nastavi akciu na uspanie. Kolko tikov bude akcia uspana
         * rozhoduje parameter ticks
         * @param ticks Pocet tikov ktore bude akcia uspana
         */
        public void setSleepTicks(int ticks) {
            this.sleepingTicks = ticks;
        }
        
        /**
         * Metoda ktora nastavi akciu ze je v procese vykonavania. Nie je vhodne volat
         * rovnaku akciu ked akurat tato prebieha.
         * @param active True/False ci ma byt akcia aktivna.
         */
        public void setActive(boolean active) {
            this.active = active;
        }
        
        /**
         * Metoda ktora nastavi akciu na uspanie. Ako dlho v sekundach bude akcia uspana
         * rozhoduje parameter time
         * @param time Cas ako dlho bude akcia uspana
         */
        public void setSleepTime(int time) {
            this.sleepingTime = time;
        }
        
        /**
         * Metoda ktora zresetuje akciu na dalsie pouzitie => nastavi done na false
         * cim dovoli vykonanie metody perform. Listener lAction zostava taky isty kedze typ/command akcie
         * zostava stale rovnaky a bolo by ho zbytocne znova vytvarat. Na prenastavenie tohoto typu
         * staci pouzit makeListener, ked je to potrebne.
         */
        public void resetAction() {
            this.currentTime = this.sleepingTime = this.currentTick = this.sleepingTicks = 0;
            this.done = false;
        }
        
        /**
         * Metoda ktora vrati ci je akcia zapamatovatelna.
         * @return 
         */
        public boolean isMemorizable() {
            return memorizable;
        }
        
        /**
         * Metoda vrati typ akcie.
         * @return Typ akcie.
         */
        public Type getType() {
            return type;
        }
        
        /**
         * Metoda ktora navracia co akcia bude vykonavat.
         * @return Akcia samotna
         */
        public String getAction() {
            return sAction;
        }
        
        /**
         * Metoda vracia pocet klikov na prevedenie akcie.
         * @return 
         */
        public int getClicks() {
            return clicks;
        }
        
        /**
         * Metoda ktora zisti ci sa akurat akcia vykonava => nie je vhodne volat akciu ked sa prave vykonava.
         * 
         * @return True/False ci je akcia aktivna
         */
        public boolean isActive() {
            return active;
        }
        
        /**
         * Metoda ktora zisti ci je akcia necinna
         * @return true/false ci je akcia necinna
         */
        public boolean isDone() {
            return done;
        }
        /**
         * Metoda ktora vrati ci je akcia transparentna.
         * @return 
         */
        public boolean isTransparent() {
            return trans;
        }
        
        /**
         * Metoda ktora vrati aky typ kliknutia vyvola akciu.
         * @return Typ kliknutia na prevedenie.
         */
        public Object getClickType() {
            return eventType;
        }
        
        /**
         * Metoda vrati aka klavesa muti byt stlacena
         * @return Klavesa na spustenie akcie
         */
        public int getKey() {
            return keyCode;
        }                
        
        /**
         * Metoda vrati listener/akciu ktora sa ma vykonat.
         * @return listener ktory sa vykonava.
         */
        public Listener getListener() {
            return lAction;
        }
        
        /**
         * Metoda ktora vrati true/false podla toho ci je akcia lua skript.
         * @return True/false podla typu skriptu.
         */
        public boolean isLuaAction() {
            return lua;
        }
        
        /**
         * Metoda makeListener vytvori listener priamo v akcii. Vhodne ked je volana akcia
         * viacero krat za sebou. Obchadza perform akciu ktora berie do uvahu aj sleep funkciu.
         * S metodami getListener vhodne nahradzuje metodu perform.          
         */
        public void makeListener() {
            this.lAction = ListenerFactory.getListener(sAction, memorizable);
        }
        
        /**
         * Metoda ktora ma za ulohu vykonat akciu podla ActionEventu zadaneho parametrom <b>e</b>.
         * Metoda rozhoduje ci je akcia lua skript alebo nie. Podla tohoto rozhodnutia zavola
         * taky typ vyvolania skriptu, aky k tomu zodpoveda.
         * 
         * @param e ActionEvent podla ktoreho sa vykona akcia.
         */
        public void perform(ActionEvent e) {
            if (lua) {
                performLuaAction(e);
            } else {
                performAction(e);
            }
        }
        
        /**
         * Metoda ktora vykona lua skript. Lua skript sam o sebe je vykonavany na samotnom Threade,
         * preto nie je nutne volat vykonavanie lua skriptu skrz DataUtils, ktory by vytvaral dalsi Thread v ExecutorService.
         * @param e ActionEvent podla ktoreho sa vykona akcia.
         */
        public void performLuaAction(ActionEvent e) {
            try {
                ScriptUtils.loadScript(sAction, e).call();
            } catch (IOException ex) {
                Logger.getLogger(Action.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        /**
         * Metoda ktora vykona akciu ktora je zadana ako Listener iba v tom pripade ked bola memorizable alebo
         * uz bola initializovana.
         * Ked nie tak vytvori nanovo novy listener. V obidvoch pripadoch vykona listener.
         * Tato metoda vytvori listener a uchovava ho v akcii.
         */
        public void performAction(ActionEvent e) {           
            if (!done) {
                // Ked ma akcia byt uspana tak nic nerobi
                if (sleepingTicks > 0) {
                    if (currentTick >= sleepingTicks) {
                        currentTick = sleepingTicks = 0;                    
                    } else {
                        currentTick++;
                        return;
                    }
                }

                if (sleepingTime > 0) {
                    if (currentTime - markTime >= sleepingTime) {
                        currentTime = 0;
                        markTime = 0;
                        sleepingTime = 0;
                    } else {
                        currentTime = System.currentTimeMillis();
                        return;
                    }
                }

                if (e.getAction() == null) {
                    e.setAction(this);
                }
                                
                
                if (lAction != null) {
                    lAction.actionPerformed(e);
                } else {
                    lAction = ListenerFactory.getListener(sAction, memorizable);
                    if (lAction != null) {
                        lAction.actionPerformed(e);
                    }                                                                 
                } 
            }
        }
        
        /**
         * Metoda run implementovana z interface Runnable, ktora je volana pri starte Threadu.
         * Podla toho ci je event prazdny zavola metodu perform. Event je udalost podla ktorej sa vykonava akcia a kedze
         * metoda run je bez parametrov musi byt predavana vnutro objektovo.
         */
        @Override
        public void run() {
            if (event != null) {
                //System.out.println(Thread.currentThread());
                //System.out.println(this.sAction);
                perform(event);
                //event = null;                
                active = false;
            }
        }
        
        /**
         * Metoda clone vytvori novu instanciu objektu Action do ktoreho sa okopiruju 
         * vsetky vlastnosti tejto akcie, okrem takych ako su doba uspania, ... (lokalne premenne)
         * @return 
         */
        @Override
        public Action clone() {
            return new Action(this);           
        }
}
