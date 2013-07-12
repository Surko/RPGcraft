/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource.types;

import rpgcraft.resource.UiResource;
import rpgcraft.utils.DataUtils.DataValues;

/**
 * Trieda ktora dedi od Abstraktneho typu zarucuje ze ho mozme pouzit ako validny typ
 * pre komponenty.
 * V tomto pripade sa to tyka komponenty SwingBar a vsetkych progress barov, ktore 
 * mozme nadefinovat.
 * Instancia triedy dokaze nastavovat minimalne a maximalna data ktore pouzivame
 * pri vykreslovani takychto progress barov.
 */
public class BarType extends AbstractType {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    private static final String DENUMERATOR = "/";
    // Min a max data  
    protected DataValues maxData, minData;
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    public BarType(UiResource.UiType type) {
         super(type);
    }  
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    /**
     * Metoda ktora nastavi data z parametru <b>sData</b>. Na ziskanie DataValues
     * treba rozparsovat text sData pomocou rozdelovaca. Kazdy rozparsovany text
     * premenim na enum DataValues.
     * @param sData Text ktory parsujeme na DataValues
     * @throws Exception 
     */
    public void setData(String sData) throws Exception{
        if (minData == null) {
            String[] data = sData.split(DENUMERATOR);
            if (data.length == 2) {
                minData = DataValues.valueOf(data[0]);
                maxData = DataValues.valueOf(data[1]);
            } else {
                throw new Exception();
            }
        }
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora vrati minimalne data pre komponentu typu Bar.
     * @return Minimalne data
     */
    public DataValues getMinData() {        
        return minData;
    }
    
    /**
     * Metoda ktora vrati maximalna data pre komponentu typu Bar.
     * @return Maximalne data
     */
    public DataValues getMaxData() {        
        return maxData;                
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Klonovanie ">
    /**
     * <i>{@inheritDoc }</i>
     * <p>
     * Taktiez treba okopirovat maximalne a minimalne data.
     * </p>
     * @return Sklovani objekt
     * @throws CloneNotSupportedException Ked metoda nepodporuje klonovanie
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        BarType clone = (BarType)super.clone();
        clone.maxData = maxData;
        clone.minData = minData;
        return clone;
    }
    //</editor-fold>
}
