/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.listeners;

import rpgcraft.plugins.Listener;

/**
 * Trieda dediaca od Listeneru je dalsi typ listeneru mozny vygenerovat v ListenerFactory,
 * ktory ma za ulohu vykonavat viacero druhov akcii. Instancia tohoto listeneru obsahuje v sebe
 * pole listenerov ktore sa sekvencne vykonavaju. Pravdaze sa daju pouzivat skoky, atd...
 */
public class CombinatedListener extends Listener {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    /**
     * Ppole s listenermi ktore sa vykonavaju sekvencne pri volani tohoto kombinovaneho listeneru.
     */
    Listener[] listeners;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Konstruktor ktory vytvori instanciu Kombinovaneho listeneru. Ako parameter dostava
     * ostatne listenery ktore budeme vykonavat sekvencne.
     * @param listeners Listener v poli na vykonanie
     */
    public CombinatedListener(Listener[] listeners) {
        this.listeners = listeners;                
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Vykonavanie ">
    /**
     * Metoda ktora vykona listener takym sposobom ze sekvencne prebieha a
     * vykonava pole listeners      
     * @param e ActionEvent ktory vyvolal Listener
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        int pos = 0;
        while (pos >= 0 && pos < listeners.length) {
            if (e.getAction().done) {
                return;
            }
            if (listeners[pos] != null) {
                listeners[pos].actionPerformed(e);
            }
            pos += e.getJumpValue();            
            e.setJumpValue(1);
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * {@inheritDoc }
     * @return Meno listeneru
     */
    @Override
    public String getName() {
        return ListenerFactory.Commands.COMPOP.toString();
    }
    // </editor-fold>
}                    
