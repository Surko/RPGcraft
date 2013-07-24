/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.listeners;

import rpgcraft.plugins.AbstractMenu;
import rpgcraft.plugins.Listener;
import rpgcraft.panels.GameMenu;
import rpgcraft.panels.components.Component;

/**
 * Trieda dediaca od Listeneru je dalsi typ listeneru mozny vygenerovat v ListenerFactory,
 * ktory ma za ulohu vykonavat Loading akcie => akcie ktore su vseobecne pre nacitavanie a vytvaranie hier
 */
public class LoadCreateListener extends Listener {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    /**
     * Enum s moznymi operaciami v tomto listenery. V metode actionPerform sa
     * podla tychto operacii vykonavaju prislusne metody
     */
    public enum Operations {
        LOAD,
        CREATE
    }
    
    /**
     * Operacia na vykonanie
     */
    Operations op;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Vytvorenie instancie listeneru pomocou textu zadaneho v parametri <b>data</b>.
     * Konstruktor rozparsuje text, urci operaciu aka sa bude vykonavat a parametre
     * pre tuto operaciu pomocou metody setParams
     * @param save Text s funkciou ktoru vykonavame
     */
    public LoadCreateListener(String save) {
        int fstBracket = save.indexOf('(');
        
        String params = null;
        
        if (fstBracket == -1) {
            this.op = LoadCreateListener.Operations.valueOf(save);
        } else {
            this.op = LoadCreateListener.Operations.valueOf(save.substring(0, fstBracket));
            params = save.substring(fstBracket);
        }
                        
        if (params != null) {            
            setParams(params.substring(1, params.length() - 1));        
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Vykonavanie + pomocne metody ">
    /**
     * {@inheritDoc }
     * @param e {@inheritDoc }
     * @see ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);

        
        switch (op) {
            case LOAD : {                                                
                Component c = (Component)e.getSource();
                GameMenu gameMenu = (GameMenu) AbstractMenu.getMenuByName("gameMenu");
                if (parsedObjects[0] instanceof String) {
                    gameMenu.loadMapInstance((String)parsedObjects[0]);
                    c.getOriginMenu().setMenu(gameMenu);
                }
            } break;
            case CREATE : {
                Component c = (Component)e.getSource();

                GameMenu gameMenu = (GameMenu) AbstractMenu.getMenuByName("gameMenu");
                if (parsedObjects[0] instanceof String) {
                    if (gameMenu.newMapInstance((String)parsedObjects[0])) {
                        c.getOriginMenu().setMenu(gameMenu);
                    }
                }    
                    
            }
        }

    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * {@inheritDoc }
     * return Meno listeneru
     */
    @Override
    public String getName() {
        return ListenerFactory.Commands.LOAD.toString();
    }
    // </editor-fold>
    
}
