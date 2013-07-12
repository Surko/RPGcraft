/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities;

import rpgcraft.resource.EntityResource;
import rpgcraft.resource.StringResource;

/**
 * Trieda Armor dediaca od predmetu nam zarucuje ze sa da s instanciami od nej pracovat
 * ako s predmetom. Spristupnene su vsetky metody a premenne z tridy Item, co znamena ze aj
 * metody z triedy Entity. Armor instancie maju nastaveny typ predmetu (itemType) na Armor. Subtyp tohoto typu
 * je dalej urceny enumom ArmorType v tejto triede. V tejto dobe je trieda je len ako
 * rozlisovac medzi roznymi typmi predmetov s tym ze defaultne operacie, ako je napriklad
 * ci sa da predmet obliect, su nastavene na true
 * @see Item
 * @author kirrie
 */
public class Armor extends Item{
    
    // <editor-fold defaultstate="collapsed" desc=" Pomocne triedy/enumy ">
    /**
     * Enum s vypisom typov pre brnenie.
     */ 
    public enum ArmorType implements TypeofItems {
           HEAD(StringResource.getResource("head")),
           TORSO(StringResource.getResource("torso")),
           LEGGINGS(StringResource.getResource("leggings")),
           LEGS(StringResource.getResource("legs")),
           HANDS(StringResource.getResource("hands"));

        private String value;
        
        private ArmorType(String value) {
            this.value = value;
        }
        
        @Override
        public String getValue() {
            return value;
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Dolezity konstruktor pri externalizaci.     
     */
    public Armor() {
        
    }
    
    /**
     * Konstruktor ktory vytvori instanciu Armor predmetu s menom zadanom v parametri <b>name</b>.
     * Ako vzor sa pouziva resource <b>res</b>. Z resource si ziskavame typ predmetu podla ktoreho
     * rozhodujeme co sa da s predmetom robit.
     * @param name Meno noveho predmetu
     * @param res Vzor pre predmet
     */
    public Armor(String name, EntityResource res) {
        super(name, res);
        for (ItemType type :res.getItemType()) {
            this.itemType = type;
        }
    }            
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Inicializacie ">
    /**
     * Metoda ktora inicializuje predmet podla udajov z resource. Volana je super
     * metoda initialize. Nakonci nastavujeme ci sa da predmet obliect na true.
     */
    @Override
    public void initialize() {
        super.initialize();
        this.equipable = true;        
    }        
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    /**
     * Metoda ktora nastavi typ brnenia.
     * @param armorType Typ brnenia
     */
    public void setArmorType(ArmorType armorType) {
           this.itemType.setSubType(armorType);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora vrati typ brnenia
     * @return ArmorType ako typ brnenia
     */
    public final ArmorType getArmorType() {
        return (ArmorType)itemType.getSubType();
    }
    // </editor-fold>
    
}
