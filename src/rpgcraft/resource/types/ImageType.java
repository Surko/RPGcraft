/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource.types;

import rpgcraft.resource.UiResource.UiType;

/**
 * Trieda ktora dedi od Abstraktneho typu zarucuje ze ho mozme pouzit ako validny typ
 * pre komponenty.
 * V tomto pripade sa to tyka komponenty SwingImage a vsetkych obrazkovych komponent, ktore 
 * mozme nadefinovat.
 * Instancia triedy je prazdna a obsahuje iba podedene metody
 */
public class ImageType extends AbstractType {
    
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    /**
     * Konstruktor na vytvorenie obrazkoveho typu s volanim rodicovskeho konstruktoru.
     * @param type Ui typ tlacidloveho typu
     */
    public ImageType(UiType type) {
        super(type);
    }       
    // </editor-fold>
    
}
