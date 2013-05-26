/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities;

import rpgcraft.resource.EntityResource;

/**
 *
 * @author kirrie
 */
public class Misc extends Item{
    
    /**
     * Enum s vypisom typov. Zatial obsahuje len jednu
     */
    public enum MiscType implements TypeofItems {
        OTHER;

        @Override
        public Object getValue() {
            return null;
        }
    }
    
    protected MiscType miscType;
        
    // Dolezity konstruktor pri externalizacii.
    public Misc() {
        
    }
    
    public Misc(String name, EntityResource res) {
        super(name, res);
    }
    
    /**
     * Metoda ktora vrati typ roznorodeho predmetu
     * @return Typ roznorodeho predmetu
     */
    public MiscType getMiscType() {
        return miscType;
    }
    
    
    
}
