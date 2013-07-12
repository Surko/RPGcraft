/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels;

import java.awt.Graphics;
import rpgcraft.handlers.InputHandle;
import rpgcraft.panels.components.Container;
import rpgcraft.plugins.AbstractMenu;


/**
 * Prazdne menu dediace od AbstractMenu. Toto menu je vsadene do hracieho panelu pri neexistencii
 * nejakeho menu. Prazdne menu ako meno napoveda nema v sebe definovane ziadne akcie, aktualizacie
 * ani ovladanie vstupu.
 * @author Surko
 */
public class BlankMenu extends AbstractMenu {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    // Meno menu
    private static final String BLANK = "blankMenu";
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Konstruktor pre prazdne menu. Nastavuje premenne changedGr a changedUi na false, co by zabranilo
     * nejakym grafickym ci ui aktualizaciam (keby bola natvrdo zavolana aktualizacia z rodica)
     */
    public BlankMenu() {
        this.changedGr = false;
        this.changedUi = false;
    }

    // </editor-fold >
    
    // <editor-fold defaultstate="collapsed" desc=" Inicializacie ">
    /**
     * Metoda ktora initializuje prazdne menu.
     * @param gameContainer
     * @param input 
     */
    @Override
    public void initialize(Container gameContainer, InputHandle input) {
        menuMap.put(BLANK, this);
    }           
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Kresliace metody ">
    /**
     * Kedze je toto menu BlankMenu tak nic neaktualizujeme => prazdna paint metoda.
     */
    @Override
    public void paintMenu(Graphics g) {}
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * <i>{@inheritDoc}</i>
     * @return Meno menu
     */
    @Override
    public String getName() {
        return BLANK;
    }

    /**
     * Metoda vrati nulovu dlzku menu.
     * @return Dlzka menu
     */
    @Override
    public int getWidth() {
        return 0;
    }

    /**
     * Metoda vrati nulovu vysku menu      
     * @return Vyska menu
     */
    @Override
    public int getHeight() {
        return 0;
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    
    /**
     * Kedze je toto menu BlankMenu tak nic neaktualizujeme => prazdna setWidthHeight metoda.
     */
    @Override
    public void setWidthHeight(int w, int h) {
        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Update + handling ">
    /**
     * Kedze je toto menu BlankMenu tak nic neaktualizujeme => prazdna update metoda.
     */
    @Override
    public void update() {}
    
    /**
     * Kedze je toto menu BlankMenu tak nic neaktualizujeme => prazdna input metoda.
     */
    @Override
    public void inputHandling() {}
    // </editor-fold>
    
}
