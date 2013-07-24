/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.scripting;

import rpgcraft.plugins.ScriptLibraryPlugin;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.TwoArgFunction;
import rpgcraft.panels.listeners.ActionEvent;
import rpgcraft.panels.listeners.ListenerFactory;

/**
 * Vytvorena trieda GameLibrary ktora dedi od ScriptLibraryPlugin. V podstate to je natvrdo
 * vytvoreny plugin (dalsou moznostou je vytvarat pluginy pomimo tohoto projektu). V triede
 * su definovane mena pre zaregistrovane metody ktore sa daju pouzit v lua skriptoch.
 * Ako kazdy plugin aj tento musi implementovat metody call(volana pri nacitani pluginu) a run 
 * (nevyuzita metoda v tejto kniznici). Trieda je vyuzivana iba pri vyvolavani skriptov a
 * inak sa snou nepracuje.
 */
public class GameLibrary extends ScriptLibraryPlugin {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    /**
     * Logger pre Kniznicu
     */
    private static final Logger LOG = Logger.getLogger(GameLibrary.class.getName());
    
    /**
     * Meno pre kniznicu
     */
    public static final String LIB_NAME = "game";
    
    /**
     * Mena pre jedno argumentove funkcie
     */
    public static final String[] ONEARG_NAMES = new String[] {
        "log",
        "value",
        "callListener"
    };
    
    /**
     * Mena pre dvoj argumentove funkcie
     */
    public static final String[] TWOARG_NAMES = new String[] {
        "setValue"
    };
    
    /**
     * Instancia GameLibrary pre navrat z metody getInstance
     */
    private static GameLibrary instance;
    // </editor-fold>
    
    /**
     * Privatny konstruktor pre vytvoreni instancie GameLibrary. Mozno pristupne
     * iba z vnutra tejto triedy.
     */
    private GameLibrary() {
        
    }
    
    /**
     * Metoda getInstance ktora vrati instanciu GameLibrary (singleton navrhovy vzor).
     * @return Instancia GameLibrary
     */
    public static GameLibrary getInstance() {
        if (instance == null) {
            instance = new GameLibrary();
        }
        return instance;
    }
    
    /**
     * <i>{@inheritDoc }</i>
     * <p>
     * Metoda ktora je volana pri nacitani kniznice k ostatnym knizniciam.
     * Bindujeme staticke finalne triedy s menami : <br>
     * ONEARG_NAMES spolu GameLib1 kedze trieda dedi od OneArgFunction <br>
     * TWOARG_NAMES spolu GameLib2 kedze trieda dedi od TwoArgFunction <br>
     * Kniznica bude vystupovat pod menom LIB_NAME
     * </p>
     * @param lv {@inheritDoc }
     * @return Tabulka s nadefinovanymi funkcia
     */
    @Override
    public LuaValue call(LuaValue lv) {
        LuaTable lt = tableOf();
                
        bind(lt, GameLib1.class, ONEARG_NAMES);
        bind(lt, GameLib2.class, TWOARG_NAMES);
        
        env.set(LIB_NAME, lt);
        PackageLib.instance.LOADED.set(LIB_NAME, lt);
        return lt;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void run() {
        
    }
    
    /**
     * Staticka finalna trieda dediaca od OneArgFunction. Metoda sa sklada zo 
     * switchu ktory dostava opcode (cisla od 0) na porovnanie. Jednotlive vetvy zodpovedaju
     * priamo hodnotam v ONEARG_NAMES. V kazdej vetve potom vykonavame prislusne funkcie.          
     */
    public static final class GameLib1 extends OneArgFunction {

        /**
         * Metoda call ktora vykonava metody z lua skriptu
         * @param lv Parameter vyvolanej metody
         * @return Navratova hodnota z prislusnej funkcie
         */
        @Override
        public LuaValue call(LuaValue lv) {            
            switch (opcode) {
                case 0 : {
                    LOG.log(Level.INFO, lv.checkjstring());
                    return TRUE;
                }
                case 1 : {                    
                    return LuaValue.userdataOf(GameLibrary.getValue(lv.checkjstring()));                    
                }  
                case 2 : {                    
                    ListenerFactory.getListener(lv.checkjstring(), false).actionPerformed(
                            ActionEvent.getScriptActionEvent(Thread.currentThread().getId()));
                    return TRUE;
                }
                default : return  NIL;
            }
        }
        
    }
    
    /**
     * Staticka finalna trieda dediaca od TwoArgFunction. Metoda sa sklada zo 
     * switchu ktory dostava opcode (cisla od 0) na porovnanie. Jednotlive vetvy zodpovedaju
     * priamo hodnotam v TWOARG_NAMES. V kazdej vetve potom vykonavame prislusne funkcie.          
     */
    public static final class GameLib2 extends TwoArgFunction {
        /**
         * Metoda call ktora vykonava metody z lua skriptu
         * @param lv1 Prvy parameter vyvolanej metody
         * @param lv2 Druhy parameter vyvolanej metody
         * @return Navratova hodnota funkcie
         */
        @Override
        public LuaValue call(LuaValue lv1, LuaValue lv2) {            
            switch (opcode) {
                case 0 : {          
                    if (!lv2.isnil()) {
                        GameLibrary.setValue(lv1.checkjstring(), lv2.checknotnil());
                    }
                    return NIL;
                }                
                default : return  NIL;
            }
        }
       
    }
    
}
