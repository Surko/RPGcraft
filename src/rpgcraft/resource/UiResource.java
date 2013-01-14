/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import rpgcraft.errors.ErrorWrn;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.utils.XmlUtils;
import rpgcraft.xml.LayoutXML;

/**
 *
 * @author Surko
 */
public class UiResource extends AbstractResource<UiResource> {           
    
    public interface UiSize {
        public static final String FILL_PARENT = "FILL_PARENT";
        public static final String WRAP_CONTENT = "WRAP_CONTENT";
    }       
    
    public enum UiPaintMode {
        OVERLAP,
        NORMAL        
    }
    
    public enum UiType {
        BUTTON,
        PANEL,
        TEXT,
        IMAGE,
        LIST
    }

    public enum UiPosition {
        CENTER,
        RIGHT,
        LEFT,
        TOP,
        TOP_RIGHT,
        TOP_LEFT,
        BOTTOM,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }
    
    public interface FillType {
        public static final int NONE = GridBagConstraints.NONE;
        public static final int HORIZONTAL = GridBagConstraints.HORIZONTAL;
        public static final int VERTICAL = GridBagConstraints.VERTICAL;
        public static final int BOTH = GridBagConstraints.BOTH;
    }
    
    public enum LayoutType {
        GRIDBAGSWING,
        FLOWSWING,
        BORDERSWING,
        GRIDSWING,
        INGAME        
    }
    
    private static final Logger LOG = Logger.getLogger(UiResource.class.getName());
    private static HashMap<String, UiResource> uiResources = new HashMap<>();
    
    private String id;
    private String text;
    private String bImageId;
    private String bImagew;
    private String bImageh;
    private String bColorId;
    private String w,h,mw,mh;
    private int hGap;
    private int vGap;
    private int align;
    private UiType type;    
    private LayoutType layoutType;
    private boolean scrolling;
    private int scrollX;
    private int scrollY;
    private int x;
    private int y;
    private Object gc;
    private int iOrientation;
    private ArrayList<UiResource> elements;
    private String parent;
    private String action;
    private boolean visible;
    private UiPosition position;
    private UiPosition imagePosition;
    private UiPaintMode mode;
    
    public static UiResource getResource(String name) {
        return uiResources.get(name);
    }    
    
    private UiResource(Element elem) {        
        this.visible = true;
        parse(elem);           
        validate();
        uiResources.put(id, this);
    }
    
    private UiResource(Element elem, String parent) {
        this.parent = parent;
        this.visible = true;
        parse(elem);
        validate();
        uiResources.put(id, this);
    }
    
    /**
     * Metoda validate overi ci je resource korektny otestovanim urcitych podmienok.
     * Dolezite vlastnosti su id, typ layoutu, typ, mod prekreslovania.
     */
    private void validate() {
        if (mode == null) {
            this.mode = UiPaintMode.NORMAL;
        }
        if (id == null) {
            LOG.log(Level.SEVERE, StringResource.getResource("_ndid"));
            new MultiTypeWrn(null, Color.red, StringResource.getResource("_ndid"),
                    null).renderSpecific("Missing ID");
        }
        if (w == null) w = "0";        
        if (h == null) h = "0";
        
        if (type == null) {
            String[] param = new String[] {id};
            LOG.log(Level.SEVERE, StringResource.getResource("_ndtype",param));
            new MultiTypeWrn(null, Color.red, StringResource.getResource("_ndtype"),
                    param).renderSpecific("Missing TYPE");
        }
        if (type == UiType.LIST) {
            String[] param = new String[] {id};
            switch (elements.size()) {
                // Ziadny element, list bude prazdny
                case 0 : LOG.log(Level.INFO, StringResource.getResource("_blist", param));
                    break;
                // spravna vetva
                case 1 : break;
                // Prilis vela elementov v liste, bude pouzity iba jeden
                default : LOG.log(Level.INFO, StringResource.getResource("_mlist"));                        
            } 
        }
        
    }
    
    public static UiResource newBundledResource(Element elem) {
        return new UiResource(elem);                
    }  
    
    @Override
    protected void parse(Element elem) {
        NodeList nl = elem.getChildNodes(); 
                
        for (int i = 0; i< nl.getLength();i++) {
            Node eNode = nl.item(i);
            switch (eNode.getNodeName()) {   
                case LayoutXML.LAYOUT : {
                    parse((Element)eNode);                    
                }   break;
                case LayoutXML.BACKGROUND : {
                    parse((Element)eNode);
                }   break;
                case LayoutXML.BACKGROUNDCOLOR : {
                    bColorId = eNode.getTextContent();
                }   break;
                case LayoutXML.BACKGROUNDIMAGE : {
                    bImageId = eNode.getTextContent();
                }   break;
                case LayoutXML.IMAGEWIDTH : {
                    bImagew = eNode.getTextContent();                                                                    
                }   break;
                case LayoutXML.IMAGEHEIGHT : {
                    bImageh = eNode.getTextContent();
                }   break;    
                case LayoutXML.WIDTH : {
                    w = eNode.getTextContent();
                }   break;
                case LayoutXML.HEIGHT : {
                    h = eNode.getTextContent();
                }   break;
                case LayoutXML.MINWIDTH : {
                    mw = eNode.getTextContent();                                                                    
                }   break;
                case LayoutXML.MINHEIGHT : {
                    mh = eNode.getTextContent();
                }   break;    
                case LayoutXML.ID : {
                    id = eNode.getTextContent();                    
                } break;
                case LayoutXML.TEXT : {
                    text = eNode.getTextContent();                    
                } break;
                case LayoutXML.VISIBLE : {
                    visible = Boolean.valueOf(eNode.getTextContent());
                } break;
                case LayoutXML.FILL : {
                    switch (layoutType) {
                        case GRIDBAGSWING : {
                            /* Reflection pristup */
                             try {                            
                                Field f = FillType.class.getField(eNode.getTextContent());
                                try {
                                    ((GridBagConstraints)gc).fill = f.getInt(null);
                                } catch (IllegalArgumentException ex) {
                                    LOG.log(Level.INFO, null, ex);
                                } catch (IllegalAccessException ex) {
                                    LOG.log(Level.SEVERE, null, ex);
                                }
                            } catch (NoSuchFieldException ex) {
                                LOG.log(Level.INFO, null, ex);
                            } catch (SecurityException ex) {
                                LOG.log(Level.SEVERE, null, ex);
                            }
                        } break;
                    }
                } break;
                case LayoutXML.TEMPLATE : {
                    String templateText = eNode.getTextContent();
                    try {
                    copy(UiResource.uiResources.get(templateText));
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE,
                                StringResource.getResource("_ui_template_error"),e.toString());
                    }
                } break;
                    /*
                     * Typ Resource (button,panel,image,...). Ked je uz nastaveny bud v template alebo niecim inym tak je 
                     * nevhodne ho znova menit. Preto vypise hlasku do logu.
                     */
                case LayoutXML.TYPE : {           
                    if (type == null) {
                        type = UiType.valueOf(eNode.getTextContent());
                    } else {
                        LOG.log(Level.INFO, StringResource.getResource("_ui_error"), id + ":type");
                    }
                } break;
                     /*
                     * Mod Resource (normal,overlap) mozne doplnit o nove a k tomu operacie.
                     * Ked je uz nastaveny bud v template alebo niecim inym tak je 
                     * nevhodne ho znova menit. Preto vypise hlasku do logu.
                     */
                case LayoutXML.PAINTMODE : {           
                    if (mode == null) {
                        mode = UiPaintMode.valueOf(eNode.getTextContent());
                    } else {
                        LOG.log(Level.INFO, StringResource.getResource("_ui_error"), id + ":mode");
                    }
                } break;    
                case LayoutXML.LAYOUTTYPE : {   
                    if (layoutType == null) {
                        layoutType = LayoutType.valueOf(eNode.getTextContent());                   
                        switch (layoutType) {
                            case GRIDBAGSWING : {
                                gc = new GridBagConstraints();
                            }   break;                        
                        }
                    } else {
                        LOG.log(Level.INFO, StringResource.getResource("_ui_layoutype_error"), "layout-type");                 
                    }
                } break;
                case LayoutXML.INSETS : {            
                    switch (layoutType) {
                        case GRIDBAGSWING : {
                            String[] _insets = eNode.getTextContent().split(",");
                            try {
                                ((GridBagConstraints)gc).insets = new Insets(Integer.parseInt(_insets[0]),
                                        Integer.parseInt(_insets[1]),
                                        Integer.parseInt(_insets[2]),
                                        Integer.parseInt(_insets[3]));
                            } catch (Exception e) {
                                LOG.log(Level.WARNING,
                                        StringResource.getResource("_iparam"), "insets");
                            }
                        }   break;
                        default : {
                            LOG.log(Level.WARNING,
                                        StringResource.getResource("_uparam"), "insets");
                        };                            
                    }
                } break;
                case LayoutXML.X : {
                    switch (layoutType) {
                        case GRIDBAGSWING : {
                            try {
                            ((GridBagConstraints)gc).gridx = Integer.parseInt(eNode.getTextContent());
                            } catch (Exception e) {
                                LOG.log(Level.WARNING,
                                        StringResource.getResource("_iparam"), "gridx");
                            }
                        }   break;
                        case GRIDSWING : {
                            LOG.log(Level.WARNING,
                                        StringResource.getResource("_uparam"), "gridx");
                        } break; 
                        default : {
                            x = Integer.parseInt(eNode.getTextContent());
                        };   
                    }
                }   break;
                case LayoutXML.Y : {
                    switch (layoutType) {
                        case GRIDBAGSWING : {
                            try {
                            ((GridBagConstraints)gc).gridy = Integer.parseInt(eNode.getTextContent());
                            } catch (Exception e) {
                                LOG.log(Level.WARNING,
                                        StringResource.getResource("_iparam"), "gridy");
                            }
                        }   break;
                        case GRIDSWING : {
                            LOG.log(Level.WARNING,
                                        StringResource.getResource("_uparam"), "gridy");
                        } break; 
                        default : {
                            y = Integer.parseInt(eNode.getTextContent());
                        }; 
                    }
                } break;
                case LayoutXML.WEIGHTX : {
                    switch (layoutType) {
                        case GRIDBAGSWING : {
                            try {
                            ((GridBagConstraints)gc).weightx = Integer.parseInt(eNode.getTextContent());
                            } catch (Exception e) {
                                LOG.log(Level.WARNING,
                                        StringResource.getResource("_iparam"), "weightx");
                            }
                        }   break;
                        default : {
                            LOG.log(Level.WARNING,
                                        StringResource.getResource("_uparam"), "weightx");
                        }; 
                    }
                } break;
                case LayoutXML.WEIGHTY : {
                    switch (layoutType) {
                        case GRIDBAGSWING : {
                            try {
                            ((GridBagConstraints)gc).weighty = Integer.parseInt(eNode.getTextContent());
                            } catch (Exception e) {
                                LOG.log(Level.WARNING,
                                        StringResource.getResource("_iparam"), "weighty");
                            }
                        }   break;
                        default : {
                            LOG.log(Level.WARNING,
                                        StringResource.getResource("_uparam"), "weighty");
                        }; 
                    }
                } break;
                case LayoutXML.SCROLLING : {
                    scrolling = true;
                    parse((Element)eNode);
                } break; 
                case LayoutXML.SPEEDX : {
                    scrollX = Integer.parseInt(eNode.getTextContent());
                } break;   
                case LayoutXML.ACTION : {
                    action = eNode.getTextContent();
                } break;  
                case LayoutXML.SPEEDY : {
                    scrollY = Integer.parseInt(eNode.getTextContent());
                } break;
                case LayoutXML.IMAGEPOSITION : {
                    imagePosition = UiPosition.valueOf(eNode.getTextContent());
                } break;
                case LayoutXML.POSITION : {
                    position = UiPosition.valueOf(eNode.getTextContent());                
                } break;     
                case LayoutXML.LISTDATA : {
                    if(type == UiType.LIST) {
                        if (elements == null) {
                            elements = new ArrayList<>();
                            for (Element _elem : XmlUtils.parseRootElements(eNode, LayoutXML.LISTELEMENT)) {
                                elements.add(new UiResource(_elem,id));                        
                            }
                        } else {
                            for (Element _elem : XmlUtils.parseRootElements(eNode, LayoutXML.LISTELEMENT)) {
                                elements.add(new UiResource(_elem,id));                        
                            }
                        }
                    } else {
                        LOG.log(Level.WARNING,StringResource.getResource("_felement", new String[] {id}));
                    }                    
                } break;
                case LayoutXML.ELEMENTS : {
                    if (type == UiType.PANEL) {
                        if (elements == null) {
                            elements = new ArrayList<>();                                                    
                            for (Element _elem : XmlUtils.parseRootElements(eNode, LayoutXML.ELEMENT)) {
                                elements.add(new UiResource(_elem,id));                        
                            }
                        } else {
                            for (Element _elem : XmlUtils.parseRootElements(eNode, LayoutXML.ELEMENT)) {
                                elements.add(new UiResource(_elem,id));                        
                            }
                        }
                    } else {
                        LOG.log(Level.WARNING,StringResource.getResource("_felement", new String[] {id}));
                    }
                }
                default : break;
            }
        }
    }
    
    /**
     * Metoda copy ma za ulohu skopirovat vsetky udaje z UiResource zadaneho parametrom
     * res, okrem udaju id ktory je unikatny. Pre <b>gc</b> -> GridBagConstraint a <b>elements</b> -> ArrayList vyuzivame
     * metody clone.
     * Z praktickeho hladiska sa tato metoda vyuziva vzdy pri narazeni na tag <template>
     * z layout.xml
     * @param res
     * @throws Exception 
     */
    @Override
    protected void copy(UiResource res) throws Exception {
        this.type = res.type;   
        this.layoutType = res.layoutType;
        this.mode = res.mode;
        this.bImageId = res.bImageId;
        this.bImagew = res.bImagew;
        this.bImageh = res.bImageh;
        this.bColorId = res.bColorId;
        this.w = res.w;
        this.h = res.h;
        
        switch (layoutType) {
            case GRIDBAGSWING : {
                this.gc = ((GridBagConstraints)res.gc).clone();
            } break; 
            case BORDERSWING : {
                this.gc = res.gc;
            } break;
            case FLOWSWING : {
                this.gc = res.gc;
            } break;
            
        }
        
        this.iOrientation = res.iOrientation;
        this.elements = res.elements != null ? (ArrayList<UiResource>) res.elements.clone() : null ;
    }
    
    public String getId() {
        return id;        
    }
    
    /**
     * Metoda getTextureId nam vrati textovy retazec s id-ckom obrazku.
     * @return 
     */
    public String getBackgroundTextureId() {
        return bImageId;
    }
    
    public String getBackgroundColorId() {
        return bColorId;
    }
    
    public ArrayList<UiResource> getElements() {
        return elements;
    }
    
    public String getText() {        
        return text;
    }
    
    public UiType getType() {
        return type;
    }
    
    public LayoutType getLayoutType() {
        return layoutType;
    }
    
    public int getX() {
        return x;        
    }
    
    public int getY() {
        return y;
    }
    
    public boolean isScrolling() {
        return scrolling;
    }        
    
    public int getScrollX() {
        return scrollX;        
    }
    
    public int getScrollY() {
        return scrollY;
    }
    
    public String getWidth() {
        return w;
    }
    
    public String getHeight() {
        return h;
    }
    
    public String getMinWidth() {
        return mw;
    }
    
    public String getMinHeight() {
        return mh;
    }
    
    public UiResource getParent() {
        return uiResources.get(parent);
    }
    
    public String getImageWidth() {
        return bImagew;
    }
    
    public String getImageHeight() {
        return bImageh;
    }
    
    public int getImageOrientation() {
        return iOrientation;
    }
    
    public int getHGap() {
        return hGap;
    }
    
    public int getVGap() {
        return vGap;
    }
    
    public int getAlign() {
        return align;
    }
    
    public UiPosition getPosition() {
        return position;
    }
    
    public UiPosition getImagePosition() {
        return imagePosition;
    }
    
    public UiPaintMode getPaintMode() {
        return mode;
    }
    
    public boolean isOverlapping() {
        return mode.compareTo(UiPaintMode.OVERLAP)==0;
    }       
    
    public boolean isHardMoving() {
        return false;
    }
    
    public Object getConstraints() {
        return gc;
    }
    
    public String getAction() {
        return action;
    }
    
    public boolean isVisible() {
        return visible;
    }
}
