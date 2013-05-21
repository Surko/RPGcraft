/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.plugins;

import rpgcraft.map.generators.MapGenerator;

/**
 * Interface pre Pluginy na generatory map. Kazda trieda implementujuca tento interface musi
 * implementovat metodu generate. Metoda generate ma ako parameter MapGenerator, ktory spristupnuje 
 * metody setTile,getTile s ktorym vygeneruje mapu podla poziadavok. 
 * @author kirrie
 */
public interface GeneratorPlugin {
    
    /**
     * Metoda run ma za ulohu zavolat nejaky kus kodu programatorom zvoleny. Metoda je spustena 
     * pri prvom nainicializovani a moze poskytovat zakladny vypis udajov o tomto plugine.
     */
    public void run();
    
    /**
     * Metoda generate generuje mapu ktora je zatial ulozena v objekte MapGenerator 
     * poslanym do metody ako parameter. MapGenerator spristupnuje metody getTile a setTile
     * ktorymi moze plugin vytvorit rozne mapy.
     * @param mapGenerator MapGenerator v ktorom je ulozena mapa ktoru meni tato metoda.
     * @throws Vynimka pri generovani terenu. Vacsinou priradovanie dlazdic ktore neexistuju. Pri inych vynimkach mozne doplnit
     * o ine vynimky
     */
    public void generate(MapGenerator mapGenerator) throws Exception;
        
}
