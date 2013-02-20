/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
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
import rpgcraft.resource.types.AbstractType;
import rpgcraft.resource.types.ButtonType;
import rpgcraft.resource.types.ImageType;
import rpgcraft.resource.types.ListType;
import rpgcraft.resource.types.PanelType;
import rpgcraft.resource.types.TextType;
import rpgcraft.utils.TextUtils;
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
    
    public enum ClickType {
            onListElement,
            onDefault
    }
    
    public class Action {
                
        ClickType clickType;
        String type;
        int clicks = 0;
        boolean trans = false;
        
        public Action(Action action) {
            this.clickType = action.clickType;
            this.clicks = action.clicks;
            this.trans = action.trans;
            this.type = action.type;
        }
        
        public Action() {}
        
        public String getType() {
            return type;
        }
        
        public int getClicks() {
            return clicks;
        }
        
        public boolean isTransparent() {
            return trans;
        }
        
        public ClickType getClickType() {
            return clickType;
        }
    }
    
    private static final Logger LOG = Logger.getLogger(UiResource.class.getName());
    private static HashMap<String, UiResource> uiResources = new HashMap<>();
    
    private String id;    
    private String font;
    private String bImageId;
    private String bImagew;
    private String bImageh;
    private String bColorId;
    private String w,h,mw,mh;
    private int hGap;
    private int vGap;
    private int align;   
    private boolean scrolling;
    private int scrollX;
    private int scrollY;
    private int iOrientation;
    
    private String parent;
    private ArrayList<Action> actions;
    private boolean visible;
    private UiPosition position;
    private UiPosition imagePosition;
    private UiPaintMode mode;
    private AbstractType type;
    
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
        
        if (type.getUiType() == UiType.LIST) {
            String[] param = new String[] {id};
            ListType lt = (ListType) type;
            if (lt.getElements() != null) {
                switch (lt.getElements().size()) {
                    // Ziadny element, list bude prazdny
                    case 0 : LOG.log(Level.INFO, StringResource.getResource("_blist", param));
                        break;
                    // spravna vetva
                    case 1 : break;
                    // Prilis vela elementov v liste, bude pouzity iba jeden
                    default : LOG.log(Level.INFO, StringResource.getResource("_mlist"));                        
                }
            } else {
                LOG.log(Level.INFO, StringResource.getResource("_blist", param));
            } 
        }
        
        if (type.getLayoutType() == null) {
            LOG.log(Level.INFO, StringResource.getResource("_mltype", new String[]{id}));
            type.setLayoutType(LayoutType.INGAME);
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
                    switch (type.getUiType()) {
                    case BUTTON : {                        
                        ButtonType bType = (ButtonType)type;
                        bType.setText(eNode.getTextContent()); 
                        bType.setFont(((Element)eNode).getAttribute(LayoutXML.FONT));
                        bType.setTextColor(((Element)eNode).getAttribute(LayoutXML.TEXTCOLOR));
                    } break;
                    case TEXT : {                        
                        TextType txType = (TextType)type;
                        txType.setText(eNode.getTextContent()); 
                        txType.setFont(((Element)eNode).getAttribute(LayoutXML.FONT));                        
                        txType.setTextColor(((Element)eNode).getAttribute(LayoutXML.TEXTCOLOR));
                    } break;    
                    default : {
                        String[] param = new String[] {LayoutXML.TEXT, id};
                        LOG.log(Level.WARNING, StringResource.getResource("_uparam", param));
                    }
                }
                } break;
                case LayoutXML.VISIBLE : {
                    visible = Boolean.valueOf(eNode.getTextContent());
                } break;
                case LayoutXML.FILL : {
                    switch (type.getLayoutType()) {
                        case GRIDBAGSWING : {
                            /* Reflection pristup */
                             try {                            
                                Field f = FillType.class.getField(eNode.getTextContent());
                                try {
                                    ((GridBagConstraints)type.getConstraints()).fill = f.getInt(null);
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
                        UiType uitype = UiType.valueOf(eNode.getTextContent());
                        switch (uitype) {
                            case BUTTON : {
                                type = new ButtonType(uitype);
                            } break;
                            case LIST : {
                                type = new ListType(uitype);                                
                            } break;                            
                            case PANEL : {
                                type = new PanelType(uitype);
                            } break;
                            case TEXT : {
                                type = new TextType(uitype);
                            } break;
                            case IMAGE : {
                                type = new ImageType(uitype);                                
                            } break;                               
                        }
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
                    if (type.getLayoutType() == null) {
                        type.setLayoutType(LayoutType.valueOf(eNode.getTextContent()));
                        // Zatial pre vsetky typy pridavam GridBagContstraints
                        type.setConstraints(new GridBagConstraints());
                    } else {
                        LOG.log(Level.INFO, StringResource.getResource("_ui_layoutype_error"), "layout-type");                 
                    }
                } break;
                case LayoutXML.INSETS : {            
                    switch (type.getLayoutType()) {
                        case GRIDBAGSWING : {
                            String[] _insets = eNode.getTextContent().split(",");
                            try {
                                ((GridBagConstraints)type.getConstraints()).insets = new Insets(Integer.parseInt(_insets[0]),
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
                    try {
                    ((GridBagConstraints)type.getConstraints()).gridx = Integer.parseInt(eNode.getTextContent());
                    } catch (Exception e) {
                        LOG.log(Level.WARNING,
                                StringResource.getResource("_iparam"), "gridx");
                    }                          
                }   break;
                case LayoutXML.Y : {
                    try {
                    ((GridBagConstraints)type.getConstraints()).gridy = Integer.parseInt(eNode.getTextContent());
                    } catch (Exception e) {
                        LOG.log(Level.WARNING,
                                StringResource.getResource("_iparam"), "gridy");
                    }                          
                }   break;
                case LayoutXML.WEIGHTX : {
                    switch (type.getLayoutType()) {
                        case GRIDBAGSWING : {
                            try {
                            ((GridBagConstraints)type.getConstraints()).weightx = Integer.parseInt(eNode.getTextContent());
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
                    switch (type.getLayoutType()) {
                        case GRIDBAGSWING : {
                            try {
                            ((GridBagConstraints)type.getConstraints()).weighty = Integer.parseInt(eNode.getTextContent());
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
                    try {
                        scrollX = ((Element)eNode).hasAttribute(LayoutXML.SPEEDX) ? Integer.parseInt(((Element)eNode).getAttribute(LayoutXML.SPEEDX))
                                : 0;

                        scrollY = ((Element)eNode).hasAttribute(LayoutXML.SPEEDY) ? Integer.parseInt(((Element)eNode).getAttribute(LayoutXML.SPEEDY))
                                : 0;
                    } catch (Exception e) {
                        
                    }
                } break; 
                case LayoutXML.SPEEDX : {
                    scrollX = Integer.parseInt(eNode.getTextContent());
                } break;   
                case LayoutXML.ACTION : {
                    if (actions == null) {
                        actions = new ArrayList<>();
                    }
                    Action action = new Action();                    
                    action.type = eNode.getTextContent();
                    try {
                        action.clicks = Integer.parseInt(((Element)eNode).getAttribute(LayoutXML.CLICK));
                    } catch (Exception e) {
                        LOG.log(Level.WARNING,StringResource.getResource("_iattrib", new String[] {"click", "action"}));
                    }
                    if (((Element)eNode).hasAttribute(LayoutXML.CLICKTYPE)) {
                        try {
                            switch (ClickType.valueOf(((Element)eNode).getAttribute(LayoutXML.CLICKTYPE))) {
                                case onListElement : {
                                    if (!type.getUiType().equals(UiType.LIST)) {
                                        LOG.log(Level.WARNING, StringResource.getResource("_nuattrib", new String[] {"clicktype", "action"}));
                                    }
                                    action.clickType = ClickType.onListElement;
                                } break;                                                         
                            }
                        } catch (Exception e) {
                            LOG.log(Level.WARNING,StringResource.getResource("_iattrib", new String[] {"onListElement", "action"}));
                        }
                    }
                    if (((Element)eNode).hasAttribute(LayoutXML.TRANSPARENT)) {
                        try {
                            action.trans = Boolean.parseBoolean(((Element)eNode).getAttribute(LayoutXML.TRANSPARENT));
                        } catch (Exception e) {
                            LOG.log(Level.WARNING,StringResource.getResource("_iattrib", new String[] {"transparent", "action"}));
                        }
                    }
                    actions.add(action);
                } break; 
                case LayoutXML.ACTIONS : {
                    actions = new ArrayList<>();
                    parse((Element)eNode);
                } break;
                case LayoutXML.SPEEDY : {
                    scrollY = Integer.parseInt(eNode.getTextContent());
                } break;
                case LayoutXML.IMAGEPOSITION : {
                    imagePosition = UiPosition.valueOf(eNode.getTextContent());
                } break;
                case LayoutXML.ORIENTATION : {
                    iOrientation = Integer.parseInt(eNode.getTextContent());
                } break;
                case LayoutXML.POSITION : {
                    position = UiPosition.valueOf(eNode.getTextContent());                
                } break;     
                case LayoutXML.LISTDATA : {
                    if(type.getUiType() == UiType.LIST) {
                        ListType lt = (ListType)type; 
                        lt.setData(((Element)eNode).getAttribute(LayoutXML.DATA));
                        for (Element _elem : XmlUtils.parseRootElements(eNode, LayoutXML.LISTELEMENT)) {
                            lt.addElement(new UiResource(_elem,id));                        
                        }
                    } else {
                        LOG.log(Level.WARNING,StringResource.getResource("_felement", new String[] {id}));
                    }                    
                } break;
                case LayoutXML.ELEMENTS : {
                    if (type.getUiType() == UiType.PANEL) {
                         PanelType pt = (PanelType)type;                                                  
                            for (Element _elem : XmlUtils.parseRootElements(eNode, LayoutXML.ELEMENT)) {
                                pt.addElement(new UiResource(_elem,id)); 
                            }
                    } else {
                        LOG.log(Level.WARNING,StringResource.getResource("_felement", new String[] {id}));
                    }
                } break;
                case LayoutXML.ROWLAYOUT : {
                    if (type.getUiType() == UiType.LIST) { 
                        try {
                            Field f = ListType.RowLayout.class.getField(eNode.getTextContent());
                            ((ListType)type).setLayout(f.getInt(null));
                        } catch (Exception e) {  
                            ((ListType)type).setLayout(ListType.RowLayout.VERTICALROWS);
                            String[] param = new String[] {LayoutXML.ROWLAYOUT, id};
                            LOG.log(Level.WARNING, StringResource.getResource("_rparam", param));
                        }
                    } else {
                        String[] param = new String[] {LayoutXML.ROWLAYOUT, id};
                        LOG.log(Level.INFO, StringResource.getResource("_uparam",param));
                    }
                } break;
                case LayoutXML.ROWSMAX : {
                    if (type.getUiType() == UiType.LIST) {                          
                        ((ListType)type).setRowsMax(eNode.getTextContent());
                    } else {
                        String[] param = new String[] {LayoutXML.ROWSMAX, id};
                        LOG.log(Level.INFO, StringResource.getResource("_uparam",param));
                    }
                } break;  
                case LayoutXML.COLSMAX : {
                    if (type.getUiType() == UiType.LIST) {                          
                        ((ListType)type).setColsMax(eNode.getTextContent());
                    } else {
                        String[] param = new String[] {LayoutXML.COLSMAX, id};
                        LOG.log(Level.INFO, StringResource.getResource("_uparam",param));
                    }
                } break;     
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
        this.type = (AbstractType)res.type.clone();   
        this.mode = res.mode;
        if (res.actions != null) {
            this.actions = new ArrayList(res.actions.size());
            for (Action action : res.actions) {
                this.actions.add(new Action(action));
            }
        }
        this.bImageId = res.bImageId;
        this.bImagew = res.bImagew;
        this.bImageh = res.bImageh;
        this.bColorId = res.bColorId;
        this.w = res.w;
        this.h = res.h;
        this.iOrientation = res.iOrientation;        
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
    
    public AbstractType getType() {
        return type;
    }
    
    public UiType getUiType() {
        return type.getUiType();
    }
    
    public LayoutType getLayoutType() {
        return type.getLayoutType();
    }        
    
    public int getX() {
        return type.getConstraints() == null ? 0 : ((GridBagConstraints)type.getConstraints()).gridx;        
    }
    
    public int getY() {
        return type.getConstraints() == null ? 0 : ((GridBagConstraints)type.getConstraints()).gridy;
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
        return type.getConstraints();
    }
    
    public ArrayList<Action> getActions() {
        return actions;
    }
    
    public boolean isVisible() {
        return visible;
    }
}
