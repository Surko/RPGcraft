/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource.types;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.UiResource;
import rpgcraft.resource.UiResource.UiType;
import rpgcraft.utils.TextUtils;

/**
 *
 * @author kirrie
 */
public class ListType extends PanelType{
    
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
    protected int rowLayout = -3;
    protected String data;
    protected ArrayList<UiResource> elements;
    
    public ListType(UiType uiType) {
        super(uiType);
    }    

    public void setData(String data) {
        this.data = data;
    }
    
    public void setRowsMax(String rowsMax) {
        this.rowsMax = TextUtils.getRowCount(rowsMax);
    }
    
    public void setColsMax(String rowsMax) {
        this.colsMax = TextUtils.getRowCount(rowsMax);
    }
    
    public void setLayout(int rowLayout) {        
        this.rowLayout = rowLayout;
    }
    
    public int getLayout() {
        return rowLayout;
    }

    public int getMaxRows() {
        return rowsMax;
    }
    
    public int getMaxCols() {
        return colsMax;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        ListType clone = (ListType)super.clone();
        clone.data = data;
        clone.rowLayout = rowLayout;
        clone.rowsMax = rowsMax;
        clone.colsMax = colsMax;                
        return clone;
    }
}
