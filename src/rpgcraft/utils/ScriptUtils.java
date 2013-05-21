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
 *
 * @author kirrie
 */
public class ScriptUtils {        
    
    public static LuaTable getGlobals() {
        return ScriptFactory.getInstance().getGlobals();                
    }        
    
    public static void resetGlobals() {
        ScriptFactory.getInstance().resetGlobals();
    }
    
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
    
    public static LuaFunction loadScript(String sFile, LuaTable globals, ActionEvent e) throws IOException {
        File script = PathManager.getInstance().getScriptSavePath(sFile, false);
        return LoadState.load( new FileInputStream(script), sFile, globals );
    }
    
    public static LuaFunction loadScript(String sFile, ActionEvent e) throws IOException {
        File script = PathManager.getInstance().getScriptSavePath(sFile, false);
        LuaTable _G = ScriptFactory.getInstance().getGlobals();
        System.out.println(Thread.currentThread());
        return LoadState.load( new FileInputStream(script), sFile, _G );
    }
    
    public static void callLoadScript(String sFile, LuaTable globals, ActionEvent e) throws IOException {
        if (globals != null) {
            loadScript(sFile, globals, e).call();
            return;
        }
        
        loadScript(sFile, e).call();
    }
    
    public static void callLoadScript(String sFile, ActionEvent e) throws IOException {
        loadScript(sFile, e).call();
    }
     
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
    
    public static void setValue(String str, Object obj) {
        ScriptFactory.getInstance().put(str, obj);
    }
    
    public static Object getValue(String str) {
        Object object = ScriptFactory.getInstance().get(str);
        return object;
    }
}
