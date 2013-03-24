/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.listeners;

/**
 *
 * @author kirrie
 */
public class CombinatedListener extends Listener {
    
    Listener[] listeners;
    
    public CombinatedListener(Listener[] listeners) {
        this.listeners = listeners;                
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (Listener listener : listeners) {
            listener.actionPerformed(e);
        }
    }
    
    
    
    
    
}
