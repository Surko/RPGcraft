/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities;

/**
 * Trieda EntityPosition ktora tvori par pre suradnice entity. Trieda je mozna vyuzit
 * pri zmenenej implementacie pre nacitavanie entit ktore potrebujeme. Napriklad zmena
 * na nacitavanie entit pomocou QuadTree.
 * @author kirrie
 */
public class EntityPosition {
    /**
     * X-ova pozicia entity
     */
    private int x;
    /**
     * Y-ova pozicia entity
     */
    private int y;

    /**
     * Konstruktor pre vytvorenie paru s poziciami kde sa nachadza entita.
     * @param x X-ova pozicia entity
     * @param y Y-ova pozicia entity
     */
    public EntityPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Metoda ktora zmeni x-ove a y-ove pozicie na tie v parametroch. Metoda vrati ci 
     * sa podarilo zmenit pozicie.
     * @param x Nova x-ova pozicia
     * @param y Nova y-ova pozicia
     * @return True/false ci sa zmenili pozicie
     */
    public boolean changePosition(int x, int y) {
        if (this.x != x || this.y != y) {
            this.x = x;
            this.y = y;
            return true;
        }
        return false;
    }

    /**
     * Metoda ktora porovna dva objekty. Objekty su rovnake ked maju rovnake x-ove a
     * y-ove suradnice.
     * @param obj Objekt s ktorym porovnavame.
     * @return True/false ci sa objekty rovnaju
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EntityPosition other = (EntityPosition) obj;

        if (other.x == x && other.y == y) {
            return true;
        }

        return false;            
    }

    /**
     * Metoda ktora vrati hash kod pre pozicie.
     * @return Hash kod pre poziciu entity
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.x;
        hash = 37 * hash + this.y;
        return hash;
    }                
}
