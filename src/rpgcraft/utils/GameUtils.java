/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.utils;

import java.util.ArrayList;
import rpgcraft.entities.Entity;
import rpgcraft.map.SaveMap;
import rpgcraft.resource.EntityResource;

/**
 * Utility trieda ktora v sebe zdruzuje rozne metody pre pracu s hrou. Trieda je cela staticka =>
 * mozne k nej pristupovat z kazdej inej triedy ci instancie. 
 * Metoda dokaze pracovat s entitami, mapou atd.
 */
public class GameUtils {        
    
    /**
     * Trieda ktora zabezpecuje vytvaranie entit na plane. Implementuje Runnable
     * aby bolo mozne vytvorit nove vlakno, ktore vykona toto vytvorenie.
     */
    public static class Spawn implements Runnable {
        private SaveMap map;
        
        /**
         * Konstruktor pre vytvorenie objektu na vytvorenie entit na plane.
         * @param map Mapa v ktorej vytvarame entity
         */
        public Spawn(SaveMap map) {            
            this.map = map;
        }
        
        /**
         * Metoda run ktora je tu kvoli implementacii Runnable. Ma za ulohu vytvorit novu entitu
         * z moznych entit, ktore sa daju vytvorit (zadane v xml). Na polozenie na plan
         * volame metodu trySpawn.
         */
        @Override
        public void run() {
            ArrayList<String> spawnableEntities = EntityResource.getSpawnableEntities();
            if (spawnableEntities != null && !spawnableEntities.isEmpty()) {
                int index = MainUtils.random.nextInt(spawnableEntities.size());

                Entity e = Entity.createEntity(null, map, EntityResource.getResource(spawnableEntities.get(index)));
                e.initialize();                
                e.trySpawn(map.getPlayer(), 96);
            }
        }
        
    }
    
}
