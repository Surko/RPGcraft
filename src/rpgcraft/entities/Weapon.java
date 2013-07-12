/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities;

import rpgcraft.resource.EntityResource;
import rpgcraft.resource.StringResource;

/**
 * Trieda Weapon dediaca od predmetu nam zarucuje ze sa da s instanciami od nej pracovat
 * ako s predmetom. Spristupnene su vsetky metody a premenne z tridy Item, co znamena ze aj
 * metody z triedy Entity. Weapon instancie maju nastaveny typ predmetu (itemType) na WEAPON. Subtyp tohoto typu
 * je dalej urceny enumom WeaponType v tejto triede. V tejto dobe je trieda je len ako
 * rozlisovac medzi roznymi typmi predmetov s tym ze defaultne operacie, ako je napriklad
 * ci sa da predmet pouzit, su nastavene na true.
 * @see Item
 * @author kirrie
 */
public class Weapon extends Item{
    
    // <editor-fold defaultstate="collapsed" desc=" Pomocne triedy/enumy ">
    /**
     * Enum s vypisom typov pre zbrane.
     */
    public enum WeaponType implements TypeofItems {
        HAND(StringResource.getResource("hand"),1),
        SHOVEL(StringResource.getResource("shovel"),2),        
        ONEHAXE(StringResource.getResource("onehaxe"),4),
        TWOHAXE(StringResource.getResource("twohaxe"),8),
        PICKAXE(StringResource.getResource("pickaxe"),16),
        ONEHSWORD(StringResource.getResource("onehsword"),32),
        TWOHSWORD(StringResource.getResource("twohsword"),64);

        private String sValue;
        private int iValue;
        
        private WeaponType(String sValue, int iValue) {
            this.sValue = sValue;
            this.iValue = iValue;
        }
                
        public int getIntValue() {
            return iValue;
        }
        
        @Override
        public String getValue() {
            return sValue;
        }
        
        @Override
        public String toString() {
            return sValue;
        }        
    }   
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Dolezity konstruktor pri externalizaci.     
     */
    public Weapon() {
        
    }
    
    /**
     * Konstruktor ktory vytvori instanciu Weapon predmetu s menom zadanom v parametri <b>name</b>.
     * Ako vzor sa pouziva resource <b>res</b>. Z resource si ziskavame typ predmetu podla ktoreho
     * rozhodujeme co sa da s predmetom robit.
     * @param name Meno noveho predmetu
     * @param res Vzor pre predmet
     */
    public Weapon(String name, EntityResource res) {
        super(name, res);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Inicializacie ">
    /**
     * Metoda ktora inicializuje predmet podla udajov z resource. Volana je super
     * metoda initialize. Nakonci nastavujeme ci sa da predmet aktivovat v ruke na true.
     */
    @Override
    public void initialize() {
        super.initialize();
        this.activable = true; 
        this.attackable = true;
        if (res != null) {
            for (ItemType type :res.getItemType()) {
                this.itemType = type;
            }
        }
    }  
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    /**
     * Metoda ktora nastavi typ zbrane.
     * @param weaponType Typ zbrane
     */
    public void setWeaponType(WeaponType weaponType) {
           this.itemType.setSubType(weaponType);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora vrati typ zbrane
     * @return WeaponType ako typ zbrane
     */
    public final WeaponType getWeaponType() {
        return (WeaponType)itemType.getSubType();
    }
    // </editor-fold>
    
    
}
