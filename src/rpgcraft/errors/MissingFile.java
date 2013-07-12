/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.errors;

import rpgcraft.graphics.Colors;

/**
 * Trieda ktora vytvara instanciu MissingFile. Dedenim od ErrorWrn
 * je instancia chybove okno ktore v tomto pripade zobrazuje chybu o neexistencii
 * nejakeho suboru (missError farba pre chybajuce subory)
 */
public class MissingFile extends ErrorWrn {
    
    /**
     * Konstruktor pre vytvorenie instancie MissingFile. Nastavujeme v nom
     * chybovu hlasku s farbou pozadia v chybovom okne a dodatocnou spravou
     * @param e Vynimka ktoru vypisujeme do okna
     * @param msg Dodatocna sprava
     */
    public MissingFile(Exception e,String msg) {
        this.e = e;
        this.cl = Colors.getColor(Colors.missError);
        this.msg = msg;        
        
    }
    
    /**
     * Metoda ktora vyrenderuje instanciu MissingFile. Vyrenderovanim sa mysli
     * zobrazenie chyboveho okna. Natoto sluzi volanie renderSpecific s danym textom.
     */
    public void render() {
        super.renderSpecific("RPGcraft!! --- MissingFile Error");
    }
    
}
