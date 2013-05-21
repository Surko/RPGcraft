/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource.types;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import rpgcraft.graphics.Colors;
import rpgcraft.resource.UiResource;

/**
 *
 * @author kirrie
 */
public abstract class AbstractType implements Cloneable {
        protected Object gc;
        protected UiResource.UiType uitype;
        protected UiResource.LayoutType layoutType;
        protected Color bColor,tColor;
        
        public AbstractType(UiResource.UiType type) {
            this.uitype = type;
        }                 
        
        public void setLayoutType(UiResource.LayoutType layoutType) {
            this.layoutType = layoutType;
        }
        
        public void setConstraints(Object gc) {
            this.gc = gc;
        }
        
        public void setTopColor(String color) throws Exception {
            this.tColor = Colors.parseColor(color);
        }
        
        public void setBackColor(String color) throws Exception {
            this.bColor = Colors.parseColor(color);
        }
        
        public void setBackColor(Color color) {
            this.bColor = color;
        }
        
        public void setTopColor(Color color) {
            this.tColor = color;
        }
        
        public Object getConstraints() {
            return gc;
        }
        
        public Color getTopColor() {
            return tColor;
        }
        
        public Color getBackColor() {
            return bColor;
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
                        clone.gc = ((GridBagConstraints)gc).clone();
                    } break;                                    
                }
            }
            clone.bColor = bColor;
            clone.tColor = tColor;
            return clone;
        }
}
