/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.manager;

import java.io.File;

/**
 * Singleton trieda so vsetkymi ulozenymi cestami pre subory.
 * @author 
 */
public final class PathManager {
    public static final String MAPS = File.separator + "maps";    
    
    public static final String MAPLUG = "map-plugins";
    public static final String SCRIPTPLUG = "script-plugins";
    public static final String INMENUPLUG = "inmenu-plugins";
    public static final String MENUPLUG = "menu-plugins";
    public static final String ITEMGENPLUG = "itemgen-plugins";
    
    public static final String RENDER = "render";   
    private static final String SAVE = "saves";
    private static final String LOG = "logs";
    private static final String SCRIPT = "scripts";
    private static final String PLUGINS = "plugins";        
    private static final String XML = "xml";
    private static final String IMAGE = "images";
    private static final String UI = "ui";
    private static final String SHEET = "sheets";
    private static final String LOCAL = "locales";
    
    private static PathManager instance;
    private static String[] args;
    
    private static final byte LINUX = 0;
    private static final byte WINDOWS = 1;
    private static final byte MAC = 2;
    
    private File renderPath;
    private File rootPath;
    private File worldPath;
    private File logPath;
    private File scriptPath;
    private File pluginPath;
    private File xmlPath;
    private File imagePath;
    private File uiPath;
    private File sheetPath;
    private File localizationPath;
    
    private PathManager() {
        determineRootPath(true);
    }
    
    public static PathManager getInstance() {
        if (instance == null)
            instance = new PathManager();
        return instance;
    }
    
    public static PathManager getInstance(String[] args) {
        PathManager.args = args;
        if (instance == null)
            instance = new PathManager();
        return instance;
    }
    
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
    
    public File getWorldSavePath(String worldTitle, boolean create) {
        File result = new File(worldPath, worldTitle);        
        if (create)
            result.mkdirs();
        return result;
    }
    
    public File getXmlSavePath(String xmlTitle, boolean create) {
        File result = new File(xmlPath, xmlTitle);
        if (create) {
            result.mkdirs();
        }
        return result;
    }
    
    public File getScriptSavePath(String scriptTitle, boolean create) {
        File result = new File(scriptPath, scriptTitle);
        if (create) {
            result.mkdirs();
        }
        return result;
    }        
    
    public File getLogPath() {
        return logPath;
    }
    
    public File getWorldPath() {
        return worldPath;
    }
    
    public File getRootPath() {
        return rootPath;
    }
    
    public File getScriptPath() {
        return scriptPath;
    }
    
    public File getPluginsPath() {
        return pluginPath;
    }    
    
    public File getXmlPath() {
        return xmlPath;
    }
    
    public File getImagePath() {
        return imagePath;                
    }
    
    public File getUiPath() {
        return uiPath;
    }
    
    public File getSheetPath() {
        return sheetPath;
    }
    
    public File getRenderPath() {
        return renderPath;
    }
    
    public File getLocalizationPath() {
        return localizationPath;
    }
    
    
    private void updateDirs() {
        rootPath.mkdirs();
        localizationPath = new File(rootPath, LOCAL);
        localizationPath.mkdirs();
        renderPath = new File(rootPath, RENDER);
        renderPath.mkdirs();
        worldPath = new File(rootPath, SAVE);
        worldPath.mkdirs();
        logPath = new File(rootPath, LOG);
        logPath.mkdirs();
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
    }
    
}
