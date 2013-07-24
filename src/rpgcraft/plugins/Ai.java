/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.plugins;

import java.util.HashMap;
import java.util.Random;
import rpgcraft.entities.MovingEntity;

/**
 * Abstraktna trieda ktora vytvara interface pre vsetky pluginy ktore by chceli vytvorit
 * inteligenciu pre NPC entity. Trieda je abstraktna kvoli tomu ze v sebe udrzuje moznost
 * pridat a vratit inteligenciu podla mena z definovaneho listu. 
 * @author kirrie
 */
public abstract class Ai {          
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    /**
     * Nahodny generator cisel pri pohybu entit. 
     */
    public static final Random random = new Random();    
    /**
     * List s nacitanymi inteligenciami z disku
     */
    protected static HashMap<String, Ai> aiList = new HashMap();
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Abstraktne metody ">
    /**
     * Metoda ktora musi vratit meno inteligencie ako vystupuje v liste s ostatnymi.
     * Kazda implementujuca Ai musi mat tuto metodu implementovanu na spravne fungovanie
     * @return Meno inteligencneho pluginu
     */
    public abstract String getName();        
    
    /**
     * Metoda ktora je volana z aktualiznych metod pri entitach. Vykonava sa v nej
     * pohyb a reakcie entity zadanej ako parameter <b>e</b>.
     * Kazda implementujuca Ai musi mat tuto metodu implementovanu na spravne fungovanie.
     * @param e Entita s ktorou hybeme
     * @return True/false ci sa podarilo pohnut entitou
     */
    public abstract boolean aiMove(MovingEntity e);            
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Staticke metody ">
    
    /**
     * Metoda ktora prida Ai do listu s pluginmu. Ai sa prida na miesto s klucom
     * ziskanych z metody getName tohoto ai
     * @param ai Ai na pridanie do listu
     */
    public static void addAi(Ai ai) {
        aiList.put(ai.getName(), ai);
    }
    
    /**
     * Metoda ktora vrati Ai z listu s pluginmi. Vrati sa ta co je na mieste s
     * klucom <b>name</b>
     * @param name Meno Ai ktoru vratime
     * @return Ai/Inteligencia
     */
    public static Ai getAi(String name) {
        return aiList.get(name);
    }
    
    // </editor-fold>
    
}
