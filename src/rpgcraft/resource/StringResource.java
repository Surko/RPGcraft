/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.manager.PathManager;



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
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    private static final Logger LOG = Logger.getLogger(StringResource.class.getName());
    private static final String init = "String Resource initialising";
    private static final String done = "DONE ";
    private static final String missing = "String Resource is missing";
    private static final String nores = "MRES ";
    private static final String LOCAL = "strings";
    
    private static HashMap<String, String> stringResources;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Inicializacie ">
    /**
     * Nastavenie string resource s lokalizaciou zadanou parametrom <b>locale</b>.
     * Lokalizacne subory nachadza v zlozke locales (PathManager.getInstance().getLocalizationPath())
     * kde vytvori class loader ktory pouzijeme v metode getBundle. Nakonci skonvertujeme
     * tieto stringove hodnoty do hashmap stringResources z ktorej hra cerpa vsetky udaje.
     * @param locale Lokalizacia suboru ktory nacitavame.
     * @throws Exception Vynimka pri zlom nacitani string resource
     */
    public static void initializeResources(Locale locale) throws Exception{         
        LOG.log(Level.INFO, init);
        File resFile = PathManager.getInstance().getLocalizationPath();           
        try {
            URL[] urls = {resFile.toURI().toURL()};
            ClassLoader authorizedLoader = URLClassLoader.newInstance(urls);
            ResourceBundle rb = ResourceBundle.getBundle(LOCAL, locale, authorizedLoader);  
            stringResources = convertBundleToMap(rb);
            Logger.getLogger(StringResource.class.getName()).log(Level.INFO, done);
        } catch (Exception e) {
            Logger.getLogger(StringResource.class.getName()).log(Level.SEVERE, missing);
            throw e;
        }
    }
    
    /**
     * Nastavenie string resource s defaultnou lokalizaciou. Metoda je inak rovnaka
     * ako initializeResources s parametrom locale.
     * @see #initializeResources(java.util.Locale) 
     * @throws Exception Vynimka pri zlom nacitani string resource
     */
    public static void initializeResources() throws Exception{  
        initializeResources(Locale.getDefault());        
    }
    
    /**
     * Nastavenie string resource priamo z resource bundle. Metoda iba okopiruje/skonvertuje
     * tento resource bundle do hashmapy.     
     * @see ResourceBundle
     */
    public static void initializeResources(ResourceBundle rb) {
        LOG.log(Level.INFO, init);
        
        try { 
            stringResources = convertBundleToMap(rb);
            Logger.getLogger(StringResource.class.getName()).log(Level.INFO, done);
        } catch (Exception e) {
            Logger.getLogger(StringResource.class.getName()).log(Level.SEVERE, missing);        
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Staticka metoda getResource ktora ziska z hashmapy stringResources text ktory ma kluc rovny
     * parametru name.
     * @param name Kluc na hashmapu a v nej text ktory sa v hashmape nachadza.
     * @return Textovu hodnotu z hashmapy
     */
    public static String getResource(String name) {
        if (stringResources.containsKey(name)) {
            return stringResources.get(name);
        } else {
            Logger.getLogger(StringResource.class.getName()).log(Level.WARNING, nores + name);
            return nores + name;
        }
    }
    
    /**
     * Staticka metoda getResource ktora ziska z hashmapy stringResources text ktory ma kluc rovny
     * parametru <b>name</b>. Nasledne pouzije metodu z format ktora vyuzije druhy parameter <b>param</b>
     * ako vypln do resource.
     * @param name Kluc na hashmapu a v nej text ktory sa v hashmape nachadza.
     * @param param Objekty/parametre ktorymi vyplname miesta v resource
     * @return Textovu hodnotu z hashmapy na ktoru boli pouzite parametre.
     */
    public static String getResource(String name, Object[] param) {
        if (stringResources.containsKey(name)) {
            return String.format(stringResources.get(name), param);
        } else {
            Logger.getLogger(StringResource.class.getName()).log(Level.WARNING, nores + name);
            return nores + name;
        }           
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Pomocne metody ">    
    /**
     * Metoda ktora skonvertuje ResourceBundle to hashmapy. Metoda vyberie kluce z resource bundle
     * a postupne ako prechadza klucami tak vyplna mapu.
     * @param res ResourceBundle ktory prekonvertujeme na mapu
     * @return HashMapu z textami.
     * @throws Exception Vynimka pri res == null
     */
    private static HashMap<String, String> convertBundleToMap(ResourceBundle res) throws Exception {
        HashMap<String, String> map = new HashMap<>();
        
        Enumeration<String> keys = res.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            map.put(key, res.getString(key));
        }
                
        return map;
    }
    // </editor-fold>
}
