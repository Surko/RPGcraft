/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource.types;

import java.util.ArrayList;
import rpgcraft.resource.UiResource;

/**
 * Trieda ktora dedi od Abstraktneho typu zarucuje ze ho mozme pouzit ako validny typ
 * pre komponenty.
 * V tomto pripade sa to tyka komponenty SwingImagePanel a vsetkych panelovych komponent, ktore 
 * mozme nadefinovat.
 * Instancia triedy dokaze nastavovat elementy panelu
 */
public class PanelType extends AbstractType {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    private ArrayList<UiResource> elements;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Konstruktor ktory vytvori instanciu paneloveho typu. Volame zakladny konstruktor
     * z rodica
     * @param uiType Ui typ komponenty
     */
    public PanelType(UiResource.UiType uiType) {
        super(uiType);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    /**
     * Metoda ktora nastavi elementy/komponenty v paneli
     * @param elements Elementy v panelovej komponente
     */
    public void setElements(ArrayList<UiResource> elements) {
        this.elements = elements;
    }
    
    /**
     * Metoda ktora prida element/komponentu do panelu.
     * @param element Element ktory pridavame do panelu
     */
    public void addElement(UiResource element) {
        if (elements == null) {
            elements = new ArrayList<>();
        }
        elements.add(element);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda vrati nadefinovane elementy v tejto panelovej komponente.
     * @return Nadefinovane elementy pre panel
     */
    @Override
    public ArrayList<UiResource> getElements() {
        return elements;
       
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Klonovanie ">
    /**
     * <i>{@inheritDoc }</i>
     * <p>
     * Taktiez treba okopirovat elementy v liste.
     * </p>
     * @return Sklonovani objekt
     * @throws CloneNotSupportedException Ked metoda nepodporuje klonovanie
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        PanelType clone = (PanelType)super.clone();
        clone.elements = (ArrayList<UiResource>) elements.clone();
        return clone;
    }
    // </editor-fold>
}
