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
           WOODEN(3),
           STONE(7),
           IRON(15),
           GOLD(31),
           ORCISH(63),
           ADAMANTITE(127),
           DIAMOND(255),
           DARKDIAMOND(511),
           PLASMA(1023),
           MATTER(2047);
           
           int value;
           
           private ItemLevelType(int value) {
               this.value = value;
           }
           
           public int getValue() {
               return value;
           }
}
