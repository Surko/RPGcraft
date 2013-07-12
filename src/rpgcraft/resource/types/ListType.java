/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource.types;

import java.util.ArrayList;
import rpgcraft.resource.UiResource;
import rpgcraft.resource.UiResource.UiType;
import rpgcraft.utils.TextUtils;

/**
 * Trieda ktora dedi od Abstraktneho typu zarucuje ze ho mozme pouzit ako validny typ
 * pre komponenty.
 * V tomto pripade sa to tyka komponenty SwingList a vsetkych listovych komponent, ktore 
 * mozme nadefinovat.
 * Instancia triedy dokaze manipulovat s poctom riadkov a stlpcov listu, nastavovat
 * data pre list a vratiti elementy z listu.
 */
public class ListType extends PanelType{
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    public static final String DENUMERATOR = ":";
        
    public static final int DEFAULTTYPE = -1;
    public static final int AUTOTYPE = -2;
    
    public interface RowLayout {
        public static final int HORIZONTALROWS = -1;
        public static final int VERTICALROWS = -2;
        public static final int DIAGONALROWS = -3;
        public static final int HORIZONTALROWSFIT = -4;
        public static final int VERTICALROWSFIT = -5;
    }
    
    public enum RowType {
        AUTO
    }
    
    protected int rowsMax;
    protected int colsMax;
    protected int rowLayout = -2;
    protected String sData;
    protected ArrayList<UiResource> elements;
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Konstruktor ktory vytvori instanciu listovho typu. Volame zakladny konstruktor
     * z rodica
     * @param uiType Ui typ komponenty
     */
    public ListType(UiType uiType) {
        super(uiType);
    }    
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    /**
     * Metoda ktora nastavi data pre list.
     * @param data Data listu (moze byt nadefinovany typ dat, priamy vypis alebo kombinacia)
     */
    public void setData(String data) {
        this.sData = data;
    }
    
    /**
     * Metoda ktora nastavi maximalny pocet riadkov
     * @param rowsMax Maximalny pocet riadkov
     */
    public void setRowsMax(String rowsMax) {
        this.rowsMax = TextUtils.getRowCount(rowsMax);
    }
    
    /**
     * Metoda ktora nastavi maximalny pocet stlpcov.
     * @param colsMax Maximalny pocet stlpcov
     */
    public void setColsMax(String colsMax) {
        this.colsMax = TextUtils.getRowCount(colsMax);
    }
    
    /**
     * Metoda ktora rowLayout pre tento list. Riadkove layouty su popisane v UiResource.
     * @param rowLayout Riadkovy layout pre list
     */
    public void setLayout(int rowLayout) {        
        this.rowLayout = rowLayout;
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora vrati layout listu.
     * @return Layout listu
     */
    public int getLayout() {
        return rowLayout;
    }

    /**
     * Metoda ktora vrati maximalny pocet riadkov
     * @return Maximalny pocet riadkov
     */
    public int getMaxRows() {
        return rowsMax;
    }
    
    /**
     * Metoda ktora vrati maximalny pocet stlpcov.
     * @return Maximalny pocet stlpcov.
     */
    public int getMaxCols() {
        return colsMax;
    }
    
    /**
     * Metoda ktora vrati data listu
     * @return Data listu
     */
    public String getData() {
        return sData;
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Klonovanie ">
    /**
     * <i>{@inheritDoc }</i>
     * <p>
     * Taktiez treba okopirovat layout rozlozenia riadkov, pocet riadkov a stlpcov a 
     * elementy.
     * </p>
     * @return Sklonovani objekt
     * @throws CloneNotSupportedException Ked metoda nepodporuje klonovanie
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        ListType clone = (ListType)super.clone();
        clone.rowLayout = rowLayout;
        clone.rowsMax = rowsMax;
        clone.colsMax = colsMax;                
        return clone;
    }
    //</editor-fold>
}
