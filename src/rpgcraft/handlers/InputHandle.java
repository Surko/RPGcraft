/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.handlers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Trieda InputHandle implementujuca KeyListener, ma reagovat na vstup z klavesnice.
 * Vsetky vstupy su riadene z tejto triedy. Vsetky mozne klavesy su definovane v enume
 * Keys. V enume DefinedKeys su nadefinovane klavesy ze maju meno ktore priblizne urcuje co 
 * sa bude pri stlaceni diat. Trieda taktiez obsahuje listy clickedKeys (stlacene klavesove kody) a
 * running keys (drzane klavesove kody). Kedze trieda implementuje od KeyListener
 * tak musi obsahovat metody reagujuce na vstup ako keyType, keyPressed, ... v ktorych nastavujeme
 * stlacene klavesy do listov clickedKeys a runningKeys.
 * <p>
 * HashMapa s menom charKeys v sebe obsahuje dvojice Integer, Character => pre kazdy klavesovy kod
 * existuje char znak. Tento princip vyuzivame pri pisani do vstupnych boxov.
 * </p>
 * @author Kirrie
 */
public class InputHandle implements KeyListener { 
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    /**
     * Hashmapa s dvojicou klavesovy kod a char znak
     */
    private static HashMap<Integer, Character> charKeys = new HashMap();
    
    /**
     * Ovladanie hry definovane triedou
     */
    private static InputHandle input;    
    
    /**
     * Stlacene a drzane klavesy
     */
    public ArrayList<Integer> clickedKeys,runningKeys;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Pomocne triedy a enumy ">
    
    /**
     * Trieda kluc ktora v sebe obsahuje definiciu kluca
     * s kodom ktory mu zodpoveda. Vyuzite pri enume DefineKey.
     */
    public static class Key {
     
        private int code;
        
        public Key(int code) {            
            this.code = code;
        }
       
        public int getKeyCode() {
            return code;
        }        
                                
    }
    
    /**
     * Objekt s definovanymi klucmi. Kluce maju rozumenejsie nazvy, z ktorych
     * mozno usudit co bude kluc robit. Do konstruktoru enumu pridavame instanciu Key s kodom 
     * ktory mu zodpoveda. Viacero takychto enumovych klucov moze mat rovnaky kod.
     */
    public enum DefinedKey {
        ENTER(new Key(KeyEvent.VK_ENTER)),
        UP(new Key(KeyEvent.VK_W)),
        DOWN(new Key(KeyEvent.VK_S)),
        LEFT( new Key(KeyEvent.VK_A)),
        RIGHT(new Key(KeyEvent.VK_D)),
        ESCAPE(new Key(KeyEvent.VK_ESCAPE)),
        ATTACK(new Key(KeyEvent.VK_SPACE)),
        TILEATTACKVER(new Key(KeyEvent.VK_CONTROL)),
        TILEATTACKHOR(new Key(KeyEvent.VK_L)),
        INVENTORY(new Key(KeyEvent.VK_I)),
        STAT( new Key(KeyEvent.VK_F3)),
        DEBUG( new Key(KeyEvent.VK_F5)),
        PARTICLES( new Key(KeyEvent.VK_F7)),
        SCALING( new Key(KeyEvent.VK_F12)),
        DEFENSE( new Key(KeyEvent.VK_CONTROL)),
        PRINT( new Key(KeyEvent.VK_PRINTSCREEN)),
        LEVELUP( new Key(KeyEvent.VK_PAGE_UP)),
        LEVELDOWN( new Key(KeyEvent.VK_PAGE_DOWN)),
        CRAFTING( new Key(KeyEvent.VK_C)),
        QUEST( new Key(KeyEvent.VK_Q)),
        JUMP(new Key(KeyEvent.VK_J)),
        CHARACTER(new Key(KeyEvent.VK_C)),
        ACTIVE( new Key(KeyEvent.VK_E)),
        LIGHTING( new Key(KeyEvent.VK_F6));                
        
        private Key key;
        
        private DefinedKey(Key key) {
            this.key = key;
        }
        
        /**
         * Metoda nastavi klavesovy kod pre kluc
         * @param key Kod pre kluc
         */
        public void setKey(int key) {
            this.key = new Key(key);
        }
        
        /**
         * Metoda nastavi novy Key objekt do definovaneho kluca.
         * @param key Key ako objekt
         */
        public void setKey(Key key) {
            this.key = key;
        }
        
        /**
         * Metoda vrati Key objekt v ktorom sa nachadza kod ktory mu zodpoveda
         * @return Key objekt
         */
        public Key getKey() {
            return key;
        }
        
        /**
         * Metoda vrati klavesnicovy kod zodpovedajuci Key objektu priradenemu
         * k tomuto enumu.
         * @return Klavesnicovy kod
         */
        public int getKeyCode() {
            return key.code;
        }
    }                
       
    
    /**
     * Enum s definovanymi klavesami ktore sa daju stlacit. V konstruktore vzdy obsahuju
     * co za kod a aky znak zodpoveda klavese
     */
    public enum Keys {
        VK_W(KeyEvent.VK_W, 'w'),
        VK_A(KeyEvent.VK_A, 'a'),        
        VK_S(KeyEvent.VK_S, 's'),
        VK_D(KeyEvent.VK_D, 'd'),
        VK_B(KeyEvent.VK_B, 'b'),
        VK_C(KeyEvent.VK_C, 'c'),        
        VK_F(KeyEvent.VK_F, 'f'),
        VK_E(KeyEvent.VK_E, 'e'),
        VK_I(KeyEvent.VK_I, 'i'),
        VK_G(KeyEvent.VK_G, 'g'),
        VK_H(KeyEvent.VK_H, 'h'),        
        VK_J(KeyEvent.VK_J, 'j'),
        VK_K(KeyEvent.VK_K, 'k'),
        VK_L(KeyEvent.VK_L, 'l'),
        VK_M(KeyEvent.VK_M, 'm'),        
        VK_N(KeyEvent.VK_N, 'n'),
        VK_O(KeyEvent.VK_O, 'o'),
        VK_P(KeyEvent.VK_P, 'p'),
        VK_R(KeyEvent.VK_R, 'r'),        
        VK_T(KeyEvent.VK_T, 't'),
        VK_U(KeyEvent.VK_U, 'u'),
        VK_V(KeyEvent.VK_V, 'v'),      
        VK_X(KeyEvent.VK_X, 'x'),
        VK_Y(KeyEvent.VK_Y, 'y'),
        VK_Z(KeyEvent.VK_Z, 'z'),
        VK_ESCAPE(KeyEvent.VK_ESCAPE, '\0'),
        VK_CONTROL(KeyEvent.VK_CONTROL, '\0'),        
        VK_ALT(KeyEvent.VK_ALT, '\0'),
        VK_SPACE(KeyEvent.VK_SPACE, ' '),
        VK_BACK_SPACE(KeyEvent.VK_BACK_SPACE, '\0'),
        VK_SHIFT(KeyEvent.VK_SHIFT, '\0'),
        VK_ENTER(KeyEvent.VK_ENTER, '\n'),
        VK_PAGE_UP(KeyEvent.VK_PAGE_UP, '\0'),
        VK_PAGE_DOWN(KeyEvent.VK_PAGE_DOWN, '\0'),
        VK_DELETE(KeyEvent.VK_DELETE, '\0'),
        VK_F3(KeyEvent.VK_F3, '\0'),        
        VK_Q(KeyEvent.VK_Q, 'q'),
        VK_1(KeyEvent.VK_1, '1'),
        VK_2(KeyEvent.VK_2, '2'),        
        VK_3(KeyEvent.VK_3, '3'),
        VK_4(KeyEvent.VK_4, '4'),
        VK_5(KeyEvent.VK_5, '5'),
        VK_6(KeyEvent.VK_6, '6'),        
        VK_7(KeyEvent.VK_7, '7'),
        VK_8(KeyEvent.VK_8, '8'),
        VK_9(KeyEvent.VK_9, '9'),
        VK_0(KeyEvent.VK_0, '0');
        
        
        private final int keyCode;        
        
        private Keys(int keyCode, char keyChar) {
            this.keyCode = keyCode;    
            charKeys.put(keyCode, keyChar);
        }
        
        public final int getCode() {
            return keyCode;
        }
         
    }
    
    // </editor-fold>
      
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Privatny konstruktor na zostavenie vstupu. Vytvarame listy clickedKeys a runningKeys
     * aby sme donich mohli pri stlaceni klaves pridavat kody tychto klaves.
     */
    private InputHandle() {
        clickedKeys = new ArrayList<>();
        runningKeys = new ArrayList<>();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery " >
    /**
     * Metoda vrati char znak z definovanej hashmapy  charKeys. Pri neexistenci takeho 
     * znaku vratime ziadny znak.
     * @param keyCode Klavesovy kod ktoreho znak chcem
     * @return Znak pre klavesu.
     */
    public char getChar(int keyCode) {
        Character ch = charKeys.get(keyCode);
        if (ch == null) {
            return '\0';
        }
        
        return charKeys.get(keyCode);
        
    }        
    // </editor-fold>
                           
    // <editor-fold defaultstate="collapsed" desc=" Klavesove metody ">
    
    /**
     * Metoda ktora uvolni vsetky kluce z clickedKeys.
     */
    public void freeKeys() {
        clickedKeys.clear();
    }
    
    /**
     * Metoda ktora podla parametru <b>state</b> rozhoduje ci do listu clickedKeys
     * pridavame klavesovy kod zadany parametrom <b>code</b>
     * @param code Kod ktory pridavame/odoberame do/z listu clickedKeys.
     * @param state Stav ci pridavame alebo odoberame
     */
    private void clickEvent(int code, boolean state) {
        if (state) {
            if (!clickedKeys.contains(code)) {
                clickedKeys.add(code);
            }
        } else {
            clickedKeys.remove(code);
        }
    }
    
    
    /**
     * Prazdna metoda reagujuca pri napisani niecoho na klavensnici.
     * @param e KeyEvent podla ktoreho zistujeme co bolo stlacene.
     */
    @Override
    public void keyTyped(KeyEvent e) {
        
    }  
    
    /**
     * Metoda ktora podla parametru <b>state</b> rozhoduje ci do listu runningKeys
     * pridavame klavesovy kod zadany parametrom <b>code</b>
     * @param code Kod ktory pridavame/odoberame do/z listu runningKeys.
     * @param state Stav ci pridavame alebo odoberame
     */
    private void runEvent(int code, boolean state) {
        //System.out.println(runningKeys.size());
        if (state) {
            if (!runningKeys.contains(code)) {
                runningKeys.add(code);
            }
        } else {
            runningKeys.remove((Integer)code);
        }
    }
    
    /**
     * Metoda ktora reaguje na drzanie klavesy ktorej kod najdeme v parametri <b>e</b>.
     * Pri drzani pridame kod do runningKeys.
     * @param e KeyEvent z ktoreho urcujeme co bolo stlacene
     */
    @Override
    public void keyPressed(KeyEvent e) {        
        runEvent(e.getKeyCode(), true);
    }

    /**
     * Metoda ktora reaguje na upustenie klavesy ktorej kod najdeme v parametri <b>e</b>.
     * Pri pusteni klavesy odobereme kod z runningKeys, no pridame ho do clickedKeys.
     * @param e KeyEvent z ktoreho urcujeme co bolo uvolnene
     */
    @Override
    public void keyReleased(KeyEvent e) {
        runEvent(e.getKeyCode(), false);
        clickEvent(e.getKeyCode(), true);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Staticke metody ">
        
    /**
     * Metoda ktora vrati instanciu vstupu (singleton).
     * @return InputHandle objekt
     */
    public static InputHandle getInstance() {
        if (input == null) {
            input = new InputHandle();
        }
        return input;
    }
    
    
    // </editor-fold>
}
