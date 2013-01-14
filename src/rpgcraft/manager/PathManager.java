/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.manager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton trieda so vsetkymi ulozenymi cestami pre subory.
 * @author 
 */
public final class PathManager {
    private static final String WORLD = "maps";
    private static final String LOG = "logs";
    private static final String SCRIPT = "scripts";
    private static final String XML = "xml";
    private static final String IMAGE = "images";
    private static final String UI = "ui";
    private static final String SHEET = "sheets";
    
    private static PathManager instance;
    private static String[] args;
    
    private static final byte LINUX = 0;
    private static final byte WINDOWS = 1;
    private static final byte MAC = 2;
    
    private File rootPath;
    private File worldPath;
    private File logPath;
    private File scriptPath;
    private File xmlPath;
    private File imagePath;
    private File uiPath;
    private File sheetPath;
    
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
            return 1;
        }
        if (os.startsWith("Linux")) {
            return 0;
        }
        if (os.startsWith("MacOS")) {
            return 2;
        }
        
        return -1;
    }
    
    public File getWorldSavePath(String worldTitle) {
        File result = new File(worldPath, worldTitle);
        result.mkdirs();
        return result;
    }
    
    public File getXmlSavePath(String xmlTitle) {
        File result = new File(xmlPath, xmlTitle);
        result.mkdirs();
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
    
    
    
    private void updateDirs() {
        rootPath.mkdirs();
        worldPath = new File(rootPath, WORLD);
        worldPath.mkdirs();
        logPath = new File(rootPath, LOG);
        logPath.mkdirs();
        scriptPath = new File(rootPath, SCRIPT);
        scriptPath.mkdirs();
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
