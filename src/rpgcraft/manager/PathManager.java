/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.manager;

import java.io.File;

/**
 * Singleton trieda so vsetkymi ulozenymi cestami pre subory.
 * Mena adresarov su ulozene v premennych. Trieda obsahuje zakladne metody
 * pre inicializovanie a ziskanie ciest pre prislusne sektory (xml cesta, plugin cesta,
 * image cesta,...). Vacsina metod je tu na ziskanie tychto ciest. Dolezita je ale determineRootPath ktora
 * urci zakladnu cestu k suborom. 
 * <p>Ziskanie moze prebiehat v 3 modoch : <br>
 * - natvrdo dana cesta zavisla od OS, vacsinou aplikacne data. <br>
 * - cesta do aktualneho adresara kde sa nachadza jar subor. <br>
 * - cesta do adresara kde sa nachadza trieda (PathManager.class). Pouzita je pri
 * kompilovani v IDE ako NetBeans kedze ta uklada .class subory najprv do adresara build/classes kde 
 * nahodou kompilator uklada aj resource. Na fungovanie tejto vetvy musi mat IDE 
 * neprazdny argument pri spustani hry. 
 * </p>
 */
public final class PathManager {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    public static final String MAPS = File.separator + "maps";   
    
    public static final String AIPLUG = "ai-plugins";
    public static final String MAPLUG = "map-plugins";
    public static final String SCRIPTPLUG = "script-plugins";
    public static final String INMENUPLUG = "inmenu-plugins";
    public static final String MENUPLUG = "menu-plugins";
    public static final String DATAPLUG = "data-plugins";
    public static final String ITEMGENPLUG = "itemgen-plugins";
    public static final String LISTPLUG = "listener-plugins";
    
    public static final String RENDER = "render";   
    private static final String SAVE = "saves";    
    private static final String SCRIPT = "scripts";
    private static final String PLUGINS = "plugins";        
    private static final String XML = "xml";
    private static final String IMAGE = "images";
    private static final String UI = "ui";
    private static final String SHEET = "sheets";
    private static final String LOCAL = "locales";
    private static final String SOUNDS = "sounds";
    
    private static PathManager instance;
    private static String[] args;
    
    private static final byte LINUX = 0;
    private static final byte WINDOWS = 1;
    private static final byte MAC = 2;
    
    private File renderPath;
    private File rootPath;
    private File worldPath;    
    private File scriptPath;
    private File pluginPath;
    private File xmlPath;
    private File imagePath;
    private File uiPath;
    private File sheetPath;
    private File localizationPath;
    private File soundPath;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Privatny konstruktor volany pri vytvarani singleton instancie. Konstruktor vola
     * metodu determinRootPath
     */
    private PathManager() {
        determineRootPath(true);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Update ">
    /**
     * Metoda ktora nastavi hlavnu/root cestu k resource suborom. Parameter <b>useDefDir</b>
     * nam urcuje ci ziskavame cestu z defaultnej cesty alebo podla OS.
     * <p>
     * Rozhodovanie podla OS urcujeme metodou getOs ktora ma parameter systemovu vlastnost
     * System.getProperty("os.name"), ktora nam vrati byte podla ktoreho sa rozhodujeme 
     * ci mame LINUX, MAC, WINDOWS. Podla OS sa urcite rootPath.
     * </p>
     * <p>
     * Pouzitie default/zakladnej cesty urcujeme priamo vytvorenym suboru ktory ako parameter
     * dostava systemovu vlastno System.getProperty("user.dir"), ktora nam vrati 
     * adresar v ktorom je .jar subor. Existuje ale aj druha moznost ziskanie rootPath a to v pripade
     * ked spustame program priamo v IDE => nacitavame resource subory z priecinku build/classes 
     * (kedze v tomto priecinku sa nachadzaju aj resource priecinky - build-impl.properties).
     * </p>
     * @param useDefDir True/false ci pouzivame default/zakladnu cestu pre nacitavanie resource.
     */
    public void determineRootPath(boolean useDefDir) {
        if (!useDefDir) {
            switch (getOs(System.getProperty("os.name"))) {
                case LINUX:
                    rootPath = new File("~/RPGcraft");
                    break;
                case MAC:
                    rootPath = new File(System.getProperty("user.home") + "/Library/Application Support/" + "RPGcraft");
                    break;
                case WINDOWS:
                    rootPath = new File(System.getenv("APPDATA") + "\\RPGcraft");
                    break;
                default:
                    rootPath = new File(System.getProperty("user.home") + "/RPGcraft");
            }
        } else {
           rootPath = new File(System.getProperty("user.dir"));                                  
           if (args.length > 0) {
               rootPath = new File(getClass().getResource("/").getFile());
           }
        }
        updateDirs();
    }
        
    /**
     * Metoda ktora aktualizuje a nastavuje priecinky. Nastavovanie prebieha ze
     * prislusne subory priradujeme k prislusnym nazvom (localizationPath - subor s local textami,...).
     * Aktualizovanie prebieha tak ze vytvorime vsetky adresare (ked neexistuju) pre 
     * kazdu cestu k prislusnym resource.
     */
    private void updateDirs() {
        rootPath.mkdirs();
        localizationPath = new File(rootPath, LOCAL);
        localizationPath.mkdirs();
        renderPath = new File(rootPath, RENDER);
        renderPath.mkdirs();
        worldPath = new File(rootPath, SAVE);
        worldPath.mkdirs();
        scriptPath = new File(rootPath, SCRIPT);
        scriptPath.mkdirs();
        pluginPath = new File(rootPath, PLUGINS);
        pluginPath.mkdirs();
        imagePath = new File(rootPath, IMAGE);
        imagePath.mkdirs();
        uiPath = new File(imagePath, UI);
        uiPath.mkdirs();
        sheetPath = new File(imagePath, SHEET);
        sheetPath.mkdirs();
        xmlPath = new File(rootPath, XML);
        xmlPath.mkdirs();
        soundPath = new File(rootPath, SOUNDS);
        soundPath.mkdirs();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora vrati instanciu PathManageru. Kedze je konstruktor privatny
     * tak je tato metoda jedina co dokaze vytvorit instanciu PathManageru => singleton class.
     * 
     * @return Instanciu PathManageru.
     */
    public static PathManager getInstance() {
        if (instance == null) {
            instance = new PathManager();
        }
        return instance;
    }
    
    /**
     * Metoda ktora vrati instanciu PathManageru. Kedze je konstruktor privatny
     * tak je tato metoda jedina co dokaze vytvorit instanciu PathManageru => singleton class.    
     * Metoda je podobna od tej s rovnakym menom ale bez parametrov. V tomto pripade
     * iba nastavujeme na zaciatku argumenty podla ktorych rozhodujeme zakladnu cestu/adresar 
     * odkial nacitavame resource
     * @param args Argumenty podla ktorych vytvarame PathManager.
     * @return Instanciu PathManageru.
     */
    public static PathManager getInstance(String[] args) {
        PathManager.args = args;
        if (instance == null) {
            instance = new PathManager();
        }
        return instance;
    }
    
    
    /**
     * Metoda ktora vrati na akom operacnom system spustame hru. To aky OS ziskame 
     * z parametru <b>os</b> co je vlastne systemova vlastnost <b>os.name</b>.
     * Vraciam bytovu hodnotu kde cisla zodpovedaju operacnym systemom.
     * @param os Text s typom OS.
     * @return Bytova hodnota urcena v triede PathManager v premennych
     */
    private byte getOs(String os) {
        
        if (os.startsWith("Windows")) {
            return WINDOWS;
        }
        if (os.startsWith("Linux")) {
            return LINUX;
        }
        if (os.startsWith("MacOS")) {
            return MAC;
        }
        
        return -1;
    }
    
    /**
     * Metoda ktora vrati adresar ulozenej pozicie. Co to bude za ulozenu poziciu urcuje
     * parameter <b>worldTitle</b>. Priznak create urcuje ci aj vytvarame adresare 
     * ked nahodou neexistuje taky pozicia.
     * @param worldTitle Meno/nazov ulozenej pozicie ktory vratime
     * @param create Priznak urcujuci ci aj vytvaram adresare po ceste k ulozenej pozicii.
     * @return Adresar k ulozenej pozicii
     */
    public File getWorldSavePath(String worldTitle, boolean create) {
        File result = new File(worldPath, worldTitle);        
        if (create)
            result.mkdirs();
        return result;
    }
    
    /**
     * Metoda ktora vrati adresar kde sa nachadzaju xml subory. Co to bude za xml adresar urcuje
     * parameter <b>xmlTitle</b>. Priznak create urcuje ci aj vytvarame adresare 
     * ked nahodou neexistuje taky adresar.
     * @param xmlTitle Meno/nazov xml adresara ktory vratime
     * @param create Priznak urcujuci ci aj vytvaram adresare po ceste k xml adresaru
     * @return Adresar k xml suborom
     */
    public File getXmlSavePath(String xmlTitle, boolean create) {
        File result = new File(xmlPath, xmlTitle);
        if (create) {
            result.mkdirs();
        }
        return result;
    }
    
    /**
     * Metoda ktora vrati adresar kde sa nachadzaju script subory. Co to bude za script adresar urcuje
     * parameter <b>scriptTitle</b>. Priznak create urcuje ci aj vytvarame adresare 
     * ked nahodou neexistuje taky adresar.
     * @param scriptTitle Meno/nazov script adresara ktory vratime
     * @param create Priznak urcujuci ci aj vytvaram adresare po ceste k script adresaru
     * @return Adresar k script suborom
     */
    public File getScriptSavePath(String scriptTitle, boolean create) {
        File result = new File(scriptPath, scriptTitle);
        if (create) {
            result.mkdirs();
        }
        return result;
    }    
    
    /**
     * Metoda ktora vrati adresar kde sa nachadzaju sound subory. Co to bude za zvukovy adresar urcuje
     * parameter <b>soundTitle</b>. Priznak create urcuje ci aj vytvarame adresare 
     * ked nahodou neexistuje taky adresar.
     * @param soundTitle Meno/nazov sound adresara ktory vratime
     * @param create Priznak urcujuci ci aj vytvaram adresare po ceste k sound adresaru
     * @return Adresar k sound suborom
     */
    public File getSoundPath(String soundTitle, boolean create) {
        File result = new File(soundPath, soundTitle);
        if (create) {
            result.mkdirs();
        }
        return result;
    } 
    
    /**
     * Metoda ktora vrati adresar k ulozenym poziciam
     * @return Adresar ulozenych pozicii.
     */
    public File getWorldPath() {
        return worldPath;
    }
    
    /**
     * Metoda ktora vrati korenovu cestu
     * @return Korenova cesta
     */
    public File getRootPath() {
        return rootPath;
    }
    
    /**
     * Metoda ktora vrati cestu ku zvukovym suborom
     * @return Aresar zvukovych stop
     */
    public File getSoundPath() {
        return soundPath;
    }
    
    /**
     * Metoda ktora vrati adresar kde su vsetky skripty.
     * @return Adresar so vsetkymi skriptami
     */
    public File getScriptPath() {
        return scriptPath;
    }
    
    /**
     * Metoda ktora vrati adresar kde su vsetky adresary s pluginmi
     * @return Adresar so vsetkymi pluginmi
     */
    public File getPluginsPath() {
        return pluginPath;
    }    
    
    /**
     * Metoda ktora vrati adresar ku xml suborom.
     * @return Adresar ku vsetkym adresarom ktore obsahuju xml subory
     */
    public File getXmlPath() {
        return xmlPath;
    }
    
    /**
     * Metoda ktora vrati adresar s obrazkami
     * @return Adresar ku vsetkym obrazkom
     */
    public File getImagePath() {
        return imagePath;                
    }
    
    /**
     * Metoda ktora vrati adresar s ui obrazkami.
     * @return Adresar ku ui obrazkom
     */
    public File getUiPath() {
        return uiPath;
    }
    
    /**
     * Metoda ktora vrati adresar so sheet obrazkami.
     * @return Adresar ku sheet obrazkom.
     */
    public File getSheetPath() {
        return sheetPath;
    }
    
    /**
     * Metoda ktora vrati adresar s render pluginmi
     * @return Adresar s render pluginmi.
     */
    public File getRenderPath() {
        return renderPath;
    }
    
    /**
     * Metoda ktora vrati adresar s textami/resourcebundle.
     * @return Adresar ku textom pouzitych v hre.
     */
    public File getLocalizationPath() {
        return localizationPath;
    }
    
    // </editor-fold>
    
    
}
