/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource.types;

import java.awt.Font;
import rpgcraft.resource.UiResource.UiType;
import rpgcraft.utils.TextUtils;

/**
 *
 * @author kirrie
 */
public class TextType extends AbstractType {

    String txText;
    String txFont;

    public TextType(UiType uiType) {
        super(uiType);
    }        
    
    public void setText(String txText) {
        this.txText = txText;
    }
    
    public void setFont(String txFont) {
        this.txFont = txFont;                
    }
    
    public String getText() {
        return txText;
    }

    public String getsFont() {
        return txFont;
    }
    
    public Font getFont() {
        if (txFont == null) {
            return TextUtils.DEFAULT_FONT;
        } else {
            return Font.decode(txFont);
        }
    }
    
}
