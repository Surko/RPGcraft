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
        int pos = 0;
        while (pos >= 0 && pos < listeners.length) {
            if (e.getAction().done) {
                return;
            }
            listeners[pos].actionPerformed(e);
            pos += e.getJumpValue();
            e.setJumpValue(1);
        }
    }

    @Override
    public String getName() {
        return ListenerFactory.Commands.COMPOP.toString();
    }
}                    
