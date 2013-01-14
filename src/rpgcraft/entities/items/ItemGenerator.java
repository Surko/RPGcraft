/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities.items;

import rpgcraft.entities.Item;
import java.util.Random;

/**
 *
 * @author Kirrie
 */
public class ItemGenerator {
    
    private String name = "";
    private Random randomizer;
    private int hash;
    
    private Item resultItm;
    

    
    public ItemGenerator(String name) {
        if (name != null) {
                this.hash = name.hashCode();                
        }
        else {
            this.hash=new Random().nextInt();
        } 
        this.randomizer = new Random(hash);
        this.name = name;        
    }
    
    
    public Item generateAll() {
        resultItm = new Item();
        resultItm.setName(generate("name", 0, 5));
        resultItm.setBaseDamage(Integer.parseInt(generate("dmg", 1, 20)));
        return resultItm;
    }
    
    /**
     * Vygeneruje atribut podla zadaneho parametru stat. Atribut moze mat rozne hodnoty od Stringu az po
     * boolean. Preto sa vracia String ako navratovy typ. Objekt ktory tuto funkciu vola musi
     * nasledne Vybrat taku hodnotu aku z toho chceme. Funkcia je dolezita pri dodatocnom generovani urciteho
     * atributu.
     * @param stat Vygenerovanie hodnoty pre urcity atribut [boolean/number]
     * @return Objekt
     */
    private String generate(String stat, int down, int up) {
        switch (stat) {
            case "dmg" : {
                Integer res = randomizer.nextInt(up);
                if (res + down > up) {
                    return res.toString();
                } else {
                    res+=down;
                    return res.toString();
                }
            }
            case "name" : {
                
            }
                
        }
        return stat;
    }
}
