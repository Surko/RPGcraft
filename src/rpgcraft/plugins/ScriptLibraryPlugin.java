/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.plugins;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import rpgcraft.panels.listeners.ActionEvent;
import rpgcraft.scripting.ScriptFactory;
import rpgcraft.utils.DataUtils;
import rpgcraft.utils.ScriptUtils;

/**
 *
 * @author kirrie
 */
public abstract class ScriptLibraryPlugin extends OneArgFunction {        
    /**
     * Vykona knihovnu akciu. Nacita knihovnu, atd...
     */
    public abstract void run();
        
    @Override
    public abstract LuaValue call(LuaValue lv);        
    
    public static void setValue(String str, Object obj) {        
        ScriptFactory.getInstance().put(str, obj);
    }
    
    public static Object getValue(String str) {
        Object object = ScriptFactory.getInstance().get(str);
        if (object != null) {
            return object;
        }
        
        return DataUtils.getValueOfVariable(str);
    }          
            
}
