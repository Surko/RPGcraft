/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities;

import rpgcraft.resource.EntityResource;
import rpgcraft.resource.StringResource;

/**
 *
 * @author kirrie
 */
public class Misc extends Item{   
    
    /**
     * Enum s vypisom typov. Zatial obsahuje len jednu
     */
    public enum MiscType implements TypeofItems {
        OTHER(StringResource.getResource("otheritem"));

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
        
    // Dolezity konstruktor pri externalizacii.
    public Misc() {
        
    }
    
    public Misc(String name, EntityResource res) {
        super(name, res);
        if (res != null) {
            for (ItemType type :res.getItemType()) {
                this.itemType = type;
            }
        }
        this.usable = true;
    }        
    
    /**
     * Metoda ktora vrati typ roznorodeho predmetu
     * @return Typ roznorodeho predmetu
     */
    public MiscType getMiscType() {
        return (MiscType)itemType.getSubType();
    }
    
    
    
}
