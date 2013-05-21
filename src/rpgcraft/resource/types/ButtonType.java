/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource.types;

import java.awt.Font;
import rpgcraft.resource.UiResource;
import rpgcraft.utils.TextUtils;

/**
 *
 * @author kirrie
 */
public class ButtonType extends AbstractType {

    String btnText;
    Font btnFont;
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
        if (btnFont == null) {
            this.btnFont = TextUtils.DEFAULT_FONT;            
        } else {
            this.btnFont = Font.decode(btnFont); 
            
        }
    }
    
    public void setFontSize(String size) {
        if (btnFont != null && !size.equals("")) {
            this.btnFont = btnFont.deriveFont(Float.valueOf(size));
        }                
    }
    
    public String getText() {
        return btnText;
    }

    public Font getFont() {
        return btnFont;
    }
    
    public String getTextColor() {
        return btnColor;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        ButtonType clone = (ButtonType)super.clone();
        clone.btnColor = btnColor;
        clone.btnText = btnText;
        clone.btnFont = btnFont;
        return clone;
    }
    
}
