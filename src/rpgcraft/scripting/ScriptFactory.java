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
 * Trieda ktora tvori zakladnu triedu pre nacitanie skriptov. Kedze trieda ma len privatny konstruktor
 * tak jediny sposob ako ziskat instanciu je metoda getInstance (singleton). Instancia
 * tejto triedy potom dokaze nacitavat (metoda addLibrary) a nastavovat ktore lua-kniznice chceme a budeme pouzivat
 * (metoda loadLibraries). Taktiez poskytuje moznost vykonavat lua skripty priamo z kodu 
 * pomocou metod eval.
 */
public class ScriptFactory {
    // <editor-fold defaultstate="collapsed" desc=" Premnne ">
    private static final Logger LOG = Logger.getLogger(ScriptFactory.class.getName());
    
    private static ArrayList<ScriptLibraryPlugin> libs;    
    
    private static ScriptFactory instance;
    
    private ScriptEngineManager sem;
    private ScriptEngine e ;
    private ScriptEngineFactory f;
    
    private LuaTable _G;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Privatny konstruktor ktory vytvara instanciu ScriptFactory. V konstruktore
     * inicializujeme manazer, lua-engine, factory a standardne globalne premenne 
     * (alebo tiez aj zakladne kniznice spristupnene bez pridavanie nejakych kniznic)
     */
    private ScriptFactory() {
        sem = new ScriptEngineManager();
        e = sem.getEngineByExtension(".lua");
        f = e.getFactory();         
        _G = JsePlatform.standardGlobals();  
        
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Staticke metody ">
    /**
     * Metoda ktora vrati instanciu ScriptFactory volanim privatneho konstruktora.
     * Jediny sposob ako vytvorit instanciu tohoto objektu. Po vytvoreni pridame
     * do instancie nami vytvorenu kniznicu GameLibrary a pomocou metody loadLibraries 
     * nacitame do ScriptFactory kniznice ktore sme si pridali do listu libs.
     * Pri resete treba pouzit metodu resetGlobals.
     * @return ScriptFactory instancia
     */
    public static ScriptFactory getInstance() {
        if (instance == null) {
            instance = new ScriptFactory();
            instance.addLibrary(GameLibrary.getInstance());
            instance.loadLibraries();
        }
        return instance;
    }            
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Kniznicne metody ">
    /**
     * Metoda ktora nastavi kniznice ktore sa budu pouzivat z parametru <b>libs</b>
     * @param libs Kniznice ktore budeme pouzivat.
     */
    public static void setLibraries(ArrayList<ScriptLibraryPlugin> libs) {
        ScriptFactory.libs = libs;
    }
    
    /**
     * Metoda ktora prida kniznicu ktora sa bude pouzivat z parametru <b>lib</b>.
     * @param lib Kniznica ktoru nacitavame do kniznic.
     */
    public static void addLibrary(ScriptLibraryPlugin lib) {
        if (libs == null) {
            libs = new ArrayList<>();
        }
        
        libs.add(lib);
    } 
    
    /**
     * Metoda ktora nacita nami definovane kniznice z premennej <b>libs</b> tak ze volame metodu load
     * pre premennu _G ktora tvori vsetky nacitane kniznice a metody co sa daju
     * v lua skriptoch pouzit.
     */
    private void loadLibraries() {
        for (ScriptLibraryPlugin lib : libs) {
            _G.load(lib);
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Globals ">
    /**
     * Metoda ktora zresetuje premennu _G, ktora tvori vsetky nacitane kniznice a metody co sa daju
     * pouzit v lua skriptoch. Zresetovanie prebieha tak ze vratime pomocou metody standardGlobals
     * novo vytvorenu tabulku s takymito udajmi. Nasledne volame loadLibraries pre nacitanie
     * nasich kniznic.
     */
    public void resetGlobals() {
        _G = JsePlatform.standardGlobals();
        loadLibraries();
    }
    
    /**
     * Metoda ktora vrati premennu _G, tabulku ktora tvori vsetky nacitane kniznice
     * a metody co sa daju pouzit v lua skriptoch.
     * @return Tabulka/standarne globalne premenne.
     */
    public LuaTable getGlobals() {
        return _G;
    }    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Evaluacia skriptov z kodu ">
    /**
     * Metoda ktora vykona prikaz zadany parametrom <b>statemenet</b>. Vykonavanie
     * spravime zavolanim metody eval pre nas definovany engine.
     * @param statement Text s prikazom ktory vykonavame.
     * @throws ScriptException Skriptovacia vynimka.
     */
    public void eval(String statement) throws ScriptException {
        e.eval(statement);
    }
    
    /**
     * Metoda ktora vykona skompilovany prikaz zadany parametrom <b>cs</b>. Vykonavanie
     * spravime zavolanim metody eval na tento skompilovany prikaz. Ako parameter dostava
     * eval Bindings ktore mozu obsahovat vstupne informacie pre vykonanie.
     * (napr. ze sa ma nacitat nejaka kniznica).
     * @param cs Skompilovany prikaz
     * @param b Bindings podla ktoreho vykonavame skript.
     * @throws ScriptException Skriptovacia vynimka.
     */
    public void eval(CompiledScript cs, Bindings b) throws ScriptException {
        cs.eval(b);
    }
    
    /**
     * Metoda ktora vykona prikaz zadany parametrom <b>statemenet</b>. Vykonavanie
     * spravime vytvorenim skompilovaneho skriptu pomocou metody compile.
     * Skompilovany prikaz nasledne vykona skript pomocou bindingov.
     * @param statement Text s prikazom ktory vykonavame.
     * @param b Bindings podla ktorych vykonavame skript.
     * @throws ScriptException Skriptovacia vynimka.
     */
    public void eval(String statement, Bindings b) throws ScriptException {
        CompiledScript cs = ((Compilable)e).compile(statement);
        cs.eval(b);
    }
    
    /**
     * Metoda ktora vykona prikaz zadany parametrom <b>statemenet</b>. Vykonavanie
     * spravime vytvorenim skompilovaneho skriptu pomocou metody compile.
     * Skompilovany prikaz nasledne vykona skript pomocou bindingov. Bindingy si vytvorime
     * pomocou enginu a nasledne donho nasypeme dvojice [vars,vals].
     * @param statement Text s prikazom ktory vykonavame.
     * @param vars Premenne ktore pridame do bindingov
     * @param vals Hodnoty premennych ktore pridame do bindingov
     * @throws ScriptException Skriptovacia vynimka.
     */
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
    // </editor-fold>
}
