/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.errors;

import java.awt.Color;

/**
 * Trieda ktora vytvara instanciu multi typovej hlasky. Dedenim od ErrorWrn
 * je instancia chybove okno ktore v tomto pripade zobrazuje nespecificky chybu,
 * ktoru specifikujeme az dodatocne metodou renderSpecific. Obycajne je metoda 
 * renderSpecific bez parametrov, no v tomto pripade parameter sluzi ako specifikum chyby.
 * nejakeho suboru (missError farba pre chybajuce subory)
 */
public class MultiTypeWrn extends ErrorWrn {
    
    /**
     * Konstruktor ktory pre tuto triedu vytvori novu chybu s danou chybovou hlaskou
     * podla parametru e, ktory bude obsahovat StackTrace. Taktiez v nom nastavujeme
     * farbu pozadia a dodatocnu spravu. Obycajne je sprava <b>msg</b> uz naformatovana
     * no pre dodatocne naformatovanie posuvame do konstruktoru este parametre <b>param</b>.
     * @param e Typ Vynimky
     * @param cl Farba okolia chybovej hlasky
     * @param msg Dodatocna sprava od progamu k chybovemu vypisu.
     * @param param Parametre ktore naformatuju text msg.
     */    
    public MultiTypeWrn(Exception e,Color cl,String msg, Object[] param) {
        this.e = e;
        this.cl = cl;
        this.msg = String.format(msg, param);        
    }

    /**
     * Metoda ktora vyrenderuje instanciu MissingFile. Vyrenderovanim sa mysli
     * zobrazenie chyboveho okna. Natoto sluzi volanie renderSpecific s textom
     * ktory je v parametri <b>errorType</b>
     */
    @Override
    public void renderSpecific(String errorType) {
        super.renderSpecific(errorType);
    }            
    
}
