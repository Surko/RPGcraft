/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.plugins;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import rpgcraft.utils.DataUtils;

/**
 * Trieda ktora tvori interface pre vsetky skriptovacie pluginy typu LUA. Trieda dedi
 * od OneArgFunction, co je v pripade definicie pluginovacieho interfacu jedno, pretoze
 * kniznice nebude nikdy volana ako metoda. Interface zdruzuje zakladne metody, ktore musi
 * kazdy plugin implementovat pre spravne fungovanie pluginu. Z nich najdolezitejsia je metoda call
 * ktora registruje kniznicu a do kniznice registruje (bindne) dalsie triedy ktore uz vykonavaju
 * jednotlive metody definovane v lua skriptoch. Trieda taktiez poskytuje dve metody ktorymi
 * mozme pridavat a odoberat premenne do DataUtils. Tieto premenne su pristupne ako
 * z listenerov tak aj z lua skriptvo (pri dodefinovani takeho pristupu).
 */
public abstract class ScriptLibraryPlugin extends OneArgFunction {        
    /**
     * Vykona knihovnu akciu. Nacita knihovnu, atd...
     */
    public abstract void run();
       
    /**
     * Metoda ktora je volana pri kazdom zavolani skriptu. V metode by sme mali nastavit tabulku
     * pomocou metody tableOf. Potom musime spravit bind triedy ktora dedi od One/Two/atd...Function s
     * nadefinovanymi menami pre tieto metody v lua. V tomto bode sa prepajaju nadefinovane funkcie
     * s menami a vysledok sa vykazuje vo vytvorenej tabulke. Nakonci registrujeme tabulku a pridame do nacitanych
     * kniznic pod nejakym menom pod ktorym budem vystupovat pri volani funkcii z lua skriptu.
     * @param lv LuaValue objekt. 
     * @return Tabulku s nadefinovanou kniznicou
     */
    @Override
    public abstract LuaValue call(LuaValue lv);        
    
    /**
     * Metoda ktora nastavi do ulozenych premennych novy dvojicu. Nazov premennej je
     * parameter <b>str</b>. Hodnota premennej je v premennej <b>obj</b>.
     * @param str Nazov premennej
     * @param obj Hodnota premennej
     */
    public static void setValue(String str, Object obj) {        
        DataUtils.setValueOfVariable(obj, str);
    }
    
    /**
     * Metoda ktora vrati hodnotu premennej na pozicii danej parametrom <b>str</b>.
     * Hodnotu vyberame z mapy ulozenej v DataUtils.
     * @param str Nazov premennej ktoru chceme
     * @return Hodnota premennej ktoru ziskavame
     */
    public static Object getValue(String str) {                
        return DataUtils.getValueOfVariable(str);
    }          
            
}
