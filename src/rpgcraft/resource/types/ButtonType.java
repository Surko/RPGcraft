/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource.types;

import rpgcraft.resource.UiResource;

/**
 *
 * @author kirrie
 */
public class ButtonType extends AbstractType {

    String btnText;
    String btnFont;
    String btnColor;
        
    public ButtonType(UiResource.UiType type) {
        super(type);
    }  
    
    public void setText(String btnText) {
        this.btnText = btnText;
    }
    
    public void setTextColor(String btnColor) {
        this.btnColor = btnColor;
    }
    
    public void setFont(String btnFont) {
        this.btnFont = btnFont;                
    }
    
    public String getText() {
        return btnText;
    }

    public String getFont() {
        return btnFont;
    }
    
    public String getTextColor() {
        return btnColor;
    }
    
}
