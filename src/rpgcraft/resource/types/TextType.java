/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource.types;

import java.awt.Font;
import java.util.ArrayList;
import rpgcraft.resource.UiResource;
import rpgcraft.resource.UiResource.UiType;
import rpgcraft.utils.TextUtils;

/**
 *
 * @author kirrie
 */
public class TextType extends AbstractType {

    String txText;
    Font txtFont; // Skutocny font
    String txColor;

    public TextType(UiType uiType) {
        super(uiType);
    }        
    
    public void setText(String txText) {
        this.txText = txText;
    }
    
    public void setFont(String txFont) {           
        if (txFont == null) {
            this.txtFont = TextUtils.DEFAULT_FONT;            
        } else {
            this.txtFont = Font.decode(txFont); 
            
        }
    }
    
    public void setFontSize(String size) {
        if (txtFont != null && !size.equals("")) {
            this.txtFont = txtFont.deriveFont(Float.valueOf(size));
        }                
    }
    
    public void setTextColor(String txColor) {
        this.txColor = txColor;
    }
    
    public String getText() {
        return txText;
    }

    public String getTextColor() {
        return txColor;
    }
    
    public String getsFont() {
        return txtFont.getName();
    }
        
    public Font getFont() {
        return txtFont;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        TextType clone = (TextType)super.clone();
        clone.txColor = txColor;
        clone.txText = txText;
        clone.txtFont = txtFont;
        return clone;
    }
    
}
