/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels;

import rpgcraft.handlers.InputHandle;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.resource.UiResource;


/**
 * Menu pre nacitavacie menu dediace od AbstractMenu. Vacsina metod zabezpecuje abstraktna trieda AbstractMenu.
 * Najdolezitejsie kusy kodu su v konstruktore, ktory prida intro do definovanych menu, a v inputHandling
 * ktory spracovava vstup uzivatela vzhladom na toto menu.
 * @author Kirrie
 */
public class LoadCreateMenu extends AbstractMenu {
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Konstruktor ktory vytvori novu instanciu triedy. Inicializujeme v nej
     * resource a vkladame toto menu do hash mapy menuMap.
     * @param res UiResource z ktoreho vznika menu
     */
    public LoadCreateMenu(UiResource res) {
        this.res = res; 
        menuMap.put(res.getId(), this);
    }                
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * <i>{@inheritDoc }</i>
     * @return Meno tohoto menu
     */
    @Override
    public String getName() {
        return res.getId();
    }
    
    /**
     * {@inheritDoc }
     * @return Sirka menu (default : 0)
     */
    @Override
    public int getWidth() {
        return 0;
    }

    /**
     * {@inheritDoc }
     * @return Vyska menu (default : 0)
     */
    @Override
    public int getHeight() {
        return 0;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Handling ">    
    /**
     * {@inheritDoc }
     */
    @Override
    public void inputHandling() {
        super.inputHandling();                
        if (input.runningKeys.contains(InputHandle.DefinedKey.ESCAPE.getKeyCode())) {
            setMenu(menuMap.get("mainMenu"));
        }                
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    /**
     * {@inheritDoc }
     * @param w {@inheritDoc }
     * @param h {@inheritDoc }
     */
    @Override
    public void setWidthHeight(int w, int h) {                
    }
    // </editor-fold>
    
}
