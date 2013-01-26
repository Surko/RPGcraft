/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource.types;

import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.UiResource;

/**
 *
 * @author kirrie
 */
public class PanelType extends AbstractType {
    
    private ArrayList<UiResource> elements;
    
    public PanelType(UiResource.UiType uiType) {
        super(uiType);
    }
    
    public void setElements(ArrayList<UiResource> elements) {
        this.elements = elements;
    }
    
    public void addElement(UiResource element) {
        if (elements == null) {
            elements = new ArrayList<>();
        }
        elements.add(element);
    }
    
    public ArrayList<UiResource> getElements() {
        return elements;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        PanelType clone = (PanelType)super.clone();
        clone.elements = (ArrayList<UiResource>) elements.clone();
        return clone;
    }
    
}
