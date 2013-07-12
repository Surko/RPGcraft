/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities.types;

/**
 * Nazov tu je dost zavadzajuci ale tento enum predstavuju z coho vyrobene su predmety
 * ale aj entity.
 * V enume su definove rozne druhy typov ako HAND, ORCISH, atd... , ktore sa pri strete dvoch entit
 * beru do uvahy tak ze entita nato aby udrela druhu musi mat typ entity taky aby ked sa spravi
 * binarne AND tak vyjde cislo vacsie ako nula. Takymto sposobom mozme entitam nadefinovat
 * kombinacie a len entity so specifickym typom moze nanich utocit.
 * Cisla v enumoch su naschval o jednotku zmensene od nasobkov 2 aby v binarnom zapise
 * boli cislice same jednicky => ked nadefinujeme entite obranu 16 tak mozu jej ublizit jedine
 * typy GOLD a viac.
 */
public enum ItemLevelType {
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
