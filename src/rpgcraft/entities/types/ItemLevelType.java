/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities.types;

/**
 *
 * @author doma
 */
public enum ItemLevelType implements Type {
           HAND(1),
           WOODEN(2),
           STONE(4),
           IRON(8),
           GOLD(16),
           ORCISH(32),
           ADAMANTITE(64),
           DIAMOND(128),
           PLASMA(256),
           MATTER(512);
           
           int value;
           
           private ItemLevelType(int value) {
               this.value = value;
           }
           
           public int getValue() {
               return value;
           }
}
