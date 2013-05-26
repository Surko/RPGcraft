/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.plugins;

import java.util.HashMap;
import java.util.Random;
import rpgcraft.entities.Armor;
import rpgcraft.entities.Item;
import rpgcraft.entities.Misc;
import rpgcraft.entities.TileItem;
import rpgcraft.entities.Weapon;
import rpgcraft.map.tiles.Tile;
import rpgcraft.resource.StatResource;

/**
 *
 * @author kirrie
 */
public abstract class ItemGeneratorPlugin {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    private static HashMap<String, ItemGeneratorPlugin> itemGenerators = new HashMap<>();    
    private long seed;
    private Random randomizer;
    
    // Konecny item 
    private Item resultItem;    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Prazdny Konstruktor ktory vytvori novu instanciu pluginu. Na dalsiu pracu 
     * sluzia metody ako initialize a generateAll.
     */
    private ItemGeneratorPlugin() {}        
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Generovacie metody ">
    /**
     * Metoda ktora initializuje plugin. Triedy tuto metodu nemozu pretazit kedze kazdy plugin
     * od ItemGeneratorPluginu by mal initializovat rovnako. To znamena, ze podla typu predmetu
     * vytvori prisluchajuci predmet a priradit ho do resultItem s menom <b>name</b>. Podla tohoto mena
     * potom vytvorit objekt typu Random so seedom podla ktoreho budeme vytvarat predmet (seed zarucuje ze
     * pri rovnakom mene dostaneme rovnaky predmet).
     * @param name Meno predmetu
     * @param itemType Typ predmetu
     * @return Initializovany ItemGeneratorPlugin alebo jeho potomka.
     */
    public final ItemGeneratorPlugin initialize(String name, Item.ItemType itemType) {
        switch (itemType) {
            case ARMOR : {
                resultItem = new Armor(name, null);
            } break;
            case WEAPON : {
                resultItem = new Weapon(name, null);
            } break;
            case MISC : {
                resultItem = new Misc(name, null);
            } break;
            case TILE : {
                resultItem = new TileItem(name, (Tile)null);
            }
            default : break;    
        }
        
        if (name != null) {
                this.seed = name.hashCode();                
        } else {
            this.seed=new Random().nextLong();
        }
        
        randomizer = new Random(seed);
        resultItem.setSeed(seed);
        
        return this;
    }
    
    /**
     * Metoda generateAll vy mal vytvorit/vygenerovat vsetky vlastnosti predmetu ktore su spristupnene cez
     * Item API. Nakonci vrati tento vygenerovany predmet (vacsinou to bude premenna resultItem definovana
     * v tejto triede).
     * @return Vygenerovany predmet podla pluginu ktory ho generuje.
     */
    public abstract Item generateAll();
    
    /**
     * Vygeneruje atribut podla zadaneho parametru stat. Atribut moze mat rozne hodnoty od Stringu az po
     * boolean. Preto sa vracia String ako navratovy typ. Objekt ktory tuto funkciu vola musi
     * nasledne Vybrat taku hodnotu aku z toho chceme. Funkcia je dolezita pri dodatocnom generovani urciteho
     * atributu.
     * @param stat Vygenerovanie hodnoty pre urcity atribut [boolean/number]
     * @return Objekt
     */
    protected abstract String generate(StatResource.Stat stat, int down, int up);                   
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    
    /**
     * Abstraktna metoda ktoru treba implementovat v dediacich potomkoch. 
     * Metoda vrati meno pluginu.
     * @return Meno pluginu.
     */
    public abstract String getName();
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Staticke metody ">
    /**
     * Metoda newInstance ktora vrati pravu instanciu ItemGeneratorPluginu. Prava v tomto vyzname
     * znamena ze to bude instancia ItemGeneratorPluginu alebo jeho potomkov (vsetko si zisti z typu triedy).
     * Po vytvoreni novej instancie zavola initialize co prenastavi meno a typ predmetu ktory vytvarame.
     * @param name Meno predmetu ktory bude instancia vytvarat.
     * @param itemType Typ predmetu ktory bude instancia vytvarat.
     * @return Instanciu typu ItemGeneratorPlugin alebo jeho potomkov.
     */
    public ItemGeneratorPlugin newInstance(String name, Item.ItemType itemType) {
        try {
            return this.getClass().newInstance().initialize(name, itemType);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }    
    
    /**
     * Metoda vrati z hashmapy itemGenerators predmetovy generator ktory lezi v tejto hashmape
     * na kluci zadanom parametrom <b>name</b>.     
     * @param name Meno generatoru.
     * @return ItemGeneratorPlugin alebo jeho potomkovia.
     */
    public static ItemGeneratorPlugin getGenerator(String name) {        
        return itemGenerators.get(name);
    }
    
    /**
     * Metoda ktora prida ku predmetovym generatorom novy generator zadany parametrom <b>plugin</b>.
     * Z tohoto generatora vyberie meno pomocou metody getName a pod tymto menom sa ulozi do hashmapy
     * 
     * @param plugin Plugin ktory ukladame do pamate.
     */
    public static void addGenerator(ItemGeneratorPlugin plugin) {
        itemGenerators.put(plugin.getName(), plugin);
    }
    
    // </editor-fold>
    
}
