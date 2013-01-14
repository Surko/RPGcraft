/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.handlers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

/**
 *
 * @author Kirrie
 */
public class InputHandle implements KeyListener {
    private static ArrayList<Key> regKeys = new ArrayList();
    
    public class Key {
        public boolean on = false, click = false;        
        public Key() {
            regKeys.add(this);
        }
        
        public void update() {
            
        }
        
                        
    }
    
    public Key enter = new Key();
    public Key up = new Key();
    public Key down = new Key();
    public Key left = new Key();
    public Key right = new Key();
    public Key escape = new Key();
    public Key attack = new Key();
    public Key inventory = new Key();
    public Key q = new Key();
    public Key x = new Key();
    public Key stat = new Key();
    public Key debug = new Key();
    public Key particles = new Key();
    public Key scaling = new Key();
    public Key defense = new Key();
    public Key print = new Key();
    
    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    public void keyUpdates() {
        for (Key k : regKeys) {
            k.update();
        }
    }
    
    public void freeKeys() {
        for (Key k : regKeys) {
            k.click = false;
        }
    }
    
    private void clickEvent(int code, boolean state) {
        switch (code) {
            case KeyEvent.VK_I : inventory.click = state;
                break;
            case KeyEvent.VK_W : up.click = state;
                break;            
            case KeyEvent.VK_S : down.click = state;
                break;
            case KeyEvent.VK_A : left.click = state;
                break;
            case KeyEvent.VK_D : right.click = state;
                break;            
            case KeyEvent.VK_F3 : stat.click = state;    
                break;    
            case KeyEvent.VK_F4 : particles.click = state;
                break;
            case KeyEvent.VK_F5 : debug.click = state;
                break;
            case KeyEvent.VK_F12 : scaling.click = state;
                break;
            case KeyEvent.VK_SPACE : attack.click = state;
                break;
            case KeyEvent.VK_ESCAPE : escape.click = state;
                break;    
            case KeyEvent.VK_PRINTSCREEN : print.click = state;
                break;
        }
    }
    
    private void runEvent(int code, boolean state) {
        
        switch (code) {
            case KeyEvent.VK_W : up.on = state;
                break;            
            case KeyEvent.VK_UP : up.on = state;
                break;
            case KeyEvent.VK_S : down.on = state;
                break;
            case KeyEvent.VK_A : left.on = state;
                break;
            case KeyEvent.VK_D : right.on = state;
                break;        
            case KeyEvent.VK_I : inventory.on = state;
                break;    
            case KeyEvent.VK_ENTER : enter.on = state;
                break;
            case KeyEvent.VK_ESCAPE : escape.on = state;
                break;
            case KeyEvent.VK_SPACE : attack.on = state;
                break;
            case KeyEvent.VK_CONTROL : defense.on = state;
                break;
            case KeyEvent.VK_X : x.on = state;
                break;
            case KeyEvent.VK_Q : q.on = state;    
                break;   
            case KeyEvent.VK_PRINTSCREEN : print.on = state;
                break;    
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
