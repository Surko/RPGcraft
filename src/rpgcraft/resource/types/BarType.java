/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource.types;

import rpgcraft.resource.UiResource;
import rpgcraft.utils.DataUtils.DataValues;

/**
 *
 * @author kirrie
 */
public class BarType extends AbstractType {
    
    private static final String DENUMERATOR = "/";
    
    protected DataValues maxData, minData;
    
    public BarType(UiResource.UiType type) {
         super(type);
    }  
    
    public void setData(String sData) throws Exception {
        String[] data = sData.split(DENUMERATOR);
        if (data.length == 2) {
            minData = DataValues.valueOf(data[0]);
            maxData = DataValues.valueOf(data[1]);
        } else {
            throw new Exception();
        }
    }
    
    public DataValues getMinData() {
        return minData;
    }
    
    public DataValues getMaxData() {
        return maxData;                
    }
    
}
