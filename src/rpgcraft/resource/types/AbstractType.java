/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource.types;

import java.awt.GridBagConstraints;
import java.util.ArrayList;
import javax.swing.SpringLayout;
import rpgcraft.resource.UiResource;

/**
 *
 * @author kirrie
 */
public abstract class AbstractType implements Cloneable {
        protected Object gc;
        protected UiResource.UiType uitype;
        protected UiResource.LayoutType layoutType;
        
        public AbstractType(UiResource.UiType type) {
            this.uitype = type;
        }                 
        
        public void setLayoutType(UiResource.LayoutType layoutType) {
            this.layoutType = layoutType;
        }
        
        public void setConstraints(Object gc) {
            this.gc = gc;
        }
        
        public Object getConstraints() {
            return gc;
        }
        
        public UiResource.UiType getUiType() {
            return uitype;
        }
        
        public UiResource.LayoutType getLayoutType() {
            return layoutType;
        }
        
        public ArrayList<UiResource> getElements() {
            return null;
        }
        
        @Override
        public Object clone() throws CloneNotSupportedException {
            AbstractType clone = (AbstractType)super.clone();
            if (layoutType != null) {
                switch (layoutType) {
                    case GRIDBAGSWING : {
                        this.gc = ((GridBagConstraints)gc).clone();
                    } break;                                    
                }
            }
            return clone;
        }
}
