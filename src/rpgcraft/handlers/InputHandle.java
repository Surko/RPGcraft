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
        VK_ESCAPE(KeyEvent.VK_ESCAPE, '\0'),
        VK_CONTROL(KeyEvent.VK_CONTROL, '\0'),        
        VK_ALT(KeyEvent.VK_ALT, '\0'),
        VK_SPACE(KeyEvent.VK_SPACE, ' '),
        VK_BACK_SPACE(KeyEvent.VK_BACK_SPACE, '\0'),
        VK_SHIFT(KeyEvent.VK_SHIFT, '\0'),
        VK_ENTER(KeyEvent.VK_ENTER, '\n');
        
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
    public static Key inventory = new Key(KeyEvent.VK_I);
    public static Key q = new Key(KeyEvent.VK_Q);
    public static Key x = new Key(KeyEvent.VK_X);
    public static Key stat = new Key(KeyEvent.VK_F3);
    public static Key debug = new Key(KeyEvent.VK_F5);
    public static Key particles = new Key(KeyEvent.VK_F4);
    public static Key scaling = new Key(KeyEvent.VK_F12);
    public static Key defense = new Key(KeyEvent.VK_CONTROL);
    public static Key print = new Key(KeyEvent.VK_PRINTSCREEN);

    
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
