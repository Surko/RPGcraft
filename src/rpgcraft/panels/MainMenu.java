/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels;

import rpgcraft.plugins.AbstractMenu;
import rpgcraft.resource.UiResource;


/**
 * Menu pre hlavne menu dediace od AbstractMenu. Vacsina metod zabezpecuje abstraktna trieda AbstractMenu.
 * Najdolezitejsie kusy kodu su v konstruktore, ktory prida intro do definovanych menu, a v inputHandling
 * ktory spracovava vstup uzivatela vzhladom na toto menu.
 * @author Kirrie
 */
public class MainMenu extends AbstractMenu{
       
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">    
    /**
     * Konstruktor ktory vytvori novu instanciu triedy. Inicializujeme v nej
     * resource a vkladame toto menu do hash mapy menuMap.
     * @param res UiResource z ktoreho vznika menu
     */
    public MainMenu(UiResource res) {  
        this.res = res;
        menuMap.put(res.getId(), this);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Update + handling ">        
    /**
     * {@inheritDoc }
     */
    @Override
    public void inputHandling() {
        super.inputHandling();
    }         
        
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    /**
     * {@inheritDoc }
     * @param w {@inheritDoc }
     * @param h {@inheritDoc }
     */
    @Override
    public void setWidthHeight(int w, int h) {}   
    
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
        return 0;
    }

    /**
     * {@inheritDoc }
     * @return Vyska menu
     */
    @Override
    public int getHeight() {
        return 0;
    }

    // </editor-fold>

} 
