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
    
    private static final String init = "String Resource initialising";
    private static final String done = "DONE";
    private static final String missing = "String Resource is missing";
    private static final String nores = "MRES";
    
    private static HashMap<String, String> stringResources;
    
    public static void initializeResources(Locale locale) {        
        Logger.getLogger(StringResource.class.getName()).log(Level.INFO, init);
        
        try {
            ResourceBundle rb = ResourceBundle.getBundle("rpgcraft.properties.strings", locale);  
            stringResources = convertBundleToMap(rb);
            Logger.getLogger(StringResource.class.getName()).log(Level.INFO, done);
        } catch (Exception e) {
            Logger.getLogger(StringResource.class.getName()).log(Level.SEVERE, missing);        
        }
    }
    
    public static void initializeResources() {  
        Logger.getLogger(StringResource.class.getName()).log(Level.INFO, init);
        
        try {
            ResourceBundle rb = ResourceBundle.getBundle("rpgcraft.properties.strings");  
            stringResources = convertBundleToMap(rb);
            Logger.getLogger(StringResource.class.getName()).log(Level.INFO, done);
        } catch (Exception e) {
            Logger.getLogger(StringResource.class.getName()).log(Level.SEVERE, missing);        
        }
        
    }
    
    public static void initializeResources(ResourceBundle rb) {
        Logger.getLogger(StringResource.class.getName()).log(Level.INFO, init);
        
        try { 
            stringResources = convertBundleToMap(rb);
            Logger.getLogger(StringResource.class.getName()).log(Level.INFO, done);
        } catch (Exception e) {
            Logger.getLogger(StringResource.class.getName()).log(Level.SEVERE, missing);        
        }
    }
    
    public static String getResource(Object name) {
        if (stringResources.containsKey(name)) {
            return String.format(stringResources.get(name), null);
        } else {
            return nores;
        }
    }
    
    public static String getResource(String name, Object[] param) {
        if (stringResources.containsKey(name)) {
            return String.format(stringResources.get(name), param);
        } else {
            return nores;
        }           
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
