/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities;

import rpgcraft.resource.EntityResource;
import rpgcraft.resource.StringResource;

/**
 * Trieda Misc dediaca od predmetu nam zarucuje ze sa da s instanciami od nej pracovat
 * ako s predmetom. Spristupnene su vsetky metody a premenne z triedy Item, co znamena ze aj
 * metody z triedy Entity. Misc instance maju nastaveny typ predmetu (itemType) na Misc. Subtyp tohoto typu
 * je dalej urceny enumom MiscType v tejto triede. V tejto dobe je trieda je len ako
 * rozlisovac medzi roznymi typmi predmetov s tym ze defaultne operacie, ako je napriklad
 * ci sa da predmet pouzit, su nastavene na true. Takisto ma MiscType zatial iba dva typy
 * a to OTHER a POTION.
 * @see Item
 * @author kirrie
 */
public class Misc extends Item{   
    // <editor-fold defaultstate="collapsed" desc=" Pomocne triedy/enumy ">
    /**
     * Enum s vypisom typov pre Misc predmety.
     */
    public enum MiscType implements TypeofItems {
        OTHER(StringResource.getResource("otheritem")),
        POTION(StringResource.getResource("potitem"));
        
        private String value;        
        
        private MiscType(String type) {
            this.value = type;
        }
        
        @Override
        public String getValue() {
            return value;
        }
        
        @Override
        public String toString() {
            return value;
        }
    }    
    // </editor-fold>  
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Dolezity konstruktor pri externalizaci.     
     */
    public Misc() {
        
    }
    
    /**
     * Konstruktor ktory vytvori instanciu Misc predmetu s menom zadanom v parametri <b>name</b>.
     * Ako vzor sa pouziva resource <b>res</b>. Z resource si ziskavame typ predmetu podla ktoreho
     * rozhodujeme co sa da s predmetom robit (pri POTION sa da predmet pouzit)
     * @param name Meno noveho predmetu
     * @param res Vzor pre predmet
     */
    public Misc(String name, EntityResource res) {
        super(name, res);
        if (res != null) {
            for (ItemType type :res.getItemType()) {
                this.itemType = type;
            }
        }                        
    }        
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Inicializacie ">
    /**
     * Metoda ktora inicializuje predmet podla udajov z resource. Volana je super
     * metoda initialize. Nakonci kontrolujeme MiscType tohoto predmetu a urcime co sa da s 
     * predmetom robit
     */
    @Override
    public void initialize() {
        super.initialize();
        switch (getMiscType()) {
            case POTION : {
                this.usable = true;
            } break;
            default : break;
        }       
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
     /**
     * Metoda ktora nastavi subTyp pre typ predmetu na ten zadany parametrom <b>type</b>
     * @param type Pod-typ predmetu.
     */
    public final void setMiscType(MiscType type) {
        if (itemType != null) {
            itemType.setSubType(type);
        }
    }
    // </editor-fold>
   
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    
    /**
     * Metoda ktora vrati typ roznorodeho predmetu
     * @return Typ roznorodeho predmetu
     */
    public final MiscType getMiscType() {
        return (MiscType)itemType.getSubType();
    }        
    
    // </editor-fold>
    
}
