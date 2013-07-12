/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels;

import rpgcraft.handlers.InputHandle;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.resource.UiResource;

/**
 * Trieda dediace od AbstractMenu zobrazujuca info o hre. Vacsina metod zabezpecuje abstraktna trieda AbstractMenu.
 * Najdolezitejsie kusy kodu su v konstruktore, ktory prida intro do definovanych menu, a v inputHandling
 * ktory spracovava vstup uzivatela vzhladom na toto menu.
 * @author Kirrie
 */
public class AboutMenu extends AbstractMenu {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    private int w;
    private int h;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Konstruktor ktory vytvori novu instanciu triedy. Inicializujeme v nej
     * resource a vkladame toto menu do hash mapy menuMap.
     * @param res UiResource z ktoreho vznika menu
     */
    public AboutMenu(UiResource res) {
        this.res = res;  
        menuMap.put(res.getId(), this);
    }  
    // </editor-fold>
           
    // <editor-fold defaultstate="collapsed" desc=" Handling ">
    /**
     * {@inheritDoc }
     */
    @Override
    public void inputHandling() {
        super.inputHandling();
        if (input.clickedKeys.contains(InputHandle.DefinedKey.ESCAPE.getKeyCode())) {
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
        this.w = w;
        this.h = h;
    }  
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * {@inheritDoc }
     * @return Meno pre menu
     */
    @Override
    public String getName() {
        return res.getId();
    }

    /**
     * {@inheritDoc }
     * @return Sirka menu
     */
    @Override
    public int getWidth() {
        return w;
    }

    /**
     * {@inheritDoc }
     * @return Vyska menu
     */
    @Override
    public int getHeight() {
        return h;
    }
    // </editor-fold>
}
