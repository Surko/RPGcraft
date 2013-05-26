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
 *
 * @author Kirrie
 */
public class InputHandle implements KeyListener {
    private static ArrayList<Key> regKeys = new ArrayList<>();
    private static HashMap<Integer, Character> charKeys = new HashMap();
    
    // Ovladanie hry definovane triedou
    private static InputHandle input;    
    
    public ArrayList<Integer> clickedKeys;
    public ArrayList<Integer> runningKeys;
    
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
    
    public char getChar(int keyCode) {
        Character ch = charKeys.get(keyCode);
        if (ch == null) {
            return '\0';
        }
        
        return charKeys.get(keyCode);
        
    }
    
    public static class Key {
     
        private int code;
        
        public Key(int code) {            
            this.code = code;
        }
       
        public int getKeyCode() {
            return code;
        }
        
        public void update() {
            
        }
                                
    }
    
    private InputHandle() {
        clickedKeys = new ArrayList<>();
        runningKeys = new ArrayList<>();
    }
    
    public static InputHandle getInstance() {
        if (input == null) {
            input = new InputHandle();
        }
        return input;
    }
    
        
    public static Key enter = new Key(KeyEvent.VK_ENTER);
    public static Key up = new Key(KeyEvent.VK_W);
    public static Key down = new Key(KeyEvent.VK_S);
    public static Key left = new Key(KeyEvent.VK_A);
    public static Key right = new Key(KeyEvent.VK_D);
    public static Key escape = new Key(KeyEvent.VK_ESCAPE);
    public static Key attack = new Key(KeyEvent.VK_SPACE);
    public static Key tileattack = new Key(KeyEvent.VK_SHIFT);
    public static Key inventory = new Key(KeyEvent.VK_I);
    public static Key q = new Key(KeyEvent.VK_Q);
    public static Key x = new Key(KeyEvent.VK_X);
    public static Key stat = new Key(KeyEvent.VK_F3);
    public static Key debug = new Key(KeyEvent.VK_F5);
    public static Key particles = new Key(KeyEvent.VK_F4);
    public static Key scaling = new Key(KeyEvent.VK_F12);
    public static Key defense = new Key(KeyEvent.VK_CONTROL);
    public static Key print = new Key(KeyEvent.VK_PRINTSCREEN);
    public static Key levelUp = new Key(KeyEvent.VK_PAGE_UP);
    public static Key levelDown = new Key(KeyEvent.VK_PAGE_DOWN);
    public static Key crafting = new Key(KeyEvent.VK_C);
    public static Key quest = new Key(KeyEvent.VK_Q);
    public static Key jump = new Key(KeyEvent.VK_J);

    
    @Override
    public void keyTyped(KeyEvent e) {
        
    }
    
    public void keyUpdates() {
        for (Key k : regKeys) {
            k.update();
        }
    }
    
    public void freeKeys() {
        clickedKeys.clear();
    }
    
    private void clickEvent(int code, boolean state) {
        if (state) {
            if (!clickedKeys.contains(code)) {
                clickedKeys.add(code);
            }
        } else {
            clickedKeys.remove(code);
        }
    }
    
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
    
    @Override
    public void keyPressed(KeyEvent e) {        
        runEvent(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        runEvent(e.getKeyCode(), false);
        clickEvent(e.getKeyCode(), true);
    }
}
