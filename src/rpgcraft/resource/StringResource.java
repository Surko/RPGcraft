/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Trieda StringResource ma za ulohu v sebe uchovavat vsetky textove hodnoty pouzite v programe.
 * Tieto hodnoty sa nachadzaju v premennej <b>stringResources</b> a trieda si ich
 * inicializuje cez metodu <b>initializeResources</b>,
 * ktora by mala byt volana pri spusteni aplikacie. Text si ziskava zo suborov
 * typu properties v ktorych sa nachadzaju
 * vzdy dvojice <b>kluc</b>=<b>hodnota</b>. Trieda prekonvertuje tento subor na 
 * ResourceBundle a metodou <b>convertBundleToMap</b> ich inicializuje
 * do spominanej premennej <b>stringResources</b>.
 * @author doma
 * @see ResourceBundle
 */
public class StringResource {
    
    private static HashMap<String, String> stringResources;
    
    public static void initializeResources(Locale locale) {        
        Logger.getLogger(StringResource.class.getName()).log(Level.INFO, "String Resource initialising");
        
        try {
            ResourceBundle rb = ResourceBundle.getBundle("rpgcraft.properties.strings", locale);  
            stringResources = convertBundleToMap(rb);
            Logger.getLogger(StringResource.class.getName()).log(Level.INFO, "DONE");
        } catch (Exception e) {
            Logger.getLogger(StringResource.class.getName()).log(Level.SEVERE, "String Resource: not found ");        
        }
    }
    
    public static void initializeResources() {  
        Logger.getLogger(StringResource.class.getName()).log(Level.INFO, "String Resource initialising");
        
        try {
            ResourceBundle rb = ResourceBundle.getBundle("rpgcraft.properties.strings");  
            stringResources = convertBundleToMap(rb);
            Logger.getLogger(StringResource.class.getName()).log(Level.INFO, "DONE");
        } catch (Exception e) {
            Logger.getLogger(StringResource.class.getName()).log(Level.SEVERE, "String Resource: not found ");        
        }
        
    }
    
    public static void initializeResources(ResourceBundle rb) {
        Logger.getLogger(StringResource.class.getName()).log(Level.INFO, "String Resource initialising");
        
        try { 
            stringResources = convertBundleToMap(rb);
            Logger.getLogger(StringResource.class.getName()).log(Level.INFO, "DONE");
        } catch (Exception e) {
            Logger.getLogger(StringResource.class.getName()).log(Level.SEVERE, "String Resource: not found ");        
        }
    }
    
    public static String getResource(String name) {
        return stringResources.get(name);
    }
    
    public static String getResource(String name, String[] param) {
        return String.format(stringResources.get(name), param);            
    }
    
    private static HashMap<String, String> convertBundleToMap(ResourceBundle res) throws Exception {
        HashMap<String, String> map = new HashMap<>();
        
        Enumeration<String> keys = res.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            map.put(key, res.getString(key));
        }
                
        return map;
    }
}
