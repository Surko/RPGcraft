/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.listeners;

import rpgcraft.handlers.InputHandle;
import rpgcraft.handlers.InputHandle.Keys;
import rpgcraft.resource.UiResource;

/**
 * Trieda definovana na poskytnutie zakladnej informacie o akcii. Tuto triedu pouziva hlavne 
 * UiResource, ktora do listu uklada viacero instancii tejto triedy. Dalej tieto vytvorene objekty
 * vyuzivaju instancie gui komponent, ktore testuju podmienky ako je pocet klikov, typ akcie
 * a podla toho rozhodne ci nejaky event vyhovuje akcii a vykona hu podla dat ktore 
 * su ulozene v premennej </b>action</b>. Tymto padom je tato trieda len datova polozka, ktora 
 * sa nepouziva na nic dalsie.
 * @author kirrie
 */
public class Action {
                
        /**
         * Vypis roznych typov pre akciu. Zatial dovoluje iba klavesnicove a mys akcie.
         */         
        public enum Type {
            MOUSE,
            KEY
        }
    
        // Typ kliknutia (na list elementov, atd...)
        UiResource.ClickType clickType;
        // Typ akcie (mys, klavesnica)
        Type type = Type.MOUSE;
        // Textove pole s akciou co sa ma vykonat. Treba nejaky parser, ktory vytvori akciu.
        String action;
        // Pocet klikov aby sa vykonala akcia.
        int clicks = 0;
        // Transparentna akcia => akcia preteka komponentami az do rodicovskeho komponentu.
        boolean trans = false;
        // Klavesa ktora musi byt stlacena na spustenie
        int keyCode;
        
        /**
         * Kopirovaci konstruktor pre akciu.
         * @param action Akcia od ktorej kopirujeme data.
         */
        public Action(Action action) {
            this.clickType = action.clickType;
            this.clicks = action.clicks;
            this.trans = action.trans;
            this.type = action.type;
            this.action = action.action;
            this.keyCode = keyCode;                    
        }
           
        /**
         * DEFAULT Constructor
         */
        public Action() {}
        
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
        public void setClickType(UiResource.ClickType clickType) {
            this.clickType = clickType;
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
            this.action = action;
        }
                   
        public void setCode(String code) {
            this.keyCode = Keys.valueOf(code).getCode();
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
            return action;
        }
        
        /**
         * Metoda vracia pocet klikov na prevedenie akcie.
         * @return 
         */
        public int getClicks() {
            return clicks;
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
        public UiResource.ClickType getClickType() {
            return clickType;
        }
        
        /**
         * Metoda vrati aka klavesa muti byt stlacena
         * @return Klavesa na spustenie akcie
         */
        public int getKey() {
            return keyCode;
        }
}
