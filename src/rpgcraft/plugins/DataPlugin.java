/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.plugins;

import java.util.ArrayList;

/**
 * Abstraktna trieda DataPlugin, ktora vytvara interface pre datove pluginy pre 
 * ziskanie dat do listov a pre dalsie pouzitie v listeneroch ci lua-skriptoch.
 * Vacsinou, ako je tomu aj v DefaultDataPlugin, vytvarame enum, ktory zdruzuje ake data dokazeme vratit.
 * Na ziskanie dat pouzivame metodu getData ktora tento inf predefinuje na enum
 * a podla toho, na ktory pasuje, volame prislusne metody na ziskanie nasich dat.
 * Taktiez trieda obsahuje list dataPlugins, ktory mozme vratit metodou getAllPlugins, ci pridat
 * novy plugin metoodu addPlugin. 
 */
public abstract class DataPlugin {
    
    /**
     * List s nacitanymi data pluginmi
     */
    private static ArrayList<DataPlugin> dataPlugins;
    
    /**
     * Metoda ma za ulohu poskladat do jedneho ArrayListu result informacie. Ziskanie
     * informacii uz implmentuju jednotlive pluginy vytvorene z toho DataPluginu.
     * Pocitame s tym ze jeden z listov predanych ako parameter je nenulovy.
     * @param inf Tag podla ktoreho vyberie informacie do listu
     * @param param Parametre podla ktorych vyplni vysledny list.
     * @param result1 List listov v ktorom su ulozene informacie po riadkoch.
     * @param result2 List s informaciami/datami v ktorom su ulozene informacie sekvencne.List sluzi ako jeden riadok
     * @return True/false ci sa podarilo ziskat data
     */
    public abstract boolean getData(String inf, ArrayList<String> param, Object srcObject,  
            ArrayList<ArrayList<Object>> result1, ArrayList<Object> result2);
    
    
    /**
     * Metoda ktora prida DataPlugin do hashmapy s pluginmi
     * @param plugin Novy data plugin 
     */
    public static void addPlugin(DataPlugin plugin) {
        if (dataPlugins == null) {
            dataPlugins = new ArrayList<>();
        }
        dataPlugins.add(plugin);
    }
    
    /**
     * Metoda ktora vrati vsetky data pluginy.
     * @return List s data pluginmi.
     */
    public static ArrayList<DataPlugin> getAllPlugins() {
        return dataPlugins;
    }
    
}
