/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.errors;

import java.awt.Color;

/**
 * Trieda CorruptedFile vytvori novu chybovu hlasku s danym popiskom
 * do noveho ramu ktory vystrieda ten aktivny. Trieda dedi od ErrorWrn, ktora definuje zakladnu pracu
 * s tymito hlaskami.
 * @see ErrorWrn
 * @author Kirrie
 */
public class CorruptedFile extends ErrorWrn {
    
    /**
     * Konstruktor ktory pre tuto triedu vytvori novu chybu s danou chybovou hlaskou
     * podla parametru e, ktory bude obsahovat StackTrace. Taktiez v nom nastavujeme
     * farbu pozadia a dodatocnu spravu
     * @param e Typ Vynimky
     * @param cl Farba okolia chybovej hlasky
     * @param msg Dodatocna sprava od progamu k chybovemu vypisu.
     */
    
    public CorruptedFile(Exception e,Color cl,String msg) {
        this.e = e;
        this.cl = cl;
        this.msg = msg;        
        
    }
        
    /**
     * Metoda ktora vyrenderuje instanciu MissingFile. Vyrenderovanim sa mysli
     * zobrazenie chyboveho okna. Natoto sluzi volanie renderSpecific s danym textom.     
     * @see ErrorWrn#renderSpecific(java.lang.String)  
     */
    public void render(){             
        super.renderSpecific("RPGcraft!! --- CorruptedFile Error");
    }
}
