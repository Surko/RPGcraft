/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource.types;

import java.awt.Font;
import rpgcraft.resource.UiResource.UiType;
import rpgcraft.utils.TextUtils;

/**
 * Trieda ktora dedi od Abstraktneho typu zarucuje ze ho mozme pouzit ako validny typ
 * pre komponenty.
 * V tomto pripade sa to tyka komponenty SwingText/SwingInputText a vsetkych textovych komponent, ktore 
 * mozme nadefinovat.
 * Instancia triedy dokaze nastavovat farbu, font textu a samostatny text textov
 */
public class TextType extends AbstractType {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    String txText;
    Font txtFont; // Skutocny font
    String txColor;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Konstruktor ktory vytvori instanciu textoveho typu. Volame zakladny konstruktor
     * z rodica.
     * @param uiType Ui typ komponenty
     */
    public TextType(UiType uiType) {
        super(uiType);
    }        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    /**
     * Metoda ktora nastavi text v komponente
     * @param txText Text v komponente
     */
    public void setText(String txText) {
        this.txText = txText;
    }
    
    /**
     * Metoda ktora nastavi font textu. Pri font = null nastavime zakladny/default font.
     * @param txFont Font textu
     */
    public void setFont(String txFont) {           
        if (txFont == null) {
            this.txtFont = TextUtils.DEFAULT_FONT;            
        } else {
            this.txtFont = Font.decode(txFont); 
            
        }
    }
    
    /**
     * Metoda ktora nastavi velkost fontu
     * @param size Velkost fontu
     */
    public void setFontSize(String size) {
        if (txtFont != null && !size.equals("")) {
            this.txtFont = txtFont.deriveFont(Float.valueOf(size));
        }                
    }
    
    /**
     * Metoda ktora nastavi farbu textu
     * @param txColor Farba textu
     */
    public void setTextColor(String txColor) {
        this.txColor = txColor;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora vrati text textu
     * @return Text v komponente 
     */
    public String getText() {
        return txText;
    }

    /**
     * Metoda ktora vrati farbu textu.
     * @return Farba textu
     */
    public String getTextColor() {
        return txColor;
    }
    
    /**
     * Metoda ktora vrati textovu hodnotu fontu
     * @return Textova hodnota fontu
     */
    public String getsFont() {
        return txtFont.getName();
    }
        
    /**
     * Metoda ktora font pre text.
     * @return Font textu
     */
    public Font getFont() {
        return txtFont;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Klonovanie ">
   /**
     * <i>{@inheritDoc }</i>
     * <p>
     * Taktiez treba okopirovat farbu, font, text textu
     * </p>
     * @return Sklonovani objekt
     * @throws CloneNotSupportedException Ked metoda nepodporuje klonovanie
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        TextType clone = (TextType)super.clone();
        clone.txColor = txColor;
        clone.txText = txText;
        clone.txtFont = txtFont;
        return clone;
    }
    // </editor-fold>
    
}
