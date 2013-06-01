/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities;

/**
 *
 * @author kirrie
 */
public class EntityPosition {
    int x;
    int y;

    public EntityPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean changePosition(int x, int y) {
        if (this.x != x || this.y != y) {
            this.x = x;
            this.y = y;
            return true;
        }
        return false;
    }

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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.x;
        hash = 37 * hash + this.y;
        return hash;
    }                
}
