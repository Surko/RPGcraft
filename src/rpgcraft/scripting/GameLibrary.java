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

/**
 *
 * @author kirrie
 */
public class GameLibrary extends ScriptLibraryPlugin {

    private static final Logger LOG = Logger.getLogger(GameLibrary.class.getName());
    
    public static final String LIB_NAME = "game";
    
    public static final String[] ONEARG_NAMES = new String[] {
        "log",
        "value",        
    };
    
    public static final String[] TWOARG_NAMES = new String[] {
        "setValue"
    };
    
    private static GameLibrary instance;
    
    private GameLibrary() {
        
    }
    
    public static GameLibrary getInstance() {
        if (instance == null) {
            instance = new GameLibrary();
        }
        return instance;
    }
    
    @Override
    public LuaValue call(LuaValue lv) {
        LuaTable lt = tableOf();
                
        bind(lt, GameLib1.class, ONEARG_NAMES);
        bind(lt, GameLib2.class, TWOARG_NAMES);
        
        env.set(LIB_NAME, lt);
        PackageLib.instance.LOADED.set(LIB_NAME, lt);
        return lt;
    }

    @Override
    public void run() {
        
    }
    
    public static final class GameLib1 extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue lv) {            
            switch (opcode) {
                case 0 : {
                    LOG.log(Level.INFO, lv.checkjstring());
                    return NIL;
                }
                case 1 : {                    
                    return LuaValue.userdataOf(GameLibrary.getValue(lv.checkjstring()));                    
                }                
                default : return  NIL;
            }
        }
        
    }
    
    public static final class GameLib2 extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue lv1, LuaValue lv2) {
            System.out.println(Thread.currentThread());
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
