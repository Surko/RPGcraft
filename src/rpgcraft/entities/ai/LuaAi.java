/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities.ai;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import rpgcraft.entities.MovingEntity;
import rpgcraft.plugins.Ai;
import rpgcraft.resource.StringResource;
import rpgcraft.utils.ScriptUtils;

/**
 * Lua inteligencia pre pohyblive entity pri aktualizacii entit. Trieda dedi od Ai => musi mat implementovane
 * abstraktne metody z triedy Ai. Zaklad inteligencie je 
 * v metode aiMove, ktora zavola lua skript
 * @author kirrie
 */
public class LuaAi extends Ai{
    private static final Logger LOG = Logger.getLogger(LuaAi.class.getName());
    private String sFunction;
    private LuaFunction luaFunction;
    
    /**
     * Konstruktor ktory vytvori novu instanciu LuaInteligencie. Skript s inteligenciou nacitame
     * pomocou ScriptUtils a ulozime ako LuaFunction. Text s funkciou si tiez ulozime.
     * @see ScriptUtils
     * @see LuaFunction
     */
    private LuaAi(String luaAi) {
        try {
            sFunction = luaAi;
            luaFunction = ScriptUtils.loadScript(luaAi, null);            
        } catch (IOException ex) {
            LOG.log(Level.WARNING, StringResource.getResource("_mluascript"), ex);            
        }
    }
    
    /**
     * Metoda ktora vrati Lua inteligenciu podla parametru luaAi. Otestuje ci uz taka existuje
     * a ked ano, tak hu vrati. Ked neexistuje tak vytvori novu, ulozi v liste a vrati novo vytvorenu.
     * @param luaAi Text s cestou k lua inteligencii.
     * @return LuaInteligencia podla parametru
     */
    public static LuaAi getLuaAi(String luaAi) {
        Ai ai = aiList.get(luaAi);
        if (ai == null || !(ai instanceof LuaAi)) {
            LuaAi newLuaAi = new LuaAi(luaAi);
            aiList.put(luaAi, newLuaAi);
            return newLuaAi;
        }
        return (LuaAi)ai;
    }
    
    /**
     * Metoda ktora vrati meno tejto inteligencie
     * @return Meno inteligencie
     */
    @Override
    public String getName() {
        return sFunction;
    }

    /**
     * Metoda ktora vykona pohyb entity podla lua skriptu. Na objekt LuaFunction zavolame
     * metodu call s parametrom entity aby mohol skript spracovat pohyby entity.
     * @param e Entita s ktorou pohybujeme
     * @return True/false ci sa podaril pohyb
     */
    @Override
    public boolean aiMove(MovingEntity e) {
        if (luaFunction != null) {
            return luaFunction.call(LuaValue.userdataOf(e)).checkboolean();
        }
        return true;
    }
    
}
