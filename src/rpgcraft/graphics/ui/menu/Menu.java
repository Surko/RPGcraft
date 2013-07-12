/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics.ui.menu;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

/**
 * Interface pre vsetky menu ci su to vnutorne menu podedene od AbstractInMenu, ci
 * hlavne menu podedene od AbstractMenu. Trieda vyuziva generiku pre dodefinovane
 * niektorych parametrov v metodach (setMenu(T menu)). Trieda obsahuje zakladne 
 * metody ktore su spolocne a vyznamne pre spravne ukazanie ci interakcie s menu.
 */
public interface Menu<T extends Menu> {    
    /**
     * Metoda ktora reaguje na vstup od uzivatela podla handlera ktory do menu pridavame
     * v konstruktore alebo inym sposobom.
     */
    public void inputHandling();
    /**
     * Metoda ktora reaguje na vstup/stlacenia mysi.
     * @param e MouseEvent podla ktoreho spracovavame metodu.
     */
    public void mouseHandling(MouseEvent e);
    /**
     * Metoda ktora vykresli menu. Dolezite pri vykreslovani v AbstractInMenu.
     * @param g Graficky kontext na vykreslenie menu.
     */
    public void paintMenu(Graphics g);
    /**
     * Metoda na aktualizovanie menu
     */
    public void update();
    /**
     * Metoda ktora prenastavi aktualne menu na ine menu.
     * @param menu Menu na ktore prenastavujeme.
     */
    public void setMenu(T menu);
    /**
     * Metoda ktora vrati sirku menu
     * @return Sirka menu
     */
    public int getWidth();
    /**
     * Metoda ktora vrati vysku menu
     * @return Vyska menu
     */
    public int getHeight();
}
