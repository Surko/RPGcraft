/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource;

/**
 *
 * @author doma
 */
public class StatResource {
    
    
    
    public enum Stat {        
        HEALTHREGEN(StringResource.getResource("hregen")),
        HEALTHREGENPER(StringResource.getResource("hregenper")),
        HEALTHMAX(StringResource.getResource("hmax")),
        HEALTHMAXPER(StringResource.getResource("hmaxper")),
        HEALTHBONUS(StringResource.getResource("hbonus")),
        STAMINAREGEN(StringResource.getResource("sregen")),
        STAMINAREGENPER(StringResource.getResource("sregenper")),
        STAMINAREGENBONUS(StringResource.getResource("sregenbonus")),
        STAMINAMAX(StringResource.getResource("smax")),
        STAMINAMAXPER(StringResource.getResource("smaxper")),
        STAMINABONUS(StringResource.getResource("sbonus")),
        STRENGTH(StringResource.getResource("str")),
        STRENGTHPER(StringResource.getResource("strper")),
        AGILITY(StringResource.getResource("agi")),
        AGILITYPER(StringResource.getResource("agiper")),
        SPEED(StringResource.getResource("spe")),
        SPEEDPER(StringResource.getResource("speper")),
        ENDURANCE(StringResource.getResource("end")),
        ENDURANCEPER(StringResource.getResource("endper")),        
        ATTACK(StringResource.getResource("atk")),
        ATTACKPER(StringResource.getResource("atkper")),
        DEFENSE(StringResource.getResource("def")),
        DEFENSEPER(StringResource.getResource("defper")),
        DAMAGE(StringResource.getResource("dmg")),
        DMGBONUS(StringResource.getResource("dmgbonus")),
        DAMAGEPER(StringResource.getResource("dmgper")),
        ATKRATING(StringResource.getResource("atkrat")),
        ATKRATINGBONUS(StringResource.getResource("atkratbonus")),
        DEFRATINGBONUS(StringResource.getResource("defratbonus")),
        ATKRATINGPER(StringResource.getResource("atkratper")),
        DEFRATING(StringResource.getResource("defrat")),
        DEFRATINGPER(StringResource.getResource("defratper")),
        ATKPOWER(StringResource.getResource("atkpwr")),
        DEFPOWER(StringResource.getResource("defpwr")),        
        ATKRATER(StringResource.getResource("atkrater")),
        DEFRATER(StringResource.getResource("defrater")),        
        SPDRATER(StringResource.getResource("spdrater")),
        DMGRATER(StringResource.getResource("dmgrater")),
        HPSPRATER(StringResource.getResource("hsrater")),
        ATKRADIUS(StringResource.getResource("atkradius")),
        ATKRADIUSPER(StringResource.getResource("atkradiusper")),
        DBLFACTOR(StringResource.getResource("dblfactor")),
        INTCHANCE(StringResource.getResource("intchance")),
        FIRERES(StringResource.getResource("fres")),
        FIRERESPER(StringResource.getResource("fresper")),
        COLDRES(StringResource.getResource("cres")),
        COLDRESPER(StringResource.getResource("cresper")),
        POISONERES(StringResource.getResource("pres")),
        POISONRESPER(StringResource.getResource("presper")),
        LIGHTERES(StringResource.getResource("lres")),
        LIGHTRESPER(StringResource.getResource("lresper")),
        DMGRES(StringResource.getResource("dres")),
        DMGRESPER(StringResource.getResource("dresper"));
        
        private String name;
        
        private Stat(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
}
