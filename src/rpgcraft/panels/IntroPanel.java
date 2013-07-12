/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels;

import java.awt.Image;
import rpgcraft.handlers.InputHandle;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.resource.UiResource;


/**
 * Menu pre intro dediace od AbstractMenu. Vacsina metod zabezpecuje abstraktna trieda AbstractMenu.
 * Najdolezitejsie kusy kodu su v konstruktore, ktory prida intro do definovanych menu, a v inputHandling
 * ktory spracovava vstup uzivatela vzhladom na toto menu.
 * @author Kirrie
 */
public class IntroPanel extends AbstractMenu {
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
    public IntroPanel(UiResource res) { 
        this.res = res;
        menuMap.put(res.getId(), this);
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
     * Metoda ktora vrati obrazok intro menu.
     * @return Obrazok intro menu
     */
    public Image getIntroImage() {
        return contImage;
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
    
    // <editor-fold defaultstate="collapsed" desc=" Handling ">
    /**
     * {@inheritDoc }
     */
    @Override
    public void inputHandling() {
        super.inputHandling();
        if (input.runningKeys.contains(InputHandle.DefinedKey.ENTER.getKeyCode())||
                input.runningKeys.contains(InputHandle.DefinedKey.ESCAPE.getKeyCode())) {            
            setMenu(menuMap.get("mainMenu"));            
        }      
        
    }  
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    /**
     * {@inheritDoc }
     * @param w Nova sirka menu
     * @param h Nova vyska menu
     */
    @Override
    public void setWidthHeight(int w, int h) {
        this.w = w;
        this.h = h;
    }

    // </editor-fold>
    
}
