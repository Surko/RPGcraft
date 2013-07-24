/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import rpgcraft.entities.Misc.MiscType;
import rpgcraft.resource.EntityResource;

/**
 * Trieda CombinatedItem dediaca od predmetu nam zarucuje ze sa da s instanciami od nej pracovat
 * ako s predmetom. Spristupnene su vsetky metody a premenne z triedy Item, co znamena ze aj
 * metody z triedy Entity. CombinatedItem instance nemaju nastaveny typ predmetu, ale ziskava
 * si ich vsetky do <b>types</b> listu.
 * V tejto dobe je trieda je len ako
 * rozlisovac medzi roznymi typmi predmetov s tym ze defaultne operacie, ako je napriklad
 * ci sa da predmet pouzit, su nastavene na true. 
 * @see Item
 */
public class CombinatedItem extends Item{
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    /**
     * Typy predmetov
     */
    private ArrayList<ItemType> types;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    public CombinatedItem() {
        
    }
    
    public CombinatedItem(String name, EntityResource res) {
        super(name, res);
    }
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Inicializacie ">
    
    /**
     * Metoda ktora inicializuje predmet. Ako obycajne volame super metodu
     * initialize. Na doplnenie prechadzame typy definovane v resource a podla tychto typov
     * nastavujeme co sa bude dat robit s predmetom.
     */
    @Override
    public void initialize() {
        super.initialize();
        types = res.getItemType();
         
        for (ItemType type : types) {
            switch (type) {
                case ARMOR : {
                    this.equipable = true;                     
                } break;
                case WEAPON : {
                    this.activable = true;
                } break;
                case TILE : {
                    this.placeable = true;
                } break;
                case MISC : {
                    switch ((MiscType)type.getSubType()) {
                        case POTION : {
                           this.usable = true;
                        } break;
                        default : break;
                    }
                } break;
            }
        }
    }        
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Externalizacne metody ">
    
    /**
     * Metoda ktora zapise predmet do suboru/vystupneho streamu ktory je zadany 
     * ako parameter <b>out</b>. Vyuzivame rodicovskej metody writeExternal ktora zapise
     * do suboru vsetky vlastnosti spolocne pre entity. V tejto metode zapiseme do suboru
     * iba take vlastnosti ktore su vytvorene v predmete a nie v inych entitach.
     * Nazvy napovedaju za vsetko.
     * @param out Vystupny stream/subor.
     * @throws IOException VYhodena pri neexistenci suboru
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(types);        
    }

    /**
     * Metoda ktora nacita predmet zo suboru/vstupneho streamu ktory je zadany 
     * ako parameter <b>in</b>. Vyuzivame rodicovskej metody readExternal ktora nacita zo
     * suboru vsetky vlastnosti spolocne pre entity. V tejto metode nacitame zo suboru
     * iba take vlastnosti ktore su vytvorene v predmete a nie v inych entitach.
     * Nazvy napovedaju za vsetko.
     * @param in Vstupny stream/subor.
     * @throws IOException VYhodena pri neexistenci suboru
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        this.types = (ArrayList<ItemType>)in.readObject();
    }               

    
    // </editor-fold>
}
