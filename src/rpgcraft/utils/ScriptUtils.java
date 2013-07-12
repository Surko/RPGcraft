/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import rpgcraft.manager.PathManager;
import rpgcraft.panels.listeners.ActionEvent;
import rpgcraft.plugins.ScriptLibraryPlugin;
import rpgcraft.scripting.ScriptFactory;

/**
 * Utility trieda ktora v sebe zdruzuje rozne metody pre pracu so skriptami. Trieda je cela staticka =>
 * mozne k nej pristupovat z kazdej inej triedy ci instancie. 
 * Metoda dokaze nacitavat a volat skripty a
 * nastavovat/resetovat/vratit globalne premenne zo ScriptFactory.
 */
public class ScriptUtils {        
    
    /**
     * Metoda vrati tabulku (globalne premenne) s nacitanymi kniznicami pri vykonavani
     * lua skriptov.     
     * @return Tabulka s nacitanymi kniznicami + datami
     */
    public static LuaTable getGlobals() {
        return ScriptFactory.getInstance().getGlobals();                
    }        
    
    /**
     * Metoda ktora zresetuje tabulky (globalne premnne) s nacitanymi kniznicami pri vykonavani
     * lua skriptov
     */
    public static void resetGlobals() {
        ScriptFactory.getInstance().resetGlobals();
    }
    
    /**
     * Metoda ktora vykona skript ktoreho cesta je zadana suborom <b>sFile</b>.
     * Parametre reqLibs urcuju ake kniznice nacitavame pre vykonanie skriptu
     * @param sFile Cesta k suboru ktory volame
     * @param reqLibs Kniznice ktore chceme dodatocne nacitat
     */
    public static void doFile(String sFile, ScriptLibraryPlugin ... reqLibs) {
        LuaTable _G = ScriptFactory.getInstance().getGlobals();
        if (reqLibs != null) {            
            try {
                for (ScriptLibraryPlugin lib : reqLibs) {
                    _G.load(lib);
                }
            } catch (Exception e) {
                
            }
        }
        
        _G.get("dofile").call( LuaValue.valueOf(sFile) );
    }
    
    /**
     * Metoda ktora nacita skript s cestou <b>sFile</b> podla kniznic zadanych <b>globals</b>.
     * Vykonanie skriptu bude vykonane s ActionEventom <b>e</b>. Metoda nastavuje do triedy ActionEvent aky thread vykonava
     * actionevent.
     * @param sFile Cesta k suboru
     * @param globals Globalne premnne (nacitane kniznice)
     * @param e ActionEvent pri vykonavani skriptu
     * @return Nacitana lua-funkcia ktoru mozme vykonat
     * @throws IOException Vynimka pri chybe so suborom
     */
    public static LuaFunction loadScript(String sFile, LuaTable globals, ActionEvent e) throws IOException {
        File script = PathManager.getInstance().getScriptSavePath(sFile, false);
        if (e != null) {
            ActionEvent.setScriptActionEvent(Thread.currentThread().getId(), e);
        }
        return LoadState.load( new FileInputStream(script), sFile, globals );
    }
    
     /**
     * Metoda ktora nacita skript s cestou <b>sFile</b> podla zakladnych kniznic
     * nadefinovanych v ScriptFactory. Metoda nastavuje do triedy ActionEvent aky thread vykonava
     * actionevent.
     * Skript bude vykonany s ActionEventom <b>e</b>.
     * @param sFile Cesta k suboru
     * @param e ActionEvent pri vykonavani skriptu
     * @return Nacitana lua-funkcia ktoru mozme vykonat
     * @throws IOException Vynimka pri chybe so suborom
     */
    public static LuaFunction loadScript(String sFile, ActionEvent e) throws IOException {
        File script = PathManager.getInstance().getScriptSavePath(sFile, false);
        LuaTable _G = ScriptFactory.getInstance().getGlobals();    
        if (e != null) {
            ActionEvent.setScriptActionEvent(Thread.currentThread().getId(), e);
        }
        return LoadState.load( new FileInputStream(script), sFile, _G );
    }
    
    /**
     * Metoda ktora nacita a vykona skript s cestou <b>sFile</b> podla kniznic zadanych <b>globals</b>.
     * Skript bude vykonany s ActionEventom <b>e</b>. Metoda nastavuje do triedy ActionEvent aky thread vykonava
     * actionevent. 
     * @param sFile Cesta k suboru
     * @param globals Globalne premnne (nacitane kniznice)
     * @param e ActionEvent pri vykonavani skriptu
     * @throws IOException Vynimka pri chybe so suborom
     */
    public static void callLoadScript(String sFile, LuaTable globals, ActionEvent e) throws IOException {
        if (globals != null) {
            loadScript(sFile, globals, e).call();
            ActionEvent.removeActionEvent(Thread.currentThread().getId());
            return;
        }
        
        loadScript(sFile, e).call();
        ActionEvent.removeActionEvent(Thread.currentThread().getId());
    }
    
    /**
     * Metoda ktora nacita a vykona skript s cestou <b>sFile</b> podla zakladnych kniznic
     * nadefinovanych v ScriptFactory. Metoda nastavuje do triedy ActionEvent aky thread vykonava
     * actionevent.
     * Skript bude vykonany s ActionEventom <b>e</b>.
     * @param sFile Cesta k suboru
     * @param e ActionEvent pri vykonavani skriptu     
     * @throws IOException Vynimka pri chybe so suborom
     */
    public static void callLoadScript(String sFile, ActionEvent e) throws IOException {
        loadScript(sFile, e).call();
        ActionEvent.removeActionEvent(Thread.currentThread().getId());
    }
     
    /**
     * Metoda ktora vykona skript ktoreho cesta je zadana suborom <b>sFile</b>.
     * Parametre reqLibs urcuju ake kniznice nacitavame pre vykonanie skriptu.               
     * @param sFile Cesta k suboru
     * @param reqLibs Kniznice ktore chceme dodatocne nacitat
     * @throws IOException Vynimka pri chybe so suborom
     */
    public static void callScript(String sFile, ScriptLibraryPlugin ... reqLibs) throws IOException {
        File script = PathManager.getInstance().getScriptSavePath(sFile, false);
        LuaTable _G = ScriptFactory.getInstance().getGlobals();
        if (reqLibs != null) {            
            try {
                for (ScriptLibraryPlugin lib : reqLibs) {
                    _G.load(lib);
                }
            } catch (Exception e) {
                
            }
        }
        LoadState.load( new FileInputStream(script), sFile, _G ).call();
    }
    
    /**
     * Metoda ktora vykona skript ktoreho cesta je zadana suborom <b>sFile</b>.
     * Parametre reqLibs urcuju ake kniznice nacitavame pre vykonanie skriptu. Kniznice
     * nacitavame go tabulky (s nacitanymi kniznicami) <b>globals</b>.
     * @param sFile Cesta k suboru
     * @param globals Premenne v ktorych sa nachadzaju doposial nacitane kniznice
     * @param reqLibs Kniznice ktore chceme dodatocne nacitat
     * @throws IOException Vynimka pri chybe so suborom
     */
    public static void callScript(String sFile, LuaTable globals, ScriptLibraryPlugin ... reqLibs) throws IOException {
        File script = PathManager.getInstance().getScriptSavePath(sFile, false);        
        if (reqLibs != null) {            
            try {
                for (ScriptLibraryPlugin lib : reqLibs) {
                    globals.load(lib);
                }
            } catch (Exception e) {                
            }
        }
        LoadState.load( new FileInputStream(script), sFile, globals ).call();
    }        
}
