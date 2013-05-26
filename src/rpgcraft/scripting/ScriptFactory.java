/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.scripting;

import rpgcraft.plugins.ScriptLibraryPlugin;
import java.awt.Color;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.lib.jse.JsePlatform;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.resource.StringResource;

/**
 *
 * @author kirrie
 */
public class ScriptFactory {
    
    private static final Logger LOG = Logger.getLogger(ScriptFactory.class.getName());
    
    private static ArrayList<ScriptLibraryPlugin> libs;    
    
    private static ScriptFactory instance;
    
    private ScriptEngineManager sem;
    private ScriptEngine e ;
    private ScriptEngineFactory f;
    
    private LuaTable _G;
    
    private ScriptFactory() {
        sem = new ScriptEngineManager();
        e = sem.getEngineByExtension(".lua");
        f = e.getFactory();         
        _G = JsePlatform.standardGlobals();  
        
    }
    
    public static ScriptFactory getInstance() {
        if (instance == null) {
            instance = new ScriptFactory();
            instance.addLibrary(GameLibrary.getInstance());
            instance.loadLibraries();
        }
        return instance;
    }            
    
    public static void setLibraries(ArrayList<ScriptLibraryPlugin> libs) {
        ScriptFactory.libs = libs;
    }
    
    public static void addLibrary(ScriptLibraryPlugin lib) {
        if (libs == null) {
            libs = new ArrayList<>();
        }
        
        libs.add(lib);
    } 
    
    private void loadLibraries() {
        for (ScriptLibraryPlugin lib : libs) {
            _G.load(lib);
        }
    }
    
    public void resetGlobals() {
        _G = JsePlatform.standardGlobals();
        loadLibraries();
    }
    
    public LuaTable getGlobals() {
        return _G;
    }    
    
    public void eval(String statement) throws ScriptException {
        e.eval(statement);
    }
    
    public void eval(CompiledScript cs, Bindings b) throws ScriptException {
        cs.eval(b);
    }
    
    public void eval(String statement, Bindings b) throws ScriptException {
        CompiledScript cs = ((Compilable)e).compile(statement);
        cs.eval(b);
    }
    
    public void eval(String statement, String[] vars, Object[] vals) throws ScriptException {
        if (vars.length != vals.length) {
            LOG.log(Level.SEVERE, StringResource.getResource("_vparams"));
            new MultiTypeWrn(null, Color.red, StringResource.getResource("_vparams"), null).renderSpecific("_label_scripterror");
            return;
        }
        
        CompiledScript cs = ((Compilable)e).compile(statement);
        
        Bindings b = e.createBindings();
        for (int i = 0; i < vars.length; i++) {
            b.put(vars[i], vals[i]);
        }
        
        cs.eval(b);
        
    }
    
    public void put(String var, Object val) {
        e.put(var, val);                
    }        
    
    public Object get(String var) {
        return e.get(var);
        
    }
    
}
