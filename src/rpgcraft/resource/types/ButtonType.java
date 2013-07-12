/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource.types;

import java.awt.Font;
import rpgcraft.resource.UiResource;
import rpgcraft.utils.TextUtils;

/**
 * Trieda ktora dedi od Abstraktneho typu zarucuje ze ho mozme pouzit ako validny typ
 * pre komponenty.
 * V tomto pripade sa to tyka komponenty SwingButton a vsetkych tlacidlovych komponent, ktore 
 * mozme nadefinovat.
 * Instancia triedy dokaze nastavovat farbu, font textu a samostatny text tlacidla ktore pouzivame
 * pri vykreslovani takychto tlacidlovych komponent.
 */
public class ButtonType extends AbstractType {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    String btnText;
    Font btnFont;
    String btnColor;
    //</editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Konstruktor ktory vytvori instanciu tlacidloveho typu. Volame zakladny konstruktory
     * s rodica
     * @param type Ui typ komponenty
     */
    public ButtonType(UiResource.UiType type) {
        super(type);
    }  
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    /**
     * Metoda ktora nastavi text v tlacidlu
     * @param btnText Text v tlacidle
     */
    public void setText(String btnText) {
        this.btnText = btnText;
    }    
    
    /**
     * Metoda ktora nastavi farbu textu v tlacidle
     * @param btnColor Farba textu tlacidla
     */
    public void setTextColor(String btnColor) {
        this.btnColor = btnColor;
    }
    
    /**
     * Metoda ktora nastavi font textu v tlacidle
     * @param btnFont Font textu tlacidla
     */
    public void setFont(String btnFont) {
        if (btnFont == null) {
            this.btnFont = TextUtils.DEFAULT_FONT;            
        } else {
            this.btnFont = Font.decode(btnFont); 
            
        }
    }
    
    /**
     * Metoda ktora nastavi vysku fontu textu v tlacidle
     * @param size Velkost fontu
     */
    public void setFontSize(String size) {
        if (btnFont != null && !size.equals("")) {
            this.btnFont = btnFont.deriveFont(Float.valueOf(size));
        }                
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora vrati text tlacidla
     * @return Text tlacidla
     */
    public String getText() {
        return btnText;
    }

    /**
     * Metoda ktora vrati font textu tlacidla
     * @return Font textu
     */
    public Font getFont() {
        return btnFont;
    }
    
    /**
     * Metoda ktora vrati farbu textu v tlacidle
     * @return Farba textu tlacidla
     */
    public String getTextColor() {
        return btnColor;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Klonovanie ">
    /**
     * <i>{@inheritDoc }</i>
     * <p>
     * Taktiez treba okopirovat farbu, font, text tlacidla
     * </p>
     * @return Sklonovani objekt
     * @throws CloneNotSupportedException Ked metoda nepodporuje klonovanie
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        ButtonType clone = (ButtonType)super.clone();
        clone.btnColor = btnColor;
        clone.btnText = btnText;
        clone.btnFont = btnFont;
        return clone;
    }
    // </editor-fold>    
}
