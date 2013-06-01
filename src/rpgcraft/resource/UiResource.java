/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource;

import java.awt.Color;
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
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.graphics.Colors;
import rpgcraft.panels.listeners.Action;
import rpgcraft.resource.types.AbstractType;
import rpgcraft.resource.types.BarType;
import rpgcraft.resource.types.ButtonType;
import rpgcraft.resource.types.ImageType;
import rpgcraft.resource.types.ListType;
import rpgcraft.resource.types.PanelType;
import rpgcraft.resource.types.TextType;
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
        public static final String BLANK = "";
        public static final String AUTO = "AUTO";
    }       
    
    public enum UiPaintMode {
        OVERLAP,
        NORMAL        
    }
    
    public enum UiType {
        BUTTON,
        PANEL,
        TEXT,
        EDITTEXT,
        IMAGE,
        LIST,
        BAR
    }

    public enum UiPosition {
        CENTER(GridBagConstraints.CENTER),
        RIGHT(GridBagConstraints.EAST),
        LEFT(GridBagConstraints.WEST),
        TOP(GridBagConstraints.NORTH),
        TOP_RIGHT(GridBagConstraints.NORTHEAST),
        TOP_LEFT(GridBagConstraints.NORTHWEST),
        BOTTOM(GridBagConstraints.SOUTH),
        BOTTOM_LEFT(GridBagConstraints.SOUTHWEST),
        BOTTOM_RIGHT(GridBagConstraints.SOUTHEAST);
        
        private int value;
        
        private UiPosition(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
        
        
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
    
    private static final Logger LOG = Logger.getLogger(UiResource.class.getName());
    private static HashMap<String, UiResource> uiResources = new HashMap<>();
    
    private String id;    
    private String bImageId;
    private String bImagew;
    private String bImageh;
    private String w=UiSize.BLANK,h=UiSize.BLANK,mw=UiSize.BLANK,mh =UiSize.BLANK;
    private int hGap;
    private int vGap;
    private int align;   
    private boolean scrolling;
    private int scrollX;
    private int scrollY;
    private int iOrientation;
    
    private String parent;
    private ArrayList<Action> mouseActions;
    private ArrayList<Action> keyActions;
    private boolean active;
    private boolean visible;
    private UiPosition position;
    private int transPosX,transPosY;
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
                    if (type != null) {
                        parse((Element)eNode);         
                    } else {
                        LOG.log(Level.SEVERE, StringResource.getResource("_ndtype"));
                        new MultiTypeWrn(null, Color.red, StringResource.getResource("_ndtype"),
                                null).renderSpecific("_label_resourcerror");
                    }                  
                }   break;
                case LayoutXML.BACKGROUND : {
                    if (type != null) {
                        parse((Element)eNode);         
                    }         
                    else {
                        LOG.log(Level.SEVERE, StringResource.getResource("_ndtype"));
                        new MultiTypeWrn(null, Color.red, StringResource.getResource("_ndtype"),
                                null).renderSpecific("_label_resourcerror");
                    }
                }   break;
                case LayoutXML.BACKGROUNDCOLOR : {
                try {
                    type.setBackColor(eNode.getTextContent());
                } catch (Exception ex) {
                    LOG.log(Level.WARNING, StringResource.getResource("_ecolor",
                            new String[] {id, ex.getMessage()}));
                    type.setBackColor(Colors.getColor(Colors.Black));
                }
                }   break;
                case LayoutXML.TOPCOLOR : {
                try {
                    type.setTopColor(eNode.getTextContent());
                } catch (Exception ex) {
                    LOG.log(Level.WARNING, StringResource.getResource("_ecolor",
                            new String[] {id, ex.getMessage()}));
                    type.setTopColor(Colors.getColor(Colors.Black));
                }
                } break;
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
                    //System.out.println(id);
                } break;
                case LayoutXML.TEXT : {
                    switch (type.getUiType()) {
                    case BUTTON : {                        
                        ButtonType bType = (ButtonType)type;
                        bType.setText(eNode.getTextContent()); 
                        bType.setFont(((Element)eNode).getAttribute(LayoutXML.FONT));
                        bType.setTextColor(((Element)eNode).getAttribute(LayoutXML.TEXTCOLOR));
                        bType.setFontSize(((Element)eNode).getAttribute(LayoutXML.FONTSIZE));
                    } break;
                    case EDITTEXT : 
                    case TEXT : {                        
                        TextType txType = (TextType)type;
                        txType.setText(eNode.getTextContent()); 
                        txType.setFont(((Element)eNode).getAttribute(LayoutXML.FONT));                        
                        txType.setTextColor(((Element)eNode).getAttribute(LayoutXML.TEXTCOLOR));
                        txType.setFontSize(((Element)eNode).getAttribute(LayoutXML.FONTSIZE));
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
                case LayoutXML.EVENTACTIVE : {
                    this.active = Boolean.parseBoolean(eNode.getTextContent());                                
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
                            case EDITTEXT :                                                            
                            case TEXT : {
                                type = new TextType(uitype);
                            } break;                                
                            case IMAGE : {
                                type = new ImageType(uitype);                                
                            } break;
                            case BAR : {
                                type = new BarType(uitype);
                            } break;
                        }
                    } else {
                        LOG.log(Level.INFO, StringResource.getResource("_ui_error", new String[] {LayoutXML.TYPE, id}));
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
                        LOG.log(Level.INFO, StringResource.getResource("_ui_error", new String[] { LayoutXML.PAINTMODE, id}));
                    }
                } break;    
                case LayoutXML.LAYOUTTYPE : {   
                    if (type.getLayoutType() == null) {
                        type.setLayoutType(LayoutType.valueOf(eNode.getTextContent()));
                        // Zatial pre vsetky typy pridavam GridBagContstraints
                        type.setConstraints(new GridBagConstraints());
                    } else {
                        LOG.log(Level.INFO, StringResource.getResource("_ui_error", new String[] {LayoutXML.LAYOUTTYPE, id}));             
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
                                        StringResource.getResource("_iparam"), LayoutXML.INSETS);
                            }
                        }   break;
                        default : {
                            LOG.log(Level.WARNING,
                                        StringResource.getResource("_uparam"), LayoutXML.INSETS);
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
                    if (type.getLayoutType() == null) {
                        LOG.log(Level.WARNING,
                                        StringResource.getResource("_uparam"), "weightx");
                    } else {
                        switch (type.getLayoutType()) {
                            case GRIDBAGSWING : {
                                try {
                                ((GridBagConstraints)type.getConstraints()).weightx = Integer.parseInt(eNode.getTextContent());
                                } catch (Exception e) {
                                    LOG.log(Level.WARNING, StringResource.getResource("_iparam",
                                            new String[] {LayoutXML.WEIGHTX, this.getClass().getName(), id}));
                                }
                            }   break;
                            default : {
                                LOG.log(Level.WARNING,
                                            StringResource.getResource("_uparam"), "weightx");
                            }; 
                        }
                    }
                } break;
                case LayoutXML.WEIGHTY : {
                    if (type.getLayoutType() == null) {
                        LOG.log(Level.WARNING,
                                        StringResource.getResource("_uparam"), LayoutXML.WEIGHTY);
                    } else {
                        switch (type.getLayoutType()) {
                            case GRIDBAGSWING : {
                                try {
                                ((GridBagConstraints)type.getConstraints()).weighty = Integer.parseInt(eNode.getTextContent());
                                } catch (Exception e) {
                                    LOG.log(Level.WARNING, StringResource.getResource("_iparam",
                                            new String[] {LayoutXML.WEIGHTY, this.getClass().getName(), id}));
                                }
                            }   break;
                            default : {
                                LOG.log(Level.WARNING,
                                            StringResource.getResource("_uparam"), LayoutXML.WEIGHTY);
                            }; 
                        }
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
                    Action action = new Action();                    
                    action.setAction(eNode.getTextContent());
                    
                    Element currElem = (Element)eNode;                    
                    
                    if (currElem.hasAttribute(LayoutXML.SCRIPTYPE)) {
                        try {
                            switch (ScriptType.valueOf(currElem.getAttribute(LayoutXML.SCRIPTYPE))) {
                                case LUA : {                                    
                                    action.setLua(true);
                                } break;
                                case LISTENER : {
                                    action.setLua(false);
                                }
                                default : break;
                            }
                        } catch (Exception e) {
                            LOG.log(Level.WARNING,StringResource.getResource("_iattrib",
                                    new String[] {LayoutXML.SCRIPTYPE, LayoutXML.ACTION, toString()}));
                        }
                    }
                    
                    action.setMemorizable(Boolean.parseBoolean((currElem.getAttribute(LayoutXML.MEMORIZE))));
                    
                    if (currElem.hasAttribute(LayoutXML.CLICKTYPE)) {
                        try {
                            switch (ClickType.valueOf(currElem.getAttribute(LayoutXML.CLICKTYPE))) {
                                case onListElement : {
                                    if (!type.getUiType().equals(UiType.LIST)) {
                                        LOG.log(Level.WARNING, StringResource.getResource("_nuattrib", new String[] {"clicktype", "action"}));
                                    }
                                    action.setClickType(ClickType.onListElement);
                                }
                                default : break;
                            }
                        } catch (Exception e) {
                            LOG.log(Level.WARNING,StringResource.getResource("_iattrib",
                                    new String[] {LayoutXML.CLICKTYPE, LayoutXML.ACTION, toString()}));
                        }                    
                    }
                    
                    try {
                        action.setActionTransparency(Boolean.parseBoolean(currElem.getAttribute(LayoutXML.TRANSPARENT)));
                    } catch (Exception e) {
                        LOG.log(Level.WARNING,StringResource.getResource("_iattrib",
                                new String[] {LayoutXML.TRANSPARENT, LayoutXML.ACTION, toString()}));
                    }
                    
                    try {
                        action.setType(Action.Type.valueOf(currElem.getAttribute(LayoutXML.TYPE)));
                    } catch (Exception e) {
                        LOG.log(Level.WARNING,StringResource.getResource("_iattrib",
                                new String[] {LayoutXML.TYPE, LayoutXML.ACTION, toString()}));
                    }                                                                                                  
                    
                    switch (action.getType()) {
                        case MOUSE : {
                            
                            if (currElem.hasAttribute(LayoutXML.CLICK)) {
                                try {                        
                                    action.setClicks(Integer.parseInt((currElem.getAttribute(LayoutXML.CLICK))));                        
                                } catch (Exception e) {
                                    LOG.log(Level.WARNING,StringResource.getResource("_iattrib",
                                            new String[] {LayoutXML.CLICK, LayoutXML.ACTION, toString()}));
                                }
                            } else {
                               LOG.log(Level.WARNING,StringResource.getResource("_mattrib",
                                       new String[] {LayoutXML.CLICK, LayoutXML.ACTION, toString()})); 
                            }
                            
                            if (mouseActions == null) {
                                mouseActions = new ArrayList<>();
                            }
                            mouseActions.add(action);
                        } break;
                        case KEY : {
                            
                            if (currElem.hasAttribute(LayoutXML.CODE)) {
                                try {
                                    action.setCode((currElem.getAttribute(LayoutXML.CODE)));
                                } catch (Exception e) {
                                    LOG.log(Level.WARNING,StringResource.getResource("_iattrib",
                                            new String[] {LayoutXML.CODE, LayoutXML.ACTION, toString()}));
                                }
                            } else {
                               LOG.log(Level.WARNING,StringResource.getResource("_mattrib",
                                            new String[] {LayoutXML.CODE, LayoutXML.ACTION, toString()})); 
                            }
                            
                            if (keyActions == null) {
                                keyActions = new ArrayList<>();
                            }
                            keyActions.add(action);
                        }
                    }                                        
                } break; 
                case LayoutXML.ACTIONS : {
                    mouseActions = new ArrayList<>();
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
                    if (type.getLayoutType() != null) {
                        switch (type.getLayoutType()) {
                            case GRIDBAGSWING : {
                                try {
                                    ((GridBagConstraints)type.getConstraints()).anchor = UiPosition.valueOf(eNode.getTextContent()).getValue();
                                } catch (Exception e) {
                                    LOG.log(Level.WARNING,
                                            StringResource.getResource("_iparam"), "weighty");
                                }
                            }   break;
                            default :
                                setPositions(eNode);                                      
                        }
                    }                    
                    setPositions(eNode);
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
                case LayoutXML.DATA : {
                    if (type.getUiType() == UiType.BAR) {
                        try {
                            ((BarType)type).setData(eNode.getTextContent());
                        } catch (Exception ex) {
                            LOG.log(Level.INFO, StringResource.getResource("_iparam", 
                                    new String[] {LayoutXML.DATA, type.getUiType().toString()}));
                        }
                    } else {
                        LOG.log(Level.INFO, StringResource.getResource("_rparam", new String[] {
                            LayoutXML.DATA, type.getUiType().toString()}));
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
        this.type = (AbstractType)res.type.clone();   
        this.mode = res.mode;
        if (res.mouseActions != null) {
            this.mouseActions = new ArrayList(res.mouseActions.size());
            for (Action action : res.mouseActions) {
                this.mouseActions.add(new Action(action));
            }
        }
        this.bImageId = res.bImageId;
        this.bImagew = res.bImagew;
        this.bImageh = res.bImageh;
        this.w = res.w;
        this.h = res.h;        
        this.iOrientation = res.iOrientation;        
        this.active = res.active;
    }
    
    /**
     * Metoda vrati id resource.
     * @return Id resource
     */
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
    
    /**
     * Metoda vrati farbu ktora sa nachadza uplne v pozadi.
     * @return Farba pozadia
     */
    public Color getBackgroundColorId() {
        return type.getBackColor();
    }            
    
    /**
     * Metoda vrati farbu vrchneho pozadia.
     * @return Farba pozadia
     */
    public Color getTopColorId() {
        return type.getTopColor();
    }
    
    /**
     * Metoda vrati typ resource.
     * @return Typ Resource
     */
    public AbstractType getType() {
        return type;
    }
    
    /**
     * Metoda vrati UiTyp resource
     * @return UiTyp Resource.
     * @see UiType
     */
    public UiType getUiType() {
        return type.getUiType();
    }
    
    /**
     * Metoda ktora vrati typ layoutu resourcu.
     * @return Layout typ resourcu.
     */
    public LayoutType getLayoutType() {
        return type.getLayoutType();
    }        
    
    /**
     * Metoda vrati x-ovu poziciu komponenty. Vsetky pozicie su ulozene v GridBagConstraints aj ked
     * resource nema v type zadany GridBagSwing.
     * @return x-ova pozicia kde bude lezat komponenta
     */
    public int getX() {
        return type.getConstraints() == null ? 0 : ((GridBagConstraints)type.getConstraints()).gridx;        
    }
    
    /**
     * Metoda vrati y-ovu poziciu komponenty. Vsetky pozicie su ulozene v GridBagConstraints aj ked
     * resource nema v type zadany GridBagSwing.
     * @return y-ova pozicia kde bude lezat komponenta
     */
    public int getY() {
        return type.getConstraints() == null ? 0 : ((GridBagConstraints)type.getConstraints()).gridy;
    }
    
    /**
     * Metoda ktora vrati true/false podla toho ci komponenta z tohoto resource je skrolovacia.
     * @return true/false ci sa komponenta bude skrolovat.
     */
    public boolean isScrolling() {
        return scrolling;
    }        
    
    /**
     * Metoda ktora vrati ako rychlo sa bude skrolovat po x-ovej suradnici.
     * @return Okolko sa za jeden update komponenta posunie.
     */
    public int getScrollX() {
        return scrollX;        
    }
    
    /**
     * Metoda ktora vrati ako rychlo sa bude skrolovat po y-ovej suradnici.
     * @return Okolko sa za jeden update komponenta posunie.
     */
    public int getScrollY() {
        return scrollY;
    }
    
    /**
     * Metoda vrati sirku resource.
     * @return Sirka resource v textovej podobe.
     */
    public String getWidth() {
        return w;
    }
    
    /**
     * Metoda vrati vysku resource.
     * @return Vyska resource v textovej podobe.
     */
    public String getHeight() {
        return h;
    }
    
    /**
     * Metoda vrati minimalnu sirku resource.
     * @return Vyska resource v textovej podobe.
     */
    public String getMinWidth() {
        return mw;
    }
    
    /**
     * Metoda vrati minimalnu vysku resource.
     * @return Minimalna vyska resource v textovej podobe.
     */
    public String getMinHeight() {
        return mh;
    }
    
    /**
     * Metoda ktora vrati otcovsky resource. Komponenta ktora bola vytvorena tymto resourcom
     * bude mat otcovsku komponentu vytvorenu z resource ktory tato metoda vrati.
     * @return Otcovsky resource pre tento resource.
     */
    public UiResource getParent() {
        return uiResources.get(parent);
    }
    
    /**
     * Metoda ktora vrati sirku obrazku pre tento resource.
     * @return Sirka obrazku v textovej podobe
     */
    public String getImageWidth() {
        return bImagew;
    }
        
    /**
     * Metoda ktora vrati vysku obrazku pre tento resource.
     * @return Vyska obrazku v textovej podobe
     */
    public String getImageHeight() {
        return bImageh;
    }
    
    /**
     * Metoda ktora vrati orientaciu obrazku v stupnoch v tomto resource.
     * @return Orientacia obrazku v stupnoch.
     */
    public int getImageOrientation() {
        return iOrientation;
    }
    
    /**
     * Metoda ktora vrati horizontalne miesto pri komponente. Zatial nepouzite.
     * Pouzitie pri definovani FlowLayoutu.
     * @return Odsadenie komponenty po krajoch.        
     */
    public int getHGap() {
        return hGap;
    }
    
    /**
     * Metoda ktora vrati vertikalne miesto pri komponente. Zatial nepouzite.
     * Pouzitie pri definovani FlowLayoutu.
     * @return Odsadenie komponenty po vyske.        
     */
    public int getVGap() {
        return vGap;
    }
    
    /**
     * Metoda ktora vrati do ktorej strany je zarovnana komponenta. Zatial nepouzite.
     * Pouzitie pri definovani FlowLayoutu.
     * @return 
     */
    public int getAlign() {
        return align;
    }
    
    /**
     * Metoda ktora vrati poziciu komponenty.
     * @return Pozicia komponenty v rodicovskej komponente
     * @see UiPosition
     */
    public UiPosition getPosition() {
        return position;
    }
    
    /**
     * Metoda ktora vrati posunutu poziciu o x-ovu suradnicu
     * @return X-ova posunuta pozicia
     */
    public int getTransPosX() {
        return transPosX;
    }
    
    /**
     * Metoda ktora vrati posunutu poziciu o y-ovu suradnicu
     * @return Y-ova posunuta pozicia
     */
    public int getTransPosY() {
        return transPosY;
    }
    
    /**
     * Metoda ktora vrati poziciu obrazku v komponente v ktorej sa nachadza.     
     * @return Pozicia komponenty v komponente
     * @see UiPosition
     */
    public UiPosition getImagePosition() {
        return imagePosition;
    }
    
    /**
     * Metoda ktora vrati vykreslovaci mod pre komponentu. Zatial nepouzite
     * (zatial iba hruby nacrt prekreslovania a normalneho vykreslovania
     * @return PaintMod pre vykreslovanie
     * @see UiPaintMode
     */
    public UiPaintMode getPaintMode() {
        return mode;
    }
    
    /**
     * Metoda ktora vrati ci bude komponenta prekreslujuca.
     * Porovnavanie UiPaintModu s UiPaintMode.Overlap
     * @return True/False ci je metoda prekreslujuca.
     * @see UiPaintMode
     */
    public boolean isOverlapping() {
        return mode.compareTo(UiPaintMode.OVERLAP)==0;
    }       
    
    /**
     * Metoda ktora vrati constraints podla ktorej sa bude riadit rozlozenie komponenty.
     * @return Constraints pre komponentu.
     */
    public Object getConstraints() {
        return type.getConstraints();
    }
    
    /**
     * Metoda ktora vrati akcie pri stlaceni mysi. Mozne doplnit o Hover,Pressed,Released akcie pre mys.
     * @return ArrayList s akciami pre mys eventy.
     */
    public ArrayList<Action> getMouseActions() {
        return mouseActions;
    }
    
    /**
     * Metoda ktora vrati akcie pri stlaceni klaves.
     * @return ArrayList s akciami pre klavesove eventy.
     * @return 
     */
    public ArrayList<Action> getKeyActions() {
        return keyActions;
    }
    
    /**
     * Metoda ktora vrati true/false podla toho ci bude komponenta aktivna
     * pre spracovanie eventov. Vykonavanie mys/klavesovych akcii.
     * @return True/False <=>aktivne/neaktivn na eventy.
     */
    public boolean isEventActive() {
        return active;
    }
    
    /**
     * Metoda ktora vrati ci bude komponenta viditelna alebo nie.
     * @return True/false ci je komponenta viditelna.
     */
    public boolean isVisible() {
        return visible;
    }
    
    /**
     * Metoda ktora nastavi pozicie komponenty z tagu position
     * @param eNode Vrchol typu position
     */
    public void setPositions(Node eNode) {
        position = UiPosition.valueOf(eNode.getTextContent()); 
        Element element = (Element)eNode;
        if (element.hasAttribute(LayoutXML.X)) {
            transPosX = Integer.parseInt(element.getAttribute(LayoutXML.X));
        }
        if (element.hasAttribute(LayoutXML.Y)) {
            transPosY = Integer.parseInt(element.getAttribute(LayoutXML.Y));
        }
    }
    
    /**
     * Metoda toString vrati text s vypisom/zakladnymi informaciami o tomto resource.
     * Meno triedy + id.
     * @return String s popiskom objektu
     */
    @Override
    public String toString() {
        return this.getClass().getName() + ":" + id;
    }
}
